/*    */ package com.google.common.base;
/*    */ 
/*    */ import java.lang.ref.WeakReference;
/*    */ 
/*    */ public abstract class FinalizableWeakReference<T> extends WeakReference<T>
/*    */   implements FinalizableReference
/*    */ {
/*    */   protected FinalizableWeakReference(T referent, FinalizableReferenceQueue queue)
/*    */   {
/* 39 */     super(referent, queue.queue);
/* 40 */     queue.cleanUp();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.FinalizableWeakReference
 * JD-Core Version:    0.6.2
 */