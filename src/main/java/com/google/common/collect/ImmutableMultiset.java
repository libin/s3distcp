/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true)
/*     */ public abstract class ImmutableMultiset<E> extends ImmutableCollection<E>
/*     */   implements Multiset<E>
/*     */ {
/*     */   private transient ImmutableSet<Multiset.Entry<E>> entrySet;
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of()
/*     */   {
/*  61 */     return EmptyImmutableMultiset.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E element)
/*     */   {
/*  72 */     return copyOfInternal(new Object[] { element });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2)
/*     */   {
/*  83 */     return copyOfInternal(new Object[] { e1, e2 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3)
/*     */   {
/*  94 */     return copyOfInternal(new Object[] { e1, e2, e3 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 105 */     return copyOfInternal(new Object[] { e1, e2, e3, e4 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 116 */     return copyOfInternal(new Object[] { e1, e2, e3, e4, e5 });
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] others)
/*     */   {
/* 128 */     int size = others.length + 6;
/* 129 */     List all = new ArrayList(size);
/* 130 */     Collections.addAll(all, new Object[] { e1, e2, e3, e4, e5, e6 });
/* 131 */     Collections.addAll(all, others);
/* 132 */     return copyOf(all);
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> copyOf(E[] elements)
/*     */   {
/* 146 */     return copyOf(Arrays.asList(elements));
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> copyOf(Iterable<? extends E> elements)
/*     */   {
/* 168 */     if ((elements instanceof ImmutableMultiset))
/*     */     {
/* 170 */       ImmutableMultiset result = (ImmutableMultiset)elements;
/* 171 */       if (!result.isPartialView()) {
/* 172 */         return result;
/*     */       }
/*     */     }
/*     */ 
/* 176 */     Multiset multiset = (elements instanceof Multiset) ? Multisets.cast(elements) : LinkedHashMultiset.create(elements);
/*     */ 
/* 180 */     return copyOfInternal(multiset);
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableMultiset<E> copyOfInternal(E[] elements) {
/* 184 */     return copyOf(Arrays.asList(elements));
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableMultiset<E> copyOfInternal(Multiset<? extends E> multiset)
/*     */   {
/* 189 */     return copyFromEntries(multiset.entrySet());
/*     */   }
/*     */ 
/*     */   static <E> ImmutableMultiset<E> copyFromEntries(Collection<? extends Multiset.Entry<? extends E>> entries)
/*     */   {
/* 194 */     long size = 0L;
/* 195 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/* 196 */     for (Multiset.Entry entry : entries) {
/* 197 */       int count = entry.getCount();
/* 198 */       if (count > 0)
/*     */       {
/* 201 */         builder.put(entry.getElement(), Integer.valueOf(count));
/* 202 */         size += count;
/*     */       }
/*     */     }
/*     */ 
/* 206 */     if (size == 0L) {
/* 207 */       return of();
/*     */     }
/* 209 */     return new RegularImmutableMultiset(builder.build(), Ints.saturatedCast(size));
/*     */   }
/*     */ 
/*     */   public static <E> ImmutableMultiset<E> copyOf(Iterator<? extends E> elements)
/*     */   {
/* 224 */     Multiset multiset = LinkedHashMultiset.create();
/* 225 */     Iterators.addAll(multiset, elements);
/* 226 */     return copyOfInternal(multiset);
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator()
/*     */   {
/* 232 */     final Iterator entryIterator = entrySet().iterator();
/* 233 */     return new UnmodifiableIterator() {
/*     */       int remaining;
/*     */       E element;
/*     */ 
/*     */       public boolean hasNext() {
/* 239 */         return (this.remaining > 0) || (entryIterator.hasNext());
/*     */       }
/*     */ 
/*     */       public E next()
/*     */       {
/* 244 */         if (this.remaining <= 0) {
/* 245 */           Multiset.Entry entry = (Multiset.Entry)entryIterator.next();
/* 246 */           this.element = entry.getElement();
/* 247 */           this.remaining = entry.getCount();
/*     */         }
/* 249 */         this.remaining -= 1;
/* 250 */         return this.element;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object object)
/*     */   {
/* 257 */     return count(object) > 0;
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> targets)
/*     */   {
/* 262 */     return elementSet().containsAll(targets);
/*     */   }
/*     */ 
/*     */   public final int add(E element, int occurrences)
/*     */   {
/* 272 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final int remove(Object element, int occurrences)
/*     */   {
/* 282 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final int setCount(E element, int count)
/*     */   {
/* 292 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final boolean setCount(E element, int oldCount, int newCount)
/*     */   {
/* 302 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 306 */     if (object == this) {
/* 307 */       return true;
/*     */     }
/* 309 */     if ((object instanceof Multiset)) {
/* 310 */       Multiset that = (Multiset)object;
/* 311 */       if (size() != that.size()) {
/* 312 */         return false;
/*     */       }
/* 314 */       for (Multiset.Entry entry : that.entrySet()) {
/* 315 */         if (count(entry.getElement()) != entry.getCount()) {
/* 316 */           return false;
/*     */         }
/*     */       }
/* 319 */       return true;
/*     */     }
/* 321 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 325 */     return Sets.hashCodeImpl(entrySet());
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 329 */     return entrySet().toString();
/*     */   }
/*     */ 
/*     */   public final ImmutableSet<Multiset.Entry<E>> entrySet()
/*     */   {
/* 336 */     ImmutableSet es = this.entrySet;
/* 337 */     return es == null ? (this.entrySet = createEntrySet()) : es;
/*     */   }
/*     */ 
/*     */   abstract ImmutableSet<Multiset.Entry<E>> createEntrySet();
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 451 */     return new SerializedForm(this);
/*     */   }
/*     */ 
/*     */   public static <E> Builder<E> builder()
/*     */   {
/* 459 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static class Builder<E> extends ImmutableCollection.Builder<E>
/*     */   {
/*     */     final Multiset<E> contents;
/*     */ 
/*     */     public Builder()
/*     */     {
/* 488 */       this(LinkedHashMultiset.create());
/*     */     }
/*     */ 
/*     */     Builder(Multiset<E> contents) {
/* 492 */       this.contents = contents;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E element)
/*     */     {
/* 503 */       this.contents.add(Preconditions.checkNotNull(element));
/* 504 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addCopies(E element, int occurrences)
/*     */     {
/* 521 */       this.contents.add(Preconditions.checkNotNull(element), occurrences);
/* 522 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> setCount(E element, int count)
/*     */     {
/* 536 */       this.contents.setCount(Preconditions.checkNotNull(element), count);
/* 537 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 549 */       super.add(elements);
/* 550 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 563 */       if ((elements instanceof Multiset)) {
/* 564 */         Multiset multiset = Multisets.cast(elements);
/* 565 */         for (Multiset.Entry entry : multiset.entrySet())
/* 566 */           addCopies(entry.getElement(), entry.getCount());
/*     */       }
/*     */       else {
/* 569 */         super.addAll(elements);
/*     */       }
/* 571 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 583 */       super.addAll(elements);
/* 584 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableMultiset<E> build()
/*     */     {
/* 592 */       return ImmutableMultiset.copyOf(this.contents);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SerializedForm
/*     */     implements Serializable
/*     */   {
/*     */     final Object[] elements;
/*     */     final int[] counts;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SerializedForm(Multiset<?> multiset)
/*     */     {
/* 425 */       int distinct = multiset.entrySet().size();
/* 426 */       this.elements = new Object[distinct];
/* 427 */       this.counts = new int[distinct];
/* 428 */       int i = 0;
/* 429 */       for (Multiset.Entry entry : multiset.entrySet()) {
/* 430 */         this.elements[i] = entry.getElement();
/* 431 */         this.counts[i] = entry.getCount();
/* 432 */         i++;
/*     */       }
/*     */     }
/*     */ 
/*     */     Object readResolve() {
/* 437 */       LinkedHashMultiset multiset = LinkedHashMultiset.create(this.elements.length);
/*     */ 
/* 439 */       for (int i = 0; i < this.elements.length; i++) {
/* 440 */         multiset.add(this.elements[i], this.counts[i]);
/*     */       }
/* 442 */       return ImmutableMultiset.copyOf(multiset);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class EntrySetSerializedForm<E>
/*     */     implements Serializable
/*     */   {
/*     */     final ImmutableMultiset<E> multiset;
/*     */ 
/*     */     EntrySetSerializedForm(ImmutableMultiset<E> multiset)
/*     */     {
/* 412 */       this.multiset = multiset;
/*     */     }
/*     */ 
/*     */     Object readResolve() {
/* 416 */       return this.multiset.entrySet();
/*     */     }
/*     */   }
/*     */ 
/*     */   abstract class EntrySet extends ImmutableSet<Multiset.Entry<E>>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     EntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 345 */       return ImmutableMultiset.this.isPartialView();
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o)
/*     */     {
/* 350 */       if ((o instanceof Multiset.Entry)) {
/* 351 */         Multiset.Entry entry = (Multiset.Entry)o;
/* 352 */         if (entry.getCount() <= 0) {
/* 353 */           return false;
/*     */         }
/* 355 */         int count = ImmutableMultiset.this.count(entry.getElement());
/* 356 */         return count == entry.getCount();
/*     */       }
/* 358 */       return false;
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 367 */       Object[] newArray = new Object[size()];
/* 368 */       return toArray(newArray);
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] other)
/*     */     {
/* 377 */       int size = size();
/* 378 */       if (other.length < size)
/* 379 */         other = ObjectArrays.newArray(other, size);
/* 380 */       else if (other.length > size) {
/* 381 */         other[size] = null;
/*     */       }
/*     */ 
/* 385 */       Object[] otherAsObjectArray = other;
/* 386 */       int index = 0;
/* 387 */       for (Multiset.Entry element : this) {
/* 388 */         otherAsObjectArray[(index++)] = element;
/*     */       }
/* 390 */       return other;
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 395 */       return ImmutableMultiset.this.hashCode();
/*     */     }
/*     */ 
/*     */     Object writeReplace()
/*     */     {
/* 402 */       return new ImmutableMultiset.EntrySetSerializedForm(ImmutableMultiset.this);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableMultiset
 * JD-Core Version:    0.6.2
 */