/*     */ package com.google.common.collect;
/*     */ 
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ final class DescendingImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E>
/*     */ {
/*     */   private final transient ImmutableSortedMultiset<E> forward;
/*     */ 
/*     */   DescendingImmutableSortedMultiset(ImmutableSortedMultiset<E> forward)
/*     */   {
/*  29 */     super(forward.reverseComparator());
/*  30 */     this.forward = forward;
/*     */   }
/*     */ 
/*     */   public int count(@Nullable Object element)
/*     */   {
/*  35 */     return this.forward.count(element);
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> firstEntry()
/*     */   {
/*  40 */     return this.forward.lastEntry();
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> lastEntry()
/*     */   {
/*  45 */     return this.forward.firstEntry();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  50 */     return this.forward.size();
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> createElementSet()
/*     */   {
/*  55 */     return this.forward.createDescendingElementSet();
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> createDescendingElementSet()
/*     */   {
/*  60 */     return this.forward.elementSet();
/*     */   }
/*     */ 
/*     */   ImmutableSet<Multiset.Entry<E>> createEntrySet()
/*     */   {
/*  65 */     final ImmutableSet forwardEntrySet = this.forward.entrySet();
/*  66 */     return new ImmutableMultiset.EntrySet(forwardEntrySet)
/*     */     {
/*     */       public int size() {
/*  69 */         return forwardEntrySet.size();
/*     */       }
/*     */ 
/*     */       public UnmodifiableIterator<Multiset.Entry<E>> iterator()
/*     */       {
/*  74 */         return asList().iterator();
/*     */       }
/*     */ 
/*     */       ImmutableList<Multiset.Entry<E>> createAsList()
/*     */       {
/*  79 */         return forwardEntrySet.asList().reverse();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMultiset<E> descendingMultiset()
/*     */   {
/*  86 */     return this.forward;
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */   {
/*  91 */     return this.forward.tailMultiset(upperBound, boundType).descendingMultiset();
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */   {
/*  96 */     return this.forward.headMultiset(lowerBound, boundType).descendingMultiset();
/*     */   }
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/* 101 */     return this.forward.isPartialView();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.DescendingImmutableSortedMultiset
 * JD-Core Version:    0.6.2
 */