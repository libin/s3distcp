/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Throwables;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ 
/*     */ abstract class AbstractNonStreamingHashFunction
/*     */   implements HashFunction
/*     */ {
/*     */   public Hasher newHasher()
/*     */   {
/*  35 */     return new BufferingHasher(32);
/*     */   }
/*     */ 
/*     */   public Hasher newHasher(int expectedInputSize)
/*     */   {
/*  40 */     Preconditions.checkArgument(expectedInputSize >= 0);
/*  41 */     return new BufferingHasher(expectedInputSize);
/*     */   }
/*     */ 
/*     */   private static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream
/*     */   {
/*     */     ExposedByteArrayOutputStream(int expectedInputSize)
/*     */     {
/* 123 */       super();
/*     */     }
/*     */     byte[] byteArray() {
/* 126 */       return this.buf;
/*     */     }
/*     */     int length() {
/* 129 */       return this.count;
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class BufferingHasher extends AbstractHasher
/*     */   {
/*     */     final AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream stream;
/*     */     static final int BOTTOM_BYTE = 255;
/*     */ 
/*     */     BufferingHasher(int expectedInputSize)
/*     */     {
/*  52 */       this.stream = new AbstractNonStreamingHashFunction.ExposedByteArrayOutputStream(expectedInputSize);
/*     */     }
/*     */ 
/*     */     public Hasher putByte(byte b)
/*     */     {
/*  57 */       this.stream.write(b);
/*  58 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putBytes(byte[] bytes)
/*     */     {
/*     */       try {
/*  64 */         this.stream.write(bytes);
/*     */       } catch (IOException e) {
/*  66 */         throw Throwables.propagate(e);
/*     */       }
/*  68 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putBytes(byte[] bytes, int off, int len)
/*     */     {
/*  73 */       this.stream.write(bytes, off, len);
/*  74 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putShort(short s)
/*     */     {
/*  79 */       this.stream.write(s & 0xFF);
/*  80 */       this.stream.write(s >>> 8 & 0xFF);
/*  81 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putInt(int i)
/*     */     {
/*  86 */       this.stream.write(i & 0xFF);
/*  87 */       this.stream.write(i >>> 8 & 0xFF);
/*  88 */       this.stream.write(i >>> 16 & 0xFF);
/*  89 */       this.stream.write(i >>> 24 & 0xFF);
/*  90 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putLong(long l)
/*     */     {
/*  95 */       for (int i = 0; i < 64; i += 8) {
/*  96 */         this.stream.write((byte)(int)(l >>> i & 0xFF));
/*     */       }
/*  98 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putChar(char c)
/*     */     {
/* 103 */       this.stream.write(c & 0xFF);
/* 104 */       this.stream.write(c >>> '\b' & 0xFF);
/* 105 */       return this;
/*     */     }
/*     */ 
/*     */     public <T> Hasher putObject(T instance, Funnel<? super T> funnel)
/*     */     {
/* 110 */       funnel.funnel(instance, this);
/* 111 */       return this;
/*     */     }
/*     */ 
/*     */     public HashCode hash()
/*     */     {
/* 116 */       return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.AbstractNonStreamingHashFunction
 * JD-Core Version:    0.6.2
 */