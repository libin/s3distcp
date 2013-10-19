/*     */ package com.amazonaws;
/*     */ 
/*     */ import com.amazonaws.http.HttpMethodName;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class DefaultRequest<T>
/*     */   implements Request<T>
/*     */ {
/*     */   private String resourcePath;
/*  36 */   private Map<String, String> parameters = new HashMap();
/*     */ 
/*  39 */   private Map<String, String> headers = new HashMap();
/*     */   private URI endpoint;
/*     */   private String serviceName;
/*     */   private final AmazonWebServiceRequest originalRequest;
/*  54 */   private HttpMethodName httpMethod = HttpMethodName.POST;
/*     */   private InputStream content;
/*     */ 
/*     */   public DefaultRequest(AmazonWebServiceRequest originalRequest, String serviceName)
/*     */   {
/*  70 */     this.serviceName = serviceName;
/*  71 */     this.originalRequest = originalRequest;
/*     */   }
/*     */ 
/*     */   public DefaultRequest(String serviceName)
/*     */   {
/*  82 */     this(null, serviceName);
/*     */   }
/*     */ 
/*     */   public AmazonWebServiceRequest getOriginalRequest()
/*     */   {
/*  94 */     return this.originalRequest;
/*     */   }
/*     */ 
/*     */   public void addHeader(String name, String value)
/*     */   {
/* 101 */     this.headers.put(name, value);
/*     */   }
/*     */ 
/*     */   public Map<String, String> getHeaders()
/*     */   {
/* 108 */     return this.headers;
/*     */   }
/*     */ 
/*     */   public void setResourcePath(String resourcePath)
/*     */   {
/* 115 */     this.resourcePath = resourcePath;
/*     */   }
/*     */ 
/*     */   public String getResourcePath()
/*     */   {
/* 122 */     return this.resourcePath;
/*     */   }
/*     */ 
/*     */   public void addParameter(String name, String value)
/*     */   {
/* 129 */     this.parameters.put(name, value);
/*     */   }
/*     */ 
/*     */   public Map<String, String> getParameters()
/*     */   {
/* 136 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   public Request<T> withParameter(String name, String value)
/*     */   {
/* 143 */     addParameter(name, value);
/* 144 */     return this;
/*     */   }
/*     */ 
/*     */   public HttpMethodName getHttpMethod()
/*     */   {
/* 151 */     return this.httpMethod;
/*     */   }
/*     */ 
/*     */   public void setHttpMethod(HttpMethodName httpMethod)
/*     */   {
/* 158 */     this.httpMethod = httpMethod;
/*     */   }
/*     */ 
/*     */   public void setEndpoint(URI endpoint)
/*     */   {
/* 165 */     this.endpoint = endpoint;
/*     */   }
/*     */ 
/*     */   public URI getEndpoint()
/*     */   {
/* 172 */     return this.endpoint;
/*     */   }
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/* 179 */     return this.serviceName;
/*     */   }
/*     */ 
/*     */   public InputStream getContent()
/*     */   {
/* 186 */     return this.content;
/*     */   }
/*     */ 
/*     */   public void setContent(InputStream content)
/*     */   {
/* 193 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public void setHeaders(Map<String, String> headers)
/*     */   {
/* 200 */     this.headers.clear();
/* 201 */     this.headers.putAll(headers);
/*     */   }
/*     */ 
/*     */   public void setParameters(Map<String, String> parameters)
/*     */   {
/* 208 */     this.parameters.clear();
/* 209 */     this.parameters.putAll(parameters);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 214 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 216 */     builder.append(new StringBuilder().append(getHttpMethod().toString()).append(" ").toString());
/* 217 */     builder.append(new StringBuilder().append(getEndpoint().toString()).append(" ").toString());
/*     */ 
/* 219 */     builder.append(new StringBuilder().append("/").append(getResourcePath() != null ? getResourcePath() : "").append(" ").toString());
/*     */ 
/* 223 */     if (!getParameters().isEmpty()) {
/* 224 */       builder.append("Parameters: (");
/* 225 */       for (String key : getParameters().keySet()) {
/* 226 */         String value = (String)getParameters().get(key);
/* 227 */         builder.append(new StringBuilder().append(key).append(": ").append(value).append(", ").toString());
/*     */       }
/* 229 */       builder.append(") ");
/*     */     }
/*     */ 
/* 232 */     if (!getHeaders().isEmpty()) {
/* 233 */       builder.append("Headers: (");
/* 234 */       for (String key : getHeaders().keySet()) {
/* 235 */         String value = (String)getHeaders().get(key);
/* 236 */         builder.append(new StringBuilder().append(key).append(": ").append(value).append(", ").toString());
/*     */       }
/* 238 */       builder.append(") ");
/*     */     }
/*     */ 
/* 241 */     return builder.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.DefaultRequest
 * JD-Core Version:    0.6.2
 */