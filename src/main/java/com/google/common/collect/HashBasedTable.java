/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import java.io.Serializable;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true)
/*     */ public class HashBasedTable<R, C, V> extends StandardTable<R, C, V>
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <R, C, V> HashBasedTable<R, C, V> create()
/*     */   {
/*  74 */     return new HashBasedTable(new HashMap(), new Factory(0));
/*     */   }
/*     */ 
/*     */   public static <R, C, V> HashBasedTable<R, C, V> create(int expectedRows, int expectedCellsPerRow)
/*     */   {
/*  89 */     Preconditions.checkArgument(expectedCellsPerRow >= 0);
/*  90 */     Map backingMap = Maps.newHashMapWithExpectedSize(expectedRows);
/*     */ 
/*  92 */     return new HashBasedTable(backingMap, new Factory(expectedCellsPerRow));
/*     */   }
/*     */ 
/*     */   public static <R, C, V> HashBasedTable<R, C, V> create(Table<? extends R, ? extends C, ? extends V> table)
/*     */   {
/* 106 */     HashBasedTable result = create();
/* 107 */     result.putAll(table);
/* 108 */     return result;
/*     */   }
/*     */ 
/*     */   HashBasedTable(Map<R, Map<C, V>> backingMap, Factory<C, V> factory) {
/* 112 */     super(backingMap, factory);
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 119 */     return super.contains(rowKey, columnKey);
/*     */   }
/*     */ 
/*     */   public boolean containsColumn(@Nullable Object columnKey) {
/* 123 */     return super.containsColumn(columnKey);
/*     */   }
/*     */ 
/*     */   public boolean containsRow(@Nullable Object rowKey) {
/* 127 */     return super.containsRow(rowKey);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/* 131 */     return super.containsValue(value);
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
/* 135 */     return super.get(rowKey, columnKey);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj) {
/* 139 */     return super.equals(obj);
/*     */   }
/*     */ 
/*     */   public V remove(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/* 144 */     return super.remove(rowKey, columnKey);
/*     */   }
/*     */ 
/*     */   private static class Factory<C, V>
/*     */     implements Supplier<Map<C, V>>, Serializable
/*     */   {
/*     */     final int expectedSize;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     Factory(int expectedSize)
/*     */     {
/*  61 */       this.expectedSize = expectedSize;
/*     */     }
/*     */ 
/*     */     public Map<C, V> get() {
/*  65 */       return Maps.newHashMapWithExpectedSize(this.expectedSize);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.HashBasedTable
 * JD-Core Version:    0.6.2
 */