/*    */ package org.apache.log4j;
/*    */ 
/*    */ public class BasicConfigurator
/*    */ {
/*    */   public static void configure()
/*    */   {
/* 46 */     Logger root = Logger.getRootLogger();
/* 47 */     root.addAppender(new ConsoleAppender(new PatternLayout("%r [%t] %p %c %x - %m%n")));
/*    */   }
/*    */ 
/*    */   public static void configure(Appender appender)
/*    */   {
/* 58 */     Logger root = Logger.getRootLogger();
/* 59 */     root.addAppender(appender);
/*    */   }
/*    */ 
/*    */   public static void resetConfiguration()
/*    */   {
/* 71 */     LogManager.resetConfiguration();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.BasicConfigurator
 * JD-Core Version:    0.6.2
 */