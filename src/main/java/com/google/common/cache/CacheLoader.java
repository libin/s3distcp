/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.util.concurrent.Futures;
/*     */ import com.google.common.util.concurrent.ListenableFuture;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class CacheLoader<K, V>
/*     */ {
/*     */   public abstract V load(K paramK)
/*     */     throws Exception;
/*     */ 
/*     */   @GwtIncompatible("Futures")
/*     */   public ListenableFuture<V> reload(K key, V oldValue)
/*     */     throws Exception
/*     */   {
/*  83 */     return Futures.immediateFuture(load(key));
/*     */   }
/*     */ 
/*     */   public Map<K, V> loadAll(Iterable<? extends K> keys)
/*     */     throws Exception
/*     */   {
/* 111 */     throw new UnsupportedLoadingOperationException();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <K, V> CacheLoader<K, V> from(Function<K, V> function)
/*     */   {
/* 124 */     return new FunctionToCacheLoader(function);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <V> CacheLoader<Object, V> from(Supplier<V> supplier)
/*     */   {
/* 154 */     return new SupplierToCacheLoader(supplier);
/*     */   }
/*     */ 
/*     */   public static final class InvalidCacheLoadException extends RuntimeException
/*     */   {
/*     */     public InvalidCacheLoadException(String message)
/*     */     {
/* 182 */       super();
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class UnsupportedLoadingOperationException extends UnsupportedOperationException
/*     */   {
/*     */   }
/*     */ 
/*     */   private static final class SupplierToCacheLoader<V> extends CacheLoader<Object, V>
/*     */     implements Serializable
/*     */   {
/*     */     private final Supplier<V> computingSupplier;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public SupplierToCacheLoader(Supplier<V> computingSupplier)
/*     */     {
/* 162 */       this.computingSupplier = ((Supplier)Preconditions.checkNotNull(computingSupplier));
/*     */     }
/*     */ 
/*     */     public V load(Object key)
/*     */     {
/* 167 */       return this.computingSupplier.get();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class FunctionToCacheLoader<K, V> extends CacheLoader<K, V>
/*     */     implements Serializable
/*     */   {
/*     */     private final Function<K, V> computingFunction;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public FunctionToCacheLoader(Function<K, V> computingFunction)
/*     */     {
/* 132 */       this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */     }
/*     */ 
/*     */     public V load(K key)
/*     */     {
/* 137 */       return this.computingFunction.apply(key);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.CacheLoader
 * JD-Core Version:    0.6.2
 */