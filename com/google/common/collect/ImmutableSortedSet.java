/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableSortedSet<E> extends ImmutableSortedSetFauxverideShim<E>
/*     */   implements NavigableSet<E>, SortedIterable<E>
/*     */ {
/*  98 */   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
/*     */ 
/* 101 */   private static final ImmutableSortedSet<Comparable> NATURAL_EMPTY_SET = new EmptyImmutableSortedSet(NATURAL_ORDER);
/*     */   final transient Comparator<? super E> comparator;
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   transient ImmutableSortedSet<E> descendingSet;
/*     */ 
/*     */   private static <E> ImmutableSortedSet<E> emptySet()
/*     */   {
/* 106 */     return NATURAL_EMPTY_SET;
/*     */   }
/*     */ 
/*     */   static <E> ImmutableSortedSet<E> emptySet(Comparator<? super E> comparator)
/*     */   {
/* 111 */     if (NATURAL_ORDER.equals(comparator)) {
/* 112 */       return emptySet();
/*     */     }
/* 114 */     return new EmptyImmutableSortedSet(comparator);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> of()
/*     */   {
/* 122 */     return emptySet();
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E element)
/*     */   {
/* 130 */     return new RegularImmutableSortedSet(ImmutableList.of(element), Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2)
/*     */   {
/* 144 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3)
/*     */   {
/* 157 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 170 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3, e4 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 183 */     return copyOf(Ordering.natural(), Arrays.asList(new Comparable[] { e1, e2, e3, e4, e5 }));
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] remaining)
/*     */   {
/* 197 */     int size = remaining.length + 6;
/* 198 */     List all = new ArrayList(size);
/* 199 */     Collections.addAll(all, new Comparable[] { e1, e2, e3, e4, e5, e6 });
/* 200 */     Collections.addAll(all, remaining);
/* 201 */     return copyOf(Ordering.natural(), all);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<? super E>> ImmutableSortedSet<E> copyOf(E[] elements)
/*     */   {
/* 216 */     return copyOf(Ordering.natural(), Arrays.asList(elements));
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 250 */     Ordering naturalOrder = Ordering.natural();
/* 251 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Collection<? extends E> elements)
/*     */   {
/* 288 */     Ordering naturalOrder = Ordering.natural();
/* 289 */     return copyOf(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 308 */     Ordering naturalOrder = Ordering.natural();
/* 309 */     return copyOfInternal(naturalOrder, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterator<? extends E> elements)
/*     */   {
/* 323 */     Preconditions.checkNotNull(comparator);
/* 324 */     return copyOfInternal(comparator, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Iterable<? extends E> elements)
/*     */   {
/* 342 */     Preconditions.checkNotNull(comparator);
/* 343 */     return copyOfInternal(comparator, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOf(Comparator<? super E> comparator, Collection<? extends E> elements)
/*     */   {
/* 366 */     Preconditions.checkNotNull(comparator);
/* 367 */     return copyOfInternal(comparator, elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSortedSet<E> copyOfSorted(SortedSet<E> sortedSet)
/*     */   {
/* 389 */     Comparator comparator = sortedSet.comparator();
/* 390 */     if (comparator == null) {
/* 391 */       comparator = NATURAL_ORDER;
/*     */     }
/* 393 */     return copyOfInternal(comparator, sortedSet);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableSortedSet<E> copyOfInternal(Comparator<? super E> comparator, Iterable<? extends E> elements)
/*     */   {
/* 398 */     boolean hasSameComparator = SortedIterables.hasSameComparator(comparator, elements);
/*     */ 
/* 401 */     if ((hasSameComparator) && ((elements instanceof ImmutableSortedSet)))
/*     */     {
/* 403 */       ImmutableSortedSet original = (ImmutableSortedSet)elements;
/* 404 */       if (!original.isPartialView()) {
/* 405 */         return original;
/*     */       }
/*     */     }
/* 408 */     ImmutableList list = ImmutableList.copyOf(SortedIterables.sortedUnique(comparator, elements));
/*     */ 
/* 410 */     return list.isEmpty() ? emptySet(comparator) : new RegularImmutableSortedSet(list, comparator);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableSortedSet<E> copyOfInternal(Comparator<? super E> comparator, Iterator<? extends E> elements)
/*     */   {
/* 417 */     ImmutableList list = ImmutableList.copyOf(SortedIterables.sortedUnique(comparator, elements));
/*     */ 
/* 419 */     return list.isEmpty() ? emptySet(comparator) : new RegularImmutableSortedSet(list, comparator);
/*     */   }
/*     */ 
/*     */   public static <E> Builder<E> orderedBy(Comparator<E> comparator)
/*     */   {
/* 433 */     return new Builder(comparator);
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<E>> Builder<E> reverseOrder()
/*     */   {
/* 446 */     return new Builder(Ordering.natural().reverse());
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<E>> Builder<E> naturalOrder()
/*     */   {
/* 462 */     return new Builder(Ordering.natural());
/*     */   }
/*     */ 
/*     */   int unsafeCompare(Object a, Object b)
/*     */   {
/* 557 */     return unsafeCompare(this.comparator, a, b);
/*     */   }
/*     */ 
/*     */   static int unsafeCompare(Comparator<?> comparator, Object a, Object b)
/*     */   {
/* 566 */     Comparator unsafeComparator = comparator;
/* 567 */     return unsafeComparator.compare(a, b);
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet(Comparator<? super E> comparator)
/*     */   {
/* 573 */     this.comparator = comparator;
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/* 585 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   public abstract UnmodifiableIterator<E> iterator();
/*     */ 
/*     */   public ImmutableSortedSet<E> headSet(E toElement)
/*     */   {
/* 604 */     return headSet(toElement, false);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> headSet(E toElement, boolean inclusive)
/*     */   {
/* 613 */     return headSetImpl(Preconditions.checkNotNull(toElement), inclusive);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<E> subSet(E fromElement, E toElement)
/*     */   {
/* 631 */     return subSet(fromElement, true, toElement, false);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/* 641 */     Preconditions.checkNotNull(fromElement);
/* 642 */     Preconditions.checkNotNull(toElement);
/* 643 */     Preconditions.checkArgument(this.comparator.compare(fromElement, toElement) <= 0);
/* 644 */     return subSetImpl(fromElement, fromInclusive, toElement, toInclusive);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<E> tailSet(E fromElement)
/*     */   {
/* 660 */     return tailSet(fromElement, true);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> tailSet(E fromElement, boolean inclusive)
/*     */   {
/* 669 */     return tailSetImpl(Preconditions.checkNotNull(fromElement), inclusive);
/*     */   }
/*     */ 
/*     */   abstract ImmutableSortedSet<E> headSetImpl(E paramE, boolean paramBoolean);
/*     */ 
/*     */   abstract ImmutableSortedSet<E> subSetImpl(E paramE1, boolean paramBoolean1, E paramE2, boolean paramBoolean2);
/*     */ 
/*     */   abstract ImmutableSortedSet<E> tailSetImpl(E paramE, boolean paramBoolean);
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E lower(E e)
/*     */   {
/* 689 */     return Iterables.getFirst(headSet(e, false).descendingSet(), null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E floor(E e)
/*     */   {
/* 698 */     return Iterables.getFirst(headSet(e, true).descendingSet(), null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E ceiling(E e)
/*     */   {
/* 707 */     return Iterables.getFirst(tailSet(e, true), null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public E higher(E e)
/*     */   {
/* 716 */     return Iterables.getFirst(tailSet(e, false), null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public final E pollFirst()
/*     */   {
/* 725 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public final E pollLast()
/*     */   {
/* 734 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public ImmutableSortedSet<E> descendingSet()
/*     */   {
/* 746 */     ImmutableSortedSet result = this.descendingSet;
/* 747 */     if (result == null) {
/* 748 */       result = this.descendingSet = createDescendingSet();
/* 749 */       result.descendingSet = this;
/*     */     }
/* 751 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   abstract ImmutableSortedSet<E> createDescendingSet();
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   public UnmodifiableIterator<E> descendingIterator()
/*     */   {
/* 763 */     return descendingSet().iterator();
/*     */   }
/*     */ 
/*     */   abstract int indexOf(@Nullable Object paramObject);
/*     */ 
/*     */   private void readObject(ObjectInputStream stream)
/*     */     throws InvalidObjectException
/*     */   {
/* 796 */     throw new InvalidObjectException("Use SerializedForm");
/*     */   }
/*     */ 
/*     */   Object writeReplace() {
/* 800 */     return new SerializedForm(this.comparator, toArray());
/*     */   }
/*     */ 
/*     */   private static class SerializedForm<E>
/*     */     implements Serializable
/*     */   {
/*     */     final Comparator<? super E> comparator;
/*     */     final Object[] elements;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public SerializedForm(Comparator<? super E> comparator, Object[] elements)
/*     */     {
/* 782 */       this.comparator = comparator;
/* 783 */       this.elements = elements;
/*     */     }
/*     */ 
/*     */     Object readResolve()
/*     */     {
/* 788 */       return new ImmutableSortedSet.Builder(this.comparator).add((Object[])this.elements).build();
/*     */     }
/*     */   }
/*     */ 
/*     */   public static final class Builder<E> extends ImmutableSet.Builder<E>
/*     */   {
/*     */     private final Comparator<? super E> comparator;
/*     */ 
/*     */     public Builder(Comparator<? super E> comparator)
/*     */     {
/* 490 */       this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 504 */       super.add(element);
/* 505 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 517 */       super.add(elements);
/* 518 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 530 */       super.addAll(elements);
/* 531 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 543 */       super.addAll(elements);
/* 544 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSortedSet<E> build()
/*     */     {
/* 552 */       return ImmutableSortedSet.copyOfInternal(this.comparator, this.contents.iterator());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSortedSet
 * JD-Core Version:    0.6.2
 */