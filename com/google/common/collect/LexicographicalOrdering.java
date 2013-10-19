/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.Iterator;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class LexicographicalOrdering<T> extends Ordering<Iterable<T>>
/*    */   implements Serializable
/*    */ {
/*    */   final Ordering<? super T> elementOrder;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   LexicographicalOrdering(Ordering<? super T> elementOrder)
/*    */   {
/* 36 */     this.elementOrder = elementOrder;
/*    */   }
/*    */ 
/*    */   public int compare(Iterable<T> leftIterable, Iterable<T> rightIterable)
/*    */   {
/* 41 */     Iterator left = leftIterable.iterator();
/* 42 */     Iterator right = rightIterable.iterator();
/* 43 */     while (left.hasNext()) {
/* 44 */       if (!right.hasNext()) {
/* 45 */         return 1;
/*    */       }
/* 47 */       int result = this.elementOrder.compare(left.next(), right.next());
/* 48 */       if (result != 0) {
/* 49 */         return result;
/*    */       }
/*    */     }
/* 52 */     if (right.hasNext()) {
/* 53 */       return -1;
/*    */     }
/* 55 */     return 0;
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 59 */     if (object == this) {
/* 60 */       return true;
/*    */     }
/* 62 */     if ((object instanceof LexicographicalOrdering)) {
/* 63 */       LexicographicalOrdering that = (LexicographicalOrdering)object;
/* 64 */       return this.elementOrder.equals(that.elementOrder);
/*    */     }
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 70 */     return this.elementOrder.hashCode() ^ 0x7BB78CF5;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 74 */     return this.elementOrder + ".lexicographical()";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.LexicographicalOrdering
 * JD-Core Version:    0.6.2
 */