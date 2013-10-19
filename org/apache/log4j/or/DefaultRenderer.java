/*    */ package org.apache.log4j.or;
/*    */ 
/*    */ class DefaultRenderer
/*    */   implements ObjectRenderer
/*    */ {
/*    */   public String doRender(Object o)
/*    */   {
/*    */     try
/*    */     {
/* 37 */       return o.toString();
/*    */     } catch (Exception ex) {
/* 39 */       return ex.toString();
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.or.DefaultRenderer
 * JD-Core Version:    0.6.2
 */