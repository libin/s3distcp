/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Multisets
/*     */ {
/* 954 */   private static final Ordering<Multiset.Entry<?>> DECREASING_COUNT_ORDERING = new Ordering()
/*     */   {
/*     */     public int compare(Multiset.Entry<?> entry1, Multiset.Entry<?> entry2) {
/* 957 */       return Ints.compare(entry2.getCount(), entry1.getCount());
/*     */     }
/* 954 */   };
/*     */ 
/*     */   public static <E> Multiset<E> unmodifiableMultiset(Multiset<? extends E> multiset)
/*     */   {
/*  73 */     if (((multiset instanceof UnmodifiableMultiset)) || ((multiset instanceof ImmutableMultiset)))
/*     */     {
/*  77 */       Multiset result = multiset;
/*  78 */       return result;
/*     */     }
/*  80 */     return new UnmodifiableMultiset((Multiset)Preconditions.checkNotNull(multiset));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> Multiset<E> unmodifiableMultiset(ImmutableMultiset<E> multiset)
/*     */   {
/*  91 */     return (Multiset)Preconditions.checkNotNull(multiset);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> SortedMultiset<E> unmodifiableSortedMultiset(SortedMultiset<E> sortedMultiset)
/*     */   {
/* 198 */     return new UnmodifiableSortedMultiset((SortedMultiset)Preconditions.checkNotNull(sortedMultiset), null);
/*     */   }
/*     */ 
/*     */   public static <E> Multiset.Entry<E> immutableEntry(@Nullable E e, int n)
/*     */   {
/* 293 */     return new ImmutableEntry(e, n);
/*     */   }
/*     */ 
/*     */   static <E> Multiset<E> forSet(Set<E> set)
/*     */   {
/* 338 */     return new SetMultiset(set);
/*     */   }
/*     */ 
/*     */   static int inferDistinctElements(Iterable<?> elements)
/*     */   {
/* 476 */     if ((elements instanceof Multiset)) {
/* 477 */       return ((Multiset)elements).elementSet().size();
/*     */     }
/* 479 */     return 11;
/*     */   }
/*     */ 
/*     */   public static <E> Multiset<E> intersection(Multiset<E> multiset1, final Multiset<?> multiset2)
/*     */   {
/* 497 */     Preconditions.checkNotNull(multiset1);
/* 498 */     Preconditions.checkNotNull(multiset2);
/*     */ 
/* 500 */     return new AbstractMultiset()
/*     */     {
/*     */       public int count(Object element) {
/* 503 */         int count1 = this.val$multiset1.count(element);
/* 504 */         return count1 == 0 ? 0 : Math.min(count1, multiset2.count(element));
/*     */       }
/*     */ 
/*     */       Set<E> createElementSet()
/*     */       {
/* 509 */         return Sets.intersection(this.val$multiset1.elementSet(), multiset2.elementSet());
/*     */       }
/*     */ 
/*     */       Iterator<Multiset.Entry<E>> entryIterator()
/*     */       {
/* 515 */         final Iterator iterator1 = this.val$multiset1.entrySet().iterator();
/* 516 */         return new AbstractIterator()
/*     */         {
/*     */           protected Multiset.Entry<E> computeNext() {
/* 519 */             while (iterator1.hasNext()) {
/* 520 */               Multiset.Entry entry1 = (Multiset.Entry)iterator1.next();
/* 521 */               Object element = entry1.getElement();
/* 522 */               int count = Math.min(entry1.getCount(), Multisets.1.this.val$multiset2.count(element));
/* 523 */               if (count > 0) {
/* 524 */                 return Multisets.immutableEntry(element, count);
/*     */               }
/*     */             }
/* 527 */             return (Multiset.Entry)endOfData();
/*     */           }
/*     */         };
/*     */       }
/*     */ 
/*     */       int distinctElements()
/*     */       {
/* 534 */         return elementSet().size();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static boolean containsOccurrences(Multiset<?> superMultiset, Multiset<?> subMultiset)
/*     */   {
/* 548 */     Preconditions.checkNotNull(superMultiset);
/* 549 */     Preconditions.checkNotNull(subMultiset);
/* 550 */     for (Multiset.Entry entry : subMultiset.entrySet()) {
/* 551 */       int superCount = superMultiset.count(entry.getElement());
/* 552 */       if (superCount < entry.getCount()) {
/* 553 */         return false;
/*     */       }
/*     */     }
/* 556 */     return true;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static boolean retainOccurrences(Multiset<?> multisetToModify, Multiset<?> multisetToRetain)
/*     */   {
/* 580 */     return retainOccurrencesImpl(multisetToModify, multisetToRetain);
/*     */   }
/*     */ 
/*     */   private static <E> boolean retainOccurrencesImpl(Multiset<E> multisetToModify, Multiset<?> occurrencesToRetain)
/*     */   {
/* 588 */     Preconditions.checkNotNull(multisetToModify);
/* 589 */     Preconditions.checkNotNull(occurrencesToRetain);
/*     */ 
/* 591 */     Iterator entryIterator = multisetToModify.entrySet().iterator();
/* 592 */     boolean changed = false;
/* 593 */     while (entryIterator.hasNext()) {
/* 594 */       Multiset.Entry entry = (Multiset.Entry)entryIterator.next();
/* 595 */       int retainCount = occurrencesToRetain.count(entry.getElement());
/* 596 */       if (retainCount == 0) {
/* 597 */         entryIterator.remove();
/* 598 */         changed = true;
/* 599 */       } else if (retainCount < entry.getCount()) {
/* 600 */         multisetToModify.setCount(entry.getElement(), retainCount);
/* 601 */         changed = true;
/*     */       }
/*     */     }
/* 604 */     return changed;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static boolean removeOccurrences(Multiset<?> multisetToModify, Multiset<?> occurrencesToRemove)
/*     */   {
/* 632 */     return removeOccurrencesImpl(multisetToModify, occurrencesToRemove);
/*     */   }
/*     */ 
/*     */   private static <E> boolean removeOccurrencesImpl(Multiset<E> multisetToModify, Multiset<?> occurrencesToRemove)
/*     */   {
/* 641 */     Preconditions.checkNotNull(multisetToModify);
/* 642 */     Preconditions.checkNotNull(occurrencesToRemove);
/*     */ 
/* 644 */     boolean changed = false;
/* 645 */     Iterator entryIterator = multisetToModify.entrySet().iterator();
/* 646 */     while (entryIterator.hasNext()) {
/* 647 */       Multiset.Entry entry = (Multiset.Entry)entryIterator.next();
/* 648 */       int removeCount = occurrencesToRemove.count(entry.getElement());
/* 649 */       if (removeCount >= entry.getCount()) {
/* 650 */         entryIterator.remove();
/* 651 */         changed = true;
/* 652 */       } else if (removeCount > 0) {
/* 653 */         multisetToModify.remove(entry.getElement(), removeCount);
/* 654 */         changed = true;
/*     */       }
/*     */     }
/* 657 */     return changed;
/*     */   }
/*     */ 
/*     */   static boolean equalsImpl(Multiset<?> multiset, @Nullable Object object)
/*     */   {
/* 705 */     if (object == multiset) {
/* 706 */       return true;
/*     */     }
/* 708 */     if ((object instanceof Multiset)) {
/* 709 */       Multiset that = (Multiset)object;
/*     */ 
/* 716 */       if ((multiset.size() != that.size()) || (multiset.entrySet().size() != that.entrySet().size()))
/*     */       {
/* 718 */         return false;
/*     */       }
/* 720 */       for (Multiset.Entry entry : that.entrySet()) {
/* 721 */         if (multiset.count(entry.getElement()) != entry.getCount()) {
/* 722 */           return false;
/*     */         }
/*     */       }
/* 725 */       return true;
/*     */     }
/* 727 */     return false;
/*     */   }
/*     */ 
/*     */   static <E> boolean addAllImpl(Multiset<E> self, Collection<? extends E> elements)
/*     */   {
/* 735 */     if (elements.isEmpty()) {
/* 736 */       return false;
/*     */     }
/* 738 */     if ((elements instanceof Multiset)) {
/* 739 */       Multiset that = cast(elements);
/* 740 */       for (Multiset.Entry entry : that.entrySet())
/* 741 */         self.add(entry.getElement(), entry.getCount());
/*     */     }
/*     */     else {
/* 744 */       Iterators.addAll(self, elements.iterator());
/*     */     }
/* 746 */     return true;
/*     */   }
/*     */ 
/*     */   static boolean removeAllImpl(Multiset<?> self, Collection<?> elementsToRemove)
/*     */   {
/* 754 */     Collection collection = (elementsToRemove instanceof Multiset) ? ((Multiset)elementsToRemove).elementSet() : elementsToRemove;
/*     */ 
/* 757 */     return self.elementSet().removeAll(collection);
/*     */   }
/*     */ 
/*     */   static boolean retainAllImpl(Multiset<?> self, Collection<?> elementsToRetain)
/*     */   {
/* 765 */     Collection collection = (elementsToRetain instanceof Multiset) ? ((Multiset)elementsToRetain).elementSet() : elementsToRetain;
/*     */ 
/* 768 */     return self.elementSet().retainAll(collection);
/*     */   }
/*     */ 
/*     */   static <E> int setCountImpl(Multiset<E> self, E element, int count)
/*     */   {
/* 775 */     checkNonnegative(count, "count");
/*     */ 
/* 777 */     int oldCount = self.count(element);
/*     */ 
/* 779 */     int delta = count - oldCount;
/* 780 */     if (delta > 0)
/* 781 */       self.add(element, delta);
/* 782 */     else if (delta < 0) {
/* 783 */       self.remove(element, -delta);
/*     */     }
/*     */ 
/* 786 */     return oldCount;
/*     */   }
/*     */ 
/*     */   static <E> boolean setCountImpl(Multiset<E> self, E element, int oldCount, int newCount)
/*     */   {
/* 794 */     checkNonnegative(oldCount, "oldCount");
/* 795 */     checkNonnegative(newCount, "newCount");
/*     */ 
/* 797 */     if (self.count(element) == oldCount) {
/* 798 */       self.setCount(element, newCount);
/* 799 */       return true;
/*     */     }
/* 801 */     return false;
/*     */   }
/*     */ 
/*     */   static <E> Iterator<E> iteratorImpl(Multiset<E> multiset)
/*     */   {
/* 880 */     return new MultisetIteratorImpl(multiset, multiset.entrySet().iterator());
/*     */   }
/*     */ 
/*     */   static int sizeImpl(Multiset<?> multiset)
/*     */   {
/* 936 */     long size = 0L;
/* 937 */     for (Multiset.Entry entry : multiset.entrySet()) {
/* 938 */       size += entry.getCount();
/*     */     }
/* 940 */     return Ints.saturatedCast(size);
/*     */   }
/*     */ 
/*     */   static void checkNonnegative(int count, String name) {
/* 944 */     Preconditions.checkArgument(count >= 0, "%s cannot be negative: %s", new Object[] { name, Integer.valueOf(count) });
/*     */   }
/*     */ 
/*     */   static <T> Multiset<T> cast(Iterable<T> iterable)
/*     */   {
/* 951 */     return (Multiset)iterable;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> ImmutableMultiset<E> copyHighestCountFirst(Multiset<E> multiset)
/*     */   {
/* 969 */     List sortedEntries = DECREASING_COUNT_ORDERING.sortedCopy(multiset.entrySet());
/*     */ 
/* 971 */     return ImmutableMultiset.copyFromEntries(sortedEntries);
/*     */   }
/*     */ 
/*     */   static final class MultisetIteratorImpl<E>
/*     */     implements Iterator<E>
/*     */   {
/*     */     private final Multiset<E> multiset;
/*     */     private final Iterator<Multiset.Entry<E>> entryIterator;
/*     */     private Multiset.Entry<E> currentEntry;
/*     */     private int laterCount;
/*     */     private int totalCount;
/*     */     private boolean canRemove;
/*     */ 
/*     */     MultisetIteratorImpl(Multiset<E> multiset, Iterator<Multiset.Entry<E>> entryIterator)
/*     */     {
/* 896 */       this.multiset = multiset;
/* 897 */       this.entryIterator = entryIterator;
/*     */     }
/*     */ 
/*     */     public boolean hasNext()
/*     */     {
/* 902 */       return (this.laterCount > 0) || (this.entryIterator.hasNext());
/*     */     }
/*     */ 
/*     */     public E next()
/*     */     {
/* 907 */       if (!hasNext()) {
/* 908 */         throw new NoSuchElementException();
/*     */       }
/* 910 */       if (this.laterCount == 0) {
/* 911 */         this.currentEntry = ((Multiset.Entry)this.entryIterator.next());
/* 912 */         this.totalCount = (this.laterCount = this.currentEntry.getCount());
/*     */       }
/* 914 */       this.laterCount -= 1;
/* 915 */       this.canRemove = true;
/* 916 */       return this.currentEntry.getElement();
/*     */     }
/*     */ 
/*     */     public void remove()
/*     */     {
/* 921 */       Iterators.checkRemove(this.canRemove);
/* 922 */       if (this.totalCount == 1)
/* 923 */         this.entryIterator.remove();
/*     */       else {
/* 925 */         this.multiset.remove(this.currentEntry.getElement());
/*     */       }
/* 927 */       this.totalCount -= 1;
/* 928 */       this.canRemove = false;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class EntrySet<E> extends AbstractSet<Multiset.Entry<E>>
/*     */   {
/*     */     abstract Multiset<E> multiset();
/*     */ 
/*     */     public boolean contains(@Nullable Object o)
/*     */     {
/* 852 */       if ((o instanceof Multiset.Entry))
/*     */       {
/* 854 */         Multiset.Entry entry = (Multiset.Entry)o;
/* 855 */         if (entry.getCount() <= 0) {
/* 856 */           return false;
/*     */         }
/* 858 */         int count = multiset().count(entry.getElement());
/* 859 */         return count == entry.getCount();
/*     */       }
/*     */ 
/* 862 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o)
/*     */     {
/* 867 */       return (contains(o)) && (multiset().elementSet().remove(((Multiset.Entry)o).getElement()));
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 872 */       multiset().clear();
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class ElementSet<E> extends AbstractSet<E>
/*     */   {
/*     */     abstract Multiset<E> multiset();
/*     */ 
/*     */     public void clear()
/*     */     {
/* 809 */       multiset().clear();
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 813 */       return multiset().contains(o);
/*     */     }
/*     */ 
/*     */     public boolean containsAll(Collection<?> c) {
/* 817 */       return multiset().containsAll(c);
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 821 */       return multiset().isEmpty();
/*     */     }
/*     */ 
/*     */     public Iterator<E> iterator() {
/* 825 */       return new TransformedIterator(multiset().entrySet().iterator())
/*     */       {
/*     */         E transform(Multiset.Entry<E> entry) {
/* 828 */           return entry.getElement();
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o)
/*     */     {
/* 835 */       int count = multiset().count(o);
/* 836 */       if (count > 0) {
/* 837 */         multiset().remove(o, count);
/* 838 */         return true;
/*     */       }
/* 840 */       return false;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 844 */       return multiset().entrySet().size();
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class AbstractEntry<E>
/*     */     implements Multiset.Entry<E>
/*     */   {
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 670 */       if ((object instanceof Multiset.Entry)) {
/* 671 */         Multiset.Entry that = (Multiset.Entry)object;
/* 672 */         return (getCount() == that.getCount()) && (Objects.equal(getElement(), that.getElement()));
/*     */       }
/*     */ 
/* 675 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 683 */       Object e = getElement();
/* 684 */       return (e == null ? 0 : e.hashCode()) ^ getCount();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 695 */       String text = String.valueOf(getElement());
/* 696 */       int n = getCount();
/* 697 */       return text + " x " + n;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SetMultiset<E> extends ForwardingCollection<E>
/*     */     implements Multiset<E>, Serializable
/*     */   {
/*     */     final Set<E> delegate;
/*     */     transient Set<E> elementSet;
/*     */     transient Set<Multiset.Entry<E>> entrySet;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SetMultiset(Set<E> set)
/*     */     {
/* 347 */       this.delegate = ((Set)Preconditions.checkNotNull(set));
/*     */     }
/*     */ 
/*     */     protected Set<E> delegate() {
/* 351 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public int count(Object element)
/*     */     {
/* 356 */       return this.delegate.contains(element) ? 1 : 0;
/*     */     }
/*     */ 
/*     */     public int add(E element, int occurrences)
/*     */     {
/* 361 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public int remove(Object element, int occurrences)
/*     */     {
/* 366 */       if (occurrences == 0) {
/* 367 */         return count(element);
/*     */       }
/* 369 */       Preconditions.checkArgument(occurrences > 0);
/* 370 */       return this.delegate.remove(element) ? 1 : 0;
/*     */     }
/*     */ 
/*     */     public Set<E> elementSet()
/*     */     {
/* 377 */       Set es = this.elementSet;
/* 378 */       return es == null ? (this.elementSet = new ElementSet()) : es;
/*     */     }
/*     */ 
/*     */     public Set<Multiset.Entry<E>> entrySet()
/*     */     {
/* 384 */       Set es = this.entrySet;
/* 385 */       if (es == null) {
/* 386 */         es = this.entrySet = new Multisets.EntrySet() {
/*     */           Multiset<E> multiset() {
/* 388 */             return Multisets.SetMultiset.this;
/*     */           }
/*     */ 
/*     */           public Iterator<Multiset.Entry<E>> iterator() {
/* 392 */             return new TransformedIterator(Multisets.SetMultiset.this.delegate.iterator())
/*     */             {
/*     */               Multiset.Entry<E> transform(E e) {
/* 395 */                 return Multisets.immutableEntry(e, 1);
/*     */               }
/*     */             };
/*     */           }
/*     */ 
/*     */           public int size() {
/* 401 */             return Multisets.SetMultiset.this.delegate.size();
/*     */           }
/*     */         };
/*     */       }
/* 405 */       return es;
/*     */     }
/*     */ 
/*     */     public boolean add(E o) {
/* 409 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean addAll(Collection<? extends E> c) {
/* 413 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public int setCount(E element, int count)
/*     */     {
/* 418 */       Multisets.checkNonnegative(count, "count");
/*     */ 
/* 420 */       if (count == count(element))
/* 421 */         return count;
/* 422 */       if (count == 0) {
/* 423 */         remove(element);
/* 424 */         return 1;
/*     */       }
/* 426 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean setCount(E element, int oldCount, int newCount)
/*     */     {
/* 432 */       return Multisets.setCountImpl(this, element, oldCount, newCount);
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object object) {
/* 436 */       if ((object instanceof Multiset)) {
/* 437 */         Multiset that = (Multiset)object;
/* 438 */         return (size() == that.size()) && (this.delegate.equals(that.elementSet()));
/*     */       }
/* 440 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 444 */       int sum = 0;
/* 445 */       for (Iterator i$ = iterator(); i$.hasNext(); ) { Object e = i$.next();
/* 446 */         sum += ((e == null ? 0 : e.hashCode()) ^ 0x1);
/*     */       }
/* 448 */       return sum;
/*     */     }
/*     */     class ElementSet extends ForwardingSet<E> {
/*     */       ElementSet() {
/*     */       }
/*     */       protected Set<E> delegate() {
/* 454 */         return Multisets.SetMultiset.this.delegate;
/*     */       }
/*     */ 
/*     */       public boolean add(E o) {
/* 458 */         throw new UnsupportedOperationException();
/*     */       }
/*     */ 
/*     */       public boolean addAll(Collection<? extends E> c) {
/* 462 */         throw new UnsupportedOperationException();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ImmutableEntry<E> extends Multisets.AbstractEntry<E>
/*     */     implements Serializable
/*     */   {
/*     */ 
/*     */     @Nullable
/*     */     final E element;
/*     */     final int count;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     ImmutableEntry(@Nullable E element, int count)
/*     */     {
/* 302 */       this.element = element;
/* 303 */       this.count = count;
/* 304 */       Preconditions.checkArgument(count >= 0);
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     public E getElement() {
/* 309 */       return this.element;
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/* 314 */       return this.count;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class UnmodifiableSortedMultiset<E> extends Multisets.UnmodifiableMultiset<E>
/*     */     implements SortedMultiset<E>
/*     */   {
/*     */     private transient UnmodifiableSortedMultiset<E> descendingMultiset;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private UnmodifiableSortedMultiset(SortedMultiset<E> delegate)
/*     */     {
/* 204 */       super();
/*     */     }
/*     */ 
/*     */     protected SortedMultiset<E> delegate()
/*     */     {
/* 209 */       return (SortedMultiset)super.delegate();
/*     */     }
/*     */ 
/*     */     public Comparator<? super E> comparator()
/*     */     {
/* 214 */       return delegate().comparator();
/*     */     }
/*     */ 
/*     */     SortedSet<E> createElementSet()
/*     */     {
/* 219 */       return Collections.unmodifiableSortedSet(delegate().elementSet());
/*     */     }
/*     */ 
/*     */     public SortedSet<E> elementSet()
/*     */     {
/* 224 */       return (SortedSet)super.elementSet();
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> descendingMultiset()
/*     */     {
/* 231 */       UnmodifiableSortedMultiset result = this.descendingMultiset;
/* 232 */       if (result == null) {
/* 233 */         result = new UnmodifiableSortedMultiset(delegate().descendingMultiset());
/*     */ 
/* 235 */         result.descendingMultiset = this;
/* 236 */         return this.descendingMultiset = result;
/*     */       }
/* 238 */       return result;
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> firstEntry()
/*     */     {
/* 243 */       return delegate().firstEntry();
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> lastEntry()
/*     */     {
/* 248 */       return delegate().lastEntry();
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> pollFirstEntry()
/*     */     {
/* 253 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> pollLastEntry()
/*     */     {
/* 258 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */     {
/* 263 */       return Multisets.unmodifiableSortedMultiset(delegate().headMultiset(upperBound, boundType));
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> subMultiset(E lowerBound, BoundType lowerBoundType, E upperBound, BoundType upperBoundType)
/*     */     {
/* 271 */       return Multisets.unmodifiableSortedMultiset(delegate().subMultiset(lowerBound, lowerBoundType, upperBound, upperBoundType));
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */     {
/* 277 */       return Multisets.unmodifiableSortedMultiset(delegate().tailMultiset(lowerBound, boundType));
/*     */     }
/*     */   }
/*     */ 
/*     */   static class UnmodifiableMultiset<E> extends ForwardingMultiset<E>
/*     */     implements Serializable
/*     */   {
/*     */     final Multiset<? extends E> delegate;
/*     */     transient Set<E> elementSet;
/*     */     transient Set<Multiset.Entry<E>> entrySet;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     UnmodifiableMultiset(Multiset<? extends E> delegate)
/*     */     {
/*  99 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     protected Multiset<E> delegate()
/*     */     {
/* 105 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     Set<E> createElementSet()
/*     */     {
/* 111 */       return Collections.unmodifiableSet(this.delegate.elementSet());
/*     */     }
/*     */ 
/*     */     public Set<E> elementSet()
/*     */     {
/* 116 */       Set es = this.elementSet;
/* 117 */       return es == null ? (this.elementSet = createElementSet()) : es;
/*     */     }
/*     */ 
/*     */     public Set<Multiset.Entry<E>> entrySet()
/*     */     {
/* 124 */       Set es = this.entrySet;
/* 125 */       return es == null ? (this.entrySet = Collections.unmodifiableSet(this.delegate.entrySet())) : es;
/*     */     }
/*     */ 
/*     */     public Iterator<E> iterator()
/*     */     {
/* 135 */       return Iterators.unmodifiableIterator(this.delegate.iterator());
/*     */     }
/*     */ 
/*     */     public boolean add(E element) {
/* 139 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public int add(E element, int occurences) {
/* 143 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean addAll(Collection<? extends E> elementsToAdd) {
/* 147 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean remove(Object element) {
/* 151 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public int remove(Object element, int occurrences) {
/* 155 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean removeAll(Collection<?> elementsToRemove) {
/* 159 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean retainAll(Collection<?> elementsToRetain) {
/* 163 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 167 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public int setCount(E element, int count) {
/* 171 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public boolean setCount(E element, int oldCount, int newCount) {
/* 175 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Multisets
 * JD-Core Version:    0.6.2
 */