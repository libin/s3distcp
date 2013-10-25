/*     */ package com.google.common.math;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Booleans;
/*     */ import java.math.BigInteger;
/*     */ import java.math.RoundingMode;
/*     */ 
/*     */ @Beta
/*     */ public final class DoubleMath
/*     */ {
/*     */   private static final double MIN_INT_AS_DOUBLE = -2147483648.0D;
/*     */   private static final double MAX_INT_AS_DOUBLE = 2147483647.0D;
/*     */   private static final double MIN_LONG_AS_DOUBLE = -9.223372036854776E+18D;
/*     */   private static final double MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E+18D;
/* 212 */   private static final double LN_2 = Math.log(2.0D);
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final int MAX_FACTORIAL = 170;
/*     */ 
/*     */   @VisibleForTesting
/* 303 */   static final double[] EVERY_SIXTEENTH_FACTORIAL = { 1.0D, 20922789888000.0D, 2.631308369336935E+35D, 1.241391559253607E+61D, 1.268869321858842E+89D, 7.156945704626381E+118D, 9.916779348709497E+149D, 1.974506857221074E+182D, 3.856204823625804E+215D, 5.550293832739304E+249D, 4.714723635992062E+284D };
/*     */ 
/*     */   static double roundIntermediate(double x, RoundingMode mode)
/*     */   {
/*  55 */     if (!DoubleUtils.isFinite(x)) {
/*  56 */       throw new ArithmeticException("input is infinite or NaN");
/*     */     }
/*  58 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/*  60 */       MathPreconditions.checkRoundingUnnecessary(isMathematicalInteger(x));
/*  61 */       return x;
/*     */     case 2:
/*  64 */       if ((x >= 0.0D) || (isMathematicalInteger(x))) {
/*  65 */         return x;
/*     */       }
/*  67 */       return x - 1.0D;
/*     */     case 3:
/*  71 */       if ((x <= 0.0D) || (isMathematicalInteger(x))) {
/*  72 */         return x;
/*     */       }
/*  74 */       return x + 1.0D;
/*     */     case 4:
/*  78 */       return x;
/*     */     case 5:
/*  81 */       if (isMathematicalInteger(x)) {
/*  82 */         return x;
/*     */       }
/*  84 */       return x + Math.copySign(1.0D, x);
/*     */     case 6:
/*  88 */       return Math.rint(x);
/*     */     case 7:
/*  91 */       double z = Math.rint(x);
/*  92 */       if (Math.abs(x - z) == 0.5D) {
/*  93 */         return x + Math.copySign(0.5D, x);
/*     */       }
/*  95 */       return z;
/*     */     case 8:
/* 100 */       double z = Math.rint(x);
/* 101 */       if (Math.abs(x - z) == 0.5D) {
/* 102 */         return x;
/*     */       }
/* 104 */       return z;
/*     */     }
/*     */ 
/* 109 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public static int roundToInt(double x, RoundingMode mode)
/*     */   {
/* 128 */     double z = roundIntermediate(x, mode);
/* 129 */     MathPreconditions.checkInRange((z > -2147483649.0D ? 1 : 0) & (z < 2147483648.0D ? 1 : 0));
/* 130 */     return (int)z;
/*     */   }
/*     */ 
/*     */   public static long roundToLong(double x, RoundingMode mode)
/*     */   {
/* 151 */     double z = roundIntermediate(x, mode);
/* 152 */     MathPreconditions.checkInRange((-9.223372036854776E+18D - z < 1.0D ? 1 : 0) & (z < 9.223372036854776E+18D ? 1 : 0));
/* 153 */     return ()z;
/*     */   }
/*     */ 
/*     */   public static BigInteger roundToBigInteger(double x, RoundingMode mode)
/*     */   {
/* 175 */     x = roundIntermediate(x, mode);
/* 176 */     if (((-9.223372036854776E+18D - x < 1.0D ? 1 : 0) & (x < 9.223372036854776E+18D ? 1 : 0)) != 0) {
/* 177 */       return BigInteger.valueOf(()x);
/*     */     }
/* 179 */     int exponent = Math.getExponent(x);
/* 180 */     long significand = DoubleUtils.getSignificand(x);
/* 181 */     BigInteger result = BigInteger.valueOf(significand).shiftLeft(exponent - 52);
/* 182 */     return x < 0.0D ? result.negate() : result;
/*     */   }
/*     */ 
/*     */   public static boolean isPowerOfTwo(double x)
/*     */   {
/* 190 */     return (x > 0.0D) && (DoubleUtils.isFinite(x)) && (LongMath.isPowerOfTwo(DoubleUtils.getSignificand(x)));
/*     */   }
/*     */ 
/*     */   public static double log2(double x)
/*     */   {
/* 209 */     return Math.log(x) / LN_2;
/*     */   }
/*     */ 
/*     */   public static int log2(double x, RoundingMode mode)
/*     */   {
/* 225 */     Preconditions.checkArgument((x > 0.0D) && (DoubleUtils.isFinite(x)), "x must be positive and finite");
/* 226 */     int exponent = Math.getExponent(x);
/* 227 */     if (!DoubleUtils.isNormal(x))
/* 228 */       return log2(x * 4503599627370496.0D, mode) - 52;
/*     */     boolean increment;
/* 233 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 235 */       MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
/*     */     case 2:
/* 238 */       increment = false;
/* 239 */       break;
/*     */     case 3:
/* 241 */       increment = !isPowerOfTwo(x);
/* 242 */       break;
/*     */     case 4:
/* 244 */       increment = (exponent < 0 ? 1 : 0) & (!isPowerOfTwo(x) ? 1 : 0);
/* 245 */       break;
/*     */     case 5:
/* 247 */       increment = (exponent >= 0 ? 1 : 0) & (!isPowerOfTwo(x) ? 1 : 0);
/* 248 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 252 */       double xScaled = DoubleUtils.scaleNormalize(x);
/*     */ 
/* 255 */       increment = xScaled * xScaled > 2.0D;
/* 256 */       break;
/*     */     default:
/* 258 */       throw new AssertionError();
/*     */     }
/* 260 */     return increment ? exponent + 1 : exponent;
/*     */   }
/*     */ 
/*     */   public static boolean isMathematicalInteger(double x)
/*     */   {
/* 270 */     return (DoubleUtils.isFinite(x)) && ((x == 0.0D) || (52 - Long.numberOfTrailingZeros(DoubleUtils.getSignificand(x)) <= Math.getExponent(x)));
/*     */   }
/*     */ 
/*     */   public static double factorial(int n)
/*     */   {
/* 285 */     MathPreconditions.checkNonNegative("n", n);
/* 286 */     if (n > 170) {
/* 287 */       return (1.0D / 0.0D);
/*     */     }
/*     */ 
/* 291 */     double accum = 1.0D;
/* 292 */     for (int i = 1 + (n & 0xFFFFFFF0); i <= n; i++) {
/* 293 */       accum *= i;
/*     */     }
/* 295 */     return accum * EVERY_SIXTEENTH_FACTORIAL[(n >> 4)];
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static boolean fuzzyEquals(double a, double b, double tolerance)
/*     */   {
/* 344 */     MathPreconditions.checkNonNegative("tolerance", tolerance);
/* 345 */     return (Math.copySign(a - b, 1.0D) <= tolerance) || (a == b) || ((a != a) && (b != b));
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static int fuzzyCompare(double a, double b, double tolerance)
/*     */   {
/* 368 */     if (fuzzyEquals(a, b, tolerance))
/* 369 */       return 0;
/* 370 */     if (a < b)
/* 371 */       return -1;
/* 372 */     if (a > b) {
/* 373 */       return 1;
/*     */     }
/* 375 */     return Booleans.compare(Double.isNaN(a), Double.isNaN(b));
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.math.DoubleMath
 * JD-Core Version:    0.6.2
 */