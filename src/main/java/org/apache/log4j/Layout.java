/*    */ package org.apache.log4j;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ import org.apache.log4j.spi.OptionHandler;
/*    */ 
/*    */ public abstract class Layout
/*    */   implements OptionHandler
/*    */ {
/* 34 */   public static final String LINE_SEP = System.getProperty("line.separator");
/* 35 */   public static final int LINE_SEP_LEN = LINE_SEP.length();
/*    */ 
/*    */   public abstract String format(LoggingEvent paramLoggingEvent);
/*    */ 
/*    */   public String getContentType()
/*    */   {
/* 51 */     return "text/plain";
/*    */   }
/*    */ 
/*    */   public String getHeader()
/*    */   {
/* 59 */     return null;
/*    */   }
/*    */ 
/*    */   public String getFooter()
/*    */   {
/* 67 */     return null;
/*    */   }
/*    */ 
/*    */   public abstract boolean ignoresThrowable();
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.Layout
 * JD-Core Version:    0.6.2
 */