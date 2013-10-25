/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.ListIterator;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingList<E> extends ForwardingCollection<E>
/*     */   implements List<E>
/*     */ {
/*     */   protected abstract List<E> delegate();
/*     */ 
/*     */   public void add(int index, E element)
/*     */   {
/*  66 */     delegate().add(index, element);
/*     */   }
/*     */ 
/*     */   public boolean addAll(int index, Collection<? extends E> elements)
/*     */   {
/*  71 */     return delegate().addAll(index, elements);
/*     */   }
/*     */ 
/*     */   public E get(int index)
/*     */   {
/*  76 */     return delegate().get(index);
/*     */   }
/*     */ 
/*     */   public int indexOf(Object element)
/*     */   {
/*  81 */     return delegate().indexOf(element);
/*     */   }
/*     */ 
/*     */   public int lastIndexOf(Object element)
/*     */   {
/*  86 */     return delegate().lastIndexOf(element);
/*     */   }
/*     */ 
/*     */   public ListIterator<E> listIterator()
/*     */   {
/*  91 */     return delegate().listIterator();
/*     */   }
/*     */ 
/*     */   public ListIterator<E> listIterator(int index)
/*     */   {
/*  96 */     return delegate().listIterator(index);
/*     */   }
/*     */ 
/*     */   public E remove(int index)
/*     */   {
/* 101 */     return delegate().remove(index);
/*     */   }
/*     */ 
/*     */   public E set(int index, E element)
/*     */   {
/* 106 */     return delegate().set(index, element);
/*     */   }
/*     */ 
/*     */   public List<E> subList(int fromIndex, int toIndex)
/*     */   {
/* 111 */     return delegate().subList(fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 115 */     return (object == this) || (delegate().equals(object));
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 119 */     return delegate().hashCode();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardAdd(E element)
/*     */   {
/* 131 */     add(size(), element);
/* 132 */     return true;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardAddAll(int index, Iterable<? extends E> elements)
/*     */   {
/* 145 */     return Lists.addAllImpl(this, index, elements);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected int standardIndexOf(@Nullable Object element)
/*     */   {
/* 156 */     return Lists.indexOfImpl(this, element);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected int standardLastIndexOf(@Nullable Object element)
/*     */   {
/* 168 */     return Lists.lastIndexOfImpl(this, element);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected Iterator<E> standardIterator()
/*     */   {
/* 179 */     return listIterator();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected ListIterator<E> standardListIterator()
/*     */   {
/* 191 */     return listIterator(0);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected ListIterator<E> standardListIterator(int start)
/*     */   {
/* 204 */     return Lists.listIteratorImpl(this, start);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected List<E> standardSubList(int fromIndex, int toIndex)
/*     */   {
/* 215 */     return Lists.subListImpl(this, fromIndex, toIndex);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardEquals(@Nullable Object object)
/*     */   {
/* 226 */     return Lists.equalsImpl(this, object);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected int standardHashCode()
/*     */   {
/* 237 */     return Lists.hashCodeImpl(this);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingList
 * JD-Core Version:    0.6.2
 */