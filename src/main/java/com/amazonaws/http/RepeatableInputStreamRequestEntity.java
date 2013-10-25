/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.Request;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.entity.BasicHttpEntity;
/*     */ import org.apache.http.entity.InputStreamEntity;
/*     */ 
/*     */ class RepeatableInputStreamRequestEntity extends BasicHttpEntity
/*     */ {
/*  38 */   private boolean firstAttempt = true;
/*     */   private InputStreamEntity inputStreamRequestEntity;
/*     */   private InputStream content;
/*  47 */   private static final Log log = LogFactory.getLog(AmazonHttpClient.class);
/*     */   private IOException originalException;
/*     */ 
/*     */   RepeatableInputStreamRequestEntity(Request<?> request)
/*     */   {
/*  69 */     setChunked(false);
/*     */ 
/*  81 */     long contentLength = -1L;
/*     */     try {
/*  83 */       String contentLengthString = (String)request.getHeaders().get("Content-Length");
/*  84 */       if (contentLengthString != null)
/*  85 */         contentLength = Long.parseLong(contentLengthString);
/*     */     }
/*     */     catch (NumberFormatException nfe) {
/*  88 */       log.warn("Unable to parse content length from request.  Buffering contents in memory.");
/*     */     }
/*     */ 
/*  92 */     String contentType = (String)request.getHeaders().get("Content-Type");
/*     */ 
/*  94 */     this.inputStreamRequestEntity = new InputStreamEntity(request.getContent(), contentLength);
/*  95 */     this.inputStreamRequestEntity.setContentType(contentType);
/*  96 */     this.content = request.getContent();
/*     */ 
/*  98 */     setContent(this.content);
/*  99 */     setContentType(contentType);
/* 100 */     setContentLength(contentLength);
/*     */   }
/*     */ 
/*     */   public boolean isChunked()
/*     */   {
/* 105 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isRepeatable()
/*     */   {
/* 119 */     return (this.content.markSupported()) || (this.inputStreamRequestEntity.isRepeatable());
/*     */   }
/*     */ 
/*     */   public void writeTo(OutputStream output)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 137 */       if ((!this.firstAttempt) && (isRepeatable())) this.content.reset();
/*     */ 
/* 139 */       this.firstAttempt = false;
/* 140 */       this.inputStreamRequestEntity.writeTo(output);
/*     */     } catch (IOException ioe) {
/* 142 */       if (this.originalException == null) this.originalException = ioe;
/* 143 */       throw this.originalException;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.RepeatableInputStreamRequestEntity
 * JD-Core Version:    0.6.2
 */