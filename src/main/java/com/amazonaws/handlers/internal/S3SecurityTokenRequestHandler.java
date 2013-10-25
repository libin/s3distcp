/*    */ package com.amazonaws.handlers.internal;
/*    */ 
/*    */ import com.amazonaws.AmazonWebServiceRequest;
/*    */ import com.amazonaws.Request;
/*    */ import com.amazonaws.handlers.AbstractRequestHandler;
/*    */ 
/*    */ @Deprecated
/*    */ public class S3SecurityTokenRequestHandler extends AbstractRequestHandler
/*    */ {
/*    */   public void beforeRequest(Request<?> request)
/*    */   {
/* 32 */     AmazonWebServiceRequest originalRequest = request.getOriginalRequest();
/* 33 */     if ((originalRequest != null) && (originalRequest.getDelegationToken() != null))
/* 34 */       request.addHeader("x-amz-security-token", originalRequest.getDelegationToken());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.handlers.internal.S3SecurityTokenRequestHandler
 * JD-Core Version:    0.6.2
 */