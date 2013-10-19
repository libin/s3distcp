/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.collect.ForwardingObject;
/*    */ 
/*    */ @Beta
/*    */ public abstract class ForwardingService extends ForwardingObject
/*    */   implements Service
/*    */ {
/*    */   protected abstract Service delegate();
/*    */ 
/*    */   public ListenableFuture<Service.State> start()
/*    */   {
/* 38 */     return delegate().start();
/*    */   }
/*    */ 
/*    */   public Service.State state() {
/* 42 */     return delegate().state();
/*    */   }
/*    */ 
/*    */   public ListenableFuture<Service.State> stop() {
/* 46 */     return delegate().stop();
/*    */   }
/*    */ 
/*    */   public Service.State startAndWait() {
/* 50 */     return delegate().startAndWait();
/*    */   }
/*    */ 
/*    */   public Service.State stopAndWait() {
/* 54 */     return delegate().stopAndWait();
/*    */   }
/*    */ 
/*    */   public boolean isRunning() {
/* 58 */     return delegate().isRunning();
/*    */   }
/*    */ 
/*    */   protected Service.State standardStartAndWait()
/*    */   {
/* 68 */     return (Service.State)Futures.getUnchecked(start());
/*    */   }
/*    */ 
/*    */   protected Service.State standardStopAndWait()
/*    */   {
/* 78 */     return (Service.State)Futures.getUnchecked(stop());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ForwardingService
 * JD-Core Version:    0.6.2
 */