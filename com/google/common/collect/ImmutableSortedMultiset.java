/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ 
/*     */ @Beta
/*     */ @GwtIncompatible("hasn't been tested yet")
/*     */ public abstract class ImmutableSortedMultiset<E> extends ImmutableSortedMultisetFauxverideShim<E>
/*     */   implements SortedMultiset<E>
/*     */ {
/*  85 */   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
/*     */ 
/*  87 */   private static final ImmutableSortedMultiset<Comparable> NATURAL_EMPTY_MULTISET = new EmptyImmutableSortedMultiset(NATURAL_ORDER);
/*     */   private final transient Comparator<? super E> comparator;
/*     */   private transient Comparator<? super E> reverseComparator;
/*     */   private transient ImmutableSortedSet<E> elementSet;
/*     */   transient ImmutableSortedMultiset<E> descendingMultiset;
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> of()
/*     */   {
/*  95 */     return NATURAL_EMPTY_MULTISET;
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E element)
/*     */   {
/* 102 */     return RegularImmutableSortedMultiset.createFromSorted(NATURAL_ORDER, ImmutableList.of(Multisets.immutableEntry(Preconditions.checkNotNull(element), 1)));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2)
/*     */   {
/* 114 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3)
/*     */   {
/* 125 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 137 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3, e4 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 149 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3, e4, e5 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] remaining)
/*     */   {
/* 161 */     int size = remaining.length + 6;
/* 162 */     List all = Lists.newArrayListWithCapacity(size);
/* 163 */     Collections.addAll(all, new Comparable[] { e1, e2, e3, e4, e5, e6 });
/* 164 */     Collections.addAll(all, remaining);
/* 165 */     return copyOf(Ordering.natural(), all);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedMultiset<E> copyOf(E[] elements)
/*     */   {
/* 175 */     return copyOf(Ordering.natural(), Arrays.asList(elements));
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 204 */     Ordering naturalOrder = Ordering.natural();
/* 205 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 222 */     Ordering naturalOrder = Ordering.natural();
/* 223 */     return copyOfInternal(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements)
/*     */   {
/* 234 */     Preconditions.checkNotNull(comparator);
/* 235 */     return copyOfInternal(comparator, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements)
/*     */   {
/* 250 */     Preconditions.checkNotNull(comparator);
/* 251 */     return copyOfInternal(comparator, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedMultiset<E> copyOfSorted(SortedMultiset<E> sortedMultiset)
/*     */   {
/* 270 */     Comparator comparator = sortedMultiset.comparator();
/* 271 */     if (comparator == null) {
/* 272 */       comparator = NATURAL_ORDER;
/*     */     }
/* 274 */     return copyOfInternal(comparator, sortedMultiset);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableSortedMultiset<E> copyOfInternal(Comparator<? super E> comparator, Iterable<? extends E> iterable)
/*     */   {
/* 280 */     if ((SortedIterables.hasSameComparator(comparator, iterable)) && ((iterable instanceof ImmutableSortedMultiset)))
/*     */     {
/* 282 */       ImmutableSortedMultiset multiset = (ImmutableSortedMultiset)iterable;
/* 283 */       if (!multiset.isPartialView()) {
/* 284 */         return (ImmutableSortedMultiset)iterable;
/*     */       }
/*     */     }
/* 287 */     ImmutableList entries = ImmutableList.copyOf(SortedIterables.sortedCounts(comparator, iterable));
/*     */ 
/* 289 */     if (entries.isEmpty()) {
/* 290 */       return emptyMultiset(comparator);
/*     */     }
/* 292 */     verifyEntries(entries);
/* 293 */     return RegularImmutableSortedMultiset.createFromSorted(comparator, entries);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableSortedMultiset<E> copyOfInternal(Comparator<? super E> comparator, Iterator<? extends E> iterator)
/*     */   {
/* 299 */     ImmutableList entries = ImmutableList.copyOf(SortedIterables.sortedCounts(comparator, iterator));
/*     */ 
/* 301 */     if (entries.isEmpty()) {
/* 302 */       return emptyMultiset(comparator);
/*     */     }
/* 304 */     verifyEntries(entries);
/* 305 */     return RegularImmutableSortedMultiset.createFromSorted(comparator, entries);
/*     */   }
/*     */ 
/*     */   private static <E> void verifyEntries(Collection<Multiset.Entry<E>> entries) {
/* 309 */     for (Multiset.Entry entry : entries)
/* 310 */       Preconditions.checkNotNull(entry.getElement());
/*     */   }
/*     */ 
/*     */   static <E> ImmutableSortedMultiset<E> emptyMultiset(Comparator<? super E> comparator)
/*     */   {
/* 316 */     if (NATURAL_ORDER.equals(comparator)) {
/* 317 */       return NATURAL_EMPTY_MULTISET;
/*     */     }
/* 319 */     return new EmptyImmutableSortedMultiset(comparator);
/*     */   }
/*     */ 
/*     */   ImmutableSortedMultiset(Comparator<? super E> comparator)
/*     */   {
/* 325 */     this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/* 330 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   Comparator<Object> unsafeComparator()
/*     */   {
/* 338 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   Comparator<? super E> reverseComparator()
/*     */   {
/* 344 */     Comparator result = this.reverseComparator;
/* 345 */     if (result == null) {
/* 346 */       return this.reverseComparator = Ordering.from(this.comparator).reverse();
/*     */     }
/* 348 */     return result;
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<E> elementSet()
/*     */   {
/* 355 */     ImmutableSortedSet result = this.elementSet;
/* 356 */     if (result == null) {
/* 357 */       return this.elementSet = createElementSet();
/*     */     }
/* 359 */     return result;
/*     */   }
/*     */ 
/*     */   abstract ImmutableSortedSet<E> createElementSet();
/*     */ 
/*     */   abstract ImmutableSortedSet<E> createDescendingElementSet();
/*     */ 
/*     */   public ImmutableSortedMultiset<E> descendingMultiset()
/*     */   {
/* 370 */     ImmutableSortedMultiset result = this.descendingMultiset;
/* 371 */     if (result == null) {
/* 372 */       return this.descendingMultiset = new DescendingImmutableSortedMultiset(this);
/*     */     }
/* 374 */     return result;
/*     */   }
/*     */ 
/*     */   public final Multiset.Entry<E> pollFirstEntry()
/*     */   {
/* 386 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public Multiset.Entry<E> pollLastEntry()
/*     */   {
/* 398 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedMultiset<E> headMultiset(E paramE, BoundType paramBoundType);
/*     */ 
/*     */   public ImmutableSortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType)
/*     */   {
/* 407 */     return tailMultiset(lowerBound, lowerBoundType).headMultiset(upperBound, upperBoundType);
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSortedMultiset<E> tailMultiset(E paramE, BoundType paramBoundType);
/*     */ 
/*     */   public static <E> Builder<E> orderedBy(Comparator<E> comparator)
/*     */   {
/* 422 */     return new Builder(comparator);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<E>> Builder<E> reverseOrder()
/*     */   {
/* 434 */     return new Builder(Ordering.natural().reverse());
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<E>> Builder<E> naturalOrder()
/*     */   {
/* 448 */     return new Builder(Ordering.natural());
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 608 */     return new SerializedForm(this);
/*     */   }
/*     */ 
/*     */   private static final class SerializedForm
/*     */     implements Serializable
/*     */   {
/*     */     Comparator comparator;
/*     */     Object[] elements;
/*     */     int[] counts;
/*     */ 
/*     */     SerializedForm(SortedMultiset<?> multiset)
/*     */     {
/* 583 */       this.comparator = multiset.comparator();
/* 584 */       int n = multiset.entrySet().size();
/* 585 */       this.elements = new Object[n];
/* 586 */       this.counts = new int[n];
/* 587 */       int i = 0;
/* 588 */       for (Multiset.Entry entry : multiset.entrySet()) {
/* 589 */         this.elements[i] = entry.getElement();
/* 590 */         this.counts[i] = entry.getCount();
/* 591 */         i++;
/*     */       }
/*     */     }
/*     */ 
/*     */     Object readResolve()
/*     */     {
/* 597 */       int n = this.elements.length;
/* 598 */       ImmutableSortedMultiset.Builder builder = ImmutableSortedMultiset.orderedBy(this.comparator);
/* 599 */       for (int i = 0; i < n; i++) {
/* 600 */         builder.addCopies(this.elements[i], this.counts[i]);
/*     */       }
/* 602 */       return builder.build();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Builder<E> extends ImmutableMultiset.Builder<E>
/*     */   {
/*     */     private final Comparator<? super E> comparator;
/*     */ 
/*     */     public Builder(Comparator<? super E> comparator)
/*     */     {
/* 478 */       super();
/* 479 */       this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 491 */       super.add(element);
/* 492 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addCopies(E element, int occurrences)
/*     */     {
/* 508 */       super.addCopies(element, occurrences);
/* 509 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> setCount(E element, int count)
/*     */     {
/* 524 */       super.setCount(element, count);
/* 525 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 537 */       super.add(elements);
/* 538 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 550 */       super.addAll(elements);
/* 551 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 563 */       super.addAll(elements);
/* 564 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSortedMultiset<E> build()
/*     */     {
/* 573 */       return ImmutableSortedMultiset.copyOf(this.comparator, this.contents);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSortedMultiset
 * JD-Core Version:    0.6.2
 */