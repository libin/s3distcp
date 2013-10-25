/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ final class Platform
/*    */ {
/* 45 */   private static final ThreadLocal<char[]> DEST_TL = new ThreadLocal()
/*    */   {
/*    */     protected char[] initialValue() {
/* 48 */       return new char[1024];
/*    */     }
/* 45 */   };
/*    */ 
/*    */   static char[] charBufferFromThreadLocal()
/*    */   {
/* 32 */     return (char[])DEST_TL.get();
/*    */   }
/*    */ 
/*    */   static long systemNanoTime()
/*    */   {
/* 37 */     return System.nanoTime();
/*    */   }
/*    */ 
/*    */   static CharMatcher precomputeCharMatcher(CharMatcher matcher)
/*    */   {
/* 53 */     return matcher.precomputedInternal();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Platform
 * JD-Core Version:    0.6.2
 */