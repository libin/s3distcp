/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ @GwtCompatible(serializable=true, emulated=true)
/*    */ class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V>
/*    */ {
/*    */   final transient ImmutableMap<K, V> delegate;
/*    */   final transient ImmutableBiMap<V, K> inverse;
/*    */ 
/*    */   RegularImmutableBiMap(ImmutableMap<K, V> delegate)
/*    */   {
/* 33 */     this.delegate = delegate;
/*    */ 
/* 35 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/* 36 */     for (Map.Entry entry : delegate.entrySet()) {
/* 37 */       builder.put(entry.getValue(), entry.getKey());
/*    */     }
/* 39 */     ImmutableMap backwardMap = builder.build();
/* 40 */     this.inverse = new RegularImmutableBiMap(backwardMap, this);
/*    */   }
/*    */ 
/*    */   RegularImmutableBiMap(ImmutableMap<K, V> delegate, ImmutableBiMap<V, K> inverse)
/*    */   {
/* 45 */     this.delegate = delegate;
/* 46 */     this.inverse = inverse;
/*    */   }
/*    */ 
/*    */   ImmutableMap<K, V> delegate() {
/* 50 */     return this.delegate;
/*    */   }
/*    */ 
/*    */   public ImmutableBiMap<V, K> inverse() {
/* 54 */     return this.inverse;
/*    */   }
/*    */ 
/*    */   boolean isPartialView() {
/* 58 */     return (this.delegate.isPartialView()) || (this.inverse.delegate().isPartialView());
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RegularImmutableBiMap
 * JD-Core Version:    0.6.2
 */