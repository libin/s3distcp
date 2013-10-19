/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.AbstractCollection;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public final class ArrayTable<R, C, V>
/*     */   implements Table<R, C, V>, Serializable
/*     */ {
/*     */   private final ImmutableList<R> rowList;
/*     */   private final ImmutableList<C> columnList;
/*     */   private final ImmutableMap<R, Integer> rowKeyToIndex;
/*     */   private final ImmutableMap<C, Integer> columnKeyToIndex;
/*     */   private final V[][] array;
/*     */   private transient ArrayTable<R, C, V>.CellSet cellSet;
/*     */   private transient ArrayTable<R, C, V>.ColumnMap columnMap;
/*     */   private transient ArrayTable<R, C, V>.RowMap rowMap;
/*     */   private transient Collection<V> values;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <R, C, V> ArrayTable<R, C, V> create(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys)
/*     */   {
/*  97 */     return new ArrayTable(rowKeys, columnKeys);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> ArrayTable<R, C, V> create(Table<R, C, V> table)
/*     */   {
/* 129 */     return new ArrayTable(table);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> ArrayTable<R, C, V> create(ArrayTable<R, C, V> table)
/*     */   {
/* 138 */     return new ArrayTable(table);
/*     */   }
/*     */ 
/*     */   private ArrayTable(Iterable<? extends R> rowKeys, Iterable<? extends C> columnKeys)
/*     */   {
/* 151 */     this.rowList = ImmutableList.copyOf(rowKeys);
/* 152 */     this.columnList = ImmutableList.copyOf(columnKeys);
/* 153 */     Preconditions.checkArgument(!this.rowList.isEmpty());
/* 154 */     Preconditions.checkArgument(!this.columnList.isEmpty());
/*     */ 
/* 161 */     this.rowKeyToIndex = index(this.rowList);
/* 162 */     this.columnKeyToIndex = index(this.columnList);
/*     */ 
/* 165 */     Object[][] tmpArray = (Object[][])new Object[this.rowList.size()][this.columnList.size()];
/*     */ 
/* 167 */     this.array = tmpArray;
/*     */   }
/*     */ 
/*     */   private static <E> ImmutableMap<E, Integer> index(List<E> list) {
/* 171 */     ImmutableMap.Builder columnBuilder = ImmutableMap.builder();
/* 172 */     for (int i = 0; i < list.size(); i++) {
/* 173 */       columnBuilder.put(list.get(i), Integer.valueOf(i));
/*     */     }
/* 175 */     return columnBuilder.build();
/*     */   }
/*     */ 
/*     */   private ArrayTable(Table<R, C, V> table) {
/* 179 */     this(table.rowKeySet(), table.columnKeySet());
/* 180 */     putAll(table);
/*     */   }
/*     */ 
/*     */   private ArrayTable(ArrayTable<R, C, V> table) {
/* 184 */     this.rowList = table.rowList;
/* 185 */     this.columnList = table.columnList;
/* 186 */     this.rowKeyToIndex = table.rowKeyToIndex;
/* 187 */     this.columnKeyToIndex = table.columnKeyToIndex;
/*     */ 
/* 189 */     Object[][] copy = (Object[][])new Object[this.rowList.size()][this.columnList.size()];
/* 190 */     this.array = copy;
/* 191 */     for (int i = 0; i < this.rowList.size(); i++)
/* 192 */       System.arraycopy(table.array[i], 0, copy[i], 0, table.array[i].length);
/*     */   }
/*     */ 
/*     */   public ImmutableList<R> rowKeyList()
/*     */   {
/* 304 */     return this.rowList;
/*     */   }
/*     */ 
/*     */   public ImmutableList<C> columnKeyList()
/*     */   {
/* 312 */     return this.columnList;
/*     */   }
/*     */ 
/*     */   public V at(int rowIndex, int columnIndex)
/*     */   {
/* 330 */     return this.array[rowIndex][columnIndex];
/*     */   }
/*     */ 
/*     */   public V set(int rowIndex, int columnIndex, @Nullable V value)
/*     */   {
/* 349 */     Object oldValue = this.array[rowIndex][columnIndex];
/* 350 */     this.array[rowIndex][columnIndex] = value;
/* 351 */     return oldValue;
/*     */   }
/*     */ 
/*     */   public V[][] toArray(Class<V> valueClass)
/*     */   {
/* 367 */     Object[][] copy = (Object[][])Array.newInstance(valueClass, new int[] { this.rowList.size(), this.columnList.size() });
/*     */ 
/* 369 */     for (int i = 0; i < this.rowList.size(); i++) {
/* 370 */       System.arraycopy(this.array[i], 0, copy[i], 0, this.array[i].length);
/*     */     }
/* 372 */     return copy;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void clear()
/*     */   {
/* 383 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void eraseAll()
/*     */   {
/* 391 */     for (Object[] row : this.array)
/* 392 */       Arrays.fill(row, null);
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 402 */     return (containsRow(rowKey)) && (containsColumn(columnKey));
/*     */   }
/*     */ 
/*     */   public boolean containsColumn(@Nullable Object columnKey)
/*     */   {
/* 411 */     return this.columnKeyToIndex.containsKey(columnKey);
/*     */   }
/*     */ 
/*     */   public boolean containsRow(@Nullable Object rowKey)
/*     */   {
/* 420 */     return this.rowKeyToIndex.containsKey(rowKey);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 425 */     for (Object[] row : this.array) {
/* 426 */       for (Object element : row) {
/* 427 */         if (Objects.equal(value, element)) {
/* 428 */           return true;
/*     */         }
/*     */       }
/*     */     }
/* 432 */     return false;
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 437 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 438 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 439 */     return (rowIndex == null) || (columnIndex == null) ? null : this.array[rowIndex.intValue()][columnIndex.intValue()];
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 448 */     return false;
/*     */   }
/*     */ 
/*     */   public V put(R rowKey, C columnKey, @Nullable V value)
/*     */   {
/* 459 */     Preconditions.checkNotNull(rowKey);
/* 460 */     Preconditions.checkNotNull(columnKey);
/* 461 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 462 */     Preconditions.checkArgument(rowIndex != null, "Row %s not in %s", new Object[] { rowKey, this.rowList });
/* 463 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 464 */     Preconditions.checkArgument(columnIndex != null, "Column %s not in %s", new Object[] { columnKey, this.columnList });
/*     */ 
/* 466 */     return set(rowIndex.intValue(), columnIndex.intValue(), value);
/*     */   }
/*     */ 
/*     */   public void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 487 */     for (Table.Cell cell : table.cellSet())
/* 488 */       put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public V remove(Object rowKey, Object columnKey)
/*     */   {
/* 500 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public V erase(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 517 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 518 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 519 */     if ((rowIndex == null) || (columnIndex == null)) {
/* 520 */       return null;
/*     */     }
/* 522 */     return set(rowIndex.intValue(), columnIndex.intValue(), null);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 529 */     return this.rowList.size() * this.columnList.size();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj) {
/* 533 */     if ((obj instanceof Table)) {
/* 534 */       Table other = (Table)obj;
/* 535 */       return cellSet().equals(other.cellSet());
/*     */     }
/* 537 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 541 */     return cellSet().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 548 */     return rowMap().toString();
/*     */   }
/*     */ 
/*     */   public Set<Table.Cell<R, C, V>> cellSet()
/*     */   {
/* 568 */     CellSet set = this.cellSet;
/* 569 */     return set == null ? (this.cellSet = new CellSet(null)) : set;
/*     */   }
/*     */ 
/*     */   public Map<R, V> column(C columnKey)
/*     */   {
/* 628 */     Preconditions.checkNotNull(columnKey);
/* 629 */     Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 630 */     return columnIndex == null ? ImmutableMap.of() : new Column(columnIndex.intValue());
/*     */   }
/*     */ 
/*     */   public ImmutableSet<C> columnKeySet()
/*     */   {
/* 666 */     return this.columnKeyToIndex.keySet();
/*     */   }
/*     */ 
/*     */   public Map<C, Map<R, V>> columnMap()
/*     */   {
/* 673 */     ColumnMap map = this.columnMap;
/* 674 */     return map == null ? (this.columnMap = new ColumnMap(null)) : map;
/*     */   }
/*     */ 
/*     */   public Map<C, V> row(R rowKey)
/*     */   {
/* 718 */     Preconditions.checkNotNull(rowKey);
/* 719 */     Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 720 */     return rowIndex == null ? ImmutableMap.of() : new Row(rowIndex.intValue());
/*     */   }
/*     */ 
/*     */   public ImmutableSet<R> rowKeySet()
/*     */   {
/* 755 */     return this.rowKeyToIndex.keySet();
/*     */   }
/*     */ 
/*     */   public Map<R, Map<C, V>> rowMap()
/*     */   {
/* 762 */     RowMap map = this.rowMap;
/* 763 */     return map == null ? (this.rowMap = new RowMap(null)) : map;
/*     */   }
/*     */ 
/*     */   public Collection<V> values()
/*     */   {
/* 805 */     Collection v = this.values;
/* 806 */     return v == null ? (this.values = new Values(null)) : v;
/*     */   }
/*     */   private class Values extends AbstractCollection<V> {
/*     */     private Values() {
/*     */     }
/* 811 */     public Iterator<V> iterator() { return new TransformedIterator(ArrayTable.this.cellSet().iterator())
/*     */       {
/*     */         V transform(Table.Cell<R, C, V> cell) {
/* 814 */           return cell.getValue();
/*     */         }
/*     */       }; }
/*     */ 
/*     */     public int size()
/*     */     {
/* 820 */       return ArrayTable.this.size();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class RowMap extends ArrayTable.ArrayMap<R, Map<C, V>>
/*     */   {
/*     */     private RowMap()
/*     */     {
/* 768 */       super(null);
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 773 */       return "Row";
/*     */     }
/*     */ 
/*     */     Map<C, V> getValue(int index)
/*     */     {
/* 778 */       return new ArrayTable.Row(ArrayTable.this, index);
/*     */     }
/*     */ 
/*     */     Map<C, V> setValue(int index, Map<C, V> newValue)
/*     */     {
/* 783 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Map<C, V> put(R key, Map<C, V> value)
/*     */     {
/* 788 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Row extends ArrayTable.ArrayMap<C, V>
/*     */   {
/*     */     final int rowIndex;
/*     */ 
/*     */     Row(int rowIndex)
/*     */     {
/* 727 */       super(null);
/* 728 */       this.rowIndex = rowIndex;
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 733 */       return "Column";
/*     */     }
/*     */ 
/*     */     V getValue(int index)
/*     */     {
/* 738 */       return ArrayTable.this.at(this.rowIndex, index);
/*     */     }
/*     */ 
/*     */     V setValue(int index, V newValue)
/*     */     {
/* 743 */       return ArrayTable.this.set(this.rowIndex, index, newValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class ColumnMap extends ArrayTable.ArrayMap<C, Map<R, V>>
/*     */   {
/*     */     private ColumnMap()
/*     */     {
/* 679 */       super(null);
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 684 */       return "Column";
/*     */     }
/*     */ 
/*     */     Map<R, V> getValue(int index)
/*     */     {
/* 689 */       return new ArrayTable.Column(ArrayTable.this, index);
/*     */     }
/*     */ 
/*     */     Map<R, V> setValue(int index, Map<R, V> newValue)
/*     */     {
/* 694 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Map<R, V> put(C key, Map<R, V> value)
/*     */     {
/* 699 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ 
/*     */   private class Column extends ArrayTable.ArrayMap<R, V>
/*     */   {
/*     */     final int columnIndex;
/*     */ 
/*     */     Column(int columnIndex)
/*     */     {
/* 638 */       super(null);
/* 639 */       this.columnIndex = columnIndex;
/*     */     }
/*     */ 
/*     */     String getKeyRole()
/*     */     {
/* 644 */       return "Row";
/*     */     }
/*     */ 
/*     */     V getValue(int index)
/*     */     {
/* 649 */       return ArrayTable.this.at(index, this.columnIndex);
/*     */     }
/*     */ 
/*     */     V setValue(int index, V newValue)
/*     */     {
/* 654 */       return ArrayTable.this.set(index, this.columnIndex, newValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   private class CellSet extends AbstractSet<Table.Cell<R, C, V>>
/*     */   {
/*     */     private CellSet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public Iterator<Table.Cell<R, C, V>> iterator()
/*     */     {
/* 575 */       return new AbstractIndexedListIterator(size()) {
/*     */         protected Table.Cell<R, C, V> get(final int index) {
/* 577 */           return new Tables.AbstractCell() {
/* 578 */             final int rowIndex = index / ArrayTable.this.columnList.size();
/* 579 */             final int columnIndex = index % ArrayTable.this.columnList.size();
/*     */ 
/*     */             public R getRowKey() {
/* 582 */               return ArrayTable.this.rowList.get(this.rowIndex);
/*     */             }
/*     */ 
/*     */             public C getColumnKey() {
/* 586 */               return ArrayTable.this.columnList.get(this.columnIndex);
/*     */             }
/*     */ 
/*     */             public V getValue() {
/* 590 */               return ArrayTable.this.array[this.rowIndex][this.columnIndex];
/*     */             }
/*     */           };
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public int size() {
/* 598 */       return ArrayTable.this.size();
/*     */     }
/*     */ 
/*     */     public boolean contains(Object obj) {
/* 602 */       if ((obj instanceof Table.Cell)) {
/* 603 */         Table.Cell cell = (Table.Cell)obj;
/* 604 */         Integer rowIndex = (Integer)ArrayTable.this.rowKeyToIndex.get(cell.getRowKey());
/* 605 */         Integer columnIndex = (Integer)ArrayTable.this.columnKeyToIndex.get(cell.getColumnKey());
/* 606 */         return (rowIndex != null) && (columnIndex != null) && (Objects.equal(ArrayTable.this.array[rowIndex.intValue()][columnIndex.intValue()], cell.getValue()));
/*     */       }
/*     */ 
/* 610 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract class ArrayMap<K, V> extends Maps.ImprovedAbstractMap<K, V>
/*     */   {
/*     */     private final ImmutableMap<K, Integer> keyIndex;
/*     */ 
/*     */     private ArrayMap(ImmutableMap<K, Integer> keyIndex)
/*     */     {
/* 200 */       this.keyIndex = keyIndex;
/*     */     }
/*     */ 
/*     */     public Set<K> keySet()
/*     */     {
/* 205 */       return this.keyIndex.keySet();
/*     */     }
/*     */ 
/*     */     K getKey(int index) {
/* 209 */       return this.keyIndex.keySet().asList().get(index); } 
/*     */     abstract String getKeyRole();
/*     */ 
/*     */     @Nullable
/*     */     abstract V getValue(int paramInt);
/*     */ 
/*     */     @Nullable
/*     */     abstract V setValue(int paramInt, V paramV);
/*     */ 
/* 220 */     public int size() { return this.keyIndex.size(); }
/*     */ 
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 225 */       return this.keyIndex.isEmpty();
/*     */     }
/*     */ 
/*     */     protected Set<Map.Entry<K, V>> createEntrySet()
/*     */     {
/* 230 */       return new Maps.EntrySet()
/*     */       {
/*     */         Map<K, V> map() {
/* 233 */           return ArrayTable.ArrayMap.this;
/*     */         }
/*     */ 
/*     */         public Iterator<Map.Entry<K, V>> iterator()
/*     */         {
/* 238 */           return new AbstractIndexedListIterator(size())
/*     */           {
/*     */             protected Map.Entry<K, V> get(final int index) {
/* 241 */               return new AbstractMapEntry()
/*     */               {
/*     */                 public K getKey() {
/* 244 */                   return ArrayTable.ArrayMap.this.getKey(index);
/*     */                 }
/*     */ 
/*     */                 public V getValue()
/*     */                 {
/* 249 */                   return ArrayTable.ArrayMap.this.getValue(index);
/*     */                 }
/*     */ 
/*     */                 public V setValue(V value)
/*     */                 {
/* 254 */                   return ArrayTable.ArrayMap.this.setValue(index, value);
/*     */                 }
/*     */               };
/*     */             }
/*     */           };
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public boolean containsKey(@Nullable Object key)
/*     */     {
/* 265 */       return this.keyIndex.containsKey(key);
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object key)
/*     */     {
/* 270 */       Integer index = (Integer)this.keyIndex.get(key);
/* 271 */       if (index == null) {
/* 272 */         return null;
/*     */       }
/* 274 */       return getValue(index.intValue());
/*     */     }
/*     */ 
/*     */     public V put(K key, V value)
/*     */     {
/* 280 */       Integer index = (Integer)this.keyIndex.get(key);
/* 281 */       if (index == null) {
/* 282 */         throw new IllegalArgumentException(getKeyRole() + " " + key + " not in " + this.keyIndex.keySet());
/*     */       }
/*     */ 
/* 285 */       return setValue(index.intValue(), value);
/*     */     }
/*     */ 
/*     */     public V remove(Object key)
/*     */     {
/* 290 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 295 */       throw new UnsupportedOperationException();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ArrayTable
 * JD-Core Version:    0.6.2
 */