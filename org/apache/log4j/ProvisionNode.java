/*    */ package org.apache.log4j;
/*    */ 
/*    */ import java.util.Vector;
/*    */ 
/*    */ class ProvisionNode extends Vector
/*    */ {
/*    */   private static final long serialVersionUID = -4479121426311014469L;
/*    */ 
/*    */   ProvisionNode(Logger logger)
/*    */   {
/* 27 */     addElement(logger);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.ProvisionNode
 * JD-Core Version:    0.6.2
 */