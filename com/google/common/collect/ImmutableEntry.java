/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ class ImmutableEntry<K, V> extends AbstractMapEntry<K, V>
/*    */   implements Serializable
/*    */ {
/*    */   private final K key;
/*    */   private final V value;
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   ImmutableEntry(@Nullable K key, @Nullable V value)
/*    */   {
/* 35 */     this.key = key;
/* 36 */     this.value = value;
/*    */   }
/*    */   @Nullable
/*    */   public K getKey() {
/* 40 */     return this.key;
/*    */   }
/*    */   @Nullable
/*    */   public V getValue() {
/* 44 */     return this.value;
/*    */   }
/*    */ 
/*    */   public final V setValue(V value) {
/* 48 */     throw new UnsupportedOperationException();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableEntry
 * JD-Core Version:    0.6.2
 */