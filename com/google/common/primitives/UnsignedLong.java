/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(serializable=true)
/*     */ public final class UnsignedLong extends Number
/*     */   implements Comparable<UnsignedLong>, Serializable
/*     */ {
/*     */   private static final long UNSIGNED_MASK = 9223372036854775807L;
/*  48 */   public static final UnsignedLong ZERO = new UnsignedLong(0L);
/*  49 */   public static final UnsignedLong ONE = new UnsignedLong(1L);
/*  50 */   public static final UnsignedLong MAX_VALUE = new UnsignedLong(-1L);
/*     */   private final long value;
/*     */ 
/*     */   private UnsignedLong(long value)
/*     */   {
/*  55 */     this.value = value;
/*     */   }
/*     */ 
/*     */   public static UnsignedLong asUnsigned(long value)
/*     */   {
/*  66 */     return new UnsignedLong(value);
/*     */   }
/*     */ 
/*     */   public static UnsignedLong valueOf(BigInteger value)
/*     */   {
/*  76 */     Preconditions.checkNotNull(value);
/*  77 */     Preconditions.checkArgument((value.signum() >= 0) && (value.bitLength() <= 64), "value (%s) is outside the range for an unsigned long value", new Object[] { value });
/*     */ 
/*  79 */     return asUnsigned(value.longValue());
/*     */   }
/*     */ 
/*     */   public static UnsignedLong valueOf(String string)
/*     */   {
/*  90 */     return valueOf(string, 10);
/*     */   }
/*     */ 
/*     */   public static UnsignedLong valueOf(String string, int radix)
/*     */   {
/* 102 */     return asUnsigned(UnsignedLongs.parseUnsignedLong(string, radix));
/*     */   }
/*     */ 
/*     */   public UnsignedLong add(UnsignedLong val)
/*     */   {
/* 110 */     Preconditions.checkNotNull(val);
/* 111 */     return asUnsigned(this.value + val.value);
/*     */   }
/*     */ 
/*     */   public UnsignedLong subtract(UnsignedLong val)
/*     */   {
/* 119 */     Preconditions.checkNotNull(val);
/* 120 */     return asUnsigned(this.value - val.value);
/*     */   }
/*     */ 
/*     */   public UnsignedLong multiply(UnsignedLong val)
/*     */   {
/* 128 */     Preconditions.checkNotNull(val);
/* 129 */     return asUnsigned(this.value * val.value);
/*     */   }
/*     */ 
/*     */   public UnsignedLong divide(UnsignedLong val)
/*     */   {
/* 136 */     Preconditions.checkNotNull(val);
/* 137 */     return asUnsigned(UnsignedLongs.divide(this.value, val.value));
/*     */   }
/*     */ 
/*     */   public UnsignedLong remainder(UnsignedLong val)
/*     */   {
/* 144 */     Preconditions.checkNotNull(val);
/* 145 */     return asUnsigned(UnsignedLongs.remainder(this.value, val.value));
/*     */   }
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 153 */     return (int)this.value;
/*     */   }
/*     */ 
/*     */   public long longValue()
/*     */   {
/* 165 */     return this.value;
/*     */   }
/*     */ 
/*     */   public float floatValue()
/*     */   {
/* 175 */     float fValue = (float)(this.value & 0xFFFFFFFF);
/* 176 */     if (this.value < 0L) {
/* 177 */       fValue += 9.223372E+18F;
/*     */     }
/* 179 */     return fValue;
/*     */   }
/*     */ 
/*     */   public double doubleValue()
/*     */   {
/* 189 */     double dValue = this.value & 0xFFFFFFFF;
/* 190 */     if (this.value < 0L) {
/* 191 */       dValue += 9.223372036854776E+18D;
/*     */     }
/* 193 */     return dValue;
/*     */   }
/*     */ 
/*     */   public BigInteger bigIntegerValue()
/*     */   {
/* 200 */     BigInteger bigInt = BigInteger.valueOf(this.value & 0xFFFFFFFF);
/* 201 */     if (this.value < 0L) {
/* 202 */       bigInt = bigInt.setBit(63);
/*     */     }
/* 204 */     return bigInt;
/*     */   }
/*     */ 
/*     */   public int compareTo(UnsignedLong o)
/*     */   {
/* 209 */     Preconditions.checkNotNull(o);
/* 210 */     return UnsignedLongs.compare(this.value, o.value);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 215 */     return Longs.hashCode(this.value);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj)
/*     */   {
/* 220 */     if ((obj instanceof UnsignedLong)) {
/* 221 */       UnsignedLong other = (UnsignedLong)obj;
/* 222 */       return this.value == other.value;
/*     */     }
/* 224 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 232 */     return UnsignedLongs.toString(this.value);
/*     */   }
/*     */ 
/*     */   public String toString(int radix)
/*     */   {
/* 241 */     return UnsignedLongs.toString(this.value, radix);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.UnsignedLong
 * JD-Core Version:    0.6.2
 */