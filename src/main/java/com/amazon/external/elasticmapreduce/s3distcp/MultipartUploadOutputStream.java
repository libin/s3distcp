/*     */ package com.amazon.external.elasticmapreduce.s3distcp;
/*     */ 
/*     */ //import amazon.emr.metrics.MetricsSaver;
/*     */ //import amazon.emr.metrics.MetricsSaver.StopWatch;
/*     */ import com.amazonaws.services.s3.AmazonS3;
/*     */ import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
/*     */ import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
/*     */ import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
/*     */ import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
/*     */ import com.amazonaws.services.s3.model.ObjectMetadata;
/*     */ import com.amazonaws.services.s3.model.PartETag;
/*     */ import com.amazonaws.services.s3.model.UploadPartRequest;
/*     */ import com.amazonaws.services.s3.model.UploadPartResult;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.DigestOutputStream;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.hadoop.fs.common.Abortable;
/*     */ import org.apache.hadoop.io.retry.RetryPolicies;
/*     */ import org.apache.hadoop.io.retry.RetryPolicy;
/*     */ import org.apache.hadoop.io.retry.RetryProxy;
/*     */ import org.apache.hadoop.util.Progressable;
/*     */ 
/*     */ public class MultipartUploadOutputStream extends OutputStream
/*     */   implements Abortable
/*     */ {
/*  47 */   public static final Log LOG = LogFactory.getLog("org.apache.hadoop.fs.s3native.MultipartUploadOutputStream");
/*     */   final AmazonS3 s3;
/*     */   final ThreadPoolExecutor threadPool;
/*     */   final Progressable progressable;
/*     */   final List<Future<PartETag>> futures;
/*     */   final File[] tempDirs;
/*     */   final String bucketName;
/*     */   final String key;
/*     */   final String uploadId;
/*     */   final long partSize;
/*  60 */   int partCount = 0;
/*  61 */   long currentPartSize = 0L;
/*     */   File currentTemp;
/*     */   DigestOutputStream currentOutput;
/*     */ 
/*     */   public MultipartUploadOutputStream(AmazonS3 s3, ThreadPoolExecutor threadPool, Progressable progressable, String bucketName, String key, ObjectMetadata metadata, long partSize, File[] tempDirs)
/*     */   {
/*  75 */     RetryPolicy basePolicy = RetryPolicies.retryUpToMaximumCountWithFixedSleep(4, 10L, TimeUnit.SECONDS);
/*  76 */     Map exceptionToPolicyMap = new HashMap();
/*     */ 
/*  78 */     exceptionToPolicyMap.put(Exception.class, basePolicy);
/*     */ 
/*  80 */     RetryPolicy methodPolicy = RetryPolicies.retryByException(RetryPolicies.TRY_ONCE_THEN_FAIL, exceptionToPolicyMap);
/*     */ 
/*  82 */     Map methodNameToPolicyMap = new HashMap();
/*     */ 
/*  84 */     methodNameToPolicyMap.put("completeMultipartUpload", methodPolicy);
/*     */ 
/*  86 */     this.s3 = ((AmazonS3)RetryProxy.create(AmazonS3.class, s3, methodNameToPolicyMap));
/*  87 */     InitiateMultipartUploadResult result = this.s3.initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, key).withObjectMetadata(metadata));
/*     */ 
/*  89 */     this.threadPool = threadPool;
/*  90 */     this.progressable = progressable;
/*  91 */     this.futures = new ArrayList();
/*     */ 
/*  93 */     this.tempDirs = tempDirs;
/*  94 */     this.bucketName = bucketName;
/*  95 */     this.key = key;
/*  96 */     this.uploadId = result.getUploadId();
/*  97 */     this.partSize = partSize;
/*     */ 
/*  99 */     setTempFileAndOutput();
/*     */   }
/*     */ 
/*     */   public void write(byte[] b) throws IOException
/*     */   {
/* 104 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public void write(byte[] b, int off, int len) throws IOException
/*     */   {
/* 109 */     long capacityLeft = capacityLeft();
/* 110 */     int offset = off;
/* 111 */     int length = len;
/* 112 */     while (capacityLeft < length)
/*     */     {
/* 115 */       int capacityLeftInt = (int)capacityLeft;
/* 116 */       this.currentOutput.write(b, offset, capacityLeftInt);
/* 117 */       kickOffUpload();
/* 118 */       offset += capacityLeftInt;
/* 119 */       length -= capacityLeftInt;
/* 120 */       capacityLeft = capacityLeft();
/*     */     }
/* 122 */     this.currentOutput.write(b, offset, length);
/* 123 */     this.currentPartSize += length;
/*     */   }
/*     */ 
/*     */   public void write(int b) throws IOException
/*     */   {
/* 128 */     if (capacityLeft() < 1L) {
/* 129 */       kickOffUpload();
/*     */     }
/* 131 */     this.currentOutput.write(b);
/* 132 */     this.currentPartSize += 1L;
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 143 */       kickOffUpload();
/*     */ 
/* 148 */       boolean anyNotDone = false;
/* 149 */       while (!anyNotDone) {
/* 150 */         anyNotDone = true;
/* 151 */         for (Future future : this.futures) {
/* 152 */           anyNotDone &= future.isDone();
/*     */         }
/* 154 */         if (this.progressable != null) {
/* 155 */           this.progressable.progress();
/*     */         }
/* 157 */         Thread.sleep(1000L);
/*     */       }
/*     */ 
/* 160 */       List etags = new ArrayList();
/* 161 */       for (Future future : this.futures) {
/* 162 */         etags.add(future.get());
/*     */       }
/* 164 */       LOG.debug("About to close multipart upload " + this.uploadId + " with bucket '" + this.bucketName + "' key '" + this.key + "' and etags '" + etags + "'");
/*     */ 
/* 166 */       this.s3.completeMultipartUpload(new CompleteMultipartUploadRequest(this.bucketName, this.key, this.uploadId, etags));
/*     */     } catch (Exception e) {
/* 168 */       this.s3.abortMultipartUpload(new AbortMultipartUploadRequest(this.bucketName, this.key, this.uploadId));
/* 169 */       throw new RuntimeException("Error closing multipart upload", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void abort()
/*     */   {
/* 175 */     for (Future future : this.futures) {
/* 176 */       future.cancel(true);
/*     */     }
/* 178 */     this.s3.abortMultipartUpload(new AbortMultipartUploadRequest(this.bucketName, this.key, this.uploadId));
/*     */   }
/*     */ 
/*     */   private void kickOffUpload() throws IOException {
/* 182 */     this.currentOutput.close();
/* 183 */     String md5sum = new String(Base64.encodeBase64(this.currentOutput.getMessageDigest().digest()), Charset.forName("UTF-8"));
/* 184 */     this.futures.add(this.threadPool.submit(new MultipartUploadCallable(this.partCount, this.currentTemp, md5sum)));
/*     */ 
/* 186 */     setTempFileAndOutput();
/*     */   }
/*     */ 
/*     */   private long capacityLeft() {
/* 190 */     return this.partSize - this.currentPartSize;
/*     */   }
/*     */ 
/*     */   private void setTempFileAndOutput() {
/*     */     try {
/* 195 */       this.currentPartSize = 0L;
/* 196 */       this.currentTemp = new File(this.tempDirs[(this.partCount % this.tempDirs.length)], "multipart-" + this.uploadId + "-" + this.partCount++);
/* 197 */       this.currentOutput = new DigestOutputStream(new BufferedOutputStream(new FileOutputStream(this.currentTemp)), MessageDigest.getInstance("MD5"));
/*     */     } catch (IOException e) {
/* 199 */       throw new RuntimeException("Error creating temporary output stream.", e);
/*     */     } catch (NoSuchAlgorithmException e) {
/* 201 */       throw new RuntimeException("Error creating DigestOutputStream", e);
/*     */     }
/*     */   }
/*     */   private class MultipartUploadCallable implements Callable<PartETag> {
/*     */     private final int partNumber;
/*     */     private final File partFile;
/*     */     private final String md5sum;
/*     */ 
/* 211 */     public MultipartUploadCallable(int partNumber, File partFile, String md5sum) { this.partNumber = partNumber;
/* 212 */       this.partFile = partFile;
/* 213 */       this.md5sum = md5sum; }
/*     */ 
/*     */     public PartETag call()
/*     */       throws Exception
/*     */     {
/* 218 */       InputStream is = new ProgressableResettableBufferedFileInputStream(this.partFile, MultipartUploadOutputStream.this.progressable);
/*     */ 
/* 220 */       UploadPartRequest request = new UploadPartRequest().withBucketName(MultipartUploadOutputStream.this.bucketName).withKey(MultipartUploadOutputStream.this.key).withUploadId(MultipartUploadOutputStream.this.uploadId).withInputStream(is).withPartNumber(this.partNumber).withPartSize(this.partFile.length()).withMD5Digest(this.md5sum);
/*     */ 
/* 230 */       //MetricsSaver.StopWatch stopWatch = new MetricsSaver.StopWatch();
/*     */       UploadPartResult result;
/*     */       try
/*     */       {
/* 232 */         String message = String.format("S3 uploadPart bucket:%s key:%s part:%d size:%d", new Object[] { MultipartUploadOutputStream.this.bucketName, MultipartUploadOutputStream.this.key, Integer.valueOf(this.partNumber), Long.valueOf(this.partFile.length()) });
/*     */ 
/* 235 */         MultipartUploadOutputStream.LOG.info(message);
/* 236 */         result = MultipartUploadOutputStream.this.s3.uploadPart(request);
/* 237 */         //MetricsSaver.addValue("S3WriteDelay", stopWatch.elapsedTime());
/* 238 */         //MetricsSaver.addValue("S3WriteBytes", this.partFile.length());
/*     */       } catch (Exception e) {
/* 240 */         //MetricsSaver.addValueWithError("S3WriteDelay", stopWatch.elapsedTime(), e);
/* 241 */         throw e;
/*     */       } finally {
/*     */         try {
/* 244 */           if (is != null)
/* 245 */             is.close();
/*     */         }
/*     */         finally {
/* 248 */           this.partFile.delete();
/*     */         }
/*     */       }
/*     */ 
/* 252 */       return result.getPartETag();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.MultipartUploadOutputStream
 * JD-Core Version:    0.6.2
 */