/*    */ package com.amazonaws;
/*    */ 
/*    */ public class AmazonWebServiceResponse<T>
/*    */ {
/*    */   private T result;
/*    */   private ResponseMetadata responseMetadata;
/*    */ 
/*    */   public T getResult()
/*    */   {
/* 40 */     return this.result;
/*    */   }
/*    */ 
/*    */   public void setResult(T result)
/*    */   {
/* 50 */     this.result = result;
/*    */   }
/*    */ 
/*    */   public void setResponseMetadata(ResponseMetadata responseMetadata)
/*    */   {
/* 60 */     this.responseMetadata = responseMetadata;
/*    */   }
/*    */ 
/*    */   public ResponseMetadata getResponseMetadata()
/*    */   {
/* 73 */     return this.responseMetadata;
/*    */   }
/*    */ 
/*    */   public String getRequestId()
/*    */   {
/* 84 */     if (this.responseMetadata == null) return null;
/* 85 */     return this.responseMetadata.getRequestId();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.AmazonWebServiceResponse
 * JD-Core Version:    0.6.2
 */