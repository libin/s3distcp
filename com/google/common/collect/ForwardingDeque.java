/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ @Beta
/*     */ public abstract class ForwardingDeque<E> extends ForwardingQueue<E>
/*     */   implements Deque<E>
/*     */ {
/*     */   protected abstract Deque<E> delegate();
/*     */ 
/*     */   public void addFirst(E e)
/*     */   {
/*  54 */     delegate().addFirst(e);
/*     */   }
/*     */ 
/*     */   public void addLast(E e)
/*     */   {
/*  59 */     delegate().addLast(e);
/*     */   }
/*     */ 
/*     */   public Iterator<E> descendingIterator()
/*     */   {
/*  64 */     return delegate().descendingIterator();
/*     */   }
/*     */ 
/*     */   public E getFirst()
/*     */   {
/*  69 */     return delegate().getFirst();
/*     */   }
/*     */ 
/*     */   public E getLast()
/*     */   {
/*  74 */     return delegate().getLast();
/*     */   }
/*     */ 
/*     */   public boolean offerFirst(E e)
/*     */   {
/*  79 */     return delegate().offerFirst(e);
/*     */   }
/*     */ 
/*     */   public boolean offerLast(E e)
/*     */   {
/*  84 */     return delegate().offerLast(e);
/*     */   }
/*     */ 
/*     */   public E peekFirst()
/*     */   {
/*  89 */     return delegate().peekFirst();
/*     */   }
/*     */ 
/*     */   public E peekLast()
/*     */   {
/*  94 */     return delegate().peekLast();
/*     */   }
/*     */ 
/*     */   public E pollFirst()
/*     */   {
/*  99 */     return delegate().pollFirst();
/*     */   }
/*     */ 
/*     */   public E pollLast()
/*     */   {
/* 104 */     return delegate().pollLast();
/*     */   }
/*     */ 
/*     */   public E pop()
/*     */   {
/* 109 */     return delegate().pop();
/*     */   }
/*     */ 
/*     */   public void push(E e)
/*     */   {
/* 114 */     delegate().push(e);
/*     */   }
/*     */ 
/*     */   public E removeFirst()
/*     */   {
/* 119 */     return delegate().removeFirst();
/*     */   }
/*     */ 
/*     */   public E removeLast()
/*     */   {
/* 124 */     return delegate().removeLast();
/*     */   }
/*     */ 
/*     */   public boolean removeFirstOccurrence(Object o)
/*     */   {
/* 129 */     return delegate().removeFirstOccurrence(o);
/*     */   }
/*     */ 
/*     */   public boolean removeLastOccurrence(Object o)
/*     */   {
/* 134 */     return delegate().removeLastOccurrence(o);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingDeque
 * JD-Core Version:    0.6.2
 */