/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.List;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingListMultimap<K, V> extends ForwardingMultimap<K, V>
/*    */   implements ListMultimap<K, V>
/*    */ {
/*    */   protected abstract ListMultimap<K, V> delegate();
/*    */ 
/*    */   public List<V> get(@Nullable K key)
/*    */   {
/* 44 */     return delegate().get(key);
/*    */   }
/*    */ 
/*    */   public List<V> removeAll(@Nullable Object key) {
/* 48 */     return delegate().removeAll(key);
/*    */   }
/*    */ 
/*    */   public List<V> replaceValues(K key, Iterable<? extends V> values) {
/* 52 */     return delegate().replaceValues(key, values);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingListMultimap
 * JD-Core Version:    0.6.2
 */