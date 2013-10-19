/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.TimeoutException;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractService
/*     */   implements Service
/*     */ {
/*     */   private final ReentrantLock lock;
/*     */   private final Transition startup;
/*     */   private final Transition shutdown;
/*     */   private Service.State state;
/*     */   private boolean shutdownWhenStartupFinishes;
/*     */ 
/*     */   public AbstractService()
/*     */   {
/*  42 */     this.lock = new ReentrantLock();
/*     */ 
/*  44 */     this.startup = new Transition(null);
/*  45 */     this.shutdown = new Transition(null);
/*     */ 
/*  51 */     this.state = Service.State.NEW;
/*     */ 
/*  57 */     this.shutdownWhenStartupFinishes = false;
/*     */   }
/*     */ 
/*     */   protected abstract void doStart();
/*     */ 
/*     */   protected abstract void doStop();
/*     */ 
/*     */   public final ListenableFuture<Service.State> start()
/*     */   {
/*  86 */     this.lock.lock();
/*     */     try {
/*  88 */       if (this.state == Service.State.NEW) {
/*  89 */         this.state = Service.State.STARTING;
/*  90 */         doStart();
/*     */       }
/*     */     }
/*     */     catch (Throwable startupFailure) {
/*  94 */       notifyFailed(startupFailure);
/*     */     } finally {
/*  96 */       this.lock.unlock();
/*     */     }
/*     */ 
/*  99 */     return this.startup;
/*     */   }
/*     */ 
/*     */   public final ListenableFuture<Service.State> stop()
/*     */   {
/* 104 */     this.lock.lock();
/*     */     try {
/* 106 */       if (this.state == Service.State.NEW) {
/* 107 */         this.state = Service.State.TERMINATED;
/* 108 */         this.startup.set(Service.State.TERMINATED);
/* 109 */         this.shutdown.set(Service.State.TERMINATED);
/* 110 */       } else if (this.state == Service.State.STARTING) {
/* 111 */         this.shutdownWhenStartupFinishes = true;
/* 112 */         this.startup.set(Service.State.STOPPING);
/* 113 */       } else if (this.state == Service.State.RUNNING) {
/* 114 */         this.state = Service.State.STOPPING;
/* 115 */         doStop();
/*     */       }
/*     */     }
/*     */     catch (Throwable shutdownFailure) {
/* 119 */       notifyFailed(shutdownFailure);
/*     */     } finally {
/* 121 */       this.lock.unlock();
/*     */     }
/*     */ 
/* 124 */     return this.shutdown;
/*     */   }
/*     */ 
/*     */   public Service.State startAndWait()
/*     */   {
/* 129 */     return (Service.State)Futures.getUnchecked(start());
/*     */   }
/*     */ 
/*     */   public Service.State stopAndWait()
/*     */   {
/* 134 */     return (Service.State)Futures.getUnchecked(stop());
/*     */   }
/*     */ 
/*     */   protected final void notifyStarted()
/*     */   {
/* 146 */     this.lock.lock();
/*     */     try {
/* 148 */       if (this.state != Service.State.STARTING) {
/* 149 */         IllegalStateException failure = new IllegalStateException("Cannot notifyStarted() when the service is " + this.state);
/*     */ 
/* 151 */         notifyFailed(failure);
/* 152 */         throw failure;
/*     */       }
/*     */ 
/* 155 */       this.state = Service.State.RUNNING;
/* 156 */       if (this.shutdownWhenStartupFinishes)
/* 157 */         stop();
/*     */       else
/* 159 */         this.startup.set(Service.State.RUNNING);
/*     */     }
/*     */     finally {
/* 162 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void notifyStopped()
/*     */   {
/* 175 */     this.lock.lock();
/*     */     try {
/* 177 */       if ((this.state != Service.State.STOPPING) && (this.state != Service.State.RUNNING)) {
/* 178 */         IllegalStateException failure = new IllegalStateException("Cannot notifyStopped() when the service is " + this.state);
/*     */ 
/* 180 */         notifyFailed(failure);
/* 181 */         throw failure;
/*     */       }
/*     */ 
/* 184 */       this.state = Service.State.TERMINATED;
/* 185 */       this.shutdown.set(Service.State.TERMINATED);
/*     */     } finally {
/* 187 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   protected final void notifyFailed(Throwable cause)
/*     */   {
/* 198 */     Preconditions.checkNotNull(cause);
/*     */ 
/* 200 */     this.lock.lock();
/*     */     try {
/* 202 */       if (this.state == Service.State.STARTING) {
/* 203 */         this.startup.setException(cause);
/* 204 */         this.shutdown.setException(new Exception("Service failed to start.", cause));
/*     */       }
/* 206 */       else if (this.state == Service.State.STOPPING) {
/* 207 */         this.shutdown.setException(cause);
/* 208 */       } else if (this.state == Service.State.RUNNING) {
/* 209 */         this.shutdown.setException(new Exception("Service failed while running", cause));
/* 210 */       } else if ((this.state == Service.State.NEW) || (this.state == Service.State.TERMINATED)) {
/* 211 */         throw new IllegalStateException("Failed while in state:" + this.state, cause);
/*     */       }
/* 213 */       this.state = Service.State.FAILED;
/*     */     } finally {
/* 215 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public final boolean isRunning()
/*     */   {
/* 221 */     return state() == Service.State.RUNNING;
/*     */   }
/*     */ 
/*     */   public final Service.State state()
/*     */   {
/* 226 */     this.lock.lock();
/*     */     try
/*     */     {
/*     */       Service.State localState;
/* 228 */       if ((this.shutdownWhenStartupFinishes) && (this.state == Service.State.STARTING)) {
/* 229 */         return Service.State.STOPPING;
/*     */       }
/* 231 */       return this.state;
/*     */     }
/*     */     finally {
/* 234 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 239 */     return getClass().getSimpleName() + " [" + state() + "]";
/*     */   }
/*     */ 
/*     */   private class Transition extends AbstractFuture<Service.State>
/*     */   {
/*     */     private Transition()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Service.State get(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
/*     */       try {
/* 250 */         return (Service.State)super.get(timeout, unit); } catch (TimeoutException e) {
/*     */       }
/* 252 */       throw new TimeoutException(AbstractService.this.toString());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AbstractService
 * JD-Core Version:    0.6.2
 */