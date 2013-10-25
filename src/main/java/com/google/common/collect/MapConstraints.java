/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class MapConstraints
/*     */ {
/*     */   public static MapConstraint<Object, Object> notNull()
/*     */   {
/*  53 */     return NotNullMapConstraint.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <K, V> Map<K, V> constrainedMap(Map<K, V> map, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/*  85 */     return new ConstrainedMap(map, constraint);
/*     */   }
/*     */ 
/*     */   public static <K, V> Multimap<K, V> constrainedMultimap(Multimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 106 */     return new ConstrainedMultimap(multimap, constraint);
/*     */   }
/*     */ 
/*     */   public static <K, V> ListMultimap<K, V> constrainedListMultimap(ListMultimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 128 */     return new ConstrainedListMultimap(multimap, constraint);
/*     */   }
/*     */ 
/*     */   public static <K, V> SetMultimap<K, V> constrainedSetMultimap(SetMultimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 149 */     return new ConstrainedSetMultimap(multimap, constraint);
/*     */   }
/*     */ 
/*     */   public static <K, V> SortedSetMultimap<K, V> constrainedSortedSetMultimap(SortedSetMultimap<K, V> multimap, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 170 */     return new ConstrainedSortedSetMultimap(multimap, constraint);
/*     */   }
/*     */ 
/*     */   private static <K, V> Map.Entry<K, V> constrainedEntry(Map.Entry<K, V> entry, final MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 185 */     Preconditions.checkNotNull(entry);
/* 186 */     Preconditions.checkNotNull(constraint);
/* 187 */     return new ForwardingMapEntry() {
/*     */       protected Map.Entry<K, V> delegate() {
/* 189 */         return this.val$entry;
/*     */       }
/*     */       public V setValue(V value) {
/* 192 */         constraint.checkKeyValue(getKey(), value);
/* 193 */         return this.val$entry.setValue(value);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static <K, V> Map.Entry<K, Collection<V>> constrainedAsMapEntry(Map.Entry<K, Collection<V>> entry, final MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 211 */     Preconditions.checkNotNull(entry);
/* 212 */     Preconditions.checkNotNull(constraint);
/* 213 */     return new ForwardingMapEntry() {
/*     */       protected Map.Entry<K, Collection<V>> delegate() {
/* 215 */         return this.val$entry;
/*     */       }
/*     */       public Collection<V> getValue() {
/* 218 */         return Constraints.constrainedTypePreservingCollection((Collection)this.val$entry.getValue(), new Constraint()
/*     */         {
/*     */           public V checkElement(V value)
/*     */           {
/* 222 */             MapConstraints.2.this.val$constraint.checkKeyValue(MapConstraints.2.this.getKey(), value);
/* 223 */             return value;
/*     */           }
/*     */         });
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static <K, V> Set<Map.Entry<K, Collection<V>>> constrainedAsMapEntries(Set<Map.Entry<K, Collection<V>>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 245 */     return new ConstrainedAsMapEntries(entries, constraint);
/*     */   }
/*     */ 
/*     */   private static <K, V> Collection<Map.Entry<K, V>> constrainedEntries(Collection<Map.Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 263 */     if ((entries instanceof Set)) {
/* 264 */       return constrainedEntrySet((Set)entries, constraint);
/*     */     }
/* 266 */     return new ConstrainedEntries(entries, constraint);
/*     */   }
/*     */ 
/*     */   private static <K, V> Set<Map.Entry<K, V>> constrainedEntrySet(Set<Map.Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 286 */     return new ConstrainedEntrySet(entries, constraint);
/*     */   }
/*     */ 
/*     */   public static <K, V> BiMap<K, V> constrainedBiMap(BiMap<K, V> map, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 333 */     return new ConstrainedBiMap(map, null, constraint);
/*     */   }
/*     */ 
/*     */   private static <K, V> Collection<V> checkValues(K key, Iterable<? extends V> values, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 768 */     Collection copy = Lists.newArrayList(values);
/* 769 */     for (Iterator i$ = copy.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 770 */       constraint.checkKeyValue(key, value);
/*     */     }
/* 772 */     return copy;
/*     */   }
/*     */ 
/*     */   private static <K, V> Map<K, V> checkMap(Map<? extends K, ? extends V> map, MapConstraint<? super K, ? super V> constraint)
/*     */   {
/* 777 */     Map copy = new LinkedHashMap(map);
/* 778 */     for (Map.Entry entry : copy.entrySet()) {
/* 779 */       constraint.checkKeyValue(entry.getKey(), entry.getValue());
/*     */     }
/* 781 */     return copy;
/*     */   }
/*     */ 
/*     */   private static class ConstrainedSortedSetMultimap<K, V> extends MapConstraints.ConstrainedSetMultimap<K, V>
/*     */     implements SortedSetMultimap<K, V>
/*     */   {
/*     */     ConstrainedSortedSetMultimap(SortedSetMultimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 747 */       super(constraint);
/*     */     }
/*     */     public SortedSet<V> get(K key) {
/* 750 */       return (SortedSet)super.get(key);
/*     */     }
/*     */     public SortedSet<V> removeAll(Object key) {
/* 753 */       return (SortedSet)super.removeAll(key);
/*     */     }
/*     */ 
/*     */     public SortedSet<V> replaceValues(K key, Iterable<? extends V> values) {
/* 757 */       return (SortedSet)super.replaceValues(key, values);
/*     */     }
/*     */ 
/*     */     public Comparator<? super V> valueComparator() {
/* 761 */       return ((SortedSetMultimap)delegate()).valueComparator();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstrainedSetMultimap<K, V> extends MapConstraints.ConstrainedMultimap<K, V>
/*     */     implements SetMultimap<K, V>
/*     */   {
/*     */     ConstrainedSetMultimap(SetMultimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 726 */       super(constraint);
/*     */     }
/*     */     public Set<V> get(K key) {
/* 729 */       return (Set)super.get(key);
/*     */     }
/*     */     public Set<Map.Entry<K, V>> entries() {
/* 732 */       return (Set)super.entries();
/*     */     }
/*     */     public Set<V> removeAll(Object key) {
/* 735 */       return (Set)super.removeAll(key);
/*     */     }
/*     */ 
/*     */     public Set<V> replaceValues(K key, Iterable<? extends V> values) {
/* 739 */       return (Set)super.replaceValues(key, values);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstrainedListMultimap<K, V> extends MapConstraints.ConstrainedMultimap<K, V>
/*     */     implements ListMultimap<K, V>
/*     */   {
/*     */     ConstrainedListMultimap(ListMultimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 708 */       super(constraint);
/*     */     }
/*     */     public List<V> get(K key) {
/* 711 */       return (List)super.get(key);
/*     */     }
/*     */     public List<V> removeAll(Object key) {
/* 714 */       return (List)super.removeAll(key);
/*     */     }
/*     */ 
/*     */     public List<V> replaceValues(K key, Iterable<? extends V> values) {
/* 718 */       return (List)super.replaceValues(key, values);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConstrainedAsMapEntries<K, V> extends ForwardingSet<Map.Entry<K, Collection<V>>>
/*     */   {
/*     */     private final MapConstraint<? super K, ? super V> constraint;
/*     */     private final Set<Map.Entry<K, Collection<V>>> entries;
/*     */ 
/*     */     ConstrainedAsMapEntries(Set<Map.Entry<K, Collection<V>>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 645 */       this.entries = entries;
/* 646 */       this.constraint = constraint;
/*     */     }
/*     */ 
/*     */     protected Set<Map.Entry<K, Collection<V>>> delegate() {
/* 650 */       return this.entries;
/*     */     }
/*     */ 
/*     */     public Iterator<Map.Entry<K, Collection<V>>> iterator() {
/* 654 */       final Iterator iterator = this.entries.iterator();
/* 655 */       return new ForwardingIterator() {
/*     */         public Map.Entry<K, Collection<V>> next() {
/* 657 */           return MapConstraints.constrainedAsMapEntry((Map.Entry)iterator.next(), MapConstraints.ConstrainedAsMapEntries.this.constraint);
/*     */         }
/*     */         protected Iterator<Map.Entry<K, Collection<V>>> delegate() {
/* 660 */           return iterator;
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 668 */       return standardToArray();
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array) {
/* 672 */       return standardToArray(array);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object o) {
/* 676 */       return Maps.containsEntryImpl(delegate(), o);
/*     */     }
/*     */ 
/*     */     public boolean containsAll(Collection<?> c) {
/* 680 */       return standardContainsAll(c);
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object object) {
/* 684 */       return standardEquals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 688 */       return standardHashCode();
/*     */     }
/*     */ 
/*     */     public boolean remove(Object o) {
/* 692 */       return Maps.removeEntryImpl(delegate(), o);
/*     */     }
/*     */ 
/*     */     public boolean removeAll(Collection<?> c) {
/* 696 */       return standardRemoveAll(c);
/*     */     }
/*     */ 
/*     */     public boolean retainAll(Collection<?> c) {
/* 700 */       return standardRetainAll(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConstrainedEntrySet<K, V> extends MapConstraints.ConstrainedEntries<K, V>
/*     */     implements Set<Map.Entry<K, V>>
/*     */   {
/*     */     ConstrainedEntrySet(Set<Map.Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 623 */       super(constraint);
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object object)
/*     */     {
/* 629 */       return Sets.equalsImpl(this, object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 633 */       return Sets.hashCodeImpl(this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstrainedEntries<K, V> extends ForwardingCollection<Map.Entry<K, V>>
/*     */   {
/*     */     final MapConstraint<? super K, ? super V> constraint;
/*     */     final Collection<Map.Entry<K, V>> entries;
/*     */ 
/*     */     ConstrainedEntries(Collection<Map.Entry<K, V>> entries, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 574 */       this.entries = entries;
/* 575 */       this.constraint = constraint;
/*     */     }
/*     */     protected Collection<Map.Entry<K, V>> delegate() {
/* 578 */       return this.entries;
/*     */     }
/*     */ 
/*     */     public Iterator<Map.Entry<K, V>> iterator() {
/* 582 */       final Iterator iterator = this.entries.iterator();
/* 583 */       return new ForwardingIterator() {
/*     */         public Map.Entry<K, V> next() {
/* 585 */           return MapConstraints.constrainedEntry((Map.Entry)iterator.next(), MapConstraints.ConstrainedEntries.this.constraint);
/*     */         }
/*     */         protected Iterator<Map.Entry<K, V>> delegate() {
/* 588 */           return iterator;
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Object[] toArray()
/*     */     {
/* 596 */       return standardToArray();
/*     */     }
/*     */     public <T> T[] toArray(T[] array) {
/* 599 */       return standardToArray(array);
/*     */     }
/*     */     public boolean contains(Object o) {
/* 602 */       return Maps.containsEntryImpl(delegate(), o);
/*     */     }
/*     */     public boolean containsAll(Collection<?> c) {
/* 605 */       return standardContainsAll(c);
/*     */     }
/*     */     public boolean remove(Object o) {
/* 608 */       return Maps.removeEntryImpl(delegate(), o);
/*     */     }
/*     */     public boolean removeAll(Collection<?> c) {
/* 611 */       return standardRemoveAll(c);
/*     */     }
/*     */     public boolean retainAll(Collection<?> c) {
/* 614 */       return standardRetainAll(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstrainedAsMapValues<K, V> extends ForwardingCollection<Collection<V>>
/*     */   {
/*     */     final Collection<Collection<V>> delegate;
/*     */     final Set<Map.Entry<K, Collection<V>>> entrySet;
/*     */ 
/*     */     ConstrainedAsMapValues(Collection<Collection<V>> delegate, Set<Map.Entry<K, Collection<V>>> entrySet)
/*     */     {
/* 518 */       this.delegate = delegate;
/* 519 */       this.entrySet = entrySet;
/*     */     }
/*     */     protected Collection<Collection<V>> delegate() {
/* 522 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public Iterator<Collection<V>> iterator() {
/* 526 */       final Iterator iterator = this.entrySet.iterator();
/* 527 */       return new Iterator()
/*     */       {
/*     */         public boolean hasNext() {
/* 530 */           return iterator.hasNext();
/*     */         }
/*     */ 
/*     */         public Collection<V> next() {
/* 534 */           return (Collection)((Map.Entry)iterator.next()).getValue();
/*     */         }
/*     */ 
/*     */         public void remove() {
/* 538 */           iterator.remove();
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Object[] toArray() {
/* 544 */       return standardToArray();
/*     */     }
/*     */     public <T> T[] toArray(T[] array) {
/* 547 */       return standardToArray(array);
/*     */     }
/*     */     public boolean contains(Object o) {
/* 550 */       return standardContains(o);
/*     */     }
/*     */     public boolean containsAll(Collection<?> c) {
/* 553 */       return standardContainsAll(c);
/*     */     }
/*     */     public boolean remove(Object o) {
/* 556 */       return standardRemove(o);
/*     */     }
/*     */     public boolean removeAll(Collection<?> c) {
/* 559 */       return standardRemoveAll(c);
/*     */     }
/*     */     public boolean retainAll(Collection<?> c) {
/* 562 */       return standardRetainAll(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstrainedMultimap<K, V> extends ForwardingMultimap<K, V>
/*     */   {
/*     */     final MapConstraint<? super K, ? super V> constraint;
/*     */     final Multimap<K, V> delegate;
/*     */     transient Collection<Map.Entry<K, V>> entries;
/*     */     transient Map<K, Collection<V>> asMap;
/*     */ 
/*     */     public ConstrainedMultimap(Multimap<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 406 */       this.delegate = ((Multimap)Preconditions.checkNotNull(delegate));
/* 407 */       this.constraint = ((MapConstraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */ 
/*     */     protected Multimap<K, V> delegate() {
/* 411 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public Map<K, Collection<V>> asMap() {
/* 415 */       Map result = this.asMap;
/* 416 */       if (result == null) {
/* 417 */         final Map asMapDelegate = this.delegate.asMap();
/*     */ 
/* 419 */         this.asMap = (result = new ForwardingMap() {
/*     */           Set<Map.Entry<K, Collection<V>>> entrySet;
/*     */           Collection<Collection<V>> values;
/*     */ 
/* 424 */           protected Map<K, Collection<V>> delegate() { return asMapDelegate; }
/*     */ 
/*     */           public Set<Map.Entry<K, Collection<V>>> entrySet()
/*     */           {
/* 428 */             Set result = this.entrySet;
/* 429 */             if (result == null) {
/* 430 */               this.entrySet = (result = MapConstraints.constrainedAsMapEntries(asMapDelegate.entrySet(), MapConstraints.ConstrainedMultimap.this.constraint));
/*     */             }
/*     */ 
/* 433 */             return result;
/*     */           }
/*     */ 
/*     */           public Collection<V> get(Object key)
/*     */           {
/*     */             try {
/* 439 */               Collection collection = MapConstraints.ConstrainedMultimap.this.get(key);
/* 440 */               return collection.isEmpty() ? null : collection; } catch (ClassCastException e) {
/*     */             }
/* 442 */             return null;
/*     */           }
/*     */ 
/*     */           public Collection<Collection<V>> values()
/*     */           {
/* 447 */             Collection result = this.values;
/* 448 */             if (result == null) {
/* 449 */               this.values = (result = new MapConstraints.ConstrainedAsMapValues(delegate().values(), entrySet()));
/*     */             }
/*     */ 
/* 452 */             return result;
/*     */           }
/*     */ 
/*     */           public boolean containsValue(Object o) {
/* 456 */             return values().contains(o);
/*     */           }
/*     */         });
/*     */       }
/* 460 */       return result;
/*     */     }
/*     */ 
/*     */     public Collection<Map.Entry<K, V>> entries() {
/* 464 */       Collection result = this.entries;
/* 465 */       if (result == null) {
/* 466 */         this.entries = (result = MapConstraints.constrainedEntries(this.delegate.entries(), this.constraint));
/*     */       }
/* 468 */       return result;
/*     */     }
/*     */ 
/*     */     public Collection<V> get(final K key) {
/* 472 */       return Constraints.constrainedTypePreservingCollection(this.delegate.get(key), new Constraint()
/*     */       {
/*     */         public V checkElement(V value)
/*     */         {
/* 476 */           MapConstraints.ConstrainedMultimap.this.constraint.checkKeyValue(key, value);
/* 477 */           return value;
/*     */         }
/*     */       });
/*     */     }
/*     */ 
/*     */     public boolean put(K key, V value) {
/* 483 */       this.constraint.checkKeyValue(key, value);
/* 484 */       return this.delegate.put(key, value);
/*     */     }
/*     */ 
/*     */     public boolean putAll(K key, Iterable<? extends V> values) {
/* 488 */       return this.delegate.putAll(key, MapConstraints.checkValues(key, values, this.constraint));
/*     */     }
/*     */ 
/*     */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*     */     {
/* 493 */       boolean changed = false;
/* 494 */       for (Map.Entry entry : multimap.entries()) {
/* 495 */         changed |= put(entry.getKey(), entry.getValue());
/*     */       }
/* 497 */       return changed;
/*     */     }
/*     */ 
/*     */     public Collection<V> replaceValues(K key, Iterable<? extends V> values)
/*     */     {
/* 502 */       return this.delegate.replaceValues(key, MapConstraints.checkValues(key, values, this.constraint));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class InverseConstraint<K, V>
/*     */     implements MapConstraint<K, V>
/*     */   {
/*     */     final MapConstraint<? super V, ? super K> constraint;
/*     */ 
/*     */     public InverseConstraint(MapConstraint<? super V, ? super K> constraint)
/*     */     {
/* 388 */       this.constraint = ((MapConstraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */ 
/*     */     public void checkKeyValue(K key, V value) {
/* 392 */       this.constraint.checkKeyValue(value, key);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstrainedBiMap<K, V> extends MapConstraints.ConstrainedMap<K, V>
/*     */     implements BiMap<K, V>
/*     */   {
/*     */     volatile BiMap<V, K> inverse;
/*     */ 
/*     */     ConstrainedBiMap(BiMap<K, V> delegate, @Nullable BiMap<V, K> inverse, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 355 */       super(constraint);
/* 356 */       this.inverse = inverse;
/*     */     }
/*     */ 
/*     */     protected BiMap<K, V> delegate() {
/* 360 */       return (BiMap)super.delegate();
/*     */     }
/*     */ 
/*     */     public V forcePut(K key, V value)
/*     */     {
/* 365 */       this.constraint.checkKeyValue(key, value);
/* 366 */       return delegate().forcePut(key, value);
/*     */     }
/*     */ 
/*     */     public BiMap<V, K> inverse()
/*     */     {
/* 371 */       if (this.inverse == null) {
/* 372 */         this.inverse = new ConstrainedBiMap(delegate().inverse(), this, new MapConstraints.InverseConstraint(this.constraint));
/*     */       }
/*     */ 
/* 375 */       return this.inverse;
/*     */     }
/*     */ 
/*     */     public Set<V> values() {
/* 379 */       return delegate().values();
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConstrainedMap<K, V> extends ForwardingMap<K, V>
/*     */   {
/*     */     private final Map<K, V> delegate;
/*     */     final MapConstraint<? super K, ? super V> constraint;
/*     */     private transient Set<Map.Entry<K, V>> entrySet;
/*     */ 
/*     */     ConstrainedMap(Map<K, V> delegate, MapConstraint<? super K, ? super V> constraint)
/*     */     {
/* 297 */       this.delegate = ((Map)Preconditions.checkNotNull(delegate));
/* 298 */       this.constraint = ((MapConstraint)Preconditions.checkNotNull(constraint));
/*     */     }
/*     */     protected Map<K, V> delegate() {
/* 301 */       return this.delegate;
/*     */     }
/*     */     public Set<Map.Entry<K, V>> entrySet() {
/* 304 */       Set result = this.entrySet;
/* 305 */       if (result == null) {
/* 306 */         this.entrySet = (result = MapConstraints.constrainedEntrySet(this.delegate.entrySet(), this.constraint));
/*     */       }
/*     */ 
/* 309 */       return result;
/*     */     }
/*     */     public V put(K key, V value) {
/* 312 */       this.constraint.checkKeyValue(key, value);
/* 313 */       return this.delegate.put(key, value);
/*     */     }
/*     */     public void putAll(Map<? extends K, ? extends V> map) {
/* 316 */       this.delegate.putAll(MapConstraints.checkMap(map, this.constraint));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum NotNullMapConstraint
/*     */     implements MapConstraint<Object, Object>
/*     */   {
/*  58 */     INSTANCE;
/*     */ 
/*     */     public void checkKeyValue(Object key, Object value)
/*     */     {
/*  62 */       Preconditions.checkNotNull(key);
/*  63 */       Preconditions.checkNotNull(value);
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  67 */       return "Not null";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.MapConstraints
 * JD-Core Version:    0.6.2
 */