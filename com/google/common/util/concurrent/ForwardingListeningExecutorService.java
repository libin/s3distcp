/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ 
/*    */ public abstract class ForwardingListeningExecutorService extends ForwardingExecutorService
/*    */   implements ListeningExecutorService
/*    */ {
/*    */   protected abstract ListeningExecutorService delegate();
/*    */ 
/*    */   public <T> ListenableFuture<T> submit(Callable<T> task)
/*    */   {
/* 40 */     return delegate().submit(task);
/*    */   }
/*    */ 
/*    */   public ListenableFuture<?> submit(Runnable task)
/*    */   {
/* 45 */     return delegate().submit(task);
/*    */   }
/*    */ 
/*    */   public <T> ListenableFuture<T> submit(Runnable task, T result)
/*    */   {
/* 50 */     return delegate().submit(task, result);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ForwardingListeningExecutorService
 * JD-Core Version:    0.6.2
 */