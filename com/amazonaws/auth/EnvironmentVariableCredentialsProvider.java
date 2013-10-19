/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ import com.amazonaws.AmazonClientException;
/*    */ 
/*    */ public class EnvironmentVariableCredentialsProvider
/*    */   implements AWSCredentialsProvider
/*    */ {
/*    */   private static final String ACCESS_KEY_ENV_VAR = "AWS_ACCESS_KEY_ID";
/*    */   private static final String SECRET_KEY_ENV_VAR = "AWS_SECRET_KEY";
/*    */ 
/*    */   public AWSCredentials getCredentials()
/*    */   {
/* 33 */     if ((System.getenv("AWS_ACCESS_KEY_ID") != null) && (System.getenv("AWS_SECRET_KEY") != null))
/*    */     {
/* 36 */       return new BasicAWSCredentials(System.getenv("AWS_ACCESS_KEY_ID"), System.getenv("AWS_SECRET_KEY"));
/*    */     }
/*    */ 
/* 41 */     throw new AmazonClientException("Unable to load AWS credentials from environment variables (AWS_ACCESS_KEY_ID and AWS_SECRET_KEY)");
/*    */   }
/*    */ 
/*    */   public void refresh()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 50 */     return getClass().getSimpleName();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.EnvironmentVariableCredentialsProvider
 * JD-Core Version:    0.6.2
 */