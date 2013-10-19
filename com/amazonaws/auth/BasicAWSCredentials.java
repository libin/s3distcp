/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ public class BasicAWSCredentials
/*    */   implements AWSCredentials
/*    */ {
/*    */   private final String accessKey;
/*    */   private final String secretKey;
/*    */ 
/*    */   public BasicAWSCredentials(String accessKey, String secretKey)
/*    */   {
/* 36 */     if (accessKey == null) {
/* 37 */       throw new IllegalArgumentException("Access key cannot be null.");
/*    */     }
/* 39 */     if (secretKey == null) {
/* 40 */       throw new IllegalArgumentException("Secret key cannot be null.");
/*    */     }
/*    */ 
/* 43 */     this.accessKey = accessKey;
/* 44 */     this.secretKey = secretKey;
/*    */   }
/*    */ 
/*    */   public String getAWSAccessKeyId()
/*    */   {
/* 51 */     return this.accessKey;
/*    */   }
/*    */ 
/*    */   public String getAWSSecretKey()
/*    */   {
/* 58 */     return this.secretKey;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.BasicAWSCredentials
 * JD-Core Version:    0.6.2
 */