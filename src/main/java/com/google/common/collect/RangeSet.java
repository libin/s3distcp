/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.AbstractSet;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ abstract class RangeSet<C extends Comparable>
/*     */ {
/*     */   public boolean contains(C value)
/*     */   {
/*  44 */     return rangeContaining(value) != null;
/*     */   }
/*     */ 
/*     */   public Range<C> rangeContaining(C value)
/*     */   {
/*  52 */     Preconditions.checkNotNull(value);
/*  53 */     for (Range range : asRanges()) {
/*  54 */       if (range.contains(value)) {
/*  55 */         return range;
/*     */       }
/*     */     }
/*  58 */     return null;
/*     */   }
/*     */ 
/*     */   public abstract Set<Range<C>> asRanges();
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  73 */     return asRanges().isEmpty();
/*     */   }
/*     */ 
/*     */   public abstract RangeSet<C> complement();
/*     */ 
/*     */   public void add(Range<C> range)
/*     */   {
/* 174 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void remove(Range<C> range)
/*     */   {
/* 187 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean encloses(Range<C> otherRange)
/*     */   {
/* 195 */     for (Range range : asRanges()) {
/* 196 */       if (range.encloses(otherRange)) {
/* 197 */         return true;
/*     */       }
/*     */     }
/* 200 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean enclosesAll(RangeSet<C> other)
/*     */   {
/* 214 */     for (Range range : other.asRanges()) {
/* 215 */       if (!encloses(range)) {
/* 216 */         return false;
/*     */       }
/*     */     }
/* 219 */     return true;
/*     */   }
/*     */ 
/*     */   public void addAll(RangeSet<C> other)
/*     */   {
/* 234 */     for (Range range : other.asRanges())
/* 235 */       add(range);
/*     */   }
/*     */ 
/*     */   public void removeAll(RangeSet<C> other)
/*     */   {
/* 251 */     for (Range range : other.asRanges())
/* 252 */       remove(range);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj)
/*     */   {
/* 262 */     if ((obj instanceof RangeSet)) {
/* 263 */       RangeSet other = (RangeSet)obj;
/* 264 */       return asRanges().equals(other.asRanges());
/*     */     }
/* 266 */     return false;
/*     */   }
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 271 */     return asRanges().hashCode();
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 281 */     StringBuilder builder = new StringBuilder();
/* 282 */     builder.append('{');
/* 283 */     for (Range range : asRanges()) {
/* 284 */       builder.append(range);
/*     */     }
/* 286 */     builder.append('}');
/* 287 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   static class StandardComplement<C extends Comparable> extends RangeSet<C>
/*     */   {
/*     */     final RangeSet<C> positive;
/*     */     private transient Set<Range<C>> asRanges;
/*     */ 
/*     */     public StandardComplement(RangeSet<C> positive)
/*     */     {
/*  92 */       this.positive = positive;
/*     */     }
/*     */ 
/*     */     public boolean contains(C value)
/*     */     {
/*  97 */       return !this.positive.contains(value);
/*     */     }
/*     */ 
/*     */     public void add(Range<C> range)
/*     */     {
/* 102 */       this.positive.remove(range);
/*     */     }
/*     */ 
/*     */     public void remove(Range<C> range)
/*     */     {
/* 107 */       this.positive.add(range);
/*     */     }
/*     */ 
/*     */     public final Set<Range<C>> asRanges()
/*     */     {
/* 114 */       Set result = this.asRanges;
/* 115 */       return result == null ? (this.asRanges = createAsRanges()) : result;
/*     */     }
/*     */ 
/*     */     Set<Range<C>> createAsRanges() {
/* 119 */       return new AbstractSet()
/*     */       {
/*     */         public Iterator<Range<C>> iterator()
/*     */         {
/* 123 */           final Iterator positiveIterator = RangeSet.StandardComplement.this.positive.asRanges().iterator();
/* 124 */           return new AbstractIterator() {
/* 125 */             Cut<C> prevCut = Cut.belowAll();
/*     */ 
/*     */             protected Range<C> computeNext()
/*     */             {
/* 129 */               while (positiveIterator.hasNext()) {
/* 130 */                 Cut oldCut = this.prevCut;
/* 131 */                 Range positiveRange = (Range)positiveIterator.next();
/* 132 */                 this.prevCut = positiveRange.upperBound;
/* 133 */                 if (oldCut.compareTo(positiveRange.lowerBound) < 0) {
/* 134 */                   return new Range(oldCut, positiveRange.lowerBound);
/*     */                 }
/*     */               }
/* 137 */               Cut posInfinity = Cut.aboveAll();
/* 138 */               if (this.prevCut.compareTo(posInfinity) < 0) {
/* 139 */                 Range result = new Range(this.prevCut, posInfinity);
/* 140 */                 this.prevCut = posInfinity;
/* 141 */                 return result;
/*     */               }
/* 143 */               return (Range)endOfData();
/*     */             }
/*     */           };
/*     */         }
/*     */ 
/*     */         public int size()
/*     */         {
/* 150 */           return Iterators.size(iterator());
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public RangeSet<C> complement()
/*     */     {
/* 157 */       return this.positive;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RangeSet
 * JD-Core Version:    0.6.2
 */