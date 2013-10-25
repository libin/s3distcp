/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.ClientConfiguration;
/*     */ import com.amazonaws.Request;
/*     */ import com.amazonaws.util.HttpUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.client.methods.HttpDelete;
/*     */ import org.apache.http.client.methods.HttpGet;
/*     */ import org.apache.http.client.methods.HttpHead;
/*     */ import org.apache.http.client.methods.HttpPost;
/*     */ import org.apache.http.client.methods.HttpPut;
/*     */ import org.apache.http.client.methods.HttpRequestBase;
/*     */ import org.apache.http.entity.BufferedHttpEntity;
/*     */ import org.apache.http.entity.StringEntity;
/*     */ import org.apache.http.params.HttpParams;
/*     */ 
/*     */ class HttpRequestFactory
/*     */ {
/*     */   private static final String DEFAULT_ENCODING = "UTF-8";
/*     */ 
/*     */   HttpRequestBase createHttpRequest(Request<?> request, ClientConfiguration clientConfiguration, HttpEntity previousEntity, ExecutionContext context)
/*     */   {
/*  59 */     URI endpoint = request.getEndpoint();
/*  60 */     String uri = endpoint.toString();
/*  61 */     if ((request.getResourcePath() != null) && (request.getResourcePath().length() > 0)) {
/*  62 */       if (request.getResourcePath().startsWith("/")) {
/*  63 */         if (uri.endsWith("/"))
/*  64 */           uri = uri.substring(0, uri.length() - 1);
/*     */       }
/*  66 */       else if (!uri.endsWith("/")) {
/*  67 */         uri = uri + "/";
/*     */       }
/*  69 */       uri = uri + request.getResourcePath();
/*  70 */     } else if (!uri.endsWith("/")) {
/*  71 */       uri = uri + "/";
/*     */     }
/*     */ 
/*  74 */     String encodedParams = HttpUtils.encodeParameters(request);
/*     */ 
/*  81 */     boolean requestHasNoPayload = request.getContent() != null;
/*  82 */     boolean requestIsPost = request.getHttpMethod() == HttpMethodName.POST;
/*  83 */     boolean putParamsInUri = (!requestIsPost) || (requestHasNoPayload);
/*  84 */     if ((encodedParams != null) && (putParamsInUri))
/*  85 */       uri = uri + "?" + encodedParams;
/*     */     HttpRequestBase httpRequest;
/*  89 */     if (request.getHttpMethod() == HttpMethodName.POST) {
/*  90 */       HttpPost postMethod = new HttpPost(uri);
/*     */ 
/*  99 */       if ((request.getContent() == null) && (encodedParams != null))
/* 100 */         postMethod.setEntity(newStringEntity(encodedParams));
/*     */       else {
/* 102 */         postMethod.setEntity(new RepeatableInputStreamRequestEntity(request));
/*     */       }
/* 104 */       httpRequest = postMethod;
/* 105 */     } else if (request.getHttpMethod() == HttpMethodName.PUT) {
/* 106 */       HttpPut putMethod = new HttpPut(uri);
/* 107 */       HttpRequestBase httpRequest = putMethod;
/*     */ 
/* 116 */       putMethod.getParams().setParameter("http.protocol.expect-continue", Boolean.valueOf(true));
/*     */ 
/* 118 */       if (previousEntity != null) {
/* 119 */         putMethod.setEntity(previousEntity);
/* 120 */       } else if (request.getContent() != null) {
/* 121 */         HttpEntity entity = new RepeatableInputStreamRequestEntity(request);
/* 122 */         if (request.getHeaders().get("Content-Length") == null) {
/* 123 */           entity = newBufferedHttpEntity(entity);
/*     */         }
/* 125 */         putMethod.setEntity(entity);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       HttpRequestBase httpRequest;
/* 127 */       if (request.getHttpMethod() == HttpMethodName.GET) {
/* 128 */         httpRequest = new HttpGet(uri);
/*     */       }
/*     */       else
/*     */       {
/*     */         HttpRequestBase httpRequest;
/* 129 */         if (request.getHttpMethod() == HttpMethodName.DELETE) {
/* 130 */           httpRequest = new HttpDelete(uri);
/*     */         }
/*     */         else
/*     */         {
/*     */           HttpRequestBase httpRequest;
/* 131 */           if (request.getHttpMethod() == HttpMethodName.HEAD)
/* 132 */             httpRequest = new HttpHead(uri);
/*     */           else
/* 134 */             throw new AmazonClientException("Unknown HTTP method name: " + request.getHttpMethod());
/*     */         }
/*     */       }
/*     */     }
/*     */     HttpRequestBase httpRequest;
/* 137 */     configureHeaders(httpRequest, request, context, clientConfiguration);
/*     */ 
/* 139 */     return httpRequest;
/*     */   }
/*     */ 
/*     */   private void configureHeaders(HttpRequestBase httpRequest, Request<?> request, ExecutionContext context, ClientConfiguration clientConfiguration)
/*     */   {
/* 152 */     URI endpoint = request.getEndpoint();
/* 153 */     String hostHeader = endpoint.getHost();
/* 154 */     if (HttpUtils.isUsingNonDefaultPort(endpoint)) {
/* 155 */       hostHeader = hostHeader + ":" + endpoint.getPort();
/*     */     }
/* 157 */     httpRequest.addHeader("Host", hostHeader);
/*     */ 
/* 160 */     for (Map.Entry entry : request.getHeaders().entrySet())
/*     */     {
/* 167 */       if ((!((String)entry.getKey()).equalsIgnoreCase("Content-Length")) && (!((String)entry.getKey()).equalsIgnoreCase("Host")))
/*     */       {
/* 169 */         httpRequest.addHeader((String)entry.getKey(), (String)entry.getValue());
/*     */       }
/*     */     }
/*     */ 
/* 173 */     if ((httpRequest.getHeaders("Content-Type") == null) || (httpRequest.getHeaders("Content-Type").length == 0)) {
/* 174 */       httpRequest.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + "UTF-8".toLowerCase());
/*     */     }
/*     */ 
/* 180 */     if ((context != null) && (context.getContextUserAgent() != null))
/* 181 */       httpRequest.addHeader("User-Agent", createUserAgentString(clientConfiguration, context.getContextUserAgent()));
/*     */   }
/*     */ 
/*     */   private String createUserAgentString(ClientConfiguration clientConfiguration, String contextUserAgent)
/*     */   {
/* 187 */     if (clientConfiguration.getUserAgent().contains(contextUserAgent)) {
/* 188 */       return clientConfiguration.getUserAgent();
/*     */     }
/* 190 */     return clientConfiguration.getUserAgent() + " " + contextUserAgent;
/*     */   }
/*     */ 
/*     */   private HttpEntity newStringEntity(String s)
/*     */   {
/*     */     try
/*     */     {
/* 205 */       return new StringEntity(s);
/*     */     } catch (UnsupportedEncodingException e) {
/* 207 */       throw new AmazonClientException("Unable to create HTTP entity: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private HttpEntity newBufferedHttpEntity(HttpEntity entity)
/*     */   {
/*     */     try
/*     */     {
/* 222 */       return new BufferedHttpEntity(entity);
/*     */     } catch (IOException e) {
/* 224 */       throw new AmazonClientException("Unable to create HTTP entity: " + e.getMessage(), e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.HttpRequestFactory
 * JD-Core Version:    0.6.2
 */