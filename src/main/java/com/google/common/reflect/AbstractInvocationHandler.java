/*    */ package com.google.common.reflect;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.lang.reflect.InvocationHandler;
/*    */ import java.lang.reflect.Method;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public abstract class AbstractInvocationHandler
/*    */   implements InvocationHandler
/*    */ {
/* 36 */   private static final Object[] NO_ARGS = new Object[0];
/*    */ 
/*    */   public final Object invoke(Object proxy, Method method, @Nullable Object[] args)
/*    */     throws Throwable
/*    */   {
/* 47 */     if (args == null) {
/* 48 */       args = NO_ARGS;
/*    */     }
/* 50 */     if ((args.length == 0) && (method.getName().equals("hashCode"))) {
/* 51 */       return Integer.valueOf(System.identityHashCode(proxy));
/*    */     }
/* 53 */     if ((args.length == 1) && (method.getName().equals("equals")) && (method.getParameterTypes()[0] == Object.class))
/*    */     {
/* 56 */       return Boolean.valueOf(proxy == args[0]);
/*    */     }
/* 58 */     if ((args.length == 0) && (method.getName().equals("toString"))) {
/* 59 */       return toString();
/*    */     }
/* 61 */     return handleInvocation(proxy, method, args);
/*    */   }
/*    */ 
/*    */   protected abstract Object handleInvocation(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
/*    */     throws Throwable;
/*    */ 
/*    */   public String toString()
/*    */   {
/* 80 */     return super.toString();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.reflect.AbstractInvocationHandler
 * JD-Core Version:    0.6.2
 */