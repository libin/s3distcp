/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ 
/*     */ @GwtCompatible
/*     */ final class SortedIterables
/*     */ {
/*     */   public static boolean hasSameComparator(Comparator<?> comparator, Iterable<?> elements)
/*     */   {
/*  46 */     Preconditions.checkNotNull(comparator);
/*  47 */     Preconditions.checkNotNull(elements);
/*     */     Comparator comparator2;
/*  49 */     if ((elements instanceof SortedSet)) {
/*  50 */       SortedSet sortedSet = (SortedSet)elements;
/*  51 */       Comparator comparator2 = sortedSet.comparator();
/*  52 */       if (comparator2 == null)
/*  53 */         comparator2 = Ordering.natural();
/*     */     }
/*     */     else
/*     */     {
/*     */       Comparator comparator2;
/*  55 */       if ((elements instanceof SortedIterable))
/*  56 */         comparator2 = ((SortedIterable)elements).comparator();
/*     */       else
/*  58 */         comparator2 = null;
/*     */     }
/*  60 */     return comparator.equals(comparator2);
/*     */   }
/*     */ 
/*     */   public static <E> Collection<E> sortedUnique(Comparator<? super E> comparator, Iterator<E> elements)
/*     */   {
/*  70 */     SortedSet sortedSet = Sets.newTreeSet(comparator);
/*  71 */     Iterators.addAll(sortedSet, elements);
/*  72 */     return sortedSet;
/*     */   }
/*     */ 
/*     */   public static <E> Collection<E> sortedUnique(Comparator<? super E> comparator, Iterable<E> elements)
/*     */   {
/*  82 */     if ((elements instanceof Multiset)) {
/*  83 */       elements = ((Multiset)elements).elementSet();
/*     */     }
/*  85 */     if ((elements instanceof Set)) {
/*  86 */       if (hasSameComparator(comparator, elements)) {
/*  87 */         return (Set)elements;
/*     */       }
/*  89 */       List list = Lists.newArrayList(elements);
/*  90 */       Collections.sort(list, comparator);
/*  91 */       return list;
/*     */     }
/*  93 */     Object[] array = (Object[])Iterables.toArray(elements);
/*  94 */     if (!hasSameComparator(comparator, elements)) {
/*  95 */       Arrays.sort(array, comparator);
/*     */     }
/*  97 */     return uniquifySortedArray(comparator, array);
/*     */   }
/*     */ 
/*     */   private static <E> Collection<E> uniquifySortedArray(Comparator<? super E> comparator, E[] array)
/*     */   {
/* 102 */     if (array.length == 0) {
/* 103 */       return Collections.emptySet();
/*     */     }
/* 105 */     int length = 1;
/* 106 */     for (int i = 1; i < array.length; i++) {
/* 107 */       int cmp = comparator.compare(array[i], array[(length - 1)]);
/* 108 */       if (cmp != 0) {
/* 109 */         array[(length++)] = array[i];
/*     */       }
/*     */     }
/* 112 */     if (length < array.length) {
/* 113 */       array = ObjectArrays.arraysCopyOf(array, length);
/*     */     }
/* 115 */     return Arrays.asList(array);
/*     */   }
/*     */ 
/*     */   public static <E> Collection<Multiset.Entry<E>> sortedCounts(Comparator<? super E> comparator, Iterator<E> elements)
/*     */   {
/* 124 */     TreeMultiset multiset = TreeMultiset.create(comparator);
/* 125 */     Iterators.addAll(multiset, elements);
/* 126 */     return multiset.entrySet();
/*     */   }
/*     */ 
/*     */   public static <E> Collection<Multiset.Entry<E>> sortedCounts(Comparator<? super E> comparator, Iterable<E> elements)
/*     */   {
/* 135 */     if ((elements instanceof Multiset)) {
/* 136 */       Multiset multiset = (Multiset)elements;
/* 137 */       if (hasSameComparator(comparator, elements)) {
/* 138 */         return multiset.entrySet();
/*     */       }
/* 140 */       List entries = Lists.newArrayList(multiset.entrySet());
/* 141 */       Collections.sort(entries, Ordering.from(comparator).onResultOf(new Function()
/*     */       {
/*     */         public E apply(Multiset.Entry<E> entry)
/*     */         {
/* 145 */           return entry.getElement();
/*     */         }
/*     */       }));
/* 148 */       return entries;
/* 149 */     }if ((elements instanceof Set))
/*     */     {
/*     */       Collection sortedElements;
/*     */       Collection sortedElements;
/* 151 */       if (hasSameComparator(comparator, elements)) {
/* 152 */         sortedElements = (Collection)elements;
/*     */       } else {
/* 154 */         List list = Lists.newArrayList(elements);
/* 155 */         Collections.sort(list, comparator);
/* 156 */         sortedElements = list;
/*     */       }
/* 158 */       return singletonEntries(sortedElements);
/* 159 */     }if (hasSameComparator(comparator, elements)) {
/* 160 */       Object current = null;
/* 161 */       int currentCount = 0;
/* 162 */       List sortedEntries = Lists.newArrayList();
/* 163 */       for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object e = i$.next();
/* 164 */         if (currentCount > 0) {
/* 165 */           if (comparator.compare(current, e) == 0) {
/* 166 */             currentCount++;
/*     */           } else {
/* 168 */             sortedEntries.add(Multisets.immutableEntry(current, currentCount));
/* 169 */             current = e;
/* 170 */             currentCount = 1;
/*     */           }
/*     */         } else {
/* 173 */           current = e;
/* 174 */           currentCount = 1;
/*     */         }
/*     */       }
/* 177 */       if (currentCount > 0) {
/* 178 */         sortedEntries.add(Multisets.immutableEntry(current, currentCount));
/*     */       }
/* 180 */       return sortedEntries;
/*     */     }
/* 182 */     TreeMultiset multiset = TreeMultiset.create(comparator);
/* 183 */     Iterables.addAll(multiset, elements);
/* 184 */     return multiset.entrySet();
/*     */   }
/*     */ 
/*     */   static <E> Collection<Multiset.Entry<E>> singletonEntries(Collection<E> set) {
/* 188 */     return Collections2.transform(set, new Function()
/*     */     {
/*     */       public Multiset.Entry<E> apply(E elem) {
/* 191 */         return Multisets.immutableEntry(elem, 1);
/*     */       }
/*     */     });
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SortedIterables
 * JD-Core Version:    0.6.2
 */