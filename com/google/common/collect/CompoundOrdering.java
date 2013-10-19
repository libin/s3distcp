/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.Comparator;
/*    */ import java.util.List;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class CompoundOrdering<T> extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final ImmutableList<Comparator<? super T>> comparators;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   CompoundOrdering(Comparator<? super T> primary, Comparator<? super T> secondary)
/*    */   {
/* 32 */     this.comparators = ImmutableList.of(primary, secondary);
/*    */   }
/*    */ 
/*    */   CompoundOrdering(Iterable<? extends Comparator<? super T>> comparators)
/*    */   {
/* 37 */     this.comparators = ImmutableList.copyOf(comparators);
/*    */   }
/*    */ 
/*    */   CompoundOrdering(List<? extends Comparator<? super T>> comparators, Comparator<? super T> lastComparator)
/*    */   {
/* 42 */     this.comparators = new ImmutableList.Builder().addAll(comparators).add(lastComparator).build();
/*    */   }
/*    */ 
/*    */   public int compare(T left, T right)
/*    */   {
/* 47 */     for (Comparator comparator : this.comparators) {
/* 48 */       int result = comparator.compare(left, right);
/* 49 */       if (result != 0) {
/* 50 */         return result;
/*    */       }
/*    */     }
/* 53 */     return 0;
/*    */   }
/*    */ 
/*    */   public boolean equals(Object object) {
/* 57 */     if (object == this) {
/* 58 */       return true;
/*    */     }
/* 60 */     if ((object instanceof CompoundOrdering)) {
/* 61 */       CompoundOrdering that = (CompoundOrdering)object;
/* 62 */       return this.comparators.equals(that.comparators);
/*    */     }
/* 64 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 68 */     return this.comparators.hashCode();
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 72 */     return "Ordering.compound(" + this.comparators + ")";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.CompoundOrdering
 * JD-Core Version:    0.6.2
 */