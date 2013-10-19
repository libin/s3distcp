/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Throwables;
/*     */ import com.google.common.collect.Lists;
/*     */ import java.util.ArrayList;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.locks.Condition;
/*     */ import java.util.concurrent.locks.ReentrantLock;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.GuardedBy;
/*     */ 
/*     */ @Beta
/*     */ public final class Monitor
/*     */ {
/*     */   private final boolean fair;
/*     */   private final ReentrantLock lock;
/*     */ 
/*     */   @GuardedBy("lock")
/* 268 */   private final ArrayList<Guard> activeGuards = Lists.newArrayListWithCapacity(1);
/*     */ 
/*     */   public Monitor()
/*     */   {
/* 276 */     this(false);
/*     */   }
/*     */ 
/*     */   public Monitor(boolean fair)
/*     */   {
/* 286 */     this.fair = fair;
/* 287 */     this.lock = new ReentrantLock(fair);
/*     */   }
/*     */ 
/*     */   public void enter()
/*     */   {
/* 294 */     this.lock.lock();
/*     */   }
/*     */ 
/*     */   public void enterInterruptibly()
/*     */     throws InterruptedException
/*     */   {
/* 301 */     this.lock.lockInterruptibly();
/*     */   }
/*     */ 
/*     */   public boolean enter(long time, TimeUnit unit)
/*     */   {
/* 310 */     ReentrantLock lock = this.lock;
/* 311 */     if ((!this.fair) && (lock.tryLock())) {
/* 312 */       return true;
/*     */     }
/* 314 */     long startNanos = System.nanoTime();
/* 315 */     long timeoutNanos = unit.toNanos(time);
/* 316 */     long remainingNanos = timeoutNanos;
/* 317 */     boolean interruptIgnored = false;
/*     */     try
/*     */     {
/* 321 */       return lock.tryLock(remainingNanos, TimeUnit.NANOSECONDS); } catch (InterruptedException ignored) {
/*     */       while (true) {
/* 323 */         interruptIgnored = true;
/* 324 */         remainingNanos = timeoutNanos - (System.nanoTime() - startNanos);
/*     */       }
/*     */     }
/*     */     finally {
/* 328 */       if (interruptIgnored)
/* 329 */         Thread.currentThread().interrupt();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean enterInterruptibly(long time, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 340 */     return this.lock.tryLock(time, unit);
/*     */   }
/*     */ 
/*     */   public boolean tryEnter()
/*     */   {
/* 351 */     return this.lock.tryLock();
/*     */   }
/*     */ 
/*     */   public void enterWhen(Guard guard)
/*     */     throws InterruptedException
/*     */   {
/* 358 */     if (guard.monitor != this) {
/* 359 */       throw new IllegalMonitorStateException();
/*     */     }
/* 361 */     ReentrantLock lock = this.lock;
/* 362 */     boolean reentrant = lock.isHeldByCurrentThread();
/* 363 */     boolean success = false;
/* 364 */     lock.lockInterruptibly();
/*     */     try {
/* 366 */       waitInterruptibly(guard, reentrant);
/* 367 */       success = true;
/*     */     } finally {
/* 369 */       if (!success)
/* 370 */         lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void enterWhenUninterruptibly(Guard guard)
/*     */   {
/* 379 */     if (guard.monitor != this) {
/* 380 */       throw new IllegalMonitorStateException();
/*     */     }
/* 382 */     ReentrantLock lock = this.lock;
/* 383 */     boolean reentrant = lock.isHeldByCurrentThread();
/* 384 */     boolean success = false;
/* 385 */     lock.lock();
/*     */     try {
/* 387 */       waitUninterruptibly(guard, reentrant);
/* 388 */       success = true;
/*     */     } finally {
/* 390 */       if (!success)
/* 391 */         lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean enterWhen(Guard guard, long time, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 404 */     if (guard.monitor != this) {
/* 405 */       throw new IllegalMonitorStateException();
/*     */     }
/* 407 */     ReentrantLock lock = this.lock;
/* 408 */     boolean reentrant = lock.isHeldByCurrentThread();
/*     */     long remainingNanos;
/*     */     long remainingNanos;
/* 410 */     if ((!this.fair) && (lock.tryLock())) {
/* 411 */       remainingNanos = unit.toNanos(time);
/*     */     } else {
/* 413 */       long startNanos = System.nanoTime();
/* 414 */       if (!lock.tryLock(time, unit)) {
/* 415 */         return false;
/*     */       }
/* 417 */       remainingNanos = unit.toNanos(time) - (System.nanoTime() - startNanos);
/*     */     }
/* 419 */     boolean satisfied = false;
/*     */     try {
/* 421 */       satisfied = waitInterruptibly(guard, remainingNanos, reentrant);
/*     */     } finally {
/* 423 */       if (!satisfied) {
/* 424 */         lock.unlock();
/*     */       }
/*     */     }
/* 427 */     return satisfied;
/*     */   }
/*     */ 
/*     */   public boolean enterWhenUninterruptibly(Guard guard, long time, TimeUnit unit)
/*     */   {
/* 437 */     if (guard.monitor != this) {
/* 438 */       throw new IllegalMonitorStateException();
/*     */     }
/* 440 */     ReentrantLock lock = this.lock;
/* 441 */     boolean reentrant = lock.isHeldByCurrentThread();
/* 442 */     boolean interruptIgnored = false;
/*     */     try
/*     */     {
/*     */       long remainingNanos;
/*     */       long remainingNanos;
/* 445 */       if ((!this.fair) && (lock.tryLock())) {
/* 446 */         remainingNanos = unit.toNanos(time);
/*     */       } else {
/* 448 */         long startNanos = System.nanoTime();
/* 449 */         long timeoutNanos = unit.toNanos(time);
/* 450 */         remainingNanos = timeoutNanos;
/*     */         while (true) {
/*     */           try {
/* 453 */             if (lock.tryLock(remainingNanos, TimeUnit.NANOSECONDS))
/*     */             {
/* 461 */               remainingNanos = timeoutNanos - (System.nanoTime() - startNanos); break;
/*     */             }
/* 456 */             boolean bool2 = false;
/*     */ 
/* 461 */             remainingNanos = timeoutNanos - (System.nanoTime() - startNanos);
/*     */ 
/* 476 */             return bool2;
/*     */           }
/*     */           catch (InterruptedException ignored)
/*     */           {
/* 459 */             interruptIgnored = true;
/*     */           } finally {
/* 461 */             remainingNanos = timeoutNanos - (System.nanoTime() - startNanos);
/*     */           }
/*     */         }
/*     */       }
/* 465 */       boolean satisfied = false;
/*     */       try {
/* 467 */         satisfied = waitUninterruptibly(guard, remainingNanos, reentrant);
/*     */       } finally {
/* 469 */         if (!satisfied) {
/* 470 */           lock.unlock();
/*     */         }
/*     */       }
/* 473 */       return satisfied;
/*     */     } finally {
/* 475 */       if (interruptIgnored)
/* 476 */         Thread.currentThread().interrupt();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean enterIf(Guard guard)
/*     */   {
/* 488 */     if (guard.monitor != this) {
/* 489 */       throw new IllegalMonitorStateException();
/*     */     }
/* 491 */     ReentrantLock lock = this.lock;
/* 492 */     lock.lock();
/* 493 */     boolean satisfied = false;
/*     */     try {
/* 495 */       satisfied = guard.isSatisfied();
/*     */     } finally {
/* 497 */       if (!satisfied) {
/* 498 */         lock.unlock();
/*     */       }
/*     */     }
/* 501 */     return satisfied;
/*     */   }
/*     */ 
/*     */   public boolean enterIfInterruptibly(Guard guard)
/*     */     throws InterruptedException
/*     */   {
/* 511 */     if (guard.monitor != this) {
/* 512 */       throw new IllegalMonitorStateException();
/*     */     }
/* 514 */     ReentrantLock lock = this.lock;
/* 515 */     lock.lockInterruptibly();
/* 516 */     boolean satisfied = false;
/*     */     try {
/* 518 */       satisfied = guard.isSatisfied();
/*     */     } finally {
/* 520 */       if (!satisfied) {
/* 521 */         lock.unlock();
/*     */       }
/*     */     }
/* 524 */     return satisfied;
/*     */   }
/*     */ 
/*     */   public boolean enterIf(Guard guard, long time, TimeUnit unit)
/*     */   {
/* 534 */     if (guard.monitor != this) {
/* 535 */       throw new IllegalMonitorStateException();
/*     */     }
/* 537 */     ReentrantLock lock = this.lock;
/* 538 */     if (!enter(time, unit)) {
/* 539 */       return false;
/*     */     }
/* 541 */     boolean satisfied = false;
/*     */     try {
/* 543 */       satisfied = guard.isSatisfied();
/*     */     } finally {
/* 545 */       if (!satisfied) {
/* 546 */         lock.unlock();
/*     */       }
/*     */     }
/* 549 */     return satisfied;
/*     */   }
/*     */ 
/*     */   public boolean enterIfInterruptibly(Guard guard, long time, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 560 */     if (guard.monitor != this) {
/* 561 */       throw new IllegalMonitorStateException();
/*     */     }
/* 563 */     ReentrantLock lock = this.lock;
/* 564 */     if (!lock.tryLock(time, unit)) {
/* 565 */       return false;
/*     */     }
/* 567 */     boolean satisfied = false;
/*     */     try {
/* 569 */       satisfied = guard.isSatisfied();
/*     */     } finally {
/* 571 */       if (!satisfied) {
/* 572 */         lock.unlock();
/*     */       }
/*     */     }
/* 575 */     return satisfied;
/*     */   }
/*     */ 
/*     */   public boolean tryEnterIf(Guard guard)
/*     */   {
/* 587 */     if (guard.monitor != this) {
/* 588 */       throw new IllegalMonitorStateException();
/*     */     }
/* 590 */     ReentrantLock lock = this.lock;
/* 591 */     if (!lock.tryLock()) {
/* 592 */       return false;
/*     */     }
/* 594 */     boolean satisfied = false;
/*     */     try {
/* 596 */       satisfied = guard.isSatisfied();
/*     */     } finally {
/* 598 */       if (!satisfied) {
/* 599 */         lock.unlock();
/*     */       }
/*     */     }
/* 602 */     return satisfied;
/*     */   }
/*     */ 
/*     */   public void waitFor(Guard guard)
/*     */     throws InterruptedException
/*     */   {
/* 610 */     if (guard.monitor != this) {
/* 611 */       throw new IllegalMonitorStateException();
/*     */     }
/* 613 */     if (!this.lock.isHeldByCurrentThread()) {
/* 614 */       throw new IllegalMonitorStateException();
/*     */     }
/* 616 */     waitInterruptibly(guard, true);
/*     */   }
/*     */ 
/*     */   public void waitForUninterruptibly(Guard guard)
/*     */   {
/* 624 */     if (guard.monitor != this) {
/* 625 */       throw new IllegalMonitorStateException();
/*     */     }
/* 627 */     if (!this.lock.isHeldByCurrentThread()) {
/* 628 */       throw new IllegalMonitorStateException();
/*     */     }
/* 630 */     waitUninterruptibly(guard, true);
/*     */   }
/*     */ 
/*     */   public boolean waitFor(Guard guard, long time, TimeUnit unit)
/*     */     throws InterruptedException
/*     */   {
/* 640 */     if (guard.monitor != this) {
/* 641 */       throw new IllegalMonitorStateException();
/*     */     }
/* 643 */     if (!this.lock.isHeldByCurrentThread()) {
/* 644 */       throw new IllegalMonitorStateException();
/*     */     }
/* 646 */     return waitInterruptibly(guard, unit.toNanos(time), true);
/*     */   }
/*     */ 
/*     */   public boolean waitForUninterruptibly(Guard guard, long time, TimeUnit unit)
/*     */   {
/* 656 */     if (guard.monitor != this) {
/* 657 */       throw new IllegalMonitorStateException();
/*     */     }
/* 659 */     if (!this.lock.isHeldByCurrentThread()) {
/* 660 */       throw new IllegalMonitorStateException();
/*     */     }
/* 662 */     return waitUninterruptibly(guard, unit.toNanos(time), true);
/*     */   }
/*     */ 
/*     */   public void leave()
/*     */   {
/* 669 */     ReentrantLock lock = this.lock;
/* 670 */     if (!lock.isHeldByCurrentThread())
/* 671 */       throw new IllegalMonitorStateException();
/*     */     try
/*     */     {
/* 674 */       signalConditionsOfSatisfiedGuards(null);
/*     */     } finally {
/* 676 */       lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isFair()
/*     */   {
/* 684 */     return this.lock.isFair();
/*     */   }
/*     */ 
/*     */   public boolean isOccupied()
/*     */   {
/* 692 */     return this.lock.isLocked();
/*     */   }
/*     */ 
/*     */   public boolean isOccupiedByCurrentThread()
/*     */   {
/* 700 */     return this.lock.isHeldByCurrentThread();
/*     */   }
/*     */ 
/*     */   public int getOccupiedDepth()
/*     */   {
/* 708 */     return this.lock.getHoldCount();
/*     */   }
/*     */ 
/*     */   public int getQueueLength()
/*     */   {
/* 718 */     return this.lock.getQueueLength();
/*     */   }
/*     */ 
/*     */   public boolean hasQueuedThreads()
/*     */   {
/* 728 */     return this.lock.hasQueuedThreads();
/*     */   }
/*     */ 
/*     */   public boolean hasQueuedThread(Thread thread)
/*     */   {
/* 738 */     return this.lock.hasQueuedThread(thread);
/*     */   }
/*     */ 
/*     */   public boolean hasWaiters(Guard guard)
/*     */   {
/* 748 */     if (guard.monitor != this) {
/* 749 */       throw new IllegalMonitorStateException();
/*     */     }
/* 751 */     this.lock.lock();
/*     */     try {
/* 753 */       return guard.waiterCount > 0;
/*     */     } finally {
/* 755 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getWaitQueueLength(Guard guard)
/*     */   {
/* 766 */     if (guard.monitor != this) {
/* 767 */       throw new IllegalMonitorStateException();
/*     */     }
/* 769 */     this.lock.lock();
/*     */     try {
/* 771 */       return guard.waiterCount;
/*     */     } finally {
/* 773 */       this.lock.unlock();
/*     */     }
/*     */   }
/*     */ 
/*     */   @GuardedBy("lock")
/*     */   private void signalConditionsOfSatisfiedGuards(@Nullable Guard interruptedGuard) {
/* 779 */     ArrayList guards = this.activeGuards;
/* 780 */     int guardCount = guards.size();
/*     */     try {
/* 782 */       for (int i = 0; i < guardCount; i++) {
/* 783 */         Guard guard = (Guard)guards.get(i);
/* 784 */         if ((guard != interruptedGuard) || (guard.waiterCount != 1))
/*     */         {
/* 789 */           if (guard.isSatisfied()) {
/* 790 */             guard.condition.signal();
/* 791 */             return;
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (Throwable throwable) { for (int i = 0; i < guardCount; i++) {
/* 796 */         Guard guard = (Guard)guards.get(i);
/* 797 */         guard.condition.signalAll();
/*     */       }
/* 799 */       throw Throwables.propagate(throwable); }
/*     */   }
/*     */ 
/*     */   @GuardedBy("lock")
/*     */   private void incrementWaiters(Guard guard)
/*     */   {
/* 805 */     int waiters = guard.waiterCount++;
/* 806 */     if (waiters == 0)
/* 807 */       this.activeGuards.add(guard);
/*     */   }
/*     */ 
/*     */   @GuardedBy("lock")
/*     */   private void decrementWaiters(Guard guard)
/*     */   {
/* 813 */     int waiters = --guard.waiterCount;
/* 814 */     if (waiters == 0)
/* 815 */       this.activeGuards.remove(guard);
/*     */   }
/*     */ 
/*     */   @GuardedBy("lock")
/*     */   private void waitInterruptibly(Guard guard, boolean signalBeforeWaiting)
/*     */     throws InterruptedException
/*     */   {
/* 822 */     if (!guard.isSatisfied()) {
/* 823 */       if (signalBeforeWaiting) {
/* 824 */         signalConditionsOfSatisfiedGuards(null);
/*     */       }
/* 826 */       incrementWaiters(guard);
/*     */       try {
/* 828 */         Condition condition = guard.condition;
/*     */         do
/*     */           try {
/* 831 */             condition.await();
/*     */           } catch (InterruptedException interrupt) {
/*     */             try {
/*     */             }
/*     */             catch (Throwable throwable) {
/* 836 */               Thread.currentThread().interrupt();
/* 837 */               throw Throwables.propagate(throwable);
/*     */             }
/* 839 */             throw interrupt;
/*     */           }
/* 841 */         while (!guard.isSatisfied());
/*     */       } finally {
/* 843 */         decrementWaiters(guard);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @GuardedBy("lock")
/*     */   private void waitUninterruptibly(Guard guard, boolean signalBeforeWaiting) {
/* 850 */     if (!guard.isSatisfied()) {
/* 851 */       if (signalBeforeWaiting) {
/* 852 */         signalConditionsOfSatisfiedGuards(null);
/*     */       }
/* 854 */       incrementWaiters(guard);
/*     */       try {
/* 856 */         Condition condition = guard.condition;
/*     */         do
/* 858 */           condition.awaitUninterruptibly();
/* 859 */         while (!guard.isSatisfied());
/*     */       } finally {
/* 861 */         decrementWaiters(guard);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   @GuardedBy("lock")
/*     */   private boolean waitInterruptibly(Guard guard, long remainingNanos, boolean signalBeforeWaiting) throws InterruptedException
/*     */   {
/* 869 */     if (!guard.isSatisfied()) {
/* 870 */       if (signalBeforeWaiting) {
/* 871 */         signalConditionsOfSatisfiedGuards(null);
/*     */       }
/* 873 */       incrementWaiters(guard);
/*     */       try {
/* 875 */         Condition condition = guard.condition;
/*     */         do {
/* 877 */           if (remainingNanos <= 0L)
/* 878 */             return false;
/*     */           try
/*     */           {
/* 881 */             remainingNanos = condition.awaitNanos(remainingNanos);
/*     */           } catch (InterruptedException interrupt) {
/*     */             try {
/*     */             }
/*     */             catch (Throwable throwable) {
/* 886 */               Thread.currentThread().interrupt();
/* 887 */               throw Throwables.propagate(throwable);
/*     */             }
/* 889 */             throw interrupt;
/*     */           }
/*     */         }
/* 891 */         while (!guard.isSatisfied());
/*     */       } finally {
/* 893 */         decrementWaiters(guard);
/*     */       }
/*     */     }
/* 896 */     return true;
/*     */   }
/*     */ 
/*     */   @GuardedBy("lock")
/*     */   private boolean waitUninterruptibly(Guard guard, long timeoutNanos, boolean signalBeforeWaiting)
/*     */   {
/* 902 */     if (!guard.isSatisfied()) {
/* 903 */       long startNanos = System.nanoTime();
/* 904 */       if (signalBeforeWaiting) {
/* 905 */         signalConditionsOfSatisfiedGuards(null);
/*     */       }
/* 907 */       boolean interruptIgnored = false;
/*     */       try {
/* 909 */         incrementWaiters(guard);
/*     */         try {
/* 911 */           Condition condition = guard.condition;
/* 912 */           long remainingNanos = timeoutNanos;
/*     */           do {
/* 914 */             if (remainingNanos <= 0L) {
/* 915 */               boolean bool1 = false;
/*     */ 
/* 931 */               decrementWaiters(guard);
/*     */ 
/* 935 */               return bool1;
/*     */             }
/*     */             try
/*     */             {
/* 918 */               remainingNanos = condition.awaitNanos(remainingNanos);
/*     */             } catch (InterruptedException ignored) {
/*     */               try {
/*     */               }
/*     */               catch (Throwable throwable) {
/* 923 */                 Thread.currentThread().interrupt();
/* 924 */                 throw Throwables.propagate(throwable);
/*     */               }
/* 926 */               interruptIgnored = true;
/* 927 */               remainingNanos = timeoutNanos - (System.nanoTime() - startNanos);
/*     */             }
/*     */           }
/* 929 */           while (!guard.isSatisfied());
/*     */         } finally {
/* 931 */           decrementWaiters(guard);
/*     */         }
/*     */       } finally {
/* 934 */         if (interruptIgnored) {
/* 935 */           Thread.currentThread().interrupt();
/*     */         }
/*     */       }
/*     */     }
/* 939 */     return true;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static abstract class Guard
/*     */   {
/*     */     final Monitor monitor;
/*     */     final Condition condition;
/*     */ 
/*     */     @GuardedBy("monitor.lock")
/* 224 */     int waiterCount = 0;
/*     */ 
/*     */     protected Guard(Monitor monitor)
/*     */     {
/* 228 */       this.monitor = ((Monitor)Preconditions.checkNotNull(monitor, "monitor"));
/* 229 */       this.condition = monitor.lock.newCondition();
/*     */     }
/*     */ 
/*     */     public abstract boolean isSatisfied();
/*     */ 
/*     */     public final boolean equals(Object other)
/*     */     {
/* 242 */       return this == other;
/*     */     }
/*     */ 
/*     */     public final int hashCode()
/*     */     {
/* 248 */       return super.hashCode();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.Monitor
 * JD-Core Version:    0.6.2
 */