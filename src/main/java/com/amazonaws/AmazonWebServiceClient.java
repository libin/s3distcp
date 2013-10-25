/*     */ package com.amazonaws;
/*     */ 
/*     */ import com.amazonaws.handlers.RequestHandler;
/*     */ import com.amazonaws.http.AmazonHttpClient;
/*     */ import com.amazonaws.http.ExecutionContext;
/*     */ import com.amazonaws.http.HttpMethodName;
/*     */ import com.amazonaws.http.HttpRequest;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public abstract class AmazonWebServiceClient
/*     */ {
/*     */   protected URI endpoint;
/*     */   protected ClientConfiguration clientConfiguration;
/*     */   protected AmazonHttpClient client;
/*     */   protected final List<RequestHandler> requestHandlers;
/*     */ 
/*     */   public AmazonWebServiceClient(ClientConfiguration clientConfiguration)
/*     */   {
/*  59 */     this.clientConfiguration = clientConfiguration;
/*  60 */     this.client = new AmazonHttpClient(clientConfiguration);
/*  61 */     this.requestHandlers = Collections.synchronizedList(new LinkedList());
/*     */   }
/*     */ 
/*     */   public void setEndpoint(String endpoint)
/*     */     throws IllegalArgumentException
/*     */   {
/*  97 */     if (!endpoint.contains("://")) {
/*  98 */       endpoint = this.clientConfiguration.getProtocol().toString() + "://" + endpoint;
/*     */     }
/*     */     try
/*     */     {
/* 102 */       this.endpoint = new URI(endpoint);
/*     */     } catch (URISyntaxException e) {
/* 104 */       throw new IllegalArgumentException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setConfiguration(ClientConfiguration clientConfiguration) {
/* 109 */     this.clientConfiguration = clientConfiguration;
/* 110 */     this.client = new AmazonHttpClient(clientConfiguration);
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/* 121 */     this.client.shutdown();
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   protected <T> HttpRequest convertToHttpRequest(Request<T> request, HttpMethodName methodName)
/*     */   {
/* 139 */     HttpRequest httpRequest = new HttpRequest(methodName);
/* 140 */     for (Map.Entry parameter : request.getParameters().entrySet()) {
/* 141 */       httpRequest.addParameter((String)parameter.getKey(), (String)parameter.getValue());
/*     */     }
/*     */ 
/* 144 */     for (Map.Entry parameter : request.getHeaders().entrySet()) {
/* 145 */       httpRequest.addHeader((String)parameter.getKey(), (String)parameter.getValue());
/*     */     }
/*     */ 
/* 148 */     httpRequest.setServiceName(request.getServiceName());
/* 149 */     httpRequest.setEndpoint(request.getEndpoint());
/* 150 */     httpRequest.setResourcePath(request.getResourcePath());
/* 151 */     httpRequest.setOriginalRequest(request.getOriginalRequest());
/*     */ 
/* 153 */     return httpRequest;
/*     */   }
/*     */ 
/*     */   public void addRequestHandler(RequestHandler requestHandler)
/*     */   {
/* 165 */     this.requestHandlers.add(requestHandler);
/*     */   }
/*     */ 
/*     */   public void removeRequestHandler(RequestHandler requestHandler)
/*     */   {
/* 177 */     this.requestHandlers.remove(requestHandler);
/*     */   }
/*     */ 
/*     */   protected ExecutionContext createExecutionContext() {
/* 181 */     ExecutionContext executionContext = new ExecutionContext(this.requestHandlers);
/* 182 */     return executionContext;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.AmazonWebServiceClient
 * JD-Core Version:    0.6.2
 */