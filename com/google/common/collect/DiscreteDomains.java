/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Beta
/*     */ public final class DiscreteDomains
/*     */ {
/*     */   public static DiscreteDomain<Integer> integers()
/*     */   {
/*  44 */     return IntegerDomain.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static DiscreteDomain<Long> longs()
/*     */   {
/*  84 */     return LongDomain.INSTANCE;
/*     */   }
/*     */ 
/*     */   static DiscreteDomain<BigInteger> bigIntegers()
/*     */   {
/* 132 */     return BigIntegerDomain.INSTANCE;
/*     */   }
/*     */   private static final class BigIntegerDomain extends DiscreteDomain<BigInteger> implements Serializable {
/* 137 */     private static final BigIntegerDomain INSTANCE = new BigIntegerDomain();
/*     */ 
/* 139 */     private static final BigInteger MIN_LONG = BigInteger.valueOf(-9223372036854775808L);
/*     */ 
/* 141 */     private static final BigInteger MAX_LONG = BigInteger.valueOf(9223372036854775807L);
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 145 */     public BigInteger next(BigInteger value) { return value.add(BigInteger.ONE); }
/*     */ 
/*     */     public BigInteger previous(BigInteger value)
/*     */     {
/* 149 */       return value.subtract(BigInteger.ONE);
/*     */     }
/*     */ 
/*     */     public long distance(BigInteger start, BigInteger end) {
/* 153 */       return start.subtract(end).max(MIN_LONG).min(MAX_LONG).longValue();
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/* 157 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class LongDomain extends DiscreteDomain<Long>
/*     */     implements Serializable
/*     */   {
/*  89 */     private static final LongDomain INSTANCE = new LongDomain();
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public Long next(Long value)
/*     */     {
/*  92 */       long l = value.longValue();
/*  93 */       return l == 9223372036854775807L ? null : Long.valueOf(l + 1L);
/*     */     }
/*     */ 
/*     */     public Long previous(Long value) {
/*  97 */       long l = value.longValue();
/*  98 */       return l == -9223372036854775808L ? null : Long.valueOf(l - 1L);
/*     */     }
/*     */ 
/*     */     public long distance(Long start, Long end) {
/* 102 */       long result = end.longValue() - start.longValue();
/* 103 */       if ((end.longValue() > start.longValue()) && (result < 0L)) {
/* 104 */         return 9223372036854775807L;
/*     */       }
/* 106 */       if ((end.longValue() < start.longValue()) && (result > 0L)) {
/* 107 */         return -9223372036854775808L;
/*     */       }
/* 109 */       return result;
/*     */     }
/*     */ 
/*     */     public Long minValue() {
/* 113 */       return Long.valueOf(-9223372036854775808L);
/*     */     }
/*     */ 
/*     */     public Long maxValue() {
/* 117 */       return Long.valueOf(9223372036854775807L);
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/* 121 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class IntegerDomain extends DiscreteDomain<Integer>
/*     */     implements Serializable
/*     */   {
/*  49 */     private static final IntegerDomain INSTANCE = new IntegerDomain();
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public Integer next(Integer value)
/*     */     {
/*  52 */       int i = value.intValue();
/*  53 */       return i == 2147483647 ? null : Integer.valueOf(i + 1);
/*     */     }
/*     */ 
/*     */     public Integer previous(Integer value) {
/*  57 */       int i = value.intValue();
/*  58 */       return i == -2147483648 ? null : Integer.valueOf(i - 1);
/*     */     }
/*     */ 
/*     */     public long distance(Integer start, Integer end) {
/*  62 */       return end.intValue() - start.intValue();
/*     */     }
/*     */ 
/*     */     public Integer minValue() {
/*  66 */       return Integer.valueOf(-2147483648);
/*     */     }
/*     */ 
/*     */     public Integer maxValue() {
/*  70 */       return Integer.valueOf(2147483647);
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/*  74 */       return INSTANCE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.DiscreteDomains
 * JD-Core Version:    0.6.2
 */