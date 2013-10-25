/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumSet;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ final class ImmutableEnumSet<E extends Enum<E>> extends ImmutableSet<E>
/*     */ {
/*     */   private final transient EnumSet<E> delegate;
/*     */   private transient int hashCode;
/*     */ 
/*     */   ImmutableEnumSet(EnumSet<E> delegate)
/*     */   {
/*  45 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/*  49 */     return false;
/*     */   }
/*     */ 
/*     */   public UnmodifiableIterator<E> iterator() {
/*  53 */     return Iterators.unmodifiableIterator(this.delegate.iterator());
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  58 */     return this.delegate.size();
/*     */   }
/*     */ 
/*     */   public boolean contains(Object object) {
/*  62 */     return this.delegate.contains(object);
/*     */   }
/*     */ 
/*     */   public boolean containsAll(Collection<?> collection) {
/*  66 */     return this.delegate.containsAll(collection);
/*     */   }
/*     */ 
/*     */   public boolean isEmpty() {
/*  70 */     return this.delegate.isEmpty();
/*     */   }
/*     */ 
/*     */   public Object[] toArray() {
/*  74 */     return this.delegate.toArray();
/*     */   }
/*     */ 
/*     */   public <T> T[] toArray(T[] array) {
/*  78 */     return this.delegate.toArray(array);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object object) {
/*  82 */     return (object == this) || (this.delegate.equals(object));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/*  88 */     int result = this.hashCode;
/*  89 */     return result == 0 ? (this.hashCode = this.delegate.hashCode()) : result;
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  93 */     return this.delegate.toString();
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/*  98 */     return new EnumSerializedForm(this.delegate);
/*     */   }
/*     */ 
/*     */   private static class EnumSerializedForm<E extends Enum<E>> implements Serializable
/*     */   {
/*     */     final EnumSet<E> delegate;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     EnumSerializedForm(EnumSet<E> delegate) {
/* 108 */       this.delegate = delegate;
/*     */     }
/*     */ 
/*     */     Object readResolve() {
/* 112 */       return new ImmutableEnumSet(this.delegate.clone());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableEnumSet
 * JD-Core Version:    0.6.2
 */