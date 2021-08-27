package com.amazon.external.elasticmapreduce.s3distcp;

//import amazon.emr.metrics.MetricsSaver;
//import amazon.emr.metrics.MetricsSaver.StopWatch;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.common.Abortable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;

class CopyFilesRunable implements Runnable {
  private static final Log LOG = LogFactory.getLog(CopyFilesRunable.class);
  private final List<FileInfo> fileInfos;
  private final CopyFilesReducer reducer;
  private final String tempPath;
  private final Path finalPath;
  private final boolean groupWithNewLine;
  private final byte[] newLine = "\n".getBytes();

  public CopyFilesRunable(CopyFilesReducer reducer, List<FileInfo> fileInfos, Path tempPath, Path finalPath,
      boolean groupWithNewLine) {
    this.fileInfos = fileInfos;
    this.reducer = reducer;
    this.tempPath = tempPath.toString();
    this.finalPath = finalPath;
    this.groupWithNewLine = groupWithNewLine;
    LOG.info("Creating CopyFilesRunnable " + tempPath.toString() + ":" + finalPath.toString());
  }

  private long copyStream(InputStream inputStream, OutputStream outputStream, MessageDigest md) throws IOException {
    long bytesCopied = 0L;
    // MetricsSaver.StopWatch stopWatch = new MetricsSaver.StopWatch();
    try {
      int len = 0;
      byte[] buffer = new byte[this.reducer.getBufferSize()];
      while ((len = inputStream.read(buffer)) > 0) {
        md.update(buffer, 0, len);
        outputStream.write(buffer, 0, len);
        // this.reducer.progress();
        bytesCopied += len;
      }

      if (groupWithNewLine) {
        // 最後が改行でなければ改行を追記
        if (len < 2 || (buffer[len - 2] != newLine[0] || buffer[len - 1] != newLine[1])) {
          md.update(newLine);
          outputStream.write(newLine);
          bytesCopied += 2;
        }
      }

      // MetricsSaver.addValue("S3DistCpCopyStreamDelay", stopWatch.elapsedTime());
      // MetricsSaver.addValue("S3DistCpCopyStreamBytes", bytesCopied);
    } catch (Exception e) {
      // MetricsSaver.addValueWithError("S3DistCpCopyStreamDelay",
      // stopWatch.elapsedTime(), e);
      throw new IOException("exception raised while copying data file", e);
    }
    return bytesCopied;
  }

  public ProcessedFile downloadAndMergeInputFiles() throws Exception {
    boolean finished = false;
    int numRetriesRemaining = this.reducer.getNumTransferRetries();
    byte[] digest = null;
    Path curTempPath = null;

    while ((!finished) && (numRetriesRemaining > 0)) {
      numRetriesRemaining--;
      OutputStream outputStream = null;

      curTempPath = new Path(this.tempPath + UUID.randomUUID());
      try {
        LOG.info("Opening temp file: " + curTempPath.toString());
        outputStream = this.reducer.openOutputStream(curTempPath);
        MessageDigest md = MessageDigest.getInstance("MD5");
        for (FileInfo fileInfo : this.fileInfos) {
          try {
            LOG.info("Starting download of " + fileInfo.inputFileName + " to " + curTempPath);
            InputStream inputStream = this.reducer.openInputStream(new Path(fileInfo.inputFileName.toString()));
            try {
              long bytesCopied = copyStream(inputStream, outputStream, md);
              LOG.info("Copied " + bytesCopied + " bytes");
            } finally {
              inputStream.close();
            }
          } catch (Exception e) {
            if ((outputStream != null) && ((outputStream instanceof Abortable))) {
              LOG.warn("Output stream is abortable, aborting the output stream for " + fileInfo.inputFileName);
              Abortable abortable = (Abortable) outputStream;
              abortable.abort();
            }
            throw e;
          }
          finished = true;
          LOG.info("Finished downloading " + fileInfo.inputFileName);
        }
        outputStream.close();
        digest = md.digest();
        return new ProcessedFile(digest, curTempPath);
      } catch (Exception e) {
        LOG.warn("Exception raised while copying file data to:  file=" + this.finalPath + " numRetriesRemaining="
            + numRetriesRemaining, e);
        try {
          FileSystem fs = curTempPath.getFileSystem(this.reducer.getConf());
          fs.delete(curTempPath, false);
        } catch (IOException e1) {
        }
        if (numRetriesRemaining <= 0)
          throw e;
      } finally {
        try {
          outputStream.close();
        } catch (Exception e) {
        }
      }
    }
    return null;
  }

  private static File[] getTempDirs(Configuration conf) {
    String[] backupDirs = conf.get("fs.s3.buffer.dir").split(",");
    List tempDirs = new ArrayList(backupDirs.length);
    int directoryIndex = 0;

    File result = null;
    while (directoryIndex < backupDirs.length) {
      File dir = new File(backupDirs[directoryIndex]);
      dir.mkdirs();
      try {
        result = File.createTempFile("output-", ".tmp", dir);
        if (result != null) {
          tempDirs.add(new File(backupDirs[directoryIndex]));
        }
        result.delete();
      } catch (Exception e) {
      }
      directoryIndex += 1;
    }

    return (File[]) tempDirs.toArray(new File[0]);
  }

