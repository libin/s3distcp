/*     */ package com.amazon.external.elasticmapreduce.s3distcp;
/*     */ 
/*     */ //import amazon.emr.metrics.MetricsSaver;
/*     */ //import amazon.emr.metrics.MetricsSaver.StopWatch;
/*     */ import com.amazonaws.services.s3.AmazonS3Client;
/*     */ import com.amazonaws.services.s3.model.ObjectMetadata;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URI;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.UUID;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.hadoop.conf.Configuration;
/*     */ import org.apache.hadoop.fs.FileStatus;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ import org.apache.hadoop.fs.common.Abortable;
/*     */ import org.apache.hadoop.io.Text;
/*     */ import org.apache.hadoop.mapred.JobConf;
/*     */ 
/*     */ class CopyFilesRunable
/*     */   implements Runnable
/*     */ {
/*  49 */   private static final Log LOG = LogFactory.getLog(CopyFilesRunable.class);
/*     */   private final List<FileInfo> fileInfos;
/*     */   private final CopyFilesReducer reducer;
/*     */   private final String tempPath;
/*     */   private final Path finalPath;
/*     */ 
/*     */   public CopyFilesRunable(CopyFilesReducer reducer, List<FileInfo> fileInfos, Path tempPath, Path finalPath)
/*     */   {
/*  58 */     this.fileInfos = fileInfos;
/*  59 */     this.reducer = reducer;
/*  60 */     this.tempPath = tempPath.toString();
/*  61 */     this.finalPath = finalPath;
/*  62 */     LOG.info("Creating CopyFilesRunnable " + tempPath.toString() + ":" + finalPath.toString());
/*     */   }
/*     */ 
/*     */   private long copyStream(InputStream inputStream, OutputStream outputStream, MessageDigest md) throws IOException
/*     */   {
/*  67 */     long bytesCopied = 0L;
/*  68 */     //MetricsSaver.StopWatch stopWatch = new MetricsSaver.StopWatch();
/*     */     try {
/*  70 */       int len = 0;
/*  71 */       byte[] buffer = new byte[this.reducer.getBufferSize()];
/*  72 */       while ((len = inputStream.read(buffer)) > 0) {
/*  73 */         md.update(buffer, 0, len);
/*  74 */         outputStream.write(buffer, 0, len);
/*  75 */         this.reducer.progress();
/*  76 */         bytesCopied += len;
/*     */       }
/*  78 */       //MetricsSaver.addValue("S3DistCpCopyStreamDelay", stopWatch.elapsedTime());
/*  79 */       //MetricsSaver.addValue("S3DistCpCopyStreamBytes", bytesCopied);
/*     */     } catch (Exception e) {
/*  81 */       //MetricsSaver.addValueWithError("S3DistCpCopyStreamDelay", stopWatch.elapsedTime(), e);
/*  82 */       throw new IOException("exception raised while copying data file", e);
/*     */     }
/*  84 */     return bytesCopied;
/*     */   }
/*     */ 
/*     */   public ProcessedFile downloadAndMergeInputFiles() throws Exception {
/*  88 */     boolean finished = false;
/*  89 */     int numRetriesRemaining = this.reducer.getNumTransferRetries();
/*  90 */     byte[] digest = null;
/*  91 */     Path curTempPath = null;
/*     */ 
/*  93 */     while ((!finished) && (numRetriesRemaining > 0)) {
/*  94 */       numRetriesRemaining--;
/*  95 */       OutputStream outputStream = null;
/*     */ 
/*  98 */       curTempPath = new Path(this.tempPath + UUID.randomUUID());
/*     */       try {
/* 100 */         LOG.info("Opening temp file: " + curTempPath.toString());
/* 101 */         outputStream = this.reducer.openOutputStream(curTempPath);
/* 102 */         MessageDigest md = MessageDigest.getInstance("MD5");
/* 103 */         for (FileInfo fileInfo : this.fileInfos) {
/*     */           try {
/* 105 */             LOG.info("Starting download of " + fileInfo.inputFileName + " to " + curTempPath);
/* 106 */             InputStream inputStream = this.reducer.openInputStream(new Path(fileInfo.inputFileName.toString()));
/*     */             try {
/* 108 */               long bytesCopied = copyStream(inputStream, outputStream, md);
/* 109 */               LOG.info("Copied " + bytesCopied + " bytes");
/*     */             } finally {
/* 111 */               inputStream.close();
/*     */             }
/*     */           } catch (Exception e) {
/* 114 */             if ((outputStream != null) && ((outputStream instanceof Abortable))) {
/* 115 */               LOG.warn("Output stream is abortable, aborting the output stream for " + fileInfo.inputFileName);
/* 116 */               Abortable abortable = (Abortable)outputStream;
/* 117 */               abortable.abort();
/*     */             }
/* 119 */             throw e;
/*     */           }
/* 121 */           finished = true;
/* 122 */           LOG.info("Finished downloading " + fileInfo.inputFileName);
/*     */         }
/* 124 */         outputStream.close();
/* 125 */         digest = md.digest();
/* 126 */         return new ProcessedFile(digest, curTempPath);
/*     */       } catch (Exception e) {
/* 128 */         LOG.warn("Exception raised while copying file data to:  file=" + this.finalPath + " numRetriesRemaining=" + numRetriesRemaining, e);
/*     */         try
/*     */         {
/* 131 */           FileSystem fs = curTempPath.getFileSystem(this.reducer.getConf());
/* 132 */           fs.delete(curTempPath, false);
/*     */         }
/*     */         catch (IOException e1) {
/*     */         }
/* 136 */         if (numRetriesRemaining <= 0) throw e; 
/*     */       }
/*     */       finally {
/*     */         try { outputStream.close();
/*     */         } catch (Exception e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 145 */     return null;
/*     */   }
/*     */ 
/*     */   private static File[] getTempDirs(Configuration conf) {
/* 149 */     String[] backupDirs = conf.get("fs.s3.buffer.dir").split(",");
/* 150 */     List tempDirs = new ArrayList(backupDirs.length);
/* 151 */     int directoryIndex = 0;
/*     */ 
/* 153 */     File result = null;
/* 154 */     while (directoryIndex < backupDirs.length) {
/* 155 */       File dir = new File(backupDirs[directoryIndex]);
/* 156 */       dir.mkdirs();
/*     */       try {
/* 158 */         result = File.createTempFile("output-", ".tmp", dir);
/* 159 */         if (result != null) {
/* 160 */           tempDirs.add(new File(backupDirs[directoryIndex]));
/*     */         }
/* 162 */         result.delete();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/* 167 */       directoryIndex += 1;
/*     */     }
/*     */ 
/* 170 */     return (File[])tempDirs.toArray(new File[0]);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/* 175 */     int retriesRemaining = this.reducer.getNumTransferRetries();
/* 176 */     ProcessedFile processedFile = null;
/*     */     try
/*     */     {
/* 181 */       processedFile = downloadAndMergeInputFiles();
/*     */     } catch (Exception e) {
/* 183 */       LOG.warn("Error download input files. Not marking as committed", e);
/*     */     }
/*     */ 
/* 186 */     while (retriesRemaining > 0) {
/* 187 */       retriesRemaining--;
/*     */       try {
/* 189 */         Path curTempPath = processedFile.path;
/* 190 */         FileSystem inFs = curTempPath.getFileSystem(this.reducer.getConf());
/* 191 */         FileSystem outFs = this.finalPath.getFileSystem(this.reducer.getConf());
/* 192 */         if (inFs.getUri().equals(outFs.getUri())) {
/* 193 */           LOG.info("Renaming " + curTempPath.toString() + " to " + this.finalPath.toString());
/* 194 */           inFs.mkdirs(this.finalPath.getParent());
/* 195 */           inFs.rename(curTempPath, this.finalPath);
/*     */         } else {
/* 197 */           LOG.info("inFs.getUri()!=outFs.getUri(): " + inFs.getUri() + "!=" + outFs.getUri());
/* 198 */           copyToFinalDestination(curTempPath, this.finalPath, processedFile, inFs, outFs);
/*     */         }
/*     */ 
/* 201 */         for (FileInfo fileInfo : this.fileInfos) {
/* 202 */           this.reducer.markFileAsCommited(fileInfo);
/* 203 */           if (this.reducer.shouldDeleteOnSuccess()) {
/* 204 */             LOG.info("Deleting " + fileInfo.inputFileName);
/* 205 */             Path inPath = new Path(fileInfo.inputFileName.toString());
/* 206 */             FileSystem deleteFs = FileSystem.get(inPath.toUri(), this.reducer.getConf());
/* 207 */             deleteFs.delete(inPath, false);
/*     */           }
/*     */         }
/* 210 */         Path localTempPath = new Path(this.tempPath);
/* 211 */         FileSystem fs = localTempPath.getFileSystem(this.reducer.getConf());
/* 212 */         fs.delete(localTempPath, true);
/* 213 */         return;
/*     */       } catch (Exception e) {
/* 215 */         LOG.warn("Error processing files. Not marking as committed", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void copyToFinalDestination(Path curTempPath, Path finalPath, ProcessedFile processedFile, FileSystem inFs, FileSystem outFs) throws Exception
/*     */   {
/* 222 */     LOG.info("Copying " + curTempPath.toString() + " to " + finalPath.toString());
/* 223 */     byte[] digest = processedFile.checksum;
/* 224 */     InputStream inStream = this.reducer.openInputStream(curTempPath);
/* 225 */     OutputStream outStream = null;
/* 226 */     if (Utils.isS3Scheme(outFs.getUri().getScheme())) {
/* 227 */       FileStatus status = inFs.getFileStatus(curTempPath);
/* 228 */       URI outUri = finalPath.toUri();
/* 229 */       String bucket = outUri.getHost();
/*     */ 
/* 231 */       String key = outUri.getPath().substring(1);
/* 232 */       AmazonS3Client s3 = S3DistCp.createAmazonS3Client(this.reducer.getConf());
/* 233 */       s3.setEndpoint(this.reducer.getConf().get("fs.s3n.endpoint", "s3.amazonaws.com"));
/* 234 */       ObjectMetadata meta = new ObjectMetadata();
/* 235 */       meta.setContentLength(status.getLen());
/* 236 */       if (digest != null) {
/* 237 */         meta.setContentMD5(new String(Base64.encodeBase64(digest), Charset.forName("UTF-8")));
/*     */       }
/*     */ 
/* 240 */       if (this.reducer.shouldUseMutlipartUpload()) {
/* 241 */         int chunkSize = this.reducer.getMultipartSize();
/* 242 */         outStream = new MultipartUploadOutputStream(s3, Utils.createDefaultExecutorService(), this.reducer.getProgressable(), bucket, key, meta, chunkSize, getTempDirs(this.reducer.getConf()));
/*     */       }
/*     */       else {
/* 245 */         int retries = this.reducer.getNumTransferRetries();
/* 246 */         while (retries > 0) {
/* 247 */           //MetricsSaver.StopWatch stopWatch = new MetricsSaver.StopWatch();
/*     */           try {
/* 249 */             retries--;
/* 250 */             s3.putObject(outUri.getHost(), outUri.getPath(), this.reducer.openInputStream(curTempPath), meta);
/* 251 */             //MetricsSaver.addValue("S3WriteDelay", stopWatch.elapsedTime());
/* 252 */             //MetricsSaver.addValue("S3WriteBytes", status.getLen());
/*     */           }
/*     */           catch (Exception e) {
/* 255 */             //MetricsSaver.addValueWithError("S3WriteDelay", stopWatch.elapsedTime(), e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */     else {
/* 261 */       outStream = this.reducer.openOutputStream(finalPath);
/*     */     }
/*     */ 
/* 264 */     if (outStream != null) {
/* 265 */       MessageDigest md = MessageDigest.getInstance("MD5");
/* 266 */       copyStream(inStream, outStream, md);
/* 267 */       outStream.close();
/*     */     }
/* 269 */     inStream.close();
/*     */   }
/*     */ 
/*     */   private class ProcessedFile
/*     */   {
/*     */     public byte[] checksum;
/*     */     public Path path;
/*     */ 
/*     */     public ProcessedFile(byte[] checksum, Path path)
/*     */     {
/*  41 */       this.checksum = checksum;
/*  42 */       this.path = path;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.CopyFilesRunable
 * JD-Core Version:    0.6.2
 */