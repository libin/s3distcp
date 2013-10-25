/*    */ package org.apache.log4j.jmx;
/*    */ 
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ class MethodUnion
/*    */ {
/*    */   Method readMethod;
/*    */   Method writeMethod;
/*    */ 
/*    */   MethodUnion(Method readMethod, Method writeMethod)
/*    */   {
/* 28 */     this.readMethod = readMethod;
/* 29 */     this.writeMethod = writeMethod;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.jmx.MethodUnion
 * JD-Core Version:    0.6.2
 */