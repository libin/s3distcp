/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public final class Callables
/*    */ {
/*    */   public static <T> Callable<T> returning(@Nullable T value)
/*    */   {
/* 37 */     return new Callable() {
/*    */       public T call() {
/* 39 */         return this.val$value;
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.Callables
 * JD-Core Version:    0.6.2
 */