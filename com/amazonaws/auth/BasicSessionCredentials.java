/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ public class BasicSessionCredentials
/*    */   implements AWSSessionCredentials
/*    */ {
/*    */   private final String awsAccessKey;
/*    */   private final String awsSecretKey;
/*    */   private final String sessionToken;
/*    */ 
/*    */   public BasicSessionCredentials(String awsAccessKey, String awsSecretKey, String sessionToken)
/*    */   {
/* 27 */     this.awsAccessKey = awsAccessKey;
/* 28 */     this.awsSecretKey = awsSecretKey;
/* 29 */     this.sessionToken = sessionToken;
/*    */   }
/*    */ 
/*    */   public String getAWSAccessKeyId() {
/* 33 */     return this.awsAccessKey;
/*    */   }
/*    */ 
/*    */   public String getAWSSecretKey() {
/* 37 */     return this.awsSecretKey;
/*    */   }
/*    */ 
/*    */   public String getSessionToken() {
/* 41 */     return this.sessionToken;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.BasicSessionCredentials
 * JD-Core Version:    0.6.2
 */