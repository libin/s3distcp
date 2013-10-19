/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.io.Serializable;
/*    */ import java.util.Iterator;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class ReverseOrdering<T> extends Ordering<T>
/*    */   implements Serializable
/*    */ {
/*    */   final Ordering<? super T> forwardOrder;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   ReverseOrdering(Ordering<? super T> forwardOrder)
/*    */   {
/* 34 */     this.forwardOrder = ((Ordering)Preconditions.checkNotNull(forwardOrder));
/*    */   }
/*    */ 
/*    */   public int compare(T a, T b) {
/* 38 */     return this.forwardOrder.compare(b, a);
/*    */   }
/*    */ 
/*    */   public <S extends T> Ordering<S> reverse()
/*    */   {
/* 43 */     return this.forwardOrder;
/*    */   }
/*    */ 
/*    */   public <E extends T> E min(E a, E b)
/*    */   {
/* 49 */     return this.forwardOrder.max(a, b);
/*    */   }
/*    */ 
/*    */   public <E extends T> E min(E a, E b, E c, E[] rest) {
/* 53 */     return this.forwardOrder.max(a, b, c, rest);
/*    */   }
/*    */ 
/*    */   public <E extends T> E min(Iterator<E> iterator) {
/* 57 */     return this.forwardOrder.max(iterator);
/*    */   }
/*    */ 
/*    */   public <E extends T> E min(Iterable<E> iterable) {
/* 61 */     return this.forwardOrder.max(iterable);
/*    */   }
/*    */ 
/*    */   public <E extends T> E max(E a, E b) {
/* 65 */     return this.forwardOrder.min(a, b);
/*    */   }
/*    */ 
/*    */   public <E extends T> E max(E a, E b, E c, E[] rest) {
/* 69 */     return this.forwardOrder.min(a, b, c, rest);
/*    */   }
/*    */ 
/*    */   public <E extends T> E max(Iterator<E> iterator) {
/* 73 */     return this.forwardOrder.min(iterator);
/*    */   }
/*    */ 
/*    */   public <E extends T> E max(Iterable<E> iterable) {
/* 77 */     return this.forwardOrder.min(iterable);
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 81 */     return -this.forwardOrder.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 85 */     if (object == this) {
/* 86 */       return true;
/*    */     }
/* 88 */     if ((object instanceof ReverseOrdering)) {
/* 89 */       ReverseOrdering that = (ReverseOrdering)object;
/* 90 */       return this.forwardOrder.equals(that.forwardOrder);
/*    */     }
/* 92 */     return false;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 96 */     return this.forwardOrder + ".reverse()";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ReverseOrdering
 * JD-Core Version:    0.6.2
 */