/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ class EmptyImmutableSortedSet<E> extends ImmutableSortedSet<E>
/*     */ {
/*  61 */   private static final Object[] EMPTY_ARRAY = new Object[0];
/*     */ 
/*     */   EmptyImmutableSortedSet(Comparator<? super E> comparator)
/*     */   {
/*  37 */     super(comparator);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  42 */     return 0;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  46 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean contains(Object target) {
/*  50 */     return false;
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator() {
/*  54 */     return Iterators.emptyIterator();
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/*  64 */     return EMPTY_ARRAY;
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] a) {
/*  68 */     if (a.length > 0) {
/*  69 */       a[0] = null;
/*     */     }
/*  71 */     return a;
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> targets) {
/*  75 */     return targets.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/*  79 */     if ((object instanceof Set)) {
/*  80 */       Set that = (Set)object;
/*  81 */       return that.isEmpty();
/*     */     }
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  87 */     return 0;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  91 */     return "[]";
/*     */   }
/*     */ 
/*     */   public E first()
/*     */   {
/*  96 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */   public E last()
/*     */   {
/* 101 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> headSetImpl(E toElement, boolean inclusive)
/*     */   {
/* 106 */     return this;
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> subSetImpl(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/* 112 */     return this;
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> tailSetImpl(E fromElement, boolean inclusive)
/*     */   {
/* 117 */     return this;
/*     */   }
/*     */ 
/*     */   int indexOf(@Nullable Object target) {
/* 121 */     return -1;
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<E> createDescendingSet()
/*     */   {
/* 126 */     return new EmptyImmutableSortedSet(Ordering.from(this.comparator).reverse());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableSortedSet
 * JD-Core Version:    0.6.2
 */