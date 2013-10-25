/*     */ package com.amazonaws.transform;
/*     */ 
/*     */ import com.amazonaws.AmazonServiceException;
/*     */ import com.amazonaws.AmazonServiceException.ErrorType;
/*     */ import com.amazonaws.util.XpathUtils;
/*     */ import java.lang.reflect.Constructor;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class LegacyErrorUnmarshaller
/*     */   implements Unmarshaller<AmazonServiceException, Node>
/*     */ {
/*     */   private final Class<? extends AmazonServiceException> exceptionClass;
/*     */ 
/*     */   public LegacyErrorUnmarshaller()
/*     */   {
/*  42 */     this(AmazonServiceException.class);
/*     */   }
/*     */ 
/*     */   protected LegacyErrorUnmarshaller(Class<? extends AmazonServiceException> exceptionClass)
/*     */   {
/*  55 */     this.exceptionClass = exceptionClass;
/*     */   }
/*     */ 
/*     */   public AmazonServiceException unmarshall(Node in)
/*     */     throws Exception
/*     */   {
/*  62 */     String errorCode = parseErrorCode(in);
/*  63 */     String message = XpathUtils.asString("Response/Errors/Error/Message", in);
/*  64 */     String requestId = XpathUtils.asString("Response/RequestID", in);
/*  65 */     String errorType = XpathUtils.asString("Response/Errors/Error/Type", in);
/*     */ 
/*  67 */     Constructor constructor = this.exceptionClass.getConstructor(new Class[] { String.class });
/*  68 */     AmazonServiceException ase = (AmazonServiceException)constructor.newInstance(new Object[] { message });
/*  69 */     ase.setErrorCode(errorCode);
/*  70 */     ase.setRequestId(requestId);
/*     */ 
/*  72 */     if (errorType == null)
/*  73 */       ase.setErrorType(AmazonServiceException.ErrorType.Unknown);
/*  74 */     else if (errorType.equalsIgnoreCase("server"))
/*  75 */       ase.setErrorType(AmazonServiceException.ErrorType.Service);
/*  76 */     else if (errorType.equalsIgnoreCase("client")) {
/*  77 */       ase.setErrorType(AmazonServiceException.ErrorType.Client);
/*     */     }
/*     */ 
/*  80 */     return ase;
/*     */   }
/*     */ 
/*     */   public String parseErrorCode(Node in)
/*     */     throws Exception
/*     */   {
/*  96 */     return XpathUtils.asString("Response/Errors/Error/Code", in);
/*     */   }
/*     */ 
/*     */   public String getErrorPropertyPath(String property)
/*     */   {
/* 108 */     return "Response/Errors/Error/" + property;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.LegacyErrorUnmarshaller
 * JD-Core Version:    0.6.2
 */