/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Queue;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingQueue<E> extends ForwardingCollection<E>
/*     */   implements Queue<E>
/*     */ {
/*     */   protected abstract Queue<E> delegate();
/*     */ 
/*     */   public boolean offer(E o)
/*     */   {
/*  56 */     return delegate().offer(o);
/*     */   }
/*     */ 
/*     */   public E poll()
/*     */   {
/*  61 */     return delegate().poll();
/*     */   }
/*     */ 
/*     */   public E remove()
/*     */   {
/*  66 */     return delegate().remove();
/*     */   }
/*     */ 
/*     */   public E peek()
/*     */   {
/*  71 */     return delegate().peek();
/*     */   }
/*     */ 
/*     */   public E element()
/*     */   {
/*  76 */     return delegate().element();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardOffer(E e)
/*     */   {
/*     */     try
/*     */     {
/*  88 */       return add(e); } catch (IllegalStateException caught) {
/*     */     }
/*  90 */     return false;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected E standardPeek()
/*     */   {
/*     */     try
/*     */     {
/* 103 */       return element(); } catch (NoSuchElementException caught) {
/*     */     }
/* 105 */     return null;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected E standardPoll()
/*     */   {
/*     */     try
/*     */     {
/* 118 */       return remove(); } catch (NoSuchElementException caught) {
/*     */     }
/* 120 */     return null;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingQueue
 * JD-Core Version:    0.6.2
 */