/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import com.google.common.base.Preconditions;
/*    */ import java.util.Collection;
/*    */ import java.util.Set;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingSet<E> extends ForwardingCollection<E>
/*    */   implements Set<E>
/*    */ {
/*    */   protected abstract Set<E> delegate();
/*    */ 
/*    */   public boolean equals(@Nullable Object object)
/*    */   {
/* 60 */     return (object == this) || (delegate().equals(object));
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 64 */     return delegate().hashCode();
/*    */   }
/*    */ 
/*    */   protected boolean standardRemoveAll(Collection<?> collection)
/*    */   {
/* 76 */     return Sets.removeAllImpl(this, (Collection)Preconditions.checkNotNull(collection));
/*    */   }
/*    */ 
/*    */   @Beta
/*    */   protected boolean standardEquals(@Nullable Object object)
/*    */   {
/* 87 */     return Sets.equalsImpl(this, object);
/*    */   }
/*    */ 
/*    */   @Beta
/*    */   protected int standardHashCode()
/*    */   {
/* 98 */     return Sets.hashCodeImpl(this);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingSet
 * JD-Core Version:    0.6.2
 */