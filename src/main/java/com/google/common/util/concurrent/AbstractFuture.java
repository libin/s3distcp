/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.locks.AbstractQueuedSynchronizer;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public abstract class AbstractFuture<V>
/*     */   implements ListenableFuture<V>
/*     */ {
/*     */   private final Sync<V> sync;
/*     */   private final ExecutionList executionList;
/*     */ 
/*     */   public AbstractFuture()
/*     */   {
/*  68 */     this.sync = new Sync();
/*     */ 
/*  71 */     this.executionList = new ExecutionList();
/*     */   }
/*     */ 
/*     */   public V get(long timeout, TimeUnit unit)
/*     */     throws InterruptedException, TimeoutException, ExecutionException
/*     */   {
/*  91 */     return this.sync.get(unit.toNanos(timeout));
/*     */   }
/*     */ 
/*     */   public V get()
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/* 111 */     return this.sync.get();
/*     */   }
/*     */ 
/*     */   public boolean isDone()
/*     */   {
/* 116 */     return this.sync.isDone();
/*     */   }
/*     */ 
/*     */   public boolean isCancelled()
/*     */   {
/* 121 */     return this.sync.isCancelled();
/*     */   }
/*     */ 
/*     */   public boolean cancel(boolean mayInterruptIfRunning)
/*     */   {
/* 126 */     if (!this.sync.cancel()) {
/* 127 */       return false;
/*     */     }
/* 129 */     this.executionList.execute();
/* 130 */     if (mayInterruptIfRunning) {
/* 131 */       interruptTask();
/*     */     }
/* 133 */     return true;
/*     */   }
/*     */ 
/*     */   protected void interruptTask()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void addListener(Runnable listener, Executor exec)
/*     */   {
/* 155 */     this.executionList.add(listener, exec);
/*     */   }
/*     */ 
/*     */   protected boolean set(@Nullable V value)
/*     */   {
/* 168 */     boolean result = this.sync.set(value);
/* 169 */     if (result) {
/* 170 */       this.executionList.execute();
/*     */     }
/* 172 */     return result;
/*     */   }
/*     */ 
/*     */   protected boolean setException(Throwable throwable)
/*     */   {
/* 186 */     boolean result = this.sync.setException((Throwable)Preconditions.checkNotNull(throwable));
/* 187 */     if (result) {
/* 188 */       this.executionList.execute();
/*     */     }
/*     */ 
/* 193 */     if ((throwable instanceof Error)) {
/* 194 */       throw ((Error)throwable);
/*     */     }
/* 196 */     return result;
/*     */   }
/*     */ 
/*     */   static final class Sync<V> extends AbstractQueuedSynchronizer
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     static final int RUNNING = 0;
/*     */     static final int COMPLETING = 1;
/*     */     static final int COMPLETED = 2;
/*     */     static final int CANCELLED = 4;
/*     */     private V value;
/*     */     private Throwable exception;
/*     */ 
/*     */     protected int tryAcquireShared(int ignored)
/*     */     {
/* 233 */       if (isDone()) {
/* 234 */         return 1;
/*     */       }
/* 236 */       return -1;
/*     */     }
/*     */ 
/*     */     protected boolean tryReleaseShared(int finalState)
/*     */     {
/* 245 */       setState(finalState);
/* 246 */       return true;
/*     */     }
/*     */ 
/*     */     V get(long nanos)
/*     */       throws TimeoutException, CancellationException, ExecutionException, InterruptedException
/*     */     {
/* 258 */       if (!tryAcquireSharedNanos(-1, nanos)) {
/* 259 */         throw new TimeoutException("Timeout waiting for task.");
/*     */       }
/*     */ 
/* 262 */       return getValue();
/*     */     }
/*     */ 
/*     */     V get()
/*     */       throws CancellationException, ExecutionException, InterruptedException
/*     */     {
/* 275 */       acquireSharedInterruptibly(-1);
/* 276 */       return getValue();
/*     */     }
/*     */ 
/*     */     private V getValue()
/*     */       throws CancellationException, ExecutionException
/*     */     {
/* 285 */       int state = getState();
/* 286 */       switch (state) {
/*     */       case 2:
/* 288 */         if (this.exception != null) {
/* 289 */           throw new ExecutionException(this.exception);
/*     */         }
/* 291 */         return this.value;
/*     */       case 4:
/* 295 */         throw new CancellationException("Task was cancelled.");
/*     */       }
/*     */ 
/* 298 */       throw new IllegalStateException("Error, synchronizer in invalid state: " + state);
/*     */     }
/*     */ 
/*     */     boolean isDone()
/*     */     {
/* 307 */       return (getState() & 0x6) != 0;
/*     */     }
/*     */ 
/*     */     boolean isCancelled()
/*     */     {
/* 314 */       return getState() == 4;
/*     */     }
/*     */ 
/*     */     boolean set(@Nullable V v)
/*     */     {
/* 321 */       return complete(v, null, 2);
/*     */     }
/*     */ 
/*     */     boolean setException(Throwable t)
/*     */     {
/* 328 */       return complete(null, t, 2);
/*     */     }
/*     */ 
/*     */     boolean cancel()
/*     */     {
/* 335 */       return complete(null, null, 4);
/*     */     }
/*     */ 
/*     */     private boolean complete(@Nullable V v, @Nullable Throwable t, int finalState)
/*     */     {
/* 351 */       boolean doCompletion = compareAndSetState(0, 1);
/* 352 */       if (doCompletion)
/*     */       {
/* 355 */         this.value = v;
/* 356 */         this.exception = t;
/* 357 */         releaseShared(finalState);
/* 358 */       } else if (getState() == 1)
/*     */       {
/* 361 */         acquireShared(-1);
/*     */       }
/* 363 */       return doCompletion;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AbstractFuture
 * JD-Core Version:    0.6.2
 */