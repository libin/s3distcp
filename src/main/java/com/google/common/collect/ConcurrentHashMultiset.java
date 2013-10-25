/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.math.IntMath;
/*     */ import com.google.common.primitives.Ints;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ public final class ConcurrentHashMultiset<E> extends AbstractMultiset<E>
/*     */   implements Serializable
/*     */ {
/*     */   private final transient ConcurrentMap<E, AtomicInteger> countMap;
/*     */   private transient ConcurrentHashMultiset<E>.EntrySet entrySet;
/*     */   private static final long serialVersionUID = 1L;
/*     */ 
/*     */   public static <E> ConcurrentHashMultiset<E> create()
/*     */   {
/*  86 */     return new ConcurrentHashMultiset(new ConcurrentHashMap());
/*     */   }
/*     */ 
/*     */   public static <E> ConcurrentHashMultiset<E> create(Iterable<? extends E> elements)
/*     */   {
/*  98 */     ConcurrentHashMultiset multiset = create();
/*  99 */     Iterables.addAll(multiset, elements);
/* 100 */     return multiset;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <E> ConcurrentHashMultiset<E> create(GenericMapMaker<? super E, ? super Number> mapMaker)
/*     */   {
/* 126 */     return new ConcurrentHashMultiset(mapMaker.makeMap());
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   ConcurrentHashMultiset(ConcurrentMap<E, AtomicInteger> countMap)
/*     */   {
/* 140 */     Preconditions.checkArgument(countMap.isEmpty());
/* 141 */     this.countMap = countMap;
/*     */   }
/*     */ 
/*     */   public int count(@Nullable Object element)
/*     */   {
/* 153 */     AtomicInteger existingCounter = safeGet(element);
/* 154 */     return existingCounter == null ? 0 : existingCounter.get();
/*     */   }
/*     */ 
/*     */   private AtomicInteger safeGet(Object element)
/*     */   {
/*     */     try
/*     */     {
/* 164 */       return (AtomicInteger)this.countMap.get(element);
/*     */     } catch (NullPointerException e) {
/* 166 */       return null; } catch (ClassCastException e) {
/*     */     }
/* 168 */     return null;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 179 */     long sum = 0L;
/* 180 */     for (AtomicInteger value : this.countMap.values()) {
/* 181 */       sum += value.get();
/*     */     }
/* 183 */     return Ints.saturatedCast(sum);
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 192 */     return snapshot().toArray();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] array) {
/* 196 */     return snapshot().toArray(array);
/*     */   }
/*     */ 
/*     */   private List<E> snapshot()
/*     */   {
/* 204 */     List list = Lists.newArrayListWithExpectedSize(size());
/* 205 */     for (Multiset.Entry entry : entrySet()) {
/* 206 */       Object element = entry.getElement();
/* 207 */       for (int i = entry.getCount(); i > 0; i--) {
/* 208 */         list.add(element);
/*     */       }
/*     */     }
/* 211 */     return list;
/*     */   }
/*     */ 
/*     */   public int add(E element, int occurrences)
/*     */   {
/* 226 */     if (occurrences == 0) {
/* 227 */       return count(element);
/*     */     }
/* 229 */     Preconditions.checkArgument(occurrences > 0, "Invalid occurrences: %s", new Object[] { Integer.valueOf(occurrences) });
/*     */     while (true)
/*     */     {
/* 232 */       AtomicInteger existingCounter = safeGet(element);
/* 233 */       if (existingCounter == null) {
/* 234 */         existingCounter = (AtomicInteger)this.countMap.putIfAbsent(element, new AtomicInteger(occurrences));
/* 235 */         if (existingCounter == null) {
/* 236 */           return 0;
/*     */         }
/*     */       }
/*     */ 
/*     */       while (true)
/*     */       {
/* 242 */         int oldValue = existingCounter.get();
/* 243 */         if (oldValue != 0) {
/*     */           try {
/* 245 */             int newValue = IntMath.checkedAdd(oldValue, occurrences);
/* 246 */             if (existingCounter.compareAndSet(oldValue, newValue))
/*     */             {
/* 248 */               return oldValue;
/*     */             }
/*     */           } catch (ArithmeticException overflow) {
/* 251 */             throw new IllegalArgumentException("Overflow adding " + occurrences + " occurrences to a count of " + oldValue);
/*     */           }
/*     */ 
/*     */         }
/*     */         else
/*     */         {
/* 258 */           AtomicInteger newCounter = new AtomicInteger(occurrences);
/* 259 */           if ((this.countMap.putIfAbsent(element, newCounter) != null) && (!this.countMap.replace(element, existingCounter, newCounter)))
/*     */             break;
/* 261 */           return 0;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int remove(@Nullable Object element, int occurrences)
/*     */   {
/* 281 */     if (occurrences == 0) {
/* 282 */       return count(element);
/*     */     }
/* 284 */     Preconditions.checkArgument(occurrences > 0, "Invalid occurrences: %s", new Object[] { Integer.valueOf(occurrences) });
/*     */ 
/* 286 */     AtomicInteger existingCounter = safeGet(element);
/* 287 */     if (existingCounter == null)
/* 288 */       return 0;
/*     */     while (true)
/*     */     {
/* 291 */       int oldValue = existingCounter.get();
/* 292 */       if (oldValue != 0) {
/* 293 */         int newValue = Math.max(0, oldValue - occurrences);
/* 294 */         if (existingCounter.compareAndSet(oldValue, newValue)) {
/* 295 */           if (newValue == 0)
/*     */           {
/* 298 */             this.countMap.remove(element, existingCounter);
/*     */           }
/* 300 */           return oldValue;
/*     */         }
/*     */       } else {
/* 303 */         return 0;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean removeExactly(@Nullable Object element, int occurrences)
/*     */   {
/* 320 */     if (occurrences == 0) {
/* 321 */       return true;
/*     */     }
/* 323 */     Preconditions.checkArgument(occurrences > 0, "Invalid occurrences: %s", new Object[] { Integer.valueOf(occurrences) });
/*     */ 
/* 325 */     AtomicInteger existingCounter = safeGet(element);
/* 326 */     if (existingCounter == null)
/* 327 */       return false;
/*     */     while (true)
/*     */     {
/* 330 */       int oldValue = existingCounter.get();
/* 331 */       if (oldValue < occurrences) {
/* 332 */         return false;
/*     */       }
/* 334 */       int newValue = oldValue - occurrences;
/* 335 */       if (existingCounter.compareAndSet(oldValue, newValue)) {
/* 336 */         if (newValue == 0)
/*     */         {
/* 339 */           this.countMap.remove(element, existingCounter);
/*     */         }
/* 341 */         return true;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int setCount(E element, int count)
/*     */   {
/* 354 */     Multisets.checkNonnegative(count, "count");
/*     */     while (true) {
/* 356 */       AtomicInteger existingCounter = safeGet(element);
/* 357 */       if (existingCounter == null) {
/* 358 */         if (count == 0) {
/* 359 */           return 0;
/*     */         }
/* 361 */         existingCounter = (AtomicInteger)this.countMap.putIfAbsent(element, new AtomicInteger(count));
/* 362 */         if (existingCounter == null) {
/* 363 */           return 0;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */       while (true)
/*     */       {
/* 370 */         int oldValue = existingCounter.get();
/* 371 */         if (oldValue == 0) {
/* 372 */           if (count == 0) {
/* 373 */             return 0;
/*     */           }
/* 375 */           AtomicInteger newCounter = new AtomicInteger(count);
/* 376 */           if ((this.countMap.putIfAbsent(element, newCounter) == null) || (this.countMap.replace(element, existingCounter, newCounter)))
/*     */           {
/* 378 */             return 0;
/*     */           }
/*     */ 
/* 381 */           break;
/*     */         }
/* 383 */         if (existingCounter.compareAndSet(oldValue, count)) {
/* 384 */           if (count == 0)
/*     */           {
/* 387 */             this.countMap.remove(element, existingCounter);
/*     */           }
/* 389 */           return oldValue;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean setCount(E element, int expectedOldCount, int newCount)
/*     */   {
/* 408 */     Multisets.checkNonnegative(expectedOldCount, "oldCount");
/* 409 */     Multisets.checkNonnegative(newCount, "newCount");
/*     */ 
/* 411 */     AtomicInteger existingCounter = safeGet(element);
/* 412 */     if (existingCounter == null) {
/* 413 */       if (expectedOldCount != 0)
/* 414 */         return false;
/* 415 */       if (newCount == 0) {
/* 416 */         return true;
/*     */       }
/*     */ 
/* 419 */       return this.countMap.putIfAbsent(element, new AtomicInteger(newCount)) == null;
/*     */     }
/*     */ 
/* 422 */     int oldValue = existingCounter.get();
/* 423 */     if (oldValue == expectedOldCount) {
/* 424 */       if (oldValue == 0) {
/* 425 */         if (newCount == 0)
/*     */         {
/* 427 */           this.countMap.remove(element, existingCounter);
/* 428 */           return true;
/*     */         }
/* 430 */         AtomicInteger newCounter = new AtomicInteger(newCount);
/* 431 */         return (this.countMap.putIfAbsent(element, newCounter) == null) || (this.countMap.replace(element, existingCounter, newCounter));
/*     */       }
/*     */ 
/* 435 */       if (existingCounter.compareAndSet(oldValue, newCount)) {
/* 436 */         if (newCount == 0)
/*     */         {
/* 439 */           this.countMap.remove(element, existingCounter);
/*     */         }
/* 441 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 445 */     return false;
/*     */   }
/*     */ 
/*     */   Set<E> createElementSet()
/*     */   {
/* 451 */     final Set delegate = this.countMap.keySet();
/* 452 */     return new ForwardingSet() {
/*     */       protected Set<E> delegate() {
/* 454 */         return delegate;
/*     */       }
/*     */       public boolean remove(Object object) {
/*     */         try {
/* 458 */           return delegate.remove(object);
/*     */         } catch (NullPointerException e) {
/* 460 */           return false; } catch (ClassCastException e) {
/*     */         }
/* 462 */         return false;
/*     */       }
/*     */ 
/*     */       public boolean removeAll(Collection<?> c) {
/* 466 */         return standardRemoveAll(c);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public Set<Multiset.Entry<E>> entrySet()
/*     */   {
/* 474 */     EntrySet result = this.entrySet;
/* 475 */     if (result == null) {
/* 476 */       this.entrySet = (result = new EntrySet(null));
/*     */     }
/* 478 */     return result;
/*     */   }
/*     */ 
/*     */   int distinctElements() {
/* 482 */     return this.countMap.size();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 486 */     return this.countMap.isEmpty();
/*     */   }
/*     */ 
/*     */   Iterator<Multiset.Entry<E>> entryIterator()
/*     */   {
/* 492 */     final Iterator readOnlyIterator = new AbstractIterator()
/*     */     {
/* 494 */       private Iterator<Map.Entry<E, AtomicInteger>> mapEntries = ConcurrentHashMultiset.this.countMap.entrySet().iterator();
/*     */ 
/*     */       protected Multiset.Entry<E> computeNext() {
/*     */         while (true) {
/* 498 */           if (!this.mapEntries.hasNext()) {
/* 499 */             return (Multiset.Entry)endOfData();
/*     */           }
/* 501 */           Map.Entry mapEntry = (Map.Entry)this.mapEntries.next();
/* 502 */           int count = ((AtomicInteger)mapEntry.getValue()).get();
/* 503 */           if (count != 0)
/* 504 */             return Multisets.immutableEntry(mapEntry.getKey(), count);
/*     */         }
/*     */       }
/*     */     };
/* 510 */     return new ForwardingIterator() {
/*     */       private Multiset.Entry<E> last;
/*     */ 
/*     */       protected Iterator<Multiset.Entry<E>> delegate() {
/* 514 */         return readOnlyIterator;
/*     */       }
/*     */ 
/*     */       public Multiset.Entry<E> next() {
/* 518 */         this.last = ((Multiset.Entry)super.next());
/* 519 */         return this.last;
/*     */       }
/*     */ 
/*     */       public void remove() {
/* 523 */         Preconditions.checkState(this.last != null);
/* 524 */         ConcurrentHashMultiset.this.setCount(this.last.getElement(), 0);
/* 525 */         this.last = null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public void clear() {
/* 531 */     this.countMap.clear();
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 579 */     stream.defaultWriteObject();
/* 580 */     stream.writeObject(this.countMap);
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
/* 584 */     stream.defaultReadObject();
/*     */ 
/* 586 */     ConcurrentMap deserializedCountMap = (ConcurrentMap)stream.readObject();
/*     */ 
/* 588 */     FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set(this, deserializedCountMap);
/*     */   }
/*     */ 
/*     */   private class EntrySet extends AbstractMultiset.EntrySet
/*     */   {
/*     */     private EntrySet()
/*     */     {
/* 534 */       super();
/*     */     }
/* 536 */     ConcurrentHashMultiset<E> multiset() { return ConcurrentHashMultiset.this; }
/*     */ 
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 545 */       return snapshot().toArray();
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array) {
/* 549 */       return snapshot().toArray(array);
/*     */     }
/*     */ 
/*     */     private List<Multiset.Entry<E>> snapshot() {
/* 553 */       List list = Lists.newArrayListWithExpectedSize(size());
/*     */ 
/* 555 */       Iterators.addAll(list, iterator());
/* 556 */       return list;
/*     */     }
/*     */ 
/*     */     public boolean remove(Object object) {
/* 560 */       if ((object instanceof Multiset.Entry)) {
/* 561 */         Multiset.Entry entry = (Multiset.Entry)object;
/* 562 */         Object element = entry.getElement();
/* 563 */         int entryCount = entry.getCount();
/* 564 */         if (entryCount != 0)
/*     */         {
/* 567 */           Multiset multiset = multiset();
/* 568 */           return multiset.setCount(element, entryCount, 0);
/*     */         }
/*     */       }
/* 571 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FieldSettersHolder
/*     */   {
/*  74 */     static final Serialization.FieldSetter<ConcurrentHashMultiset> COUNT_MAP_FIELD_SETTER = Serialization.getFieldSetter(ConcurrentHashMultiset.class, "countMap");
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ConcurrentHashMultiset
 * JD-Core Version:    0.6.2
 */