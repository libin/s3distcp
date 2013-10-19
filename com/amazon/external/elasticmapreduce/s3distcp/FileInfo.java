/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import org.apache.hadoop.io.LongWritable;
/*    */ import org.apache.hadoop.io.Text;
/*    */ import org.apache.hadoop.io.Writable;
/*    */ 
/*    */ class FileInfo extends WritableStruct
/*    */   implements Cloneable
/*    */ {
/*  8 */   public LongWritable fileUID = new LongWritable(0L);
/*  9 */   public Text inputFileName = new Text();
/* 10 */   public Text outputFileName = new Text();
/* 11 */   public LongWritable fileSize = new LongWritable(0L);
/*    */ 
/*    */   public FileInfo() {
/*    */   }
/*    */ 
/*    */   public FileInfo(Long fileUID, String inputUri, String outputUri, long fileSize) {
/* 17 */     this.fileUID = new LongWritable(fileUID.longValue());
/* 18 */     this.inputFileName = new Text(inputUri);
/* 19 */     this.outputFileName = new Text(outputUri);
/* 20 */     this.fileSize = new LongWritable(fileSize);
/*    */   }
/*    */ 
/*    */   public FileInfo clone()
/*    */   {
/* 25 */     return new FileInfo(Long.valueOf(this.fileUID.get()), this.inputFileName.toString(), this.outputFileName.toString(), this.fileSize.get());
/*    */   }
/*    */ 
/*    */   public Writable[] getFields()
/*    */   {
/* 30 */     return new Writable[] { this.fileUID, this.inputFileName, this.outputFileName, this.fileSize };
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 35 */     return "{" + this.fileUID + ", '" + this.inputFileName + "', '" + this.outputFileName + "', " + this.fileSize + "}";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.FileInfo
 * JD-Core Version:    0.6.2
 */