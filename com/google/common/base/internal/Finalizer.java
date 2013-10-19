/*     */ package com.google.common.base.internal;
/*     */ 
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class Finalizer
/*     */   implements Runnable
/*     */ {
/*  51 */   private static final Logger logger = Logger.getLogger(Finalizer.class.getName());
/*     */   private static final String FINALIZABLE_REFERENCE = "com.google.common.base.FinalizableReference";
/*     */   private final WeakReference<Class<?>> finalizableReferenceClassReference;
/*     */   private final PhantomReference<Object> frqReference;
/* 102 */   private final ReferenceQueue<Object> queue = new ReferenceQueue();
/*     */ 
/* 104 */   private static final Field inheritableThreadLocals = getInheritableThreadLocalsField();
/*     */ 
/*     */   public static ReferenceQueue<Object> startFinalizer(Class<?> finalizableReferenceClass, Object frq)
/*     */   {
/*  77 */     if (!finalizableReferenceClass.getName().equals("com.google.common.base.FinalizableReference")) {
/*  78 */       throw new IllegalArgumentException("Expected com.google.common.base.FinalizableReference.");
/*     */     }
/*     */ 
/*  82 */     Finalizer finalizer = new Finalizer(finalizableReferenceClass, frq);
/*  83 */     Thread thread = new Thread(finalizer);
/*  84 */     thread.setName(Finalizer.class.getName());
/*  85 */     thread.setDaemon(true);
/*     */     try
/*     */     {
/*  88 */       if (inheritableThreadLocals != null)
/*  89 */         inheritableThreadLocals.set(thread, null);
/*     */     }
/*     */     catch (Throwable t) {
/*  92 */       logger.log(Level.INFO, "Failed to clear thread local values inherited by reference finalizer thread.", t);
/*     */     }
/*     */ 
/*  96 */     thread.start();
/*  97 */     return finalizer.queue;
/*     */   }
/*     */ 
/*     */   private Finalizer(Class<?> finalizableReferenceClass, Object frq)
/*     */   {
/* 109 */     this.finalizableReferenceClassReference = new WeakReference(finalizableReferenceClass);
/*     */ 
/* 113 */     this.frqReference = new PhantomReference(frq, this.queue);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*     */       while (true)
/*     */         try
/*     */         {
/* 125 */           cleanUp(this.queue.remove());
/*     */         }
/*     */         catch (InterruptedException e) {
/*     */         }
/*     */     }
/*     */     catch (ShutDown shutDown) {
/*     */     }
/*     */   }
/*     */ 
/*     */   private void cleanUp(Reference<?> reference) throws Finalizer.ShutDown {
/* 135 */     Method finalizeReferentMethod = getFinalizeReferentMethod();
/*     */     do
/*     */     {
/* 141 */       reference.clear();
/*     */ 
/* 143 */       if (reference == this.frqReference)
/*     */       {
/* 148 */         throw new ShutDown(null);
/*     */       }
/*     */       try
/*     */       {
/* 152 */         finalizeReferentMethod.invoke(reference, new Object[0]);
/*     */       } catch (Throwable t) {
/* 154 */         logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 161 */     while ((reference = this.queue.poll()) != null);
/*     */   }
/*     */ 
/*     */   private Method getFinalizeReferentMethod()
/*     */     throws Finalizer.ShutDown
/*     */   {
/* 168 */     Class finalizableReferenceClass = (Class)this.finalizableReferenceClassReference.get();
/*     */ 
/* 170 */     if (finalizableReferenceClass == null)
/*     */     {
/* 179 */       throw new ShutDown(null);
/*     */     }
/*     */     try {
/* 182 */       return finalizableReferenceClass.getMethod("finalizeReferent", new Class[0]);
/*     */     } catch (NoSuchMethodException e) {
/* 184 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Field getInheritableThreadLocalsField() {
/*     */     try {
/* 190 */       Field inheritableThreadLocals = Thread.class.getDeclaredField("inheritableThreadLocals");
/*     */ 
/* 192 */       inheritableThreadLocals.setAccessible(true);
/* 193 */       return inheritableThreadLocals;
/*     */     } catch (Throwable t) {
/* 195 */       logger.log(Level.INFO, "Couldn't access Thread.inheritableThreadLocals. Reference finalizer threads will inherit thread local values.");
/*     */     }
/*     */ 
/* 198 */     return null;
/*     */   }
/*     */ 
/*     */   private static class ShutDown extends Exception
/*     */   {
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.internal.Finalizer
 * JD-Core Version:    0.6.2
 */