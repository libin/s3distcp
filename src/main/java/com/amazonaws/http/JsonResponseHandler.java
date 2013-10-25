/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.AmazonWebServiceResponse;
/*     */ import com.amazonaws.ResponseMetadata;
/*     */ import com.amazonaws.internal.CRC32MismatchException;
/*     */ import com.amazonaws.transform.JsonUnmarshallerContext;
/*     */ import com.amazonaws.transform.Unmarshaller;
/*     */ import com.amazonaws.transform.VoidJsonUnmarshaller;
/*     */ import com.amazonaws.util.CRC32ChecksumCalculatingInputStream;
/*     */ import java.util.Map;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.codehaus.jackson.JsonFactory;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ 
/*     */ public class JsonResponseHandler<T>
/*     */   implements HttpResponseHandler<AmazonWebServiceResponse<T>>
/*     */ {
/*     */   private Unmarshaller<T, JsonUnmarshallerContext> responseUnmarshaller;
/*  45 */   private static final Log log = LogFactory.getLog("com.amazonaws.request");
/*     */ 
/*  47 */   private static JsonFactory jsonFactory = new JsonFactory();
/*     */ 
/*  49 */   public boolean needsConnectionLeftOpen = false;
/*     */ 
/*     */   public JsonResponseHandler(Unmarshaller<T, JsonUnmarshallerContext> responseUnmarshaller)
/*     */   {
/*  62 */     this.responseUnmarshaller = responseUnmarshaller;
/*     */ 
/*  71 */     if (this.responseUnmarshaller == null)
/*  72 */       this.responseUnmarshaller = new VoidJsonUnmarshaller();
/*     */   }
/*     */ 
/*     */   public AmazonWebServiceResponse<T> handle(HttpResponse response)
/*     */     throws Exception
/*     */   {
/*  81 */     log.trace("Parsing service response JSON");
/*     */ 
/*  83 */     String CRC32Checksum = (String)response.getHeaders().get("x-amz-crc32");
/*  84 */     CRC32ChecksumCalculatingInputStream crc32ChecksumInputStream = null;
/*     */ 
/*  86 */     JsonParser jsonParser = null;
/*     */ 
/*  88 */     if (!this.needsConnectionLeftOpen) {
/*  89 */       if (CRC32Checksum != null) {
/*  90 */         crc32ChecksumInputStream = new CRC32ChecksumCalculatingInputStream(response.getContent());
/*  91 */         jsonParser = jsonFactory.createJsonParser(crc32ChecksumInputStream);
/*     */       } else {
/*  93 */         jsonParser = jsonFactory.createJsonParser(response.getContent());
/*     */       }
/*     */     }
/*     */     try
/*     */     {
/*  98 */       AmazonWebServiceResponse awsResponse = new AmazonWebServiceResponse();
/*  99 */       JsonUnmarshallerContext unmarshallerContext = new JsonUnmarshallerContext(jsonParser, response);
/* 100 */       registerAdditionalMetadataExpressions(unmarshallerContext);
/*     */ 
/* 102 */       Object result = this.responseUnmarshaller.unmarshall(unmarshallerContext);
/*     */ 
/* 104 */       if (CRC32Checksum != null) {
/* 105 */         long serverSideCRC = Long.parseLong(CRC32Checksum);
/* 106 */         long clientSideCRC = crc32ChecksumInputStream.getCRC32Checksum();
/* 107 */         if (clientSideCRC != serverSideCRC) {
/* 108 */           throw new CRC32MismatchException("Client calculated crc32 checksum didn't match that calculated by server side");
/*     */         }
/*     */       }
/*     */ 
/* 112 */       awsResponse.setResult(result);
/*     */ 
/* 114 */       Map metadata = unmarshallerContext.getMetadata();
/* 115 */       metadata.put("AWS_REQUEST_ID", response.getHeaders().get("x-amzn-RequestId"));
/* 116 */       awsResponse.setResponseMetadata(new ResponseMetadata(metadata));
/*     */ 
/* 118 */       log.trace("Done parsing service response");
/* 119 */       return awsResponse;
/*     */     } finally {
/* 121 */       if (!this.needsConnectionLeftOpen) try {
/* 122 */           jsonParser.close();
/*     */         }
/*     */         catch (Exception e)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void registerAdditionalMetadataExpressions(JsonUnmarshallerContext unmarshallerContext)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean needsConnectionLeftOpen()
/*     */   {
/* 145 */     return this.needsConnectionLeftOpen;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.JsonResponseHandler
 * JD-Core Version:    0.6.2
 */