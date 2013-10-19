/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import java.io.DataInput;
/*    */ import java.io.DataOutput;
/*    */ import java.io.IOException;
/*    */ import org.apache.hadoop.io.Writable;
/*    */ 
/*    */ abstract class WritableStruct
/*    */   implements Writable
/*    */ {
/*    */   public abstract Writable[] getFields();
/*    */ 
/*    */   public void readFields(DataInput input)
/*    */     throws IOException
/*    */   {
/* 21 */     for (Writable field : getFields())
/* 22 */       field.readFields(input);
/*    */   }
/*    */ 
/*    */   public void write(DataOutput output)
/*    */     throws IOException
/*    */   {
/* 28 */     for (Writable field : getFields())
/* 29 */       field.write(output);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.WritableStruct
 * JD-Core Version:    0.6.2
 */