/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class AbstractListMultimap<K, V> extends AbstractMultimap<K, V>
/*     */   implements ListMultimap<K, V>
/*     */ {
/*     */   private static final long serialVersionUID = 6588350623831699109L;
/*     */ 
/*     */   protected AbstractListMultimap(Map<K, Collection<V>> map)
/*     */   {
/*  46 */     super(map);
/*     */   }
/*     */ 
/*     */   abstract List<V> createCollection();
/*     */ 
/*     */   public List<V> get(@Nullable K key)
/*     */   {
/*  61 */     return (List)super.get(key);
/*     */   }
/*     */ 
/*     */   public List<V> removeAll(@Nullable Object key)
/*     */   {
/*  72 */     return (List)super.removeAll(key);
/*     */   }
/*     */ 
/*     */   public List<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*     */   {
/*  84 */     return (List)super.replaceValues(key, values);
/*     */   }
/*     */ 
/*     */   public boolean put(@Nullable K key, @Nullable V value)
/*     */   {
/*  95 */     return super.put(key, value);
/*     */   }
/*     */ 
/*     */   public Map<K, Collection<V>> asMap()
/*     */   {
/* 105 */     return super.asMap();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 116 */     return super.equals(object);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractListMultimap
 * JD-Core Version:    0.6.2
 */