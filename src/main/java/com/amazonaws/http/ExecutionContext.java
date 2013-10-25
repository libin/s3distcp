/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.auth.AWSCredentials;
/*     */ import com.amazonaws.auth.Signer;
/*     */ import com.amazonaws.handlers.RequestHandler;
/*     */ import com.amazonaws.internal.CustomBackoffStrategy;
/*     */ import com.amazonaws.util.AWSRequestMetrics;
/*     */ import java.util.List;
/*     */ 
/*     */ public class ExecutionContext
/*     */ {
/*     */   private List<RequestHandler> requestHandlers;
/*     */   private String contextUserAgent;
/*  28 */   private AWSRequestMetrics awsRequestMetrics = new AWSRequestMetrics();
/*     */   private CustomBackoffStrategy backoffStrategy;
/*     */   private Signer signer;
/*     */   private AWSCredentials credentials;
/*     */ 
/*     */   public String getContextUserAgent()
/*     */   {
/*  39 */     return this.contextUserAgent;
/*     */   }
/*     */ 
/*     */   public void setContextUserAgent(String contextUserAgent) {
/*  43 */     this.contextUserAgent = contextUserAgent;
/*     */   }
/*     */   public ExecutionContext() {
/*     */   }
/*     */ 
/*     */   public ExecutionContext(List<RequestHandler> requestHandlers) {
/*  49 */     this.requestHandlers = requestHandlers;
/*     */   }
/*     */ 
/*     */   public List<RequestHandler> getRequestHandlers()
/*     */   {
/*  59 */     return this.requestHandlers;
/*     */   }
/*     */ 
/*     */   public AWSRequestMetrics getAwsRequestMetrics() {
/*  63 */     return this.awsRequestMetrics;
/*     */   }
/*     */ 
/*     */   public void setAwsRequestMetrics(AWSRequestMetrics awsRequestMetrics) {
/*  67 */     this.awsRequestMetrics = awsRequestMetrics;
/*     */   }
/*     */ 
/*     */   public Signer getSigner()
/*     */   {
/*  76 */     return this.signer;
/*     */   }
/*     */ 
/*     */   public void setSigner(Signer signer)
/*     */   {
/*  88 */     this.signer = signer;
/*     */   }
/*     */ 
/*     */   public AWSCredentials getCredentials()
/*     */   {
/*  97 */     return this.credentials;
/*     */   }
/*     */ 
/*     */   public void setCredentials(AWSCredentials credentials)
/*     */   {
/* 110 */     this.credentials = credentials;
/*     */   }
/*     */ 
/*     */   public CustomBackoffStrategy getCustomBackoffStrategy()
/*     */   {
/* 121 */     return this.backoffStrategy;
/*     */   }
/*     */ 
/*     */   public void setCustomBackoffStrategy(CustomBackoffStrategy backoffStrategy)
/*     */   {
/* 134 */     this.backoffStrategy = backoffStrategy;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.ExecutionContext
 * JD-Core Version:    0.6.2
 */