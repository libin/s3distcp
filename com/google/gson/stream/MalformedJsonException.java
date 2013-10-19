/*    */ package com.google.gson.stream;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ public final class MalformedJsonException extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public MalformedJsonException(String msg)
/*    */   {
/* 29 */     super(msg);
/*    */   }
/*    */ 
/*    */   public MalformedJsonException(String msg, Throwable throwable) {
/* 33 */     super(msg);
/*    */ 
/* 36 */     initCause(throwable);
/*    */   }
/*    */ 
/*    */   public MalformedJsonException(Throwable throwable)
/*    */   {
/* 42 */     initCause(throwable);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.stream.MalformedJsonException
 * JD-Core Version:    0.6.2
 */