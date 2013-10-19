/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Beta
/*     */ public final class Ranges
/*     */ {
/*     */   static <C extends Comparable<?>> Range<C> create(Cut<C> lowerBound, Cut<C> upperBound)
/*     */   {
/*  80 */     return new Range(lowerBound, upperBound);
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> open(C lower, C upper)
/*     */   {
/*  91 */     return create(Cut.aboveValue(lower), Cut.belowValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> closed(C lower, C upper)
/*     */   {
/* 102 */     return create(Cut.belowValue(lower), Cut.aboveValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> closedOpen(C lower, C upper)
/*     */   {
/* 114 */     return create(Cut.belowValue(lower), Cut.belowValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> openClosed(C lower, C upper)
/*     */   {
/* 126 */     return create(Cut.aboveValue(lower), Cut.aboveValue(upper));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> range(C lower, BoundType lowerType, C upper, BoundType upperType)
/*     */   {
/* 139 */     Preconditions.checkNotNull(lowerType);
/* 140 */     Preconditions.checkNotNull(upperType);
/*     */ 
/* 142 */     Cut lowerBound = lowerType == BoundType.OPEN ? Cut.aboveValue(lower) : Cut.belowValue(lower);
/*     */ 
/* 145 */     Cut upperBound = upperType == BoundType.OPEN ? Cut.belowValue(upper) : Cut.aboveValue(upper);
/*     */ 
/* 148 */     return create(lowerBound, upperBound);
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> lessThan(C endpoint)
/*     */   {
/* 156 */     return create(Cut.belowAll(), Cut.belowValue(endpoint));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> atMost(C endpoint)
/*     */   {
/* 164 */     return create(Cut.belowAll(), Cut.aboveValue(endpoint));
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> upTo(C endpoint, BoundType boundType)
/*     */   {
/* 173 */     switch (1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */     case 1:
/* 175 */       return lessThan(endpoint);
/*     */     case 2:
/* 177 */       return atMost(endpoint);
/*     */     }
/* 179 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> greaterThan(C endpoint)
/*     */   {
/* 188 */     return create(Cut.aboveValue(endpoint), Cut.aboveAll());
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> atLeast(C endpoint)
/*     */   {
/* 196 */     return create(Cut.belowValue(endpoint), Cut.aboveAll());
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> downTo(C endpoint, BoundType boundType)
/*     */   {
/* 205 */     switch (1.$SwitchMap$com$google$common$collect$BoundType[boundType.ordinal()]) {
/*     */     case 1:
/* 207 */       return greaterThan(endpoint);
/*     */     case 2:
/* 209 */       return atLeast(endpoint);
/*     */     }
/* 211 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> all()
/*     */   {
/* 217 */     return create(Cut.belowAll(), Cut.aboveAll());
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> singleton(C value)
/*     */   {
/* 226 */     return closed(value, value);
/*     */   }
/*     */ 
/*     */   public static <C extends Comparable<?>> Range<C> encloseAll(Iterable<C> values)
/*     */   {
/* 241 */     Preconditions.checkNotNull(values);
/* 242 */     if ((values instanceof ContiguousSet)) {
/* 243 */       return ((ContiguousSet)values).range();
/*     */     }
/* 245 */     Iterator valueIterator = values.iterator();
/* 246 */     Comparable min = (Comparable)Preconditions.checkNotNull(valueIterator.next());
/* 247 */     Comparable max = min;
/* 248 */     while (valueIterator.hasNext()) {
/* 249 */       Comparable value = (Comparable)Preconditions.checkNotNull(valueIterator.next());
/* 250 */       min = (Comparable)Ordering.natural().min(min, value);
/* 251 */       max = (Comparable)Ordering.natural().max(max, value);
/*     */     }
/* 253 */     return closed(min, max);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Ranges
 * JD-Core Version:    0.6.2
 */