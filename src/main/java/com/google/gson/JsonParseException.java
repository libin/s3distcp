/*    */ package com.google.gson;
/*    */ 
/*    */ public class JsonParseException extends RuntimeException
/*    */ {
/*    */   static final long serialVersionUID = -4086729973971783390L;
/*    */ 
/*    */   public JsonParseException(String msg)
/*    */   {
/* 42 */     super(msg);
/*    */   }
/*    */ 
/*    */   public JsonParseException(String msg, Throwable cause)
/*    */   {
/* 52 */     super(msg, cause);
/*    */   }
/*    */ 
/*    */   public JsonParseException(Throwable cause)
/*    */   {
/* 62 */     super(cause);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonParseException
 * JD-Core Version:    0.6.2
 */