/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class SingletonImmutableMap<K, V> extends ImmutableMap<K, V>
/*     */ {
/*     */   final transient K singleKey;
/*     */   final transient V singleValue;
/*     */   private transient Map.Entry<K, V> entry;
/*     */ 
/*     */   SingletonImmutableMap(K singleKey, V singleValue)
/*     */   {
/*  41 */     this.singleKey = singleKey;
/*  42 */     this.singleValue = singleValue;
/*     */   }
/*     */ 
/*     */   SingletonImmutableMap(Map.Entry<K, V> entry) {
/*  46 */     this.entry = entry;
/*  47 */     this.singleKey = entry.getKey();
/*  48 */     this.singleValue = entry.getValue();
/*     */   }
/*     */ 
/*     */   private Map.Entry<K, V> entry() {
/*  52 */     Map.Entry e = this.entry;
/*  53 */     return e == null ? (this.entry = Maps.immutableEntry(this.singleKey, this.singleValue)) : e;
/*     */   }
/*     */ 
/*     */   public V get(@Nullable Object key)
/*     */   {
/*  58 */     return this.singleKey.equals(key) ? this.singleValue : null;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  63 */     return 1;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  67 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key) {
/*  71 */     return this.singleKey.equals(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value) {
/*  75 */     return this.singleValue.equals(value);
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   ImmutableSet<Map.Entry<K, V>> createEntrySet()
/*     */   {
/*  84 */     return ImmutableSet.of(entry());
/*     */   }
/*     */ 
/*     */   ImmutableSet<K> createKeySet()
/*     */   {
/*  89 */     return ImmutableSet.of(this.singleKey);
/*     */   }
/*     */ 
/*     */   ImmutableCollection<V> createValues()
/*     */   {
/*  94 */     return ImmutableList.of(this.singleValue);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/*  98 */     if (object == this) {
/*  99 */       return true;
/*     */     }
/* 101 */     if ((object instanceof Map)) {
/* 102 */       Map that = (Map)object;
/* 103 */       if (that.size() != 1) {
/* 104 */         return false;
/*     */       }
/* 106 */       Map.Entry entry = (Map.Entry)that.entrySet().iterator().next();
/* 107 */       return (this.singleKey.equals(entry.getKey())) && (this.singleValue.equals(entry.getValue()));
/*     */     }
/*     */ 
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 114 */     return this.singleKey.hashCode() ^ this.singleValue.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 118 */     return '{' + this.singleKey.toString() + '=' + this.singleValue.toString() + '}';
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SingletonImmutableMap
 * JD-Core Version:    0.6.2
 */