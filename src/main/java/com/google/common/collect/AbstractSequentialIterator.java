/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.NoSuchElementException;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class AbstractSequentialIterator<T> extends UnmodifiableIterator<T>
/*    */ {
/*    */   private T nextOrNull;
/*    */ 
/*    */   protected AbstractSequentialIterator(@Nullable T firstOrNull)
/*    */   {
/* 52 */     this.nextOrNull = firstOrNull;
/*    */   }
/*    */ 
/*    */   protected abstract T computeNext(T paramT);
/*    */ 
/*    */   public final boolean hasNext()
/*    */   {
/* 65 */     return this.nextOrNull != null;
/*    */   }
/*    */ 
/*    */   public final T next()
/*    */   {
/* 70 */     if (!hasNext())
/* 71 */       throw new NoSuchElementException();
/*    */     try
/*    */     {
/* 74 */       return this.nextOrNull;
/*    */     } finally {
/* 76 */       this.nextOrNull = computeNext(this.nextOrNull);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractSequentialIterator
 * JD-Core Version:    0.6.2
 */