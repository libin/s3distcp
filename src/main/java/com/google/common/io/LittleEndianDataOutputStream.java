/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Longs;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.FilterOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ @Beta
/*     */ public class LittleEndianDataOutputStream extends FilterOutputStream
/*     */   implements DataOutput
/*     */ {
/*     */   public LittleEndianDataOutputStream(OutputStream out)
/*     */   {
/*  52 */     super(new DataOutputStream((OutputStream)Preconditions.checkNotNull(out)));
/*     */   }
/*     */ 
/*     */   public void write(byte[] b, int off, int len) throws IOException
/*     */   {
/*  57 */     this.out.write(b, off, len);
/*     */   }
/*     */ 
/*     */   public void writeBoolean(boolean v) throws IOException {
/*  61 */     ((DataOutputStream)this.out).writeBoolean(v);
/*     */   }
/*     */ 
/*     */   public void writeByte(int v) throws IOException {
/*  65 */     ((DataOutputStream)this.out).writeByte(v);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void writeBytes(String s)
/*     */     throws IOException
/*     */   {
/*  75 */     ((DataOutputStream)this.out).writeBytes(s);
/*     */   }
/*     */ 
/*     */   public void writeChar(int v)
/*     */     throws IOException
/*     */   {
/*  85 */     writeShort(v);
/*     */   }
/*     */ 
/*     */   public void writeChars(String s)
/*     */     throws IOException
/*     */   {
/*  96 */     for (int i = 0; i < s.length(); i++)
/*  97 */       writeChar(s.charAt(i));
/*     */   }
/*     */ 
/*     */   public void writeDouble(double v)
/*     */     throws IOException
/*     */   {
/* 109 */     writeLong(Double.doubleToLongBits(v));
/*     */   }
/*     */ 
/*     */   public void writeFloat(float v)
/*     */     throws IOException
/*     */   {
/* 120 */     writeInt(Float.floatToIntBits(v));
/*     */   }
/*     */ 
/*     */   public void writeInt(int v)
/*     */     throws IOException
/*     */   {
/* 131 */     this.out.write(0xFF & v);
/* 132 */     this.out.write(0xFF & v >> 8);
/* 133 */     this.out.write(0xFF & v >> 16);
/* 134 */     this.out.write(0xFF & v >> 24);
/*     */   }
/*     */ 
/*     */   public void writeLong(long v)
/*     */     throws IOException
/*     */   {
/* 145 */     byte[] bytes = Longs.toByteArray(Long.reverseBytes(v));
/* 146 */     write(bytes, 0, bytes.length);
/*     */   }
/*     */ 
/*     */   public void writeShort(int v)
/*     */     throws IOException
/*     */   {
/* 157 */     this.out.write(0xFF & v);
/* 158 */     this.out.write(0xFF & v >> 8);
/*     */   }
/*     */ 
/*     */   public void writeUTF(String str) throws IOException {
/* 162 */     ((DataOutputStream)this.out).writeUTF(str);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.LittleEndianDataOutputStream
 * JD-Core Version:    0.6.2
 */