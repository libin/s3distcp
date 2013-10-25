/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ 
/*     */ @Beta
/*     */ public abstract interface Service
/*     */ {
/*     */   public abstract ListenableFuture<State> start();
/*     */ 
/*     */   public abstract State startAndWait();
/*     */ 
/*     */   public abstract boolean isRunning();
/*     */ 
/*     */   public abstract State state();
/*     */ 
/*     */   public abstract ListenableFuture<State> stop();
/*     */ 
/*     */   public abstract State stopAndWait();
/*     */ 
/*     */   @Beta
/*     */   public static enum State
/*     */   {
/* 154 */     NEW, 
/*     */ 
/* 159 */     STARTING, 
/*     */ 
/* 164 */     RUNNING, 
/*     */ 
/* 169 */     STOPPING, 
/*     */ 
/* 175 */     TERMINATED, 
/*     */ 
/* 181 */     FAILED;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.Service
 * JD-Core Version:    0.6.2
 */