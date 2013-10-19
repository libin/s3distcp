/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true)
/*     */ @Beta
/*     */ public class TreeBasedTable<R, C, V> extends StandardRowSortedTable<R, C, V>
/*     */ {
/*     */   private final Comparator<? super C> columnComparator;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <R extends Comparable, C extends Comparable, V> TreeBasedTable<R, C, V> create()
/*     */   {
/* 106 */     return new TreeBasedTable(Ordering.natural(), Ordering.natural());
/*     */   }
/*     */ 
/*     */   public static <R, C, V> TreeBasedTable<R, C, V> create(Comparator<? super R> rowComparator, Comparator<? super C> columnComparator)
/*     */   {
/* 120 */     Preconditions.checkNotNull(rowComparator);
/* 121 */     Preconditions.checkNotNull(columnComparator);
/* 122 */     return new TreeBasedTable(rowComparator, columnComparator);
/*     */   }
/*     */ 
/*     */   public static <R, C, V> TreeBasedTable<R, C, V> create(TreeBasedTable<R, C, ? extends V> table)
/*     */   {
/* 131 */     TreeBasedTable result = new TreeBasedTable(table.rowComparator(), table.columnComparator());
/*     */ 
/* 134 */     result.putAll(table);
/* 135 */     return result;
/*     */   }
/*     */ 
/*     */   TreeBasedTable(Comparator<? super R> rowComparator, Comparator<? super C> columnComparator)
/*     */   {
/* 140 */     super(new TreeMap(rowComparator), new Factory(columnComparator));
/*     */ 
/* 142 */     this.columnComparator = columnComparator;
/*     */   }
/*     */ 
/*     */   public Comparator<? super R> rowComparator()
/*     */   {
/* 152 */     return rowKeySet().comparator();
/*     */   }
/*     */ 
/*     */   public Comparator<? super C> columnComparator()
/*     */   {
/* 160 */     return this.columnComparator;
/*     */   }
/*     */ 
/*     */   public SortedMap<C, V> row(R rowKey)
/*     */   {
/* 177 */     return new TreeRow(rowKey);
/*     */   }
/*     */ 
/*     */   public SortedSet<R> rowKeySet()
/*     */   {
/* 300 */     return super.rowKeySet();
/*     */   }
/*     */ 
/*     */   public SortedMap<R, Map<C, V>> rowMap() {
/* 304 */     return super.rowMap();
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 311 */     return super.contains(rowKey, columnKey);
/*     */   }
/*     */ 
/*     */   public boolean containsColumn(@Nullable Object columnKey) {
/* 315 */     return super.containsColumn(columnKey);
/*     */   }
/*     */ 
/*     */   public boolean containsRow(@Nullable Object rowKey) {
/* 319 */     return super.containsRow(rowKey);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/* 323 */     return super.containsValue(value);
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
/* 327 */     return super.get(rowKey, columnKey);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj) {
/* 331 */     return super.equals(obj);
/*     */   }
/*     */ 
/*     */   public V remove(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 336 */     return super.remove(rowKey, columnKey);
/*     */   }
/*     */ 
/*     */   Iterator<C> createColumnKeyIterator()
/*     */   {
/* 345 */     final Comparator comparator = columnComparator();
/*     */ 
/* 347 */     final Iterator merged = Iterators.mergeSorted(Iterables.transform(this.backingMap.values(), new Function()
/*     */     {
/*     */       public Iterator<C> apply(Map<C, V> input)
/*     */       {
/* 352 */         return input.keySet().iterator();
/*     */       }
/*     */     }), comparator);
/*     */ 
/* 356 */     return new AbstractIterator()
/*     */     {
/*     */       C lastValue;
/*     */ 
/*     */       protected C computeNext() {
/* 361 */         while (merged.hasNext()) {
/* 362 */           Object next = merged.next();
/* 363 */           boolean duplicate = (this.lastValue != null) && (comparator.compare(next, this.lastValue) == 0);
/*     */ 
/* 367 */           if (!duplicate) {
/* 368 */             this.lastValue = next;
/* 369 */             return this.lastValue;
/*     */           }
/*     */         }
/*     */ 
/* 373 */         this.lastValue = null;
/* 374 */         return endOfData();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private class TreeRow extends StandardTable<R, C, V>.Row
/*     */     implements SortedMap<C, V>
/*     */   {
/*     */ 
/*     */     @Nullable
/*     */     final C lowerBound;
/*     */ 
/*     */     @Nullable
/*     */     final C upperBound;
/*     */     transient SortedMap<C, V> wholeRow;
/*     */ 
/*     */     TreeRow()
/*     */     {
/* 185 */       this(rowKey, null, null);
/*     */     }
/*     */ 
/*     */     TreeRow(@Nullable C rowKey, @Nullable C lowerBound) {
/* 189 */       super(rowKey);
/* 190 */       this.lowerBound = lowerBound;
/* 191 */       this.upperBound = upperBound;
/* 192 */       Preconditions.checkArgument((lowerBound == null) || (upperBound == null) || (compare(lowerBound, upperBound) <= 0));
/*     */     }
/*     */ 
/*     */     public Comparator<? super C> comparator()
/*     */     {
/* 197 */       return TreeBasedTable.this.columnComparator();
/*     */     }
/*     */ 
/*     */     int compare(Object a, Object b)
/*     */     {
/* 203 */       Comparator cmp = comparator();
/* 204 */       return cmp.compare(a, b);
/*     */     }
/*     */ 
/*     */     boolean rangeContains(@Nullable Object o) {
/* 208 */       return (o != null) && ((this.lowerBound == null) || (compare(this.lowerBound, o) <= 0)) && ((this.upperBound == null) || (compare(this.upperBound, o) > 0));
/*     */     }
/*     */ 
/*     */     public SortedMap<C, V> subMap(C fromKey, C toKey)
/*     */     {
/* 213 */       Preconditions.checkArgument((rangeContains(Preconditions.checkNotNull(fromKey))) && (rangeContains(Preconditions.checkNotNull(toKey))));
/*     */ 
/* 215 */       return new TreeRow(TreeBasedTable.this, this.rowKey, fromKey, toKey);
/*     */     }
/*     */ 
/*     */     public SortedMap<C, V> headMap(C toKey) {
/* 219 */       Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(toKey)));
/* 220 */       return new TreeRow(TreeBasedTable.this, this.rowKey, this.lowerBound, toKey);
/*     */     }
/*     */ 
/*     */     public SortedMap<C, V> tailMap(C fromKey) {
/* 224 */       Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(fromKey)));
/* 225 */       return new TreeRow(TreeBasedTable.this, this.rowKey, fromKey, this.upperBound);
/*     */     }
/*     */ 
/*     */     public C firstKey() {
/* 229 */       SortedMap backing = backingRowMap();
/* 230 */       if (backing == null) {
/* 231 */         throw new NoSuchElementException();
/*     */       }
/* 233 */       return backingRowMap().firstKey();
/*     */     }
/*     */ 
/*     */     public C lastKey() {
/* 237 */       SortedMap backing = backingRowMap();
/* 238 */       if (backing == null) {
/* 239 */         throw new NoSuchElementException();
/*     */       }
/* 241 */       return backingRowMap().lastKey();
/*     */     }
/*     */ 
/*     */     SortedMap<C, V> wholeRow()
/*     */     {
/* 251 */       if ((this.wholeRow == null) || ((this.wholeRow.isEmpty()) && (TreeBasedTable.this.backingMap.containsKey(this.rowKey))))
/*     */       {
/* 253 */         this.wholeRow = ((SortedMap)TreeBasedTable.this.backingMap.get(this.rowKey));
/*     */       }
/* 255 */       return this.wholeRow;
/*     */     }
/*     */ 
/*     */     SortedMap<C, V> backingRowMap()
/*     */     {
/* 260 */       return (SortedMap)super.backingRowMap();
/*     */     }
/*     */ 
/*     */     SortedMap<C, V> computeBackingRowMap()
/*     */     {
/* 265 */       SortedMap map = wholeRow();
/* 266 */       if (map != null) {
/* 267 */         if (this.lowerBound != null) {
/* 268 */           map = map.tailMap(this.lowerBound);
/*     */         }
/* 270 */         if (this.upperBound != null) {
/* 271 */           map = map.headMap(this.upperBound);
/*     */         }
/* 273 */         return map;
/*     */       }
/* 275 */       return null;
/*     */     }
/*     */ 
/*     */     void maintainEmptyInvariant()
/*     */     {
/* 280 */       if ((wholeRow() != null) && (this.wholeRow.isEmpty())) {
/* 281 */         TreeBasedTable.this.backingMap.remove(this.rowKey);
/* 282 */         this.wholeRow = null;
/* 283 */         this.backingRowMap = null;
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean containsKey(Object key) {
/* 288 */       return (rangeContains(key)) && (super.containsKey(key));
/*     */     }
/*     */ 
/*     */     public V put(C key, V value) {
/* 292 */       Preconditions.checkArgument(rangeContains(Preconditions.checkNotNull(key)));
/* 293 */       return super.put(key, value);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class Factory<C, V>
/*     */     implements Supplier<TreeMap<C, V>>, Serializable
/*     */   {
/*     */     final Comparator<? super C> comparator;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     Factory(Comparator<? super C> comparator)
/*     */     {
/*  86 */       this.comparator = comparator;
/*     */     }
/*     */ 
/*     */     public TreeMap<C, V> get() {
/*  90 */       return new TreeMap(this.comparator);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.TreeBasedTable
 * JD-Core Version:    0.6.2
 */