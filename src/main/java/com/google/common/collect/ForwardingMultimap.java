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
/*     */ public abstract class ForwardingMultimap<K, V> extends ForwardingObject
/*     */   implements Multimap<K, V>
/*     */ {
/*     */   protected abstract Multimap<K, V> delegate();
/*     */ 
/*     */   public Map<K, Collection<V>> asMap()
/*     */   {
/*  48 */     return delegate().asMap();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/*  53 */     delegate().clear();
/*     */   }
/*     */ 
/*     */   public boolean containsEntry(@Nullable Object key, @Nullable Object value)
/*     */   {
/*  58 */     return delegate().containsEntry(key, value);
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/*  63 */     return delegate().containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/*  68 */     return delegate().containsValue(value);
/*     */   }
/*     */ 
/*     */   public Collection<Map.Entry<K, V>> entries()
/*     */   {
/*  73 */     return delegate().entries();
/*     */   }
/*     */ 
/*     */   public Collection<V> get(@Nullable K key)
/*     */   {
/*  78 */     return delegate().get(key);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  83 */     return delegate().isEmpty();
/*     */   }
/*     */ 
/*     */   public Multiset<K> keys()
/*     */   {
/*  88 */     return delegate().keys();
/*     */   }
/*     */ 
/*     */   public Set<K> keySet()
/*     */   {
/*  93 */     return delegate().keySet();
/*     */   }
/*     */ 
/*     */   public boolean put(K key, V value)
/*     */   {
/*  98 */     return delegate().put(key, value);
/*     */   }
/*     */ 
/*     */   public boolean putAll(K key, Iterable<? extends V> values)
/*     */   {
/* 103 */     return delegate().putAll(key, values);
/*     */   }
/*     */ 
/*     */   public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 108 */     return delegate().putAll(multimap);
/*     */   }
/*     */ 
/*     */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*     */   {
/* 113 */     return delegate().remove(key, value);
/*     */   }
/*     */ 
/*     */   public Collection<V> removeAll(@Nullable Object key)
/*     */   {
/* 118 */     return delegate().removeAll(key);
/*     */   }
/*     */ 
/*     */   public Collection<V> replaceValues(K key, Iterable<? extends V> values)
/*     */   {
/* 123 */     return delegate().replaceValues(key, values);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 128 */     return delegate().size();
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 133 */     return delegate().values();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 137 */     return (object == this) || (delegate().equals(object));
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 141 */     return delegate().hashCode();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingMultimap
 * JD-Core Version:    0.6.2
 */