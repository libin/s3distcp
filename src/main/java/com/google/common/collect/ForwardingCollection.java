/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public abstract class ForwardingCollection<E> extends ForwardingObject
/*     */   implements Collection<E>
/*     */ {
/*     */   protected abstract Collection<E> delegate();
/*     */ 
/*     */   public Iterator<E> iterator()
/*     */   {
/*  60 */     return delegate().iterator();
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  65 */     return delegate().size();
/*     */   }
/*     */ 
/*     */   public boolean removeAll(Collection<?> collection)
/*     */   {
/*  70 */     return delegate().removeAll(collection);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/*  75 */     return delegate().isEmpty();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object object)
/*     */   {
/*  80 */     return delegate().contains(object);
/*     */   }
/*     */ 
/*     */   public boolean add(E element)
/*     */   {
/*  85 */     return delegate().add(element);
/*     */   }
/*     */ 
/*     */   public boolean remove(Object object)
/*     */   {
/*  90 */     return delegate().remove(object);
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> collection)
/*     */   {
/*  95 */     return delegate().containsAll(collection);
/*     */   }
/*     */ 
/*     */   public boolean addAll(Collection<? extends E> collection)
/*     */   {
/* 100 */     return delegate().addAll(collection);
/*     */   }
/*     */ 
/*     */   public boolean retainAll(Collection<?> collection)
/*     */   {
/* 105 */     return delegate().retainAll(collection);
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 110 */     delegate().clear();
/*     */   }
/*     */ 
/*     */   public Object[] toArray()
/*     */   {
/* 115 */     return delegate().toArray();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] array)
/*     */   {
/* 120 */     return delegate().toArray(array);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardContains(@Nullable Object object)
/*     */   {
/* 131 */     return Iterators.contains(iterator(), object);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardContainsAll(Collection<?> collection)
/*     */   {
/* 142 */     for (Iterator i$ = collection.iterator(); i$.hasNext(); ) { Object o = i$.next();
/* 143 */       if (!contains(o)) {
/* 144 */         return false;
/*     */       }
/*     */     }
/* 147 */     return true;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardAddAll(Collection<? extends E> collection)
/*     */   {
/* 158 */     return Iterators.addAll(this, collection.iterator());
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardRemove(@Nullable Object object)
/*     */   {
/* 170 */     Iterator iterator = iterator();
/* 171 */     while (iterator.hasNext()) {
/* 172 */       if (Objects.equal(iterator.next(), object)) {
/* 173 */         iterator.remove();
/* 174 */         return true;
/*     */       }
/*     */     }
/* 177 */     return false;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardRemoveAll(Collection<?> collection)
/*     */   {
/* 189 */     return Iterators.removeAll(iterator(), collection);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardRetainAll(Collection<?> collection)
/*     */   {
/* 201 */     return Iterators.retainAll(iterator(), collection);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected void standardClear()
/*     */   {
/* 213 */     Iterator iterator = iterator();
/* 214 */     while (iterator.hasNext()) {
/* 215 */       iterator.next();
/* 216 */       iterator.remove();
/*     */     }
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected boolean standardIsEmpty()
/*     */   {
/* 229 */     return !iterator().hasNext();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected String standardToString()
/*     */   {
/* 240 */     return Collections2.toStringImpl(this);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected Object[] standardToArray()
/*     */   {
/* 251 */     Object[] newArray = new Object[size()];
/* 252 */     return toArray(newArray);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   protected <T> T[] standardToArray(T[] array)
/*     */   {
/* 263 */     return ObjectArrays.toArrayImpl(this, array);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingCollection
 * JD-Core Version:    0.6.2
 */