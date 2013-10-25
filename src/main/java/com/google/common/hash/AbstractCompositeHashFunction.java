/*     */ package com.google.common.hash;
/*     */ 
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ abstract class AbstractCompositeHashFunction extends AbstractStreamingHashFunction
/*     */ {
/*     */   final HashFunction[] functions;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   AbstractCompositeHashFunction(HashFunction[] functions)
/*     */   {
/*  32 */     this.functions = functions;
/*     */   }
/*     */ 
/*     */   abstract HashCode makeHash(Hasher[] paramArrayOfHasher);
/*     */ 
/*     */   public Hasher newHasher()
/*     */   {
/*  45 */     final Hasher[] hashers = new Hasher[this.functions.length];
/*  46 */     for (int i = 0; i < hashers.length; i++) {
/*  47 */       hashers[i] = this.functions[i].newHasher();
/*     */     }
/*  49 */     return new Hasher() {
/*     */       public Hasher putByte(byte b) {
/*  51 */         for (Hasher hasher : hashers) {
/*  52 */           hasher.putByte(b);
/*     */         }
/*  54 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putBytes(byte[] bytes) {
/*  58 */         for (Hasher hasher : hashers) {
/*  59 */           hasher.putBytes(bytes);
/*     */         }
/*  61 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putBytes(byte[] bytes, int off, int len) {
/*  65 */         for (Hasher hasher : hashers) {
/*  66 */           hasher.putBytes(bytes, off, len);
/*     */         }
/*  68 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putShort(short s) {
/*  72 */         for (Hasher hasher : hashers) {
/*  73 */           hasher.putShort(s);
/*     */         }
/*  75 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putInt(int i) {
/*  79 */         for (Hasher hasher : hashers) {
/*  80 */           hasher.putInt(i);
/*     */         }
/*  82 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putLong(long l) {
/*  86 */         for (Hasher hasher : hashers) {
/*  87 */           hasher.putLong(l);
/*     */         }
/*  89 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putFloat(float f) {
/*  93 */         for (Hasher hasher : hashers) {
/*  94 */           hasher.putFloat(f);
/*     */         }
/*  96 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putDouble(double d) {
/* 100 */         for (Hasher hasher : hashers) {
/* 101 */           hasher.putDouble(d);
/*     */         }
/* 103 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putBoolean(boolean b) {
/* 107 */         for (Hasher hasher : hashers) {
/* 108 */           hasher.putBoolean(b);
/*     */         }
/* 110 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putChar(char c) {
/* 114 */         for (Hasher hasher : hashers) {
/* 115 */           hasher.putChar(c);
/*     */         }
/* 117 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putString(CharSequence chars) {
/* 121 */         for (Hasher hasher : hashers) {
/* 122 */           hasher.putString(chars);
/*     */         }
/* 124 */         return this;
/*     */       }
/*     */ 
/*     */       public Hasher putString(CharSequence chars, Charset charset) {
/* 128 */         for (Hasher hasher : hashers) {
/* 129 */           hasher.putString(chars, charset);
/*     */         }
/* 131 */         return this;
/*     */       }
/*     */ 
/*     */       public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
/* 135 */         for (Hasher hasher : hashers) {
/* 136 */           hasher.putObject(instance, funnel);
/*     */         }
/* 138 */         return this;
/*     */       }
/*     */ 
/*     */       public HashCode hash() {
/* 142 */         return AbstractCompositeHashFunction.this.makeHash(hashers);
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.AbstractCompositeHashFunction
 * JD-Core Version:    0.6.2
 */