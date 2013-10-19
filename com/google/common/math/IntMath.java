/*     */ package com.google.common.math;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.math.RoundingMode;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class IntMath
/*     */ {
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final int MAX_POWER_OF_SQRT2_UNSIGNED = -1257966797;
/*     */ 
/*     */   @VisibleForTesting
/* 149 */   static final int[] POWERS_OF_10 = { 1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000 };
/*     */ 
/*     */   @VisibleForTesting
/* 153 */   static final int[] HALF_POWERS_OF_10 = { 3, 31, 316, 3162, 31622, 316227, 3162277, 31622776, 316227766, 2147483647 };
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final int FLOOR_SQRT_MAX_INT = 46340;
/* 439 */   static final int[] FACTORIALS = { 1, 1, 2, 6, 24, 120, 720, 5040, 40320, 362880, 3628800, 39916800, 479001600 };
/*     */ 
/*     */   @VisibleForTesting
/* 487 */   static int[] BIGGEST_BINOMIALS = { 2147483647, 2147483647, 65536, 2345, 477, 193, 110, 75, 58, 49, 43, 39, 37, 35, 34, 34, 33 };
/*     */ 
/*     */   public static boolean isPowerOfTwo(int x)
/*     */   {
/*  64 */     return (x > 0 ? 1 : 0) & ((x & x - 1) == 0 ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public static int log2(int x, RoundingMode mode)
/*     */   {
/*  76 */     MathPreconditions.checkPositive("x", x);
/*  77 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/*  79 */       MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
/*     */     case 2:
/*     */     case 3:
/*  83 */       return 31 - Integer.numberOfLeadingZeros(x);
/*     */     case 4:
/*     */     case 5:
/*  87 */       return 32 - Integer.numberOfLeadingZeros(x - 1);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*  93 */       int leadingZeros = Integer.numberOfLeadingZeros(x);
/*  94 */       int cmp = -1257966797 >>> leadingZeros;
/*     */ 
/*  96 */       int logFloor = 31 - leadingZeros;
/*  97 */       return x <= cmp ? logFloor : logFloor + 1;
/*     */     }
/*     */ 
/* 100 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("need BigIntegerMath to adequately test")
/*     */   public static int log10(int x, RoundingMode mode)
/*     */   {
/* 117 */     MathPreconditions.checkPositive("x", x);
/* 118 */     int logFloor = log10Floor(x);
/* 119 */     int floorPow = POWERS_OF_10[logFloor];
/* 120 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 122 */       MathPreconditions.checkRoundingUnnecessary(x == floorPow);
/*     */     case 2:
/*     */     case 3:
/* 126 */       return logFloor;
/*     */     case 4:
/*     */     case 5:
/* 129 */       return x == floorPow ? logFloor : logFloor + 1;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 134 */       return x <= HALF_POWERS_OF_10[logFloor] ? logFloor : logFloor + 1;
/*     */     }
/* 136 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   private static int log10Floor(int x)
/*     */   {
/* 141 */     for (int i = 1; i < POWERS_OF_10.length; i++) {
/* 142 */       if (x < POWERS_OF_10[i]) {
/* 143 */         return i - 1;
/*     */       }
/*     */     }
/* 146 */     return POWERS_OF_10.length - 1;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("failing tests")
/*     */   public static int pow(int b, int k)
/*     */   {
/* 167 */     MathPreconditions.checkNonNegative("exponent", k);
/* 168 */     switch (b) {
/*     */     case 0:
/* 170 */       return k == 0 ? 1 : 0;
/*     */     case 1:
/* 172 */       return 1;
/*     */     case -1:
/* 174 */       return (k & 0x1) == 0 ? 1 : -1;
/*     */     case 2:
/* 176 */       return k < 32 ? 1 << k : 0;
/*     */     case -2:
/* 178 */       if (k < 32) {
/* 179 */         return (k & 0x1) == 0 ? 1 << k : -(1 << k);
/*     */       }
/* 181 */       return 0;
/*     */     }
/*     */ 
/* 184 */     for (int accum = 1; ; k >>= 1) {
/* 185 */       switch (k) {
/*     */       case 0:
/* 187 */         return accum;
/*     */       case 1:
/* 189 */         return b * accum;
/*     */       }
/* 191 */       accum *= ((k & 0x1) == 0 ? 1 : b);
/* 192 */       b *= b;
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("need BigIntegerMath to adequately test")
/*     */   public static int sqrt(int x, RoundingMode mode)
/*     */   {
/* 207 */     MathPreconditions.checkNonNegative("x", x);
/* 208 */     int sqrtFloor = sqrtFloor(x);
/* 209 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 211 */       MathPreconditions.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x);
/*     */     case 2:
/*     */     case 3:
/* 214 */       return sqrtFloor;
/*     */     case 4:
/*     */     case 5:
/* 217 */       return sqrtFloor * sqrtFloor == x ? sqrtFloor : sqrtFloor + 1;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 221 */       int halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
/*     */ 
/* 227 */       return ((x <= halfSquare ? 1 : 0) | (halfSquare < 0 ? 1 : 0)) != 0 ? sqrtFloor : sqrtFloor + 1;
/*     */     }
/* 229 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   private static int sqrtFloor(int x)
/*     */   {
/* 236 */     return (int)Math.sqrt(x);
/*     */   }
/*     */ 
/*     */   public static int divide(int p, int q, RoundingMode mode)
/*     */   {
/* 248 */     Preconditions.checkNotNull(mode);
/* 249 */     if (q == 0) {
/* 250 */       throw new ArithmeticException("/ by zero");
/*     */     }
/* 252 */     int div = p / q;
/* 253 */     int rem = p - q * div;
/*     */ 
/* 255 */     if (rem == 0) {
/* 256 */       return div;
/*     */     }
/*     */ 
/* 266 */     int signum = 0x1 | (p ^ q) >> 31;
/*     */     boolean increment;
/*     */     boolean increment;
/* 268 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 270 */       MathPreconditions.checkRoundingUnnecessary(rem == 0);
/*     */     case 2:
/* 273 */       increment = false;
/* 274 */       break;
/*     */     case 4:
/* 276 */       increment = true;
/* 277 */       break;
/*     */     case 5:
/* 279 */       increment = signum > 0;
/* 280 */       break;
/*     */     case 3:
/* 282 */       increment = signum < 0;
/* 283 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 287 */       int absRem = Math.abs(rem);
/* 288 */       int cmpRemToHalfDivisor = absRem - (Math.abs(q) - absRem);
/*     */ 
/* 291 */       if (cmpRemToHalfDivisor == 0) {
/* 292 */         if (mode != RoundingMode.HALF_UP);
/* 292 */         increment = ((mode == RoundingMode.HALF_EVEN ? 1 : 0) & ((div & 0x1) != 0 ? 1 : 0)) != 0;
/*     */       } else {
/* 294 */         increment = cmpRemToHalfDivisor > 0;
/*     */       }
/* 296 */       break;
/*     */     default:
/* 298 */       throw new AssertionError();
/*     */     }
/* 300 */     return increment ? div + signum : div;
/*     */   }
/*     */ 
/*     */   public static int mod(int x, int m)
/*     */   {
/* 318 */     if (m <= 0) {
/* 319 */       throw new ArithmeticException("Modulus " + m + " must be > 0");
/*     */     }
/* 321 */     int result = x % m;
/* 322 */     return result >= 0 ? result : result + m;
/*     */   }
/*     */ 
/*     */   public static int gcd(int a, int b)
/*     */   {
/* 337 */     MathPreconditions.checkNonNegative("a", a);
/* 338 */     MathPreconditions.checkNonNegative("b", b);
/*     */ 
/* 340 */     while (b != 0) {
/* 341 */       int t = b;
/* 342 */       b = a % b;
/* 343 */       a = t;
/*     */     }
/* 345 */     return a;
/*     */   }
/*     */ 
/*     */   public static int checkedAdd(int a, int b)
/*     */   {
/* 354 */     long result = a + b;
/* 355 */     MathPreconditions.checkNoOverflow(result == (int)result);
/* 356 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static int checkedSubtract(int a, int b)
/*     */   {
/* 365 */     long result = a - b;
/* 366 */     MathPreconditions.checkNoOverflow(result == (int)result);
/* 367 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static int checkedMultiply(int a, int b)
/*     */   {
/* 376 */     long result = a * b;
/* 377 */     MathPreconditions.checkNoOverflow(result == (int)result);
/* 378 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static int checkedPow(int b, int k)
/*     */   {
/* 390 */     MathPreconditions.checkNonNegative("exponent", k);
/* 391 */     switch (b) {
/*     */     case 0:
/* 393 */       return k == 0 ? 1 : 0;
/*     */     case 1:
/* 395 */       return 1;
/*     */     case -1:
/* 397 */       return (k & 0x1) == 0 ? 1 : -1;
/*     */     case 2:
/* 399 */       MathPreconditions.checkNoOverflow(k < 31);
/* 400 */       return 1 << k;
/*     */     case -2:
/* 402 */       MathPreconditions.checkNoOverflow(k < 32);
/* 403 */       return (k & 0x1) == 0 ? 1 << k : -1 << k;
/*     */     }
/* 405 */     int accum = 1;
/*     */     while (true) {
/* 407 */       switch (k) {
/*     */       case 0:
/* 409 */         return accum;
/*     */       case 1:
/* 411 */         return checkedMultiply(accum, b);
/*     */       }
/* 413 */       if ((k & 0x1) != 0) {
/* 414 */         accum = checkedMultiply(accum, b);
/*     */       }
/* 416 */       k >>= 1;
/* 417 */       if (k > 0) {
/* 418 */         MathPreconditions.checkNoOverflow((-46340 <= b ? 1 : 0) & (b <= 46340 ? 1 : 0));
/* 419 */         b *= b;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static int factorial(int n)
/*     */   {
/* 435 */     MathPreconditions.checkNonNegative("n", n);
/* 436 */     return n < FACTORIALS.length ? FACTORIALS[n] : 2147483647;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("need BigIntegerMath to adequately test")
/*     */   public static int binomial(int n, int k)
/*     */   {
/* 462 */     MathPreconditions.checkNonNegative("n", n);
/* 463 */     MathPreconditions.checkNonNegative("k", k);
/* 464 */     Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", new Object[] { Integer.valueOf(k), Integer.valueOf(n) });
/* 465 */     if (k > n >> 1) {
/* 466 */       k = n - k;
/*     */     }
/* 468 */     if ((k >= BIGGEST_BINOMIALS.length) || (n > BIGGEST_BINOMIALS[k])) {
/* 469 */       return 2147483647;
/*     */     }
/* 471 */     switch (k) {
/*     */     case 0:
/* 473 */       return 1;
/*     */     case 1:
/* 475 */       return n;
/*     */     }
/* 477 */     long result = 1L;
/* 478 */     for (int i = 0; i < k; i++) {
/* 479 */       result *= (n - i);
/* 480 */       result /= (i + 1);
/*     */     }
/* 482 */     return (int)result;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.math.IntMath
 * JD-Core Version:    0.6.2
 */