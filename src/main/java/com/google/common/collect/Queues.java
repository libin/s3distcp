/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Collection;
/*     */ import java.util.PriorityQueue;
/*     */ import java.util.concurrent.ArrayBlockingQueue;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.LinkedBlockingDeque;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ import java.util.concurrent.PriorityBlockingQueue;
/*     */ import java.util.concurrent.SynchronousQueue;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ @Beta
/*     */ public final class Queues
/*     */ {
/*     */   public static <E> ArrayBlockingQueue<E> newArrayBlockingQueue(int capacity)
/*     */   {
/*  53 */     return new ArrayBlockingQueue(capacity);
/*     */   }
/*     */ 
/*     */   public static <E> ArrayDeque<E> newArrayDeque()
/*     */   {
/*  65 */     return new ArrayDeque();
/*     */   }
/*     */ 
/*     */   public static <E> ArrayDeque<E> newArrayDeque(Iterable<? extends E> elements)
/*     */   {
/*  76 */     if ((elements instanceof Collection)) {
/*  77 */       return new ArrayDeque(Collections2.cast(elements));
/*     */     }
/*  79 */     ArrayDeque deque = new ArrayDeque();
/*  80 */     Iterables.addAll(deque, elements);
/*  81 */     return deque;
/*     */   }
/*     */ 
/*     */   public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue()
/*     */   {
/*  92 */     return new ConcurrentLinkedQueue();
/*     */   }
/*     */ 
/*     */   public static <E> ConcurrentLinkedQueue<E> newConcurrentLinkedQueue(Iterable<? extends E> elements)
/*     */   {
/* 103 */     if ((elements instanceof Collection)) {
/* 104 */       return new ConcurrentLinkedQueue(Collections2.cast(elements));
/*     */     }
/* 106 */     ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
/* 107 */     Iterables.addAll(queue, elements);
/* 108 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque()
/*     */   {
/* 120 */     return new LinkedBlockingDeque();
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(int capacity)
/*     */   {
/* 132 */     return new LinkedBlockingDeque(capacity);
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingDeque<E> newLinkedBlockingDeque(Iterable<? extends E> elements)
/*     */   {
/* 143 */     if ((elements instanceof Collection)) {
/* 144 */       return new LinkedBlockingDeque(Collections2.cast(elements));
/*     */     }
/* 146 */     LinkedBlockingDeque deque = new LinkedBlockingDeque();
/* 147 */     Iterables.addAll(deque, elements);
/* 148 */     return deque;
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue()
/*     */   {
/* 159 */     return new LinkedBlockingQueue();
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(int capacity)
/*     */   {
/* 170 */     return new LinkedBlockingQueue(capacity);
/*     */   }
/*     */ 
/*     */   public static <E> LinkedBlockingQueue<E> newLinkedBlockingQueue(Iterable<? extends E> elements)
/*     */   {
/* 180 */     if ((elements instanceof Collection)) {
/* 181 */       return new LinkedBlockingQueue(Collections2.cast(elements));
/*     */     }
/* 183 */     LinkedBlockingQueue queue = new LinkedBlockingQueue();
/* 184 */     Iterables.addAll(queue, elements);
/* 185 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue()
/*     */   {
/* 198 */     return new PriorityBlockingQueue();
/*     */   }
/*     */ 
/*     */   public static <E> PriorityBlockingQueue<E> newPriorityBlockingQueue(Iterable<? extends E> elements)
/*     */   {
/* 209 */     if ((elements instanceof Collection)) {
/* 210 */       return new PriorityBlockingQueue(Collections2.cast(elements));
/*     */     }
/* 212 */     PriorityBlockingQueue queue = new PriorityBlockingQueue();
/* 213 */     Iterables.addAll(queue, elements);
/* 214 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E> PriorityQueue<E> newPriorityQueue()
/*     */   {
/* 225 */     return new PriorityQueue();
/*     */   }
/*     */ 
/*     */   public static <E> PriorityQueue<E> newPriorityQueue(Iterable<? extends E> elements)
/*     */   {
/* 235 */     if ((elements instanceof Collection)) {
/* 236 */       return new PriorityQueue(Collections2.cast(elements));
/*     */     }
/* 238 */     PriorityQueue queue = new PriorityQueue();
/* 239 */     Iterables.addAll(queue, elements);
/* 240 */     return queue;
/*     */   }
/*     */ 
/*     */   public static <E> SynchronousQueue<E> newSynchronousQueue()
/*     */   {
/* 251 */     return new SynchronousQueue();
/*     */   }
/*     */ 
/*     */   public static <E> int drain(BlockingQueue<E> q, Collection<? super E> buffer, int numElements, long timeout, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 269 */     Preconditions.checkNotNull(buffer);
/*     */ 
/* 275 */     long deadline = System.nanoTime() + unit.toNanos(timeout);
/* 276 */     int added = 0;
/* 277 */     while (added < numElements)
/*     */     {
/* 280 */       added += q.drainTo(buffer, numElements - added);
/* 281 */       if (added < numElements) {
/* 282 */         Object e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
/* 283 */         if (e == null) {
/*     */           break;
/*     */         }
/* 286 */         buffer.add(e);
/* 287 */         added++;
/*     */       }
/*     */     }
/* 290 */     return added;
/*     */   }
/*     */ 
/*     */   public static <E> int drainUninterruptibly(BlockingQueue<E> q, Collection<? super E> buffer, int numElements, long timeout, TimeUnit unit)
/*     */   {
/* 308 */     Preconditions.checkNotNull(buffer);
/* 309 */     long deadline = System.nanoTime() + unit.toNanos(timeout);
/* 310 */     int added = 0;
/* 311 */     boolean interrupted = false;
/*     */     try {
/* 313 */       while (added < numElements)
/*     */       {
/* 316 */         added += q.drainTo(buffer, numElements - added);
/* 317 */         if (added < numElements) {
/*     */           Object e;
/*     */           while (true) {
/*     */             try {
/* 321 */               e = q.poll(deadline - System.nanoTime(), TimeUnit.NANOSECONDS);
/*     */             }
/*     */             catch (InterruptedException ex) {
/* 324 */               interrupted = true;
/*     */             }
/*     */           }
/* 327 */           if (e == null) {
/*     */             break;
/*     */           }
/* 330 */           buffer.add(e);
/* 331 */           added++;
/*     */         }
/*     */       }
/*     */     } finally {
/* 335 */       if (interrupted) {
/* 336 */         Thread.currentThread().interrupt();
/*     */       }
/*     */     }
/* 339 */     return added;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Queues
 * JD-Core Version:    0.6.2
 */