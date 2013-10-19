/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.spi.LocationInfo;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class FileLocationPatternConverter extends LoggingEventPatternConverter
/*    */ {
/* 34 */   private static final FileLocationPatternConverter INSTANCE = new FileLocationPatternConverter();
/*    */ 
/*    */   private FileLocationPatternConverter()
/*    */   {
/* 41 */     super("File Location", "file");
/*    */   }
/*    */ 
/*    */   public static FileLocationPatternConverter newInstance(String[] options)
/*    */   {
/* 51 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer output)
/*    */   {
/* 58 */     LocationInfo locationInfo = event.getLocationInformation();
/*    */ 
/* 60 */     if (locationInfo != null)
/* 61 */       output.append(locationInfo.getFileName());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.FileLocationPatternConverter
 * JD-Core Version:    0.6.2
 */