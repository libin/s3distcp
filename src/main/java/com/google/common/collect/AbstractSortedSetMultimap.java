/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class AbstractSortedSetMultimap<K, V> extends AbstractSetMultimap<K, V>
/*     */   implements SortedSetMultimap<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 430848587173315748L;
/*     */ 
/*     */   protected AbstractSortedSetMultimap(Map<K, Collection<V>> map)
/*     */   {
/*  45 */     super(map);
/*     */   }
/*     */ 
/*     */   abstract SortedSet<V> createCollection();
/*     */ 
/*     */   public SortedSet<V> get(@Nullable K key)
/*     */   {
/*  65 */     return (SortedSet)super.get(key);
/*     */   }
/*     */ 
/*     */   public SortedSet<V> removeAll(@Nullable Object key)
/*     */   {
/*  77 */     return (SortedSet)super.removeAll(key);
/*     */   }
/*     */ 
/*     */   public SortedSet<V> replaceValues(K key, Iterable<? extends V> values)
/*     */   {
/*  92 */     return (SortedSet)super.replaceValues(key, values);
/*     */   }
/*     */ 
/*     */   public Map<K, Collection<V>> asMap()
/*     */   {
/* 110 */     return super.asMap();
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 120 */     return super.values();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractSortedSetMultimap
 * JD-Core Version:    0.6.2
 */