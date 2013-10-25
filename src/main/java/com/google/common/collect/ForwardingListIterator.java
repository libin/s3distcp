/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingListIterator<E> extends ForwardingIterator<E>
/*    */   implements ListIterator<E>
/*    */ {
/*    */   protected abstract ListIterator<E> delegate();
/*    */ 
/*    */   public void add(E element)
/*    */   {
/* 43 */     delegate().add(element);
/*    */   }
/*    */ 
/*    */   public boolean hasPrevious()
/*    */   {
/* 48 */     return delegate().hasPrevious();
/*    */   }
/*    */ 
/*    */   public int nextIndex()
/*    */   {
/* 53 */     return delegate().nextIndex();
/*    */   }
/*    */ 
/*    */   public E previous()
/*    */   {
/* 58 */     return delegate().previous();
/*    */   }
/*    */ 
/*    */   public int previousIndex()
/*    */   {
/* 63 */     return delegate().previousIndex();
/*    */   }
/*    */ 
/*    */   public void set(E element)
/*    */   {
/* 68 */     delegate().set(element);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingListIterator
 * JD-Core Version:    0.6.2
 */