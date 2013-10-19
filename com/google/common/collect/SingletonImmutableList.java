/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.List;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class SingletonImmutableList<E> extends ImmutableList<E>
/*     */ {
/*     */   final transient E element;
/*     */ 
/*     */   SingletonImmutableList(E element)
/*     */   {
/*  40 */     this.element = Preconditions.checkNotNull(element);
/*     */   }
/*     */ 
/*     */   public E get(int index)
/*     */   {
/*  45 */     Preconditions.checkElementIndex(index, 1);
/*  46 */     return this.element;
/*     */   }
/*     */ 
/*     */   public int indexOf(@Nullable Object object) {
/*  50 */     return this.element.equals(object) ? 0 : -1;
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator() {
/*  54 */     return Iterators.singletonIterator(this.element);
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(@Nullable Object object) {
/*  58 */     return indexOf(object);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  63 */     return 1;
/*     */   }
/*     */ 
/*     */   public ImmutableList<E> subList(int fromIndex, int toIndex) {
/*  67 */     Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
/*  68 */     return fromIndex == toIndex ? ImmutableList.of() : this;
/*     */   }
/*     */ 
/*     */   public ImmutableList<E> reverse() {
/*  72 */     return this;
/*     */   }
/*     */ 
/*     */   public boolean contains(@Nullable Object object) {
/*  76 */     return this.element.equals(object);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object) {
/*  80 */     if (object == this) {
/*  81 */       return true;
/*     */     }
/*  83 */     if ((object instanceof List)) {
/*  84 */       List that = (List)object;
/*  85 */       return (that.size() == 1) && (this.element.equals(that.get(0)));
/*     */     }
/*  87 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  93 */     return 31 + this.element.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  97 */     String elementToString = this.element.toString();
/*  98 */     return elementToString.length() + 2 + '[' + elementToString + ']';
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 106 */     return false;
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/* 110 */     return false;
/*     */   }
/*     */ 
/*     */   public Object[] toArray() {
/* 114 */     return new Object[] { this.element };
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] array) {
/* 118 */     if (array.length == 0)
/* 119 */       array = ObjectArrays.newArray(array, 1);
/* 120 */     else if (array.length > 1) {
/* 121 */       array[1] = null;
/*     */     }
/*     */ 
/* 124 */     Object[] objectArray = array;
/* 125 */     objectArray[0] = this.element;
/* 126 */     return array;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SingletonImmutableList
 * JD-Core Version:    0.6.2
 */