/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Strings
/*     */ {
/*     */   public static String nullToEmpty(@Nullable String string)
/*     */   {
/*  48 */     return string == null ? "" : string;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public static String emptyToNull(@Nullable String string)
/*     */   {
/*  59 */     return isNullOrEmpty(string) ? null : string;
/*     */   }
/*     */ 
/*     */   public static boolean isNullOrEmpty(@Nullable String string)
/*     */   {
/*  76 */     return (string == null) || (string.length() == 0);
/*     */   }
/*     */ 
/*     */   public static String padStart(String string, int minLength, char padChar)
/*     */   {
/*  99 */     Preconditions.checkNotNull(string);
/* 100 */     if (string.length() >= minLength) {
/* 101 */       return string;
/*     */     }
/* 103 */     StringBuilder sb = new StringBuilder(minLength);
/* 104 */     for (int i = string.length(); i < minLength; i++) {
/* 105 */       sb.append(padChar);
/*     */     }
/* 107 */     sb.append(string);
/* 108 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String padEnd(String string, int minLength, char padChar)
/*     */   {
/* 131 */     Preconditions.checkNotNull(string);
/* 132 */     if (string.length() >= minLength) {
/* 133 */       return string;
/*     */     }
/* 135 */     StringBuilder sb = new StringBuilder(minLength);
/* 136 */     sb.append(string);
/* 137 */     for (int i = string.length(); i < minLength; i++) {
/* 138 */       sb.append(padChar);
/*     */     }
/* 140 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   public static String repeat(String string, int count)
/*     */   {
/* 155 */     Preconditions.checkNotNull(string);
/*     */ 
/* 157 */     if (count <= 1) {
/* 158 */       Preconditions.checkArgument(count >= 0, "invalid count: %s", new Object[] { Integer.valueOf(count) });
/* 159 */       return count == 0 ? "" : string;
/*     */     }
/*     */ 
/* 163 */     int len = string.length();
/* 164 */     long longSize = len * count;
/* 165 */     int size = (int)longSize;
/* 166 */     if (size != longSize) {
/* 167 */       throw new ArrayIndexOutOfBoundsException(new StringBuilder().append("Required array size too large: ").append(String.valueOf(longSize)).toString());
/*     */     }
/*     */ 
/* 171 */     char[] array = new char[size];
/* 172 */     string.getChars(0, len, array, 0);
/*     */ 
/* 174 */     for (int n = len; n < size - n; n <<= 1) {
/* 175 */       System.arraycopy(array, 0, array, n, n);
/*     */     }
/* 177 */     System.arraycopy(array, 0, array, n, size - n);
/* 178 */     return new String(array);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static String commonPrefix(CharSequence a, CharSequence b)
/*     */   {
/* 191 */     Preconditions.checkNotNull(a);
/* 192 */     Preconditions.checkNotNull(b);
/*     */ 
/* 194 */     int maxPrefixLength = Math.min(a.length(), b.length());
/* 195 */     int p = 0;
/* 196 */     while ((p < maxPrefixLength) && (a.charAt(p) == b.charAt(p))) {
/* 197 */       p++;
/*     */     }
/* 199 */     if ((validSurrogatePairAt(a, p - 1)) || (validSurrogatePairAt(b, p - 1))) {
/* 200 */       p--;
/*     */     }
/* 202 */     return a.subSequence(0, p).toString();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static String commonSuffix(CharSequence a, CharSequence b)
/*     */   {
/* 215 */     Preconditions.checkNotNull(a);
/* 216 */     Preconditions.checkNotNull(b);
/*     */ 
/* 218 */     int maxSuffixLength = Math.min(a.length(), b.length());
/* 219 */     int s = 0;
/*     */ 
/* 221 */     while ((s < maxSuffixLength) && (a.charAt(a.length() - s - 1) == b.charAt(b.length() - s - 1))) {
/* 222 */       s++;
/*     */     }
/* 224 */     if ((validSurrogatePairAt(a, a.length() - s - 1)) || (validSurrogatePairAt(b, b.length() - s - 1)))
/*     */     {
/* 226 */       s--;
/*     */     }
/* 228 */     return a.subSequence(a.length() - s, a.length()).toString();
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static boolean validSurrogatePairAt(CharSequence string, int index)
/*     */   {
/* 237 */     return (index >= 0) && (index <= string.length() - 2) && (Character.isHighSurrogate(string.charAt(index))) && (Character.isLowSurrogate(string.charAt(index + 1)));
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Strings
 * JD-Core Version:    0.6.2
 */