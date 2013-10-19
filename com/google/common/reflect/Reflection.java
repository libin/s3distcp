/*    */ package com.google.common.reflect;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Proxy;
/*    */ 
/*    */ @Beta
/*    */ public final class Reflection
/*    */ {
/*    */   public static String getPackageName(Class<?> cls)
/*    */   {
/* 41 */     return getPackageName(cls.getName());
/*    */   }
/*    */ 
/*    */   public static String getPackageName(String classFullName)
/*    */   {
/* 50 */     int lastDot = classFullName.lastIndexOf('.');
/* 51 */     if (lastDot < 0) {
/* 52 */       return "";
/*    */     }
/* 54 */     return classFullName.substring(0, lastDot);
/*    */   }
/*    */ 
/*    */   public static void initialize(Class<?>[] classes)
/*    */   {
/* 71 */     for (Class clazz : classes)
/*    */       try {
/* 73 */         Class.forName(clazz.getName(), true, clazz.getClassLoader());
/*    */       } catch (ClassNotFoundException e) {
/* 75 */         throw new AssertionError(e);
/*    */       }
/*    */   }
/*    */ 
/*    */   public static <T> T newProxy(Class<T> interfaceType, InvocationHandler handler)
/*    */   {
/* 92 */     Preconditions.checkNotNull(interfaceType);
/* 93 */     Preconditions.checkNotNull(handler);
/* 94 */     Preconditions.checkArgument(interfaceType.isInterface());
/* 95 */     Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[] { interfaceType }, handler);
/*    */ 
/* 99 */     return interfaceType.cast(object);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.reflect.Reflection
 * JD-Core Version:    0.6.2
 */