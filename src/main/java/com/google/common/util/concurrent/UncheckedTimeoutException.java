/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ public class UncheckedTimeoutException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public UncheckedTimeoutException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public UncheckedTimeoutException(String message)
/*    */   {
/* 29 */     super(message);
/*    */   }
/*    */ 
/*    */   public UncheckedTimeoutException(Throwable cause) {
/* 33 */     super(cause);
/*    */   }
/*    */ 
/*    */   public UncheckedTimeoutException(String message, Throwable cause) {
/* 37 */     super(message, cause);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.UncheckedTimeoutException
 * JD-Core Version:    0.6.2
 */