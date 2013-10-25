/*    */ package com.amazonaws.transform;
/*    */ 
/*    */ import com.amazonaws.AmazonServiceException;
/*    */ import java.lang.reflect.Constructor;
/*    */ 
/*    */ public abstract class AbstractErrorUnmarshaller<T>
/*    */   implements Unmarshaller<AmazonServiceException, T>
/*    */ {
/*    */   protected final Class<? extends AmazonServiceException> exceptionClass;
/*    */ 
/*    */   public AbstractErrorUnmarshaller()
/*    */   {
/* 35 */     this(AmazonServiceException.class);
/*    */   }
/*    */ 
/*    */   public AbstractErrorUnmarshaller(Class<? extends AmazonServiceException> exceptionClass)
/*    */   {
/* 47 */     this.exceptionClass = exceptionClass;
/*    */   }
/*    */ 
/*    */   protected AmazonServiceException newException(String message)
/*    */     throws Exception
/*    */   {
/* 65 */     Constructor constructor = this.exceptionClass.getConstructor(new Class[] { String.class });
/* 66 */     return (AmazonServiceException)constructor.newInstance(new Object[] { message });
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.AbstractErrorUnmarshaller
 * JD-Core Version:    0.6.2
 */