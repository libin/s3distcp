/*    */ package com.google.common.eventbus;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ 
/*    */ @Beta
/*    */ public class DeadEvent
/*    */ {
/*    */   private final Object source;
/*    */   private final Object event;
/*    */ 
/*    */   public DeadEvent(Object source, Object event)
/*    */   {
/* 45 */     this.source = source;
/* 46 */     this.event = event;
/*    */   }
/*    */ 
/*    */   public Object getSource()
/*    */   {
/* 56 */     return this.source;
/*    */   }
/*    */ 
/*    */   public Object getEvent()
/*    */   {
/* 66 */     return this.event;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.eventbus.DeadEvent
 * JD-Core Version:    0.6.2
 */