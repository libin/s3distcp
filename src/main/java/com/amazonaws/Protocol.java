/*    */ package com.amazonaws;
/*    */ 
/*    */ public enum Protocol
/*    */ {
/* 32 */   HTTP("http"), 
/*    */ 
/* 39 */   HTTPS("https");
/*    */ 
/*    */   private final String protocol;
/*    */ 
/*    */   private Protocol(String protocol) {
/* 44 */     this.protocol = protocol;
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 52 */     return this.protocol;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.Protocol
 * JD-Core Version:    0.6.2
 */