/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ 
/*     */ @GwtCompatible
/*     */ final class SortedMultisets
/*     */ {
/*     */   private static <E> E getElementOrThrow(Multiset.Entry<E> entry)
/*     */   {
/*  74 */     if (entry == null) {
/*  75 */       throw new NoSuchElementException();
/*     */     }
/*  77 */     return entry.getElement();
/*     */   }
/*     */ 
/*     */   static abstract class DescendingMultiset<E> extends ForwardingMultiset<E> implements SortedMultiset<E>
/*     */   {
/*     */     private transient Comparator<? super E> comparator;
/*     */     private transient SortedSet<E> elementSet;
/*     */     private transient Set<Multiset.Entry<E>> entrySet;
/*     */ 
/*     */     abstract SortedMultiset<E> forwardMultiset();
/*     */ 
/*     */     public Comparator<? super E> comparator() {
/*  91 */       Comparator result = this.comparator;
/*  92 */       if (result == null) {
/*  93 */         return this.comparator = Ordering.from(forwardMultiset().comparator()).reverse();
/*     */       }
/*     */ 
/*  96 */       return result;
/*     */     }
/*     */ 
/*     */     public SortedSet<E> elementSet()
/*     */     {
/* 102 */       SortedSet result = this.elementSet;
/* 103 */       if (result == null) {
/* 104 */         return this.elementSet = new SortedMultisets.ElementSet() {
/*     */           SortedMultiset<E> multiset() {
/* 106 */             return SortedMultisets.DescendingMultiset.this;
/*     */           }
/*     */         };
/*     */       }
/* 110 */       return result;
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> pollFirstEntry() {
/* 114 */       return forwardMultiset().pollLastEntry();
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> pollLastEntry() {
/* 118 */       return forwardMultiset().pollFirstEntry();
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> headMultiset(E toElement, BoundType boundType)
/*     */     {
/* 123 */       return forwardMultiset().tailMultiset(toElement, boundType).descendingMultiset();
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> subMultiset(E fromElement, BoundType fromBoundType, E toElement, BoundType toBoundType)
/*     */     {
/* 129 */       return forwardMultiset().subMultiset(toElement, toBoundType, fromElement, fromBoundType).descendingMultiset();
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> tailMultiset(E fromElement, BoundType boundType)
/*     */     {
/* 135 */       return forwardMultiset().headMultiset(fromElement, boundType).descendingMultiset();
/*     */     }
/*     */ 
/*     */     protected Multiset<E> delegate()
/*     */     {
/* 140 */       return forwardMultiset();
/*     */     }
/*     */ 
/*     */     public SortedMultiset<E> descendingMultiset() {
/* 144 */       return forwardMultiset();
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> firstEntry() {
/* 148 */       return forwardMultiset().lastEntry();
/*     */     }
/*     */ 
/*     */     public Multiset.Entry<E> lastEntry() {
/* 152 */       return forwardMultiset().firstEntry();
/*     */     }
/*     */ 
/*     */     abstract Iterator<Multiset.Entry<E>> entryIterator();
/*     */ 
/*     */     public Set<Multiset.Entry<E>> entrySet()
/*     */     {
/* 160 */       Set result = this.entrySet;
/* 161 */       return result == null ? (this.entrySet = createEntrySet()) : result;
/*     */     }
/*     */ 
/*     */     Set<Multiset.Entry<E>> createEntrySet() {
/* 165 */       return new Multisets.EntrySet() {
/*     */         Multiset<E> multiset() {
/* 167 */           return SortedMultisets.DescendingMultiset.this;
/*     */         }
/*     */ 
/*     */         public Iterator<Multiset.Entry<E>> iterator() {
/* 171 */           return SortedMultisets.DescendingMultiset.this.entryIterator();
/*     */         }
/*     */ 
/*     */         public int size() {
/* 175 */           return SortedMultisets.DescendingMultiset.this.forwardMultiset().entrySet().size();
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     public Iterator<E> iterator() {
/* 181 */       return Multisets.iteratorImpl(this);
/*     */     }
/*     */ 
/*     */     public Object[] toArray() {
/* 185 */       return standardToArray();
/*     */     }
/*     */ 
/*     */     public <T> T[] toArray(T[] array) {
/* 189 */       return standardToArray(array);
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 193 */       return entrySet().toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class ElementSet<E> extends Multisets.ElementSet<E>
/*     */     implements SortedSet<E>
/*     */   {
/*     */     abstract SortedMultiset<E> multiset();
/*     */ 
/*     */     public Comparator<? super E> comparator()
/*     */     {
/*  47 */       return multiset().comparator();
/*     */     }
/*     */ 
/*     */     public SortedSet<E> subSet(E fromElement, E toElement) {
/*  51 */       return multiset().subMultiset(fromElement, BoundType.CLOSED, toElement, BoundType.OPEN).elementSet();
/*     */     }
/*     */ 
/*     */     public SortedSet<E> headSet(E toElement)
/*     */     {
/*  56 */       return multiset().headMultiset(toElement, BoundType.OPEN).elementSet();
/*     */     }
/*     */ 
/*     */     public SortedSet<E> tailSet(E fromElement) {
/*  60 */       return multiset().tailMultiset(fromElement, BoundType.CLOSED).elementSet();
/*     */     }
/*     */ 
/*     */     public E first()
/*     */     {
/*  65 */       return SortedMultisets.getElementOrThrow(multiset().firstEntry());
/*     */     }
/*     */ 
/*     */     public E last() {
/*  69 */       return SortedMultisets.getElementOrThrow(multiset().lastEntry());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SortedMultisets
 * JD-Core Version:    0.6.2
 */