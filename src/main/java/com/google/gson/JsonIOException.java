/*    */ package com.google.gson;
/*    */ 
/*    */ public final class JsonIOException extends JsonParseException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public JsonIOException(String msg)
/*    */   {
/* 29 */     super(msg);
/*    */   }
/*    */ 
/*    */   public JsonIOException(String msg, Throwable cause) {
/* 33 */     super(msg, cause);
/*    */   }
/*    */ 
/*    */   public JsonIOException(Throwable cause)
/*    */   {
/* 43 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonIOException
 * JD-Core Version:    0.6.2
 */