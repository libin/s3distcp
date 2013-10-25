/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LocationInfo;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class MethodLocationPatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 34 */   private static final MethodLocationPatternConverter INSTANCE = new MethodLocationPatternConverter();
/*    */ 
/*    */   private MethodLocationPatternConverter()
/*    */   {
/* 41 */     super("Method", "method");
/*    */   }
/*    */ 
/*    */   public static MethodLocationPatternConverter newInstance(String[] options)
/*    */   {
/* 51 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*    */   {
/* 58 */     LocationInfo locationInfo = event.getLocationInformation();
/*    */ 
/* 60 */     if (locationInfo != null)
/* 61 */       toAppendTo.append(locationInfo.getMethodName());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.MethodLocationPatternConverter
 * JD-Core Version:    0.6.2
 */