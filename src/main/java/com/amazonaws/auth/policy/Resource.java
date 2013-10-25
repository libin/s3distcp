/*    */ package com.amazonaws.auth.policy;
/*    */ 
/*    */ public class Resource
/*    */ {
/*    */   private final String resource;
/*    */ 
/*    */   public Resource(String resource)
/*    */   {
/* 61 */     this.resource = resource;
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 72 */     return this.resource;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.Resource
 * JD-Core Version:    0.6.2
 */