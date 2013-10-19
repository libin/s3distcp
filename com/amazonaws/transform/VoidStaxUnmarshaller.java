/*    */ package com.amazonaws.transform;
/*    */ 
/*    */ import javax.xml.stream.events.XMLEvent;
/*    */ 
/*    */ public class VoidStaxUnmarshaller<T>
/*    */   implements Unmarshaller<T, StaxUnmarshallerContext>
/*    */ {
/*    */   public T unmarshall(StaxUnmarshallerContext context)
/*    */     throws Exception
/*    */   {
/* 23 */     while (!context.nextEvent().isEndDocument());
/* 24 */     return null;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.VoidStaxUnmarshaller
 * JD-Core Version:    0.6.2
 */