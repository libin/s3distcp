/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.RandomAccess;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableList<E> extends ImmutableCollection<E>
/*     */   implements List<E>, RandomAccess
/*     */ {
/*     */   public static <E> ImmutableList<E> of()
/*     */   {
/*  74 */     return EmptyImmutableList.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E element)
/*     */   {
/*  86 */     return new SingletonImmutableList(element);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2)
/*     */   {
/*  95 */     return construct(new Object[] { e1, e2 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3)
/*     */   {
/* 104 */     return construct(new Object[] { e1, e2, e3 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 113 */     return construct(new Object[] { e1, e2, e3, e4 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 122 */     return construct(new Object[] { e1, e2, e3, e4, e5 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6)
/*     */   {
/* 131 */     return construct(new Object[] { e1, e2, e3, e4, e5, e6 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7)
/*     */   {
/* 141 */     return construct(new Object[] { e1, e2, e3, e4, e5, e6, e7 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8)
/*     */   {
/* 151 */     return construct(new Object[] { e1, e2, e3, e4, e5, e6, e7, e8 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9)
/*     */   {
/* 161 */     return construct(new Object[] { e1, e2, e3, e4, e5, e6, e7, e8, e9 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10)
/*     */   {
/* 171 */     return construct(new Object[] { e1, e2, e3, e4, e5, e6, e7, e8, e9, e10 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11)
/*     */   {
/* 181 */     return construct(new Object[] { e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E e7, E e8, E e9, E e10, E e11, E e12, E[] others)
/*     */   {
/* 196 */     Object[] array = new Object[12 + others.length];
/* 197 */     array[0] = e1;
/* 198 */     array[1] = e2;
/* 199 */     array[2] = e3;
/* 200 */     array[3] = e4;
/* 201 */     array[4] = e5;
/* 202 */     array[5] = e6;
/* 203 */     array[6] = e7;
/* 204 */     array[7] = e8;
/* 205 */     array[8] = e9;
/* 206 */     array[9] = e10;
/* 207 */     array[10] = e11;
/* 208 */     array[11] = e12;
/* 209 */     System.arraycopy(others, 0, array, 12, others.length);
/* 210 */     return construct(array);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 222 */     Preconditions.checkNotNull(elements);
/* 223 */     return (elements instanceof Collection) ? copyOf(Collections2.cast(elements)) : copyOf(elements.iterator());
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> copyOf(Collection<? extends E> elements)
/*     */   {
/* 248 */     if ((elements instanceof ImmutableCollection))
/*     */     {
/* 250 */       ImmutableList list = ((ImmutableCollection)elements).asList();
/* 251 */       return list.isPartialView() ? copyFromCollection(list) : list;
/*     */     }
/* 253 */     return copyFromCollection(elements);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 262 */     return copyFromCollection(Lists.newArrayList(elements));
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableList<E> copyOf(E[] elements)
/*     */   {
/* 272 */     switch (elements.length) {
/*     */     case 0:
/* 274 */       return of();
/*     */     case 1:
/* 276 */       return new SingletonImmutableList(elements[0]);
/*     */     }
/* 278 */     return construct((Object[])elements.clone());
/*     */   }
/*     */ 
/*     */   static <E> ImmutableList<E> asImmutableList(Object[] elements)
/*     */   {
/* 288 */     switch (elements.length) {
/*     */     case 0:
/* 290 */       return of();
/*     */     case 1:
/* 293 */       ImmutableList list = new SingletonImmutableList(elements[0]);
/* 294 */       return list;
/*     */     }
/* 296 */     return construct(elements);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableList<E> copyFromCollection(Collection<? extends E> collection)
/*     */   {
/* 302 */     return asImmutableList(collection.toArray());
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableList<E> construct(Object[] elements)
/*     */   {
/* 307 */     for (int i = 0; i < elements.length; i++) {
/* 308 */       checkElementNotNull(elements[i], i);
/*     */     }
/* 310 */     return new RegularImmutableList(elements);
/*     */   }
/*     */ 
/*     */   private static Object checkElementNotNull(Object element, int index)
/*     */   {
/* 316 */     if (element == null) {
/* 317 */       throw new NullPointerException("at index " + index);
/*     */     }
/* 319 */     return element;
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator()
/*     */   {
/* 327 */     return listIterator();
/*     */   }
/*     */ 
/*     */   public UnmodifiableListIterator<E> listIterator() {
/* 331 */     return listIterator(0);
/*     */   }
/*     */ 
/*     */   public UnmodifiableListIterator<E> listIterator(int index) {
/* 335 */     return new AbstractIndexedListIterator(size(), index)
/*     */     {
/*     */       protected E get(int index) {
/* 338 */         return ImmutableList.this.get(index);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public int indexOf(@Nullable Object object)
/*     */   {
/* 345 */     return object == null ? -1 : Lists.indexOfImpl(this, object);
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(@Nullable Object object)
/*     */   {
/* 350 */     return object == null ? -1 : Lists.lastIndexOfImpl(this, object);
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object object)
/*     */   {
/* 355 */     return indexOf(object) >= 0;
/*     */   }
/*     */ 
/*     */   public ImmutableList<E> subList(int fromIndex, int toIndex)
/*     */   {
/* 368 */     Preconditions.checkPositionIndexes(fromIndex, toIndex, size());
/* 369 */     int length = toIndex - fromIndex;
/* 370 */     switch (length) {
/*     */     case 0:
/* 372 */       return of();
/*     */     case 1:
/* 374 */       return of(get(fromIndex));
/*     */     }
/* 376 */     return subListUnchecked(fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */   ImmutableList<E> subListUnchecked(int fromIndex, int toIndex)
/*     */   {
/* 385 */     return new SubList(fromIndex, toIndex - fromIndex);
/*     */   }
/*     */ 
/*     */   public final boolean addAll(int index, Collection<? extends E> newElements)
/*     */   {
/* 427 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final E set(int index, E element)
/*     */   {
/* 437 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final void add(int index, E element)
/*     */   {
/* 447 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final E remove(int index)
/*     */   {
/* 457 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableList<E> asList()
/*     */   {
/* 466 */     return this;
/*     */   }
/*     */ 
/*     */   public ImmutableList<E> reverse()
/*     */   {
/* 478 */     return new ReverseImmutableList(this);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 576 */     return Lists.equalsImpl(this, obj);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 580 */     return Lists.hashCodeImpl(this);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream stream)
/*     */     throws InvalidObjectException
/*     */   {
/* 600 */     throw new InvalidObjectException("Use SerializedForm");
/*     */   }
/*     */ 
/*     */   Object writeReplace() {
/* 604 */     return new SerializedForm(toArray());
/*     */   }
/*     */ 
/*     */   public static <E> Builder<E> builder()
/*     */   {
/* 612 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static final class Builder<E> extends ImmutableCollection.Builder<E>
/*     */   {
/* 632 */     private final ArrayList<E> contents = Lists.newArrayList();
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 648 */       this.contents.add(Preconditions.checkNotNull(element));
/* 649 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 661 */       if ((elements instanceof Collection)) {
/* 662 */         Collection collection = (Collection)elements;
/* 663 */         this.contents.ensureCapacity(this.contents.size() + collection.size());
/*     */       }
/* 665 */       super.addAll(elements);
/* 666 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 678 */       this.contents.ensureCapacity(this.contents.size() + elements.length);
/* 679 */       super.add(elements);
/* 680 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 692 */       super.addAll(elements);
/* 693 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableList<E> build()
/*     */     {
/* 701 */       return ImmutableList.copyOf(this.contents);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SerializedForm
/*     */     implements Serializable
/*     */   {
/*     */     final Object[] elements;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SerializedForm(Object[] elements)
/*     */     {
/* 590 */       this.elements = elements;
/*     */     }
/*     */     Object readResolve() {
/* 593 */       return ImmutableList.copyOf(this.elements);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ReverseImmutableList<E> extends ImmutableList<E>
/*     */   {
/*     */     private final transient ImmutableList<E> forwardList;
/*     */     private final transient int size;
/*     */ 
/*     */     ReverseImmutableList(ImmutableList<E> backingList)
/*     */     {
/* 486 */       this.forwardList = backingList;
/* 487 */       this.size = backingList.size();
/*     */     }
/*     */ 
/*     */     private int reverseIndex(int index) {
/* 491 */       return this.size - 1 - index;
/*     */     }
/*     */ 
/*     */     private int reversePosition(int index) {
/* 495 */       return this.size - index;
/*     */     }
/*     */ 
/*     */     public ImmutableList<E> reverse() {
/* 499 */       return this.forwardList;
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object object) {
/* 503 */       return this.forwardList.contains(object);
/*     */     }
/*     */ 
/*     */     public boolean containsAll(Collection<?> targets) {
/* 507 */       return this.forwardList.containsAll(targets);
/*     */     }
/*     */ 
/*     */     public int indexOf(@Nullable Object object) {
/* 511 */       int index = this.forwardList.lastIndexOf(object);
/* 512 */       return index >= 0 ? reverseIndex(index) : -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(@Nullable Object object) {
/* 516 */       int index = this.forwardList.indexOf(object);
/* 517 */       return index >= 0 ? reverseIndex(index) : -1;
/*     */     }
/*     */ 
/*     */     public ImmutableList<E> subList(int fromIndex, int toIndex) {
/* 521 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, this.size);
/* 522 */       return this.forwardList.subList(reversePosition(toIndex), reversePosition(fromIndex)).reverse();
/*     */     }
/*     */ 
/*     */     public E get(int index)
/*     */     {
/* 527 */       Preconditions.checkElementIndex(index, this.size);
/* 528 */       return this.forwardList.get(reverseIndex(index));
/*     */     }
/*     */ 
/*     */     public UnmodifiableListIterator<E> listIterator(int index) {
/* 532 */       Preconditions.checkPositionIndex(index, this.size);
/* 533 */       final UnmodifiableListIterator forward = this.forwardList.listIterator(reversePosition(index));
/*     */ 
/* 535 */       return new UnmodifiableListIterator() {
/*     */         public boolean hasNext() {
/* 537 */           return forward.hasPrevious();
/*     */         }
/*     */ 
/*     */         public boolean hasPrevious() {
/* 541 */           return forward.hasNext();
/*     */         }
/*     */ 
/*     */         public E next() {
/* 545 */           return forward.previous();
/*     */         }
/*     */ 
/*     */         public int nextIndex() {
/* 549 */           return ImmutableList.ReverseImmutableList.this.reverseIndex(forward.previousIndex());
/*     */         }
/*     */ 
/*     */         public E previous() {
/* 553 */           return forward.next();
/*     */         }
/*     */ 
/*     */         public int previousIndex() {
/* 557 */           return ImmutableList.ReverseImmutableList.this.reverseIndex(forward.nextIndex());
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public int size() {
/* 563 */       return this.size;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 567 */       return this.forwardList.isEmpty();
/*     */     }
/*     */ 
/*     */     boolean isPartialView() {
/* 571 */       return this.forwardList.isPartialView();
/*     */     }
/*     */   }
/*     */ 
/*     */   class SubList extends ImmutableList<E>
/*     */   {
/*     */     final transient int offset;
/*     */     final transient int length;
/*     */ 
/*     */     SubList(int offset, int length)
/*     */     {
/* 393 */       this.offset = offset;
/* 394 */       this.length = length;
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 399 */       return this.length;
/*     */     }
/*     */ 
/*     */     public E get(int index)
/*     */     {
/* 404 */       Preconditions.checkElementIndex(index, this.length);
/* 405 */       return ImmutableList.this.get(index + this.offset);
/*     */     }
/*     */ 
/*     */     public ImmutableList<E> subList(int fromIndex, int toIndex)
/*     */     {
/* 410 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, this.length);
/* 411 */       return ImmutableList.this.subList(fromIndex + this.offset, toIndex + this.offset);
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 416 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableList
 * JD-Core Version:    0.6.2
 */