/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ImmutableCollection<E>
/*     */   implements Collection<E>, Serializable
/*     */ {
/*  44 */   static final ImmutableCollection<Object> EMPTY_IMMUTABLE_COLLECTION = new EmptyImmutableCollection(null);
/*     */   private transient ImmutableList<E> asList;
/*     */ 
/*     */   public abstract UnmodifiableIterator<E> iterator();
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/*  57 */     return ObjectArrays.toArrayImpl(this);
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] other)
/*     */   {
/*  62 */     return ObjectArrays.toArrayImpl(this, other);
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object object)
/*     */   {
/*  67 */     return (object != null) && (Iterators.contains(iterator(), object));
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> targets)
/*     */   {
/*  72 */     return Collections2.containsAllImpl(this, targets);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  77 */     return size() == 0;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  81 */     return Collections2.toStringImpl(this);
/*     */   }
/*     */ 
/*     */   public final boolean add(E e)
/*     */   {
/*  91 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final boolean remove(Object object)
/*     */   {
/* 101 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final boolean addAll(Collection<? extends E> newElements)
/*     */   {
/* 111 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final boolean removeAll(Collection<?> oldElements)
/*     */   {
/* 121 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final boolean retainAll(Collection<?> elementsToKeep)
/*     */   {
/* 131 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final void clear()
/*     */   {
/* 141 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableList<E> asList()
/*     */   {
/* 156 */     ImmutableList list = this.asList;
/* 157 */     return list == null ? (this.asList = createAsList()) : list;
/*     */   }
/*     */ 
/*     */   ImmutableList<E> createAsList() {
/* 161 */     switch (size()) {
/*     */     case 0:
/* 163 */       return ImmutableList.of();
/*     */     case 1:
/* 165 */       return ImmutableList.of(iterator().next());
/*     */     }
/* 167 */     return new RegularImmutableAsList(this, toArray());
/*     */   }
/*     */ 
/*     */   abstract boolean isPartialView();
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 266 */     return new SerializedForm(toArray());
/*     */   }
/*     */ 
/*     */   public static abstract class Builder<E>
/*     */   {
/*     */     public abstract Builder<E> add(E paramE);
/*     */ 
/*     */     public Builder<E> add(E[] elements)
/*     */     {
/* 304 */       for (Object element : elements) {
/* 305 */         add(element);
/*     */       }
/* 307 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterable<? extends E> elements)
/*     */     {
/* 323 */       for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 324 */         add(element);
/*     */       }
/* 326 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<E> addAll(Iterator<? extends E> elements)
/*     */     {
/* 342 */       while (elements.hasNext()) {
/* 343 */         add(elements.next());
/*     */       }
/* 345 */       return this;
/*     */     }
/*     */ 
/*     */     public abstract ImmutableCollection<E> build();
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
/* 255 */       this.elements = elements;
/*     */     }
/*     */     Object readResolve() {
/* 258 */       return this.elements.length == 0 ? ImmutableCollection.EMPTY_IMMUTABLE_COLLECTION : new ImmutableCollection.ArrayImmutableCollection(Platform.clone(this.elements));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ArrayImmutableCollection<E> extends ImmutableCollection<E>
/*     */   {
/*     */     private final E[] elements;
/*     */ 
/*     */     ArrayImmutableCollection(E[] elements)
/*     */     {
/* 222 */       this.elements = elements;
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 227 */       return this.elements.length;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 231 */       return false;
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<E> iterator() {
/* 235 */       return Iterators.forArray(this.elements);
/*     */     }
/*     */ 
/*     */     ImmutableList<E> createAsList() {
/* 239 */       return this.elements.length == 1 ? new SingletonImmutableList(this.elements[0]) : new RegularImmutableList(this.elements);
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 244 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class EmptyImmutableCollection extends ImmutableCollection<Object>
/*     */   {
/* 192 */     private static final Object[] EMPTY_ARRAY = new Object[0];
/*     */ 
/*     */     public int size()
/*     */     {
/* 177 */       return 0;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 181 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object object) {
/* 185 */       return false;
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<Object> iterator() {
/* 189 */       return Iterators.EMPTY_ITERATOR;
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 195 */       return EMPTY_ARRAY;
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array) {
/* 199 */       if (array.length > 0) {
/* 200 */         array[0] = null;
/*     */       }
/* 202 */       return array;
/*     */     }
/*     */ 
/*     */     ImmutableList<Object> createAsList() {
/* 206 */       return ImmutableList.of();
/*     */     }
/*     */ 
/*     */     boolean isPartialView() {
/* 210 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableCollection
 * JD-Core Version:    0.6.2
 */