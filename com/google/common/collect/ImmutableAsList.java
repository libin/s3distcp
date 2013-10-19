/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.annotations.GwtIncompatible;
/*    */ import java.io.InvalidObjectException;
/*    */ import java.io.ObjectInputStream;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ @GwtCompatible(serializable=true, emulated=true)
/*    */ abstract class ImmutableAsList<E> extends ImmutableList<E>
/*    */ {
/*    */   abstract ImmutableCollection<E> delegateCollection();
/*    */ 
/*    */   public boolean contains(Object target)
/*    */   {
/* 41 */     return delegateCollection().contains(target);
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 46 */     return delegateCollection().size();
/*    */   }
/*    */ 
/*    */   public boolean isEmpty()
/*    */   {
/* 51 */     return delegateCollection().isEmpty();
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 56 */     return delegateCollection().isPartialView();
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   private void readObject(ObjectInputStream stream)
/*    */     throws InvalidObjectException
/*    */   {
/* 77 */     throw new InvalidObjectException("Use SerializedForm");
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   Object writeReplace() {
/* 82 */     return new SerializedForm(delegateCollection());
/*    */   }
/*    */ 
/*    */   @GwtIncompatible("serialization")
/*    */   static class SerializedForm
/*    */     implements Serializable
/*    */   {
/*    */     final ImmutableCollection<?> collection;
/*    */     private static final long serialVersionUID = 0L;
/*    */ 
/*    */     SerializedForm(ImmutableCollection<?> collection)
/*    */     {
/* 66 */       this.collection = collection;
/*    */     }
/*    */     Object readResolve() {
/* 69 */       return this.collection.asList();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableAsList
 * JD-Core Version:    0.6.2
 */