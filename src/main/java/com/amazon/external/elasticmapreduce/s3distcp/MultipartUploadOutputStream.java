package com.amazon.external.elasticmapreduce.s3distcp;

//import amazon.emr.metrics.MetricsSaver;
//import amazon.emr.metrics.MetricsSaver.StopWatch;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;
import com.amazonaws.services.s3.model.UploadPartResult;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.common.Abortable;
import org.apache.hadoop.io.retry.RetryPolicies;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.io.retry.RetryProxy;
import org.apache.hadoop.util.Progressable;

public class MultipartUploadOutputStream extends OutputStream implements Abortable {
  public static final Log LOG = LogFactory.getLog("org.apache.hadoop.fs.s3native.MultipartUploadOutputStream");
  final AmazonS3 s3;
  final ThreadPoolExecutor threadPool;
  final Progressable progressable;
  final List<Future<PartETag>> futures;
  final File[] tempDirs;
  final String bucketName;
  final String key;
  final String uploadId;
  final long partSize;
  int partCount = 0;
  long currentPartSize = 0L;
  File currentTemp;
  DigestOutputStream currentOutput;

  public MultipartUploadOutputStream(AmazonS3 s3, ThreadPoolExecutor threadPool, Progressable progressable,
      String bucketName, String key, ObjectMetadata metadata, long partSize, File[] tempDirs) {
    RetryPolicy basePolicy = RetryPolicies.retryUpToMaximumCountWithFixedSleep(4, 10L, TimeUnit.SECONDS);
    Map exceptionToPolicyMap = new HashMap();

    exceptionToPolicyMap.put(Exception.class, basePolicy);

    RetryPolicy methodPolicy = RetryPolicies.retryByException(RetryPolicies.TRY_ONCE_THEN_FAIL, exceptionToPolicyMap);

    Map methodNameToPolicyMap = new HashMap();

    methodNameToPolicyMap.put("completeMultipartUpload", methodPolicy);

    this.s3 = ((AmazonS3) RetryProxy.create(AmazonS3.class, s3, methodNameToPolicyMap));
    InitiateMultipartUploadResult result = this.s3
        .initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, key).withObjectMetadata(metadata));

    this.threadPool = threadPool;
    this.progressable = progressable;
    this.futures = new ArrayList();

    this.tempDirs = tempDirs;
    this.bucketName = bucketName;
    this.key = key;
    this.uploadId = result.getUploadId();
    this.partSize = partSize;

    setTempFileAndOutput();
  }

  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  public void write(byte[] b, int off, int len) throws IOException {
    long capacityLeft = capacityLeft();
    int offset = off;
    int length = len;
    while (capacityLeft < length) {
      int capacityLeftInt = (int) capacityLeft;
      this.currentOutput.write(b, offset, capacityLeftInt);
      kickOffUpload();
      offset += capacityLeftInt;
      length -= capacityLeftInt;
      capacityLeft = capacityLeft();
    }
    this.currentOutput.write(b, offset, length);
    this.currentPartSize += length;
  }

  public void write(int b) throws IOException {
    if (capacityLeft() < 1L) {
      kickOffUpload();
    }
    this.currentOutput.write(b);
    this.currentPartSize += 1L;
  }

  public void flush() {
  }

  public void close() {
    try {
      kickOffUpload();

      boolean anyNotDone = false;
      while (!anyNotDone) {
        anyNotDone = true;
        for (Future future : this.futures) {
          anyNotDone &= future.isDone();
        }
        if (this.progressable != null) {
          this.progressable.progress();
        }
        Thread.sleep(1000L);
      }

      List etags = new ArrayList();
      for (Future future : this.futures) {
        etags.add(future.get());
      }
      LOG.debug("About to close multipart upload " + this.uploadId + " with bucket '" + this.bucketName + "' key '"
          + this.key + "' and etags '" + etags + "'");

      this.s3
          .completeMultipartUpload(new CompleteMultipartUploadRequest(this.bucketName, this.key, this.uploadId, etags));
    } catch (Exception e) {
      this.s3.abortMultipartUpload(new AbortMultipartUploadRequest(this.bucketName, this.key, this.uploadId));
      throw new RuntimeException("Error closing multipart upload", e);
    }
  }

  public void abort() {
    for (Future future : this.futures) {
      future.cancel(true);
    }
    this.s3.abortMultipartUpload(new AbortMultipartUploadRequest(this.bucketName, this.key, this.uploadId));
  }

  private void kickOffUpload() throws IOException {
    this.currentOutput.close();
    String md5sum = new String(Base64.encodeBase64(this.currentOutput.getMessageDigest().digest()),
        Charset.forName("UTF-8"));
    this.futures.add(this.threadPool.submit(new MultipartUploadCallable(this.partCount, this.currentTemp, md5sum)));

    setTempFileAndOutput();
  }

  private long capacityLeft() {
    return this.partSize - this.currentPartSize;
  }

  private void setTempFileAndOutput() {
    try {
      this.currentPartSize = 0L;
      this.currentTemp = new File(this.tempDirs[(this.partCount % this.tempDirs.length)],
          "multipart-" + this.uploadId + "-" + this.partCount++);
      this.currentOutput = new DigestOutputStream(new BufferedOutputStream(new FileOutputStream(this.currentTemp)),
          MessageDigest.getInstance("MD5"));
    } catch (IOException e) {
      throw new RuntimeException("Error creating temporary output stream.", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error creating DigestOutputStream", e);
    }
  }

  private class MultipartUploadCallable implements Callable<PartETag> {
    private final int partNumber;
    private final File partFile;
    private final String md5sum;

    public MultipartUploadCallable(int partNumber, File partFile, String md5sum) {
      this.partNumber = partNumber;
      this.partFile = partFile;
      this.md5sum = md5sum;
    }

    public PartETag call() throws Exception {
      InputStream is = new ProgressableResettableBufferedFileInputStream(this.partFile,
          MultipartUploadOutputStream.this.progressable);

      UploadPartRequest request = new UploadPartRequest().withBucketName(MultipartUploadOutputStream.this.bucketName)
          .withKey(MultipartUploadOutputStream.this.key).withUploadId(MultipartUploadOutputStream.this.uploadId)
          .withInputStream(is).withPartNumber(this.partNumber).withPartSize(this.partFile.length())
          .withMD5Digest(this.md5sum);

      // MetricsSaver.StopWatch stopWatch = new MetricsSaver.StopWatch();
      UploadPartResult result;
      try {
        String message = String.format("S3 uploadPart bucket:%s key:%s part:%d size:%d",
            new Object[] { MultipartUploadOutputStream.this.bucketName, MultipartUploadOutputStream.this.key,
                Integer.valueOf(this.partNumber), Long.valueOf(this.partFile.length()) });

        MultipartUploadOutputStream.LOG.info(message);
        result = MultipartUploadOutputStream.this.s3.uploadPart(request);
        // MetricsSaver.addValue("S3WriteDelay", stopWatch.elapsedTime());
        // MetricsSaver.addValue("S3WriteBytes", this.partFile.length());
      } catch (Exception e) {
        // MetricsSaver.addValueWithError("S3WriteDelay", stopWatch.elapsedTime(), e);
        throw e;
      } finally {
        try {
          if (is != null)
            is.close();
        } finally {
          this.partFile.delete();
        }
      }

      return result.getPartETag();
    }
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.MultipartUploadOutputStream
 * JD-Core Version: 0.6.2
 */