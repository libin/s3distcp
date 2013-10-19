/*     */ package com.google.common.math;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ final class DoubleUtils
/*     */ {
/*     */   static final long SIGNIFICAND_MASK = 4503599627370495L;
/*     */   static final long EXPONENT_MASK = 9218868437227405312L;
/*     */   static final long SIGN_MASK = -9223372036854775808L;
/*     */   static final int SIGNIFICAND_BITS = 52;
/*     */   static final int EXPONENT_BIAS = 1023;
/*     */   static final long IMPLICIT_BIT = 4503599627370496L;
/* 147 */   private static final long ONE_BITS = Double.doubleToRawLongBits(1.0D);
/*     */ 
/*     */   static double nextDown(double d)
/*     */   {
/*  40 */     return -Math.nextUp(-d);
/*     */   }
/*     */ 
/*     */   static long getSignificand(double d)
/*     */   {
/*  65 */     Preconditions.checkArgument(isFinite(d), "not a normal value");
/*  66 */     int exponent = Math.getExponent(d);
/*  67 */     long bits = Double.doubleToRawLongBits(d);
/*  68 */     bits &= 4503599627370495L;
/*  69 */     return exponent == -1023 ? bits << 1 : bits | 0x0;
/*     */   }
/*     */ 
/*     */   static boolean isFinite(double d)
/*     */   {
/*  75 */     return Math.getExponent(d) <= 1023;
/*     */   }
/*     */ 
/*     */   static boolean isNormal(double d) {
/*  79 */     return Math.getExponent(d) >= -1022;
/*     */   }
/*     */ 
/*     */   static double scaleNormalize(double x)
/*     */   {
/*  87 */     long significand = Double.doubleToRawLongBits(x) & 0xFFFFFFFF;
/*  88 */     return Double.longBitsToDouble(significand | ONE_BITS);
/*     */   }
/*     */ 
/*     */   static double bigToDouble(BigInteger x)
/*     */   {
/*  93 */     BigInteger absX = x.abs();
/*  94 */     int exponent = absX.bitLength() - 1;
/*     */ 
/*  96 */     if (exponent < 63)
/*  97 */       return x.longValue();
/*  98 */     if (exponent > 1023) {
/*  99 */       return x.signum() * (1.0D / 0.0D);
/*     */     }
/*     */ 
/* 110 */     int shift = exponent - 52 - 1;
/* 111 */     long twiceSignifFloor = absX.shiftRight(shift).longValue();
/* 112 */     long signifFloor = twiceSignifFloor >> 1;
/* 113 */     signifFloor &= 4503599627370495L;
/*     */ 
/* 120 */     boolean increment = ((twiceSignifFloor & 1L) != 0L) && (((signifFloor & 1L) != 0L) || (absX.getLowestSetBit() < shift));
/*     */ 
/* 122 */     long signifRounded = increment ? signifFloor + 1L : signifFloor;
/* 123 */     long bits = exponent + 1023 << 52;
/* 124 */     bits += signifRounded;
/*     */ 
/* 131 */     bits |= x.signum() & 0x0;
/* 132 */     return Double.longBitsToDouble(bits);
/*     */   }
/*     */ 
/*     */   static double ensureNonNegative(double value)
/*     */   {
/* 139 */     Preconditions.checkArgument(!Double.isNaN(value));
/* 140 */     if (value > 0.0D) {
/* 141 */       return value;
/*     */     }
/* 143 */     return 0.0D;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.math.DoubleUtils
 * JD-Core Version:    0.6.2
 */