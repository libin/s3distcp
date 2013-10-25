/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ final class RegularContiguousSet<C extends Comparable> extends ContiguousSet<C>
/*     */ {
/*     */   private final Range<C> range;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   RegularContiguousSet(Range<C> range, DiscreteDomain<C> domain)
/*     */   {
/*  40 */     super(domain);
/*  41 */     this.range = range;
/*     */   }
/*     */ 
/*     */   private ContiguousSet<C> intersectionInCurrentDomain(Range<C> other) {
/*  45 */     return this.range.isConnected(other) ? this.range.intersection(other).asSet(this.domain) : new EmptyContiguousSet(this.domain);
/*     */   }
/*     */ 
/*     */   ContiguousSet<C> headSetImpl(C toElement, boolean inclusive)
/*     */   {
/*  51 */     return intersectionInCurrentDomain(Ranges.upTo(toElement, BoundType.forBoolean(inclusive)));
/*     */   }
/*     */ 
/*     */   ContiguousSet<C> subSetImpl(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive)
/*     */   {
/*  56 */     if ((fromElement.compareTo(toElement) == 0) && (!fromInclusive) && (!toInclusive))
/*     */     {
/*  58 */       return new EmptyContiguousSet(this.domain);
/*     */     }
/*  60 */     return intersectionInCurrentDomain(Ranges.range(fromElement, BoundType.forBoolean(fromInclusive), toElement, BoundType.forBoolean(toInclusive)));
/*     */   }
/*     */ 
/*     */   ContiguousSet<C> tailSetImpl(C fromElement, boolean inclusive)
/*     */   {
/*  66 */     return intersectionInCurrentDomain(Ranges.downTo(fromElement, BoundType.forBoolean(inclusive)));
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("not used by GWT emulation")
/*     */   int indexOf(Object target) {
/*  71 */     return contains(target) ? (int)this.domain.distance(first(), (Comparable)target) : -1;
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<C> iterator() {
/*  75 */     return new AbstractSequentialIterator(first()) {
/*  76 */       final C last = RegularContiguousSet.this.last();
/*     */ 
/*     */       protected C computeNext(C previous)
/*     */       {
/*  80 */         return RegularContiguousSet.equalsOrThrow(previous, this.last) ? null : RegularContiguousSet.this.domain.next(previous);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static boolean equalsOrThrow(Comparable<?> left, @Nullable Comparable<?> right) {
/*  86 */     return (right != null) && (Range.compareOrThrow(left, right) == 0);
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */   public C first() {
/*  94 */     return this.range.lowerBound.leastValueAbove(this.domain);
/*     */   }
/*     */ 
/*     */   public C last() {
/*  98 */     return this.range.upperBound.greatestValueBelow(this.domain);
/*     */   }
/*     */ 
/*     */   public int size() {
/* 102 */     long distance = this.domain.distance(first(), last());
/* 103 */     return distance >= 2147483647L ? 2147483647 : (int)distance + 1;
/*     */   }
/*     */ 
/*     */   public boolean contains(Object object) {
/* 107 */     if (object == null)
/* 108 */       return false;
/*     */     try
/*     */     {
/* 111 */       return this.range.contains((Comparable)object); } catch (ClassCastException e) {
/*     */     }
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> targets)
/*     */   {
/* 118 */     return Collections2.containsAllImpl(this, targets);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 127 */     return ObjectArrays.toArrayImpl(this);
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] other)
/*     */   {
/* 132 */     return ObjectArrays.toArrayImpl(this, other);
/*     */   }
/*     */ 
/*     */   public ContiguousSet<C> intersection(ContiguousSet<C> other) {
/* 136 */     Preconditions.checkNotNull(other);
/* 137 */     Preconditions.checkArgument(this.domain.equals(other.domain));
/* 138 */     if (other.isEmpty()) {
/* 139 */       return other;
/*     */     }
/* 141 */     Comparable lowerEndpoint = (Comparable)Ordering.natural().max(first(), other.first());
/* 142 */     Comparable upperEndpoint = (Comparable)Ordering.natural().min(last(), other.last());
/* 143 */     return lowerEndpoint.compareTo(upperEndpoint) < 0 ? Ranges.closed(lowerEndpoint, upperEndpoint).asSet(this.domain) : new EmptyContiguousSet(this.domain);
/*     */   }
/*     */ 
/*     */   public Range<C> range()
/*     */   {
/* 150 */     return range(BoundType.CLOSED, BoundType.CLOSED);
/*     */   }
/*     */ 
/*     */   public Range<C> range(BoundType lowerBoundType, BoundType upperBoundType) {
/* 154 */     return Ranges.create(this.range.lowerBound.withLowerBoundType(lowerBoundType, this.domain), this.range.upperBound.withUpperBoundType(upperBoundType, this.domain));
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object)
/*     */   {
/* 159 */     if (object == this)
/* 160 */       return true;
/* 161 */     if ((object instanceof RegularContiguousSet)) {
/* 162 */       RegularContiguousSet that = (RegularContiguousSet)object;
/* 163 */       if (this.domain.equals(that.domain)) {
/* 164 */         return (first().equals(that.first())) && (last().equals(that.last()));
/*     */       }
/*     */     }
/*     */ 
/* 168 */     return super.equals(object);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 173 */     return Sets.hashCodeImpl(this);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("serialization")
/*     */   Object writeReplace()
/*     */   {
/* 193 */     return new SerializedForm(this.range, this.domain, null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   ImmutableSortedSet<C> createDescendingSet()
/*     */   {
/* 201 */     return new DescendingContiguousSet(null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("NavigableSet")
/*     */   private final class DescendingContiguousSet extends ImmutableSortedSet<C>
/*     */   {
/*     */     private DescendingContiguousSet() {
/* 208 */       super();
/*     */     }
/*     */ 
/*     */     public C first()
/*     */     {
/* 213 */       return RegularContiguousSet.this.last();
/*     */     }
/*     */ 
/*     */     public C last()
/*     */     {
/* 218 */       return RegularContiguousSet.this.first();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 223 */       return RegularContiguousSet.this.size();
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<C> iterator()
/*     */     {
/* 228 */       return new AbstractSequentialIterator(first()) {
/* 229 */         final C last = RegularContiguousSet.DescendingContiguousSet.this.last();
/*     */ 
/*     */         protected C computeNext(C previous)
/*     */         {
/* 233 */           return RegularContiguousSet.equalsOrThrow(previous, this.last) ? null : RegularContiguousSet.this.domain.previous(previous);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     ImmutableSortedSet<C> headSetImpl(C toElement, boolean inclusive)
/*     */     {
/* 240 */       return RegularContiguousSet.this.tailSetImpl(toElement, inclusive).descendingSet();
/*     */     }
/*     */ 
/*     */     ImmutableSortedSet<C> subSetImpl(C fromElement, boolean fromInclusive, C toElement, boolean toInclusive)
/*     */     {
/* 249 */       return RegularContiguousSet.this.subSetImpl(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
/*     */     }
/*     */ 
/*     */     ImmutableSortedSet<C> tailSetImpl(C fromElement, boolean inclusive)
/*     */     {
/* 258 */       return RegularContiguousSet.this.headSetImpl(fromElement, inclusive).descendingSet();
/*     */     }
/*     */ 
/*     */     ImmutableSortedSet<C> createDescendingSet()
/*     */     {
/* 263 */       return RegularContiguousSet.this;
/*     */     }
/*     */ 
/*     */     int indexOf(Object target)
/*     */     {
/* 268 */       return contains(target) ? (int)RegularContiguousSet.this.domain.distance(last(), (Comparable)target) : -1;
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 273 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("serialization")
/*     */   private static final class SerializedForm<C extends Comparable>
/*     */     implements Serializable
/*     */   {
/*     */     final Range<C> range;
/*     */     final DiscreteDomain<C> domain;
/*     */ 
/*     */     private SerializedForm(Range<C> range, DiscreteDomain<C> domain)
/*     */     {
/* 182 */       this.range = range;
/* 183 */       this.domain = domain;
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/* 187 */       return new RegularContiguousSet(this.range, this.domain);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RegularContiguousSet
 * JD-Core Version:    0.6.2
 */