/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Throwables;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ @Beta
/*     */ public abstract class AbstractExecutionThreadService
/*     */   implements Service
/*     */ {
/*  37 */   private static final Logger logger = Logger.getLogger(AbstractExecutionThreadService.class.getName());
/*     */ 
/*  41 */   private final Service delegate = new AbstractService() {
/*     */     protected final void doStart() {
/*  43 */       AbstractExecutionThreadService.this.executor().execute(new Runnable()
/*     */       {
/*     */         public void run() {
/*     */           try {
/*  47 */             AbstractExecutionThreadService.this.startUp();
/*  48 */             AbstractExecutionThreadService.1.this.notifyStarted();
/*     */ 
/*  50 */             if (AbstractExecutionThreadService.1.this.isRunning()) {
/*     */               try {
/*  52 */                 AbstractExecutionThreadService.this.run();
/*     */               } catch (Throwable t) {
/*     */                 try {
/*  55 */                   AbstractExecutionThreadService.this.shutDown();
/*     */                 } catch (Exception ignored) {
/*  57 */                   AbstractExecutionThreadService.logger.log(Level.WARNING, "Error while attempting to shut down the service after failure.", ignored);
/*     */                 }
/*     */ 
/*  60 */                 throw t;
/*     */               }
/*     */             }
/*     */ 
/*  64 */             AbstractExecutionThreadService.this.shutDown();
/*  65 */             AbstractExecutionThreadService.1.this.notifyStopped();
/*     */           } catch (Throwable t) {
/*  67 */             AbstractExecutionThreadService.1.this.notifyFailed(t);
/*  68 */             throw Throwables.propagate(t);
/*     */           }
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     protected void doStop() {
/*  75 */       AbstractExecutionThreadService.this.triggerShutdown();
/*     */     }
/*  41 */   };
/*     */ 
/*     */   protected void startUp()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected abstract void run()
/*     */     throws Exception;
/*     */ 
/*     */   protected void shutDown()
/*     */     throws Exception
/*     */   {
/*     */   }
/*     */ 
/*     */   protected void triggerShutdown()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected Executor executor()
/*     */   {
/* 129 */     return new Executor()
/*     */     {
/*     */       public void execute(Runnable command) {
/* 132 */         new Thread(command, AbstractExecutionThreadService.this.getServiceName()).start();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 138 */     return getServiceName() + " [" + state() + "]";
/*     */   }
/*     */ 
/*     */   public final ListenableFuture<Service.State> start()
/*     */   {
/* 144 */     return this.delegate.start();
/*     */   }
/*     */ 
/*     */   public final Service.State startAndWait() {
/* 148 */     return this.delegate.startAndWait();
/*     */   }
/*     */ 
/*     */   public final boolean isRunning() {
/* 152 */     return this.delegate.isRunning();
/*     */   }
/*     */ 
/*     */   public final Service.State state() {
/* 156 */     return this.delegate.state();
/*     */   }
/*     */ 
/*     */   public final ListenableFuture<Service.State> stop() {
/* 160 */     return this.delegate.stop();
/*     */   }
/*     */ 
/*     */   public final Service.State stopAndWait() {
/* 164 */     return this.delegate.stopAndWait();
/*     */   }
/*     */ 
/*     */   protected String getServiceName()
/*     */   {
/* 176 */     return getClass().getSimpleName();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AbstractExecutionThreadService
 * JD-Core Version:    0.6.2
 */