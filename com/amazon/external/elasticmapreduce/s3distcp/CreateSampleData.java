/*     */ package com.amazon.external.elasticmapreduce.s3distcp;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.URI;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.hadoop.conf.Configuration;
/*     */ import org.apache.hadoop.fs.FileStatus;
/*     */ import org.apache.hadoop.fs.FileSystem;
/*     */ import org.apache.hadoop.fs.Path;
/*     */ import org.apache.hadoop.io.LongWritable;
/*     */ import org.apache.hadoop.io.SequenceFile;
/*     */ import org.apache.hadoop.io.SequenceFile.CompressionType;
/*     */ import org.apache.hadoop.io.SequenceFile.Writer;
/*     */ import org.apache.hadoop.mapred.FileInputFormat;
/*     */ import org.apache.hadoop.mapred.FileOutputFormat;
/*     */ import org.apache.hadoop.mapred.JobClient;
/*     */ import org.apache.hadoop.mapred.JobConf;
/*     */ import org.apache.hadoop.mapred.RunningJob;
/*     */ import org.apache.hadoop.mapred.SequenceFileInputFormat;
/*     */ import org.apache.hadoop.mapred.SequenceFileOutputFormat;
/*     */ import org.apache.hadoop.mapred.lib.IdentityReducer;
/*     */ import org.apache.hadoop.util.Tool;
/*     */ import org.apache.hadoop.util.ToolRunner;
/*     */ 
/*     */ public class CreateSampleData
/*     */   implements Tool
/*     */ {
/*  27 */   private static final Log LOG = LogFactory.getLog(CreateSampleData.class);
/*     */   protected JobConf conf;
/*     */ 
/*     */   public static void main(String[] args)
/*     */     throws Exception
/*     */   {
/*  30 */     JobConf job = new JobConf(CreateSampleData.class);
/*  31 */     CreateSampleData distcp = new CreateSampleData(job);
/*  32 */     int result = ToolRunner.run(distcp, args);
/*  33 */     System.exit(result);
/*     */   }
/*     */ 
/*     */   public CreateSampleData(JobConf conf)
/*     */   {
/*  40 */     this.conf = new JobConf(conf);
/*     */   }
/*     */ 
/*     */   void createFileList(Path inputFileListPath, String inputLocation, URI inputUri, URI outputUri) throws IOException {
/*  44 */     FileSystem inputFS = FileSystem.get(inputUri, this.conf);
/*  45 */     FileSystem inputFileListFS = FileSystem.get(inputFileListPath.toUri(), this.conf);
/*  46 */     Path inputPath = new Path(inputLocation);
/*     */ 
/*  48 */     LongWritable uid = new LongWritable(1L);
/*     */ 
/*  50 */     inputFileListFS.delete(inputFileListPath, true);
/*  51 */     inputFileListFS.mkdirs(inputFileListPath);
/*  52 */     SequenceFile.Writer fileInfoWriter = SequenceFile.createWriter(inputFileListFS, this.conf, inputFileListPath, LongWritable.class, FileInfo.class, SequenceFile.CompressionType.NONE);
/*     */     try
/*     */     {
/*  55 */       FileStatus[] contents = inputFS.listStatus(inputPath);
/*  56 */       for (FileStatus child : contents) {
/*  57 */         String inputFilePath = child.getPath().toString();
/*  58 */         String outputFilePath = join(outputUri.toString(), child.getPath().getName());
/*  59 */         FileInfo info = new FileInfo(Long.valueOf(uid.get()), inputFilePath, outputFilePath, child.getLen());
/*  60 */         fileInfoWriter.append(uid, info);
/*  61 */         uid.set(uid.get() + 1L);
/*     */       }
/*     */     } finally {
/*  64 */       fileInfoWriter.close();
/*     */     }
/*     */ 
/*  67 */     FileStatus[] fileListContents = inputFileListFS.listStatus(inputFileListPath);
/*  68 */     for (FileStatus status : fileListContents)
/*  69 */       LOG.info("fileListContents: " + status.getPath());
/*     */   }
/*     */ 
/*     */   private void createInputFiles(String inputPathString, long numFiles, long fileSize, String outputPath)
/*     */   {
/*     */     try {
/*  75 */       FileSystem fs = FileSystem.get(new URI(inputPathString), this.conf);
/*  76 */       fs.mkdirs(new Path(inputPathString));
/*  77 */       for (int fileNumber = 1; fileNumber <= numFiles; fileNumber++) {
/*  78 */         String inputFileName = join(inputPathString, Integer.valueOf(fileNumber));
/*  79 */         Path inputFilePath = new Path(inputFileName);
/*  80 */         fs.delete(inputFilePath, true);
/*  81 */         SequenceFile.Writer writer = SequenceFile.createWriter(fs, this.conf, inputFilePath, LongWritable.class, CreateFileInfo.class, SequenceFile.CompressionType.NONE);
/*     */         try
/*     */         {
/*  84 */           writer.append(new LongWritable(fileNumber), new CreateFileInfo(join(outputPath, Integer.valueOf(fileNumber)), fileSize));
/*     */         } finally {
/*  86 */           writer.close();
/*     */         }
/*     */       }
/*     */     } catch (Exception e) {
/*  90 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Configuration getConf()
/*     */   {
/*  96 */     return this.conf;
/*     */   }
/*     */ 
/*     */   private String join(String s, Integer t) {
/* 100 */     return join(s, t.toString());
/*     */   }
/*     */ 
/*     */   private String join(String s, String t)
/*     */   {
/* 107 */     if ((s.length() != 0) && (s.charAt(s.length() - 1) == '/')) {
/* 108 */       return s + t;
/*     */     }
/* 110 */     return s + "/" + t;
/*     */   }
/*     */ 
/*     */   public int run(String[] args)
/*     */     throws Exception
/*     */   {
/* 116 */     String outputLocation = args[0];
/*     */ 
/* 118 */     long numFiles = this.conf.getLong("createSampleData.numFiles", 5L);
/* 119 */     long fileSize = this.conf.getLong("createSampleData.fileSize", 104857600L);
/* 120 */     String jobName = this.conf.get("createSampleData.baseJobName", "CreateSampleData");
/* 121 */     String tmpPathString = this.conf.get("createSampleData.tmpDir", "hdfs:///tmp/createSampleData");
/* 122 */     String inputPathString = this.conf.get("createSampleData.workingInputDir", join(tmpPathString, "input"));
/* 123 */     String outputPathString = this.conf.get("createSampleData.workingOutputDir", join(tmpPathString, "output"));
/*     */ 
/* 126 */     FileSystem.get(new URI(outputPathString), this.conf).delete(new Path(outputPathString), true);
/*     */ 
/* 129 */     createInputFiles(inputPathString, numFiles, fileSize, outputLocation);
/* 130 */     return runCreateJob(inputPathString, outputPathString, jobName);
/*     */   }
/*     */ 
/*     */   int runCreateJob(String inputPathString, String outputPathString, String jobName) throws IOException {
/* 134 */     JobConf jobConf = new JobConf(this.conf);
/* 135 */     jobConf.setJobName(jobName);
/* 136 */     jobConf.setMapSpeculativeExecution(false);
/*     */ 
/* 138 */     FileInputFormat.addInputPath(jobConf, new Path(inputPathString));
/* 139 */     FileOutputFormat.setOutputPath(jobConf, new Path(outputPathString));
/*     */ 
/* 141 */     jobConf.setInputFormat(SequenceFileInputFormat.class);
/* 142 */     jobConf.setOutputKeyClass(LongWritable.class);
/* 143 */     jobConf.setOutputValueClass(CreateFileInfo.class);
/* 144 */     jobConf.setMapperClass(CreateFileMapper.class);
/* 145 */     jobConf.setReducerClass(IdentityReducer.class);
/* 146 */     jobConf.setOutputFormat(SequenceFileOutputFormat.class);
/*     */ 
/* 148 */     RunningJob result = JobClient.runJob(jobConf);
/* 149 */     return result.isSuccessful() ? 0 : -1;
/*     */   }
/*     */ 
/*     */   public void setConf(Configuration conf)
/*     */   {
/* 154 */     this.conf = new JobConf(conf);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.CreateSampleData
 * JD-Core Version:    0.6.2
 */