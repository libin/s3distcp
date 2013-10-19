/*    */ package com.google.common.cache;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.base.Preconditions;
/*    */ import com.google.common.collect.ImmutableMap;
/*    */ import java.util.concurrent.ExecutionException;
/*    */ 
/*    */ @Beta
/*    */ public abstract class ForwardingLoadingCache<K, V> extends ForwardingCache<K, V>
/*    */   implements LoadingCache<K, V>
/*    */ {
/*    */   protected abstract LoadingCache<K, V> delegate();
/*    */ 
/*    */   public V get(K key)
/*    */     throws ExecutionException
/*    */   {
/* 48 */     return delegate().get(key);
/*    */   }
/*    */ 
/*    */   public V getUnchecked(K key)
/*    */   {
/* 53 */     return delegate().getUnchecked(key);
/*    */   }
/*    */ 
/*    */   public ImmutableMap<K, V> getAll(Iterable<? extends K> keys) throws ExecutionException
/*    */   {
/* 58 */     return delegate().getAll(keys);
/*    */   }
/*    */ 
/*    */   public V apply(K key)
/*    */   {
/* 63 */     return delegate().apply(key);
/*    */   }
/*    */ 
/*    */   public void refresh(K key)
/*    */   {
/* 68 */     delegate().refresh(key);
/*    */   }
/*    */ 
/*    */   @Beta
/*    */   public static abstract class SimpleForwardingLoadingCache<K, V> extends ForwardingLoadingCache<K, V>
/*    */   {
/*    */     private final LoadingCache<K, V> delegate;
/*    */ 
/*    */     protected SimpleForwardingLoadingCache(LoadingCache<K, V> delegate)
/*    */     {
/* 83 */       this.delegate = ((LoadingCache)Preconditions.checkNotNull(delegate));
/*    */     }
/*    */ 
/*    */     protected final LoadingCache<K, V> delegate()
/*    */     {
/* 88 */       return this.delegate;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.ForwardingLoadingCache
 * JD-Core Version:    0.6.2
 */