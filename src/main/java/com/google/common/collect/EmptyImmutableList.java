/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.NoSuchElementException;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class EmptyImmutableList extends ImmutableList<Object>
/*     */ {
/*  38 */   static final EmptyImmutableList INSTANCE = new EmptyImmutableList();
/*  39 */   static final UnmodifiableListIterator<Object> ITERATOR = new UnmodifiableListIterator()
/*     */   {
/*     */     public boolean hasNext()
/*     */     {
/*  43 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean hasPrevious() {
/*  47 */       return false;
/*     */     }
/*     */ 
/*     */     public Object next() {
/*  51 */       throw new NoSuchElementException();
/*     */     }
/*     */ 
/*     */     public int nextIndex() {
/*  55 */       return 0;
/*     */     }
/*     */ 
/*     */     public Object previous() {
/*  59 */       throw new NoSuchElementException();
/*     */     }
/*     */ 
/*     */     public int previousIndex() {
/*  63 */       return -1;
/*     */     } } ;
/*     */ 
/*  90 */   private static final Object[] EMPTY_ARRAY = new Object[0];
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*  71 */   public int size() { return 0; }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  75 */     return true;
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/*  79 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean contains(Object target) {
/*  83 */     return false;
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<Object> iterator() {
/*  87 */     return Iterators.emptyIterator();
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/*  93 */     return EMPTY_ARRAY;
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] a) {
/*  97 */     if (a.length > 0) {
/*  98 */       a[0] = null;
/*     */     }
/* 100 */     return a;
/*     */   }
/*     */ 
/*     */   public Object get(int index)
/*     */   {
/* 106 */     Preconditions.checkElementIndex(index, 0);
/* 107 */     throw new AssertionError("unreachable");
/*     */   }
/*     */ 
/*     */   public int indexOf(@Nullable Object target) {
/* 111 */     return -1;
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(@Nullable Object target) {
/* 115 */     return -1;
/*     */   }
/*     */ 
/*     */   public ImmutableList<Object> subList(int fromIndex, int toIndex) {
/* 119 */     Preconditions.checkPositionIndexes(fromIndex, toIndex, 0);
/* 120 */     return this;
/*     */   }
/*     */ 
/*     */   public ImmutableList<Object> reverse() {
/* 124 */     return this;
/*     */   }
/*     */ 
/*     */   public UnmodifiableListIterator<Object> listIterator() {
/* 128 */     return ITERATOR;
/*     */   }
/*     */ 
/*     */   public UnmodifiableListIterator<Object> listIterator(int start) {
/* 132 */     Preconditions.checkPositionIndex(start, 0);
/* 133 */     return ITERATOR;
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> targets) {
/* 137 */     return targets.isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 141 */     if ((object instanceof List)) {
/* 142 */       List that = (List)object;
/* 143 */       return that.isEmpty();
/*     */     }
/* 145 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 149 */     return 1;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 153 */     return "[]";
/*     */   }
/*     */ 
/*     */   Object readResolve() {
/* 157 */     return INSTANCE;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableList
 * JD-Core Version:    0.6.2
 */