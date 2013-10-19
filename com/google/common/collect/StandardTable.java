/*      */ package com.google.common.collect;
/*      */ 
/*      */ import com.google.common.annotations.GwtCompatible;
/*      */ import com.google.common.base.Preconditions;
/*      */ import com.google.common.base.Predicate;
/*      */ import com.google.common.base.Predicates;
/*      */ import com.google.common.base.Supplier;
/*      */ import java.io.Serializable;
/*      */ import java.util.AbstractCollection;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.AbstractSet;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Collection;
/*      */ import java.util.Iterator;
/*      */ import java.util.LinkedHashMap;
/*      */ import java.util.Map;
/*      */ import java.util.Map.Entry;
/*      */ import java.util.Set;
/*      */ import javax.annotation.Nullable;
/*      */ 
/*      */ @GwtCompatible
/*      */ class StandardTable<R, C, V>
/*      */   implements Table<R, C, V>, Serializable
/*      */ {
/*      */ 
/*      */   @GwtTransient
/*      */   final Map<R, Map<C, V>> backingMap;
/*      */ 
/*      */   @GwtTransient
/*      */   final Supplier<? extends Map<C, V>> factory;
/*      */   private transient StandardTable<R, C, V>.CellSet cellSet;
/*      */   private transient StandardTable<R, C, V>.RowKeySet rowKeySet;
/*      */   private transient Set<C> columnKeySet;
/*      */   private transient StandardTable<R, C, V>.Values values;
/*      */   private transient StandardTable<R, C, V>.RowMap rowMap;
/*      */   private transient StandardTable<R, C, V>.ColumnMap columnMap;
/*      */   private static final long serialVersionUID = 0L;
/*      */ 
/*      */   StandardTable(Map<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory)
/*      */   {
/*   70 */     this.backingMap = backingMap;
/*   71 */     this.factory = factory;
/*      */   }
/*      */ 
/*      */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*      */   {
/*   78 */     if ((rowKey == null) || (columnKey == null)) {
/*   79 */       return false;
/*      */     }
/*   81 */     Map map = (Map)Maps.safeGet(this.backingMap, rowKey);
/*   82 */     return (map != null) && (Maps.safeContainsKey(map, columnKey));
/*      */   }
/*      */ 
/*      */   public boolean containsColumn(@Nullable Object columnKey) {
/*   86 */     if (columnKey == null) {
/*   87 */       return false;
/*      */     }
/*   89 */     for (Map map : this.backingMap.values()) {
/*   90 */       if (Maps.safeContainsKey(map, columnKey)) {
/*   91 */         return true;
/*      */       }
/*      */     }
/*   94 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean containsRow(@Nullable Object rowKey) {
/*   98 */     return (rowKey != null) && (Maps.safeContainsKey(this.backingMap, rowKey));
/*      */   }
/*      */ 
/*      */   public boolean containsValue(@Nullable Object value) {
/*  102 */     if (value == null) {
/*  103 */       return false;
/*      */     }
/*  105 */     for (Map map : this.backingMap.values()) {
/*  106 */       if (map.containsValue(value)) {
/*  107 */         return true;
/*      */       }
/*      */     }
/*  110 */     return false;
/*      */   }
/*      */ 
/*      */   public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
/*  114 */     if ((rowKey == null) || (columnKey == null)) {
/*  115 */       return null;
/*      */     }
/*  117 */     Map map = (Map)Maps.safeGet(this.backingMap, rowKey);
/*  118 */     return map == null ? null : Maps.safeGet(map, columnKey);
/*      */   }
/*      */ 
/*      */   public boolean isEmpty() {
/*  122 */     return this.backingMap.isEmpty();
/*      */   }
/*      */ 
/*      */   public int size() {
/*  126 */     int size = 0;
/*  127 */     for (Map map : this.backingMap.values()) {
/*  128 */       size += map.size();
/*      */     }
/*  130 */     return size;
/*      */   }
/*      */ 
/*      */   public boolean equals(@Nullable Object obj) {
/*  134 */     if (obj == this) {
/*  135 */       return true;
/*      */     }
/*  137 */     if ((obj instanceof Table)) {
/*  138 */       Table other = (Table)obj;
/*  139 */       return cellSet().equals(other.cellSet());
/*      */     }
/*  141 */     return false;
/*      */   }
/*      */ 
/*      */   public int hashCode() {
/*  145 */     return cellSet().hashCode();
/*      */   }
/*      */ 
/*      */   public String toString()
/*      */   {
/*  152 */     return rowMap().toString();
/*      */   }
/*      */ 
/*      */   public void clear()
/*      */   {
/*  158 */     this.backingMap.clear();
/*      */   }
/*      */ 
/*      */   private Map<C, V> getOrCreate(R rowKey) {
/*  162 */     Map map = (Map)this.backingMap.get(rowKey);
/*  163 */     if (map == null) {
/*  164 */       map = (Map)this.factory.get();
/*  165 */       this.backingMap.put(rowKey, map);
/*      */     }
/*  167 */     return map;
/*      */   }
/*      */ 
/*      */   public V put(R rowKey, C columnKey, V value) {
/*  171 */     Preconditions.checkNotNull(rowKey);
/*  172 */     Preconditions.checkNotNull(columnKey);
/*  173 */     Preconditions.checkNotNull(value);
/*  174 */     return getOrCreate(rowKey).put(columnKey, value);
/*      */   }
/*      */ 
/*      */   public void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*      */   {
/*  179 */     for (Table.Cell cell : table.cellSet())
/*  180 */       put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*      */   }
/*      */ 
/*      */   public V remove(@Nullable Object rowKey, @Nullable Object columnKey)
/*      */   {
/*  186 */     if ((rowKey == null) || (columnKey == null)) {
/*  187 */       return null;
/*      */     }
/*  189 */     Map map = (Map)Maps.safeGet(this.backingMap, rowKey);
/*  190 */     if (map == null) {
/*  191 */       return null;
/*      */     }
/*  193 */     Object value = map.remove(columnKey);
/*  194 */     if (map.isEmpty()) {
/*  195 */       this.backingMap.remove(rowKey);
/*      */     }
/*  197 */     return value;
/*      */   }
/*      */ 
/*      */   private Map<R, V> removeColumn(Object column) {
/*  201 */     Map output = new LinkedHashMap();
/*  202 */     Iterator iterator = this.backingMap.entrySet().iterator();
/*      */ 
/*  204 */     while (iterator.hasNext()) {
/*  205 */       Map.Entry entry = (Map.Entry)iterator.next();
/*  206 */       Object value = ((Map)entry.getValue()).remove(column);
/*  207 */       if (value != null) {
/*  208 */         output.put(entry.getKey(), value);
/*  209 */         if (((Map)entry.getValue()).isEmpty()) {
/*  210 */           iterator.remove();
/*      */         }
/*      */       }
/*      */     }
/*  214 */     return output;
/*      */   }
/*      */ 
/*      */   private boolean containsMapping(Object rowKey, Object columnKey, Object value)
/*      */   {
/*  219 */     return (value != null) && (value.equals(get(rowKey, columnKey)));
/*      */   }
/*      */ 
/*      */   private boolean removeMapping(Object rowKey, Object columnKey, Object value)
/*      */   {
/*  224 */     if (containsMapping(rowKey, columnKey, value)) {
/*  225 */       remove(rowKey, columnKey);
/*  226 */       return true;
/*      */     }
/*  228 */     return false;
/*      */   }
/*      */ 
/*      */   public Set<Table.Cell<R, C, V>> cellSet()
/*      */   {
/*  274 */     CellSet result = this.cellSet;
/*  275 */     return result == null ? (this.cellSet = new CellSet(null)) : result;
/*      */   }
/*      */ 
/*      */   public Map<C, V> row(R rowKey)
/*      */   {
/*  336 */     return new Row(rowKey);
/*      */   }
/*      */ 
/*      */   public Map<R, V> column(C columnKey)
/*      */   {
/*  500 */     return new Column(columnKey);
/*      */   }
/*      */ 
/*      */   public Set<R> rowKeySet()
/*      */   {
/*  753 */     Set result = this.rowKeySet;
/*  754 */     return result == null ? (this.rowKeySet = new RowKeySet()) : result;
/*      */   }
/*      */ 
/*      */   public Set<C> columnKeySet()
/*      */   {
/*  788 */     Set result = this.columnKeySet;
/*  789 */     return result == null ? (this.columnKeySet = new ColumnKeySet(null)) : result;
/*      */   }
/*      */ 
/*      */   Iterator<C> createColumnKeyIterator()
/*      */   {
/*  871 */     return new ColumnKeyIterator(null);
/*      */   }
/*      */ 
/*      */   public Collection<V> values()
/*      */   {
/*  907 */     Values result = this.values;
/*  908 */     return result == null ? (this.values = new Values(null)) : result;
/*      */   }
/*      */ 
/*      */   public Map<R, Map<C, V>> rowMap()
/*      */   {
/*  929 */     RowMap result = this.rowMap;
/*  930 */     return result == null ? (this.rowMap = new RowMap()) : result;
/*      */   }
/*      */ 
/*      */   public Map<C, Map<R, V>> columnMap()
/*      */   {
/*  995 */     ColumnMap result = this.columnMap;
/*  996 */     return result == null ? (this.columnMap = new ColumnMap(null)) : result;
/*      */   }
/*      */   private class ColumnMap extends Maps.ImprovedAbstractMap<C, Map<R, V>> {
/*      */     StandardTable<R, C, V>.ColumnMap.ColumnMapValues columnMapValues;
/*      */ 
/*      */     private ColumnMap() {
/*      */     }
/*      */     public Map<R, V> get(Object key) {
/* 1004 */       return StandardTable.this.containsColumn(key) ? StandardTable.this.column(key) : null;
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key) {
/* 1008 */       return StandardTable.this.containsColumn(key);
/*      */     }
/*      */ 
/*      */     public Map<R, V> remove(Object key) {
/* 1012 */       return StandardTable.this.containsColumn(key) ? StandardTable.this.removeColumn(key) : null;
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<C, Map<R, V>>> createEntrySet() {
/* 1016 */       return new ColumnMapEntrySet();
/*      */     }
/*      */ 
/*      */     public Set<C> keySet() {
/* 1020 */       return StandardTable.this.columnKeySet();
/*      */     }
/*      */ 
/*      */     public Collection<Map<R, V>> values()
/*      */     {
/* 1026 */       ColumnMapValues result = this.columnMapValues;
/* 1027 */       return result == null ? (this.columnMapValues = new ColumnMapValues(null)) : result;
/*      */     }
/*      */ 
/*      */     private class ColumnMapValues extends StandardTable<R, C, V>.TableCollection<Map<R, V>>
/*      */     {
/*      */       private ColumnMapValues()
/*      */       {
/* 1090 */         super(null);
/*      */       }
/* 1092 */       public Iterator<Map<R, V>> iterator() { return Maps.valueIterator(StandardTable.ColumnMap.this.entrySet().iterator()); }
/*      */ 
/*      */       public boolean remove(Object obj)
/*      */       {
/* 1096 */         for (Map.Entry entry : StandardTable.ColumnMap.this.entrySet()) {
/* 1097 */           if (((Map)entry.getValue()).equals(obj)) {
/* 1098 */             StandardTable.this.removeColumn(entry.getKey());
/* 1099 */             return true;
/*      */           }
/*      */         }
/* 1102 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean removeAll(Collection<?> c) {
/* 1106 */         Preconditions.checkNotNull(c);
/* 1107 */         boolean changed = false;
/* 1108 */         for (Iterator i$ = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator(); i$.hasNext(); ) { Object columnKey = i$.next();
/* 1109 */           if (c.contains(StandardTable.this.column(columnKey))) {
/* 1110 */             StandardTable.this.removeColumn(columnKey);
/* 1111 */             changed = true;
/*      */           }
/*      */         }
/* 1114 */         return changed;
/*      */       }
/*      */ 
/*      */       public boolean retainAll(Collection<?> c) {
/* 1118 */         Preconditions.checkNotNull(c);
/* 1119 */         boolean changed = false;
/* 1120 */         for (Iterator i$ = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator(); i$.hasNext(); ) { Object columnKey = i$.next();
/* 1121 */           if (!c.contains(StandardTable.this.column(columnKey))) {
/* 1122 */             StandardTable.this.removeColumn(columnKey);
/* 1123 */             changed = true;
/*      */           }
/*      */         }
/* 1126 */         return changed;
/*      */       }
/*      */ 
/*      */       public int size() {
/* 1130 */         return StandardTable.this.columnKeySet().size();
/*      */       }
/*      */     }
/*      */ 
/*      */     class ColumnMapEntrySet extends StandardTable<R, C, V>.TableSet<Map.Entry<C, Map<R, V>>>
/*      */     {
/*      */       ColumnMapEntrySet()
/*      */       {
/* 1031 */         super(null);
/*      */       }
/* 1033 */       public Iterator<Map.Entry<C, Map<R, V>>> iterator() { return new TransformedIterator(StandardTable.this.columnKeySet().iterator())
/*      */         {
/*      */           Map.Entry<C, Map<R, V>> transform(C columnKey) {
/* 1036 */             return new ImmutableEntry(columnKey, StandardTable.this.column(columnKey));
/*      */           }
/*      */         };
/*      */       }
/*      */ 
/*      */       public int size()
/*      */       {
/* 1043 */         return StandardTable.this.columnKeySet().size();
/*      */       }
/*      */ 
/*      */       public boolean contains(Object obj) {
/* 1047 */         if ((obj instanceof Map.Entry)) {
/* 1048 */           Map.Entry entry = (Map.Entry)obj;
/* 1049 */           if (StandardTable.this.containsColumn(entry.getKey()))
/*      */           {
/* 1053 */             Object columnKey = entry.getKey();
/* 1054 */             return StandardTable.ColumnMap.this.get(columnKey).equals(entry.getValue());
/*      */           }
/*      */         }
/* 1057 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean remove(Object obj) {
/* 1061 */         if (contains(obj)) {
/* 1062 */           Map.Entry entry = (Map.Entry)obj;
/* 1063 */           StandardTable.this.removeColumn(entry.getKey());
/* 1064 */           return true;
/*      */         }
/* 1066 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean removeAll(Collection<?> c) {
/* 1070 */         boolean changed = false;
/* 1071 */         for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object obj = i$.next();
/* 1072 */           changed |= remove(obj);
/*      */         }
/* 1074 */         return changed;
/*      */       }
/*      */ 
/*      */       public boolean retainAll(Collection<?> c) {
/* 1078 */         boolean changed = false;
/* 1079 */         for (Iterator i$ = Lists.newArrayList(StandardTable.this.columnKeySet().iterator()).iterator(); i$.hasNext(); ) { Object columnKey = i$.next();
/* 1080 */           if (!c.contains(new ImmutableEntry(columnKey, StandardTable.this.column(columnKey))))
/*      */           {
/* 1082 */             StandardTable.this.removeColumn(columnKey);
/* 1083 */             changed = true;
/*      */           }
/*      */         }
/* 1086 */         return changed;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class RowMap extends Maps.ImprovedAbstractMap<R, Map<C, V>>
/*      */   {
/*      */     RowMap()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key)
/*      */     {
/*  935 */       return StandardTable.this.containsRow(key);
/*      */     }
/*      */ 
/*      */     public Map<C, V> get(Object key)
/*      */     {
/*  941 */       return StandardTable.this.containsRow(key) ? StandardTable.this.row(key) : null;
/*      */     }
/*      */ 
/*      */     public Set<R> keySet() {
/*  945 */       return StandardTable.this.rowKeySet();
/*      */     }
/*      */ 
/*      */     public Map<C, V> remove(Object key) {
/*  949 */       return key == null ? null : (Map)StandardTable.this.backingMap.remove(key);
/*      */     }
/*      */ 
/*      */     protected Set<Map.Entry<R, Map<C, V>>> createEntrySet() {
/*  953 */       return new EntrySet();
/*      */     }
/*      */     class EntrySet extends StandardTable<R, C, V>.TableSet<Map.Entry<R, Map<C, V>>> {
/*  956 */       EntrySet() { super(null); } 
/*      */       public Iterator<Map.Entry<R, Map<C, V>>> iterator() {
/*  958 */         return new TransformedIterator(StandardTable.this.backingMap.keySet().iterator())
/*      */         {
/*      */           Map.Entry<R, Map<C, V>> transform(R rowKey) {
/*  961 */             return new ImmutableEntry(rowKey, StandardTable.this.row(rowKey));
/*      */           }
/*      */         };
/*      */       }
/*      */ 
/*      */       public int size() {
/*  967 */         return StandardTable.this.backingMap.size();
/*      */       }
/*      */ 
/*      */       public boolean contains(Object obj) {
/*  971 */         if ((obj instanceof Map.Entry)) {
/*  972 */           Map.Entry entry = (Map.Entry)obj;
/*  973 */           return (entry.getKey() != null) && ((entry.getValue() instanceof Map)) && (Collections2.safeContains(StandardTable.this.backingMap.entrySet(), entry));
/*      */         }
/*      */ 
/*  977 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean remove(Object obj) {
/*  981 */         if ((obj instanceof Map.Entry)) {
/*  982 */           Map.Entry entry = (Map.Entry)obj;
/*  983 */           return (entry.getKey() != null) && ((entry.getValue() instanceof Map)) && (StandardTable.this.backingMap.entrySet().remove(entry));
/*      */         }
/*      */ 
/*  987 */         return false;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Values extends StandardTable<R, C, V>.TableCollection<V>
/*      */   {
/*      */     private Values()
/*      */     {
/*  911 */       super(null);
/*      */     }
/*  913 */     public Iterator<V> iterator() { return new TransformedIterator(StandardTable.this.cellSet().iterator())
/*      */       {
/*      */         V transform(Table.Cell<R, C, V> cell) {
/*  916 */           return cell.getValue();
/*      */         }
/*      */       }; }
/*      */ 
/*      */     public int size()
/*      */     {
/*  922 */       return StandardTable.this.size();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ColumnKeyIterator extends AbstractIterator<C>
/*      */   {
/*  877 */     final Map<C, V> seen = (Map)StandardTable.this.factory.get();
/*  878 */     final Iterator<Map<C, V>> mapIterator = StandardTable.this.backingMap.values().iterator();
/*  879 */     Iterator<Map.Entry<C, V>> entryIterator = Iterators.emptyIterator();
/*      */ 
/*      */     private ColumnKeyIterator() {
/*      */     }
/*      */     protected C computeNext() { while (true) if (this.entryIterator.hasNext()) {
/*  884 */           Map.Entry entry = (Map.Entry)this.entryIterator.next();
/*  885 */           if (!this.seen.containsKey(entry.getKey())) {
/*  886 */             this.seen.put(entry.getKey(), entry.getValue());
/*  887 */             return entry.getKey();
/*      */           }
/*      */         } else { if (!this.mapIterator.hasNext()) break;
/*  890 */           this.entryIterator = ((Map)this.mapIterator.next()).entrySet().iterator();
/*      */         }
/*  892 */       return endOfData();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class ColumnKeySet extends StandardTable<R, C, V>.TableSet<C>
/*      */   {
/*      */     private ColumnKeySet()
/*      */     {
/*  792 */       super(null);
/*      */     }
/*  794 */     public Iterator<C> iterator() { return StandardTable.this.createColumnKeyIterator(); }
/*      */ 
/*      */     public int size()
/*      */     {
/*  798 */       return Iterators.size(iterator());
/*      */     }
/*      */ 
/*      */     public boolean remove(Object obj) {
/*  802 */       if (obj == null) {
/*  803 */         return false;
/*      */       }
/*  805 */       boolean changed = false;
/*  806 */       Iterator iterator = StandardTable.this.backingMap.values().iterator();
/*  807 */       while (iterator.hasNext()) {
/*  808 */         Map map = (Map)iterator.next();
/*  809 */         if (map.keySet().remove(obj)) {
/*  810 */           changed = true;
/*  811 */           if (map.isEmpty()) {
/*  812 */             iterator.remove();
/*      */           }
/*      */         }
/*      */       }
/*  816 */       return changed;
/*      */     }
/*      */ 
/*      */     public boolean removeAll(Collection<?> c) {
/*  820 */       Preconditions.checkNotNull(c);
/*  821 */       boolean changed = false;
/*  822 */       Iterator iterator = StandardTable.this.backingMap.values().iterator();
/*  823 */       while (iterator.hasNext()) {
/*  824 */         Map map = (Map)iterator.next();
/*      */ 
/*  827 */         if (Iterators.removeAll(map.keySet().iterator(), c)) {
/*  828 */           changed = true;
/*  829 */           if (map.isEmpty()) {
/*  830 */             iterator.remove();
/*      */           }
/*      */         }
/*      */       }
/*  834 */       return changed;
/*      */     }
/*      */ 
/*      */     public boolean retainAll(Collection<?> c) {
/*  838 */       Preconditions.checkNotNull(c);
/*  839 */       boolean changed = false;
/*  840 */       Iterator iterator = StandardTable.this.backingMap.values().iterator();
/*  841 */       while (iterator.hasNext()) {
/*  842 */         Map map = (Map)iterator.next();
/*  843 */         if (map.keySet().retainAll(c)) {
/*  844 */           changed = true;
/*  845 */           if (map.isEmpty()) {
/*  846 */             iterator.remove();
/*      */           }
/*      */         }
/*      */       }
/*  850 */       return changed;
/*      */     }
/*      */ 
/*      */     public boolean contains(Object obj) {
/*  854 */       if (obj == null) {
/*  855 */         return false;
/*      */       }
/*  857 */       for (Map map : StandardTable.this.backingMap.values()) {
/*  858 */         if (map.containsKey(obj)) {
/*  859 */           return true;
/*      */         }
/*      */       }
/*  862 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   class RowKeySet extends StandardTable<R, C, V>.TableSet<R>
/*      */   {
/*      */     RowKeySet()
/*      */     {
/*  757 */       super(null);
/*      */     }
/*  759 */     public Iterator<R> iterator() { return Maps.keyIterator(StandardTable.this.rowMap().entrySet().iterator()); }
/*      */ 
/*      */     public int size()
/*      */     {
/*  763 */       return StandardTable.this.backingMap.size();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object obj) {
/*  767 */       return StandardTable.this.containsRow(obj);
/*      */     }
/*      */ 
/*      */     public boolean remove(Object obj) {
/*  771 */       return (obj != null) && (StandardTable.this.backingMap.remove(obj) != null);
/*      */     }
/*      */   }
/*      */ 
/*      */   private class Column extends Maps.ImprovedAbstractMap<R, V>
/*      */   {
/*      */     final C columnKey;
/*      */     StandardTable<R, C, V>.Column.Values columnValues;
/*      */     StandardTable<R, C, V>.Column.KeySet keySet;
/*      */ 
/*      */     Column()
/*      */     {
/*  507 */       this.columnKey = Preconditions.checkNotNull(columnKey);
/*      */     }
/*      */ 
/*      */     public V put(R key, V value) {
/*  511 */       return StandardTable.this.put(key, this.columnKey, value);
/*      */     }
/*      */ 
/*      */     public V get(Object key) {
/*  515 */       return StandardTable.this.get(key, this.columnKey);
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key) {
/*  519 */       return StandardTable.this.contains(key, this.columnKey);
/*      */     }
/*      */ 
/*      */     public V remove(Object key) {
/*  523 */       return StandardTable.this.remove(key, this.columnKey);
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<R, V>> createEntrySet() {
/*  527 */       return new EntrySet();
/*      */     }
/*      */ 
/*      */     public Collection<V> values()
/*      */     {
/*  533 */       Values result = this.columnValues;
/*  534 */       return result == null ? (this.columnValues = new Values()) : result;
/*      */     }
/*      */ 
/*      */     boolean removePredicate(Predicate<? super Map.Entry<R, V>> predicate)
/*      */     {
/*  542 */       boolean changed = false;
/*  543 */       Iterator iterator = StandardTable.this.backingMap.entrySet().iterator();
/*      */ 
/*  545 */       while (iterator.hasNext()) {
/*  546 */         Map.Entry entry = (Map.Entry)iterator.next();
/*  547 */         Map map = (Map)entry.getValue();
/*  548 */         Object value = map.get(this.columnKey);
/*  549 */         if ((value != null) && (predicate.apply(new ImmutableEntry(entry.getKey(), value))))
/*      */         {
/*  552 */           map.remove(this.columnKey);
/*  553 */           changed = true;
/*  554 */           if (map.isEmpty()) {
/*  555 */             iterator.remove();
/*      */           }
/*      */         }
/*      */       }
/*  559 */       return changed;
/*      */     }
/*      */ 
/*      */     public Set<R> keySet()
/*      */     {
/*  642 */       KeySet result = this.keySet;
/*  643 */       return result == null ? (this.keySet = new KeySet()) : result;
/*      */     }
/*      */ 
/*      */     class Values extends AbstractCollection<V>
/*      */     {
/*      */       Values()
/*      */       {
/*      */       }
/*      */ 
/*      */       public Iterator<V> iterator()
/*      */       {
/*  693 */         return Maps.valueIterator(StandardTable.Column.this.entrySet().iterator());
/*      */       }
/*      */ 
/*      */       public int size() {
/*  697 */         return StandardTable.Column.this.entrySet().size();
/*      */       }
/*      */ 
/*      */       public boolean isEmpty() {
/*  701 */         return !StandardTable.this.containsColumn(StandardTable.Column.this.columnKey);
/*      */       }
/*      */ 
/*      */       public void clear() {
/*  705 */         StandardTable.Column.this.entrySet().clear();
/*      */       }
/*      */ 
/*      */       public boolean remove(Object obj) {
/*  709 */         if (obj == null) {
/*  710 */           return false;
/*      */         }
/*  712 */         Iterator iterator = StandardTable.this.backingMap.values().iterator();
/*  713 */         while (iterator.hasNext()) {
/*  714 */           Map map = (Map)iterator.next();
/*  715 */           if (map.entrySet().remove(new ImmutableEntry(StandardTable.Column.this.columnKey, obj)))
/*      */           {
/*  717 */             if (map.isEmpty()) {
/*  718 */               iterator.remove();
/*      */             }
/*  720 */             return true;
/*      */           }
/*      */         }
/*  723 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean removeAll(final Collection<?> c) {
/*  727 */         Preconditions.checkNotNull(c);
/*  728 */         Predicate predicate = new Predicate()
/*      */         {
/*      */           public boolean apply(Map.Entry<R, V> entry) {
/*  731 */             return c.contains(entry.getValue());
/*      */           }
/*      */         };
/*  734 */         return StandardTable.Column.this.removePredicate(predicate);
/*      */       }
/*      */ 
/*      */       public boolean retainAll(final Collection<?> c) {
/*  738 */         Preconditions.checkNotNull(c);
/*  739 */         Predicate predicate = new Predicate()
/*      */         {
/*      */           public boolean apply(Map.Entry<R, V> entry) {
/*  742 */             return !c.contains(entry.getValue());
/*      */           }
/*      */         };
/*  745 */         return StandardTable.Column.this.removePredicate(predicate);
/*      */       }
/*      */     }
/*      */ 
/*      */     class KeySet extends AbstractSet<R>
/*      */     {
/*      */       KeySet()
/*      */       {
/*      */       }
/*      */ 
/*      */       public Iterator<R> iterator()
/*      */       {
/*  648 */         return Maps.keyIterator(StandardTable.Column.this.entrySet().iterator());
/*      */       }
/*      */ 
/*      */       public int size() {
/*  652 */         return StandardTable.Column.this.entrySet().size();
/*      */       }
/*      */ 
/*      */       public boolean isEmpty() {
/*  656 */         return !StandardTable.this.containsColumn(StandardTable.Column.this.columnKey);
/*      */       }
/*      */ 
/*      */       public boolean contains(Object obj) {
/*  660 */         return StandardTable.this.contains(obj, StandardTable.Column.this.columnKey);
/*      */       }
/*      */ 
/*      */       public boolean remove(Object obj) {
/*  664 */         return StandardTable.this.remove(obj, StandardTable.Column.this.columnKey) != null;
/*      */       }
/*      */ 
/*      */       public void clear() {
/*  668 */         StandardTable.Column.this.entrySet().clear();
/*      */       }
/*      */ 
/*      */       public boolean removeAll(Collection<?> c) {
/*  672 */         boolean changed = false;
/*  673 */         for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object obj = i$.next();
/*  674 */           changed |= remove(obj);
/*      */         }
/*  676 */         return changed;
/*      */       }
/*      */ 
/*      */       public boolean retainAll(final Collection<?> c) {
/*  680 */         Preconditions.checkNotNull(c);
/*  681 */         Predicate predicate = new Predicate()
/*      */         {
/*      */           public boolean apply(Map.Entry<R, V> entry) {
/*  684 */             return !c.contains(entry.getKey());
/*      */           }
/*      */         };
/*  687 */         return StandardTable.Column.this.removePredicate(predicate);
/*      */       }
/*      */     }
/*      */ 
/*      */     class EntrySetIterator extends AbstractIterator<Map.Entry<R, V>>
/*      */     {
/*  616 */       final Iterator<Map.Entry<R, Map<C, V>>> iterator = StandardTable.this.backingMap.entrySet().iterator();
/*      */ 
/*      */       EntrySetIterator() {  } 
/*  619 */       protected Map.Entry<R, V> computeNext() { while (this.iterator.hasNext()) {
/*  620 */           final Map.Entry entry = (Map.Entry)this.iterator.next();
/*  621 */           if (((Map)entry.getValue()).containsKey(StandardTable.Column.this.columnKey)) {
/*  622 */             return new AbstractMapEntry() {
/*      */               public R getKey() {
/*  624 */                 return entry.getKey();
/*      */               }
/*      */               public V getValue() {
/*  627 */                 return ((Map)entry.getValue()).get(StandardTable.Column.this.columnKey);
/*      */               }
/*      */               public V setValue(V value) {
/*  630 */                 return ((Map)entry.getValue()).put(StandardTable.Column.this.columnKey, Preconditions.checkNotNull(value));
/*      */               }
/*      */             };
/*      */           }
/*      */         }
/*  635 */         return (Map.Entry)endOfData();
/*      */       }
/*      */     }
/*      */ 
/*      */     class EntrySet extends AbstractSet<Map.Entry<R, V>>
/*      */     {
/*      */       EntrySet()
/*      */       {
/*      */       }
/*      */ 
/*      */       public Iterator<Map.Entry<R, V>> iterator()
/*      */       {
/*  564 */         return new StandardTable.Column.EntrySetIterator(StandardTable.Column.this);
/*      */       }
/*      */ 
/*      */       public int size() {
/*  568 */         int size = 0;
/*  569 */         for (Map map : StandardTable.this.backingMap.values()) {
/*  570 */           if (map.containsKey(StandardTable.Column.this.columnKey)) {
/*  571 */             size++;
/*      */           }
/*      */         }
/*  574 */         return size;
/*      */       }
/*      */ 
/*      */       public boolean isEmpty() {
/*  578 */         return !StandardTable.this.containsColumn(StandardTable.Column.this.columnKey);
/*      */       }
/*      */ 
/*      */       public void clear() {
/*  582 */         Predicate predicate = Predicates.alwaysTrue();
/*  583 */         StandardTable.Column.this.removePredicate(predicate);
/*      */       }
/*      */ 
/*      */       public boolean contains(Object o) {
/*  587 */         if ((o instanceof Map.Entry)) {
/*  588 */           Map.Entry entry = (Map.Entry)o;
/*  589 */           return StandardTable.this.containsMapping(entry.getKey(), StandardTable.Column.this.columnKey, entry.getValue());
/*      */         }
/*  591 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean remove(Object obj) {
/*  595 */         if ((obj instanceof Map.Entry)) {
/*  596 */           Map.Entry entry = (Map.Entry)obj;
/*  597 */           return StandardTable.this.removeMapping(entry.getKey(), StandardTable.Column.this.columnKey, entry.getValue());
/*      */         }
/*  599 */         return false;
/*      */       }
/*      */ 
/*      */       public boolean removeAll(Collection<?> c) {
/*  603 */         boolean changed = false;
/*  604 */         for (Iterator i$ = c.iterator(); i$.hasNext(); ) { Object obj = i$.next();
/*  605 */           changed |= remove(obj);
/*      */         }
/*  607 */         return changed;
/*      */       }
/*      */ 
/*      */       public boolean retainAll(Collection<?> c) {
/*  611 */         return StandardTable.Column.this.removePredicate(Predicates.not(Predicates.in(c)));
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   class Row extends AbstractMap<C, V>
/*      */   {
/*      */     final R rowKey;
/*      */     Map<C, V> backingRowMap;
/*      */     Set<C> keySet;
/*      */     Set<Map.Entry<C, V>> entrySet;
/*      */ 
/*      */     Row()
/*      */     {
/*  343 */       this.rowKey = Preconditions.checkNotNull(rowKey);
/*      */     }
/*      */ 
/*      */     Map<C, V> backingRowMap()
/*      */     {
/*  349 */       return (this.backingRowMap == null) || ((this.backingRowMap.isEmpty()) && (StandardTable.this.backingMap.containsKey(this.rowKey))) ? (this.backingRowMap = computeBackingRowMap()) : this.backingRowMap;
/*      */     }
/*      */ 
/*      */     Map<C, V> computeBackingRowMap()
/*      */     {
/*  356 */       return (Map)StandardTable.this.backingMap.get(this.rowKey);
/*      */     }
/*      */ 
/*      */     void maintainEmptyInvariant()
/*      */     {
/*  361 */       if ((backingRowMap() != null) && (this.backingRowMap.isEmpty())) {
/*  362 */         StandardTable.this.backingMap.remove(this.rowKey);
/*  363 */         this.backingRowMap = null;
/*      */       }
/*      */     }
/*      */ 
/*      */     public boolean containsKey(Object key)
/*      */     {
/*  369 */       Map backingRowMap = backingRowMap();
/*  370 */       return (key != null) && (backingRowMap != null) && (Maps.safeContainsKey(backingRowMap, key));
/*      */     }
/*      */ 
/*      */     public V get(Object key)
/*      */     {
/*  376 */       Map backingRowMap = backingRowMap();
/*  377 */       return (key != null) && (backingRowMap != null) ? Maps.safeGet(backingRowMap, key) : null;
/*      */     }
/*      */ 
/*      */     public V put(C key, V value)
/*      */     {
/*  384 */       Preconditions.checkNotNull(key);
/*  385 */       Preconditions.checkNotNull(value);
/*  386 */       if ((this.backingRowMap != null) && (!this.backingRowMap.isEmpty())) {
/*  387 */         return this.backingRowMap.put(key, value);
/*      */       }
/*  389 */       return StandardTable.this.put(this.rowKey, key, value);
/*      */     }
/*      */ 
/*      */     public V remove(Object key)
/*      */     {
/*      */       try {
/*  395 */         Map backingRowMap = backingRowMap();
/*  396 */         if (backingRowMap == null) {
/*  397 */           return null;
/*      */         }
/*  399 */         Object result = backingRowMap.remove(key);
/*  400 */         maintainEmptyInvariant();
/*  401 */         return result; } catch (ClassCastException e) {
/*      */       }
/*  403 */       return null;
/*      */     }
/*      */ 
/*      */     public void clear()
/*      */     {
/*  409 */       Map backingRowMap = backingRowMap();
/*  410 */       if (backingRowMap != null) {
/*  411 */         backingRowMap.clear();
/*      */       }
/*  413 */       maintainEmptyInvariant();
/*      */     }
/*      */ 
/*      */     public Set<C> keySet()
/*      */     {
/*  420 */       Set result = this.keySet;
/*  421 */       if (result == null) {
/*  422 */         return this.keySet = new Maps.KeySet()
/*      */         {
/*      */           Map<C, V> map() {
/*  425 */             return StandardTable.Row.this;
/*      */           }
/*      */         };
/*      */       }
/*  429 */       return result;
/*      */     }
/*      */ 
/*      */     public Set<Map.Entry<C, V>> entrySet()
/*      */     {
/*  436 */       Set result = this.entrySet;
/*  437 */       if (result == null) {
/*  438 */         return this.entrySet = new RowEntrySet(null);
/*      */       }
/*  440 */       return result;
/*      */     }
/*      */     private class RowEntrySet extends Maps.EntrySet<C, V> {
/*      */       private RowEntrySet() {
/*      */       }
/*      */       Map<C, V> map() {
/*  446 */         return StandardTable.Row.this;
/*      */       }
/*      */ 
/*      */       public int size()
/*      */       {
/*  451 */         Map map = StandardTable.Row.this.backingRowMap();
/*  452 */         return map == null ? 0 : map.size();
/*      */       }
/*      */ 
/*      */       public Iterator<Map.Entry<C, V>> iterator()
/*      */       {
/*  457 */         Map map = StandardTable.Row.this.backingRowMap();
/*  458 */         if (map == null) {
/*  459 */           return Iterators.emptyModifiableIterator();
/*      */         }
/*  461 */         final Iterator iterator = map.entrySet().iterator();
/*  462 */         return new Iterator() {
/*      */           public boolean hasNext() {
/*  464 */             return iterator.hasNext();
/*      */           }
/*      */           public Map.Entry<C, V> next() {
/*  467 */             final Map.Entry entry = (Map.Entry)iterator.next();
/*  468 */             return new ForwardingMapEntry() {
/*      */               protected Map.Entry<C, V> delegate() {
/*  470 */                 return entry;
/*      */               }
/*      */               public V setValue(V value) {
/*  473 */                 return super.setValue(Preconditions.checkNotNull(value));
/*      */               }
/*      */ 
/*      */               public boolean equals(Object object)
/*      */               {
/*  478 */                 return standardEquals(object);
/*      */               }
/*      */             };
/*      */           }
/*      */ 
/*      */           public void remove()
/*      */           {
/*  485 */             iterator.remove();
/*  486 */             StandardTable.Row.this.maintainEmptyInvariant();
/*      */           }
/*      */         };
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CellIterator
/*      */     implements Iterator<Table.Cell<R, C, V>>
/*      */   {
/*  307 */     final Iterator<Map.Entry<R, Map<C, V>>> rowIterator = StandardTable.this.backingMap.entrySet().iterator();
/*      */     Map.Entry<R, Map<C, V>> rowEntry;
/*  310 */     Iterator<Map.Entry<C, V>> columnIterator = Iterators.emptyModifiableIterator();
/*      */ 
/*      */     private CellIterator() {
/*      */     }
/*  314 */     public boolean hasNext() { return (this.rowIterator.hasNext()) || (this.columnIterator.hasNext()); }
/*      */ 
/*      */     public Table.Cell<R, C, V> next()
/*      */     {
/*  318 */       if (!this.columnIterator.hasNext()) {
/*  319 */         this.rowEntry = ((Map.Entry)this.rowIterator.next());
/*  320 */         this.columnIterator = ((Map)this.rowEntry.getValue()).entrySet().iterator();
/*      */       }
/*  322 */       Map.Entry columnEntry = (Map.Entry)this.columnIterator.next();
/*  323 */       return Tables.immutableCell(this.rowEntry.getKey(), columnEntry.getKey(), columnEntry.getValue());
/*      */     }
/*      */ 
/*      */     public void remove()
/*      */     {
/*  328 */       this.columnIterator.remove();
/*  329 */       if (((Map)this.rowEntry.getValue()).isEmpty())
/*  330 */         this.rowIterator.remove();
/*      */     }
/*      */   }
/*      */ 
/*      */   private class CellSet extends StandardTable<R, C, V>.TableSet<Table.Cell<R, C, V>>
/*      */   {
/*      */     private CellSet()
/*      */     {
/*  278 */       super(null);
/*      */     }
/*  280 */     public Iterator<Table.Cell<R, C, V>> iterator() { return new StandardTable.CellIterator(StandardTable.this, null); }
/*      */ 
/*      */     public int size()
/*      */     {
/*  284 */       return StandardTable.this.size();
/*      */     }
/*      */ 
/*      */     public boolean contains(Object obj) {
/*  288 */       if ((obj instanceof Table.Cell)) {
/*  289 */         Table.Cell cell = (Table.Cell)obj;
/*  290 */         return StandardTable.this.containsMapping(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*      */       }
/*      */ 
/*  293 */       return false;
/*      */     }
/*      */ 
/*      */     public boolean remove(Object obj) {
/*  297 */       if ((obj instanceof Table.Cell)) {
/*  298 */         Table.Cell cell = (Table.Cell)obj;
/*  299 */         return StandardTable.this.removeMapping(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*      */       }
/*      */ 
/*  302 */       return false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private abstract class TableSet<T> extends AbstractSet<T>
/*      */   {
/*      */     private TableSet()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/*  253 */       return StandardTable.this.backingMap.isEmpty();
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  257 */       StandardTable.this.backingMap.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private abstract class TableCollection<T> extends AbstractCollection<T>
/*      */   {
/*      */     private TableCollection()
/*      */     {
/*      */     }
/*      */ 
/*      */     public boolean isEmpty()
/*      */     {
/*  239 */       return StandardTable.this.backingMap.isEmpty();
/*      */     }
/*      */ 
/*      */     public void clear() {
/*  243 */       StandardTable.this.backingMap.clear();
/*      */     }
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.StandardTable
 * JD-Core Version:    0.6.2
 */