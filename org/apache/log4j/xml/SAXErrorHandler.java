/*    */ package org.apache.log4j.xml;
/*    */ 
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ import org.xml.sax.ErrorHandler;
/*    */ import org.xml.sax.SAXParseException;
/*    */ 
/*    */ public class SAXErrorHandler
/*    */   implements ErrorHandler
/*    */ {
/*    */   public void error(SAXParseException ex)
/*    */   {
/* 28 */     emitMessage("Continuable parsing error ", ex);
/*    */   }
/*    */ 
/*    */   public void fatalError(SAXParseException ex)
/*    */   {
/* 33 */     emitMessage("Fatal parsing error ", ex);
/*    */   }
/*    */ 
/*    */   public void warning(SAXParseException ex)
/*    */   {
/* 38 */     emitMessage("Parsing warning ", ex);
/*    */   }
/*    */ 
/*    */   private static void emitMessage(String msg, SAXParseException ex) {
/* 42 */     LogLog.warn(msg + ex.getLineNumber() + " and column " + ex.getColumnNumber());
/*    */ 
/* 44 */     LogLog.warn(ex.getMessage(), ex.getException());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.xml.SAXErrorHandler
 * JD-Core Version:    0.6.2
 */