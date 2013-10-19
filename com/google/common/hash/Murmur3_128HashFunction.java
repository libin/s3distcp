/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.primitives.UnsignedBytes;
/*     */ import java.io.Serializable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ 
/*     */ final class Murmur3_128HashFunction extends AbstractStreamingHashFunction
/*     */   implements Serializable
/*     */ {
/*     */   private final int seed;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   Murmur3_128HashFunction(int seed)
/*     */   {
/*  35 */     this.seed = seed;
/*     */   }
/*     */ 
/*     */   public int bits() {
/*  39 */     return 128;
/*     */   }
/*     */ 
/*     */   public Hasher newHasher() {
/*  43 */     return new Murmur3_128Hasher(this.seed); } 
/*     */   private static final class Murmur3_128Hasher extends AbstractStreamingHashFunction.AbstractStreamingHasher { long h1;
/*     */     long h2;
/*  49 */     long c1 = -8663945395140668459L;
/*  50 */     long c2 = 5545529020109919103L;
/*     */     int len;
/*     */ 
/*  54 */     Murmur3_128Hasher(int seed) { super();
/*  55 */       this.h1 = seed;
/*  56 */       this.h2 = seed; }
/*     */ 
/*     */     protected void process(ByteBuffer bb)
/*     */     {
/*  60 */       long k1 = bb.getLong();
/*  61 */       long k2 = bb.getLong();
/*  62 */       this.len += 16;
/*  63 */       bmix64(k1, k2);
/*     */     }
/*     */ 
/*     */     private void bmix64(long k1, long k2) {
/*  67 */       k1 *= this.c1;
/*  68 */       k1 = Long.rotateLeft(k1, 31);
/*  69 */       k1 *= this.c2;
/*  70 */       this.h1 ^= k1;
/*     */ 
/*  72 */       this.h1 = Long.rotateLeft(this.h1, 27);
/*  73 */       this.h1 += this.h2;
/*  74 */       this.h1 = (this.h1 * 5L + 1390208809L);
/*     */ 
/*  76 */       k2 *= this.c2;
/*  77 */       k2 = Long.rotateLeft(k2, 33);
/*  78 */       k2 *= this.c1;
/*  79 */       this.h2 ^= k2;
/*     */ 
/*  81 */       this.h2 = Long.rotateLeft(this.h2, 31);
/*  82 */       this.h2 += this.h1;
/*  83 */       this.h2 = (this.h2 * 5L + 944331445L);
/*     */     }
/*     */ 
/*     */     protected void processRemaining(ByteBuffer bb) {
/*  87 */       long k1 = 0L;
/*  88 */       long k2 = 0L;
/*  89 */       this.len += bb.remaining();
/*  90 */       switch (bb.remaining()) {
/*     */       case 15:
/*  92 */         k2 ^= UnsignedBytes.toInt(bb.get(14)) << 48;
/*     */       case 14:
/*  94 */         k2 ^= UnsignedBytes.toInt(bb.get(13)) << 40;
/*     */       case 13:
/*  96 */         k2 ^= UnsignedBytes.toInt(bb.get(12)) << 32;
/*     */       case 12:
/*  98 */         k2 ^= UnsignedBytes.toInt(bb.get(11)) << 24;
/*     */       case 11:
/* 100 */         k2 ^= UnsignedBytes.toInt(bb.get(10)) << 16;
/*     */       case 10:
/* 102 */         k2 ^= UnsignedBytes.toInt(bb.get(9)) << 8;
/*     */       case 9:
/* 104 */         k2 ^= UnsignedBytes.toInt(bb.get(8)) << 0;
/* 105 */         k2 *= this.c2;
/* 106 */         k2 = Long.rotateLeft(k2, 33);
/* 107 */         k2 *= this.c1;
/* 108 */         this.h2 ^= k2;
/*     */       case 8:
/* 111 */         k1 ^= UnsignedBytes.toInt(bb.get(7)) << 56;
/*     */       case 7:
/* 113 */         k1 ^= UnsignedBytes.toInt(bb.get(6)) << 48;
/*     */       case 6:
/* 115 */         k1 ^= UnsignedBytes.toInt(bb.get(5)) << 40;
/*     */       case 5:
/* 117 */         k1 ^= UnsignedBytes.toInt(bb.get(4)) << 32;
/*     */       case 4:
/* 119 */         k1 ^= UnsignedBytes.toInt(bb.get(3)) << 24;
/*     */       case 3:
/* 121 */         k1 ^= UnsignedBytes.toInt(bb.get(2)) << 16;
/*     */       case 2:
/* 123 */         k1 ^= UnsignedBytes.toInt(bb.get(1)) << 8;
/*     */       case 1:
/* 125 */         k1 ^= UnsignedBytes.toInt(bb.get(0)) << 0;
/* 126 */         k1 *= this.c1;
/* 127 */         k1 = Long.rotateLeft(k1, 31);
/* 128 */         k1 *= this.c2;
/* 129 */         this.h1 ^= k1;
/*     */       }
/*     */     }
/*     */ 
/*     */     public HashCode makeHash()
/*     */     {
/* 136 */       this.h1 ^= this.len;
/* 137 */       this.h2 ^= this.len;
/*     */ 
/* 139 */       this.h1 += this.h2;
/* 140 */       this.h2 += this.h1;
/*     */ 
/* 142 */       this.h1 = fmix64(this.h1);
/* 143 */       this.h2 = fmix64(this.h2);
/*     */ 
/* 145 */       this.h1 += this.h2;
/* 146 */       this.h2 += this.h1;
/*     */ 
/* 148 */       ByteBuffer bb = ByteBuffer.wrap(new byte[16]).order(ByteOrder.LITTLE_ENDIAN);
/* 149 */       bb.putLong(this.h1);
/* 150 */       bb.putLong(this.h2);
/* 151 */       return HashCodes.fromBytesNoCopy(bb.array());
/*     */     }
/*     */ 
/*     */     private long fmix64(long k) {
/* 155 */       k ^= k >>> 33;
/* 156 */       k *= -49064778989728563L;
/* 157 */       k ^= k >>> 33;
/* 158 */       k *= -4265267296055464877L;
/* 159 */       k ^= k >>> 33;
/* 160 */       return k;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.Murmur3_128HashFunction
 * JD-Core Version:    0.6.2
 */