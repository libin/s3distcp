/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ @GwtCompatible
/*     */ abstract class RegularImmutableTable<R, C, V> extends ImmutableTable<R, C, V>
/*     */ {
/*     */   private final ImmutableSet<Table.Cell<R, C, V>> cellSet;
/*  50 */   private static final Function<Table.Cell<Object, Object, Object>, Object> GET_VALUE_FUNCTION = new Function()
/*     */   {
/*     */     public Object apply(Table.Cell<Object, Object, Object> from) {
/*  53 */       return from.getValue();
/*     */     }
/*  50 */   };
/*     */ 
/*     */   @Nullable
/*     */   private volatile transient ImmutableList<V> valueList;
/*     */ 
/*     */   private RegularImmutableTable(ImmutableSet<Table.Cell<R, C, V>> cellSet)
/*     */   {
/*  46 */     this.cellSet = cellSet;
/*     */   }
/*     */ 
/*     */   private Function<Table.Cell<R, C, V>, V> getValueFunction()
/*     */   {
/*  59 */     return GET_VALUE_FUNCTION;
/*     */   }
/*     */ 
/*     */   public final ImmutableCollection<V> values()
/*     */   {
/*  65 */     ImmutableList result = this.valueList;
/*  66 */     if (result == null) {
/*  67 */       this.valueList = (result = ImmutableList.copyOf(Iterables.transform(cellSet(), getValueFunction())));
/*     */     }
/*     */ 
/*  70 */     return result;
/*     */   }
/*     */ 
/*     */   public final int size() {
/*  74 */     return cellSet().size();
/*     */   }
/*     */ 
/*     */   public final boolean containsValue(@Nullable Object value) {
/*  78 */     return values().contains(value);
/*     */   }
/*     */ 
/*     */   public final boolean isEmpty() {
/*  82 */     return false;
/*     */   }
/*     */ 
/*     */   public final ImmutableSet<Table.Cell<R, C, V>> cellSet() {
/*  86 */     return this.cellSet;
/*     */   }
/*     */ 
/*     */   static final <R, C, V> RegularImmutableTable<R, C, V> forCells(List<Table.Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable final Comparator<? super C> columnComparator)
/*     */   {
/*  93 */     Preconditions.checkNotNull(cells);
/*  94 */     if ((rowComparator != null) || (columnComparator != null))
/*     */     {
/* 103 */       Comparator comparator = new Comparator() {
/*     */         public int compare(Table.Cell<R, C, V> cell1, Table.Cell<R, C, V> cell2) {
/* 105 */           int rowCompare = this.val$rowComparator == null ? 0 : this.val$rowComparator.compare(cell1.getRowKey(), cell2.getRowKey());
/*     */ 
/* 107 */           if (rowCompare != 0) {
/* 108 */             return rowCompare;
/*     */           }
/* 110 */           return columnComparator == null ? 0 : columnComparator.compare(cell1.getColumnKey(), cell2.getColumnKey());
/*     */         }
/*     */       };
/* 115 */       Collections.sort(cells, comparator);
/*     */     }
/* 117 */     return forCellsInternal(cells, rowComparator, columnComparator);
/*     */   }
/*     */ 
/*     */   static final <R, C, V> RegularImmutableTable<R, C, V> forCells(Iterable<Table.Cell<R, C, V>> cells)
/*     */   {
/* 122 */     return forCellsInternal(cells, null, null);
/*     */   }
/*     */ 
/*     */   private static final <R, C, V> RegularImmutableTable<R, C, V> forCellsInternal(Iterable<Table.Cell<R, C, V>> cells, @Nullable Comparator<? super R> rowComparator, @Nullable Comparator<? super C> columnComparator)
/*     */   {
/* 133 */     ImmutableSet.Builder cellSetBuilder = ImmutableSet.builder();
/* 134 */     ImmutableSet.Builder rowSpaceBuilder = ImmutableSet.builder();
/* 135 */     ImmutableSet.Builder columnSpaceBuilder = ImmutableSet.builder();
/* 136 */     for (Table.Cell cell : cells) {
/* 137 */       cellSetBuilder.add(cell);
/* 138 */       rowSpaceBuilder.add(cell.getRowKey());
/* 139 */       columnSpaceBuilder.add(cell.getColumnKey());
/*     */     }
/* 141 */     ImmutableSet cellSet = cellSetBuilder.build();
/*     */ 
/* 143 */     ImmutableSet rowSpace = rowSpaceBuilder.build();
/* 144 */     if (rowComparator != null) {
/* 145 */       List rowList = Lists.newArrayList(rowSpace);
/* 146 */       Collections.sort(rowList, rowComparator);
/* 147 */       rowSpace = ImmutableSet.copyOf(rowList);
/*     */     }
/* 149 */     ImmutableSet columnSpace = columnSpaceBuilder.build();
/* 150 */     if (columnComparator != null) {
/* 151 */       List columnList = Lists.newArrayList(columnSpace);
/* 152 */       Collections.sort(columnList, columnComparator);
/* 153 */       columnSpace = ImmutableSet.copyOf(columnList);
/*     */     }
/*     */ 
/* 158 */     return cellSet.size() > rowSpace.size() * columnSpace.size() / 2 ? new DenseImmutableTable(cellSet, rowSpace, columnSpace) : new SparseImmutableTable(cellSet, rowSpace, columnSpace);
/*     */   }
/*     */ 
/*     */   @Immutable
/*     */   @VisibleForTesting
/*     */   static final class DenseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V>
/*     */   {
/*     */     private final ImmutableBiMap<R, Integer> rowKeyToIndex;
/*     */     private final ImmutableBiMap<C, Integer> columnKeyToIndex;
/*     */     private final V[][] values;
/*     */     private volatile transient ImmutableMap<C, Map<R, V>> columnMap;
/*     */     private volatile transient ImmutableMap<R, Map<C, V>> rowMap;
/*     */ 
/*     */     private static <E> ImmutableBiMap<E, Integer> makeIndex(ImmutableSet<E> set)
/*     */     {
/* 283 */       ImmutableBiMap.Builder indexBuilder = ImmutableBiMap.builder();
/*     */ 
/* 285 */       int i = 0;
/* 286 */       for (Iterator i$ = set.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 287 */         indexBuilder.put(key, Integer.valueOf(i));
/* 288 */         i++;
/*     */       }
/* 290 */       return indexBuilder.build();
/*     */     }
/*     */ 
/*     */     DenseImmutableTable(ImmutableSet<Table.Cell<R, C, V>> cellSet, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace)
/*     */     {
/* 295 */       super(null);
/*     */ 
/* 297 */       Object[][] array = (Object[][])new Object[rowSpace.size()][columnSpace.size()];
/* 298 */       this.values = array;
/* 299 */       this.rowKeyToIndex = makeIndex(rowSpace);
/* 300 */       this.columnKeyToIndex = makeIndex(columnSpace);
/* 301 */       for (Table.Cell cell : cellSet) {
/* 302 */         Object rowKey = cell.getRowKey();
/* 303 */         Object columnKey = cell.getColumnKey();
/* 304 */         int rowIndex = ((Integer)this.rowKeyToIndex.get(rowKey)).intValue();
/* 305 */         int columnIndex = ((Integer)this.columnKeyToIndex.get(columnKey)).intValue();
/* 306 */         Object existingValue = this.values[rowIndex][columnIndex];
/* 307 */         Preconditions.checkArgument(existingValue == null, "duplicate key: (%s, %s)", new Object[] { rowKey, columnKey });
/*     */ 
/* 309 */         this.values[rowIndex][columnIndex] = cell.getValue();
/*     */       }
/*     */     }
/*     */ 
/*     */     public ImmutableMap<R, V> column(C columnKey) {
/* 314 */       Preconditions.checkNotNull(columnKey);
/* 315 */       Integer columnIndexInteger = (Integer)this.columnKeyToIndex.get(columnKey);
/* 316 */       if (columnIndexInteger == null) {
/* 317 */         return ImmutableMap.of();
/*     */       }
/*     */ 
/* 320 */       int columnIndex = columnIndexInteger.intValue();
/* 321 */       ImmutableMap.Builder columnBuilder = ImmutableMap.builder();
/* 322 */       for (int i = 0; i < this.values.length; i++) {
/* 323 */         Object value = this.values[i][columnIndex];
/* 324 */         if (value != null) {
/* 325 */           columnBuilder.put(this.rowKeyToIndex.inverse().get(Integer.valueOf(i)), value);
/*     */         }
/*     */       }
/* 328 */       return columnBuilder.build();
/*     */     }
/*     */ 
/*     */     public ImmutableSet<C> columnKeySet()
/*     */     {
/* 333 */       return this.columnKeyToIndex.keySet();
/*     */     }
/*     */ 
/*     */     private ImmutableMap<C, Map<R, V>> makeColumnMap()
/*     */     {
/* 339 */       ImmutableMap.Builder columnMapBuilder = ImmutableMap.builder();
/*     */ 
/* 341 */       for (int c = 0; c < this.columnKeyToIndex.size(); c++) {
/* 342 */         ImmutableMap.Builder rowMapBuilder = ImmutableMap.builder();
/* 343 */         for (int r = 0; r < this.rowKeyToIndex.size(); r++) {
/* 344 */           Object value = this.values[r][c];
/* 345 */           if (value != null) {
/* 346 */             rowMapBuilder.put(this.rowKeyToIndex.inverse().get(Integer.valueOf(r)), value);
/*     */           }
/*     */         }
/* 349 */         columnMapBuilder.put(this.columnKeyToIndex.inverse().get(Integer.valueOf(c)), rowMapBuilder.build());
/*     */       }
/*     */ 
/* 352 */       return columnMapBuilder.build();
/*     */     }
/*     */ 
/*     */     public ImmutableMap<C, Map<R, V>> columnMap() {
/* 356 */       ImmutableMap result = this.columnMap;
/* 357 */       if (result == null) {
/* 358 */         this.columnMap = (result = makeColumnMap());
/*     */       }
/* 360 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 365 */       return get(rowKey, columnKey) != null;
/*     */     }
/*     */ 
/*     */     public boolean containsColumn(@Nullable Object columnKey) {
/* 369 */       return this.columnKeyToIndex.containsKey(columnKey);
/*     */     }
/*     */ 
/*     */     public boolean containsRow(@Nullable Object rowKey) {
/* 373 */       return this.rowKeyToIndex.containsKey(rowKey);
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 378 */       Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 379 */       Integer columnIndex = (Integer)this.columnKeyToIndex.get(columnKey);
/* 380 */       return (rowIndex == null) || (columnIndex == null) ? null : this.values[rowIndex.intValue()][columnIndex.intValue()];
/*     */     }
/*     */ 
/*     */     public ImmutableMap<C, V> row(R rowKey)
/*     */     {
/* 385 */       Preconditions.checkNotNull(rowKey);
/* 386 */       Integer rowIndex = (Integer)this.rowKeyToIndex.get(rowKey);
/* 387 */       if (rowIndex == null) {
/* 388 */         return ImmutableMap.of();
/*     */       }
/* 390 */       ImmutableMap.Builder rowBuilder = ImmutableMap.builder();
/* 391 */       Object[] row = this.values[rowIndex.intValue()];
/* 392 */       for (int r = 0; r < row.length; r++) {
/* 393 */         Object value = row[r];
/* 394 */         if (value != null) {
/* 395 */           rowBuilder.put(this.columnKeyToIndex.inverse().get(Integer.valueOf(r)), value);
/*     */         }
/*     */       }
/* 398 */       return rowBuilder.build();
/*     */     }
/*     */ 
/*     */     public ImmutableSet<R> rowKeySet()
/*     */     {
/* 403 */       return this.rowKeyToIndex.keySet();
/*     */     }
/*     */ 
/*     */     private ImmutableMap<R, Map<C, V>> makeRowMap()
/*     */     {
/* 409 */       ImmutableMap.Builder rowMapBuilder = ImmutableMap.builder();
/* 410 */       for (int r = 0; r < this.values.length; r++) {
/* 411 */         Object[] row = this.values[r];
/* 412 */         ImmutableMap.Builder columnMapBuilder = ImmutableMap.builder();
/* 413 */         for (int c = 0; c < row.length; c++) {
/* 414 */           Object value = row[c];
/* 415 */           if (value != null) {
/* 416 */             columnMapBuilder.put(this.columnKeyToIndex.inverse().get(Integer.valueOf(c)), value);
/*     */           }
/*     */         }
/* 419 */         rowMapBuilder.put(this.rowKeyToIndex.inverse().get(Integer.valueOf(r)), columnMapBuilder.build());
/*     */       }
/*     */ 
/* 422 */       return rowMapBuilder.build();
/*     */     }
/*     */ 
/*     */     public ImmutableMap<R, Map<C, V>> rowMap() {
/* 426 */       ImmutableMap result = this.rowMap;
/* 427 */       if (result == null) {
/* 428 */         this.rowMap = (result = makeRowMap());
/*     */       }
/* 430 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   @Immutable
/*     */   @VisibleForTesting
/*     */   static final class SparseImmutableTable<R, C, V> extends RegularImmutableTable<R, C, V>
/*     */   {
/*     */     private final ImmutableMap<R, Map<C, V>> rowMap;
/*     */     private final ImmutableMap<C, Map<R, V>> columnMap;
/*     */ 
/*     */     private static final <A, B, V> Map<A, ImmutableMap.Builder<B, V>> makeIndexBuilder(ImmutableSet<A> keySpace)
/*     */     {
/* 180 */       Map indexBuilder = Maps.newLinkedHashMap();
/* 181 */       for (Iterator i$ = keySpace.iterator(); i$.hasNext(); ) { Object key = i$.next();
/* 182 */         indexBuilder.put(key, ImmutableMap.builder());
/*     */       }
/* 184 */       return indexBuilder;
/*     */     }
/*     */ 
/*     */     private static final <A, B, V> ImmutableMap<A, Map<B, V>> buildIndex(Map<A, ImmutableMap.Builder<B, V>> indexBuilder)
/*     */     {
/* 193 */       return ImmutableMap.copyOf(Maps.transformValues(indexBuilder, new Function()
/*     */       {
/*     */         public Map<B, V> apply(ImmutableMap.Builder<B, V> from) {
/* 196 */           return from.build();
/*     */         }
/*     */       }));
/*     */     }
/*     */ 
/*     */     SparseImmutableTable(ImmutableSet<Table.Cell<R, C, V>> cellSet, ImmutableSet<R> rowSpace, ImmutableSet<C> columnSpace)
/*     */     {
/* 203 */       super(null);
/* 204 */       Map rowIndexBuilder = makeIndexBuilder(rowSpace);
/*     */ 
/* 206 */       Map columnIndexBuilder = makeIndexBuilder(columnSpace);
/*     */ 
/* 208 */       for (Table.Cell cell : cellSet) {
/* 209 */         Object rowKey = cell.getRowKey();
/* 210 */         Object columnKey = cell.getColumnKey();
/* 211 */         Object value = cell.getValue();
/* 212 */         ((ImmutableMap.Builder)rowIndexBuilder.get(rowKey)).put(columnKey, value);
/* 213 */         ((ImmutableMap.Builder)columnIndexBuilder.get(columnKey)).put(rowKey, value);
/*     */       }
/* 215 */       this.rowMap = buildIndex(rowIndexBuilder);
/* 216 */       this.columnMap = buildIndex(columnIndexBuilder);
/*     */     }
/*     */ 
/*     */     public ImmutableMap<R, V> column(C columnKey) {
/* 220 */       Preconditions.checkNotNull(columnKey);
/*     */ 
/* 222 */       return (ImmutableMap)Objects.firstNonNull((ImmutableMap)this.columnMap.get(columnKey), ImmutableMap.of());
/*     */     }
/*     */ 
/*     */     public ImmutableSet<C> columnKeySet()
/*     */     {
/* 227 */       return this.columnMap.keySet();
/*     */     }
/*     */ 
/*     */     public ImmutableMap<C, Map<R, V>> columnMap() {
/* 231 */       return this.columnMap;
/*     */     }
/*     */ 
/*     */     public ImmutableMap<C, V> row(R rowKey) {
/* 235 */       Preconditions.checkNotNull(rowKey);
/*     */ 
/* 237 */       return (ImmutableMap)Objects.firstNonNull((ImmutableMap)this.rowMap.get(rowKey), ImmutableMap.of());
/*     */     }
/*     */ 
/*     */     public ImmutableSet<R> rowKeySet()
/*     */     {
/* 242 */       return this.rowMap.keySet();
/*     */     }
/*     */ 
/*     */     public ImmutableMap<R, Map<C, V>> rowMap() {
/* 246 */       return this.rowMap;
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 251 */       Map row = (Map)this.rowMap.get(rowKey);
/* 252 */       return (row != null) && (row.containsKey(columnKey));
/*     */     }
/*     */ 
/*     */     public boolean containsColumn(@Nullable Object columnKey) {
/* 256 */       return this.columnMap.containsKey(columnKey);
/*     */     }
/*     */ 
/*     */     public boolean containsRow(@Nullable Object rowKey) {
/* 260 */       return this.rowMap.containsKey(rowKey);
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */     {
/* 265 */       Map row = (Map)this.rowMap.get(rowKey);
/* 266 */       return row == null ? null : row.get(columnKey);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RegularImmutableTable
 * JD-Core Version:    0.6.2
 */