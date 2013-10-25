/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ import com.amazonaws.AmazonClientException;
/*    */ 
/*    */ public class SystemPropertiesCredentialsProvider
/*    */   implements AWSCredentialsProvider
/*    */ {
/*    */   private static final String ACCESS_KEY_PROPERTY = "aws.accessKeyId";
/*    */   private static final String SECRET_KEY_PROPERTY = "aws.secretKey";
/*    */ 
/*    */   public AWSCredentials getCredentials()
/*    */   {
/* 33 */     if ((System.getProperty("aws.accessKeyId") != null) && (System.getProperty("aws.secretKey") != null))
/*    */     {
/* 35 */       return new BasicAWSCredentials(System.getProperty("aws.accessKeyId"), System.getProperty("aws.secretKey"));
/*    */     }
/*    */ 
/* 40 */     throw new AmazonClientException("Unable to load AWS credentials from Java system properties (aws.accessKeyId and aws.secretKey)");
/*    */   }
/*    */ 
/*    */   public void refresh()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 49 */     return getClass().getSimpleName();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.SystemPropertiesCredentialsProvider
 * JD-Core Version:    0.6.2
 */