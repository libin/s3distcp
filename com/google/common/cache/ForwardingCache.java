/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ForwardingObject;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public abstract class ForwardingCache<K, V> extends ForwardingObject
/*     */   implements Cache<K, V>
/*     */ {
/*     */   protected abstract Cache<K, V> delegate();
/*     */ 
/*     */   @Nullable
/*     */   public V getIfPresent(Object key)
/*     */   {
/*  54 */     return delegate().getIfPresent(key);
/*     */   }
/*     */ 
/*     */   public V get(K key, Callable<? extends V> valueLoader)
/*     */     throws ExecutionException
/*     */   {
/*  62 */     return delegate().get(key, valueLoader);
/*     */   }
/*     */ 
/*     */   public ImmutableMap<K, V> getAllPresent(Iterable<?> keys)
/*     */   {
/*  70 */     return delegate().getAllPresent(keys);
/*     */   }
/*     */ 
/*     */   public void put(K key, V value)
/*     */   {
/*  78 */     delegate().put(key, value);
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> m)
/*     */   {
/*  86 */     delegate().putAll(m);
/*     */   }
/*     */ 
/*     */   public void invalidate(Object key)
/*     */   {
/*  91 */     delegate().invalidate(key);
/*     */   }
/*     */ 
/*     */   public void invalidateAll(Iterable<?> keys)
/*     */   {
/*  99 */     delegate().invalidateAll(keys);
/*     */   }
/*     */ 
/*     */   public void invalidateAll()
/*     */   {
/* 104 */     delegate().invalidateAll();
/*     */   }
/*     */ 
/*     */   public long size()
/*     */   {
/* 109 */     return delegate().size();
/*     */   }
/*     */ 
/*     */   public CacheStats stats()
/*     */   {
/* 114 */     return delegate().stats();
/*     */   }
/*     */ 
/*     */   public ConcurrentMap<K, V> asMap()
/*     */   {
/* 119 */     return delegate().asMap();
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 124 */     delegate().cleanUp();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static abstract class SimpleForwardingCache<K, V> extends ForwardingCache<K, V>
/*     */   {
/*     */     private final Cache<K, V> delegate;
/*     */ 
/*     */     protected SimpleForwardingCache(Cache<K, V> delegate)
/*     */     {
/* 138 */       this.delegate = ((Cache)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */ 
/*     */     protected final Cache<K, V> delegate()
/*     */     {
/* 143 */       return this.delegate;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.ForwardingCache
 * JD-Core Version:    0.6.2
 */