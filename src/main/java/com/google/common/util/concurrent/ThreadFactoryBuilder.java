/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.concurrent.Executors;
/*     */ import java.util.concurrent.ThreadFactory;
/*     */ import java.util.concurrent.atomic.AtomicLong;
/*     */ 
/*     */ public final class ThreadFactoryBuilder
/*     */ {
/*  46 */   private String nameFormat = null;
/*  47 */   private Boolean daemon = null;
/*  48 */   private Integer priority = null;
/*  49 */   private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
/*  50 */   private ThreadFactory backingThreadFactory = null;
/*     */ 
/*     */   public ThreadFactoryBuilder setNameFormat(String nameFormat)
/*     */   {
/*  68 */     String.format(nameFormat, new Object[] { Integer.valueOf(0) });
/*  69 */     this.nameFormat = nameFormat;
/*  70 */     return this;
/*     */   }
/*     */ 
/*     */   public ThreadFactoryBuilder setDaemon(boolean daemon)
/*     */   {
/*  81 */     this.daemon = Boolean.valueOf(daemon);
/*  82 */     return this;
/*     */   }
/*     */ 
/*     */   public ThreadFactoryBuilder setPriority(int priority)
/*     */   {
/*  95 */     Preconditions.checkArgument(priority >= 1, "Thread priority (%s) must be >= %s", new Object[] { Integer.valueOf(priority), Integer.valueOf(1) });
/*     */ 
/*  97 */     Preconditions.checkArgument(priority <= 10, "Thread priority (%s) must be <= %s", new Object[] { Integer.valueOf(priority), Integer.valueOf(10) });
/*     */ 
/*  99 */     this.priority = Integer.valueOf(priority);
/* 100 */     return this;
/*     */   }
/*     */ 
/*     */   public ThreadFactoryBuilder setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler uncaughtExceptionHandler)
/*     */   {
/* 113 */     this.uncaughtExceptionHandler = ((Thread.UncaughtExceptionHandler)Preconditions.checkNotNull(uncaughtExceptionHandler));
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   public ThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory)
/*     */   {
/* 130 */     this.backingThreadFactory = ((ThreadFactory)Preconditions.checkNotNull(backingThreadFactory));
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   public ThreadFactory build()
/*     */   {
/* 143 */     return build(this);
/*     */   }
/*     */ 
/*     */   private static ThreadFactory build(ThreadFactoryBuilder builder) {
/* 147 */     final String nameFormat = builder.nameFormat;
/* 148 */     final Boolean daemon = builder.daemon;
/* 149 */     final Integer priority = builder.priority;
/* 150 */     final Thread.UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
/*     */ 
/* 152 */     ThreadFactory backingThreadFactory = builder.backingThreadFactory != null ? builder.backingThreadFactory : Executors.defaultThreadFactory();
/*     */ 
/* 156 */     final AtomicLong count = nameFormat != null ? new AtomicLong(0L) : null;
/* 157 */     return new ThreadFactory() {
/*     */       public Thread newThread(Runnable runnable) {
/* 159 */         Thread thread = this.val$backingThreadFactory.newThread(runnable);
/* 160 */         if (nameFormat != null) {
/* 161 */           thread.setName(String.format(nameFormat, new Object[] { Long.valueOf(count.getAndIncrement()) }));
/*     */         }
/* 163 */         if (daemon != null) {
/* 164 */           thread.setDaemon(daemon.booleanValue());
/*     */         }
/* 166 */         if (priority != null) {
/* 167 */           thread.setPriority(priority.intValue());
/*     */         }
/* 169 */         if (uncaughtExceptionHandler != null) {
/* 170 */           thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
/*     */         }
/* 172 */         return thread;
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ThreadFactoryBuilder
 * JD-Core Version:    0.6.2
 */