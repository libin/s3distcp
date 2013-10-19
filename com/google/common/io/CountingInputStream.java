/*    */ package com.google.common.io;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ @Beta
/*    */ public final class CountingInputStream extends FilterInputStream
/*    */ {
/*    */   private long count;
/* 35 */   private long mark = -1L;
/*    */ 
/*    */   public CountingInputStream(InputStream in)
/*    */   {
/* 43 */     super(in);
/*    */   }
/*    */ 
/*    */   public long getCount()
/*    */   {
/* 48 */     return this.count;
/*    */   }
/*    */ 
/*    */   public int read() throws IOException {
/* 52 */     int result = this.in.read();
/* 53 */     if (result != -1) {
/* 54 */       this.count += 1L;
/*    */     }
/* 56 */     return result;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b, int off, int len) throws IOException {
/* 60 */     int result = this.in.read(b, off, len);
/* 61 */     if (result != -1) {
/* 62 */       this.count += result;
/*    */     }
/* 64 */     return result;
/*    */   }
/*    */ 
/*    */   public long skip(long n) throws IOException {
/* 68 */     long result = this.in.skip(n);
/* 69 */     this.count += result;
/* 70 */     return result;
/*    */   }
/*    */ 
/*    */   public synchronized void mark(int readlimit) {
/* 74 */     this.in.mark(readlimit);
/* 75 */     this.mark = this.count;
/*    */   }
/*    */ 
/*    */   public synchronized void reset() throws IOException
/*    */   {
/* 80 */     if (!this.in.markSupported()) {
/* 81 */       throw new IOException("Mark not supported");
/*    */     }
/* 83 */     if (this.mark == -1L) {
/* 84 */       throw new IOException("Mark not set");
/*    */     }
/*    */ 
/* 87 */     this.in.reset();
/* 88 */     this.count = this.mark;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.CountingInputStream
 * JD-Core Version:    0.6.2
 */