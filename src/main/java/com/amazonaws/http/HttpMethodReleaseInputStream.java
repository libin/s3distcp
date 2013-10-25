/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.HttpEntity;
/*     */ import org.apache.http.HttpEntityEnclosingRequest;
/*     */ import org.apache.http.client.methods.AbortableHttpRequest;
/*     */ 
/*     */ public class HttpMethodReleaseInputStream extends InputStream
/*     */ {
/*  47 */   private static final Log log = LogFactory.getLog(HttpMethodReleaseInputStream.class);
/*     */ 
/*  49 */   private InputStream inputStream = null;
/*  50 */   private HttpEntityEnclosingRequest httpRequest = null;
/*  51 */   private boolean alreadyReleased = false;
/*  52 */   private boolean underlyingStreamConsumed = false;
/*     */ 
/*     */   public HttpMethodReleaseInputStream(HttpEntityEnclosingRequest httpMethod)
/*     */   {
/*  67 */     this.httpRequest = httpMethod;
/*     */     try
/*     */     {
/*  70 */       this.inputStream = httpMethod.getEntity().getContent();
/*     */     } catch (IOException e) {
/*  72 */       if (log.isWarnEnabled())
/*  73 */         log.warn("Unable to obtain HttpMethod's response data stream", e);
/*     */       try
/*     */       {
/*  76 */         httpMethod.getEntity().getContent().close(); } catch (Exception ex) {
/*     */       }
/*  78 */       this.inputStream = new ByteArrayInputStream(new byte[0]);
/*     */     }
/*     */   }
/*     */ 
/*     */   public HttpEntityEnclosingRequest getHttpRequest()
/*     */   {
/*  89 */     return this.httpRequest;
/*     */   }
/*     */ 
/*     */   protected void releaseConnection()
/*     */     throws IOException
/*     */   {
/* 100 */     if (!this.alreadyReleased) {
/* 101 */       if (!this.underlyingStreamConsumed)
/*     */       {
/* 104 */         if ((this.httpRequest instanceof AbortableHttpRequest)) {
/* 105 */           AbortableHttpRequest abortableHttpRequest = (AbortableHttpRequest)this.httpRequest;
/* 106 */           abortableHttpRequest.abort();
/*     */         }
/*     */       }
/* 109 */       this.inputStream.close();
/* 110 */       this.alreadyReleased = true;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 122 */       int read = this.inputStream.read();
/* 123 */       if (read == -1) {
/* 124 */         this.underlyingStreamConsumed = true;
/* 125 */         if (!this.alreadyReleased) {
/* 126 */           releaseConnection();
/* 127 */           if (log.isDebugEnabled()) {
/* 128 */             log.debug("Released HttpMethod as its response data stream is fully consumed");
/*     */           }
/*     */         }
/*     */       }
/* 132 */       return read;
/*     */     } catch (IOException e) {
/* 134 */       releaseConnection();
/* 135 */       if (log.isDebugEnabled()) {
/* 136 */         log.debug("Released HttpMethod as its response data stream threw an exception", e);
/*     */       }
/* 138 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 150 */       int read = this.inputStream.read(b, off, len);
/* 151 */       if (read == -1) {
/* 152 */         this.underlyingStreamConsumed = true;
/* 153 */         if (!this.alreadyReleased) {
/* 154 */           releaseConnection();
/* 155 */           if (log.isDebugEnabled()) {
/* 156 */             log.debug("Released HttpMethod as its response data stream is fully consumed");
/*     */           }
/*     */         }
/*     */       }
/* 160 */       return read;
/*     */     } catch (IOException e) {
/* 162 */       releaseConnection();
/* 163 */       if (log.isDebugEnabled()) {
/* 164 */         log.debug("Released HttpMethod as its response data stream threw an exception", e);
/*     */       }
/* 166 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int available()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 179 */       return this.inputStream.available();
/*     */     } catch (IOException e) {
/* 181 */       releaseConnection();
/* 182 */       if (log.isDebugEnabled()) {
/* 183 */         log.debug("Released HttpMethod as its response data stream threw an exception", e);
/*     */       }
/* 185 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 196 */     if (!this.alreadyReleased) {
/* 197 */       releaseConnection();
/* 198 */       if (log.isDebugEnabled()) {
/* 199 */         log.debug("Released HttpMethod as its response data stream is closed");
/*     */       }
/*     */     }
/* 202 */     this.inputStream.close();
/*     */   }
/*     */ 
/*     */   protected void finalize()
/*     */     throws Throwable
/*     */   {
/* 217 */     if (!this.alreadyReleased) {
/* 218 */       if (log.isWarnEnabled()) {
/* 219 */         log.warn("Attempting to release HttpMethod in finalize() as its response data stream has gone out of scope. This attempt will not always succeed and cannot be relied upon! Please ensure S3 response data streams are always fully consumed or closed to avoid HTTP connection starvation.");
/*     */       }
/*     */ 
/* 223 */       releaseConnection();
/* 224 */       if (log.isWarnEnabled()) {
/* 225 */         log.warn("Successfully released HttpMethod in finalize(). You were lucky this time... Please ensure S3 response data streams are always fully consumed or closed.");
/*     */       }
/*     */     }
/*     */ 
/* 229 */     super.finalize();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.HttpMethodReleaseInputStream
 * JD-Core Version:    0.6.2
 */