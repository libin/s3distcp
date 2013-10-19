/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collection;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.NavigableMap;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtIncompatible("uses NavigableMap")
/*     */ final class TreeRangeSet<C extends Comparable> extends RangeSet<C>
/*     */ {
/*     */   private final NavigableMap<Cut<C>, Range<C>> rangesByLowerCut;
/*     */   private transient Set<Range<C>> asRanges;
/*     */   private transient RangeSet<C> complement;
/*     */ 
/*     */   public static <C extends Comparable> TreeRangeSet<C> create()
/*     */   {
/*  44 */     return new TreeRangeSet(new TreeMap());
/*     */   }
/*     */ 
/*     */   private TreeRangeSet(NavigableMap<Cut<C>, Range<C>> rangesByLowerCut) {
/*  48 */     this.rangesByLowerCut = rangesByLowerCut;
/*     */   }
/*     */ 
/*     */   public Set<Range<C>> asRanges()
/*     */   {
/*  55 */     Set result = this.asRanges;
/*  56 */     return result == null ? (this.asRanges = new AsRanges()) : result;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public Range<C> rangeContaining(C value)
/*     */   {
/*  79 */     Preconditions.checkNotNull(value);
/*  80 */     Map.Entry floorEntry = this.rangesByLowerCut.floorEntry(Cut.belowValue(value));
/*  81 */     if ((floorEntry != null) && (((Range)floorEntry.getValue()).contains(value))) {
/*  82 */       return (Range)floorEntry.getValue();
/*     */     }
/*     */ 
/*  85 */     return null;
/*     */   }
/*     */ 
/*     */   public boolean encloses(Range<C> range)
/*     */   {
/*  91 */     Preconditions.checkNotNull(range);
/*  92 */     Map.Entry floorEntry = this.rangesByLowerCut.floorEntry(range.lowerBound);
/*  93 */     return (floorEntry != null) && (((Range)floorEntry.getValue()).encloses(range));
/*     */   }
/*     */ 
/*     */   public void add(Range<C> rangeToAdd)
/*     */   {
/*  98 */     Preconditions.checkNotNull(rangeToAdd);
/*     */ 
/* 100 */     if (rangeToAdd.isEmpty()) {
/* 101 */       return;
/*     */     }
/*     */ 
/* 106 */     Cut lbToAdd = rangeToAdd.lowerBound;
/* 107 */     Cut ubToAdd = rangeToAdd.upperBound;
/*     */ 
/* 109 */     Map.Entry entryBelowLB = this.rangesByLowerCut.lowerEntry(lbToAdd);
/* 110 */     if (entryBelowLB != null)
/*     */     {
/* 112 */       Range rangeBelowLB = (Range)entryBelowLB.getValue();
/* 113 */       if (rangeBelowLB.upperBound.compareTo(lbToAdd) >= 0)
/*     */       {
/* 115 */         if (rangeBelowLB.upperBound.compareTo(ubToAdd) >= 0)
/*     */         {
/* 117 */           ubToAdd = rangeBelowLB.upperBound;
/*     */         }
/*     */ 
/* 123 */         lbToAdd = rangeBelowLB.lowerBound;
/*     */       }
/*     */     }
/*     */ 
/* 127 */     Map.Entry entryBelowUB = this.rangesByLowerCut.floorEntry(ubToAdd);
/* 128 */     if (entryBelowUB != null)
/*     */     {
/* 130 */       Range rangeBelowUB = (Range)entryBelowUB.getValue();
/* 131 */       if (rangeBelowUB.upperBound.compareTo(ubToAdd) >= 0)
/*     */       {
/* 133 */         ubToAdd = rangeBelowUB.upperBound;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 138 */     this.rangesByLowerCut.subMap(lbToAdd, ubToAdd).clear();
/*     */ 
/* 140 */     replaceRangeWithSameLowerBound(new Range(lbToAdd, ubToAdd));
/*     */   }
/*     */ 
/*     */   public void remove(Range<C> rangeToRemove)
/*     */   {
/* 145 */     Preconditions.checkNotNull(rangeToRemove);
/*     */ 
/* 147 */     if (rangeToRemove.isEmpty()) {
/* 148 */       return;
/*     */     }
/*     */ 
/* 154 */     Map.Entry entryBelowLB = this.rangesByLowerCut.lowerEntry(rangeToRemove.lowerBound);
/* 155 */     if (entryBelowLB != null)
/*     */     {
/* 157 */       Range rangeBelowLB = (Range)entryBelowLB.getValue();
/* 158 */       if (rangeBelowLB.upperBound.compareTo(rangeToRemove.lowerBound) >= 0)
/*     */       {
/* 160 */         if (rangeBelowLB.upperBound.compareTo(rangeToRemove.upperBound) >= 0)
/*     */         {
/* 162 */           replaceRangeWithSameLowerBound(new Range(rangeToRemove.upperBound, rangeBelowLB.upperBound));
/*     */         }
/*     */ 
/* 165 */         replaceRangeWithSameLowerBound(new Range(rangeBelowLB.lowerBound, rangeToRemove.lowerBound));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 170 */     Map.Entry entryBelowUB = this.rangesByLowerCut.floorEntry(rangeToRemove.upperBound);
/* 171 */     if (entryBelowUB != null)
/*     */     {
/* 173 */       Range rangeBelowUB = (Range)entryBelowUB.getValue();
/* 174 */       if (rangeBelowUB.upperBound.compareTo(rangeToRemove.upperBound) >= 0)
/*     */       {
/* 176 */         replaceRangeWithSameLowerBound(new Range(rangeToRemove.upperBound, rangeBelowUB.upperBound));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 181 */     this.rangesByLowerCut.subMap(rangeToRemove.lowerBound, rangeToRemove.upperBound).clear();
/*     */   }
/*     */ 
/*     */   private void replaceRangeWithSameLowerBound(Range<C> range) {
/* 185 */     if (range.isEmpty())
/* 186 */       this.rangesByLowerCut.remove(range.lowerBound);
/*     */     else
/* 188 */       this.rangesByLowerCut.put(range.lowerBound, range);
/*     */   }
/*     */ 
/*     */   public RangeSet<C> complement()
/*     */   {
/* 196 */     RangeSet result = this.complement;
/* 197 */     return result == null ? (this.complement = createComplement()) : result;
/*     */   }
/*     */ 
/*     */   private RangeSet<C> createComplement() {
/* 201 */     return new RangeSet.StandardComplement(this);
/*     */   }
/*     */ 
/*     */   final class AsRanges extends ForwardingCollection<Range<C>>
/*     */     implements Set<Range<C>>
/*     */   {
/*     */     AsRanges()
/*     */     {
/*     */     }
/*     */ 
/*     */     protected Collection<Range<C>> delegate()
/*     */     {
/*  62 */       return TreeRangeSet.this.rangesByLowerCut.values();
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/*  67 */       return Sets.hashCodeImpl(this);
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object o)
/*     */     {
/*  72 */       return Sets.equalsImpl(this, o);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.TreeRangeSet
 * JD-Core Version:    0.6.2
 */