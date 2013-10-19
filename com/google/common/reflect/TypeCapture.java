/*    */ package com.google.common.reflect;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
/*    */ 
/*    */ abstract class TypeCapture<T>
/*    */ {
/*    */   final Type capture()
/*    */   {
/* 33 */     Type superclass = getClass().getGenericSuperclass();
/* 34 */     Preconditions.checkArgument(superclass instanceof ParameterizedType, "%s isn't parameterized", new Object[] { superclass });
/*    */ 
/* 36 */     return ((ParameterizedType)superclass).getActualTypeArguments()[0];
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.reflect.TypeCapture
 * JD-Core Version:    0.6.2
 */