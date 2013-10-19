/*    */ package com.amazonaws.internal;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public class CRC32MismatchException extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public CRC32MismatchException(String message, Throwable t)
/*    */   {
/* 36 */     super(message, t);
/*    */   }
/*    */ 
/*    */   public CRC32MismatchException(String message)
/*    */   {
/* 46 */     super(message);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.internal.CRC32MismatchException
 * JD-Core Version:    0.6.2
 */