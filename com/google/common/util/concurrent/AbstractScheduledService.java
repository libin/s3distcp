/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Throwables;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.Future;
/*     */ import java.util.concurrent.ScheduledExecutorService;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractScheduledService
/*     */   implements Service
/*     */ {
/*  91 */   private static final Logger logger = Logger.getLogger(AbstractScheduledService.class.getName());
/*     */   private final AbstractService delegate;
/*     */ 
/*     */   public AbstractScheduledService()
/*     */   {
/* 154 */     this.delegate = new AbstractService()
/*     */     {
/*     */       private volatile Future<?> runningTask;
/*     */       private volatile ScheduledExecutorService executorService;
/* 163 */       private final ReentrantLock lock = new ReentrantLock();
/*     */ 
/* 165 */       private final Runnable task = new Runnable() {
/*     */         public void run() {
/* 167 */           AbstractScheduledService.1.this.lock.lock();
/*     */           try {
/* 169 */             AbstractScheduledService.this.runOneIteration();
/*     */           } catch (Throwable t) {
/*     */             try {
/* 172 */               AbstractScheduledService.this.shutDown();
/*     */             } catch (Exception ignored) {
/* 174 */               AbstractScheduledService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
/*     */             }
/*     */ 
/* 177 */             AbstractScheduledService.1.this.notifyFailed(t);
/* 178 */             throw Throwables.propagate(t);
/*     */           } finally {
/* 180 */             AbstractScheduledService.1.this.lock.unlock();
/*     */           }
/*     */         }
/* 165 */       };
/*     */ 
/*     */       protected final void doStart()
/*     */       {
/* 186 */         this.executorService = AbstractScheduledService.this.executor();
/* 187 */         this.executorService.execute(new Runnable() {
/*     */           public void run() {
/* 189 */             AbstractScheduledService.1.this.lock.lock();
/*     */             try {
/* 191 */               AbstractScheduledService.this.startUp();
/* 192 */               AbstractScheduledService.1.this.runningTask = AbstractScheduledService.this.scheduler().schedule(AbstractScheduledService.this.delegate, AbstractScheduledService.1.this.executorService, AbstractScheduledService.1.this.task);
/* 193 */               AbstractScheduledService.1.this.notifyStarted();
/*     */             } catch (Throwable t) {
/* 195 */               AbstractScheduledService.1.this.notifyFailed(t);
/* 196 */               throw Throwables.propagate(t);
/*     */             } finally {
/* 198 */               AbstractScheduledService.1.this.lock.unlock();
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */ 
/*     */       protected final void doStop() {
/* 205 */         this.runningTask.cancel(false);
/* 206 */         this.executorService.execute(new Runnable() {
/*     */           public void run() {
/*     */             try {
/* 209 */               AbstractScheduledService.1.this.lock.lock();
/*     */               try {
/* 211 */                 if (AbstractScheduledService.1.this.state() != Service.State.STOPPING)
/*     */                 {
/*     */                   return;
/*     */                 }
/*     */ 
/* 218 */                 AbstractScheduledService.this.shutDown();
/*     */               } finally {
/* 220 */                 AbstractScheduledService.1.this.lock.unlock();
/*     */               }
/* 222 */               AbstractScheduledService.1.this.notifyStopped();
/*     */             } catch (Throwable t) {
/* 224 */               AbstractScheduledService.1.this.notifyFailed(t);
/* 225 */               throw Throwables.propagate(t);
/*     */             }
/*     */           }
/*     */         });
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   protected abstract void runOneIteration()
/*     */     throws Exception;
/*     */ 
/*     */   protected void startUp()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void shutDown()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract Scheduler scheduler();
/*     */ 
/*     */   protected ScheduledExecutorService executor()
/*     */   {
/* 269 */     return Executors.newSingleThreadScheduledExecutor();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 273 */     return getClass().getSimpleName() + " [" + state() + "]";
/*     */   }
/*     */ 
/*     */   public final ListenableFuture<Service.State> start()
/*     */   {
/* 279 */     return this.delegate.start();
/*     */   }
/*     */ 
/*     */   public final Service.State startAndWait() {
/* 283 */     return this.delegate.startAndWait();
/*     */   }
/*     */ 
/*     */   public final boolean isRunning() {
/* 287 */     return this.delegate.isRunning();
/*     */   }
/*     */ 
/*     */   public final Service.State state() {
/* 291 */     return this.delegate.state();
/*     */   }
/*     */ 
/*     */   public final ListenableFuture<Service.State> stop() {
/* 295 */     return this.delegate.stop();
/*     */   }
/*     */ 
/*     */   public final Service.State stopAndWait() {
/* 299 */     return this.delegate.stopAndWait();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static abstract class CustomScheduler extends AbstractScheduledService.Scheduler
/*     */   {
/*     */     public CustomScheduler()
/*     */     {
/* 311 */       super();
/*     */     }
/*     */ 
/*     */     final Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable runnable)
/*     */     {
/* 403 */       ReschedulableCallable task = new ReschedulableCallable(service, executor, runnable);
/* 404 */       task.reschedule();
/* 405 */       return task;
/*     */     }
/*     */ 
/*     */     protected abstract Schedule getNextSchedule()
/*     */       throws Exception;
/*     */ 
/*     */     @Beta
/*     */     protected static final class Schedule
/*     */     {
/*     */       private final long delay;
/*     */       private final TimeUnit unit;
/*     */ 
/*     */       public Schedule(long delay, TimeUnit unit)
/*     */       {
/* 425 */         this.delay = delay;
/* 426 */         this.unit = ((TimeUnit)Preconditions.checkNotNull(unit));
/*     */       }
/*     */     }
/*     */ 
/*     */     private class ReschedulableCallable extends ForwardingFuture<Void>
/*     */       implements Callable<Void>
/*     */     {
/*     */       private final Runnable wrappedRunnable;
/*     */       private final ScheduledExecutorService executor;
/*     */       private final AbstractService service;
/* 335 */       private final ReentrantLock lock = new ReentrantLock();
/*     */ 
/*     */       @GuardedBy("lock")
/*     */       private Future<Void> currentFuture;
/*     */ 
/*     */       ReschedulableCallable(AbstractService service, ScheduledExecutorService executor, Runnable runnable)
/*     */       {
/* 343 */         this.wrappedRunnable = runnable;
/* 344 */         this.executor = executor;
/* 345 */         this.service = service;
/*     */       }
/*     */ 
/*     */       public Void call() throws Exception
/*     */       {
/* 350 */         this.wrappedRunnable.run();
/* 351 */         reschedule();
/* 352 */         return null;
/*     */       }
/*     */ 
/*     */       public void reschedule()
/*     */       {
/* 363 */         this.lock.lock();
/*     */         try {
/* 365 */           if ((this.currentFuture == null) || (!this.currentFuture.isCancelled())) {
/* 366 */             AbstractScheduledService.CustomScheduler.Schedule schedule = AbstractScheduledService.CustomScheduler.this.getNextSchedule();
/* 367 */             this.currentFuture = this.executor.schedule(this, schedule.delay, schedule.unit);
/*     */           }
/*     */ 
/*     */         }
/*     */         catch (Throwable e)
/*     */         {
/* 375 */           this.service.notifyFailed(e);
/*     */         } finally {
/* 377 */           this.lock.unlock();
/*     */         }
/*     */       }
/*     */ 
/*     */       public boolean cancel(boolean mayInterruptIfRunning)
/*     */       {
/* 386 */         this.lock.lock();
/*     */         try {
/* 388 */           return this.currentFuture.cancel(mayInterruptIfRunning);
/*     */         } finally {
/* 390 */           this.lock.unlock();
/*     */         }
/*     */       }
/*     */ 
/*     */       protected Future<Void> delegate()
/*     */       {
/* 396 */         throw new UnsupportedOperationException("Only cancel is supported by this future");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static abstract class Scheduler
/*     */   {
/*     */     public static Scheduler newFixedDelaySchedule(long initialDelay, long delay, final TimeUnit unit)
/*     */     {
/* 118 */       return new Scheduler(initialDelay)
/*     */       {
/*     */         public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
/*     */         {
/* 122 */           return executor.scheduleWithFixedDelay(task, this.val$initialDelay, unit, this.val$unit);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public static Scheduler newFixedRateSchedule(long initialDelay, long period, final TimeUnit unit)
/*     */     {
/* 137 */       return new Scheduler(initialDelay)
/*     */       {
/*     */         public Future<?> schedule(AbstractService service, ScheduledExecutorService executor, Runnable task)
/*     */         {
/* 141 */           return executor.scheduleAtFixedRate(task, this.val$initialDelay, unit, this.val$unit);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     abstract Future<?> schedule(AbstractService paramAbstractService, ScheduledExecutorService paramScheduledExecutorService, Runnable paramRunnable);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AbstractScheduledService
 * JD-Core Version:    0.6.2
 */