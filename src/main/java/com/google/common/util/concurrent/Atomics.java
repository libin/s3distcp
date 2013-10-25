/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.util.concurrent.atomic.AtomicReference;
/*    */ import java.util.concurrent.atomic.AtomicReferenceArray;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public final class Atomics
/*    */ {
/*    */   public static <V> AtomicReference<V> newReference()
/*    */   {
/* 43 */     return new AtomicReference();
/*    */   }
/*    */ 
/*    */   public static <V> AtomicReference<V> newReference(@Nullable V initialValue)
/*    */   {
/* 53 */     return new AtomicReference(initialValue);
/*    */   }
/*    */ 
/*    */   public static <E> AtomicReferenceArray<E> newReferenceArray(int length)
/*    */   {
/* 63 */     return new AtomicReferenceArray(length);
/*    */   }
/*    */ 
/*    */   public static <E> AtomicReferenceArray<E> newReferenceArray(E[] array)
/*    */   {
/* 74 */     return new AtomicReferenceArray(array);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.Atomics
 * JD-Core Version:    0.6.2
 */