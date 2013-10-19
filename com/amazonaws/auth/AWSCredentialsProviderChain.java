/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ import com.amazonaws.AmazonClientException;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ 
/*    */ public class AWSCredentialsProviderChain
/*    */   implements AWSCredentialsProvider
/*    */ {
/* 36 */   private static final Log log = LogFactory.getLog(AWSCredentialsProviderChain.class);
/*    */ 
/* 38 */   private List<AWSCredentialsProvider> credentialsProviders = new LinkedList();
/*    */ 
/*    */   public AWSCredentialsProviderChain(AWSCredentialsProvider[] credentialsProviders)
/*    */   {
/* 52 */     if ((credentialsProviders == null) || (credentialsProviders.length == 0)) {
/* 53 */       throw new IllegalArgumentException("No credential providers specified");
/*    */     }
/* 55 */     for (AWSCredentialsProvider provider : credentialsProviders)
/* 56 */       this.credentialsProviders.add(provider);
/*    */   }
/*    */ 
/*    */   public AWSCredentials getCredentials()
/*    */   {
/* 61 */     for (AWSCredentialsProvider provider : this.credentialsProviders) {
/*    */       try {
/* 63 */         AWSCredentials credentials = provider.getCredentials();
/*    */ 
/* 65 */         if ((credentials.getAWSAccessKeyId() != null) && (credentials.getAWSSecretKey() != null))
/*    */         {
/* 67 */           log.debug("Loading credentials from " + provider.toString());
/* 68 */           return credentials;
/*    */         }
/*    */       }
/*    */       catch (Exception e) {
/* 72 */         log.debug("Unable to load credentials from " + provider.toString() + ": " + e.getMessage());
/*    */       }
/*    */ 
/*    */     }
/*    */ 
/* 77 */     throw new AmazonClientException("Unable to load AWS credentials from any provider in the chain");
/*    */   }
/*    */ 
/*    */   public void refresh() {
/* 81 */     for (AWSCredentialsProvider provider : this.credentialsProviders)
/* 82 */       provider.refresh();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AWSCredentialsProviderChain
 * JD-Core Version:    0.6.2
 */