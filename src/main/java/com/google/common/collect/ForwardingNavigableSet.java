/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.util.Iterator;
/*     */ import java.util.NavigableSet;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public abstract class ForwardingNavigableSet<E> extends ForwardingSortedSet<E>
/*     */   implements NavigableSet<E>
/*     */ {
/*     */   protected abstract NavigableSet<E> delegate();
/*     */ 
/*     */   public E lower(E e)
/*     */   {
/*  61 */     return delegate().lower(e);
/*     */   }
/*     */ 
/*     */   protected E standardLower(E e)
/*     */   {
/*  70 */     return Iterators.getNext(headSet(e, false).descendingIterator(), null);
/*     */   }
/*     */ 
/*     */   public E floor(E e)
/*     */   {
/*  75 */     return delegate().floor(e);
/*     */   }
/*     */ 
/*     */   protected E standardFloor(E e)
/*     */   {
/*  84 */     return Iterators.getNext(headSet(e, true).descendingIterator(), null);
/*     */   }
/*     */ 
/*     */   public E ceiling(E e)
/*     */   {
/*  89 */     return delegate().ceiling(e);
/*     */   }
/*     */ 
/*     */   protected E standardCeiling(E e)
/*     */   {
/*  98 */     return Iterators.getNext(tailSet(e, true).iterator(), null);
/*     */   }
/*     */ 
/*     */   public E higher(E e)
/*     */   {
/* 103 */     return delegate().higher(e);
/*     */   }
/*     */ 
/*     */   protected E standardHigher(E e)
/*     */   {
/* 112 */     return Iterators.getNext(tailSet(e, false).iterator(), null);
/*     */   }
/*     */ 
/*     */   public E pollFirst()
/*     */   {
/* 117 */     return delegate().pollFirst();
/*     */   }
/*     */ 
/*     */   protected E standardPollFirst()
/*     */   {
/* 126 */     return poll(iterator());
/*     */   }
/*     */ 
/*     */   public E pollLast()
/*     */   {
/* 131 */     return delegate().pollLast();
/*     */   }
/*     */ 
/*     */   protected E standardPollLast()
/*     */   {
/* 140 */     return poll(delegate().descendingIterator());
/*     */   }
/*     */ 
/*     */   protected E standardFirst() {
/* 144 */     return iterator().next();
/*     */   }
/*     */ 
/*     */   protected E standardLast() {
/* 148 */     return descendingIterator().next();
/*     */   }
/*     */ 
/*     */   public NavigableSet<E> descendingSet()
/*     */   {
/* 153 */     return delegate().descendingSet();
/*     */   }
/*     */ 
/*     */   public Iterator<E> descendingIterator()
/*     */   {
/* 175 */     return delegate().descendingIterator();
/*     */   }
/*     */ 
/*     */   public NavigableSet<E> subSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/* 184 */     return delegate().subSet(fromElement, fromInclusive, toElement, toInclusive);
/*     */   }
/*     */ 
/*     */   protected NavigableSet<E> standardSubSet(E fromElement, boolean fromInclusive, E toElement, boolean toInclusive)
/*     */   {
/* 197 */     return tailSet(fromElement, fromInclusive).headSet(toElement, toInclusive);
/*     */   }
/*     */ 
/*     */   protected SortedSet<E> standardSubSet(E fromElement, E toElement)
/*     */   {
/* 208 */     return subSet(fromElement, true, toElement, false);
/*     */   }
/*     */ 
/*     */   public NavigableSet<E> headSet(E toElement, boolean inclusive)
/*     */   {
/* 213 */     return delegate().headSet(toElement, inclusive);
/*     */   }
/*     */ 
/*     */   protected SortedSet<E> standardHeadSet(E toElement)
/*     */   {
/* 223 */     return headSet(toElement, false);
/*     */   }
/*     */ 
/*     */   public NavigableSet<E> tailSet(E fromElement, boolean inclusive)
/*     */   {
/* 228 */     return delegate().tailSet(fromElement, inclusive);
/*     */   }
/*     */ 
/*     */   protected SortedSet<E> standardTailSet(E fromElement)
/*     */   {
/* 238 */     return tailSet(fromElement, true);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private E poll(Iterator<E> iterator) {
/* 243 */     if (iterator.hasNext()) {
/* 244 */       Object result = iterator.next();
/* 245 */       iterator.remove();
/* 246 */       return result;
/*     */     }
/* 248 */     return null;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected class StandardDescendingSet extends Sets.DescendingSet<E>
/*     */   {
/*     */     public StandardDescendingSet()
/*     */     {
/* 169 */       super();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingNavigableSet
 * JD-Core Version:    0.6.2
 */