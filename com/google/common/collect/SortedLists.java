/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.RandomAccess;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ @Beta
/*     */ final class SortedLists
/*     */ {
/*     */   public static <E extends Comparable> int binarySearch(List<? extends E> list, E e, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior)
/*     */   {
/* 191 */     Preconditions.checkNotNull(e);
/* 192 */     return binarySearch(list, Preconditions.checkNotNull(e), Ordering.natural(), presentBehavior, absentBehavior);
/*     */   }
/*     */ 
/*     */   public static <E, K extends Comparable> int binarySearch(List<E> list, Function<? super E, K> keyFunction, @Nullable K key, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior)
/*     */   {
/* 205 */     return binarySearch(list, keyFunction, key, Ordering.natural(), presentBehavior, absentBehavior);
/*     */   }
/*     */ 
/*     */   public static <E, K> int binarySearch(List<E> list, Function<? super E, K> keyFunction, @Nullable K key, Comparator<? super K> keyComparator, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior)
/*     */   {
/* 228 */     return binarySearch(Lists.transform(list, keyFunction), key, keyComparator, presentBehavior, absentBehavior);
/*     */   }
/*     */ 
/*     */   public static <E> int binarySearch(List<? extends E> list, @Nullable E key, Comparator<? super E> comparator, KeyPresentBehavior presentBehavior, KeyAbsentBehavior absentBehavior)
/*     */   {
/* 258 */     Preconditions.checkNotNull(comparator);
/* 259 */     Preconditions.checkNotNull(list);
/* 260 */     Preconditions.checkNotNull(presentBehavior);
/* 261 */     Preconditions.checkNotNull(absentBehavior);
/* 262 */     if (!(list instanceof RandomAccess)) {
/* 263 */       list = Lists.newArrayList(list);
/*     */     }
/*     */ 
/* 267 */     int lower = 0;
/* 268 */     int upper = list.size() - 1;
/*     */ 
/* 270 */     while (lower <= upper) {
/* 271 */       int middle = lower + upper >>> 1;
/* 272 */       int c = comparator.compare(key, list.get(middle));
/* 273 */       if (c < 0)
/* 274 */         upper = middle - 1;
/* 275 */       else if (c > 0)
/* 276 */         lower = middle + 1;
/*     */       else {
/* 278 */         return lower + presentBehavior.resultIndex(comparator, key, list.subList(lower, upper + 1), middle - lower);
/*     */       }
/*     */     }
/*     */ 
/* 282 */     return absentBehavior.resultIndex(lower);
/*     */   }
/*     */ 
/*     */   public static abstract enum KeyAbsentBehavior
/*     */   {
/* 144 */     NEXT_LOWER, 
/*     */ 
/* 154 */     NEXT_HIGHER, 
/*     */ 
/* 172 */     INVERTED_INSERTION_INDEX;
/*     */ 
/*     */     abstract int resultIndex(int paramInt);
/*     */   }
/*     */ 
/*     */   public static abstract enum KeyPresentBehavior
/*     */   {
/*  53 */     ANY_PRESENT, 
/*     */ 
/*  63 */     LAST_PRESENT, 
/*     */ 
/*  87 */     FIRST_PRESENT, 
/*     */ 
/* 113 */     FIRST_AFTER, 
/*     */ 
/* 124 */     LAST_BEFORE;
/*     */ 
/*     */     abstract <E> int resultIndex(Comparator<? super E> paramComparator, E paramE, List<? extends E> paramList, int paramInt);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SortedLists
 * JD-Core Version:    0.6.2
 */