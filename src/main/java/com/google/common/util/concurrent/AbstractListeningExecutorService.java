/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.CancellationException;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.ExecutorCompletionService;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ 
/*     */ abstract class AbstractListeningExecutorService
/*     */   implements ListeningExecutorService
/*     */ {
/*     */   public ListenableFuture<?> submit(Runnable task)
/*     */   {
/*  43 */     ListenableFutureTask ftask = ListenableFutureTask.create(task, null);
/*  44 */     execute(ftask);
/*  45 */     return ftask;
/*     */   }
/*     */ 
/*     */   public <T> ListenableFuture<T> submit(Runnable task, T result) {
/*  49 */     ListenableFutureTask ftask = ListenableFutureTask.create(task, result);
/*  50 */     execute(ftask);
/*  51 */     return ftask;
/*     */   }
/*     */ 
/*     */   public <T> ListenableFuture<T> submit(Callable<T> task) {
/*  55 */     ListenableFutureTask ftask = ListenableFutureTask.create(task);
/*  56 */     execute(ftask);
/*  57 */     return ftask;
/*     */   }
/*     */ 
/*     */   private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks, boolean timed, long nanos)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/*  65 */     int ntasks = tasks.size();
/*  66 */     Preconditions.checkArgument(ntasks > 0);
/*  67 */     List futures = new ArrayList(ntasks);
/*  68 */     ExecutorCompletionService ecs = new ExecutorCompletionService(this);
/*     */     try
/*     */     {
/*  79 */       ExecutionException ee = null;
/*  80 */       long lastTime = timed ? System.nanoTime() : 0L;
/*  81 */       Iterator it = tasks.iterator();
/*     */ 
/*  84 */       futures.add(ecs.submit((Callable)it.next()));
/*  85 */       ntasks--;
/*  86 */       int active = 1;
/*     */       while (true)
/*     */       {
/*  89 */         Future f = ecs.poll();
/*     */         long now;
/*  90 */         if (f == null)
/*  91 */           if (ntasks > 0) {
/*  92 */             ntasks--;
/*  93 */             futures.add(ecs.submit((Callable)it.next()));
/*  94 */             active++; } else {
/*  95 */             if (active == 0)
/*     */               break;
/*  97 */             if (timed) {
/*  98 */               f = ecs.poll(nanos, TimeUnit.NANOSECONDS);
/*  99 */               if (f == null) {
/* 100 */                 throw new TimeoutException();
/*     */               }
/* 102 */               now = System.nanoTime();
/* 103 */               nanos -= now - lastTime;
/* 104 */               lastTime = now;
/*     */             } else {
/* 106 */               f = ecs.take();
/*     */             }
/*     */           }
/* 109 */         if (f != null) {
/* 110 */           active--;
/*     */           try
/*     */           {
/*     */             Iterator i$;
/*     */             Future f;
/* 112 */             return f.get();
/*     */           } catch (ExecutionException eex) {
/* 114 */             ee = eex;
/*     */           } catch (RuntimeException rex) {
/* 116 */             ee = new ExecutionException(rex);
/*     */           }
/*     */         }
/*     */       }
/*     */ 
/* 121 */       if (ee == null) {
/* 122 */         ee = new ExecutionException(null);
/*     */       }
/* 124 */       throw ee;
/*     */     } finally {
/* 126 */       for (Future f : futures)
/* 127 */         f.cancel(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
/*     */   {
/*     */     try
/*     */     {
/* 135 */       return doInvokeAny(tasks, false, 0L); } catch (TimeoutException cannotHappen) {
/*     */     }
/* 137 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException, ExecutionException, TimeoutException
/*     */   {
/* 144 */     return doInvokeAny(tasks, true, unit.toNanos(timeout));
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
/*     */   {
/* 149 */     if (tasks == null) {
/* 150 */       throw new NullPointerException();
/*     */     }
/* 152 */     List futures = new ArrayList(tasks.size());
/* 153 */     boolean done = false;
/*     */     try {
/* 155 */       for (Callable t : tasks) {
/* 156 */         ListenableFutureTask f = ListenableFutureTask.create(t);
/* 157 */         futures.add(f);
/* 158 */         execute(f);
/*     */       }
/* 160 */       for (Future f : futures)
/* 161 */         if (!f.isDone())
/*     */           try {
/* 163 */             f.get();
/*     */           }
/*     */           catch (CancellationException ignore) {
/*     */           }
/*     */           catch (ExecutionException ignore) {
/*     */           }
/* 169 */       done = true;
/*     */       Iterator i$;
/*     */       Future f;
/* 170 */       return futures;
/*     */     } finally {
/* 172 */       if (!done)
/* 173 */         for (Future f : futures)
/* 174 */           f.cancel(true);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 183 */     if ((tasks == null) || (unit == null)) {
/* 184 */       throw new NullPointerException();
/*     */     }
/* 186 */     long nanos = unit.toNanos(timeout);
/* 187 */     List futures = new ArrayList(tasks.size());
/* 188 */     boolean done = false;
/*     */     try {
/* 190 */       for (Callable t : tasks) {
/* 191 */         futures.add(ListenableFutureTask.create(t));
/*     */       }
/*     */ 
/* 194 */       long lastTime = System.nanoTime();
/*     */ 
/* 198 */       Iterator it = futures.iterator();
/*     */       List localList1;
/* 199 */       while (it.hasNext()) {
/* 200 */         execute((Runnable)it.next());
/* 201 */         long now = System.nanoTime();
/* 202 */         nanos -= now - lastTime;
/* 203 */         lastTime = now;
/* 204 */         if (nanos <= 0L)
/*     */         {
/*     */           Iterator i$;
/*     */           Future f;
/* 205 */           return futures;
/*     */         }
/*     */       }
/*     */ 
/* 209 */       for (Future f : futures) {
/* 210 */         if (!f.isDone())
/*     */         {
/*     */           Iterator i$;
/* 211 */           if (nanos <= 0L)
/*     */           {
/*     */             Future f;
/* 212 */             return futures;
/*     */           }
/*     */           try {
/* 215 */             f.get(nanos, TimeUnit.NANOSECONDS);
/*     */           }
/*     */           catch (CancellationException ignore)
/*     */           {
/*     */           }
/*     */           catch (ExecutionException ignore)
/*     */           {
/*     */           }
/*     */           catch (TimeoutException toe)
/*     */           {
/*     */             Iterator i$;
/*     */             Future f;
/* 219 */             return futures;
/*     */           }
/* 221 */           long now = System.nanoTime();
/* 222 */           nanos -= now - lastTime;
/* 223 */           lastTime = now;
/*     */         }
/*     */       }
/* 226 */       done = true;
/*     */       Iterator i$;
/*     */       Future f;
/* 227 */       return futures;
/*     */     } finally {
/* 229 */       if (!done)
/* 230 */         for (Future f : futures)
/* 231 */           f.cancel(true);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AbstractListeningExecutorService
 * JD-Core Version:    0.6.2
 */