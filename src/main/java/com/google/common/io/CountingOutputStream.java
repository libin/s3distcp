/*    */ package com.google.common.io;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.io.FilterOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ @Beta
/*    */ public final class CountingOutputStream extends FilterOutputStream
/*    */ {
/*    */   private long count;
/*    */ 
/*    */   public CountingOutputStream(OutputStream out)
/*    */   {
/* 42 */     super(out);
/*    */   }
/*    */ 
/*    */   public long getCount()
/*    */   {
/* 47 */     return this.count;
/*    */   }
/*    */ 
/*    */   public void write(byte[] b, int off, int len) throws IOException {
/* 51 */     this.out.write(b, off, len);
/* 52 */     this.count += len;
/*    */   }
/*    */ 
/*    */   public void write(int b) throws IOException {
/* 56 */     this.out.write(b);
/* 57 */     this.count += 1L;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.CountingOutputStream
 * JD-Core Version:    0.6.2
 */