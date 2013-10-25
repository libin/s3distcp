/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.base.Equivalence;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Throwables;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ 
/*     */ class ComputingConcurrentHashMap<K, V> extends MapMakerInternalMap<K, V>
/*     */ {
/*     */   final Function<? super K, ? extends V> computingFunction;
/*     */   private static final long serialVersionUID = 4L;
/*     */ 
/*     */   ComputingConcurrentHashMap(MapMaker builder, Function<? super K, ? extends V> computingFunction)
/*     */   {
/*  53 */     super(builder);
/*  54 */     this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */   }
/*     */ 
/*     */   MapMakerInternalMap.Segment<K, V> createSegment(int initialCapacity, int maxSegmentSize)
/*     */   {
/*  59 */     return new ComputingSegment(this, initialCapacity, maxSegmentSize);
/*     */   }
/*     */ 
/*     */   ComputingSegment<K, V> segmentFor(int hash)
/*     */   {
/*  64 */     return (ComputingSegment)super.segmentFor(hash);
/*     */   }
/*     */ 
/*     */   V getOrCompute(K key) throws ExecutionException {
/*  68 */     int hash = hash(Preconditions.checkNotNull(key));
/*  69 */     return segmentFor(hash).getOrCompute(key, hash, this.computingFunction);
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 416 */     return new ComputingSerializationProxy(this.keyStrength, this.valueStrength, this.keyEquivalence, this.valueEquivalence, this.expireAfterWriteNanos, this.expireAfterAccessNanos, this.maximumSize, this.concurrencyLevel, this.removalListener, this, this.computingFunction);
/*     */   }
/*     */ 
/*     */   static final class ComputingSerializationProxy<K, V> extends MapMakerInternalMap.AbstractSerializationProxy<K, V>
/*     */   {
/*     */     final Function<? super K, ? extends V> computingFunction;
/*     */     private static final long serialVersionUID = 4L;
/*     */ 
/*     */     ComputingSerializationProxy(MapMakerInternalMap.Strength keyStrength, MapMakerInternalMap.Strength valueStrength, Equivalence<Object> keyEquivalence, Equivalence<Object> valueEquivalence, long expireAfterWriteNanos, long expireAfterAccessNanos, int maximumSize, int concurrencyLevel, MapMaker.RemovalListener<? super K, ? super V> removalListener, ConcurrentMap<K, V> delegate, Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 430 */       super(valueStrength, keyEquivalence, valueEquivalence, expireAfterWriteNanos, expireAfterAccessNanos, maximumSize, concurrencyLevel, removalListener, delegate);
/*     */ 
/* 432 */       this.computingFunction = computingFunction;
/*     */     }
/*     */ 
/*     */     private void writeObject(ObjectOutputStream out) throws IOException {
/* 436 */       out.defaultWriteObject();
/* 437 */       writeMapTo(out);
/*     */     }
/*     */ 
/*     */     private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
/*     */     {
/* 442 */       in.defaultReadObject();
/* 443 */       MapMaker mapMaker = readMapMaker(in);
/* 444 */       this.delegate = mapMaker.makeComputingMap(this.computingFunction);
/* 445 */       readEntries(in);
/*     */     }
/*     */ 
/*     */     Object readResolve() {
/* 449 */       return this.delegate;
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ComputingMapAdapter<K, V> extends ComputingConcurrentHashMap<K, V>
/*     */     implements Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     ComputingMapAdapter(MapMaker mapMaker, Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 388 */       super(computingFunction);
/*     */     }
/*     */ 
/*     */     public V get(Object key)
/*     */     {
/*     */       Object value;
/*     */       try
/*     */       {
/* 396 */         value = getOrCompute(key);
/*     */       } catch (ExecutionException e) {
/* 398 */         Throwable cause = e.getCause();
/* 399 */         Throwables.propagateIfInstanceOf(cause, ComputationException.class);
/* 400 */         throw new ComputationException(cause);
/*     */       }
/*     */ 
/* 403 */       if (value == null) {
/* 404 */         throw new NullPointerException(this.computingFunction + " returned null for key " + key + ".");
/*     */       }
/* 406 */       return value;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ComputingValueReference<K, V>
/*     */     implements MapMakerInternalMap.ValueReference<K, V>
/*     */   {
/*     */     final Function<? super K, ? extends V> computingFunction;
/*     */ 
/*     */     @GuardedBy("ComputingValueReference.this")
/* 290 */     volatile MapMakerInternalMap.ValueReference<K, V> computedReference = MapMakerInternalMap.unset();
/*     */ 
/*     */     public ComputingValueReference(Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 294 */       this.computingFunction = computingFunction;
/*     */     }
/*     */ 
/*     */     public V get()
/*     */     {
/* 301 */       return null;
/*     */     }
/*     */ 
/*     */     public MapMakerInternalMap.ReferenceEntry<K, V> getEntry()
/*     */     {
/* 306 */       return null;
/*     */     }
/*     */ 
/*     */     public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*     */     {
/* 312 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean isComputingReference()
/*     */     {
/* 317 */       return true;
/*     */     }
/*     */ 
/*     */     public V waitForValue()
/*     */       throws ExecutionException
/*     */     {
/* 325 */       if (this.computedReference == MapMakerInternalMap.UNSET) {
/* 326 */         boolean interrupted = false;
/*     */         try {
/* 328 */           synchronized (this) {
/* 329 */             while (this.computedReference == MapMakerInternalMap.UNSET)
/*     */               try {
/* 331 */                 wait();
/*     */               } catch (InterruptedException ie) {
/* 333 */                 interrupted = true;
/*     */               }
/*     */           }
/*     */         }
/*     */         finally {
/* 338 */           if (interrupted) {
/* 339 */             Thread.currentThread().interrupt();
/*     */           }
/*     */         }
/*     */       }
/* 343 */       return this.computedReference.waitForValue();
/*     */     }
/*     */ 
/*     */     public void clear(MapMakerInternalMap.ValueReference<K, V> newValue)
/*     */     {
/* 350 */       setValueReference(newValue);
/*     */     }
/*     */ 
/*     */     V compute(K key, int hash) throws ExecutionException
/*     */     {
/*     */       Object value;
/*     */       try
/*     */       {
/* 358 */         value = this.computingFunction.apply(key);
/*     */       } catch (Throwable t) {
/* 360 */         setValueReference(new ComputingConcurrentHashMap.ComputationExceptionReference(t));
/* 361 */         throw new ExecutionException(t);
/*     */       }
/*     */ 
/* 364 */       setValueReference(new ComputingConcurrentHashMap.ComputedReference(value));
/* 365 */       return value;
/*     */     }
/*     */ 
/*     */     void setValueReference(MapMakerInternalMap.ValueReference<K, V> valueReference) {
/* 369 */       synchronized (this) {
/* 370 */         if (this.computedReference == MapMakerInternalMap.UNSET) {
/* 371 */           this.computedReference = valueReference;
/* 372 */           notifyAll();
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ComputedReference<K, V>
/*     */     implements MapMakerInternalMap.ValueReference<K, V>
/*     */   {
/*     */     final V value;
/*     */ 
/*     */     ComputedReference(@Nullable V value)
/*     */     {
/* 254 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public V get()
/*     */     {
/* 259 */       return this.value;
/*     */     }
/*     */ 
/*     */     public MapMakerInternalMap.ReferenceEntry<K, V> getEntry()
/*     */     {
/* 264 */       return null;
/*     */     }
/*     */ 
/*     */     public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*     */     {
/* 270 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean isComputingReference()
/*     */     {
/* 275 */       return false;
/*     */     }
/*     */ 
/*     */     public V waitForValue()
/*     */     {
/* 280 */       return get();
/*     */     }
/*     */ 
/*     */     public void clear(MapMakerInternalMap.ValueReference<K, V> newValue)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ComputationExceptionReference<K, V>
/*     */     implements MapMakerInternalMap.ValueReference<K, V>
/*     */   {
/*     */     final Throwable t;
/*     */ 
/*     */     ComputationExceptionReference(Throwable t)
/*     */     {
/* 214 */       this.t = t;
/*     */     }
/*     */ 
/*     */     public V get()
/*     */     {
/* 219 */       return null;
/*     */     }
/*     */ 
/*     */     public MapMakerInternalMap.ReferenceEntry<K, V> getEntry()
/*     */     {
/* 224 */       return null;
/*     */     }
/*     */ 
/*     */     public MapMakerInternalMap.ValueReference<K, V> copyFor(ReferenceQueue<V> queue, V value, MapMakerInternalMap.ReferenceEntry<K, V> entry)
/*     */     {
/* 230 */       return this;
/*     */     }
/*     */ 
/*     */     public boolean isComputingReference()
/*     */     {
/* 235 */       return false;
/*     */     }
/*     */ 
/*     */     public V waitForValue() throws ExecutionException
/*     */     {
/* 240 */       throw new ExecutionException(this.t);
/*     */     }
/*     */ 
/*     */     public void clear(MapMakerInternalMap.ValueReference<K, V> newValue)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ComputingSegment<K, V> extends MapMakerInternalMap.Segment<K, V>
/*     */   {
/*     */     ComputingSegment(MapMakerInternalMap<K, V> map, int initialCapacity, int maxSegmentSize)
/*     */     {
/*  75 */       super(initialCapacity, maxSegmentSize);
/*     */     }
/*     */     V getOrCompute(K key, int hash, Function<? super K, ? extends V> computingFunction) throws ExecutionException {
/*     */       try { MapMakerInternalMap.ReferenceEntry e;
/*     */         Object computingValueReference;
/*     */         Object value;
/*     */         do { e = getEntry(key, hash);
/*  84 */           if (e != null) {
/*  85 */             Object value = getLiveValue(e);
/*  86 */             if (value != null) {
/*  87 */               recordRead(e);
/*  88 */               return value;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/*  94 */           if ((e == null) || (!e.getValueReference().isComputingReference())) { boolean createNewEntry = true;
/*  96 */             computingValueReference = null;
/*  97 */             lock();
/*     */             int newCount;
/*     */             try { preWriteCleanup();
/*     */ 
/* 101 */               newCount = this.count - 1;
/* 102 */               AtomicReferenceArray table = this.table;
/* 103 */               int index = hash & table.length() - 1;
/* 104 */               MapMakerInternalMap.ReferenceEntry first = (MapMakerInternalMap.ReferenceEntry)table.get(index);
/*     */ 
/* 106 */               for (e = first; e != null; e = e.getNext()) {
/* 107 */                 Object entryKey = e.getKey();
/* 108 */                 if ((e.getHash() == hash) && (entryKey != null) && (this.map.keyEquivalence.equivalent(key, entryKey)))
/*     */                 {
/* 110 */                   MapMakerInternalMap.ValueReference valueReference = e.getValueReference();
/* 111 */                   if (valueReference.isComputingReference()) {
/* 112 */                     createNewEntry = false; break;
/*     */                   }
/* 114 */                   Object value = e.getValueReference().get();
/* 115 */                   if (value == null) {
/* 116 */                     enqueueNotification(entryKey, hash, value, MapMaker.RemovalCause.COLLECTED);
/* 117 */                   } else if ((this.map.expires()) && (this.map.isExpired(e)))
/*     */                   {
/* 120 */                     enqueueNotification(entryKey, hash, value, MapMaker.RemovalCause.EXPIRED);
/*     */                   } else {
/* 122 */                     recordLockedRead(e);
/* 123 */                     return value;
/*     */                   }
/*     */ 
/* 127 */                   this.evictionQueue.remove(e);
/* 128 */                   this.expirationQueue.remove(e);
/* 129 */                   this.count = newCount;
/*     */ 
/* 131 */                   break;
/*     */                 }
/*     */               }
/*     */ 
/* 135 */               if (createNewEntry) {
/* 136 */                 computingValueReference = new ComputingConcurrentHashMap.ComputingValueReference(computingFunction);
/*     */ 
/* 138 */                 if (e == null) {
/* 139 */                   e = newEntry(key, hash, first);
/* 140 */                   e.setValueReference((MapMakerInternalMap.ValueReference)computingValueReference);
/* 141 */                   table.set(index, e);
/*     */                 } else {
/* 143 */                   e.setValueReference((MapMakerInternalMap.ValueReference)computingValueReference);
/*     */                 }
/*     */               }
/*     */             } finally {
/* 147 */               unlock();
/*     */             }
/*     */ 
/* 151 */             if (createNewEntry)
/*     */             {
/* 153 */               return compute(key, hash, e, (ComputingConcurrentHashMap.ComputingValueReference)computingValueReference);
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 158 */           Preconditions.checkState(!Thread.holdsLock(e), "Recursive computation");
/*     */ 
/* 160 */           value = e.getValueReference().waitForValue(); }
/* 161 */         while (value == null);
/* 162 */         recordRead(e);
/* 163 */         return value;
/*     */       }
/*     */       finally
/*     */       {
/* 169 */         postReadCleanup();
/*     */       }
/*     */     }
/*     */ 
/*     */     V compute(K key, int hash, MapMakerInternalMap.ReferenceEntry<K, V> e, ComputingConcurrentHashMap.ComputingValueReference<K, V> computingValueReference)
/*     */       throws ExecutionException
/*     */     {
/* 176 */       Object value = null;
/* 177 */       long start = System.nanoTime();
/* 178 */       long end = 0L;
/*     */       try
/*     */       {
/* 183 */         synchronized (e) {
/* 184 */           value = computingValueReference.compute(key, hash);
/* 185 */           end = System.nanoTime();
/*     */         }
/*     */         Object oldValue;
/* 187 */         if (value != null)
/*     */         {
/* 189 */           oldValue = put(key, hash, value, true);
/* 190 */           if (oldValue != null)
/*     */           {
/* 192 */             enqueueNotification(key, hash, value, MapMaker.RemovalCause.REPLACED);
/*     */           }
/*     */         }
/* 195 */         return value;
/*     */       } finally {
/* 197 */         if (end == 0L) {
/* 198 */           end = System.nanoTime();
/*     */         }
/* 200 */         if (value == null)
/* 201 */           clearValue(key, hash, computingValueReference);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ComputingConcurrentHashMap
 * JD-Core Version:    0.6.2
 */