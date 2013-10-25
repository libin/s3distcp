/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LocationInfo;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class LineLocationPatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 34 */   private static final LineLocationPatternConverter INSTANCE = new LineLocationPatternConverter();
/*    */ 
/*    */   private LineLocationPatternConverter()
/*    */   {
/* 41 */     super("Line", "line");
/*    */   }
/*    */ 
/*    */   public static LineLocationPatternConverter newInstance(String[] options)
/*    */   {
/* 51 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer output)
/*    */   {
/* 58 */     LocationInfo locationInfo = event.getLocationInformation();
/*    */ 
/* 60 */     if (locationInfo != null)
/* 61 */       output.append(locationInfo.getLineNumber());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.LineLocationPatternConverter
 * JD-Core Version:    0.6.2
 */