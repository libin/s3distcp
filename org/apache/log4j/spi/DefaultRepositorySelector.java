/*    */ package org.apache.log4j.spi;
/*    */ 
/*    */ public class DefaultRepositorySelector
/*    */   implements RepositorySelector
/*    */ {
/*    */   final LoggerRepository repository;
/*    */ 
/*    */   public DefaultRepositorySelector(LoggerRepository repository)
/*    */   {
/* 29 */     this.repository = repository;
/*    */   }
/*    */ 
/*    */   public LoggerRepository getLoggerRepository()
/*    */   {
/* 34 */     return this.repository;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.DefaultRepositorySelector
 * JD-Core Version:    0.6.2
 */