/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ abstract class TransformedImmutableList<D, E> extends ImmutableList<E>
/*    */ {
/*    */   private final transient ImmutableList<D> backingList;
/*    */ 
/*    */   TransformedImmutableList(ImmutableList<D> backingList)
/*    */   {
/* 49 */     this.backingList = ((ImmutableList)Preconditions.checkNotNull(backingList));
/*    */   }
/*    */ 
/*    */   abstract E transform(D paramD);
/*    */ 
/*    */   public E get(int index) {
/* 55 */     return transform(this.backingList.get(index));
/*    */   }
/*    */ 
/*    */   public int size() {
/* 59 */     return this.backingList.size();
/*    */   }
/*    */ 
/*    */   public ImmutableList<E> subList(int fromIndex, int toIndex) {
/* 63 */     return new TransformedView(this.backingList.subList(fromIndex, toIndex));
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object obj) {
/* 67 */     if (obj == this) {
/* 68 */       return true;
/*    */     }
/* 70 */     if ((obj instanceof List)) {
/* 71 */       List list = (List)obj;
/* 72 */       return (size() == list.size()) && (Iterators.elementsEqual(iterator(), list.iterator()));
/*    */     }
/*    */ 
/* 75 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 79 */     return Lists.hashCodeImpl(this);
/*    */   }
/*    */ 
/*    */   boolean isPartialView() {
/* 83 */     return true;
/*    */   }
/*    */ 
/*    */   private class TransformedView extends TransformedImmutableList<D, E>
/*    */   {
/*    */     TransformedView()
/*    */     {
/* 38 */       super();
/*    */     }
/*    */ 
/*    */     E transform(D d) {
/* 42 */       return TransformedImmutableList.this.transform(d);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.TransformedImmutableList
 * JD-Core Version:    0.6.2
 */