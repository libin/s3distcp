/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.collect.ForwardingObject;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ public abstract class ForwardingExecutorService extends ForwardingObject
/*     */   implements ExecutorService
/*     */ {
/*     */   protected abstract ExecutorService delegate();
/*     */ 
/*     */   public boolean awaitTermination(long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/*  50 */     return delegate().awaitTermination(timeout, unit);
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
/*     */     throws InterruptedException
/*     */   {
/*  56 */     return delegate().invokeAll(tasks);
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/*  63 */     return delegate().invokeAll(tasks, timeout, unit);
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
/*     */     throws InterruptedException, ExecutionException
/*     */   {
/*  69 */     return delegate().invokeAny(tasks);
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/*  76 */     return delegate().invokeAny(tasks, timeout, unit);
/*     */   }
/*     */ 
/*     */   public boolean isShutdown()
/*     */   {
/*  81 */     return delegate().isShutdown();
/*     */   }
/*     */ 
/*     */   public boolean isTerminated()
/*     */   {
/*  86 */     return delegate().isTerminated();
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/*  91 */     delegate().shutdown();
/*     */   }
/*     */ 
/*     */   public List<Runnable> shutdownNow()
/*     */   {
/*  96 */     return delegate().shutdownNow();
/*     */   }
/*     */ 
/*     */   public void execute(Runnable command)
/*     */   {
/* 101 */     delegate().execute(command);
/*     */   }
/*     */ 
/*     */   public <T> Future<T> submit(Callable<T> task) {
/* 105 */     return delegate().submit(task);
/*     */   }
/*     */ 
/*     */   public Future<?> submit(Runnable task)
/*     */   {
/* 110 */     return delegate().submit(task);
/*     */   }
/*     */ 
/*     */   public <T> Future<T> submit(Runnable task, T result)
/*     */   {
/* 115 */     return delegate().submit(task, result);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ForwardingExecutorService
 * JD-Core Version:    0.6.2
 */