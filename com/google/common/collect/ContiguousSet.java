/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ContiguousSet<C extends Comparable> extends ImmutableSortedSet<C>
/*     */ {
/*     */   final DiscreteDomain<C> domain;
/*     */ 
/*     */   ContiguousSet(DiscreteDomain<C> domain)
/*     */   {
/*  39 */     super(Ordering.natural());
/*  40 */     this.domain = domain;
/*     */   }
/*     */ 
/*     */   public ContiguousSet<C> headSet(C toElement) {
/*  44 */     return headSetImpl((Comparable)Preconditions.checkNotNull(toElement), false);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ContiguousSet<C> headSet(C toElement, boolean inclusive)
/*     */   {
/*  52 */     return headSetImpl((Comparable)Preconditions.checkNotNull(toElement), inclusive);
/*     */   }
/*     */ 
/*     */   public ContiguousSet<C> subSet(C fromElement, C toElement) {
/*  56 */     Preconditions.checkNotNull(fromElement);
/*  57 */     Preconditions.checkNotNull(toElement);
/*  58 */     Preconditions.checkArgument(comparator().compare(fromElement, toElement) <= 0);
/*  59 */     return subSetImpl(fromElement, true, toElement, false);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ContiguousSet<C> subSet(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive)
/*     */   {
/*  68 */     Preconditions.checkNotNull(fromElement);
/*  69 */     Preconditions.checkNotNull(toElement);
/*  70 */     Preconditions.checkArgument(comparator().compare(fromElement, toElement) <= 0);
/*  71 */     return subSetImpl(fromElement, fromInclusive, toElement, toInclusive);
/*     */   }
/*     */ 
/*     */   public ContiguousSet<C> tailSet(C fromElement) {
/*  75 */     return tailSetImpl((Comparable)Preconditions.checkNotNull(fromElement), true);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ContiguousSet<C> tailSet(C fromElement, boolean inclusive)
/*     */   {
/*  83 */     return tailSetImpl((Comparable)Preconditions.checkNotNull(fromElement), inclusive);
/*     */   }
/*     */ 
/*     */   abstract ContiguousSet<C> headSetImpl(C paramC, boolean paramBoolean);
/*     */ 
/*     */   abstract ContiguousSet<C> subSetImpl(C paramC1, boolean paramBoolean1, C paramC2, boolean paramBoolean2);
/*     */ 
/*     */   abstract ContiguousSet<C> tailSetImpl(C paramC, boolean paramBoolean);
/*     */ 
/*     */   public abstract ContiguousSet<C> intersection(ContiguousSet<C> paramContiguousSet);
/*     */ 
/*     */   public abstract Range<C> range();
/*     */ 
/*     */   public abstract Range<C> range(BoundType paramBoundType1, BoundType paramBoundType2);
/*     */ 
/*     */   public String toString()
/*     */   {
/* 127 */     return range().toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ContiguousSet
 * JD-Core Version:    0.6.2
 */