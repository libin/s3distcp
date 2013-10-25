/*    */ package org.apache.log4j.varia;
/*    */ 
/*    */ import org.apache.log4j.AppenderSkeleton;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class NullAppender extends AppenderSkeleton
/*    */ {
/* 30 */   private static NullAppender instance = new NullAppender();
/*    */ 
/*    */   public void activateOptions()
/*    */   {
/*    */   }
/*    */ 
/*    */   /** @deprecated */
/*    */   public NullAppender getInstance()
/*    */   {
/* 47 */     return instance;
/*    */   }
/*    */ 
/*    */   public static NullAppender getNullAppender()
/*    */   {
/* 55 */     return instance;
/*    */   }
/*    */ 
/*    */   public void close()
/*    */   {
/*    */   }
/*    */ 
/*    */   public void doAppend(LoggingEvent event)
/*    */   {
/*    */   }
/*    */ 
/*    */   protected void append(LoggingEvent event)
/*    */   {
/*    */   }
/*    */ 
/*    */   public boolean requiresLayout()
/*    */   {
/* 77 */     return false;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.varia.NullAppender
 * JD-Core Version:    0.6.2
 */