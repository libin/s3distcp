/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible
/*    */ public class ComputationException extends RuntimeException
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public ComputationException(Throwable cause)
/*    */   {
/* 33 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ComputationException
 * JD-Core Version:    0.6.2
 */