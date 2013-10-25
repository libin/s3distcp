/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public abstract class LoggingEventPatternConverter extends PatternConverter
/*    */ {
/*    */   protected LoggingEventPatternConverter(String name, String style)
/*    */   {
/* 38 */     super(name, style);
/*    */   }
/*    */ 
/*    */   public abstract void format(LoggingEvent paramLoggingEvent, StringBuffer paramStringBuffer);
/*    */ 
/*    */   public void format(Object obj, StringBuffer output)
/*    */   {
/* 53 */     if ((obj instanceof LoggingEvent))
/* 54 */       format((LoggingEvent)obj, output);
/*    */   }
/*    */ 
/*    */   public boolean handlesThrowable()
/*    */   {
/* 68 */     return false;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.LoggingEventPatternConverter
 * JD-Core Version:    0.6.2
 */