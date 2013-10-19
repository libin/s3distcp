/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Immutable
/*     */ final class EmptyImmutableTable extends ImmutableTable<Object, Object, Object>
/*     */ {
/*  36 */   static final EmptyImmutableTable INSTANCE = new EmptyImmutableTable();
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public int size()
/*     */   {
/*  41 */     return 0;
/*     */   }
/*     */ 
/*     */   public Object get(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  46 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  50 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj) {
/*  54 */     if (obj == this)
/*  55 */       return true;
/*  56 */     if ((obj instanceof Table)) {
/*  57 */       Table that = (Table)obj;
/*  58 */       return that.isEmpty();
/*     */     }
/*  60 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  65 */     return 0;
/*     */   }
/*     */ 
/*     */   public ImmutableSet<Table.Cell<Object, Object, Object>> cellSet() {
/*  69 */     return ImmutableSet.of();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<Object, Object> column(Object columnKey) {
/*  73 */     Preconditions.checkNotNull(columnKey);
/*  74 */     return ImmutableMap.of();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<Object> columnKeySet() {
/*  78 */     return ImmutableSet.of();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<Object, Map<Object, Object>> columnMap() {
/*  82 */     return ImmutableMap.of();
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey)
/*     */   {
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsColumn(@Nullable Object columnKey) {
/*  91 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsRow(@Nullable Object rowKey) {
/*  95 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/*  99 */     return false;
/*     */   }
/*     */ 
/*     */   public ImmutableMap<Object, Object> row(Object rowKey) {
/* 103 */     Preconditions.checkNotNull(rowKey);
/* 104 */     return ImmutableMap.of();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<Object> rowKeySet() {
/* 108 */     return ImmutableSet.of();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<Object, Map<Object, Object>> rowMap() {
/* 112 */     return ImmutableMap.of();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 116 */     return "{}";
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<Object> values() {
/* 120 */     return ImmutableSet.of();
/*     */   }
/*     */ 
/*     */   Object readResolve() {
/* 124 */     return INSTANCE;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableTable
 * JD-Core Version:    0.6.2
 */