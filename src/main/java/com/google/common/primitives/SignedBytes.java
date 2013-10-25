/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class SignedBytes
/*     */ {
/*     */   public static final byte MAX_POWER_OF_TWO = 64;
/*     */ 
/*     */   public static byte checkedCast(long value)
/*     */   {
/*  61 */     byte result = (byte)(int)value;
/*  62 */     Preconditions.checkArgument(result == value, "Out of range: %s", new Object[] { Long.valueOf(value) });
/*  63 */     return result;
/*     */   }
/*     */ 
/*     */   public static byte saturatedCast(long value)
/*     */   {
/*  75 */     if (value > 127L) {
/*  76 */       return 127;
/*     */     }
/*  78 */     if (value < -128L) {
/*  79 */       return -128;
/*     */     }
/*  81 */     return (byte)(int)value;
/*     */   }
/*     */ 
/*     */   public static int compare(byte a, byte b)
/*     */   {
/*  94 */     return a - b;
/*     */   }
/*     */ 
/*     */   public static byte min(byte[] array)
/*     */   {
/* 106 */     Preconditions.checkArgument(array.length > 0);
/* 107 */     byte min = array[0];
/* 108 */     for (int i = 1; i < array.length; i++) {
/* 109 */       if (array[i] < min) {
/* 110 */         min = array[i];
/*     */       }
/*     */     }
/* 113 */     return min;
/*     */   }
/*     */ 
/*     */   public static byte max(byte[] array)
/*     */   {
/* 125 */     Preconditions.checkArgument(array.length > 0);
/* 126 */     byte max = array[0];
/* 127 */     for (int i = 1; i < array.length; i++) {
/* 128 */       if (array[i] > max) {
/* 129 */         max = array[i];
/*     */       }
/*     */     }
/* 132 */     return max;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, byte[] array)
/*     */   {
/* 145 */     Preconditions.checkNotNull(separator);
/* 146 */     if (array.length == 0) {
/* 147 */       return "";
/*     */     }
/*     */ 
/* 151 */     StringBuilder builder = new StringBuilder(array.length * 5);
/* 152 */     builder.append(array[0]);
/* 153 */     for (int i = 1; i < array.length; i++) {
/* 154 */       builder.append(separator).append(array[i]);
/*     */     }
/* 156 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<byte[]> lexicographicalComparator()
/*     */   {
/* 176 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator implements Comparator<byte[]> {
/* 180 */     INSTANCE;
/*     */ 
/*     */     public int compare(byte[] left, byte[] right)
/*     */     {
/* 184 */       int minLength = Math.min(left.length, right.length);
/* 185 */       for (int i = 0; i < minLength; i++) {
/* 186 */         int result = SignedBytes.compare(left[i], right[i]);
/* 187 */         if (result != 0) {
/* 188 */           return result;
/*     */         }
/*     */       }
/* 191 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.SignedBytes
 * JD-Core Version:    0.6.2
 */