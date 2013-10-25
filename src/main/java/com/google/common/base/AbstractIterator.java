/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Iterator;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ @GwtCompatible
/*    */ abstract class AbstractIterator<T>
/*    */   implements Iterator<T>
/*    */ {
/* 32 */   private State state = State.NOT_READY;
/*    */   private T next;
/*    */ 
/*    */   protected abstract T computeNext();
/*    */ 
/*    */   protected final T endOfData()
/*    */   {
/* 45 */     this.state = State.DONE;
/* 46 */     return null;
/*    */   }
/*    */ 
/*    */   public final boolean hasNext()
/*    */   {
/* 51 */     Preconditions.checkState(this.state != State.FAILED);
/* 52 */     switch (1.$SwitchMap$com$google$common$base$AbstractIterator$State[this.state.ordinal()]) {
/*    */     case 1:
/* 54 */       return false;
/*    */     case 2:
/* 56 */       return true;
/*    */     }
/*    */ 
/* 59 */     return tryToComputeNext();
/*    */   }
/*    */ 
/*    */   private boolean tryToComputeNext() {
/* 63 */     this.state = State.FAILED;
/* 64 */     this.next = computeNext();
/* 65 */     if (this.state != State.DONE) {
/* 66 */       this.state = State.READY;
/* 67 */       return true;
/*    */     }
/* 69 */     return false;
/*    */   }
/*    */ 
/*    */   public final T next()
/*    */   {
/* 74 */     if (!hasNext()) {
/* 75 */       throw new NoSuchElementException();
/*    */     }
/* 77 */     this.state = State.NOT_READY;
/* 78 */     return this.next;
/*    */   }
/*    */ 
/*    */   public final void remove() {
/* 82 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ 
/*    */   private static enum State
/*    */   {
/* 37 */     READY, NOT_READY, DONE, FAILED;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.AbstractIterator
 * JD-Core Version:    0.6.2
 */