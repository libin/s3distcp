/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ @Beta
/*     */ public final class BloomFilter<T>
/*     */   implements Serializable
/*     */ {
/*     */   private final BloomFilterStrategies.BitArray bits;
/*     */   private final int numHashFunctions;
/*     */   private final Funnel<T> funnel;
/*     */   private final Strategy strategy;
/* 256 */   private static final double LN2 = Math.log(2.0D);
/* 257 */   private static final double LN2_SQUARED = LN2 * LN2;
/*     */ 
/*     */   private BloomFilter(BloomFilterStrategies.BitArray bits, int numHashFunctions, Funnel<T> funnel, Strategy strategy)
/*     */   {
/* 100 */     Preconditions.checkArgument(numHashFunctions > 0, "numHashFunctions zero or negative");
/* 101 */     this.bits = ((BloomFilterStrategies.BitArray)Preconditions.checkNotNull(bits));
/* 102 */     this.numHashFunctions = numHashFunctions;
/* 103 */     this.funnel = ((Funnel)Preconditions.checkNotNull(funnel));
/* 104 */     this.strategy = strategy;
/*     */ 
/* 111 */     if (numHashFunctions > 255)
/* 112 */       throw new AssertionError("Currently we don't allow BloomFilters that would use more than255 hash functions, please contact the guava team");
/*     */   }
/*     */ 
/*     */   public BloomFilter<T> copy()
/*     */   {
/* 124 */     return new BloomFilter(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
/*     */   }
/*     */ 
/*     */   public boolean mightContain(T object)
/*     */   {
/* 132 */     return this.strategy.mightContain(object, this.funnel, this.numHashFunctions, this.bits);
/*     */   }
/*     */ 
/*     */   public boolean put(T object)
/*     */   {
/* 148 */     return this.strategy.put(object, this.funnel, this.numHashFunctions, this.bits);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 157 */     if ((o instanceof BloomFilter)) {
/* 158 */       BloomFilter that = (BloomFilter)o;
/* 159 */       return (this.numHashFunctions == that.numHashFunctions) && (this.bits.equals(that.bits)) && (this.funnel == that.funnel) && (this.strategy == that.strategy);
/*     */     }
/*     */ 
/* 164 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 168 */     return this.bits.hashCode();
/*     */   }
/*     */   @VisibleForTesting
/*     */   int getHashCount() {
/* 172 */     return this.numHashFunctions;
/*     */   }
/*     */   @VisibleForTesting
/*     */   double computeExpectedFalsePositiveRate(int insertions) {
/* 176 */     return Math.pow(1.0D - Math.exp(-this.numHashFunctions * (insertions / this.bits.size())), this.numHashFunctions);
/*     */   }
/*     */ 
/*     */   public static <T> BloomFilter<T> create(Funnel<T> funnel, int expectedInsertions, double falsePositiveProbability)
/*     */   {
/* 205 */     Preconditions.checkNotNull(funnel);
/* 206 */     Preconditions.checkArgument(expectedInsertions > 0, "Expected insertions must be positive");
/* 207 */     Preconditions.checkArgument((falsePositiveProbability > 0.0D ? 1 : 0) & (falsePositiveProbability < 1.0D ? 1 : 0), "False positive probability in (0.0, 1.0)");
/*     */ 
/* 215 */     int numBits = optimalNumOfBits(expectedInsertions, falsePositiveProbability);
/* 216 */     int numHashFunctions = optimalNumOfHashFunctions(expectedInsertions, numBits);
/* 217 */     return new BloomFilter(new BloomFilterStrategies.BitArray(numBits), numHashFunctions, funnel, BloomFilterStrategies.MURMUR128_MITZ_32);
/*     */   }
/*     */ 
/*     */   public static <T> BloomFilter<T> create(Funnel<T> funnel, int expectedInsertions)
/*     */   {
/* 238 */     return create(funnel, expectedInsertions, 0.03D);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int optimalNumOfHashFunctions(int n, int m)
/*     */   {
/* 269 */     return Math.max(1, (int)Math.round(m / n * LN2));
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int optimalNumOfBits(int n, double p)
/*     */   {
/* 282 */     return (int)(-n * Math.log(p) / LN2_SQUARED);
/*     */   }
/*     */ 
/*     */   private Object writeReplace() {
/* 286 */     return new SerialForm(this); } 
/*     */   private static class SerialForm<T> implements Serializable { final long[] data;
/*     */     final int numHashFunctions;
/*     */     final Funnel<T> funnel;
/*     */     final BloomFilter.Strategy strategy;
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/* 296 */     SerialForm(BloomFilter<T> bf) { this.data = bf.bits.data;
/* 297 */       this.numHashFunctions = bf.numHashFunctions;
/* 298 */       this.funnel = bf.funnel;
/* 299 */       this.strategy = bf.strategy; }
/*     */ 
/*     */     Object readResolve() {
/* 302 */       return new BloomFilter(new BloomFilterStrategies.BitArray(this.data), this.numHashFunctions, this.funnel, this.strategy, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface Strategy extends Serializable
/*     */   {
/*     */     public abstract <T> boolean put(T paramT, Funnel<? super T> paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
/*     */ 
/*     */     public abstract <T> boolean mightContain(T paramT, Funnel<? super T> paramFunnel, int paramInt, BloomFilterStrategies.BitArray paramBitArray);
/*     */ 
/*     */     public abstract int ordinal();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.BloomFilter
 * JD-Core Version:    0.6.2
 */