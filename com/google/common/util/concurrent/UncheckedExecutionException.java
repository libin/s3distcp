/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public class UncheckedExecutionException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   protected UncheckedExecutionException()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected UncheckedExecutionException(String message)
/*    */   {
/* 51 */     super(message);
/*    */   }
/*    */ 
/*    */   public UncheckedExecutionException(String message, Throwable cause)
/*    */   {
/* 58 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public UncheckedExecutionException(Throwable cause)
/*    */   {
/* 65 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.UncheckedExecutionException
 * JD-Core Version:    0.6.2
 */