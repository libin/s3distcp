/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LocationInfo;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class FullLocationPatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 34 */   private static final FullLocationPatternConverter INSTANCE = new FullLocationPatternConverter();
/*    */ 
/*    */   private FullLocationPatternConverter()
/*    */   {
/* 41 */     super("Full Location", "fullLocation");
/*    */   }
/*    */ 
/*    */   public static FullLocationPatternConverter newInstance(String[] options)
/*    */   {
/* 51 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer output)
/*    */   {
/* 58 */     LocationInfo locationInfo = event.getLocationInformation();
/*    */ 
/* 60 */     if (locationInfo != null)
/* 61 */       output.append(locationInfo.fullInfo);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.FullLocationPatternConverter
 * JD-Core Version:    0.6.2
 */