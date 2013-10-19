/*    */ package com.google.common.eventbus;
/*    */ 
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.lang.reflect.Method;
/*    */ 
/*    */ class SynchronizedEventHandler extends EventHandler
/*    */ {
/*    */   public SynchronizedEventHandler(Object target, Method method)
/*    */   {
/* 40 */     super(target, method);
/*    */   }
/*    */ 
/*    */   public synchronized void handleEvent(Object event) throws InvocationTargetException
/*    */   {
/* 45 */     super.handleEvent(event);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.eventbus.SynchronizedEventHandler
 * JD-Core Version:    0.6.2
 */