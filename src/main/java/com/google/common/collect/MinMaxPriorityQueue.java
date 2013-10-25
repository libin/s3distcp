/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.math.IntMath;
/*     */ import java.util.AbstractQueue;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.ConcurrentModificationException;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
/*     */ 
/*     */ @Beta
/*     */ public final class MinMaxPriorityQueue<E> extends AbstractQueue<E>
/*     */ {
/*     */   private final MinMaxPriorityQueue<E>.Heap minHeap;
/*     */   private final MinMaxPriorityQueue<E>.Heap maxHeap;
/*     */ 
/*     */   @VisibleForTesting
/*     */   final int maximumSize;
/*     */   private Object[] queue;
/*     */   private int size;
/*     */   private int modCount;
/*     */   private static final int EVEN_POWERS_OF_TWO = 1431655765;
/*     */   private static final int ODD_POWERS_OF_TWO = -1431655766;
/*     */   private static final int DEFAULT_CAPACITY = 11;
/*     */ 
/*     */   public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create()
/*     */   {
/*  97 */     return new Builder(Ordering.natural(), null).create();
/*     */   }
/*     */ 
/*     */   public static <E extends Comparable<E>> MinMaxPriorityQueue<E> create(Iterable<? extends E> initialContents)
/*     */   {
/* 106 */     return new Builder(Ordering.natural(), null).create(initialContents);
/*     */   }
/*     */ 
/*     */   public static <B> Builder<B> orderedBy(Comparator<B> comparator)
/*     */   {
/* 115 */     return new Builder(comparator, null);
/*     */   }
/*     */ 
/*     */   public static Builder<Comparable> expectedSize(int expectedSize)
/*     */   {
/* 124 */     return new Builder(Ordering.natural(), null).expectedSize(expectedSize);
/*     */   }
/*     */ 
/*     */   public static Builder<Comparable> maximumSize(int maximumSize)
/*     */   {
/* 136 */     return new Builder(Ordering.natural(), null).maximumSize(maximumSize);
/*     */   }
/*     */ 
/*     */   private MinMaxPriorityQueue(Builder<? super E> builder, int queueSize)
/*     */   {
/* 227 */     Ordering ordering = builder.ordering();
/* 228 */     this.minHeap = new Heap(ordering);
/* 229 */     this.maxHeap = new Heap(ordering.reverse());
/* 230 */     this.minHeap.otherHeap = this.maxHeap;
/* 231 */     this.maxHeap.otherHeap = this.minHeap;
/*     */ 
/* 233 */     this.maximumSize = builder.maximumSize;
/*     */ 
/* 235 */     this.queue = new Object[queueSize];
/*     */   }
/*     */ 
/*     */   public int size() {
/* 239 */     return this.size;
/*     */   }
/*     */ 
/*     */   public boolean add(E element)
/*     */   {
/* 251 */     offer(element);
/* 252 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean addAll(Collection<? extends E> newElements) {
/* 256 */     boolean modified = false;
/* 257 */     for (Iterator i$ = newElements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 258 */       offer(element);
/* 259 */       modified = true;
/*     */     }
/* 261 */     return modified;
/*     */   }
/*     */ 
/*     */   public boolean offer(E element)
/*     */   {
/* 271 */     Preconditions.checkNotNull(element);
/* 272 */     this.modCount += 1;
/* 273 */     int insertIndex = this.size++;
/*     */ 
/* 275 */     growIfNeeded();
/*     */ 
/* 279 */     heapForIndex(insertIndex).bubbleUp(insertIndex, element);
/* 280 */     return (this.size <= this.maximumSize) || (pollLast() != element);
/*     */   }
/*     */ 
/*     */   public E poll() {
/* 284 */     return isEmpty() ? null : removeAndGet(0);
/*     */   }
/*     */ 
/*     */   E elementData(int index)
/*     */   {
/* 289 */     return this.queue[index];
/*     */   }
/*     */ 
/*     */   public E peek() {
/* 293 */     return isEmpty() ? null : elementData(0);
/*     */   }
/*     */ 
/*     */   private int getMaxElementIndex()
/*     */   {
/* 300 */     switch (this.size) {
/*     */     case 1:
/* 302 */       return 0;
/*     */     case 2:
/* 304 */       return 1;
/*     */     }
/*     */ 
/* 308 */     return this.maxHeap.compareElements(1, 2) <= 0 ? 1 : 2;
/*     */   }
/*     */ 
/*     */   public E pollFirst()
/*     */   {
/* 317 */     return poll();
/*     */   }
/*     */ 
/*     */   public E removeFirst()
/*     */   {
/* 326 */     return remove();
/*     */   }
/*     */ 
/*     */   public E peekFirst()
/*     */   {
/* 334 */     return peek();
/*     */   }
/*     */ 
/*     */   public E pollLast()
/*     */   {
/* 342 */     return isEmpty() ? null : removeAndGet(getMaxElementIndex());
/*     */   }
/*     */ 
/*     */   public E removeLast()
/*     */   {
/* 351 */     if (isEmpty()) {
/* 352 */       throw new NoSuchElementException();
/*     */     }
/* 354 */     return removeAndGet(getMaxElementIndex());
/*     */   }
/*     */ 
/*     */   public E peekLast()
/*     */   {
/* 362 */     return isEmpty() ? null : elementData(getMaxElementIndex());
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   MoveDesc<E> removeAt(int index)
/*     */   {
/* 381 */     Preconditions.checkPositionIndex(index, this.size);
/* 382 */     this.modCount += 1;
/* 383 */     this.size -= 1;
/* 384 */     if (this.size == index) {
/* 385 */       this.queue[this.size] = null;
/* 386 */       return null;
/*     */     }
/* 388 */     Object actualLastElement = elementData(this.size);
/* 389 */     int lastElementAt = heapForIndex(this.size).getCorrectLastElement(actualLastElement);
/*     */ 
/* 391 */     Object toTrickle = elementData(this.size);
/* 392 */     this.queue[this.size] = null;
/* 393 */     MoveDesc changes = fillHole(index, toTrickle);
/* 394 */     if (lastElementAt < index)
/*     */     {
/* 396 */       if (changes == null)
/*     */       {
/* 398 */         return new MoveDesc(actualLastElement, toTrickle);
/*     */       }
/*     */ 
/* 402 */       return new MoveDesc(actualLastElement, changes.replaced);
/*     */     }
/*     */ 
/* 406 */     return changes;
/*     */   }
/*     */ 
/*     */   private MoveDesc<E> fillHole(int index, E toTrickle) {
/* 410 */     Heap heap = heapForIndex(index);
/*     */ 
/* 418 */     int vacated = heap.fillHoleAt(index);
/*     */ 
/* 420 */     int bubbledTo = heap.bubbleUpAlternatingLevels(vacated, toTrickle);
/* 421 */     if (bubbledTo == vacated)
/*     */     {
/* 425 */       return heap.tryCrossOverAndBubbleUp(index, vacated, toTrickle);
/*     */     }
/* 427 */     return bubbledTo < index ? new MoveDesc(toTrickle, elementData(index)) : null;
/*     */   }
/*     */ 
/*     */   private E removeAndGet(int index)
/*     */   {
/* 448 */     Object value = elementData(index);
/* 449 */     removeAt(index);
/* 450 */     return value;
/*     */   }
/*     */ 
/*     */   private MinMaxPriorityQueue<E>.Heap heapForIndex(int i) {
/* 454 */     return isEvenLevel(i) ? this.minHeap : this.maxHeap;
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static boolean isEvenLevel(int index)
/*     */   {
/* 461 */     int oneBased = index + 1;
/* 462 */     Preconditions.checkState(oneBased > 0, "negative index");
/* 463 */     return (oneBased & 0x55555555) > (oneBased & 0xAAAAAAAA);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   boolean isIntact()
/*     */   {
/* 473 */     for (int i = 1; i < this.size; i++) {
/* 474 */       if (!heapForIndex(i).verifyIndex(i)) {
/* 475 */         return false;
/*     */       }
/*     */     }
/* 478 */     return true;
/*     */   }
/*     */ 
/*     */   public Iterator<E> iterator()
/*     */   {
/* 867 */     return new QueueIterator(null);
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 871 */     for (int i = 0; i < this.size; i++) {
/* 872 */       this.queue[i] = null;
/*     */     }
/* 874 */     this.size = 0;
/*     */   }
/*     */ 
/*     */   public Object[] toArray() {
/* 878 */     Object[] copyTo = new Object[this.size];
/* 879 */     System.arraycopy(this.queue, 0, copyTo, 0, this.size);
/* 880 */     return copyTo;
/*     */   }
/*     */ 
/*     */   public Comparator<? super E> comparator()
/*     */   {
/* 889 */     return this.minHeap.ordering;
/*     */   }
/*     */   @VisibleForTesting
/*     */   int capacity() {
/* 893 */     return this.queue.length;
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int initialQueueSize(int configuredExpectedSize, int maximumSize, Iterable<?> initialContents)
/*     */   {
/* 903 */     int result = configuredExpectedSize == -1 ? 11 : configuredExpectedSize;
/*     */ 
/* 908 */     if ((initialContents instanceof Collection)) {
/* 909 */       int initialSize = ((Collection)initialContents).size();
/* 910 */       result = Math.max(result, initialSize);
/*     */     }
/*     */ 
/* 914 */     return capAtMaximumSize(result, maximumSize);
/*     */   }
/*     */ 
/*     */   private void growIfNeeded() {
/* 918 */     if (this.size > this.queue.length) {
/* 919 */       int newCapacity = calculateNewCapacity();
/* 920 */       Object[] newQueue = new Object[newCapacity];
/* 921 */       System.arraycopy(this.queue, 0, newQueue, 0, this.queue.length);
/* 922 */       this.queue = newQueue;
/*     */     }
/*     */   }
/*     */ 
/*     */   private int calculateNewCapacity()
/*     */   {
/* 928 */     int oldCapacity = this.queue.length;
/* 929 */     int newCapacity = oldCapacity < 64 ? (oldCapacity + 1) * 2 : IntMath.checkedMultiply(oldCapacity / 2, 3);
/*     */ 
/* 932 */     return capAtMaximumSize(newCapacity, this.maximumSize);
/*     */   }
/*     */ 
/*     */   private static int capAtMaximumSize(int queueSize, int maximumSize)
/*     */   {
/* 937 */     return Math.min(queueSize - 1, maximumSize) + 1;
/*     */   }
/*     */ 
/*     */   private class QueueIterator
/*     */     implements Iterator<E>
/*     */   {
/* 748 */     private int cursor = -1;
/* 749 */     private int expectedModCount = MinMaxPriorityQueue.this.modCount;
/*     */     private Queue<E> forgetMeNot;
/*     */     private List<E> skipMe;
/*     */     private E lastFromForgetMeNot;
/*     */     private boolean canRemove;
/*     */ 
/*     */     private QueueIterator()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 756 */       checkModCount();
/* 757 */       return (nextNotInSkipMe(this.cursor + 1) < MinMaxPriorityQueue.this.size()) || ((this.forgetMeNot != null) && (!this.forgetMeNot.isEmpty()));
/*     */     }
/*     */ 
/*     */     public E next()
/*     */     {
/* 762 */       checkModCount();
/* 763 */       int tempCursor = nextNotInSkipMe(this.cursor + 1);
/* 764 */       if (tempCursor < MinMaxPriorityQueue.this.size()) {
/* 765 */         this.cursor = tempCursor;
/* 766 */         this.canRemove = true;
/* 767 */         return MinMaxPriorityQueue.this.elementData(this.cursor);
/* 768 */       }if (this.forgetMeNot != null) {
/* 769 */         this.cursor = MinMaxPriorityQueue.this.size();
/* 770 */         this.lastFromForgetMeNot = this.forgetMeNot.poll();
/* 771 */         if (this.lastFromForgetMeNot != null) {
/* 772 */           this.canRemove = true;
/* 773 */           return this.lastFromForgetMeNot;
/*     */         }
/*     */       }
/* 776 */       throw new NoSuchElementException("iterator moved past last element in queue.");
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 781 */       Preconditions.checkState(this.canRemove, "no calls to remove() since the last call to next()");
/*     */ 
/* 783 */       checkModCount();
/* 784 */       this.canRemove = false;
/* 785 */       this.expectedModCount += 1;
/* 786 */       if (this.cursor < MinMaxPriorityQueue.this.size()) {
/* 787 */         MinMaxPriorityQueue.MoveDesc moved = MinMaxPriorityQueue.this.removeAt(this.cursor);
/* 788 */         if (moved != null) {
/* 789 */           if (this.forgetMeNot == null) {
/* 790 */             this.forgetMeNot = new ArrayDeque();
/* 791 */             this.skipMe = new ArrayList(3);
/*     */           }
/* 793 */           this.forgetMeNot.add(moved.toTrickle);
/* 794 */           this.skipMe.add(moved.replaced);
/*     */         }
/* 796 */         this.cursor -= 1;
/*     */       } else {
/* 798 */         Preconditions.checkState(removeExact(this.lastFromForgetMeNot));
/* 799 */         this.lastFromForgetMeNot = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     private boolean containsExact(Iterable<E> elements, E target)
/*     */     {
/* 805 */       for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 806 */         if (element == target) {
/* 807 */           return true;
/*     */         }
/*     */       }
/* 810 */       return false;
/*     */     }
/*     */ 
/*     */     boolean removeExact(Object target)
/*     */     {
/* 815 */       for (int i = 0; i < MinMaxPriorityQueue.this.size; i++) {
/* 816 */         if (MinMaxPriorityQueue.this.queue[i] == target) {
/* 817 */           MinMaxPriorityQueue.this.removeAt(i);
/* 818 */           return true;
/*     */         }
/*     */       }
/* 821 */       return false;
/*     */     }
/*     */ 
/*     */     void checkModCount() {
/* 825 */       if (MinMaxPriorityQueue.this.modCount != this.expectedModCount)
/* 826 */         throw new ConcurrentModificationException();
/*     */     }
/*     */ 
/*     */     private int nextNotInSkipMe(int c)
/*     */     {
/* 835 */       if (this.skipMe != null) {
/* 836 */         while ((c < MinMaxPriorityQueue.this.size()) && (containsExact(this.skipMe, MinMaxPriorityQueue.this.elementData(c)))) {
/* 837 */           c++;
/*     */         }
/*     */       }
/* 840 */       return c;
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Heap
/*     */   {
/*     */     final Ordering<E> ordering;
/*     */     MinMaxPriorityQueue<E>.Heap otherHeap;
/*     */ 
/*     */     Heap()
/*     */     {
/* 492 */       this.ordering = ordering;
/*     */     }
/*     */ 
/*     */     int compareElements(int a, int b) {
/* 496 */       return this.ordering.compare(MinMaxPriorityQueue.this.elementData(a), MinMaxPriorityQueue.this.elementData(b));
/*     */     }
/*     */ 
/*     */     MinMaxPriorityQueue.MoveDesc<E> tryCrossOverAndBubbleUp(int removeIndex, int vacated, E toTrickle)
/*     */     {
/* 506 */       int crossOver = crossOver(vacated, toTrickle);
/* 507 */       if (crossOver == vacated)
/* 508 */         return null;
/*     */       Object parent;
/*     */       Object parent;
/* 516 */       if (crossOver < removeIndex)
/*     */       {
/* 519 */         parent = MinMaxPriorityQueue.this.elementData(removeIndex);
/*     */       }
/* 521 */       else parent = MinMaxPriorityQueue.this.elementData(getParentIndex(removeIndex));
/*     */ 
/* 524 */       if (this.otherHeap.bubbleUpAlternatingLevels(crossOver, toTrickle) < removeIndex)
/*     */       {
/* 526 */         return new MinMaxPriorityQueue.MoveDesc(toTrickle, parent);
/*     */       }
/* 528 */       return null;
/*     */     }
/*     */ 
/*     */     void bubbleUp(int index, E x)
/*     */     {
/* 536 */       int crossOver = crossOverUp(index, x);
/*     */       Heap heap;
/*     */       Heap heap;
/* 539 */       if (crossOver == index) {
/* 540 */         heap = this;
/*     */       } else {
/* 542 */         index = crossOver;
/* 543 */         heap = this.otherHeap;
/*     */       }
/* 545 */       heap.bubbleUpAlternatingLevels(index, x);
/*     */     }
/*     */ 
/*     */     int bubbleUpAlternatingLevels(int index, E x)
/*     */     {
/* 553 */       while (index > 2) {
/* 554 */         int grandParentIndex = getGrandparentIndex(index);
/* 555 */         Object e = MinMaxPriorityQueue.this.elementData(grandParentIndex);
/* 556 */         if (this.ordering.compare(e, x) <= 0) {
/*     */           break;
/*     */         }
/* 559 */         MinMaxPriorityQueue.this.queue[index] = e;
/* 560 */         index = grandParentIndex;
/*     */       }
/* 562 */       MinMaxPriorityQueue.this.queue[index] = x;
/* 563 */       return index;
/*     */     }
/*     */ 
/*     */     int findMin(int index, int len)
/*     */     {
/* 572 */       if (index >= MinMaxPriorityQueue.this.size) {
/* 573 */         return -1;
/*     */       }
/* 575 */       Preconditions.checkState(index > 0);
/* 576 */       int limit = Math.min(index, MinMaxPriorityQueue.this.size - len) + len;
/* 577 */       int minIndex = index;
/* 578 */       for (int i = index + 1; i < limit; i++) {
/* 579 */         if (compareElements(i, minIndex) < 0) {
/* 580 */           minIndex = i;
/*     */         }
/*     */       }
/* 583 */       return minIndex;
/*     */     }
/*     */ 
/*     */     int findMinChild(int index)
/*     */     {
/* 590 */       return findMin(getLeftChildIndex(index), 2);
/*     */     }
/*     */ 
/*     */     int findMinGrandChild(int index)
/*     */     {
/* 597 */       int leftChildIndex = getLeftChildIndex(index);
/* 598 */       if (leftChildIndex < 0) {
/* 599 */         return -1;
/*     */       }
/* 601 */       return findMin(getLeftChildIndex(leftChildIndex), 4);
/*     */     }
/*     */ 
/*     */     int crossOverUp(int index, E x)
/*     */     {
/* 610 */       if (index == 0) {
/* 611 */         MinMaxPriorityQueue.this.queue[0] = x;
/* 612 */         return 0;
/*     */       }
/* 614 */       int parentIndex = getParentIndex(index);
/* 615 */       Object parentElement = MinMaxPriorityQueue.this.elementData(parentIndex);
/* 616 */       if (parentIndex != 0)
/*     */       {
/* 621 */         int grandparentIndex = getParentIndex(parentIndex);
/* 622 */         int uncleIndex = getRightChildIndex(grandparentIndex);
/* 623 */         if ((uncleIndex != parentIndex) && (getLeftChildIndex(uncleIndex) >= MinMaxPriorityQueue.this.size))
/*     */         {
/* 625 */           Object uncleElement = MinMaxPriorityQueue.this.elementData(uncleIndex);
/* 626 */           if (this.ordering.compare(uncleElement, parentElement) < 0) {
/* 627 */             parentIndex = uncleIndex;
/* 628 */             parentElement = uncleElement;
/*     */           }
/*     */         }
/*     */       }
/* 632 */       if (this.ordering.compare(parentElement, x) < 0) {
/* 633 */         MinMaxPriorityQueue.this.queue[index] = parentElement;
/* 634 */         MinMaxPriorityQueue.this.queue[parentIndex] = x;
/* 635 */         return parentIndex;
/*     */       }
/* 637 */       MinMaxPriorityQueue.this.queue[index] = x;
/* 638 */       return index;
/*     */     }
/*     */ 
/*     */     int getCorrectLastElement(E actualLastElement)
/*     */     {
/* 651 */       int parentIndex = getParentIndex(MinMaxPriorityQueue.this.size);
/* 652 */       if (parentIndex != 0) {
/* 653 */         int grandparentIndex = getParentIndex(parentIndex);
/* 654 */         int uncleIndex = getRightChildIndex(grandparentIndex);
/* 655 */         if ((uncleIndex != parentIndex) && (getLeftChildIndex(uncleIndex) >= MinMaxPriorityQueue.this.size))
/*     */         {
/* 657 */           Object uncleElement = MinMaxPriorityQueue.this.elementData(uncleIndex);
/* 658 */           if (this.ordering.compare(uncleElement, actualLastElement) < 0) {
/* 659 */             MinMaxPriorityQueue.this.queue[uncleIndex] = actualLastElement;
/* 660 */             MinMaxPriorityQueue.this.queue[MinMaxPriorityQueue.this.size] = uncleElement;
/* 661 */             return uncleIndex;
/*     */           }
/*     */         }
/*     */       }
/* 665 */       return MinMaxPriorityQueue.this.size;
/*     */     }
/*     */ 
/*     */     int crossOver(int index, E x)
/*     */     {
/* 675 */       int minChildIndex = findMinChild(index);
/*     */ 
/* 678 */       if ((minChildIndex > 0) && (this.ordering.compare(MinMaxPriorityQueue.this.elementData(minChildIndex), x) < 0))
/*     */       {
/* 680 */         MinMaxPriorityQueue.this.queue[index] = MinMaxPriorityQueue.this.elementData(minChildIndex);
/* 681 */         MinMaxPriorityQueue.this.queue[minChildIndex] = x;
/* 682 */         return minChildIndex;
/*     */       }
/* 684 */       return crossOverUp(index, x);
/*     */     }
/*     */ 
/*     */     int fillHoleAt(int index)
/*     */     {
/*     */       int minGrandchildIndex;
/* 697 */       while ((minGrandchildIndex = findMinGrandChild(index)) > 0) {
/* 698 */         MinMaxPriorityQueue.this.queue[index] = MinMaxPriorityQueue.this.elementData(minGrandchildIndex);
/* 699 */         index = minGrandchildIndex;
/*     */       }
/* 701 */       return index;
/*     */     }
/*     */ 
/*     */     private boolean verifyIndex(int i) {
/* 705 */       if ((getLeftChildIndex(i) < MinMaxPriorityQueue.this.size) && (compareElements(i, getLeftChildIndex(i)) > 0))
/*     */       {
/* 707 */         return false;
/*     */       }
/* 709 */       if ((getRightChildIndex(i) < MinMaxPriorityQueue.this.size) && (compareElements(i, getRightChildIndex(i)) > 0))
/*     */       {
/* 711 */         return false;
/*     */       }
/* 713 */       if ((i > 0) && (compareElements(i, getParentIndex(i)) > 0)) {
/* 714 */         return false;
/*     */       }
/* 716 */       if ((i > 2) && (compareElements(getGrandparentIndex(i), i) > 0)) {
/* 717 */         return false;
/*     */       }
/* 719 */       return true;
/*     */     }
/*     */ 
/*     */     private int getLeftChildIndex(int i)
/*     */     {
/* 725 */       return i * 2 + 1;
/*     */     }
/*     */ 
/*     */     private int getRightChildIndex(int i) {
/* 729 */       return i * 2 + 2;
/*     */     }
/*     */ 
/*     */     private int getParentIndex(int i) {
/* 733 */       return (i - 1) / 2;
/*     */     }
/*     */ 
/*     */     private int getGrandparentIndex(int i) {
/* 737 */       return getParentIndex(getParentIndex(i));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class MoveDesc<E>
/*     */   {
/*     */     final E toTrickle;
/*     */     final E replaced;
/*     */ 
/*     */     MoveDesc(E toTrickle, E replaced)
/*     */     {
/* 439 */       this.toTrickle = toTrickle;
/* 440 */       this.replaced = replaced;
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static final class Builder<B>
/*     */   {
/*     */     private static final int UNSET_EXPECTED_SIZE = -1;
/*     */     private final Comparator<B> comparator;
/* 162 */     private int expectedSize = -1;
/* 163 */     private int maximumSize = 2147483647;
/*     */ 
/*     */     private Builder(Comparator<B> comparator) {
/* 166 */       this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */     }
/*     */ 
/*     */     public Builder<B> expectedSize(int expectedSize)
/*     */     {
/* 174 */       Preconditions.checkArgument(expectedSize >= 0);
/* 175 */       this.expectedSize = expectedSize;
/* 176 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<B> maximumSize(int maximumSize)
/*     */     {
/* 186 */       Preconditions.checkArgument(maximumSize > 0);
/* 187 */       this.maximumSize = maximumSize;
/* 188 */       return this;
/*     */     }
/*     */ 
/*     */     public <T extends B> MinMaxPriorityQueue<T> create()
/*     */     {
/* 196 */       return create(Collections.emptySet());
/*     */     }
/*     */ 
/*     */     public <T extends B> MinMaxPriorityQueue<T> create(Iterable<? extends T> initialContents)
/*     */     {
/* 205 */       MinMaxPriorityQueue queue = new MinMaxPriorityQueue(this, MinMaxPriorityQueue.initialQueueSize(this.expectedSize, this.maximumSize, initialContents), null);
/*     */ 
/* 207 */       for (Iterator i$ = initialContents.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 208 */         queue.offer(element);
/*     */       }
/* 210 */       return queue;
/*     */     }
/*     */ 
/*     */     private <T extends B> Ordering<T> ordering()
/*     */     {
/* 215 */       return Ordering.from(this.comparator);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.MinMaxPriorityQueue
 * JD-Core Version:    0.6.2
 */