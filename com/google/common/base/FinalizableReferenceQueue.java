/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.lang.ref.Reference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.net.URLClassLoader;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public class FinalizableReferenceQueue
/*     */ {
/*  79 */   private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());
/*     */   private static final String FINALIZER_CLASS_NAME = "com.google.common.base.internal.Finalizer";
/*  88 */   private static final Method startFinalizer = getStartFinalizer(finalizer);
/*     */   final ReferenceQueue<Object> queue;
/*     */   final boolean threadStarted;
/*     */ 
/*     */   public FinalizableReferenceQueue()
/*     */   {
/* 108 */     boolean threadStarted = false;
/*     */     ReferenceQueue queue;
/*     */     try
/*     */     {
/* 110 */       queue = (ReferenceQueue)startFinalizer.invoke(null, new Object[] { FinalizableReference.class, this });
/*     */ 
/* 112 */       threadStarted = true;
/*     */     } catch (IllegalAccessException impossible) {
/* 114 */       throw new AssertionError(impossible);
/*     */     } catch (Throwable t) {
/* 116 */       logger.log(Level.INFO, "Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.", t);
/*     */ 
/* 118 */       queue = new ReferenceQueue();
/*     */     }
/*     */ 
/* 121 */     this.queue = queue;
/* 122 */     this.threadStarted = threadStarted;
/*     */   }
/*     */ 
/*     */   void cleanUp()
/*     */   {
/* 131 */     if (this.threadStarted)
/*     */       return;
/*     */     Reference reference;
/* 136 */     while ((reference = this.queue.poll()) != null)
/*     */     {
/* 141 */       reference.clear();
/*     */       try {
/* 143 */         ((FinalizableReference)reference).finalizeReferent();
/*     */       } catch (Throwable t) {
/* 145 */         logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Class<?> loadFinalizer(FinalizerLoader[] loaders)
/*     */   {
/* 156 */     for (FinalizerLoader loader : loaders) {
/* 157 */       Class finalizer = loader.loadFinalizer();
/* 158 */       if (finalizer != null) {
/* 159 */         return finalizer;
/*     */       }
/*     */     }
/*     */ 
/* 163 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   static Method getStartFinalizer(Class<?> finalizer)
/*     */   {
/*     */     try
/*     */     {
/* 294 */       return finalizer.getMethod("startFinalizer", new Class[] { Class.class, Object.class });
/*     */     } catch (NoSuchMethodException e) {
/* 296 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  86 */     Class finalizer = loadFinalizer(new FinalizerLoader[] { new SystemLoader(), new DecoupledLoader(), new DirectLoader() });
/*     */   }
/*     */ 
/*     */   static class DirectLoader
/*     */     implements FinalizableReferenceQueue.FinalizerLoader
/*     */   {
/*     */     public Class<?> loadFinalizer()
/*     */     {
/*     */       try
/*     */       {
/* 282 */         return Class.forName("com.google.common.base.internal.Finalizer");
/*     */       } catch (ClassNotFoundException e) {
/* 284 */         throw new AssertionError(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class DecoupledLoader
/*     */     implements FinalizableReferenceQueue.FinalizerLoader
/*     */   {
/*     */     private static final String LOADING_ERROR = "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.";
/*     */ 
/*     */     public Class<?> loadFinalizer()
/*     */     {
/*     */       try
/*     */       {
/* 237 */         ClassLoader finalizerLoader = newLoader(getBaseUrl());
/* 238 */         return finalizerLoader.loadClass("com.google.common.base.internal.Finalizer");
/*     */       } catch (Exception e) {
/* 240 */         FinalizableReferenceQueue.logger.log(Level.WARNING, "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.", e);
/* 241 */       }return null;
/*     */     }
/*     */ 
/*     */     URL getBaseUrl()
/*     */       throws IOException
/*     */     {
/* 250 */       String finalizerPath = "com.google.common.base.internal.Finalizer".replace('.', '/') + ".class";
/* 251 */       URL finalizerUrl = getClass().getClassLoader().getResource(finalizerPath);
/* 252 */       if (finalizerUrl == null) {
/* 253 */         throw new FileNotFoundException(finalizerPath);
/*     */       }
/*     */ 
/* 257 */       String urlString = finalizerUrl.toString();
/* 258 */       if (!urlString.endsWith(finalizerPath)) {
/* 259 */         throw new IOException("Unsupported path style: " + urlString);
/*     */       }
/* 261 */       urlString = urlString.substring(0, urlString.length() - finalizerPath.length());
/* 262 */       return new URL(finalizerUrl, urlString);
/*     */     }
/*     */ 
/*     */     URLClassLoader newLoader(URL base)
/*     */     {
/* 270 */       return new URLClassLoader(new URL[] { base }, null);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class SystemLoader
/*     */     implements FinalizableReferenceQueue.FinalizerLoader
/*     */   {
/*     */ 
/*     */     @VisibleForTesting
/*     */     static boolean disabled;
/*     */ 
/*     */     public Class<?> loadFinalizer()
/*     */     {
/* 191 */       if (disabled)
/* 192 */         return null;
/*     */       ClassLoader systemLoader;
/*     */       try
/*     */       {
/* 196 */         systemLoader = ClassLoader.getSystemClassLoader();
/*     */       } catch (SecurityException e) {
/* 198 */         FinalizableReferenceQueue.logger.info("Not allowed to access system class loader.");
/* 199 */         return null;
/*     */       }
/* 201 */       if (systemLoader != null) {
/*     */         try {
/* 203 */           return systemLoader.loadClass("com.google.common.base.internal.Finalizer");
/*     */         }
/*     */         catch (ClassNotFoundException e) {
/* 206 */           return null;
/*     */         }
/*     */       }
/* 209 */       return null;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface FinalizerLoader
/*     */   {
/*     */     public abstract Class<?> loadFinalizer();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.FinalizableReferenceQueue
 * JD-Core Version:    0.6.2
 */