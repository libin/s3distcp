/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.File;
/*    */ import java.io.FileInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.apache.hadoop.util.Progressable;
/*    */ 
/*    */ public class ProgressableResettableBufferedFileInputStream extends InputStream
/*    */ {
/*    */   protected File file;
/*    */   protected Progressable progressable;
/*    */   private BufferedInputStream inputStream;
/* 17 */   private long mark = 0L;
/* 18 */   private long pos = 0L;
/*    */ 
/*    */   public ProgressableResettableBufferedFileInputStream(File file, Progressable progressable) throws IOException {
/* 21 */     this.file = file;
/* 22 */     this.progressable = progressable;
/* 23 */     this.inputStream = new BufferedInputStream(new FileInputStream(file));
/*    */   }
/*    */ 
/*    */   public int available() throws IOException
/*    */   {
/* 28 */     return this.inputStream.available();
/*    */   }
/*    */ 
/*    */   public void close() throws IOException
/*    */   {
/* 33 */     this.inputStream.close();
/*    */   }
/*    */ 
/*    */   public synchronized void mark(int readlimit)
/*    */   {
/* 38 */     if (this.progressable != null) this.progressable.progress();
/* 39 */     this.mark = this.pos;
/*    */   }
/*    */ 
/*    */   public boolean markSupported()
/*    */   {
/* 44 */     if (this.progressable != null) this.progressable.progress();
/* 45 */     return true;
/*    */   }
/*    */ 
/*    */   public int read() throws IOException
/*    */   {
/* 50 */     if (this.progressable != null) this.progressable.progress();
/*    */ 
/* 52 */     int read = this.inputStream.read();
/* 53 */     if (read != -1) {
/* 54 */       this.pos += 1L;
/*    */     }
/* 56 */     return read;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b, int off, int len) throws IOException
/*    */   {
/* 61 */     if (this.progressable != null) this.progressable.progress();
/*    */ 
/* 63 */     int read = this.inputStream.read(b, off, len);
/* 64 */     if (read != -1) {
/* 65 */       this.pos += read;
/*    */     }
/* 67 */     return read;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b) throws IOException
/*    */   {
/* 72 */     if (this.progressable != null) this.progressable.progress();
/*    */ 
/* 74 */     int read = this.inputStream.read(b);
/* 75 */     if (read != -1) {
/* 76 */       this.pos += read;
/*    */     }
/* 78 */     return read;
/*    */   }
/*    */ 
/*    */   public synchronized void reset() throws IOException
/*    */   {
/* 83 */     if (this.progressable != null) this.progressable.progress();
/*    */ 
/* 85 */     this.inputStream.close();
/* 86 */     this.inputStream = new BufferedInputStream(new FileInputStream(this.file));
/* 87 */     this.pos = this.inputStream.skip(this.mark);
/*    */   }
/*    */ 
/*    */   public long skip(long n) throws IOException
/*    */   {
/* 92 */     if (this.progressable != null) this.progressable.progress();
/*    */ 
/* 94 */     long skipped = this.inputStream.skip(n);
/* 95 */     this.pos += skipped;
/* 96 */     return skipped;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.ProgressableResettableBufferedFileInputStream
 * JD-Core Version:    0.6.2
 */