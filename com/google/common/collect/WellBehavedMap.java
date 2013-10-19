/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class WellBehavedMap<K, V> extends ForwardingMap<K, V>
/*    */ {
/*    */   private final Map<K, V> delegate;
/*    */   private Set<Map.Entry<K, V>> entrySet;
/*    */ 
/*    */   private WellBehavedMap(Map<K, V> delegate)
/*    */   {
/* 42 */     this.delegate = delegate;
/*    */   }
/*    */ 
/*    */   static <K, V> WellBehavedMap<K, V> wrap(Map<K, V> delegate)
/*    */   {
/* 52 */     return new WellBehavedMap(delegate);
/*    */   }
/*    */ 
/*    */   protected Map<K, V> delegate() {
/* 56 */     return this.delegate;
/*    */   }
/*    */ 
/*    */   public Set<Map.Entry<K, V>> entrySet() {
/* 60 */     Set es = this.entrySet;
/* 61 */     if (es != null) {
/* 62 */       return es;
/*    */     }
/* 64 */     return this.entrySet = new EntrySet(null);
/*    */   }
/*    */   private final class EntrySet extends Maps.EntrySet<K, V> {
/*    */     private EntrySet() {
/*    */     }
/*    */     Map<K, V> map() {
/* 70 */       return WellBehavedMap.this;
/*    */     }
/*    */ 
/*    */     public Iterator<Map.Entry<K, V>> iterator()
/*    */     {
/* 75 */       return new TransformedIterator(WellBehavedMap.this.keySet().iterator())
/*    */       {
/*    */         Map.Entry<K, V> transform(final K key) {
/* 78 */           return new AbstractMapEntry()
/*    */           {
/*    */             public K getKey() {
/* 81 */               return key;
/*    */             }
/*    */ 
/*    */             public V getValue()
/*    */             {
/* 86 */               return WellBehavedMap.this.get(key);
/*    */             }
/*    */ 
/*    */             public V setValue(V value)
/*    */             {
/* 91 */               return WellBehavedMap.this.put(key, value);
/*    */             }
/*    */           };
/*    */         }
/*    */       };
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.WellBehavedMap
 * JD-Core Version:    0.6.2
 */