/*    */ package com.amazonaws.transform;
/*    */ 
/*    */ import com.amazonaws.AmazonServiceException;
/*    */ import com.amazonaws.util.json.JSONObject;
/*    */ 
/*    */ public class JsonErrorUnmarshaller extends AbstractErrorUnmarshaller<JSONObject>
/*    */ {
/*    */   public JsonErrorUnmarshaller()
/*    */   {
/*    */   }
/*    */ 
/*    */   protected JsonErrorUnmarshaller(Class<? extends AmazonServiceException> exceptionClass)
/*    */   {
/* 28 */     super(exceptionClass);
/*    */   }
/*    */ 
/*    */   public AmazonServiceException unmarshall(JSONObject json) throws Exception {
/* 32 */     String message = parseMessage(json);
/*    */ 
/* 34 */     AmazonServiceException ase = newException(message);
/*    */ 
/* 36 */     String errorCode = parseErrorCode(json);
/* 37 */     ase.setErrorCode(errorCode);
/* 38 */     return ase;
/*    */   }
/*    */ 
/*    */   public String parseMessage(JSONObject json) throws Exception {
/* 42 */     String message = "";
/* 43 */     if (json.has("message"))
/* 44 */       message = json.getString("message");
/* 45 */     else if (json.has("Message")) {
/* 46 */       message = json.getString("Message");
/*    */     }
/*    */ 
/* 49 */     return message;
/*    */   }
/*    */ 
/*    */   public String parseErrorCode(JSONObject json) throws Exception {
/* 53 */     if (json.has("__type")) {
/* 54 */       String type = json.getString("__type");
/* 55 */       int separator = type.lastIndexOf("#");
/* 56 */       return type.substring(separator + 1);
/*    */     }
/*    */ 
/* 59 */     return null;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.JsonErrorUnmarshaller
 * JD-Core Version:    0.6.2
 */