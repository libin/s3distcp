/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Objects;
/*      */ import com.google.common.base.Optional;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Comparator;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Queue;
/*      */ import java.util.RandomAccess;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Iterables
/*      */ {
/*      */   public static <T> Iterable<T> unmodifiableIterable(Iterable<T> iterable)
/*      */   {
/*   70 */     Preconditions.checkNotNull(iterable);
/*   71 */     if (((iterable instanceof UnmodifiableIterable)) || ((iterable instanceof ImmutableCollection)))
/*      */     {
/*   73 */       return iterable;
/*      */     }
/*   75 */     return new UnmodifiableIterable(iterable, null);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static <E> Iterable<E> unmodifiableIterable(ImmutableCollection<E> iterable)
/*      */   {
/*   86 */     return (Iterable)Preconditions.checkNotNull(iterable);
/*      */   }
/*      */ 
/*      */   public static int size(Iterable<?> iterable)
/*      */   {
/*  112 */     return (iterable instanceof Collection) ? ((Collection)iterable).size() : Iterators.size(iterable.iterator());
/*      */   }
/*      */ 
/*      */   public static boolean contains(Iterable<?> iterable, @Nullable Object element)
/*      */   {
/*  123 */     if ((iterable instanceof Collection)) {
/*  124 */       Collection collection = (Collection)iterable;
/*      */       try {
/*  126 */         return collection.contains(element);
/*      */       } catch (NullPointerException e) {
/*  128 */         return false;
/*      */       } catch (ClassCastException e) {
/*  130 */         return false;
/*      */       }
/*      */     }
/*  133 */     return Iterators.contains(iterable.iterator(), element);
/*      */   }
/*      */ 
/*      */   public static boolean removeAll(Iterable<?> removeFrom, Collection<?> elementsToRemove)
/*      */   {
/*  149 */     return (removeFrom instanceof Collection) ? ((Collection)removeFrom).removeAll((Collection)Preconditions.checkNotNull(elementsToRemove)) : Iterators.removeAll(removeFrom.iterator(), elementsToRemove);
/*      */   }
/*      */ 
/*      */   public static boolean retainAll(Iterable<?> removeFrom, Collection<?> elementsToRetain)
/*      */   {
/*  167 */     return (removeFrom instanceof Collection) ? ((Collection)removeFrom).retainAll((Collection)Preconditions.checkNotNull(elementsToRetain)) : Iterators.retainAll(removeFrom.iterator(), elementsToRetain);
/*      */   }
/*      */ 
/*      */   public static <T> boolean removeIf(Iterable<T> removeFrom, Predicate<? super T> predicate)
/*      */   {
/*  187 */     if (((removeFrom instanceof RandomAccess)) && ((removeFrom instanceof List))) {
/*  188 */       return removeIfFromRandomAccessList((List)removeFrom, (Predicate)Preconditions.checkNotNull(predicate));
/*      */     }
/*      */ 
/*  191 */     return Iterators.removeIf(removeFrom.iterator(), predicate);
/*      */   }
/*      */ 
/*      */   private static <T> boolean removeIfFromRandomAccessList(List<T> list, Predicate<? super T> predicate)
/*      */   {
/*  198 */     int from = 0;
/*  199 */     int to = 0;
/*      */ 
/*  201 */     for (; from < list.size(); from++) {
/*  202 */       Object element = list.get(from);
/*  203 */       if (!predicate.apply(element)) {
/*  204 */         if (from > to) {
/*      */           try {
/*  206 */             list.set(to, element);
/*      */           } catch (UnsupportedOperationException e) {
/*  208 */             slowRemoveIfForRemainingElements(list, predicate, to, from);
/*  209 */             return true;
/*      */           }
/*      */         }
/*  212 */         to++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  217 */     list.subList(to, list.size()).clear();
/*  218 */     return from != to;
/*      */   }
/*      */ 
/*      */   private static <T> void slowRemoveIfForRemainingElements(List<T> list, Predicate<? super T> predicate, int to, int from)
/*      */   {
/*  233 */     for (int n = list.size() - 1; n > from; n--) {
/*  234 */       if (predicate.apply(list.get(n))) {
/*  235 */         list.remove(n);
/*      */       }
/*      */     }
/*      */ 
/*  239 */     for (int n = from - 1; n >= to; n--)
/*  240 */       list.remove(n);
/*      */   }
/*      */ 
/*      */   public static boolean elementsEqual(Iterable<?> iterable1, Iterable<?> iterable2)
/*      */   {
/*  253 */     return Iterators.elementsEqual(iterable1.iterator(), iterable2.iterator());
/*      */   }
/*      */ 
/*      */   public static String toString(Iterable<?> iterable)
/*      */   {
/*  261 */     return Iterators.toString(iterable.iterator());
/*      */   }
/*      */ 
/*      */   public static <T> T getOnlyElement(Iterable<T> iterable)
/*      */   {
/*  272 */     return Iterators.getOnlyElement(iterable.iterator());
/*      */   }
/*      */ 
/*      */   public static <T> T getOnlyElement(Iterable<? extends T> iterable, @Nullable T defaultValue)
/*      */   {
/*  284 */     return Iterators.getOnlyElement(iterable.iterator(), defaultValue);
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("Array.newInstance(Class, int)")
/*      */   public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type)
/*      */   {
/*  297 */     Collection collection = toCollection(iterable);
/*  298 */     Object[] array = ObjectArrays.newArray(type, collection.size());
/*  299 */     return collection.toArray(array);
/*      */   }
/*      */ 
/*      */   static Object[] toArray(Iterable<?> iterable)
/*      */   {
/*  310 */     return toCollection(iterable).toArray();
/*      */   }
/*      */ 
/*      */   private static <E> Collection<E> toCollection(Iterable<E> iterable)
/*      */   {
/*  319 */     return (iterable instanceof Collection) ? (Collection)iterable : Lists.newArrayList(iterable.iterator());
/*      */   }
/*      */ 
/*      */   public static <T> boolean addAll(Collection<T> addTo, Iterable<? extends T> elementsToAdd)
/*      */   {
/*  332 */     if ((elementsToAdd instanceof Collection)) {
/*  333 */       Collection c = Collections2.cast(elementsToAdd);
/*  334 */       return addTo.addAll(c);
/*      */     }
/*  336 */     return Iterators.addAll(addTo, elementsToAdd.iterator());
/*      */   }
/*      */ 
/*      */   public static int frequency(Iterable<?> iterable, @Nullable Object element)
/*      */   {
/*  347 */     if ((iterable instanceof Multiset)) {
/*  348 */       return ((Multiset)iterable).count(element);
/*      */     }
/*  350 */     if ((iterable instanceof Set)) {
/*  351 */       return ((Set)iterable).contains(element) ? 1 : 0;
/*      */     }
/*  353 */     return Iterators.frequency(iterable.iterator(), element);
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> cycle(Iterable<T> iterable)
/*      */   {
/*  374 */     Preconditions.checkNotNull(iterable);
/*  375 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  378 */         return Iterators.cycle(this.val$iterable);
/*      */       }
/*      */       public String toString() {
/*  381 */         return this.val$iterable.toString() + " (cycled)";
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> cycle(T[] elements)
/*      */   {
/*  405 */     return cycle(Lists.newArrayList(elements));
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b)
/*      */   {
/*  419 */     Preconditions.checkNotNull(a);
/*  420 */     Preconditions.checkNotNull(b);
/*  421 */     return concat(Arrays.asList(new Iterable[] { a, b }));
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c)
/*      */   {
/*  436 */     Preconditions.checkNotNull(a);
/*  437 */     Preconditions.checkNotNull(b);
/*  438 */     Preconditions.checkNotNull(c);
/*  439 */     return concat(Arrays.asList(new Iterable[] { a, b, c }));
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T> a, Iterable<? extends T> b, Iterable<? extends T> c, Iterable<? extends T> d)
/*      */   {
/*  456 */     Preconditions.checkNotNull(a);
/*  457 */     Preconditions.checkNotNull(b);
/*  458 */     Preconditions.checkNotNull(c);
/*  459 */     Preconditions.checkNotNull(d);
/*  460 */     return concat(Arrays.asList(new Iterable[] { a, b, c, d }));
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> concat(Iterable<? extends T>[] inputs)
/*      */   {
/*  474 */     return concat(ImmutableList.copyOf(inputs));
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> concat(Iterable<? extends Iterable<? extends T>> inputs)
/*      */   {
/*  489 */     Preconditions.checkNotNull(inputs);
/*  490 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  493 */         return Iterators.concat(Iterables.iterators(this.val$inputs));
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private static <T> UnmodifiableIterator<Iterator<? extends T>> iterators(Iterable<? extends Iterable<? extends T>> iterables)
/*      */   {
/*  503 */     Iterator iterableIterator = iterables.iterator();
/*      */ 
/*  505 */     return new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/*  508 */         return this.val$iterableIterator.hasNext();
/*      */       }
/*      */ 
/*      */       public Iterator<? extends T> next() {
/*  512 */         return ((Iterable)this.val$iterableIterator.next()).iterator();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<List<T>> partition(Iterable<T> iterable, final int size)
/*      */   {
/*  539 */     Preconditions.checkNotNull(iterable);
/*  540 */     Preconditions.checkArgument(size > 0);
/*  541 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<List<T>> iterator() {
/*  544 */         return Iterators.partition(this.val$iterable.iterator(), size);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<List<T>> paddedPartition(Iterable<T> iterable, final int size)
/*      */   {
/*  568 */     Preconditions.checkNotNull(iterable);
/*  569 */     Preconditions.checkArgument(size > 0);
/*  570 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<List<T>> iterator() {
/*  573 */         return Iterators.paddedPartition(this.val$iterable.iterator(), size);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> filter(Iterable<T> unfiltered, final Predicate<? super T> predicate)
/*      */   {
/*  584 */     Preconditions.checkNotNull(unfiltered);
/*  585 */     Preconditions.checkNotNull(predicate);
/*  586 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  589 */         return Iterators.filter(this.val$unfiltered.iterator(), predicate);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("Class.isInstance")
/*      */   public static <T> Iterable<T> filter(Iterable<?> unfiltered, final Class<T> type)
/*      */   {
/*  608 */     Preconditions.checkNotNull(unfiltered);
/*  609 */     Preconditions.checkNotNull(type);
/*  610 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  613 */         return Iterators.filter(this.val$unfiltered.iterator(), type);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> boolean any(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  623 */     return Iterators.any(iterable.iterator(), predicate);
/*      */   }
/*      */ 
/*      */   public static <T> boolean all(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  632 */     return Iterators.all(iterable.iterator(), predicate);
/*      */   }
/*      */ 
/*      */   public static <T> T find(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  646 */     return Iterators.find(iterable.iterator(), predicate);
/*      */   }
/*      */ 
/*      */   public static <T> T find(Iterable<? extends T> iterable, Predicate<? super T> predicate, @Nullable T defaultValue)
/*      */   {
/*  659 */     return Iterators.find(iterable.iterator(), predicate, defaultValue);
/*      */   }
/*      */ 
/*      */   public static <T> Optional<T> tryFind(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  674 */     return Iterators.tryFind(iterable.iterator(), predicate);
/*      */   }
/*      */ 
/*      */   public static <T> int indexOf(Iterable<T> iterable, Predicate<? super T> predicate)
/*      */   {
/*  690 */     return Iterators.indexOf(iterable.iterator(), predicate);
/*      */   }
/*      */ 
/*      */   public static <F, T> Iterable<T> transform(Iterable<F> fromIterable, final Function<? super F, ? extends T> function)
/*      */   {
/*  707 */     Preconditions.checkNotNull(fromIterable);
/*  708 */     Preconditions.checkNotNull(function);
/*  709 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  712 */         return Iterators.transform(this.val$fromIterable.iterator(), function);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> T get(Iterable<T> iterable, int position)
/*      */   {
/*  726 */     Preconditions.checkNotNull(iterable);
/*  727 */     if ((iterable instanceof List)) {
/*  728 */       return ((List)iterable).get(position);
/*      */     }
/*      */ 
/*  731 */     if ((iterable instanceof Collection))
/*      */     {
/*  733 */       Collection collection = (Collection)iterable;
/*  734 */       Preconditions.checkElementIndex(position, collection.size());
/*      */     }
/*      */     else {
/*  737 */       checkNonnegativeIndex(position);
/*      */     }
/*  739 */     return Iterators.get(iterable.iterator(), position);
/*      */   }
/*      */ 
/*      */   private static void checkNonnegativeIndex(int position) {
/*  743 */     if (position < 0)
/*  744 */       throw new IndexOutOfBoundsException("position cannot be negative: " + position);
/*      */   }
/*      */ 
/*      */   public static <T> T get(Iterable<? extends T> iterable, int position, @Nullable T defaultValue)
/*      */   {
/*  763 */     Preconditions.checkNotNull(iterable);
/*  764 */     checkNonnegativeIndex(position);
/*      */     try
/*      */     {
/*  767 */       return get(iterable, position); } catch (IndexOutOfBoundsException e) {
/*      */     }
/*  769 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public static <T> T getFirst(Iterable<? extends T> iterable, @Nullable T defaultValue)
/*      */   {
/*  783 */     return Iterators.getNext(iterable.iterator(), defaultValue);
/*      */   }
/*      */ 
/*      */   public static <T> T getLast(Iterable<T> iterable)
/*      */   {
/*  794 */     if ((iterable instanceof List)) {
/*  795 */       List list = (List)iterable;
/*  796 */       if (list.isEmpty()) {
/*  797 */         throw new NoSuchElementException();
/*      */       }
/*  799 */       return getLastInNonemptyList(list);
/*      */     }
/*      */ 
/*  807 */     if ((iterable instanceof SortedSet)) {
/*  808 */       SortedSet sortedSet = (SortedSet)iterable;
/*  809 */       return sortedSet.last();
/*      */     }
/*      */ 
/*  812 */     return Iterators.getLast(iterable.iterator());
/*      */   }
/*      */ 
/*      */   public static <T> T getLast(Iterable<? extends T> iterable, @Nullable T defaultValue)
/*      */   {
/*  824 */     if ((iterable instanceof Collection)) {
/*  825 */       Collection collection = Collections2.cast(iterable);
/*  826 */       if (collection.isEmpty()) {
/*  827 */         return defaultValue;
/*      */       }
/*      */     }
/*      */ 
/*  831 */     if ((iterable instanceof List)) {
/*  832 */       List list = Lists.cast(iterable);
/*  833 */       return getLastInNonemptyList(list);
/*      */     }
/*      */ 
/*  841 */     if ((iterable instanceof SortedSet)) {
/*  842 */       SortedSet sortedSet = Sets.cast(iterable);
/*  843 */       return sortedSet.last();
/*      */     }
/*      */ 
/*  846 */     return Iterators.getLast(iterable.iterator(), defaultValue);
/*      */   }
/*      */ 
/*      */   private static <T> T getLastInNonemptyList(List<T> list) {
/*  850 */     return list.get(list.size() - 1);
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> skip(Iterable<T> iterable, final int numberToSkip)
/*      */   {
/*  875 */     Preconditions.checkNotNull(iterable);
/*  876 */     Preconditions.checkArgument(numberToSkip >= 0, "number to skip cannot be negative");
/*      */ 
/*  878 */     if ((iterable instanceof List)) {
/*  879 */       final List list = (List)iterable;
/*  880 */       return new FluentIterable()
/*      */       {
/*      */         public Iterator<T> iterator()
/*      */         {
/*  884 */           return this.val$numberToSkip >= list.size() ? Iterators.emptyIterator() : list.subList(this.val$numberToSkip, list.size()).iterator();
/*      */         }
/*      */ 
/*      */       };
/*      */     }
/*      */ 
/*  891 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  894 */         final Iterator iterator = this.val$iterable.iterator();
/*      */ 
/*  896 */         Iterators.skip(iterator, numberToSkip);
/*      */ 
/*  903 */         return new Iterator() {
/*  904 */           boolean atStart = true;
/*      */ 
/*      */           public boolean hasNext()
/*      */           {
/*  908 */             return iterator.hasNext();
/*      */           }
/*      */ 
/*      */           public T next()
/*      */           {
/*  913 */             if (!hasNext()) {
/*  914 */               throw new NoSuchElementException();
/*      */             }
/*      */             try
/*      */             {
/*  918 */               return iterator.next();
/*      */             } finally {
/*  920 */               this.atStart = false;
/*      */             }
/*      */           }
/*      */ 
/*      */           public void remove()
/*      */           {
/*  926 */             if (this.atStart) {
/*  927 */               throw new IllegalStateException();
/*      */             }
/*  929 */             iterator.remove();
/*      */           }
/*      */         };
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> limit(Iterable<T> iterable, final int limitSize)
/*      */   {
/*  950 */     Preconditions.checkNotNull(iterable);
/*  951 */     Preconditions.checkArgument(limitSize >= 0, "limit is negative");
/*  952 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  955 */         return Iterators.limit(this.val$iterable.iterator(), limitSize);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterable<T> consumingIterable(Iterable<T> iterable)
/*      */   {
/*  980 */     if ((iterable instanceof Queue)) {
/*  981 */       return new FluentIterable()
/*      */       {
/*      */         public Iterator<T> iterator() {
/*  984 */           return new Iterables.ConsumingQueueIterator((Queue)this.val$iterable, null);
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*  989 */     Preconditions.checkNotNull(iterable);
/*      */ 
/*  991 */     return new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/*  994 */         return Iterators.consumingIterator(this.val$iterable.iterator());
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static <T> Iterable<T> reverse(List<T> list)
/*      */   {
/* 1038 */     return Lists.reverse(list);
/*      */   }
/*      */ 
/*      */   public static boolean isEmpty(Iterable<?> iterable)
/*      */   {
/* 1051 */     if ((iterable instanceof Collection)) {
/* 1052 */       return ((Collection)iterable).isEmpty();
/*      */     }
/* 1054 */     return !iterable.iterator().hasNext();
/*      */   }
/*      */ 
/*      */   static boolean remove(Iterable<?> iterable, @Nullable Object o)
/*      */   {
/* 1078 */     Iterator i = iterable.iterator();
/* 1079 */     while (i.hasNext()) {
/* 1080 */       if (Objects.equal(i.next(), o)) {
/* 1081 */         i.remove();
/* 1082 */         return true;
/*      */       }
/*      */     }
/* 1085 */     return false;
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <T> Iterable<T> mergeSorted(Iterable<? extends Iterable<? extends T>> iterables, final Comparator<? super T> comparator)
/*      */   {
/* 1104 */     Preconditions.checkNotNull(iterables, "iterables");
/* 1105 */     Preconditions.checkNotNull(comparator, "comparator");
/* 1106 */     Iterable iterable = new FluentIterable()
/*      */     {
/*      */       public Iterator<T> iterator() {
/* 1109 */         return Iterators.mergeSorted(Iterables.transform(this.val$iterables, Iterables.access$300()), comparator);
/*      */       }
/*      */     };
/* 1114 */     return new UnmodifiableIterable(iterable, null);
/*      */   }
/*      */ 
/*      */   private static <T> Function<Iterable<? extends T>, Iterator<? extends T>> toIterator()
/*      */   {
/* 1121 */     return new Function()
/*      */     {
/*      */       public Iterator<? extends T> apply(Iterable<? extends T> iterable) {
/* 1124 */         return iterable.iterator();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private static class ConsumingQueueIterator<T> extends AbstractIterator<T>
/*      */   {
/*      */     private final Queue<T> queue;
/*      */ 
/*      */     private ConsumingQueueIterator(Queue<T> queue)
/*      */     {
/* 1003 */       this.queue = queue;
/*      */     }
/*      */ 
/*      */     public T computeNext() {
/*      */       try {
/* 1008 */         return this.queue.remove(); } catch (NoSuchElementException e) {
/*      */       }
/* 1010 */       return endOfData();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class UnmodifiableIterable<T> extends FluentIterable<T>
/*      */   {
/*      */     private final Iterable<T> iterable;
/*      */ 
/*      */     private UnmodifiableIterable(Iterable<T> iterable)
/*      */     {
/*   93 */       this.iterable = iterable;
/*      */     }
/*      */ 
/*      */     public Iterator<T> iterator()
/*      */     {
/*   98 */       return Iterators.unmodifiableIterator(this.iterable.iterator());
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  103 */       return this.iterable.toString();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Iterables
 * JD-Core Version:    0.6.2
 */