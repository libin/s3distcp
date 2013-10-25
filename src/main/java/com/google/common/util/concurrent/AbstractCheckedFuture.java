/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V>
/*     */   implements CheckedFuture<V, X>
/*     */ {
/*     */   protected AbstractCheckedFuture(ListenableFuture<V> delegate)
/*     */   {
/*  41 */     super(delegate);
/*     */   }
/*     */ 
/*     */   protected abstract X mapException(Exception paramException);
/*     */ 
/*     */   public V checkedGet()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  78 */       return get();
/*     */     } catch (InterruptedException e) {
/*  80 */       Thread.currentThread().interrupt();
/*  81 */       throw mapException(e);
/*     */     } catch (CancellationException e) {
/*  83 */       throw mapException(e);
/*     */     } catch (ExecutionException e) {
/*  85 */       throw mapException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public V checkedGet(long timeout, TimeUnit unit)
/*     */     throws TimeoutException, Exception
/*     */   {
/*     */     try
/*     */     {
/* 107 */       return get(timeout, unit);
/*     */     } catch (InterruptedException e) {
/* 109 */       Thread.currentThread().interrupt();
/* 110 */       throw mapException(e);
/*     */     } catch (CancellationException e) {
/* 112 */       throw mapException(e);
/*     */     } catch (ExecutionException e) {
/* 114 */       throw mapException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AbstractCheckedFuture
 * JD-Core Version:    0.6.2
 */