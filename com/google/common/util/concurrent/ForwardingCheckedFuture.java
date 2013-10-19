/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import java.util.concurrent.TimeoutException;
/*    */ 
/*    */ @Beta
/*    */ public abstract class ForwardingCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture<V>
/*    */   implements CheckedFuture<V, X>
/*    */ {
/*    */   public V checkedGet()
/*    */     throws Exception
/*    */   {
/* 46 */     return delegate().checkedGet();
/*    */   }
/*    */ 
/*    */   public V checkedGet(long timeout, TimeUnit unit) throws TimeoutException, Exception
/*    */   {
/* 51 */     return delegate().checkedGet(timeout, unit);
/*    */   }
/*    */ 
/*    */   protected abstract CheckedFuture<V, X> delegate();
/*    */ 
/*    */   @Beta
/*    */   public static abstract class SimpleForwardingCheckedFuture<V, X extends Exception> extends ForwardingCheckedFuture<V, X>
/*    */   {
/*    */     private final CheckedFuture<V, X> delegate;
/*    */ 
/*    */     protected SimpleForwardingCheckedFuture(CheckedFuture<V, X> delegate)
/*    */     {
/* 70 */       this.delegate = ((CheckedFuture)Preconditions.checkNotNull(delegate));
/*    */     }
/*    */ 
/*    */     protected final CheckedFuture<V, X> delegate()
/*    */     {
/* 75 */       return this.delegate;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ForwardingCheckedFuture
 * JD-Core Version:    0.6.2
 */