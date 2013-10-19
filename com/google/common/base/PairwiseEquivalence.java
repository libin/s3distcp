/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import java.util.Iterator;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class PairwiseEquivalence<T> extends Equivalence<Iterable<T>>
/*    */   implements Serializable
/*    */ {
/*    */   final Equivalence<? super T> elementEquivalence;
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   PairwiseEquivalence(Equivalence<? super T> elementEquivalence)
/*    */   {
/* 33 */     this.elementEquivalence = ((Equivalence)Preconditions.checkNotNull(elementEquivalence));
/*    */   }
/*    */ 
/*    */   protected boolean doEquivalent(Iterable<T> iterableA, Iterable<T> iterableB)
/*    */   {
/* 38 */     Iterator iteratorA = iterableA.iterator();
/* 39 */     Iterator iteratorB = iterableB.iterator();
/*    */ 
/* 41 */     while ((iteratorA.hasNext()) && (iteratorB.hasNext())) {
/* 42 */       if (!this.elementEquivalence.equivalent(iteratorA.next(), iteratorB.next())) {
/* 43 */         return false;
/*    */       }
/*    */     }
/*    */ 
/* 47 */     return (!iteratorA.hasNext()) && (!iteratorB.hasNext());
/*    */   }
/*    */ 
/*    */   protected int doHash(Iterable<T> iterable)
/*    */   {
/* 52 */     int hash = 78721;
/* 53 */     for (Iterator i$ = iterable.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 54 */       hash = hash * 24943 + this.elementEquivalence.hash(element);
/*    */     }
/* 56 */     return hash;
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object)
/*    */   {
/* 61 */     if ((object instanceof PairwiseEquivalence)) {
/* 62 */       PairwiseEquivalence that = (PairwiseEquivalence)object;
/* 63 */       return this.elementEquivalence.equals(that.elementEquivalence);
/*    */     }
/*    */ 
/* 66 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 71 */     return this.elementEquivalence.hashCode() ^ 0x46A3EB07;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 76 */     return this.elementEquivalence + ".pairwise()";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.PairwiseEquivalence
 * JD-Core Version:    0.6.2
 */