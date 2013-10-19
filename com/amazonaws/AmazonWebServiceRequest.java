/*     */ package com.amazonaws;
/*     */ 
/*     */ import com.amazonaws.auth.AWSCredentials;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public abstract class AmazonWebServiceRequest
/*     */ {
/*  31 */   private final RequestClientOptions requestClientOptions = new RequestClientOptions();
/*     */   private String delegationToken;
/*     */   private AWSCredentials credentials;
/*     */ 
/*     */   @Deprecated
/*     */   public String getDelegationToken()
/*     */   {
/*  56 */     return this.delegationToken;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public void setDelegationToken(String delegationToken)
/*     */   {
/*  73 */     this.delegationToken = delegationToken;
/*     */   }
/*     */ 
/*     */   public void setRequestCredentials(AWSCredentials credentials)
/*     */   {
/*  85 */     this.credentials = credentials;
/*     */   }
/*     */ 
/*     */   public AWSCredentials getRequestCredentials()
/*     */   {
/*  96 */     return this.credentials;
/*     */   }
/*     */ 
/*     */   public Map<String, String> copyPrivateRequestParameters()
/*     */   {
/* 107 */     HashMap map = new HashMap();
/* 108 */     if (this.delegationToken != null) map.put("SecurityToken", this.delegationToken);
/*     */ 
/* 110 */     return map;
/*     */   }
/*     */ 
/*     */   public RequestClientOptions getRequestClientOptions()
/*     */   {
/* 119 */     return this.requestClientOptions;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.AmazonWebServiceRequest
 * JD-Core Version:    0.6.2
 */