/*    */ package com.amazonaws;
/*    */ 
/*    */ public class AmazonClientException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public AmazonClientException(String message, Throwable t)
/*    */   {
/* 49 */     super(message, t);
/*    */   }
/*    */ 
/*    */   public AmazonClientException(String message)
/*    */   {
/* 59 */     super(message);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.AmazonClientException
 * JD-Core Version:    0.6.2
 */