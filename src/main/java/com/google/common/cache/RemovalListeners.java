/*    */ package com.google.common.cache;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.util.concurrent.Executor;
/*    */ 
/*    */ @Beta
/*    */ public final class RemovalListeners
/*    */ {
/*    */   public static <K, V> RemovalListener<K, V> asynchronous(final RemovalListener<K, V> listener, Executor executor)
/*    */   {
/* 44 */     return new RemovalListener()
/*    */     {
/*    */       public void onRemoval(final RemovalNotification<K, V> notification) {
/* 47 */         this.val$executor.execute(new Runnable()
/*    */         {
/*    */           public void run() {
/* 50 */             RemovalListeners.1.this.val$listener.onRemoval(notification);
/*    */           }
/*    */         });
/*    */       }
/*    */     };
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.RemovalListeners
 * JD-Core Version:    0.6.2
 */