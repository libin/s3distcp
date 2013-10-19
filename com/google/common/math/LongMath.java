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
/*     */ public final class LongMath
/*     */ {
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final long MAX_POWER_OF_SQRT2_UNSIGNED = -5402926248376769404L;
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   @VisibleForTesting
/* 155 */   static final long[] POWERS_OF_10 = { 1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L, 1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L, 1000000000000000L, 10000000000000000L, 100000000000000000L, 1000000000000000000L };
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   @VisibleForTesting
/* 180 */   static final long[] HALF_POWERS_OF_10 = { 3L, 31L, 316L, 3162L, 31622L, 316227L, 3162277L, 31622776L, 316227766L, 3162277660L, 31622776601L, 316227766016L, 3162277660168L, 31622776601683L, 316227766016837L, 3162277660168379L, 31622776601683793L, 316227766016837933L, 3162277660168379331L };
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   @VisibleForTesting
/*     */   static final long FLOOR_SQRT_MAX_LONG = 3037000499L;
/* 560 */   static final long[] FACTORIALS = { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
/*     */ 
/* 623 */   static final int[] BIGGEST_BINOMIALS = { 2147483647, 2147483647, 2147483647, 3810779, 121977, 16175, 4337, 1733, 887, 534, 361, 265, 206, 169, 143, 125, 111, 101, 94, 88, 83, 79, 76, 74, 72, 70, 69, 68, 67, 67, 66, 66, 66, 66 };
/*     */ 
/*     */   @VisibleForTesting
/* 632 */   static final int[] BIGGEST_SIMPLE_BINOMIALS = { 2147483647, 2147483647, 2147483647, 2642246, 86251, 11724, 3218, 1313, 684, 419, 287, 214, 169, 139, 119, 105, 95, 87, 81, 76, 73, 70, 68, 66, 64, 63, 62, 62, 61, 61, 61 };
/*     */ 
/*     */   public static boolean isPowerOfTwo(long x)
/*     */   {
/*  64 */     return (x > 0L ? 1 : 0) & ((x & x - 1L) == 0L ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public static int log2(long x, RoundingMode mode)
/*     */   {
/*  76 */     MathPreconditions.checkPositive("x", x);
/*  77 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/*  79 */       MathPreconditions.checkRoundingUnnecessary(isPowerOfTwo(x));
/*     */     case 2:
/*     */     case 3:
/*  83 */       return 63 - Long.numberOfLeadingZeros(x);
/*     */     case 4:
/*     */     case 5:
/*  87 */       return 64 - Long.numberOfLeadingZeros(x - 1L);
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/*  93 */       int leadingZeros = Long.numberOfLeadingZeros(x);
/*  94 */       long cmp = -5402926248376769404L >>> leadingZeros;
/*     */ 
/*  96 */       int logFloor = 63 - leadingZeros;
/*  97 */       return x <= cmp ? logFloor : logFloor + 1;
/*     */     }
/*     */ 
/* 100 */     throw new AssertionError("impossible");
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static int log10(long x, RoundingMode mode)
/*     */   {
/* 117 */     MathPreconditions.checkPositive("x", x);
/* 118 */     if (fitsInInt(x)) {
/* 119 */       return IntMath.log10((int)x, mode);
/*     */     }
/* 121 */     int logFloor = log10Floor(x);
/* 122 */     long floorPow = POWERS_OF_10[logFloor];
/* 123 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 125 */       MathPreconditions.checkRoundingUnnecessary(x == floorPow);
/*     */     case 2:
/*     */     case 3:
/* 129 */       return logFloor;
/*     */     case 4:
/*     */     case 5:
/* 132 */       return x == floorPow ? logFloor : logFloor + 1;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 137 */       return x <= HALF_POWERS_OF_10[logFloor] ? logFloor : logFloor + 1;
/*     */     }
/* 139 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   static int log10Floor(long x)
/*     */   {
/* 145 */     for (int i = 1; i < POWERS_OF_10.length; i++) {
/* 146 */       if (x < POWERS_OF_10[i]) {
/* 147 */         return i - 1;
/*     */       }
/*     */     }
/* 150 */     return POWERS_OF_10.length - 1;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long pow(long b, int k)
/*     */   {
/* 211 */     MathPreconditions.checkNonNegative("exponent", k);
/* 212 */     if ((-2L <= b) && (b <= 2L)) {
/* 213 */       switch ((int)b) {
/*     */       case 0:
/* 215 */         return k == 0 ? 1L : 0L;
/*     */       case 1:
/* 217 */         return 1L;
/*     */       case -1:
/* 219 */         return (k & 0x1) == 0 ? 1L : -1L;
/*     */       case 2:
/* 221 */         return k < 64 ? 1L << k : 0L;
/*     */       case -2:
/* 223 */         if (k < 64) {
/* 224 */           return (k & 0x1) == 0 ? 1L << k : -(1L << k);
/*     */         }
/* 226 */         return 0L;
/*     */       }
/*     */     }
/*     */ 
/* 230 */     for (long accum = 1L; ; k >>= 1) {
/* 231 */       switch (k) {
/*     */       case 0:
/* 233 */         return accum;
/*     */       case 1:
/* 235 */         return accum * b;
/*     */       }
/* 237 */       accum *= ((k & 0x1) == 0 ? 1L : b);
/* 238 */       b *= b;
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long sqrt(long x, RoundingMode mode)
/*     */   {
/* 253 */     MathPreconditions.checkNonNegative("x", x);
/* 254 */     if (fitsInInt(x)) {
/* 255 */       return IntMath.sqrt((int)x, mode);
/*     */     }
/* 257 */     long sqrtFloor = sqrtFloor(x);
/* 258 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 260 */       MathPreconditions.checkRoundingUnnecessary(sqrtFloor * sqrtFloor == x);
/*     */     case 2:
/*     */     case 3:
/* 263 */       return sqrtFloor;
/*     */     case 4:
/*     */     case 5:
/* 266 */       return sqrtFloor * sqrtFloor == x ? sqrtFloor : sqrtFloor + 1L;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 270 */       long halfSquare = sqrtFloor * sqrtFloor + sqrtFloor;
/*     */ 
/* 276 */       return ((halfSquare >= x ? 1 : 0) | (halfSquare < 0L ? 1 : 0)) != 0 ? sqrtFloor : sqrtFloor + 1L;
/*     */     }
/* 278 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   private static long sqrtFloor(long x)
/*     */   {
/* 285 */     long sqrt0 = ()Math.sqrt(x);
/*     */ 
/* 287 */     long sqrt1 = sqrt0 + x / sqrt0 >> 1;
/* 288 */     if (sqrt1 == sqrt0)
/* 289 */       return sqrt0;
/*     */     do
/*     */     {
/* 292 */       sqrt0 = sqrt1;
/* 293 */       sqrt1 = sqrt0 + x / sqrt0 >> 1;
/* 294 */     }while (sqrt1 < sqrt0);
/* 295 */     return sqrt0;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long divide(long p, long q, RoundingMode mode)
/*     */   {
/* 308 */     Preconditions.checkNotNull(mode);
/* 309 */     long div = p / q;
/* 310 */     long rem = p - q * div;
/*     */ 
/* 312 */     if (rem == 0L) {
/* 313 */       return div;
/*     */     }
/*     */ 
/* 323 */     int signum = 0x1 | (int)((p ^ q) >> 63);
/*     */     boolean increment;
/*     */     boolean increment;
/* 325 */     switch (1.$SwitchMap$java$math$RoundingMode[mode.ordinal()]) {
/*     */     case 1:
/* 327 */       MathPreconditions.checkRoundingUnnecessary(rem == 0L);
/*     */     case 2:
/* 330 */       increment = false;
/* 331 */       break;
/*     */     case 4:
/* 333 */       increment = true;
/* 334 */       break;
/*     */     case 5:
/* 336 */       increment = signum > 0;
/* 337 */       break;
/*     */     case 3:
/* 339 */       increment = signum < 0;
/* 340 */       break;
/*     */     case 6:
/*     */     case 7:
/*     */     case 8:
/* 344 */       long absRem = Math.abs(rem);
/* 345 */       long cmpRemToHalfDivisor = absRem - (Math.abs(q) - absRem);
/*     */ 
/* 348 */       if (cmpRemToHalfDivisor == 0L)
/* 349 */         increment = (mode == RoundingMode.HALF_UP ? 1 : 0) | (mode == RoundingMode.HALF_EVEN ? 1 : 0) & ((div & 1L) != 0L ? 1 : 0);
/*     */       else {
/* 351 */         increment = cmpRemToHalfDivisor > 0L;
/*     */       }
/* 353 */       break;
/*     */     default:
/* 355 */       throw new AssertionError();
/*     */     }
/* 357 */     return increment ? div + signum : div;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static int mod(long x, int m)
/*     */   {
/* 379 */     return (int)mod(x, m);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long mod(long x, long m)
/*     */   {
/* 400 */     if (m <= 0L) {
/* 401 */       throw new ArithmeticException("Modulus " + m + " must be > 0");
/*     */     }
/* 403 */     long result = x % m;
/* 404 */     return result >= 0L ? result : result + m;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long gcd(long a, long b)
/*     */   {
/* 420 */     MathPreconditions.checkNonNegative("a", a);
/* 421 */     MathPreconditions.checkNonNegative("b", b);
/* 422 */     if (((a == 0L ? 1 : 0) | (b == 0L ? 1 : 0)) != 0) {
/* 423 */       return a | b;
/*     */     }
/*     */ 
/* 429 */     int aTwos = Long.numberOfTrailingZeros(a);
/* 430 */     a >>= aTwos;
/* 431 */     int bTwos = Long.numberOfTrailingZeros(b);
/* 432 */     b >>= bTwos;
/* 433 */     while (a != b) {
/* 434 */       if (a < b) {
/* 435 */         long t = b;
/* 436 */         b = a;
/* 437 */         a = t;
/*     */       }
/* 439 */       a -= b;
/* 440 */       a >>= Long.numberOfTrailingZeros(a);
/*     */     }
/* 442 */     return a << Math.min(aTwos, bTwos);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedAdd(long a, long b)
/*     */   {
/* 452 */     long result = a + b;
/* 453 */     MathPreconditions.checkNoOverflow(((a ^ b) < 0L ? 1 : 0) | ((a ^ result) >= 0L ? 1 : 0));
/* 454 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedSubtract(long a, long b)
/*     */   {
/* 464 */     long result = a - b;
/* 465 */     MathPreconditions.checkNoOverflow(((a ^ b) >= 0L ? 1 : 0) | ((a ^ result) >= 0L ? 1 : 0));
/* 466 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedMultiply(long a, long b)
/*     */   {
/* 477 */     int leadingZeros = Long.numberOfLeadingZeros(a) + Long.numberOfLeadingZeros(a ^ 0xFFFFFFFF) + Long.numberOfLeadingZeros(b) + Long.numberOfLeadingZeros(b ^ 0xFFFFFFFF);
/*     */ 
/* 489 */     if (leadingZeros > 65) {
/* 490 */       return a * b;
/*     */     }
/* 492 */     MathPreconditions.checkNoOverflow(leadingZeros >= 64);
/* 493 */     MathPreconditions.checkNoOverflow((a >= 0L ? 1 : 0) | (b != -9223372036854775808L ? 1 : 0));
/* 494 */     long result = a * b;
/* 495 */     MathPreconditions.checkNoOverflow((a == 0L) || (result / a == b));
/* 496 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long checkedPow(long b, int k)
/*     */   {
/* 507 */     MathPreconditions.checkNonNegative("exponent", k);
/* 508 */     if (((b >= -2L ? 1 : 0) & (b <= 2L ? 1 : 0)) != 0) {
/* 509 */       switch ((int)b) {
/*     */       case 0:
/* 511 */         return k == 0 ? 1L : 0L;
/*     */       case 1:
/* 513 */         return 1L;
/*     */       case -1:
/* 515 */         return (k & 0x1) == 0 ? 1L : -1L;
/*     */       case 2:
/* 517 */         MathPreconditions.checkNoOverflow(k < 63);
/* 518 */         return 1L << k;
/*     */       case -2:
/* 520 */         MathPreconditions.checkNoOverflow(k < 64);
/* 521 */         return (k & 0x1) == 0 ? 1L << k : -1L << k;
/*     */       }
/*     */     }
/* 524 */     long accum = 1L;
/*     */     while (true) {
/* 526 */       switch (k) {
/*     */       case 0:
/* 528 */         return accum;
/*     */       case 1:
/* 530 */         return checkedMultiply(accum, b);
/*     */       }
/* 532 */       if ((k & 0x1) != 0) {
/* 533 */         accum = checkedMultiply(accum, b);
/*     */       }
/* 535 */       k >>= 1;
/* 536 */       if (k > 0) {
/* 537 */         MathPreconditions.checkNoOverflow(b <= 3037000499L);
/* 538 */         b *= b;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   public static long factorial(int n)
/*     */   {
/* 556 */     MathPreconditions.checkNonNegative("n", n);
/* 557 */     return n < FACTORIALS.length ? FACTORIALS[n] : 9223372036854775807L;
/*     */   }
/*     */ 
/*     */   public static long binomial(int n, int k)
/*     */   {
/* 591 */     MathPreconditions.checkNonNegative("n", n);
/* 592 */     MathPreconditions.checkNonNegative("k", k);
/* 593 */     Preconditions.checkArgument(k <= n, "k (%s) > n (%s)", new Object[] { Integer.valueOf(k), Integer.valueOf(n) });
/* 594 */     if (k > n >> 1) {
/* 595 */       k = n - k;
/*     */     }
/* 597 */     if ((k >= BIGGEST_BINOMIALS.length) || (n > BIGGEST_BINOMIALS[k])) {
/* 598 */       return 9223372036854775807L;
/*     */     }
/* 600 */     long result = 1L;
/* 601 */     if ((k < BIGGEST_SIMPLE_BINOMIALS.length) && (n <= BIGGEST_SIMPLE_BINOMIALS[k]))
/*     */     {
/* 603 */       for (int i = 0; i < k; i++) {
/* 604 */         result *= (n - i);
/* 605 */         result /= (i + 1);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 610 */       for (int i = 1; i <= k; n--) {
/* 611 */         int d = IntMath.gcd(n, i);
/* 612 */         result /= i / d;
/* 613 */         result *= n / d;
/*     */ 
/* 610 */         i++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 616 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("TODO")
/*     */   static boolean fitsInInt(long x)
/*     */   {
/* 641 */     return (int)x == x;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.math.LongMath
 * JD-Core Version:    0.6.2
 */