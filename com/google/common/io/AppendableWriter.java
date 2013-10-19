/*     */ package com.google.common.io;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.Flushable;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ 
/*     */ class AppendableWriter extends Writer
/*     */ {
/*     */   private final Appendable target;
/*     */   private boolean closed;
/*     */ 
/*     */   AppendableWriter(Appendable target)
/*     */   {
/*  43 */     this.target = target;
/*     */   }
/*     */ 
/*     */   public void write(char[] cbuf, int off, int len)
/*     */     throws IOException
/*     */   {
/*  52 */     checkNotClosed();
/*     */ 
/*  55 */     this.target.append(new String(cbuf, off, len));
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException {
/*  59 */     checkNotClosed();
/*  60 */     if ((this.target instanceof Flushable))
/*  61 */       ((Flushable)this.target).flush();
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/*  66 */     this.closed = true;
/*  67 */     if ((this.target instanceof Closeable))
/*  68 */       ((Closeable)this.target).close();
/*     */   }
/*     */ 
/*     */   public void write(int c)
/*     */     throws IOException
/*     */   {
/*  78 */     checkNotClosed();
/*  79 */     this.target.append((char)c);
/*     */   }
/*     */ 
/*     */   public void write(String str) throws IOException {
/*  83 */     checkNotClosed();
/*  84 */     this.target.append(str);
/*     */   }
/*     */ 
/*     */   public void write(String str, int off, int len) throws IOException {
/*  88 */     checkNotClosed();
/*     */ 
/*  90 */     this.target.append(str, off, off + len);
/*     */   }
/*     */ 
/*     */   public Writer append(char c) throws IOException {
/*  94 */     checkNotClosed();
/*  95 */     this.target.append(c);
/*  96 */     return this;
/*     */   }
/*     */ 
/*     */   public Writer append(CharSequence charSeq) throws IOException {
/* 100 */     checkNotClosed();
/* 101 */     this.target.append(charSeq);
/* 102 */     return this;
/*     */   }
/*     */ 
/*     */   public Writer append(CharSequence charSeq, int start, int end) throws IOException
/*     */   {
/* 107 */     checkNotClosed();
/* 108 */     this.target.append(charSeq, start, end);
/* 109 */     return this;
/*     */   }
/*     */ 
/*     */   private void checkNotClosed() throws IOException {
/* 113 */     if (this.closed)
/* 114 */       throw new IOException("Cannot write to a closed writer.");
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.AppendableWriter
 * JD-Core Version:    0.6.2
 */