/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class EmptyImmutableMultiset extends ImmutableMultiset<Object>
/*    */ {
/* 31 */   static final EmptyImmutableMultiset INSTANCE = new EmptyImmutableMultiset();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public int count(@Nullable Object element)
/*    */   {
/* 35 */     return 0;
/*    */   }
/*    */ 
/*    */   public ImmutableSet<Object> elementSet()
/*    */   {
/* 40 */     return ImmutableSet.of();
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 45 */     return 0;
/*    */   }
/*    */ 
/*    */   boolean isPartialView()
/*    */   {
/* 50 */     return false;
/*    */   }
/*    */ 
/*    */   ImmutableSet<Multiset.Entry<Object>> createEntrySet()
/*    */   {
/* 55 */     return ImmutableSet.of();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableMultiset
 * JD-Core Version:    0.6.2
 */