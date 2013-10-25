/*    */ package com.amazonaws;
/*    */ 
/*    */ public final class RequestClientOptions
/*    */ {
/*    */   private String clientMarker;
/*    */ 
/*    */   public String getClientMarker()
/*    */   {
/* 29 */     return this.clientMarker;
/*    */   }
/*    */ 
/*    */   public void addClientMarker(String clientMarker)
/*    */   {
/* 36 */     if (this.clientMarker == null) {
/* 37 */       this.clientMarker = "";
/*    */     }
/* 39 */     this.clientMarker = createClientMarkerString(clientMarker);
/*    */   }
/*    */ 
/*    */   private String createClientMarkerString(String clientMarker)
/*    */   {
/* 46 */     if (this.clientMarker.contains(clientMarker)) {
/* 47 */       return this.clientMarker;
/*    */     }
/* 49 */     return this.clientMarker + " " + clientMarker;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.RequestClientOptions
 * JD-Core Version:    0.6.2
 */