/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.concurrent.Executor;
/*    */ 
/*    */ public abstract class ForwardingListenableFuture<V> extends ForwardingFuture<V>
/*    */   implements ListenableFuture<V>
/*    */ {
/*    */   protected abstract ListenableFuture<V> delegate();
/*    */ 
/*    */   public void addListener(Runnable listener, Executor exec)
/*    */   {
/* 47 */     delegate().addListener(listener, exec);
/*    */   }
/*    */ 
/*    */   public static abstract class SimpleForwardingListenableFuture<V> extends ForwardingListenableFuture<V>
/*    */   {
/*    */     private final ListenableFuture<V> delegate;
/*    */ 
/*    */     protected SimpleForwardingListenableFuture(ListenableFuture<V> delegate)
/*    */     {
/* 66 */       this.delegate = ((ListenableFuture)Preconditions.checkNotNull(delegate));
/*    */     }
/*    */ 
/*    */     protected final ListenableFuture<V> delegate()
/*    */     {
/* 71 */       return this.delegate;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ForwardingListenableFuture
 * JD-Core Version:    0.6.2
 */