/*    */ package org.apache.log4j.varia;
/*    */ 
/*    */ import org.apache.log4j.spi.Filter;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class DenyAllFilter extends Filter
/*    */ {
/*    */   /** @deprecated */
/*    */   public String[] getOptionStrings()
/*    */   {
/* 46 */     return null;
/*    */   }
/*    */ 
/*    */   /** @deprecated */
/*    */   public void setOption(String key, String value)
/*    */   {
/*    */   }
/*    */ 
/*    */   public int decide(LoggingEvent event)
/*    */   {
/* 69 */     return -1;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.varia.DenyAllFilter
 * JD-Core Version:    0.6.2
 */