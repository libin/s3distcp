/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class NDCPatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 32 */   private static final NDCPatternConverter INSTANCE = new NDCPatternConverter();
/*    */ 
/*    */   private NDCPatternConverter()
/*    */   {
/* 39 */     super("NDC", "ndc");
/*    */   }
/*    */ 
/*    */   public static NDCPatternConverter newInstance(String[] options)
/*    */   {
/* 49 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*    */   {
/* 56 */     toAppendTo.append(event.getNDC());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.NDCPatternConverter
 * JD-Core Version:    0.6.2
 */