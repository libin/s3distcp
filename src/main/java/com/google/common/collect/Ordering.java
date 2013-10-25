/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class Ordering<T>
/*     */   implements Comparator<T>
/*     */ {
/*     */   static final int LEFT_IS_GREATER = 1;
/*     */   static final int RIGHT_IS_GREATER = -1;
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <C extends Comparable> Ordering<C> natural()
/*     */   {
/*  86 */     return NaturalOrdering.INSTANCE;
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> from(Comparator<T> comparator)
/*     */   {
/* 101 */     return (comparator instanceof Ordering) ? (Ordering)comparator : new ComparatorOrdering(comparator);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> from(Ordering<T> ordering)
/*     */   {
/* 113 */     return (Ordering)Preconditions.checkNotNull(ordering);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> explicit(List<T> valuesInOrder)
/*     */   {
/* 139 */     return new ExplicitOrdering(valuesInOrder);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> explicit(T leastValue, T[] remainingValuesInOrder)
/*     */   {
/* 168 */     return explicit(Lists.asList(leastValue, remainingValuesInOrder));
/*     */   }
/*     */ 
/*     */   public static Ordering<Object> arbitrary()
/*     */   {
/* 207 */     return ArbitraryOrderingHolder.ARBITRARY_ORDERING;
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static Ordering<Object> usingToString()
/*     */   {
/* 270 */     return UsingToStringOrdering.INSTANCE;
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Ordering<T> compound(Iterable<? extends Comparator<? super T>> comparators)
/*     */   {
/* 291 */     return new CompoundOrdering(comparators);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <U extends T> Ordering<U> compound(Comparator<? super U> secondaryComparator)
/*     */   {
/* 316 */     return new CompoundOrdering(this, (Comparator)Preconditions.checkNotNull(secondaryComparator));
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<S> reverse()
/*     */   {
/* 327 */     return new ReverseOrdering(this);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <F> Ordering<F> onResultOf(Function<F, ? extends T> function)
/*     */   {
/* 341 */     return new ByFunctionOrdering(function, this);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<Iterable<S>> lexicographical()
/*     */   {
/* 370 */     return new LexicographicalOrdering(this);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<S> nullsFirst()
/*     */   {
/* 381 */     return new NullsFirstOrdering(this);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public <S extends T> Ordering<S> nullsLast()
/*     */   {
/* 392 */     return new NullsLastOrdering(this);
/*     */   }
/*     */ 
/*     */   public abstract int compare(@Nullable T paramT1, @Nullable T paramT2);
/*     */ 
/*     */   @Beta
/*     */   public <E extends T> List<E> leastOf(Iterable<E> iterable, int k)
/*     */   {
/* 416 */     Preconditions.checkArgument(k >= 0, "%d is negative", new Object[] { Integer.valueOf(k) });
/*     */ 
/* 420 */     Object[] values = (Object[])Iterables.toArray(iterable);
/*     */     Object[] resultArray;
/*     */     Object[] resultArray;
/* 425 */     if (values.length <= k) {
/* 426 */       Arrays.sort(values, this);
/* 427 */       resultArray = values;
/*     */     } else {
/* 429 */       quicksortLeastK(values, 0, values.length - 1, k);
/*     */ 
/* 433 */       Object[] tmp = (Object[])new Object[k];
/* 434 */       resultArray = tmp;
/* 435 */       System.arraycopy(values, 0, resultArray, 0, k);
/*     */     }
/*     */ 
/* 438 */     return Collections.unmodifiableList(Arrays.asList(resultArray));
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public <E extends T> List<E> greatestOf(Iterable<E> iterable, int k)
/*     */   {
/* 459 */     return reverse().leastOf(iterable, k);
/*     */   }
/*     */ 
/*     */   private <E extends T> void quicksortLeastK(E[] values, int left, int right, int k)
/*     */   {
/* 464 */     if (right > left) {
/* 465 */       int pivotIndex = left + right >>> 1;
/* 466 */       int pivotNewIndex = partition(values, left, right, pivotIndex);
/* 467 */       quicksortLeastK(values, left, pivotNewIndex - 1, k);
/* 468 */       if (pivotNewIndex < k)
/* 469 */         quicksortLeastK(values, pivotNewIndex + 1, right, k);
/*     */     }
/*     */   }
/*     */ 
/*     */   private <E extends T> int partition(E[] values, int left, int right, int pivotIndex)
/*     */   {
/* 476 */     Object pivotValue = values[pivotIndex];
/*     */ 
/* 478 */     values[pivotIndex] = values[right];
/* 479 */     values[right] = pivotValue;
/*     */ 
/* 481 */     int storeIndex = left;
/* 482 */     for (int i = left; i < right; i++) {
/* 483 */       if (compare(values[i], pivotValue) < 0) {
/* 484 */         ObjectArrays.swap(values, storeIndex, i);
/* 485 */         storeIndex++;
/*     */       }
/*     */     }
/* 488 */     ObjectArrays.swap(values, right, storeIndex);
/* 489 */     return storeIndex;
/*     */   }
/*     */ 
/*     */   public int binarySearch(List<? extends T> sortedList, @Nullable T key)
/*     */   {
/* 501 */     return Collections.binarySearch(sortedList, key, this);
/*     */   }
/*     */ 
/*     */   public <E extends T> List<E> sortedCopy(Iterable<E> iterable)
/*     */   {
/* 518 */     List list = Lists.newArrayList(iterable);
/* 519 */     Collections.sort(list, this);
/* 520 */     return list;
/*     */   }
/*     */ 
/*     */   public <E extends T> ImmutableList<E> immutableSortedCopy(Iterable<E> iterable)
/*     */   {
/* 540 */     return ImmutableList.copyOf(sortedCopy(iterable));
/*     */   }
/*     */ 
/*     */   public boolean isOrdered(Iterable<? extends T> iterable)
/*     */   {
/* 550 */     Iterator it = iterable.iterator();
/* 551 */     if (it.hasNext()) {
/* 552 */       Object prev = it.next();
/* 553 */       while (it.hasNext()) {
/* 554 */         Object next = it.next();
/* 555 */         if (compare(prev, next) > 0) {
/* 556 */           return false;
/*     */         }
/* 558 */         prev = next;
/*     */       }
/*     */     }
/* 561 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean isStrictlyOrdered(Iterable<? extends T> iterable)
/*     */   {
/* 571 */     Iterator it = iterable.iterator();
/* 572 */     if (it.hasNext()) {
/* 573 */       Object prev = it.next();
/* 574 */       while (it.hasNext()) {
/* 575 */         Object next = it.next();
/* 576 */         if (compare(prev, next) >= 0) {
/* 577 */           return false;
/*     */         }
/* 579 */         prev = next;
/*     */       }
/*     */     }
/* 582 */     return true;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public <E extends T> E max(Iterator<E> iterator)
/*     */   {
/* 601 */     Object maxSoFar = iterator.next();
/*     */ 
/* 603 */     while (iterator.hasNext()) {
/* 604 */       maxSoFar = max(maxSoFar, iterator.next());
/*     */     }
/*     */ 
/* 607 */     return maxSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> E max(Iterable<E> iterable)
/*     */   {
/* 620 */     return max(iterable.iterator());
/*     */   }
/*     */ 
/*     */   public <E extends T> E max(@Nullable E a, @Nullable E b, @Nullable E c, E[] rest)
/*     */   {
/* 636 */     Object maxSoFar = max(max(a, b), c);
/*     */ 
/* 638 */     for (Object r : rest) {
/* 639 */       maxSoFar = max(maxSoFar, r);
/*     */     }
/*     */ 
/* 642 */     return maxSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> E max(@Nullable E a, @Nullable E b)
/*     */   {
/* 659 */     return compare(a, b) >= 0 ? a : b;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public <E extends T> E min(Iterator<E> iterator)
/*     */   {
/* 678 */     Object minSoFar = iterator.next();
/*     */ 
/* 680 */     while (iterator.hasNext()) {
/* 681 */       minSoFar = min(minSoFar, iterator.next());
/*     */     }
/*     */ 
/* 684 */     return minSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> E min(Iterable<E> iterable)
/*     */   {
/* 697 */     return min(iterable.iterator());
/*     */   }
/*     */ 
/*     */   public <E extends T> E min(@Nullable E a, @Nullable E b, @Nullable E c, E[] rest)
/*     */   {
/* 713 */     Object minSoFar = min(min(a, b), c);
/*     */ 
/* 715 */     for (Object r : rest) {
/* 716 */       minSoFar = min(minSoFar, r);
/*     */     }
/*     */ 
/* 719 */     return minSoFar;
/*     */   }
/*     */ 
/*     */   public <E extends T> E min(@Nullable E a, @Nullable E b)
/*     */   {
/* 736 */     return compare(a, b) <= 0 ? a : b;
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class ArbitraryOrdering extends Ordering<Object>
/*     */   {
/* 215 */     private Map<Object, Integer> uids = Platform.tryWeakKeys(new MapMaker()).makeComputingMap(new Function()
/*     */     {
/* 219 */       final AtomicInteger counter = new AtomicInteger(0);
/*     */ 
/*     */       public Integer apply(Object from) {
/* 222 */         return Integer.valueOf(this.counter.getAndIncrement());
/*     */       }
/*     */     });
/*     */ 
/*     */     public int compare(Object left, Object right)
/*     */     {
/* 227 */       if (left == right) {
/* 228 */         return 0;
/*     */       }
/* 230 */       int leftCode = identityHashCode(left);
/* 231 */       int rightCode = identityHashCode(right);
/* 232 */       if (leftCode != rightCode) {
/* 233 */         return leftCode < rightCode ? -1 : 1;
/*     */       }
/*     */ 
/* 237 */       int result = ((Integer)this.uids.get(left)).compareTo((Integer)this.uids.get(right));
/* 238 */       if (result == 0) {
/* 239 */         throw new AssertionError();
/*     */       }
/* 241 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 245 */       return "Ordering.arbitrary()";
/*     */     }
/*     */ 
/*     */     int identityHashCode(Object object)
/*     */     {
/* 257 */       return System.identityHashCode(object);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ArbitraryOrderingHolder
/*     */   {
/* 211 */     static final Ordering<Object> ARBITRARY_ORDERING = new Ordering.ArbitraryOrdering();
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class IncomparableValueException extends ClassCastException
/*     */   {
/*     */     final Object value;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     IncomparableValueException(Object value)
/*     */     {
/* 183 */       super();
/* 184 */       this.value = value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Ordering
 * JD-Core Version:    0.6.2
 */