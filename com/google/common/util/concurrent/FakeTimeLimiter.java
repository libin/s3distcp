/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ @Beta
/*    */ public final class FakeTimeLimiter
/*    */   implements TimeLimiter
/*    */ {
/*    */   public <T> T newProxy(T target, Class<T> interfaceType, long timeoutDuration, TimeUnit timeoutUnit)
/*    */   {
/* 39 */     return target;
/*    */   }
/*    */ 
/*    */   public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible)
/*    */     throws Exception
/*    */   {
/* 45 */     return callable.call();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.FakeTimeLimiter
 * JD-Core Version:    0.6.2
 */