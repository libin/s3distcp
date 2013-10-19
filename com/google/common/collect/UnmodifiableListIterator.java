/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.ListIterator;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class UnmodifiableListIterator<E> extends UnmodifiableIterator<E>
/*    */   implements ListIterator<E>
/*    */ {
/*    */   public final void add(E e)
/*    */   {
/* 42 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   public final void set(E e)
/*    */   {
/* 51 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.UnmodifiableListIterator
 * JD-Core Version:    0.6.2
 */