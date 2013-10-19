/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.concurrent.ConcurrentMap;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingConcurrentMap<K, V> extends ForwardingMap<K, V>
/*    */   implements ConcurrentMap<K, V>
/*    */ {
/*    */   protected abstract ConcurrentMap<K, V> delegate();
/*    */ 
/*    */   public V putIfAbsent(K key, V value)
/*    */   {
/* 43 */     return delegate().putIfAbsent(key, value);
/*    */   }
/*    */ 
/*    */   public boolean remove(Object key, Object value)
/*    */   {
/* 48 */     return delegate().remove(key, value);
/*    */   }
/*    */ 
/*    */   public V replace(K key, V value)
/*    */   {
/* 53 */     return delegate().replace(key, value);
/*    */   }
/*    */ 
/*    */   public boolean replace(K key, V oldValue, V newValue)
/*    */   {
/* 58 */     return delegate().replace(key, oldValue, newValue);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingConcurrentMap
 * JD-Core Version:    0.6.2
 */