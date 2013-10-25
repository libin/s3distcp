/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ final class RegularImmutableSortedMultiset<E> extends ImmutableSortedMultiset<E>
/*     */ {
/*     */   final transient ImmutableList<CumulativeCountEntry<E>> entries;
/*     */ 
/*     */   static <E> RegularImmutableSortedMultiset<E> createFromSorted(Comparator<? super E> comparator, List<? extends Multiset.Entry<E>> entries)
/*     */   {
/*  61 */     List newEntries = Lists.newArrayListWithCapacity(entries.size());
/*  62 */     CumulativeCountEntry previous = null;
/*  63 */     for (Multiset.Entry entry : entries) {
/*  64 */       newEntries.add(previous = new CumulativeCountEntry(entry.getElement(), entry.getCount(), previous));
/*     */     }
/*     */ 
/*  67 */     return new RegularImmutableSortedMultiset(comparator, ImmutableList.copyOf(newEntries));
/*     */   }
/*     */ 
/*     */   RegularImmutableSortedMultiset(Comparator<? super E> comparator, ImmutableList<CumulativeCountEntry<E>> entries)
/*     */   {
/*  74 */     super(comparator);
/*  75 */     this.entries = entries;
/*  76 */     assert (!entries.isEmpty());
/*     */   }
/*     */ 
/*     */   ImmutableList<E> elementList() {
/*  80 */     return new TransformedImmutableList(this.entries)
/*     */     {
/*     */       E transform(RegularImmutableSortedMultiset.CumulativeCountEntry<E> entry) {
/*  83 */         return entry.getElement();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> createElementSet()
/*     */   {
/*  90 */     return new RegularImmutableSortedSet(elementList(), comparator());
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> createDescendingElementSet()
/*     */   {
/*  95 */     return new RegularImmutableSortedSet(elementList().reverse(), reverseComparator());
/*     */   }
/*     */ 
/*     */   ImmutableSet<Multiset.Entry<E>> createEntrySet()
/*     */   {
/* 100 */     return new EntrySet(null);
/*     */   }
/*     */ 
/*     */   public CumulativeCountEntry<E> firstEntry()
/*     */   {
/* 122 */     return (CumulativeCountEntry)this.entries.get(0);
/*     */   }
/*     */ 
/*     */   public CumulativeCountEntry<E> lastEntry()
/*     */   {
/* 127 */     return (CumulativeCountEntry)this.entries.get(this.entries.size() - 1);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 132 */     CumulativeCountEntry firstEntry = firstEntry();
/* 133 */     CumulativeCountEntry lastEntry = lastEntry();
/* 134 */     return Ints.saturatedCast(lastEntry.cumulativeCount - firstEntry.cumulativeCount + firstEntry.count);
/*     */   }
/*     */ 
/*     */   boolean isPartialView()
/*     */   {
/* 140 */     return this.entries.isPartialView();
/*     */   }
/*     */ 
/*     */   public int count(@Nullable Object element)
/*     */   {
/* 146 */     if (element == null)
/* 147 */       return 0;
/*     */     try
/*     */     {
/* 150 */       int index = SortedLists.binarySearch(elementList(), element, comparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
/*     */ 
/* 152 */       return index >= 0 ? ((CumulativeCountEntry)this.entries.get(index)).getCount() : 0; } catch (ClassCastException e) {
/*     */     }
/* 154 */     return 0;
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMultiset<E> headMultiset(E upperBound, BoundType boundType)
/*     */   {
/*     */     int index;
/* 161 */     switch (2.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */     case 1:
/* 163 */       index = SortedLists.binarySearch(elementList(), Preconditions.checkNotNull(upperBound), comparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */ 
/* 165 */       break;
/*     */     case 2:
/* 167 */       index = SortedLists.binarySearch(elementList(), Preconditions.checkNotNull(upperBound), comparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER) + 1;
/*     */ 
/* 169 */       break;
/*     */     default:
/* 171 */       throw new AssertionError();
/*     */     }
/* 173 */     return createSubMultiset(0, index);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMultiset<E> tailMultiset(E lowerBound, BoundType boundType)
/*     */   {
/*     */     int index;
/* 179 */     switch (2.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */     case 1:
/* 181 */       index = SortedLists.binarySearch(elementList(), Preconditions.checkNotNull(lowerBound), comparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER) + 1;
/*     */ 
/* 183 */       break;
/*     */     case 2:
/* 185 */       index = SortedLists.binarySearch(elementList(), Preconditions.checkNotNull(lowerBound), comparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */ 
/* 187 */       break;
/*     */     default:
/* 189 */       throw new AssertionError();
/*     */     }
/* 191 */     return createSubMultiset(index, this.entries.size());
/*     */   }
/*     */ 
/*     */   private ImmutableSortedMultiset<E> createSubMultiset(int newFromIndex, int newToIndex) {
/* 195 */     if ((newFromIndex == 0) && (newToIndex == this.entries.size()))
/* 196 */       return this;
/* 197 */     if (newFromIndex >= newToIndex) {
/* 198 */       return emptyMultiset(comparator());
/*     */     }
/* 200 */     return new RegularImmutableSortedMultiset(comparator(), this.entries.subList(newFromIndex, newToIndex));
/*     */   }
/*     */ 
/*     */   private class EntrySet extends ImmutableMultiset.EntrySet
/*     */   {
/*     */     private EntrySet()
/*     */     {
/* 103 */       super();
/*     */     }
/*     */     public int size() {
/* 106 */       return RegularImmutableSortedMultiset.this.entries.size();
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<Multiset.Entry<E>> iterator()
/*     */     {
/* 111 */       return asList().iterator();
/*     */     }
/*     */ 
/*     */     ImmutableList<Multiset.Entry<E>> createAsList()
/*     */     {
/* 116 */       return new RegularImmutableAsList(this, RegularImmutableSortedMultiset.this.entries);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class CumulativeCountEntry<E> extends Multisets.AbstractEntry<E>
/*     */   {
/*     */     final E element;
/*     */     final int count;
/*     */     final long cumulativeCount;
/*     */ 
/*     */     CumulativeCountEntry(E element, int count, @Nullable CumulativeCountEntry<E> previous)
/*     */     {
/*  43 */       this.element = element;
/*  44 */       this.count = count;
/*  45 */       this.cumulativeCount = (count + (previous == null ? 0L : previous.cumulativeCount));
/*     */     }
/*     */ 
/*     */     public E getElement()
/*     */     {
/*  50 */       return this.element;
/*     */     }
/*     */ 
/*     */     public int getCount()
/*     */     {
/*  55 */       return this.count;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RegularImmutableSortedMultiset
 * JD-Core Version:    0.6.2
 */