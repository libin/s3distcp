/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class ImmutableSortedMapFauxverideShim<K, V> extends ImmutableMap<K, V>
/*     */ {
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap.Builder<K, V> builder()
/*     */   {
/*  41 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1)
/*     */   {
/*  54 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  68 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/*  83 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/*  98 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <K, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 113 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSortedMapFauxverideShim
 * JD-Core Version:    0.6.2
 */