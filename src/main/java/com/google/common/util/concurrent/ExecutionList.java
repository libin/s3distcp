/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.util.Queue;
/*     */ import java.util.concurrent.Executor;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ public final class ExecutionList
/*     */ {
/*  48 */   private static final Logger log = Logger.getLogger(ExecutionList.class.getName());
/*     */ 
/*  52 */   private final Queue<RunnableExecutorPair> runnables = Lists.newLinkedList();
/*     */ 
/*  56 */   private boolean executed = false;
/*     */ 
/*     */   public void add(Runnable runnable, Executor executor)
/*     */   {
/*  84 */     Preconditions.checkNotNull(runnable, "Runnable was null.");
/*  85 */     Preconditions.checkNotNull(executor, "Executor was null.");
/*     */ 
/*  87 */     boolean executeImmediate = false;
/*     */ 
/*  92 */     synchronized (this.runnables) {
/*  93 */       if (!this.executed)
/*  94 */         this.runnables.add(new RunnableExecutorPair(runnable, executor));
/*     */       else {
/*  96 */         executeImmediate = true;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 104 */     if (executeImmediate)
/* 105 */       new RunnableExecutorPair(runnable, executor).execute();
/*     */   }
/*     */ 
/*     */   public void execute()
/*     */   {
/* 124 */     synchronized (this.runnables) {
/* 125 */       if (this.executed) {
/* 126 */         return;
/*     */       }
/* 128 */       this.executed = true;
/*     */     }
/*     */ 
/* 133 */     while (!this.runnables.isEmpty())
/* 134 */       ((RunnableExecutorPair)this.runnables.poll()).execute();
/*     */   }
/*     */ 
/*     */   private static class RunnableExecutorPair {
/*     */     final Runnable runnable;
/*     */     final Executor executor;
/*     */ 
/*     */     RunnableExecutorPair(Runnable runnable, Executor executor) {
/* 143 */       this.runnable = runnable;
/* 144 */       this.executor = executor;
/*     */     }
/*     */ 
/*     */     void execute() {
/*     */       try {
/* 149 */         this.executor.execute(this.runnable);
/*     */       }
/*     */       catch (RuntimeException e)
/*     */       {
/* 154 */         ExecutionList.log.log(Level.SEVERE, "RuntimeException while executing runnable " + this.runnable + " with executor " + this.executor, e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ExecutionList
 * JD-Core Version:    0.6.2
 */