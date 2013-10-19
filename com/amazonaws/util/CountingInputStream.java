/*    */ package com.amazonaws.util;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class CountingInputStream extends FilterInputStream
/*    */ {
/* 25 */   private long byteCount = 0L;
/*    */ 
/*    */   public CountingInputStream(InputStream in) {
/* 28 */     super(in);
/*    */   }
/*    */ 
/*    */   public long getByteCount()
/*    */   {
/* 37 */     return this.byteCount;
/*    */   }
/*    */ 
/*    */   public int read() throws IOException
/*    */   {
/* 42 */     int tmp = super.read();
/* 43 */     this.byteCount += (tmp >= 0 ? 1L : 0L);
/* 44 */     return tmp;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b, int off, int len) throws IOException
/*    */   {
/* 49 */     int tmp = super.read(b, off, len);
/* 50 */     this.byteCount += (tmp >= 0 ? tmp : 0L);
/* 51 */     return tmp;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.CountingInputStream
 * JD-Core Version:    0.6.2
 */