/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public final class SettableFuture<V> extends AbstractFuture<V>
/*    */ {
/*    */   public static <V> SettableFuture<V> create()
/*    */   {
/* 34 */     return new SettableFuture();
/*    */   }
/*    */ 
/*    */   public boolean set(@Nullable V value)
/*    */   {
/* 53 */     return super.set(value);
/*    */   }
/*    */ 
/*    */   public boolean setException(Throwable throwable)
/*    */   {
/* 68 */     return super.setException(throwable);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.SettableFuture
 * JD-Core Version:    0.6.2
 */