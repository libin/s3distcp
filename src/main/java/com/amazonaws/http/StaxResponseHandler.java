/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.AmazonWebServiceResponse;
/*     */ import com.amazonaws.ResponseMetadata;
/*     */ import com.amazonaws.transform.StaxUnmarshallerContext;
/*     */ import com.amazonaws.transform.Unmarshaller;
/*     */ import com.amazonaws.transform.VoidStaxUnmarshaller;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.InputStream;
/*     */ import java.util.Map;
/*     */ import javax.xml.stream.XMLEventReader;
/*     */ import javax.xml.stream.XMLInputFactory;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class StaxResponseHandler<T>
/*     */   implements HttpResponseHandler<AmazonWebServiceResponse<T>>
/*     */ {
/*     */   private Unmarshaller<T, StaxUnmarshallerContext> responseUnmarshaller;
/*  47 */   private static final Log log = LogFactory.getLog("com.amazonaws.request");
/*     */ 
/*  50 */   private static XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
/*     */ 
/*     */   public StaxResponseHandler(Unmarshaller<T, StaxUnmarshallerContext> responseUnmarshaller)
/*     */   {
/*  63 */     this.responseUnmarshaller = responseUnmarshaller;
/*     */ 
/*  72 */     if (this.responseUnmarshaller == null)
/*  73 */       this.responseUnmarshaller = new VoidStaxUnmarshaller();
/*     */   }
/*     */ 
/*     */   public AmazonWebServiceResponse<T> handle(HttpResponse response)
/*     */     throws Exception
/*     */   {
/*  82 */     log.trace("Parsing service response XML");
/*  83 */     InputStream content = response.getContent();
/*  84 */     if (content == null) content = new ByteArrayInputStream("<eof/>".getBytes());
/*  85 */     XMLEventReader eventReader = xmlInputFactory.createXMLEventReader(content);
/*     */     try {
/*  87 */       AmazonWebServiceResponse awsResponse = new AmazonWebServiceResponse();
/*  88 */       StaxUnmarshallerContext unmarshallerContext = new StaxUnmarshallerContext(eventReader, response.getHeaders());
/*  89 */       unmarshallerContext.registerMetadataExpression("ResponseMetadata/RequestId", 2, "AWS_REQUEST_ID");
/*  90 */       unmarshallerContext.registerMetadataExpression("requestId", 2, "AWS_REQUEST_ID");
/*  91 */       registerAdditionalMetadataExpressions(unmarshallerContext);
/*     */ 
/*  93 */       Object result = this.responseUnmarshaller.unmarshall(unmarshallerContext);
/*  94 */       awsResponse.setResult(result);
/*     */ 
/*  96 */       Map metadata = unmarshallerContext.getMetadata();
/*  97 */       awsResponse.setResponseMetadata(new ResponseMetadata(metadata));
/*     */ 
/*  99 */       log.trace("Done parsing service response");
/* 100 */       return awsResponse; } finally {
/*     */       try {
/* 102 */         eventReader.close();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void registerAdditionalMetadataExpressions(StaxUnmarshallerContext unmarshallerContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean needsConnectionLeftOpen()
/*     */   {
/* 124 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.StaxResponseHandler
 * JD-Core Version:    0.6.2
 */