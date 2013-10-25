/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ public abstract class StreamUtils
/*     */ {
/*     */   public static final int DEFAULT_BUFFER_SIZE = 2048;
/*     */ 
/*     */   public static void copy(InputStream input, OutputStream output)
/*     */     throws IOException
/*     */   {
/*  66 */     copy(input, output, 2048);
/*     */   }
/*     */ 
/*     */   public static void copy(InputStream input, OutputStream output, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  78 */     byte[] buf = new byte[bufferSize];
/*  79 */     int bytesRead = input.read(buf);
/*  80 */     while (bytesRead != -1) {
/*  81 */       output.write(buf, 0, bytesRead);
/*  82 */       bytesRead = input.read(buf);
/*     */     }
/*  84 */     output.flush();
/*     */   }
/*     */ 
/*     */   public static void copyThenClose(InputStream input, OutputStream output)
/*     */     throws IOException
/*     */   {
/*  94 */     copy(input, output);
/*  95 */     input.close();
/*  96 */     output.close();
/*     */   }
/*     */ 
/*     */   public static byte[] getBytes(InputStream input)
/*     */     throws IOException
/*     */   {
/* 106 */     ByteArrayOutputStream result = new ByteArrayOutputStream();
/* 107 */     copy(input, result);
/* 108 */     result.close();
/* 109 */     return result.toByteArray();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.util.StreamUtils
 * JD-Core Version:    0.6.2
 */