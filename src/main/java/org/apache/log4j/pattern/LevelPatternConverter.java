/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import org.apache.log4j.Level;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public final class LevelPatternConverter extends LoggingEventPatternConverter
/*    */ {
/*    */   private static final int TRACE_INT = 5000;
/* 38 */   private static final LevelPatternConverter INSTANCE = new LevelPatternConverter();
/*    */ 
/*    */   private LevelPatternConverter()
/*    */   {
/* 45 */     super("Level", "level");
/*    */   }
/*    */ 
/*    */   public static LevelPatternConverter newInstance(String[] options)
/*    */   {
/* 55 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(LoggingEvent event, StringBuffer output)
/*    */   {
/* 62 */     output.append(event.getLevel().toString());
/*    */   }
/*    */ 
/*    */   public String getStyleClass(Object e)
/*    */   {
/* 69 */     if ((e instanceof LoggingEvent)) {
/* 70 */       int lint = ((LoggingEvent)e).getLevel().toInt();
/*    */ 
/* 72 */       switch (lint) {
/*    */       case 5000:
/* 74 */         return "level trace";
/*    */       case 10000:
/* 77 */         return "level debug";
/*    */       case 20000:
/* 80 */         return "level info";
/*    */       case 30000:
/* 83 */         return "level warn";
/*    */       case 40000:
/* 86 */         return "level error";
/*    */       case 50000:
/* 89 */         return "level fatal";
/*    */       }
/*    */ 
/* 92 */       return "level " + ((LoggingEvent)e).getLevel().toString();
/*    */     }
/*    */ 
/* 96 */     return "level";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.LevelPatternConverter
 * JD-Core Version:    0.6.2
 */