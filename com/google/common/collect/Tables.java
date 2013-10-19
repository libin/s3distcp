/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Beta
/*     */ public final class Tables
/*     */ {
/* 744 */   private static final Function<? extends Map<?, ?>, ? extends Map<?, ?>> UNMODIFIABLE_WRAPPER = new Function()
/*     */   {
/*     */     public Map<Object, Object> apply(Map<Object, Object> input)
/*     */     {
/* 748 */       return Collections.unmodifiableMap(input);
/*     */     }
/* 744 */   };
/*     */ 
/*     */   public static <R, C, V> Table.Cell<R, C, V> immutableCell(@Nullable R rowKey, @Nullable C columnKey, @Nullable V value)
/*     */   {
/*  68 */     return new ImmutableCell(rowKey, columnKey, value);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> Table<C, R, V> transpose(Table<R, C, V> table)
/*     */   {
/* 141 */     return (table instanceof TransposeTable) ? ((TransposeTable)table).original : new TransposeTable(table);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> Table<R, C, V> newCustomTable(Map<R, Map<C, V>> backingMap, Supplier<? extends Map<C, V>> factory)
/*     */   {
/* 370 */     Preconditions.checkArgument(backingMap.isEmpty());
/* 371 */     Preconditions.checkNotNull(factory);
/*     */ 
/* 373 */     return new StandardTable(backingMap, factory);
/*     */   }
/*     */ 
/*     */   public static <R, C, V1, V2> Table<R, C, V2> transformValues(Table<R, C, V1> fromTable, Function<? super V1, V2> function)
/*     */   {
/* 404 */     return new TransformedTable(fromTable, function);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> Table<R, C, V> unmodifiableTable(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 609 */     return new UnmodifiableTable(table);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> RowSortedTable<R, C, V> unmodifiableRowSortedTable(RowSortedTable<R, ? extends C, ? extends V> table)
/*     */   {
/* 710 */     return new UnmodifiableRowSortedMap(table);
/*     */   }
/*     */ 
/*     */   private static <K, V> Function<Map<K, V>, Map<K, V>> unmodifiableWrapper()
/*     */   {
/* 741 */     return UNMODIFIABLE_WRAPPER;
/*     */   }
/*     */ 
/*     */   static final class UnmodifiableRowSortedMap<R, C, V> extends Tables.UnmodifiableTable<R, C, V>
/*     */     implements RowSortedTable<R, C, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public UnmodifiableRowSortedMap(RowSortedTable<R, ? extends C, ? extends V> delegate)
/*     */     {
/* 717 */       super();
/*     */     }
/*     */ 
/*     */     protected RowSortedTable<R, C, V> delegate()
/*     */     {
/* 722 */       return (RowSortedTable)super.delegate();
/*     */     }
/*     */ 
/*     */     public SortedMap<R, Map<C, V>> rowMap()
/*     */     {
/* 727 */       Function wrapper = Tables.access$100();
/* 728 */       return Collections.unmodifiableSortedMap(Maps.transformValues(delegate().rowMap(), wrapper));
/*     */     }
/*     */ 
/*     */     public SortedSet<R> rowKeySet()
/*     */     {
/* 733 */       return Collections.unmodifiableSortedSet(delegate().rowKeySet());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class UnmodifiableTable<R, C, V> extends ForwardingTable<R, C, V>
/*     */     implements Serializable
/*     */   {
/*     */     final Table<? extends R, ? extends C, ? extends V> delegate;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     UnmodifiableTable(Table<? extends R, ? extends C, ? extends V> delegate)
/*     */     {
/* 617 */       this.delegate = ((Table)Preconditions.checkNotNull(delegate));
/*     */     }
/*     */ 
/*     */     protected Table<R, C, V> delegate()
/*     */     {
/* 623 */       return this.delegate;
/*     */     }
/*     */ 
/*     */     public Set<Table.Cell<R, C, V>> cellSet()
/*     */     {
/* 628 */       return Collections.unmodifiableSet(super.cellSet());
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 633 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Map<R, V> column(@Nullable C columnKey)
/*     */     {
/* 638 */       return Collections.unmodifiableMap(super.column(columnKey));
/*     */     }
/*     */ 
/*     */     public Set<C> columnKeySet()
/*     */     {
/* 643 */       return Collections.unmodifiableSet(super.columnKeySet());
/*     */     }
/*     */ 
/*     */     public Map<C, Map<R, V>> columnMap()
/*     */     {
/* 648 */       Function wrapper = Tables.access$100();
/* 649 */       return Collections.unmodifiableMap(Maps.transformValues(super.columnMap(), wrapper));
/*     */     }
/*     */ 
/*     */     public V put(@Nullable R rowKey, @Nullable C columnKey, @Nullable V value)
/*     */     {
/* 654 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */     {
/* 659 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public V remove(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 664 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public Map<C, V> row(@Nullable R rowKey)
/*     */     {
/* 669 */       return Collections.unmodifiableMap(super.row(rowKey));
/*     */     }
/*     */ 
/*     */     public Set<R> rowKeySet()
/*     */     {
/* 674 */       return Collections.unmodifiableSet(super.rowKeySet());
/*     */     }
/*     */ 
/*     */     public Map<R, Map<C, V>> rowMap()
/*     */     {
/* 679 */       Function wrapper = Tables.access$100();
/* 680 */       return Collections.unmodifiableMap(Maps.transformValues(super.rowMap(), wrapper));
/*     */     }
/*     */ 
/*     */     public Collection<V> values()
/*     */     {
/* 685 */       return Collections.unmodifiableCollection(super.values());
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class TransformedTable<R, C, V1, V2>
/*     */     implements Table<R, C, V2>
/*     */   {
/*     */     final Table<R, C, V1> fromTable;
/*     */     final Function<? super V1, V2> function;
/*     */     TransformedTable<R, C, V1, V2>.CellSet cellSet;
/*     */     Collection<V2> values;
/*     */     Map<R, Map<C, V2>> rowMap;
/*     */     Map<C, Map<R, V2>> columnMap;
/*     */ 
/*     */     TransformedTable(Table<R, C, V1> fromTable, Function<? super V1, V2> function)
/*     */     {
/* 414 */       this.fromTable = ((Table)Preconditions.checkNotNull(fromTable));
/* 415 */       this.function = ((Function)Preconditions.checkNotNull(function));
/*     */     }
/*     */ 
/*     */     public boolean contains(Object rowKey, Object columnKey) {
/* 419 */       return this.fromTable.contains(rowKey, columnKey);
/*     */     }
/*     */ 
/*     */     public boolean containsRow(Object rowKey) {
/* 423 */       return this.fromTable.containsRow(rowKey);
/*     */     }
/*     */ 
/*     */     public boolean containsColumn(Object columnKey) {
/* 427 */       return this.fromTable.containsColumn(columnKey);
/*     */     }
/*     */ 
/*     */     public boolean containsValue(Object value) {
/* 431 */       return values().contains(value);
/*     */     }
/*     */ 
/*     */     public V2 get(Object rowKey, Object columnKey)
/*     */     {
/* 437 */       return contains(rowKey, columnKey) ? this.function.apply(this.fromTable.get(rowKey, columnKey)) : null;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 442 */       return this.fromTable.isEmpty();
/*     */     }
/*     */ 
/*     */     public int size() {
/* 446 */       return this.fromTable.size();
/*     */     }
/*     */ 
/*     */     public void clear() {
/* 450 */       this.fromTable.clear();
/*     */     }
/*     */ 
/*     */     public V2 put(R rowKey, C columnKey, V2 value) {
/* 454 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public void putAll(Table<? extends R, ? extends C, ? extends V2> table)
/*     */     {
/* 459 */       throw new UnsupportedOperationException();
/*     */     }
/*     */ 
/*     */     public V2 remove(Object rowKey, Object columnKey) {
/* 463 */       return contains(rowKey, columnKey) ? this.function.apply(this.fromTable.remove(rowKey, columnKey)) : null;
/*     */     }
/*     */ 
/*     */     public Map<C, V2> row(R rowKey)
/*     */     {
/* 468 */       return Maps.transformValues(this.fromTable.row(rowKey), this.function);
/*     */     }
/*     */ 
/*     */     public Map<R, V2> column(C columnKey) {
/* 472 */       return Maps.transformValues(this.fromTable.column(columnKey), this.function);
/*     */     }
/*     */ 
/*     */     Function<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>> cellFunction() {
/* 476 */       return new Function() {
/*     */         public Table.Cell<R, C, V2> apply(Table.Cell<R, C, V1> cell) {
/* 478 */           return Tables.immutableCell(cell.getRowKey(), cell.getColumnKey(), Tables.TransformedTable.this.function.apply(cell.getValue()));
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Set<Table.Cell<R, C, V2>> cellSet()
/*     */     {
/* 521 */       return this.cellSet == null ? (this.cellSet = new CellSet()) : this.cellSet;
/*     */     }
/*     */ 
/*     */     public Set<R> rowKeySet() {
/* 525 */       return this.fromTable.rowKeySet();
/*     */     }
/*     */ 
/*     */     public Set<C> columnKeySet() {
/* 529 */       return this.fromTable.columnKeySet();
/*     */     }
/*     */ 
/*     */     public Collection<V2> values()
/*     */     {
/* 535 */       return this.values == null ? (this.values = Collections2.transform(this.fromTable.values(), this.function)) : this.values;
/*     */     }
/*     */ 
/*     */     Map<R, Map<C, V2>> createRowMap()
/*     */     {
/* 541 */       Function rowFunction = new Function()
/*     */       {
/*     */         public Map<C, V2> apply(Map<C, V1> row) {
/* 544 */           return Maps.transformValues(row, Tables.TransformedTable.this.function);
/*     */         }
/*     */       };
/* 547 */       return Maps.transformValues(this.fromTable.rowMap(), rowFunction);
/*     */     }
/*     */ 
/*     */     public Map<R, Map<C, V2>> rowMap()
/*     */     {
/* 553 */       return this.rowMap == null ? (this.rowMap = createRowMap()) : this.rowMap;
/*     */     }
/*     */ 
/*     */     Map<C, Map<R, V2>> createColumnMap() {
/* 557 */       Function columnFunction = new Function()
/*     */       {
/*     */         public Map<R, V2> apply(Map<R, V1> column) {
/* 560 */           return Maps.transformValues(column, Tables.TransformedTable.this.function);
/*     */         }
/*     */       };
/* 563 */       return Maps.transformValues(this.fromTable.columnMap(), columnFunction);
/*     */     }
/*     */ 
/*     */     public Map<C, Map<R, V2>> columnMap()
/*     */     {
/* 569 */       return this.columnMap == null ? (this.columnMap = createColumnMap()) : this.columnMap;
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 573 */       if (obj == this) {
/* 574 */         return true;
/*     */       }
/* 576 */       if ((obj instanceof Table)) {
/* 577 */         Table other = (Table)obj;
/* 578 */         return cellSet().equals(other.cellSet());
/*     */       }
/* 580 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 584 */       return cellSet().hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 588 */       return rowMap().toString();
/*     */     }
/*     */ 
/*     */     class CellSet extends Collections2.TransformedCollection<Table.Cell<R, C, V1>, Table.Cell<R, C, V2>>
/*     */       implements Set<Table.Cell<R, C, V2>>
/*     */     {
/*     */       CellSet()
/*     */       {
/* 488 */         super(Tables.TransformedTable.this.cellFunction());
/*     */       }
/*     */       public boolean equals(Object obj) {
/* 491 */         return Sets.equalsImpl(this, obj);
/*     */       }
/*     */       public int hashCode() {
/* 494 */         return Sets.hashCodeImpl(this);
/*     */       }
/*     */       public boolean contains(Object obj) {
/* 497 */         if ((obj instanceof Table.Cell)) {
/* 498 */           Table.Cell cell = (Table.Cell)obj;
/* 499 */           if (!Objects.equal(cell.getValue(), Tables.TransformedTable.this.get(cell.getRowKey(), cell.getColumnKey())))
/*     */           {
/* 501 */             return false;
/*     */           }
/* 503 */           return (cell.getValue() != null) || (Tables.TransformedTable.this.fromTable.contains(cell.getRowKey(), cell.getColumnKey()));
/*     */         }
/*     */ 
/* 506 */         return false;
/*     */       }
/*     */       public boolean remove(Object obj) {
/* 509 */         if (contains(obj)) {
/* 510 */           Table.Cell cell = (Table.Cell)obj;
/* 511 */           Tables.TransformedTable.this.fromTable.remove(cell.getRowKey(), cell.getColumnKey());
/* 512 */           return true;
/*     */         }
/* 514 */         return false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class TransposeTable<C, R, V>
/*     */     implements Table<C, R, V>
/*     */   {
/*     */     final Table<R, C, V> original;
/* 264 */     private static final Function<Table.Cell<?, ?, ?>, Table.Cell<?, ?, ?>> TRANSPOSE_CELL = new Function()
/*     */     {
/*     */       public Table.Cell<?, ?, ?> apply(Table.Cell<?, ?, ?> cell)
/*     */       {
/* 268 */         return Tables.immutableCell(cell.getColumnKey(), cell.getRowKey(), cell.getValue());
/*     */       }
/* 264 */     };
/*     */     TransposeTable<C, R, V>.CellSet cellSet;
/*     */ 
/*     */     TransposeTable(Table<R, C, V> original)
/*     */     {
/* 150 */       this.original = ((Table)Preconditions.checkNotNull(original));
/*     */     }
/*     */ 
/*     */     public void clear()
/*     */     {
/* 155 */       this.original.clear();
/*     */     }
/*     */ 
/*     */     public Map<C, V> column(R columnKey)
/*     */     {
/* 160 */       return this.original.row(columnKey);
/*     */     }
/*     */ 
/*     */     public Set<R> columnKeySet()
/*     */     {
/* 165 */       return this.original.rowKeySet();
/*     */     }
/*     */ 
/*     */     public Map<R, Map<C, V>> columnMap()
/*     */     {
/* 170 */       return this.original.rowMap();
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 176 */       return this.original.contains(columnKey, rowKey);
/*     */     }
/*     */ 
/*     */     public boolean containsColumn(@Nullable Object columnKey)
/*     */     {
/* 181 */       return this.original.containsRow(columnKey);
/*     */     }
/*     */ 
/*     */     public boolean containsRow(@Nullable Object rowKey)
/*     */     {
/* 186 */       return this.original.containsColumn(rowKey);
/*     */     }
/*     */ 
/*     */     public boolean containsValue(@Nullable Object value)
/*     */     {
/* 191 */       return this.original.containsValue(value);
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 196 */       return this.original.get(columnKey, rowKey);
/*     */     }
/*     */ 
/*     */     public boolean isEmpty()
/*     */     {
/* 201 */       return this.original.isEmpty();
/*     */     }
/*     */ 
/*     */     public V put(C rowKey, R columnKey, V value)
/*     */     {
/* 206 */       return this.original.put(columnKey, rowKey, value);
/*     */     }
/*     */ 
/*     */     public void putAll(Table<? extends C, ? extends R, ? extends V> table)
/*     */     {
/* 211 */       this.original.putAll(Tables.transpose(table));
/*     */     }
/*     */ 
/*     */     public V remove(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 216 */       return this.original.remove(columnKey, rowKey);
/*     */     }
/*     */ 
/*     */     public Map<R, V> row(C rowKey)
/*     */     {
/* 221 */       return this.original.column(rowKey);
/*     */     }
/*     */ 
/*     */     public Set<C> rowKeySet()
/*     */     {
/* 226 */       return this.original.columnKeySet();
/*     */     }
/*     */ 
/*     */     public Map<C, Map<R, V>> rowMap()
/*     */     {
/* 231 */       return this.original.columnMap();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 236 */       return this.original.size();
/*     */     }
/*     */ 
/*     */     public Collection<V> values()
/*     */     {
/* 241 */       return this.original.values();
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 245 */       if (obj == this) {
/* 246 */         return true;
/*     */       }
/* 248 */       if ((obj instanceof Table)) {
/* 249 */         Table other = (Table)obj;
/* 250 */         return cellSet().equals(other.cellSet());
/*     */       }
/* 252 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 256 */       return cellSet().hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 260 */       return rowMap().toString();
/*     */     }
/*     */ 
/*     */     public Set<Table.Cell<C, R, V>> cellSet()
/*     */     {
/* 277 */       CellSet result = this.cellSet;
/* 278 */       return result == null ? (this.cellSet = new CellSet()) : result;
/*     */     }
/*     */ 
/*     */     class CellSet extends Collections2.TransformedCollection<Table.Cell<R, C, V>, Table.Cell<C, R, V>>
/*     */       implements Set<Table.Cell<C, R, V>>
/*     */     {
/*     */       CellSet()
/*     */       {
/* 286 */         super(Tables.TransposeTable.TRANSPOSE_CELL);
/*     */       }
/*     */ 
/*     */       public boolean equals(Object obj) {
/* 290 */         if (obj == this) {
/* 291 */           return true;
/*     */         }
/* 293 */         if (!(obj instanceof Set)) {
/* 294 */           return false;
/*     */         }
/* 296 */         Set os = (Set)obj;
/* 297 */         if (os.size() != size()) {
/* 298 */           return false;
/*     */         }
/* 300 */         return containsAll(os);
/*     */       }
/*     */ 
/*     */       public int hashCode() {
/* 304 */         return Sets.hashCodeImpl(this);
/*     */       }
/*     */ 
/*     */       public boolean contains(Object obj) {
/* 308 */         if ((obj instanceof Table.Cell)) {
/* 309 */           Table.Cell cell = (Table.Cell)obj;
/* 310 */           return Tables.TransposeTable.this.original.cellSet().contains(Tables.immutableCell(cell.getColumnKey(), cell.getRowKey(), cell.getValue()));
/*     */         }
/*     */ 
/* 313 */         return false;
/*     */       }
/*     */ 
/*     */       public boolean remove(Object obj) {
/* 317 */         if ((obj instanceof Table.Cell)) {
/* 318 */           Table.Cell cell = (Table.Cell)obj;
/* 319 */           return Tables.TransposeTable.this.original.cellSet().remove(Tables.immutableCell(cell.getColumnKey(), cell.getRowKey(), cell.getValue()));
/*     */         }
/*     */ 
/* 322 */         return false;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class AbstractCell<R, C, V>
/*     */     implements Table.Cell<R, C, V>
/*     */   {
/*     */     public boolean equals(Object obj)
/*     */     {
/* 105 */       if (obj == this) {
/* 106 */         return true;
/*     */       }
/* 108 */       if ((obj instanceof Table.Cell)) {
/* 109 */         Table.Cell other = (Table.Cell)obj;
/* 110 */         return (Objects.equal(getRowKey(), other.getRowKey())) && (Objects.equal(getColumnKey(), other.getColumnKey())) && (Objects.equal(getValue(), other.getValue()));
/*     */       }
/*     */ 
/* 114 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 118 */       return Objects.hashCode(new Object[] { getRowKey(), getColumnKey(), getValue() });
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 122 */       return "(" + getRowKey() + "," + getColumnKey() + ")=" + getValue();
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ImmutableCell<R, C, V> extends Tables.AbstractCell<R, C, V>
/*     */     implements Serializable
/*     */   {
/*     */     private final R rowKey;
/*     */     private final C columnKey;
/*     */     private final V value;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     ImmutableCell(@Nullable R rowKey, @Nullable C columnKey, @Nullable V value)
/*     */     {
/*  79 */       this.rowKey = rowKey;
/*  80 */       this.columnKey = columnKey;
/*  81 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public R getRowKey()
/*     */     {
/*  86 */       return this.rowKey;
/*     */     }
/*     */ 
/*     */     public C getColumnKey() {
/*  90 */       return this.columnKey;
/*     */     }
/*     */ 
/*     */     public V getValue() {
/*  94 */       return this.value;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Tables
 * JD-Core Version:    0.6.2
 */