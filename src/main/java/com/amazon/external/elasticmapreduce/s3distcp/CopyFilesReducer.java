/*     */ package com.amazon.external.elasticmapreduce.s3distcp;
/*     */ 
/*     */ import com.google.common.collect.Lists;
/*     */ //import com.hadoop.compression.lzo.LzopCodec;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ import java.util.zip.GZIPInputStream;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configurable;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ import org.apache.hadoop.io.LongWritable;
/*     */ import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.BZip2Codec;
/*     */ import org.apache.hadoop.io.compress.SnappyCodec;
/*     */ import org.apache.hadoop.mapred.JobConf;
/*     */ import org.apache.hadoop.mapred.OutputCollector;
/*     */ import org.apache.hadoop.mapred.Reducer;
/*     */ import org.apache.hadoop.mapred.Reporter;
/*     */ import org.apache.hadoop.util.Progressable;
/*     */ 
/*     */ public class CopyFilesReducer
/*     */   implements Reducer<Text, FileInfo, Text, Text>
/*     */ {
/*  32 */   private static final Log LOG = LogFactory.getLog(CopyFilesReducer.class);
/*  33 */   private static final List<String> validCodecs = Lists.newArrayList(new String[] { "snappy", "gz", "lzo", "lzop", "gzip" });
/*     */   private OutputCollector<Text, Text> collector;
/*     */   private Reporter reporter;
/*     */   private SimpleExecutor transferQueue;
/*     */   private Set<FileInfo> uncommitedFiles;
/*     */   private String tempDir;
/*     */   private long targetSize;
/*     */   private int bufferSize;
/*     */   private int numTransferRetries;
/*     */   private int multipartSize;
/*     */   private String outputCodec;
/*     */   private boolean deleteOnSuccess;
/*     */   private boolean useMultipartUpload;
/*     */   private boolean numberFiles;
/*     */   private JobConf conf;
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/*  56 */     this.transferQueue.close();
/*  57 */     synchronized (this) {
/*  58 */       LOG.warn("CopyFilesReducer uncommitted file " + this.uncommitedFiles.size());
/*  59 */       for (FileInfo fileInfo : this.uncommitedFiles) {
/*  60 */         LOG.warn("failed to upload " + fileInfo.inputFileName);
/*  61 */         this.collector.collect(fileInfo.outputFileName, fileInfo.inputFileName);
/*     */       }
/*     */ 
/*  65 */       if (this.uncommitedFiles.size() > 0) {
/*  66 */         String message = String.format("Reducer task failed to copy %d files: %s etc", new Object[] { Integer.valueOf(this.uncommitedFiles.size()), ((FileInfo)this.uncommitedFiles.iterator().next()).inputFileName });
/*     */ 
/*  70 */         throw new RuntimeException(message);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public JobConf getConf() {
/*  76 */     return this.conf;
/*     */   }
/*     */ 
/*     */   public boolean shouldDeleteOnSuccess() {
/*  80 */     return this.deleteOnSuccess;
/*     */   }
/*     */ 
/*     */   public boolean shouldUseMutlipartUpload() {
/*  84 */     return this.useMultipartUpload;
/*     */   }
/*     */ 
/*     */   public int getMultipartSize() {
/*  88 */     return this.multipartSize;
/*     */   }
/*     */ 
/*     */   public void configure(JobConf conf)
/*     */   {
/*  93 */     this.conf = conf;
/*  94 */     int queueSize = conf.getInt("s3DistCp.copyfiles.mapper.queueSize", 10);
/*  95 */     int numWorkers = conf.getInt("s3DistCp.copyfiles.mapper.numWorkers", 5);
/*  96 */     this.tempDir = (conf.get("s3DistCp.copyfiles.reducer.tempDir", "hdfs:///tmp") + "/" + "tempspace");
/*  97 */     this.bufferSize = conf.getInt("s3DistCp.copyfiles.mapper.bufferSize", 1048576);
/*  98 */     this.targetSize = conf.getLong("s3DistCp.copyfiles.reducer.targetSize", 9223372036854775807L);
/*  99 */     this.outputCodec = conf.get("s3DistCp.copyfiles.reducer.outputCodec").toLowerCase();
/* 100 */     this.numberFiles = conf.getBoolean("s3DistCp.copyfiles.reducer.numberFiles", false);
/* 101 */     this.transferQueue = new SimpleExecutor(queueSize, numWorkers);
/* 102 */     this.multipartSize = conf.getInt("s3DistCp.copyFiles.multipartUploadPartSize", 16777216);
/* 103 */     this.uncommitedFiles = new HashSet();
/* 104 */     this.deleteOnSuccess = conf.getBoolean("s3DistCp.copyFiles.deleteFilesOnSuccess", false);
/* 105 */     this.numTransferRetries = conf.getInt("s3DistCp.copyfiles.mapper.numRetries", 10);
/* 106 */     this.useMultipartUpload = conf.getBoolean("s3DistCp.copyFiles.useMultipartUploads", true);
/*     */   }
/*     */ 
/*     */   public int getNumTransferRetries()
/*     */   {
/* 111 */     return this.numTransferRetries;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 116 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public boolean shouldReencodeFiles() {
/* 120 */     return validCodecs.contains(this.outputCodec);
/*     */   }
/*     */ 
/*     */   private String makeFinalPath(long fileUid, String finalDir, String groupId, String groupIndex) {
/*     */ 
/* 127 */     if (this.numberFiles) {
	/* 124 */     String[] groupIds = groupId.split("/");
	/* 125 */     groupId = fileUid + groupIds[(groupIds.length - 1)];
/*     */     }
/*     */ 	
/* 131 */     if (!this.outputCodec.equalsIgnoreCase("keep"))
/*     */     {
				if (groupIndex != null && !groupIndex.isEmpty())
					groupIndex = "." + groupIndex;
/*     */       String suffix;
/* 133 */       if (this.outputCodec.equalsIgnoreCase("gzip")) {
/* 134 */         suffix = groupIndex + ".gz";
/*     */       }
/*     */       else
/*     */       {
/* 135 */         if (this.outputCodec.equalsIgnoreCase("none"))
/* 136 */           suffix = groupIndex;
/*     */         else
/* 138 */           suffix = groupIndex + "." + this.outputCodec;
/*     */       }
/* 140 */       return finalDir + "/" + groupId + suffix;
/*     */     }
/* 142 */     String suffix = Utils.getSuffix(groupId);
/* 143 */     String name = groupId;
/* 144 */     if (!groupIndex.isEmpty()) {
/* 145 */       name = Utils.replaceSuffix(name, "." + groupIndex);
/* 146 */       if (suffix.length() > 0) {
/* 147 */         name = name + "." + suffix;
/*     */       }
/*     */     }
/*     */ 
/* 151 */     return finalDir + "/" + name;
/*     */   }
/*     */ 
/*     */   public void reduce(Text groupKey, Iterator<FileInfo> fileInfos, OutputCollector<Text, Text> collector, Reporter reporter)
/*     */     throws IOException
/*     */   {	
/* 157 */     this.collector = collector;
/* 158 */     this.reporter = reporter;
/* 159 */     long curSize = 0L;
/* 160 */     int groupNum = 0;
/* 161 */     int numFiles = 0;
/* 162 */     List curFiles = new ArrayList();
/* 163 */     while (fileInfos.hasNext()) {
/* 164 */       FileInfo fileInfo = ((FileInfo)fileInfos.next()).clone();
/* 165 */       numFiles++;
/* 166 */       curSize += fileInfo.fileSize.get();
/* 167 */       curFiles.add(fileInfo);
/* 168 */       if (curSize >= this.targetSize) {
/* 169 */         String groupId = groupKey.toString();
/* 170 */         Path tempPath = new Path(this.tempDir + "/" + groupId);
/* 171 */         Path finalPath = new Path(fileInfo.outputFileName.toString()).getParent();
/* 172 */         String groupIndex = Integer.toString(groupNum);
/* 173 */         if ((numFiles == 1) && 
/* 174 */           (!fileInfos.hasNext())) {
/* 175 */           groupIndex = "";
/*     */         }
/*     */ 
/* 178 */         finalPath = new Path(makeFinalPath(fileInfo.fileUID.get(), finalPath.toString(), groupId, groupIndex));
/* 179 */         LOG.warn("tempPath:" + tempPath + " finalPath:" + finalPath);
/* 180 */         executeDownloads(this, curFiles, tempPath, finalPath);
/* 181 */         groupNum++;
/* 182 */         curFiles = new ArrayList();
/* 183 */         curSize = 0L;
/*     */       }
/*     */     }
/* 186 */     if (!curFiles.isEmpty()) {
/* 187 */       String groupId = groupKey.toString();
/* 188 */       Path tempPath = new Path(this.tempDir + "/" + UUID.randomUUID());
/* 189 */       Path intermediateFinal = new Path(((FileInfo)curFiles.get(0)).outputFileName.toString()).getParent();
/* 190 */       LOG.warn("tempPath:" + tempPath + " interPath:" + intermediateFinal);
/* 191 */       String groupIndex = Integer.toString(groupNum);
/* 192 */       if (numFiles == 1) {
/* 193 */         groupIndex = "";
/*     */       }
/* 195 */       Path finalPath = new Path(makeFinalPath(((FileInfo)curFiles.get(0)).fileUID.get(), intermediateFinal.toString(), groupId, groupIndex));
/* 196 */       executeDownloads(this, curFiles, tempPath, finalPath);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void executeDownloads(CopyFilesReducer reducer, List<FileInfo> fileInfos, Path tempPath, Path finalPath) {
/* 201 */     synchronized (this) {
/* 202 */       for (FileInfo fileInfo : fileInfos) {
/* 203 */         this.uncommitedFiles.add(fileInfo);
/* 204 */         LOG.warn("Processing object: " + fileInfo.inputFileName.toString());
/*     */       }
/*     */     }
/* 207 */     if (fileInfos.size() > 0) {
/* 208 */       LOG.warn("Processing " + fileInfos.size() + " files");
try {
CopyFilesRunable runnable = new CopyFilesRunable(reducer, fileInfos, tempPath, finalPath);
/* 208 */       LOG.warn("running: " + runnable + " with transferQueue: " + transferQueue.toString());
/* 209 */       this.transferQueue.execute(runnable);
} catch (Throwable e) {
	LOG.error("error", e);
}
/*     */     } else {
/* 211 */       LOG.warn("No files to process");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void markFileAsCommited(FileInfo fileInfo) {
/* 216 */     LOG.warn("commit " + fileInfo.inputFileName);
/* 217 */     synchronized (this) {
/* 218 */       this.uncommitedFiles.remove(fileInfo);
/* 219 */       progress();
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputStream openInputStream(Path inputFilePath) throws IOException {
/* 224 */     FileSystem inputFs = inputFilePath.getFileSystem(this.conf);
/* 225 */     InputStream inputStream = inputFs.open(inputFilePath);
/*     */ 
/* 227 */     if (!this.outputCodec.equalsIgnoreCase("keep")) {
/* 228 */       String suffix = Utils.getSuffix(inputFilePath.getName());
/* 229 */       if (suffix.equalsIgnoreCase("gz"))
/* 230 */         return new GZIPInputStream(inputStream);
/* 229 */       if (suffix.equalsIgnoreCase("bz2"))	 {
					BZip2Codec codec = new BZip2Codec();
					if (codec instanceof Configurable)
						((Configurable)codec).setConf(getConf());
/* 230 */         	return codec.createInputStream(inputStream);
				}
/* 231 */       if (suffix.equalsIgnoreCase("snappy")) {
/* 232 */         SnappyCodec codec = new SnappyCodec();
/* 233 */         codec.setConf(getConf());
/* 234 */         return codec.createInputStream(inputStream);
/* 235 */       }

               // if ((suffix.equalsIgnoreCase("lzop")) || (suffix.equalsIgnoreCase("lzo"))) {
/* 236 */      //   LzopCodec codec = new LzopCodec();
/* 237 */      //   codec.setConf(getConf());
/* 238 */      //   return codec.createInputStream(inputStream);
/*     */      // }
/*     */     }
/* 241 */     return inputStream;
/*     */   }
/*     */ 
/*     */   public OutputStream openOutputStream(Path outputFilePath) throws IOException {
/* 245 */     FileSystem outputFs = outputFilePath.getFileSystem(this.conf);
/* 246 */     OutputStream outputStream = outputFs.create(outputFilePath, this.reporter);
/* 247 */     if ((this.outputCodec.equalsIgnoreCase("gzip")) || (this.outputCodec.equalsIgnoreCase("gz")))
/* 248 */       return new GZIPOutputStream(outputStream);
/* 249 */     //if (this.outputCodec.equalsIgnoreCase("lzo")) {
/* 250 */       //LzopCodec codec = new LzopCodec();
/* 251 */       //codec.setConf(getConf());
/* 252 */       //return codec.createOutputStream(outputStream);
/* 253 */     //}
              if (this.outputCodec.equalsIgnoreCase("snappy")) {
/* 254 */       SnappyCodec codec = new SnappyCodec();
/* 255 */       codec.setConf(getConf());
/* 256 */       return codec.createOutputStream(outputStream);
/*     */     }
/* 258 */     return outputStream;
/*     */   }
/*     */ 
/*     */   public Progressable getProgressable() {
/* 262 */     return this.reporter;
/*     */   }
/*     */ 
/*     */   public void progress() {
/* 266 */     this.reporter.progress();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.CopyFilesReducer
 * JD-Core Version:    0.6.2
 */