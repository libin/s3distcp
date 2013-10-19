/*     */ package com.amazonaws;
/*     */ 
/*     */ public class AmazonServiceException extends AmazonClientException
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private String requestId;
/*     */   private String errorCode;
/*  82 */   private ErrorType errorType = ErrorType.Unknown;
/*     */   private int statusCode;
/*     */   private String serviceName;
/*     */ 
/*     */   public AmazonServiceException(String message)
/*     */   {
/*  99 */     super(message);
/*     */   }
/*     */ 
/*     */   public AmazonServiceException(String message, Exception cause)
/*     */   {
/* 112 */     super(message, cause);
/*     */   }
/*     */ 
/*     */   public void setRequestId(String requestId)
/*     */   {
/* 122 */     this.requestId = requestId;
/*     */   }
/*     */ 
/*     */   public String getRequestId()
/*     */   {
/* 133 */     return this.requestId;
/*     */   }
/*     */ 
/*     */   public void setServiceName(String serviceName)
/*     */   {
/* 143 */     this.serviceName = serviceName;
/*     */   }
/*     */ 
/*     */   public String getServiceName()
/*     */   {
/* 152 */     return this.serviceName;
/*     */   }
/*     */ 
/*     */   public void setErrorCode(String errorCode)
/*     */   {
/* 162 */     this.errorCode = errorCode;
/*     */   }
/*     */ 
/*     */   public String getErrorCode()
/*     */   {
/* 171 */     return this.errorCode;
/*     */   }
/*     */ 
/*     */   public void setErrorType(ErrorType errorType)
/*     */   {
/* 185 */     this.errorType = errorType;
/*     */   }
/*     */ 
/*     */   public ErrorType getErrorType()
/*     */   {
/* 195 */     return this.errorType;
/*     */   }
/*     */ 
/*     */   public void setStatusCode(int statusCode)
/*     */   {
/* 206 */     this.statusCode = statusCode;
/*     */   }
/*     */ 
/*     */   public int getStatusCode()
/*     */   {
/* 217 */     return this.statusCode;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 228 */     return "Status Code: " + getStatusCode() + ", " + "AWS Service: " + getServiceName() + ", " + "AWS Request ID: " + getRequestId() + ", " + "AWS Error Code: " + getErrorCode() + ", " + "AWS Error Message: " + getMessage();
/*     */   }
/*     */ 
/*     */   public static enum ErrorType
/*     */   {
/*  58 */     Client, 
/*  59 */     Service, 
/*  60 */     Unknown;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.AmazonServiceException
 * JD-Core Version:    0.6.2
 */