/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.Request;
/*     */ import java.io.InputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import org.apache.http.client.methods.HttpRequestBase;
/*     */ 
/*     */ public class HttpResponse
/*     */ {
/*     */   private final Request<?> request;
/*     */   private final HttpRequestBase httpRequest;
/*     */   private String statusText;
/*     */   private int statusCode;
/*     */   private InputStream content;
/*  37 */   private Map<String, String> headers = new HashMap();
/*     */ 
/*     */   public HttpResponse(Request<?> request, HttpRequestBase httpRequest)
/*     */   {
/*  48 */     this.request = request;
/*  49 */     this.httpRequest = httpRequest;
/*     */   }
/*     */ 
/*     */   public Request<?> getRequest()
/*     */   {
/*  58 */     return this.request;
/*     */   }
/*     */ 
/*     */   public HttpRequestBase getHttpRequest()
/*     */   {
/*  67 */     return this.httpRequest;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getHeaders()
/*     */   {
/*  76 */     return this.headers;
/*     */   }
/*     */ 
/*     */   public void addHeader(String name, String value)
/*     */   {
/*  88 */     this.headers.put(name, value);
/*     */   }
/*     */ 
/*     */   public void setContent(InputStream content)
/*     */   {
/*  98 */     this.content = content;
/*     */   }
/*     */ 
/*     */   public InputStream getContent()
/*     */   {
/* 107 */     return this.content;
/*     */   }
/*     */ 
/*     */   public void setStatusText(String statusText)
/*     */   {
/* 118 */     this.statusText = statusText;
/*     */   }
/*     */ 
/*     */   public String getStatusText()
/*     */   {
/* 127 */     return this.statusText;
/*     */   }
/*     */ 
/*     */   public void setStatusCode(int statusCode)
/*     */   {
/* 138 */     this.statusCode = statusCode;
/*     */   }
/*     */ 
/*     */   public int getStatusCode()
/*     */   {
/* 148 */     return this.statusCode;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.HttpResponse
 * JD-Core Version:    0.6.2
 */