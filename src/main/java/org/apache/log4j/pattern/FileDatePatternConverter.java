/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ public final class FileDatePatternConverter
/*    */ {
/*    */   public static PatternConverter newInstance(String[] options)
/*    */   {
/* 41 */     if ((options == null) || (options.length == 0)) {
/* 42 */       return DatePatternConverter.newInstance(new String[] { "yyyy-MM-dd" });
/*    */     }
/*    */ 
/* 48 */     return DatePatternConverter.newInstance(options);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.FileDatePatternConverter
 * JD-Core Version:    0.6.2
 */