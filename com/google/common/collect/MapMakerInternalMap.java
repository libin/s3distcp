/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.VisibleForTesting;
/*      */ import com.google.common.base.Equivalence;
/*      */ import com.google.common.base.Equivalences;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Ticker;
/*      */ import com.google.common.primitives.Ints;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
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
/*      */ import java.util.concurrent.CancellationException;
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
/*      */ class MapMakerInternalMap<K, V> extends AbstractMap<K, V>
/*      */   implements ConcurrentMap<K, V>, Serializable
/*      */ {
/*      */   static final int MAXIMUM_CAPACITY = 1073741824;
/*      */   static final int MAX_SEGMENTS = 65536;
/*      */   static final int CONTAINS_VALUE_RETRIES = 3;
/*      */   static final int DRAIN_THRESHOLD = 63;
/*      */   static final int DRAIN_MAX = 16;
/*      */   static final long CLEANUP_EXECUTOR_DELAY_SECS = 60L;
/*  136 */   private static final Logger logger = Logger.getLogger(MapMakerInternalMap.class.getName());
/*      */   final transient int segmentMask;
/*      */   final transient int segmentShift;
/*      */   final transient Segment<K, V>[] segments;
/*      */   final int concurrencyLevel;
/*      */   final Equivalence<Object> keyEquivalence;
/*      */   final Equivalence<Object> valueEquivalence;
/*      */   final Strength keyStrength;
/*      */   final Strength valueStrength;
/*      */   final int maximumSize;
/*      */   final long expireAfterAccessNanos;
/*      */   final long expireAfterWriteNanos;
/*      */   final Queue<MapMaker.RemovalNotification<K, V>> removalNotificationQueue;
/*      */   final MapMaker.RemovalListener<K, V> removalListener;
/*      */   final transient EntryFactory entryFactory;
/*      */   final Ticker ticker;
/*  630 */   static final ValueReference<Object, Object> UNSET = new ValueReference()
/*      */   {
/*      */     public Object get() {
/*  633 */       return null;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<Object, Object> getEntry()
/*      */     {
/*  638 */       return null;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<Object, Object> copyFor(ReferenceQueue<Object> queue, Object value, MapMakerInternalMap.ReferenceEntry<Object, Object> entry)
/*      */     {
/*  644 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean isComputingReference()
/*      */     {
/*  649 */       return false;
/*      */     }
/*      */ 
/*      */     public Object waitForValue()
/*      */     {
/*  654 */       return null;
/*      */     }
/*      */ 
/*      */     public void clear(MapMakerInternalMap.ValueReference<Object, Object> newValue)
/*      */     {
/*      */     }
/*  630 */   };
/*      */ 
/*  920 */   static final Queue<? extends Object> DISCARDING_QUEUE = new AbstractQueue()
/*      */   {
/*      */     public boolean offer(Object o) {
/*  923 */       return true;
/*      */     }
/*      */ 
/*      */     public Object peek()
/*      */     {
/*  928 */       return null;
/*      */     }
/*      */ 
/*      */     public Object poll()
/*      */     {
/*  933 */       return null;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/*  938 */       return 0;
/*      */     }
/*      */ 
/*      */     public Iterator<Object> iterator()
/*      */     {
/*  943 */       return Iterators.emptyIterator();
/*      */     }
/*  920 */   };
/*      */   transient Set<K> keySet;
/*      */   transient Collection<V> values;
/*      */   transient Set<Map.Entry<K, V>> entrySet;
/*      */   private static final long serialVersionUID = 5L;
/*      */ 
/*      */   MapMakerInternalMap(MapMaker builder)
/*      */   {
/*  197 */     this.concurrencyLevel = Math.min(builder.getConcurrencyLevel(), 65536);
/*      */ 
/*  199 */     this.keyStrength = builder.getKeyStrength();
/*  200 */     this.valueStrength = builder.getValueStrength();
/*      */ 
/*  202 */     this.keyEquivalence = builder.getKeyEquivalence();
/*  203 */     this.valueEquivalence = builder.getValueEquivalence();
/*      */ 
/*  205 */     this.maximumSize = builder.maximumSize;
/*  206 */     this.expireAfterAccessNanos = builder.getExpireAfterAccessNanos();
/*  207 */     this.expireAfterWriteNanos = builder.getExpireAfterWriteNanos();
/*      */ 
/*  209 */     this.entryFactory = EntryFactory.getFactory(this.keyStrength, expires(), evictsBySize());
/*  210 */     this.ticker = builder.getTicker();
/*      */ 
/*  212 */     this.removalListener = builder.getRemovalListener();
/*  213 */     this.removalNotificationQueue = (this.removalListener == GenericMapMaker.NullListener.INSTANCE ? discardingQueue() : new ConcurrentLinkedQueue());
/*      */ 
/*  217 */     int initialCapacity = Math.min(builder.getInitialCapacity(), 1073741824);
/*  218 */     if (evictsBySize()) {
/*  219 */       initialCapacity = Math.min(initialCapacity, this.maximumSize);
/*      */     }
/*      */ 
/*  225 */     int segmentShift = 0;
/*  226 */     int segmentCount = 1;
/*      */ 
/*  228 */     while ((segmentCount < this.concurrencyLevel) && ((!evictsBySize()) || (segmentCount * 2 <= this.maximumSize))) {
/*  229 */       segmentShift++;
/*  230 */       segmentCount <<= 1;
/*      */     }
/*  232 */     this.segmentShift = (32 - segmentShift);
/*  233 */     this.segmentMask = (segmentCount - 1);
/*      */ 
/*  235 */     this.segments = newSegmentArray(segmentCount);
/*      */ 
/*  237 */     int segmentCapacity = initialCapacity / segmentCount;
/*  238 */     if (segmentCapacity * segmentCount < initialCapacity) {
/*  239 */       segmentCapacity++;
/*      */     }
/*      */ 
/*  242 */     int segmentSize = 1;
/*  243 */     while (segmentSize < segmentCapacity) {
/*  244 */       segmentSize <<= 1;
/*      */     }
/*      */ 
/*  247 */     if (evictsBySize())
/*      */     {
/*  249 */       int maximumSegmentSize = this.maximumSize / segmentCount + 1;
/*  250 */       int remainder = this.maximumSize % segmentCount;
/*  251 */       for (int i = 0; i < this.segments.length; i++) {
/*  252 */         if (i == remainder) {
/*  253 */           maximumSegmentSize--;
/*      */         }
/*  255 */         this.segments[i] = createSegment(segmentSize, maximumSegmentSize);
/*      */       }
/*      */     }
/*      */     else {
/*  259 */       for (int i = 0; i < this.segments.length; i++)
/*  260 */         this.segments[i] = createSegment(segmentSize, -1);
/*      */     }
/*      */   }
/*      */ 
/*      */   boolean evictsBySize()
/*      */   {
/*  267 */     return this.maximumSize != -1;
/*      */   }
/*      */ 
/*      */   boolean expires() {
/*  271 */     return (expiresAfterWrite()) || (expiresAfterAccess());
/*      */   }
/*      */ 
/*      */   boolean expiresAfterWrite() {
/*  275 */     return this.expireAfterWriteNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean expiresAfterAccess() {
/*  279 */     return this.expireAfterAccessNanos > 0L;
/*      */   }
/*      */ 
/*      */   boolean usesKeyReferences() {
/*  283 */     return this.keyStrength != Strength.STRONG;
/*      */   }
/*      */ 
/*      */   boolean usesValueReferences() {
/*  287 */     return this.valueStrength != Strength.STRONG;
/*      */   }
/*      */ 
/*      */   static <K, V> ValueReference<K, V> unset()
/*      */   {
/*  666 */     return UNSET;
/*      */   }
/*      */ 
/*      */   static <K, V> ReferenceEntry<K, V> nullEntry()
/*      */   {
/*  917 */     return NullEntry.INSTANCE;
/*      */   }
/*      */ 
/*      */   static <E> Queue<E> discardingQueue()
/*      */   {
/*  952 */     return DISCARDING_QUEUE;
/*      */   }
/*      */ 
/*      */   static int rehash(int h)
/*      */   {
/* 1865 */     h += (h << 15 ^ 0xFFFFCD7D);
/* 1866 */     h ^= h >>> 10;
/* 1867 */     h += (h << 3);
/* 1868 */     h ^= h >>> 6;
/* 1869 */     h += (h << 2) + (h << 14);
/* 1870 */     return h ^ h >>> 16;
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable ReferenceEntry<K, V> next)
/*      */   {
/* 1879 */     return segmentFor(hash).newEntry(key, hash, next);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ReferenceEntry<K, V> copyEntry(ReferenceEntry<K, V> original, ReferenceEntry<K, V> newNext)
/*      */   {
/* 1888 */     int hash = original.getHash();
/* 1889 */     return segmentFor(hash).copyEntry(original, newNext);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   @VisibleForTesting
/*      */   ValueReference<K, V> newValueReference(ReferenceEntry<K, V> entry, V value)
/*      */   {
/* 1898 */     int hash = entry.getHash();
/* 1899 */     return this.valueStrength.referenceValue(segmentFor(hash), entry, value);
/*      */   }
/*      */ 
/*      */   int hash(Object key) {
/* 1903 */     int h = this.keyEquivalence.hash(key);
/* 1904 */     return rehash(h);
/*      */   }
/*      */ 
/*      */   void reclaimValue(ValueReference<K, V> valueReference) {
/* 1908 */     ReferenceEntry entry = valueReference.getEntry();
/* 1909 */     int hash = entry.getHash();
/* 1910 */     segmentFor(hash).reclaimValue(entry.getKey(), hash, valueReference);
/*      */   }
/*      */ 
/*      */   void reclaimKey(ReferenceEntry<K, V> entry) {
/* 1914 */     int hash = entry.getHash();
/* 1915 */     segmentFor(hash).reclaimKey(entry, hash);
/*      */   }
/*      */ 
/*      */   @VisibleForTesting
/*      */   boolean isLive(ReferenceEntry<K, V> entry)
/*      */   {
/* 1924 */     return segmentFor(entry.getHash()).getLiveValue(entry) != null;
/*      */   }
/*      */ 
/*      */   Segment<K, V> segmentFor(int hash)
/*      */   {
/* 1935 */     return this.segments[(hash >>> this.segmentShift & this.segmentMask)];
/*      */   }
/*      */ 
/*      */   Segment<K, V> createSegment(int initialCapacity, int maxSegmentSize) {
/* 1939 */     return new Segment(this, initialCapacity, maxSegmentSize);
/*      */   }
/*      */ 
/*      */   V getLiveValue(ReferenceEntry<K, V> entry)
/*      */   {
/* 1948 */     if (entry.getKey() == null) {
/* 1949 */       return null;
/*      */     }
/* 1951 */     Object value = entry.getValueReference().get();
/* 1952 */     if (value == null) {
/* 1953 */       return null;
/*      */     }
/*      */ 
/* 1956 */     if ((expires()) && (isExpired(entry))) {
/* 1957 */       return null;
/*      */     }
/* 1959 */     return value;
/*      */   }
/*      */ 
/*      */   boolean isExpired(ReferenceEntry<K, V> entry)
/*      */   {
/* 1968 */     return isExpired(entry, this.ticker.read());
/*      */   }
/*      */ 
/*      */   boolean isExpired(ReferenceEntry<K, V> entry, long now)
/*      */   {
/* 1976 */     return now - entry.getExpirationTime() > 0L;
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectExpirables(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next) {
/* 1981 */     previous.setNextExpirable(next);
/* 1982 */     next.setPreviousExpirable(previous);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyExpirable(ReferenceEntry<K, V> nulled) {
/* 1987 */     ReferenceEntry nullEntry = nullEntry();
/* 1988 */     nulled.setNextExpirable(nullEntry);
/* 1989 */     nulled.setPreviousExpirable(nullEntry);
/*      */   }
/*      */ 
/*      */   void processPendingNotifications()
/*      */   {
/*      */     MapMaker.RemovalNotification notification;
/* 2001 */     while ((notification = (MapMaker.RemovalNotification)this.removalNotificationQueue.poll()) != null)
/*      */       try {
/* 2003 */         this.removalListener.onRemoval(notification);
/*      */       } catch (Exception e) {
/* 2005 */         logger.log(Level.WARNING, "Exception thrown by removal listener", e);
/*      */       }
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void connectEvictables(ReferenceEntry<K, V> previous, ReferenceEntry<K, V> next)
/*      */   {
/* 2013 */     previous.setNextEvictable(next);
/* 2014 */     next.setPreviousEvictable(previous);
/*      */   }
/*      */ 
/*      */   @GuardedBy("Segment.this")
/*      */   static <K, V> void nullifyEvictable(ReferenceEntry<K, V> nulled) {
/* 2019 */     ReferenceEntry nullEntry = nullEntry();
/* 2020 */     nulled.setNextEvictable(nullEntry);
/* 2021 */     nulled.setPreviousEvictable(nullEntry);
/*      */   }
/*      */ 
/*      */   final Segment<K, V>[] newSegmentArray(int ssize)
/*      */   {
/* 2026 */     return new Segment[ssize];
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/* 3458 */     long sum = 0L;
/* 3459 */     Segment[] segments = this.segments;
/* 3460 */     for (int i = 0; i < segments.length; i++) {
/* 3461 */       if (segments[i].count != 0) {
/* 3462 */         return false;
/*      */       }
/* 3464 */       sum += segments[i].modCount;
/*      */     }
/*      */ 
/* 3467 */     if (sum != 0L) {
/* 3468 */       for (int i = 0; i < segments.length; i++) {
/* 3469 */         if (segments[i].count != 0) {
/* 3470 */           return false;
/*      */         }
/* 3472 */         sum -= segments[i].modCount;
/*      */       }
/* 3474 */       if (sum != 0L) {
/* 3475 */         return false;
/*      */       }
/*      */     }
/* 3478 */     return true;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/* 3483 */     Segment[] segments = this.segments;
/* 3484 */     long sum = 0L;
/* 3485 */     for (int i = 0; i < segments.length; i++) {
/* 3486 */       sum += segments[i].count;
/*      */     }
/* 3488 */     return Ints.saturatedCast(sum);
/*      */   }
/*      */ 
/*      */   public V get(@Nullable Object key)
/*      */   {
/* 3493 */     if (key == null) {
/* 3494 */       return null;
/*      */     }
/* 3496 */     int hash = hash(key);
/* 3497 */     return segmentFor(hash).get(key, hash);
/*      */   }
/*      */ 
/*      */   ReferenceEntry<K, V> getEntry(@Nullable Object key)
/*      */   {
/* 3505 */     if (key == null) {
/* 3506 */       return null;
/*      */     }
/* 3508 */     int hash = hash(key);
/* 3509 */     return segmentFor(hash).getEntry(key, hash);
/*      */   }
/*      */ 
/*      */   ReferenceEntry<K, V> getLiveEntry(@Nullable Object key)
/*      */   {
/* 3516 */     if (key == null) {
/* 3517 */       return null;
/*      */     }
/* 3519 */     int hash = hash(key);
/* 3520 */     return segmentFor(hash).getLiveEntry(key, hash);
/*      */   }
/*      */ 
/*      */   public boolean containsKey(@Nullable Object key)
/*      */   {
/* 3525 */     if (key == null) {
/* 3526 */       return false;
/*      */     }
/* 3528 */     int hash = hash(key);
/* 3529 */     return segmentFor(hash).containsKey(key, hash);
/*      */   }
/*      */ 
/*      */   public boolean containsValue(@Nullable Object value)
/*      */   {
/* 3534 */     if (value == null) {
/* 3535 */       return false;
/*      */     }
/*      */ 
/* 3543 */     Segment[] segments = this.segments;
/* 3544 */     long last = -1L;
/* 3545 */     for (int i = 0; i < 3; i++) {
/* 3546 */       long sum = 0L;
/* 3547 */       for (Segment segment : segments)
/*      */       {
/* 3550 */         int c = segment.count;
/*      */ 
/* 3552 */         AtomicReferenceArray table = segment.table;
/* 3553 */         for (int j = 0; j < table.length(); j++) {
/* 3554 */           for (ReferenceEntry e = (ReferenceEntry)table.get(j); e != null; e = e.getNext()) {
/* 3555 */             Object v = segment.getLiveValue(e);
/* 3556 */             if ((v != null) && (this.valueEquivalence.equivalent(value, v))) {
/* 3557 */               return true;
/*      */             }
/*      */           }
/*      */         }
/* 3561 */         sum += segment.modCount;
/*      */       }
/* 3563 */       if (sum == last) {
/*      */         break;
/*      */       }
/* 3566 */       last = sum;
/*      */     }
/* 3568 */     return false;
/*      */   }
/*      */ 
/*      */   public V put(K key, V value)
/*      */   {
/* 3573 */     Preconditions.checkNotNull(key);
/* 3574 */     Preconditions.checkNotNull(value);
/* 3575 */     int hash = hash(key);
/* 3576 */     return segmentFor(hash).put(key, hash, value, false);
/*      */   }
/*      */ 
/*      */   public V putIfAbsent(K key, V value)
/*      */   {
/* 3581 */     Preconditions.checkNotNull(key);
/* 3582 */     Preconditions.checkNotNull(value);
/* 3583 */     int hash = hash(key);
/* 3584 */     return segmentFor(hash).put(key, hash, value, true);
/*      */   }
/*      */ 
/*      */   public void putAll(Map<? extends K, ? extends V> m)
/*      */   {
/* 3589 */     for (Map.Entry e : m.entrySet())
/* 3590 */       put(e.getKey(), e.getValue());
/*      */   }
/*      */ 
/*      */   public V remove(@Nullable Object key)
/*      */   {
/* 3596 */     if (key == null) {
/* 3597 */       return null;
/*      */     }
/* 3599 */     int hash = hash(key);
/* 3600 */     return segmentFor(hash).remove(key, hash);
/*      */   }
/*      */ 
/*      */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*      */   {
/* 3605 */     if ((key == null) || (value == null)) {
/* 3606 */       return false;
/*      */     }
/* 3608 */     int hash = hash(key);
/* 3609 */     return segmentFor(hash).remove(key, hash, value);
/*      */   }
/*      */ 
/*      */   public boolean replace(K key, @Nullable V oldValue, V newValue)
/*      */   {
/* 3614 */     Preconditions.checkNotNull(key);
/* 3615 */     Preconditions.checkNotNull(newValue);
/* 3616 */     if (oldValue == null) {
/* 3617 */       return false;
/*      */     }
/* 3619 */     int hash = hash(key);
/* 3620 */     return segmentFor(hash).replace(key, hash, oldValue, newValue);
/*      */   }
/*      */ 
/*      */   public V replace(K key, V value)
/*      */   {
/* 3625 */     Preconditions.checkNotNull(key);
/* 3626 */     Preconditions.checkNotNull(value);
/* 3627 */     int hash = hash(key);
/* 3628 */     return segmentFor(hash).replace(key, hash, value);
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/* 3633 */     for (Segment segment : this.segments)
/* 3634 */       segment.clear();
/*      */   }
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/* 3642 */     Set ks = this.keySet;
/* 3643 */     return this.keySet = new KeySet();
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/* 3650 */     Collection vs = this.values;
/* 3651 */     return this.values = new Values();
/*      */   }
/*      */ 
/*      */   public Set<Map.Entry<K, V>> entrySet()
/*      */   {
/* 3658 */     Set es = this.entrySet;
/* 3659 */     return this.entrySet = new EntrySet();
/*      */   }
/*      */ 
/*      */   Object writeReplace()
/*      */   {
/* 3956 */     return new SerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this);
/*      */   }
/*      */ 
/*      */   private static final class SerializationProxy<K, V> extends MapMakerInternalMap.AbstractSerializationProxy<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 3L;
/*      */ 
/*      */     SerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, int maximumSize, int concurrencyLevel, MapMaker.RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> delegate)
/*      */     {
/* 4060 */       super(valueStrength, keyEquivalence, valueEquivalence, expireAfterWriteNanos, expireAfterAccessNanos, maximumSize, concurrencyLevel, removalListener, delegate);
/*      */     }
/*      */ 
/*      */     private void writeObject(ObjectOutputStream out) throws IOException
/*      */     {
/* 4065 */       out.defaultWriteObject();
/* 4066 */       writeMapTo(out);
/*      */     }
/*      */ 
/*      */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
/* 4070 */       in.defaultReadObject();
/* 4071 */       MapMaker mapMaker = readMapMaker(in);
/* 4072 */       this.delegate = mapMaker.makeMap();
/* 4073 */       readEntries(in);
/*      */     }
/*      */ 
/*      */     private Object readResolve() {
/* 4077 */       return this.delegate;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class AbstractSerializationProxy<K, V> extends ForwardingConcurrentMap<K, V>
/*      */     implements Serializable
/*      */   {
/*      */     private static final long serialVersionUID = 3L;
/*      */     final MapMakerInternalMap.Strength keyStrength;
/*      */     final MapMakerInternalMap.Strength valueStrength;
/*      */     final Equivalence<Object> keyEquivalence;
/*      */     final Equivalence<Object> valueEquivalence;
/*      */     final long expireAfterWriteNanos;
/*      */     final long expireAfterAccessNanos;
/*      */     final int maximumSize;
/*      */     final int concurrencyLevel;
/*      */     final MapMaker.RemovalListener<? super K, ? super V> removalListener;
/*      */     transient ConcurrentMap<K, V> delegate;
/*      */ 
/*      */     AbstractSerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, int maximumSize, int concurrencyLevel, MapMaker.RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> delegate)
/*      */     {
/* 3986 */       this.keyStrength = keyStrength;
/* 3987 */       this.valueStrength = valueStrength;
/* 3988 */       this.keyEquivalence = keyEquivalence;
/* 3989 */       this.valueEquivalence = valueEquivalence;
/* 3990 */       this.expireAfterWriteNanos = expireAfterWriteNanos;
/* 3991 */       this.expireAfterAccessNanos = expireAfterAccessNanos;
/* 3992 */       this.maximumSize = maximumSize;
/* 3993 */       this.concurrencyLevel = concurrencyLevel;
/* 3994 */       this.removalListener = removalListener;
/* 3995 */       this.delegate = delegate;
/*      */     }
/*      */ 
/*      */     protected ConcurrentMap<K, V> delegate()
/*      */     {
/* 4000 */       return this.delegate;
/*      */     }
/*      */ 
/*      */     void writeMapTo(ObjectOutputStream out) throws IOException {
/* 4004 */       out.writeInt(this.delegate.size());
/* 4005 */       for (Map.Entry entry : this.delegate.entrySet()) {
/* 4006 */         out.writeObject(entry.getKey());
/* 4007 */         out.writeObject(entry.getValue());
/*      */       }
/* 4009 */       out.writeObject(null);
/*      */     }
/*      */ 
/*      */     MapMaker readMapMaker(ObjectInputStream in) throws IOException
/*      */     {
/* 4014 */       int size = in.readInt();
/* 4015 */       MapMaker mapMaker = new MapMaker().initialCapacity(size).setKeyStrength(this.keyStrength).setValueStrength(this.valueStrength).keyEquivalence(this.keyEquivalence).valueEquivalence(this.valueEquivalence).concurrencyLevel(this.concurrencyLevel);
/*      */ 
/* 4022 */       mapMaker.removalListener(this.removalListener);
/* 4023 */       if (this.expireAfterWriteNanos > 0L) {
/* 4024 */         mapMaker.expireAfterWrite(this.expireAfterWriteNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4026 */       if (this.expireAfterAccessNanos > 0L) {
/* 4027 */         mapMaker.expireAfterAccess(this.expireAfterAccessNanos, TimeUnit.NANOSECONDS);
/*      */       }
/* 4029 */       if (this.maximumSize != -1) {
/* 4030 */         mapMaker.maximumSize(this.maximumSize);
/*      */       }
/* 4032 */       return mapMaker;
/*      */     }
/*      */ 
/*      */     void readEntries(ObjectInputStream in) throws IOException, ClassNotFoundException
/*      */     {
/*      */       while (true) {
/* 4038 */         Object key = in.readObject();
/* 4039 */         if (key == null) {
/*      */           break;
/*      */         }
/* 4042 */         Object value = in.readObject();
/* 4043 */         this.delegate.put(key, value);
/*      */       }
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
/* 3907 */       return new MapMakerInternalMap.EntryIterator(MapMakerInternalMap.this);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3912 */       if (!(o instanceof Map.Entry)) {
/* 3913 */         return false;
/*      */       }
/* 3915 */       Map.Entry e = (Map.Entry)o;
/* 3916 */       Object key = e.getKey();
/* 3917 */       if (key == null) {
/* 3918 */         return false;
/*      */       }
/* 3920 */       Object v = MapMakerInternalMap.this.get(key);
/*      */ 
/* 3922 */       return (v != null) && (MapMakerInternalMap.this.valueEquivalence.equivalent(e.getValue(), v));
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3927 */       if (!(o instanceof Map.Entry)) {
/* 3928 */         return false;
/*      */       }
/* 3930 */       Map.Entry e = (Map.Entry)o;
/* 3931 */       Object key = e.getKey();
/* 3932 */       return (key != null) && (MapMakerInternalMap.this.remove(key, e.getValue()));
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3937 */       return MapMakerInternalMap.this.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3942 */       return MapMakerInternalMap.this.isEmpty();
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3947 */       MapMakerInternalMap.this.clear();
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
/* 3879 */       return new MapMakerInternalMap.ValueIterator(MapMakerInternalMap.this);
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3884 */       return MapMakerInternalMap.this.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3889 */       return MapMakerInternalMap.this.isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3894 */       return MapMakerInternalMap.this.containsValue(o);
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3899 */       MapMakerInternalMap.this.clear();
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
/* 3846 */       return new MapMakerInternalMap.KeyIterator(MapMakerInternalMap.this);
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3851 */       return MapMakerInternalMap.this.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3856 */       return MapMakerInternalMap.this.isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3861 */       return MapMakerInternalMap.this.containsKey(o);
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3866 */       return MapMakerInternalMap.this.remove(o) != null;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3871 */       MapMakerInternalMap.this.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class EntryIterator extends MapMakerInternalMap<K, V>.HashIterator
/*      */     implements Iterator<Map.Entry<K, V>>
/*      */   {
/*      */     EntryIterator()
/*      */     {
/* 3834 */       super();
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> next() {
/* 3838 */       return nextEntry();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class WriteThroughEntry extends AbstractMapEntry<K, V>
/*      */   {
/*      */     final K key;
/*      */     V value;
/*      */ 
/*      */     WriteThroughEntry(V key)
/*      */     {
/* 3796 */       this.key = key;
/* 3797 */       this.value = value;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 3802 */       return this.key;
/*      */     }
/*      */ 
/*      */     public V getValue()
/*      */     {
/* 3807 */       return this.value;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object)
/*      */     {
/* 3813 */       if ((object instanceof Map.Entry)) {
/* 3814 */         Map.Entry that = (Map.Entry)object;
/* 3815 */         return (this.key.equals(that.getKey())) && (this.value.equals(that.getValue()));
/*      */       }
/* 3817 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode()
/*      */     {
/* 3823 */       return this.key.hashCode() ^ this.value.hashCode();
/*      */     }
/*      */ 
/*      */     public V setValue(V newValue)
/*      */     {
/* 3828 */       Object oldValue = MapMakerInternalMap.this.put(this.key, newValue);
/* 3829 */       this.value = newValue;
/* 3830 */       return oldValue;
/*      */     }
/*      */   }
/*      */ 
/*      */   final class ValueIterator extends MapMakerInternalMap<K, V>.HashIterator
/*      */     implements Iterator<V>
/*      */   {
/*      */     ValueIterator()
/*      */     {
/* 3779 */       super();
/*      */     }
/*      */ 
/*      */     public V next() {
/* 3783 */       return nextEntry().getValue();
/*      */     }
/*      */   }
/*      */ 
/*      */   final class KeyIterator extends MapMakerInternalMap<K, V>.HashIterator
/*      */     implements Iterator<K>
/*      */   {
/*      */     KeyIterator()
/*      */     {
/* 3771 */       super();
/*      */     }
/*      */ 
/*      */     public K next() {
/* 3775 */       return nextEntry().getKey();
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract class HashIterator
/*      */   {
/*      */     int nextSegmentIndex;
/*      */     int nextTableIndex;
/*      */     MapMakerInternalMap.Segment<K, V> currentSegment;
/*      */     AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> currentTable;
/*      */     MapMakerInternalMap.ReferenceEntry<K, V> nextEntry;
/*      */     MapMakerInternalMap<K, V>.WriteThroughEntry nextExternal;
/*      */     MapMakerInternalMap<K, V>.WriteThroughEntry lastReturned;
/*      */ 
/*      */     HashIterator()
/*      */     {
/* 3675 */       this.nextSegmentIndex = (MapMakerInternalMap.this.segments.length - 1);
/* 3676 */       this.nextTableIndex = -1;
/* 3677 */       advance();
/*      */     }
/*      */ 
/*      */     final void advance() {
/* 3681 */       this.nextExternal = null;
/*      */ 
/* 3683 */       if (nextInChain()) {
/* 3684 */         return;
/*      */       }
/*      */ 
/* 3687 */       if (nextInTable()) {
/* 3688 */         return;
/*      */       }
/*      */ 
/* 3691 */       while (this.nextSegmentIndex >= 0) {
/* 3692 */         this.currentSegment = MapMakerInternalMap.this.segments[(this.nextSegmentIndex--)];
/* 3693 */         if (this.currentSegment.count != 0) {
/* 3694 */           this.currentTable = this.currentSegment.table;
/* 3695 */           this.nextTableIndex = (this.currentTable.length() - 1);
/* 3696 */           if (nextInTable());
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean nextInChain()
/*      */     {
/* 3707 */       if (this.nextEntry != null) {
/* 3708 */         for (this.nextEntry = this.nextEntry.getNext(); this.nextEntry != null; this.nextEntry = this.nextEntry.getNext()) {
/* 3709 */           if (advanceTo(this.nextEntry)) {
/* 3710 */             return true;
/*      */           }
/*      */         }
/*      */       }
/* 3714 */       return false;
/*      */     }
/*      */ 
/*      */     boolean nextInTable()
/*      */     {
/* 3721 */       while (this.nextTableIndex >= 0) {
/* 3722 */         if (((this.nextEntry = (MapMakerInternalMap.ReferenceEntry)this.currentTable.get(this.nextTableIndex--)) != null) && (
/* 3723 */           (advanceTo(this.nextEntry)) || (nextInChain()))) {
/* 3724 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 3728 */       return false;
/*      */     }
/*      */ 
/*      */     boolean advanceTo(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/*      */       try
/*      */       {
/* 3737 */         Object key = entry.getKey();
/* 3738 */         Object value = MapMakerInternalMap.this.getLiveValue(entry);
/*      */         boolean bool;
/* 3739 */         if (value != null) {
/* 3740 */           this.nextExternal = new MapMakerInternalMap.WriteThroughEntry(MapMakerInternalMap.this, key, value);
/* 3741 */           return true;
/*      */         }
/*      */ 
/* 3744 */         return false;
/*      */       }
/*      */       finally {
/* 3747 */         this.currentSegment.postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean hasNext() {
/* 3752 */       return this.nextExternal != null;
/*      */     }
/*      */ 
/*      */     MapMakerInternalMap<K, V>.WriteThroughEntry nextEntry() {
/* 3756 */       if (this.nextExternal == null) {
/* 3757 */         throw new NoSuchElementException();
/*      */       }
/* 3759 */       this.lastReturned = this.nextExternal;
/* 3760 */       advance();
/* 3761 */       return this.lastReturned;
/*      */     }
/*      */ 
/*      */     public void remove() {
/* 3765 */       Preconditions.checkState(this.lastReturned != null);
/* 3766 */       MapMakerInternalMap.this.remove(this.lastReturned.getKey());
/* 3767 */       this.lastReturned = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class CleanupMapTask
/*      */     implements Runnable
/*      */   {
/*      */     final WeakReference<MapMakerInternalMap<?, ?>> mapReference;
/*      */ 
/*      */     public CleanupMapTask(MapMakerInternalMap<?, ?> map)
/*      */     {
/* 3431 */       this.mapReference = new WeakReference(map);
/*      */     }
/*      */ 
/*      */     public void run()
/*      */     {
/* 3436 */       MapMakerInternalMap map = (MapMakerInternalMap)this.mapReference.get();
/* 3437 */       if (map == null) {
/* 3438 */         throw new CancellationException();
/*      */       }
/*      */ 
/* 3441 */       for (MapMakerInternalMap.Segment segment : map.segments)
/* 3442 */         segment.runCleanup();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class ExpirationQueue<K, V> extends AbstractQueue<MapMakerInternalMap.ReferenceEntry<K, V>>
/*      */   {
/* 3303 */     final MapMakerInternalMap.ReferenceEntry<K, V> head = new MapMakerInternalMap.AbstractReferenceEntry()
/*      */     {
/* 3313 */       MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = this;
/*      */ 
/* 3325 */       MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = this;
/*      */ 
/*      */       public long getExpirationTime()
/*      */       {
/* 3307 */         return 9223372036854775807L;
/*      */       }
/*      */ 
/*      */       public void setExpirationTime(long time)
/*      */       {
/*      */       }
/*      */ 
/*      */       public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */       {
/* 3317 */         return this.nextExpirable;
/*      */       }
/*      */ 
/*      */       public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */       {
/* 3322 */         this.nextExpirable = next;
/*      */       }
/*      */ 
/*      */       public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */       {
/* 3329 */         return this.previousExpirable;
/*      */       }
/*      */ 
/*      */       public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */       {
/* 3334 */         this.previousExpirable = previous;
/*      */       }
/* 3303 */     };
/*      */ 
/*      */     public boolean offer(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 3343 */       MapMakerInternalMap.connectExpirables(entry.getPreviousExpirable(), entry.getNextExpirable());
/*      */ 
/* 3346 */       MapMakerInternalMap.connectExpirables(this.head.getPreviousExpirable(), entry);
/* 3347 */       MapMakerInternalMap.connectExpirables(entry, this.head);
/*      */ 
/* 3349 */       return true;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> peek()
/*      */     {
/* 3354 */       MapMakerInternalMap.ReferenceEntry next = this.head.getNextExpirable();
/* 3355 */       return next == this.head ? null : next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> poll()
/*      */     {
/* 3360 */       MapMakerInternalMap.ReferenceEntry next = this.head.getNextExpirable();
/* 3361 */       if (next == this.head) {
/* 3362 */         return null;
/*      */       }
/*      */ 
/* 3365 */       remove(next);
/* 3366 */       return next;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3372 */       MapMakerInternalMap.ReferenceEntry e = (MapMakerInternalMap.ReferenceEntry)o;
/* 3373 */       MapMakerInternalMap.ReferenceEntry previous = e.getPreviousExpirable();
/* 3374 */       MapMakerInternalMap.ReferenceEntry next = e.getNextExpirable();
/* 3375 */       MapMakerInternalMap.connectExpirables(previous, next);
/* 3376 */       MapMakerInternalMap.nullifyExpirable(e);
/*      */ 
/* 3378 */       return next != MapMakerInternalMap.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3384 */       MapMakerInternalMap.ReferenceEntry e = (MapMakerInternalMap.ReferenceEntry)o;
/* 3385 */       return e.getNextExpirable() != MapMakerInternalMap.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3390 */       return this.head.getNextExpirable() == this.head;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3395 */       int size = 0;
/* 3396 */       for (MapMakerInternalMap.ReferenceEntry e = this.head.getNextExpirable(); e != this.head; e = e.getNextExpirable()) {
/* 3397 */         size++;
/*      */       }
/* 3399 */       return size;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3404 */       MapMakerInternalMap.ReferenceEntry e = this.head.getNextExpirable();
/* 3405 */       while (e != this.head) {
/* 3406 */         MapMakerInternalMap.ReferenceEntry next = e.getNextExpirable();
/* 3407 */         MapMakerInternalMap.nullifyExpirable(e);
/* 3408 */         e = next;
/*      */       }
/*      */ 
/* 3411 */       this.head.setNextExpirable(this.head);
/* 3412 */       this.head.setPreviousExpirable(this.head);
/*      */     }
/*      */ 
/*      */     public Iterator<MapMakerInternalMap.ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3417 */       return new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected MapMakerInternalMap.ReferenceEntry<K, V> computeNext(MapMakerInternalMap.ReferenceEntry<K, V> previous) {
/* 3420 */           MapMakerInternalMap.ReferenceEntry next = previous.getNextExpirable();
/* 3421 */           return next == MapMakerInternalMap.ExpirationQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class EvictionQueue<K, V> extends AbstractQueue<MapMakerInternalMap.ReferenceEntry<K, V>>
/*      */   {
/* 3175 */     final MapMakerInternalMap.ReferenceEntry<K, V> head = new MapMakerInternalMap.AbstractReferenceEntry()
/*      */     {
/* 3177 */       MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = this;
/*      */ 
/* 3189 */       MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = this;
/*      */ 
/*      */       public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */       {
/* 3181 */         return this.nextEvictable;
/*      */       }
/*      */ 
/*      */       public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */       {
/* 3186 */         this.nextEvictable = next;
/*      */       }
/*      */ 
/*      */       public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */       {
/* 3193 */         return this.previousEvictable;
/*      */       }
/*      */ 
/*      */       public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */       {
/* 3198 */         this.previousEvictable = previous;
/*      */       }
/* 3175 */     };
/*      */ 
/*      */     public boolean offer(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 3207 */       MapMakerInternalMap.connectEvictables(entry.getPreviousEvictable(), entry.getNextEvictable());
/*      */ 
/* 3210 */       MapMakerInternalMap.connectEvictables(this.head.getPreviousEvictable(), entry);
/* 3211 */       MapMakerInternalMap.connectEvictables(entry, this.head);
/*      */ 
/* 3213 */       return true;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> peek()
/*      */     {
/* 3218 */       MapMakerInternalMap.ReferenceEntry next = this.head.getNextEvictable();
/* 3219 */       return next == this.head ? null : next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> poll()
/*      */     {
/* 3224 */       MapMakerInternalMap.ReferenceEntry next = this.head.getNextEvictable();
/* 3225 */       if (next == this.head) {
/* 3226 */         return null;
/*      */       }
/*      */ 
/* 3229 */       remove(next);
/* 3230 */       return next;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o)
/*      */     {
/* 3236 */       MapMakerInternalMap.ReferenceEntry e = (MapMakerInternalMap.ReferenceEntry)o;
/* 3237 */       MapMakerInternalMap.ReferenceEntry previous = e.getPreviousEvictable();
/* 3238 */       MapMakerInternalMap.ReferenceEntry next = e.getNextEvictable();
/* 3239 */       MapMakerInternalMap.connectEvictables(previous, next);
/* 3240 */       MapMakerInternalMap.nullifyEvictable(e);
/*      */ 
/* 3242 */       return next != MapMakerInternalMap.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o)
/*      */     {
/* 3248 */       MapMakerInternalMap.ReferenceEntry e = (MapMakerInternalMap.ReferenceEntry)o;
/* 3249 */       return e.getNextEvictable() != MapMakerInternalMap.NullEntry.INSTANCE;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 3254 */       return this.head.getNextEvictable() == this.head;
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 3259 */       int size = 0;
/* 3260 */       for (MapMakerInternalMap.ReferenceEntry e = this.head.getNextEvictable(); e != this.head; e = e.getNextEvictable()) {
/* 3261 */         size++;
/*      */       }
/* 3263 */       return size;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 3268 */       MapMakerInternalMap.ReferenceEntry e = this.head.getNextEvictable();
/* 3269 */       while (e != this.head) {
/* 3270 */         MapMakerInternalMap.ReferenceEntry next = e.getNextEvictable();
/* 3271 */         MapMakerInternalMap.nullifyEvictable(e);
/* 3272 */         e = next;
/*      */       }
/*      */ 
/* 3275 */       this.head.setNextEvictable(this.head);
/* 3276 */       this.head.setPreviousEvictable(this.head);
/*      */     }
/*      */ 
/*      */     public Iterator<MapMakerInternalMap.ReferenceEntry<K, V>> iterator()
/*      */     {
/* 3281 */       return new AbstractSequentialIterator(peek())
/*      */       {
/*      */         protected MapMakerInternalMap.ReferenceEntry<K, V> computeNext(MapMakerInternalMap.ReferenceEntry<K, V> previous) {
/* 3284 */           MapMakerInternalMap.ReferenceEntry next = previous.getNextEvictable();
/* 3285 */           return next == MapMakerInternalMap.EvictionQueue.this.head ? null : next;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   static class Segment<K, V> extends ReentrantLock
/*      */   {
/*      */     final MapMakerInternalMap<K, V> map;
/*      */     volatile int count;
/*      */     int modCount;
/*      */     int threshold;
/*      */     volatile AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> table;
/*      */     final int maxSegmentSize;
/*      */     final ReferenceQueue<K> keyReferenceQueue;
/*      */     final ReferenceQueue<V> valueReferenceQueue;
/*      */     final Queue<MapMakerInternalMap.ReferenceEntry<K, V>> recencyQueue;
/* 2126 */     final AtomicInteger readCount = new AtomicInteger();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     final Queue<MapMakerInternalMap.ReferenceEntry<K, V>> evictionQueue;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     final Queue<MapMakerInternalMap.ReferenceEntry<K, V>> expirationQueue;
/*      */ 
/*      */     Segment(MapMakerInternalMap<K, V> map, int initialCapacity, int maxSegmentSize)
/*      */     {
/* 2143 */       this.map = map;
/* 2144 */       this.maxSegmentSize = maxSegmentSize;
/* 2145 */       initTable(newEntryArray(initialCapacity));
/*      */ 
/* 2147 */       this.keyReferenceQueue = (map.usesKeyReferences() ? new ReferenceQueue() : null);
/*      */ 
/* 2150 */       this.valueReferenceQueue = (map.usesValueReferences() ? new ReferenceQueue() : null);
/*      */ 
/* 2153 */       this.recencyQueue = ((map.evictsBySize()) || (map.expiresAfterAccess()) ? new ConcurrentLinkedQueue() : MapMakerInternalMap.discardingQueue());
/*      */ 
/* 2157 */       this.evictionQueue = (map.evictsBySize() ? new MapMakerInternalMap.EvictionQueue() : MapMakerInternalMap.discardingQueue());
/*      */ 
/* 2161 */       this.expirationQueue = (map.expires() ? new MapMakerInternalMap.ExpirationQueue() : MapMakerInternalMap.discardingQueue());
/*      */     }
/*      */ 
/*      */     AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> newEntryArray(int size)
/*      */     {
/* 2167 */       return new AtomicReferenceArray(size);
/*      */     }
/*      */ 
/*      */     void initTable(AtomicReferenceArray<MapMakerInternalMap.ReferenceEntry<K, V>> newTable) {
/* 2171 */       this.threshold = (newTable.length() * 3 / 4);
/* 2172 */       if (this.threshold == this.maxSegmentSize)
/*      */       {
/* 2174 */         this.threshold += 1;
/*      */       }
/* 2176 */       this.table = newTable;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     MapMakerInternalMap.ReferenceEntry<K, V> newEntry(K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next) {
/* 2181 */       return this.map.entryFactory.newEntry(this, key, hash, next);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.ReferenceEntry<K, V> original, MapMakerInternalMap.ReferenceEntry<K, V> newNext)
/*      */     {
/* 2190 */       if (original.getKey() == null)
/*      */       {
/* 2192 */         return null;
/*      */       }
/*      */ 
/* 2195 */       MapMakerInternalMap.ValueReference valueReference = original.getValueReference();
/* 2196 */       Object value = valueReference.get();
/* 2197 */       if ((value == null) && (!valueReference.isComputingReference()))
/*      */       {
/* 2199 */         return null;
/*      */       }
/*      */ 
/* 2202 */       MapMakerInternalMap.ReferenceEntry newEntry = this.map.entryFactory.copyEntry(this, original, newNext);
/* 2203 */       newEntry.setValueReference(valueReference.copyFor(this.valueReferenceQueue, value, newEntry));
/* 2204 */       return newEntry;
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void setValue(MapMakerInternalMap.ReferenceEntry<K, V> entry, V value)
/*      */     {
/* 2212 */       MapMakerInternalMap.ValueReference valueReference = this.map.valueStrength.referenceValue(this, entry, value);
/* 2213 */       entry.setValueReference(valueReference);
/* 2214 */       recordWrite(entry);
/*      */     }
/*      */ 
/*      */     void tryDrainReferenceQueues()
/*      */     {
/* 2223 */       if (tryLock())
/*      */         try {
/* 2225 */           drainReferenceQueues();
/*      */         } finally {
/* 2227 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainReferenceQueues()
/*      */     {
/* 2238 */       if (this.map.usesKeyReferences()) {
/* 2239 */         drainKeyReferenceQueue();
/*      */       }
/* 2241 */       if (this.map.usesValueReferences())
/* 2242 */         drainValueReferenceQueue();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainKeyReferenceQueue()
/*      */     {
/* 2249 */       int i = 0;
/*      */       Reference ref;
/* 2254 */       for (; (ref = this.keyReferenceQueue.poll()) != null; 
/* 2254 */         i == 16)
/*      */       {
/* 2252 */         MapMakerInternalMap.ReferenceEntry entry = (MapMakerInternalMap.ReferenceEntry)ref;
/* 2253 */         this.map.reclaimKey(entry);
/* 2254 */         i++;
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainValueReferenceQueue()
/*      */     {
/* 2263 */       int i = 0;
/*      */       Reference ref;
/* 2268 */       for (; (ref = this.valueReferenceQueue.poll()) != null; 
/* 2268 */         i == 16)
/*      */       {
/* 2266 */         MapMakerInternalMap.ValueReference valueReference = (MapMakerInternalMap.ValueReference)ref;
/* 2267 */         this.map.reclaimValue(valueReference);
/* 2268 */         i++;
/*      */       }
/*      */     }
/*      */ 
/*      */     void clearReferenceQueues()
/*      */     {
/* 2278 */       if (this.map.usesKeyReferences()) {
/* 2279 */         clearKeyReferenceQueue();
/*      */       }
/* 2281 */       if (this.map.usesValueReferences())
/* 2282 */         clearValueReferenceQueue();
/*      */     }
/*      */ 
/*      */     void clearKeyReferenceQueue()
/*      */     {
/* 2287 */       while (this.keyReferenceQueue.poll() != null);
/*      */     }
/*      */ 
/*      */     void clearValueReferenceQueue() {
/* 2291 */       while (this.valueReferenceQueue.poll() != null);
/*      */     }
/*      */ 
/*      */     void recordRead(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 2304 */       if (this.map.expiresAfterAccess()) {
/* 2305 */         recordExpirationTime(entry, this.map.expireAfterAccessNanos);
/*      */       }
/* 2307 */       this.recencyQueue.add(entry);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordLockedRead(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 2319 */       this.evictionQueue.add(entry);
/* 2320 */       if (this.map.expiresAfterAccess()) {
/* 2321 */         recordExpirationTime(entry, this.map.expireAfterAccessNanos);
/* 2322 */         this.expirationQueue.add(entry);
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void recordWrite(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 2333 */       drainRecencyQueue();
/* 2334 */       this.evictionQueue.add(entry);
/* 2335 */       if (this.map.expires())
/*      */       {
/* 2338 */         long expiration = this.map.expiresAfterAccess() ? this.map.expireAfterAccessNanos : this.map.expireAfterWriteNanos;
/*      */ 
/* 2341 */         recordExpirationTime(entry, expiration);
/* 2342 */         this.expirationQueue.add(entry);
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void drainRecencyQueue()
/*      */     {
/*      */       MapMakerInternalMap.ReferenceEntry e;
/* 2355 */       while ((e = (MapMakerInternalMap.ReferenceEntry)this.recencyQueue.poll()) != null)
/*      */       {
/* 2360 */         if (this.evictionQueue.contains(e)) {
/* 2361 */           this.evictionQueue.add(e);
/*      */         }
/* 2363 */         if ((this.map.expiresAfterAccess()) && (this.expirationQueue.contains(e)))
/* 2364 */           this.expirationQueue.add(e);
/*      */       }
/*      */     }
/*      */ 
/*      */     void recordExpirationTime(MapMakerInternalMap.ReferenceEntry<K, V> entry, long expirationNanos)
/*      */     {
/* 2373 */       entry.setExpirationTime(this.map.ticker.read() + expirationNanos);
/*      */     }
/*      */ 
/*      */     void tryExpireEntries()
/*      */     {
/* 2380 */       if (tryLock())
/*      */         try {
/* 2382 */           expireEntries();
/*      */         } finally {
/* 2384 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void expireEntries()
/*      */     {
/* 2392 */       drainRecencyQueue();
/*      */ 
/* 2394 */       if (this.expirationQueue.isEmpty())
/*      */       {
/* 2397 */         return;
/*      */       }
/* 2399 */       long now = this.map.ticker.read();
/*      */       MapMakerInternalMap.ReferenceEntry e;
/* 2401 */       while (((e = (MapMakerInternalMap.ReferenceEntry)this.expirationQueue.peek()) != null) && (this.map.isExpired(e, now)))
/* 2402 */         if (!removeEntry(e, e.getHash(), MapMaker.RemovalCause.EXPIRED))
/* 2403 */           throw new AssertionError();
/*      */     }
/*      */ 
/*      */     void enqueueNotification(MapMakerInternalMap.ReferenceEntry<K, V> entry, MapMaker.RemovalCause cause)
/*      */     {
/* 2411 */       enqueueNotification(entry.getKey(), entry.getHash(), entry.getValueReference().get(), cause);
/*      */     }
/*      */ 
/*      */     void enqueueNotification(@Nullable K key, int hash, @Nullable V value, MapMaker.RemovalCause cause) {
/* 2415 */       if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
/* 2416 */         MapMaker.RemovalNotification notification = new MapMaker.RemovalNotification(key, value, cause);
/* 2417 */         this.map.removalNotificationQueue.offer(notification);
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     boolean evictEntries()
/*      */     {
/* 2429 */       if ((this.map.evictsBySize()) && (this.count >= this.maxSegmentSize)) {
/* 2430 */         drainRecencyQueue();
/*      */ 
/* 2432 */         MapMakerInternalMap.ReferenceEntry e = (MapMakerInternalMap.ReferenceEntry)this.evictionQueue.remove();
/* 2433 */         if (!removeEntry(e, e.getHash(), MapMaker.RemovalCause.SIZE)) {
/* 2434 */           throw new AssertionError();
/*      */         }
/* 2436 */         return true;
/*      */       }
/* 2438 */       return false;
/*      */     }
/*      */ 
/*      */     MapMakerInternalMap.ReferenceEntry<K, V> getFirst(int hash)
/*      */     {
/* 2446 */       AtomicReferenceArray table = this.table;
/* 2447 */       return (MapMakerInternalMap.ReferenceEntry)table.get(hash & table.length() - 1);
/*      */     }
/*      */ 
/*      */     MapMakerInternalMap.ReferenceEntry<K, V> getEntry(Object key, int hash)
/*      */     {
/* 2453 */       if (this.count != 0) {
/* 2454 */         for (MapMakerInternalMap.ReferenceEntry e = getFirst(hash); e != null; e = e.getNext()) {
/* 2455 */           if (e.getHash() == hash)
/*      */           {
/* 2459 */             Object entryKey = e.getKey();
/* 2460 */             if (entryKey == null) {
/* 2461 */               tryDrainReferenceQueues();
/*      */             }
/* 2465 */             else if (this.map.keyEquivalence.equivalent(key, entryKey)) {
/* 2466 */               return e;
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2471 */       return null;
/*      */     }
/*      */ 
/*      */     MapMakerInternalMap.ReferenceEntry<K, V> getLiveEntry(Object key, int hash) {
/* 2475 */       MapMakerInternalMap.ReferenceEntry e = getEntry(key, hash);
/* 2476 */       if (e == null)
/* 2477 */         return null;
/* 2478 */       if ((this.map.expires()) && (this.map.isExpired(e))) {
/* 2479 */         tryExpireEntries();
/* 2480 */         return null;
/*      */       }
/* 2482 */       return e;
/*      */     }
/*      */ 
/*      */     V get(Object key, int hash) {
/*      */       try {
/* 2487 */         MapMakerInternalMap.ReferenceEntry e = getLiveEntry(key, hash);
/* 2488 */         if (e == null) {
/* 2489 */           return null;
/*      */         }
/*      */ 
/* 2492 */         Object value = e.getValueReference().get();
/* 2493 */         if (value != null)
/* 2494 */           recordRead(e);
/*      */         else {
/* 2496 */           tryDrainReferenceQueues();
/*      */         }
/* 2498 */         return value;
/*      */       } finally {
/* 2500 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean containsKey(Object key, int hash)
/*      */     {
/*      */       try
/*      */       {
/*      */         MapMakerInternalMap.ReferenceEntry e;
/* 2506 */         if (this.count != 0) {
/* 2507 */           e = getLiveEntry(key, hash);
/*      */           boolean bool;
/* 2508 */           if (e == null) {
/* 2509 */             return false;
/*      */           }
/* 2511 */           return e.getValueReference().get() != null;
/*      */         }
/*      */ 
/* 2514 */         return 0;
/*      */       } finally {
/* 2516 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @VisibleForTesting
/*      */     boolean containsValue(Object value)
/*      */     {
/*      */       try
/*      */       {
/*      */         AtomicReferenceArray table;
/* 2527 */         if (this.count != 0) {
/* 2528 */           table = this.table;
/* 2529 */           int length = table.length();
/* 2530 */           for (int i = 0; i < length; i++) {
/* 2531 */             for (MapMakerInternalMap.ReferenceEntry e = (MapMakerInternalMap.ReferenceEntry)table.get(i); e != null; e = e.getNext()) {
/* 2532 */               Object entryValue = getLiveValue(e);
/* 2533 */               if (entryValue != null)
/*      */               {
/* 2536 */                 if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 2537 */                   return true;
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/* 2543 */         return 0;
/*      */       } finally {
/* 2545 */         postReadCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     V put(K key, int hash, V value, boolean onlyIfAbsent) {
/* 2550 */       lock();
/*      */       try {
/* 2552 */         preWriteCleanup();
/*      */ 
/* 2554 */         int newCount = this.count + 1;
/* 2555 */         if (newCount > this.threshold) {
/* 2556 */           expand();
/* 2557 */           newCount = this.count + 1;
/*      */         }
/*      */ 
/* 2560 */         AtomicReferenceArray table = this.table;
/* 2561 */         int index = hash & table.length() - 1;
/* 2562 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */         Object entryKey;
/* 2565 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2566 */           entryKey = e.getKey();
/* 2567 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2571 */             MapMakerInternalMap.ValueReference valueReference = e.getValueReference();
/* 2572 */             Object entryValue = valueReference.get();
/*      */             Object localObject1;
/* 2574 */             if (entryValue == null) {
/* 2575 */               this.modCount += 1;
/* 2576 */               setValue(e, value);
/* 2577 */               if (!valueReference.isComputingReference()) {
/* 2578 */                 enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.COLLECTED);
/* 2579 */                 newCount = this.count;
/* 2580 */               } else if (evictEntries()) {
/* 2581 */                 newCount = this.count + 1;
/*      */               }
/* 2583 */               this.count = newCount;
/* 2584 */               return null;
/* 2585 */             }if (onlyIfAbsent)
/*      */             {
/* 2589 */               recordLockedRead(e);
/* 2590 */               return entryValue;
/*      */             }
/*      */ 
/* 2593 */             this.modCount += 1;
/* 2594 */             enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.REPLACED);
/* 2595 */             setValue(e, value);
/* 2596 */             return entryValue;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2602 */         this.modCount += 1;
/* 2603 */         MapMakerInternalMap.ReferenceEntry newEntry = newEntry(key, hash, first);
/* 2604 */         setValue(newEntry, value);
/* 2605 */         table.set(index, newEntry);
/* 2606 */         if (evictEntries()) {
/* 2607 */           newCount = this.count + 1;
/*      */         }
/* 2609 */         this.count = newCount;
/* 2610 */         return null;
/*      */       } finally {
/* 2612 */         unlock();
/* 2613 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void expand()
/*      */     {
/* 2622 */       AtomicReferenceArray oldTable = this.table;
/* 2623 */       int oldCapacity = oldTable.length();
/* 2624 */       if (oldCapacity >= 1073741824) {
/* 2625 */         return;
/*      */       }
/*      */ 
/* 2638 */       int newCount = this.count;
/* 2639 */       AtomicReferenceArray newTable = newEntryArray(oldCapacity << 1);
/* 2640 */       this.threshold = (newTable.length() * 3 / 4);
/* 2641 */       int newMask = newTable.length() - 1;
/* 2642 */       for (int oldIndex = 0; oldIndex < oldCapacity; oldIndex++)
/*      */       {
/* 2645 */         MapMakerInternalMap.ReferenceEntry head = (MapMakerInternalMap.ReferenceEntry)oldTable.get(oldIndex);
/*      */ 
/* 2647 */         if (head != null) {
/* 2648 */           MapMakerInternalMap.ReferenceEntry next = head.getNext();
/* 2649 */           int headIndex = head.getHash() & newMask;
/*      */ 
/* 2652 */           if (next == null) {
/* 2653 */             newTable.set(headIndex, head);
/*      */           }
/*      */           else
/*      */           {
/* 2658 */             MapMakerInternalMap.ReferenceEntry tail = head;
/* 2659 */             int tailIndex = headIndex;
/* 2660 */             for (MapMakerInternalMap.ReferenceEntry e = next; e != null; e = e.getNext()) {
/* 2661 */               int newIndex = e.getHash() & newMask;
/* 2662 */               if (newIndex != tailIndex)
/*      */               {
/* 2664 */                 tailIndex = newIndex;
/* 2665 */                 tail = e;
/*      */               }
/*      */             }
/* 2668 */             newTable.set(tailIndex, tail);
/*      */ 
/* 2671 */             for (MapMakerInternalMap.ReferenceEntry e = head; e != tail; e = e.getNext()) {
/* 2672 */               int newIndex = e.getHash() & newMask;
/* 2673 */               MapMakerInternalMap.ReferenceEntry newNext = (MapMakerInternalMap.ReferenceEntry)newTable.get(newIndex);
/* 2674 */               MapMakerInternalMap.ReferenceEntry newFirst = copyEntry(e, newNext);
/* 2675 */               if (newFirst != null) {
/* 2676 */                 newTable.set(newIndex, newFirst);
/*      */               } else {
/* 2678 */                 removeCollectedEntry(e);
/* 2679 */                 newCount--;
/*      */               }
/*      */             }
/*      */           }
/*      */         }
/*      */       }
/* 2685 */       this.table = newTable;
/* 2686 */       this.count = newCount;
/*      */     }
/*      */ 
/*      */     boolean replace(K key, int hash, V oldValue, V newValue) {
/* 2690 */       lock();
/*      */       try {
/* 2692 */         preWriteCleanup();
/*      */ 
/* 2694 */         AtomicReferenceArray table = this.table;
/* 2695 */         int index = hash & table.length() - 1;
/* 2696 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 2698 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2699 */           Object entryKey = e.getKey();
/* 2700 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2704 */             MapMakerInternalMap.ValueReference valueReference = e.getValueReference();
/* 2705 */             Object entryValue = valueReference.get();
/*      */             int newCount;
/* 2706 */             if (entryValue == null) {
/* 2707 */               if (isCollected(valueReference)) {
/* 2708 */                 newCount = this.count - 1;
/* 2709 */                 this.modCount += 1;
/* 2710 */                 enqueueNotification(entryKey, hash, entryValue, MapMaker.RemovalCause.COLLECTED);
/* 2711 */                 MapMakerInternalMap.ReferenceEntry newFirst = removeFromChain(first, e);
/* 2712 */                 newCount = this.count - 1;
/* 2713 */                 table.set(index, newFirst);
/* 2714 */                 this.count = newCount;
/*      */               }
/* 2716 */               return 0;
/*      */             }
/*      */ 
/* 2719 */             if (this.map.valueEquivalence.equivalent(oldValue, entryValue)) {
/* 2720 */               this.modCount += 1;
/* 2721 */               enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.REPLACED);
/* 2722 */               setValue(e, newValue);
/* 2723 */               return 1;
/*      */             }
/*      */ 
/* 2727 */             recordLockedRead(e);
/* 2728 */             return 0;
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/* 2733 */         return 0;
/*      */       } finally {
/* 2735 */         unlock();
/* 2736 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     V replace(K key, int hash, V newValue) {
/* 2741 */       lock();
/*      */       try {
/* 2743 */         preWriteCleanup();
/*      */ 
/* 2745 */         AtomicReferenceArray table = this.table;
/* 2746 */         int index = hash & table.length() - 1;
/* 2747 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 2749 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2750 */           Object entryKey = e.getKey();
/* 2751 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2755 */             MapMakerInternalMap.ValueReference valueReference = e.getValueReference();
/* 2756 */             Object entryValue = valueReference.get();
/*      */             int newCount;
/* 2757 */             if (entryValue == null) {
/* 2758 */               if (isCollected(valueReference)) {
/* 2759 */                 newCount = this.count - 1;
/* 2760 */                 this.modCount += 1;
/* 2761 */                 enqueueNotification(entryKey, hash, entryValue, MapMaker.RemovalCause.COLLECTED);
/* 2762 */                 MapMakerInternalMap.ReferenceEntry newFirst = removeFromChain(first, e);
/* 2763 */                 newCount = this.count - 1;
/* 2764 */                 table.set(index, newFirst);
/* 2765 */                 this.count = newCount;
/*      */               }
/* 2767 */               return null;
/*      */             }
/*      */ 
/* 2770 */             this.modCount += 1;
/* 2771 */             enqueueNotification(key, hash, entryValue, MapMaker.RemovalCause.REPLACED);
/* 2772 */             setValue(e, newValue);
/* 2773 */             return entryValue;
/*      */           }
/*      */         }
/*      */ 
/* 2777 */         return null;
/*      */       } finally {
/* 2779 */         unlock();
/* 2780 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     V remove(Object key, int hash) {
/* 2785 */       lock();
/*      */       try {
/* 2787 */         preWriteCleanup();
/*      */ 
/* 2789 */         int newCount = this.count - 1;
/* 2790 */         AtomicReferenceArray table = this.table;
/* 2791 */         int index = hash & table.length() - 1;
/* 2792 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 2794 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2795 */           Object entryKey = e.getKey();
/* 2796 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2798 */             MapMakerInternalMap.ValueReference valueReference = e.getValueReference();
/* 2799 */             Object entryValue = valueReference.get();
/*      */             MapMaker.RemovalCause cause;
/* 2802 */             if (entryValue != null) {
/* 2803 */               cause = MapMaker.RemovalCause.EXPLICIT;
/*      */             }
/*      */             else
/*      */             {
/*      */               MapMaker.RemovalCause cause;
/* 2804 */               if (isCollected(valueReference))
/* 2805 */                 cause = MapMaker.RemovalCause.COLLECTED;
/*      */               else
/* 2807 */                 return null;
/*      */             }
/*      */             MapMaker.RemovalCause cause;
/* 2810 */             this.modCount += 1;
/* 2811 */             enqueueNotification(entryKey, hash, entryValue, cause);
/* 2812 */             MapMakerInternalMap.ReferenceEntry newFirst = removeFromChain(first, e);
/* 2813 */             newCount = this.count - 1;
/* 2814 */             table.set(index, newFirst);
/* 2815 */             this.count = newCount;
/* 2816 */             return entryValue;
/*      */           }
/*      */         }
/*      */ 
/* 2820 */         return null;
/*      */       } finally {
/* 2822 */         unlock();
/* 2823 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean remove(Object key, int hash, Object value) {
/* 2828 */       lock();
/*      */       try {
/* 2830 */         preWriteCleanup();
/*      */ 
/* 2832 */         int newCount = this.count - 1;
/* 2833 */         AtomicReferenceArray table = this.table;
/* 2834 */         int index = hash & table.length() - 1;
/* 2835 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 2837 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2838 */           Object entryKey = e.getKey();
/* 2839 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2841 */             MapMakerInternalMap.ValueReference valueReference = e.getValueReference();
/* 2842 */             Object entryValue = valueReference.get();
/*      */             MapMaker.RemovalCause cause;
/* 2845 */             if (this.map.valueEquivalence.equivalent(value, entryValue)) {
/* 2846 */               cause = MapMaker.RemovalCause.EXPLICIT;
/*      */             }
/*      */             else
/*      */             {
/*      */               MapMaker.RemovalCause cause;
/* 2847 */               if (isCollected(valueReference))
/* 2848 */                 cause = MapMaker.RemovalCause.COLLECTED;
/*      */               else
/* 2850 */                 return false;
/*      */             }
/*      */             MapMaker.RemovalCause cause;
/* 2853 */             this.modCount += 1;
/* 2854 */             enqueueNotification(entryKey, hash, entryValue, cause);
/* 2855 */             MapMakerInternalMap.ReferenceEntry newFirst = removeFromChain(first, e);
/* 2856 */             newCount = this.count - 1;
/* 2857 */             table.set(index, newFirst);
/* 2858 */             this.count = newCount;
/* 2859 */             return cause == MapMaker.RemovalCause.EXPLICIT;
/*      */           }
/*      */         }
/*      */ 
/* 2863 */         return 0;
/*      */       } finally {
/* 2865 */         unlock();
/* 2866 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     void clear() {
/* 2871 */       if (this.count != 0) {
/* 2872 */         lock();
/*      */         try {
/* 2874 */           AtomicReferenceArray table = this.table;
/* 2875 */           if (this.map.removalNotificationQueue != MapMakerInternalMap.DISCARDING_QUEUE) {
/* 2876 */             for (int i = 0; i < table.length(); i++) {
/* 2877 */               for (MapMakerInternalMap.ReferenceEntry e = (MapMakerInternalMap.ReferenceEntry)table.get(i); e != null; e = e.getNext())
/*      */               {
/* 2879 */                 if (!e.getValueReference().isComputingReference()) {
/* 2880 */                   enqueueNotification(e, MapMaker.RemovalCause.EXPLICIT);
/*      */                 }
/*      */               }
/*      */             }
/*      */           }
/* 2885 */           for (int i = 0; i < table.length(); i++) {
/* 2886 */             table.set(i, null);
/*      */           }
/* 2888 */           clearReferenceQueues();
/* 2889 */           this.evictionQueue.clear();
/* 2890 */           this.expirationQueue.clear();
/* 2891 */           this.readCount.set(0);
/*      */ 
/* 2893 */           this.modCount += 1;
/* 2894 */           this.count = 0;
/*      */         } finally {
/* 2896 */           unlock();
/* 2897 */           postWriteCleanup();
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     MapMakerInternalMap.ReferenceEntry<K, V> removeFromChain(MapMakerInternalMap.ReferenceEntry<K, V> first, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 2916 */       this.evictionQueue.remove(entry);
/* 2917 */       this.expirationQueue.remove(entry);
/*      */ 
/* 2919 */       int newCount = this.count;
/* 2920 */       MapMakerInternalMap.ReferenceEntry newFirst = entry.getNext();
/* 2921 */       for (MapMakerInternalMap.ReferenceEntry e = first; e != entry; e = e.getNext()) {
/* 2922 */         MapMakerInternalMap.ReferenceEntry next = copyEntry(e, newFirst);
/* 2923 */         if (next != null) {
/* 2924 */           newFirst = next;
/*      */         } else {
/* 2926 */           removeCollectedEntry(e);
/* 2927 */           newCount--;
/*      */         }
/*      */       }
/* 2930 */       this.count = newCount;
/* 2931 */       return newFirst;
/*      */     }
/*      */ 
/*      */     void removeCollectedEntry(MapMakerInternalMap.ReferenceEntry<K, V> entry) {
/* 2935 */       enqueueNotification(entry, MapMaker.RemovalCause.COLLECTED);
/* 2936 */       this.evictionQueue.remove(entry);
/* 2937 */       this.expirationQueue.remove(entry);
/*      */     }
/*      */ 
/*      */     boolean reclaimKey(MapMakerInternalMap.ReferenceEntry<K, V> entry, int hash)
/*      */     {
/* 2944 */       lock();
/*      */       try {
/* 2946 */         int newCount = this.count - 1;
/* 2947 */         AtomicReferenceArray table = this.table;
/* 2948 */         int index = hash & table.length() - 1;
/* 2949 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 2951 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2952 */           if (e == entry) {
/* 2953 */             this.modCount += 1;
/* 2954 */             enqueueNotification(e.getKey(), hash, e.getValueReference().get(), MapMaker.RemovalCause.COLLECTED);
/*      */ 
/* 2956 */             MapMakerInternalMap.ReferenceEntry newFirst = removeFromChain(first, e);
/* 2957 */             newCount = this.count - 1;
/* 2958 */             table.set(index, newFirst);
/* 2959 */             this.count = newCount;
/* 2960 */             return true;
/*      */           }
/*      */         }
/*      */ 
/* 2964 */         return 0;
/*      */       } finally {
/* 2966 */         unlock();
/* 2967 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean reclaimValue(K key, int hash, MapMakerInternalMap.ValueReference<K, V> valueReference)
/*      */     {
/* 2975 */       lock();
/*      */       try {
/* 2977 */         int newCount = this.count - 1;
/* 2978 */         AtomicReferenceArray table = this.table;
/* 2979 */         int index = hash & table.length() - 1;
/* 2980 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 2982 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 2983 */           Object entryKey = e.getKey();
/* 2984 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 2986 */             MapMakerInternalMap.ValueReference v = e.getValueReference();
/*      */             MapMakerInternalMap.ReferenceEntry newFirst;
/* 2987 */             if (v == valueReference) {
/* 2988 */               this.modCount += 1;
/* 2989 */               enqueueNotification(key, hash, valueReference.get(), MapMaker.RemovalCause.COLLECTED);
/* 2990 */               newFirst = removeFromChain(first, e);
/* 2991 */               newCount = this.count - 1;
/* 2992 */               table.set(index, newFirst);
/* 2993 */               this.count = newCount;
/* 2994 */               return true;
/*      */             }
/* 2996 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3000 */         return 0;
/*      */       } finally {
/* 3002 */         unlock();
/* 3003 */         if (!isHeldByCurrentThread())
/* 3004 */           postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     boolean clearValue(K key, int hash, MapMakerInternalMap.ValueReference<K, V> valueReference)
/*      */     {
/* 3013 */       lock();
/*      */       try {
/* 3015 */         AtomicReferenceArray table = this.table;
/* 3016 */         int index = hash & table.length() - 1;
/* 3017 */         MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 3019 */         for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3020 */           Object entryKey = e.getKey();
/* 3021 */           if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*      */           {
/* 3023 */             MapMakerInternalMap.ValueReference v = e.getValueReference();
/*      */             MapMakerInternalMap.ReferenceEntry newFirst;
/* 3024 */             if (v == valueReference) {
/* 3025 */               newFirst = removeFromChain(first, e);
/* 3026 */               table.set(index, newFirst);
/* 3027 */               return true;
/*      */             }
/* 3029 */             return 0;
/*      */           }
/*      */         }
/*      */ 
/* 3033 */         return 0;
/*      */       } finally {
/* 3035 */         unlock();
/* 3036 */         postWriteCleanup();
/*      */       }
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     boolean removeEntry(MapMakerInternalMap.ReferenceEntry<K, V> entry, int hash, MapMaker.RemovalCause cause) {
/* 3042 */       int newCount = this.count - 1;
/* 3043 */       AtomicReferenceArray table = this.table;
/* 3044 */       int index = hash & table.length() - 1;
/* 3045 */       MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*      */ 
/* 3047 */       for (MapMakerInternalMap.ReferenceEntry e = first; e != null; e = e.getNext()) {
/* 3048 */         if (e == entry) {
/* 3049 */           this.modCount += 1;
/* 3050 */           enqueueNotification(e.getKey(), hash, e.getValueReference().get(), cause);
/* 3051 */           MapMakerInternalMap.ReferenceEntry newFirst = removeFromChain(first, e);
/* 3052 */           newCount = this.count - 1;
/* 3053 */           table.set(index, newFirst);
/* 3054 */           this.count = newCount;
/* 3055 */           return true;
/*      */         }
/*      */       }
/*      */ 
/* 3059 */       return false;
/*      */     }
/*      */ 
/*      */     boolean isCollected(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 3067 */       if (entry.getKey() == null) {
/* 3068 */         return true;
/*      */       }
/* 3070 */       return isCollected(entry.getValueReference());
/*      */     }
/*      */ 
/*      */     boolean isCollected(MapMakerInternalMap.ValueReference<K, V> valueReference)
/*      */     {
/* 3078 */       if (valueReference.isComputingReference()) {
/* 3079 */         return false;
/*      */       }
/* 3081 */       return valueReference.get() == null;
/*      */     }
/*      */ 
/*      */     V getLiveValue(MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 3089 */       if (entry.getKey() == null) {
/* 3090 */         tryDrainReferenceQueues();
/* 3091 */         return null;
/*      */       }
/* 3093 */       Object value = entry.getValueReference().get();
/* 3094 */       if (value == null) {
/* 3095 */         tryDrainReferenceQueues();
/* 3096 */         return null;
/*      */       }
/*      */ 
/* 3099 */       if ((this.map.expires()) && (this.map.isExpired(entry))) {
/* 3100 */         tryExpireEntries();
/* 3101 */         return null;
/*      */       }
/* 3103 */       return value;
/*      */     }
/*      */ 
/*      */     void postReadCleanup()
/*      */     {
/* 3112 */       if ((this.readCount.incrementAndGet() & 0x3F) == 0)
/* 3113 */         runCleanup();
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     void preWriteCleanup()
/*      */     {
/* 3125 */       runLockedCleanup();
/*      */     }
/*      */ 
/*      */     void postWriteCleanup()
/*      */     {
/* 3132 */       runUnlockedCleanup();
/*      */     }
/*      */ 
/*      */     void runCleanup() {
/* 3136 */       runLockedCleanup();
/* 3137 */       runUnlockedCleanup();
/*      */     }
/*      */ 
/*      */     void runLockedCleanup() {
/* 3141 */       if (tryLock())
/*      */         try {
/* 3143 */           drainReferenceQueues();
/* 3144 */           expireEntries();
/* 3145 */           this.readCount.set(0);
/*      */         } finally {
/* 3147 */           unlock();
/*      */         }
/*      */     }
/*      */ 
/*      */     void runUnlockedCleanup()
/*      */     {
/* 3154 */       if (!isHeldByCurrentThread())
/* 3155 */         this.map.processPendingNotifications();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongValueReference<K, V>
/*      */     implements MapMakerInternalMap.ValueReference<K, V>
/*      */   {
/*      */     final V referent;
/*      */ 
/*      */     StrongValueReference(V referent)
/*      */     {
/* 1820 */       this.referent = referent;
/*      */     }
/*      */ 
/*      */     public V get()
/*      */     {
/* 1825 */       return this.referent;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1830 */       return null;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 1836 */       return this;
/*      */     }
/*      */ 
/*      */     public boolean isComputingReference()
/*      */     {
/* 1841 */       return false;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1846 */       return get();
/*      */     }
/*      */ 
/*      */     public void clear(MapMakerInternalMap.ValueReference<K, V> newValue)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class SoftValueReference<K, V> extends SoftReference<V>
/*      */     implements MapMakerInternalMap.ValueReference<K, V>
/*      */   {
/*      */     final MapMakerInternalMap.ReferenceEntry<K, V> entry;
/*      */ 
/*      */     SoftValueReference(ReferenceQueue<V> queue, V referent, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 1782 */       super(queue);
/* 1783 */       this.entry = entry;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1788 */       return this.entry;
/*      */     }
/*      */ 
/*      */     public void clear(MapMakerInternalMap.ValueReference<K, V> newValue)
/*      */     {
/* 1793 */       clear();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 1799 */       return new SoftValueReference(queue, value, entry);
/*      */     }
/*      */ 
/*      */     public boolean isComputingReference()
/*      */     {
/* 1804 */       return false;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1809 */       return get();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakValueReference<K, V> extends WeakReference<V>
/*      */     implements MapMakerInternalMap.ValueReference<K, V>
/*      */   {
/*      */     final MapMakerInternalMap.ReferenceEntry<K, V> entry;
/*      */ 
/*      */     WeakValueReference(ReferenceQueue<V> queue, V referent, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 1743 */       super(queue);
/* 1744 */       this.entry = entry;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getEntry()
/*      */     {
/* 1749 */       return this.entry;
/*      */     }
/*      */ 
/*      */     public void clear(MapMakerInternalMap.ValueReference<K, V> newValue)
/*      */     {
/* 1754 */       clear();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*      */     {
/* 1760 */       return new WeakValueReference(queue, value, entry);
/*      */     }
/*      */ 
/*      */     public boolean isComputingReference()
/*      */     {
/* 1765 */       return false;
/*      */     }
/*      */ 
/*      */     public V waitForValue()
/*      */     {
/* 1770 */       return get();
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakExpirableEvictableEntry<K, V> extends MapMakerInternalMap.WeakEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/* 1668 */     volatile long time = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1680 */     MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1693 */     MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1708 */     MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1721 */     MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     WeakExpirableEvictableEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1663 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1672 */       return this.time;
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1677 */       this.time = time;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1685 */       return this.nextExpirable;
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1690 */       this.nextExpirable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1698 */       return this.previousExpirable;
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1703 */       this.previousExpirable = previous;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1713 */       return this.nextEvictable;
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1718 */       this.nextEvictable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1726 */       return this.previousEvictable;
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1731 */       this.previousEvictable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakEvictableEntry<K, V> extends MapMakerInternalMap.WeakEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1632 */     MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1645 */     MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     WeakEvictableEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1627 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1637 */       return this.nextEvictable;
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1642 */       this.nextEvictable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1650 */       return this.previousEvictable;
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1655 */       this.previousEvictable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class WeakExpirableEntry<K, V> extends MapMakerInternalMap.WeakEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/* 1584 */     volatile long time = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1596 */     MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1609 */     MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     WeakExpirableEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1579 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1588 */       return this.time;
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1593 */       this.time = time;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1601 */       return this.nextExpirable;
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1606 */       this.nextExpirable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1614 */       return this.previousExpirable;
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1619 */       this.previousExpirable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class WeakEntry<K, V> extends WeakReference<K>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/*      */     final int hash;
/*      */     final MapMakerInternalMap.ReferenceEntry<K, V> next;
/* 1550 */     volatile MapMakerInternalMap.ValueReference<K, V> valueReference = MapMakerInternalMap.unset();
/*      */ 
/*      */     WeakEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1482 */       super(queue);
/* 1483 */       this.hash = hash;
/* 1484 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 1489 */       return get();
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1496 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1501 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1506 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1511 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1516 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1521 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1528 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1533 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1538 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1543 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<K, V> getValueReference()
/*      */     {
/* 1554 */       return this.valueReference;
/*      */     }
/*      */ 
/*      */     public void setValueReference(MapMakerInternalMap.ValueReference<K, V> valueReference)
/*      */     {
/* 1559 */       MapMakerInternalMap.ValueReference previous = this.valueReference;
/* 1560 */       this.valueReference = valueReference;
/* 1561 */       previous.clear(valueReference);
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/* 1566 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNext()
/*      */     {
/* 1571 */       return this.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class SoftExpirableEvictableEntry<K, V> extends MapMakerInternalMap.SoftEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/* 1410 */     volatile long time = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1422 */     MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1435 */     MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1450 */     MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1463 */     MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     SoftExpirableEvictableEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1405 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1414 */       return this.time;
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1419 */       this.time = time;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1427 */       return this.nextExpirable;
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1432 */       this.nextExpirable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1440 */       return this.previousExpirable;
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1445 */       this.previousExpirable = previous;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1455 */       return this.nextEvictable;
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1460 */       this.nextEvictable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1468 */       return this.previousEvictable;
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1473 */       this.previousEvictable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class SoftEvictableEntry<K, V> extends MapMakerInternalMap.SoftEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1374 */     MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1387 */     MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     SoftEvictableEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1369 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1379 */       return this.nextEvictable;
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1384 */       this.nextEvictable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1392 */       return this.previousEvictable;
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1397 */       this.previousEvictable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class SoftExpirableEntry<K, V> extends MapMakerInternalMap.SoftEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/* 1326 */     volatile long time = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1338 */     MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1351 */     MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     SoftExpirableEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1321 */       super(key, hash, next);
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1330 */       return this.time;
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1335 */       this.time = time;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1343 */       return this.nextExpirable;
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1348 */       this.nextExpirable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1356 */       return this.previousExpirable;
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1361 */       this.previousExpirable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class SoftEntry<K, V> extends SoftReference<K>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/*      */     final int hash;
/*      */     final MapMakerInternalMap.ReferenceEntry<K, V> next;
/* 1292 */     volatile MapMakerInternalMap.ValueReference<K, V> valueReference = MapMakerInternalMap.unset();
/*      */ 
/*      */     SoftEntry(ReferenceQueue<K> queue, K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1225 */       super(queue);
/* 1226 */       this.hash = hash;
/* 1227 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/* 1232 */       return get();
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1238 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1243 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1248 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1253 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1258 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1263 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1270 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1275 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1280 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1285 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<K, V> getValueReference()
/*      */     {
/* 1296 */       return this.valueReference;
/*      */     }
/*      */ 
/*      */     public void setValueReference(MapMakerInternalMap.ValueReference<K, V> valueReference)
/*      */     {
/* 1301 */       MapMakerInternalMap.ValueReference previous = this.valueReference;
/* 1302 */       this.valueReference = valueReference;
/* 1303 */       previous.clear(valueReference);
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/* 1308 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNext()
/*      */     {
/* 1313 */       return this.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongExpirableEvictableEntry<K, V> extends MapMakerInternalMap.StrongEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/* 1153 */     volatile long time = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1165 */     MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1178 */     MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1193 */     MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1206 */     MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     StrongExpirableEvictableEntry(K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1148 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1157 */       return this.time;
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1162 */       this.time = time;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1170 */       return this.nextExpirable;
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1175 */       this.nextExpirable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1183 */       return this.previousExpirable;
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1188 */       this.previousExpirable = previous;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1198 */       return this.nextEvictable;
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1203 */       this.nextEvictable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1211 */       return this.previousEvictable;
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1216 */       this.previousEvictable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongEvictableEntry<K, V> extends MapMakerInternalMap.StrongEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1118 */     MapMakerInternalMap.ReferenceEntry<K, V> nextEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1131 */     MapMakerInternalMap.ReferenceEntry<K, V> previousEvictable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     StrongEvictableEntry(K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1113 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1123 */       return this.nextEvictable;
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1128 */       this.nextEvictable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1136 */       return this.previousEvictable;
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1141 */       this.previousEvictable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static final class StrongExpirableEntry<K, V> extends MapMakerInternalMap.StrongEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/* 1071 */     volatile long time = 9223372036854775807L;
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1083 */     MapMakerInternalMap.ReferenceEntry<K, V> nextExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     @GuardedBy("Segment.this")
/* 1096 */     MapMakerInternalMap.ReferenceEntry<K, V> previousExpirable = MapMakerInternalMap.nullEntry();
/*      */ 
/*      */     StrongExpirableEntry(K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1066 */       super(hash, next);
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/* 1075 */       return this.time;
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/* 1080 */       this.time = time;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/* 1088 */       return this.nextExpirable;
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1093 */       this.nextExpirable = next;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1101 */       return this.previousExpirable;
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1106 */       this.previousExpirable = previous;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class StrongEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/*      */     final K key;
/*      */     final int hash;
/*      */     final MapMakerInternalMap.ReferenceEntry<K, V> next;
/* 1038 */     volatile MapMakerInternalMap.ValueReference<K, V> valueReference = MapMakerInternalMap.unset();
/*      */ 
/*      */     StrongEntry(K key, int hash, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/*  970 */       this.key = key;
/*  971 */       this.hash = hash;
/*  972 */       this.next = next;
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/*  977 */       return this.key;
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/*  984 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/*  989 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/*  994 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/*  999 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/* 1004 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1009 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/* 1016 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/* 1021 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/* 1026 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/* 1031 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<K, V> getValueReference()
/*      */     {
/* 1042 */       return this.valueReference;
/*      */     }
/*      */ 
/*      */     public void setValueReference(MapMakerInternalMap.ValueReference<K, V> valueReference)
/*      */     {
/* 1047 */       MapMakerInternalMap.ValueReference previous = this.valueReference;
/* 1048 */       this.valueReference = valueReference;
/* 1049 */       previous.clear(valueReference);
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/* 1054 */       return this.hash;
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNext()
/*      */     {
/* 1059 */       return this.next;
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class AbstractReferenceEntry<K, V>
/*      */     implements MapMakerInternalMap.ReferenceEntry<K, V>
/*      */   {
/*      */     public MapMakerInternalMap.ValueReference<K, V> getValueReference()
/*      */     {
/*  841 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setValueReference(MapMakerInternalMap.ValueReference<K, V> valueReference)
/*      */     {
/*  846 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNext()
/*      */     {
/*  851 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/*  856 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public K getKey()
/*      */     {
/*  861 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/*  866 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/*  871 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextExpirable()
/*      */     {
/*  876 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/*  881 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousExpirable()
/*      */     {
/*  886 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/*  891 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getNextEvictable()
/*      */     {
/*  896 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<K, V> next)
/*      */     {
/*  901 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<K, V> getPreviousEvictable()
/*      */     {
/*  906 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<K, V> previous)
/*      */     {
/*  911 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static enum NullEntry
/*      */     implements MapMakerInternalMap.ReferenceEntry<Object, Object>
/*      */   {
/*  772 */     INSTANCE;
/*      */ 
/*      */     public MapMakerInternalMap.ValueReference<Object, Object> getValueReference()
/*      */     {
/*  776 */       return null;
/*      */     }
/*      */ 
/*      */     public void setValueReference(MapMakerInternalMap.ValueReference<Object, Object> valueReference)
/*      */     {
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<Object, Object> getNext() {
/*  784 */       return null;
/*      */     }
/*      */ 
/*      */     public int getHash()
/*      */     {
/*  789 */       return 0;
/*      */     }
/*      */ 
/*      */     public Object getKey()
/*      */     {
/*  794 */       return null;
/*      */     }
/*      */ 
/*      */     public long getExpirationTime()
/*      */     {
/*  799 */       return 0L;
/*      */     }
/*      */ 
/*      */     public void setExpirationTime(long time)
/*      */     {
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<Object, Object> getNextExpirable() {
/*  807 */       return this;
/*      */     }
/*      */ 
/*      */     public void setNextExpirable(MapMakerInternalMap.ReferenceEntry<Object, Object> next)
/*      */     {
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<Object, Object> getPreviousExpirable() {
/*  815 */       return this;
/*      */     }
/*      */ 
/*      */     public void setPreviousExpirable(MapMakerInternalMap.ReferenceEntry<Object, Object> previous)
/*      */     {
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<Object, Object> getNextEvictable() {
/*  823 */       return this;
/*      */     }
/*      */ 
/*      */     public void setNextEvictable(MapMakerInternalMap.ReferenceEntry<Object, Object> next)
/*      */     {
/*      */     }
/*      */ 
/*      */     public MapMakerInternalMap.ReferenceEntry<Object, Object> getPreviousEvictable() {
/*  831 */       return this;
/*      */     }
/*      */ 
/*      */     public void setPreviousEvictable(MapMakerInternalMap.ReferenceEntry<Object, Object> previous)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract interface ReferenceEntry<K, V>
/*      */   {
/*      */     public abstract MapMakerInternalMap.ValueReference<K, V> getValueReference();
/*      */ 
/*      */     public abstract void setValueReference(MapMakerInternalMap.ValueReference<K, V> paramValueReference);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNext();
/*      */ 
/*      */     public abstract int getHash();
/*      */ 
/*      */     public abstract K getKey();
/*      */ 
/*      */     public abstract long getExpirationTime();
/*      */ 
/*      */     public abstract void setExpirationTime(long paramLong);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextExpirable();
/*      */ 
/*      */     public abstract void setNextExpirable(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousExpirable();
/*      */ 
/*      */     public abstract void setPreviousExpirable(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getNextEvictable();
/*      */ 
/*      */     public abstract void setNextEvictable(ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract ReferenceEntry<K, V> getPreviousEvictable();
/*      */ 
/*      */     public abstract void setPreviousEvictable(ReferenceEntry<K, V> paramReferenceEntry);
/*      */   }
/*      */ 
/*      */   static abstract interface ValueReference<K, V>
/*      */   {
/*      */     public abstract V get();
/*      */ 
/*      */     public abstract V waitForValue()
/*      */       throws ExecutionException;
/*      */ 
/*      */     public abstract MapMakerInternalMap.ReferenceEntry<K, V> getEntry();
/*      */ 
/*      */     public abstract ValueReference<K, V> copyFor(ReferenceQueue<V> paramReferenceQueue, V paramV, MapMakerInternalMap.ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     public abstract void clear(@Nullable ValueReference<K, V> paramValueReference);
/*      */ 
/*      */     public abstract boolean isComputingReference();
/*      */   }
/*      */ 
/*      */   static abstract enum EntryFactory
/*      */   {
/*  353 */     STRONG, 
/*      */ 
/*  360 */     STRONG_EXPIRABLE, 
/*      */ 
/*  375 */     STRONG_EVICTABLE, 
/*      */ 
/*  390 */     STRONG_EXPIRABLE_EVICTABLE, 
/*      */ 
/*  407 */     SOFT, 
/*      */ 
/*  414 */     SOFT_EXPIRABLE, 
/*      */ 
/*  429 */     SOFT_EVICTABLE, 
/*      */ 
/*  444 */     SOFT_EXPIRABLE_EVICTABLE, 
/*      */ 
/*  461 */     WEAK, 
/*      */ 
/*  468 */     WEAK_EXPIRABLE, 
/*      */ 
/*  483 */     WEAK_EVICTABLE, 
/*      */ 
/*  498 */     WEAK_EXPIRABLE_EVICTABLE;
/*      */ 
/*      */     static final int EXPIRABLE_MASK = 1;
/*      */     static final int EVICTABLE_MASK = 2;
/*  525 */     static final EntryFactory[][] factories = { { STRONG, STRONG_EXPIRABLE, STRONG_EVICTABLE, STRONG_EXPIRABLE_EVICTABLE }, { SOFT, SOFT_EXPIRABLE, SOFT_EVICTABLE, SOFT_EXPIRABLE_EVICTABLE }, { WEAK, WEAK_EXPIRABLE, WEAK_EVICTABLE, WEAK_EXPIRABLE_EVICTABLE } };
/*      */ 
/*      */     static EntryFactory getFactory(MapMakerInternalMap.Strength keyStrength, boolean expireAfterWrite, boolean evictsBySize)
/*      */     {
/*  533 */       int flags = (expireAfterWrite ? 1 : 0) | (evictsBySize ? 2 : 0);
/*  534 */       return factories[keyStrength.ordinal()][flags];
/*      */     }
/*      */ 
/*      */     abstract <K, V> MapMakerInternalMap.ReferenceEntry<K, V> newEntry(MapMakerInternalMap.Segment<K, V> paramSegment, K paramK, int paramInt, @Nullable MapMakerInternalMap.ReferenceEntry<K, V> paramReferenceEntry);
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> MapMakerInternalMap.ReferenceEntry<K, V> copyEntry(MapMakerInternalMap.Segment<K, V> segment, MapMakerInternalMap.ReferenceEntry<K, V> original, MapMakerInternalMap.ReferenceEntry<K, V> newNext)
/*      */     {
/*  557 */       return newEntry(segment, original.getKey(), original.getHash(), newNext);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyExpirableEntry(MapMakerInternalMap.ReferenceEntry<K, V> original, MapMakerInternalMap.ReferenceEntry<K, V> newEntry)
/*      */     {
/*  564 */       newEntry.setExpirationTime(original.getExpirationTime());
/*      */ 
/*  566 */       MapMakerInternalMap.connectExpirables(original.getPreviousExpirable(), newEntry);
/*  567 */       MapMakerInternalMap.connectExpirables(newEntry, original.getNextExpirable());
/*      */ 
/*  569 */       MapMakerInternalMap.nullifyExpirable(original);
/*      */     }
/*      */ 
/*      */     @GuardedBy("Segment.this")
/*      */     <K, V> void copyEvictableEntry(MapMakerInternalMap.ReferenceEntry<K, V> original, MapMakerInternalMap.ReferenceEntry<K, V> newEntry)
/*      */     {
/*  576 */       MapMakerInternalMap.connectEvictables(original.getPreviousEvictable(), newEntry);
/*  577 */       MapMakerInternalMap.connectEvictables(newEntry, original.getNextEvictable());
/*      */ 
/*  579 */       MapMakerInternalMap.nullifyEvictable(original);
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract enum Strength
/*      */   {
/*  296 */     STRONG, 
/*      */ 
/*  309 */     SOFT, 
/*      */ 
/*  322 */     WEAK;
/*      */ 
/*      */     abstract <K, V> MapMakerInternalMap.ValueReference<K, V> referenceValue(MapMakerInternalMap.Segment<K, V> paramSegment, MapMakerInternalMap.ReferenceEntry<K, V> paramReferenceEntry, V paramV);
/*      */ 
/*      */     abstract Equivalence<Object> defaultEquivalence();
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.MapMakerInternalMap
 * JD-Core Version:    0.6.2
 */