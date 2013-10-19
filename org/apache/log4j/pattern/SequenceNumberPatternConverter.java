/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class SequenceNumberPatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 33 */   private static final SequenceNumberPatternConverter INSTANCE = new SequenceNumberPatternConverter();
/*    */ 
/*    */   private SequenceNumberPatternConverter()
/*    */   {
/* 40 */     super("Sequence Number", "sn");
/*    */   }
/*    */ 
/*    */   public static SequenceNumberPatternConverter newInstance(String[] options)
/*    */   {
/* 50 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*    */   {
/* 57 */     toAppendTo.append("0");
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.SequenceNumberPatternConverter
 * JD-Core Version:    0.6.2
 */