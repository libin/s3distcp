/*    */ package com.amazonaws;
/*    */ 
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ResponseMetadata
/*    */ {
/*    */   public static final String AWS_REQUEST_ID = "AWS_REQUEST_ID";
/*    */   protected final Map<String, String> metadata;
/*    */ 
/*    */   public ResponseMetadata(Map<String, String> metadata)
/*    */   {
/* 41 */     this.metadata = metadata;
/*    */   }
/*    */ 
/*    */   public ResponseMetadata(ResponseMetadata originalResponseMetadata)
/*    */   {
/* 53 */     this(originalResponseMetadata.metadata);
/*    */   }
/*    */ 
/*    */   public String getRequestId()
/*    */   {
/* 64 */     return (String)this.metadata.get("AWS_REQUEST_ID");
/*    */   }
/*    */ 
/*    */   public String toString()
/*    */   {
/* 69 */     if (this.metadata == null) return "{}";
/* 70 */     return this.metadata.toString();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.ResponseMetadata
 * JD-Core Version:    0.6.2
 */