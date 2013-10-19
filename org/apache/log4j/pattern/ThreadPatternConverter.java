/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class ThreadPatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 32 */   private static final ThreadPatternConverter INSTANCE = new ThreadPatternConverter();
/*    */ 
/*    */   private ThreadPatternConverter()
/*    */   {
/* 39 */     super("Thread", "thread");
/*    */   }
/*    */ 
/*    */   public static ThreadPatternConverter newInstance(String[] options)
/*    */   {
/* 49 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*    */   {
/* 56 */     toAppendTo.append(event.getThreadName());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.ThreadPatternConverter
 * JD-Core Version:    0.6.2
 */