  public void run() {
    int retriesRemaining = this.reducer.getNumTransferRetries();
    ProcessedFile processedFile = null;
    try {
      processedFile = downloadAndMergeInputFiles();
    } catch (Exception e) {
      LOG.warn("Error download input files. Not marking as committed", e);
    }

    while (retriesRemaining > 0) {
      retriesRemaining--;
      try {
        Path curTempPath = processedFile.path;
        FileSystem inFs = curTempPath.getFileSystem(this.reducer.getConf());
        FileSystem outFs = this.finalPath.getFileSystem(this.reducer.getConf());
        if (inFs.getUri().equals(outFs.getUri())) {
          LOG.info("Renaming " + curTempPath.toString() + " to " + this.finalPath.toString());
          inFs.mkdirs(this.finalPath.getParent());
          inFs.rename(curTempPath, this.finalPath);
        } else {
          LOG.info("inFs.getUri()!=outFs.getUri(): " + inFs.getUri() + "!=" + outFs.getUri());
          copyToFinalDestination(curTempPath, this.finalPath, processedFile, inFs, outFs);
        }

        for (FileInfo fileInfo : this.fileInfos) {
          this.reducer.markFileAsCommited(fileInfo);
          if (this.reducer.shouldDeleteOnSuccess()) {
            LOG.info("Deleting " + fileInfo.inputFileName);
            Path inPath = new Path(fileInfo.inputFileName.toString());
            FileSystem deleteFs = FileSystem.get(inPath.toUri(), this.reducer.getConf());
            deleteFs.delete(inPath, false);
          }
        }
        Path localTempPath = new Path(this.tempPath);
        FileSystem fs = localTempPath.getFileSystem(this.reducer.getConf());
        fs.delete(localTempPath, true);
        return;
      } catch (Exception e) {
        LOG.warn("Error processing files. Not marking as committed", e);
      }
    }
  }

  private void copyToFinalDestination(Path curTempPath, Path finalPath, ProcessedFile processedFile, FileSystem inFs,
      FileSystem outFs) throws Exception {
    LOG.info("Copying " + curTempPath.toString() + " to " + finalPath.toString());
    byte[] digest = processedFile.checksum;
    InputStream inStream = this.reducer.openInputStream(curTempPath);
    OutputStream outStream = null;
    if (Utils.isS3Scheme(outFs.getUri().getScheme())) {
      FileStatus status = inFs.getFileStatus(curTempPath);
      URI outUri = finalPath.toUri();
      String bucket = outUri.getHost();

      String key = outUri.getPath().substring(1);
      AmazonS3Client s3 = S3DistCp.createAmazonS3Client(this.reducer.getConf());
      s3.setEndpoint(this.reducer.getConf().get("fs.s3n.endpoint", "s3.amazonaws.com"));
      ObjectMetadata meta = new ObjectMetadata();
      meta.setContentLength(status.getLen());
      if (digest != null) {
        meta.setContentMD5(new String(Base64.encodeBase64(digest), Charset.forName("UTF-8")));
      }

      if (this.reducer.shouldUseMutlipartUpload()) {
        int chunkSize = this.reducer.getMultipartSize();
        outStream = new MultipartUploadOutputStream(s3, Utils.createDefaultExecutorService(),
            this.reducer.getProgressable(), bucket, key, meta, chunkSize, getTempDirs(this.reducer.getConf()));
      } else {
        int retries = this.reducer.getNumTransferRetries();
        while (retries > 0) {
          // MetricsSaver.StopWatch stopWatch = new MetricsSaver.StopWatch();
          try {
            retries--;
            s3.putObject(outUri.getHost(), outUri.getPath(), this.reducer.openInputStream(curTempPath), meta);
            // MetricsSaver.addValue("S3WriteDelay", stopWatch.elapsedTime());
            // MetricsSaver.addValue("S3WriteBytes", status.getLen());
          } catch (Exception e) {
            // MetricsSaver.addValueWithError("S3WriteDelay", stopWatch.elapsedTime(), e);
          }
        }
      }
    } else {
      outStream = this.reducer.openOutputStream(finalPath);
    }

    if (outStream != null) {
      MessageDigest md = MessageDigest.getInstance("MD5");
      copyStream(inStream, outStream, md);
      outStream.close();
    }
    inStream.close();
  }

  private class ProcessedFile {
    public byte[] checksum;
    public Path path;

    public ProcessedFile(byte[] checksum, Path path) {
      this.checksum = checksum;
      this.path = path;
    }
  }
}

/*
 * Location: /Users/libinpan/Work/s3/s3distcp.jar Qualified Name:
 * com.amazon.external.elasticmapreduce.s3distcp.CopyFilesRunable JD-Core
 * Version: 0.6.2
 */