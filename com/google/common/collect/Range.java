/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicate;
/*     */ import java.io.Serializable;
/*     */ import java.util.Comparator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Beta
/*     */ public final class Range<C extends Comparable>
/*     */   implements Predicate<C>, Serializable
/*     */ {
/*     */   final Cut<C> lowerBound;
/*     */   final Cut<C> upperBound;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   Range(Cut<C> lowerBound, Cut<C> upperBound)
/*     */   {
/* 123 */     if (lowerBound.compareTo(upperBound) > 0) {
/* 124 */       throw new IllegalArgumentException("Invalid range: " + toString(lowerBound, upperBound));
/*     */     }
/* 126 */     this.lowerBound = lowerBound;
/* 127 */     this.upperBound = upperBound;
/*     */   }
/*     */ 
/*     */   public boolean hasLowerBound()
/*     */   {
/* 134 */     return this.lowerBound != Cut.belowAll();
/*     */   }
/*     */ 
/*     */   public C lowerEndpoint()
/*     */   {
/* 144 */     return this.lowerBound.endpoint();
/*     */   }
/*     */ 
/*     */   public BoundType lowerBoundType()
/*     */   {
/* 155 */     return this.lowerBound.typeAsLowerBound();
/*     */   }
/*     */ 
/*     */   public boolean hasUpperBound()
/*     */   {
/* 162 */     return this.upperBound != Cut.aboveAll();
/*     */   }
/*     */ 
/*     */   public C upperEndpoint()
/*     */   {
/* 172 */     return this.upperBound.endpoint();
/*     */   }
/*     */ 
/*     */   public BoundType upperBoundType()
/*     */   {
/* 183 */     return this.upperBound.typeAsUpperBound();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 195 */     return this.lowerBound.equals(this.upperBound);
/*     */   }
/*     */ 
/*     */   public boolean contains(C value)
/*     */   {
/* 204 */     Preconditions.checkNotNull(value);
/*     */ 
/* 206 */     return (this.lowerBound.isLessThan(value)) && (!this.upperBound.isLessThan(value));
/*     */   }
/*     */ 
/*     */   public boolean apply(C input)
/*     */   {
/* 214 */     return contains(input);
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Iterable<? extends C> values)
/*     */   {
/* 222 */     if (Iterables.isEmpty(values)) {
/* 223 */       return true;
/*     */     }
/*     */ 
/* 227 */     if ((values instanceof SortedSet)) {
/* 228 */       SortedSet set = cast(values);
/* 229 */       Comparator comparator = set.comparator();
/* 230 */       if ((Ordering.natural().equals(comparator)) || (comparator == null)) {
/* 231 */         return (contains((Comparable)set.first())) && (contains((Comparable)set.last()));
/*     */       }
/*     */     }
/*     */ 
/* 235 */     for (Comparable value : values) {
/* 236 */       if (!contains(value)) {
/* 237 */         return false;
/*     */       }
/*     */     }
/* 240 */     return true;
/*     */   }
/*     */ 
/*     */   public boolean encloses(Range<C> other)
/*     */   {
/* 267 */     return (this.lowerBound.compareTo(other.lowerBound) <= 0) && (this.upperBound.compareTo(other.upperBound) >= 0);
/*     */   }
/*     */ 
/*     */   public boolean isConnected(Range<C> other)
/*     */   {
/* 291 */     return (this.lowerBound.compareTo(other.upperBound) <= 0) && (other.lowerBound.compareTo(this.upperBound) <= 0);
/*     */   }
/*     */ 
/*     */   public Range<C> intersection(Range<C> connectedRange)
/*     */   {
/* 312 */     Cut newLower = (Cut)Ordering.natural().max(this.lowerBound, connectedRange.lowerBound);
/* 313 */     Cut newUpper = (Cut)Ordering.natural().min(this.upperBound, connectedRange.upperBound);
/* 314 */     return Ranges.create(newLower, newUpper);
/*     */   }
/*     */ 
/*     */   public Range<C> span(Range<C> other)
/*     */   {
/* 329 */     Cut newLower = (Cut)Ordering.natural().min(this.lowerBound, other.lowerBound);
/* 330 */     Cut newUpper = (Cut)Ordering.natural().max(this.upperBound, other.upperBound);
/* 331 */     return Ranges.create(newLower, newUpper);
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=false)
/*     */   public ContiguousSet<C> asSet(DiscreteDomain<C> domain)
/*     */   {
/* 356 */     Preconditions.checkNotNull(domain);
/* 357 */     Range effectiveRange = this;
/*     */     try {
/* 359 */       if (!hasLowerBound()) {
/* 360 */         effectiveRange = effectiveRange.intersection(Ranges.atLeast(domain.minValue()));
/*     */       }
/* 362 */       if (!hasUpperBound())
/* 363 */         effectiveRange = effectiveRange.intersection(Ranges.atMost(domain.maxValue()));
/*     */     }
/*     */     catch (NoSuchElementException e) {
/* 366 */       throw new IllegalArgumentException(e);
/*     */     }
/*     */ 
/* 370 */     boolean empty = (effectiveRange.isEmpty()) || (compareOrThrow(this.lowerBound.leastValueAbove(domain), this.upperBound.greatestValueBelow(domain)) > 0);
/*     */ 
/* 375 */     return empty ? new EmptyContiguousSet(domain) : new RegularContiguousSet(effectiveRange, domain);
/*     */   }
/*     */ 
/*     */   public Range<C> canonical(DiscreteDomain<C> domain)
/*     */   {
/* 403 */     Preconditions.checkNotNull(domain);
/* 404 */     Cut lower = this.lowerBound.canonical(domain);
/* 405 */     Cut upper = this.upperBound.canonical(domain);
/* 406 */     return (lower == this.lowerBound) && (upper == this.upperBound) ? this : Ranges.create(lower, upper);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 417 */     if ((object instanceof Range)) {
/* 418 */       Range other = (Range)object;
/* 419 */       return (this.lowerBound.equals(other.lowerBound)) && (this.upperBound.equals(other.upperBound));
/*     */     }
/*     */ 
/* 422 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 427 */     return this.lowerBound.hashCode() * 31 + this.upperBound.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 435 */     return toString(this.lowerBound, this.upperBound);
/*     */   }
/*     */ 
/*     */   private static String toString(Cut<?> lowerBound, Cut<?> upperBound) {
/* 439 */     StringBuilder sb = new StringBuilder(16);
/* 440 */     lowerBound.describeAsLowerBound(sb);
/* 441 */     sb.append('‥');
/* 442 */     upperBound.describeAsUpperBound(sb);
/* 443 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   private static <T> SortedSet<T> cast(Iterable<T> iterable)
/*     */   {
/* 450 */     return (SortedSet)iterable;
/*     */   }
/*     */ 
/*     */   static int compareOrThrow(Comparable left, Comparable right)
/*     */   {
/* 455 */     return left.compareTo(right);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Range
 * JD-Core Version:    0.6.2
 */