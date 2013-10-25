/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.SortedSet;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class AbstractSortedMultiset<E> extends AbstractMultiset<E>
/*     */   implements SortedMultiset<E>
/*     */ {
/*     */ 
/*     */   @GwtTransient
/*     */   final Comparator<? super E> comparator;
/*     */   private transient SortedMultiset<E> descendingMultiset;
/*     */ 
/*     */   AbstractSortedMultiset()
/*     */   {
/*  41 */     this(Ordering.natural());
/*     */   }
/*     */ 
/*     */   AbstractSortedMultiset(Comparator<? super E> comparator) {
/*  45 */     this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */   }
/*     */ 
/*     */   public SortedSet<E> elementSet()
/*     */   {
/*  50 */     return (SortedSet)super.elementSet();
/*     */   }
/*     */ 
/*     */   SortedSet<E> createElementSet()
/*     */   {
/*  55 */     return new SortedMultisets.ElementSet()
/*     */     {
/*     */       SortedMultiset<E> multiset() {
/*  58 */         return AbstractSortedMultiset.this;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/*  65 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> firstEntry()
/*     */   {
/*  70 */     Iterator entryIterator = entryIterator();
/*  71 */     return entryIterator.hasNext() ? (Multiset.Entry)entryIterator.next() : null;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> lastEntry()
/*     */   {
/*  76 */     Iterator entryIterator = descendingEntryIterator();
/*  77 */     return entryIterator.hasNext() ? (Multiset.Entry)entryIterator.next() : null;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> pollFirstEntry()
/*     */   {
/*  82 */     Iterator entryIterator = entryIterator();
/*  83 */     if (entryIterator.hasNext()) {
/*  84 */       Multiset.Entry result = (Multiset.Entry)entryIterator.next();
/*  85 */       result = Multisets.immutableEntry(result.getElement(), result.getCount());
/*  86 */       entryIterator.remove();
/*  87 */       return result;
/*     */     }
/*  89 */     return null;
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> pollLastEntry()
/*     */   {
/*  94 */     Iterator entryIterator = descendingEntryIterator();
/*  95 */     if (entryIterator.hasNext()) {
/*  96 */       Multiset.Entry result = (Multiset.Entry)entryIterator.next();
/*  97 */       result = Multisets.immutableEntry(result.getElement(), result.getCount());
/*  98 */       entryIterator.remove();
/*  99 */       return result;
/*     */     }
/* 101 */     return null;
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> subMultiset(E fromElement, BoundType fromBoundType, E toElement, BoundType toBoundType)
/*     */   {
/* 107 */     return tailMultiset(fromElement, fromBoundType).headMultiset(toElement, toBoundType);
/*     */   }
/*     */ 
/*     */   abstract Iterator<Multiset.Entry<E>> descendingEntryIterator();
/*     */ 
/*     */   Iterator<E> descendingIterator() {
/* 113 */     return Multisets.iteratorImpl(descendingMultiset());
/*     */   }
/*     */ 
/*     */   public SortedMultiset<E> descendingMultiset()
/*     */   {
/* 120 */     SortedMultiset result = this.descendingMultiset;
/* 121 */     return result == null ? (this.descendingMultiset = createDescendingMultiset()) : result;
/*     */   }
/*     */ 
/*     */   SortedMultiset<E> createDescendingMultiset() {
/* 125 */     return new SortedMultisets.DescendingMultiset()
/*     */     {
/*     */       SortedMultiset<E> forwardMultiset() {
/* 128 */         return AbstractSortedMultiset.this;
/*     */       }
/*     */ 
/*     */       Iterator<Multiset.Entry<E>> entryIterator()
/*     */       {
/* 133 */         return AbstractSortedMultiset.this.descendingEntryIterator();
/*     */       }
/*     */ 
/*     */       public Iterator<E> iterator()
/*     */       {
/* 138 */         return AbstractSortedMultiset.this.descendingIterator();
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractSortedMultiset
 * JD-Core Version:    0.6.2
 */