/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.concurrent.Callable;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public abstract class AbstractCache<K, V>
/*     */   implements Cache<K, V>
/*     */ {
/*     */   public V get(K key, Callable<? extends V> valueLoader)
/*     */     throws ExecutionException
/*     */   {
/*  55 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<K, V> getAllPresent(Iterable<?> keys)
/*     */   {
/*  69 */     Map result = Maps.newLinkedHashMap();
/*  70 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/*  71 */       if (!result.containsKey(key))
/*     */       {
/*  73 */         Object castKey = key;
/*  74 */         result.put(castKey, getIfPresent(key));
/*     */       }
/*     */     }
/*  77 */     return ImmutableMap.copyOf(result);
/*     */   }
/*     */ 
/*     */   public void put(K key, V value)
/*     */   {
/*  85 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends V> m)
/*     */   {
/*  93 */     for (Map.Entry entry : m.entrySet())
/*  94 */       put(entry.getKey(), entry.getValue());
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/*     */   }
/*     */ 
/*     */   public long size()
/*     */   {
/* 103 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void invalidate(Object key)
/*     */   {
/* 108 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void invalidateAll(Iterable<?> keys)
/*     */   {
/* 116 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 117 */       invalidate(key);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void invalidateAll()
/*     */   {
/* 123 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public CacheStats stats()
/*     */   {
/* 128 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ConcurrentMap<K, V> asMap()
/*     */   {
/* 133 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static class SimpleStatsCounter
/*     */     implements AbstractCache.StatsCounter
/*     */   {
/* 206 */     private final LongAdder hitCount = new LongAdder();
/* 207 */     private final LongAdder missCount = new LongAdder();
/* 208 */     private final LongAdder loadSuccessCount = new LongAdder();
/* 209 */     private final LongAdder loadExceptionCount = new LongAdder();
/* 210 */     private final LongAdder totalLoadTime = new LongAdder();
/* 211 */     private final LongAdder evictionCount = new LongAdder();
/*     */ 
/*     */     public void recordHits(int count)
/*     */     {
/* 218 */       this.hitCount.add(count);
/*     */     }
/*     */ 
/*     */     public void recordMisses(int count)
/*     */     {
/* 226 */       this.missCount.add(count);
/*     */     }
/*     */ 
/*     */     public void recordLoadSuccess(long loadTime)
/*     */     {
/* 231 */       this.loadSuccessCount.increment();
/* 232 */       this.totalLoadTime.add(loadTime);
/*     */     }
/*     */ 
/*     */     public void recordLoadException(long loadTime)
/*     */     {
/* 237 */       this.loadExceptionCount.increment();
/* 238 */       this.totalLoadTime.add(loadTime);
/*     */     }
/*     */ 
/*     */     public void recordEviction()
/*     */     {
/* 243 */       this.evictionCount.increment();
/*     */     }
/*     */ 
/*     */     public CacheStats snapshot()
/*     */     {
/* 248 */       return new CacheStats(this.hitCount.sum(), this.missCount.sum(), this.loadSuccessCount.sum(), this.loadExceptionCount.sum(), this.totalLoadTime.sum(), this.evictionCount.sum());
/*     */     }
/*     */ 
/*     */     public void incrementBy(AbstractCache.StatsCounter other)
/*     */     {
/* 261 */       CacheStats otherStats = other.snapshot();
/* 262 */       this.hitCount.add(otherStats.hitCount());
/* 263 */       this.missCount.add(otherStats.missCount());
/* 264 */       this.loadSuccessCount.add(otherStats.loadSuccessCount());
/* 265 */       this.loadExceptionCount.add(otherStats.loadExceptionCount());
/* 266 */       this.totalLoadTime.add(otherStats.totalLoadTime());
/* 267 */       this.evictionCount.add(otherStats.evictionCount());
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static abstract interface StatsCounter
/*     */   {
/*     */     public abstract void recordHits(int paramInt);
/*     */ 
/*     */     public abstract void recordMisses(int paramInt);
/*     */ 
/*     */     public abstract void recordLoadSuccess(long paramLong);
/*     */ 
/*     */     public abstract void recordLoadException(long paramLong);
/*     */ 
/*     */     public abstract void recordEviction();
/*     */ 
/*     */     public abstract CacheStats snapshot();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.AbstractCache
 * JD-Core Version:    0.6.2
 */