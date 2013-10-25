/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingObject
/*    */ {
/*    */   protected abstract Object delegate();
/*    */ 
/*    */   public String toString()
/*    */   {
/* 72 */     return delegate().toString();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingObject
 * JD-Core Version:    0.6.2
 */