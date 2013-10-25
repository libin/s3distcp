/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.security.CodeSource;
/*     */ import java.security.ProtectionDomain;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.log4j.spi.ThrowableRenderer;
/*     */ 
/*     */ public final class EnhancedThrowableRenderer
/*     */   implements ThrowableRenderer
/*     */ {
/*     */   private Method getStackTraceMethod;
/*     */   private Method getClassNameMethod;
/*     */ 
/*     */   public EnhancedThrowableRenderer()
/*     */   {
/*     */     try
/*     */     {
/*  51 */       Class[] noArgs = null;
/*  52 */       this.getStackTraceMethod = Throwable.class.getMethod("getStackTrace", noArgs);
/*  53 */       Class ste = Class.forName("java.lang.StackTraceElement");
/*  54 */       this.getClassNameMethod = ste.getMethod("getClassName", noArgs);
/*     */     }
/*     */     catch (Exception ex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] doRender(Throwable throwable)
/*     */   {
/*  63 */     if (this.getStackTraceMethod != null)
/*     */       try {
/*  65 */         Object[] noArgs = null;
/*  66 */         Object[] elements = (Object[])this.getStackTraceMethod.invoke(throwable, noArgs);
/*  67 */         String[] lines = new String[elements.length + 1];
/*  68 */         lines[0] = throwable.toString();
/*  69 */         Map classMap = new HashMap();
/*  70 */         for (int i = 0; i < elements.length; i++) {
/*  71 */           lines[(i + 1)] = formatElement(elements[i], classMap);
/*     */         }
/*  73 */         return lines;
/*     */       }
/*     */       catch (Exception ex) {
/*     */       }
/*  77 */     return DefaultThrowableRenderer.render(throwable);
/*     */   }
/*     */ 
/*     */   private String formatElement(Object element, Map classMap)
/*     */   {
/*  87 */     StringBuffer buf = new StringBuffer("\tat ");
/*  88 */     buf.append(element);
/*     */     try {
/*  90 */       String className = this.getClassNameMethod.invoke(element, (Object[])null).toString();
/*  91 */       Object classDetails = classMap.get(className);
/*  92 */       if (classDetails != null) {
/*  93 */         buf.append(classDetails);
/*     */       } else {
/*  95 */         Class cls = findClass(className);
/*  96 */         int detailStart = buf.length();
/*  97 */         buf.append('[');
/*     */         try {
/*  99 */           CodeSource source = cls.getProtectionDomain().getCodeSource();
/* 100 */           if (source != null) {
/* 101 */             URL locationURL = source.getLocation();
/* 102 */             if (locationURL != null)
/*     */             {
/* 106 */               if ("file".equals(locationURL.getProtocol())) {
/* 107 */                 String path = locationURL.getPath();
/* 108 */                 if (path != null)
/*     */                 {
/* 112 */                   int lastSlash = path.lastIndexOf('/');
/* 113 */                   int lastBack = path.lastIndexOf(File.separatorChar);
/* 114 */                   if (lastBack > lastSlash) {
/* 115 */                     lastSlash = lastBack;
/*     */                   }
/*     */ 
/* 121 */                   if ((lastSlash <= 0) || (lastSlash == path.length() - 1))
/* 122 */                     buf.append(locationURL);
/*     */                   else
/* 124 */                     buf.append(path.substring(lastSlash + 1));
/*     */                 }
/*     */               }
/*     */               else {
/* 128 */                 buf.append(locationURL);
/*     */               }
/*     */             }
/*     */           }
/*     */         } catch (SecurityException ex) {
/*     */         }
/* 134 */         buf.append(':');
/* 135 */         Package pkg = cls.getPackage();
/* 136 */         if (pkg != null) {
/* 137 */           String implVersion = pkg.getImplementationVersion();
/* 138 */           if (implVersion != null) {
/* 139 */             buf.append(implVersion);
/*     */           }
/*     */         }
/* 142 */         buf.append(']');
/* 143 */         classMap.put(className, buf.substring(detailStart));
/*     */       }
/*     */     } catch (Exception ex) {
/*     */     }
/* 147 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   private Class findClass(String className)
/*     */     throws ClassNotFoundException
/*     */   {
/*     */     try
/*     */     {
/* 158 */       return Thread.currentThread().getContextClassLoader().loadClass(className);
/*     */     } catch (ClassNotFoundException e) {
/*     */       try {
/* 161 */         return Class.forName(className); } catch (ClassNotFoundException e1) {  }
/*     */     }
/* 163 */     return getClass().getClassLoader().loadClass(className);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.EnhancedThrowableRenderer
 * JD-Core Version:    0.6.2
 */