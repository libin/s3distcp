/*     */ package com.google.common.collect;
/*     */ 
/*     */ abstract class ImmutableSortedMultisetFauxverideShim<E> extends ImmutableMultiset<E>
/*     */ {
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset.Builder<E> builder()
/*     */   {
/*  45 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset<E> of(E element)
/*     */   {
/*  59 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset<E> of(E e1, E e2)
/*     */   {
/*  73 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3)
/*     */   {
/*  87 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4)
/*     */   {
/* 101 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5)
/*     */   {
/* 116 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset<E> of(E e1, E e2, E e3, E e4, E e5, E e6, E[] remaining)
/*     */   {
/* 138 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> ImmutableSortedMultiset<E> copyOf(E[] elements)
/*     */   {
/* 152 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSortedMultisetFauxverideShim
 * JD-Core Version:    0.6.2
 */