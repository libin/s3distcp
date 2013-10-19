/*      */ package com.google.common.cache;
/*      */ 
/*      */ import com.google.common.annotations.VisibleForTesting;
/*      */ import com.google.common.base.Equivalence;
/*      */ import com.google.common.base.Equivalences;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Stopwatch;
/*      */ import com.google.common.base.Supplier;
/*      */ import com.google.common.base.Ticker;
/*      */ import com.google.common.collect.AbstractSequentialIterator;
/*      */ import com.google.common.collect.ImmutableMap;
/*      */ import com.google.common.collect.Iterators;
/*      */ import com.google.common.collect.Maps;
/*      */ import com.google.common.collect.Sets;
/*      */ import com.google.common.primitives.Ints;
/*      */ import com.google.common.util.concurrent.ExecutionError;
/*      */ import com.google.common.util.concurrent.Futures;
/*      */ import com.google.common.util.concurrent.ListenableFuture;
/*      */ import com.google.common.util.concurrent.ListeningExecutorService;
/*      */ import com.google.common.util.concurrent.MoreExecutors;
/*      */ import com.google.common.util.concurrent.SettableFuture;
/*      */ import com.google.common.util.concurrent.UncheckedExecutionException;
/*      */ import com.google.common.util.concurrent.Uninterruptibles;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.Serializable;
/*      */ import java.lang.ref.Reference;
/*      */ import java.lang.ref.ReferenceQueue;
/*      */ import java.lang.ref.SoftReference;
/*      */ import java.lang.ref.WeakReference;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.AbstractQueue;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Queue;
/*      */ import java.util.Set;
/*      */ import java.util.concurrent.Callable;
/*      */ import java.util.concurrent.ConcurrentLinkedQueue;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import java.util.concurrent.ExecutionException;
/*      */ import java.util.concurrent.TimeUnit;
/*      */ import java.util.concurrent.atomic.AtomicInteger;
/*      */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*      */ import java.util.concurrent.locks.ReentrantLock;
/*      */ import java.util.logging.Level;
/*      */ import java.util.logging.Logger;
/*      */ import javax.annotation.Nullable;
/*      */ import javax.annotation.concurrent.GuardedBy;
/*      */ 
/*      */ class LocalCache<K, V> extends AbstractMap<K, V>
/*      */   implements ConcurrentMap<K, V>
/*      */ {
/*      */   static final int MAXIMUM_CAPACITY = 1073741824;
/*      */   static final int MAX_SEGMENTS = 65536;
/*      */   static final int CONTAINS_VALUE_RETRIES = 3;
/*      */   static final int DRAIN_THRESHOLD = 63;
/*      */   static final int DRAIN_MAX = 16;
/*  154 */   static final Logger logger = Logger.getLogger(LocalCache.class.getName());
/*      */ 
/*  156 */   static final ListeningExecutorService sameThreadExecutor = MoreExecutors.sameThreadExecutor();
/*      */   final int segmentMask;
/*      */   final int segmentShift;
/*      */   final Segment<K, V>[] segments;
/*      */   final int concurrencyLevel;
/*      */   final Equivalence<Object> keyEquivalence;
/*      */   final Equivalence<Object> valueEquivalence;
/*      */   final Strength keyStrength;
/*      */   final Strength valueStrength;
/*      */   final long maxWeight;
/*      */   final Weigher<K, V> weigher;
/*      */   final long expireAfterAccessNanos;
/*      */   final long expireAfterWriteNanos;
/*      */   final long refreshNanos;
/*      */   final Queue<RemovalNotification<K, V>> removalNotificationQueue;
/*      */   final RemovalListener<K, V> removalListener;
/*      */   final Ticker ticker;
/*      */   final EntryFactory entryFactory;
/*      */   final AbstractCache.StatsCounter globalStatsCounter;
/*      */ 
/*      */   @Nullable
/*      */   final CacheLoader<? super K, V> defaultLoader;
/*  683 */   static final ValueReference<Object, Object> UNSET = new ValueReference()
/*      */   {
/*      */     public Object get() {
/*  686 */       return null;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/*  691 */       return 0;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getEntry()
/*      */     {
/*  696 */       return null;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> queue, Object value, LocalCache.ReferenceEntry<Object, Object> entry)
/*      */     {
/*  702 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/*  707 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/*  712 */       return false;
/*      */     }
/*      */ 
/*      */     public Object waitForValue()
/*      */     {
/*  717 */       return null;
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(Object newValue)
/*      */     {
/*      */     }
/*  683 */   };
/*      */ 
/* 1014 */   static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue()
/*      */   {
/*      */     public boolean offer(Object o) {
/* 1017 */       return true;
/*      */     }
/*      */ 
/*      */     public Object peek()
/*      */     {
/* 1022 */       return null;
/*      */     }
/*      */ 
/*      */     public Object poll()
/*      */     {
/* 1027 */       return null;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 1032 */       return 0;
/*      */     }
/*      */ 
/*      */     public Iterator<Object> iterator()
/*      */     {
/* 1037 */       return Iterators.emptyIterator();
/*      */     }
/* 1014 */   };
/*      */   Set<K> keySet;
/*      */   Collection<V> values;
/*      */   Set<Map.Entry<K, V>> entrySet;
/*      */ 
/*      */   LocalCache(CacheBuilder<? super K, ? super V> builder, @Nullable CacheLoader<? super K, V> loader)
/*      */   {
/*  236 */     this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
/*      */ 
/*  238 */     this.keyStrength = builder.getKeyStrength();
/*  239 */     this.valueStrength = builder.getValueStrength();
/*      */ 
/*  241 */     this.keyEquivalence = builder.getKeyEquivalence();
/*  242 */     this.valueEquivalence = builder.getValueEquivalence();
/*      */ 
/*  244 */     this.maxWeight = builder.getMaximumWeight();
/*  245 */     this.weigher = builder.getWeigher();
/*  246 */     this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
/*  247 */     this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
/*  248 */     this.refreshNanos = builder.getRefreshNanos();
/*      */ 
/*  250 */     this.removalListener = builder.getRemovalListener();
/*  251 */     this.removalNotificationQueue = (this.removalListener == CacheBuilder.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
/*      */ 
/*  255 */     this.ticker = builder.getTicker(recordsTime());
/*  256 */     this.entryFactory = EntryFactory.getFactory(this.keyStrength, usesAccessEntries(), usesWriteEntries());
/*  257 */     this.globalStatsCounter = ((AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*  258 */     this.defaultLoader = loader;
/*      */ 
/*  260 */     int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
/*  261 */     if ((evictsBySize()) && (!customWeigher())) {
/*  262 */       initialCapacity = Math.min(initialCapacity, (int)this.maxWeight);
/*      */     }
/*      */ 
/*  270 */     int segmentShift = 0;
/*  271 */     int segmentCount = 1;
/*      */ 
/*  273 */     while ((segmentCount < this.concurrencyLevel) && ((!evictsBySize()) || (segmentCount * 20 <= this.maxWeight))) {
/*  274 */       segmentShift++;
/*  275 */       segmentCount <<= 1;
/*      */     }
/*  277 */     this.segmentShift = (32 - segmentShift);
/*  278 */     this.segmentMask = (segmentCount - 1);
/*      */ 
/*  280 */     this.segments = newSegmentArray(segmentCount);
/*      */ 
/*  282 */     int segmentCapacity = initialCapacity / segmentCount;
/*  283 */     if (segmentCapacity * segmentCount < initialCapacity) {
/*  284 */       segmentCapacity++;
/*      */     }
/*      */ 
/*  287 */     int segmentSize = 1;
/*  288 */     while (segmentSize < segmentCapacity) {
/*  289 */       segmentSize <<= 1;
/*      */     }
/*      */ 
/*  292 */     if (evictsBySize())
/*      */     {
/*  294 */       long maxSegmentWeight = this.maxWeight / segmentCount + 1L;
/*  295 */       long remainder = this.maxWeight % segmentCount;
/*  296 */       for (int i = 0; i < this.segments.length; i++) {
/*  297 */         if (i == remainder) {
/*  298 */           maxSegmentWeight -= 1L;
/*      */         }
/*  300 */         this.segments[i] = createSegment(segmentSize, maxSegmentWeight, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */       }
/*      */     }
/*      */     else {
/*  304 */       for (int i = 0; i < this.segments.length; i++)
/*  305 */         this.segments[i] = createSegment(segmentSize, -1L, (AbstractCache.StatsCounter)builder.getStatsCounterSupplier().get());
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean evictsBySize()
/*      */   {
/*  312 */     return this.maxWeight >= 0L;
/*      */   }
/*      */ 
/*      */   boolean customWeigher() {
/*  316 */     return this.weigher != CacheBuilder.OneWeigher.INSTANCE;
/*      */   }
/*      */ 
/*      */   boolean expires() {
/*  320 */     return (expiresAfterWrite()) || (expiresAfterAccess());
/*      */   }
/*      */ 
/*      */   boolean expiresAfterWrite() {
/*  324 */     return this.expireAfterWriteNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean expiresAfterAccess() {
/*  328 */     return this.expireAfterAccessNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean refreshes() {
/*  332 */     return this.refreshNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean usesAccessQueue() {
/*  336 */     return (expiresAfterAccess()) || (evictsBySize());
/*      */   }
/*      */ 
/*      */   boolean usesWriteQueue() {
/*  340 */     return expiresAfterWrite();
/*      */   }
/*      */ 
/*      */   boolean recordsWrite() {
/*  344 */     return (expiresAfterWrite()) || (refreshes());
/*      */   }
/*      */ 
/*      */   boolean recordsAccess() {
/*  348 */     return expiresAfterAccess();
/*      */   }
/*      */ 
/*      */   boolean recordsTime() {
/*  352 */     return (recordsWrite()) || (recordsAccess());
/*      */   }
/*      */ 
/*      */   boolean usesWriteEntries() {
/*  356 */     return (usesWriteQueue()) || (recordsWrite());
/*      */   }
/*      */ 
/*      */   boolean usesAccessEntries() {
/*  360 */     return (usesAccessQueue()) || (recordsAccess());
/*      */   }
/*      */ 
/*      */   boolean usesKeyReferences() {
/*  364 */     return this.keyStrength != Strength.STRONG;
/*      */   }
/*      */ 
/*      */   boolean usesValueReferences() {
/*  368 */     return this.valueStrength != Strength.STRONG;
/*      */   }
/*      */ 
/*      */   static <K, V> ValueReference<K, V> unset()
/*      */   {
/*  729 */     return UNSET;
/*      */   }
/*      */ 
/*      */   static <K, V> ReferenceEntry<K, V> nullEntry()
/*      */   {
/* 1011 */     return NullEntry.INSTANCE;
/*      */   }
/*      */ 
/*      */   static <E> Queue<E> discardingQueue()
/*      */   {
/* 1046 */     return DISCARDING_QUEUE;
/*      */   }
/*      */ 
/*      */   static int rehash(int h)
/*      */   {
/* 1857 */     h += (h << 15 ^ 0xFFFFCD7D);
/* 1858 */     h ^= h >>> 10;
/* 1859 */     h += (h << 3);
/* 1860 */     h ^= h >>> 6;
/* 1861 */     h += (h << 2) + (h << 14);
/* 1862 */     return h ^ h >>> 16;
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable ReferenceEntry<K, V> next)
/*      */   {
/* 1871 */     return segmentFor(hash).newEntry(key, hash, next);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */   {
/* 1880 */     int hash = original.getHash();
/* 1881 */     return segmentFor(hash).copyEntry(original, newNext);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ValueReference<K, V> newValueReference(ReferenceEntry<K, V> entry, V value, int weight)
/*      */   {
/* 1890 */     int hash = entry.getHash();
/* 1891 */     return this.valueStrength.referenceValue(segmentFor(hash), entry, value, weight);
/*      */   }
/*      */ 
/*      */   int hash(Object key) {
/* 1895 */     int h = this.keyEquivalence.hash(key);
/* 1896 */     return rehash(h);
/*      */   }
/*      */ 
/*      */   void reclaimValue(ValueReference<K, V> valueReference) {
/* 1900 */     ReferenceEntry entry = valueReference.getEntry();
/* 1901 */     int hash = entry.getHash();
/* 1902 */     segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
/*      */   }
/*      */ 
/*      */   void reclaimKey(ReferenceEntry<K, V> entry) {
/* 1906 */     int hash = entry.getHash();
/* 1907 */     segmentFor(hash).reclaimKey(entry, hash);
/*      */   }
/*      */ 
/*      */   @VisibleForTesting
/*      */   boolean isLive(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1916 */     return segmentFor(entry.getHash()).getLiveValue(entry, now) != null;
/*      */   }
/*      */ 
/*      */   Segment<K, V> segmentFor(int hash)
/*      */   {
/* 1927 */     return this.segments[(hash >>> this.segmentShift & this.segmentMask)];
/*      */   }
/*      */ 
/*      */   Segment<K, V> createSegment(int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter)
/*      */   {
/* 1932 */     return new Segment(this, initialCapacity, maxSegmentWeight, statsCounter);
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   V getLiveValue(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1943 */     if (entry.getKey() == null) {
/* 1944 */       return null;
/*      */     }
/* 1946 */     Object value = entry.getValueReference().get();
/* 1947 */     if (value == null) {
/* 1948 */       return null;
/*      */     }
/*      */ 
/* 1951 */     if (isExpired(entry, now)) {
/* 1952 */       return null;
/*      */     }
/* 1954 */     return value;
/*      */   }
/*      */ 
/*      */   boolean isExpired(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1963 */     if ((expiresAfterAccess()) && (now - entry.getAccessTime() > this.expireAfterAccessNanos))
/*      */     {
/* 1965 */       return true;
/*      */     }
/* 1967 */     if ((expiresAfterWrite()) && (now - entry.getWriteTime() > this.expireAfterWriteNanos))
/*      */     {
/* 1969 */       return true;
/*      */     }
/* 1971 */     return false;
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectAccessOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next)
/*      */   {
/* 1978 */     previous.setNextInAccessQueue(next);
/* 1979 */     next.setPreviousInAccessQueue(previous);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyAccessOrder(ReferenceEntry<K, V> nulled) {
/* 1984 */     ReferenceEntry nullEntry = nullEntry();
/* 1985 */     nulled.setNextInAccessQueue(nullEntry);
/* 1986 */     nulled.setPreviousInAccessQueue(nullEntry);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectWriteOrder(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
/* 1991 */     previous.setNextInWriteQueue(next);
/* 1992 */     next.setPreviousInWriteQueue(previous);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyWriteOrder(ReferenceEntry<K, V> nulled) {
/* 1997 */     ReferenceEntry nullEntry = nullEntry();
/* 1998 */     nulled.setNextInWriteQueue(nullEntry);
/* 1999 */     nulled.setPreviousInWriteQueue(nullEntry);
/*      */   }
/*      */ 
/*      */   void processPendingNotifications()
/*      */   {
/*      */     RemovalNotification notification;
/* 2009 */     while ((notification = (RemovalNotification)this.removalNotificationQueue.poll()) != null)
/*      */       try {
/* 2011 */         this.removalListener.onRemoval(notification);
/*      */       } catch (Throwable e) {
/* 2013 */         logger.log(Level.WARNING, "Exception thrown by removal listener", e);
/*      */       }
/*      */   }
/*      */ 
/*      */   final Segment<K, V>[] newSegmentArray(int ssize)
/*      */   {
/* 2020 */     return new Segment[ssize];
/*      */   }
/*      */ 
/*      */   public void cleanUp()
/*      */   {
/* 3903 */     for (Segment segment : this.segments)
/* 3904 */       segment.cleanUp();
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/* 3919 */     long sum = 0L;
/* 3920 */     Segment[] segments = this.segments;
/* 3921 */     for (int i = 0; i < segments.length; i++) {
/* 3922 */       if (segments[i].count != 0) {
/* 3923 */         return false;
/*      */       }
/* 3925 */       sum += segments[i].modCount;
/*      */     }
/*      */ 
/* 3928 */     if (sum != 0L) {
/* 3929 */       for (int i = 0; i < segments.length; i++) {
/* 3930 */         if (segments[i].count != 0) {
/* 3931 */           return false;
/*      */         }
/* 3933 */         sum -= segments[i].modCount;
/*      */       }
/* 3935 */       if (sum != 0L) {
/* 3936 */         return false;
/*      */       }
/*      */     }
/* 3939 */     return true;
/*      */   }
/*      */ 
/*      */   long longSize() {
/* 3943 */     Segment[] segments = this.segments;
/* 3944 */     long sum = 0L;
/* 3945 */     for (int i = 0; i < segments.length; i++) {
/* 3946 */       sum += segments[i].count;
/*      */     }
/* 3948 */     return sum;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/* 3953 */     return Ints.saturatedCast(longSize());
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   public V get(@Nullable Object key)
/*      */   {
/* 3959 */     if (key == null) {
/* 3960 */       return null;
/*      */     }
/* 3962 */     int hash = hash(key);
/* 3963 */     return segmentFor(hash).get(key, hash);
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   public V getIfPresent(Object key) {
/* 3968 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3969 */     Object value = segmentFor(hash).get(key, hash);
/* 3970 */     if (value == null)
/* 3971 */       this.globalStatsCounter.recordMisses(1);
/*      */     else {
/* 3973 */       this.globalStatsCounter.recordHits(1);
/*      */     }
/* 3975 */     return value;
/*      */   }
/*      */ 
/*      */   V get(K key, CacheLoader<? super K, V> loader) throws ExecutionException {
/* 3979 */     int hash = hash(Preconditions.checkNotNull(key));
/* 3980 */     return segmentFor(hash).get(key, hash, loader);
/*      */   }
/*      */ 
/*      */   V getOrLoad(K key) throws ExecutionException {
/* 3984 */     return get(key, this.defaultLoader);
/*      */   }
/*      */ 
/*      */   ImmutableMap<K, V> getAllPresent(Iterable<?> keys) {
/* 3988 */     int hits = 0;
/* 3989 */     int misses = 0;
/*      */ 
/* 3991 */     Map result = Maps.newLinkedHashMap();
/* 3992 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 3993 */       Object value = get(key);
/* 3994 */       if (value == null) {
/* 3995 */         misses++;
/*      */       }
/*      */       else
/*      */       {
/* 3999 */         Object castKey = key;
/* 4000 */         result.put(castKey, value);
/* 4001 */         hits++;
/*      */       }
/*      */     }
/* 4004 */     this.globalStatsCounter.recordHits(hits);
/* 4005 */     this.globalStatsCounter.recordMisses(misses);
/* 4006 */     return ImmutableMap.copyOf(result);
/*      */   }
/*      */ 
/*      */   ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException {
/* 4010 */     int hits = 0;
/* 4011 */     int misses = 0;
/*      */ 
/* 4013 */     Map result = Maps.newLinkedHashMap();
/* 4014 */     Set keysToLoad = Sets.newLinkedHashSet();
/* 4015 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 4016 */       Object value = get(key);
/* 4017 */       if (!result.containsKey(key)) {
/* 4018 */         result.put(key, value);
/* 4019 */         if (value == null) {
/* 4020 */           misses++;
/* 4021 */           keysToLoad.add(key);
/*      */         } else {
/* 4023 */           hits++;
/*      */         }
/*      */       }
/*      */     }
/*      */     try
/*      */     {
/* 4029 */       if (!keysToLoad.isEmpty()) {
/*      */         Iterator i$;
/*      */         try { newEntries = loadAll(keysToLoad, this.defaultLoader);
/* 4032 */           for (i$ = keysToLoad.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 4033 */             Object value = newEntries.get(key);
/* 4034 */             if (value == null) {
/* 4035 */               throw new CacheLoader.InvalidCacheLoadException("loadAll failed to return a value for " + key);
/*      */             }
/* 4037 */             result.put(key, value);
/*      */           }
/*      */         }
/*      */         catch (CacheLoader.UnsupportedLoadingOperationException e)
/*      */         {
/* 4041 */           Map newEntries;
/*      */           Iterator i$;
/* 4041 */           i$ = keysToLoad.iterator(); } while (i$.hasNext()) { Object key = i$.next();
/* 4042 */           misses--;
/* 4043 */           result.put(key, get(key, this.defaultLoader));
/*      */         }
/*      */       }
/*      */ 
/* 4047 */       return ImmutableMap.copyOf(result);
/*      */     } finally {
/* 4049 */       this.globalStatsCounter.recordHits(hits);
/* 4050 */       this.globalStatsCounter.recordMisses(misses);
/*      */     }
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   Map<K, V> loadAll(Set<? extends K> keys, CacheLoader<? super K, V> loader)
/*      */     throws ExecutionException
/*      */   {
/* 4061 */     Stopwatch stopwatch = new Stopwatch().start();
/*      */ 
/* 4063 */     boolean success = false;
/*      */     Map result;
/*      */     try
/*      */     {
/* 4066 */       Map map = loader.loadAll(keys);
/* 4067 */       result = map;
/* 4068 */       success = true;
/*      */     } catch (CacheLoader.UnsupportedLoadingOperationException e) {
/* 4070 */       success = true;
/* 4071 */       throw e;
/*      */     } catch (InterruptedException e) {
/* 4073 */       Thread.currentThread().interrupt();
/* 4074 */       throw new ExecutionException(e);
/*      */     } catch (RuntimeException e) {
/* 4076 */       throw new UncheckedExecutionException(e);
/*      */     } catch (Exception e) {
/* 4078 */       throw new ExecutionException(e);
/*      */     } catch (Error e) {
/* 4080 */       throw new ExecutionError(e);
/*      */     } finally {
/* 4082 */       if (!success) {
/* 4083 */         this.globalStatsCounter.recordLoadException(stopwatch.elapsedTime(TimeUnit.NANOSECONDS));
/*      */       }
/*      */     }
/*      */ 
/* 4087 */     if (result == null) {
/* 4088 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsedTime(TimeUnit.NANOSECONDS));
/* 4089 */       throw new CacheLoader.InvalidCacheLoadException(loader + " returned null map from loadAll");
/*      */     }
/*      */ 
/* 4092 */     stopwatch.stop();
/*      */ 
/* 4094 */     boolean nullsPresent = false;
/* 4095 */     for (Map.Entry entry : result.entrySet()) {
/* 4096 */       Object key = entry.getKey();
/* 4097 */       Object value = entry.getValue();
/* 4098 */       if ((key == null) || (value == null))
/*      */       {
/* 4100 */         nullsPresent = true;
/*      */       }
/* 4102 */       else put(key, value);
/*      */ 
/*      */     }
/*      */ 
/* 4106 */     if (nullsPresent) {
/* 4107 */       this.globalStatsCounter.recordLoadException(stopwatch.elapsedTime(TimeUnit.NANOSECONDS));
/* 4108 */       throw new CacheLoader.InvalidCacheLoadException(loader + " returned null keys or values from loadAll");
/*      */     }
/*      */ 
/* 4112 */     this.globalStatsCounter.recordLoadSuccess(stopwatch.elapsedTime(TimeUnit.NANOSECONDS));
/* 4113 */     return result;
/*      */   }
/*      */ 
/*      */   ReferenceEntry<K, V> getEntry(@Nullable Object key)
/*      */   {
/* 4122 */     if (key == null) {
/* 4123 */       return null;
/*      */     }
/* 4125 */     int hash = hash(key);
/* 4126 */     return segmentFor(hash).getEntry(key, hash);
/*      */   }
/*      */ 
/*      */   ReferenceEntry<K, V> getLiveEntry(@Nullable Object key)
/*      */   {
/* 4134 */     if (key == null) {
/* 4135 */       return null;
/*      */     }
/* 4137 */     int hash = hash(key);
/* 4138 */     return segmentFor(hash).getLiveEntry(key, hash, this.ticker.read());
/*      */   }
/*      */ 
/*      */   void refresh(K key) {
/* 4142 */     int hash = hash(Preconditions.checkNotNull(key));
/* 4143 */     segmentFor(hash).refresh(key, hash, this.defaultLoader);
/*      */   }
/*      */ 
/*      */   public boolean containsKey(@Nullable Object key)
/*      */   {
/* 4149 */     if (key == null) {
/* 4150 */       return false;
/*      */     }
/* 4152 */     int hash = hash(key);
/* 4153 */     return segmentFor(hash).containsKey(key, hash);
/*      */   }
/*      */ 
/*      */   public boolean containsValue(@Nullable Object value)
/*      */   {
/* 4159 */     if (value == null) {
/* 4160 */       return false;
/*      */     }
/*      */ 
/* 4168 */     long now = this.ticker.read();
/* 4169 */     Segment[] segments = this.segments;
/* 4170 */     long last = -1L;
/* 4171 */     for (int i = 0; i < 3; i++) {
/* 4172 */       long sum = 0L;
/* 4173 */       for (Segment segment : segments)
/*      */       {
/* 4176 */         int c = segment.count;
/*      */ 
/* 4178 */         AtomicReferenceArray table = segment.table;
/* 4179 */         for (int j = 0; j < table.length(); j++) {
/* 4180 */           for (ReferenceEntry e = (ReferenceEntry)table.get(j); e != null; e = e.getNext()) {
/* 4181 */             Object v = segment.getLiveValue(e, now);
/* 4182 */             if ((v != null) && (this.valueEquivalence.equivalent(value, v))) {
/* 4183 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 4187 */         sum += segment.modCount;
/*      */       }
/* 4189 */       if (sum == last) {
/*      */         break;
/*      */       }
/* 4192 */       last = sum;
/*      */     }
/* 4194 */     return false;
/*      */   }
/*      */ 
/*      */   public V put(K key, V value)
/*      */   {
/* 4199 */     Preconditions.checkNotNull(key);
/* 4200 */     Preconditions.checkNotNull(value);
/* 4201 */     int hash = hash(key);
/* 4202 */     return segmentFor(hash).put(key, hash, value, false);
/*      */   }
/*      */ 
/*      */   public V putIfAbsent(K key, V value)
/*      */   {
/* 4207 */     Preconditions.checkNotNull(key);
/* 4208 */     Preconditions.checkNotNull(value);
/* 4209 */     int hash = hash(key);
/* 4210 */     return segmentFor(hash).put(key, hash, value, true);
/*      */   }
/*      */ 
/*      */   public void putAll(Map<? extends K, ? extends V> m)
/*      */   {
/* 4215 */     for (Map.Entry e : m.entrySet())
/* 4216 */       put(e.getKey(), e.getValue());
/*      */   }
/*      */ 
/*      */   public V remove(@Nullable Object key)
/*      */   {
/* 4222 */     if (key == null) {
/* 4223 */       return null;
/*      */     }
/* 4225 */     int hash = hash(key);
/* 4226 */     return segmentFor(hash).remove(key, hash);
/*      */   }
/*      */ 
/*      */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*      */   {
/* 4231 */     if ((key == null) || (value == null)) {
/* 4232 */       return false;
/*      */     }
/* 4234 */     int hash = hash(key);
/* 4235 */     return segmentFor(hash).remove(key, hash, value);
/*      */   }
/*      */ 
/*      */   public boolean replace(K key, @Nullable V oldValue, V newValue)
/*      */   {
/* 4240 */     Preconditions.checkNotNull(key);
/* 4241 */     Preconditions.checkNotNull(newValue);
/* 4242 */     if (oldValue == null) {
/* 4243 */       return false;
/*      */     }
/* 4245 */     int hash = hash(key);
/* 4246 */     return segmentFor(hash).replace(key, hash, oldValue, newValue);
/*      */   }
/*      */ 
/*      */   public V replace(K key, V value)
/*      */   {
/* 4251 */     Preconditions.checkNotNull(key);
/* 4252 */     Preconditions.checkNotNull(value);
/* 4253 */     int hash = hash(key);
/* 4254 */     return segmentFor(hash).replace(key, hash, value);
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/* 4259 */     for (Segment segment : this.segments)
/* 4260 */       segment.clear();
/*      */   }
/*      */ 
/*      */   void invalidateAll(Iterable<?> keys)
/*      */   {
/* 4266 */     for (Iterator i$ = keys.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 4267 */       remove(key);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/* 4276 */     Set ks = this.keySet;
/* 4277 */     return this.keySet = new KeySet();
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/* 4285 */     Collection vs = this.values;
/* 4286 */     return this.values = new Values();
/*      */   }
/*      */ 
/*      */   public Set<Map.Entry<K, V>> entrySet()
/*      */   {
/* 4294 */     Set es = this.entrySet;
/* 4295 */     return this.entrySet = new EntrySet();
/*      */   }
/*      */ 
/*      */   static class LocalLoadingCache<K, V> extends LocalCache.LocalManualCache<K, V>
/*      */     implements LoadingCache<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */     LocalLoadingCache(CacheBuilder<? super K, ? super V> builder, CacheLoader<? super K, V> loader)
/*      */     {
/* 4861 */       super(null);
/*      */     }
/*      */ 
/*      */     public V get(K key)
/*      */       throws ExecutionException
/*      */     {
/* 4868 */       return this.localCache.getOrLoad(key);
/*      */     }
/*      */ 
/*      */     public V getUnchecked(K key)
/*      */     {
/*      */       try {
/* 4874 */         return get(key);
/*      */       } catch (ExecutionException e) {
/* 4876 */         throw new UncheckedExecutionException(e.getCause());
/*      */       }
/*      */     }
/*      */ 
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4882 */       return this.localCache.getAll(keys);
/*      */     }
/*      */ 
/*      */     public void refresh(K key)
/*      */     {
/* 4887 */       this.localCache.refresh(key);
/*      */     }
/*      */ 
/*      */     public final V apply(K key)
/*      */     {
/* 4892 */       return getUnchecked(key);
/*      */     }
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4900 */       return new LocalCache.LoadingSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LocalManualCache<K, V>
/*      */     implements Cache<K, V>, Serializable
/*      */   {
/*      */     final LocalCache<K, V> localCache;
/*      */     private static final long serialVersionUID = 1L;
/*      */ 
/*      */     LocalManualCache(CacheBuilder<? super K, ? super V> builder)
/*      */     {
/* 4765 */       this(new LocalCache(builder, null));
/*      */     }
/*      */ 
/*      */     private LocalManualCache(LocalCache<K, V> localCache) {
/* 4769 */       this.localCache = localCache;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     public V getIfPresent(Object key)
/*      */     {
/* 4777 */       return this.localCache.getIfPresent(key);
/*      */     }
/*      */ 
/*      */     public V get(K key, final Callable<? extends V> valueLoader) throws ExecutionException
/*      */     {
/* 4782 */       Preconditions.checkNotNull(valueLoader);
/* 4783 */       return this.localCache.get(key, new CacheLoader()
/*      */       {
/*      */         public V load(Object key) throws Exception {
/* 4786 */           return valueLoader.call();
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public ImmutableMap<K, V> getAllPresent(Iterable<?> keys)
/*      */     {
/* 4793 */       return this.localCache.getAllPresent(keys);
/*      */     }
/*      */ 
/*      */     public void put(K key, V value)
/*      */     {
/* 4798 */       this.localCache.put(key, value);
/*      */     }
/*      */ 
/*      */     public void putAll(Map<? extends K, ? extends V> m)
/*      */     {
/* 4803 */       this.localCache.putAll(m);
/*      */     }
/*      */ 
/*      */     public void invalidate(Object key)
/*      */     {
/* 4808 */       Preconditions.checkNotNull(key);
/* 4809 */       this.localCache.remove(key);
/*      */     }
/*      */ 
/*      */     public void invalidateAll(Iterable<?> keys)
/*      */     {
/* 4814 */       this.localCache.invalidateAll(keys);
/*      */     }
/*      */ 
/*      */     public void invalidateAll()
/*      */     {
/* 4819 */       this.localCache.clear();
/*      */     }
/*      */ 
/*      */     public long size()
/*      */     {
/* 4824 */       return this.localCache.longSize();
/*      */     }
/*      */ 
/*      */     public ConcurrentMap<K, V> asMap()
/*      */     {
/* 4829 */       return this.localCache;
/*      */     }
/*      */ 
/*      */     public CacheStats stats()
/*      */     {
/* 4834 */       AbstractCache.SimpleStatsCounter aggregator = new AbstractCache.SimpleStatsCounter();
/* 4835 */       aggregator.incrementBy(this.localCache.globalStatsCounter);
/* 4836 */       for (LocalCache.Segment segment : this.localCache.segments) {
/* 4837 */         aggregator.incrementBy(segment.statsCounter);
/*      */       }
/* 4839 */       return aggregator.snapshot();
/*      */     }
/*      */ 
/*      */     public void cleanUp()
/*      */     {
/* 4844 */       this.localCache.cleanUp();
/*      */     }
/*      */ 
/*      */     Object writeReplace()
/*      */     {
/* 4852 */       return new LocalCache.ManualSerializationProxy(this.localCache);
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class LoadingSerializationProxy<K, V> extends LocalCache.ManualSerializationProxy<K, V>
/*      */     implements LoadingCache<K, V>, Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     transient LoadingCache<K, V> autoDelegate;
/*      */ 
/*      */     LoadingSerializationProxy(LocalCache<K, V> cache)
/*      */     {
/* 4722 */       super();
/*      */     }
/*      */ 
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4726 */       in.defaultReadObject();
/* 4727 */       CacheBuilder builder = recreateCacheBuilder();
/* 4728 */       this.autoDelegate = builder.build(this.loader);
/*      */     }
/*      */ 
/*      */     public V get(K key) throws ExecutionException
/*      */     {
/* 4733 */       return this.autoDelegate.get(key);
/*      */     }
/*      */ 
/*      */     public V getUnchecked(K key)
/*      */     {
/* 4738 */       return this.autoDelegate.getUnchecked(key);
/*      */     }
/*      */ 
/*      */     public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*      */     {
/* 4743 */       return this.autoDelegate.getAll(keys);
/*      */     }
/*      */ 
/*      */     public final V apply(K key)
/*      */     {
/* 4748 */       return this.autoDelegate.apply(key);
/*      */     }
/*      */ 
/*      */     public void refresh(K key)
/*      */     {
/* 4753 */       this.autoDelegate.refresh(key);
/*      */     }
/*      */ 
/*      */     private Object readResolve() {
/* 4757 */       return this.autoDelegate;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ManualSerializationProxy<K, V> extends ForwardingCache<K, V>
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 1L;
/*      */     final LocalCache.Strength keyStrength;
/*      */     final LocalCache.Strength valueStrength;
/*      */     final Equivalence<Object> keyEquivalence;
/*      */     final Equivalence<Object> valueEquivalence;
/*      */     final long expireAfterWriteNanos;
/*      */     final long expireAfterAccessNanos;
/*      */     final long maxWeight;
/*      */     final Weigher<K, V> weigher;
/*      */     final int concurrencyLevel;
/*      */     final RemovalListener<? super K, ? super V> removalListener;
/*      */     final Ticker ticker;
/*      */     final CacheLoader<? super K, V> loader;
/*      */     transient Cache<K, V> delegate;
/*      */ 
/*      */     ManualSerializationProxy(LocalCache<K, V> cache)
/*      */     {
/* 4623 */       this(cache.keyStrength, cache.valueStrength, cache.keyEquivalence, cache.valueEquivalence, cache.expireAfterWriteNanos, cache.expireAfterAccessNanos, cache.maxWeight, cache.weigher, cache.concurrencyLevel, cache.removalListener, cache.ticker, cache.defaultLoader);
/*      */     }
/*      */ 
/*      */     private ManualSerializationProxy(LocalCache.Strength keyStrength, LocalCache.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, long maxWeight, Weigher<K, V> weigher, int concurrencyLevel, RemovalListener<? super K, ? super V> removalListener, Ticker ticker, CacheLoader<? super K, V> loader)
/*      */     {
/* 4645 */       this.keyStrength = keyStrength;
/* 4646 */       this.valueStrength = valueStrength;
/* 4647 */       this.keyEquivalence = keyEquivalence;
/* 4648 */       this.valueEquivalence = valueEquivalence;
/* 4649 */       this.expireAfterWriteNanos = expireAfterWriteNanos;
/* 4650 */       this.expireAfterAccessNanos = expireAfterAccessNanos;
/* 4651 */       this.maxWeight = maxWeight;
/* 4652 */       this.weigher = weigher;
/* 4653 */       this.concurrencyLevel = concurrencyLevel;
/* 4654 */       this.removalListener = removalListener;
/* 4655 */       this.ticker = ((ticker == Ticker.systemTicker()) || (ticker == CacheBuilder.NULL_TICKER) ? null : ticker);
/*      */ 
/* 4657 */       this.loader = loader;
/*      */     }
/*      */ 
/*      */     CacheBuilder<Object, Object> recreateCacheBuilder() {
/* 4661 */       CacheBuilder builder = CacheBuilder.newBuilder().setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel);
/*      */ 
/* 4667 */       builder.strictParsing = false;
/* 4668 */       builder.removalListener(this.removalListener);
/* 4669 */       if (this.expireAfterWriteNanos > 0L) {
/* 4670 */         builder.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4672 */       if (this.expireAfterAccessNanos > 0L) {
/* 4673 */         builder.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4675 */       if (this.weigher != CacheBuilder.OneWeigher.INSTANCE) {
/* 4676 */         builder.weigher(this.weigher);
/* 4677 */         if (this.maxWeight != -1L) {
/* 4678 */           builder.maximumWeight(this.maxWeight);
/*      */         }
/*      */       }
/* 4681 */       else if (this.maxWeight != -1L) {
/* 4682 */         builder.maximumSize(this.maxWeight);
/*      */       }
/*      */ 
/* 4685 */       if (this.ticker != null) {
/* 4686 */         builder.ticker(this.ticker);
/*      */       }
/* 4688 */       return builder;
/*      */     }
/*      */ 
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4692 */       in.defaultReadObject();
/* 4693 */       CacheBuilder builder = recreateCacheBuilder();
/* 4694 */       this.delegate = builder.build();
/*      */     }
/*      */ 
/*      */     private Object readResolve() {
/* 4698 */       return this.delegate;
/*      */     }
/*      */ 
/*      */     protected Cache<K, V> delegate()
/*      */     {
/* 4703 */       return this.delegate;
/*      */     }
/*      */   }
/*      */ 
/*      */   final class EntrySet extends AbstractSet<Map.Entry<K, V>>
/*      */   {
/*      */     EntrySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<Map.Entry<K, V>> iterator()
/*      */     {
/* 4549 */       return new LocalCache.EntryIterator(LocalCache.this);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 4554 */       if (!(o instanceof Map.Entry)) {
/* 4555 */         return false;
/*      */       }
/* 4557 */       Map.Entry e = (Map.Entry)o;
/* 4558 */       Object key = e.getKey();
/* 4559 */       if (key == null) {
/* 4560 */         return false;
/*      */       }
/* 4562 */       Object v = LocalCache.this.get(key);
/*      */ 
/* 4564 */       return (v != null) && (LocalCache.this.valueEquivalence.equivalent(e.getValue(), v));
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 4569 */       if (!(o instanceof Map.Entry)) {
/* 4570 */         return false;
/*      */       }
/* 4572 */       Map.Entry e = (Map.Entry)o;
/* 4573 */       Object key = e.getKey();
/* 4574 */       return (key != null) && (LocalCache.this.remove(key, e.getValue()));
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 4579 */       return LocalCache.this.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 4584 */       return LocalCache.this.isEmpty();
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 4589 */       LocalCache.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class Values extends AbstractCollection<V>
/*      */   {
/*      */     Values()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<V> iterator()
/*      */     {
/* 4521 */       return new LocalCache.ValueIterator(LocalCache.this);
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 4526 */       return LocalCache.this.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 4531 */       return LocalCache.this.isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 4536 */       return LocalCache.this.containsValue(o);
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 4541 */       LocalCache.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class KeySet extends AbstractSet<K>
/*      */   {
/*      */     KeySet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public Iterator<K> iterator()
/*      */     {
/* 4488 */       return new LocalCache.KeyIterator(LocalCache.this);
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 4493 */       return LocalCache.this.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 4498 */       return LocalCache.this.isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 4503 */       return LocalCache.this.containsKey(o);
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 4508 */       return LocalCache.this.remove(o) != null;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 4513 */       LocalCache.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class EntryIterator extends LocalCache<K, V>.HashIterator
/*      */     implements Iterator<Map.Entry<K, V>>
/*      */   {
/*      */     EntryIterator()
/*      */     {
/* 4476 */       super();
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> next() {
/* 4480 */       return nextEntry();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class WriteThroughEntry
/*      */     implements Map.Entry<K, V>
/*      */   {
/*      */     final K key;
/*      */     V value;
/*      */ 
/*      */     WriteThroughEntry(V key)
/*      */     {
/* 4433 */       this.key = key;
/* 4434 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 4439 */       return this.key;
/*      */     }
/*      */ 
/*      */     public V getValue()
/*      */     {
/* 4444 */       return this.value;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object)
/*      */     {
/* 4450 */       if ((object instanceof Map.Entry)) {
/* 4451 */         Map.Entry that = (Map.Entry)object;
/* 4452 */         return (this.key.equals(that.getKey())) && (this.value.equals(that.getValue()));
/*      */       }
/* 4454 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 4460 */       return this.key.hashCode() ^ this.value.hashCode();
/*      */     }
/*      */ 
/*      */     public V setValue(V newValue)
/*      */     {
/* 4465 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 4472 */       return getKey() + "=" + getValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class ValueIterator extends LocalCache<K, V>.HashIterator
/*      */     implements Iterator<V>
/*      */   {
/*      */     ValueIterator()
/*      */     {
/* 4416 */       super();
/*      */     }
/*      */ 
/*      */     public V next() {
/* 4420 */       return nextEntry().getValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class KeyIterator extends LocalCache<K, V>.HashIterator
/*      */     implements Iterator<K>
/*      */   {
/*      */     KeyIterator()
/*      */     {
/* 4408 */       super();
/*      */     }
/*      */ 
/*      */     public K next() {
/* 4412 */       return nextEntry().getKey();
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract class HashIterator
/*      */   {
/*      */     int nextSegmentIndex;
/*      */     int nextTableIndex;
/*      */     LocalCache.Segment<K, V> currentSegment;
/*      */     AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> currentTable;
/*      */     LocalCache.ReferenceEntry<K, V> nextEntry;
/*      */     LocalCache<K, V>.WriteThroughEntry nextExternal;
/*      */     LocalCache<K, V>.WriteThroughEntry lastReturned;
/*      */ 
/*      */     HashIterator()
/*      */     {
/* 4311 */       this.nextSegmentIndex = (LocalCache.this.segments.length - 1);
/* 4312 */       this.nextTableIndex = -1;
/* 4313 */       advance();
/*      */     }
/*      */ 
/*      */     final void advance() {
/* 4317 */       this.nextExternal = null;
/*      */ 
/* 4319 */       if (nextInChain()) {
/* 4320 */         return;
/*      */       }
/*      */ 
/* 4323 */       if (nextInTable()) {
/* 4324 */         return;
/*      */       }
/*      */ 
/* 4327 */       while (this.nextSegmentIndex >= 0) {
/* 4328 */         this.currentSegment = LocalCache.this.segments[(this.nextSegmentIndex--)];
/* 4329 */         if (this.currentSegment.count != 0) {
/* 4330 */           this.currentTable = this.currentSegment.table;
/* 4331 */           this.nextTableIndex = (this.currentTable.length() - 1);
/* 4332 */           if (nextInTable());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean nextInChain()
/*      */     {
/* 4343 */       if (this.nextEntry != null) {
/* 4344 */         for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
/* 4345 */           if (advanceTo(this.nextEntry)) {
/* 4346 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 4350 */       return false;
/*      */     }
/*      */ 
/*      */     boolean nextInTable()
/*      */     {
/* 4357 */       while (this.nextTableIndex >= 0) {
/* 4358 */         if (((this.nextEntry = (LocalCache.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) != null) && (
/* 4359 */           (advanceTo(this.nextEntry)) || (nextInChain()))) {
/* 4360 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 4364 */       return false;
/*      */     }
/*      */ 
/*      */     boolean advanceTo(LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/*      */       try
/*      */       {
/* 4373 */         long now = LocalCache.this.ticker.read();
/* 4374 */         Object key = entry.getKey();
/* 4375 */         Object value = LocalCache.this.getLiveValue(entry, now);
/*      */         boolean bool;
/* 4376 */         if (value != null) {
/* 4377 */           this.nextExternal = new LocalCache.WriteThroughEntry(LocalCache.this, key, value);
/* 4378 */           return true;
/*      */         }
/*      */ 
/* 4381 */         return false;
/*      */       }
/*      */       finally {
/* 4384 */         this.currentSegment.postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean hasNext() {
/* 4389 */       return this.nextExternal != null;
/*      */     }
/*      */ 
/*      */     LocalCache<K, V>.WriteThroughEntry nextEntry() {
/* 4393 */       if (this.nextExternal == null) {
/* 4394 */         throw new NoSuchElementException();
/*      */       }
/* 4396 */       this.lastReturned = this.nextExternal;
/* 4397 */       advance();
/* 4398 */       return this.lastReturned;
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 4402 */       Preconditions.checkState(this.lastReturned != null);
/* 4403 */       LocalCache.this.remove(this.lastReturned.getKey());
/* 4404 */       this.lastReturned = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class AccessQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>>
/*      */   {
/* 3775 */     final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry()
/*      */     {
/* 3785 */       LocalCache.ReferenceEntry<K, V> nextAccess = this;
/*      */ 
/* 3797 */       LocalCache.ReferenceEntry<K, V> previousAccess = this;
/*      */ 
/*      */       public long getAccessTime()
/*      */       {
/* 3779 */         return 9223372036854775807L;
/*      */       }
/*      */ 
/*      */       public void setAccessTime(long time)
/*      */       {
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */       {
/* 3789 */         return this.nextAccess;
/*      */       }
/*      */ 
/*      */       public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */       {
/* 3794 */         this.nextAccess = next;
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */       {
/* 3801 */         return this.previousAccess;
/*      */       }
/*      */ 
/*      */       public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */       {
/* 3806 */         this.previousAccess = previous;
/*      */       }
/* 3775 */     };
/*      */ 
/*      */     public boolean offer(LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3815 */       LocalCache.connectAccessOrder(entry.getPreviousInAccessQueue(), entry.getNextInAccessQueue());
/*      */ 
/* 3818 */       LocalCache.connectAccessOrder(this.head.getPreviousInAccessQueue(), entry);
/* 3819 */       LocalCache.connectAccessOrder(entry, this.head);
/*      */ 
/* 3821 */       return true;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> peek()
/*      */     {
/* 3826 */       LocalCache.ReferenceEntry next = this.head.getNextInAccessQueue();
/* 3827 */       return next == this.head ? null : next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> poll()
/*      */     {
/* 3832 */       LocalCache.ReferenceEntry next = this.head.getNextInAccessQueue();
/* 3833 */       if (next == this.head) {
/* 3834 */         return null;
/*      */       }
/*      */ 
/* 3837 */       remove(next);
/* 3838 */       return next;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3844 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3845 */       LocalCache.ReferenceEntry previous = e.getPreviousInAccessQueue();
/* 3846 */       LocalCache.ReferenceEntry next = e.getNextInAccessQueue();
/* 3847 */       LocalCache.connectAccessOrder(previous, next);
/* 3848 */       LocalCache.nullifyAccessOrder(e);
/*      */ 
/* 3850 */       return next != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3856 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3857 */       return e.getNextInAccessQueue() != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3862 */       return this.head.getNextInAccessQueue() == this.head;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3867 */       int size = 0;
/* 3868 */       for (LocalCache.ReferenceEntry e = this.head.getNextInAccessQueue(); e != this.head; 
/* 3869 */         e = e.getNextInAccessQueue()) {
/* 3870 */         size++;
/*      */       }
/* 3872 */       return size;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3877 */       LocalCache.ReferenceEntry e = this.head.getNextInAccessQueue();
/* 3878 */       while (e != this.head) {
/* 3879 */         LocalCache.ReferenceEntry next = e.getNextInAccessQueue();
/* 3880 */         LocalCache.nullifyAccessOrder(e);
/* 3881 */         e = next;
/*      */       }
/*      */ 
/* 3884 */       this.head.setNextInAccessQueue(this.head);
/* 3885 */       this.head.setPreviousInAccessQueue(this.head);
/*      */     }
/*      */ 
/*      */     public Iterator<LocalCache.ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3890 */       return new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> previous) {
/* 3893 */           LocalCache.ReferenceEntry next = previous.getNextInAccessQueue();
/* 3894 */           return next == LocalCache.AccessQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WriteQueue<K, V> extends AbstractQueue<LocalCache.ReferenceEntry<K, V>>
/*      */   {
/* 3638 */     final LocalCache.ReferenceEntry<K, V> head = new LocalCache.AbstractReferenceEntry()
/*      */     {
/* 3648 */       LocalCache.ReferenceEntry<K, V> nextWrite = this;
/*      */ 
/* 3660 */       LocalCache.ReferenceEntry<K, V> previousWrite = this;
/*      */ 
/*      */       public long getWriteTime()
/*      */       {
/* 3642 */         return 9223372036854775807L;
/*      */       }
/*      */ 
/*      */       public void setWriteTime(long time)
/*      */       {
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */       {
/* 3652 */         return this.nextWrite;
/*      */       }
/*      */ 
/*      */       public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */       {
/* 3657 */         this.nextWrite = next;
/*      */       }
/*      */ 
/*      */       public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */       {
/* 3664 */         return this.previousWrite;
/*      */       }
/*      */ 
/*      */       public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */       {
/* 3669 */         this.previousWrite = previous;
/*      */       }
/* 3638 */     };
/*      */ 
/*      */     public boolean offer(LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3678 */       LocalCache.connectWriteOrder(entry.getPreviousInWriteQueue(), entry.getNextInWriteQueue());
/*      */ 
/* 3681 */       LocalCache.connectWriteOrder(this.head.getPreviousInWriteQueue(), entry);
/* 3682 */       LocalCache.connectWriteOrder(entry, this.head);
/*      */ 
/* 3684 */       return true;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> peek()
/*      */     {
/* 3689 */       LocalCache.ReferenceEntry next = this.head.getNextInWriteQueue();
/* 3690 */       return next == this.head ? null : next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> poll()
/*      */     {
/* 3695 */       LocalCache.ReferenceEntry next = this.head.getNextInWriteQueue();
/* 3696 */       if (next == this.head) {
/* 3697 */         return null;
/*      */       }
/*      */ 
/* 3700 */       remove(next);
/* 3701 */       return next;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3707 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3708 */       LocalCache.ReferenceEntry previous = e.getPreviousInWriteQueue();
/* 3709 */       LocalCache.ReferenceEntry next = e.getNextInWriteQueue();
/* 3710 */       LocalCache.connectWriteOrder(previous, next);
/* 3711 */       LocalCache.nullifyWriteOrder(e);
/*      */ 
/* 3713 */       return next != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3719 */       LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)o;
/* 3720 */       return e.getNextInWriteQueue() != LocalCache.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3725 */       return this.head.getNextInWriteQueue() == this.head;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3730 */       int size = 0;
/* 3731 */       for (LocalCache.ReferenceEntry e = this.head.getNextInWriteQueue(); e != this.head; 
/* 3732 */         e = e.getNextInWriteQueue()) {
/* 3733 */         size++;
/*      */       }
/* 3735 */       return size;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3740 */       LocalCache.ReferenceEntry e = this.head.getNextInWriteQueue();
/* 3741 */       while (e != this.head) {
/* 3742 */         LocalCache.ReferenceEntry next = e.getNextInWriteQueue();
/* 3743 */         LocalCache.nullifyWriteOrder(e);
/* 3744 */         e = next;
/*      */       }
/*      */ 
/* 3747 */       this.head.setNextInWriteQueue(this.head);
/* 3748 */       this.head.setPreviousInWriteQueue(this.head);
/*      */     }
/*      */ 
/*      */     public Iterator<LocalCache.ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3753 */       return new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected LocalCache.ReferenceEntry<K, V> computeNext(LocalCache.ReferenceEntry<K, V> previous) {
/* 3756 */           LocalCache.ReferenceEntry next = previous.getNextInWriteQueue();
/* 3757 */           return next == LocalCache.WriteQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   static class LoadingValueReference<K, V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     volatile LocalCache.ValueReference<K, V> oldValue;
/* 3511 */     final SettableFuture<V> futureValue = SettableFuture.create();
/* 3512 */     final Stopwatch stopwatch = new Stopwatch();
/*      */ 
/*      */     public LoadingValueReference() {
/* 3515 */       this(LocalCache.unset());
/*      */     }
/*      */ 
/*      */     public LoadingValueReference(LocalCache.ValueReference<K, V> oldValue) {
/* 3519 */       this.oldValue = oldValue;
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 3524 */       return true;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 3529 */       return this.oldValue.isActive();
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 3534 */       return this.oldValue.getWeight();
/*      */     }
/*      */ 
/*      */     public boolean set(@Nullable V newValue) {
/* 3538 */       return this.futureValue.set(newValue);
/*      */     }
/*      */ 
/*      */     public boolean setException(Throwable t) {
/* 3542 */       return setException(this.futureValue, t);
/*      */     }
/*      */ 
/*      */     private static boolean setException(SettableFuture<?> future, Throwable t) {
/*      */       try {
/* 3547 */         return future.setException(t);
/*      */       } catch (Error e) {
/*      */       }
/* 3550 */       return false;
/*      */     }
/*      */ 
/*      */     private ListenableFuture<V> fullyFailedFuture(Throwable t)
/*      */     {
/* 3555 */       SettableFuture future = SettableFuture.create();
/* 3556 */       setException(future, t);
/* 3557 */       return future;
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(@Nullable V newValue)
/*      */     {
/* 3562 */       if (newValue != null)
/*      */       {
/* 3565 */         set(newValue);
/*      */       }
/*      */       else
/* 3568 */         this.oldValue = LocalCache.unset();
/*      */     }
/*      */ 
/*      */     public ListenableFuture<V> loadFuture(K key, CacheLoader<? super K, V> loader)
/*      */     {
/* 3575 */       this.stopwatch.start();
/* 3576 */       Object previousValue = this.oldValue.get();
/*      */       try {
/* 3578 */         if (previousValue == null) {
/* 3579 */           Object newValue = loader.load(key);
/* 3580 */           return set(newValue) ? this.futureValue : Futures.immediateFuture(newValue);
/*      */         }
/* 3582 */         ListenableFuture newValue = loader.reload(key, previousValue);
/*      */ 
/* 3584 */         return newValue != null ? newValue : Futures.immediateFuture(null);
/*      */       }
/*      */       catch (Throwable t) {
/* 3587 */         if ((t instanceof InterruptedException)) {
/* 3588 */           Thread.currentThread().interrupt();
/*      */         }
/* 3590 */         return setException(t) ? this.futureValue : fullyFailedFuture(t);
/*      */       }
/*      */     }
/*      */ 
/*      */     public long elapsedNanos() {
/* 3595 */       return this.stopwatch.elapsedTime(TimeUnit.NANOSECONDS);
/*      */     }
/*      */ 
/*      */     public V waitForValue() throws ExecutionException
/*      */     {
/* 3600 */       return Uninterruptibles.getUninterruptibly(this.futureValue);
/*      */     }
/*      */ 
/*      */     public V get()
/*      */     {
/* 3605 */       return this.oldValue.get();
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> getOldValue() {
/* 3609 */       return this.oldValue;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 3614 */       return null;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3620 */       return this;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class Segment<K, V> extends ReentrantLock
/*      */   {
/*      */     final LocalCache<K, V> map;
/*      */     volatile int count;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     int totalWeight;
/*      */     int modCount;
/*      */     int threshold;
/*      */     volatile AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> table;
/*      */     final long maxSegmentWeight;
/*      */     final ReferenceQueue<K> keyReferenceQueue;
/*      */     final ReferenceQueue<V> valueReferenceQueue;
/*      */     final Queue<LocalCache.ReferenceEntry<K, V>> recencyQueue;
/* 2125 */     final AtomicInteger readCount = new AtomicInteger();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     final Queue<LocalCache.ReferenceEntry<K, V>> writeQueue;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     final Queue<LocalCache.ReferenceEntry<K, V>> accessQueue;
/*      */     final AbstractCache.StatsCounter statsCounter;
/*      */ 
/*      */     Segment(LocalCache<K, V> map, int initialCapacity, long maxSegmentWeight, AbstractCache.StatsCounter statsCounter)
/*      */     {
/* 2146 */       this.map = map;
/* 2147 */       this.maxSegmentWeight = maxSegmentWeight;
/* 2148 */       this.statsCounter = statsCounter;
/* 2149 */       initTable(newEntryArray(initialCapacity));
/*      */ 
/* 2151 */       this.keyReferenceQueue = (map.usesKeyReferences() ? new ReferenceQueue() : null);
/*      */ 
/* 2154 */       this.valueReferenceQueue = (map.usesValueReferences() ? new ReferenceQueue() : null);
/*      */ 
/* 2157 */       this.recencyQueue = (map.usesAccessQueue() ? new ConcurrentLinkedQueue() : LocalCache.discardingQueue());
/*      */ 
/* 2161 */       this.writeQueue = (map.usesWriteQueue() ? new LocalCache.WriteQueue() : LocalCache.discardingQueue());
/*      */ 
/* 2165 */       this.accessQueue = (map.usesAccessQueue() ? new LocalCache.AccessQueue() : LocalCache.discardingQueue());
/*      */     }
/*      */ 
/*      */     AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> newEntryArray(int size)
/*      */     {
/* 2171 */       return new AtomicReferenceArray(size);
/*      */     }
/*      */ 
/*      */     void initTable(AtomicReferenceArray<LocalCache.ReferenceEntry<K, V>> newTable) {
/* 2175 */       this.threshold = (newTable.length() * 3 / 4);
/* 2176 */       if ((!this.map.customWeigher()) && (this.threshold == this.maxSegmentWeight))
/*      */       {
/* 2178 */         this.threshold += 1;
/*      */       }
/* 2180 */       this.table = newTable;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next) {
/* 2185 */       return this.map.entryFactory.newEntry(this, key, hash, next);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newNext)
/*      */     {
/* 2194 */       if (original.getKey() == null)
/*      */       {
/* 2196 */         return null;
/*      */       }
/*      */ 
/* 2199 */       LocalCache.ValueReference valueReference = original.getValueReference();
/* 2200 */       Object value = valueReference.get();
/* 2201 */       if ((value == null) && (valueReference.isActive()))
/*      */       {
/* 2203 */         return null;
/*      */       }
/*      */ 
/* 2206 */       LocalCache.ReferenceEntry newEntry = this.map.entryFactory.copyEntry(this, original, newNext);
/* 2207 */       newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
/* 2208 */       return newEntry;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void setValue(LocalCache.ReferenceEntry<K, V> entry, K key, V value, long now)
/*      */     {
/* 2216 */       LocalCache.ValueReference previous = entry.getValueReference();
/* 2217 */       int weight = this.map.weigher.weigh(key, value);
/* 2218 */       Preconditions.checkState(weight >= 0, "Weights must be non-negative");
/*      */ 
/* 2220 */       LocalCache.ValueReference valueReference = this.map.valueStrength.referenceValue(this, entry, value, weight);
/*      */ 
/* 2222 */       entry.setValueReference(valueReference);
/* 2223 */       recordWrite(entry, weight, now);
/* 2224 */       previous.notifyNewValue(value);
/*      */     }
/*      */ 
/*      */     V get(K key, int hash, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/*      */       try
/*      */       {
/*      */         LocalCache.ReferenceEntry e;
/* 2231 */         if (this.count != 0)
/*      */         {
/* 2233 */           e = getEntry(key, hash);
/* 2234 */           if (e != null) {
/* 2235 */             long now = this.map.ticker.read();
/* 2236 */             Object value = getLiveValue(e, now);
/* 2237 */             if (value != null) {
/* 2238 */               recordRead(e, now);
/* 2239 */               this.statsCounter.recordHits(1);
/* 2240 */               return scheduleRefresh(e, key, hash, value, now, loader);
/*      */             }
/* 2242 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 2243 */             if (valueReference.isLoading()) {
/* 2244 */               return waitForLoadingValue(e, key, valueReference);
/*      */             }
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2250 */         return lockedGetOrLoad(key, hash, loader);
/*      */       } catch (ExecutionException ee) {
/* 2252 */         Throwable cause = ee.getCause();
/* 2253 */         if ((cause instanceof Error))
/* 2254 */           throw new ExecutionError((Error)cause);
/* 2255 */         if ((cause instanceof RuntimeException)) {
/* 2256 */           throw new UncheckedExecutionException(cause);
/*      */         }
/* 2258 */         throw ee;
/*      */       } finally {
/* 2260 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */     V lockedGetOrLoad(K key, int hash, CacheLoader<? super K, V> loader) throws ExecutionException {
/* 2267 */       LocalCache.ValueReference valueReference = null;
/* 2268 */       LocalCache.LoadingValueReference loadingValueReference = null;
/* 2269 */       boolean createNewEntry = true;
/*      */ 
/* 2271 */       lock();
/*      */       LocalCache.ReferenceEntry e;
/*      */       try { long now = this.map.ticker.read();
/* 2275 */         preWriteCleanup(now);
/*      */ 
/* 2277 */         int newCount = this.count - 1;
/* 2278 */         AtomicReferenceArray table = this.table;
/* 2279 */         int index = hash & table.length() - 1;
/* 2280 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 2282 */         for (e = first; e != null; e = e.getNext()) {
/* 2283 */           Object entryKey = e.getKey();
/* 2284 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2286 */             valueReference = e.getValueReference();
/* 2287 */             if (valueReference.isLoading()) {
/* 2288 */               createNewEntry = false; break;
/*      */             }
/* 2290 */             Object value = valueReference.get();
/* 2291 */             if (value == null) {
/* 2292 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.COLLECTED);
/* 2293 */             } else if (this.map.isExpired(e, now))
/*      */             {
/* 2296 */               enqueueNotification(entryKey, hash, valueReference, RemovalCause.EXPIRED);
/*      */             } else {
/* 2298 */               recordLockedRead(e, now);
/* 2299 */               this.statsCounter.recordHits(1);
/*      */ 
/* 2301 */               return value;
/*      */             }
/*      */ 
/* 2305 */             this.writeQueue.remove(e);
/* 2306 */             this.accessQueue.remove(e);
/* 2307 */             this.count = newCount;
/*      */ 
/* 2309 */             break;
/*      */           }
/*      */         }
/*      */ 
/* 2313 */         if (createNewEntry) {
/* 2314 */           loadingValueReference = new LocalCache.LoadingValueReference();
/*      */ 
/* 2316 */           if (e == null) {
/* 2317 */             e = newEntry(key, hash, first);
/* 2318 */             e.setValueReference(loadingValueReference);
/* 2319 */             table.set(index, e);
/*      */           } else {
/* 2321 */             e.setValueReference(loadingValueReference);
/*      */           }
/*      */         }
/*      */       } finally {
/* 2325 */         unlock();
/* 2326 */         postWriteCleanup();
/*      */       }
/*      */ 
/* 2329 */       if (createNewEntry)
/*      */       {
/*      */         try
/*      */         {
/* 2334 */           synchronized (e) {
/* 2335 */             return loadSync(key, hash, loadingValueReference, loader);
/*      */           }
/*      */         } finally {
/* 2338 */           this.statsCounter.recordMisses(1);
/*      */         }
/*      */       }
/*      */ 
/* 2342 */       return waitForLoadingValue(e, key, valueReference);
/*      */     }
/*      */ 
/*      */     V waitForLoadingValue(LocalCache.ReferenceEntry<K, V> e, K key, LocalCache.ValueReference<K, V> valueReference)
/*      */       throws ExecutionException
/*      */     {
/* 2348 */       if (!valueReference.isLoading()) {
/* 2349 */         throw new AssertionError();
/*      */       }
/*      */ 
/* 2352 */       Preconditions.checkState(!Thread.holdsLock(e), "Recursive load");
/*      */       try
/*      */       {
/* 2355 */         Object value = valueReference.waitForValue();
/* 2356 */         if (value == null) {
/* 2357 */           throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
/*      */         }
/*      */ 
/* 2360 */         long now = this.map.ticker.read();
/* 2361 */         recordRead(e, now);
/* 2362 */         return value;
/*      */       } finally {
/* 2364 */         this.statsCounter.recordMisses(1);
/*      */       }
/*      */     }
/*      */ 
/*      */     V loadSync(K key, int hash, LocalCache.LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */       throws ExecutionException
/*      */     {
/* 2372 */       ListenableFuture loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2373 */       return getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */     }
/*      */ 
/*      */     ListenableFuture<V> loadAsync(final K key, final int hash, final LocalCache.LoadingValueReference<K, V> loadingValueReference, CacheLoader<? super K, V> loader)
/*      */     {
/* 2378 */       final ListenableFuture loadingFuture = loadingValueReference.loadFuture(key, loader);
/* 2379 */       loadingFuture.addListener(new Runnable()
/*      */       {
/*      */         public void run()
/*      */         {
/*      */           try {
/* 2384 */             Object newValue = LocalCache.Segment.this.getAndRecordStats(key, hash, loadingValueReference, loadingFuture);
/*      */ 
/* 2386 */             loadingValueReference.set(newValue);
/*      */           } catch (Throwable t) {
/* 2388 */             LocalCache.logger.log(Level.WARNING, "Exception thrown during refresh", t);
/* 2389 */             loadingValueReference.setException(t);
/*      */           }
/*      */         }
/*      */       }
/*      */       , LocalCache.sameThreadExecutor);
/*      */ 
/* 2393 */       return loadingFuture;
/*      */     }
/*      */ 
/*      */     V getAndRecordStats(K key, int hash, LocalCache.LoadingValueReference<K, V> loadingValueReference, ListenableFuture<V> newValue)
/*      */       throws ExecutionException
/*      */     {
/* 2401 */       Object value = null;
/*      */       try {
/* 2403 */         value = Uninterruptibles.getUninterruptibly(newValue);
/* 2404 */         if (value == null) {
/* 2405 */           throw new CacheLoader.InvalidCacheLoadException("CacheLoader returned null for key " + key + ".");
/*      */         }
/* 2407 */         this.statsCounter.recordLoadSuccess(loadingValueReference.elapsedNanos());
/* 2408 */         storeLoadedValue(key, hash, loadingValueReference, value);
/* 2409 */         return value;
/*      */       } finally {
/* 2411 */         if (value == null) {
/* 2412 */           this.statsCounter.recordLoadException(loadingValueReference.elapsedNanos());
/* 2413 */           removeLoadingValue(key, hash, loadingValueReference);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     V scheduleRefresh(LocalCache.ReferenceEntry<K, V> entry, K key, int hash, V oldValue, long now, CacheLoader<? super K, V> loader)
/*      */     {
/* 2420 */       if ((this.map.refreshes()) && (now - entry.getWriteTime() > this.map.refreshNanos)) {
/* 2421 */         Object newValue = refresh(key, hash, loader);
/* 2422 */         if (newValue != null) {
/* 2423 */           return newValue;
/*      */         }
/*      */       }
/* 2426 */       return oldValue;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V refresh(K key, int hash, CacheLoader<? super K, V> loader)
/*      */     {
/* 2437 */       LocalCache.LoadingValueReference loadingValueReference = insertLoadingValueReference(key, hash);
/*      */ 
/* 2439 */       if (loadingValueReference == null) {
/* 2440 */         return null;
/*      */       }
/*      */ 
/* 2443 */       ListenableFuture result = loadAsync(key, hash, loadingValueReference, loader);
/* 2444 */       if (result.isDone())
/*      */         try {
/* 2446 */           return Uninterruptibles.getUninterruptibly(result);
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/* 2451 */       return null;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.LoadingValueReference<K, V> insertLoadingValueReference(K key, int hash)
/*      */     {
/* 2460 */       LocalCache.ReferenceEntry e = null;
/* 2461 */       lock();
/*      */       try {
/* 2463 */         long now = this.map.ticker.read();
/* 2464 */         preWriteCleanup(now);
/*      */ 
/* 2466 */         AtomicReferenceArray table = this.table;
/* 2467 */         int index = hash & table.length() - 1;
/* 2468 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */         LocalCache.ValueReference valueReference;
/* 2471 */         for (e = first; e != null; e = e.getNext()) {
/* 2472 */           Object entryKey = e.getKey();
/* 2473 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2477 */             valueReference = e.getValueReference();
/* 2478 */             if (valueReference.isLoading())
/*      */             {
/* 2480 */               return null;
/*      */             }
/*      */ 
/* 2484 */             this.modCount += 1;
/* 2485 */             LocalCache.LoadingValueReference loadingValueReference = new LocalCache.LoadingValueReference(valueReference);
/*      */ 
/* 2487 */             e.setValueReference(loadingValueReference);
/* 2488 */             return loadingValueReference;
/*      */           }
/*      */         }
/*      */ 
/* 2492 */         this.modCount += 1;
/* 2493 */         LocalCache.LoadingValueReference loadingValueReference = new LocalCache.LoadingValueReference();
/* 2494 */         e = newEntry(key, hash, first);
/* 2495 */         e.setValueReference(loadingValueReference);
/* 2496 */         table.set(index, e);
/* 2497 */         return loadingValueReference;
/*      */       } finally {
/* 2499 */         unlock();
/* 2500 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     void tryDrainReferenceQueues()
/*      */     {
/* 2510 */       if (tryLock())
/*      */         try {
/* 2512 */           drainReferenceQueues();
/*      */         } finally {
/* 2514 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainReferenceQueues()
/*      */     {
/* 2525 */       if (this.map.usesKeyReferences()) {
/* 2526 */         drainKeyReferenceQueue();
/*      */       }
/* 2528 */       if (this.map.usesValueReferences())
/* 2529 */         drainValueReferenceQueue();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainKeyReferenceQueue()
/*      */     {
/* 2536 */       int i = 0;
/*      */       Reference ref;
/* 2541 */       for (; (ref = this.keyReferenceQueue.poll()) != null; 
/* 2541 */         i == 16)
/*      */       {
/* 2539 */         LocalCache.ReferenceEntry entry = (LocalCache.ReferenceEntry)ref;
/* 2540 */         this.map.reclaimKey(entry);
/* 2541 */         i++;
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainValueReferenceQueue()
/*      */     {
/* 2550 */       int i = 0;
/*      */       Reference ref;
/* 2555 */       for (; (ref = this.valueReferenceQueue.poll()) != null; 
/* 2555 */         i == 16)
/*      */       {
/* 2553 */         LocalCache.ValueReference valueReference = (LocalCache.ValueReference)ref;
/* 2554 */         this.map.reclaimValue(valueReference);
/* 2555 */         i++;
/*      */       }
/*      */     }
/*      */ 
/*      */     void clearReferenceQueues()
/*      */     {
/* 2565 */       if (this.map.usesKeyReferences()) {
/* 2566 */         clearKeyReferenceQueue();
/*      */       }
/* 2568 */       if (this.map.usesValueReferences())
/* 2569 */         clearValueReferenceQueue();
/*      */     }
/*      */ 
/*      */     void clearKeyReferenceQueue()
/*      */     {
/* 2574 */       while (this.keyReferenceQueue.poll() != null);
/*      */     }
/*      */ 
/*      */     void clearValueReferenceQueue() {
/* 2578 */       while (this.valueReferenceQueue.poll() != null);
/*      */     }
/*      */ 
/*      */     void recordRead(LocalCache.ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2591 */       if (this.map.recordsAccess()) {
/* 2592 */         entry.setAccessTime(now);
/*      */       }
/* 2594 */       this.recencyQueue.add(entry);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordLockedRead(LocalCache.ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2606 */       if (this.map.recordsAccess()) {
/* 2607 */         entry.setAccessTime(now);
/*      */       }
/* 2609 */       this.accessQueue.add(entry);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordWrite(LocalCache.ReferenceEntry<K, V> entry, int weight, long now)
/*      */     {
/* 2619 */       drainRecencyQueue();
/* 2620 */       this.totalWeight += weight;
/*      */ 
/* 2622 */       if (this.map.recordsAccess()) {
/* 2623 */         entry.setAccessTime(now);
/*      */       }
/* 2625 */       if (this.map.recordsWrite()) {
/* 2626 */         entry.setWriteTime(now);
/*      */       }
/* 2628 */       this.accessQueue.add(entry);
/* 2629 */       this.writeQueue.add(entry);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainRecencyQueue()
/*      */     {
/*      */       LocalCache.ReferenceEntry e;
/* 2641 */       while ((e = (LocalCache.ReferenceEntry)this.recencyQueue.poll()) != null)
/*      */       {
/* 2646 */         if (this.accessQueue.contains(e))
/* 2647 */           this.accessQueue.add(e);
/*      */       }
/*      */     }
/*      */ 
/*      */     void tryExpireEntries(long now)
/*      */     {
/* 2658 */       if (tryLock())
/*      */         try {
/* 2660 */           expireEntries(now);
/*      */         } finally {
/* 2662 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void expireEntries(long now)
/*      */     {
/* 2670 */       drainRecencyQueue();
/*      */       LocalCache.ReferenceEntry e;
/* 2673 */       while (((e = (LocalCache.ReferenceEntry)this.writeQueue.peek()) != null) && (this.map.isExpired(e, now))) {
/* 2674 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED)) {
/* 2675 */           throw new AssertionError();
/*      */         }
/*      */       }
/* 2678 */       while (((e = (LocalCache.ReferenceEntry)this.accessQueue.peek()) != null) && (this.map.isExpired(e, now)))
/* 2679 */         if (!removeEntry(e, e.getHash(), RemovalCause.EXPIRED))
/* 2680 */           throw new AssertionError();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void enqueueNotification(LocalCache.ReferenceEntry<K, V> entry, RemovalCause cause)
/*      */     {
/* 2689 */       enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference(), cause);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void enqueueNotification(@Nullable K key, int hash, LocalCache.ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 2695 */       this.totalWeight -= valueReference.getWeight();
/* 2696 */       if (cause.wasEvicted()) {
/* 2697 */         this.statsCounter.recordEviction();
/*      */       }
/* 2699 */       if (this.map.removalNotificationQueue != LocalCache.DISCARDING_QUEUE) {
/* 2700 */         Object value = valueReference.get();
/* 2701 */         RemovalNotification notification = new RemovalNotification(key, value, cause);
/* 2702 */         this.map.removalNotificationQueue.offer(notification);
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void evictEntries()
/*      */     {
/* 2712 */       if (!this.map.evictsBySize()) {
/* 2713 */         return;
/*      */       }
/*      */ 
/* 2716 */       drainRecencyQueue();
/* 2717 */       while (this.totalWeight > this.maxSegmentWeight) {
/* 2718 */         LocalCache.ReferenceEntry e = getNextEvictable();
/* 2719 */         if (!removeEntry(e, e.getHash(), RemovalCause.SIZE))
/* 2720 */           throw new AssertionError();
/*      */       }
/*      */     }
/*      */ 
/*      */     LocalCache.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 2727 */       for (LocalCache.ReferenceEntry e : this.accessQueue) {
/* 2728 */         int weight = e.getValueReference().getWeight();
/* 2729 */         if (weight > 0) {
/* 2730 */           return e;
/*      */         }
/*      */       }
/* 2733 */       throw new AssertionError();
/*      */     }
/*      */ 
/*      */     LocalCache.ReferenceEntry<K, V> getFirst(int hash)
/*      */     {
/* 2741 */       AtomicReferenceArray table = this.table;
/* 2742 */       return (LocalCache.ReferenceEntry)table.get(hash & table.length() - 1);
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getEntry(Object key, int hash)
/*      */     {
/* 2749 */       for (LocalCache.ReferenceEntry e = getFirst(hash); e != null; e = e.getNext()) {
/* 2750 */         if (e.getHash() == hash)
/*      */         {
/* 2754 */           Object entryKey = e.getKey();
/* 2755 */           if (entryKey == null) {
/* 2756 */             tryDrainReferenceQueues();
/*      */           }
/* 2760 */           else if (this.map.keyEquivalence.equivalent(key, entryKey)) {
/* 2761 */             return e;
/*      */           }
/*      */         }
/*      */       }
/* 2765 */       return null;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     LocalCache.ReferenceEntry<K, V> getLiveEntry(Object key, int hash, long now) {
/* 2770 */       LocalCache.ReferenceEntry e = getEntry(key, hash);
/* 2771 */       if (e == null)
/* 2772 */         return null;
/* 2773 */       if (this.map.isExpired(e, now)) {
/* 2774 */         tryExpireEntries(now);
/* 2775 */         return null;
/*      */       }
/* 2777 */       return e;
/*      */     }
/*      */ 
/*      */     V getLiveValue(LocalCache.ReferenceEntry<K, V> entry, long now)
/*      */     {
/* 2785 */       if (entry.getKey() == null) {
/* 2786 */         tryDrainReferenceQueues();
/* 2787 */         return null;
/*      */       }
/* 2789 */       Object value = entry.getValueReference().get();
/* 2790 */       if (value == null) {
/* 2791 */         tryDrainReferenceQueues();
/* 2792 */         return null;
/*      */       }
/*      */ 
/* 2795 */       if (this.map.isExpired(entry, now)) {
/* 2796 */         tryExpireEntries(now);
/* 2797 */         return null;
/*      */       }
/* 2799 */       return value;
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V get(Object key, int hash)
/*      */     {
/*      */       try
/*      */       {
/*      */         long now;
/* 2805 */         if (this.count != 0) {
/* 2806 */           now = this.map.ticker.read();
/* 2807 */           LocalCache.ReferenceEntry e = getLiveEntry(key, hash, now);
/* 2808 */           if (e == null) {
/* 2809 */             return null;
/*      */           }
/*      */ 
/* 2812 */           Object value = e.getValueReference().get();
/* 2813 */           if (value != null) {
/* 2814 */             recordRead(e, now);
/* 2815 */             return scheduleRefresh(e, e.getKey(), hash, value, now, this.map.defaultLoader);
/*      */           }
/* 2817 */           tryDrainReferenceQueues();
/*      */         }
/* 2819 */         return null;
/*      */       } finally {
/* 2821 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean containsKey(Object key, int hash)
/*      */     {
/*      */       try
/*      */       {
/*      */         long now;
/* 2827 */         if (this.count != 0) {
/* 2828 */           now = this.map.ticker.read();
/* 2829 */           LocalCache.ReferenceEntry e = getLiveEntry(key, hash, now);
/*      */           boolean bool;
/* 2830 */           if (e == null) {
/* 2831 */             return false;
/*      */           }
/* 2833 */           return e.getValueReference().get() != null;
/*      */         }
/*      */ 
/* 2836 */         return 0;
/*      */       } finally {
/* 2838 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @VisibleForTesting
/*      */     boolean containsValue(Object value)
/*      */     {
/*      */       try
/*      */       {
/*      */         long now;
/* 2849 */         if (this.count != 0) {
/* 2850 */           now = this.map.ticker.read();
/* 2851 */           AtomicReferenceArray table = this.table;
/* 2852 */           int length = table.length();
/* 2853 */           for (int i = 0; i < length; i++) {
/* 2854 */             for (LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
/* 2855 */               Object entryValue = getLiveValue(e, now);
/* 2856 */               if (entryValue != null)
/*      */               {
/* 2859 */                 if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 2860 */                   return true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2866 */         return 0;
/*      */       } finally {
/* 2868 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V put(K key, int hash, V value, boolean onlyIfAbsent) {
/* 2874 */       lock();
/*      */       try {
/* 2876 */         long now = this.map.ticker.read();
/* 2877 */         preWriteCleanup(now);
/*      */ 
/* 2879 */         int newCount = this.count + 1;
/* 2880 */         if (newCount > this.threshold) {
/* 2881 */           expand();
/* 2882 */           newCount = this.count + 1;
/*      */         }
/*      */ 
/* 2885 */         AtomicReferenceArray table = this.table;
/* 2886 */         int index = hash & table.length() - 1;
/* 2887 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */         Object entryKey;
/* 2890 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2891 */           entryKey = e.getKey();
/* 2892 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2896 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 2897 */             Object entryValue = valueReference.get();
/*      */             Object localObject1;
/* 2899 */             if (entryValue == null) {
/* 2900 */               this.modCount += 1;
/* 2901 */               if (valueReference.isActive()) {
/* 2902 */                 enqueueNotification(key, hash, valueReference, RemovalCause.COLLECTED);
/* 2903 */                 setValue(e, key, value, now);
/* 2904 */                 newCount = this.count;
/*      */               } else {
/* 2906 */                 setValue(e, key, value, now);
/* 2907 */                 newCount = this.count + 1;
/*      */               }
/* 2909 */               this.count = newCount;
/* 2910 */               evictEntries();
/* 2911 */               return null;
/* 2912 */             }if (onlyIfAbsent)
/*      */             {
/* 2916 */               recordLockedRead(e, now);
/* 2917 */               return entryValue;
/*      */             }
/*      */ 
/* 2920 */             this.modCount += 1;
/* 2921 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 2922 */             setValue(e, key, value, now);
/* 2923 */             evictEntries();
/* 2924 */             return entryValue;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2930 */         this.modCount += 1;
/* 2931 */         LocalCache.ReferenceEntry newEntry = newEntry(key, hash, first);
/* 2932 */         setValue(newEntry, key, value, now);
/* 2933 */         table.set(index, newEntry);
/* 2934 */         newCount = this.count + 1;
/* 2935 */         this.count = newCount;
/* 2936 */         evictEntries();
/* 2937 */         return null;
/*      */       } finally {
/* 2939 */         unlock();
/* 2940 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void expand()
/*      */     {
/* 2949 */       AtomicReferenceArray oldTable = this.table;
/* 2950 */       int oldCapacity = oldTable.length();
/* 2951 */       if (oldCapacity >= 1073741824) {
/* 2952 */         return;
/*      */       }
/*      */ 
/* 2965 */       int newCount = this.count;
/* 2966 */       AtomicReferenceArray newTable = newEntryArray(oldCapacity << 1);
/* 2967 */       this.threshold = (newTable.length() * 3 / 4);
/* 2968 */       int newMask = newTable.length() - 1;
/* 2969 */       for (int oldIndex = 0; oldIndex < oldCapacity; oldIndex++)
/*      */       {
/* 2972 */         LocalCache.ReferenceEntry head = (LocalCache.ReferenceEntry)oldTable.get(oldIndex);
/*      */ 
/* 2974 */         if (head != null) {
/* 2975 */           LocalCache.ReferenceEntry next = head.getNext();
/* 2976 */           int headIndex = head.getHash() & newMask;
/*      */ 
/* 2979 */           if (next == null) {
/* 2980 */             newTable.set(headIndex, head);
/*      */           }
/*      */           else
/*      */           {
/* 2985 */             LocalCache.ReferenceEntry tail = head;
/* 2986 */             int tailIndex = headIndex;
/* 2987 */             for (LocalCache.ReferenceEntry e = next; e != null; e = e.getNext()) {
/* 2988 */               int newIndex = e.getHash() & newMask;
/* 2989 */               if (newIndex != tailIndex)
/*      */               {
/* 2991 */                 tailIndex = newIndex;
/* 2992 */                 tail = e;
/*      */               }
/*      */             }
/* 2995 */             newTable.set(tailIndex, tail);
/*      */ 
/* 2998 */             for (LocalCache.ReferenceEntry e = head; e != tail; e = e.getNext()) {
/* 2999 */               int newIndex = e.getHash() & newMask;
/* 3000 */               LocalCache.ReferenceEntry newNext = (LocalCache.ReferenceEntry)newTable.get(newIndex);
/* 3001 */               LocalCache.ReferenceEntry newFirst = copyEntry(e, newNext);
/* 3002 */               if (newFirst != null) {
/* 3003 */                 newTable.set(newIndex, newFirst);
/*      */               } else {
/* 3005 */                 removeCollectedEntry(e);
/* 3006 */                 newCount--;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 3012 */       this.table = newTable;
/* 3013 */       this.count = newCount;
/*      */     }
/*      */ 
/*      */     boolean replace(K key, int hash, V oldValue, V newValue) {
/* 3017 */       lock();
/*      */       try {
/* 3019 */         long now = this.map.ticker.read();
/* 3020 */         preWriteCleanup(now);
/*      */ 
/* 3022 */         AtomicReferenceArray table = this.table;
/* 3023 */         int index = hash & table.length() - 1;
/* 3024 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3026 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3027 */           Object entryKey = e.getKey();
/* 3028 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3030 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3031 */             Object entryValue = valueReference.get();
/*      */             int newCount;
/* 3032 */             if (entryValue == null) {
/* 3033 */               if (valueReference.isActive())
/*      */               {
/* 3035 */                 newCount = this.count - 1;
/* 3036 */                 this.modCount += 1;
/* 3037 */                 LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */ 
/* 3039 */                 newCount = this.count - 1;
/* 3040 */                 table.set(index, newFirst);
/* 3041 */                 this.count = newCount;
/*      */               }
/* 3043 */               return 0;
/*      */             }
/*      */ 
/* 3046 */             if (this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
/* 3047 */               this.modCount += 1;
/* 3048 */               enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3049 */               setValue(e, key, newValue, now);
/* 3050 */               evictEntries();
/* 3051 */               return 1;
/*      */             }
/*      */ 
/* 3055 */             recordLockedRead(e, now);
/* 3056 */             return 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 3061 */         return 0;
/*      */       } finally {
/* 3063 */         unlock();
/* 3064 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V replace(K key, int hash, V newValue) {
/* 3070 */       lock();
/*      */       try {
/* 3072 */         long now = this.map.ticker.read();
/* 3073 */         preWriteCleanup(now);
/*      */ 
/* 3075 */         AtomicReferenceArray table = this.table;
/* 3076 */         int index = hash & table.length() - 1;
/* 3077 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3079 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3080 */           Object entryKey = e.getKey();
/* 3081 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3083 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3084 */             Object entryValue = valueReference.get();
/*      */             int newCount;
/* 3085 */             if (entryValue == null) {
/* 3086 */               if (valueReference.isActive())
/*      */               {
/* 3088 */                 newCount = this.count - 1;
/* 3089 */                 this.modCount += 1;
/* 3090 */                 LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */ 
/* 3092 */                 newCount = this.count - 1;
/* 3093 */                 table.set(index, newFirst);
/* 3094 */                 this.count = newCount;
/*      */               }
/* 3096 */               return null;
/*      */             }
/*      */ 
/* 3099 */             this.modCount += 1;
/* 3100 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3101 */             setValue(e, key, newValue, now);
/* 3102 */             evictEntries();
/* 3103 */             return entryValue;
/*      */           }
/*      */         }
/*      */ 
/* 3107 */         return null;
/*      */       } finally {
/* 3109 */         unlock();
/* 3110 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     V remove(Object key, int hash) {
/* 3116 */       lock();
/*      */       try {
/* 3118 */         long now = this.map.ticker.read();
/* 3119 */         preWriteCleanup(now);
/*      */ 
/* 3121 */         int newCount = this.count - 1;
/* 3122 */         AtomicReferenceArray table = this.table;
/* 3123 */         int index = hash & table.length() - 1;
/* 3124 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3126 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3127 */           Object entryKey = e.getKey();
/* 3128 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3130 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3131 */             Object entryValue = valueReference.get();
/*      */             RemovalCause cause;
/* 3134 */             if (entryValue != null) {
/* 3135 */               cause = RemovalCause.EXPLICIT;
/*      */             }
/*      */             else
/*      */             {
/*      */               RemovalCause cause;
/* 3136 */               if (valueReference.isActive()) {
/* 3137 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3140 */                 return null;
/*      */             }
/*      */             RemovalCause cause;
/* 3143 */             this.modCount += 1;
/* 3144 */             LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */ 
/* 3146 */             newCount = this.count - 1;
/* 3147 */             table.set(index, newFirst);
/* 3148 */             this.count = newCount;
/* 3149 */             return entryValue;
/*      */           }
/*      */         }
/*      */ 
/* 3153 */         return null;
/*      */       } finally {
/* 3155 */         unlock();
/* 3156 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean storeLoadedValue(K key, int hash, LocalCache.LoadingValueReference<K, V> oldValueReference, V newValue)
/*      */     {
/* 3162 */       lock();
/*      */       try {
/* 3164 */         long now = this.map.ticker.read();
/* 3165 */         preWriteCleanup(now);
/*      */ 
/* 3167 */         int newCount = this.count + 1;
/* 3168 */         AtomicReferenceArray table = this.table;
/* 3169 */         int index = hash & table.length() - 1;
/* 3170 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */         Object entryKey;
/* 3172 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3173 */           entryKey = e.getKey();
/* 3174 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3176 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3177 */             Object entryValue = valueReference.get();
/*      */             RemovalCause cause;
/* 3178 */             if ((entryValue == null) || (oldValueReference == valueReference)) {
/* 3179 */               this.modCount += 1;
/* 3180 */               if (oldValueReference.isActive()) {
/* 3181 */                 cause = entryValue == null ? RemovalCause.COLLECTED : RemovalCause.REPLACED;
/*      */ 
/* 3183 */                 enqueueNotification(key, hash, oldValueReference, cause);
/* 3184 */                 newCount--;
/*      */               }
/* 3186 */               setValue(e, key, newValue, now);
/* 3187 */               this.count = newCount;
/* 3188 */               evictEntries();
/* 3189 */               return 1;
/*      */             }
/*      */ 
/* 3193 */             valueReference = new LocalCache.WeightedStrongValueReference(newValue, 0);
/* 3194 */             enqueueNotification(key, hash, valueReference, RemovalCause.REPLACED);
/* 3195 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3199 */         this.modCount += 1;
/* 3200 */         LocalCache.ReferenceEntry newEntry = newEntry(key, hash, first);
/* 3201 */         setValue(newEntry, key, newValue, now);
/* 3202 */         table.set(index, newEntry);
/* 3203 */         this.count = newCount;
/* 3204 */         evictEntries();
/* 3205 */         return 1;
/*      */       } finally {
/* 3207 */         unlock();
/* 3208 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean remove(Object key, int hash, Object value) {
/* 3213 */       lock();
/*      */       try {
/* 3215 */         long now = this.map.ticker.read();
/* 3216 */         preWriteCleanup(now);
/*      */ 
/* 3218 */         int newCount = this.count - 1;
/* 3219 */         AtomicReferenceArray table = this.table;
/* 3220 */         int index = hash & table.length() - 1;
/* 3221 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3223 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3224 */           Object entryKey = e.getKey();
/* 3225 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3227 */             LocalCache.ValueReference valueReference = e.getValueReference();
/* 3228 */             Object entryValue = valueReference.get();
/*      */             RemovalCause cause;
/* 3231 */             if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 3232 */               cause = RemovalCause.EXPLICIT;
/*      */             }
/*      */             else
/*      */             {
/*      */               RemovalCause cause;
/* 3233 */               if ((entryValue == null) && (valueReference.isActive())) {
/* 3234 */                 cause = RemovalCause.COLLECTED;
/*      */               }
/*      */               else
/* 3237 */                 return false;
/*      */             }
/*      */             RemovalCause cause;
/* 3240 */             this.modCount += 1;
/* 3241 */             LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, cause);
/*      */ 
/* 3243 */             newCount = this.count - 1;
/* 3244 */             table.set(index, newFirst);
/* 3245 */             this.count = newCount;
/* 3246 */             return cause == RemovalCause.EXPLICIT;
/*      */           }
/*      */         }
/*      */ 
/* 3250 */         return 0;
/*      */       } finally {
/* 3252 */         unlock();
/* 3253 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     void clear() {
/* 3258 */       if (this.count != 0) {
/* 3259 */         lock();
/*      */         try {
/* 3261 */           AtomicReferenceArray table = this.table;
/* 3262 */           for (int i = 0; i < table.length(); i++) {
/* 3263 */             for (LocalCache.ReferenceEntry e = (LocalCache.ReferenceEntry)table.get(i); e != null; e = e.getNext())
/*      */             {
/* 3265 */               if (e.getValueReference().isActive()) {
/* 3266 */                 enqueueNotification(e, RemovalCause.EXPLICIT);
/*      */               }
/*      */             }
/*      */           }
/* 3270 */           for (int i = 0; i < table.length(); i++) {
/* 3271 */             table.set(i, null);
/*      */           }
/* 3273 */           clearReferenceQueues();
/* 3274 */           this.writeQueue.clear();
/* 3275 */           this.accessQueue.clear();
/* 3276 */           this.readCount.set(0);
/*      */ 
/* 3278 */           this.modCount += 1;
/* 3279 */           this.count = 0;
/*      */         } finally {
/* 3281 */           unlock();
/* 3282 */           postWriteCleanup();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> removeValueFromChain(LocalCache.ReferenceEntry<K, V> first, LocalCache.ReferenceEntry<K, V> entry, @Nullable K key, int hash, LocalCache.ValueReference<K, V> valueReference, RemovalCause cause)
/*      */     {
/* 3292 */       enqueueNotification(key, hash, valueReference, cause);
/* 3293 */       this.writeQueue.remove(entry);
/* 3294 */       this.accessQueue.remove(entry);
/*      */ 
/* 3296 */       if (valueReference.isLoading()) {
/* 3297 */         valueReference.notifyNewValue(null);
/* 3298 */         return first;
/*      */       }
/* 3300 */       return removeEntryFromChain(first, entry);
/*      */     }
/*      */ 
/*      */     @Nullable
/*      */     @GuardedBy("Segment.this")
/*      */     LocalCache.ReferenceEntry<K, V> removeEntryFromChain(LocalCache.ReferenceEntry<K, V> first, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 3308 */       int newCount = this.count;
/* 3309 */       LocalCache.ReferenceEntry newFirst = entry.getNext();
/* 3310 */       for (LocalCache.ReferenceEntry e = first; e != entry; e = e.getNext()) {
/* 3311 */         LocalCache.ReferenceEntry next = copyEntry(e, newFirst);
/* 3312 */         if (next != null) {
/* 3313 */           newFirst = next;
/*      */         } else {
/* 3315 */           removeCollectedEntry(e);
/* 3316 */           newCount--;
/*      */         }
/*      */       }
/* 3319 */       this.count = newCount;
/* 3320 */       return newFirst;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void removeCollectedEntry(LocalCache.ReferenceEntry<K, V> entry) {
/* 3325 */       enqueueNotification(entry, RemovalCause.COLLECTED);
/* 3326 */       this.writeQueue.remove(entry);
/* 3327 */       this.accessQueue.remove(entry);
/*      */     }
/*      */ 
/*      */     boolean reclaimKey(LocalCache.ReferenceEntry<K, V> entry, int hash)
/*      */     {
/* 3334 */       lock();
/*      */       try {
/* 3336 */         int newCount = this.count - 1;
/* 3337 */         AtomicReferenceArray table = this.table;
/* 3338 */         int index = hash & table.length() - 1;
/* 3339 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3341 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3342 */           if (e == entry) {
/* 3343 */             this.modCount += 1;
/* 3344 */             LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), RemovalCause.COLLECTED);
/*      */ 
/* 3346 */             newCount = this.count - 1;
/* 3347 */             table.set(index, newFirst);
/* 3348 */             this.count = newCount;
/* 3349 */             return true;
/*      */           }
/*      */         }
/*      */ 
/* 3353 */         return 0;
/*      */       } finally {
/* 3355 */         unlock();
/* 3356 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean reclaimValue(K key, int hash, LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/* 3364 */       lock();
/*      */       try {
/* 3366 */         int newCount = this.count - 1;
/* 3367 */         AtomicReferenceArray table = this.table;
/* 3368 */         int index = hash & table.length() - 1;
/* 3369 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3371 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3372 */           Object entryKey = e.getKey();
/* 3373 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3375 */             LocalCache.ValueReference v = e.getValueReference();
/*      */             LocalCache.ReferenceEntry newFirst;
/* 3376 */             if (v == valueReference) {
/* 3377 */               this.modCount += 1;
/* 3378 */               newFirst = removeValueFromChain(first, e, entryKey, hash, valueReference, RemovalCause.COLLECTED);
/*      */ 
/* 3380 */               newCount = this.count - 1;
/* 3381 */               table.set(index, newFirst);
/* 3382 */               this.count = newCount;
/* 3383 */               return true;
/*      */             }
/* 3385 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3389 */         return 0;
/*      */       } finally {
/* 3391 */         unlock();
/* 3392 */         if (!isHeldByCurrentThread())
/* 3393 */           postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean removeLoadingValue(K key, int hash, LocalCache.LoadingValueReference<K, V> valueReference)
/*      */     {
/* 3399 */       lock();
/*      */       try {
/* 3401 */         AtomicReferenceArray table = this.table;
/* 3402 */         int index = hash & table.length() - 1;
/* 3403 */         LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3405 */         for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3406 */           Object entryKey = e.getKey();
/* 3407 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3409 */             LocalCache.ValueReference v = e.getValueReference();
/*      */             LocalCache.ReferenceEntry newFirst;
/* 3410 */             if (v == valueReference) {
/* 3411 */               if (valueReference.isActive()) {
/* 3412 */                 e.setValueReference(valueReference.getOldValue());
/*      */               } else {
/* 3414 */                 newFirst = removeEntryFromChain(first, e);
/* 3415 */                 table.set(index, newFirst);
/*      */               }
/* 3417 */               return 1;
/*      */             }
/* 3419 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3423 */         return 0;
/*      */       } finally {
/* 3425 */         unlock();
/* 3426 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     boolean removeEntry(LocalCache.ReferenceEntry<K, V> entry, int hash, RemovalCause cause) {
/* 3432 */       int newCount = this.count - 1;
/* 3433 */       AtomicReferenceArray table = this.table;
/* 3434 */       int index = hash & table.length() - 1;
/* 3435 */       LocalCache.ReferenceEntry first = (LocalCache.ReferenceEntry)table.get(index);
/*      */ 
/* 3437 */       for (LocalCache.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3438 */         if (e == entry) {
/* 3439 */           this.modCount += 1;
/* 3440 */           LocalCache.ReferenceEntry newFirst = removeValueFromChain(first, e, e.getKey(), hash, e.getValueReference(), cause);
/*      */ 
/* 3442 */           newCount = this.count - 1;
/* 3443 */           table.set(index, newFirst);
/* 3444 */           this.count = newCount;
/* 3445 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 3449 */       return false;
/*      */     }
/*      */ 
/*      */     void postReadCleanup()
/*      */     {
/* 3457 */       if ((this.readCount.incrementAndGet() & 0x3F) == 0)
/* 3458 */         cleanUp();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void preWriteCleanup(long now)
/*      */     {
/* 3470 */       runLockedCleanup(now);
/*      */     }
/*      */ 
/*      */     void postWriteCleanup()
/*      */     {
/* 3477 */       runUnlockedCleanup();
/*      */     }
/*      */ 
/*      */     void cleanUp() {
/* 3481 */       long now = this.map.ticker.read();
/* 3482 */       runLockedCleanup(now);
/* 3483 */       runUnlockedCleanup();
/*      */     }
/*      */ 
/*      */     void runLockedCleanup(long now) {
/* 3487 */       if (tryLock())
/*      */         try {
/* 3489 */           drainReferenceQueues();
/* 3490 */           expireEntries(now);
/* 3491 */           this.readCount.set(0);
/*      */         } finally {
/* 3493 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     void runUnlockedCleanup()
/*      */     {
/* 3500 */       if (!isHeldByCurrentThread())
/* 3501 */         this.map.processPendingNotifications();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeightedStrongValueReference<K, V> extends LocalCache.StrongValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */ 
/*      */     WeightedStrongValueReference(V referent, int weight)
/*      */     {
/* 1835 */       super();
/* 1836 */       this.weight = weight;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1841 */       return this.weight;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeightedSoftValueReference<K, V> extends LocalCache.SoftValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */ 
/*      */     WeightedSoftValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry, int weight)
/*      */     {
/* 1812 */       super(referent, entry);
/* 1813 */       this.weight = weight;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1818 */       return this.weight;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1823 */       return new WeightedSoftValueReference(queue, value, entry, this.weight);
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeightedWeakValueReference<K, V> extends LocalCache.WeakValueReference<K, V>
/*      */   {
/*      */     final int weight;
/*      */ 
/*      */     WeightedWeakValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry, int weight)
/*      */     {
/* 1788 */       super(referent, entry);
/* 1789 */       this.weight = weight;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1794 */       return this.weight;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1800 */       return new WeightedWeakValueReference(queue, value, entry, this.weight);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class StrongValueReference<K, V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     final V referent;
/*      */ 
/*      */     StrongValueReference(V referent)
/*      */     {
/* 1737 */       this.referent = referent;
/*      */     }
/*      */ 
/*      */     public V get()
/*      */     {
/* 1742 */       return this.referent;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1747 */       return 1;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1752 */       return null;
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1758 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 1763 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 1768 */       return true;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1773 */       return get();
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(V newValue)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static class SoftValueReference<K, V> extends SoftReference<V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     final LocalCache.ReferenceEntry<K, V> entry;
/*      */ 
/*      */     SoftValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1691 */       super(queue);
/* 1692 */       this.entry = entry;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1697 */       return 1;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1702 */       return this.entry;
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(V newValue)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1711 */       return new SoftValueReference(queue, value, entry);
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 1716 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 1721 */       return true;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1726 */       return get();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WeakValueReference<K, V> extends WeakReference<V>
/*      */     implements LocalCache.ValueReference<K, V>
/*      */   {
/*      */     final LocalCache.ReferenceEntry<K, V> entry;
/*      */ 
/*      */     WeakValueReference(ReferenceQueue<V> queue, V referent, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1644 */       super(queue);
/* 1645 */       this.entry = entry;
/*      */     }
/*      */ 
/*      */     public int getWeight()
/*      */     {
/* 1650 */       return 1;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1655 */       return this.entry;
/*      */     }
/*      */ 
/*      */     public void notifyNewValue(V newValue)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, LocalCache.ReferenceEntry<K, V> entry)
/*      */     {
/* 1664 */       return new WeakValueReference(queue, value, entry);
/*      */     }
/*      */ 
/*      */     public boolean isLoading()
/*      */     {
/* 1669 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isActive()
/*      */     {
/* 1674 */       return true;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1679 */       return get();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakAccessWriteEntry<K, V> extends LocalCache.WeakEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/* 1557 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1569 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1582 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/* 1597 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1609 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1622 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     WeakAccessWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1552 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1561 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1566 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1574 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1579 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1587 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1592 */       this.previousAccess = previous;
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1601 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1606 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1614 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1619 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1627 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1632 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakWriteEntry<K, V> extends LocalCache.WeakEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/* 1509 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1521 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1534 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     WeakWriteEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1504 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1513 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1518 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1526 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1531 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1539 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1544 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakAccessEntry<K, V> extends LocalCache.WeakEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/* 1461 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1473 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1486 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/*      */     WeakAccessEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1456 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1465 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1470 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1478 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1483 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1491 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1496 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WeakEntry<K, V> extends WeakReference<K>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/*      */     final int hash;
/*      */     final LocalCache.ReferenceEntry<K, V> next;
/* 1429 */     volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();
/*      */ 
/*      */     WeakEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1351 */       super(queue);
/* 1352 */       this.hash = hash;
/* 1353 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 1358 */       return get();
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1365 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1370 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1375 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1380 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1385 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1390 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1397 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1402 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1407 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1412 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1417 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1422 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> getValueReference()
/*      */     {
/* 1433 */       return this.valueReference;
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/* 1438 */       this.valueReference = valueReference;
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/* 1443 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNext()
/*      */     {
/* 1448 */       return this.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongAccessWriteEntry<K, V> extends LocalCache.StrongEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/* 1267 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1279 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1292 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/* 1307 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1319 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1332 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     StrongAccessWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1262 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1271 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1276 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1284 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1289 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1297 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1302 */       this.previousAccess = previous;
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1311 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1316 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1324 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1329 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1337 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1342 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongWriteEntry<K, V> extends LocalCache.StrongEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/* 1220 */     volatile long writeTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1232 */     LocalCache.ReferenceEntry<K, V> nextWrite = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1245 */     LocalCache.ReferenceEntry<K, V> previousWrite = LocalCache.nullEntry();
/*      */ 
/*      */     StrongWriteEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1215 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1224 */       return this.writeTime;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1229 */       this.writeTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1237 */       return this.nextWrite;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1242 */       this.nextWrite = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1250 */       return this.previousWrite;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1255 */       this.previousWrite = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongAccessEntry<K, V> extends LocalCache.StrongEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/* 1173 */     volatile long accessTime = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1185 */     LocalCache.ReferenceEntry<K, V> nextAccess = LocalCache.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1198 */     LocalCache.ReferenceEntry<K, V> previousAccess = LocalCache.nullEntry();
/*      */ 
/*      */     StrongAccessEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1168 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1177 */       return this.accessTime;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1182 */       this.accessTime = time;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1190 */       return this.nextAccess;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1195 */       this.nextAccess = next;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1203 */       return this.previousAccess;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1208 */       this.previousAccess = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class StrongEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/*      */     final K key;
/*      */     final int hash;
/*      */     final LocalCache.ReferenceEntry<K, V> next;
/* 1142 */     volatile LocalCache.ValueReference<K, V> valueReference = LocalCache.unset();
/*      */ 
/*      */     StrongEntry(K key, int hash, @Nullable LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1064 */       this.key = key;
/* 1065 */       this.hash = hash;
/* 1066 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 1071 */       return this.key;
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/* 1078 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/* 1083 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/* 1088 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1093 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/* 1098 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1103 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/* 1110 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/* 1115 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/* 1120 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/* 1125 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1130 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1135 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ValueReference<K, V> getValueReference()
/*      */     {
/* 1146 */       return this.valueReference;
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/* 1151 */       this.valueReference = valueReference;
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/* 1156 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNext()
/*      */     {
/* 1161 */       return this.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class AbstractReferenceEntry<K, V>
/*      */     implements LocalCache.ReferenceEntry<K, V>
/*      */   {
/*      */     public LocalCache.ValueReference<K, V> getValueReference()
/*      */     {
/*  925 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<K, V> valueReference)
/*      */     {
/*  930 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNext()
/*      */     {
/*  935 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/*  940 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/*  945 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/*  950 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/*  955 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInAccessQueue()
/*      */     {
/*  960 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/*  965 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInAccessQueue()
/*      */     {
/*  970 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/*  975 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getWriteTime()
/*      */     {
/*  980 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/*  985 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getNextInWriteQueue()
/*      */     {
/*  990 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<K, V> next)
/*      */     {
/*  995 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<K, V> getPreviousInWriteQueue()
/*      */     {
/* 1000 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<K, V> previous)
/*      */     {
/* 1005 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static enum NullEntry
/*      */     implements LocalCache.ReferenceEntry<Object, Object>
/*      */   {
/*  848 */     INSTANCE;
/*      */ 
/*      */     public LocalCache.ValueReference<Object, Object> getValueReference()
/*      */     {
/*  852 */       return null;
/*      */     }
/*      */ 
/*      */     public void setValueReference(LocalCache.ValueReference<Object, Object> valueReference)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getNext() {
/*  860 */       return null;
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/*  865 */       return 0;
/*      */     }
/*      */ 
/*      */     public Object getKey()
/*      */     {
/*  870 */       return null;
/*      */     }
/*      */ 
/*      */     public long getAccessTime()
/*      */     {
/*  875 */       return 0L;
/*      */     }
/*      */ 
/*      */     public void setAccessTime(long time)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getNextInAccessQueue() {
/*  883 */       return this;
/*      */     }
/*      */ 
/*      */     public void setNextInAccessQueue(LocalCache.ReferenceEntry<Object, Object> next)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getPreviousInAccessQueue() {
/*  891 */       return this;
/*      */     }
/*      */ 
/*      */     public void setPreviousInAccessQueue(LocalCache.ReferenceEntry<Object, Object> previous)
/*      */     {
/*      */     }
/*      */ 
/*      */     public long getWriteTime() {
/*  899 */       return 0L;
/*      */     }
/*      */ 
/*      */     public void setWriteTime(long time)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getNextInWriteQueue() {
/*  907 */       return this;
/*      */     }
/*      */ 
/*      */     public void setNextInWriteQueue(LocalCache.ReferenceEntry<Object, Object> next)
/*      */     {
/*      */     }
/*      */ 
/*      */     public LocalCache.ReferenceEntry<Object, Object> getPreviousInWriteQueue() {
/*  915 */       return this;
/*      */     }
/*      */ 
/*      */     public void setPreviousInWriteQueue(LocalCache.ReferenceEntry<Object, Object> previous)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract interface ReferenceEntry<K, V>
/*      */   {
/*      */     public abstract LocalCache.ValueReference<K, V> getValueReference();
/*      */ 
/*      */     public abstract void setValueReference(LocalCache.ValueReference<K, V> paramValueReference);
/*      */ 
/*      */     @Nullable
/*      */     public abstract ReferenceEntry<K, V> getNext();
/*      */ 
/*      */     public abstract int getHash();
/*      */ 
/*      */     @Nullable
/*      */     public abstract K getKey();
/*      */ 
/*      */     public abstract long getAccessTime();
/*      */ 
/*      */     public abstract void setAccessTime(long paramLong);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextInAccessQueue();
/*      */ 
/*      */     public abstract void setNextInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousInAccessQueue();
/*      */ 
/*      */     public abstract void setPreviousInAccessQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract long getWriteTime();
/*      */ 
/*      */     public abstract void setWriteTime(long paramLong);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextInWriteQueue();
/*      */ 
/*      */     public abstract void setNextInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousInWriteQueue();
/*      */ 
/*      */     public abstract void setPreviousInWriteQueue(ReferenceEntry<K, V> paramReferenceEntry);
/*      */   }
/*      */ 
/*      */   static abstract interface ValueReference<K, V>
/*      */   {
/*      */     @Nullable
/*      */     public abstract V get();
/*      */ 
/*      */     public abstract V waitForValue()
/*      */       throws ExecutionException;
/*      */ 
/*      */     public abstract int getWeight();
/*      */ 
/*      */     @Nullable
/*      */     public abstract LocalCache.ReferenceEntry<K, V> getEntry();
/*      */ 
/*      */     public abstract ValueReference<K, V> copyFor(ReferenceQueue<V> paramReferenceQueue, V paramV, LocalCache.ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract void notifyNewValue(@Nullable V paramV);
/*      */ 
/*      */     public abstract boolean isLoading();
/*      */ 
/*      */     public abstract boolean isActive();
/*      */   }
/*      */ 
/*      */   static abstract enum EntryFactory
/*      */   {
/*  442 */     STRONG, 
/*      */ 
/*  449 */     STRONG_ACCESS, 
/*      */ 
/*  464 */     STRONG_WRITE, 
/*      */ 
/*  479 */     STRONG_ACCESS_WRITE, 
/*      */ 
/*  496 */     WEAK, 
/*      */ 
/*  503 */     WEAK_ACCESS, 
/*      */ 
/*  518 */     WEAK_WRITE, 
/*      */ 
/*  533 */     WEAK_ACCESS_WRITE;
/*      */ 
/*      */     static final int ACCESS_MASK = 1;
/*      */     static final int WRITE_MASK = 2;
/*      */     static final int WEAK_MASK = 4;
/*  560 */     static final EntryFactory[] factories = { STRONG, STRONG_ACCESS, STRONG_WRITE, STRONG_ACCESS_WRITE, WEAK, WEAK_ACCESS, WEAK_WRITE, WEAK_ACCESS_WRITE };
/*      */ 
/*      */     static EntryFactory getFactory(LocalCache.Strength keyStrength, boolean usesAccessQueue, boolean usesWriteQueue)
/*      */     {
/*  567 */       int flags = (keyStrength == LocalCache.Strength.WEAK ? 4 : 0) | (usesAccessQueue ? 1 : 0) | (usesWriteQueue ? 2 : 0);
/*      */ 
/*  570 */       return factories[flags];
/*      */     }
/*      */ 
/*      */     abstract <K, V> LocalCache.ReferenceEntry<K, V> newEntry(LocalCache.Segment<K, V> paramSegment, K paramK, int paramInt, @Nullable LocalCache.ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> LocalCache.ReferenceEntry<K, V> copyEntry(LocalCache.Segment<K, V> segment, LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newNext)
/*      */     {
/*  593 */       return newEntry(segment, original.getKey(), original.getHash(), newNext);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyAccessEntry(LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newEntry)
/*      */     {
/*  600 */       newEntry.setAccessTime(original.getAccessTime());
/*      */ 
/*  602 */       LocalCache.connectAccessOrder(original.getPreviousInAccessQueue(), newEntry);
/*  603 */       LocalCache.connectAccessOrder(newEntry, original.getNextInAccessQueue());
/*      */ 
/*  605 */       LocalCache.nullifyAccessOrder(original);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyWriteEntry(LocalCache.ReferenceEntry<K, V> original, LocalCache.ReferenceEntry<K, V> newEntry)
/*      */     {
/*  612 */       newEntry.setWriteTime(original.getWriteTime());
/*      */ 
/*  614 */       LocalCache.connectWriteOrder(original.getPreviousInWriteQueue(), newEntry);
/*  615 */       LocalCache.connectWriteOrder(newEntry, original.getNextInWriteQueue());
/*      */ 
/*  617 */       LocalCache.nullifyWriteOrder(original);
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract enum Strength
/*      */   {
/*  377 */     STRONG, 
/*      */ 
/*  392 */     SOFT, 
/*      */ 
/*  408 */     WEAK;
/*      */ 
/*      */     abstract <K, V> LocalCache.ValueReference<K, V> referenceValue(LocalCache.Segment<K, V> paramSegment, LocalCache.ReferenceEntry<K, V> paramReferenceEntry, V paramV, int paramInt);
/*      */ 
/*      */     abstract Equivalence<Object> defaultEquivalence();
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.LocalCache
 * JD-Core Version:    0.6.2
 */