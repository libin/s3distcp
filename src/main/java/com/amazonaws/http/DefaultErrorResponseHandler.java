/*    */ package com.amazonaws.http;
/*    */ 
/*    */ import com.amazonaws.AmazonClientException;
/*    */ import com.amazonaws.AmazonServiceException;
/*    */ import com.amazonaws.transform.Unmarshaller;
/*    */ import com.amazonaws.util.XpathUtils;
/*    */ import java.util.List;
/*    */ import org.w3c.dom.Document;
/*    */ import org.w3c.dom.Node;
/*    */ 
/*    */ public class DefaultErrorResponseHandler
/*    */   implements HttpResponseHandler<AmazonServiceException>
/*    */ {
/*    */   private List<Unmarshaller<AmazonServiceException, Node>> unmarshallerList;
/*    */ 
/*    */   public DefaultErrorResponseHandler(List<Unmarshaller<AmazonServiceException, Node>> unmarshallerList)
/*    */   {
/* 57 */     this.unmarshallerList = unmarshallerList;
/*    */   }
/*    */ 
/*    */   public AmazonServiceException handle(HttpResponse errorResponse)
/*    */     throws Exception
/*    */   {
/* 65 */     Document document = XpathUtils.documentFrom(errorResponse.getContent());
/*    */ 
/* 74 */     for (Unmarshaller unmarshaller : this.unmarshallerList) {
/* 75 */       AmazonServiceException ase = (AmazonServiceException)unmarshaller.unmarshall(document);
/* 76 */       if (ase != null) {
/* 77 */         ase.setStatusCode(errorResponse.getStatusCode());
/* 78 */         return ase;
/*    */       }
/*    */     }
/*    */ 
/* 82 */     throw new AmazonClientException("Unable to unmarshall error response from service");
/*    */   }
/*    */ 
/*    */   public boolean needsConnectionLeftOpen()
/*    */   {
/* 93 */     return false;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.DefaultErrorResponseHandler
 * JD-Core Version:    0.6.2
 */