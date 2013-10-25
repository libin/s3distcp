/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import java.util.concurrent.Callable;
/*    */ import java.util.concurrent.Executor;
/*    */ import java.util.concurrent.FutureTask;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ public final class ListenableFutureTask<V> extends FutureTask<V>
/*    */   implements ListenableFuture<V>
/*    */ {
/* 40 */   private final ExecutionList executionList = new ExecutionList();
/*    */ 
/*    */   public static <V> ListenableFutureTask<V> create(Callable<V> callable)
/*    */   {
/* 50 */     return new ListenableFutureTask(callable);
/*    */   }
/*    */ 
/*    */   public static <V> ListenableFutureTask<V> create(Runnable runnable, @Nullable V result)
/*    */   {
/* 67 */     return new ListenableFutureTask(runnable, result);
/*    */   }
/*    */ 
/*    */   private ListenableFutureTask(Callable<V> callable) {
/* 71 */     super(callable);
/*    */   }
/*    */ 
/*    */   private ListenableFutureTask(Runnable runnable, @Nullable V result) {
/* 75 */     super(runnable, result);
/*    */   }
/*    */ 
/*    */   public void addListener(Runnable listener, Executor exec)
/*    */   {
/* 80 */     this.executionList.add(listener, exec);
/*    */   }
/*    */ 
/*    */   protected void done()
/*    */   {
/* 88 */     this.executionList.execute();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ListenableFutureTask
 * JD-Core Version:    0.6.2
 */