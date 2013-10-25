/*     */ package com.amazonaws.transform;
/*     */ 
/*     */ import com.amazonaws.AmazonServiceException;
/*     */ import com.amazonaws.AmazonServiceException.ErrorType;
/*     */ import com.amazonaws.util.XpathUtils;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class StandardErrorUnmarshaller extends AbstractErrorUnmarshaller<Node>
/*     */ {
/*     */   public StandardErrorUnmarshaller()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected StandardErrorUnmarshaller(Class<? extends AmazonServiceException> exceptionClass)
/*     */   {
/*  48 */     super(exceptionClass);
/*     */   }
/*     */ 
/*     */   public AmazonServiceException unmarshall(Node in)
/*     */     throws Exception
/*     */   {
/*  55 */     String errorCode = parseErrorCode(in);
/*  56 */     String errorType = XpathUtils.asString("ErrorResponse/Error/Type", in);
/*  57 */     String requestId = XpathUtils.asString("ErrorResponse/RequestId", in);
/*  58 */     String message = XpathUtils.asString("ErrorResponse/Error/Message", in);
/*     */ 
/*  60 */     AmazonServiceException ase = newException(message);
/*  61 */     ase.setErrorCode(errorCode);
/*  62 */     ase.setRequestId(requestId);
/*     */ 
/*  64 */     if (errorType == null)
/*  65 */       ase.setErrorType(AmazonServiceException.ErrorType.Unknown);
/*  66 */     else if (errorType.equalsIgnoreCase("Receiver"))
/*  67 */       ase.setErrorType(AmazonServiceException.ErrorType.Service);
/*  68 */     else if (errorType.equalsIgnoreCase("Sender")) {
/*  69 */       ase.setErrorType(AmazonServiceException.ErrorType.Client);
/*     */     }
/*     */ 
/*  72 */     return ase;
/*     */   }
/*     */ 
/*     */   public String parseErrorCode(Node in)
/*     */     throws Exception
/*     */   {
/*  88 */     return XpathUtils.asString("ErrorResponse/Error/Code", in);
/*     */   }
/*     */ 
/*     */   public String getErrorPropertyPath(String property)
/*     */   {
/* 100 */     return "ErrorResponse/Error/" + property;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.StandardErrorUnmarshaller
 * JD-Core Version:    0.6.2
 */