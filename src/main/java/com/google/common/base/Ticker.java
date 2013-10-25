/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public abstract class Ticker
/*    */ {
/* 57 */   private static final Ticker SYSTEM_TICKER = new Ticker()
/*    */   {
/*    */     public long read() {
/* 60 */       return Platform.systemNanoTime();
/*    */     }
/* 57 */   };
/*    */ 
/*    */   public abstract long read();
/*    */ 
/*    */   public static Ticker systemTicker()
/*    */   {
/* 54 */     return SYSTEM_TICKER;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Ticker
 * JD-Core Version:    0.6.2
 */