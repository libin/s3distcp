/*    */ package org.apache.log4j.config;
/*    */ 
/*    */ public class PropertySetterException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = -1352613734254235861L;
/*    */   protected Throwable rootCause;
/*    */ 
/*    */   public PropertySetterException(String msg)
/*    */   {
/* 33 */     super(msg);
/*    */   }
/*    */ 
/*    */   public PropertySetterException(Throwable rootCause)
/*    */   {
/* 40 */     this.rootCause = rootCause;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 48 */     String msg = super.getMessage();
/* 49 */     if ((msg == null) && (this.rootCause != null)) {
/* 50 */       msg = this.rootCause.getMessage();
/*    */     }
/* 52 */     return msg;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.config.PropertySetterException
 * JD-Core Version:    0.6.2
 */