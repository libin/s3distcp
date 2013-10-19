/*    */ package com.google.common.base;
/*    */ 
/*    */ import java.lang.ref.SoftReference;
/*    */ 
/*    */ public abstract class FinalizableSoftReference<T> extends SoftReference<T>
/*    */   implements FinalizableReference
/*    */ {
/*    */   protected FinalizableSoftReference(T referent, FinalizableReferenceQueue queue)
/*    */   {
/* 39 */     super(referent, queue.queue);
/* 40 */     queue.cleanUp();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.FinalizableSoftReference
 * JD-Core Version:    0.6.2
 */