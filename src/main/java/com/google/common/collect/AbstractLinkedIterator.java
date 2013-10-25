/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.NoSuchElementException;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Deprecated
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public abstract class AbstractLinkedIterator<T> extends UnmodifiableIterator<T>
/*    */ {
/*    */   private T nextOrNull;
/*    */ 
/*    */   protected AbstractLinkedIterator(@Nullable T firstOrNull)
/*    */   {
/* 57 */     this.nextOrNull = firstOrNull;
/*    */   }
/*    */ 
/*    */   protected abstract T computeNext(T paramT);
/*    */ 
/*    */   public final boolean hasNext()
/*    */   {
/* 70 */     return this.nextOrNull != null;
/*    */   }
/*    */ 
/*    */   public final T next()
/*    */   {
/* 75 */     if (!hasNext())
/* 76 */       throw new NoSuchElementException();
/*    */     try
/*    */     {
/* 79 */       return this.nextOrNull;
/*    */     } finally {
/* 81 */       this.nextOrNull = computeNext(this.nextOrNull);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.AbstractLinkedIterator
 * JD-Core Version:    0.6.2
 */