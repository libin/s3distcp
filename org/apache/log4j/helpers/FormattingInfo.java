/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ public class FormattingInfo
/*    */ {
/* 31 */   int min = -1;
/* 32 */   int max = 2147483647;
/* 33 */   boolean leftAlign = false;
/*    */ 
/*    */   void reset() {
/* 36 */     this.min = -1;
/* 37 */     this.max = 2147483647;
/* 38 */     this.leftAlign = false;
/*    */   }
/*    */ 
/*    */   void dump() {
/* 42 */     LogLog.debug("min=" + this.min + ", max=" + this.max + ", leftAlign=" + this.leftAlign);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.FormattingInfo
 * JD-Core Version:    0.6.2
 */