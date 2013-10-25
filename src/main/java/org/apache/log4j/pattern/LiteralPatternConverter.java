/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class LiteralPatternConverter extends LoggingEventPatternConverter
/*    */ {
/*    */   private final String literal;
/*    */ 
/*    */   public LiteralPatternConverter(String literal)
/*    */   {
/* 40 */     super("Literal", "literal");
/* 41 */     this.literal = literal;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*    */   {
/* 48 */     toAppendTo.append(this.literal);
/*    */   }
/*    */ 
/*    */   public void format(Object obj, StringBuffer toAppendTo)
/*    */   {
/* 55 */     toAppendTo.append(this.literal);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.LiteralPatternConverter
 * JD-Core Version:    0.6.2
 */