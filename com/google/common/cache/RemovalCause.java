/*    */ package com.google.common.cache;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ 
/*    */ @Beta
/*    */ public enum RemovalCause
/*    */ {
/* 38 */   EXPLICIT, 
/*    */ 
/* 51 */   REPLACED, 
/*    */ 
/* 63 */   COLLECTED, 
/*    */ 
/* 74 */   EXPIRED, 
/*    */ 
/* 85 */   SIZE;
/*    */ 
/*    */   abstract boolean wasEvicted();
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.RemovalCause
 * JD-Core Version:    0.6.2
 */