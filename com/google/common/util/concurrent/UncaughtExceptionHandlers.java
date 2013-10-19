/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.VisibleForTesting;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ 
/*    */ public final class UncaughtExceptionHandlers
/*    */ {
/*    */   public static Thread.UncaughtExceptionHandler systemExit()
/*    */   {
/* 47 */     return new Exiter(Runtime.getRuntime());
/*    */   }
/*    */   @VisibleForTesting
/*    */   static final class Exiter implements Thread.UncaughtExceptionHandler {
/* 51 */     private static final Logger logger = Logger.getLogger(Exiter.class.getName());
/*    */     private final Runtime runtime;
/*    */ 
/*    */     Exiter(Runtime runtime) {
/* 56 */       this.runtime = runtime;
/*    */     }
/*    */ 
/*    */     public void uncaughtException(Thread t, Throwable e)
/*    */     {
/* 61 */       logger.log(Level.SEVERE, String.format("Caught an exception in %s.  Shutting down.", new Object[] { t }), e);
/* 62 */       this.runtime.exit(1);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.UncaughtExceptionHandlers
 * JD-Core Version:    0.6.2
 */