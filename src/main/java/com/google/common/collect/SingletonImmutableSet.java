/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class SingletonImmutableSet<E> extends ImmutableSet<E>
/*     */ {
/*     */   final transient E element;
/*     */   private transient int cachedHashCode;
/*     */ 
/*     */   SingletonImmutableSet(E element)
/*     */   {
/*  47 */     this.element = Preconditions.checkNotNull(element);
/*     */   }
/*     */ 
/*     */   SingletonImmutableSet(E element, int hashCode)
/*     */   {
/*  52 */     this.element = element;
/*  53 */     this.cachedHashCode = hashCode;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  58 */     return 1;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  62 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean contains(Object target) {
/*  66 */     return this.element.equals(target);
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator() {
/*  70 */     return Iterators.singletonIterator(this.element);
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/*  74 */     return false;
/*     */   }
/*     */ 
/*     */   public Object[] toArray() {
/*  78 */     return new Object[] { this.element };
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] array) {
/*  82 */     if (array.length == 0)
/*  83 */       array = ObjectArrays.newArray(array, 1);
/*  84 */     else if (array.length > 1) {
/*  85 */       array[1] = null;
/*     */     }
/*     */ 
/*  88 */     Object[] objectArray = array;
/*  89 */     objectArray[0] = this.element;
/*  90 */     return array;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/*  94 */     if (object == this) {
/*  95 */       return true;
/*     */     }
/*  97 */     if ((object instanceof Set)) {
/*  98 */       Set that = (Set)object;
/*  99 */       return (that.size() == 1) && (this.element.equals(that.iterator().next()));
/*     */     }
/* 101 */     return false;
/*     */   }
/*     */ 
/*     */   public final int hashCode()
/*     */   {
/* 106 */     int code = this.cachedHashCode;
/* 107 */     if (code == 0) {
/* 108 */       this.cachedHashCode = (code = this.element.hashCode());
/*     */     }
/* 110 */     return code;
/*     */   }
/*     */ 
/*     */   boolean isHashCodeFast() {
/* 114 */     return this.cachedHashCode != 0;
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 118 */     String elementToString = this.element.toString();
/* 119 */     return elementToString.length() + 2 + '[' + elementToString + ']';
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SingletonImmutableSet
 * JD-Core Version:    0.6.2
 */