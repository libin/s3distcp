/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.AmazonWebServiceRequest;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class HttpRequest
/*     */ {
/*  26 */   private Map<String, String> parameters = new HashMap();
/*  27 */   private Map<String, String> headers = new HashMap();
/*     */   private HttpMethodName methodName;
/*     */   private String serviceName;
/*     */   private URI endpoint;
/*     */   private String resourcePath;
/*     */   private InputStream inputStream;
/*     */   private AmazonWebServiceRequest originalRequest;
/*     */ 
/*     */   public HttpRequest(HttpMethodName methodName)
/*     */   {
/*  44 */     this.methodName = methodName;
/*     */   }
/*     */ 
/*     */   public HttpMethodName getMethodName()
/*     */   {
/*  53 */     return this.methodName;
/*     */   }
/*     */ 
/*     */   public void setServiceName(String serviceName)
/*     */   {
/*  63 */     this.serviceName = serviceName;
/*     */   }
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/*  72 */     return this.serviceName;
/*     */   }
/*     */ 
/*     */   public URI getEndpoint()
/*     */   {
/*  82 */     return this.endpoint;
/*     */   }
/*     */ 
/*     */   public void setEndpoint(URI endpoint)
/*     */   {
/*  94 */     this.endpoint = endpoint;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getParameters()
/*     */   {
/* 103 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getHeaders()
/*     */   {
/* 112 */     return this.headers;
/*     */   }
/*     */ 
/*     */   public void addHeader(String name, String value)
/*     */   {
/* 124 */     this.headers.put(name, value);
/*     */   }
/*     */ 
/*     */   public void removeHeader(String name)
/*     */   {
/* 134 */     this.headers.remove(name);
/*     */   }
/*     */ 
/*     */   public void addParameter(String name, String value) {
/* 138 */     this.parameters.put(name, value);
/*     */   }
/*     */ 
/*     */   public void setParameters(Map<String, String> parameters) {
/* 142 */     this.parameters = parameters;
/*     */   }
/*     */ 
/*     */   public HttpRequest withParameter(String name, String value) {
/* 146 */     addParameter(name, value);
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */   public String getResourcePath()
/*     */   {
/* 156 */     return this.resourcePath;
/*     */   }
/*     */ 
/*     */   public void setResourcePath(String resourcePath)
/*     */   {
/* 166 */     this.resourcePath = resourcePath;
/*     */   }
/*     */ 
/*     */   public void setContent(InputStream inputStream)
/*     */   {
/* 178 */     this.inputStream = inputStream;
/*     */   }
/*     */ 
/*     */   public InputStream getContent()
/*     */   {
/* 189 */     return this.inputStream;
/*     */   }
/*     */ 
/*     */   public void setOriginalRequest(AmazonWebServiceRequest request)
/*     */   {
/* 200 */     this.originalRequest = request;
/*     */   }
/*     */ 
/*     */   public AmazonWebServiceRequest getOriginalRequest()
/*     */   {
/* 210 */     return this.originalRequest;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 216 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 218 */     builder.append(new StringBuilder().append(getMethodName().toString()).append(" ").toString());
/* 219 */     builder.append(new StringBuilder().append(getEndpoint().toString()).append(" ").toString());
/*     */ 
/* 221 */     builder.append(new StringBuilder().append("/").append(getResourcePath() != null ? getResourcePath() : "").append(" ").toString());
/*     */ 
/* 225 */     if (!getParameters().isEmpty()) {
/* 226 */       builder.append("Parameters: (");
/* 227 */       for (String key : getParameters().keySet()) {
/* 228 */         String value = (String)getParameters().get(key);
/* 229 */         builder.append(new StringBuilder().append(key).append(": ").append(value).append(", ").toString());
/*     */       }
/* 231 */       builder.append(") ");
/*     */     }
/*     */ 
/* 234 */     if (!getHeaders().isEmpty()) {
/* 235 */       builder.append("Headers: (");
/* 236 */       for (String key : getHeaders().keySet()) {
/* 237 */         String value = (String)getHeaders().get(key);
/* 238 */         builder.append(new StringBuilder().append(key).append(": ").append(value).append(", ").toString());
/*     */       }
/* 240 */       builder.append(") ");
/*     */     }
/*     */ 
/* 243 */     return builder.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.HttpRequest
 * JD-Core Version:    0.6.2
 */