/*     */ package com.amazon.external.elasticmapreduce.s3distcp;
/*     */ 
/*     */ import java.util.concurrent.Executor;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class SimpleExecutor
/*     */   implements Executor
/*     */ {
/*  30 */   private static final Log LOG = LogFactory.getLog(Worker.class);
/*     */   protected boolean closed;
/*     */   protected int tail;
/*     */   protected int head;
/*     */   protected Exception lastException;
/*     */   protected Runnable[] queue;
/*     */   protected Thread[] workers;
/*     */ 
/*     */   public SimpleExecutor(int queueSize, int workerSize)
/*     */   {
/*  81 */     this.queue = new Runnable[queueSize + 1];
/*  82 */     this.workers = new Thread[workerSize];
/*  83 */     this.head = 0;
/*  84 */     this.tail = 0;
/*  85 */     this.closed = false;
/*  86 */     this.lastException = null;
/*  87 */     startWorkers();
/*     */   }
/*     */ 
/*     */   public synchronized void registerException(Exception e) {
/*  91 */     this.lastException = e;
/*     */   }
/*     */ 
/*     */   public synchronized void assertNoExceptions() {
/*  95 */     if (this.lastException != null)
/*  96 */       throw new RuntimeException("Some tasks in remote executor failed", this.lastException);
/*     */   }
/*     */ 
/*     */   private void startWorkers()
/*     */   {
/* 101 */     for (int i = 0; i < this.workers.length; i++) {
/* 102 */       this.workers[i] = new Thread(new Worker(this));
/* 103 */       this.workers[i].start();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/* 109 */     synchronized (this) {
/* 110 */       this.closed = true;
/* 111 */       notifyAll();
/*     */     }
/*     */ 
/* 114 */     for (int i = 0; i < this.workers.length; i++)
/*     */       try {
/* 116 */         this.workers[i].join();
/*     */       }
/*     */       catch (InterruptedException e) {
/* 119 */         LOG.error("Interrupted while waiting for workers", e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public synchronized boolean closed()
/*     */   {
/* 125 */     return this.closed;
/*     */   }
/*     */ 
/*     */   public synchronized void execute(Runnable command)
/*     */   {
/*     */     try
/*     */     {
/* 132 */       while (isFull())
/* 133 */         wait();
/*     */     }
/*     */     catch (InterruptedException e) {
/* 136 */       throw new RuntimeException(e);
/*     */     }
/* 138 */     this.queue[this.head] = command;
/* 139 */     this.head = ((this.head + 1) % this.queue.length);
/* 140 */     notifyAll();
/*     */   }
/*     */ 
/*     */   synchronized boolean isEmpty() {
/* 144 */     return this.head == this.tail;
/*     */   }
/*     */ 
/*     */   synchronized boolean isFull() {
/* 148 */     return (this.head + 1) % this.queue.length == this.tail;
/*     */   }
/*     */ 
/*     */   synchronized int size() {
/* 152 */     int result = this.head - this.tail;
/* 153 */     if (result < 0) {
/* 154 */       return result + this.queue.length;
/*     */     }
/* 156 */     return result;
/*     */   }
/*     */ 
/*     */   public synchronized Runnable take() throws InterruptedException
/*     */   {
/* 161 */     while ((isEmpty()) && (!this.closed)) {
/* 162 */       wait(15000L);
/*     */     }
/* 164 */     if (!isEmpty()) {
/* 165 */       Runnable returnItem = this.queue[this.tail];
/* 166 */       this.tail = ((this.tail + 1) % this.queue.length);
/* 167 */       notifyAll();
/* 168 */       return returnItem;
/*     */     }
/* 170 */     return null;
/*     */   }
/*     */ 
/*     */   static class Worker
/*     */     implements Runnable
/*     */   {
/*     */     private final SimpleExecutor executor;
/*     */ 
/*     */     Worker(SimpleExecutor executor)
/*     */     {
/*  40 */       this.executor = executor;
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/*     */       try
/*     */       {
/*     */         Runnable job;
/*  47 */         while ((job = this.executor.take()) != null)
/*     */           try {
/*  49 */             job.run();
/*     */           } catch (RuntimeException e) {
/*  51 */             this.executor.registerException(e);
/*  52 */             SimpleExecutor.LOG.error("Worker task threw exception", e);
/*     */           }
/*     */       }
/*     */       catch (InterruptedException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.SimpleExecutor
 * JD-Core Version:    0.6.2
 */