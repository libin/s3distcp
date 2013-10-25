/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ class EmptyImmutableListMultimap extends ImmutableListMultimap<Object, Object>
/*    */ {
/* 28 */   static final EmptyImmutableListMultimap INSTANCE = new EmptyImmutableListMultimap();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   private EmptyImmutableListMultimap()
/*    */   {
/* 32 */     super(ImmutableMap.of(), 0);
/*    */   }
/*    */ 
/*    */   private Object readResolve() {
/* 36 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableListMultimap
 * JD-Core Version:    0.6.2
 */