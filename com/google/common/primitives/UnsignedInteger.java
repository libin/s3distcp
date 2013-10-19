/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.math.BigInteger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class UnsignedInteger extends Number
/*     */   implements Comparable<UnsignedInteger>
/*     */ {
/*  47 */   public static final UnsignedInteger ZERO = asUnsigned(0);
/*  48 */   public static final UnsignedInteger ONE = asUnsigned(1);
/*  49 */   public static final UnsignedInteger MAX_VALUE = asUnsigned(-1);
/*     */   private final int value;
/*     */ 
/*     */   private UnsignedInteger(int value)
/*     */   {
/*  54 */     this.value = (value & 0xFFFFFFFF);
/*     */   }
/*     */ 
/*     */   public static UnsignedInteger asUnsigned(int value)
/*     */   {
/*  62 */     return new UnsignedInteger(value);
/*     */   }
/*     */ 
/*     */   public static UnsignedInteger valueOf(long value)
/*     */   {
/*  70 */     Preconditions.checkArgument((value & 0xFFFFFFFF) == value, "value (%s) is outside the range for an unsigned integer value", new Object[] { Long.valueOf(value) });
/*     */ 
/*  72 */     return asUnsigned((int)value);
/*     */   }
/*     */ 
/*     */   public static UnsignedInteger valueOf(BigInteger value)
/*     */   {
/*  82 */     Preconditions.checkNotNull(value);
/*  83 */     Preconditions.checkArgument((value.signum() >= 0) && (value.bitLength() <= 32), "value (%s) is outside the range for an unsigned integer value", new Object[] { value });
/*     */ 
/*  85 */     return asUnsigned(value.intValue());
/*     */   }
/*     */ 
/*     */   public static UnsignedInteger valueOf(String string)
/*     */   {
/*  96 */     return valueOf(string, 10);
/*     */   }
/*     */ 
/*     */   public static UnsignedInteger valueOf(String string, int radix)
/*     */   {
/* 107 */     return asUnsigned(UnsignedInts.parseUnsignedInt(string, radix));
/*     */   }
/*     */ 
/*     */   public UnsignedInteger add(UnsignedInteger val)
/*     */   {
/* 115 */     Preconditions.checkNotNull(val);
/* 116 */     return asUnsigned(this.value + val.value);
/*     */   }
/*     */ 
/*     */   public UnsignedInteger subtract(UnsignedInteger val)
/*     */   {
/* 124 */     Preconditions.checkNotNull(val);
/* 125 */     return asUnsigned(this.value - val.value);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Does not truncate correctly")
/*     */   public UnsignedInteger multiply(UnsignedInteger val)
/*     */   {
/* 134 */     Preconditions.checkNotNull(val);
/* 135 */     return asUnsigned(this.value * val.value);
/*     */   }
/*     */ 
/*     */   public UnsignedInteger divide(UnsignedInteger val)
/*     */   {
/* 142 */     Preconditions.checkNotNull(val);
/* 143 */     return asUnsigned(UnsignedInts.divide(this.value, val.value));
/*     */   }
/*     */ 
/*     */   public UnsignedInteger remainder(UnsignedInteger val)
/*     */   {
/* 150 */     Preconditions.checkNotNull(val);
/* 151 */     return asUnsigned(UnsignedInts.remainder(this.value, val.value));
/*     */   }
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 163 */     return this.value;
/*     */   }
/*     */ 
/*     */   public long longValue()
/*     */   {
/* 171 */     return UnsignedInts.toLong(this.value);
/*     */   }
/*     */ 
/*     */   public float floatValue()
/*     */   {
/* 180 */     return (float)longValue();
/*     */   }
/*     */ 
/*     */   public double doubleValue()
/*     */   {
/* 189 */     return longValue();
/*     */   }
/*     */ 
/*     */   public BigInteger bigIntegerValue()
/*     */   {
/* 196 */     return BigInteger.valueOf(longValue());
/*     */   }
/*     */ 
/*     */   public int compareTo(UnsignedInteger other)
/*     */   {
/* 206 */     Preconditions.checkNotNull(other);
/* 207 */     return UnsignedInts.compare(this.value, other.value);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 212 */     return this.value;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj)
/*     */   {
/* 217 */     if ((obj instanceof UnsignedInteger)) {
/* 218 */       UnsignedInteger other = (UnsignedInteger)obj;
/* 219 */       return this.value == other.value;
/*     */     }
/* 221 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 229 */     return toString(10);
/*     */   }
/*     */ 
/*     */   public String toString(int radix)
/*     */   {
/* 238 */     return UnsignedInts.toString(this.value, radix);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.UnsignedInteger
 * JD-Core Version:    0.6.2
 */