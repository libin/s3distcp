/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Comparator;
/*    */ import java.util.SortedSet;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible
/*    */ public abstract class ForwardingSortedSetMultimap<K, V> extends ForwardingSetMultimap<K, V>
/*    */   implements SortedSetMultimap<K, V>
/*    */ {
/*    */   protected abstract SortedSetMultimap<K, V> delegate();
/*    */ 
/*    */   public SortedSet<V> get(@Nullable K key)
/*    */   {
/* 45 */     return delegate().get(key);
/*    */   }
/*    */ 
/*    */   public SortedSet<V> removeAll(@Nullable Object key) {
/* 49 */     return delegate().removeAll(key);
/*    */   }
/*    */ 
/*    */   public SortedSet<V> replaceValues(K key, Iterable<? extends V> values)
/*    */   {
/* 54 */     return delegate().replaceValues(key, values);
/*    */   }
/*    */ 
/*    */   public Comparator<? super V> valueComparator() {
/* 58 */     return delegate().valueComparator();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ForwardingSortedSetMultimap
 * JD-Core Version:    0.6.2
 */