/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ public enum SignatureVersion
/*    */ {
/* 19 */   V1("1"), V2("2");
/*    */ 
/*    */   private String value;
/*    */ 
/*    */   private SignatureVersion(String value) {
/* 24 */     this.value = value;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 29 */     return this.value;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.SignatureVersion
 * JD-Core Version:    0.6.2
 */