/*    */ package org.apache.log4j;
/*    */ 
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class SimpleLayout extends Layout
/*    */ {
/* 38 */   StringBuffer sbuf = new StringBuffer(128);
/*    */ 
/*    */   public void activateOptions()
/*    */   {
/*    */   }
/*    */ 
/*    */   public String format(LoggingEvent event)
/*    */   {
/* 60 */     this.sbuf.setLength(0);
/* 61 */     this.sbuf.append(event.getLevel().toString());
/* 62 */     this.sbuf.append(" - ");
/* 63 */     this.sbuf.append(event.getRenderedMessage());
/* 64 */     this.sbuf.append(LINE_SEP);
/* 65 */     return this.sbuf.toString();
/*    */   }
/*    */ 
/*    */   public boolean ignoresThrowable()
/*    */   {
/* 76 */     return true;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.SimpleLayout
 * JD-Core Version:    0.6.2
 */