/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ @Beta
/*     */ public final class LimitInputStream extends FilterInputStream
/*     */ {
/*     */   private long left;
/*  36 */   private long mark = -1L;
/*     */ 
/*     */   public LimitInputStream(InputStream in, long limit)
/*     */   {
/*  45 */     super(in);
/*  46 */     Preconditions.checkNotNull(in);
/*  47 */     Preconditions.checkArgument(limit >= 0L, "limit must be non-negative");
/*  48 */     this.left = limit;
/*     */   }
/*     */ 
/*     */   public int available() throws IOException {
/*  52 */     return (int)Math.min(this.in.available(), this.left);
/*     */   }
/*     */ 
/*     */   public synchronized void mark(int readlimit) {
/*  56 */     this.in.mark(readlimit);
/*  57 */     this.mark = this.left;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException
/*     */   {
/*  62 */     if (this.left == 0L) {
/*  63 */       return -1;
/*     */     }
/*     */ 
/*  66 */     int result = this.in.read();
/*  67 */     if (result != -1) {
/*  68 */       this.left -= 1L;
/*     */     }
/*  70 */     return result;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  74 */     if (this.left == 0L) {
/*  75 */       return -1;
/*     */     }
/*     */ 
/*  78 */     len = (int)Math.min(len, this.left);
/*  79 */     int result = this.in.read(b, off, len);
/*  80 */     if (result != -1) {
/*  81 */       this.left -= result;
/*     */     }
/*  83 */     return result;
/*     */   }
/*     */ 
/*     */   public synchronized void reset() throws IOException {
/*  87 */     if (!this.in.markSupported()) {
/*  88 */       throw new IOException("Mark not supported");
/*     */     }
/*  90 */     if (this.mark == -1L) {
/*  91 */       throw new IOException("Mark not set");
/*     */     }
/*     */ 
/*  94 */     this.in.reset();
/*  95 */     this.left = this.mark;
/*     */   }
/*     */ 
/*     */   public long skip(long n) throws IOException {
/*  99 */     n = Math.min(n, this.left);
/* 100 */     long skipped = this.in.skip(n);
/* 101 */     this.left -= skipped;
/* 102 */     return skipped;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.LimitInputStream
 * JD-Core Version:    0.6.2
 */