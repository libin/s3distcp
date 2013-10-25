/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.math.BigInteger;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class UnsignedLongs
/*     */ {
/*     */   public static final long MAX_VALUE = -1L;
/* 351 */   private static final long[] maxValueDivs = new long[37];
/* 352 */   private static final int[] maxValueMods = new int[37];
/* 353 */   private static final int[] maxSafeDigits = new int[37];
/*     */ 
/*     */   private static long flip(long a)
/*     */   {
/*  63 */     return a ^ 0x0;
/*     */   }
/*     */ 
/*     */   public static int compare(long a, long b)
/*     */   {
/*  76 */     return Longs.compare(flip(a), flip(b));
/*     */   }
/*     */ 
/*     */   public static long min(long[] array)
/*     */   {
/*  88 */     Preconditions.checkArgument(array.length > 0);
/*  89 */     long min = flip(array[0]);
/*  90 */     for (int i = 1; i < array.length; i++) {
/*  91 */       long next = flip(array[i]);
/*  92 */       if (next < min) {
/*  93 */         min = next;
/*     */       }
/*     */     }
/*  96 */     return flip(min);
/*     */   }
/*     */ 
/*     */   public static long max(long[] array)
/*     */   {
/* 108 */     Preconditions.checkArgument(array.length > 0);
/* 109 */     long max = flip(array[0]);
/* 110 */     for (int i = 1; i < array.length; i++) {
/* 111 */       long next = flip(array[i]);
/* 112 */       if (next > max) {
/* 113 */         max = next;
/*     */       }
/*     */     }
/* 116 */     return flip(max);
/*     */   }
/*     */ 
/*     */   public static String join(String separator, long[] array)
/*     */   {
/* 128 */     Preconditions.checkNotNull(separator);
/* 129 */     if (array.length == 0) {
/* 130 */       return "";
/*     */     }
/*     */ 
/* 134 */     StringBuilder builder = new StringBuilder(array.length * 5);
/* 135 */     builder.append(toString(array[0]));
/* 136 */     for (int i = 1; i < array.length; i++) {
/* 137 */       builder.append(separator).append(toString(array[i]));
/*     */     }
/* 139 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<long[]> lexicographicalComparator()
/*     */   {
/* 156 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static long divide(long dividend, long divisor)
/*     */   {
/* 183 */     if (divisor < 0L) {
/* 184 */       if (compare(dividend, divisor) < 0) {
/* 185 */         return 0L;
/*     */       }
/* 187 */       return 1L;
/*     */     }
/*     */ 
/* 192 */     if (dividend >= 0L) {
/* 193 */       return dividend / divisor;
/*     */     }
/*     */ 
/* 202 */     long quotient = (dividend >>> 1) / divisor << 1;
/* 203 */     long rem = dividend - quotient * divisor;
/* 204 */     return quotient + (compare(rem, divisor) >= 0 ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public static long remainder(long dividend, long divisor)
/*     */   {
/* 217 */     if (divisor < 0L) {
/* 218 */       if (compare(dividend, divisor) < 0) {
/* 219 */         return dividend;
/*     */       }
/* 221 */       return dividend - divisor;
/*     */     }
/*     */ 
/* 226 */     if (dividend >= 0L) {
/* 227 */       return dividend % divisor;
/*     */     }
/*     */ 
/* 236 */     long quotient = (dividend >>> 1) / divisor << 1;
/* 237 */     long rem = dividend - quotient * divisor;
/* 238 */     return rem - (compare(rem, divisor) >= 0 ? divisor : 0L);
/*     */   }
/*     */ 
/*     */   public static long parseUnsignedLong(String s)
/*     */   {
/* 248 */     return parseUnsignedLong(s, 10);
/*     */   }
/*     */ 
/*     */   public static long parseUnsignedLong(String s, int radix)
/*     */   {
/* 261 */     Preconditions.checkNotNull(s);
/* 262 */     if (s.length() == 0) {
/* 263 */       throw new NumberFormatException("empty string");
/*     */     }
/* 265 */     if ((radix < 2) || (radix > 36)) {
/* 266 */       throw new NumberFormatException(new StringBuilder().append("illegal radix: ").append(radix).toString());
/*     */     }
/*     */ 
/* 269 */     int max_safe_pos = maxSafeDigits[radix] - 1;
/* 270 */     long value = 0L;
/* 271 */     for (int pos = 0; pos < s.length(); pos++) {
/* 272 */       int digit = Character.digit(s.charAt(pos), radix);
/* 273 */       if (digit == -1) {
/* 274 */         throw new NumberFormatException(s);
/*     */       }
/* 276 */       if ((pos > max_safe_pos) && (overflowInParse(value, digit, radix))) {
/* 277 */         throw new NumberFormatException(new StringBuilder().append("Too large for unsigned long: ").append(s).toString());
/*     */       }
/* 279 */       value = value * radix + digit;
/*     */     }
/*     */ 
/* 282 */     return value;
/*     */   }
/*     */ 
/*     */   private static boolean overflowInParse(long current, int digit, int radix)
/*     */   {
/* 292 */     if (current >= 0L) {
/* 293 */       if (current < maxValueDivs[radix]) {
/* 294 */         return false;
/*     */       }
/* 296 */       if (current > maxValueDivs[radix]) {
/* 297 */         return true;
/*     */       }
/*     */ 
/* 300 */       return digit > maxValueMods[radix];
/*     */     }
/*     */ 
/* 304 */     return true;
/*     */   }
/*     */ 
/*     */   public static String toString(long x)
/*     */   {
/* 311 */     return toString(x, 10);
/*     */   }
/*     */ 
/*     */   public static String toString(long x, int radix)
/*     */   {
/* 324 */     Preconditions.checkArgument((radix >= 2) && (radix <= 36), "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", new Object[] { Integer.valueOf(radix) });
/*     */ 
/* 326 */     if (x == 0L)
/*     */     {
/* 328 */       return "0";
/*     */     }
/* 330 */     char[] buf = new char[64];
/* 331 */     int i = buf.length;
/* 332 */     if (x < 0L)
/*     */     {
/* 335 */       long quotient = divide(x, radix);
/* 336 */       long rem = x - quotient * radix;
/* 337 */       buf[(--i)] = Character.forDigit((int)rem, radix);
/* 338 */       x = quotient;
/*     */     }
/*     */ 
/* 341 */     while (x > 0L) {
/* 342 */       buf[(--i)] = Character.forDigit((int)(x % radix), radix);
/* 343 */       x /= radix;
/*     */     }
/*     */ 
/* 346 */     return new String(buf, i, buf.length - i);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 355 */     BigInteger overflow = new BigInteger("10000000000000000", 16);
/* 356 */     for (int i = 2; i <= 36; i++) {
/* 357 */       maxValueDivs[i] = divide(-1L, i);
/* 358 */       maxValueMods[i] = ((int)remainder(-1L, i));
/* 359 */       maxSafeDigits[i] = (overflow.toString(i).length() - 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum LexicographicalComparator
/*     */     implements Comparator<long[]>
/*     */   {
/* 160 */     INSTANCE;
/*     */ 
/*     */     public int compare(long[] left, long[] right)
/*     */     {
/* 164 */       int minLength = Math.min(left.length, right.length);
/* 165 */       for (int i = 0; i < minLength; i++) {
/* 166 */         if (left[i] != right[i]) {
/* 167 */           return UnsignedLongs.compare(left[i], right[i]);
/*     */         }
/*     */       }
/* 170 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.UnsignedLongs
 * JD-Core Version:    0.6.2
 */