/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ImmutableTable<R, C, V>
/*     */   implements Table<R, C, V>
/*     */ {
/*     */   public static final <R, C, V> ImmutableTable<R, C, V> of()
/*     */   {
/*  50 */     return EmptyImmutableTable.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static final <R, C, V> ImmutableTable<R, C, V> of(R rowKey, C columnKey, V value)
/*     */   {
/*  56 */     return new SingletonImmutableTable(rowKey, columnKey, value);
/*     */   }
/*     */ 
/*     */   public static final <R, C, V> ImmutableTable<R, C, V> copyOf(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/*  75 */     if ((table instanceof ImmutableTable))
/*     */     {
/*  77 */       ImmutableTable parameterizedTable = (ImmutableTable)table;
/*     */ 
/*  79 */       return parameterizedTable;
/*     */     }
/*  81 */     int size = table.size();
/*  82 */     switch (size) {
/*     */     case 0:
/*  84 */       return of();
/*     */     case 1:
/*  86 */       Table.Cell onlyCell = (Table.Cell)Iterables.getOnlyElement(table.cellSet());
/*     */ 
/*  88 */       return of(onlyCell.getRowKey(), onlyCell.getColumnKey(), onlyCell.getValue());
/*     */     }
/*     */ 
/*  91 */     ImmutableSet.Builder cellSetBuilder = ImmutableSet.builder();
/*     */ 
/*  94 */     for (Table.Cell cell : table.cellSet())
/*     */     {
/*  99 */       cellSetBuilder.add(cellOf(cell.getRowKey(), cell.getColumnKey(), cell.getValue()));
/*     */     }
/*     */ 
/* 102 */     return RegularImmutableTable.forCells(cellSetBuilder.build());
/*     */   }
/*     */ 
/*     */   public static final <R, C, V> Builder<R, C, V> builder()
/*     */   {
/* 112 */     return new Builder();
/*     */   }
/*     */ 
/*     */   static <R, C, V> Table.Cell<R, C, V> cellOf(R rowKey, C columnKey, V value)
/*     */   {
/* 120 */     return Tables.immutableCell(Preconditions.checkNotNull(rowKey), Preconditions.checkNotNull(columnKey), Preconditions.checkNotNull(value));
/*     */   }
/*     */ 
/*     */   public abstract ImmutableSet<Table.Cell<R, C, V>> cellSet();
/*     */ 
/*     */   public abstract ImmutableMap<R, V> column(C paramC);
/*     */ 
/*     */   public abstract ImmutableSet<C> columnKeySet();
/*     */ 
/*     */   public abstract ImmutableMap<C, Map<R, V>> columnMap();
/*     */ 
/*     */   public abstract ImmutableMap<C, V> row(R paramR);
/*     */ 
/*     */   public abstract ImmutableSet<R> rowKeySet();
/*     */ 
/*     */   public abstract ImmutableMap<R, Map<C, V>> rowMap();
/*     */ 
/*     */   public final void clear()
/*     */   {
/* 287 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final V put(R rowKey, C columnKey, V value)
/*     */   {
/* 296 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final void putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 306 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final V remove(Object rowKey, Object columnKey)
/*     */   {
/* 315 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj) {
/* 319 */     if (obj == this)
/* 320 */       return true;
/* 321 */     if ((obj instanceof Table)) {
/* 322 */       Table that = (Table)obj;
/* 323 */       return cellSet().equals(that.cellSet());
/*     */     }
/* 325 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 330 */     return cellSet().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 334 */     return rowMap().toString();
/*     */   }
/*     */ 
/*     */   public static final class Builder<R, C, V>
/*     */   {
/* 151 */     private final List<Table.Cell<R, C, V>> cells = Lists.newArrayList();
/*     */     private Comparator<? super R> rowComparator;
/*     */     private Comparator<? super C> columnComparator;
/*     */ 
/*     */     public Builder<R, C, V> orderRowsBy(Comparator<? super R> rowComparator)
/*     */     {
/* 165 */       this.rowComparator = ((Comparator)Preconditions.checkNotNull(rowComparator));
/* 166 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<R, C, V> orderColumnsBy(Comparator<? super C> columnComparator)
/*     */     {
/* 174 */       this.columnComparator = ((Comparator)Preconditions.checkNotNull(columnComparator));
/* 175 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<R, C, V> put(R rowKey, C columnKey, V value)
/*     */     {
/* 184 */       this.cells.add(ImmutableTable.cellOf(rowKey, columnKey, value));
/* 185 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<R, C, V> put(Table.Cell<? extends R, ? extends C, ? extends V> cell)
/*     */     {
/* 195 */       if ((cell instanceof Tables.ImmutableCell)) {
/* 196 */         Preconditions.checkNotNull(cell.getRowKey());
/* 197 */         Preconditions.checkNotNull(cell.getColumnKey());
/* 198 */         Preconditions.checkNotNull(cell.getValue());
/*     */ 
/* 200 */         Table.Cell immutableCell = cell;
/* 201 */         this.cells.add(immutableCell);
/*     */       } else {
/* 203 */         put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
/*     */       }
/* 205 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<R, C, V> putAll(Table<? extends R, ? extends C, ? extends V> table)
/*     */     {
/* 217 */       for (Table.Cell cell : table.cellSet()) {
/* 218 */         put(cell);
/*     */       }
/* 220 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableTable<R, C, V> build()
/*     */     {
/* 229 */       int size = this.cells.size();
/* 230 */       switch (size) {
/*     */       case 0:
/* 232 */         return ImmutableTable.of();
/*     */       case 1:
/* 234 */         return new SingletonImmutableTable((Table.Cell)Iterables.getOnlyElement(this.cells));
/*     */       }
/*     */ 
/* 237 */       return RegularImmutableTable.forCells(this.cells, this.rowComparator, this.columnComparator);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableTable
 * JD-Core Version:    0.6.2
 */