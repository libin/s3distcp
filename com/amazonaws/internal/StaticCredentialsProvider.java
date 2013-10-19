/*    */ package com.amazonaws.internal;
/*    */ 
/*    */ import com.amazonaws.auth.AWSCredentials;
/*    */ import com.amazonaws.auth.AWSCredentialsProvider;
/*    */ 
/*    */ public class StaticCredentialsProvider
/*    */   implements AWSCredentialsProvider
/*    */ {
/*    */   private final AWSCredentials credentials;
/*    */ 
/*    */   public StaticCredentialsProvider(AWSCredentials credentials)
/*    */   {
/* 28 */     this.credentials = credentials;
/*    */   }
/*    */ 
/*    */   public AWSCredentials getCredentials() {
/* 32 */     return this.credentials;
/*    */   }
/*    */ 
/*    */   public void refresh()
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.internal.StaticCredentialsProvider
 * JD-Core Version:    0.6.2
 */