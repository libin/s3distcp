/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class UnsignedInts
/*     */ {
/*     */   static final long INT_MASK = 4294967295L;
/*     */ 
/*     */   static int flip(int value)
/*     */   {
/*  55 */     return value ^ 0x80000000;
/*     */   }
/*     */ 
/*     */   public static int compare(int a, int b)
/*     */   {
/*  68 */     return Ints.compare(flip(a), flip(b));
/*     */   }
/*     */ 
/*     */   public static long toLong(int value)
/*     */   {
/*  75 */     return value & 0xFFFFFFFF;
/*     */   }
/*     */ 
/*     */   public static int min(int[] array)
/*     */   {
/*  87 */     Preconditions.checkArgument(array.length > 0);
/*  88 */     int min = flip(array[0]);
/*  89 */     for (int i = 1; i < array.length; i++) {
/*  90 */       int next = flip(array[i]);
/*  91 */       if (next < min) {
/*  92 */         min = next;
/*     */       }
/*     */     }
/*  95 */     return flip(min);
/*     */   }
/*     */ 
/*     */   public static int max(int[] array)
/*     */   {
/* 107 */     Preconditions.checkArgument(array.length > 0);
/* 108 */     int max = flip(array[0]);
/* 109 */     for (int i = 1; i < array.length; i++) {
/* 110 */       int next = flip(array[i]);
/* 111 */       if (next > max) {
/* 112 */         max = next;
/*     */       }
/*     */     }
/* 115 */     return flip(max);
/*     */   }
/*     */ 
/*     */   public static String join(String separator, int[] array)
/*     */   {
/* 127 */     Preconditions.checkNotNull(separator);
/* 128 */     if (array.length == 0) {
/* 129 */       return "";
/*     */     }
/*     */ 
/* 133 */     StringBuilder builder = new StringBuilder(array.length * 5);
/* 134 */     builder.append(toString(array[0]));
/* 135 */     for (int i = 1; i < array.length; i++) {
/* 136 */       builder.append(separator).append(toString(array[i]));
/*     */     }
/* 138 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<int[]> lexicographicalComparator()
/*     */   {
/* 154 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static int divide(int dividend, int divisor)
/*     */   {
/* 181 */     return (int)(toLong(dividend) / toLong(divisor));
/*     */   }
/*     */ 
/*     */   public static int remainder(int dividend, int divisor)
/*     */   {
/* 193 */     return (int)(toLong(dividend) % toLong(divisor));
/*     */   }
/*     */ 
/*     */   public static int parseUnsignedInt(String s)
/*     */   {
/* 204 */     return parseUnsignedInt(s, 10);
/*     */   }
/*     */ 
/*     */   public static int parseUnsignedInt(String string, int radix)
/*     */   {
/* 217 */     Preconditions.checkNotNull(string);
/* 218 */     long result = Long.parseLong(string, radix);
/* 219 */     if ((result & 0xFFFFFFFF) != result) {
/* 220 */       throw new NumberFormatException(new StringBuilder().append("Input ").append(string).append(" in base ").append(radix).append(" is not in the range of an unsigned integer").toString());
/*     */     }
/*     */ 
/* 223 */     return (int)result;
/*     */   }
/*     */ 
/*     */   public static String toString(int x)
/*     */   {
/* 230 */     return toString(x, 10);
/*     */   }
/*     */ 
/*     */   public static String toString(int x, int radix)
/*     */   {
/* 243 */     long asLong = x & 0xFFFFFFFF;
/* 244 */     return Long.toString(asLong, radix);
/*     */   }
/*     */ 
/*     */   static enum LexicographicalComparator
/*     */     implements Comparator<int[]>
/*     */   {
/* 158 */     INSTANCE;
/*     */ 
/*     */     public int compare(int[] left, int[] right)
/*     */     {
/* 162 */       int minLength = Math.min(left.length, right.length);
/* 163 */       for (int i = 0; i < minLength; i++) {
/* 164 */         if (left[i] != right[i]) {
/* 165 */           return UnsignedInts.compare(left[i], right[i]);
/*     */         }
/*     */       }
/* 168 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.UnsignedInts
 * JD-Core Version:    0.6.2
 */