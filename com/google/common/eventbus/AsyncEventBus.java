/*    */ package com.google.common.eventbus;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import java.util.concurrent.ConcurrentLinkedQueue;
/*    */ import java.util.concurrent.Executor;
/*    */ 
/*    */ @Beta
/*    */ public class AsyncEventBus extends EventBus
/*    */ {
/*    */   private final Executor executor;
/* 36 */   private final ConcurrentLinkedQueue<EventBus.EventWithHandler> eventsToDispatch = new ConcurrentLinkedQueue();
/*    */ 
/*    */   public AsyncEventBus(String identifier, Executor executor)
/*    */   {
/* 49 */     super(identifier);
/* 50 */     this.executor = executor;
/*    */   }
/*    */ 
/*    */   public AsyncEventBus(Executor executor)
/*    */   {
/* 62 */     this.executor = executor;
/*    */   }
/*    */ 
/*    */   protected void enqueueEvent(Object event, EventHandler handler)
/*    */   {
/* 67 */     this.eventsToDispatch.offer(new EventBus.EventWithHandler(event, handler));
/*    */   }
/*    */ 
/*    */   protected void dispatchQueuedEvents()
/*    */   {
/*    */     while (true)
/*    */     {
/* 77 */       EventBus.EventWithHandler eventWithHandler = (EventBus.EventWithHandler)this.eventsToDispatch.poll();
/* 78 */       if (eventWithHandler == null)
/*    */       {
/*    */         break;
/*    */       }
/* 82 */       dispatch(eventWithHandler.event, eventWithHandler.handler);
/*    */     }
/*    */   }
/*    */ 
/*    */   protected void dispatch(final Object event, final EventHandler handler)
/*    */   {
/* 91 */     this.executor.execute(new Runnable()
/*    */     {
/*    */       public void run()
/*    */       {
/* 95 */         AsyncEventBus.this.dispatch(event, handler);
/*    */       }
/*    */     });
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.eventbus.AsyncEventBus
 * JD-Core Version:    0.6.2
 */