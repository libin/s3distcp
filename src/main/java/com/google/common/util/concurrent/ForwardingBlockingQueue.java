/*    */ package com.google.common.util.concurrent;
/*    */ 
/*    */ import com.google.common.collect.ForwardingQueue;
/*    */ import java.util.Collection;
/*    */ import java.util.concurrent.BlockingQueue;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public abstract class ForwardingBlockingQueue<E> extends ForwardingQueue<E>
/*    */   implements BlockingQueue<E>
/*    */ {
/*    */   protected abstract BlockingQueue<E> delegate();
/*    */ 
/*    */   public int drainTo(Collection<? super E> c, int maxElements)
/*    */   {
/* 46 */     return delegate().drainTo(c, maxElements);
/*    */   }
/*    */ 
/*    */   public int drainTo(Collection<? super E> c) {
/* 50 */     return delegate().drainTo(c);
/*    */   }
/*    */ 
/*    */   public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException
/*    */   {
/* 55 */     return delegate().offer(e, timeout, unit);
/*    */   }
/*    */ 
/*    */   public E poll(long timeout, TimeUnit unit) throws InterruptedException
/*    */   {
/* 60 */     return delegate().poll(timeout, unit);
/*    */   }
/*    */ 
/*    */   public void put(E e) throws InterruptedException {
/* 64 */     delegate().put(e);
/*    */   }
/*    */ 
/*    */   public int remainingCapacity() {
/* 68 */     return delegate().remainingCapacity();
/*    */   }
/*    */ 
/*    */   public E take() throws InterruptedException {
/* 72 */     return delegate().take();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ForwardingBlockingQueue
 * JD-Core Version:    0.6.2
 */