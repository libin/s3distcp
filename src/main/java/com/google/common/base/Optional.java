/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(serializable=true)
/*     */ public abstract class Optional<T>
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <T> Optional<T> absent()
/*     */   {
/*  80 */     return Absent.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <T> Optional<T> of(T reference)
/*     */   {
/*  87 */     return new Present(Preconditions.checkNotNull(reference));
/*     */   }
/*     */ 
/*     */   public static <T> Optional<T> fromNullable(@Nullable T nullableReference)
/*     */   {
/*  95 */     return nullableReference == null ? absent() : new Present(nullableReference);
/*     */   }
/*     */ 
/*     */   public abstract boolean isPresent();
/*     */ 
/*     */   public abstract T get();
/*     */ 
/*     */   public abstract T or(T paramT);
/*     */ 
/*     */   public abstract Optional<T> or(Optional<? extends T> paramOptional);
/*     */ 
/*     */   public abstract T or(Supplier<? extends T> paramSupplier);
/*     */ 
/*     */   @Nullable
/*     */   public abstract T orNull();
/*     */ 
/*     */   public abstract Set<T> asSet();
/*     */ 
/*     */   public abstract <V> Optional<V> transform(Function<? super T, V> paramFunction);
/*     */ 
/*     */   public abstract boolean equals(@Nullable Object paramObject);
/*     */ 
/*     */   public abstract int hashCode();
/*     */ 
/*     */   public abstract String toString();
/*     */ 
/*     */   public static <T> Iterable<T> presentInstances(Iterable<Optional<T>> optionals)
/*     */   {
/* 213 */     Preconditions.checkNotNull(optionals);
/* 214 */     return new Iterable() {
/*     */       public Iterator<T> iterator() {
/* 216 */         return new AbstractIterator() {
/* 217 */           private final Iterator<Optional<T>> iterator = (Iterator)Preconditions.checkNotNull(Optional.1.this.val$optionals.iterator());
/*     */ 
/*     */           protected T computeNext() {
/* 220 */             while (this.iterator.hasNext()) {
/* 221 */               Optional optional = (Optional)this.iterator.next();
/* 222 */               if (optional.isPresent()) {
/* 223 */                 return optional.get();
/*     */               }
/*     */             }
/* 226 */             return endOfData();
/*     */           }
/*     */         };
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Optional
 * JD-Core Version:    0.6.2
 */