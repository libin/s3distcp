/*    */ package com.amazonaws.http;
/*    */ 
/*    */ import com.amazonaws.AmazonClientException;
/*    */ import com.amazonaws.AmazonServiceException;
/*    */ import com.amazonaws.AmazonServiceException.ErrorType;
/*    */ import com.amazonaws.Request;
/*    */ import com.amazonaws.transform.Unmarshaller;
/*    */ import com.amazonaws.util.json.JSONObject;
/*    */ import java.io.BufferedReader;
/*    */ import java.io.InputStream;
/*    */ import java.io.InputStreamReader;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class JsonErrorResponseHandler
/*    */   implements HttpResponseHandler<AmazonServiceException>
/*    */ {
/*    */   private List<Unmarshaller<AmazonServiceException, JSONObject>> unmarshallerList;
/*    */ 
/*    */   public JsonErrorResponseHandler(List<Unmarshaller<AmazonServiceException, JSONObject>> exceptionUnmarshallers)
/*    */   {
/* 38 */     this.unmarshallerList = exceptionUnmarshallers;
/*    */   }
/*    */ 
/*    */   public AmazonServiceException handle(HttpResponse response) throws Exception {
/* 42 */     JSONObject jsonErrorMessage = new JSONObject(readStreamContents(response.getContent()));
/*    */ 
/* 44 */     AmazonServiceException ase = runErrorUnmarshallers(response, jsonErrorMessage);
/* 45 */     if (ase == null) return null;
/*    */ 
/* 47 */     ase.setServiceName(response.getRequest().getServiceName());
/* 48 */     ase.setStatusCode(response.getStatusCode());
/* 49 */     if (response.getStatusCode() < 500)
/* 50 */       ase.setErrorType(AmazonServiceException.ErrorType.Client);
/*    */     else {
/* 52 */       ase.setErrorType(AmazonServiceException.ErrorType.Service);
/*    */     }
/*    */ 
/* 55 */     for (Map.Entry headerEntry : response.getHeaders().entrySet()) {
/* 56 */       if (((String)headerEntry.getKey()).equalsIgnoreCase("X-Amzn-RequestId")) {
/* 57 */         ase.setRequestId((String)headerEntry.getValue());
/*    */       }
/*    */     }
/*    */ 
/* 61 */     return ase;
/*    */   }
/*    */ 
/*    */   protected AmazonServiceException runErrorUnmarshallers(HttpResponse errorResponse, JSONObject json)
/*    */     throws Exception
/*    */   {
/* 72 */     for (Unmarshaller unmarshaller : this.unmarshallerList) {
/* 73 */       AmazonServiceException ase = (AmazonServiceException)unmarshaller.unmarshall(json);
/* 74 */       if (ase != null) {
/* 75 */         ase.setStatusCode(errorResponse.getStatusCode());
/* 76 */         return ase;
/*    */       }
/*    */     }
/*    */ 
/* 80 */     return null;
/*    */   }
/*    */ 
/*    */   public boolean needsConnectionLeftOpen() {
/* 84 */     return false;
/*    */   }
/*    */ 
/*    */   private String readStreamContents(InputStream stream) {
/*    */     try {
/* 89 */       BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
/* 90 */       StringBuilder sb = new StringBuilder();
/*    */       while (true) {
/* 92 */         String line = reader.readLine();
/* 93 */         if (line == null) break;
/* 94 */         sb.append(line);
/*    */       }
/*    */ 
/* 97 */       return sb.toString(); } catch (Exception e) {
/*    */       try {
/* 99 */         stream.close(); } catch (Exception ex) {
/* 100 */       }throw new AmazonClientException(new StringBuilder().append("Unable to read error response: ").append(e.getMessage()).toString(), e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.JsonErrorResponseHandler
 * JD-Core Version:    0.6.2
 */