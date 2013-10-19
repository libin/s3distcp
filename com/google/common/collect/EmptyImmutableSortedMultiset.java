/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.Comparator;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ final class EmptyImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E>
/*    */ {
/*    */   EmptyImmutableSortedMultiset(Comparator<? super E> comparator)
/*    */   {
/* 31 */     super(comparator);
/*    */   }
/*    */ 
/*    */   public Multiset.Entry<E> firstEntry()
/*    */   {
/* 36 */     return null;
/*    */   }
/*    */ 
/*    */   public Multiset.Entry<E> lastEntry()
/*    */   {
/* 41 */     return null;
/*    */   }
/*    */ 
/*    */   public int count(@Nullable Object element)
/*    */   {
/* 46 */     return 0;
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 51 */     return 0;
/*    */   }
/*    */ 
/*    */   ImmutableSortedSet<E> createElementSet()
/*    */   {
/* 56 */     return ImmutableSortedSet.emptySet(comparator());
/*    */   }
/*    */ 
/*    */   ImmutableSortedSet<E> createDescendingElementSet()
/*    */   {
/* 61 */     return ImmutableSortedSet.emptySet(reverseComparator());
/*    */   }
/*    */ 
/*    */   ImmutableSet<Multiset.Entry<E>> createEntrySet()
/*    */   {
/* 66 */     return ImmutableSet.of();
/*    */   }
/*    */ 
/*    */   public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*    */   {
/* 71 */     Preconditions.checkNotNull(upperBound);
/* 72 */     Preconditions.checkNotNull(boundType);
/* 73 */     return this;
/*    */   }
/*    */ 
/*    */   public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*    */   {
/* 78 */     Preconditions.checkNotNull(lowerBound);
/* 79 */     Preconditions.checkNotNull(boundType);
/* 80 */     return this;
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 85 */     return false;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableSortedMultiset
 * JD-Core Version:    0.6.2
 */