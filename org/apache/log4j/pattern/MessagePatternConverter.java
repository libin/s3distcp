/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class MessagePatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 32 */   private static final MessagePatternConverter INSTANCE = new MessagePatternConverter();
/*    */ 
/*    */   private MessagePatternConverter()
/*    */   {
/* 39 */     super("Message", "message");
/*    */   }
/*    */ 
/*    */   public static MessagePatternConverter newInstance(String[] options)
/*    */   {
/* 49 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*    */   {
/* 56 */     toAppendTo.append(event.getRenderedMessage());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.MessagePatternConverter
 * JD-Core Version:    0.6.2
 */