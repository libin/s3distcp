/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public class ExecutionError extends Error
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   protected ExecutionError()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected ExecutionError(String message)
/*    */   {
/* 46 */     super(message);
/*    */   }
/*    */ 
/*    */   public ExecutionError(String message, Error cause)
/*    */   {
/* 53 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public ExecutionError(Error cause)
/*    */   {
/* 60 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ExecutionError
 * JD-Core Version:    0.6.2
 */