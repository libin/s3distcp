/*    */ package com.google.common.base;
/*    */ 
/*    */ import java.lang.ref.PhantomReference;
/*    */ 
/*    */ public abstract class FinalizablePhantomReference<T> extends PhantomReference<T>
/*    */   implements FinalizableReference
/*    */ {
/*    */   protected FinalizablePhantomReference(T referent, FinalizableReferenceQueue queue)
/*    */   {
/* 41 */     super(referent, queue.queue);
/* 42 */     queue.cleanUp();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.FinalizablePhantomReference
 * JD-Core Version:    0.6.2
 */