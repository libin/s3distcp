/*       */ package com.google.common.collect;
/*       */ 
/*       */ import com.google.common.annotations.GwtCompatible;
/*       */ import com.google.common.annotations.GwtIncompatible;
/*       */ import com.google.common.annotations.VisibleForTesting;
/*       */ import com.google.common.base.Preconditions;
/*       */ import java.io.IOException;
/*       */ import java.io.ObjectOutputStream;
/*       */ import java.io.Serializable;
/*       */ import java.util.Collection;
/*       */ import java.util.Comparator;
/*       */ import java.util.Iterator;
/*       */ import java.util.List;
/*       */ import java.util.ListIterator;
/*       */ import java.util.Map;
/*       */ import java.util.Map.Entry;
/*       */ import java.util.RandomAccess;
/*       */ import java.util.Set;
/*       */ import java.util.SortedMap;
/*       */ import java.util.SortedSet;
/*       */ import javax.annotation.Nullable;
/*       */ 
/*       */ @GwtCompatible(emulated=true)
/*       */ final class Synchronized
/*       */ {
/*       */   private static <E> Collection<E> collection(Collection<E> collection, @Nullable Object mutex)
/*       */   {
/*    97 */     return new SynchronizedCollection(collection, mutex, null);
/*       */   }
/*       */ 
/*       */   @VisibleForTesting
/*       */   static <E> Set<E> set(Set<E> set, @Nullable Object mutex)
/*       */   {
/*   205 */     return new SynchronizedSet(set, mutex);
/*       */   }
/*       */ 
/*       */   private static <E> SortedSet<E> sortedSet(SortedSet<E> set, @Nullable Object mutex)
/*       */   {
/*   239 */     return new SynchronizedSortedSet(set, mutex);
/*       */   }
/*       */ 
/*       */   private static <E> List<E> list(List<E> list, @Nullable Object mutex)
/*       */   {
/*   298 */     return (list instanceof RandomAccess) ? new SynchronizedRandomAccessList(list, mutex) : new SynchronizedList(list, mutex);
/*       */   }
/*       */ 
/*       */   static <E> Multiset<E> multiset(Multiset<E> multiset, @Nullable Object mutex)
/*       */   {
/*   407 */     if (((multiset instanceof SynchronizedMultiset)) || ((multiset instanceof ImmutableMultiset)))
/*       */     {
/*   409 */       return multiset;
/*       */     }
/*   411 */     return new SynchronizedMultiset(multiset, mutex);
/*       */   }
/*       */ 
/*       */   static <K, V> Multimap<K, V> multimap(Multimap<K, V> multimap, @Nullable Object mutex)
/*       */   {
/*   502 */     if (((multimap instanceof SynchronizedMultimap)) || ((multimap instanceof ImmutableMultimap)))
/*       */     {
/*   504 */       return multimap;
/*       */     }
/*   506 */     return new SynchronizedMultimap(multimap, mutex);
/*       */   }
/*       */ 
/*       */   static <K, V> ListMultimap<K, V> listMultimap(ListMultimap<K, V> multimap, @Nullable Object mutex)
/*       */   {
/*   687 */     if (((multimap instanceof SynchronizedListMultimap)) || ((multimap instanceof ImmutableListMultimap)))
/*       */     {
/*   689 */       return multimap;
/*       */     }
/*   691 */     return new SynchronizedListMultimap(multimap, mutex);
/*       */   }
/*       */ 
/*       */   static <K, V> SetMultimap<K, V> setMultimap(SetMultimap<K, V> multimap, @Nullable Object mutex)
/*       */   {
/*   724 */     if (((multimap instanceof SynchronizedSetMultimap)) || ((multimap instanceof ImmutableSetMultimap)))
/*       */     {
/*   726 */       return multimap;
/*       */     }
/*   728 */     return new SynchronizedSetMultimap(multimap, mutex);
/*       */   }
/*       */ 
/*       */   static <K, V> SortedSetMultimap<K, V> sortedSetMultimap(SortedSetMultimap<K, V> multimap, @Nullable Object mutex)
/*       */   {
/*   771 */     if ((multimap instanceof SynchronizedSortedSetMultimap)) {
/*   772 */       return multimap;
/*       */     }
/*   774 */     return new SynchronizedSortedSetMultimap(multimap, mutex);
/*       */   }
/*       */ 
/*       */   private static <E> Collection<E> typePreservingCollection(Collection<E> collection, @Nullable Object mutex)
/*       */   {
/*   813 */     if ((collection instanceof SortedSet)) {
/*   814 */       return sortedSet((SortedSet)collection, mutex);
/*       */     }
/*   816 */     if ((collection instanceof Set)) {
/*   817 */       return set((Set)collection, mutex);
/*       */     }
/*   819 */     if ((collection instanceof List)) {
/*   820 */       return list((List)collection, mutex);
/*       */     }
/*   822 */     return collection(collection, mutex);
/*       */   }
/*       */ 
/*       */   private static <E> Set<E> typePreservingSet(Set<E> set, @Nullable Object mutex)
/*       */   {
/*   827 */     if ((set instanceof SortedSet)) {
/*   828 */       return sortedSet((SortedSet)set, mutex);
/*       */     }
/*   830 */     return set(set, mutex);
/*       */   }
/*       */ 
/*       */   @VisibleForTesting
/*       */   static <K, V> Map<K, V> map(Map<K, V> map, @Nullable Object mutex)
/*       */   {
/*   914 */     return new SynchronizedMap(map, mutex);
/*       */   }
/*       */ 
/*       */   static <K, V> SortedMap<K, V> sortedMap(SortedMap<K, V> sortedMap, @Nullable Object mutex)
/*       */   {
/*  1045 */     return new SynchronizedSortedMap(sortedMap, mutex);
/*       */   }
/*       */ 
/*       */   static <K, V> BiMap<K, V> biMap(BiMap<K, V> bimap, @Nullable Object mutex)
/*       */   {
/*  1099 */     if (((bimap instanceof SynchronizedBiMap)) || ((bimap instanceof ImmutableBiMap)))
/*       */     {
/*  1101 */       return bimap;
/*       */     }
/*  1103 */     return new SynchronizedBiMap(bimap, mutex, null, null);
/*       */   }
/*       */ 
/*       */   private static class SynchronizedAsMapValues<V> extends Synchronized.SynchronizedCollection<Collection<V>>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedAsMapValues(Collection<Collection<V>> delegate, @Nullable Object mutex)
/*       */     {
/*  1200 */       super(mutex, null);
/*       */     }
/*       */ 
/*       */     public Iterator<Collection<V>> iterator()
/*       */     {
/*  1205 */       final Iterator iterator = super.iterator();
/*  1206 */       return new ForwardingIterator() {
/*       */         protected Iterator<Collection<V>> delegate() {
/*  1208 */           return iterator;
/*       */         }
/*       */         public Collection<V> next() {
/*  1211 */           return Synchronized.typePreservingCollection((Collection)super.next(), Synchronized.SynchronizedAsMapValues.this.mutex);
/*       */         }
/*       */       };
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedAsMap<K, V> extends Synchronized.SynchronizedMap<K, Collection<V>>
/*       */   {
/*       */     transient Set<Map.Entry<K, Collection<V>>> asMapEntrySet;
/*       */     transient Collection<Collection<V>> asMapValues;
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedAsMap(Map<K, Collection<V>> delegate, @Nullable Object mutex)
/*       */     {
/*  1157 */       super(mutex);
/*       */     }
/*       */ 
/*       */     public Collection<V> get(Object key) {
/*  1161 */       synchronized (this.mutex) {
/*  1162 */         Collection collection = (Collection)super.get(key);
/*  1163 */         return collection == null ? null : Synchronized.typePreservingCollection(collection, this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public Set<Map.Entry<K, Collection<V>>> entrySet()
/*       */     {
/*  1169 */       synchronized (this.mutex) {
/*  1170 */         if (this.asMapEntrySet == null) {
/*  1171 */           this.asMapEntrySet = new Synchronized.SynchronizedAsMapEntries(delegate().entrySet(), this.mutex);
/*       */         }
/*       */ 
/*  1174 */         return this.asMapEntrySet;
/*       */       }
/*       */     }
/*       */ 
/*       */     public Collection<Collection<V>> values() {
/*  1179 */       synchronized (this.mutex) {
/*  1180 */         if (this.asMapValues == null) {
/*  1181 */           this.asMapValues = new Synchronized.SynchronizedAsMapValues(delegate().values(), this.mutex);
/*       */         }
/*       */ 
/*  1184 */         return this.asMapValues;
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean containsValue(Object o)
/*       */     {
/*  1190 */       return values().contains(o);
/*       */     }
/*       */   }
/*       */ 
/*       */   @VisibleForTesting
/*       */   static class SynchronizedBiMap<K, V> extends Synchronized.SynchronizedMap<K, V>
/*       */     implements BiMap<K, V>, Serializable
/*       */   {
/*       */     private transient Set<V> valueSet;
/*       */     private transient BiMap<V, K> inverse;
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     private SynchronizedBiMap(BiMap<K, V> delegate, @Nullable Object mutex, @Nullable BiMap<V, K> inverse)
/*       */     {
/*  1113 */       super(mutex);
/*  1114 */       this.inverse = inverse;
/*       */     }
/*       */ 
/*       */     BiMap<K, V> delegate() {
/*  1118 */       return (BiMap)super.delegate();
/*       */     }
/*       */ 
/*       */     public Set<V> values() {
/*  1122 */       synchronized (this.mutex) {
/*  1123 */         if (this.valueSet == null) {
/*  1124 */           this.valueSet = Synchronized.set(delegate().values(), this.mutex);
/*       */         }
/*  1126 */         return this.valueSet;
/*       */       }
/*       */     }
/*       */ 
/*       */     public V forcePut(K key, V value)
/*       */     {
/*  1132 */       synchronized (this.mutex) {
/*  1133 */         return delegate().forcePut(key, value);
/*       */       }
/*       */     }
/*       */ 
/*       */     public BiMap<V, K> inverse()
/*       */     {
/*  1139 */       synchronized (this.mutex) {
/*  1140 */         if (this.inverse == null) {
/*  1141 */           this.inverse = new SynchronizedBiMap(delegate().inverse(), this.mutex, this);
/*       */         }
/*       */ 
/*  1144 */         return this.inverse;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   static class SynchronizedSortedMap<K, V> extends Synchronized.SynchronizedMap<K, V>
/*       */     implements SortedMap<K, V>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedSortedMap(SortedMap<K, V> delegate, @Nullable Object mutex)
/*       */     {
/*  1052 */       super(mutex);
/*       */     }
/*       */ 
/*       */     SortedMap<K, V> delegate() {
/*  1056 */       return (SortedMap)super.delegate();
/*       */     }
/*       */ 
/*       */     public Comparator<? super K> comparator() {
/*  1060 */       synchronized (this.mutex) {
/*  1061 */         return delegate().comparator();
/*       */       }
/*       */     }
/*       */ 
/*       */     public K firstKey() {
/*  1066 */       synchronized (this.mutex) {
/*  1067 */         return delegate().firstKey();
/*       */       }
/*       */     }
/*       */ 
/*       */     public SortedMap<K, V> headMap(K toKey) {
/*  1072 */       synchronized (this.mutex) {
/*  1073 */         return Synchronized.sortedMap(delegate().headMap(toKey), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public K lastKey() {
/*  1078 */       synchronized (this.mutex) {
/*  1079 */         return delegate().lastKey();
/*       */       }
/*       */     }
/*       */ 
/*       */     public SortedMap<K, V> subMap(K fromKey, K toKey) {
/*  1084 */       synchronized (this.mutex) {
/*  1085 */         return Synchronized.sortedMap(delegate().subMap(fromKey, toKey), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public SortedMap<K, V> tailMap(K fromKey) {
/*  1090 */       synchronized (this.mutex) {
/*  1091 */         return Synchronized.sortedMap(delegate().tailMap(fromKey), this.mutex);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedMap<K, V> extends Synchronized.SynchronizedObject
/*       */     implements Map<K, V>
/*       */   {
/*       */     transient Set<K> keySet;
/*       */     transient Collection<V> values;
/*       */     transient Set<Map.Entry<K, V>> entrySet;
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedMap(Map<K, V> delegate, @Nullable Object mutex)
/*       */     {
/*   924 */       super(mutex);
/*       */     }
/*       */ 
/*       */     Map<K, V> delegate()
/*       */     {
/*   929 */       return (Map)super.delegate();
/*       */     }
/*       */ 
/*       */     public void clear()
/*       */     {
/*   934 */       synchronized (this.mutex) {
/*   935 */         delegate().clear();
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean containsKey(Object key)
/*       */     {
/*   941 */       synchronized (this.mutex) {
/*   942 */         return delegate().containsKey(key);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean containsValue(Object value)
/*       */     {
/*   948 */       synchronized (this.mutex) {
/*   949 */         return delegate().containsValue(value);
/*       */       }
/*       */     }
/*       */ 
/*       */     public Set<Map.Entry<K, V>> entrySet()
/*       */     {
/*   955 */       synchronized (this.mutex) {
/*   956 */         if (this.entrySet == null) {
/*   957 */           this.entrySet = Synchronized.set(delegate().entrySet(), this.mutex);
/*       */         }
/*   959 */         return this.entrySet;
/*       */       }
/*       */     }
/*       */ 
/*       */     public V get(Object key)
/*       */     {
/*   965 */       synchronized (this.mutex) {
/*   966 */         return delegate().get(key);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean isEmpty()
/*       */     {
/*   972 */       synchronized (this.mutex) {
/*   973 */         return delegate().isEmpty();
/*       */       }
/*       */     }
/*       */ 
/*       */     public Set<K> keySet()
/*       */     {
/*   979 */       synchronized (this.mutex) {
/*   980 */         if (this.keySet == null) {
/*   981 */           this.keySet = Synchronized.set(delegate().keySet(), this.mutex);
/*       */         }
/*   983 */         return this.keySet;
/*       */       }
/*       */     }
/*       */ 
/*       */     public V put(K key, V value)
/*       */     {
/*   989 */       synchronized (this.mutex) {
/*   990 */         return delegate().put(key, value);
/*       */       }
/*       */     }
/*       */ 
/*       */     public void putAll(Map<? extends K, ? extends V> map)
/*       */     {
/*   996 */       synchronized (this.mutex) {
/*   997 */         delegate().putAll(map);
/*       */       }
/*       */     }
/*       */ 
/*       */     public V remove(Object key)
/*       */     {
/*  1003 */       synchronized (this.mutex) {
/*  1004 */         return delegate().remove(key);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int size()
/*       */     {
/*  1010 */       synchronized (this.mutex) {
/*  1011 */         return delegate().size();
/*       */       }
/*       */     }
/*       */ 
/*       */     public Collection<V> values()
/*       */     {
/*  1017 */       synchronized (this.mutex) {
/*  1018 */         if (this.values == null) {
/*  1019 */           this.values = Synchronized.collection(delegate().values(), this.mutex);
/*       */         }
/*  1021 */         return this.values;
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean equals(Object o) {
/*  1026 */       if (o == this) {
/*  1027 */         return true;
/*       */       }
/*  1029 */       synchronized (this.mutex) {
/*  1030 */         return delegate().equals(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int hashCode() {
/*  1035 */       synchronized (this.mutex) {
/*  1036 */         return delegate().hashCode();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedAsMapEntries<K, V> extends Synchronized.SynchronizedSet<Map.Entry<K, Collection<V>>>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedAsMapEntries(Set<Map.Entry<K, Collection<V>>> delegate, @Nullable Object mutex)
/*       */     {
/*   838 */       super(mutex);
/*       */     }
/*       */ 
/*       */     public Iterator<Map.Entry<K, Collection<V>>> iterator()
/*       */     {
/*   843 */       final Iterator iterator = super.iterator();
/*   844 */       return new ForwardingIterator() {
/*       */         protected Iterator<Map.Entry<K, Collection<V>>> delegate() {
/*   846 */           return iterator;
/*       */         }
/*       */ 
/*       */         public Map.Entry<K, Collection<V>> next() {
/*   850 */           final Map.Entry entry = (Map.Entry)super.next();
/*   851 */           return new ForwardingMapEntry() {
/*       */             protected Map.Entry<K, Collection<V>> delegate() {
/*   853 */               return entry;
/*       */             }
/*       */             public Collection<V> getValue() {
/*   856 */               return Synchronized.typePreservingCollection((Collection)entry.getValue(), Synchronized.SynchronizedAsMapEntries.this.mutex);
/*       */             }
/*       */           };
/*       */         }
/*       */       };
/*       */     }
/*       */ 
/*       */     public Object[] toArray()
/*       */     {
/*   866 */       synchronized (this.mutex) {
/*   867 */         return ObjectArrays.toArrayImpl(delegate());
/*       */       }
/*       */     }
/*       */ 
/*   871 */     public <T> T[] toArray(T[] array) { synchronized (this.mutex) {
/*   872 */         return ObjectArrays.toArrayImpl(delegate(), array);
/*       */       } }
/*       */ 
/*       */     public boolean contains(Object o) {
/*   876 */       synchronized (this.mutex) {
/*   877 */         return Maps.containsEntryImpl(delegate(), o);
/*       */       }
/*       */     }
/*       */ 
/*   881 */     public boolean containsAll(Collection<?> c) { synchronized (this.mutex) {
/*   882 */         return Collections2.containsAllImpl(delegate(), c);
/*       */       } }
/*       */ 
/*       */     public boolean equals(Object o) {
/*   886 */       if (o == this) {
/*   887 */         return true;
/*       */       }
/*   889 */       synchronized (this.mutex) {
/*   890 */         return Sets.equalsImpl(delegate(), o);
/*       */       }
/*       */     }
/*       */ 
/*   894 */     public boolean remove(Object o) { synchronized (this.mutex) {
/*   895 */         return Maps.removeEntryImpl(delegate(), o);
/*       */       } }
/*       */ 
/*       */     public boolean removeAll(Collection<?> c) {
/*   899 */       synchronized (this.mutex) {
/*   900 */         return Iterators.removeAll(delegate().iterator(), c);
/*       */       }
/*       */     }
/*       */ 
/*   904 */     public boolean retainAll(Collection<?> c) { synchronized (this.mutex) {
/*   905 */         return Iterators.retainAll(delegate().iterator(), c);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedSortedSetMultimap<K, V> extends Synchronized.SynchronizedSetMultimap<K, V>
/*       */     implements SortedSetMultimap<K, V>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedSortedSetMultimap(SortedSetMultimap<K, V> delegate, @Nullable Object mutex)
/*       */     {
/*   781 */       super(mutex);
/*       */     }
/*       */     SortedSetMultimap<K, V> delegate() {
/*   784 */       return (SortedSetMultimap)super.delegate();
/*       */     }
/*       */     public SortedSet<V> get(K key) {
/*   787 */       synchronized (this.mutex) {
/*   788 */         return Synchronized.sortedSet(delegate().get(key), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*   792 */     public SortedSet<V> removeAll(Object key) { synchronized (this.mutex) {
/*   793 */         return delegate().removeAll(key);
/*       */       } }
/*       */ 
/*       */     public SortedSet<V> replaceValues(K key, Iterable<? extends V> values)
/*       */     {
/*   798 */       synchronized (this.mutex) {
/*   799 */         return delegate().replaceValues(key, values);
/*       */       }
/*       */     }
/*       */ 
/*       */     public Comparator<? super V> valueComparator() {
/*   804 */       synchronized (this.mutex) {
/*   805 */         return delegate().valueComparator();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedSetMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V>
/*       */     implements SetMultimap<K, V>
/*       */   {
/*       */     transient Set<Map.Entry<K, V>> entrySet;
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedSetMultimap(SetMultimap<K, V> delegate, @Nullable Object mutex)
/*       */     {
/*   737 */       super(mutex);
/*       */     }
/*       */     SetMultimap<K, V> delegate() {
/*   740 */       return (SetMultimap)super.delegate();
/*       */     }
/*       */     public Set<V> get(K key) {
/*   743 */       synchronized (this.mutex) {
/*   744 */         return Synchronized.set(delegate().get(key), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*   748 */     public Set<V> removeAll(Object key) { synchronized (this.mutex) {
/*   749 */         return delegate().removeAll(key);
/*       */       } }
/*       */ 
/*       */     public Set<V> replaceValues(K key, Iterable<? extends V> values)
/*       */     {
/*   754 */       synchronized (this.mutex) {
/*   755 */         return delegate().replaceValues(key, values);
/*       */       }
/*       */     }
/*       */ 
/*   759 */     public Set<Map.Entry<K, V>> entries() { synchronized (this.mutex) {
/*   760 */         if (this.entrySet == null) {
/*   761 */           this.entrySet = Synchronized.set(delegate().entries(), this.mutex);
/*       */         }
/*   763 */         return this.entrySet;
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedListMultimap<K, V> extends Synchronized.SynchronizedMultimap<K, V>
/*       */     implements ListMultimap<K, V>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedListMultimap(ListMultimap<K, V> delegate, @Nullable Object mutex)
/*       */     {
/*   698 */       super(mutex);
/*       */     }
/*       */     ListMultimap<K, V> delegate() {
/*   701 */       return (ListMultimap)super.delegate();
/*       */     }
/*       */     public List<V> get(K key) {
/*   704 */       synchronized (this.mutex) {
/*   705 */         return Synchronized.list(delegate().get(key), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*   709 */     public List<V> removeAll(Object key) { synchronized (this.mutex) {
/*   710 */         return delegate().removeAll(key);
/*       */       } }
/*       */ 
/*       */     public List<V> replaceValues(K key, Iterable<? extends V> values)
/*       */     {
/*   715 */       synchronized (this.mutex) {
/*   716 */         return delegate().replaceValues(key, values);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedMultimap<K, V> extends Synchronized.SynchronizedObject
/*       */     implements Multimap<K, V>
/*       */   {
/*       */     transient Set<K> keySet;
/*       */     transient Collection<V> valuesCollection;
/*       */     transient Collection<Map.Entry<K, V>> entries;
/*       */     transient Map<K, Collection<V>> asMap;
/*       */     transient Multiset<K> keys;
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     Multimap<K, V> delegate()
/*       */     {
/*   519 */       return (Multimap)super.delegate();
/*       */     }
/*       */ 
/*       */     SynchronizedMultimap(Multimap<K, V> delegate, @Nullable Object mutex) {
/*   523 */       super(mutex);
/*       */     }
/*       */ 
/*       */     public int size()
/*       */     {
/*   528 */       synchronized (this.mutex) {
/*   529 */         return delegate().size();
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean isEmpty()
/*       */     {
/*   535 */       synchronized (this.mutex) {
/*   536 */         return delegate().isEmpty();
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean containsKey(Object key)
/*       */     {
/*   542 */       synchronized (this.mutex) {
/*   543 */         return delegate().containsKey(key);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean containsValue(Object value)
/*       */     {
/*   549 */       synchronized (this.mutex) {
/*   550 */         return delegate().containsValue(value);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean containsEntry(Object key, Object value)
/*       */     {
/*   556 */       synchronized (this.mutex) {
/*   557 */         return delegate().containsEntry(key, value);
/*       */       }
/*       */     }
/*       */ 
/*       */     public Collection<V> get(K key)
/*       */     {
/*   563 */       synchronized (this.mutex) {
/*   564 */         return Synchronized.typePreservingCollection(delegate().get(key), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean put(K key, V value)
/*       */     {
/*   570 */       synchronized (this.mutex) {
/*   571 */         return delegate().put(key, value);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean putAll(K key, Iterable<? extends V> values)
/*       */     {
/*   577 */       synchronized (this.mutex) {
/*   578 */         return delegate().putAll(key, values);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*       */     {
/*   584 */       synchronized (this.mutex) {
/*   585 */         return delegate().putAll(multimap);
/*       */       }
/*       */     }
/*       */ 
/*       */     public Collection<V> replaceValues(K key, Iterable<? extends V> values)
/*       */     {
/*   591 */       synchronized (this.mutex) {
/*   592 */         return delegate().replaceValues(key, values);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean remove(Object key, Object value)
/*       */     {
/*   598 */       synchronized (this.mutex) {
/*   599 */         return delegate().remove(key, value);
/*       */       }
/*       */     }
/*       */ 
/*       */     public Collection<V> removeAll(Object key)
/*       */     {
/*   605 */       synchronized (this.mutex) {
/*   606 */         return delegate().removeAll(key);
/*       */       }
/*       */     }
/*       */ 
/*       */     public void clear()
/*       */     {
/*   612 */       synchronized (this.mutex) {
/*   613 */         delegate().clear();
/*       */       }
/*       */     }
/*       */ 
/*       */     public Set<K> keySet()
/*       */     {
/*   619 */       synchronized (this.mutex) {
/*   620 */         if (this.keySet == null) {
/*   621 */           this.keySet = Synchronized.typePreservingSet(delegate().keySet(), this.mutex);
/*       */         }
/*   623 */         return this.keySet;
/*       */       }
/*       */     }
/*       */ 
/*       */     public Collection<V> values()
/*       */     {
/*   629 */       synchronized (this.mutex) {
/*   630 */         if (this.valuesCollection == null) {
/*   631 */           this.valuesCollection = Synchronized.collection(delegate().values(), this.mutex);
/*       */         }
/*   633 */         return this.valuesCollection;
/*       */       }
/*       */     }
/*       */ 
/*       */     public Collection<Map.Entry<K, V>> entries()
/*       */     {
/*   639 */       synchronized (this.mutex) {
/*   640 */         if (this.entries == null) {
/*   641 */           this.entries = Synchronized.typePreservingCollection(delegate().entries(), this.mutex);
/*       */         }
/*   643 */         return this.entries;
/*       */       }
/*       */     }
/*       */ 
/*       */     public Map<K, Collection<V>> asMap()
/*       */     {
/*   649 */       synchronized (this.mutex) {
/*   650 */         if (this.asMap == null) {
/*   651 */           this.asMap = new Synchronized.SynchronizedAsMap(delegate().asMap(), this.mutex);
/*       */         }
/*   653 */         return this.asMap;
/*       */       }
/*       */     }
/*       */ 
/*       */     public Multiset<K> keys()
/*       */     {
/*   659 */       synchronized (this.mutex) {
/*   660 */         if (this.keys == null) {
/*   661 */           this.keys = Synchronized.multiset(delegate().keys(), this.mutex);
/*       */         }
/*   663 */         return this.keys;
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean equals(Object o) {
/*   668 */       if (o == this) {
/*   669 */         return true;
/*       */       }
/*   671 */       synchronized (this.mutex) {
/*   672 */         return delegate().equals(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int hashCode() {
/*   677 */       synchronized (this.mutex) {
/*   678 */         return delegate().hashCode();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedMultiset<E> extends Synchronized.SynchronizedCollection<E>
/*       */     implements Multiset<E>
/*       */   {
/*       */     transient Set<E> elementSet;
/*       */     transient Set<Multiset.Entry<E>> entrySet;
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedMultiset(Multiset<E> delegate, @Nullable Object mutex)
/*       */     {
/*   420 */       super(mutex, null);
/*       */     }
/*       */ 
/*       */     Multiset<E> delegate() {
/*   424 */       return (Multiset)super.delegate();
/*       */     }
/*       */ 
/*       */     public int count(Object o)
/*       */     {
/*   429 */       synchronized (this.mutex) {
/*   430 */         return delegate().count(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int add(E e, int n)
/*       */     {
/*   436 */       synchronized (this.mutex) {
/*   437 */         return delegate().add(e, n);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int remove(Object o, int n)
/*       */     {
/*   443 */       synchronized (this.mutex) {
/*   444 */         return delegate().remove(o, n);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int setCount(E element, int count)
/*       */     {
/*   450 */       synchronized (this.mutex) {
/*   451 */         return delegate().setCount(element, count);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean setCount(E element, int oldCount, int newCount)
/*       */     {
/*   457 */       synchronized (this.mutex) {
/*   458 */         return delegate().setCount(element, oldCount, newCount);
/*       */       }
/*       */     }
/*       */ 
/*       */     public Set<E> elementSet()
/*       */     {
/*   464 */       synchronized (this.mutex) {
/*   465 */         if (this.elementSet == null) {
/*   466 */           this.elementSet = Synchronized.typePreservingSet(delegate().elementSet(), this.mutex);
/*       */         }
/*   468 */         return this.elementSet;
/*       */       }
/*       */     }
/*       */ 
/*       */     public Set<Multiset.Entry<E>> entrySet()
/*       */     {
/*   474 */       synchronized (this.mutex) {
/*   475 */         if (this.entrySet == null) {
/*   476 */           this.entrySet = Synchronized.typePreservingSet(delegate().entrySet(), this.mutex);
/*       */         }
/*   478 */         return this.entrySet;
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean equals(Object o) {
/*   483 */       if (o == this) {
/*   484 */         return true;
/*       */       }
/*   486 */       synchronized (this.mutex) {
/*   487 */         return delegate().equals(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int hashCode() {
/*   492 */       synchronized (this.mutex) {
/*   493 */         return delegate().hashCode();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedRandomAccessList<E> extends Synchronized.SynchronizedList<E>
/*       */     implements RandomAccess
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedRandomAccessList(List<E> list, @Nullable Object mutex)
/*       */     {
/*   400 */       super(mutex);
/*       */     }
/*       */   }
/*       */ 
/*       */   private static class SynchronizedList<E> extends Synchronized.SynchronizedCollection<E>
/*       */     implements List<E>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedList(List<E> delegate, @Nullable Object mutex)
/*       */     {
/*   306 */       super(mutex, null);
/*       */     }
/*       */ 
/*       */     List<E> delegate() {
/*   310 */       return (List)super.delegate();
/*       */     }
/*       */ 
/*       */     public void add(int index, E element)
/*       */     {
/*   315 */       synchronized (this.mutex) {
/*   316 */         delegate().add(index, element);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean addAll(int index, Collection<? extends E> c)
/*       */     {
/*   322 */       synchronized (this.mutex) {
/*   323 */         return delegate().addAll(index, c);
/*       */       }
/*       */     }
/*       */ 
/*       */     public E get(int index)
/*       */     {
/*   329 */       synchronized (this.mutex) {
/*   330 */         return delegate().get(index);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int indexOf(Object o)
/*       */     {
/*   336 */       synchronized (this.mutex) {
/*   337 */         return delegate().indexOf(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int lastIndexOf(Object o)
/*       */     {
/*   343 */       synchronized (this.mutex) {
/*   344 */         return delegate().lastIndexOf(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public ListIterator<E> listIterator()
/*       */     {
/*   350 */       return delegate().listIterator();
/*       */     }
/*       */ 
/*       */     public ListIterator<E> listIterator(int index)
/*       */     {
/*   355 */       return delegate().listIterator(index);
/*       */     }
/*       */ 
/*       */     public E remove(int index)
/*       */     {
/*   360 */       synchronized (this.mutex) {
/*   361 */         return delegate().remove(index);
/*       */       }
/*       */     }
/*       */ 
/*       */     public E set(int index, E element)
/*       */     {
/*   367 */       synchronized (this.mutex) {
/*   368 */         return delegate().set(index, element);
/*       */       }
/*       */     }
/*       */ 
/*       */     public List<E> subList(int fromIndex, int toIndex)
/*       */     {
/*   374 */       synchronized (this.mutex) {
/*   375 */         return Synchronized.list(delegate().subList(fromIndex, toIndex), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean equals(Object o) {
/*   380 */       if (o == this) {
/*   381 */         return true;
/*       */       }
/*   383 */       synchronized (this.mutex) {
/*   384 */         return delegate().equals(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int hashCode() {
/*   389 */       synchronized (this.mutex) {
/*   390 */         return delegate().hashCode();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   static class SynchronizedSortedSet<E> extends Synchronized.SynchronizedSet<E>
/*       */     implements SortedSet<E>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedSortedSet(SortedSet<E> delegate, @Nullable Object mutex)
/*       */     {
/*   245 */       super(mutex);
/*       */     }
/*       */ 
/*       */     SortedSet<E> delegate() {
/*   249 */       return (SortedSet)super.delegate();
/*       */     }
/*       */ 
/*       */     public Comparator<? super E> comparator()
/*       */     {
/*   254 */       synchronized (this.mutex) {
/*   255 */         return delegate().comparator();
/*       */       }
/*       */     }
/*       */ 
/*       */     public SortedSet<E> subSet(E fromElement, E toElement)
/*       */     {
/*   261 */       synchronized (this.mutex) {
/*   262 */         return Synchronized.sortedSet(delegate().subSet(fromElement, toElement), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public SortedSet<E> headSet(E toElement)
/*       */     {
/*   268 */       synchronized (this.mutex) {
/*   269 */         return Synchronized.sortedSet(delegate().headSet(toElement), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public SortedSet<E> tailSet(E fromElement)
/*       */     {
/*   275 */       synchronized (this.mutex) {
/*   276 */         return Synchronized.sortedSet(delegate().tailSet(fromElement), this.mutex);
/*       */       }
/*       */     }
/*       */ 
/*       */     public E first()
/*       */     {
/*   282 */       synchronized (this.mutex) {
/*   283 */         return delegate().first();
/*       */       }
/*       */     }
/*       */ 
/*       */     public E last()
/*       */     {
/*   289 */       synchronized (this.mutex) {
/*   290 */         return delegate().last();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   static class SynchronizedSet<E> extends Synchronized.SynchronizedCollection<E>
/*       */     implements Set<E>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedSet(Set<E> delegate, @Nullable Object mutex)
/*       */     {
/*   212 */       super(mutex, null);
/*       */     }
/*       */ 
/*       */     Set<E> delegate() {
/*   216 */       return (Set)super.delegate();
/*       */     }
/*       */ 
/*       */     public boolean equals(Object o) {
/*   220 */       if (o == this) {
/*   221 */         return true;
/*       */       }
/*   223 */       synchronized (this.mutex) {
/*   224 */         return delegate().equals(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int hashCode() {
/*   229 */       synchronized (this.mutex) {
/*   230 */         return delegate().hashCode();
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   @VisibleForTesting
/*       */   static class SynchronizedCollection<E> extends Synchronized.SynchronizedObject
/*       */     implements Collection<E>
/*       */   {
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     private SynchronizedCollection(Collection<E> delegate, @Nullable Object mutex)
/*       */     {
/*   104 */       super(mutex);
/*       */     }
/*       */ 
/*       */     Collection<E> delegate()
/*       */     {
/*   109 */       return (Collection)super.delegate();
/*       */     }
/*       */ 
/*       */     public boolean add(E e)
/*       */     {
/*   114 */       synchronized (this.mutex) {
/*   115 */         return delegate().add(e);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean addAll(Collection<? extends E> c)
/*       */     {
/*   121 */       synchronized (this.mutex) {
/*   122 */         return delegate().addAll(c);
/*       */       }
/*       */     }
/*       */ 
/*       */     public void clear()
/*       */     {
/*   128 */       synchronized (this.mutex) {
/*   129 */         delegate().clear();
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean contains(Object o)
/*       */     {
/*   135 */       synchronized (this.mutex) {
/*   136 */         return delegate().contains(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean containsAll(Collection<?> c)
/*       */     {
/*   142 */       synchronized (this.mutex) {
/*   143 */         return delegate().containsAll(c);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean isEmpty()
/*       */     {
/*   149 */       synchronized (this.mutex) {
/*   150 */         return delegate().isEmpty();
/*       */       }
/*       */     }
/*       */ 
/*       */     public Iterator<E> iterator()
/*       */     {
/*   156 */       return delegate().iterator();
/*       */     }
/*       */ 
/*       */     public boolean remove(Object o)
/*       */     {
/*   161 */       synchronized (this.mutex) {
/*   162 */         return delegate().remove(o);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean removeAll(Collection<?> c)
/*       */     {
/*   168 */       synchronized (this.mutex) {
/*   169 */         return delegate().removeAll(c);
/*       */       }
/*       */     }
/*       */ 
/*       */     public boolean retainAll(Collection<?> c)
/*       */     {
/*   175 */       synchronized (this.mutex) {
/*   176 */         return delegate().retainAll(c);
/*       */       }
/*       */     }
/*       */ 
/*       */     public int size()
/*       */     {
/*   182 */       synchronized (this.mutex) {
/*   183 */         return delegate().size();
/*       */       }
/*       */     }
/*       */ 
/*       */     public Object[] toArray()
/*       */     {
/*   189 */       synchronized (this.mutex) {
/*   190 */         return delegate().toArray();
/*       */       }
/*       */     }
/*       */ 
/*       */     public <T> T[] toArray(T[] a)
/*       */     {
/*   196 */       synchronized (this.mutex) {
/*   197 */         return delegate().toArray(a);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   static class SynchronizedObject
/*       */     implements Serializable
/*       */   {
/*       */     final Object delegate;
/*       */     final Object mutex;
/*       */ 
/*       */     @GwtIncompatible("not needed in emulated source")
/*       */     private static final long serialVersionUID = 0L;
/*       */ 
/*       */     SynchronizedObject(Object delegate, @Nullable Object mutex)
/*       */     {
/*    63 */       this.delegate = Preconditions.checkNotNull(delegate);
/*    64 */       this.mutex = (mutex == null ? this : mutex);
/*       */     }
/*       */ 
/*       */     Object delegate() {
/*    68 */       return this.delegate;
/*       */     }
/*       */ 
/*       */     public String toString()
/*       */     {
/*    74 */       synchronized (this.mutex) {
/*    75 */         return this.delegate.toString();
/*       */       }
/*       */     }
/*       */ 
/*       */     @GwtIncompatible("java.io.ObjectOutputStream")
/*       */     private void writeObject(ObjectOutputStream stream)
/*       */       throws IOException
/*       */     {
/*    86 */       synchronized (this.mutex) {
/*    87 */         stream.defaultWriteObject();
/*       */       }
/*       */     }
/*       */   }
/*       */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Synchronized
 * JD-Core Version:    0.6.2
 */