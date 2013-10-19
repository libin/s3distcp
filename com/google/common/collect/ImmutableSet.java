/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableSet<E> extends ImmutableCollection<E>
/*     */   implements Set<E>
/*     */ {
/*     */   static final int MAX_TABLE_SIZE = 1073741824;
/*     */   private static final double DESIRED_LOAD_FACTOR = 0.7D;
/* 222 */   private static final int CUTOFF = (int)Math.floor(751619276.79999995D);
/*     */ 
/*     */   public static <E> ImmutableSet<E> of()
/*     */   {
/*  83 */     return EmptyImmutableSet.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> of(E element)
/*     */   {
/*  93 */     return new SingletonImmutableSet(element);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> of(E e1, E e2)
/*     */   {
/* 104 */     return construct(new Object[] { e1, e2 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> of(E e1, E e2, E e3)
/*     */   {
/* 115 */     return construct(new Object[] { e1, e2, e3 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 126 */     return construct(new Object[] { e1, e2, e3, e4 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 137 */     return construct(new Object[] { e1, e2, e3, e4, e5 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] others)
/*     */   {
/* 150 */     int paramCount = 6;
/* 151 */     Object[] elements = new Object[6 + others.length];
/* 152 */     elements[0] = e1;
/* 153 */     elements[1] = e2;
/* 154 */     elements[2] = e3;
/* 155 */     elements[3] = e4;
/* 156 */     elements[4] = e5;
/* 157 */     elements[5] = e6;
/* 158 */     for (int i = 6; i < elements.length; i++) {
/* 159 */       elements[i] = others[(i - 6)];
/*     */     }
/* 161 */     return construct(elements);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableSet<E> construct(Object[] elements)
/*     */   {
/* 166 */     int tableSize = chooseTableSize(elements.length);
/* 167 */     Object[] table = new Object[tableSize];
/* 168 */     int mask = tableSize - 1;
/* 169 */     ArrayList uniqueElementsList = null;
/* 170 */     int hashCode = 0;
/* 171 */     for (int i = 0; i < elements.length; i++) {
/* 172 */       Object element = elements[i];
/* 173 */       int hash = element.hashCode();
/* 174 */       for (int j = Hashing.smear(hash); ; j++) {
/* 175 */         int index = j & mask;
/* 176 */         Object value = table[index];
/* 177 */         if (value == null) {
/* 178 */           if (uniqueElementsList != null) {
/* 179 */             uniqueElementsList.add(element);
/*     */           }
/*     */ 
/* 182 */           table[index] = element;
/* 183 */           hashCode += hash;
/* 184 */           break;
/* 185 */         }if (value.equals(element)) {
/* 186 */           if (uniqueElementsList != null)
/*     */             break;
/* 188 */           uniqueElementsList = new ArrayList(elements.length);
/* 189 */           for (int k = 0; k < i; k++) {
/* 190 */             Object previous = elements[k];
/* 191 */             uniqueElementsList.add(previous);
/*     */           }
/* 189 */           break;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 198 */     Object[] uniqueElements = uniqueElementsList == null ? elements : uniqueElementsList.toArray();
/*     */ 
/* 201 */     if (uniqueElements.length == 1)
/*     */     {
/* 204 */       Object element = uniqueElements[0];
/* 205 */       return new SingletonImmutableSet(element, hashCode);
/* 206 */     }if (tableSize != chooseTableSize(uniqueElements.length))
/*     */     {
/* 209 */       return construct(uniqueElements);
/*     */     }
/* 211 */     return new RegularImmutableSet(uniqueElements, hashCode, table, mask);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int chooseTableSize(int setSize)
/*     */   {
/* 234 */     if (setSize < CUTOFF)
/*     */     {
/* 236 */       int tableSize = Integer.highestOneBit(setSize - 1) << 1;
/* 237 */       while (tableSize * 0.7D < setSize) {
/* 238 */         tableSize <<= 1;
/*     */       }
/* 240 */       return tableSize;
/*     */     }
/*     */ 
/* 244 */     Preconditions.checkArgument(setSize < 1073741824, "collection too large");
/* 245 */     return 1073741824;
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> copyOf(E[] elements)
/*     */   {
/* 259 */     switch (elements.length) {
/*     */     case 0:
/* 261 */       return of();
/*     */     case 1:
/* 263 */       return of(elements[0]);
/*     */     }
/* 265 */     return construct((Object[])elements.clone());
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 287 */     return (elements instanceof Collection) ? copyOf(Collections2.cast(elements)) : copyOf(elements.iterator());
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 302 */     return copyFromCollection(Lists.newArrayList(elements));
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableSet<E> copyOf(Collection<? extends E> elements)
/*     */   {
/* 337 */     if (((elements instanceof ImmutableSet)) && (!(elements instanceof ImmutableSortedSet)))
/*     */     {
/* 340 */       ImmutableSet set = (ImmutableSet)elements;
/* 341 */       if (!set.isPartialView()) {
/* 342 */         return set;
/*     */       }
/*     */     }
/* 345 */     return copyFromCollection(elements);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableSet<E> copyFromCollection(Collection<? extends E> collection)
/*     */   {
/* 350 */     Object[] elements = collection.toArray();
/* 351 */     switch (elements.length) {
/*     */     case 0:
/* 353 */       return of();
/*     */     case 1:
/* 356 */       Object onlyElement = elements[0];
/* 357 */       return of(onlyElement);
/*     */     }
/*     */ 
/* 361 */     return construct(elements);
/*     */   }
/*     */ 
/*     */   boolean isHashCodeFast()
/*     */   {
/* 369 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 373 */     if (object == this) {
/* 374 */       return true;
/*     */     }
/* 376 */     if (((object instanceof ImmutableSet)) && (isHashCodeFast()) && (((ImmutableSet)object).isHashCodeFast()) && (hashCode() != object.hashCode()))
/*     */     {
/* 380 */       return false;
/*     */     }
/* 382 */     return Sets.equalsImpl(this, object);
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 386 */     return Sets.hashCodeImpl(this);
/*     */   }
/*     */ 
/*     */   public abstract UnmodifiableIterator<E> iterator();
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 539 */     return new SerializedForm(toArray());
/*     */   }
/*     */ 
/*     */   public static <E> Builder<E> builder()
/*     */   {
/* 547 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static class Builder<E> extends ImmutableCollection.Builder<E>
/*     */   {
/* 568 */     final ArrayList<E> contents = Lists.newArrayList();
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 586 */       this.contents.add(Preconditions.checkNotNull(element));
/* 587 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 600 */       this.contents.ensureCapacity(this.contents.size() + elements.length);
/* 601 */       super.add(elements);
/* 602 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 615 */       if ((elements instanceof Collection)) {
/* 616 */         Collection collection = (Collection)elements;
/* 617 */         this.contents.ensureCapacity(this.contents.size() + collection.size());
/*     */       }
/* 619 */       super.addAll(elements);
/* 620 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 633 */       super.addAll(elements);
/* 634 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSet<E> build()
/*     */     {
/* 642 */       return ImmutableSet.copyOf(this.contents);
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
/* 530 */       this.elements = elements;
/*     */     }
/*     */     Object readResolve() {
/* 533 */       return ImmutableSet.copyOf(this.elements);
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class TransformedImmutableSet<D, E> extends ImmutableSet<E>
/*     */   {
/*     */     final ImmutableCollection<D> source;
/*     */     final int hashCode;
/*     */ 
/*     */     TransformedImmutableSet(ImmutableCollection<D> source)
/*     */     {
/* 455 */       this.source = source;
/* 456 */       this.hashCode = Sets.hashCodeImpl(this);
/*     */     }
/*     */ 
/*     */     TransformedImmutableSet(ImmutableCollection<D> source, int hashCode) {
/* 460 */       this.source = source;
/* 461 */       this.hashCode = hashCode;
/*     */     }
/*     */ 
/*     */     abstract E transform(D paramD);
/*     */ 
/*     */     public int size()
/*     */     {
/* 468 */       return this.source.size();
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 472 */       return false;
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<E> iterator() {
/* 476 */       final Iterator backingIterator = this.source.iterator();
/* 477 */       return new UnmodifiableIterator()
/*     */       {
/*     */         public boolean hasNext() {
/* 480 */           return backingIterator.hasNext();
/*     */         }
/*     */ 
/*     */         public E next()
/*     */         {
/* 485 */           return ImmutableSet.TransformedImmutableSet.this.transform(backingIterator.next());
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Object[] toArray() {
/* 491 */       return toArray(new Object[size()]);
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array) {
/* 495 */       int size = size();
/* 496 */       if (array.length < size)
/* 497 */         array = ObjectArrays.newArray(array, size);
/* 498 */       else if (array.length > size) {
/* 499 */         array[size] = null;
/*     */       }
/*     */ 
/* 503 */       Object[] objectArray = array;
/* 504 */       int i = 0;
/* 505 */       for (Iterator i$ = this.source.iterator(); i$.hasNext(); ) { Object d = i$.next();
/* 506 */         objectArray[(i++)] = transform(d);
/*     */       }
/* 508 */       return array;
/*     */     }
/*     */ 
/*     */     public final int hashCode() {
/* 512 */       return this.hashCode;
/*     */     }
/*     */ 
/*     */     boolean isHashCodeFast() {
/* 516 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class ArrayImmutableSet<E> extends ImmutableSet<E>
/*     */   {
/*     */     final transient Object[] elements;
/*     */ 
/*     */     ArrayImmutableSet(Object[] elements)
/*     */     {
/* 398 */       this.elements = elements;
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 403 */       return this.elements.length;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 407 */       return false;
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<E> iterator() {
/* 411 */       return asList().iterator();
/*     */     }
/*     */ 
/*     */     public Object[] toArray() {
/* 415 */       return asList().toArray();
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array) {
/* 419 */       return asList().toArray(array);
/*     */     }
/*     */ 
/*     */     public boolean containsAll(Collection<?> targets) {
/* 423 */       if (targets == this) {
/* 424 */         return true;
/*     */       }
/* 426 */       if (!(targets instanceof ArrayImmutableSet)) {
/* 427 */         return super.containsAll(targets);
/*     */       }
/* 429 */       if (targets.size() > size()) {
/* 430 */         return false;
/*     */       }
/* 432 */       for (Object target : ((ArrayImmutableSet)targets).elements) {
/* 433 */         if (!contains(target)) {
/* 434 */           return false;
/*     */         }
/*     */       }
/* 437 */       return true;
/*     */     }
/*     */ 
/*     */     boolean isPartialView() {
/* 441 */       return false;
/*     */     }
/*     */ 
/*     */     ImmutableList<E> createAsList() {
/* 445 */       return new RegularImmutableAsList(this, this.elements);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSet
 * JD-Core Version:    0.6.2
 */