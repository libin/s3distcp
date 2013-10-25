/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicate;
/*     */ import com.google.common.base.Predicates;
/*     */ import com.google.common.math.IntMath;
/*     */ import com.google.common.math.LongMath;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Collections2
/*     */ {
/* 350 */   static final Joiner STANDARD_JOINER = Joiner.on(", ").useForNull("null");
/*     */ 
/*     */   public static <E> Collection<E> filter(Collection<E> unfiltered, Predicate<? super E> predicate)
/*     */   {
/*  86 */     if ((unfiltered instanceof FilteredCollection))
/*     */     {
/*  89 */       return ((FilteredCollection)unfiltered).createCombined(predicate);
/*     */     }
/*     */ 
/*  92 */     return new FilteredCollection((Collection)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
/*     */   }
/*     */ 
/*     */   static boolean safeContains(Collection<?> collection, Object object)
/*     */   {
/*     */     try
/*     */     {
/* 102 */       return collection.contains(object); } catch (ClassCastException e) {
/*     */     }
/* 104 */     return false;
/*     */   }
/*     */ 
/*     */   public static <F, T> Collection<T> transform(Collection<F> fromCollection, Function<? super F, T> function)
/*     */   {
/* 268 */     return new TransformedCollection(fromCollection, function);
/*     */   }
/*     */ 
/*     */   static boolean containsAllImpl(Collection<?> self, Collection<?> c)
/*     */   {
/* 311 */     Preconditions.checkNotNull(self);
/* 312 */     for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object o = i$.next();
/* 313 */       if (!self.contains(o)) {
/* 314 */         return false;
/*     */       }
/*     */     }
/* 317 */     return true;
/*     */   }
/*     */ 
/*     */   static String toStringImpl(Collection<?> collection)
/*     */   {
/* 324 */     StringBuilder sb = newStringBuilderForCollection(collection.size()).append('[');
/*     */ 
/* 326 */     STANDARD_JOINER.appendTo(sb, Iterables.transform(collection, new Object()
/*     */     {
/*     */       public Object apply(Object input) {
/* 329 */         return input == this.val$collection ? "(this Collection)" : input;
/*     */       }
/*     */     }));
/* 332 */     return ']';
/*     */   }
/*     */ 
/*     */   static StringBuilder newStringBuilderForCollection(int size)
/*     */   {
/* 339 */     Preconditions.checkArgument(size >= 0, "size must be non-negative");
/* 340 */     return new StringBuilder((int)Math.min(size * 8L, 1073741824L));
/*     */   }
/*     */ 
/*     */   static <T> Collection<T> cast(Iterable<T> iterable)
/*     */   {
/* 347 */     return (Collection)iterable;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E extends Comparable<? super E>> Collection<List<E>> orderedPermutations(Iterable<E> elements)
/*     */   {
/* 381 */     return orderedPermutations(elements, Ordering.natural());
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> Collection<List<E>> orderedPermutations(Iterable<E> elements, Comparator<? super E> comparator)
/*     */   {
/* 433 */     return new OrderedPermutationCollection(elements, comparator);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> Collection<List<E>> permutations(Collection<E> elements)
/*     */   {
/* 586 */     return new PermutationCollection(ImmutableList.copyOf(elements));
/*     */   }
/*     */ 
/*     */   private static boolean isPermutation(List<?> first, List<?> second)
/*     */   {
/* 692 */     if (first.size() != second.size()) {
/* 693 */       return false;
/*     */     }
/* 695 */     Multiset firstSet = HashMultiset.create(first);
/* 696 */     Multiset secondSet = HashMultiset.create(second);
/* 697 */     return firstSet.equals(secondSet);
/*     */   }
/*     */ 
/*     */   private static boolean isPositiveInt(long n) {
/* 701 */     return (n >= 0L) && (n <= 2147483647L);
/*     */   }
/*     */ 
/*     */   private static class PermutationIterator<E> extends AbstractIterator<List<E>>
/*     */   {
/*     */     final List<E> list;
/*     */     final int[] c;
/*     */     final int[] o;
/*     */     int j;
/*     */ 
/*     */     PermutationIterator(List<E> list)
/*     */     {
/* 630 */       this.list = new ArrayList(list);
/* 631 */       int n = list.size();
/* 632 */       this.c = new int[n];
/* 633 */       this.o = new int[n];
/* 634 */       for (int i = 0; i < n; i++) {
/* 635 */         this.c[i] = 0;
/* 636 */         this.o[i] = 1;
/*     */       }
/* 638 */       this.j = 2147483647;
/*     */     }
/*     */ 
/*     */     protected List<E> computeNext() {
/* 642 */       if (this.j <= 0) {
/* 643 */         return (List)endOfData();
/*     */       }
/* 645 */       ImmutableList next = ImmutableList.copyOf(this.list);
/* 646 */       calculateNextPermutation();
/* 647 */       return next;
/*     */     }
/*     */ 
/*     */     void calculateNextPermutation() {
/* 651 */       this.j = (this.list.size() - 1);
/* 652 */       int s = 0;
/*     */ 
/* 656 */       if (this.j == -1)
/*     */         return;
/*     */       int q;
/*     */       while (true)
/*     */       {
/* 661 */         q = this.c[this.j] + this.o[this.j];
/* 662 */         if (q < 0) {
/* 663 */           switchDirection();
/*     */         }
/*     */         else {
/* 666 */           if (q != this.j + 1) break;
/* 667 */           if (this.j == 0) {
/*     */             return;
/*     */           }
/* 670 */           s++;
/* 671 */           switchDirection();
/*     */         }
/*     */       }
/*     */ 
/* 675 */       Collections.swap(this.list, this.j - this.c[this.j] + s, this.j - q + s);
/* 676 */       this.c[this.j] = q;
/*     */     }
/*     */ 
/*     */     void switchDirection()
/*     */     {
/* 682 */       this.o[this.j] = (-this.o[this.j]);
/* 683 */       this.j -= 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class PermutationCollection<E> extends AbstractCollection<List<E>>
/*     */   {
/*     */     final ImmutableList<E> inputList;
/*     */ 
/*     */     PermutationCollection(ImmutableList<E> input)
/*     */     {
/* 594 */       this.inputList = input;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 598 */       return IntMath.factorial(this.inputList.size());
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 602 */       return false;
/*     */     }
/*     */ 
/*     */     public Iterator<List<E>> iterator() {
/* 606 */       return new Collections2.PermutationIterator(this.inputList);
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object obj) {
/* 610 */       if ((obj instanceof List)) {
/* 611 */         List list = (List)obj;
/* 612 */         return Collections2.isPermutation(this.inputList, list);
/*     */       }
/* 614 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 618 */       return "permutations(" + this.inputList + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class OrderedPermutationIterator<E> extends AbstractIterator<List<E>>
/*     */   {
/*     */     List<E> nextPermutation;
/*     */     final Comparator<? super E> comparator;
/*     */ 
/*     */     OrderedPermutationIterator(List<E> list, Comparator<? super E> comparator)
/*     */     {
/* 517 */       this.nextPermutation = Lists.newArrayList(list);
/* 518 */       this.comparator = comparator;
/*     */     }
/*     */ 
/*     */     protected List<E> computeNext() {
/* 522 */       if (this.nextPermutation == null) {
/* 523 */         return (List)endOfData();
/*     */       }
/* 525 */       ImmutableList next = ImmutableList.copyOf(this.nextPermutation);
/* 526 */       calculateNextPermutation();
/* 527 */       return next;
/*     */     }
/*     */ 
/*     */     void calculateNextPermutation() {
/* 531 */       int j = findNextJ();
/* 532 */       if (j == -1) {
/* 533 */         this.nextPermutation = null;
/* 534 */         return;
/*     */       }
/*     */ 
/* 537 */       int l = findNextL(j);
/* 538 */       Collections.swap(this.nextPermutation, j, l);
/* 539 */       int n = this.nextPermutation.size();
/* 540 */       Collections.reverse(this.nextPermutation.subList(j + 1, n));
/*     */     }
/*     */ 
/*     */     int findNextJ() {
/* 544 */       for (int k = this.nextPermutation.size() - 2; k >= 0; k--) {
/* 545 */         if (this.comparator.compare(this.nextPermutation.get(k), this.nextPermutation.get(k + 1)) < 0)
/*     */         {
/* 547 */           return k;
/*     */         }
/*     */       }
/* 550 */       return -1;
/*     */     }
/*     */ 
/*     */     int findNextL(int j) {
/* 554 */       Object ak = this.nextPermutation.get(j);
/* 555 */       for (int l = this.nextPermutation.size() - 1; l > j; l--) {
/* 556 */         if (this.comparator.compare(ak, this.nextPermutation.get(l)) < 0) {
/* 557 */           return l;
/*     */         }
/*     */       }
/* 560 */       throw new AssertionError("this statement should be unreachable");
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class OrderedPermutationCollection<E> extends AbstractCollection<List<E>>
/*     */   {
/*     */     final ImmutableList<E> inputList;
/*     */     final Comparator<? super E> comparator;
/*     */     final int size;
/*     */ 
/*     */     OrderedPermutationCollection(Iterable<E> input, Comparator<? super E> comparator)
/*     */     {
/* 444 */       this.inputList = Ordering.from(comparator).immutableSortedCopy(input);
/* 445 */       this.comparator = comparator;
/* 446 */       this.size = calculateSize(this.inputList, comparator);
/*     */     }
/*     */ 
/*     */     private static <E> int calculateSize(List<E> sortedInputList, Comparator<? super E> comparator)
/*     */     {
/* 460 */       long permutations = 1L;
/* 461 */       int n = 1;
/* 462 */       int r = 1;
/* 463 */       while (n < sortedInputList.size()) {
/* 464 */         int comparison = comparator.compare(sortedInputList.get(n - 1), sortedInputList.get(n));
/*     */ 
/* 466 */         if (comparison < 0)
/*     */         {
/* 468 */           permutations *= LongMath.binomial(n, r);
/* 469 */           r = 0;
/* 470 */           if (!Collections2.isPositiveInt(permutations)) {
/* 471 */             return 2147483647;
/*     */           }
/*     */         }
/* 474 */         n++;
/* 475 */         r++;
/*     */       }
/* 477 */       permutations *= LongMath.binomial(n, r);
/* 478 */       if (!Collections2.isPositiveInt(permutations)) {
/* 479 */         return 2147483647;
/*     */       }
/* 481 */       return (int)permutations;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 485 */       return this.size;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 489 */       return false;
/*     */     }
/*     */ 
/*     */     public Iterator<List<E>> iterator() {
/* 493 */       return new Collections2.OrderedPermutationIterator(this.inputList, this.comparator);
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object obj) {
/* 497 */       if ((obj instanceof List)) {
/* 498 */         List list = (List)obj;
/* 499 */         return Collections2.isPermutation(this.inputList, list);
/*     */       }
/* 501 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 505 */       return "orderedPermutationCollection(" + this.inputList + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   static class TransformedCollection<F, T> extends AbstractCollection<T>
/*     */   {
/*     */     final Collection<F> fromCollection;
/*     */     final Function<? super F, ? extends T> function;
/*     */ 
/*     */     TransformedCollection(Collection<F> fromCollection, Function<? super F, ? extends T> function)
/*     */     {
/* 277 */       this.fromCollection = ((Collection)Preconditions.checkNotNull(fromCollection));
/* 278 */       this.function = ((Function)Preconditions.checkNotNull(function));
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 282 */       this.fromCollection.clear();
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 286 */       return this.fromCollection.isEmpty();
/*     */     }
/*     */ 
/*     */     public Iterator<T> iterator() {
/* 290 */       return Iterators.transform(this.fromCollection.iterator(), this.function);
/*     */     }
/*     */ 
/*     */     public int size() {
/* 294 */       return this.fromCollection.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   static class FilteredCollection<E>
/*     */     implements Collection<E>
/*     */   {
/*     */     final Collection<E> unfiltered;
/*     */     final Predicate<? super E> predicate;
/*     */ 
/*     */     FilteredCollection(Collection<E> unfiltered, Predicate<? super E> predicate)
/*     */     {
/* 114 */       this.unfiltered = unfiltered;
/* 115 */       this.predicate = predicate;
/*     */     }
/*     */ 
/*     */     FilteredCollection<E> createCombined(Predicate<? super E> newPredicate) {
/* 119 */       return new FilteredCollection(this.unfiltered, Predicates.and(this.predicate, newPredicate));
/*     */     }
/*     */ 
/*     */     public boolean add(E element)
/*     */     {
/* 126 */       Preconditions.checkArgument(this.predicate.apply(element));
/* 127 */       return this.unfiltered.add(element);
/*     */     }
/*     */ 
/*     */     public boolean addAll(Collection<? extends E> collection)
/*     */     {
/* 132 */       for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 133 */         Preconditions.checkArgument(this.predicate.apply(element));
/*     */       }
/* 135 */       return this.unfiltered.addAll(collection);
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 140 */       Iterables.removeIf(this.unfiltered, this.predicate);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object element)
/*     */     {
/*     */       try
/*     */       {
/* 149 */         Object e = element;
/*     */ 
/* 156 */         return (this.predicate.apply(e)) && (this.unfiltered.contains(element));
/*     */       } catch (NullPointerException e) {
/* 158 */         return false; } catch (ClassCastException e) {
/*     */       }
/* 160 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean containsAll(Collection<?> collection)
/*     */     {
/* 166 */       for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 167 */         if (!contains(element)) {
/* 168 */           return false;
/*     */         }
/*     */       }
/* 171 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 176 */       return !Iterators.any(this.unfiltered.iterator(), this.predicate);
/*     */     }
/*     */ 
/*     */     public Iterator<E> iterator()
/*     */     {
/* 181 */       return Iterators.filter(this.unfiltered.iterator(), this.predicate);
/*     */     }
/*     */ 
/*     */     public boolean remove(Object element)
/*     */     {
/*     */       try
/*     */       {
/* 190 */         Object e = element;
/*     */ 
/* 193 */         return (this.predicate.apply(e)) && (this.unfiltered.remove(element));
/*     */       } catch (NullPointerException e) {
/* 195 */         return false; } catch (ClassCastException e) {
/*     */       }
/* 197 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean removeAll(final Collection<?> collection)
/*     */     {
/* 203 */       Preconditions.checkNotNull(collection);
/* 204 */       Predicate combinedPredicate = new Predicate()
/*     */       {
/*     */         public boolean apply(E input) {
/* 207 */           return (Collections2.FilteredCollection.this.predicate.apply(input)) && (collection.contains(input));
/*     */         }
/*     */       };
/* 210 */       return Iterables.removeIf(this.unfiltered, combinedPredicate);
/*     */     }
/*     */ 
/*     */     public boolean retainAll(final Collection<?> collection)
/*     */     {
/* 215 */       Preconditions.checkNotNull(collection);
/* 216 */       Predicate combinedPredicate = new Predicate()
/*     */       {
/*     */         public boolean apply(E input)
/*     */         {
/* 220 */           return (Collections2.FilteredCollection.this.predicate.apply(input)) && (!collection.contains(input));
/*     */         }
/*     */       };
/* 223 */       return Iterables.removeIf(this.unfiltered, combinedPredicate);
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 228 */       return Iterators.size(iterator());
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 234 */       return Lists.newArrayList(iterator()).toArray();
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array)
/*     */     {
/* 239 */       return Lists.newArrayList(iterator()).toArray(array);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 243 */       return Iterators.toString(iterator());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Collections2
 * JD-Core Version:    0.6.2
 */