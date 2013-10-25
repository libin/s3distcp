/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.math.IntMath;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.Arrays;
/*     */ 
/*     */  enum BloomFilterStrategies
/*     */   implements BloomFilter.Strategy
/*     */ {
/*  41 */   MURMUR128_MITZ_32;
/*     */ 
/*     */   static class BitArray
/*     */   {
/*     */     final long[] data;
/*     */ 
/*     */     BitArray(int bits)
/*     */     {
/*  82 */       this(new long[IntMath.divide(bits, 64, RoundingMode.CEILING)]);
/*     */     }
/*     */ 
/*     */     BitArray(long[] data)
/*     */     {
/*  87 */       Preconditions.checkArgument(data.length > 0, "data length is zero!");
/*  88 */       this.data = data;
/*     */     }
/*     */ 
/*     */     boolean set(int index)
/*     */     {
/*  93 */       boolean wasSet = get(index);
/*  94 */       this.data[(index >> 6)] |= 1L << index;
/*  95 */       return !wasSet;
/*     */     }
/*     */ 
/*     */     boolean get(int index) {
/*  99 */       return (this.data[(index >> 6)] & 1L << index) != 0L;
/*     */     }
/*     */ 
/*     */     int size()
/*     */     {
/* 104 */       return this.data.length * 64;
/*     */     }
/*     */ 
/*     */     BitArray copy() {
/* 108 */       return new BitArray((long[])this.data.clone());
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 112 */       if ((o instanceof BitArray)) {
/* 113 */         BitArray bitArray = (BitArray)o;
/* 114 */         return Arrays.equals(this.data, bitArray.data);
/*     */       }
/*     */ 
/* 117 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 121 */       return Arrays.hashCode(this.data);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.BloomFilterStrategies
 * JD-Core Version:    0.6.2
 */