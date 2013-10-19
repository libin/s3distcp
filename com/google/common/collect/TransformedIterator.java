/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.Iterator;
/*    */ 
/*    */ @GwtCompatible
/*    */ abstract class TransformedIterator<F, T>
/*    */   implements Iterator<T>
/*    */ {
/*    */   final Iterator<? extends F> backingIterator;
/*    */ 
/*    */   TransformedIterator(Iterator<? extends F> backingIterator)
/*    */   {
/* 36 */     this.backingIterator = ((Iterator)Preconditions.checkNotNull(backingIterator));
/*    */   }
/*    */ 
/*    */   abstract T transform(F paramF);
/*    */ 
/*    */   public final boolean hasNext()
/*    */   {
/* 43 */     return this.backingIterator.hasNext();
/*    */   }
/*    */ 
/*    */   public final T next()
/*    */   {
/* 48 */     return transform(this.backingIterator.next());
/*    */   }
/*    */ 
/*    */   public final void remove()
/*    */   {
/* 53 */     this.backingIterator.remove();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.TransformedIterator
 * JD-Core Version:    0.6.2
 */