/*     */ package com.google.common.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ final class MultiInputStream extends InputStream
/*     */ {
/*     */   private Iterator<? extends InputSupplier<? extends InputStream>> it;
/*     */   private InputStream in;
/*     */ 
/*     */   public MultiInputStream(Iterator<? extends InputSupplier<? extends InputStream>> it)
/*     */     throws IOException
/*     */   {
/*  43 */     this.it = it;
/*  44 */     advance();
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/*  48 */     if (this.in != null)
/*     */       try {
/*  50 */         this.in.close();
/*     */       } finally {
/*  52 */         this.in = null;
/*     */       }
/*     */   }
/*     */ 
/*     */   private void advance()
/*     */     throws IOException
/*     */   {
/*  61 */     close();
/*  62 */     if (this.it.hasNext())
/*  63 */       this.in = ((InputStream)((InputSupplier)this.it.next()).getInput());
/*     */   }
/*     */ 
/*     */   public int available() throws IOException
/*     */   {
/*  68 */     if (this.in == null) {
/*  69 */       return 0;
/*     */     }
/*  71 */     return this.in.available();
/*     */   }
/*     */ 
/*     */   public boolean markSupported() {
/*  75 */     return false;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException {
/*  79 */     if (this.in == null) {
/*  80 */       return -1;
/*     */     }
/*  82 */     int result = this.in.read();
/*  83 */     if (result == -1) {
/*  84 */       advance();
/*  85 */       return read();
/*     */     }
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len) throws IOException {
/*  91 */     if (this.in == null) {
/*  92 */       return -1;
/*     */     }
/*  94 */     int result = this.in.read(b, off, len);
/*  95 */     if (result == -1) {
/*  96 */       advance();
/*  97 */       return read(b, off, len);
/*     */     }
/*  99 */     return result;
/*     */   }
/*     */ 
/*     */   public long skip(long n) throws IOException {
/* 103 */     if ((this.in == null) || (n <= 0L)) {
/* 104 */       return 0L;
/*     */     }
/* 106 */     long result = this.in.skip(n);
/* 107 */     if (result != 0L) {
/* 108 */       return result;
/*     */     }
/* 110 */     if (read() == -1) {
/* 111 */       return 0L;
/*     */     }
/* 113 */     return 1L + this.in.skip(n - 1L);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.MultiInputStream
 * JD-Core Version:    0.6.2
 */