/*    */ package org.apache.log4j;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggerFactory;
/*    */ 
/*    */ class DefaultCategoryFactory
/*    */   implements LoggerFactory
/*    */ {
/*    */   public Logger makeNewLoggerInstance(String name)
/*    */   {
/* 29 */     return new Logger(name);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.DefaultCategoryFactory
 * JD-Core Version:    0.6.2
 */