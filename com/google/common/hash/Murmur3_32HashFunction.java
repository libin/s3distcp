/*    */ package com.google.common.hash;
/*    */ 
/*    */ import com.google.common.primitives.UnsignedBytes;
/*    */ import java.io.Serializable;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ final class Murmur3_32HashFunction extends AbstractStreamingHashFunction
/*    */   implements Serializable
/*    */ {
/*    */   private final int seed;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   Murmur3_32HashFunction(int seed)
/*    */   {
/* 33 */     this.seed = seed;
/*    */   }
/*    */ 
/*    */   public int bits() {
/* 37 */     return 32;
/*    */   }
/*    */ 
/*    */   public Hasher newHasher() {
/* 41 */     return new Murmur3_32Hasher(this.seed);
/*    */   }
/*    */   private static final class Murmur3_32Hasher extends AbstractStreamingHashFunction.AbstractStreamingHasher { int h1;
/* 46 */     int c1 = -862048943;
/* 47 */     int c2 = 461845907;
/*    */     int len;
/*    */ 
/* 51 */     Murmur3_32Hasher(int seed) { super();
/* 52 */       this.h1 = seed; }
/*    */ 
/*    */     protected void process(ByteBuffer bb)
/*    */     {
/* 56 */       int k1 = bb.getInt();
/* 57 */       this.len += 4;
/*    */ 
/* 59 */       k1 *= this.c1;
/* 60 */       k1 = Integer.rotateLeft(k1, 15);
/* 61 */       k1 *= this.c2;
/*    */ 
/* 63 */       this.h1 ^= k1;
/* 64 */       this.h1 = Integer.rotateLeft(this.h1, 13);
/* 65 */       this.h1 = (this.h1 * 5 + -430675100);
/*    */     }
/*    */ 
/*    */     protected void processRemaining(ByteBuffer bb) {
/* 69 */       this.len += bb.remaining();
/* 70 */       int k1 = 0;
/* 71 */       switch (bb.remaining()) {
/*    */       case 3:
/* 73 */         k1 ^= UnsignedBytes.toInt(bb.get(2)) << 16;
/*    */       case 2:
/* 76 */         k1 ^= UnsignedBytes.toInt(bb.get(1)) << 8;
/*    */       case 1:
/* 79 */         k1 ^= UnsignedBytes.toInt(bb.get(0));
/*    */       }
/*    */ 
/* 82 */       k1 *= this.c1;
/* 83 */       k1 = Integer.rotateLeft(k1, 15);
/* 84 */       k1 *= this.c2;
/* 85 */       this.h1 ^= k1;
/*    */     }
/*    */ 
/*    */     public HashCode makeHash()
/*    */     {
/* 90 */       this.h1 ^= this.len;
/*    */ 
/* 92 */       this.h1 ^= this.h1 >>> 16;
/* 93 */       this.h1 *= -2048144789;
/* 94 */       this.h1 ^= this.h1 >>> 13;
/* 95 */       this.h1 *= -1028477387;
/* 96 */       this.h1 ^= this.h1 >>> 16;
/*    */ 
/* 98 */       return HashCodes.fromInt(this.h1);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.Murmur3_32HashFunction
 * JD-Core Version:    0.6.2
 */