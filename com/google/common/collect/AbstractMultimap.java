/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.base.Preconditions;
/*      */ import java.io.Serializable;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.Collection;
/*      */ import java.util.Collections;
/*      */ import java.util.Comparator;
/*      */ import java.util.ConcurrentModificationException;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.ListIterator;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.RandomAccess;
/*      */ import java.util.Set;
/*      */ import java.util.SortedMap;
/*      */ import java.util.SortedSet;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible
/*      */ abstract class AbstractMultimap<K, V>
/*      */   implements Multimap<K, V>, Serializable
/*      */ {
/*      */   private transient Map<K, Collection<V>> map;
/*      */   private transient int totalSize;
/*      */   private transient Set<K> keySet;
/*      */   private transient Multiset<K> multiset;
/*      */   private transient Collection<V> valuesCollection;
/*      */   private transient Collection<Map.Entry<K, V>> entries;
/*      */   private transient Map<K, Collection<V>> asMap;
/*      */   private static final long serialVersionUID = 2447537837011683357L;
/*      */ 
/*      */   protected AbstractMultimap(Map<K, Collection<V>> map)
/*      */   {
/*  118 */     Preconditions.checkArgument(map.isEmpty());
/*  119 */     this.map = map;
/*      */   }
/*      */ 
/*      */   final void setMap(Map<K, Collection<V>> map)
/*      */   {
/*  124 */     this.map = map;
/*  125 */     this.totalSize = 0;
/*  126 */     for (Collection values : map.values()) {
/*  127 */       Preconditions.checkArgument(!values.isEmpty());
/*  128 */       this.totalSize += values.size();
/*      */     }
/*      */   }
/*      */ 
/*      */   abstract Collection<V> createCollection();
/*      */ 
/*      */   Collection<V> createCollection(@Nullable K key)
/*      */   {
/*  155 */     return createCollection();
/*      */   }
/*      */ 
/*      */   Map<K, Collection<V>> backingMap() {
/*  159 */     return this.map;
/*      */   }
/*      */ 
/*      */   public int size()
/*      */   {
/*  166 */     return this.totalSize;
/*      */   }
/*      */ 
/*      */   public boolean isEmpty()
/*      */   {
/*  171 */     return this.totalSize == 0;
/*      */   }
/*      */ 
/*      */   public boolean containsKey(@Nullable Object key)
/*      */   {
/*  176 */     return this.map.containsKey(key);
/*      */   }
/*      */ 
/*      */   public boolean containsValue(@Nullable Object value)
/*      */   {
/*  181 */     for (Collection collection : this.map.values()) {
/*  182 */       if (collection.contains(value)) {
/*  183 */         return true;
/*      */       }
/*      */     }
/*      */ 
/*  187 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean containsEntry(@Nullable Object key, @Nullable Object value)
/*      */   {
/*  192 */     Collection collection = (Collection)this.map.get(key);
/*  193 */     return (collection != null) && (collection.contains(value));
/*      */   }
/*      */ 
/*      */   public boolean put(@Nullable K key, @Nullable V value)
/*      */   {
/*  200 */     Collection collection = (Collection)this.map.get(key);
/*  201 */     if (collection == null) {
/*  202 */       collection = createCollection(key);
/*  203 */       if (collection.add(value)) {
/*  204 */         this.totalSize += 1;
/*  205 */         this.map.put(key, collection);
/*  206 */         return true;
/*      */       }
/*  208 */       throw new AssertionError("New Collection violated the Collection spec");
/*      */     }
/*  210 */     if (collection.add(value)) {
/*  211 */       this.totalSize += 1;
/*  212 */       return true;
/*      */     }
/*  214 */     return false;
/*      */   }
/*      */ 
/*      */   private Collection<V> getOrCreateCollection(@Nullable K key)
/*      */   {
/*  219 */     Collection collection = (Collection)this.map.get(key);
/*  220 */     if (collection == null) {
/*  221 */       collection = createCollection(key);
/*  222 */       this.map.put(key, collection);
/*      */     }
/*  224 */     return collection;
/*      */   }
/*      */ 
/*      */   public boolean remove(@Nullable Object key, @Nullable Object value)
/*      */   {
/*  229 */     Collection collection = (Collection)this.map.get(key);
/*  230 */     if (collection == null) {
/*  231 */       return false;
/*      */     }
/*      */ 
/*  234 */     boolean changed = collection.remove(value);
/*  235 */     if (changed) {
/*  236 */       this.totalSize -= 1;
/*  237 */       if (collection.isEmpty()) {
/*  238 */         this.map.remove(key);
/*      */       }
/*      */     }
/*  241 */     return changed;
/*      */   }
/*      */ 
/*      */   public boolean putAll(@Nullable K key, Iterable<? extends V> values)
/*      */   {
/*  248 */     if (!values.iterator().hasNext()) {
/*  249 */       return false;
/*      */     }
/*      */ 
/*  252 */     Collection collection = getOrCreateCollection(key);
/*  253 */     int oldSize = collection.size();
/*      */ 
/*  255 */     boolean changed = false;
/*      */     Iterator i$;
/*  256 */     if ((values instanceof Collection)) {
/*  257 */       Collection c = Collections2.cast(values);
/*  258 */       changed = collection.addAll(c);
/*      */     } else {
/*  260 */       for (i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/*  261 */         changed |= collection.add(value);
/*      */       }
/*      */     }
/*      */ 
/*  265 */     this.totalSize += collection.size() - oldSize;
/*  266 */     return changed;
/*      */   }
/*      */ 
/*      */   public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*      */   {
/*  271 */     boolean changed = false;
/*  272 */     for (Map.Entry entry : multimap.entries()) {
/*  273 */       changed |= put(entry.getKey(), entry.getValue());
/*      */     }
/*  275 */     return changed;
/*      */   }
/*      */ 
/*      */   public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values)
/*      */   {
/*  286 */     Iterator iterator = values.iterator();
/*  287 */     if (!iterator.hasNext()) {
/*  288 */       return removeAll(key);
/*      */     }
/*      */ 
/*  292 */     Collection collection = getOrCreateCollection(key);
/*  293 */     Collection oldValues = createCollection();
/*  294 */     oldValues.addAll(collection);
/*      */ 
/*  296 */     this.totalSize -= collection.size();
/*  297 */     collection.clear();
/*      */ 
/*  299 */     while (iterator.hasNext()) {
/*  300 */       if (collection.add(iterator.next())) {
/*  301 */         this.totalSize += 1;
/*      */       }
/*      */     }
/*      */ 
/*  305 */     return unmodifiableCollectionSubclass(oldValues);
/*      */   }
/*      */ 
/*      */   public Collection<V> removeAll(@Nullable Object key)
/*      */   {
/*  315 */     Collection collection = (Collection)this.map.remove(key);
/*  316 */     Collection output = createCollection();
/*      */ 
/*  318 */     if (collection != null) {
/*  319 */       output.addAll(collection);
/*  320 */       this.totalSize -= collection.size();
/*  321 */       collection.clear();
/*      */     }
/*      */ 
/*  324 */     return unmodifiableCollectionSubclass(output);
/*      */   }
/*      */ 
/*      */   private Collection<V> unmodifiableCollectionSubclass(Collection<V> collection)
/*      */   {
/*  329 */     if ((collection instanceof SortedSet))
/*  330 */       return Collections.unmodifiableSortedSet((SortedSet)collection);
/*  331 */     if ((collection instanceof Set))
/*  332 */       return Collections.unmodifiableSet((Set)collection);
/*  333 */     if ((collection instanceof List)) {
/*  334 */       return Collections.unmodifiableList((List)collection);
/*      */     }
/*  336 */     return Collections.unmodifiableCollection(collection);
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  343 */     for (Collection collection : this.map.values()) {
/*  344 */       collection.clear();
/*      */     }
/*  346 */     this.map.clear();
/*  347 */     this.totalSize = 0;
/*      */   }
/*      */ 
/*      */   public Collection<V> get(@Nullable K key)
/*      */   {
/*  359 */     Collection collection = (Collection)this.map.get(key);
/*  360 */     if (collection == null) {
/*  361 */       collection = createCollection(key);
/*      */     }
/*  363 */     return wrapCollection(key, collection);
/*      */   }
/*      */ 
/*      */   private Collection<V> wrapCollection(@Nullable K key, Collection<V> collection)
/*      */   {
/*  373 */     if ((collection instanceof SortedSet))
/*  374 */       return new WrappedSortedSet(key, (SortedSet)collection, null);
/*  375 */     if ((collection instanceof Set))
/*  376 */       return new WrappedSet(key, (Set)collection);
/*  377 */     if ((collection instanceof List)) {
/*  378 */       return wrapList(key, (List)collection, null);
/*      */     }
/*  380 */     return new WrappedCollection(key, collection, null);
/*      */   }
/*      */ 
/*      */   private List<V> wrapList(@Nullable K key, List<V> list, @Nullable AbstractMultimap<K, V>.WrappedCollection ancestor)
/*      */   {
/*  386 */     return (list instanceof RandomAccess) ? new RandomAccessWrappedList(key, list, ancestor) : new WrappedList(key, list, ancestor);
/*      */   }
/*      */ 
/*      */   private Iterator<V> iteratorOrListIterator(Collection<V> collection)
/*      */   {
/*  649 */     return (collection instanceof List) ? ((List)collection).listIterator() : collection.iterator();
/*      */   }
/*      */ 
/*      */   public Set<K> keySet()
/*      */   {
/*  875 */     Set result = this.keySet;
/*  876 */     return result == null ? (this.keySet = createKeySet()) : result;
/*      */   }
/*      */ 
/*      */   private Set<K> createKeySet() {
/*  880 */     return (this.map instanceof SortedMap) ? new SortedKeySet((SortedMap)this.map) : new KeySet(this.map);
/*      */   }
/*      */ 
/*      */   public Multiset<K> keys()
/*      */   {
/* 1003 */     Multiset result = this.multiset;
/* 1004 */     if (result == null) {
/* 1005 */       return this.multiset = new Multimaps.Keys() {
/*      */         Multimap<K, V> multimap() {
/* 1007 */           return AbstractMultimap.this;
/*      */         }
/*      */       };
/*      */     }
/* 1011 */     return result;
/*      */   }
/*      */ 
/*      */   private int removeValuesForKey(Object key)
/*      */   {
/*      */     Collection collection;
/*      */     try
/*      */     {
/* 1021 */       collection = (Collection)this.map.remove(key);
/*      */     } catch (NullPointerException e) {
/* 1023 */       return 0;
/*      */     } catch (ClassCastException e) {
/* 1025 */       return 0;
/*      */     }
/*      */ 
/* 1028 */     int count = 0;
/* 1029 */     if (collection != null) {
/* 1030 */       count = collection.size();
/* 1031 */       collection.clear();
/* 1032 */       this.totalSize -= count;
/*      */     }
/* 1034 */     return count;
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/* 1046 */     Collection result = this.valuesCollection;
/* 1047 */     if (result == null) {
/* 1048 */       return this.valuesCollection = new Multimaps.Values() {
/*      */         Multimap<K, V> multimap() {
/* 1050 */           return AbstractMultimap.this;
/*      */         }
/*      */       };
/*      */     }
/* 1054 */     return result;
/*      */   }
/*      */ 
/*      */   public Collection<Map.Entry<K, V>> entries()
/*      */   {
/* 1077 */     Collection result = this.entries;
/* 1078 */     return result == null ? (this.entries = createEntries()) : result;
/*      */   }
/*      */ 
/*      */   Collection<Map.Entry<K, V>> createEntries() {
/* 1082 */     if ((this instanceof SetMultimap)) {
/* 1083 */       return new Multimaps.EntrySet() {
/*      */         Multimap<K, V> multimap() {
/* 1085 */           return AbstractMultimap.this;
/*      */         }
/*      */ 
/*      */         public Iterator<Map.Entry<K, V>> iterator() {
/* 1089 */           return AbstractMultimap.this.createEntryIterator();
/*      */         }
/*      */       };
/*      */     }
/* 1093 */     return new Multimaps.Entries() {
/*      */       Multimap<K, V> multimap() {
/* 1095 */         return AbstractMultimap.this;
/*      */       }
/*      */ 
/*      */       public Iterator<Map.Entry<K, V>> iterator() {
/* 1099 */         return AbstractMultimap.this.createEntryIterator();
/*      */       }
/*      */     };
/*      */   }
/*      */ 
/*      */   Iterator<Map.Entry<K, V>> createEntryIterator()
/*      */   {
/* 1113 */     return new EntryIterator();
/*      */   }
/*      */ 
/*      */   public Map<K, Collection<V>> asMap()
/*      */   {
/* 1166 */     Map result = this.asMap;
/* 1167 */     return result == null ? (this.asMap = createAsMap()) : result;
/*      */   }
/*      */ 
/*      */   private Map<K, Collection<V>> createAsMap() {
/* 1171 */     return (this.map instanceof SortedMap) ? new SortedAsMap((SortedMap)this.map) : new AsMap(this.map);
/*      */   }
/*      */ 
/*      */   public boolean equals(@Nullable Object object)
/*      */   {
/* 1361 */     if (object == this) {
/* 1362 */       return true;
/*      */     }
/* 1364 */     if ((object instanceof Multimap)) {
/* 1365 */       Multimap that = (Multimap)object;
/* 1366 */       return this.map.equals(that.asMap());
/*      */     }
/* 1368 */     return false;
/*      */   }
/*      */ 
/*      */   public int hashCode()
/*      */   {
/* 1380 */     return this.map.hashCode();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/* 1391 */     return this.map.toString();
/*      */   }
/*      */ 
/*      */   private class SortedAsMap extends AbstractMultimap<K, V>.AsMap
/*      */     implements SortedMap<K, Collection<V>>
/*      */   {
/*      */     SortedSet<K> sortedKeySet;
/*      */ 
/*      */     SortedAsMap()
/*      */     {
/* 1310 */       super(submap);
/*      */     }
/*      */ 
/*      */     SortedMap<K, Collection<V>> sortedMap() {
/* 1314 */       return (SortedMap)this.submap;
/*      */     }
/*      */ 
/*      */     public Comparator<? super K> comparator()
/*      */     {
/* 1319 */       return sortedMap().comparator();
/*      */     }
/*      */ 
/*      */     public K firstKey()
/*      */     {
/* 1324 */       return sortedMap().firstKey();
/*      */     }
/*      */ 
/*      */     public K lastKey()
/*      */     {
/* 1329 */       return sortedMap().lastKey();
/*      */     }
/*      */ 
/*      */     public SortedMap<K, Collection<V>> headMap(K toKey)
/*      */     {
/* 1334 */       return new SortedAsMap(AbstractMultimap.this, sortedMap().headMap(toKey));
/*      */     }
/*      */ 
/*      */     public SortedMap<K, Collection<V>> subMap(K fromKey, K toKey)
/*      */     {
/* 1339 */       return new SortedAsMap(AbstractMultimap.this, sortedMap().subMap(fromKey, toKey));
/*      */     }
/*      */ 
/*      */     public SortedMap<K, Collection<V>> tailMap(K fromKey)
/*      */     {
/* 1344 */       return new SortedAsMap(AbstractMultimap.this, sortedMap().tailMap(fromKey));
/*      */     }
/*      */ 
/*      */     public SortedSet<K> keySet()
/*      */     {
/* 1352 */       SortedSet result = this.sortedKeySet;
/* 1353 */       return result == null ? (this.sortedKeySet = new AbstractMultimap.SortedKeySet(AbstractMultimap.this, sortedMap())) : result;
/*      */     }
/*      */   }
/*      */ 
/*      */   private class AsMap extends AbstractMap<K, Collection<V>>
/*      */   {
/*      */     final transient Map<K, Collection<V>> submap;
/*      */     transient Set<Map.Entry<K, Collection<V>>> entrySet;
/*      */ 
/*      */     AsMap()
/*      */     {
/* 1183 */       this.submap = submap;
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<K, Collection<V>>> entrySet()
/*      */     {
/* 1189 */       Set result = this.entrySet;
/* 1190 */       return result == null ? (this.entrySet = new AsMapEntries()) : result;
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key)
/*      */     {
/* 1196 */       return Maps.safeContainsKey(this.submap, key);
/*      */     }
/*      */ 
/*      */     public Collection<V> get(Object key) {
/* 1200 */       Collection collection = (Collection)Maps.safeGet(this.submap, key);
/* 1201 */       if (collection == null) {
/* 1202 */         return null;
/*      */       }
/*      */ 
/* 1205 */       Object k = key;
/* 1206 */       return AbstractMultimap.this.wrapCollection(k, collection);
/*      */     }
/*      */ 
/*      */     public Set<K> keySet() {
/* 1210 */       return AbstractMultimap.this.keySet();
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/* 1215 */       return this.submap.size();
/*      */     }
/*      */ 
/*      */     public Collection<V> remove(Object key) {
/* 1219 */       Collection collection = (Collection)this.submap.remove(key);
/* 1220 */       if (collection == null) {
/* 1221 */         return null;
/*      */       }
/*      */ 
/* 1224 */       Collection output = AbstractMultimap.this.createCollection();
/* 1225 */       output.addAll(collection);
/* 1226 */       AbstractMultimap.access$220(AbstractMultimap.this, collection.size());
/* 1227 */       collection.clear();
/* 1228 */       return output;
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/* 1232 */       return (this == object) || (this.submap.equals(object));
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/* 1236 */       return this.submap.hashCode();
/*      */     }
/*      */ 
/*      */     public String toString() {
/* 1240 */       return this.submap.toString();
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/* 1245 */       if (this.submap == AbstractMultimap.this.map) {
/* 1246 */         AbstractMultimap.this.clear();
/*      */       }
/*      */       else
/* 1249 */         Iterators.clear(new AsMapIterator());
/*      */     }
/*      */ 
/*      */     class AsMapIterator
/*      */       implements Iterator<Map.Entry<K, Collection<V>>>
/*      */     {
/* 1281 */       final Iterator<Map.Entry<K, Collection<V>>> delegateIterator = AbstractMultimap.AsMap.this.submap.entrySet().iterator();
/*      */       Collection<V> collection;
/*      */ 
/*      */       AsMapIterator()
/*      */       {
/*      */       }
/*      */ 
/*      */       public boolean hasNext()
/*      */       {
/* 1287 */         return this.delegateIterator.hasNext();
/*      */       }
/*      */ 
/*      */       public Map.Entry<K, Collection<V>> next()
/*      */       {
/* 1292 */         Map.Entry entry = (Map.Entry)this.delegateIterator.next();
/* 1293 */         Object key = entry.getKey();
/* 1294 */         this.collection = ((Collection)entry.getValue());
/* 1295 */         return Maps.immutableEntry(key, AbstractMultimap.this.wrapCollection(key, this.collection));
/*      */       }
/*      */ 
/*      */       public void remove()
/*      */       {
/* 1300 */         this.delegateIterator.remove();
/* 1301 */         AbstractMultimap.access$220(AbstractMultimap.this, this.collection.size());
/* 1302 */         this.collection.clear();
/*      */       }
/*      */     }
/*      */ 
/*      */     class AsMapEntries extends Maps.EntrySet<K, Collection<V>>
/*      */     {
/*      */       AsMapEntries()
/*      */       {
/*      */       }
/*      */ 
/*      */       Map<K, Collection<V>> map()
/*      */       {
/* 1256 */         return AbstractMultimap.AsMap.this;
/*      */       }
/*      */ 
/*      */       public Iterator<Map.Entry<K, Collection<V>>> iterator() {
/* 1260 */         return new AbstractMultimap.AsMap.AsMapIterator(AbstractMultimap.AsMap.this);
/*      */       }
/*      */ 
/*      */       public boolean contains(Object o)
/*      */       {
/* 1266 */         return Collections2.safeContains(AbstractMultimap.AsMap.this.submap.entrySet(), o);
/*      */       }
/*      */ 
/*      */       public boolean remove(Object o) {
/* 1270 */         if (!contains(o)) {
/* 1271 */           return false;
/*      */         }
/* 1273 */         Map.Entry entry = (Map.Entry)o;
/* 1274 */         AbstractMultimap.this.removeValuesForKey(entry.getKey());
/* 1275 */         return true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class EntryIterator
/*      */     implements Iterator<Map.Entry<K, V>>
/*      */   {
/*      */     final Iterator<Map.Entry<K, Collection<V>>> keyIterator;
/*      */     K key;
/*      */     Collection<V> collection;
/*      */     Iterator<V> valueIterator;
/*      */ 
/*      */     EntryIterator()
/*      */     {
/* 1124 */       this.keyIterator = AbstractMultimap.this.map.entrySet().iterator();
/* 1125 */       if (this.keyIterator.hasNext())
/* 1126 */         findValueIteratorAndKey();
/*      */       else
/* 1128 */         this.valueIterator = Iterators.emptyModifiableIterator();
/*      */     }
/*      */ 
/*      */     void findValueIteratorAndKey()
/*      */     {
/* 1133 */       Map.Entry entry = (Map.Entry)this.keyIterator.next();
/* 1134 */       this.key = entry.getKey();
/* 1135 */       this.collection = ((Collection)entry.getValue());
/* 1136 */       this.valueIterator = this.collection.iterator();
/*      */     }
/*      */ 
/*      */     public boolean hasNext()
/*      */     {
/* 1141 */       return (this.keyIterator.hasNext()) || (this.valueIterator.hasNext());
/*      */     }
/*      */ 
/*      */     public Map.Entry<K, V> next()
/*      */     {
/* 1146 */       if (!this.valueIterator.hasNext()) {
/* 1147 */         findValueIteratorAndKey();
/*      */       }
/* 1149 */       return Maps.immutableEntry(this.key, this.valueIterator.next());
/*      */     }
/*      */ 
/*      */     public void remove()
/*      */     {
/* 1154 */       this.valueIterator.remove();
/* 1155 */       if (this.collection.isEmpty()) {
/* 1156 */         this.keyIterator.remove();
/*      */       }
/* 1158 */       AbstractMultimap.access$210(AbstractMultimap.this);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class SortedKeySet extends AbstractMultimap<K, V>.KeySet
/*      */     implements SortedSet<K>
/*      */   {
/*      */     SortedKeySet()
/*      */     {
/*  961 */       super(subMap);
/*      */     }
/*      */ 
/*      */     SortedMap<K, Collection<V>> sortedMap() {
/*  965 */       return (SortedMap)this.subMap;
/*      */     }
/*      */ 
/*      */     public Comparator<? super K> comparator()
/*      */     {
/*  970 */       return sortedMap().comparator();
/*      */     }
/*      */ 
/*      */     public K first()
/*      */     {
/*  975 */       return sortedMap().firstKey();
/*      */     }
/*      */ 
/*      */     public SortedSet<K> headSet(K toElement)
/*      */     {
/*  980 */       return new SortedKeySet(AbstractMultimap.this, sortedMap().headMap(toElement));
/*      */     }
/*      */ 
/*      */     public K last()
/*      */     {
/*  985 */       return sortedMap().lastKey();
/*      */     }
/*      */ 
/*      */     public SortedSet<K> subSet(K fromElement, K toElement)
/*      */     {
/*  990 */       return new SortedKeySet(AbstractMultimap.this, sortedMap().subMap(fromElement, toElement));
/*      */     }
/*      */ 
/*      */     public SortedSet<K> tailSet(K fromElement)
/*      */     {
/*  995 */       return new SortedKeySet(AbstractMultimap.this, sortedMap().tailMap(fromElement));
/*      */     }
/*      */   }
/*      */ 
/*      */   private class KeySet extends Maps.KeySet<K, Collection<V>>
/*      */   {
/*      */     final Map<K, Collection<V>> subMap;
/*      */ 
/*      */     KeySet()
/*      */     {
/*  893 */       this.subMap = subMap;
/*      */     }
/*      */ 
/*      */     Map<K, Collection<V>> map()
/*      */     {
/*  898 */       return this.subMap;
/*      */     }
/*      */ 
/*      */     public Iterator<K> iterator() {
/*  902 */       return new Iterator() {
/*  903 */         final Iterator<Map.Entry<K, Collection<V>>> entryIterator = AbstractMultimap.KeySet.this.subMap.entrySet().iterator();
/*      */         Map.Entry<K, Collection<V>> entry;
/*      */ 
/*      */         public boolean hasNext()
/*      */         {
/*  909 */           return this.entryIterator.hasNext();
/*      */         }
/*      */ 
/*      */         public K next() {
/*  913 */           this.entry = ((Map.Entry)this.entryIterator.next());
/*  914 */           return this.entry.getKey();
/*      */         }
/*      */ 
/*      */         public void remove() {
/*  918 */           Iterators.checkRemove(this.entry != null);
/*  919 */           Collection collection = (Collection)this.entry.getValue();
/*  920 */           this.entryIterator.remove();
/*  921 */           AbstractMultimap.access$220(AbstractMultimap.this, collection.size());
/*  922 */           collection.clear();
/*      */         }
/*      */       };
/*      */     }
/*      */ 
/*      */     public boolean remove(Object key)
/*      */     {
/*  930 */       int count = 0;
/*  931 */       Collection collection = (Collection)this.subMap.remove(key);
/*  932 */       if (collection != null) {
/*  933 */         count = collection.size();
/*  934 */         collection.clear();
/*  935 */         AbstractMultimap.access$220(AbstractMultimap.this, count);
/*      */       }
/*  937 */       return count > 0;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/*  942 */       Iterators.clear(iterator());
/*      */     }
/*      */ 
/*      */     public boolean containsAll(Collection<?> c) {
/*  946 */       return this.subMap.keySet().containsAll(c);
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/*  950 */       return (this == object) || (this.subMap.keySet().equals(object));
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  954 */       return this.subMap.keySet().hashCode();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class RandomAccessWrappedList extends AbstractMultimap.WrappedList
/*      */     implements RandomAccess
/*      */   {
/*      */     RandomAccessWrappedList(List<V> key, @Nullable AbstractMultimap<K, V>.WrappedCollection delegate)
/*      */     {
/*  867 */       super(key, delegate, ancestor);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class WrappedList extends AbstractMultimap<K, V>.WrappedCollection
/*      */     implements List<V>
/*      */   {
/*      */     WrappedList(List<V> key, @Nullable AbstractMultimap<K, V>.WrappedCollection delegate)
/*      */     {
/*  721 */       super(key, delegate, ancestor);
/*      */     }
/*      */ 
/*      */     List<V> getListDelegate() {
/*  725 */       return (List)getDelegate();
/*      */     }
/*      */ 
/*      */     public boolean addAll(int index, Collection<? extends V> c)
/*      */     {
/*  730 */       if (c.isEmpty()) {
/*  731 */         return false;
/*      */       }
/*  733 */       int oldSize = size();
/*  734 */       boolean changed = getListDelegate().addAll(index, c);
/*  735 */       if (changed) {
/*  736 */         int newSize = getDelegate().size();
/*  737 */         AbstractMultimap.access$212(AbstractMultimap.this, newSize - oldSize);
/*  738 */         if (oldSize == 0) {
/*  739 */           addToMap();
/*      */         }
/*      */       }
/*  742 */       return changed;
/*      */     }
/*      */ 
/*      */     public V get(int index)
/*      */     {
/*  747 */       refreshIfEmpty();
/*  748 */       return getListDelegate().get(index);
/*      */     }
/*      */ 
/*      */     public V set(int index, V element)
/*      */     {
/*  753 */       refreshIfEmpty();
/*  754 */       return getListDelegate().set(index, element);
/*      */     }
/*      */ 
/*      */     public void add(int index, V element)
/*      */     {
/*  759 */       refreshIfEmpty();
/*  760 */       boolean wasEmpty = getDelegate().isEmpty();
/*  761 */       getListDelegate().add(index, element);
/*  762 */       AbstractMultimap.access$208(AbstractMultimap.this);
/*  763 */       if (wasEmpty)
/*  764 */         addToMap();
/*      */     }
/*      */ 
/*      */     public V remove(int index)
/*      */     {
/*  770 */       refreshIfEmpty();
/*  771 */       Object value = getListDelegate().remove(index);
/*  772 */       AbstractMultimap.access$210(AbstractMultimap.this);
/*  773 */       removeIfEmpty();
/*  774 */       return value;
/*      */     }
/*      */ 
/*      */     public int indexOf(Object o)
/*      */     {
/*  779 */       refreshIfEmpty();
/*  780 */       return getListDelegate().indexOf(o);
/*      */     }
/*      */ 
/*      */     public int lastIndexOf(Object o)
/*      */     {
/*  785 */       refreshIfEmpty();
/*  786 */       return getListDelegate().lastIndexOf(o);
/*      */     }
/*      */ 
/*      */     public ListIterator<V> listIterator()
/*      */     {
/*  791 */       refreshIfEmpty();
/*  792 */       return new WrappedListIterator();
/*      */     }
/*      */ 
/*      */     public ListIterator<V> listIterator(int index)
/*      */     {
/*  797 */       refreshIfEmpty();
/*  798 */       return new WrappedListIterator(index);
/*      */     }
/*      */ 
/*      */     public List<V> subList(int fromIndex, int toIndex)
/*      */     {
/*  803 */       refreshIfEmpty();
/*  804 */       return AbstractMultimap.this.wrapList(getKey(), getListDelegate().subList(fromIndex, toIndex), getAncestor() == null ? this : getAncestor());
/*      */     }
/*      */ 
/*      */     private class WrappedListIterator extends AbstractMultimap<K, V>.WrappedCollection.WrappedIterator
/*      */       implements ListIterator<V>
/*      */     {
/*      */       WrappedListIterator()
/*      */       {
/*  812 */         super();
/*      */       }
/*      */       public WrappedListIterator(int index) {
/*  815 */         super(AbstractMultimap.WrappedList.this.getListDelegate().listIterator(index));
/*      */       }
/*      */ 
/*      */       private ListIterator<V> getDelegateListIterator() {
/*  819 */         return (ListIterator)getDelegateIterator();
/*      */       }
/*      */ 
/*      */       public boolean hasPrevious()
/*      */       {
/*  824 */         return getDelegateListIterator().hasPrevious();
/*      */       }
/*      */ 
/*      */       public V previous()
/*      */       {
/*  829 */         return getDelegateListIterator().previous();
/*      */       }
/*      */ 
/*      */       public int nextIndex()
/*      */       {
/*  834 */         return getDelegateListIterator().nextIndex();
/*      */       }
/*      */ 
/*      */       public int previousIndex()
/*      */       {
/*  839 */         return getDelegateListIterator().previousIndex();
/*      */       }
/*      */ 
/*      */       public void set(V value)
/*      */       {
/*  844 */         getDelegateListIterator().set(value);
/*      */       }
/*      */ 
/*      */       public void add(V value)
/*      */       {
/*  849 */         boolean wasEmpty = AbstractMultimap.WrappedList.this.isEmpty();
/*  850 */         getDelegateListIterator().add(value);
/*  851 */         AbstractMultimap.access$208(AbstractMultimap.this);
/*  852 */         if (wasEmpty)
/*  853 */           AbstractMultimap.WrappedList.this.addToMap();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class WrappedSortedSet extends AbstractMultimap<K, V>.WrappedCollection
/*      */     implements SortedSet<V>
/*      */   {
/*      */     WrappedSortedSet(SortedSet<V> key, @Nullable AbstractMultimap<K, V>.WrappedCollection delegate)
/*      */     {
/*  668 */       super(key, delegate, ancestor);
/*      */     }
/*      */ 
/*      */     SortedSet<V> getSortedSetDelegate() {
/*  672 */       return (SortedSet)getDelegate();
/*      */     }
/*      */ 
/*      */     public Comparator<? super V> comparator()
/*      */     {
/*  677 */       return getSortedSetDelegate().comparator();
/*      */     }
/*      */ 
/*      */     public V first()
/*      */     {
/*  682 */       refreshIfEmpty();
/*  683 */       return getSortedSetDelegate().first();
/*      */     }
/*      */ 
/*      */     public V last()
/*      */     {
/*  688 */       refreshIfEmpty();
/*  689 */       return getSortedSetDelegate().last();
/*      */     }
/*      */ 
/*      */     public SortedSet<V> headSet(V toElement)
/*      */     {
/*  694 */       refreshIfEmpty();
/*  695 */       return new WrappedSortedSet(AbstractMultimap.this, getKey(), getSortedSetDelegate().headSet(toElement), getAncestor() == null ? this : getAncestor());
/*      */     }
/*      */ 
/*      */     public SortedSet<V> subSet(V fromElement, V toElement)
/*      */     {
/*  702 */       refreshIfEmpty();
/*  703 */       return new WrappedSortedSet(AbstractMultimap.this, getKey(), getSortedSetDelegate().subSet(fromElement, toElement), getAncestor() == null ? this : getAncestor());
/*      */     }
/*      */ 
/*      */     public SortedSet<V> tailSet(V fromElement)
/*      */     {
/*  710 */       refreshIfEmpty();
/*  711 */       return new WrappedSortedSet(AbstractMultimap.this, getKey(), getSortedSetDelegate().tailSet(fromElement), getAncestor() == null ? this : getAncestor());
/*      */     }
/*      */   }
/*      */ 
/*      */   private class WrappedSet extends AbstractMultimap<K, V>.WrappedCollection
/*      */     implements Set<V>
/*      */   {
/*      */     WrappedSet(Set<V> key)
/*      */     {
/*  657 */       super(key, delegate, null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class WrappedCollection extends AbstractCollection<V>
/*      */   {
/*      */     final K key;
/*      */     Collection<V> delegate;
/*      */     final AbstractMultimap<K, V>.WrappedCollection ancestor;
/*      */     final Collection<V> ancestorDelegate;
/*      */ 
/*      */     WrappedCollection(Collection<V> key, @Nullable AbstractMultimap<K, V>.WrappedCollection delegate)
/*      */     {
/*  416 */       this.key = key;
/*  417 */       this.delegate = delegate;
/*  418 */       this.ancestor = ancestor;
/*  419 */       this.ancestorDelegate = (ancestor == null ? null : ancestor.getDelegate());
/*      */     }
/*      */ 
/*      */     void refreshIfEmpty()
/*      */     {
/*  431 */       if (this.ancestor != null) {
/*  432 */         this.ancestor.refreshIfEmpty();
/*  433 */         if (this.ancestor.getDelegate() != this.ancestorDelegate)
/*  434 */           throw new ConcurrentModificationException();
/*      */       }
/*  436 */       else if (this.delegate.isEmpty()) {
/*  437 */         Collection newDelegate = (Collection)AbstractMultimap.this.map.get(this.key);
/*  438 */         if (newDelegate != null)
/*  439 */           this.delegate = newDelegate;
/*      */       }
/*      */     }
/*      */ 
/*      */     void removeIfEmpty()
/*      */     {
/*  449 */       if (this.ancestor != null)
/*  450 */         this.ancestor.removeIfEmpty();
/*  451 */       else if (this.delegate.isEmpty())
/*  452 */         AbstractMultimap.this.map.remove(this.key);
/*      */     }
/*      */ 
/*      */     K getKey()
/*      */     {
/*  457 */       return this.key;
/*      */     }
/*      */ 
/*      */     void addToMap()
/*      */     {
/*  468 */       if (this.ancestor != null)
/*  469 */         this.ancestor.addToMap();
/*      */       else
/*  471 */         AbstractMultimap.this.map.put(this.key, this.delegate);
/*      */     }
/*      */ 
/*      */     public int size()
/*      */     {
/*  476 */       refreshIfEmpty();
/*  477 */       return this.delegate.size();
/*      */     }
/*      */ 
/*      */     public boolean equals(@Nullable Object object) {
/*  481 */       if (object == this) {
/*  482 */         return true;
/*      */       }
/*  484 */       refreshIfEmpty();
/*  485 */       return this.delegate.equals(object);
/*      */     }
/*      */ 
/*      */     public int hashCode() {
/*  489 */       refreshIfEmpty();
/*  490 */       return this.delegate.hashCode();
/*      */     }
/*      */ 
/*      */     public String toString() {
/*  494 */       refreshIfEmpty();
/*  495 */       return this.delegate.toString();
/*      */     }
/*      */ 
/*      */     Collection<V> getDelegate() {
/*  499 */       return this.delegate;
/*      */     }
/*      */ 
/*      */     public Iterator<V> iterator() {
/*  503 */       refreshIfEmpty();
/*  504 */       return new WrappedIterator();
/*      */     }
/*      */ 
/*      */     public boolean add(V value)
/*      */     {
/*  557 */       refreshIfEmpty();
/*  558 */       boolean wasEmpty = this.delegate.isEmpty();
/*  559 */       boolean changed = this.delegate.add(value);
/*  560 */       if (changed) {
/*  561 */         AbstractMultimap.access$208(AbstractMultimap.this);
/*  562 */         if (wasEmpty) {
/*  563 */           addToMap();
/*      */         }
/*      */       }
/*  566 */       return changed;
/*      */     }
/*      */ 
/*      */     AbstractMultimap<K, V>.WrappedCollection getAncestor() {
/*  570 */       return this.ancestor;
/*      */     }
/*      */ 
/*      */     public boolean addAll(Collection<? extends V> collection)
/*      */     {
/*  576 */       if (collection.isEmpty()) {
/*  577 */         return false;
/*      */       }
/*  579 */       int oldSize = size();
/*  580 */       boolean changed = this.delegate.addAll(collection);
/*  581 */       if (changed) {
/*  582 */         int newSize = this.delegate.size();
/*  583 */         AbstractMultimap.access$212(AbstractMultimap.this, newSize - oldSize);
/*  584 */         if (oldSize == 0) {
/*  585 */           addToMap();
/*      */         }
/*      */       }
/*  588 */       return changed;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object o) {
/*  592 */       refreshIfEmpty();
/*  593 */       return this.delegate.contains(o);
/*      */     }
/*      */ 
/*      */     public boolean containsAll(Collection<?> c) {
/*  597 */       refreshIfEmpty();
/*  598 */       return this.delegate.containsAll(c);
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  602 */       int oldSize = size();
/*  603 */       if (oldSize == 0) {
/*  604 */         return;
/*      */       }
/*  606 */       this.delegate.clear();
/*  607 */       AbstractMultimap.access$220(AbstractMultimap.this, oldSize);
/*  608 */       removeIfEmpty();
/*      */     }
/*      */ 
/*      */     public boolean remove(Object o) {
/*  612 */       refreshIfEmpty();
/*  613 */       boolean changed = this.delegate.remove(o);
/*  614 */       if (changed) {
/*  615 */         AbstractMultimap.access$210(AbstractMultimap.this);
/*  616 */         removeIfEmpty();
/*      */       }
/*  618 */       return changed;
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection<?> c) {
/*  622 */       if (c.isEmpty()) {
/*  623 */         return false;
/*      */       }
/*  625 */       int oldSize = size();
/*  626 */       boolean changed = this.delegate.removeAll(c);
/*  627 */       if (changed) {
/*  628 */         int newSize = this.delegate.size();
/*  629 */         AbstractMultimap.access$212(AbstractMultimap.this, newSize - oldSize);
/*  630 */         removeIfEmpty();
/*      */       }
/*  632 */       return changed;
/*      */     }
/*      */ 
/*      */     public boolean retainAll(Collection<?> c) {
/*  636 */       Preconditions.checkNotNull(c);
/*  637 */       int oldSize = size();
/*  638 */       boolean changed = this.delegate.retainAll(c);
/*  639 */       if (changed) {
/*  640 */         int newSize = this.delegate.size();
/*  641 */         AbstractMultimap.access$212(AbstractMultimap.this, newSize - oldSize);
/*  642 */         removeIfEmpty();
/*      */       }
/*  644 */       return changed;
/*      */     }
/*      */ 
/*      */     class WrappedIterator
/*      */       implements Iterator<V>
/*      */     {
/*      */       final Iterator<V> delegateIterator;
/*  510 */       final Collection<V> originalDelegate = AbstractMultimap.WrappedCollection.this.delegate;
/*      */ 
/*      */       WrappedIterator() {
/*  513 */         this.delegateIterator = AbstractMultimap.this.iteratorOrListIterator(AbstractMultimap.WrappedCollection.this.delegate);
/*      */       }
/*      */ 
/*      */       WrappedIterator() {
/*  517 */         this.delegateIterator = delegateIterator;
/*      */       }
/*      */ 
/*      */       void validateIterator()
/*      */       {
/*  525 */         AbstractMultimap.WrappedCollection.this.refreshIfEmpty();
/*  526 */         if (AbstractMultimap.WrappedCollection.this.delegate != this.originalDelegate)
/*  527 */           throw new ConcurrentModificationException();
/*      */       }
/*      */ 
/*      */       public boolean hasNext()
/*      */       {
/*  533 */         validateIterator();
/*  534 */         return this.delegateIterator.hasNext();
/*      */       }
/*      */ 
/*      */       public V next()
/*      */       {
/*  539 */         validateIterator();
/*  540 */         return this.delegateIterator.next();
/*      */       }
/*      */ 
/*      */       public void remove()
/*      */       {
/*  545 */         this.delegateIterator.remove();
/*  546 */         AbstractMultimap.access$210(AbstractMultimap.this);
/*  547 */         AbstractMultimap.WrappedCollection.this.removeIfEmpty();
/*      */       }
/*      */ 
/*      */       Iterator<V> getDelegateIterator() {
/*  551 */         validateIterator();
/*  552 */         return this.delegateIterator;
/*      */       }
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractMultimap
 * JD-Core Version:    0.6.2
 */