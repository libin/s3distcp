/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class AbstractSetMultimap<K, V> extends AbstractMultimap<K, V>
/*     */   implements SetMultimap<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 7431625294878419160L;
/*     */ 
/*     */   protected AbstractSetMultimap(Map<K, Collection<V>> map)
/*     */   {
/*  44 */     super(map);
/*     */   }
/*     */ 
/*     */   abstract Set<V> createCollection();
/*     */ 
/*     */   public Set<V> get(@Nullable K key)
/*     */   {
/*  59 */     return (Set)super.get(key);
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<K, V>> entries()
/*     */   {
/*  70 */     return (Set)super.entries();
/*     */   }
/*     */ 
/*     */   public Set<V> removeAll(@Nullable Object key)
/*     */   {
/*  81 */     return (Set)super.removeAll(key);
/*     */   }
/*     */ 
/*     */   public Set<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/*  95 */     return (Set)super.replaceValues(key, values);
/*     */   }
/*     */ 
/*     */   public Map<K, Collection<V>> asMap()
/*     */   {
/* 105 */     return super.asMap();
/*     */   }
/*     */ 
/*     */   public boolean put(K key, V value)
/*     */   {
/* 117 */     return super.put(key, value);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 128 */     return super.equals(object);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractSetMultimap
 * JD-Core Version:    0.6.2
 */