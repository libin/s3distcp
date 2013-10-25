/*    */ package com.amazonaws.auth.policy;
/*    */ 
/*    */ public class Principal
/*    */ {
/* 40 */   public static final Principal AllUsers = new Principal("*");
/*    */   private final String id;
/*    */ 
/*    */   public Principal(String accountId)
/*    */   {
/* 51 */     if (accountId == null) {
/* 52 */       throw new IllegalArgumentException("Null AWS account ID specified");
/*    */     }
/* 54 */     this.id = accountId.replaceAll("-", "");
/*    */   }
/*    */ 
/*    */   public String getProvider()
/*    */   {
/* 64 */     return "AWS";
/*    */   }
/*    */ 
/*    */   public String getId()
/*    */   {
/* 73 */     return this.id;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.Principal
 * JD-Core Version:    0.6.2
 */