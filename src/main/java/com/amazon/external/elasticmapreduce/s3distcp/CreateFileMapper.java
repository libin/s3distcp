/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import org.apache.hadoop.fs.FSDataOutputStream;
/*    */ import org.apache.hadoop.fs.FileSystem;
/*    */ import org.apache.hadoop.fs.Path;
/*    */ import org.apache.hadoop.io.LongWritable;
/*    */ import org.apache.hadoop.io.Text;
/*    */ import org.apache.hadoop.mapred.JobConf;
/*    */ import org.apache.hadoop.mapred.Mapper;
/*    */ import org.apache.hadoop.mapred.OutputCollector;
/*    */ import org.apache.hadoop.mapred.Reporter;
/*    */ 
/*    */ public class CreateFileMapper
/*    */   implements Mapper<LongWritable, CreateFileInfo, LongWritable, CreateFileInfo>
/*    */ {
/*    */   protected JobConf conf;
/*    */ 
/*    */   public void close()
/*    */     throws IOException
/*    */   {
/*    */   }
/*    */ 
/*    */   public void configure(JobConf conf)
/*    */   {
/* 26 */     this.conf = conf;
/*    */   }
/*    */ 
/*    */   public void map(LongWritable key, CreateFileInfo value, OutputCollector<LongWritable, CreateFileInfo> output, Reporter reporter)
/*    */     throws IOException
/*    */   {
/*    */     try
/*    */     {
/* 34 */       FileSystem fs = FileSystem.get(new URI(value.fileName.toString()), this.conf);
/* 35 */       FSDataOutputStream outputFile = fs.create(new Path(value.fileName.toString()));
/* 36 */       long bytesLeftToWrite = value.fileSize.get();
/* 37 */       byte[] buffer = new byte[12582912];
/* 38 */       for (int i = 0; (i < buffer.length) && (i < bytesLeftToWrite); i++) {
/* 39 */         buffer[i] = ((byte)(i % 127));
/*    */       }
/* 41 */       while (bytesLeftToWrite > buffer.length) {
/* 42 */         outputFile.write(buffer);
/* 43 */         bytesLeftToWrite -= buffer.length;
/* 44 */         reporter.progress();
/*    */       }
/* 46 */       if (bytesLeftToWrite > 0L) {
/* 47 */         outputFile.write(buffer, 0, (int)bytesLeftToWrite);
/* 48 */         bytesLeftToWrite = 0L;
/*    */       }
/*    */     }
/*    */     catch (URISyntaxException e) {
/* 52 */       throw new RuntimeException(e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.CreateFileMapper
 * JD-Core Version:    0.6.2
 */