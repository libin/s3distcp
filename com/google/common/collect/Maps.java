/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.Beta;
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.annotations.GwtIncompatible;
/*      */ import com.google.common.base.Equivalence;
/*      */ import com.google.common.base.Equivalences;
/*      */ import com.google.common.base.Function;
/*      */ import com.google.common.base.Joiner;
/*      */ import com.google.common.base.Joiner.MapJoiner;
/*      */ import com.google.common.base.Objects;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import com.google.common.base.Predicates;
/*      */ import java.io.Serializable;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.EnumMap;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashMap;
/*      */ import java.util.IdentityHashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.NavigableMap;
/*      */ import java.util.NavigableSet;
/*      */ import java.util.Properties;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.SortedSet;
/*      */ import java.util.TreeMap;
/*      */ import java.util.concurrent.ConcurrentMap;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible(emulated=true)
/*      */ public final class Maps
/*      */ {
/* 2141 */   static final Joiner.MapJoiner STANDARD_JOINER = Collections2.STANDARD_JOINER.withKeyValueSeparator("=");
/*      */ 
/*      */   public static <K, V> HashMap<K, V> newHashMap()
/*      */   {
/*   93 */     return new HashMap();
/*      */   }
/*      */ 
/*      */   public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize)
/*      */   {
/*  111 */     return new HashMap(capacity(expectedSize));
/*      */   }
/*      */ 
/*      */   static int capacity(int expectedSize)
/*      */   {
/*  120 */     if (expectedSize < 3) {
/*  121 */       Preconditions.checkArgument(expectedSize >= 0);
/*  122 */       return expectedSize + 1;
/*      */     }
/*  124 */     if (expectedSize < 1073741824) {
/*  125 */       return expectedSize + expectedSize / 3;
/*      */     }
/*  127 */     return 2147483647;
/*      */   }
/*      */ 
/*      */   public static <K, V> HashMap<K, V> newHashMap(Map<? extends K, ? extends V> map)
/*      */   {
/*  146 */     return new HashMap(map);
/*      */   }
/*      */ 
/*      */   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap()
/*      */   {
/*  159 */     return new LinkedHashMap();
/*      */   }
/*      */ 
/*      */   public static <K, V> LinkedHashMap<K, V> newLinkedHashMap(Map<? extends K, ? extends V> map)
/*      */   {
/*  175 */     return new LinkedHashMap(map);
/*      */   }
/*      */ 
/*      */   public static <K, V> ConcurrentMap<K, V> newConcurrentMap()
/*      */   {
/*  194 */     return new MapMaker().makeMap();
/*      */   }
/*      */ 
/*      */   public static <K extends Comparable, V> TreeMap<K, V> newTreeMap()
/*      */   {
/*  207 */     return new TreeMap();
/*      */   }
/*      */ 
/*      */   public static <K, V> TreeMap<K, V> newTreeMap(SortedMap<K, ? extends V> map)
/*      */   {
/*  223 */     return new TreeMap(map);
/*      */   }
/*      */ 
/*      */   public static <C, K extends C, V> TreeMap<K, V> newTreeMap(@Nullable Comparator<C> comparator)
/*      */   {
/*  243 */     return new TreeMap(comparator);
/*      */   }
/*      */ 
/*      */   public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Class<K> type)
/*      */   {
/*  253 */     return new EnumMap((Class)Preconditions.checkNotNull(type));
/*      */   }
/*      */ 
/*      */   public static <K extends Enum<K>, V> EnumMap<K, V> newEnumMap(Map<K, ? extends V> map)
/*      */   {
/*  267 */     return new EnumMap(map);
/*      */   }
/*      */ 
/*      */   public static <K, V> IdentityHashMap<K, V> newIdentityHashMap()
/*      */   {
/*  276 */     return new IdentityHashMap();
/*      */   }
/*      */ 
/*      */   public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right)
/*      */   {
/*  298 */     if ((left instanceof SortedMap)) {
/*  299 */       SortedMap sortedLeft = (SortedMap)left;
/*  300 */       SortedMapDifference result = difference(sortedLeft, right);
/*  301 */       return result;
/*      */     }
/*  303 */     return difference(left, right, Equivalences.equals());
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <K, V> MapDifference<K, V> difference(Map<? extends K, ? extends V> left, Map<? extends K, ? extends V> right, Equivalence<? super V> valueEquivalence)
/*      */   {
/*  329 */     Preconditions.checkNotNull(valueEquivalence);
/*      */ 
/*  331 */     Map onlyOnLeft = newHashMap();
/*  332 */     Map onlyOnRight = new HashMap(right);
/*  333 */     Map onBoth = newHashMap();
/*  334 */     Map differences = newHashMap();
/*  335 */     boolean eq = true;
/*      */ 
/*  337 */     for (Map.Entry entry : left.entrySet()) {
/*  338 */       Object leftKey = entry.getKey();
/*  339 */       Object leftValue = entry.getValue();
/*  340 */       if (right.containsKey(leftKey)) {
/*  341 */         Object rightValue = onlyOnRight.remove(leftKey);
/*  342 */         if (valueEquivalence.equivalent(leftValue, rightValue)) {
/*  343 */           onBoth.put(leftKey, leftValue);
/*      */         } else {
/*  345 */           eq = false;
/*  346 */           differences.put(leftKey, ValueDifferenceImpl.create(leftValue, rightValue));
/*      */         }
/*      */       }
/*      */       else {
/*  350 */         eq = false;
/*  351 */         onlyOnLeft.put(leftKey, leftValue);
/*      */       }
/*      */     }
/*      */ 
/*  355 */     boolean areEqual = (eq) && (onlyOnRight.isEmpty());
/*  356 */     return mapDifference(areEqual, onlyOnLeft, onlyOnRight, onBoth, differences);
/*      */   }
/*      */ 
/*      */   private static <K, V> MapDifference<K, V> mapDifference(boolean areEqual, Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences)
/*      */   {
/*  363 */     return new MapDifferenceImpl(areEqual, Collections.unmodifiableMap(onlyOnLeft), Collections.unmodifiableMap(onlyOnRight), Collections.unmodifiableMap(onBoth), Collections.unmodifiableMap(differences));
/*      */   }
/*      */ 
/*      */   public static <K, V> SortedMapDifference<K, V> difference(SortedMap<K, ? extends V> left, Map<? extends K, ? extends V> right)
/*      */   {
/*  514 */     Preconditions.checkNotNull(left);
/*  515 */     Preconditions.checkNotNull(right);
/*  516 */     Comparator comparator = orNaturalOrder(left.comparator());
/*  517 */     SortedMap onlyOnLeft = newTreeMap(comparator);
/*  518 */     SortedMap onlyOnRight = newTreeMap(comparator);
/*  519 */     onlyOnRight.putAll(right);
/*  520 */     SortedMap onBoth = newTreeMap(comparator);
/*  521 */     SortedMap differences = newTreeMap(comparator);
/*      */ 
/*  523 */     boolean eq = true;
/*      */ 
/*  525 */     for (Map.Entry entry : left.entrySet()) {
/*  526 */       Object leftKey = entry.getKey();
/*  527 */       Object leftValue = entry.getValue();
/*  528 */       if (right.containsKey(leftKey)) {
/*  529 */         Object rightValue = onlyOnRight.remove(leftKey);
/*  530 */         if (Objects.equal(leftValue, rightValue)) {
/*  531 */           onBoth.put(leftKey, leftValue);
/*      */         } else {
/*  533 */           eq = false;
/*  534 */           differences.put(leftKey, ValueDifferenceImpl.create(leftValue, rightValue));
/*      */         }
/*      */       }
/*      */       else {
/*  538 */         eq = false;
/*  539 */         onlyOnLeft.put(leftKey, leftValue);
/*      */       }
/*      */     }
/*      */ 
/*  543 */     boolean areEqual = (eq) && (onlyOnRight.isEmpty());
/*  544 */     return sortedMapDifference(areEqual, onlyOnLeft, onlyOnRight, onBoth, differences);
/*      */   }
/*      */ 
/*      */   private static <K, V> SortedMapDifference<K, V> sortedMapDifference(boolean areEqual, SortedMap<K, V> onlyOnLeft, SortedMap<K, V> onlyOnRight, SortedMap<K, V> onBoth, SortedMap<K, MapDifference.ValueDifference<V>> differences)
/*      */   {
/*  551 */     return new SortedMapDifferenceImpl(areEqual, Collections.unmodifiableSortedMap(onlyOnLeft), Collections.unmodifiableSortedMap(onlyOnRight), Collections.unmodifiableSortedMap(onBoth), Collections.unmodifiableSortedMap(differences));
/*      */   }
/*      */ 
/*      */   static <E> Comparator<? super E> orNaturalOrder(@Nullable Comparator<? super E> comparator)
/*      */   {
/*  591 */     if (comparator != null) {
/*  592 */       return comparator;
/*      */     }
/*  594 */     return Ordering.natural();
/*      */   }
/*      */ 
/*      */   public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterable<V> values, Function<? super V, K> keyFunction)
/*      */   {
/*  612 */     return uniqueIndex(values.iterator(), keyFunction);
/*      */   }
/*      */ 
/*      */   public static <K, V> ImmutableMap<K, V> uniqueIndex(Iterator<V> values, Function<? super V, K> keyFunction)
/*      */   {
/*  632 */     Preconditions.checkNotNull(keyFunction);
/*  633 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/*  634 */     while (values.hasNext()) {
/*  635 */       Object value = values.next();
/*  636 */       builder.put(keyFunction.apply(value), value);
/*      */     }
/*  638 */     return builder.build();
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("java.util.Properties")
/*      */   public static ImmutableMap<String, String> fromProperties(Properties properties)
/*      */   {
/*  657 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/*      */ 
/*  659 */     for (Enumeration e = properties.propertyNames(); e.hasMoreElements(); ) {
/*  660 */       String key = (String)e.nextElement();
/*  661 */       builder.put(key, properties.getProperty(key));
/*      */     }
/*      */ 
/*  664 */     return builder.build();
/*      */   }
/*      */ 
/*      */   @GwtCompatible(serializable=true)
/*      */   public static <K, V> Map.Entry<K, V> immutableEntry(@Nullable K key, @Nullable V value)
/*      */   {
/*  679 */     return new ImmutableEntry(key, value);
/*      */   }
/*      */ 
/*      */   static <K, V> Set<Map.Entry<K, V>> unmodifiableEntrySet(Set<Map.Entry<K, V>> entrySet)
/*      */   {
/*  692 */     return new UnmodifiableEntrySet(Collections.unmodifiableSet(entrySet));
/*      */   }
/*      */ 
/*      */   static <K, V> Map.Entry<K, V> unmodifiableEntry(Map.Entry<K, V> entry)
/*      */   {
/*  706 */     Preconditions.checkNotNull(entry);
/*  707 */     return new AbstractMapEntry() {
/*      */       public K getKey() {
/*  709 */         return this.val$entry.getKey();
/*      */       }
/*      */ 
/*      */       public V getValue() {
/*  713 */         return this.val$entry.getValue();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   public static <K, V> BiMap<K, V> synchronizedBiMap(BiMap<K, V> bimap)
/*      */   {
/*  831 */     return Synchronized.biMap(bimap, null);
/*      */   }
/*      */ 
/*      */   public static <K, V> BiMap<K, V> unmodifiableBiMap(BiMap<? extends K, ? extends V> bimap)
/*      */   {
/*  849 */     return new UnmodifiableBiMap(bimap, null);
/*      */   }
/*      */ 
/*      */   public static <K, V1, V2> Map<K, V2> transformValues(Map<K, V1> fromMap, Function<? super V1, V2> function)
/*      */   {
/*  932 */     Preconditions.checkNotNull(function);
/*  933 */     EntryTransformer transformer = new EntryTransformer()
/*      */     {
/*      */       public V2 transformEntry(K key, V1 value)
/*      */       {
/*  937 */         return this.val$function.apply(value);
/*      */       }
/*      */     };
/*  940 */     return transformEntries(fromMap, transformer);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <K, V1, V2> SortedMap<K, V2> transformValues(SortedMap<K, V1> fromMap, Function<? super V1, V2> function)
/*      */   {
/*  985 */     Preconditions.checkNotNull(function);
/*  986 */     EntryTransformer transformer = new EntryTransformer()
/*      */     {
/*      */       public V2 transformEntry(K key, V1 value)
/*      */       {
/*  990 */         return this.val$function.apply(value);
/*      */       }
/*      */     };
/*  993 */     return transformEntries(fromMap, transformer);
/*      */   }
/*      */ 
/*      */   public static <K, V1, V2> Map<K, V2> transformEntries(Map<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */   {
/* 1050 */     if ((fromMap instanceof SortedMap)) {
/* 1051 */       return transformEntries((SortedMap)fromMap, transformer);
/*      */     }
/* 1053 */     return new TransformedEntriesMap(fromMap, transformer);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <K, V1, V2> SortedMap<K, V2> transformEntries(SortedMap<K, V1> fromMap, EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */   {
/* 1112 */     return new TransformedEntriesSortedMap(fromMap, transformer);
/*      */   }
/*      */ 
/*      */   public static <K, V> Map<K, V> filterKeys(Map<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*      */   {
/* 1309 */     if ((unfiltered instanceof SortedMap)) {
/* 1310 */       return filterKeys((SortedMap)unfiltered, keyPredicate);
/*      */     }
/* 1312 */     Preconditions.checkNotNull(keyPredicate);
/* 1313 */     Predicate entryPredicate = new Predicate()
/*      */     {
/*      */       public boolean apply(Map.Entry<K, V> input)
/*      */       {
/* 1317 */         return this.val$keyPredicate.apply(input.getKey());
/*      */       }
/*      */     };
/* 1320 */     return (unfiltered instanceof AbstractFilteredMap) ? filterFiltered((AbstractFilteredMap)unfiltered, entryPredicate) : new FilteredKeyMap((Map)Preconditions.checkNotNull(unfiltered), keyPredicate, entryPredicate);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <K, V> SortedMap<K, V> filterKeys(SortedMap<K, V> unfiltered, Predicate<? super K> keyPredicate)
/*      */   {
/* 1361 */     Preconditions.checkNotNull(keyPredicate);
/* 1362 */     Predicate entryPredicate = new Predicate()
/*      */     {
/*      */       public boolean apply(Map.Entry<K, V> input) {
/* 1365 */         return this.val$keyPredicate.apply(input.getKey());
/*      */       }
/*      */     };
/* 1368 */     return filterEntries(unfiltered, entryPredicate);
/*      */   }
/*      */ 
/*      */   public static <K, V> Map<K, V> filterValues(Map<K, V> unfiltered, Predicate<? super V> valuePredicate)
/*      */   {
/* 1402 */     if ((unfiltered instanceof SortedMap)) {
/* 1403 */       return filterValues((SortedMap)unfiltered, valuePredicate);
/*      */     }
/* 1405 */     Preconditions.checkNotNull(valuePredicate);
/* 1406 */     Predicate entryPredicate = new Predicate()
/*      */     {
/*      */       public boolean apply(Map.Entry<K, V> input)
/*      */       {
/* 1410 */         return this.val$valuePredicate.apply(input.getValue());
/*      */       }
/*      */     };
/* 1413 */     return filterEntries(unfiltered, entryPredicate);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <K, V> SortedMap<K, V> filterValues(SortedMap<K, V> unfiltered, Predicate<? super V> valuePredicate)
/*      */   {
/* 1450 */     Preconditions.checkNotNull(valuePredicate);
/* 1451 */     Predicate entryPredicate = new Predicate()
/*      */     {
/*      */       public boolean apply(Map.Entry<K, V> input)
/*      */       {
/* 1455 */         return this.val$valuePredicate.apply(input.getValue());
/*      */       }
/*      */     };
/* 1458 */     return filterEntries(unfiltered, entryPredicate);
/*      */   }
/*      */ 
/*      */   public static <K, V> Map<K, V> filterEntries(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */   {
/* 1492 */     if ((unfiltered instanceof SortedMap)) {
/* 1493 */       return filterEntries((SortedMap)unfiltered, entryPredicate);
/*      */     }
/* 1495 */     Preconditions.checkNotNull(entryPredicate);
/* 1496 */     return (unfiltered instanceof AbstractFilteredMap) ? filterFiltered((AbstractFilteredMap)unfiltered, entryPredicate) : new FilteredEntryMap((Map)Preconditions.checkNotNull(unfiltered), entryPredicate);
/*      */   }
/*      */ 
/*      */   @Beta
/*      */   public static <K, V> SortedMap<K, V> filterEntries(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */   {
/* 1536 */     Preconditions.checkNotNull(entryPredicate);
/* 1537 */     return (unfiltered instanceof FilteredEntrySortedMap) ? filterFiltered((FilteredEntrySortedMap)unfiltered, entryPredicate) : new FilteredEntrySortedMap((SortedMap)Preconditions.checkNotNull(unfiltered), entryPredicate);
/*      */   }
/*      */ 
/*      */   private static <K, V> Map<K, V> filterFiltered(AbstractFilteredMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */   {
/* 1548 */     Predicate predicate = Predicates.and(map.predicate, entryPredicate);
/*      */ 
/* 1550 */     return new FilteredEntryMap(map.unfiltered, predicate);
/*      */   }
/*      */ 
/*      */   private static <K, V> SortedMap<K, V> filterFiltered(FilteredEntrySortedMap<K, V> map, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */   {
/* 1694 */     Predicate predicate = Predicates.and(map.predicate, entryPredicate);
/*      */ 
/* 1696 */     return new FilteredEntrySortedMap(map.sortedMap(), predicate);
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("NavigableMap")
/*      */   public static <K, V> NavigableMap<K, V> unmodifiableNavigableMap(NavigableMap<K, V> map)
/*      */   {
/* 1925 */     Preconditions.checkNotNull(map);
/* 1926 */     if ((map instanceof UnmodifiableNavigableMap)) {
/* 1927 */       return map;
/*      */     }
/* 1929 */     return new UnmodifiableNavigableMap(map);
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   private static <K, V> Map.Entry<K, V> unmodifiableOrNull(@Nullable Map.Entry<K, V> entry) {
/* 1934 */     return entry == null ? null : unmodifiableEntry(entry);
/*      */   }
/*      */ 
/*      */   static <V> V safeGet(Map<?, V> map, Object key)
/*      */   {
/*      */     try
/*      */     {
/* 2150 */       return map.get(key); } catch (ClassCastException e) {
/*      */     }
/* 2152 */     return null;
/*      */   }
/*      */ 
/*      */   static boolean safeContainsKey(Map<?, ?> map, Object key)
/*      */   {
/*      */     try
/*      */     {
/* 2162 */       return map.containsKey(key); } catch (ClassCastException e) {
/*      */     }
/* 2164 */     return false;
/*      */   }
/*      */ 
/*      */   static <K, V> boolean containsEntryImpl(Collection<Map.Entry<K, V>> c, Object o)
/*      */   {
/* 2182 */     if (!(o instanceof Map.Entry)) {
/* 2183 */       return false;
/*      */     }
/* 2185 */     return c.contains(unmodifiableEntry((Map.Entry)o));
/*      */   }
/*      */ 
/*      */   static <K, V> boolean removeEntryImpl(Collection<Map.Entry<K, V>> c, Object o)
/*      */   {
/* 2202 */     if (!(o instanceof Map.Entry)) {
/* 2203 */       return false;
/*      */     }
/* 2205 */     return c.remove(unmodifiableEntry((Map.Entry)o));
/*      */   }
/*      */ 
/*      */   static boolean equalsImpl(Map<?, ?> map, Object object)
/*      */   {
/* 2212 */     if (map == object) {
/* 2213 */       return true;
/*      */     }
/* 2215 */     if ((object instanceof Map)) {
/* 2216 */       Map o = (Map)object;
/* 2217 */       return map.entrySet().equals(o.entrySet());
/*      */     }
/* 2219 */     return false;
/*      */   }
/*      */ 
/*      */   static int hashCodeImpl(Map<?, ?> map)
/*      */   {
/* 2226 */     return Sets.hashCodeImpl(map.entrySet());
/*      */   }
/*      */ 
/*      */   static String toStringImpl(Map<?, ?> map)
/*      */   {
/* 2233 */     StringBuilder sb = Collections2.newStringBuilderForCollection(map.size()).append('{');
/*      */ 
/* 2235 */     STANDARD_JOINER.appendTo(sb, map);
/* 2236 */     return '}';
/*      */   }
/*      */ 
/*      */   static <K, V> void putAllImpl(Map<K, V> self, Map<? extends K, ? extends V> map)
/*      */   {
/* 2244 */     for (Map.Entry entry : map.entrySet())
/* 2245 */       self.put(entry.getKey(), entry.getValue());
/*      */   }
/*      */ 
/*      */   static boolean containsKeyImpl(Map<?, ?> map, @Nullable Object key)
/*      */   {
/* 2253 */     for (Map.Entry entry : map.entrySet()) {
/* 2254 */       if (Objects.equal(entry.getKey(), key)) {
/* 2255 */         return true;
/*      */       }
/*      */     }
/* 2258 */     return false;
/*      */   }
/*      */ 
/*      */   static boolean containsValueImpl(Map<?, ?> map, @Nullable Object value)
/*      */   {
/* 2265 */     for (Map.Entry entry : map.entrySet()) {
/* 2266 */       if (Objects.equal(entry.getValue(), value)) {
/* 2267 */         return true;
/*      */       }
/*      */     }
/* 2270 */     return false;
/*      */   }
/*      */ 
/*      */   static <K, V> Iterator<K> keyIterator(Iterator<Map.Entry<K, V>> entryIterator) {
/* 2274 */     return new TransformedIterator(entryIterator)
/*      */     {
/*      */       K transform(Map.Entry<K, V> entry) {
/* 2277 */         return entry.getKey();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   @Nullable
/*      */   static <K> K keyOrNull(@Nullable Map.Entry<K, ?> entry)
/*      */   {
/* 2322 */     return entry == null ? null : entry.getKey();
/*      */   }
/*      */ 
/*      */   static <K, V> Iterator<V> valueIterator(Iterator<Map.Entry<K, V>> entryIterator)
/*      */   {
/* 2421 */     return new TransformedIterator(entryIterator)
/*      */     {
/*      */       V transform(Map.Entry<K, V> entry) {
/* 2424 */         return entry.getValue();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   static <K, V> UnmodifiableIterator<V> valueIterator(UnmodifiableIterator<Map.Entry<K, V>> entryIterator)
/*      */   {
/* 2431 */     return new UnmodifiableIterator()
/*      */     {
/*      */       public boolean hasNext() {
/* 2434 */         return this.val$entryIterator.hasNext();
/*      */       }
/*      */ 
/*      */       public V next()
/*      */       {
/* 2439 */         return ((Map.Entry)this.val$entryIterator.next()).getValue();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("NavigableMap")
/*      */   static abstract class DescendingMap<K, V> extends ForwardingMap<K, V>
/*      */     implements NavigableMap<K, V>
/*      */   {
/*      */     private transient Comparator<? super K> comparator;
/*      */     private transient Set<Map.Entry<K, V>> entrySet;
/*      */     private transient NavigableSet<K> navigableKeySet;
/*      */ 
/*      */     abstract NavigableMap<K, V> forward();
/*      */ 
/*      */     protected final Map<K, V> delegate()
/*      */     {
/* 2582 */       return forward();
/*      */     }
/*      */ 
/*      */     public Comparator<? super K> comparator()
/*      */     {
/* 2590 */       Comparator result = this.comparator;
/* 2591 */       if (result == null) {
/* 2592 */         Comparator forwardCmp = forward().comparator();
/* 2593 */         if (forwardCmp == null) {
/* 2594 */           forwardCmp = Ordering.natural();
/*      */         }
/* 2596 */         result = this.comparator = reverse(forwardCmp);
/*      */       }
/* 2598 */       return result;
/*      */     }
/*      */ 
/*      */     private static <T> Ordering<T> reverse(Comparator<T> forward)
/*      */     {
/* 2603 */       return Ordering.from(forward).reverse();
/*      */     }
/*      */ 
/*      */     public K firstKey()
/*      */     {
/* 2608 */       return forward().lastKey();
/*      */     }
/*      */ 
/*      */     public K lastKey()
/*      */     {
/* 2613 */       return forward().firstKey();
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> lowerEntry(K key)
/*      */     {
/* 2618 */       return forward().higherEntry(key);
/*      */     }
/*      */ 
/*      */     public K lowerKey(K key)
/*      */     {
/* 2623 */       return forward().higherKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> floorEntry(K key)
/*      */     {
/* 2628 */       return forward().ceilingEntry(key);
/*      */     }
/*      */ 
/*      */     public K floorKey(K key)
/*      */     {
/* 2633 */       return forward().ceilingKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> ceilingEntry(K key)
/*      */     {
/* 2638 */       return forward().floorEntry(key);
/*      */     }
/*      */ 
/*      */     public K ceilingKey(K key)
/*      */     {
/* 2643 */       return forward().floorKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> higherEntry(K key)
/*      */     {
/* 2648 */       return forward().lowerEntry(key);
/*      */     }
/*      */ 
/*      */     public K higherKey(K key)
/*      */     {
/* 2653 */       return forward().lowerKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> firstEntry()
/*      */     {
/* 2658 */       return forward().lastEntry();
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> lastEntry()
/*      */     {
/* 2663 */       return forward().firstEntry();
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> pollFirstEntry()
/*      */     {
/* 2668 */       return forward().pollLastEntry();
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> pollLastEntry()
/*      */     {
/* 2673 */       return forward().pollFirstEntry();
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> descendingMap()
/*      */     {
/* 2678 */       return forward();
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<K, V>> entrySet()
/*      */     {
/* 2685 */       Set result = this.entrySet;
/* 2686 */       return result == null ? (this.entrySet = createEntrySet()) : result;
/*      */     }
/*      */ 
/*      */     abstract Iterator<Map.Entry<K, V>> entryIterator();
/*      */ 
/*      */     Set<Map.Entry<K, V>> createEntrySet() {
/* 2692 */       return new Maps.EntrySet()
/*      */       {
/*      */         Map<K, V> map()
/*      */         {
/* 2696 */           return Maps.DescendingMap.this;
/*      */         }
/*      */ 
/*      */         public Iterator<Map.Entry<K, V>> iterator()
/*      */         {
/* 2701 */           return Maps.DescendingMap.this.entryIterator();
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public Set<K> keySet()
/*      */     {
/* 2708 */       return navigableKeySet();
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> navigableKeySet()
/*      */     {
/* 2715 */       NavigableSet result = this.navigableKeySet;
/* 2716 */       if (result == null) {
/* 2717 */         result = this.navigableKeySet = new Maps.NavigableKeySet()
/*      */         {
/*      */           NavigableMap<K, V> map() {
/* 2720 */             return Maps.DescendingMap.this;
/*      */           }
/*      */         };
/*      */       }
/* 2724 */       return result;
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> descendingKeySet()
/*      */     {
/* 2729 */       return forward().navigableKeySet();
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
/*      */     {
/* 2736 */       return forward().subMap(toKey, toInclusive, fromKey, fromInclusive).descendingMap();
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> headMap(K toKey, boolean inclusive)
/*      */     {
/* 2741 */       return forward().tailMap(toKey, inclusive).descendingMap();
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive)
/*      */     {
/* 2746 */       return forward().headMap(fromKey, inclusive).descendingMap();
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> subMap(K fromKey, K toKey)
/*      */     {
/* 2751 */       return subMap(fromKey, true, toKey, false);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> headMap(K toKey)
/*      */     {
/* 2756 */       return headMap(toKey, false);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> tailMap(K fromKey)
/*      */     {
/* 2761 */       return tailMap(fromKey, true);
/*      */     }
/*      */ 
/*      */     public Collection<V> values()
/*      */     {
/* 2766 */       return new Maps.Values()
/*      */       {
/*      */         Map<K, V> map() {
/* 2769 */           return Maps.DescendingMap.this;
/*      */         }
/*      */       };
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class EntrySet<K, V> extends AbstractSet<Map.Entry<K, V>>
/*      */   {
/*      */     abstract Map<K, V> map();
/*      */ 
/*      */     public int size()
/*      */     {
/* 2514 */       return map().size();
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 2518 */       map().clear();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/* 2522 */       if ((o instanceof Map.Entry)) {
/* 2523 */         Map.Entry entry = (Map.Entry)o;
/* 2524 */         Object key = entry.getKey();
/* 2525 */         Object value = map().get(key);
/* 2526 */         return (Objects.equal(value, entry.getValue())) && ((value != null) || (map().containsKey(key)));
/*      */       }
/*      */ 
/* 2529 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 2533 */       return map().isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o) {
/* 2537 */       if (contains(o)) {
/* 2538 */         Map.Entry entry = (Map.Entry)o;
/* 2539 */         return map().keySet().remove(entry.getKey());
/*      */       }
/* 2541 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection<?> c) {
/*      */       try {
/* 2546 */         return super.removeAll((Collection)Preconditions.checkNotNull(c));
/*      */       }
/*      */       catch (UnsupportedOperationException e) {
/* 2549 */         boolean changed = true;
/* 2550 */         for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object o = i$.next();
/* 2551 */           changed |= remove(o);
/*      */         }
/* 2553 */         return changed;
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean retainAll(Collection<?> c) {
/*      */       try {
/* 2559 */         return super.retainAll((Collection)Preconditions.checkNotNull(c));
/*      */       }
/*      */       catch (UnsupportedOperationException e) {
/* 2562 */         Set keys = Sets.newHashSetWithExpectedSize(c.size());
/* 2563 */         for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object o = i$.next();
/* 2564 */           if (contains(o)) {
/* 2565 */             Map.Entry entry = (Map.Entry)o;
/* 2566 */             keys.add(entry.getKey());
/*      */           }
/*      */         }
/* 2569 */         return map().keySet().retainAll(keys);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class Values<K, V> extends AbstractCollection<V>
/*      */   {
/*      */     abstract Map<K, V> map();
/*      */ 
/*      */     public Iterator<V> iterator()
/*      */     {
/* 2448 */       return Maps.valueIterator(map().entrySet().iterator());
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o) {
/*      */       try {
/* 2453 */         return super.remove(o);
/*      */       } catch (UnsupportedOperationException e) {
/* 2455 */         for (Map.Entry entry : map().entrySet())
/* 2456 */           if (Objects.equal(o, entry.getValue())) {
/* 2457 */             map().remove(entry.getKey());
/* 2458 */             return true;
/*      */           }
/*      */       }
/* 2461 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection<?> c)
/*      */     {
/*      */       try {
/* 2467 */         return super.removeAll((Collection)Preconditions.checkNotNull(c));
/*      */       } catch (UnsupportedOperationException e) {
/* 2469 */         Set toRemove = Sets.newHashSet();
/* 2470 */         for (Map.Entry entry : map().entrySet()) {
/* 2471 */           if (c.contains(entry.getValue())) {
/* 2472 */             toRemove.add(entry.getKey());
/*      */           }
/*      */         }
/* 2475 */         return map().keySet().removeAll(toRemove);
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean retainAll(Collection<?> c) {
/*      */       try {
/* 2481 */         return super.retainAll((Collection)Preconditions.checkNotNull(c));
/*      */       } catch (UnsupportedOperationException e) {
/* 2483 */         Set toRetain = Sets.newHashSet();
/* 2484 */         for (Map.Entry entry : map().entrySet()) {
/* 2485 */           if (c.contains(entry.getValue())) {
/* 2486 */             toRetain.add(entry.getKey());
/*      */           }
/*      */         }
/* 2489 */         return map().keySet().retainAll(toRetain);
/*      */       }
/*      */     }
/*      */ 
/*      */     public int size() {
/* 2494 */       return map().size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 2498 */       return map().isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean contains(@Nullable Object o) {
/* 2502 */       return map().containsValue(o);
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 2506 */       map().clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("NavigableMap")
/*      */   static abstract class NavigableKeySet<K, V> extends Maps.KeySet<K, V>
/*      */     implements NavigableSet<K>
/*      */   {
/*      */     abstract NavigableMap<K, V> map();
/*      */ 
/*      */     public Comparator<? super K> comparator()
/*      */     {
/* 2332 */       return map().comparator();
/*      */     }
/*      */ 
/*      */     public K first()
/*      */     {
/* 2337 */       return map().firstKey();
/*      */     }
/*      */ 
/*      */     public K last()
/*      */     {
/* 2342 */       return map().lastKey();
/*      */     }
/*      */ 
/*      */     public K lower(K e)
/*      */     {
/* 2347 */       return map().lowerKey(e);
/*      */     }
/*      */ 
/*      */     public K floor(K e)
/*      */     {
/* 2352 */       return map().floorKey(e);
/*      */     }
/*      */ 
/*      */     public K ceiling(K e)
/*      */     {
/* 2357 */       return map().ceilingKey(e);
/*      */     }
/*      */ 
/*      */     public K higher(K e)
/*      */     {
/* 2362 */       return map().higherKey(e);
/*      */     }
/*      */ 
/*      */     public K pollFirst()
/*      */     {
/* 2367 */       return Maps.keyOrNull(map().pollFirstEntry());
/*      */     }
/*      */ 
/*      */     public K pollLast()
/*      */     {
/* 2372 */       return Maps.keyOrNull(map().pollLastEntry());
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> descendingSet()
/*      */     {
/* 2377 */       return map().descendingKeySet();
/*      */     }
/*      */ 
/*      */     public Iterator<K> descendingIterator()
/*      */     {
/* 2382 */       return descendingSet().iterator();
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> subSet(K fromElement, boolean fromInclusive, K toElement, boolean toInclusive)
/*      */     {
/* 2391 */       return map().subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> headSet(K toElement, boolean inclusive)
/*      */     {
/* 2396 */       return map().headMap(toElement, inclusive).navigableKeySet();
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> tailSet(K fromElement, boolean inclusive)
/*      */     {
/* 2401 */       return map().tailMap(fromElement, inclusive).navigableKeySet();
/*      */     }
/*      */ 
/*      */     public SortedSet<K> subSet(K fromElement, K toElement)
/*      */     {
/* 2406 */       return subSet(fromElement, true, toElement, false);
/*      */     }
/*      */ 
/*      */     public SortedSet<K> headSet(K toElement)
/*      */     {
/* 2411 */       return headSet(toElement, false);
/*      */     }
/*      */ 
/*      */     public SortedSet<K> tailSet(K fromElement)
/*      */     {
/* 2416 */       return tailSet(fromElement, true);
/*      */     }
/*      */   }
/*      */ 
/*      */   static abstract class KeySet<K, V> extends AbstractSet<K>
/*      */   {
/*      */     abstract Map<K, V> map();
/*      */ 
/*      */     public Iterator<K> iterator()
/*      */     {
/* 2286 */       return Maps.keyIterator(map().entrySet().iterator());
/*      */     }
/*      */ 
/*      */     public int size() {
/* 2290 */       return map().size();
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 2294 */       return map().isEmpty();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/* 2298 */       return map().containsKey(o);
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o) {
/* 2302 */       if (contains(o)) {
/* 2303 */         map().remove(o);
/* 2304 */         return true;
/*      */       }
/* 2306 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection<?> c)
/*      */     {
/* 2312 */       return super.removeAll((Collection)Preconditions.checkNotNull(c));
/*      */     }
/*      */ 
/*      */     public void clear() {
/* 2316 */       map().clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   @GwtCompatible
/*      */   static abstract class ImprovedAbstractMap<K, V> extends AbstractMap<K, V>
/*      */   {
/*      */     private Set<Map.Entry<K, V>> entrySet;
/*      */     private Set<K> keySet;
/*      */     private Collection<V> values;
/*      */ 
/*      */     protected abstract Set<Map.Entry<K, V>> createEntrySet();
/*      */ 
/*      */     public Set<Map.Entry<K, V>> entrySet()
/*      */     {
/* 2094 */       Set result = this.entrySet;
/* 2095 */       if (result == null) {
/* 2096 */         this.entrySet = (result = createEntrySet());
/*      */       }
/* 2098 */       return result;
/*      */     }
/*      */ 
/*      */     public Set<K> keySet()
/*      */     {
/* 2104 */       Set result = this.keySet;
/* 2105 */       if (result == null) {
/* 2106 */         return this.keySet = new Maps.KeySet() {
/*      */           Map<K, V> map() {
/* 2108 */             return Maps.ImprovedAbstractMap.this;
/*      */           }
/*      */         };
/*      */       }
/* 2112 */       return result;
/*      */     }
/*      */ 
/*      */     public Collection<V> values()
/*      */     {
/* 2118 */       Collection result = this.values;
/* 2119 */       if (result == null) {
/* 2120 */         return this.values = new Maps.Values() {
/*      */           Map<K, V> map() {
/* 2122 */             return Maps.ImprovedAbstractMap.this;
/*      */           }
/*      */         };
/*      */       }
/* 2126 */       return result;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/* 2137 */       return entrySet().isEmpty();
/*      */     }
/*      */   }
/*      */ 
/*      */   @GwtIncompatible("NavigableMap")
/*      */   static class UnmodifiableNavigableMap<K, V> extends ForwardingSortedMap<K, V>
/*      */     implements NavigableMap<K, V>, Serializable
/*      */   {
/*      */     private final NavigableMap<K, V> delegate;
/*      */     private transient UnmodifiableNavigableMap<K, V> descendingMap;
/*      */ 
/*      */     UnmodifiableNavigableMap(NavigableMap<K, V> delegate)
/*      */     {
/* 1943 */       this.delegate = delegate;
/*      */     }
/*      */ 
/*      */     protected SortedMap<K, V> delegate()
/*      */     {
/* 1948 */       return Collections.unmodifiableSortedMap(this.delegate);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> lowerEntry(K key)
/*      */     {
/* 1953 */       return Maps.unmodifiableOrNull(this.delegate.lowerEntry(key));
/*      */     }
/*      */ 
/*      */     public K lowerKey(K key)
/*      */     {
/* 1958 */       return this.delegate.lowerKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> floorEntry(K key)
/*      */     {
/* 1963 */       return Maps.unmodifiableOrNull(this.delegate.floorEntry(key));
/*      */     }
/*      */ 
/*      */     public K floorKey(K key)
/*      */     {
/* 1968 */       return this.delegate.floorKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> ceilingEntry(K key)
/*      */     {
/* 1973 */       return Maps.unmodifiableOrNull(this.delegate.ceilingEntry(key));
/*      */     }
/*      */ 
/*      */     public K ceilingKey(K key)
/*      */     {
/* 1978 */       return this.delegate.ceilingKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> higherEntry(K key)
/*      */     {
/* 1983 */       return Maps.unmodifiableOrNull(this.delegate.higherEntry(key));
/*      */     }
/*      */ 
/*      */     public K higherKey(K key)
/*      */     {
/* 1988 */       return this.delegate.higherKey(key);
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> firstEntry()
/*      */     {
/* 1993 */       return Maps.unmodifiableOrNull(this.delegate.firstEntry());
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> lastEntry()
/*      */     {
/* 1998 */       return Maps.unmodifiableOrNull(this.delegate.lastEntry());
/*      */     }
/*      */ 
/*      */     public final Map.Entry<K, V> pollFirstEntry()
/*      */     {
/* 2003 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public final Map.Entry<K, V> pollLastEntry()
/*      */     {
/* 2008 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> descendingMap()
/*      */     {
/* 2015 */       UnmodifiableNavigableMap result = this.descendingMap;
/* 2016 */       if (result == null) {
/* 2017 */         this.descendingMap = (result = new UnmodifiableNavigableMap(this.delegate.descendingMap()));
/* 2018 */         result.descendingMap = this;
/*      */       }
/* 2020 */       return result;
/*      */     }
/*      */ 
/*      */     public Set<K> keySet()
/*      */     {
/* 2025 */       return navigableKeySet();
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> navigableKeySet()
/*      */     {
/* 2030 */       return Sets.unmodifiableNavigableSet(this.delegate.navigableKeySet());
/*      */     }
/*      */ 
/*      */     public NavigableSet<K> descendingKeySet()
/*      */     {
/* 2035 */       return Sets.unmodifiableNavigableSet(this.delegate.descendingKeySet());
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> subMap(K fromKey, K toKey)
/*      */     {
/* 2040 */       return subMap(fromKey, true, toKey, false);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> headMap(K toKey)
/*      */     {
/* 2045 */       return headMap(toKey, false);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> tailMap(K fromKey)
/*      */     {
/* 2050 */       return tailMap(fromKey, true);
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
/*      */     {
/* 2057 */       return Maps.unmodifiableNavigableMap(this.delegate.subMap(fromKey, fromInclusive, toKey, toInclusive));
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> headMap(K toKey, boolean inclusive)
/*      */     {
/* 2066 */       return Maps.unmodifiableNavigableMap(this.delegate.headMap(toKey, inclusive));
/*      */     }
/*      */ 
/*      */     public NavigableMap<K, V> tailMap(K fromKey, boolean inclusive)
/*      */     {
/* 2071 */       return Maps.unmodifiableNavigableMap(this.delegate.tailMap(fromKey, inclusive));
/*      */     }
/*      */   }
/*      */ 
/*      */   static class FilteredEntryMap<K, V> extends Maps.AbstractFilteredMap<K, V>
/*      */   {
/*      */     final Set<Map.Entry<K, V>> filteredEntrySet;
/*      */     Set<Map.Entry<K, V>> entrySet;
/*      */     Set<K> keySet;
/*      */ 
/*      */     FilteredEntryMap(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */     {
/* 1792 */       super(entryPredicate);
/* 1793 */       this.filteredEntrySet = Sets.filter(unfiltered.entrySet(), this.predicate);
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<K, V>> entrySet()
/*      */     {
/* 1799 */       Set result = this.entrySet;
/* 1800 */       return result == null ? (this.entrySet = new EntrySet(null)) : result;
/*      */     }
/*      */ 
/*      */     public Set<K> keySet()
/*      */     {
/* 1837 */       Set result = this.keySet;
/* 1838 */       return result == null ? (this.keySet = new KeySet(null)) : result;
/*      */     }
/*      */     private class KeySet extends AbstractSet<K> {
/*      */       private KeySet() {
/*      */       }
/* 1843 */       public Iterator<K> iterator() { final Iterator iterator = Maps.FilteredEntryMap.this.filteredEntrySet.iterator();
/* 1844 */         return new UnmodifiableIterator()
/*      */         {
/*      */           public boolean hasNext() {
/* 1847 */             return iterator.hasNext();
/*      */           }
/*      */ 
/*      */           public K next()
/*      */           {
/* 1852 */             return ((Map.Entry)iterator.next()).getKey();
/*      */           }
/*      */         }; }
/*      */ 
/*      */       public int size()
/*      */       {
/* 1858 */         return Maps.FilteredEntryMap.this.filteredEntrySet.size();
/*      */       }
/*      */ 
/*      */       public void clear() {
/* 1862 */         Maps.FilteredEntryMap.this.filteredEntrySet.clear();
/*      */       }
/*      */ 
/*      */       public boolean contains(Object o) {
/* 1866 */         return Maps.FilteredEntryMap.this.containsKey(o);
/*      */       }
/*      */ 
/*      */       public boolean remove(Object o) {
/* 1870 */         if (Maps.FilteredEntryMap.this.containsKey(o)) {
/* 1871 */           Maps.FilteredEntryMap.this.unfiltered.remove(o);
/* 1872 */           return true;
/*      */         }
/* 1874 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean removeAll(Collection<?> collection) {
/* 1878 */         Preconditions.checkNotNull(collection);
/* 1879 */         boolean changed = false;
/* 1880 */         for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object obj = i$.next();
/* 1881 */           changed |= remove(obj);
/*      */         }
/* 1883 */         return changed;
/*      */       }
/*      */ 
/*      */       public boolean retainAll(Collection<?> collection) {
/* 1887 */         Preconditions.checkNotNull(collection);
/* 1888 */         boolean changed = false;
/* 1889 */         Iterator iterator = Maps.FilteredEntryMap.this.unfiltered.entrySet().iterator();
/* 1890 */         while (iterator.hasNext()) {
/* 1891 */           Map.Entry entry = (Map.Entry)iterator.next();
/* 1892 */           if ((!collection.contains(entry.getKey())) && (Maps.FilteredEntryMap.this.predicate.apply(entry))) {
/* 1893 */             iterator.remove();
/* 1894 */             changed = true;
/*      */           }
/*      */         }
/* 1897 */         return changed;
/*      */       }
/*      */ 
/*      */       public Object[] toArray()
/*      */       {
/* 1902 */         return Lists.newArrayList(iterator()).toArray();
/*      */       }
/*      */ 
/*      */       public <T> T[] toArray(T[] array) {
/* 1906 */         return Lists.newArrayList(iterator()).toArray(array);
/*      */       }
/*      */     }
/*      */ 
/*      */     private class EntrySet extends ForwardingSet<Map.Entry<K, V>>
/*      */     {
/*      */       private EntrySet()
/*      */       {
/*      */       }
/*      */ 
/*      */       protected Set<Map.Entry<K, V>> delegate()
/*      */       {
/* 1805 */         return Maps.FilteredEntryMap.this.filteredEntrySet;
/*      */       }
/*      */ 
/*      */       public Iterator<Map.Entry<K, V>> iterator() {
/* 1809 */         final Iterator iterator = Maps.FilteredEntryMap.this.filteredEntrySet.iterator();
/* 1810 */         return new UnmodifiableIterator()
/*      */         {
/*      */           public boolean hasNext() {
/* 1813 */             return iterator.hasNext();
/*      */           }
/*      */ 
/*      */           public Map.Entry<K, V> next()
/*      */           {
/* 1818 */             final Map.Entry entry = (Map.Entry)iterator.next();
/* 1819 */             return new ForwardingMapEntry() {
/*      */               protected Map.Entry<K, V> delegate() {
/* 1821 */                 return entry;
/*      */               }
/*      */ 
/*      */               public V setValue(V value) {
/* 1825 */                 Preconditions.checkArgument(Maps.FilteredEntryMap.this.apply(entry.getKey(), value));
/* 1826 */                 return super.setValue(value);
/*      */               }
/*      */             };
/*      */           }
/*      */         };
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FilteredKeyMap<K, V> extends Maps.AbstractFilteredMap<K, V>
/*      */   {
/*      */     Predicate<? super K> keyPredicate;
/*      */     Set<Map.Entry<K, V>> entrySet;
/*      */     Set<K> keySet;
/*      */ 
/*      */     FilteredKeyMap(Map<K, V> unfiltered, Predicate<? super K> keyPredicate, Predicate<Map.Entry<K, V>> entryPredicate)
/*      */     {
/* 1752 */       super(entryPredicate);
/* 1753 */       this.keyPredicate = keyPredicate;
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<K, V>> entrySet()
/*      */     {
/* 1759 */       Set result = this.entrySet;
/* 1760 */       return result == null ? (this.entrySet = Sets.filter(this.unfiltered.entrySet(), this.predicate)) : result;
/*      */     }
/*      */ 
/*      */     public Set<K> keySet()
/*      */     {
/* 1768 */       Set result = this.keySet;
/* 1769 */       return result == null ? (this.keySet = Sets.filter(this.unfiltered.keySet(), this.keyPredicate)) : result;
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key)
/*      */     {
/* 1779 */       return (this.unfiltered.containsKey(key)) && (this.keyPredicate.apply(key));
/*      */     }
/*      */   }
/*      */ 
/*      */   private static class FilteredEntrySortedMap<K, V> extends Maps.FilteredEntryMap<K, V>
/*      */     implements SortedMap<K, V>
/*      */   {
/*      */     FilteredEntrySortedMap(SortedMap<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> entryPredicate)
/*      */     {
/* 1704 */       super(entryPredicate);
/*      */     }
/*      */ 
/*      */     SortedMap<K, V> sortedMap() {
/* 1708 */       return (SortedMap)this.unfiltered;
/*      */     }
/*      */ 
/*      */     public Comparator<? super K> comparator() {
/* 1712 */       return sortedMap().comparator();
/*      */     }
/*      */ 
/*      */     public K firstKey()
/*      */     {
/* 1717 */       return keySet().iterator().next();
/*      */     }
/*      */ 
/*      */     public K lastKey() {
/* 1721 */       SortedMap headMap = sortedMap();
/*      */       while (true)
/*      */       {
/* 1724 */         Object key = headMap.lastKey();
/* 1725 */         if (apply(key, this.unfiltered.get(key))) {
/* 1726 */           return key;
/*      */         }
/* 1728 */         headMap = sortedMap().headMap(key);
/*      */       }
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> headMap(K toKey) {
/* 1733 */       return new FilteredEntrySortedMap(sortedMap().headMap(toKey), this.predicate);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> subMap(K fromKey, K toKey) {
/* 1737 */       return new FilteredEntrySortedMap(sortedMap().subMap(fromKey, toKey), this.predicate);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> tailMap(K fromKey)
/*      */     {
/* 1742 */       return new FilteredEntrySortedMap(sortedMap().tailMap(fromKey), this.predicate);
/*      */     }
/*      */   }
/*      */ 
/*      */   private static abstract class AbstractFilteredMap<K, V> extends AbstractMap<K, V>
/*      */   {
/*      */     final Map<K, V> unfiltered;
/*      */     final Predicate<? super Map.Entry<K, V>> predicate;
/*      */     Collection<V> values;
/*      */ 
/*      */     AbstractFilteredMap(Map<K, V> unfiltered, Predicate<? super Map.Entry<K, V>> predicate)
/*      */     {
/* 1560 */       this.unfiltered = unfiltered;
/* 1561 */       this.predicate = predicate;
/*      */     }
/*      */ 
/*      */     boolean apply(Object key, V value)
/*      */     {
/* 1568 */       Object k = key;
/* 1569 */       return this.predicate.apply(Maps.immutableEntry(k, value));
/*      */     }
/*      */ 
/*      */     public V put(K key, V value) {
/* 1573 */       Preconditions.checkArgument(apply(key, value));
/* 1574 */       return this.unfiltered.put(key, value);
/*      */     }
/*      */ 
/*      */     public void putAll(Map<? extends K, ? extends V> map) {
/* 1578 */       for (Map.Entry entry : map.entrySet()) {
/* 1579 */         Preconditions.checkArgument(apply(entry.getKey(), entry.getValue()));
/*      */       }
/* 1581 */       this.unfiltered.putAll(map);
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key) {
/* 1585 */       return (this.unfiltered.containsKey(key)) && (apply(key, this.unfiltered.get(key)));
/*      */     }
/*      */ 
/*      */     public V get(Object key) {
/* 1589 */       Object value = this.unfiltered.get(key);
/* 1590 */       return (value != null) && (apply(key, value)) ? value : null;
/*      */     }
/*      */ 
/*      */     public boolean isEmpty() {
/* 1594 */       return entrySet().isEmpty();
/*      */     }
/*      */ 
/*      */     public V remove(Object key) {
/* 1598 */       return containsKey(key) ? this.unfiltered.remove(key) : null;
/*      */     }
/*      */ 
/*      */     public Collection<V> values()
/*      */     {
/* 1604 */       Collection result = this.values;
/* 1605 */       return result == null ? (this.values = new Values()) : result;
/*      */     }
/*      */     class Values extends AbstractCollection<V> {
/*      */       Values() {
/*      */       }
/* 1610 */       public Iterator<V> iterator() { final Iterator entryIterator = Maps.AbstractFilteredMap.this.entrySet().iterator();
/* 1611 */         return new UnmodifiableIterator()
/*      */         {
/*      */           public boolean hasNext() {
/* 1614 */             return entryIterator.hasNext();
/*      */           }
/*      */ 
/*      */           public V next()
/*      */           {
/* 1619 */             return ((Map.Entry)entryIterator.next()).getValue();
/*      */           }
/*      */         }; }
/*      */ 
/*      */       public int size()
/*      */       {
/* 1625 */         return Maps.AbstractFilteredMap.this.entrySet().size();
/*      */       }
/*      */ 
/*      */       public void clear() {
/* 1629 */         Maps.AbstractFilteredMap.this.entrySet().clear();
/*      */       }
/*      */ 
/*      */       public boolean isEmpty() {
/* 1633 */         return Maps.AbstractFilteredMap.this.entrySet().isEmpty();
/*      */       }
/*      */ 
/*      */       public boolean remove(Object o) {
/* 1637 */         Iterator iterator = Maps.AbstractFilteredMap.this.unfiltered.entrySet().iterator();
/* 1638 */         while (iterator.hasNext()) {
/* 1639 */           Map.Entry entry = (Map.Entry)iterator.next();
/* 1640 */           if ((Objects.equal(o, entry.getValue())) && (Maps.AbstractFilteredMap.this.predicate.apply(entry))) {
/* 1641 */             iterator.remove();
/* 1642 */             return true;
/*      */           }
/*      */         }
/* 1645 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean removeAll(Collection<?> collection) {
/* 1649 */         Preconditions.checkNotNull(collection);
/* 1650 */         boolean changed = false;
/* 1651 */         Iterator iterator = Maps.AbstractFilteredMap.this.unfiltered.entrySet().iterator();
/* 1652 */         while (iterator.hasNext()) {
/* 1653 */           Map.Entry entry = (Map.Entry)iterator.next();
/* 1654 */           if ((collection.contains(entry.getValue())) && (Maps.AbstractFilteredMap.this.predicate.apply(entry))) {
/* 1655 */             iterator.remove();
/* 1656 */             changed = true;
/*      */           }
/*      */         }
/* 1659 */         return changed;
/*      */       }
/*      */ 
/*      */       public boolean retainAll(Collection<?> collection) {
/* 1663 */         Preconditions.checkNotNull(collection);
/* 1664 */         boolean changed = false;
/* 1665 */         Iterator iterator = Maps.AbstractFilteredMap.this.unfiltered.entrySet().iterator();
/* 1666 */         while (iterator.hasNext()) {
/* 1667 */           Map.Entry entry = (Map.Entry)iterator.next();
/* 1668 */           if ((!collection.contains(entry.getValue())) && (Maps.AbstractFilteredMap.this.predicate.apply(entry)))
/*      */           {
/* 1670 */             iterator.remove();
/* 1671 */             changed = true;
/*      */           }
/*      */         }
/* 1674 */         return changed;
/*      */       }
/*      */ 
/*      */       public Object[] toArray()
/*      */       {
/* 1679 */         return Lists.newArrayList(iterator()).toArray();
/*      */       }
/*      */ 
/*      */       public <T> T[] toArray(T[] array) {
/* 1683 */         return Lists.newArrayList(iterator()).toArray(array);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   static class TransformedEntriesSortedMap<K, V1, V2> extends Maps.TransformedEntriesMap<K, V1, V2>
/*      */     implements SortedMap<K, V2>
/*      */   {
/*      */     protected SortedMap<K, V1> fromMap()
/*      */     {
/* 1244 */       return (SortedMap)this.fromMap;
/*      */     }
/*      */ 
/*      */     TransformedEntriesSortedMap(SortedMap<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */     {
/* 1249 */       super(transformer);
/*      */     }
/*      */ 
/*      */     public Comparator<? super K> comparator() {
/* 1253 */       return fromMap().comparator();
/*      */     }
/*      */ 
/*      */     public K firstKey() {
/* 1257 */       return fromMap().firstKey();
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V2> headMap(K toKey) {
/* 1261 */       return Maps.transformEntries(fromMap().headMap(toKey), this.transformer);
/*      */     }
/*      */ 
/*      */     public K lastKey() {
/* 1265 */       return fromMap().lastKey();
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V2> subMap(K fromKey, K toKey) {
/* 1269 */       return Maps.transformEntries(fromMap().subMap(fromKey, toKey), this.transformer);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V2> tailMap(K fromKey)
/*      */     {
/* 1274 */       return Maps.transformEntries(fromMap().tailMap(fromKey), this.transformer);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class TransformedEntriesMap<K, V1, V2> extends AbstractMap<K, V2>
/*      */   {
/*      */     final Map<K, V1> fromMap;
/*      */     final Maps.EntryTransformer<? super K, ? super V1, V2> transformer;
/*      */     Set<Map.Entry<K, V2>> entrySet;
/*      */     Collection<V2> values;
/*      */ 
/*      */     TransformedEntriesMap(Map<K, V1> fromMap, Maps.EntryTransformer<? super K, ? super V1, V2> transformer)
/*      */     {
/* 1154 */       this.fromMap = ((Map)Preconditions.checkNotNull(fromMap));
/* 1155 */       this.transformer = ((Maps.EntryTransformer)Preconditions.checkNotNull(transformer));
/*      */     }
/*      */ 
/*      */     public int size() {
/* 1159 */       return this.fromMap.size();
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key) {
/* 1163 */       return this.fromMap.containsKey(key);
/*      */     }
/*      */ 
/*      */     public V2 get(Object key)
/*      */     {
/* 1169 */       Object value = this.fromMap.get(key);
/* 1170 */       return (value != null) || (this.fromMap.containsKey(key)) ? this.transformer.transformEntry(key, value) : null;
/*      */     }
/*      */ 
/*      */     public V2 remove(Object key)
/*      */     {
/* 1178 */       return this.fromMap.containsKey(key) ? this.transformer.transformEntry(key, this.fromMap.remove(key)) : null;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 1184 */       this.fromMap.clear();
/*      */     }
/*      */ 
/*      */     public Set<K> keySet() {
/* 1188 */       return this.fromMap.keySet();
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<K, V2>> entrySet()
/*      */     {
/* 1194 */       Set result = this.entrySet;
/* 1195 */       if (result == null) {
/* 1196 */         this.entrySet = (result = new Maps.EntrySet() {
/*      */           Map<K, V2> map() {
/* 1198 */             return Maps.TransformedEntriesMap.this;
/*      */           }
/*      */ 
/*      */           public Iterator<Map.Entry<K, V2>> iterator() {
/* 1202 */             return new TransformedIterator(Maps.TransformedEntriesMap.this.fromMap.entrySet().iterator())
/*      */             {
/*      */               Map.Entry<K, V2> transform(final Map.Entry<K, V1> entry)
/*      */               {
/* 1206 */                 return new AbstractMapEntry()
/*      */                 {
/*      */                   public K getKey() {
/* 1209 */                     return entry.getKey();
/*      */                   }
/*      */ 
/*      */                   public V2 getValue()
/*      */                   {
/* 1214 */                     return Maps.TransformedEntriesMap.this.transformer.transformEntry(entry.getKey(), entry.getValue());
/*      */                   }
/*      */                 };
/*      */               }
/*      */             };
/*      */           }
/*      */         });
/*      */       }
/* 1222 */       return result;
/*      */     }
/*      */ 
/*      */     public Collection<V2> values()
/*      */     {
/* 1228 */       Collection result = this.values;
/* 1229 */       if (result == null) {
/* 1230 */         return this.values = new Maps.Values() {
/*      */           Map<K, V2> map() {
/* 1232 */             return Maps.TransformedEntriesMap.this;
/*      */           }
/*      */         };
/*      */       }
/* 1236 */       return result;
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface EntryTransformer<K, V1, V2>
/*      */   {
/*      */     public abstract V2 transformEntry(@Nullable K paramK, @Nullable V1 paramV1);
/*      */   }
/*      */ 
/*      */   private static class UnmodifiableBiMap<K, V> extends ForwardingMap<K, V>
/*      */     implements BiMap<K, V>, Serializable
/*      */   {
/*      */     final Map<K, V> unmodifiableMap;
/*      */     final BiMap<? extends K, ? extends V> delegate;
/*      */     transient BiMap<V, K> inverse;
/*      */     transient Set<V> values;
/*      */     private static final long serialVersionUID = 0L;
/*      */ 
/*      */     UnmodifiableBiMap(BiMap<? extends K, ? extends V> delegate, @Nullable BiMap<V, K> inverse)
/*      */     {
/*  862 */       this.unmodifiableMap = Collections.unmodifiableMap(delegate);
/*  863 */       this.delegate = delegate;
/*  864 */       this.inverse = inverse;
/*      */     }
/*      */ 
/*      */     protected Map<K, V> delegate() {
/*  868 */       return this.unmodifiableMap;
/*      */     }
/*      */ 
/*      */     public V forcePut(K key, V value)
/*      */     {
/*  873 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public BiMap<V, K> inverse()
/*      */     {
/*  878 */       BiMap result = this.inverse;
/*  879 */       return result == null ? (this.inverse = new UnmodifiableBiMap(this.delegate.inverse(), this)) : result;
/*      */     }
/*      */ 
/*      */     public Set<V> values()
/*      */     {
/*  885 */       Set result = this.values;
/*  886 */       return result == null ? (this.values = Collections.unmodifiableSet(this.delegate.values())) : result;
/*      */     }
/*      */   }
/*      */ 
/*      */   static class UnmodifiableEntrySet<K, V> extends Maps.UnmodifiableEntries<K, V>
/*      */     implements Set<Map.Entry<K, V>>
/*      */   {
/*      */     UnmodifiableEntrySet(Set<Map.Entry<K, V>> entries)
/*      */     {
/*  788 */       super();
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object)
/*      */     {
/*  794 */       return Sets.equalsImpl(this, object);
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  798 */       return Sets.hashCodeImpl(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class UnmodifiableEntries<K, V> extends ForwardingCollection<Map.Entry<K, V>>
/*      */   {
/*      */     private final Collection<Map.Entry<K, V>> entries;
/*      */ 
/*      */     UnmodifiableEntries(Collection<Map.Entry<K, V>> entries)
/*      */     {
/*  724 */       this.entries = entries;
/*      */     }
/*      */ 
/*      */     protected Collection<Map.Entry<K, V>> delegate() {
/*  728 */       return this.entries;
/*      */     }
/*      */ 
/*      */     public Iterator<Map.Entry<K, V>> iterator() {
/*  732 */       final Iterator delegate = super.iterator();
/*  733 */       return new ForwardingIterator() {
/*      */         public Map.Entry<K, V> next() {
/*  735 */           return Maps.unmodifiableEntry((Map.Entry)super.next());
/*      */         }
/*      */ 
/*      */         public void remove() {
/*  739 */           throw new UnsupportedOperationException();
/*      */         }
/*      */ 
/*      */         protected Iterator<Map.Entry<K, V>> delegate() {
/*  743 */           return delegate;
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public boolean add(Map.Entry<K, V> element)
/*      */     {
/*  751 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean addAll(Collection<? extends Map.Entry<K, V>> collection)
/*      */     {
/*  756 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  760 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean remove(Object object) {
/*  764 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection<?> collection) {
/*  768 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public boolean retainAll(Collection<?> collection) {
/*  772 */       throw new UnsupportedOperationException();
/*      */     }
/*      */ 
/*      */     public Object[] toArray() {
/*  776 */       return standardToArray();
/*      */     }
/*      */ 
/*      */     public <T> T[] toArray(T[] array) {
/*  780 */       return standardToArray(array);
/*      */     }
/*      */   }
/*      */ 
/*      */   static class SortedMapDifferenceImpl<K, V> extends Maps.MapDifferenceImpl<K, V>
/*      */     implements SortedMapDifference<K, V>
/*      */   {
/*      */     SortedMapDifferenceImpl(boolean areEqual, SortedMap<K, V> onlyOnLeft, SortedMap<K, V> onlyOnRight, SortedMap<K, V> onBoth, SortedMap<K, MapDifference.ValueDifference<V>> differences)
/*      */     {
/*  563 */       super(onlyOnLeft, onlyOnRight, onBoth, differences);
/*      */     }
/*      */ 
/*      */     public SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering() {
/*  567 */       return (SortedMap)super.entriesDiffering();
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> entriesInCommon() {
/*  571 */       return (SortedMap)super.entriesInCommon();
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> entriesOnlyOnLeft() {
/*  575 */       return (SortedMap)super.entriesOnlyOnLeft();
/*      */     }
/*      */ 
/*      */     public SortedMap<K, V> entriesOnlyOnRight() {
/*  579 */       return (SortedMap)super.entriesOnlyOnRight();
/*      */     }
/*      */   }
/*      */ 
/*      */   static class ValueDifferenceImpl<V>
/*      */     implements MapDifference.ValueDifference<V>
/*      */   {
/*      */     private final V left;
/*      */     private final V right;
/*      */ 
/*      */     static <V> MapDifference.ValueDifference<V> create(@Nullable V left, @Nullable V right)
/*      */     {
/*  456 */       return new ValueDifferenceImpl(left, right);
/*      */     }
/*      */ 
/*      */     private ValueDifferenceImpl(@Nullable V left, @Nullable V right) {
/*  460 */       this.left = left;
/*  461 */       this.right = right;
/*      */     }
/*      */ 
/*      */     public V leftValue()
/*      */     {
/*  466 */       return this.left;
/*      */     }
/*      */ 
/*      */     public V rightValue()
/*      */     {
/*  471 */       return this.right;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/*  475 */       if ((object instanceof MapDifference.ValueDifference)) {
/*  476 */         MapDifference.ValueDifference that = (MapDifference.ValueDifference)object;
/*      */ 
/*  478 */         return (Objects.equal(this.left, that.leftValue())) && (Objects.equal(this.right, that.rightValue()));
/*      */       }
/*      */ 
/*  481 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  485 */       return Objects.hashCode(new Object[] { this.left, this.right });
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  489 */       return "(" + this.left + ", " + this.right + ")";
/*      */     }
/*      */   }
/*      */ 
/*      */   static class MapDifferenceImpl<K, V>
/*      */     implements MapDifference<K, V>
/*      */   {
/*      */     final boolean areEqual;
/*      */     final Map<K, V> onlyOnLeft;
/*      */     final Map<K, V> onlyOnRight;
/*      */     final Map<K, V> onBoth;
/*      */     final Map<K, MapDifference.ValueDifference<V>> differences;
/*      */ 
/*      */     MapDifferenceImpl(boolean areEqual, Map<K, V> onlyOnLeft, Map<K, V> onlyOnRight, Map<K, V> onBoth, Map<K, MapDifference.ValueDifference<V>> differences)
/*      */     {
/*  380 */       this.areEqual = areEqual;
/*  381 */       this.onlyOnLeft = onlyOnLeft;
/*  382 */       this.onlyOnRight = onlyOnRight;
/*  383 */       this.onBoth = onBoth;
/*  384 */       this.differences = differences;
/*      */     }
/*      */ 
/*      */     public boolean areEqual()
/*      */     {
/*  389 */       return this.areEqual;
/*      */     }
/*      */ 
/*      */     public Map<K, V> entriesOnlyOnLeft()
/*      */     {
/*  394 */       return this.onlyOnLeft;
/*      */     }
/*      */ 
/*      */     public Map<K, V> entriesOnlyOnRight()
/*      */     {
/*  399 */       return this.onlyOnRight;
/*      */     }
/*      */ 
/*      */     public Map<K, V> entriesInCommon()
/*      */     {
/*  404 */       return this.onBoth;
/*      */     }
/*      */ 
/*      */     public Map<K, MapDifference.ValueDifference<V>> entriesDiffering()
/*      */     {
/*  409 */       return this.differences;
/*      */     }
/*      */ 
/*      */     public boolean equals(Object object) {
/*  413 */       if (object == this) {
/*  414 */         return true;
/*      */       }
/*  416 */       if ((object instanceof MapDifference)) {
/*  417 */         MapDifference other = (MapDifference)object;
/*  418 */         return (entriesOnlyOnLeft().equals(other.entriesOnlyOnLeft())) && (entriesOnlyOnRight().equals(other.entriesOnlyOnRight())) && (entriesInCommon().equals(other.entriesInCommon())) && (entriesDiffering().equals(other.entriesDiffering()));
/*      */       }
/*      */ 
/*  423 */       return false;
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  427 */       return Objects.hashCode(new Object[] { entriesOnlyOnLeft(), entriesOnlyOnRight(), entriesInCommon(), entriesDiffering() });
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  432 */       if (this.areEqual) {
/*  433 */         return "equal";
/*      */       }
/*      */ 
/*  436 */       StringBuilder result = new StringBuilder("not equal");
/*  437 */       if (!this.onlyOnLeft.isEmpty()) {
/*  438 */         result.append(": only on left=").append(this.onlyOnLeft);
/*      */       }
/*  440 */       if (!this.onlyOnRight.isEmpty()) {
/*  441 */         result.append(": only on right=").append(this.onlyOnRight);
/*      */       }
/*  443 */       if (!this.differences.isEmpty()) {
/*  444 */         result.append(": value differences=").append(this.differences);
/*      */       }
/*  446 */       return result.toString();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Maps
 * JD-Core Version:    0.6.2
 */