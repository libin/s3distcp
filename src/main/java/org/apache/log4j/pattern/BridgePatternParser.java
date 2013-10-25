/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.helpers.PatternConverter;
/*    */ import org.apache.log4j.helpers.PatternParser;
/*    */ 
/*    */ public final class BridgePatternParser extends PatternParser
/*    */ {
/*    */   public BridgePatternParser(String conversionPattern)
/*    */   {
/* 38 */     super(conversionPattern);
/*    */   }
/*    */ 
/*    */   public PatternConverter parse()
/*    */   {
/* 46 */     return new BridgePatternConverter(this.pattern);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.BridgePatternParser
 * JD-Core Version:    0.6.2
 */