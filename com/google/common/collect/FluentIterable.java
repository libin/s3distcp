/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Optional;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicate;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class FluentIterable<E>
/*     */   implements Iterable<E>
/*     */ {
/*     */   private final Iterable<E> iterable;
/*     */ 
/*     */   protected FluentIterable()
/*     */   {
/*  76 */     this.iterable = this;
/*     */   }
/*     */ 
/*     */   FluentIterable(Iterable<E> iterable) {
/*  80 */     this.iterable = ((Iterable)Preconditions.checkNotNull(iterable));
/*     */   }
/*     */ 
/*     */   public static <E> FluentIterable<E> from(final Iterable<E> iterable)
/*     */   {
/*  88 */     return (iterable instanceof FluentIterable) ? (FluentIterable)iterable : new FluentIterable(iterable)
/*     */     {
/*     */       public Iterator<E> iterator()
/*     */       {
/*  92 */         return iterable.iterator();
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static <E> FluentIterable<E> from(FluentIterable<E> iterable)
/*     */   {
/* 107 */     return (FluentIterable)Preconditions.checkNotNull(iterable);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 116 */     return Iterables.toString(this.iterable);
/*     */   }
/*     */ 
/*     */   public final int size()
/*     */   {
/* 123 */     return Iterables.size(this.iterable);
/*     */   }
/*     */ 
/*     */   public final boolean contains(@Nullable Object element)
/*     */   {
/* 131 */     return Iterables.contains(this.iterable, element);
/*     */   }
/*     */ 
/*     */   public final FluentIterable<E> cycle()
/*     */   {
/* 148 */     return from(Iterables.cycle(this.iterable));
/*     */   }
/*     */ 
/*     */   public final FluentIterable<E> filter(Predicate<? super E> predicate)
/*     */   {
/* 156 */     return from(Iterables.filter(this.iterable, predicate));
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Class.isInstance")
/*     */   public final <T> FluentIterable<T> filter(Class<T> type)
/*     */   {
/* 166 */     return from(Iterables.filter(this.iterable, type));
/*     */   }
/*     */ 
/*     */   public final boolean anyMatch(Predicate<? super E> predicate)
/*     */   {
/* 173 */     return Iterables.any(this.iterable, predicate);
/*     */   }
/*     */ 
/*     */   public final boolean allMatch(Predicate<? super E> predicate)
/*     */   {
/* 181 */     return Iterables.all(this.iterable, predicate);
/*     */   }
/*     */ 
/*     */   public final Optional<E> firstMatch(Predicate<? super E> predicate)
/*     */   {
/* 192 */     return Iterables.tryFind(this.iterable, predicate);
/*     */   }
/*     */ 
/*     */   public final <T> FluentIterable<T> transform(Function<? super E, T> function)
/*     */   {
/* 204 */     return from(Iterables.transform(this.iterable, function));
/*     */   }
/*     */ 
/*     */   public final Optional<E> first()
/*     */   {
/* 215 */     Iterator iterator = this.iterable.iterator();
/* 216 */     return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.absent();
/*     */   }
/*     */ 
/*     */   public final Optional<E> last()
/*     */   {
/* 232 */     if ((this.iterable instanceof List)) {
/* 233 */       List list = (List)this.iterable;
/* 234 */       if (list.isEmpty()) {
/* 235 */         return Optional.absent();
/*     */       }
/* 237 */       return Optional.of(list.get(list.size() - 1));
/*     */     }
/* 239 */     Iterator iterator = this.iterable.iterator();
/* 240 */     if (!iterator.hasNext()) {
/* 241 */       return Optional.absent();
/*     */     }
/*     */ 
/* 249 */     if ((this.iterable instanceof SortedSet)) {
/* 250 */       SortedSet sortedSet = (SortedSet)this.iterable;
/* 251 */       return Optional.of(sortedSet.last());
/*     */     }
/*     */     while (true)
/*     */     {
/* 255 */       Object current = iterator.next();
/* 256 */       if (!iterator.hasNext())
/* 257 */         return Optional.of(current);
/*     */     }
/*     */   }
/*     */ 
/*     */   public final FluentIterable<E> skip(int numberToSkip)
/*     */   {
/* 280 */     return from(Iterables.skip(this.iterable, numberToSkip));
/*     */   }
/*     */ 
/*     */   public final FluentIterable<E> limit(int size)
/*     */   {
/* 294 */     return from(Iterables.limit(this.iterable, size));
/*     */   }
/*     */ 
/*     */   public final boolean isEmpty()
/*     */   {
/* 301 */     return !this.iterable.iterator().hasNext();
/*     */   }
/*     */ 
/*     */   public final ImmutableList<E> toImmutableList()
/*     */   {
/* 309 */     return ImmutableList.copyOf(this.iterable);
/*     */   }
/*     */ 
/*     */   public final ImmutableSet<E> toImmutableSet()
/*     */   {
/* 317 */     return ImmutableSet.copyOf(this.iterable);
/*     */   }
/*     */ 
/*     */   public final ImmutableSortedSet<E> toImmutableSortedSet(Comparator<? super E> comparator)
/*     */   {
/* 331 */     return ImmutableSortedSet.copyOf(comparator, this.iterable);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Array.newArray(Class, int)")
/*     */   public final E[] toArray(Class<E> type)
/*     */   {
/* 343 */     return Iterables.toArray(this.iterable, type);
/*     */   }
/*     */ 
/*     */   public final E get(int position)
/*     */   {
/* 355 */     return Iterables.get(this.iterable, position);
/*     */   }
/*     */ 
/*     */   private static class FromIterableFunction<E>
/*     */     implements Function<Iterable<E>, FluentIterable<E>>
/*     */   {
/*     */     public FluentIterable<E> apply(Iterable<E> fromObject)
/*     */     {
/* 365 */       return FluentIterable.from(fromObject);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.FluentIterable
 * JD-Core Version:    0.6.2
 */