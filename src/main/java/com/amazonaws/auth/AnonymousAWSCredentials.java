/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ public class AnonymousAWSCredentials
/*    */   implements AWSCredentials
/*    */ {
/*    */   public String getAWSAccessKeyId()
/*    */   {
/* 30 */     return null;
/*    */   }
/*    */ 
/*    */   public String getAWSSecretKey()
/*    */   {
/* 37 */     return null;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AnonymousAWSCredentials
 * JD-Core Version:    0.6.2
 */