/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.annotations.VisibleForTesting;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Objects;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.primitives.Ints;
/*      */ import java.io.Serializable;
/*      */ import java.util.AbstractList;
/*      */ import java.util.AbstractSequentialList;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedList;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.RandomAccess;
/*      */ import java.util.concurrent.CopyOnWriteArrayList;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Lists
/*      */ {
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> ArrayList<E> newArrayList()
/*      */   {
/*   80 */     return new ArrayList();
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> ArrayList<E> newArrayList(E[] elements)
/*      */   {
/*   96 */     Preconditions.checkNotNull(elements);
/*      */ 
/*   98 */     int capacity = computeArrayListCapacity(elements.length);
/*   99 */     ArrayList list = new ArrayList(capacity);
/*  100 */     Collections.addAll(list, elements);
/*  101 */     return list;
/*      */   }
/*      */   @VisibleForTesting
/*      */   static int computeArrayListCapacity(int arraySize) {
/*  105 */     Preconditions.checkArgument(arraySize >= 0);
/*      */ 
/*  108 */     return Ints.saturatedCast(5L + arraySize + arraySize / 10);
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements)
/*      */   {
/*  123 */     Preconditions.checkNotNull(elements);
/*      */ 
/*  125 */     return (elements instanceof Collection) ? new ArrayList(Collections2.cast(elements)) : newArrayList(elements.iterator());
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements)
/*      */   {
/*  142 */     Preconditions.checkNotNull(elements);
/*  143 */     ArrayList list = newArrayList();
/*  144 */     while (elements.hasNext()) {
/*  145 */       list.add(elements.next());
/*      */     }
/*  147 */     return list;
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize)
/*      */   {
/*  173 */     Preconditions.checkArgument(initialArraySize >= 0);
/*  174 */     return new ArrayList(initialArraySize);
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> ArrayList<E> newArrayListWithExpectedSize(int estimatedSize)
/*      */   {
/*  195 */     return new ArrayList(computeArrayListCapacity(estimatedSize));
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> LinkedList<E> newLinkedList()
/*      */   {
/*  210 */     return new LinkedList();
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <E> LinkedList<E> newLinkedList(Iterable<? extends E> elements)
/*      */   {
/*  222 */     LinkedList list = newLinkedList();
/*  223 */     for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/*  224 */       list.add(element);
/*      */     }
/*  226 */     return list;
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   @GwtIncompatible("CopyOnWriteArrayList")
/*      */   public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList()
/*      */   {
/*  241 */     return new CopyOnWriteArrayList();
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   @GwtIncompatible("CopyOnWriteArrayList")
/*      */   public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList(Iterable<? extends E> elements)
/*      */   {
/*  257 */     Collection elementsCollection = (elements instanceof Collection) ? Collections2.cast(elements) : newArrayList(elements);
/*      */ 
/*  260 */     return new CopyOnWriteArrayList(elementsCollection);
/*      */   }
/*      */ 
/*      */   public static <E> List<E> asList(@Nullable E first, E[] rest)
/*      */   {
/*  280 */     return new OnePlusArrayList(first, rest);
/*      */   }
/*      */ 
/*      */   public static <E> List<E> asList(@Nullable E first, @Nullable E second, E[] rest)
/*      */   {
/*  323 */     return new TwoPlusArrayList(first, second, rest);
/*      */   }
/*      */ 
/*      */   public static <F, T> List<T> transform(List<F> fromList, Function<? super F, ? extends T> function)
/*      */   {
/*  385 */     return (fromList instanceof RandomAccess) ? new TransformingRandomAccessList(fromList, function) : new TransformingSequentialList(fromList, function);
/*      */   }
/*      */ 
/*      */   public static <T> List<List<T>> partition(List<T> list, int size)
/*      */   {
/*  524 */     Preconditions.checkNotNull(list);
/*  525 */     Preconditions.checkArgument(size > 0);
/*  526 */     return (list instanceof RandomAccess) ? new RandomAccessPartition(list, size) : new Partition(list, size);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static ImmutableList<Character> charactersOf(String string)
/*      */   {
/*  576 */     return new StringAsImmutableList((String)Preconditions.checkNotNull(string));
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static List<Character> charactersOf(CharSequence sequence)
/*      */   {
/*  665 */     return new CharSequenceAsList((CharSequence)Preconditions.checkNotNull(sequence));
/*      */   }
/*      */ 
/*      */   public static <T> List<T> reverse(List<T> list)
/*      */   {
/*  760 */     if ((list instanceof ReverseList))
/*  761 */       return ((ReverseList)list).getForwardList();
/*  762 */     if ((list instanceof RandomAccess)) {
/*  763 */       return new RandomAccessReverseList(list);
/*      */     }
/*  765 */     return new ReverseList(list);
/*      */   }
/*      */ 
/*      */   static int hashCodeImpl(List<?> list)
/*      */   {
/*  923 */     int hashCode = 1;
/*  924 */     for (Iterator i$ = list.iterator(); i$.hasNext(); ) { Object o = i$.next();
/*  925 */       hashCode = 31 * hashCode + (o == null ? 0 : o.hashCode());
/*      */     }
/*  927 */     return hashCode;
/*      */   }
/*      */ 
/*      */   static boolean equalsImpl(List<?> list, @Nullable Object object)
/*      */   {
/*  934 */     if (object == Preconditions.checkNotNull(list)) {
/*  935 */       return true;
/*      */     }
/*  937 */     if (!(object instanceof List)) {
/*  938 */       return false;
/*      */     }
/*      */ 
/*  941 */     List o = (List)object;
/*      */ 
/*  943 */     return (list.size() == o.size()) && (Iterators.elementsEqual(list.iterator(), o.iterator()));
/*      */   }
/*      */ 
/*      */   static <E> boolean addAllImpl(List<E> list, int index, Iterable<? extends E> elements)
/*      */   {
/*  952 */     boolean changed = false;
/*  953 */     ListIterator listIterator = list.listIterator(index);
/*  954 */     for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object e = i$.next();
/*  955 */       listIterator.add(e);
/*  956 */       changed = true;
/*      */     }
/*  958 */     return changed;
/*      */   }
/*      */ 
/*      */   static int indexOfImpl(List<?> list, @Nullable Object element)
/*      */   {
/*  965 */     ListIterator listIterator = list.listIterator();
/*  966 */     while (listIterator.hasNext()) {
/*  967 */       if (Objects.equal(element, listIterator.next())) {
/*  968 */         return listIterator.previousIndex();
/*      */       }
/*      */     }
/*  971 */     return -1;
/*      */   }
/*      */ 
/*      */   static int lastIndexOfImpl(List<?> list, @Nullable Object element)
/*      */   {
/*  978 */     ListIterator listIterator = list.listIterator(list.size());
/*  979 */     while (listIterator.hasPrevious()) {
/*  980 */       if (Objects.equal(element, listIterator.previous())) {
/*  981 */         return listIterator.nextIndex();
/*      */       }
/*      */     }
/*  984 */     return -1;
/*      */   }
/*      */ 
/*      */   static <E> ListIterator<E> listIteratorImpl(List<E> list, int index)
/*      */   {
/*  991 */     return new AbstractListWrapper(list).listIterator(index);
/*      */   }
/*      */ 
/*      */   static <E> List<E> subListImpl(List<E> list, int fromIndex, int toIndex)
/*      */   {
/*      */     List wrapper;
/*      */     List wrapper;
/* 1000 */     if ((list instanceof RandomAccess)) {
/* 1001 */       wrapper = new RandomAccessListWrapper(list) { private static final long serialVersionUID = 0L;
/*      */ 
/* 1003 */         public ListIterator<E> listIterator(int index) { return this.backingList.listIterator(index); }
/*      */ 
/*      */       };
/*      */     }
/*      */     else
/*      */     {
/* 1009 */       wrapper = new AbstractListWrapper(list) { private static final long serialVersionUID = 0L;
/*      */ 
/* 1011 */         public ListIterator<E> listIterator(int index) { return this.backingList.listIterator(index); }
/*      */ 
/*      */ 
/*      */       };
/*      */     }
/*      */ 
/* 1017 */     return wrapper.subList(fromIndex, toIndex);
/*      */   }
/*      */ 
/*      */   static <T> List<T> cast(Iterable<T> iterable)
/*      */   {
/* 1067 */     return (List)iterable;
/*      */   }
/*      */ 
/*      */   private static class RandomAccessListWrapper<E> extends Lists.AbstractListWrapper<E>
/*      */     implements RandomAccess
/*      */   {
/*      */     RandomAccessListWrapper(List<E> backingList)
/*      */     {
/* 1059 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class AbstractListWrapper<E> extends AbstractList<E>
/*      */   {
/*      */     final List<E> backingList;
/*      */ 
/*      */     AbstractListWrapper(List<E> backingList)
/*      */     {
/* 1024 */       this.backingList = ((List)Preconditions.checkNotNull(backingList));
/*      */     }
/*      */ 
/*      */     public void add(int index, E element) {
/* 1028 */       this.backingList.add(index, element);
/*      */     }
/*      */ 
/*      */     public boolean addAll(int index, Collection<? extends E> c) {
/* 1032 */       return this.backingList.addAll(index, c);
/*      */     }
/*      */ 
/*      */     public E get(int index) {
/* 1036 */       return this.backingList.get(index);
/*      */     }
/*      */ 
/*      */     public E remove(int index) {
/* 1040 */       return this.backingList.remove(index);
/*      */     }
/*      */ 
/*      */     public E set(int index, E element) {
/* 1044 */       return this.backingList.set(index, element);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/* 1048 */       return this.backingList.contains(o);
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1052 */       return this.backingList.size();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class RandomAccessReverseList<T> extends Lists.ReverseList<T>
/*      */     implements RandomAccess
/*      */   {
/*      */     RandomAccessReverseList(List<T> forwardList)
/*      */     {
/*  915 */       super();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class ReverseList<T> extends AbstractList<T>
/*      */   {
/*      */     private final List<T> forwardList;
/*      */ 
/*      */     ReverseList(List<T> forwardList)
/*      */     {
/*  773 */       this.forwardList = ((List)Preconditions.checkNotNull(forwardList));
/*      */     }
/*      */ 
/*      */     List<T> getForwardList() {
/*  777 */       return this.forwardList;
/*      */     }
/*      */ 
/*      */     private int reverseIndex(int index) {
/*  781 */       int size = size();
/*  782 */       Preconditions.checkElementIndex(index, size);
/*  783 */       return size - 1 - index;
/*      */     }
/*      */ 
/*      */     private int reversePosition(int index) {
/*  787 */       int size = size();
/*  788 */       Preconditions.checkPositionIndex(index, size);
/*  789 */       return size - index;
/*      */     }
/*      */ 
/*      */     public void add(int index, @Nullable T element) {
/*  793 */       this.forwardList.add(reversePosition(index), element);
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  797 */       this.forwardList.clear();
/*      */     }
/*      */ 
/*      */     public T remove(int index) {
/*  801 */       return this.forwardList.remove(reverseIndex(index));
/*      */     }
/*      */ 
/*      */     protected void removeRange(int fromIndex, int toIndex) {
/*  805 */       subList(fromIndex, toIndex).clear();
/*      */     }
/*      */ 
/*      */     public T set(int index, @Nullable T element) {
/*  809 */       return this.forwardList.set(reverseIndex(index), element);
/*      */     }
/*      */ 
/*      */     public T get(int index) {
/*  813 */       return this.forwardList.get(reverseIndex(index));
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/*  817 */       return this.forwardList.isEmpty();
/*      */     }
/*      */ 
/*      */     public int size() {
/*  821 */       return this.forwardList.size();
/*      */     }
/*      */ 
/*      */     public boolean contains(@Nullable Object o) {
/*  825 */       return this.forwardList.contains(o);
/*      */     }
/*      */ 
/*      */     public boolean containsAll(Collection<?> c) {
/*  829 */       return this.forwardList.containsAll(c);
/*      */     }
/*      */ 
/*      */     public List<T> subList(int fromIndex, int toIndex) {
/*  833 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size());
/*  834 */       return Lists.reverse(this.forwardList.subList(reversePosition(toIndex), reversePosition(fromIndex)));
/*      */     }
/*      */ 
/*      */     public int indexOf(@Nullable Object o)
/*      */     {
/*  839 */       int index = this.forwardList.lastIndexOf(o);
/*  840 */       return index >= 0 ? reverseIndex(index) : -1;
/*      */     }
/*      */ 
/*      */     public int lastIndexOf(@Nullable Object o) {
/*  844 */       int index = this.forwardList.indexOf(o);
/*  845 */       return index >= 0 ? reverseIndex(index) : -1;
/*      */     }
/*      */ 
/*      */     public Iterator<T> iterator() {
/*  849 */       return listIterator();
/*      */     }
/*      */ 
/*      */     public ListIterator<T> listIterator(int index) {
/*  853 */       int start = reversePosition(index);
/*  854 */       final ListIterator forwardIterator = this.forwardList.listIterator(start);
/*  855 */       return new ListIterator() {
/*      */         boolean canRemove;
/*      */         boolean canSet;
/*      */ 
/*      */         public void add(T e) {
/*  861 */           forwardIterator.add(e);
/*  862 */           forwardIterator.previous();
/*  863 */           this.canSet = (this.canRemove = 0);
/*      */         }
/*      */ 
/*      */         public boolean hasNext() {
/*  867 */           return forwardIterator.hasPrevious();
/*      */         }
/*      */ 
/*      */         public boolean hasPrevious() {
/*  871 */           return forwardIterator.hasNext();
/*      */         }
/*      */ 
/*      */         public T next() {
/*  875 */           if (!hasNext()) {
/*  876 */             throw new NoSuchElementException();
/*      */           }
/*  878 */           this.canSet = (this.canRemove = 1);
/*  879 */           return forwardIterator.previous();
/*      */         }
/*      */ 
/*      */         public int nextIndex() {
/*  883 */           return Lists.ReverseList.this.reversePosition(forwardIterator.nextIndex());
/*      */         }
/*      */ 
/*      */         public T previous() {
/*  887 */           if (!hasPrevious()) {
/*  888 */             throw new NoSuchElementException();
/*      */           }
/*  890 */           this.canSet = (this.canRemove = 1);
/*  891 */           return forwardIterator.next();
/*      */         }
/*      */ 
/*      */         public int previousIndex() {
/*  895 */           return nextIndex() - 1;
/*      */         }
/*      */ 
/*      */         public void remove() {
/*  899 */           Preconditions.checkState(this.canRemove);
/*  900 */           forwardIterator.remove();
/*  901 */           this.canRemove = (this.canSet = 0);
/*      */         }
/*      */ 
/*      */         public void set(T e) {
/*  905 */           Preconditions.checkState(this.canSet);
/*  906 */           forwardIterator.set(e);
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class CharSequenceAsList extends AbstractList<Character>
/*      */   {
/*      */     private final CharSequence sequence;
/*      */ 
/*      */     CharSequenceAsList(CharSequence sequence)
/*      */     {
/*  673 */       this.sequence = sequence;
/*      */     }
/*      */ 
/*      */     public Character get(int index) {
/*  677 */       Preconditions.checkElementIndex(index, size());
/*  678 */       return Character.valueOf(this.sequence.charAt(index));
/*      */     }
/*      */ 
/*      */     public boolean contains(@Nullable Object o) {
/*  682 */       return indexOf(o) >= 0;
/*      */     }
/*      */ 
/*      */     public int indexOf(@Nullable Object o) {
/*  686 */       if ((o instanceof Character)) {
/*  687 */         char c = ((Character)o).charValue();
/*  688 */         for (int i = 0; i < this.sequence.length(); i++) {
/*  689 */           if (this.sequence.charAt(i) == c) {
/*  690 */             return i;
/*      */           }
/*      */         }
/*      */       }
/*  694 */       return -1;
/*      */     }
/*      */ 
/*      */     public int lastIndexOf(@Nullable Object o) {
/*  698 */       if ((o instanceof Character)) {
/*  699 */         char c = ((Character)o).charValue();
/*  700 */         for (int i = this.sequence.length() - 1; i >= 0; i--) {
/*  701 */           if (this.sequence.charAt(i) == c) {
/*  702 */             return i;
/*      */           }
/*      */         }
/*      */       }
/*  706 */       return -1;
/*      */     }
/*      */ 
/*      */     public int size() {
/*  710 */       return this.sequence.length();
/*      */     }
/*      */ 
/*      */     public List<Character> subList(int fromIndex, int toIndex) {
/*  714 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size());
/*  715 */       return Lists.charactersOf(this.sequence.subSequence(fromIndex, toIndex));
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  719 */       int hash = 1;
/*  720 */       for (int i = 0; i < this.sequence.length(); i++) {
/*  721 */         hash = hash * 31 + this.sequence.charAt(i);
/*      */       }
/*  723 */       return hash;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object o) {
/*  727 */       if (!(o instanceof List)) {
/*  728 */         return false;
/*      */       }
/*  730 */       List list = (List)o;
/*  731 */       int n = this.sequence.length();
/*  732 */       if (n != list.size()) {
/*  733 */         return false;
/*      */       }
/*  735 */       Iterator iterator = list.iterator();
/*  736 */       for (int i = 0; i < n; i++) {
/*  737 */         Object elem = iterator.next();
/*  738 */         if ((!(elem instanceof Character)) || (((Character)elem).charValue() != this.sequence.charAt(i)))
/*      */         {
/*  740 */           return false;
/*      */         }
/*      */       }
/*  743 */       return true;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class StringAsImmutableList extends ImmutableList<Character>
/*      */   {
/*      */     private final String string;
/*  638 */     int hash = 0;
/*      */ 
/*      */     StringAsImmutableList(String string)
/*      */     {
/*  586 */       this.string = string;
/*      */     }
/*      */ 
/*      */     public int indexOf(@Nullable Object object) {
/*  590 */       return (object instanceof Character) ? this.string.indexOf(((Character)object).charValue()) : -1;
/*      */     }
/*      */ 
/*      */     public int lastIndexOf(@Nullable Object object)
/*      */     {
/*  595 */       return (object instanceof Character) ? this.string.lastIndexOf(((Character)object).charValue()) : -1;
/*      */     }
/*      */ 
/*      */     public ImmutableList<Character> subList(int fromIndex, int toIndex)
/*      */     {
/*  601 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size());
/*  602 */       return Lists.charactersOf(this.string.substring(fromIndex, toIndex));
/*      */     }
/*      */ 
/*      */     boolean isPartialView() {
/*  606 */       return false;
/*      */     }
/*      */ 
/*      */     public Character get(int index) {
/*  610 */       Preconditions.checkElementIndex(index, size());
/*  611 */       return Character.valueOf(this.string.charAt(index));
/*      */     }
/*      */ 
/*      */     public int size() {
/*  615 */       return this.string.length();
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object obj) {
/*  619 */       if (!(obj instanceof List)) {
/*  620 */         return false;
/*      */       }
/*  622 */       List list = (List)obj;
/*  623 */       int n = this.string.length();
/*  624 */       if (n != list.size()) {
/*  625 */         return false;
/*      */       }
/*  627 */       Iterator iterator = list.iterator();
/*  628 */       for (int i = 0; i < n; i++) {
/*  629 */         Object elem = iterator.next();
/*  630 */         if ((!(elem instanceof Character)) || (((Character)elem).charValue() != this.string.charAt(i)))
/*      */         {
/*  632 */           return false;
/*      */         }
/*      */       }
/*  635 */       return true;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/*  641 */       int h = this.hash;
/*  642 */       if (h == 0) {
/*  643 */         h = 1;
/*  644 */         for (int i = 0; i < this.string.length(); i++) {
/*  645 */           h = h * 31 + this.string.charAt(i);
/*      */         }
/*  647 */         this.hash = h;
/*      */       }
/*  649 */       return h;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class RandomAccessPartition<T> extends Lists.Partition<T>
/*      */     implements RandomAccess
/*      */   {
/*      */     RandomAccessPartition(List<T> list, int size)
/*      */     {
/*  565 */       super(size);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class Partition<T> extends AbstractList<List<T>>
/*      */   {
/*      */     final List<T> list;
/*      */     final int size;
/*      */ 
/*      */     Partition(List<T> list, int size)
/*      */     {
/*  536 */       this.list = list;
/*  537 */       this.size = size;
/*      */     }
/*      */ 
/*      */     public List<T> get(int index) {
/*  541 */       int listSize = size();
/*  542 */       Preconditions.checkElementIndex(index, listSize);
/*  543 */       int start = index * this.size;
/*  544 */       int end = Math.min(start + this.size, this.list.size());
/*  545 */       return this.list.subList(start, end);
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/*  550 */       int result = this.list.size() / this.size;
/*  551 */       if (result * this.size != this.list.size()) {
/*  552 */         result++;
/*      */       }
/*  554 */       return result;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/*  558 */       return this.list.isEmpty();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TransformingRandomAccessList<F, T> extends AbstractList<T>
/*      */     implements RandomAccess, Serializable
/*      */   {
/*      */     final List<F> fromList;
/*      */     final Function<? super F, ? extends T> function;
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     TransformingRandomAccessList(List<F> fromList, Function<? super F, ? extends T> function)
/*      */     {
/*  484 */       this.fromList = ((List)Preconditions.checkNotNull(fromList));
/*  485 */       this.function = ((Function)Preconditions.checkNotNull(function));
/*      */     }
/*      */     public void clear() {
/*  488 */       this.fromList.clear();
/*      */     }
/*      */     public T get(int index) {
/*  491 */       return this.function.apply(this.fromList.get(index));
/*      */     }
/*      */     public boolean isEmpty() {
/*  494 */       return this.fromList.isEmpty();
/*      */     }
/*      */     public T remove(int index) {
/*  497 */       return this.function.apply(this.fromList.remove(index));
/*      */     }
/*      */     public int size() {
/*  500 */       return this.fromList.size();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TransformingSequentialList<F, T> extends AbstractSequentialList<T>
/*      */     implements Serializable
/*      */   {
/*      */     final List<F> fromList;
/*      */     final Function<? super F, ? extends T> function;
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     TransformingSequentialList(List<F> fromList, Function<? super F, ? extends T> function)
/*      */     {
/*  402 */       this.fromList = ((List)Preconditions.checkNotNull(fromList));
/*  403 */       this.function = ((Function)Preconditions.checkNotNull(function));
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/*  411 */       this.fromList.clear();
/*      */     }
/*      */     public int size() {
/*  414 */       return this.fromList.size();
/*      */     }
/*      */     public ListIterator<T> listIterator(int index) {
/*  417 */       final ListIterator delegate = this.fromList.listIterator(index);
/*  418 */       return new ListIterator()
/*      */       {
/*      */         public void add(T e) {
/*  421 */           throw new UnsupportedOperationException();
/*      */         }
/*      */ 
/*      */         public boolean hasNext()
/*      */         {
/*  426 */           return delegate.hasNext();
/*      */         }
/*      */ 
/*      */         public boolean hasPrevious()
/*      */         {
/*  431 */           return delegate.hasPrevious();
/*      */         }
/*      */ 
/*      */         public T next()
/*      */         {
/*  436 */           return Lists.TransformingSequentialList.this.function.apply(delegate.next());
/*      */         }
/*      */ 
/*      */         public int nextIndex()
/*      */         {
/*  441 */           return delegate.nextIndex();
/*      */         }
/*      */ 
/*      */         public T previous()
/*      */         {
/*  446 */           return Lists.TransformingSequentialList.this.function.apply(delegate.previous());
/*      */         }
/*      */ 
/*      */         public int previousIndex()
/*      */         {
/*  451 */           return delegate.previousIndex();
/*      */         }
/*      */ 
/*      */         public void remove()
/*      */         {
/*  456 */           delegate.remove();
/*      */         }
/*      */ 
/*      */         public void set(T e)
/*      */         {
/*  461 */           throw new UnsupportedOperationException("not supported");
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TwoPlusArrayList<E> extends AbstractList<E>
/*      */     implements Serializable, RandomAccess
/*      */   {
/*      */     final E first;
/*      */     final E second;
/*      */     final E[] rest;
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     TwoPlusArrayList(@Nullable E first, @Nullable E second, E[] rest)
/*      */     {
/*  334 */       this.first = first;
/*  335 */       this.second = second;
/*  336 */       this.rest = ((Object[])Preconditions.checkNotNull(rest));
/*      */     }
/*      */     public int size() {
/*  339 */       return this.rest.length + 2;
/*      */     }
/*      */     public E get(int index) {
/*  342 */       switch (index) {
/*      */       case 0:
/*  344 */         return this.first;
/*      */       case 1:
/*  346 */         return this.second;
/*      */       }
/*      */ 
/*  349 */       Preconditions.checkElementIndex(index, size());
/*  350 */       return this.rest[(index - 2)];
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class OnePlusArrayList<E> extends AbstractList<E>
/*      */     implements Serializable, RandomAccess
/*      */   {
/*      */     final E first;
/*      */     final E[] rest;
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     OnePlusArrayList(@Nullable E first, E[] rest)
/*      */     {
/*  290 */       this.first = first;
/*  291 */       this.rest = ((Object[])Preconditions.checkNotNull(rest));
/*      */     }
/*      */     public int size() {
/*  294 */       return this.rest.length + 1;
/*      */     }
/*      */ 
/*      */     public E get(int index) {
/*  298 */       Preconditions.checkElementIndex(index, size());
/*  299 */       return index == 0 ? this.first : this.rest[(index - 1)];
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Lists
 * JD-Core Version:    0.6.2
 */