/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Maps;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class AtomicLongMap<K>
/*     */ {
/*     */   private final ConcurrentHashMap<K, AtomicLong> map;
/*     */   private transient Map<K, Long> asMap;
/*     */ 
/*     */   private AtomicLongMap(ConcurrentHashMap<K, AtomicLong> map)
/*     */   {
/*  60 */     this.map = ((ConcurrentHashMap)Preconditions.checkNotNull(map));
/*     */   }
/*     */ 
/*     */   public static <K> AtomicLongMap<K> create()
/*     */   {
/*  67 */     return new AtomicLongMap(new ConcurrentHashMap());
/*     */   }
/*     */ 
/*     */   public static <K> AtomicLongMap<K> create(Map<? extends K, ? extends Long> m)
/*     */   {
/*  74 */     AtomicLongMap result = create();
/*  75 */     result.putAll(m);
/*  76 */     return result;
/*     */   }
/*     */ 
/*     */   public long get(K key)
/*     */   {
/*  84 */     AtomicLong atomic = (AtomicLong)this.map.get(key);
/*  85 */     return atomic == null ? 0L : atomic.get();
/*     */   }
/*     */ 
/*     */   public long incrementAndGet(K key)
/*     */   {
/*  92 */     return addAndGet(key, 1L);
/*     */   }
/*     */ 
/*     */   public long decrementAndGet(K key)
/*     */   {
/*  99 */     return addAndGet(key, -1L);
/*     */   }
/*     */ 
/*     */   public long addAndGet(K key, long delta)
/*     */   {
/* 108 */     AtomicLong atomic = (AtomicLong)this.map.get(key);
/* 109 */     if (atomic == null) {
/* 110 */       atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(delta));
/* 111 */       if (atomic == null) {
/* 112 */         return delta;
/*     */       }
/*     */     }
/*     */ 
/*     */     while (true)
/*     */     {
/* 118 */       long oldValue = atomic.get();
/* 119 */       if (oldValue == 0L)
/*     */       {
/* 121 */         if (!this.map.replace(key, atomic, new AtomicLong(delta))) break;
/* 122 */         return delta;
/*     */       }
/*     */ 
/* 128 */       long newValue = oldValue + delta;
/* 129 */       if (atomic.compareAndSet(oldValue, newValue))
/* 130 */         return newValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long getAndIncrement(K key)
/*     */   {
/* 141 */     return getAndAdd(key, 1L);
/*     */   }
/*     */ 
/*     */   public long getAndDecrement(K key)
/*     */   {
/* 148 */     return getAndAdd(key, -1L);
/*     */   }
/*     */ 
/*     */   public long getAndAdd(K key, long delta)
/*     */   {
/* 157 */     AtomicLong atomic = (AtomicLong)this.map.get(key);
/* 158 */     if (atomic == null) {
/* 159 */       atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(delta));
/* 160 */       if (atomic == null) {
/* 161 */         return 0L;
/*     */       }
/*     */     }
/*     */ 
/*     */     while (true)
/*     */     {
/* 167 */       long oldValue = atomic.get();
/* 168 */       if (oldValue == 0L)
/*     */       {
/* 170 */         if (!this.map.replace(key, atomic, new AtomicLong(delta))) break;
/* 171 */         return 0L;
/*     */       }
/*     */ 
/* 177 */       long newValue = oldValue + delta;
/* 178 */       if (atomic.compareAndSet(oldValue, newValue))
/* 179 */         return oldValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   public long put(K key, long newValue)
/*     */   {
/* 192 */     AtomicLong atomic = (AtomicLong)this.map.get(key);
/* 193 */     if (atomic == null) {
/* 194 */       atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(newValue));
/* 195 */       if (atomic == null) {
/* 196 */         return 0L;
/*     */       }
/*     */     }
/*     */ 
/*     */     while (true)
/*     */     {
/* 202 */       long oldValue = atomic.get();
/* 203 */       if (oldValue == 0L)
/*     */       {
/* 205 */         if (!this.map.replace(key, atomic, new AtomicLong(newValue))) break;
/* 206 */         return 0L;
/*     */       }
/*     */ 
/* 212 */       if (atomic.compareAndSet(oldValue, newValue))
/* 213 */         return oldValue;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void putAll(Map<? extends K, ? extends Long> m)
/*     */   {
/* 227 */     for (Map.Entry entry : m.entrySet())
/* 228 */       put(entry.getKey(), ((Long)entry.getValue()).longValue());
/*     */   }
/*     */ 
/*     */   public long remove(K key)
/*     */   {
/* 237 */     AtomicLong atomic = (AtomicLong)this.map.get(key);
/* 238 */     if (atomic == null) {
/* 239 */       return 0L;
/*     */     }
/*     */     while (true)
/*     */     {
/* 243 */       long oldValue = atomic.get();
/* 244 */       if ((oldValue == 0L) || (atomic.compareAndSet(oldValue, 0L)))
/*     */       {
/* 246 */         this.map.remove(key, atomic);
/*     */ 
/* 248 */         return oldValue;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeAllZeros()
/*     */   {
/* 260 */     for (Iterator i$ = this.map.keySet().iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 261 */       AtomicLong atomic = (AtomicLong)this.map.get(key);
/* 262 */       if ((atomic != null) && (atomic.get() == 0L))
/* 263 */         this.map.remove(key, atomic);
/*     */     }
/*     */   }
/*     */ 
/*     */   public long sum()
/*     */   {
/* 274 */     long sum = 0L;
/* 275 */     for (AtomicLong value : this.map.values()) {
/* 276 */       sum += value.get();
/*     */     }
/* 278 */     return sum;
/*     */   }
/*     */ 
/*     */   public Map<K, Long> asMap()
/*     */   {
/* 287 */     Map result = this.asMap;
/* 288 */     return result == null ? (this.asMap = createAsMap()) : result;
/*     */   }
/*     */ 
/*     */   private Map<K, Long> createAsMap() {
/* 292 */     return Collections.unmodifiableMap(Maps.transformValues(this.map, new Function()
/*     */     {
/*     */       public Long apply(AtomicLong atomic)
/*     */       {
/* 296 */         return Long.valueOf(atomic.get());
/*     */       }
/*     */     }));
/*     */   }
/*     */ 
/*     */   public boolean containsKey(Object key)
/*     */   {
/* 305 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 313 */     return this.map.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 320 */     return this.map.isEmpty();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 330 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 335 */     return this.map.toString();
/*     */   }
/*     */ 
/*     */   long putIfAbsent(K key, long newValue)
/*     */   {
/*     */     AtomicLong atomic;
/*     */     long oldValue;
/*     */     do
/*     */     {
/* 368 */       atomic = (AtomicLong)this.map.get(key);
/* 369 */       if (atomic == null) {
/* 370 */         atomic = (AtomicLong)this.map.putIfAbsent(key, new AtomicLong(newValue));
/* 371 */         if (atomic == null) {
/* 372 */           return 0L;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 377 */       oldValue = atomic.get();
/* 378 */       if (oldValue != 0L) break;
/*     */     }
/* 380 */     while (!this.map.replace(key, atomic, new AtomicLong(newValue)));
/* 381 */     return 0L;
/*     */ 
/* 387 */     return oldValue;
/*     */   }
/*     */ 
/*     */   boolean replace(K key, long expectedOldValue, long newValue)
/*     */   {
/* 400 */     if (expectedOldValue == 0L) {
/* 401 */       return putIfAbsent(key, newValue) == 0L;
/*     */     }
/* 403 */     AtomicLong atomic = (AtomicLong)this.map.get(key);
/* 404 */     return atomic == null ? false : atomic.compareAndSet(expectedOldValue, newValue);
/*     */   }
/*     */ 
/*     */   boolean remove(K key, long value)
/*     */   {
/* 413 */     AtomicLong atomic = (AtomicLong)this.map.get(key);
/* 414 */     if (atomic == null) {
/* 415 */       return false;
/*     */     }
/*     */ 
/* 418 */     long oldValue = atomic.get();
/* 419 */     if (oldValue != value) {
/* 420 */       return false;
/*     */     }
/*     */ 
/* 423 */     if ((oldValue == 0L) || (atomic.compareAndSet(oldValue, 0L)))
/*     */     {
/* 425 */       this.map.remove(key, atomic);
/*     */ 
/* 427 */       return true;
/*     */     }
/*     */ 
/* 431 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AtomicLongMap
 * JD-Core Version:    0.6.2
 */