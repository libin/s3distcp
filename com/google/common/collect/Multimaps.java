/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Joiner;
/*      */ import com.google.common.base.Joiner.MapJoiner;
/*      */ import com.google.common.base.Objects;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import com.google.common.base.Predicates;
/*      */ import com.google.common.base.Supplier;
/*      */ import java.io.IOException;
/*      */ import java.io.ObjectInputStream;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.Serializable;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.HashSet;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NoSuchElementException;
/*      */ import java.util.Set;
/*      */ import java.util.SortedSet;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Multimaps
/*      */ {
/*      */   public static <K, V> Multimap<K, V> newMultimap(Map<K, Collection<V>> map, Supplier<? extends Collection<V>> factory)
/*      */   {
/*  113 */     return new CustomMultimap(map, factory);
/*      */   }
/*      */ 
/*      */   public static <K, V> ListMultimap<K, V> newListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> factory)
/*      */   {
/*  194 */     return new CustomListMultimap(map, factory);
/*      */   }
/*      */ 
/*      */   public static <K, V> SetMultimap<K, V> newSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> factory)
/*      */   {
/*  272 */     return new CustomSetMultimap(map, factory);
/*      */   }
/*      */ 
/*      */   public static <K, V> SortedSetMultimap<K, V> newSortedSetMultimap(Map<K, Collection<V>> map, Supplier<? extends SortedSet<V>> factory)
/*      */   {
/*  350 */     return new CustomSortedSetMultimap(map, factory);
/*      */   }
/*      */ 
/*      */   public static <K, V, M extends Multimap<K, V>> M invertFrom(Multimap<? extends V, ? extends K> source, M dest)
/*      */   {
/*  409 */     Preconditions.checkNotNull(dest);
/*  410 */     for (Map.Entry entry : source.entries()) {
/*  411 */       dest.put(entry.getValue(), entry.getKey());
/*      */     }
/*  413 */     return dest;
/*      */   }
/*      */ 
/*      */   public static <K, V> Multimap<K, V> synchronizedMultimap(Multimap<K, V> multimap)
/*      */   {
/*  451 */     return Synchronized.multimap(multimap, null);
/*      */   }
/*      */ 
/*      */   public static <K, V> Multimap<K, V> unmodifiableMultimap(Multimap<K, V> delegate)
/*      */   {
/*  473 */     if (((delegate instanceof UnmodifiableMultimap)) || ((delegate instanceof ImmutableMultimap)))
/*      */     {
/*  475 */       return delegate;
/*      */     }
/*  477 */     return new UnmodifiableMultimap(delegate);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static <K, V> Multimap<K, V> unmodifiableMultimap(ImmutableMultimap<K, V> delegate)
/*      */   {
/*  488 */     return (Multimap)Preconditions.checkNotNull(delegate);
/*      */   }
/*      */ 
/*      */   public static <K, V> SetMultimap<K, V> synchronizedSetMultimap(SetMultimap<K, V> multimap)
/*      */   {
/*  745 */     return Synchronized.setMultimap(multimap, null);
/*      */   }
/*      */ 
/*      */   public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(SetMultimap<K, V> delegate)
/*      */   {
/*  768 */     if (((delegate instanceof UnmodifiableSetMultimap)) || ((delegate instanceof ImmutableSetMultimap)))
/*      */     {
/*  770 */       return delegate;
/*      */     }
/*  772 */     return new UnmodifiableSetMultimap(delegate);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static <K, V> SetMultimap<K, V> unmodifiableSetMultimap(ImmutableSetMultimap<K, V> delegate)
/*      */   {
/*  783 */     return (SetMultimap)Preconditions.checkNotNull(delegate);
/*      */   }
/*      */ 
/*      */   public static <K, V> SortedSetMultimap<K, V> synchronizedSortedSetMultimap(SortedSetMultimap<K, V> multimap)
/*      */   {
/*  800 */     return Synchronized.sortedSetMultimap(multimap, null);
/*      */   }
/*      */ 
/*      */   public static <K, V> SortedSetMultimap<K, V> unmodifiableSortedSetMultimap(SortedSetMultimap<K, V> delegate)
/*      */   {
/*  823 */     if ((delegate instanceof UnmodifiableSortedSetMultimap)) {
/*  824 */       return delegate;
/*      */     }
/*  826 */     return new UnmodifiableSortedSetMultimap(delegate);
/*      */   }
/*      */ 
/*      */   public static <K, V> ListMultimap<K, V> synchronizedListMultimap(ListMultimap<K, V> multimap)
/*      */   {
/*  840 */     return Synchronized.listMultimap(multimap, null);
/*      */   }
/*      */ 
/*      */   public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ListMultimap<K, V> delegate)
/*      */   {
/*  863 */     if (((delegate instanceof UnmodifiableListMultimap)) || ((delegate instanceof ImmutableListMultimap)))
/*      */     {
/*  865 */       return delegate;
/*      */     }
/*  867 */     return new UnmodifiableListMultimap(delegate);
/*      */   }
/*      */ 
/*      */   @Deprecated
/*      */   public static <K, V> ListMultimap<K, V> unmodifiableListMultimap(ImmutableListMultimap<K, V> delegate)
/*      */   {
/*  878 */     return (ListMultimap)Preconditions.checkNotNull(delegate);
/*      */   }
/*      */ 
/*      */   private static <V> Collection<V> unmodifiableValueCollection(Collection<V> collection)
/*      */   {
/*  891 */     if ((collection instanceof SortedSet))
/*  892 */       return Collections.unmodifiableSortedSet((SortedSet)collection);
/*  893 */     if ((collection instanceof Set))
/*  894 */       return Collections.unmodifiableSet((Set)collection);
/*  895 */     if ((collection instanceof List)) {
/*  896 */       return Collections.unmodifiableList((List)collection);
/*      */     }
/*  898 */     return Collections.unmodifiableCollection(collection);
/*      */   }
/*      */ 
/*      */   private static <K, V> Map.Entry<K, Collection<V>> unmodifiableAsMapEntry(Map.Entry<K, Collection<V>> entry)
/*      */   {
/*  914 */     Preconditions.checkNotNull(entry);
/*  915 */     return new AbstractMapEntry() {
/*      */       public K getKey() {
/*  917 */         return this.val$entry.getKey();
/*      */       }
/*      */ 
/*      */       public Collection<V> getValue() {
/*  921 */         return Multimaps.unmodifiableValueCollection((Collection)this.val$entry.getValue());
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   private static <K, V> Collection<Map.Entry<K, V>> unmodifiableEntries(Collection<Map.Entry<K, V>> entries)
/*      */   {
/*  937 */     if ((entries instanceof Set)) {
/*  938 */       return Maps.unmodifiableEntrySet((Set)entries);
/*      */     }
/*  940 */     return new Maps.UnmodifiableEntries(Collections.unmodifiableCollection(entries));
/*      */   }
/*      */ 
/*      */   private static <K, V> Set<Map.Entry<K, Collection<V>>> unmodifiableAsMapEntries(Set<Map.Entry<K, Collection<V>>> asMapEntries)
/*      */   {
/*  956 */     return new UnmodifiableAsMapEntries(Collections.unmodifiableSet(asMapEntries));
/*      */   }
/*      */ 
/*      */   public static <K, V> SetMultimap<K, V> forMap(Map<K, V> map)
/*      */   {
/* 1023 */     return new MapMultimap(map);
/*      */   }
/*      */ 
/*      */   public static <K, V1, V2> Multimap<K, V2> transformValues(Multimap<K, V1> fromMultimap, Function<? super V1, V2> function)
/*      */   {
/* 1319 */     Preconditions.checkNotNull(function);
/* 1320 */     Maps.EntryTransformer transformer = new Maps.EntryTransformer()
/*      */     {
/*      */       public V2 transformEntry(K key, V1 value)
/*      */       {
/* 1324 */         return this.val$function.apply(value);
/*      */       }
/*      */     };
/* 1327 */     return transformEntries(fromMultimap, transformer);
/*      */   }
/*      */ 
/*      */   public static <K, V1, V2> Multimap<K, V2> transformEntries(Multimap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */   {
/* 1388 */     return new TransformedEntriesMultimap(fromMap, transformer);
/*      */   }
/*      */ 
/*      */   public static <K, V1, V2> ListMultimap<K, V2> transformValues(ListMultimap<K, V1> fromMultimap, Function<? super V1, V2> function)
/*      */   {
/* 1627 */     Preconditions.checkNotNull(function);
/* 1628 */     Maps.EntryTransformer transformer = new Maps.EntryTransformer()
/*      */     {
/*      */       public V2 transformEntry(K key, V1 value)
/*      */       {
/* 1632 */         return this.val$function.apply(value);
/*      */       }
/*      */     };
/* 1635 */     return transformEntries(fromMultimap, transformer);
/*      */   }
/*      */ 
/*      */   public static <K, V1, V2> ListMultimap<K, V2> transformEntries(ListMultimap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */   {
/* 1693 */     return new TransformedEntriesListMultimap(fromMap, transformer);
/*      */   }
/*      */ 
/*      */   public static <K, V> ImmutableListMultimap<K, V> index(Iterable<V> values, Function<? super V, K> keyFunction)
/*      */   {
/* 1772 */     return index(values.iterator(), keyFunction);
/*      */   }
/*      */ 
/*      */   public static <K, V> ImmutableListMultimap<K, V> index(Iterator<V> values, Function<? super V, K> keyFunction)
/*      */   {
/* 1820 */     Preconditions.checkNotNull(keyFunction);
/* 1821 */     ImmutableListMultimap.Builder builder = ImmutableListMultimap.builder();
/*      */ 
/* 1823 */     while (values.hasNext()) {
/* 1824 */       Object value = values.next();
/* 1825 */       Preconditions.checkNotNull(value, values);
/* 1826 */       builder.put(keyFunction.apply(value), value);
/*      */     }
/* 1828 */     return builder.build();
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   @GwtIncompatible("untested")
/*      */   public static <K, V> Multimap<K, V> filterKeys(Multimap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*      */   {
/* 2129 */     Preconditions.checkNotNull(keyPredicate);
/* 2130 */     Predicate entryPredicate = new Predicate()
/*      */     {
/*      */       public boolean apply(Map.Entry<K, V> input)
/*      */       {
/* 2134 */         return this.val$keyPredicate.apply(input.getKey());
/*      */       }
/*      */     };
/* 2137 */     return filterEntries(unfiltered, entryPredicate);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   @GwtIncompatible("untested")
/*      */   public static <K, V> Multimap<K, V> filterValues(Multimap<K, V> unfiltered, Predicate<? super V> valuePredicate)
/*      */   {
/* 2174 */     Preconditions.checkNotNull(valuePredicate);
/* 2175 */     Predicate entryPredicate = new Predicate()
/*      */     {
/*      */       public boolean apply(Map.Entry<K, V> input)
/*      */       {
/* 2179 */         return this.val$valuePredicate.apply(input.getValue());
/*      */       }
/*      */     };
/* 2182 */     return filterEntries(unfiltered, entryPredicate);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   @GwtIncompatible("untested")
/*      */   public static <K, V> Multimap<K, V> filterEntries(Multimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */   {
/* 2217 */     Preconditions.checkNotNull(entryPredicate);
/* 2218 */     return (unfiltered instanceof FilteredMultimap) ? filterFiltered((FilteredMultimap)unfiltered, entryPredicate) : new FilteredMultimap((Multimap)Preconditions.checkNotNull(unfiltered), entryPredicate);
/*      */   }
/*      */ 
/*      */   private static <K, V> Multimap<K, V> filterFiltered(FilteredMultimap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */   {
/* 2232 */     Predicate predicate = Predicates.and(map.predicate, entryPredicate);
/*      */ 
/* 2234 */     return new FilteredMultimap(map.unfiltered, predicate);
/*      */   }
/*      */ 
/*      */   private static class FilteredMultimap<K, V>
/*      */     implements Multimap<K, V>
/*      */   {
/*      */     final Multimap<K, V> unfiltered;
/*      */     final Predicate<? super Map.Entry<K, V>> predicate;
/*      */     Collection<V> values;
/*      */     Collection<Map.Entry<K, V>> entries;
/*      */     Map<K, Collection<V>> asMap;
/* 2471 */     static final Predicate<Collection<?>> NOT_EMPTY = new Predicate() {
/*      */       public boolean apply(Collection<?> input) {
/* 2473 */         return !input.isEmpty();
/*      */       }
/* 2471 */     };
/*      */     AbstractMultiset<K> keys;
/*      */ 
/*      */     FilteredMultimap(Multimap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate)
/*      */     {
/* 2242 */       this.unfiltered = unfiltered;
/* 2243 */       this.predicate = predicate;
/*      */     }
/*      */ 
/*      */     public int size() {
/* 2247 */       return entries().size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 2251 */       return entries().isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key) {
/* 2255 */       return asMap().containsKey(key);
/*      */     }
/*      */ 
/*      */     public boolean containsValue(Object value) {
/* 2259 */       return values().contains(value);
/*      */     }
/*      */ 
/*      */     boolean satisfiesPredicate(Object key, Object value)
/*      */     {
/* 2265 */       return this.predicate.apply(Maps.immutableEntry(key, value));
/*      */     }
/*      */ 
/*      */     public boolean containsEntry(Object key, Object value) {
/* 2269 */       return (this.unfiltered.containsEntry(key, value)) && (satisfiesPredicate(key, value));
/*      */     }
/*      */ 
/*      */     public boolean put(K key, V value) {
/* 2273 */       Preconditions.checkArgument(satisfiesPredicate(key, value));
/* 2274 */       return this.unfiltered.put(key, value);
/*      */     }
/*      */ 
/*      */     public boolean remove(Object key, Object value) {
/* 2278 */       return containsEntry(key, value) ? this.unfiltered.remove(key, value) : false;
/*      */     }
/*      */ 
/*      */     public boolean putAll(K key, Iterable<? extends V> values) {
/* 2282 */       for (Iterator i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 2283 */         Preconditions.checkArgument(satisfiesPredicate(key, value));
/*      */       }
/* 2285 */       return this.unfiltered.putAll(key, values);
/*      */     }
/*      */ 
/*      */     public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
/* 2289 */       for (Map.Entry entry : multimap.entries()) {
/* 2290 */         Preconditions.checkArgument(satisfiesPredicate(entry.getKey(), entry.getValue()));
/*      */       }
/* 2292 */       return this.unfiltered.putAll(multimap);
/*      */     }
/*      */ 
/*      */     public Collection<V> replaceValues(K key, Iterable<? extends V> values) {
/* 2296 */       for (Iterator i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 2297 */         Preconditions.checkArgument(satisfiesPredicate(key, value));
/*      */       }
/*      */ 
/* 2301 */       Collection oldValues = removeAll(key);
/* 2302 */       this.unfiltered.putAll(key, values);
/* 2303 */       return oldValues;
/*      */     }
/*      */ 
/*      */     public Collection<V> removeAll(Object key) {
/* 2307 */       List removed = Lists.newArrayList();
/* 2308 */       Collection values = (Collection)this.unfiltered.asMap().get(key);
/* 2309 */       if (values != null) {
/* 2310 */         Iterator iterator = values.iterator();
/* 2311 */         while (iterator.hasNext()) {
/* 2312 */           Object value = iterator.next();
/* 2313 */           if (satisfiesPredicate(key, value)) {
/* 2314 */             removed.add(value);
/* 2315 */             iterator.remove();
/*      */           }
/*      */         }
/*      */       }
/* 2319 */       if ((this.unfiltered instanceof SetMultimap)) {
/* 2320 */         return Collections.unmodifiableSet(Sets.newLinkedHashSet(removed));
/*      */       }
/* 2322 */       return Collections.unmodifiableList(removed);
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 2327 */       entries().clear();
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/* 2331 */       if (object == this) {
/* 2332 */         return true;
/*      */       }
/* 2334 */       if ((object instanceof Multimap)) {
/* 2335 */         Multimap that = (Multimap)object;
/* 2336 */         return asMap().equals(that.asMap());
/*      */       }
/* 2338 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/* 2342 */       return asMap().hashCode();
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 2346 */       return asMap().toString();
/*      */     }
/*      */ 
/*      */     Collection<V> filterCollection(Collection<V> collection, Predicate<V> predicate)
/*      */     {
/* 2360 */       if ((collection instanceof Set)) {
/* 2361 */         return Sets.filter((Set)collection, predicate);
/*      */       }
/* 2363 */       return Collections2.filter(collection, predicate);
/*      */     }
/*      */ 
/*      */     public Collection<V> get(K key)
/*      */     {
/* 2368 */       return filterCollection(this.unfiltered.get(key), new ValuePredicate(key));
/*      */     }
/*      */ 
/*      */     public Set<K> keySet() {
/* 2372 */       return asMap().keySet();
/*      */     }
/*      */ 
/*      */     public Collection<V> values()
/*      */     {
/* 2378 */       return this.values == null ? (this.values = new Values()) : this.values;
/*      */     }
/*      */ 
/*      */     public Collection<Map.Entry<K, V>> entries()
/*      */     {
/* 2434 */       return this.entries == null ? (this.entries = Collections2.filter(this.unfiltered.entries(), this.predicate)) : this.entries;
/*      */     }
/*      */ 
/*      */     boolean removeEntriesIf(Predicate<Map.Entry<K, Collection<V>>> removalPredicate)
/*      */     {
/* 2443 */       Iterator iterator = this.unfiltered.asMap().entrySet().iterator();
/* 2444 */       boolean changed = false;
/* 2445 */       while (iterator.hasNext())
/*      */       {
/* 2447 */         Map.Entry entry = (Map.Entry)iterator.next();
/* 2448 */         Object key = entry.getKey();
/* 2449 */         Collection collection = (Collection)entry.getValue();
/* 2450 */         Predicate valuePredicate = new ValuePredicate(key);
/* 2451 */         Collection filteredCollection = filterCollection(collection, valuePredicate);
/* 2452 */         Map.Entry filteredEntry = Maps.immutableEntry(key, filteredCollection);
/* 2453 */         if ((removalPredicate.apply(filteredEntry)) && (!filteredCollection.isEmpty())) {
/* 2454 */           changed = true;
/* 2455 */           if (Iterables.all(collection, valuePredicate))
/* 2456 */             iterator.remove();
/*      */           else {
/* 2458 */             filteredCollection.clear();
/*      */           }
/*      */         }
/*      */       }
/* 2462 */       return changed;
/*      */     }
/*      */ 
/*      */     public Map<K, Collection<V>> asMap()
/*      */     {
/* 2468 */       return this.asMap == null ? (this.asMap = createAsMap()) : this.asMap;
/*      */     }
/*      */ 
/*      */     Map<K, Collection<V>> createAsMap()
/*      */     {
/* 2479 */       Maps.EntryTransformer transformer = new Maps.EntryTransformer()
/*      */       {
/*      */         public Collection<V> transformEntry(K key, Collection<V> collection) {
/* 2482 */           return Multimaps.FilteredMultimap.this.filterCollection(collection, new Multimaps.FilteredMultimap.ValuePredicate(Multimaps.FilteredMultimap.this, key));
/*      */         }
/*      */       };
/* 2485 */       Map transformed = Maps.transformEntries(this.unfiltered.asMap(), transformer);
/*      */ 
/* 2489 */       Map filtered = Maps.filterValues(transformed, NOT_EMPTY);
/*      */ 
/* 2493 */       return new AsMap(filtered);
/*      */     }
/*      */ 
/*      */     public Multiset<K> keys()
/*      */     {
/* 2645 */       return this.keys == null ? (this.keys = new Keys()) : this.keys;
/*      */     }
/*      */     class Keys extends Multimaps.Keys<K, V> {
/*      */       Keys() {
/*      */       }
/* 2650 */       Multimap<K, V> multimap() { return Multimaps.FilteredMultimap.this; }
/*      */ 
/*      */       public int remove(Object o, int occurrences)
/*      */       {
/* 2654 */         Preconditions.checkArgument(occurrences >= 0);
/* 2655 */         Collection values = (Collection)Multimaps.FilteredMultimap.this.unfiltered.asMap().get(o);
/* 2656 */         if (values == null) {
/* 2657 */           return 0;
/*      */         }
/* 2659 */         int priorCount = 0;
/* 2660 */         int removed = 0;
/* 2661 */         Iterator iterator = values.iterator();
/* 2662 */         while (iterator.hasNext()) {
/* 2663 */           if (Multimaps.FilteredMultimap.this.satisfiesPredicate(o, iterator.next())) {
/* 2664 */             priorCount++;
/* 2665 */             if (removed < occurrences) {
/* 2666 */               iterator.remove();
/* 2667 */               removed++;
/*      */             }
/*      */           }
/*      */         }
/* 2671 */         return priorCount;
/*      */       }
/*      */ 
/*      */       Set<Multiset.Entry<K>> createEntrySet() {
/* 2675 */         return new EntrySet();
/*      */       }
/*      */       class EntrySet extends Multimaps.Keys.KeysEntrySet {
/* 2678 */         EntrySet() { super(); }
/*      */ 
/*      */         public boolean removeAll(Collection<?> c) {
/* 2681 */           return Sets.removeAllImpl(this, c.iterator());
/*      */         }
/*      */ 
/*      */         public boolean retainAll(final Collection<?> c) {
/* 2685 */           Predicate removalPredicate = new Predicate()
/*      */           {
/*      */             public boolean apply(Map.Entry<K, Collection<V>> entry) {
/* 2688 */               Multiset.Entry multisetEntry = Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size());
/*      */ 
/* 2690 */               return !c.contains(multisetEntry);
/*      */             }
/*      */           };
/* 2693 */           return Multimaps.FilteredMultimap.this.removeEntriesIf(removalPredicate);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     class AsMap extends ForwardingMap<K, Collection<V>>
/*      */     {
/*      */       final Map<K, Collection<V>> delegate;
/*      */       Set<K> keySet;
/*      */       Multimaps.FilteredMultimap<K, V>.AsMap.Values asMapValues;
/*      */       Multimaps.FilteredMultimap<K, V>.AsMap.EntrySet entrySet;
/*      */ 
/*      */       AsMap()
/*      */       {
/* 2500 */         this.delegate = delegate;
/*      */       }
/*      */ 
/*      */       protected Map<K, Collection<V>> delegate() {
/* 2504 */         return this.delegate;
/*      */       }
/*      */ 
/*      */       public Collection<V> remove(Object o) {
/* 2508 */         Collection output = Multimaps.FilteredMultimap.this.removeAll(o);
/* 2509 */         return output.isEmpty() ? null : output;
/*      */       }
/*      */ 
/*      */       public void clear() {
/* 2513 */         Multimaps.FilteredMultimap.this.clear();
/*      */       }
/*      */ 
/*      */       public Set<K> keySet()
/*      */       {
/* 2519 */         return this.keySet == null ? (this.keySet = new KeySet()) : this.keySet;
/*      */       }
/*      */ 
/*      */       public Collection<Collection<V>> values()
/*      */       {
/* 2554 */         return this.asMapValues == null ? (this.asMapValues = new Values()) : this.asMapValues;
/*      */       }
/*      */ 
/*      */       public Set<Map.Entry<K, Collection<V>>> entrySet()
/*      */       {
/* 2596 */         return this.entrySet == null ? (this.entrySet = new EntrySet(super.entrySet())) : this.entrySet;
/*      */       }
/*      */ 
/*      */       class EntrySet extends Maps.EntrySet<K, Collection<V>> {
/*      */         Set<Map.Entry<K, Collection<V>>> delegateEntries;
/*      */ 
/*      */         public EntrySet() {
/* 2603 */           this.delegateEntries = delegateEntries;
/*      */         }
/*      */ 
/*      */         Map<K, Collection<V>> map() {
/* 2607 */           return Multimaps.FilteredMultimap.AsMap.this;
/*      */         }
/*      */ 
/*      */         public Iterator<Map.Entry<K, Collection<V>>> iterator() {
/* 2611 */           return this.delegateEntries.iterator();
/*      */         }
/*      */ 
/*      */         public boolean remove(Object o) {
/* 2615 */           if ((o instanceof Map.Entry)) {
/* 2616 */             Map.Entry entry = (Map.Entry)o;
/* 2617 */             Collection collection = (Collection)Multimaps.FilteredMultimap.AsMap.this.delegate.get(entry.getKey());
/* 2618 */             if ((collection != null) && (collection.equals(entry.getValue()))) {
/* 2619 */               collection.clear();
/* 2620 */               return true;
/*      */             }
/*      */           }
/* 2623 */           return false;
/*      */         }
/*      */ 
/*      */         public boolean removeAll(Collection<?> c) {
/* 2627 */           return Sets.removeAllImpl(this, c);
/*      */         }
/*      */ 
/*      */         public boolean retainAll(final Collection<?> c) {
/* 2631 */           Predicate removalPredicate = new Predicate()
/*      */           {
/*      */             public boolean apply(Map.Entry<K, Collection<V>> entry) {
/* 2634 */               return !c.contains(entry);
/*      */             }
/*      */           };
/* 2637 */           return Multimaps.FilteredMultimap.this.removeEntriesIf(removalPredicate);
/*      */         }
/*      */       }
/*      */ 
/*      */       class Values extends Maps.Values<K, Collection<V>>
/*      */       {
/*      */         Values()
/*      */         {
/*      */         }
/*      */ 
/*      */         Map<K, Collection<V>> map()
/*      */         {
/* 2559 */           return Multimaps.FilteredMultimap.AsMap.this;
/*      */         }
/*      */ 
/*      */         public boolean remove(Object o) {
/* 2563 */           for (Collection collection : this) {
/* 2564 */             if (collection.equals(o)) {
/* 2565 */               collection.clear();
/* 2566 */               return true;
/*      */             }
/*      */           }
/* 2569 */           return false;
/*      */         }
/*      */ 
/*      */         public boolean removeAll(final Collection<?> c) {
/* 2573 */           Predicate removalPredicate = new Predicate()
/*      */           {
/*      */             public boolean apply(Map.Entry<K, Collection<V>> entry) {
/* 2576 */               return c.contains(entry.getValue());
/*      */             }
/*      */           };
/* 2579 */           return Multimaps.FilteredMultimap.this.removeEntriesIf(removalPredicate);
/*      */         }
/*      */ 
/*      */         public boolean retainAll(final Collection<?> c) {
/* 2583 */           Predicate removalPredicate = new Predicate()
/*      */           {
/*      */             public boolean apply(Map.Entry<K, Collection<V>> entry) {
/* 2586 */               return !c.contains(entry.getValue());
/*      */             }
/*      */           };
/* 2589 */           return Multimaps.FilteredMultimap.this.removeEntriesIf(removalPredicate);
/*      */         }
/*      */       }
/*      */ 
/*      */       class KeySet extends Maps.KeySet<K, Collection<V>>
/*      */       {
/*      */         KeySet()
/*      */         {
/*      */         }
/*      */ 
/*      */         Map<K, Collection<V>> map()
/*      */         {
/* 2524 */           return Multimaps.FilteredMultimap.AsMap.this;
/*      */         }
/*      */ 
/*      */         public boolean remove(Object o) {
/* 2528 */           Collection collection = (Collection)Multimaps.FilteredMultimap.AsMap.this.delegate.get(o);
/* 2529 */           if (collection == null) {
/* 2530 */             return false;
/*      */           }
/* 2532 */           collection.clear();
/* 2533 */           return true;
/*      */         }
/*      */ 
/*      */         public boolean removeAll(Collection<?> c) {
/* 2537 */           return Sets.removeAllImpl(this, c.iterator());
/*      */         }
/*      */ 
/*      */         public boolean retainAll(final Collection<?> c) {
/* 2541 */           Predicate removalPredicate = new Predicate()
/*      */           {
/*      */             public boolean apply(Map.Entry<K, Collection<V>> entry) {
/* 2544 */               return !c.contains(entry.getKey());
/*      */             }
/*      */           };
/* 2547 */           return Multimaps.FilteredMultimap.this.removeEntriesIf(removalPredicate);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     class Values extends Multimaps.Values<K, V>
/*      */     {
/*      */       Values()
/*      */       {
/*      */       }
/*      */ 
/*      */       Multimap<K, V> multimap()
/*      */       {
/* 2383 */         return Multimaps.FilteredMultimap.this;
/*      */       }
/*      */ 
/*      */       public boolean contains(@Nullable Object o) {
/* 2387 */         return Iterators.contains(iterator(), o);
/*      */       }
/*      */ 
/*      */       public boolean remove(Object o)
/*      */       {
/* 2393 */         Iterator iterator = Multimaps.FilteredMultimap.this.unfiltered.entries().iterator();
/* 2394 */         while (iterator.hasNext()) {
/* 2395 */           Map.Entry entry = (Map.Entry)iterator.next();
/* 2396 */           if ((Objects.equal(o, entry.getValue())) && (Multimaps.FilteredMultimap.this.predicate.apply(entry))) {
/* 2397 */             iterator.remove();
/* 2398 */             return true;
/*      */           }
/*      */         }
/* 2401 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean removeAll(Collection<?> c) {
/* 2405 */         boolean changed = false;
/* 2406 */         Iterator iterator = Multimaps.FilteredMultimap.this.unfiltered.entries().iterator();
/* 2407 */         while (iterator.hasNext()) {
/* 2408 */           Map.Entry entry = (Map.Entry)iterator.next();
/* 2409 */           if ((c.contains(entry.getValue())) && (Multimaps.FilteredMultimap.this.predicate.apply(entry))) {
/* 2410 */             iterator.remove();
/* 2411 */             changed = true;
/*      */           }
/*      */         }
/* 2414 */         return changed;
/*      */       }
/*      */ 
/*      */       public boolean retainAll(Collection<?> c) {
/* 2418 */         boolean changed = false;
/* 2419 */         Iterator iterator = Multimaps.FilteredMultimap.this.unfiltered.entries().iterator();
/* 2420 */         while (iterator.hasNext()) {
/* 2421 */           Map.Entry entry = (Map.Entry)iterator.next();
/* 2422 */           if ((!c.contains(entry.getValue())) && (Multimaps.FilteredMultimap.this.predicate.apply(entry))) {
/* 2423 */             iterator.remove();
/* 2424 */             changed = true;
/*      */           }
/*      */         }
/* 2427 */         return changed;
/*      */       }
/*      */     }
/*      */ 
/*      */     class ValuePredicate
/*      */       implements Predicate<V>
/*      */     {
/*      */       final K key;
/*      */ 
/*      */       ValuePredicate()
/*      */       {
/* 2352 */         this.key = key;
/*      */       }
/*      */       public boolean apply(V value) {
/* 2355 */         return Multimaps.FilteredMultimap.this.satisfiesPredicate(this.key, value);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class AsMap<K, V> extends Maps.ImprovedAbstractMap<K, Collection<V>>
/*      */   {
/*      */     abstract Multimap<K, V> multimap();
/*      */ 
/*      */     public abstract int size();
/*      */ 
/*      */     abstract Iterator<Map.Entry<K, Collection<V>>> entryIterator();
/*      */ 
/*      */     protected Set<Map.Entry<K, Collection<V>>> createEntrySet()
/*      */     {
/* 2043 */       return new EntrySet();
/*      */     }
/*      */ 
/*      */     void removeValuesForKey(Object key) {
/* 2047 */       multimap().removeAll(key);
/*      */     }
/*      */ 
/*      */     public Collection<V> get(Object key)
/*      */     {
/* 2071 */       return containsKey(key) ? multimap().get(key) : null;
/*      */     }
/*      */ 
/*      */     public Collection<V> remove(Object key) {
/* 2075 */       return containsKey(key) ? multimap().removeAll(key) : null;
/*      */     }
/*      */ 
/*      */     public Set<K> keySet() {
/* 2079 */       return multimap().keySet();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 2083 */       return multimap().isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key) {
/* 2087 */       return multimap().containsKey(key);
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 2091 */       multimap().clear();
/*      */     }
/*      */ 
/*      */     class EntrySet extends Maps.EntrySet<K, Collection<V>>
/*      */     {
/*      */       EntrySet()
/*      */       {
/*      */       }
/*      */ 
/*      */       Map<K, Collection<V>> map()
/*      */       {
/* 2052 */         return Multimaps.AsMap.this;
/*      */       }
/*      */ 
/*      */       public Iterator<Map.Entry<K, Collection<V>>> iterator() {
/* 2056 */         return Multimaps.AsMap.this.entryIterator();
/*      */       }
/*      */ 
/*      */       public boolean remove(Object o) {
/* 2060 */         if (!contains(o)) {
/* 2061 */           return false;
/*      */         }
/* 2063 */         Map.Entry entry = (Map.Entry)o;
/* 2064 */         Multimaps.AsMap.this.removeValuesForKey(entry.getKey());
/* 2065 */         return true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class EntrySet<K, V> extends Multimaps.Entries<K, V>
/*      */     implements Set<Map.Entry<K, V>>
/*      */   {
/*      */     public int hashCode()
/*      */     {
/* 2023 */       return Sets.hashCodeImpl(this);
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object obj) {
/* 2027 */       return Sets.equalsImpl(this, obj);
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class Entries<K, V> extends AbstractCollection<Map.Entry<K, V>>
/*      */   {
/*      */     abstract Multimap<K, V> multimap();
/*      */ 
/*      */     public int size()
/*      */     {
/* 1993 */       return multimap().size();
/*      */     }
/*      */ 
/*      */     public boolean contains(@Nullable Object o) {
/* 1997 */       if ((o instanceof Map.Entry)) {
/* 1998 */         Map.Entry entry = (Map.Entry)o;
/* 1999 */         return multimap().containsEntry(entry.getKey(), entry.getValue());
/*      */       }
/* 2001 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean remove(@Nullable Object o) {
/* 2005 */       if ((o instanceof Map.Entry)) {
/* 2006 */         Map.Entry entry = (Map.Entry)o;
/* 2007 */         return multimap().remove(entry.getKey(), entry.getValue());
/*      */       }
/* 2009 */       return false;
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 2013 */       multimap().clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class Values<K, V> extends AbstractCollection<V>
/*      */   {
/*      */     abstract Multimap<K, V> multimap();
/*      */ 
/*      */     public Iterator<V> iterator()
/*      */     {
/* 1969 */       return Maps.valueIterator(multimap().entries().iterator());
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1973 */       return multimap().size();
/*      */     }
/*      */ 
/*      */     public boolean contains(@Nullable Object o) {
/* 1977 */       return multimap().containsValue(o);
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 1981 */       multimap().clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class Keys<K, V> extends AbstractMultiset<K>
/*      */   {
/*      */     abstract Multimap<K, V> multimap();
/*      */ 
/*      */     Iterator<Multiset.Entry<K>> entryIterator()
/*      */     {
/* 1835 */       return new TransformedIterator(multimap().asMap().entrySet().iterator())
/*      */       {
/*      */         Multiset.Entry<K> transform(final Map.Entry<K, Collection<V>> backingEntry)
/*      */         {
/* 1840 */           return new Multisets.AbstractEntry()
/*      */           {
/*      */             public K getElement() {
/* 1843 */               return backingEntry.getKey();
/*      */             }
/*      */ 
/*      */             public int getCount()
/*      */             {
/* 1848 */               return ((Collection)backingEntry.getValue()).size();
/*      */             }
/*      */           };
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     int distinctElements() {
/* 1856 */       return multimap().asMap().size();
/*      */     }
/*      */ 
/*      */     Set<Multiset.Entry<K>> createEntrySet() {
/* 1860 */       return new KeysEntrySet();
/*      */     }
/*      */ 
/*      */     public boolean contains(@Nullable Object element)
/*      */     {
/* 1903 */       return multimap().containsKey(element);
/*      */     }
/*      */ 
/*      */     public Iterator<K> iterator() {
/* 1907 */       return Maps.keyIterator(multimap().entries().iterator());
/*      */     }
/*      */ 
/*      */     public int count(@Nullable Object element) {
/*      */       try {
/* 1912 */         if (multimap().containsKey(element)) {
/* 1913 */           Collection values = (Collection)multimap().asMap().get(element);
/* 1914 */           return values == null ? 0 : values.size();
/*      */         }
/* 1916 */         return 0;
/*      */       } catch (ClassCastException e) {
/* 1918 */         return 0; } catch (NullPointerException e) {
/*      */       }
/* 1920 */       return 0;
/*      */     }
/*      */ 
/*      */     public int remove(@Nullable Object element, int occurrences)
/*      */     {
/* 1925 */       Preconditions.checkArgument(occurrences >= 0);
/* 1926 */       if (occurrences == 0) {
/* 1927 */         return count(element);
/*      */       }
/*      */       Collection values;
/*      */       try
/*      */       {
/* 1932 */         values = (Collection)multimap().asMap().get(element);
/*      */       } catch (ClassCastException e) {
/* 1934 */         return 0;
/*      */       } catch (NullPointerException e) {
/* 1936 */         return 0;
/*      */       }
/*      */ 
/* 1939 */       if (values == null) {
/* 1940 */         return 0;
/*      */       }
/*      */ 
/* 1943 */       int oldCount = values.size();
/* 1944 */       if (occurrences >= oldCount) {
/* 1945 */         values.clear();
/*      */       } else {
/* 1947 */         Iterator iterator = values.iterator();
/* 1948 */         for (int i = 0; i < occurrences; i++) {
/* 1949 */           iterator.next();
/* 1950 */           iterator.remove();
/*      */         }
/*      */       }
/* 1953 */       return oldCount;
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 1957 */       multimap().clear();
/*      */     }
/*      */ 
/*      */     public Set<K> elementSet() {
/* 1961 */       return multimap().keySet();
/*      */     }
/*      */ 
/*      */     class KeysEntrySet extends Multisets.EntrySet<K>
/*      */     {
/*      */       KeysEntrySet()
/*      */       {
/*      */       }
/*      */ 
/*      */       Multiset<K> multiset()
/*      */       {
/* 1865 */         return Multimaps.Keys.this;
/*      */       }
/*      */ 
/*      */       public Iterator<Multiset.Entry<K>> iterator() {
/* 1869 */         return Multimaps.Keys.this.entryIterator();
/*      */       }
/*      */ 
/*      */       public int size() {
/* 1873 */         return Multimaps.Keys.this.distinctElements();
/*      */       }
/*      */ 
/*      */       public boolean isEmpty() {
/* 1877 */         return Multimaps.Keys.this.multimap().isEmpty();
/*      */       }
/*      */ 
/*      */       public boolean contains(@Nullable Object o) {
/* 1881 */         if ((o instanceof Multiset.Entry)) {
/* 1882 */           Multiset.Entry entry = (Multiset.Entry)o;
/* 1883 */           Collection collection = (Collection)Multimaps.Keys.this.multimap().asMap().get(entry.getElement());
/* 1884 */           return (collection != null) && (collection.size() == entry.getCount());
/*      */         }
/* 1886 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean remove(@Nullable Object o) {
/* 1890 */         if ((o instanceof Multiset.Entry)) {
/* 1891 */           Multiset.Entry entry = (Multiset.Entry)o;
/* 1892 */           Collection collection = (Collection)Multimaps.Keys.this.multimap().asMap().get(entry.getElement());
/* 1893 */           if ((collection != null) && (collection.size() == entry.getCount())) {
/* 1894 */             collection.clear();
/* 1895 */             return true;
/*      */           }
/*      */         }
/* 1898 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static final class TransformedEntriesListMultimap<K, V1, V2> extends Multimaps.TransformedEntriesMultimap<K, V1, V2>
/*      */     implements ListMultimap<K, V2>
/*      */   {
/*      */     TransformedEntriesListMultimap(ListMultimap<K, V1> fromMultimap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */     {
/* 1702 */       super(transformer);
/*      */     }
/*      */ 
/*      */     List<V2> transform(final K key, Collection<V1> values) {
/* 1706 */       return Lists.transform((List)values, new Function() {
/*      */         public V2 apply(V1 value) {
/* 1708 */           return Multimaps.TransformedEntriesListMultimap.this.transformer.transformEntry(key, value);
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public List<V2> get(K key) {
/* 1714 */       return transform(key, this.fromMultimap.get(key));
/*      */     }
/*      */ 
/*      */     public List<V2> removeAll(Object key)
/*      */     {
/* 1719 */       return transform(key, this.fromMultimap.removeAll(key));
/*      */     }
/*      */ 
/*      */     public List<V2> replaceValues(K key, Iterable<? extends V2> values)
/*      */     {
/* 1724 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class TransformedEntriesMultimap<K, V1, V2>
/*      */     implements Multimap<K, V2>
/*      */   {
/*      */     final Multimap<K, V1> fromMultimap;
/*      */     final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;
/*      */     private transient Map<K, Collection<V2>> asMap;
/*      */     private transient Collection<Map.Entry<K, V2>> entries;
/*      */     private transient Collection<V2> values;
/*      */ 
/*      */     TransformedEntriesMultimap(Multimap<K, V1> fromMultimap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */     {
/* 1398 */       this.fromMultimap = ((Multimap)Preconditions.checkNotNull(fromMultimap));
/* 1399 */       this.transformer = ((Maps.EntryTransformer)Preconditions.checkNotNull(transformer));
/*      */     }
/*      */ 
/*      */     Collection<V2> transform(final K key, Collection<V1> values) {
/* 1403 */       return Collections2.transform(values, new Function() {
/*      */         public V2 apply(V1 value) {
/* 1405 */           return Multimaps.TransformedEntriesMultimap.this.transformer.transformEntry(key, value);
/*      */         }
/*      */       });
/*      */     }
/*      */ 
/*      */     public Map<K, Collection<V2>> asMap()
/*      */     {
/* 1413 */       if (this.asMap == null) {
/* 1414 */         Map aM = Maps.transformEntries(this.fromMultimap.asMap(), new Maps.EntryTransformer()
/*      */         {
/*      */           public Collection<V2> transformEntry(K key, Collection<V1> value)
/*      */           {
/* 1419 */             return Multimaps.TransformedEntriesMultimap.this.transform(key, value);
/*      */           }
/*      */         });
/* 1422 */         this.asMap = aM;
/* 1423 */         return aM;
/*      */       }
/* 1425 */       return this.asMap;
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 1429 */       this.fromMultimap.clear();
/*      */     }
/*      */ 
/*      */     public boolean containsEntry(Object key, Object value)
/*      */     {
/* 1434 */       Collection values = get(key);
/* 1435 */       return values.contains(value);
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key) {
/* 1439 */       return this.fromMultimap.containsKey(key);
/*      */     }
/*      */ 
/*      */     public boolean containsValue(Object value) {
/* 1443 */       return values().contains(value);
/*      */     }
/*      */ 
/*      */     public Collection<Map.Entry<K, V2>> entries()
/*      */     {
/* 1449 */       if (this.entries == null) {
/* 1450 */         Collection es = new TransformedEntries(this.transformer);
/* 1451 */         this.entries = es;
/* 1452 */         return es;
/*      */       }
/* 1454 */       return this.entries;
/*      */     }
/*      */ 
/*      */     public Collection<V2> get(K key)
/*      */     {
/* 1501 */       return transform(key, this.fromMultimap.get(key));
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 1505 */       return this.fromMultimap.isEmpty();
/*      */     }
/*      */ 
/*      */     public Set<K> keySet() {
/* 1509 */       return this.fromMultimap.keySet();
/*      */     }
/*      */ 
/*      */     public Multiset<K> keys() {
/* 1513 */       return this.fromMultimap.keys();
/*      */     }
/*      */ 
/*      */     public boolean put(K key, V2 value) {
/* 1517 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean putAll(K key, Iterable<? extends V2> values) {
/* 1521 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean putAll(Multimap<? extends K, ? extends V2> multimap)
/*      */     {
/* 1526 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean remove(Object key, Object value)
/*      */     {
/* 1531 */       return get(key).remove(value);
/*      */     }
/*      */ 
/*      */     public Collection<V2> removeAll(Object key)
/*      */     {
/* 1536 */       return transform(key, this.fromMultimap.removeAll(key));
/*      */     }
/*      */ 
/*      */     public Collection<V2> replaceValues(K key, Iterable<? extends V2> values)
/*      */     {
/* 1541 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1545 */       return this.fromMultimap.size();
/*      */     }
/*      */ 
/*      */     public Collection<V2> values()
/*      */     {
/* 1551 */       if (this.values == null) {
/* 1552 */         Collection vs = Collections2.transform(this.fromMultimap.entries(), new Function()
/*      */         {
/*      */           public V2 apply(Map.Entry<K, V1> entry)
/*      */           {
/* 1556 */             return Multimaps.TransformedEntriesMultimap.this.transformer.transformEntry(entry.getKey(), entry.getValue());
/*      */           }
/*      */         });
/* 1560 */         this.values = vs;
/* 1561 */         return vs;
/*      */       }
/* 1563 */       return this.values;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object obj) {
/* 1567 */       if ((obj instanceof Multimap)) {
/* 1568 */         Multimap other = (Multimap)obj;
/* 1569 */         return asMap().equals(other.asMap());
/*      */       }
/* 1571 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/* 1575 */       return asMap().hashCode();
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1579 */       return asMap().toString();
/*      */     }
/*      */ 
/*      */     private class TransformedEntries extends Collections2.TransformedCollection<Map.Entry<K, V1>, Map.Entry<K, V2>>
/*      */     {
/*      */       TransformedEntries()
/*      */       {
/* 1462 */         super(new Function()
/*      */         {
/*      */           public Map.Entry<K, V2> apply(final Map.Entry<K, V1> entry) {
/* 1465 */             return new AbstractMapEntry()
/*      */             {
/*      */               public K getKey() {
/* 1468 */                 return entry.getKey();
/*      */               }
/*      */ 
/*      */               public V2 getValue() {
/* 1472 */                 return Multimaps.TransformedEntriesMultimap.TransformedEntries.1.this.val$transformer.transformEntry(entry.getKey(), entry.getValue());
/*      */               }
/*      */             };
/*      */           }
/*      */         });
/*      */       }
/*      */ 
/*      */       public boolean contains(Object o)
/*      */       {
/* 1481 */         if ((o instanceof Map.Entry)) {
/* 1482 */           Map.Entry entry = (Map.Entry)o;
/* 1483 */           return Multimaps.TransformedEntriesMultimap.this.containsEntry(entry.getKey(), entry.getValue());
/*      */         }
/* 1485 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean remove(Object o)
/*      */       {
/* 1490 */         if ((o instanceof Map.Entry)) {
/* 1491 */           Map.Entry entry = (Map.Entry)o;
/* 1492 */           Collection values = Multimaps.TransformedEntriesMultimap.this.get(entry.getKey());
/* 1493 */           return values.remove(entry.getValue());
/*      */         }
/* 1495 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class MapMultimap<K, V>
/*      */     implements SetMultimap<K, V>, Serializable
/*      */   {
/*      */     final Map<K, V> map;
/*      */     transient Map<K, Collection<V>> asMap;
/* 1181 */     private static final Joiner.MapJoiner JOINER = Joiner.on("], ").withKeyValueSeparator("=[").useForNull("null");
/*      */     private static final long serialVersionUID = 7845222491160860175L;
/*      */ 
/*      */     MapMultimap(Map<K, V> map)
/*      */     {
/* 1033 */       this.map = ((Map)Preconditions.checkNotNull(map));
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 1038 */       return this.map.size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 1043 */       return this.map.isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key)
/*      */     {
/* 1048 */       return this.map.containsKey(key);
/*      */     }
/*      */ 
/*      */     public boolean containsValue(Object value)
/*      */     {
/* 1053 */       return this.map.containsValue(value);
/*      */     }
/*      */ 
/*      */     public boolean containsEntry(Object key, Object value)
/*      */     {
/* 1058 */       return this.map.entrySet().contains(Maps.immutableEntry(key, value));
/*      */     }
/*      */ 
/*      */     public Set<V> get(final K key)
/*      */     {
/* 1063 */       return new AbstractSet() {
/*      */         public Iterator<V> iterator() {
/* 1065 */           return new Iterator()
/*      */           {
/*      */             int i;
/*      */ 
/*      */             public boolean hasNext() {
/* 1070 */               return (this.i == 0) && (Multimaps.MapMultimap.this.map.containsKey(Multimaps.MapMultimap.1.this.val$key));
/*      */             }
/*      */ 
/*      */             public V next()
/*      */             {
/* 1075 */               if (!hasNext()) {
/* 1076 */                 throw new NoSuchElementException();
/*      */               }
/* 1078 */               this.i += 1;
/* 1079 */               return Multimaps.MapMultimap.this.map.get(Multimaps.MapMultimap.1.this.val$key);
/*      */             }
/*      */ 
/*      */             public void remove()
/*      */             {
/* 1084 */               Preconditions.checkState(this.i == 1);
/* 1085 */               this.i = -1;
/* 1086 */               Multimaps.MapMultimap.this.map.remove(Multimaps.MapMultimap.1.this.val$key);
/*      */             }
/*      */           };
/*      */         }
/*      */ 
/*      */         public int size() {
/* 1092 */           return Multimaps.MapMultimap.this.map.containsKey(key) ? 1 : 0;
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public boolean put(K key, V value)
/*      */     {
/* 1099 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean putAll(K key, Iterable<? extends V> values)
/*      */     {
/* 1104 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*      */     {
/* 1109 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Set<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/* 1114 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean remove(Object key, Object value)
/*      */     {
/* 1119 */       return this.map.entrySet().remove(Maps.immutableEntry(key, value));
/*      */     }
/*      */ 
/*      */     public Set<V> removeAll(Object key)
/*      */     {
/* 1124 */       Set values = new HashSet(2);
/* 1125 */       if (!this.map.containsKey(key)) {
/* 1126 */         return values;
/*      */       }
/* 1128 */       values.add(this.map.remove(key));
/* 1129 */       return values;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 1134 */       this.map.clear();
/*      */     }
/*      */ 
/*      */     public Set<K> keySet()
/*      */     {
/* 1139 */       return this.map.keySet();
/*      */     }
/*      */ 
/*      */     public Multiset<K> keys()
/*      */     {
/* 1144 */       return Multisets.forSet(this.map.keySet());
/*      */     }
/*      */ 
/*      */     public Collection<V> values()
/*      */     {
/* 1149 */       return this.map.values();
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<K, V>> entries()
/*      */     {
/* 1154 */       return this.map.entrySet();
/*      */     }
/*      */ 
/*      */     public Map<K, Collection<V>> asMap()
/*      */     {
/* 1159 */       Map result = this.asMap;
/* 1160 */       if (result == null) {
/* 1161 */         this.asMap = (result = new AsMap());
/*      */       }
/* 1163 */       return result;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/* 1167 */       if (object == this) {
/* 1168 */         return true;
/*      */       }
/* 1170 */       if ((object instanceof Multimap)) {
/* 1171 */         Multimap that = (Multimap)object;
/* 1172 */         return (size() == that.size()) && (asMap().equals(that.asMap()));
/*      */       }
/* 1174 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/* 1178 */       return this.map.hashCode();
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/* 1185 */       if (this.map.isEmpty()) {
/* 1186 */         return "{}";
/*      */       }
/* 1188 */       StringBuilder builder = Collections2.newStringBuilderForCollection(this.map.size()).append('{');
/*      */ 
/* 1190 */       JOINER.appendTo(builder, this.map);
/* 1191 */       return "]}";
/*      */     }
/*      */ 
/*      */     class AsMap extends Maps.ImprovedAbstractMap<K, Collection<V>>
/*      */     {
/*      */       AsMap()
/*      */       {
/*      */       }
/*      */ 
/*      */       protected Set<Map.Entry<K, Collection<V>>> createEntrySet()
/*      */       {
/* 1250 */         return new Multimaps.MapMultimap.AsMapEntries(Multimaps.MapMultimap.this);
/*      */       }
/*      */ 
/*      */       public boolean containsKey(Object key)
/*      */       {
/* 1256 */         return Multimaps.MapMultimap.this.map.containsKey(key);
/*      */       }
/*      */ 
/*      */       public Collection<V> get(Object key)
/*      */       {
/* 1261 */         Collection collection = Multimaps.MapMultimap.this.get(key);
/* 1262 */         return collection.isEmpty() ? null : collection;
/*      */       }
/*      */ 
/*      */       public Collection<V> remove(Object key) {
/* 1266 */         Collection collection = Multimaps.MapMultimap.this.removeAll(key);
/* 1267 */         return collection.isEmpty() ? null : collection;
/*      */       }
/*      */     }
/*      */ 
/*      */     class AsMapEntries extends AbstractSet<Map.Entry<K, Collection<V>>>
/*      */     {
/*      */       AsMapEntries()
/*      */       {
/*      */       }
/*      */ 
/*      */       public int size()
/*      */       {
/* 1197 */         return Multimaps.MapMultimap.this.map.size();
/*      */       }
/*      */ 
/*      */       public Iterator<Map.Entry<K, Collection<V>>> iterator() {
/* 1201 */         return new TransformedIterator(Multimaps.MapMultimap.this.map.keySet().iterator())
/*      */         {
/*      */           Map.Entry<K, Collection<V>> transform(final K key) {
/* 1204 */             return new AbstractMapEntry()
/*      */             {
/*      */               public K getKey() {
/* 1207 */                 return key;
/*      */               }
/*      */ 
/*      */               public Collection<V> getValue()
/*      */               {
/* 1212 */                 return Multimaps.MapMultimap.this.get(key);
/*      */               }
/*      */             };
/*      */           }
/*      */         };
/*      */       }
/*      */ 
/*      */       public boolean contains(Object o) {
/* 1220 */         if (!(o instanceof Map.Entry)) {
/* 1221 */           return false;
/*      */         }
/* 1223 */         Map.Entry entry = (Map.Entry)o;
/* 1224 */         if (!(entry.getValue() instanceof Set)) {
/* 1225 */           return false;
/*      */         }
/* 1227 */         Set set = (Set)entry.getValue();
/* 1228 */         return (set.size() == 1) && (Multimaps.MapMultimap.this.containsEntry(entry.getKey(), set.iterator().next()));
/*      */       }
/*      */ 
/*      */       public boolean remove(Object o)
/*      */       {
/* 1233 */         if (!(o instanceof Map.Entry)) {
/* 1234 */           return false;
/*      */         }
/* 1236 */         Map.Entry entry = (Map.Entry)o;
/* 1237 */         if (!(entry.getValue() instanceof Set)) {
/* 1238 */           return false;
/*      */         }
/* 1240 */         Set set = (Set)entry.getValue();
/* 1241 */         return (set.size() == 1) && (Multimaps.MapMultimap.this.map.entrySet().remove(Maps.immutableEntry(entry.getKey(), set.iterator().next())));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static class UnmodifiableAsMapEntries<K, V> extends ForwardingSet<Map.Entry<K, Collection<V>>>
/*      */   {
/*      */     private final Set<Map.Entry<K, Collection<V>>> delegate;
/*      */ 
/*      */     UnmodifiableAsMapEntries(Set<Map.Entry<K, Collection<V>>> delegate)
/*      */     {
/*  965 */       this.delegate = delegate;
/*      */     }
/*      */ 
/*      */     protected Set<Map.Entry<K, Collection<V>>> delegate() {
/*  969 */       return this.delegate;
/*      */     }
/*      */ 
/*      */     public Iterator<Map.Entry<K, Collection<V>>> iterator() {
/*  973 */       final Iterator iterator = this.delegate.iterator();
/*  974 */       return new ForwardingIterator() {
/*      */         protected Iterator<Map.Entry<K, Collection<V>>> delegate() {
/*  976 */           return iterator;
/*      */         }
/*      */         public Map.Entry<K, Collection<V>> next() {
/*  979 */           return Multimaps.unmodifiableAsMapEntry((Map.Entry)iterator.next());
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public Object[] toArray() {
/*  985 */       return standardToArray();
/*      */     }
/*      */ 
/*      */     public <T> T[] toArray(T[] array) {
/*  989 */       return standardToArray(array);
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/*  993 */       return Maps.containsEntryImpl(delegate(), o);
/*      */     }
/*      */ 
/*      */     public boolean containsAll(Collection<?> c) {
/*  997 */       return standardContainsAll(c);
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/* 1001 */       return standardEquals(object);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class UnmodifiableSortedSetMultimap<K, V> extends Multimaps.UnmodifiableSetMultimap<K, V>
/*      */     implements SortedSetMultimap<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     UnmodifiableSortedSetMultimap(SortedSetMultimap<K, V> delegate)
/*      */     {
/*  709 */       super();
/*      */     }
/*      */     public SortedSetMultimap<K, V> delegate() {
/*  712 */       return (SortedSetMultimap)super.delegate();
/*      */     }
/*      */     public SortedSet<V> get(K key) {
/*  715 */       return Collections.unmodifiableSortedSet(delegate().get(key));
/*      */     }
/*      */     public SortedSet<V> removeAll(Object key) {
/*  718 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public SortedSet<V> replaceValues(K key, Iterable<? extends V> values) {
/*  722 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Comparator<? super V> valueComparator() {
/*  726 */       return delegate().valueComparator();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class UnmodifiableSetMultimap<K, V> extends Multimaps.UnmodifiableMultimap<K, V>
/*      */     implements SetMultimap<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     UnmodifiableSetMultimap(SetMultimap<K, V> delegate)
/*      */     {
/*  681 */       super();
/*      */     }
/*      */     public SetMultimap<K, V> delegate() {
/*  684 */       return (SetMultimap)super.delegate();
/*      */     }
/*      */ 
/*      */     public Set<V> get(K key)
/*      */     {
/*  691 */       return Collections.unmodifiableSet(delegate().get(key));
/*      */     }
/*      */     public Set<Map.Entry<K, V>> entries() {
/*  694 */       return Maps.unmodifiableEntrySet(delegate().entries());
/*      */     }
/*      */     public Set<V> removeAll(Object key) {
/*  697 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Set<V> replaceValues(K key, Iterable<? extends V> values) {
/*  701 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class UnmodifiableListMultimap<K, V> extends Multimaps.UnmodifiableMultimap<K, V>
/*      */     implements ListMultimap<K, V>
/*      */   {
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     UnmodifiableListMultimap(ListMultimap<K, V> delegate)
/*      */     {
/*  660 */       super();
/*      */     }
/*      */     public ListMultimap<K, V> delegate() {
/*  663 */       return (ListMultimap)super.delegate();
/*      */     }
/*      */     public List<V> get(K key) {
/*  666 */       return Collections.unmodifiableList(delegate().get(key));
/*      */     }
/*      */     public List<V> removeAll(Object key) {
/*  669 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public List<V> replaceValues(K key, Iterable<? extends V> values) {
/*  673 */       throw new UnsupportedOperationException();
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class UnmodifiableAsMapValues<V> extends ForwardingCollection<Collection<V>>
/*      */   {
/*      */     final Collection<Collection<V>> delegate;
/*      */ 
/*      */     UnmodifiableAsMapValues(Collection<Collection<V>> delegate)
/*      */     {
/*  625 */       this.delegate = Collections.unmodifiableCollection(delegate);
/*      */     }
/*      */     protected Collection<Collection<V>> delegate() {
/*  628 */       return this.delegate;
/*      */     }
/*      */     public Iterator<Collection<V>> iterator() {
/*  631 */       final Iterator iterator = this.delegate.iterator();
/*  632 */       return new UnmodifiableIterator()
/*      */       {
/*      */         public boolean hasNext() {
/*  635 */           return iterator.hasNext();
/*      */         }
/*      */ 
/*      */         public Collection<V> next() {
/*  639 */           return Multimaps.unmodifiableValueCollection((Collection)iterator.next());
/*      */         } } ;
/*      */     }
/*      */ 
/*      */     public Object[] toArray() {
/*  644 */       return standardToArray();
/*      */     }
/*      */     public <T> T[] toArray(T[] array) {
/*  647 */       return standardToArray(array);
/*      */     }
/*      */     public boolean contains(Object o) {
/*  650 */       return standardContains(o);
/*      */     }
/*      */     public boolean containsAll(Collection<?> c) {
/*  653 */       return standardContainsAll(c);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class UnmodifiableMultimap<K, V> extends ForwardingMultimap<K, V>
/*      */     implements Serializable
/*      */   {
/*      */     final Multimap<K, V> delegate;
/*      */     transient Collection<Map.Entry<K, V>> entries;
/*      */     transient Multiset<K> keys;
/*      */     transient Set<K> keySet;
/*      */     transient Collection<V> values;
/*      */     transient Map<K, Collection<V>> map;
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     UnmodifiableMultimap(Multimap<K, V> delegate)
/*      */     {
/*  501 */       this.delegate = ((Multimap)Preconditions.checkNotNull(delegate));
/*      */     }
/*      */ 
/*      */     protected Multimap<K, V> delegate() {
/*  505 */       return this.delegate;
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  509 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Map<K, Collection<V>> asMap() {
/*  513 */       Map result = this.map;
/*  514 */       if (result == null) {
/*  515 */         final Map unmodifiableMap = Collections.unmodifiableMap(this.delegate.asMap());
/*      */ 
/*  517 */         this.map = (result = new ForwardingMap() { Set<Map.Entry<K, Collection<V>>> entrySet;
/*      */           Collection<Collection<V>> asMapValues;
/*      */ 
/*  519 */           protected Map<K, Collection<V>> delegate() { return unmodifiableMap; }
/*      */ 
/*      */ 
/*      */           public Set<Map.Entry<K, Collection<V>>> entrySet()
/*      */           {
/*  525 */             Set result = this.entrySet;
/*  526 */             return result == null ? (this.entrySet = Multimaps.unmodifiableAsMapEntries(unmodifiableMap.entrySet())) : result;
/*      */           }
/*      */ 
/*      */           public Collection<V> get(Object key)
/*      */           {
/*  533 */             Collection collection = (Collection)unmodifiableMap.get(key);
/*  534 */             return collection == null ? null : Multimaps.unmodifiableValueCollection(collection);
/*      */           }
/*      */ 
/*      */           public Collection<Collection<V>> values()
/*      */           {
/*  541 */             Collection result = this.asMapValues;
/*  542 */             return result == null ? (this.asMapValues = new Multimaps.UnmodifiableAsMapValues(unmodifiableMap.values())) : result;
/*      */           }
/*      */ 
/*      */           public boolean containsValue(Object o)
/*      */           {
/*  549 */             return values().contains(o);
/*      */           }
/*      */         });
/*      */       }
/*  553 */       return result;
/*      */     }
/*      */ 
/*      */     public Collection<Map.Entry<K, V>> entries() {
/*  557 */       Collection result = this.entries;
/*  558 */       if (result == null) {
/*  559 */         this.entries = (result = Multimaps.unmodifiableEntries(this.delegate.entries()));
/*      */       }
/*  561 */       return result;
/*      */     }
/*      */ 
/*      */     public Collection<V> get(K key) {
/*  565 */       return Multimaps.unmodifiableValueCollection(this.delegate.get(key));
/*      */     }
/*      */ 
/*      */     public Multiset<K> keys() {
/*  569 */       Multiset result = this.keys;
/*  570 */       if (result == null) {
/*  571 */         this.keys = (result = Multisets.unmodifiableMultiset(this.delegate.keys()));
/*      */       }
/*  573 */       return result;
/*      */     }
/*      */ 
/*      */     public Set<K> keySet() {
/*  577 */       Set result = this.keySet;
/*  578 */       if (result == null) {
/*  579 */         this.keySet = (result = Collections.unmodifiableSet(this.delegate.keySet()));
/*      */       }
/*  581 */       return result;
/*      */     }
/*      */ 
/*      */     public boolean put(K key, V value) {
/*  585 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean putAll(K key, Iterable<? extends V> values) {
/*  589 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*      */     {
/*  594 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean remove(Object key, Object value) {
/*  598 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Collection<V> removeAll(Object key) {
/*  602 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Collection<V> replaceValues(K key, Iterable<? extends V> values)
/*      */     {
/*  607 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Collection<V> values() {
/*  611 */       Collection result = this.values;
/*  612 */       if (result == null) {
/*  613 */         this.values = (result = Collections.unmodifiableCollection(this.delegate.values()));
/*      */       }
/*  615 */       return result;
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CustomSortedSetMultimap<K, V> extends AbstractSortedSetMultimap<K, V>
/*      */   {
/*      */     transient Supplier<? extends SortedSet<V>> factory;
/*      */     transient Comparator<? super V> valueComparator;
/*      */ 
/*      */     @GwtIncompatible("not needed in emulated source")
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     CustomSortedSetMultimap(Map<K, Collection<V>> map, Supplier<? extends SortedSet<V>> factory)
/*      */     {
/*  360 */       super();
/*  361 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*  362 */       this.valueComparator = ((SortedSet)factory.get()).comparator();
/*      */     }
/*      */ 
/*      */     protected SortedSet<V> createCollection() {
/*  366 */       return (SortedSet)this.factory.get();
/*      */     }
/*      */ 
/*      */     public Comparator<? super V> valueComparator() {
/*  370 */       return this.valueComparator;
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream) throws IOException
/*      */     {
/*  376 */       stream.defaultWriteObject();
/*  377 */       stream.writeObject(this.factory);
/*  378 */       stream.writeObject(backingMap());
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  385 */       stream.defaultReadObject();
/*  386 */       this.factory = ((Supplier)stream.readObject());
/*  387 */       this.valueComparator = ((SortedSet)this.factory.get()).comparator();
/*  388 */       Map map = (Map)stream.readObject();
/*  389 */       setMap(map);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CustomSetMultimap<K, V> extends AbstractSetMultimap<K, V>
/*      */   {
/*      */     transient Supplier<? extends Set<V>> factory;
/*      */ 
/*      */     @GwtIncompatible("not needed in emulated source")
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     CustomSetMultimap(Map<K, Collection<V>> map, Supplier<? extends Set<V>> factory)
/*      */     {
/*  281 */       super();
/*  282 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*      */     }
/*      */ 
/*      */     protected Set<V> createCollection() {
/*  286 */       return (Set)this.factory.get();
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream) throws IOException
/*      */     {
/*  292 */       stream.defaultWriteObject();
/*  293 */       stream.writeObject(this.factory);
/*  294 */       stream.writeObject(backingMap());
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  301 */       stream.defaultReadObject();
/*  302 */       this.factory = ((Supplier)stream.readObject());
/*  303 */       Map map = (Map)stream.readObject();
/*  304 */       setMap(map);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CustomListMultimap<K, V> extends AbstractListMultimap<K, V>
/*      */   {
/*      */     transient Supplier<? extends List<V>> factory;
/*      */ 
/*      */     @GwtIncompatible("java serialization not supported")
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     CustomListMultimap(Map<K, Collection<V>> map, Supplier<? extends List<V>> factory)
/*      */     {
/*  203 */       super();
/*  204 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*      */     }
/*      */ 
/*      */     protected List<V> createCollection() {
/*  208 */       return (List)this.factory.get();
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream) throws IOException
/*      */     {
/*  214 */       stream.defaultWriteObject();
/*  215 */       stream.writeObject(this.factory);
/*  216 */       stream.writeObject(backingMap());
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  223 */       stream.defaultReadObject();
/*  224 */       this.factory = ((Supplier)stream.readObject());
/*  225 */       Map map = (Map)stream.readObject();
/*  226 */       setMap(map);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class CustomMultimap<K, V> extends AbstractMultimap<K, V>
/*      */   {
/*      */     transient Supplier<? extends Collection<V>> factory;
/*      */ 
/*      */     @GwtIncompatible("java serialization not supported")
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     CustomMultimap(Map<K, Collection<V>> map, Supplier<? extends Collection<V>> factory)
/*      */     {
/*  121 */       super();
/*  122 */       this.factory = ((Supplier)Preconditions.checkNotNull(factory));
/*      */     }
/*      */ 
/*      */     protected Collection<V> createCollection() {
/*  126 */       return (Collection)this.factory.get();
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectOutputStream")
/*      */     private void writeObject(ObjectOutputStream stream)
/*      */       throws IOException
/*      */     {
/*  135 */       stream.defaultWriteObject();
/*  136 */       stream.writeObject(this.factory);
/*  137 */       stream.writeObject(backingMap());
/*      */     }
/*      */ 
/*      */     @GwtIncompatible("java.io.ObjectInputStream")
/*      */     private void readObject(ObjectInputStream stream)
/*      */       throws IOException, ClassNotFoundException
/*      */     {
/*  144 */       stream.defaultReadObject();
/*  145 */       this.factory = ((Supplier)stream.readObject());
/*  146 */       Map map = (Map)stream.readObject();
/*  147 */       setMap(map);
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Multimaps
 * JD-Core Version:    0.6.2
 */