/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractMap.SimpleEntry;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtIncompatible("NavigableMap")
/*     */ final class RangeMap<K extends Comparable, V>
/*     */   implements Function<K, V>, Serializable
/*     */ {
/*     */   private final NavigableMap<Cut<K>, RangeValue<K, V>> map;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K extends Comparable, V> RangeMap<K, V> create()
/*     */   {
/*  44 */     return new RangeMap(new TreeMap());
/*     */   }
/*     */ 
/*     */   private RangeMap(NavigableMap<Cut<K>, RangeValue<K, V>> map) {
/*  48 */     this.map = map;
/*     */   }
/*     */ 
/*     */   public V apply(K input)
/*     */   {
/*  58 */     return get(input);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public V get(K key)
/*     */   {
/*  66 */     Map.Entry lowerEntry = this.map.lowerEntry(Cut.aboveValue(key));
/*  67 */     if ((lowerEntry != null) && (((Range)((RangeValue)lowerEntry.getValue()).getKey()).contains(key))) {
/*  68 */       return ((RangeValue)lowerEntry.getValue()).getValue();
/*     */     }
/*  70 */     return null;
/*     */   }
/*     */ 
/*     */   public void put(Range<K> keyRange, V value)
/*     */   {
/*  80 */     Preconditions.checkNotNull(keyRange);
/*  81 */     Preconditions.checkNotNull(value);
/*  82 */     if (keyRange.isEmpty()) {
/*  83 */       return;
/*     */     }
/*  85 */     clear(keyRange);
/*  86 */     putRange(new RangeValue(keyRange, value));
/*     */   }
/*     */ 
/*     */   public void putAll(RangeMap<K, V> rangeMap)
/*     */   {
/*  93 */     Preconditions.checkNotNull(rangeMap);
/*  94 */     for (RangeValue rangeValue : rangeMap.map.values())
/*  95 */       put((Range)rangeValue.getKey(), rangeValue.getValue());
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 103 */     this.map.clear();
/*     */   }
/*     */ 
/*     */   public void clear(Range<K> rangeToClear)
/*     */   {
/* 111 */     Preconditions.checkNotNull(rangeToClear);
/* 112 */     if (rangeToClear.isEmpty()) {
/* 113 */       return;
/*     */     }
/*     */ 
/* 116 */     Map.Entry lowerThanLB = this.map.lowerEntry(rangeToClear.lowerBound);
/*     */ 
/* 121 */     if (lowerThanLB != null) {
/* 122 */       RangeValue lowerRangeValue = (RangeValue)lowerThanLB.getValue();
/* 123 */       Cut upperCut = lowerRangeValue.getUpperBound();
/* 124 */       if (upperCut.compareTo(rangeToClear.lowerBound) >= 0) {
/* 125 */         RangeValue replacement = lowerRangeValue.withUpperBound(rangeToClear.lowerBound);
/* 126 */         if (replacement == null)
/* 127 */           removeRange(lowerRangeValue);
/*     */         else {
/* 129 */           putRange(replacement);
/*     */         }
/* 131 */         if (upperCut.compareTo(rangeToClear.upperBound) >= 0) {
/* 132 */           putRange(lowerRangeValue.withLowerBound(rangeToClear.upperBound));
/* 133 */           return;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 138 */     Map.Entry lowerThanUB = this.map.lowerEntry(rangeToClear.upperBound);
/* 139 */     if (lowerThanUB != null) {
/* 140 */       RangeValue lowerRangeValue = (RangeValue)lowerThanUB.getValue();
/* 141 */       Cut upperCut = lowerRangeValue.getUpperBound();
/* 142 */       if (upperCut.compareTo(rangeToClear.upperBound) >= 0)
/*     */       {
/* 144 */         removeRange(lowerRangeValue);
/* 145 */         putRange(lowerRangeValue.withLowerBound(rangeToClear.upperBound));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 150 */     this.map.subMap(rangeToClear.lowerBound, rangeToClear.upperBound).clear();
/*     */   }
/*     */ 
/*     */   private void removeRange(RangeValue<K, V> rangeValue) {
/* 154 */     RangeValue removed = (RangeValue)this.map.remove(rangeValue.getLowerBound());
/* 155 */     assert (removed == rangeValue);
/*     */   }
/*     */ 
/*     */   private void putRange(@Nullable RangeValue<K, V> rangeValue) {
/* 159 */     if ((rangeValue != null) && (!((Range)rangeValue.getKey()).isEmpty()))
/* 160 */       this.map.put(rangeValue.getLowerBound(), rangeValue);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object o)
/*     */   {
/* 209 */     return ((o instanceof RangeMap)) && (this.map.equals(((RangeMap)o).map));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 214 */     return this.map.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 219 */     return this.map.toString();
/*     */   }
/*     */ 
/*     */   private static final class RangeValue<K extends Comparable, V> extends AbstractMap.SimpleEntry<Range<K>, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     RangeValue(Range<K> key, V value)
/*     */     {
/* 168 */       super(Preconditions.checkNotNull(value));
/* 169 */       assert (!key.isEmpty());
/*     */     }
/*     */ 
/*     */     Cut<K> getLowerBound() {
/* 173 */       return ((Range)getKey()).lowerBound;
/*     */     }
/*     */ 
/*     */     Cut<K> getUpperBound() {
/* 177 */       return ((Range)getKey()).upperBound;
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     RangeValue<K, V> withLowerBound(Cut<K> newLowerBound) {
/* 182 */       Range newRange = new Range(newLowerBound, getUpperBound());
/* 183 */       return newRange.isEmpty() ? null : new RangeValue(newRange, getValue());
/*     */     }
/*     */ 
/*     */     @Nullable
/*     */     RangeValue<K, V> withUpperBound(Cut<K> newUpperBound) {
/* 188 */       Range newRange = new Range(getLowerBound(), newUpperBound);
/* 189 */       return newRange.isEmpty() ? null : new RangeValue(newRange, getValue());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RangeMap
 * JD-Core Version:    0.6.2
 */