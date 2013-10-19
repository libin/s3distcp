/*     */ package org.apache.log4j.config;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public class PropertyGetter
/*     */ {
/*  38 */   protected static final Object[] NULL_ARG = new Object[0];
/*     */   protected Object obj;
/*     */   protected PropertyDescriptor[] props;
/*     */ 
/*     */   public PropertyGetter(Object obj)
/*     */     throws IntrospectionException
/*     */   {
/*  55 */     BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
/*  56 */     this.props = bi.getPropertyDescriptors();
/*  57 */     this.obj = obj;
/*     */   }
/*     */ 
/*     */   public static void getProperties(Object obj, PropertyCallback callback, String prefix)
/*     */   {
/*     */     try
/*     */     {
/*  64 */       new PropertyGetter(obj).getProperties(callback, prefix);
/*     */     } catch (IntrospectionException ex) {
/*  66 */       LogLog.error("Failed to introspect object " + obj, ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void getProperties(PropertyCallback callback, String prefix)
/*     */   {
/*  72 */     for (int i = 0; i < this.props.length; i++) {
/*  73 */       Method getter = this.props[i].getReadMethod();
/*  74 */       if ((getter != null) && 
/*  75 */         (isHandledType(getter.getReturnType())))
/*     */       {
/*  79 */         String name = this.props[i].getName();
/*     */         try {
/*  81 */           Object result = getter.invoke(this.obj, NULL_ARG);
/*     */ 
/*  83 */           if (result != null)
/*  84 */             callback.foundProperty(this.obj, prefix, name, result);
/*     */         }
/*     */         catch (IllegalAccessException ex) {
/*  87 */           LogLog.warn("Failed to get value of property " + name);
/*     */         } catch (InvocationTargetException ex) {
/*  89 */           if (((ex.getTargetException() instanceof InterruptedException)) || ((ex.getTargetException() instanceof InterruptedIOException)))
/*     */           {
/*  91 */             Thread.currentThread().interrupt();
/*     */           }
/*  93 */           LogLog.warn("Failed to get value of property " + name);
/*     */         } catch (RuntimeException ex) {
/*  95 */           LogLog.warn("Failed to get value of property " + name);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean isHandledType(Class type) {
/* 102 */     return (String.class.isAssignableFrom(type)) || (Integer.TYPE.isAssignableFrom(type)) || (Long.TYPE.isAssignableFrom(type)) || (Boolean.TYPE.isAssignableFrom(type)) || (Priority.class.isAssignableFrom(type));
/*     */   }
/*     */ 
/*     */   public static abstract interface PropertyCallback
/*     */   {
/*     */     public abstract void foundProperty(Object paramObject1, String paramString1, String paramString2, Object paramObject2);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.config.PropertyGetter
 * JD-Core Version:    0.6.2
 */