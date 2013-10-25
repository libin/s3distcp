/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.io.Serializable;
/*    */ import java.util.Collections;
/*    */ import java.util.List;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class NaturalOrdering extends Ordering<Comparable>
/*    */   implements Serializable
/*    */ {
/* 32 */   static final NaturalOrdering INSTANCE = new NaturalOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public int compare(Comparable left, Comparable right)
/*    */   {
/* 35 */     Preconditions.checkNotNull(left);
/* 36 */     Preconditions.checkNotNull(right);
/* 37 */     if (left == right) {
/* 38 */       return 0;
/*    */     }
/*    */ 
/* 41 */     return left.compareTo(right);
/*    */   }
/*    */ 
/*    */   public <S extends Comparable> Ordering<S> reverse() {
/* 45 */     return ReverseNaturalOrdering.INSTANCE;
/*    */   }
/*    */ 
/*    */   public int binarySearch(List<? extends Comparable> sortedList, Comparable key)
/*    */   {
/* 51 */     return Collections.binarySearch(sortedList, key);
/*    */   }
/*    */ 
/*    */   public <E extends Comparable> List<E> sortedCopy(Iterable<E> iterable)
/*    */   {
/* 57 */     List list = Lists.newArrayList(iterable);
/* 58 */     Collections.sort(list);
/* 59 */     return list;
/*    */   }
/*    */ 
/*    */   private Object readResolve()
/*    */   {
/* 64 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 68 */     return "Ordering.natural()";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.NaturalOrdering
 * JD-Core Version:    0.6.2
 */