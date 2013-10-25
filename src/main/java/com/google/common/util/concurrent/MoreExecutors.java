/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.ScheduledFuture;
/*     */ import java.util.concurrent.ScheduledThreadPoolExecutor;
/*     */ import java.util.concurrent.ThreadPoolExecutor;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.Lock;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ public final class MoreExecutors
/*     */ {
/*     */   @Beta
/*     */   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit)
/*     */   {
/*  70 */     executor.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setThreadFactory(executor.getThreadFactory()).build());
/*     */ 
/*  75 */     ExecutorService service = Executors.unconfigurableExecutorService(executor);
/*     */ 
/*  77 */     addDelayedShutdownHook(service, terminationTimeout, timeUnit);
/*     */ 
/*  79 */     return service;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor, long terminationTimeout, TimeUnit timeUnit)
/*     */   {
/* 102 */     executor.setThreadFactory(new ThreadFactoryBuilder().setDaemon(true).setThreadFactory(executor.getThreadFactory()).build());
/*     */ 
/* 107 */     ScheduledExecutorService service = Executors.unconfigurableScheduledExecutorService(executor);
/*     */ 
/* 110 */     addDelayedShutdownHook(service, terminationTimeout, timeUnit);
/*     */ 
/* 112 */     return service;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static void addDelayedShutdownHook(ExecutorService service, final long terminationTimeout, TimeUnit timeUnit)
/*     */   {
/* 130 */     Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
/*     */     {
/*     */       public void run()
/*     */       {
/*     */         try
/*     */         {
/* 139 */           this.val$service.shutdown();
/* 140 */           this.val$service.awaitTermination(terminationTimeout, this.val$timeUnit);
/*     */         }
/*     */         catch (InterruptedException ignored)
/*     */         {
/*     */         }
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static ExecutorService getExitingExecutorService(ThreadPoolExecutor executor)
/*     */   {
/* 166 */     return getExitingExecutorService(executor, 120L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static ScheduledExecutorService getExitingScheduledExecutorService(ScheduledThreadPoolExecutor executor)
/*     */   {
/* 187 */     return getExitingScheduledExecutorService(executor, 120L, TimeUnit.SECONDS);
/*     */   }
/*     */ 
/*     */   public static ListeningExecutorService sameThreadExecutor()
/*     */   {
/* 224 */     return new SameThreadExecutorService(null);
/*     */   }
/*     */ 
/*     */   public static ListeningExecutorService listeningDecorator(ExecutorService delegate)
/*     */   {
/* 371 */     return (delegate instanceof ScheduledExecutorService) ? new ScheduledListeningDecorator((ScheduledExecutorService)delegate) : (delegate instanceof ListeningExecutorService) ? (ListeningExecutorService)delegate : new ListeningDecorator(delegate);
/*     */   }
/*     */ 
/*     */   public static ListeningScheduledExecutorService listeningDecorator(ScheduledExecutorService delegate)
/*     */   {
/* 399 */     return (delegate instanceof ListeningScheduledExecutorService) ? (ListeningScheduledExecutorService)delegate : new ScheduledListeningDecorator(delegate);
/*     */   }
/*     */ 
/*     */   private static class ScheduledListeningDecorator extends MoreExecutors.ListeningDecorator
/*     */     implements ListeningScheduledExecutorService
/*     */   {
/*     */     final ScheduledExecutorService delegate;
/*     */ 
/*     */     ScheduledListeningDecorator(ScheduledExecutorService delegate)
/*     */     {
/* 449 */       super();
/* 450 */       this.delegate = ((ScheduledExecutorService)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */ 
/*     */     public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
/*     */     {
/* 456 */       return this.delegate.schedule(command, delay, unit);
/*     */     }
/*     */ 
/*     */     public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
/*     */     {
/* 462 */       return this.delegate.schedule(callable, delay, unit);
/*     */     }
/*     */ 
/*     */     public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
/*     */     {
/* 468 */       return this.delegate.scheduleAtFixedRate(command, initialDelay, period, unit);
/*     */     }
/*     */ 
/*     */     public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
/*     */     {
/* 474 */       return this.delegate.scheduleWithFixedDelay(command, initialDelay, delay, unit);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ListeningDecorator extends AbstractListeningExecutorService
/*     */   {
/*     */     final ExecutorService delegate;
/*     */ 
/*     */     ListeningDecorator(ExecutorService delegate)
/*     */     {
/* 409 */       this.delegate = ((ExecutorService)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */ 
/*     */     public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */       throws InterruptedException
/*     */     {
/* 415 */       return this.delegate.awaitTermination(timeout, unit);
/*     */     }
/*     */ 
/*     */     public boolean isShutdown()
/*     */     {
/* 420 */       return this.delegate.isShutdown();
/*     */     }
/*     */ 
/*     */     public boolean isTerminated()
/*     */     {
/* 425 */       return this.delegate.isTerminated();
/*     */     }
/*     */ 
/*     */     public void shutdown()
/*     */     {
/* 430 */       this.delegate.shutdown();
/*     */     }
/*     */ 
/*     */     public List<Runnable> shutdownNow()
/*     */     {
/* 435 */       return this.delegate.shutdownNow();
/*     */     }
/*     */ 
/*     */     public void execute(Runnable command)
/*     */     {
/* 440 */       this.delegate.execute(command);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SameThreadExecutorService extends AbstractListeningExecutorService
/*     */   {
/* 234 */     private final Lock lock = new ReentrantLock();
/*     */ 
/* 237 */     private final Condition termination = this.lock.newCondition();
/*     */ 
/* 246 */     private int runningTasks = 0;
/* 247 */     private boolean shutdown = false;
/*     */ 
/*     */     public void execute(Runnable command)
/*     */     {
/* 251 */       startTask();
/*     */       try {
/* 253 */         command.run();
/*     */       } finally {
/* 255 */         endTask();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean isShutdown()
/*     */     {
/* 261 */       this.lock.lock();
/*     */       try {
/* 263 */         return this.shutdown;
/*     */       } finally {
/* 265 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void shutdown()
/*     */     {
/* 271 */       this.lock.lock();
/*     */       try {
/* 273 */         this.shutdown = true;
/*     */       } finally {
/* 275 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public List<Runnable> shutdownNow()
/*     */     {
/* 282 */       shutdown();
/* 283 */       return Collections.emptyList();
/*     */     }
/*     */ 
/*     */     public boolean isTerminated()
/*     */     {
/* 288 */       this.lock.lock();
/*     */       try {
/* 290 */         return (this.shutdown) && (this.runningTasks == 0);
/*     */       } finally {
/* 292 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */       throws InterruptedException
/*     */     {
/* 299 */       long nanos = unit.toNanos(timeout);
/* 300 */       this.lock.lock();
/*     */       try
/*     */       {
/*     */         while (true)
/*     */         {
/*     */           boolean bool;
/* 303 */           if (isTerminated())
/* 304 */             return true;
/* 305 */           if (nanos <= 0L) {
/* 306 */             return false;
/*     */           }
/* 308 */           nanos = this.termination.awaitNanos(nanos);
/*     */         }
/*     */       }
/*     */       finally {
/* 312 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void startTask()
/*     */     {
/* 324 */       this.lock.lock();
/*     */       try {
/* 326 */         if (isShutdown()) {
/* 327 */           throw new RejectedExecutionException("Executor already shutdown");
/*     */         }
/* 329 */         this.runningTasks += 1;
/*     */       } finally {
/* 331 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void endTask()
/*     */     {
/* 339 */       this.lock.lock();
/*     */       try {
/* 341 */         this.runningTasks -= 1;
/* 342 */         if (isTerminated())
/* 343 */           this.termination.signalAll();
/*     */       }
/*     */       finally {
/* 346 */         this.lock.unlock();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.MoreExecutors
 * JD-Core Version:    0.6.2
 */