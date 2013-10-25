/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.security.MessageDigest;
/*     */ 
/*     */ @Beta
/*     */ public abstract class HashCode
/*     */ {
/* 119 */   private static final char[] hexDigits = "0123456789abcdef".toCharArray();
/*     */ 
/*     */   public abstract int asInt();
/*     */ 
/*     */   public abstract long asLong();
/*     */ 
/*     */   public abstract byte[] asBytes();
/*     */ 
/*     */   public int writeBytesTo(byte[] dest, int offset, int maxLength)
/*     */   {
/*  65 */     byte[] hash = asBytes();
/*  66 */     maxLength = Ints.min(new int[] { maxLength, hash.length });
/*  67 */     Preconditions.checkPositionIndexes(offset, offset + maxLength, dest.length);
/*  68 */     System.arraycopy(hash, 0, dest, offset, maxLength);
/*  69 */     return maxLength;
/*     */   }
/*     */ 
/*     */   public abstract int bits();
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/*  78 */     if ((object instanceof HashCode)) {
/*  79 */       HashCode that = (HashCode)object;
/*     */ 
/*  82 */       return MessageDigest.isEqual(asBytes(), that.asBytes());
/*     */     }
/*  84 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  97 */     return asInt();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 110 */     byte[] bytes = asBytes();
/*     */ 
/* 112 */     StringBuilder sb = new StringBuilder(2 * bytes.length);
/* 113 */     for (byte b : bytes) {
/* 114 */       sb.append(hexDigits[(b >> 4 & 0xF)]).append(hexDigits[(b & 0xF)]);
/*     */     }
/* 116 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.HashCode
 * JD-Core Version:    0.6.2
 */