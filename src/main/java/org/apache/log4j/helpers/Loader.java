/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.io.InterruptedIOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ 
/*     */ public class Loader
/*     */ {
/*     */   static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
/*  37 */   private static boolean java1 = true;
/*     */ 
/*  39 */   private static boolean ignoreTCL = false;
/*     */ 
/*     */   /** @deprecated */
/*     */   public static URL getResource(String resource, Class clazz)
/*     */   {
/*  65 */     return getResource(resource);
/*     */   }
/*     */ 
/*     */   public static URL getResource(String resource)
/*     */   {
/*  88 */     ClassLoader classLoader = null;
/*  89 */     URL url = null;
/*     */     try
/*     */     {
/*  92 */       if ((!java1) && (!ignoreTCL)) {
/*  93 */         classLoader = getTCL();
/*  94 */         if (classLoader != null) {
/*  95 */           LogLog.debug("Trying to find [" + resource + "] using context classloader " + classLoader + ".");
/*     */ 
/*  97 */           url = classLoader.getResource(resource);
/*  98 */           if (url != null) {
/*  99 */             return url;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 106 */       classLoader = Loader.class.getClassLoader();
/* 107 */       if (classLoader != null) {
/* 108 */         LogLog.debug("Trying to find [" + resource + "] using " + classLoader + " class loader.");
/*     */ 
/* 110 */         url = classLoader.getResource(resource);
/* 111 */         if (url != null)
/* 112 */           return url;
/*     */       }
/*     */     }
/*     */     catch (IllegalAccessException t) {
/* 116 */       LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
/*     */     } catch (InvocationTargetException t) {
/* 118 */       if (((t.getTargetException() instanceof InterruptedException)) || ((t.getTargetException() instanceof InterruptedIOException)))
/*     */       {
/* 120 */         Thread.currentThread().interrupt();
/*     */       }
/* 122 */       LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
/*     */     }
/*     */     catch (Throwable t)
/*     */     {
/* 127 */       LogLog.warn("Caught Exception while in Loader.getResource. This may be innocuous.", t);
/*     */     }
/*     */ 
/* 134 */     LogLog.debug("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
/*     */ 
/* 136 */     return ClassLoader.getSystemResource(resource);
/*     */   }
/*     */ 
/*     */   public static boolean isJava1()
/*     */   {
/* 145 */     return java1;
/*     */   }
/*     */ 
/*     */   private static ClassLoader getTCL()
/*     */     throws IllegalAccessException, InvocationTargetException
/*     */   {
/* 158 */     Method method = null;
/*     */     try {
/* 160 */       method = Thread.class.getMethod("getContextClassLoader", null);
/*     */     }
/*     */     catch (NoSuchMethodException e) {
/* 163 */       return null;
/*     */     }
/*     */ 
/* 166 */     return (ClassLoader)method.invoke(Thread.currentThread(), null);
/*     */   }
/*     */ 
/*     */   public static Class loadClass(String clazz)
/*     */     throws ClassNotFoundException
/*     */   {
/* 181 */     if ((java1) || (ignoreTCL))
/* 182 */       return Class.forName(clazz);
/*     */     try
/*     */     {
/* 185 */       return getTCL().loadClass(clazz);
/*     */     }
/*     */     catch (InvocationTargetException e)
/*     */     {
/* 191 */       if (((e.getTargetException() instanceof InterruptedException)) || ((e.getTargetException() instanceof InterruptedIOException)))
/*     */       {
/* 193 */         Thread.currentThread().interrupt();
/*     */       }
/*     */     }
/*     */     catch (Throwable t) {
/*     */     }
/* 198 */     return Class.forName(clazz);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  42 */     String prop = OptionConverter.getSystemProperty("java.version", null);
/*     */ 
/*  44 */     if (prop != null) {
/*  45 */       int i = prop.indexOf('.');
/*  46 */       if ((i != -1) && 
/*  47 */         (prop.charAt(i + 1) != '1')) {
/*  48 */         java1 = false;
/*     */       }
/*     */     }
/*  51 */     String ignoreTCLProp = OptionConverter.getSystemProperty("log4j.ignoreTCL", null);
/*  52 */     if (ignoreTCLProp != null)
/*  53 */       ignoreTCL = OptionConverter.toBoolean(ignoreTCLProp, true);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.Loader
 * JD-Core Version:    0.6.2
 */