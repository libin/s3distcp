/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import org.apache.hadoop.io.LongWritable;
/*    */ import org.apache.hadoop.io.Text;
/*    */ import org.apache.hadoop.io.Writable;
/*    */ 
/*    */ class CreateFileInfo extends WritableStruct
/*    */   implements Cloneable
/*    */ {
/*  8 */   public Text fileName = new Text();
/*  9 */   public LongWritable fileSize = new LongWritable();
/*    */ 
/*    */   public CreateFileInfo() {
/*    */   }
/*    */ 
/*    */   public CreateFileInfo(String fileName, long fileSize) {
/* 15 */     this.fileName = new Text(fileName);
/* 16 */     this.fileSize = new LongWritable(fileSize);
/*    */   }
/*    */ 
/*    */   public CreateFileInfo clone()
/*    */   {
/* 21 */     return new CreateFileInfo(this.fileName.toString(), this.fileSize.get());
/*    */   }
/*    */ 
/*    */   public Writable[] getFields()
/*    */   {
/* 26 */     return new Writable[] { this.fileName, this.fileSize };
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.CreateFileInfo
 * JD-Core Version:    0.6.2
 */