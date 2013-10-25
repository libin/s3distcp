/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public enum BoundType
/*    */ {
/* 33 */   OPEN, 
/*    */ 
/* 42 */   CLOSED;
/*    */ 
/*    */   static BoundType forBoolean(boolean inclusive)
/*    */   {
/* 53 */     return inclusive ? CLOSED : OPEN;
/*    */   }
/*    */ 
/*    */   abstract BoundType flip();
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.BoundType
 * JD-Core Version:    0.6.2
 */