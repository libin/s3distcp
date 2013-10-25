/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class LoggerPatternConverter extends NamePatternConverter
/*    */ {
/* 33 */   private static final LoggerPatternConverter INSTANCE = new LoggerPatternConverter(null);
/*    */ 
/*    */   private LoggerPatternConverter(String[] options)
/*    */   {
/* 41 */     super("Logger", "logger", options);
/*    */   }
/*    */ 
/*    */   public static LoggerPatternConverter newInstance(String[] options)
/*    */   {
/* 51 */     if ((options == null) || (options.length == 0)) {
/* 52 */       return INSTANCE;
/*    */     }
/*    */ 
/* 55 */     return new LoggerPatternConverter(options);
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*    */   {
/* 62 */     int initialLength = toAppendTo.length();
/* 63 */     toAppendTo.append(event.getLoggerName());
/* 64 */     abbreviate(initialLength, toAppendTo);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.LoggerPatternConverter
 * JD-Core Version:    0.6.2
 */