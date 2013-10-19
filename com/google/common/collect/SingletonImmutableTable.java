/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ final class SingletonImmutableTable<R, C, V> extends ImmutableTable<R, C, V>
/*     */ {
/*     */   private final R singleRowKey;
/*     */   private final C singleColumnKey;
/*     */   private final V singleValue;
/*     */ 
/*     */   SingletonImmutableTable(R rowKey, C columnKey, V value)
/*     */   {
/*  40 */     this.singleRowKey = Preconditions.checkNotNull(rowKey);
/*  41 */     this.singleColumnKey = Preconditions.checkNotNull(columnKey);
/*  42 */     this.singleValue = Preconditions.checkNotNull(value);
/*     */   }
/*     */ 
/*     */   SingletonImmutableTable(Table.Cell<R, C, V> cell) {
/*  46 */     this(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*     */   }
/*     */ 
/*     */   public ImmutableSet<Table.Cell<R, C, V>> cellSet() {
/*  50 */     return ImmutableSet.of(Tables.immutableCell(this.singleRowKey, this.singleColumnKey, this.singleValue));
/*     */   }
/*     */ 
/*     */   public ImmutableMap<R, V> column(C columnKey)
/*     */   {
/*  55 */     Preconditions.checkNotNull(columnKey);
/*  56 */     return containsColumn(columnKey) ? ImmutableMap.of(this.singleRowKey, this.singleValue) : ImmutableMap.of();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<C> columnKeySet()
/*     */   {
/*  62 */     return ImmutableSet.of(this.singleColumnKey);
/*     */   }
/*     */ 
/*     */   public ImmutableMap<C, Map<R, V>> columnMap() {
/*  66 */     return ImmutableMap.of(this.singleColumnKey, ImmutableMap.of(this.singleRowKey, this.singleValue));
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  72 */     return (containsRow(rowKey)) && (containsColumn(columnKey));
/*     */   }
/*     */ 
/*     */   public boolean containsColumn(@Nullable Object columnKey) {
/*  76 */     return Objects.equal(this.singleColumnKey, columnKey);
/*     */   }
/*     */ 
/*     */   public boolean containsRow(@Nullable Object rowKey) {
/*  80 */     return Objects.equal(this.singleRowKey, rowKey);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/*  84 */     return Objects.equal(this.singleValue, value);
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
/*  88 */     return contains(rowKey, columnKey) ? this.singleValue : null;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  92 */     return false;
/*     */   }
/*     */ 
/*     */   public ImmutableMap<C, V> row(R rowKey) {
/*  96 */     Preconditions.checkNotNull(rowKey);
/*  97 */     return containsRow(rowKey) ? ImmutableMap.of(this.singleColumnKey, this.singleValue) : ImmutableMap.of();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<R> rowKeySet()
/*     */   {
/* 103 */     return ImmutableSet.of(this.singleRowKey);
/*     */   }
/*     */ 
/*     */   public ImmutableMap<R, Map<C, V>> rowMap() {
/* 107 */     return ImmutableMap.of(this.singleRowKey, ImmutableMap.of(this.singleColumnKey, this.singleValue));
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 112 */     return 1;
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<V> values() {
/* 116 */     return ImmutableSet.of(this.singleValue);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj) {
/* 120 */     if (obj == this)
/* 121 */       return true;
/* 122 */     if ((obj instanceof Table)) {
/* 123 */       Table that = (Table)obj;
/* 124 */       if (that.size() == 1) {
/* 125 */         Table.Cell thatCell = (Table.Cell)that.cellSet().iterator().next();
/* 126 */         return (Objects.equal(this.singleRowKey, thatCell.getRowKey())) && (Objects.equal(this.singleColumnKey, thatCell.getColumnKey())) && (Objects.equal(this.singleValue, thatCell.getValue()));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 131 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 135 */     return Objects.hashCode(new Object[] { this.singleRowKey, this.singleColumnKey, this.singleValue });
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 139 */     return '{' + this.singleRowKey + "={" + this.singleColumnKey + '=' + this.singleValue + "}}";
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SingletonImmutableTable
 * JD-Core Version:    0.6.2
 */