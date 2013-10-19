/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ public class DefaultAWSCredentialsProviderChain extends AWSCredentialsProviderChain
/*    */ {
/*    */   public DefaultAWSCredentialsProviderChain()
/*    */   {
/* 31 */     super(new AWSCredentialsProvider[] { new EnvironmentVariableCredentialsProvider(), new SystemPropertiesCredentialsProvider(), new InstanceProfileCredentialsProvider() });
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.DefaultAWSCredentialsProviderChain
 * JD-Core Version:    0.6.2
 */