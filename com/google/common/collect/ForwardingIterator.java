/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingIterator<T> extends ForwardingObject
/*    */   implements Iterator<T>
/*    */ {
/*    */   protected abstract Iterator<T> delegate();
/*    */ 
/*    */   public boolean hasNext()
/*    */   {
/* 43 */     return delegate().hasNext();
/*    */   }
/*    */ 
/*    */   public T next()
/*    */   {
/* 48 */     return delegate().next();
/*    */   }
/*    */ 
/*    */   public void remove()
/*    */   {
/* 53 */     delegate().remove();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingIterator
 * JD-Core Version:    0.6.2
 */