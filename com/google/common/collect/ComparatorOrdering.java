/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.io.Serializable;
/*    */ import java.util.Collections;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class ComparatorOrdering<T> extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final Comparator<T> comparator;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   ComparatorOrdering(Comparator<T> comparator)
/*    */   {
/* 36 */     this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*    */   }
/*    */ 
/*    */   public int compare(T a, T b) {
/* 40 */     return this.comparator.compare(a, b);
/*    */   }
/*    */ 
/*    */   public int binarySearch(List<? extends T> sortedList, T key)
/*    */   {
/* 45 */     return Collections.binarySearch(sortedList, key, this.comparator);
/*    */   }
/*    */ 
/*    */   public <E extends T> List<E> sortedCopy(Iterable<E> iterable)
/*    */   {
/* 50 */     List list = Lists.newArrayList(iterable);
/* 51 */     Collections.sort(list, this.comparator);
/* 52 */     return list;
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 56 */     if (object == this) {
/* 57 */       return true;
/*    */     }
/* 59 */     if ((object instanceof ComparatorOrdering)) {
/* 60 */       ComparatorOrdering that = (ComparatorOrdering)object;
/* 61 */       return this.comparator.equals(that.comparator);
/*    */     }
/* 63 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 67 */     return this.comparator.hashCode();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 71 */     return this.comparator.toString();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ComparatorOrdering
 * JD-Core Version:    0.6.2
 */