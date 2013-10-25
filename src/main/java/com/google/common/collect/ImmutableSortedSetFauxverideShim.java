/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class ImmutableSortedSetFauxverideShim<E> extends ImmutableSet<E>
/*     */ {
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet.Builder<E> builder()
/*     */   {
/*  49 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E element)
/*     */   {
/*  62 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2)
/*     */   {
/*  75 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3)
/*     */   {
/*  88 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 103 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 118 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] remaining)
/*     */   {
/* 133 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedSet<E> copyOf(E[] elements)
/*     */   {
/* 146 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSortedSetFauxverideShim
 * JD-Core Version:    0.6.2
 */