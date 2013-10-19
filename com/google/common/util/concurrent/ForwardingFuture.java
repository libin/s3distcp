/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import com.google.common.collect.ForwardingObject;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ import java.util.concurrent.Future;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.TimeoutException;
/*    */ 
/*    */ public abstract class ForwardingFuture<V> extends ForwardingObject
/*    */   implements Future<V>
/*    */ {
/*    */   protected abstract Future<V> delegate();
/*    */ 
/*    */   public boolean cancel(boolean mayInterruptIfRunning)
/*    */   {
/* 48 */     return delegate().cancel(mayInterruptIfRunning);
/*    */   }
/*    */ 
/*    */   public boolean isCancelled()
/*    */   {
/* 53 */     return delegate().isCancelled();
/*    */   }
/*    */ 
/*    */   public boolean isDone()
/*    */   {
/* 58 */     return delegate().isDone();
/*    */   }
/*    */ 
/*    */   public V get() throws InterruptedException, ExecutionException
/*    */   {
/* 63 */     return delegate().get();
/*    */   }
/*    */ 
/*    */   public V get(long timeout, TimeUnit unit)
/*    */     throws InterruptedException, ExecutionException, TimeoutException
/*    */   {
/* 69 */     return delegate().get(timeout, unit);
/*    */   }
/*    */ 
/*    */   public static abstract class SimpleForwardingFuture<V> extends ForwardingFuture<V>
/*    */   {
/*    */     private final Future<V> delegate;
/*    */ 
/*    */     protected SimpleForwardingFuture(Future<V> delegate)
/*    */     {
/* 87 */       this.delegate = ((Future)Preconditions.checkNotNull(delegate));
/*    */     }
/*    */ 
/*    */     protected final Future<V> delegate()
/*    */     {
/* 92 */       return this.delegate;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ForwardingFuture
 * JD-Core Version:    0.6.2
 */