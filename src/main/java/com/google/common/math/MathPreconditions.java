/*    */ package com.google.common.math;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class MathPreconditions
/*    */ {
/*    */   static int checkPositive(String role, int x)
/*    */   {
/* 33 */     if (x <= 0) {
/* 34 */       throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
/*    */     }
/* 36 */     return x;
/*    */   }
/*    */ 
/*    */   static long checkPositive(String role, long x) {
/* 40 */     if (x <= 0L) {
/* 41 */       throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
/*    */     }
/* 43 */     return x;
/*    */   }
/*    */ 
/*    */   static BigInteger checkPositive(String role, BigInteger x) {
/* 47 */     if (x.signum() <= 0) {
/* 48 */       throw new IllegalArgumentException(role + " (" + x + ") must be > 0");
/*    */     }
/* 50 */     return x;
/*    */   }
/*    */ 
/*    */   static int checkNonNegative(String role, int x) {
/* 54 */     if (x < 0) {
/* 55 */       throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
/*    */     }
/* 57 */     return x;
/*    */   }
/*    */ 
/*    */   static long checkNonNegative(String role, long x) {
/* 61 */     if (x < 0L) {
/* 62 */       throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
/*    */     }
/* 64 */     return x;
/*    */   }
/*    */ 
/*    */   static BigInteger checkNonNegative(String role, BigInteger x) {
/* 68 */     if (((BigInteger)Preconditions.checkNotNull(x)).signum() < 0) {
/* 69 */       throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
/*    */     }
/* 71 */     return x;
/*    */   }
/*    */ 
/*    */   static double checkNonNegative(String role, double x) {
/* 75 */     if (x < 0.0D) {
/* 76 */       throw new IllegalArgumentException(role + " (" + x + ") must be >= 0");
/*    */     }
/* 78 */     return x;
/*    */   }
/*    */ 
/*    */   static void checkRoundingUnnecessary(boolean condition) {
/* 82 */     if (!condition)
/* 83 */       throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
/*    */   }
/*    */ 
/*    */   static void checkInRange(boolean condition)
/*    */   {
/* 88 */     if (!condition)
/* 89 */       throw new ArithmeticException("not in range");
/*    */   }
/*    */ 
/*    */   static void checkNoOverflow(boolean condition)
/*    */   {
/* 94 */     if (!condition)
/* 95 */       throw new ArithmeticException("overflow");
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.math.MathPreconditions
 * JD-Core Version:    0.6.2
 */