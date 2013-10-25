/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ class EmptyImmutableSetMultimap extends ImmutableSetMultimap<Object, Object>
/*    */ {
/* 28 */   static final EmptyImmutableSetMultimap INSTANCE = new EmptyImmutableSetMultimap();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   private EmptyImmutableSetMultimap()
/*    */   {
/* 32 */     super(ImmutableMap.of(), 0, null);
/*    */   }
/*    */ 
/*    */   private Object readResolve() {
/* 36 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableSetMultimap
 * JD-Core Version:    0.6.2
 */