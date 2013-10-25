/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class RegularImmutableSortedSet<E> extends ImmutableSortedSet<E>
/*     */ {
/*     */   private final transient ImmutableList<E> elements;
/*     */ 
/*     */   RegularImmutableSortedSet(ImmutableList<E> elements, Comparator<? super E> comparator)
/*     */   {
/*  53 */     super(comparator);
/*  54 */     this.elements = elements;
/*  55 */     Preconditions.checkArgument(!elements.isEmpty());
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator() {
/*  59 */     return this.elements.iterator();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  63 */     return false;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  68 */     return this.elements.size();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object o) {
/*  72 */     if (o == null)
/*  73 */       return false;
/*     */     try
/*     */     {
/*  76 */       return binarySearch(o) >= 0; } catch (ClassCastException e) {
/*     */     }
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> targets)
/*     */   {
/*  87 */     if ((!SortedIterables.hasSameComparator(comparator(), targets)) || (targets.size() <= 1))
/*     */     {
/*  89 */       return super.containsAll(targets);
/*     */     }
/*     */ 
/*  96 */     Iterator thisIterator = iterator();
/*  97 */     Iterator thatIterator = targets.iterator();
/*  98 */     Object target = thatIterator.next();
/*     */     try
/*     */     {
/* 102 */       while (thisIterator.hasNext())
/*     */       {
/* 104 */         int cmp = unsafeCompare(thisIterator.next(), target);
/*     */ 
/* 106 */         if (cmp == 0)
/*     */         {
/* 108 */           if (!thatIterator.hasNext())
/*     */           {
/* 110 */             return true;
/*     */           }
/*     */ 
/* 113 */           target = thatIterator.next();
/*     */         }
/* 115 */         else if (cmp > 0) {
/* 116 */           return false;
/*     */         }
/*     */       }
/*     */     } catch (NullPointerException e) {
/* 120 */       return false;
/*     */     } catch (ClassCastException e) {
/* 122 */       return false;
/*     */     }
/*     */ 
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   private int binarySearch(Object key)
/*     */   {
/* 137 */     Comparator unsafeComparator = this.comparator;
/*     */ 
/* 139 */     return Collections.binarySearch(this.elements, key, unsafeComparator);
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/* 143 */     return this.elements.isPartialView();
/*     */   }
/*     */ 
/*     */   public Object[] toArray() {
/* 147 */     return this.elements.toArray();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] array) {
/* 151 */     return this.elements.toArray(array);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 155 */     if (object == this) {
/* 156 */       return true;
/*     */     }
/* 158 */     if (!(object instanceof Set)) {
/* 159 */       return false;
/*     */     }
/*     */ 
/* 162 */     Set that = (Set)object;
/* 163 */     if (size() != that.size()) {
/* 164 */       return false;
/*     */     }
/*     */ 
/* 167 */     if (SortedIterables.hasSameComparator(this.comparator, that)) {
/* 168 */       Iterator otherIterator = that.iterator();
/*     */       try {
/* 170 */         Iterator iterator = iterator();
/* 171 */         while (iterator.hasNext()) {
/* 172 */           Object element = iterator.next();
/* 173 */           Object otherElement = otherIterator.next();
/* 174 */           if ((otherElement == null) || (unsafeCompare(element, otherElement) != 0))
/*     */           {
/* 176 */             return false;
/*     */           }
/*     */         }
/* 179 */         return true;
/*     */       } catch (ClassCastException e) {
/* 181 */         return false;
/*     */       } catch (NoSuchElementException e) {
/* 183 */         return false;
/*     */       }
/*     */     }
/* 186 */     return containsAll(that);
/*     */   }
/*     */ 
/*     */   public E first()
/*     */   {
/* 191 */     return this.elements.get(0);
/*     */   }
/*     */ 
/*     */   public E last()
/*     */   {
/* 196 */     return this.elements.get(size() - 1);
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive)
/*     */   {
/*     */     int index;
/*     */     int index;
/* 202 */     if (inclusive) {
/* 203 */       index = SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(toElement), comparator(), SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */     }
/*     */     else {
/* 206 */       index = SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(toElement), comparator(), SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */     }
/*     */ 
/* 209 */     return createSubset(0, index);
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/* 215 */     return tailSetImpl(fromElement, fromInclusive).headSetImpl(toElement, toInclusive);
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive)
/*     */   {
/*     */     int index;
/*     */     int index;
/* 222 */     if (inclusive) {
/* 223 */       index = SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(fromElement), comparator(), SortedLists.KeyPresentBehavior.FIRST_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */     }
/*     */     else {
/* 226 */       index = SortedLists.binarySearch(this.elements, Preconditions.checkNotNull(fromElement), comparator(), SortedLists.KeyPresentBehavior.FIRST_AFTER, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */     }
/*     */ 
/* 229 */     return createSubset(index, size());
/*     */   }
/*     */ 
/*     */   Comparator<Object> unsafeComparator()
/*     */   {
/* 237 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   private ImmutableSortedSet<E> createSubset(int newFromIndex, int newToIndex) {
/* 241 */     if ((newFromIndex == 0) && (newToIndex == size()))
/* 242 */       return this;
/* 243 */     if (newFromIndex < newToIndex) {
/* 244 */       return new RegularImmutableSortedSet(this.elements.subList(newFromIndex, newToIndex), this.comparator);
/*     */     }
/*     */ 
/* 247 */     return emptySet(this.comparator);
/*     */   }
/*     */ 
/*     */   int indexOf(@Nullable Object target)
/*     */   {
/* 253 */     if (target == null)
/* 254 */       return -1;
/*     */     int position;
/*     */     try
/*     */     {
/* 258 */       position = SortedLists.binarySearch(this.elements, target, comparator(), SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
/*     */     }
/*     */     catch (ClassCastException e) {
/* 261 */       return -1;
/*     */     }
/*     */ 
/* 268 */     return (position >= 0) && (this.elements.get(position).equals(target)) ? position : -1;
/*     */   }
/*     */ 
/*     */   ImmutableList<E> createAsList()
/*     */   {
/* 273 */     return new ImmutableSortedAsList(this, this.elements);
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> createDescendingSet()
/*     */   {
/* 278 */     return new RegularImmutableSortedSet(this.elements.reverse(), Ordering.from(this.comparator).reverse());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RegularImmutableSortedSet
 * JD-Core Version:    0.6.2
 */