/*    */ package com.amazonaws.util;
/*    */ 
/*    */ import java.io.FilterInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.util.zip.CRC32;
/*    */ 
/*    */ public class CRC32ChecksumCalculatingInputStream extends FilterInputStream
/*    */ {
/*    */   private CRC32 crc32;
/*    */ 
/*    */   public CRC32ChecksumCalculatingInputStream(InputStream in)
/*    */   {
/* 32 */     super(in);
/* 33 */     this.crc32 = new CRC32();
/*    */   }
/*    */ 
/*    */   public long getCRC32Checksum() {
/* 37 */     return this.crc32.getValue();
/*    */   }
/*    */ 
/*    */   public synchronized void reset()
/*    */     throws IOException
/*    */   {
/* 47 */     this.crc32.reset();
/* 48 */     this.in.reset();
/*    */   }
/*    */ 
/*    */   public int read()
/*    */     throws IOException
/*    */   {
/* 56 */     int ch = this.in.read();
/* 57 */     if (ch != -1) {
/* 58 */       this.crc32.update(ch);
/*    */     }
/* 60 */     return ch;
/*    */   }
/*    */ 
/*    */   public int read(byte[] b, int off, int len)
/*    */     throws IOException
/*    */   {
/* 68 */     int result = this.in.read(b, off, len);
/* 69 */     if (result != -1) {
/* 70 */       this.crc32.update(b, off, result);
/*    */     }
/* 72 */     return result;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.CRC32ChecksumCalculatingInputStream
 * JD-Core Version:    0.6.2
 */