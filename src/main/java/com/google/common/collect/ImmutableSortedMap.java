/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public class ImmutableSortedMap<K, V> extends ImmutableSortedMapFauxverideShim<K, V>
/*     */   implements NavigableMap<K, V>
/*     */ {
/*  77 */   private static final Comparator<Comparable> NATURAL_ORDER = Ordering.natural();
/*     */ 
/*  81 */   private static final ImmutableSortedMap<Comparable, Object> NATURAL_EMPTY_MAP = new ImmutableSortedMap(ImmutableList.of(), NATURAL_ORDER);
/*     */   final transient ImmutableList<Map.Entry<K, V>> entries;
/*     */   private final transient Comparator<? super K> comparator;
/*     */   private transient ImmutableSortedMap<K, V> descendingMap;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> of()
/*     */   {
/*  92 */     return NATURAL_EMPTY_MAP;
/*     */   }
/*     */ 
/*     */   private static <K, V> ImmutableSortedMap<K, V> emptyMap(Comparator<? super K> comparator)
/*     */   {
/*  98 */     if (NATURAL_ORDER.equals(comparator)) {
/*  99 */       return NATURAL_EMPTY_MAP;
/*     */     }
/* 101 */     return new ImmutableSortedMap(ImmutableList.of(), comparator);
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1)
/*     */   {
/* 111 */     return new ImmutableSortedMap(ImmutableList.of(entryOf(k1, v1)), Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/* 124 */     return new Builder(Ordering.natural()).put(k1, v1).put(k2, v2).build();
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/* 137 */     return new Builder(Ordering.natural()).put(k1, v1).put(k2, v2).put(k3, v3).build();
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 150 */     return new Builder(Ordering.natural()).put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).build();
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<? super K>, V> ImmutableSortedMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 163 */     return new Builder(Ordering.natural()).put(k1, v1).put(k2, v2).put(k3, v3).put(k4, v4).put(k5, v5).build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map)
/*     */   {
/* 189 */     Ordering naturalOrder = Ordering.natural();
/* 190 */     return copyOfInternal(map, naturalOrder);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> copyOf(Map<? extends K, ? extends V> map, Comparator<? super K> comparator)
/*     */   {
/* 207 */     return copyOfInternal(map, (Comparator)Preconditions.checkNotNull(comparator));
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSortedMap<K, V> copyOfSorted(SortedMap<K, ? extends V> map)
/*     */   {
/* 223 */     Comparator comparator = map.comparator();
/* 224 */     if (comparator == null)
/*     */     {
/* 227 */       comparator = NATURAL_ORDER;
/*     */     }
/* 229 */     return copyOfInternal(map, comparator);
/*     */   }
/*     */ 
/*     */   private static <K, V> ImmutableSortedMap<K, V> copyOfInternal(Map<? extends K, ? extends V> map, Comparator<? super K> comparator)
/*     */   {
/* 234 */     boolean sameComparator = false;
/* 235 */     if ((map instanceof SortedMap)) {
/* 236 */       SortedMap sortedMap = (SortedMap)map;
/* 237 */       Comparator comparator2 = sortedMap.comparator();
/* 238 */       sameComparator = comparator2 == null ? false : comparator == NATURAL_ORDER ? true : comparator.equals(comparator2);
/*     */     }
/*     */ 
/* 243 */     if ((sameComparator) && ((map instanceof ImmutableSortedMap)))
/*     */     {
/* 247 */       ImmutableSortedMap kvMap = (ImmutableSortedMap)map;
/* 248 */       if (!kvMap.isPartialView()) {
/* 249 */         return kvMap;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 257 */     Map.Entry[] entries = (Map.Entry[])map.entrySet().toArray(new Map.Entry[0]);
/*     */ 
/* 259 */     for (int i = 0; i < entries.length; i++) {
/* 260 */       Map.Entry entry = entries[i];
/* 261 */       entries[i] = entryOf(entry.getKey(), entry.getValue());
/*     */     }
/*     */ 
/* 264 */     List list = Arrays.asList(entries);
/*     */ 
/* 266 */     if (!sameComparator) {
/* 267 */       sortEntries(list, comparator);
/* 268 */       validateEntries(list, comparator);
/*     */     }
/*     */ 
/* 271 */     return new ImmutableSortedMap(ImmutableList.copyOf(list), comparator);
/*     */   }
/*     */ 
/*     */   private static <K, V> void sortEntries(List<Map.Entry<K, V>> entries, Comparator<? super K> comparator)
/*     */   {
/* 276 */     Comparator entryComparator = new Comparator()
/*     */     {
/*     */       public int compare(Map.Entry<K, V> entry1, Map.Entry<K, V> entry2) {
/* 279 */         return this.val$comparator.compare(entry1.getKey(), entry2.getKey());
/*     */       }
/*     */     };
/* 283 */     Collections.sort(entries, entryComparator);
/*     */   }
/*     */ 
/*     */   private static <K, V> void validateEntries(List<Map.Entry<K, V>> entries, Comparator<? super K> comparator)
/*     */   {
/* 288 */     for (int i = 1; i < entries.size(); i++)
/* 289 */       if (comparator.compare(((Map.Entry)entries.get(i - 1)).getKey(), ((Map.Entry)entries.get(i)).getKey()) == 0)
/*     */       {
/* 291 */         throw new IllegalArgumentException("Duplicate keys in mappings " + entries.get(i - 1) + " and " + entries.get(i));
/*     */       }
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<K>, V> Builder<K, V> naturalOrder()
/*     */   {
/* 309 */     return new Builder(Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> orderedBy(Comparator<K> comparator)
/*     */   {
/* 321 */     return new Builder(comparator);
/*     */   }
/*     */ 
/*     */   public static <K extends Comparable<K>, V> Builder<K, V> reverseOrder()
/*     */   {
/* 334 */     return new Builder(Ordering.natural().reverse());
/*     */   }
/*     */ 
/*     */   ImmutableSortedMap(ImmutableList<Map.Entry<K, V>> entries, Comparator<? super K> comparator)
/*     */   {
/* 424 */     this.entries = entries;
/* 425 */     this.comparator = comparator;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 430 */     return this.entries.size();
/*     */   }
/*     */ 
/*     */   Comparator<Object> unsafeComparator()
/*     */   {
/* 438 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object key) {
/* 442 */     if (key == null)
/* 443 */       return null;
/*     */     int i;
/*     */     try
/*     */     {
/* 447 */       i = index(key, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.INVERTED_INSERTION_INDEX);
/*     */     } catch (ClassCastException e) {
/* 449 */       return null;
/*     */     }
/* 451 */     return i >= 0 ? ((Map.Entry)this.entries.get(i)).getValue() : null;
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/* 455 */     return (value != null) && (Maps.containsValueImpl(this, value));
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/* 459 */     return this.entries.isPartialView();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<Map.Entry<K, V>> entrySet()
/*     */   {
/* 467 */     return super.entrySet();
/*     */   }
/*     */ 
/*     */   ImmutableSet<Map.Entry<K, V>> createEntrySet()
/*     */   {
/* 472 */     return isEmpty() ? ImmutableSet.of() : new EntrySet(null);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<K> keySet()
/*     */   {
/* 493 */     return (ImmutableSortedSet)super.keySet();
/*     */   }
/*     */ 
/*     */   ImmutableSortedSet<K> createKeySet()
/*     */   {
/* 499 */     if (isEmpty()) {
/* 500 */       return ImmutableSortedSet.emptySet(this.comparator);
/*     */     }
/*     */ 
/* 503 */     return new RegularImmutableSortedSet(new TransformedImmutableList(this.entries)
/*     */     {
/*     */       K transform(Map.Entry<K, V> entry)
/*     */       {
/* 507 */         return entry.getKey();
/*     */       }
/*     */     }
/*     */     , this.comparator);
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<V> values()
/*     */   {
/* 517 */     return super.values();
/*     */   }
/*     */ 
/*     */   public Comparator<? super K> comparator()
/*     */   {
/* 528 */     return this.comparator;
/*     */   }
/*     */ 
/*     */   public K firstKey()
/*     */   {
/* 533 */     if (isEmpty()) {
/* 534 */       throw new NoSuchElementException();
/*     */     }
/* 536 */     return ((Map.Entry)this.entries.get(0)).getKey();
/*     */   }
/*     */ 
/*     */   public K lastKey()
/*     */   {
/* 541 */     if (isEmpty()) {
/* 542 */       throw new NoSuchElementException();
/*     */     }
/* 544 */     return ((Map.Entry)this.entries.get(size() - 1)).getKey();
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> headMap(K toKey)
/*     */   {
/* 559 */     return headMap(toKey, false);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> headMap(K toKey, boolean inclusive)
/*     */   {
/*     */     int index;
/*     */     int index;
/* 577 */     if (inclusive)
/* 578 */       index = index(toKey, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER) + 1;
/*     */     else {
/* 580 */       index = index(toKey, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */     }
/* 582 */     return createSubmap(0, index);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> subMap(K fromKey, K toKey)
/*     */   {
/* 600 */     return subMap(fromKey, true, toKey, false);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive)
/*     */   {
/* 621 */     Preconditions.checkNotNull(fromKey);
/* 622 */     Preconditions.checkNotNull(toKey);
/* 623 */     Preconditions.checkArgument(this.comparator.compare(fromKey, toKey) <= 0);
/* 624 */     return tailMap(fromKey, fromInclusive).headMap(toKey, toInclusive);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> tailMap(K fromKey)
/*     */   {
/* 639 */     return tailMap(fromKey, true);
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> tailMap(K fromKey, boolean inclusive)
/*     */   {
/*     */     int index;
/*     */     int index;
/* 658 */     if (inclusive)
/* 659 */       index = index(fromKey, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_HIGHER);
/*     */     else {
/* 661 */       index = index(fromKey, SortedLists.KeyPresentBehavior.ANY_PRESENT, SortedLists.KeyAbsentBehavior.NEXT_LOWER) + 1;
/*     */     }
/* 663 */     return createSubmap(index, size());
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> lowerEntry(K key)
/*     */   {
/* 668 */     return headMap(key, false).lastEntry();
/*     */   }
/*     */ 
/*     */   public K lowerKey(K key)
/*     */   {
/* 673 */     return Maps.keyOrNull(lowerEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> floorEntry(K key)
/*     */   {
/* 678 */     return headMap(key, true).lastEntry();
/*     */   }
/*     */ 
/*     */   public K floorKey(K key)
/*     */   {
/* 683 */     return Maps.keyOrNull(floorEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> ceilingEntry(K key)
/*     */   {
/* 688 */     return tailMap(key, true).firstEntry();
/*     */   }
/*     */ 
/*     */   public K ceilingKey(K key)
/*     */   {
/* 693 */     return Maps.keyOrNull(ceilingEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> higherEntry(K key)
/*     */   {
/* 698 */     return tailMap(key, false).firstEntry();
/*     */   }
/*     */ 
/*     */   public K higherKey(K key)
/*     */   {
/* 703 */     return Maps.keyOrNull(higherEntry(key));
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> firstEntry()
/*     */   {
/* 708 */     return isEmpty() ? null : (Map.Entry)this.entries.get(0);
/*     */   }
/*     */ 
/*     */   public Map.Entry<K, V> lastEntry()
/*     */   {
/* 713 */     return isEmpty() ? null : (Map.Entry)this.entries.get(this.entries.size() - 1);
/*     */   }
/*     */ 
/*     */   public final Map.Entry<K, V> pollFirstEntry()
/*     */   {
/* 718 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final Map.Entry<K, V> pollLastEntry()
/*     */   {
/* 723 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableSortedMap<K, V> descendingMap()
/*     */   {
/* 730 */     ImmutableSortedMap result = this.descendingMap;
/* 731 */     if (result == null) {
/* 732 */       result = this.descendingMap = new ImmutableSortedMap(this.entries.reverse(), Ordering.from(comparator()).reverse());
/*     */ 
/* 734 */       result.descendingMap = this;
/*     */     }
/* 736 */     return result;
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<K> navigableKeySet()
/*     */   {
/* 741 */     return keySet();
/*     */   }
/*     */ 
/*     */   public ImmutableSortedSet<K> descendingKeySet()
/*     */   {
/* 746 */     return descendingMap().keySet();
/*     */   }
/*     */ 
/*     */   private ImmutableList<K> keyList() {
/* 750 */     return new TransformedImmutableList(this.entries)
/*     */     {
/*     */       K transform(Map.Entry<K, V> entry) {
/* 753 */         return entry.getKey();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private int index(Object key, SortedLists.KeyPresentBehavior presentBehavior, SortedLists.KeyAbsentBehavior absentBehavior)
/*     */   {
/* 760 */     return SortedLists.binarySearch(keyList(), Preconditions.checkNotNull(key), unsafeComparator(), presentBehavior, absentBehavior);
/*     */   }
/*     */ 
/*     */   private ImmutableSortedMap<K, V> createSubmap(int newFromIndex, int newToIndex)
/*     */   {
/* 766 */     if (newFromIndex < newToIndex) {
/* 767 */       return new ImmutableSortedMap(this.entries.subList(newFromIndex, newToIndex), this.comparator);
/*     */     }
/*     */ 
/* 770 */     return emptyMap(this.comparator);
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 795 */     return new SerializedForm(this);
/*     */   }
/*     */ 
/*     */   private static class SerializedForm extends ImmutableMap.SerializedForm
/*     */   {
/*     */     private final Comparator<Object> comparator;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SerializedForm(ImmutableSortedMap<?, ?> sortedMap)
/*     */     {
/* 784 */       super();
/* 785 */       this.comparator = sortedMap.comparator();
/*     */     }
/*     */     Object readResolve() {
/* 788 */       ImmutableSortedMap.Builder builder = new ImmutableSortedMap.Builder(this.comparator);
/* 789 */       return createMap(builder);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class EntrySet extends ImmutableMap.EntrySet
/*     */   {
/*     */     private EntrySet()
/*     */     {
/* 477 */       super();
/*     */     }
/*     */     public UnmodifiableIterator<Map.Entry<K, V>> iterator() {
/* 480 */       return ImmutableSortedMap.this.entries.iterator();
/*     */     }
/*     */ 
/*     */     ImmutableList<Map.Entry<K, V>> createAsList()
/*     */     {
/* 485 */       return new RegularImmutableAsList(this, ImmutableSortedMap.this.entries);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Builder<K, V> extends ImmutableMap.Builder<K, V>
/*     */   {
/*     */     private final Comparator<? super K> comparator;
/*     */ 
/*     */     public Builder(Comparator<? super K> comparator)
/*     */     {
/* 365 */       this.comparator = ((Comparator)Preconditions.checkNotNull(comparator));
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 374 */       this.entries.add(ImmutableMap.entryOf(key, value));
/* 375 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry)
/*     */     {
/* 387 */       super.put(entry);
/* 388 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Map<? extends K, ? extends V> map)
/*     */     {
/* 399 */       for (Map.Entry entry : map.entrySet()) {
/* 400 */         put(entry.getKey(), entry.getValue());
/*     */       }
/* 402 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSortedMap<K, V> build()
/*     */     {
/* 412 */       ImmutableSortedMap.sortEntries(this.entries, this.comparator);
/* 413 */       ImmutableSortedMap.validateEntries(this.entries, this.comparator);
/* 414 */       return new ImmutableSortedMap(ImmutableList.copyOf(this.entries), this.comparator);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSortedMap
 * JD-Core Version:    0.6.2
 */