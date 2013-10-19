/*     */ package com.google.common.math;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.math.RoundingMode;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class BigIntegerMath
/*     */ {
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final int SQRT2_PRECOMPUTE_THRESHOLD = 256;
/*     */ 
/*     */   @VisibleForTesting
/* 118 */   static final BigInteger SQRT2_PRECOMPUTED_BITS = new BigInteger("16a09e667f3bcc908b2fb1366ea957d3e3adec17512775099da2f590b0667322a", 16);
/*     */ 
/*     */   public static boolean isPowerOfTwo(BigInteger x)
/*     */   {
/*  58 */     Preconditions.checkNotNull(x);
/*  59 */     return (x.signum() > 0) && (x.getLowestSetBit() == x.bitLength() - 1);
/*     */   }
/*     */ 
/*     */   public static int log2(BigInteger x, RoundingMode mode)
/*     */   {
/*  71 */     MathPreconditions.checkPositive("x", (BigInteger)Preconditions.checkNotNull(x));
/*  72 */     int logFloor = x.bitLength() - 1;
/*  73 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/*  75 */       MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
/*     */     case 2:
/*     */     case 3:
/*  78 */       return logFloor;
/*     */     case 4:
/*     */     case 5:
/*  82 */       return isPowerOfTwo(x) ? logFloor : logFloor + 1;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*  87 */       if (logFloor < 256) {
/*  88 */         BigInteger halfPower = SQRT2_PRECOMPUTED_BITS.shiftRight(256 - logFloor);
/*     */ 
/*  90 */         if (x.compareTo(halfPower) <= 0) {
/*  91 */           return logFloor;
/*     */         }
/*  93 */         return logFloor + 1;
/*     */       }
/*     */ 
/* 102 */       BigInteger x2 = x.pow(2);
/* 103 */       int logX2Floor = x2.bitLength() - 1;
/* 104 */       return logX2Floor < 2 * logFloor + 1 ? logFloor : logFloor + 1;
/*     */     }
/*     */ 
/* 107 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static int log10(BigInteger x, RoundingMode mode)
/*     */   {
/* 131 */     MathPreconditions.checkPositive("x", x);
/* 132 */     if (fitsInLong(x)) {
/* 133 */       return LongMath.log10(x.longValue(), mode);
/*     */     }
/*     */ 
/* 137 */     List powersOf10 = new ArrayList(10);
/* 138 */     BigInteger powerOf10 = BigInteger.TEN;
/* 139 */     while (x.compareTo(powerOf10) >= 0) {
/* 140 */       powersOf10.add(powerOf10);
/* 141 */       powerOf10 = powerOf10.pow(2);
/*     */     }
/* 143 */     BigInteger floorPow = BigInteger.ONE;
/* 144 */     int floorLog = 0;
/* 145 */     for (int i = powersOf10.size() - 1; i >= 0; i--) {
/* 146 */       BigInteger powOf10 = (BigInteger)powersOf10.get(i);
/* 147 */       floorLog *= 2;
/* 148 */       BigInteger tenPow = powOf10.multiply(floorPow);
/* 149 */       if (x.compareTo(tenPow) >= 0) {
/* 150 */         floorPow = tenPow;
/* 151 */         floorLog++;
/*     */       }
/*     */     }
/* 154 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 156 */       MathPreconditions.checkRoundingUnnecessary(floorPow.equals(x));
/*     */     case 2:
/*     */     case 3:
/* 160 */       return floorLog;
/*     */     case 4:
/*     */     case 5:
/* 164 */       return floorPow.equals(x) ? floorLog : floorLog + 1;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 170 */       BigInteger x2 = x.pow(2);
/* 171 */       BigInteger halfPowerSquared = floorPow.pow(2).multiply(BigInteger.TEN);
/* 172 */       return x2.compareTo(halfPowerSquared) <= 0 ? floorLog : floorLog + 1;
/*     */     }
/* 174 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static BigInteger sqrt(BigInteger x, RoundingMode mode)
/*     */   {
/* 188 */     MathPreconditions.checkNonNegative("x", x);
/* 189 */     if (fitsInLong(x)) {
/* 190 */       return BigInteger.valueOf(LongMath.sqrt(x.longValue(), mode));
/*     */     }
/* 192 */     BigInteger sqrtFloor = sqrtFloor(x);
/* 193 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 195 */       MathPreconditions.checkRoundingUnnecessary(sqrtFloor.pow(2).equals(x));
/*     */     case 2:
/*     */     case 3:
/* 198 */       return sqrtFloor;
/*     */     case 4:
/*     */     case 5:
/* 201 */       return sqrtFloor.pow(2).equals(x) ? sqrtFloor : sqrtFloor.add(BigInteger.ONE);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 205 */       BigInteger halfSquare = sqrtFloor.pow(2).add(sqrtFloor);
/*     */ 
/* 211 */       return halfSquare.compareTo(x) >= 0 ? sqrtFloor : sqrtFloor.add(BigInteger.ONE);
/*     */     }
/* 213 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   private static BigInteger sqrtFloor(BigInteger x)
/*     */   {
/* 239 */     int log2 = log2(x, RoundingMode.FLOOR);
/*     */     BigInteger sqrt0;
/*     */     BigInteger sqrt0;
/* 240 */     if (log2 < 1023) {
/* 241 */       sqrt0 = sqrtApproxWithDoubles(x);
/*     */     } else {
/* 243 */       int shift = log2 - 52 & 0xFFFFFFFE;
/*     */ 
/* 248 */       sqrt0 = sqrtApproxWithDoubles(x.shiftRight(shift)).shiftLeft(shift >> 1);
/*     */     }
/* 250 */     BigInteger sqrt1 = sqrt0.add(x.divide(sqrt0)).shiftRight(1);
/* 251 */     if (sqrt0.equals(sqrt1))
/* 252 */       return sqrt0;
/*     */     do
/*     */     {
/* 255 */       sqrt0 = sqrt1;
/* 256 */       sqrt1 = sqrt0.add(x.divide(sqrt0)).shiftRight(1);
/* 257 */     }while (sqrt1.compareTo(sqrt0) < 0);
/* 258 */     return sqrt0;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   private static BigInteger sqrtApproxWithDoubles(BigInteger x) {
/* 263 */     return DoubleMath.roundToBigInteger(Math.sqrt(DoubleUtils.bigToDouble(x)), RoundingMode.HALF_EVEN);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static BigInteger divide(BigInteger p, BigInteger q, RoundingMode mode)
/*     */   {
/* 275 */     BigDecimal pDec = new BigDecimal(p);
/* 276 */     BigDecimal qDec = new BigDecimal(q);
/* 277 */     return pDec.divide(qDec, 0, mode).toBigIntegerExact();
/*     */   }
/*     */ 
/*     */   public static BigInteger factorial(int n)
/*     */   {
/* 293 */     MathPreconditions.checkNonNegative("n", n);
/*     */ 
/* 296 */     if (n < LongMath.FACTORIALS.length) {
/* 297 */       return BigInteger.valueOf(LongMath.FACTORIALS[n]);
/*     */     }
/*     */ 
/* 301 */     int approxSize = IntMath.divide(n * IntMath.log2(n, RoundingMode.CEILING), 64, RoundingMode.CEILING);
/* 302 */     ArrayList bignums = new ArrayList(approxSize);
/*     */ 
/* 305 */     int startingNumber = LongMath.FACTORIALS.length;
/* 306 */     long product = LongMath.FACTORIALS[(startingNumber - 1)];
/*     */ 
/* 308 */     int shift = Long.numberOfTrailingZeros(product);
/* 309 */     product >>= shift;
/*     */ 
/* 312 */     int productBits = LongMath.log2(product, RoundingMode.FLOOR) + 1;
/* 313 */     int bits = LongMath.log2(startingNumber, RoundingMode.FLOOR) + 1;
/*     */ 
/* 315 */     int nextPowerOfTwo = 1 << bits - 1;
/*     */ 
/* 318 */     for (long num = startingNumber; num <= n; num += 1L)
/*     */     {
/* 320 */       if ((num & nextPowerOfTwo) != 0L) {
/* 321 */         nextPowerOfTwo <<= 1;
/* 322 */         bits++;
/*     */       }
/*     */ 
/* 325 */       int tz = Long.numberOfTrailingZeros(num);
/* 326 */       long normalizedNum = num >> tz;
/* 327 */       shift += tz;
/*     */ 
/* 329 */       int normalizedBits = bits - tz;
/*     */ 
/* 331 */       if (normalizedBits + productBits >= 64) {
/* 332 */         bignums.add(BigInteger.valueOf(product));
/* 333 */         product = 1L;
/* 334 */         productBits = 0;
/*     */       }
/* 336 */       product *= normalizedNum;
/* 337 */       productBits = LongMath.log2(product, RoundingMode.FLOOR) + 1;
/*     */     }
/*     */ 
/* 340 */     if (product > 1L) {
/* 341 */       bignums.add(BigInteger.valueOf(product));
/*     */     }
/*     */ 
/* 344 */     return listProduct(bignums).shiftLeft(shift);
/*     */   }
/*     */ 
/*     */   static BigInteger listProduct(List<BigInteger> nums) {
/* 348 */     return listProduct(nums, 0, nums.size());
/*     */   }
/*     */ 
/*     */   static BigInteger listProduct(List<BigInteger> nums, int start, int end) {
/* 352 */     switch (end - start) {
/*     */     case 0:
/* 354 */       return BigInteger.ONE;
/*     */     case 1:
/* 356 */       return (BigInteger)nums.get(start);
/*     */     case 2:
/* 358 */       return ((BigInteger)nums.get(start)).multiply((BigInteger)nums.get(start + 1));
/*     */     case 3:
/* 360 */       return ((BigInteger)nums.get(start)).multiply((BigInteger)nums.get(start + 1)).multiply((BigInteger)nums.get(start + 2));
/*     */     }
/*     */ 
/* 363 */     int m = end + start >>> 1;
/* 364 */     return listProduct(nums, start, m).multiply(listProduct(nums, m, end));
/*     */   }
/*     */ 
/*     */   public static BigInteger binomial(int n, int k)
/*     */   {
/* 377 */     MathPreconditions.checkNonNegative("n", n);
/* 378 */     MathPreconditions.checkNonNegative("k", k);
/* 379 */     Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", new Object[] { Integer.valueOf(k), Integer.valueOf(n) });
/* 380 */     if (k > n >> 1) {
/* 381 */       k = n - k;
/*     */     }
/* 383 */     if ((k < LongMath.BIGGEST_BINOMIALS.length) && (n <= LongMath.BIGGEST_BINOMIALS[k])) {
/* 384 */       return BigInteger.valueOf(LongMath.binomial(n, k));
/*     */     }
/* 386 */     BigInteger result = BigInteger.ONE;
/* 387 */     for (int i = 0; i < k; i++) {
/* 388 */       result = result.multiply(BigInteger.valueOf(n - i));
/* 389 */       result = result.divide(BigInteger.valueOf(i + 1));
/*     */     }
/* 391 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   static boolean fitsInLong(BigInteger x)
/*     */   {
/* 397 */     return x.bitLength() <= 63;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.math.BigIntegerMath
 * JD-Core Version:    0.6.2
 */