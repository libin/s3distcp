/*     */ package com.amazon.external.elasticmapreduce.s3distcp;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.net.URLDecoder;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import java.util.zip.GZIPOutputStream;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.hadoop.conf.Configuration;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ import org.apache.hadoop.io.LongWritable;
/*     */ import org.apache.hadoop.io.SequenceFile;
/*     */ import org.apache.hadoop.io.SequenceFile.CompressionType;
/*     */ import org.apache.hadoop.io.SequenceFile.Writer;
/*     */ 
/*     */ public class FileInfoListing
/*     */ {
/*  26 */   private static final Log LOG = LogFactory.getLog(FileInfoListing.class);
/*     */   private FileSystem fs;
/*     */   private SequenceFile.Writer writer;
/*  30 */   private Long fileIndex = Long.valueOf(0L);
/*  31 */   private long recordIndex = 0L;
/*  32 */   private Long recordsInThisFile = Long.valueOf(0L);
/*     */   private Long recordsPerFile;
/*     */   private Path tmpDir;
/*     */   private Configuration conf;
/*     */   private Path outputDir;
/*     */   private Path defaultSrcDir;
/*     */   private Pattern srcPattern;
/*     */   private Pattern groupBy;
/*     */   private OutputStream manifestStream;
/*     */   private Map<String, ManifestEntry> previousManifest;
/*  42 */   private final Gson gson = new Gson();
/*     */ 
/*     */   public FileInfoListing(Configuration conf, Path srcDir, Path tmpDir, Path outputDir, long startingIndex, File manifestFile, Map<String, ManifestEntry> previousManifest)
/*     */     throws IOException
/*     */   {
/*  47 */     this.conf = conf;
/*  48 */     this.defaultSrcDir = srcDir;
/*  49 */     this.tmpDir = tmpDir;
/*  50 */     this.outputDir = outputDir;
/*  51 */     this.recordsPerFile = Long.valueOf(500000L);
/*  52 */     this.recordIndex = startingIndex;
/*  53 */     this.previousManifest = previousManifest;
/*  54 */     if (manifestFile != null)
/*  55 */       this.manifestStream = new GZIPOutputStream(new FileOutputStream(manifestFile));
/*     */   }
/*     */ 
/*     */   public void openNewFile()
/*     */   {
/*     */     try {
/*  61 */       if (this.writer != null) {
/*  62 */         this.writer.close();
/*     */       }
/*  64 */       this.fileIndex = Long.valueOf(this.fileIndex.longValue() + 1L);
/*  65 */       this.recordsInThisFile = Long.valueOf(0L);
/*  66 */       this.fs = FileSystem.get(this.tmpDir.toUri(), this.conf);
/*  67 */       Path path = new Path(this.tmpDir, this.fileIndex.toString());
/*  68 */       LOG.info(new StringBuilder().append("Opening new file: ").append(path.toString()).toString());
/*  69 */       this.writer = SequenceFile.createWriter(this.fs, this.conf, path, LongWritable.class, FileInfo.class, SequenceFile.CompressionType.NONE);
/*     */     }
/*     */     catch (IOException e) {
/*  72 */       throw new RuntimeException(new StringBuilder().append("Unable to open new file for writing").append(new Path(this.tmpDir, this.fileIndex.toString()).toString()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void add(Path filePath, long fileSize)
/*     */   {
/*  78 */     add(filePath, this.defaultSrcDir, fileSize);
/*     */   }
/*     */ 
/*     */   public void add(Path filePath, Path srcDir, long fileSize) {
/*  82 */     String filePathString = filePath.toString();
/*  83 */     if (this.srcPattern != null) {
/*  84 */       Matcher matcher = this.srcPattern.matcher(filePathString);
/*  85 */       if (!matcher.matches()) {
/*  86 */         return;
/*     */       }
/*     */     }
/*     */ 
/*  90 */     if (this.groupBy != null) {
/*  91 */       Matcher matcher = this.groupBy.matcher(filePathString);
/*  92 */       if (!matcher.matches()) {
/*  93 */         return;
/*     */       }
/*  95 */       int numGroups = matcher.groupCount();
/*  96 */       StringBuilder builder = new StringBuilder();
/*  97 */       for (int i = 0; i < numGroups; i++) {
/*  98 */         builder.append(matcher.group(i + 1));
/*     */       }
/* 100 */       if (builder.toString().length() == 0) {
/* 101 */         return;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 106 */     if ((this.writer == null) || (this.recordsInThisFile.longValue() > this.recordsPerFile.longValue())) {
/* 107 */       openNewFile();
/*     */     }
/* 109 */     this.recordIndex += 1L;
/* 110 */     this.recordsInThisFile = Long.valueOf(this.recordsInThisFile.longValue() + 1L);
/* 111 */     String outputFilePath = getOutputFilePath(filePath, srcDir);
/* 112 */     String basePath = getBaseName(filePath, srcDir);
/* 113 */     String manifestSrcDir = this.outputDir.toString();
/*     */     try {
/* 115 */       FileInfo fileInfo = new FileInfo(Long.valueOf(this.recordIndex), filePathString, outputFilePath, fileSize);
/*     */ 
/* 117 */       LOG.debug(new StringBuilder().append("Adding ").append(fileInfo).toString());
/* 118 */       if ((this.previousManifest != null) && (this.previousManifest.containsKey(basePath)) && (((ManifestEntry)this.previousManifest.get(basePath)).size == fileSize))
/*     */       {
/* 121 */         outputFilePath = ((ManifestEntry)this.previousManifest.get(basePath)).path;
/* 122 */         manifestSrcDir = ((ManifestEntry)this.previousManifest.get(basePath)).srcDir;
/*     */       } else {
/* 124 */         this.writer.append(new LongWritable(this.recordIndex), fileInfo);
/*     */       }
/* 126 */       if (this.manifestStream != null) {
/* 127 */         ManifestEntry entry = new ManifestEntry(URLDecoder.decode(outputFilePath, "UTF-8"), URLDecoder.decode(basePath, "UTF-8"), manifestSrcDir, fileSize);
/*     */ 
/* 129 */         String outLine = new StringBuilder().append(this.gson.toJson(entry)).append("\n").toString();
/* 130 */         this.manifestStream.write(outLine.getBytes("utf-8"));
/*     */       }
/*     */     } catch (IOException e) {
/* 133 */       throw new RuntimeException(new StringBuilder().append("Unable to write file copy entry ").append(filePathString).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private String getBaseName(Path filePath, Path srcDir)
/*     */   {
/* 139 */     String filePathString = filePath.toString();
/* 140 */     String suffix = filePathString;
/* 141 */     String srcDirString = srcDir.toString();
/* 142 */     if (filePathString.startsWith(srcDirString)) {
/* 143 */       suffix = filePathString.substring(srcDirString.length());
/* 144 */       if (suffix.startsWith("/")) {
/* 145 */         suffix = suffix.substring(1);
/*     */       }
/*     */     }
/* 148 */     return suffix;
/*     */   }
/*     */ 
/*     */   private String getOutputFilePath(Path filePath, Path srcDir) {
/* 152 */     String suffix = getBaseName(filePath, srcDir);
/* 153 */     LOG.debug(new StringBuilder().append("outputDir: '").append(this.outputDir).append("'").toString());
/* 154 */     LOG.debug(new StringBuilder().append("suffix: '").append(suffix).append("'").toString());
/* 155 */     LOG.debug(new StringBuilder().append("Output path: '").append(new Path(this.outputDir, suffix).toString()).toString());
/* 156 */     return new Path(this.outputDir, suffix).toString();
/*     */   }
/*     */ 
/*     */   public void close() {
/*     */     try {
/* 161 */       if (this.writer != null) {
/* 162 */         this.writer.close();
/*     */       }
/* 164 */       if (this.manifestStream != null)
/* 165 */         this.manifestStream.close();
/*     */     }
/*     */     catch (IOException e) {
/* 168 */       throw new RuntimeException("Unable to close fileInfo writer", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Long getRecordsPerFile() {
/* 173 */     return this.recordsPerFile;
/*     */   }
/*     */ 
/*     */   public void setRecordsPerFile(Long recordsPerFile) {
/* 177 */     this.recordsPerFile = recordsPerFile;
/*     */   }
/*     */ 
/*     */   public Pattern getSrcPattern() {
/* 181 */     return this.srcPattern;
/*     */   }
/*     */ 
/*     */   public void setSrcPattern(Pattern srcPattern) {
/* 185 */     this.srcPattern = srcPattern;
/*     */   }
/*     */ 
/*     */   public Pattern getGroupBy() {
/* 189 */     return this.groupBy;
/*     */   }
/*     */ 
/*     */   public void setGroupBy(Pattern groupBy) {
/* 193 */     this.groupBy = groupBy;
/*     */   }
/*     */ 
/*     */   public Long getFileIndex() {
/* 197 */     return this.fileIndex;
/*     */   }
/*     */ 
/*     */   public Long getRecordIndex() {
/* 201 */     return Long.valueOf(this.recordIndex);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.FileInfoListing
 * JD-Core Version:    0.6.2
 */