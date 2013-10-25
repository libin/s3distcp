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
/*      */ import com.google.common.base.Predicates;
/*      */ import java.util.Arrays;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.PriorityQueue;
/*      */ import java.util.Queue;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Iterators
/*      */ {
/*   68 */   static final UnmodifiableIterator<Object> EMPTY_ITERATOR = new UnmodifiableIterator()
/*      */   {
/*      */     public boolean hasNext()
/*      */     {
/*   72 */       return false;
/*      */     }
/*      */ 
/*      */     public Object next() {
/*   76 */       throw new NoSuchElementException();
/*      */     }
/*   68 */   };
/*      */ 
/*   92 */   private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator()
/*      */   {
/*      */     public boolean hasNext() {
/*   95 */       return false;
/*      */     }
/*      */ 
/*      */     public Object next() {
/*   99 */       throw new NoSuchElementException();
/*      */     }
/*      */ 
/*      */     public void remove() {
/*  103 */       throw new IllegalStateException();
/*      */     }
/*   92 */   };
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> emptyIterator()
/*      */   {
/*   89 */     return EMPTY_ITERATOR;
/*      */   }
/*      */ 
/*      */   static <T> Iterator<T> emptyModifiableIterator()
/*      */   {
/*  116 */     return EMPTY_MODIFIABLE_ITERATOR;
/*      */   }
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> unmodifiableIterator(Iterator<T> iterator)
/*      */   {
/*  122 */     Preconditions.checkNotNull(iterator);
/*  123 */     if ((iterator instanceof UnmodifiableIterator)) {
/*  124 */       return (UnmodifiableIterator)iterator;
/*      */     }
/*  126 */     return new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/*  129 */         return this.val$iterator.hasNext();
/*      */       }
/*      */ 
/*      */       public T next() {
/*  133 */         return this.val$iterator.next();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static <T> UnmodifiableIterator<T> unmodifiableIterator(UnmodifiableIterator<T> iterator)
/*      */   {
/*  146 */     return (UnmodifiableIterator)Preconditions.checkNotNull(iterator);
/*      */   }
/*      */ 
/*      */   static <T> UnmodifiableListIterator<T> unmodifiableListIterator(ListIterator<T> iterator)
/*      */   {
/*  152 */     Preconditions.checkNotNull(iterator);
/*  153 */     if ((iterator instanceof UnmodifiableListIterator)) {
/*  154 */       return (UnmodifiableListIterator)iterator;
/*      */     }
/*  156 */     return new UnmodifiableListIterator()
/*      */     {
/*      */       public boolean hasNext() {
/*  159 */         return this.val$iterator.hasNext();
/*      */       }
/*      */ 
/*      */       public boolean hasPrevious() {
/*  163 */         return this.val$iterator.hasPrevious();
/*      */       }
/*      */ 
/*      */       public T next() {
/*  167 */         return this.val$iterator.next();
/*      */       }
/*      */ 
/*      */       public T previous() {
/*  171 */         return this.val$iterator.previous();
/*      */       }
/*      */ 
/*      */       public int nextIndex() {
/*  175 */         return this.val$iterator.nextIndex();
/*      */       }
/*      */ 
/*      */       public int previousIndex() {
/*  179 */         return this.val$iterator.previousIndex();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static int size(Iterator<?> iterator)
/*      */   {
/*  190 */     int count = 0;
/*  191 */     while (iterator.hasNext()) {
/*  192 */       iterator.next();
/*  193 */       count++;
/*      */     }
/*  195 */     return count;
/*      */   }
/*      */ 
/*      */   public static boolean contains(Iterator<?> iterator, @Nullable Object element)
/*      */   {
/*  203 */     if (element == null) {
/*      */       do if (!iterator.hasNext())
/*      */           break; while (iterator.next() != null);
/*  206 */       return true;
/*      */     }
/*      */ 
/*  210 */     while (iterator.hasNext()) {
/*  211 */       if (element.equals(iterator.next())) {
/*  212 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  216 */     return false;
/*      */   }
/*      */ 
/*      */   public static boolean removeAll(Iterator<?> removeFrom, Collection<?> elementsToRemove)
/*      */   {
/*  230 */     Preconditions.checkNotNull(elementsToRemove);
/*  231 */     boolean modified = false;
/*  232 */     while (removeFrom.hasNext()) {
/*  233 */       if (elementsToRemove.contains(removeFrom.next())) {
/*  234 */         removeFrom.remove();
/*  235 */         modified = true;
/*      */       }
/*      */     }
/*  238 */     return modified;
/*      */   }
/*      */ 
/*      */   public static <T> boolean removeIf(Iterator<T> removeFrom, Predicate<? super T> predicate)
/*      */   {
/*  254 */     Preconditions.checkNotNull(predicate);
/*  255 */     boolean modified = false;
/*  256 */     while (removeFrom.hasNext()) {
/*  257 */       if (predicate.apply(removeFrom.next())) {
/*  258 */         removeFrom.remove();
/*  259 */         modified = true;
/*      */       }
/*      */     }
/*  262 */     return modified;
/*      */   }
/*      */ 
/*      */   public static boolean retainAll(Iterator<?> removeFrom, Collection<?> elementsToRetain)
/*      */   {
/*  276 */     Preconditions.checkNotNull(elementsToRetain);
/*  277 */     boolean modified = false;
/*  278 */     while (removeFrom.hasNext()) {
/*  279 */       if (!elementsToRetain.contains(removeFrom.next())) {
/*  280 */         removeFrom.remove();
/*  281 */         modified = true;
/*      */       }
/*      */     }
/*  284 */     return modified;
/*      */   }
/*      */ 
/*      */   public static boolean elementsEqual(Iterator<?> iterator1, Iterator<?> iterator2)
/*      */   {
/*  299 */     while (iterator1.hasNext()) {
/*  300 */       if (!iterator2.hasNext()) {
/*  301 */         return false;
/*      */       }
/*  303 */       Object o1 = iterator1.next();
/*  304 */       Object o2 = iterator2.next();
/*  305 */       if (!Objects.equal(o1, o2)) {
/*  306 */         return false;
/*      */       }
/*      */     }
/*  309 */     return !iterator2.hasNext();
/*      */   }
/*      */ 
/*      */   public static String toString(Iterator<?> iterator)
/*      */   {
/*  318 */     if (!iterator.hasNext()) {
/*  319 */       return "[]";
/*      */     }
/*  321 */     StringBuilder builder = new StringBuilder();
/*  322 */     builder.append('[').append(iterator.next());
/*  323 */     while (iterator.hasNext()) {
/*  324 */       builder.append(", ").append(iterator.next());
/*      */     }
/*  326 */     return builder.append(']').toString();
/*      */   }
/*      */ 
/*      */   public static <T> T getOnlyElement(Iterator<T> iterator)
/*      */   {
/*  337 */     Object first = iterator.next();
/*  338 */     if (!iterator.hasNext()) {
/*  339 */       return first;
/*      */     }
/*      */ 
/*  342 */     StringBuilder sb = new StringBuilder();
/*  343 */     sb.append(new StringBuilder().append("expected one element but was: <").append(first).toString());
/*  344 */     for (int i = 0; (i < 4) && (iterator.hasNext()); i++) {
/*  345 */       sb.append(new StringBuilder().append(", ").append(iterator.next()).toString());
/*      */     }
/*  347 */     if (iterator.hasNext()) {
/*  348 */       sb.append(", ...");
/*      */     }
/*  350 */     sb.append('>');
/*      */ 
/*  352 */     throw new IllegalArgumentException(sb.toString());
/*      */   }
/*      */ 
/*      */   public static <T> T getOnlyElement(Iterator<? extends T> iterator, @Nullable T defaultValue)
/*      */   {
/*  363 */     return iterator.hasNext() ? getOnlyElement(iterator) : defaultValue;
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("Array.newInstance(Class, int)")
/*      */   public static <T> T[] toArray(Iterator<? extends T> iterator, Class<T> type)
/*      */   {
/*  378 */     List list = Lists.newArrayList(iterator);
/*  379 */     return Iterables.toArray(list, type);
/*      */   }
/*      */ 
/*      */   public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator)
/*      */   {
/*  392 */     Preconditions.checkNotNull(addTo);
/*  393 */     boolean wasModified = false;
/*  394 */     while (iterator.hasNext()) {
/*  395 */       wasModified |= addTo.add(iterator.next());
/*      */     }
/*  397 */     return wasModified;
/*      */   }
/*      */ 
/*      */   public static int frequency(Iterator<?> iterator, @Nullable Object element)
/*      */   {
/*  408 */     int result = 0;
/*  409 */     if (element == null) {
/*  410 */       while (iterator.hasNext()) {
/*  411 */         if (iterator.next() == null) {
/*  412 */           result++;
/*      */         }
/*      */       }
/*      */     }
/*  416 */     while (iterator.hasNext()) {
/*  417 */       if (element.equals(iterator.next())) {
/*  418 */         result++;
/*      */       }
/*      */     }
/*      */ 
/*  422 */     return result;
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> cycle(Iterable<T> iterable)
/*      */   {
/*  440 */     Preconditions.checkNotNull(iterable);
/*  441 */     return new Iterator() {
/*  442 */       Iterator<T> iterator = Iterators.emptyIterator();
/*      */       Iterator<T> removeFrom;
/*      */ 
/*      */       public boolean hasNext() {
/*  447 */         if (!this.iterator.hasNext()) {
/*  448 */           this.iterator = this.val$iterable.iterator();
/*      */         }
/*  450 */         return this.iterator.hasNext();
/*      */       }
/*      */ 
/*      */       public T next() {
/*  454 */         if (!hasNext()) {
/*  455 */           throw new NoSuchElementException();
/*      */         }
/*  457 */         this.removeFrom = this.iterator;
/*  458 */         return this.iterator.next();
/*      */       }
/*      */ 
/*      */       public void remove() {
/*  462 */         Preconditions.checkState(this.removeFrom != null, "no calls to next() since last call to remove()");
/*      */ 
/*  464 */         this.removeFrom.remove();
/*  465 */         this.removeFrom = null;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> cycle(T[] elements)
/*      */   {
/*  484 */     return cycle(Lists.newArrayList(elements));
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b)
/*      */   {
/*  503 */     Preconditions.checkNotNull(a);
/*  504 */     Preconditions.checkNotNull(b);
/*  505 */     return concat(Arrays.asList(new Iterator[] { a, b }).iterator());
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c)
/*      */   {
/*  525 */     Preconditions.checkNotNull(a);
/*  526 */     Preconditions.checkNotNull(b);
/*  527 */     Preconditions.checkNotNull(c);
/*  528 */     return concat(Arrays.asList(new Iterator[] { a, b, c }).iterator());
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T> a, Iterator<? extends T> b, Iterator<? extends T> c, Iterator<? extends T> d)
/*      */   {
/*  549 */     Preconditions.checkNotNull(a);
/*  550 */     Preconditions.checkNotNull(b);
/*  551 */     Preconditions.checkNotNull(c);
/*  552 */     Preconditions.checkNotNull(d);
/*  553 */     return concat(Arrays.asList(new Iterator[] { a, b, c, d }).iterator());
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends T>[] inputs)
/*      */   {
/*  572 */     return concat(ImmutableList.copyOf(inputs).iterator());
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> concat(Iterator<? extends Iterator<? extends T>> inputs)
/*      */   {
/*  591 */     Preconditions.checkNotNull(inputs);
/*  592 */     return new Iterator() {
/*  593 */       Iterator<? extends T> current = Iterators.emptyIterator();
/*      */       Iterator<? extends T> removeFrom;
/*      */ 
/*      */       public boolean hasNext()
/*      */       {
/*      */         boolean currentHasNext;
/*  607 */         while ((!(currentHasNext = ((Iterator)Preconditions.checkNotNull(this.current)).hasNext())) && (this.val$inputs.hasNext())) {
/*  608 */           this.current = ((Iterator)this.val$inputs.next());
/*      */         }
/*  610 */         return currentHasNext;
/*      */       }
/*      */ 
/*      */       public T next() {
/*  614 */         if (!hasNext()) {
/*  615 */           throw new NoSuchElementException();
/*      */         }
/*  617 */         this.removeFrom = this.current;
/*  618 */         return this.current.next();
/*      */       }
/*      */ 
/*      */       public void remove() {
/*  622 */         Preconditions.checkState(this.removeFrom != null, "no calls to next() since last call to remove()");
/*      */ 
/*  624 */         this.removeFrom.remove();
/*  625 */         this.removeFrom = null;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> UnmodifiableIterator<List<T>> partition(Iterator<T> iterator, int size)
/*      */   {
/*  647 */     return partitionImpl(iterator, size, false);
/*      */   }
/*      */ 
/*      */   public static <T> UnmodifiableIterator<List<T>> paddedPartition(Iterator<T> iterator, int size)
/*      */   {
/*  668 */     return partitionImpl(iterator, size, true);
/*      */   }
/*      */ 
/*      */   private static <T> UnmodifiableIterator<List<T>> partitionImpl(Iterator<T> iterator, final int size, final boolean pad)
/*      */   {
/*  673 */     Preconditions.checkNotNull(iterator);
/*  674 */     Preconditions.checkArgument(size > 0);
/*  675 */     return new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/*  678 */         return this.val$iterator.hasNext();
/*      */       }
/*      */ 
/*      */       public List<T> next() {
/*  682 */         if (!hasNext()) {
/*  683 */           throw new NoSuchElementException();
/*      */         }
/*  685 */         Object[] array = new Object[size];
/*  686 */         for (int count = 0; 
/*  687 */           (count < size) && (this.val$iterator.hasNext()); count++) {
/*  688 */           array[count] = this.val$iterator.next();
/*      */         }
/*  690 */         for (int i = count; i < size; i++) {
/*  691 */           array[i] = null;
/*      */         }
/*      */ 
/*  695 */         List list = Collections.unmodifiableList(Arrays.asList(array));
/*      */ 
/*  697 */         return (pad) || (count == size) ? list : list.subList(0, count);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> filter(Iterator<T> unfiltered, final Predicate<? super T> predicate)
/*      */   {
/*  707 */     Preconditions.checkNotNull(unfiltered);
/*  708 */     Preconditions.checkNotNull(predicate);
/*  709 */     return new AbstractIterator() {
/*      */       protected T computeNext() {
/*  711 */         while (this.val$unfiltered.hasNext()) {
/*  712 */           Object element = this.val$unfiltered.next();
/*  713 */           if (predicate.apply(element)) {
/*  714 */             return element;
/*      */           }
/*      */         }
/*  717 */         return endOfData();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("Class.isInstance")
/*      */   public static <T> UnmodifiableIterator<T> filter(Iterator<?> unfiltered, Class<T> type)
/*      */   {
/*  736 */     return filter(unfiltered, Predicates.instanceOf(type));
/*      */   }
/*      */ 
/*      */   public static <T> boolean any(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  746 */     Preconditions.checkNotNull(predicate);
/*  747 */     while (iterator.hasNext()) {
/*  748 */       Object element = iterator.next();
/*  749 */       if (predicate.apply(element)) {
/*  750 */         return true;
/*      */       }
/*      */     }
/*  753 */     return false;
/*      */   }
/*      */ 
/*      */   public static <T> boolean all(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  763 */     Preconditions.checkNotNull(predicate);
/*  764 */     while (iterator.hasNext()) {
/*  765 */       Object element = iterator.next();
/*  766 */       if (!predicate.apply(element)) {
/*  767 */         return false;
/*      */       }
/*      */     }
/*  770 */     return true;
/*      */   }
/*      */ 
/*      */   public static <T> T find(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  786 */     return filter(iterator, predicate).next();
/*      */   }
/*      */ 
/*      */   public static <T> T find(Iterator<? extends T> iterator, Predicate<? super T> predicate, @Nullable T defaultValue)
/*      */   {
/*  801 */     UnmodifiableIterator filteredIterator = filter(iterator, predicate);
/*  802 */     return filteredIterator.hasNext() ? filteredIterator.next() : defaultValue;
/*      */   }
/*      */ 
/*      */   public static <T> Optional<T> tryFind(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  820 */     UnmodifiableIterator filteredIterator = filter(iterator, predicate);
/*  821 */     return filteredIterator.hasNext() ? Optional.of(filteredIterator.next()) : Optional.absent();
/*      */   }
/*      */ 
/*      */   public static <T> int indexOf(Iterator<T> iterator, Predicate<? super T> predicate)
/*      */   {
/*  844 */     Preconditions.checkNotNull(predicate, "predicate");
/*  845 */     int i = 0;
/*  846 */     while (iterator.hasNext()) {
/*  847 */       Object current = iterator.next();
/*  848 */       if (predicate.apply(current)) {
/*  849 */         return i;
/*      */       }
/*  851 */       i++;
/*      */     }
/*  853 */     return -1;
/*      */   }
/*      */ 
/*      */   public static <F, T> Iterator<T> transform(Iterator<F> fromIterator, final Function<? super F, ? extends T> function)
/*      */   {
/*  866 */     Preconditions.checkNotNull(function);
/*  867 */     return new TransformedIterator(fromIterator)
/*      */     {
/*      */       T transform(F from) {
/*  870 */         return function.apply(from);
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> T get(Iterator<T> iterator, int position)
/*      */   {
/*  886 */     checkNonnegative(position);
/*      */ 
/*  888 */     int skipped = 0;
/*  889 */     while (iterator.hasNext()) {
/*  890 */       Object t = iterator.next();
/*  891 */       if (skipped++ == position) {
/*  892 */         return t;
/*      */       }
/*      */     }
/*      */ 
/*  896 */     throw new IndexOutOfBoundsException(new StringBuilder().append("position (").append(position).append(") must be less than the number of elements that remained (").append(skipped).append(")").toString());
/*      */   }
/*      */ 
/*      */   private static void checkNonnegative(int position)
/*      */   {
/*  902 */     if (position < 0)
/*  903 */       throw new IndexOutOfBoundsException(new StringBuilder().append("position (").append(position).append(") must not be negative").toString());
/*      */   }
/*      */ 
/*      */   public static <T> T get(Iterator<? extends T> iterator, int position, @Nullable T defaultValue)
/*      */   {
/*  924 */     checkNonnegative(position);
/*      */     try
/*      */     {
/*  927 */       return get(iterator, position); } catch (IndexOutOfBoundsException e) {
/*      */     }
/*  929 */     return defaultValue;
/*      */   }
/*      */ 
/*      */   public static <T> T getNext(Iterator<? extends T> iterator, @Nullable T defaultValue)
/*      */   {
/*  943 */     return iterator.hasNext() ? iterator.next() : defaultValue;
/*      */   }
/*      */ 
/*      */   public static <T> T getLast(Iterator<T> iterator)
/*      */   {
/*      */     while (true)
/*      */     {
/*  954 */       Object current = iterator.next();
/*  955 */       if (!iterator.hasNext())
/*  956 */         return current;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static <T> T getLast(Iterator<? extends T> iterator, @Nullable T defaultValue)
/*      */   {
/*  970 */     return iterator.hasNext() ? getLast(iterator) : defaultValue;
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static int skip(Iterator<?> iterator, int numberToSkip)
/*      */   {
/*  982 */     Preconditions.checkNotNull(iterator);
/*  983 */     Preconditions.checkArgument(numberToSkip >= 0, "number to skip cannot be negative");
/*      */ 
/*  986 */     for (int i = 0; (i < numberToSkip) && (iterator.hasNext()); i++) {
/*  987 */       iterator.next();
/*      */     }
/*  989 */     return i;
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> limit(final Iterator<T> iterator, int limitSize)
/*      */   {
/* 1006 */     Preconditions.checkNotNull(iterator);
/* 1007 */     Preconditions.checkArgument(limitSize >= 0, "limit is negative");
/* 1008 */     return new Iterator()
/*      */     {
/*      */       private int count;
/*      */ 
/*      */       public boolean hasNext() {
/* 1013 */         return (this.count < this.val$limitSize) && (iterator.hasNext());
/*      */       }
/*      */ 
/*      */       public T next()
/*      */       {
/* 1018 */         if (!hasNext()) {
/* 1019 */           throw new NoSuchElementException();
/*      */         }
/* 1021 */         this.count += 1;
/* 1022 */         return iterator.next();
/*      */       }
/*      */ 
/*      */       public void remove()
/*      */       {
/* 1027 */         iterator.remove();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Iterator<T> consumingIterator(Iterator<T> iterator)
/*      */   {
/* 1046 */     Preconditions.checkNotNull(iterator);
/* 1047 */     return new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/* 1050 */         return this.val$iterator.hasNext();
/*      */       }
/*      */ 
/*      */       public T next()
/*      */       {
/* 1055 */         Object next = this.val$iterator.next();
/* 1056 */         this.val$iterator.remove();
/* 1057 */         return next;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   static void clear(Iterator<?> iterator)
/*      */   {
/* 1068 */     Preconditions.checkNotNull(iterator);
/* 1069 */     while (iterator.hasNext()) {
/* 1070 */       iterator.next();
/* 1071 */       iterator.remove();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> forArray(final T[] array)
/*      */   {
/* 1090 */     Preconditions.checkNotNull(array);
/* 1091 */     return new AbstractIndexedListIterator(array.length) {
/*      */       protected T get(int index) {
/* 1093 */         return array[index];
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   static <T> UnmodifiableIterator<T> forArray(T[] array, int offset, int length)
/*      */   {
/* 1114 */     return forArray(array, offset, length, 0);
/*      */   }
/*      */ 
/*      */   static <T> UnmodifiableListIterator<T> forArray(final T[] array, final int offset, int length, int index)
/*      */   {
/* 1126 */     Preconditions.checkArgument(length >= 0);
/* 1127 */     int end = offset + length;
/*      */ 
/* 1130 */     Preconditions.checkPositionIndexes(offset, end, array.length);
/*      */ 
/* 1137 */     return new AbstractIndexedListIterator(length, index) {
/*      */       protected T get(int index) {
/* 1139 */         return array[(offset + index)];
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> singletonIterator(@Nullable T value)
/*      */   {
/* 1152 */     return new UnmodifiableIterator() {
/*      */       boolean done;
/*      */ 
/*      */       public boolean hasNext() {
/* 1156 */         return !this.done;
/*      */       }
/*      */ 
/*      */       public T next() {
/* 1160 */         if (this.done) {
/* 1161 */           throw new NoSuchElementException();
/*      */         }
/* 1163 */         this.done = true;
/* 1164 */         return this.val$value;
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> UnmodifiableIterator<T> forEnumeration(Enumeration<T> enumeration)
/*      */   {
/* 1179 */     Preconditions.checkNotNull(enumeration);
/* 1180 */     return new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/* 1183 */         return this.val$enumeration.hasMoreElements();
/*      */       }
/*      */ 
/*      */       public T next() {
/* 1187 */         return this.val$enumeration.nextElement();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> Enumeration<T> asEnumeration(Iterator<T> iterator)
/*      */   {
/* 1200 */     Preconditions.checkNotNull(iterator);
/* 1201 */     return new Enumeration()
/*      */     {
/*      */       public boolean hasMoreElements() {
/* 1204 */         return this.val$iterator.hasNext();
/*      */       }
/*      */ 
/*      */       public T nextElement() {
/* 1208 */         return this.val$iterator.next();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <T> PeekingIterator<T> peekingIterator(Iterator<? extends T> iterator)
/*      */   {
/* 1298 */     if ((iterator instanceof PeekingImpl))
/*      */     {
/* 1302 */       PeekingImpl peeking = (PeekingImpl)iterator;
/* 1303 */       return peeking;
/*      */     }
/* 1305 */     return new PeekingImpl(iterator);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static <T> PeekingIterator<T> peekingIterator(PeekingIterator<T> iterator)
/*      */   {
/* 1316 */     return (PeekingIterator)Preconditions.checkNotNull(iterator);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <T> UnmodifiableIterator<T> mergeSorted(Iterable<? extends Iterator<? extends T>> iterators, Comparator<? super T> comparator)
/*      */   {
/* 1336 */     Preconditions.checkNotNull(iterators, "iterators");
/* 1337 */     Preconditions.checkNotNull(comparator, "comparator");
/*      */ 
/* 1339 */     return new MergingIterator(iterators, comparator);
/*      */   }
/*      */ 
/*      */   static void checkRemove(boolean canRemove)
/*      */   {
/* 1400 */     Preconditions.checkState(canRemove, "no calls to next() since the last call to remove()");
/*      */   }
/*      */ 
/*      */   static <T> ListIterator<T> cast(Iterator<T> iterator)
/*      */   {
/* 1407 */     return (ListIterator)iterator;
/*      */   }
/*      */ 
/*      */   private static class MergingIterator<T> extends AbstractIterator<T>
/*      */   {
/*      */     final Queue<PeekingIterator<T>> queue;
/*      */     final Comparator<? super T> comparator;
/*      */ 
/*      */     public MergingIterator(Iterable<? extends Iterator<? extends T>> iterators, Comparator<? super T> itemComparator)
/*      */     {
/* 1357 */       this.comparator = itemComparator;
/*      */ 
/* 1361 */       Comparator heapComparator = new Comparator()
/*      */       {
/*      */         public int compare(PeekingIterator<T> o1, PeekingIterator<T> o2)
/*      */         {
/* 1365 */           return Iterators.MergingIterator.this.comparator.compare(o1.peek(), o2.peek());
/*      */         }
/*      */       };
/* 1369 */       this.queue = new PriorityQueue(2, heapComparator);
/*      */ 
/* 1371 */       for (Iterator iterator : iterators)
/* 1372 */         if (iterator.hasNext())
/* 1373 */           this.queue.add(Iterators.peekingIterator(iterator));
/*      */     }
/*      */ 
/*      */     protected T computeNext()
/*      */     {
/* 1380 */       if (this.queue.isEmpty()) {
/* 1381 */         return endOfData();
/*      */       }
/*      */ 
/* 1384 */       PeekingIterator nextIter = (PeekingIterator)this.queue.poll();
/* 1385 */       Object next = nextIter.next();
/*      */ 
/* 1387 */       if (nextIter.hasNext()) {
/* 1388 */         this.queue.add(nextIter);
/*      */       }
/*      */ 
/* 1391 */       return next;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class PeekingImpl<E>
/*      */     implements PeekingIterator<E>
/*      */   {
/*      */     private final Iterator<? extends E> iterator;
/*      */     private boolean hasPeeked;
/*      */     private E peekedElement;
/*      */ 
/*      */     public PeekingImpl(Iterator<? extends E> iterator)
/*      */     {
/* 1223 */       this.iterator = ((Iterator)Preconditions.checkNotNull(iterator));
/*      */     }
/*      */ 
/*      */     public boolean hasNext()
/*      */     {
/* 1228 */       return (this.hasPeeked) || (this.iterator.hasNext());
/*      */     }
/*      */ 
/*      */     public E next()
/*      */     {
/* 1233 */       if (!this.hasPeeked) {
/* 1234 */         return this.iterator.next();
/*      */       }
/* 1236 */       Object result = this.peekedElement;
/* 1237 */       this.hasPeeked = false;
/* 1238 */       this.peekedElement = null;
/* 1239 */       return result;
/*      */     }
/*      */ 
/*      */     public void remove()
/*      */     {
/* 1244 */       Preconditions.checkState(!this.hasPeeked, "Can't remove after you've peeked at next");
/* 1245 */       this.iterator.remove();
/*      */     }
/*      */ 
/*      */     public E peek()
/*      */     {
/* 1250 */       if (!this.hasPeeked) {
/* 1251 */         this.peekedElement = this.iterator.next();
/* 1252 */         this.hasPeeked = true;
/*      */       }
/* 1254 */       return this.peekedElement;
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Iterators
 * JD-Core Version:    0.6.2
 */