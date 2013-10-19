/*    */ package com.amazonaws.util.json;
/*    */ 
/*    */ public class JSONException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 0L;
/*    */   private Throwable cause;
/*    */ 
/*    */   public JSONException(String message)
/*    */   {
/* 42 */     super(message);
/*    */   }
/*    */ 
/*    */   public JSONException(Throwable t) {
/* 46 */     super(t.getMessage());
/* 47 */     this.cause = t;
/*    */   }
/*    */ 
/*    */   public Throwable getCause() {
/* 51 */     return this.cause;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.json.JSONException
 * JD-Core Version:    0.6.2
 */