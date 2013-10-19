/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.atomic.AtomicBoolean;
/*     */ 
/*     */ @Beta
/*     */ public final class JdkFutureAdapters
/*     */ {
/*     */   public static <V> ListenableFuture<V> listenInPoolThread(Future<V> future)
/*     */   {
/*  59 */     if ((future instanceof ListenableFuture)) {
/*  60 */       return (ListenableFuture)future;
/*     */     }
/*  62 */     return new ListenableFutureAdapter(future);
/*     */   }
/*     */ 
/*     */   public static <V> ListenableFuture<V> listenInPoolThread(Future<V> future, Executor executor)
/*     */   {
/*  91 */     Preconditions.checkNotNull(executor);
/*  92 */     if ((future instanceof ListenableFuture)) {
/*  93 */       return (ListenableFuture)future;
/*     */     }
/*  95 */     return new ListenableFutureAdapter(future, executor);
/*     */   }
/*     */ 
/*     */   private static class ListenableFutureAdapter<V> extends ForwardingFuture<V>
/*     */     implements ListenableFuture<V>
/*     */   {
/* 111 */     private static final ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ListenableFutureAdapter-thread-%d").build();
/*     */ 
/* 116 */     private static final Executor defaultAdapterExecutor = Executors.newCachedThreadPool(threadFactory);
/*     */     private final Executor adapterExecutor;
/* 122 */     private final ExecutionList executionList = new ExecutionList();
/*     */ 
/* 126 */     private final AtomicBoolean hasListeners = new AtomicBoolean(false);
/*     */     private final Future<V> delegate;
/*     */ 
/*     */     ListenableFutureAdapter(Future<V> delegate)
/*     */     {
/* 132 */       this(delegate, defaultAdapterExecutor);
/*     */     }
/*     */ 
/*     */     ListenableFutureAdapter(Future<V> delegate, Executor adapterExecutor) {
/* 136 */       this.delegate = ((Future)Preconditions.checkNotNull(delegate));
/* 137 */       this.adapterExecutor = ((Executor)Preconditions.checkNotNull(adapterExecutor));
/*     */     }
/*     */ 
/*     */     protected Future<V> delegate()
/*     */     {
/* 142 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public void addListener(Runnable listener, Executor exec)
/*     */     {
/* 147 */       this.executionList.add(listener, exec);
/*     */ 
/* 151 */       if (this.hasListeners.compareAndSet(false, true)) {
/* 152 */         if (this.delegate.isDone())
/*     */         {
/* 155 */           this.executionList.execute();
/* 156 */           return;
/*     */         }
/*     */ 
/* 159 */         this.adapterExecutor.execute(new Runnable()
/*     */         {
/*     */           public void run() {
/*     */             try {
/* 163 */               JdkFutureAdapters.ListenableFutureAdapter.this.delegate.get();
/*     */             } catch (Error e) {
/* 165 */               throw e;
/*     */             } catch (InterruptedException e) {
/* 167 */               Thread.currentThread().interrupt();
/*     */ 
/* 169 */               throw new AssertionError(e);
/*     */             }
/*     */             catch (Throwable e)
/*     */             {
/*     */             }
/* 174 */             JdkFutureAdapters.ListenableFutureAdapter.this.executionList.execute();
/*     */           }
/*     */         });
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.JdkFutureAdapters
 * JD-Core Version:    0.6.2
 */