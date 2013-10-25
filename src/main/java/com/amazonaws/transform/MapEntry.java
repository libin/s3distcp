/*    */ package com.amazonaws.transform;
/*    */ 
/*    */ import java.util.Map.Entry;
/*    */ 
/*    */ public class MapEntry<K, V>
/*    */   implements Map.Entry<K, V>
/*    */ {
/*    */   private K key;
/*    */   private V value;
/*    */ 
/*    */   public K getKey()
/*    */   {
/* 35 */     return this.key;
/*    */   }
/*    */ 
/*    */   public V getValue()
/*    */   {
/* 42 */     return this.value;
/*    */   }
/*    */ 
/*    */   public V setValue(V value)
/*    */   {
/* 49 */     this.value = value;
/* 50 */     return this.value;
/*    */   }
/*    */ 
/*    */   public K setKey(K key) {
/* 54 */     this.key = key;
/* 55 */     return this.key;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.MapEntry
 * JD-Core Version:    0.6.2
 */