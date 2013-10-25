/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @GwtCompatible(serializable=true, emulated=true)
/*    */ final class EmptyImmutableMap extends ImmutableMap<Object, Object>
/*    */ {
/* 33 */   static final EmptyImmutableMap INSTANCE = new EmptyImmutableMap();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public Object get(@Nullable Object key)
/*    */   {
/* 38 */     return null;
/*    */   }
/*    */ 
/*    */   public int size()
/*    */   {
/* 43 */     return 0;
/*    */   }
/*    */ 
/*    */   public boolean isEmpty() {
/* 47 */     return true;
/*    */   }
/*    */ 
/*    */   public boolean containsKey(@Nullable Object key) {
/* 51 */     return false;
/*    */   }
/*    */ 
/*    */   public boolean containsValue(@Nullable Object value) {
/* 55 */     return false;
/*    */   }
/*    */ 
/*    */   ImmutableSet<Map.Entry<Object, Object>> createEntrySet() {
/* 59 */     throw new AssertionError("should never be called");
/*    */   }
/*    */ 
/*    */   public ImmutableSet<Map.Entry<Object, Object>> entrySet() {
/* 63 */     return ImmutableSet.of();
/*    */   }
/*    */ 
/*    */   public ImmutableSet<Object> keySet() {
/* 67 */     return ImmutableSet.of();
/*    */   }
/*    */ 
/*    */   public ImmutableCollection<Object> values() {
/* 71 */     return ImmutableCollection.EMPTY_IMMUTABLE_COLLECTION;
/*    */   }
/*    */ 
/*    */   public boolean equals(@Nullable Object object) {
/* 75 */     if ((object instanceof Map)) {
/* 76 */       Map that = (Map)object;
/* 77 */       return that.isEmpty();
/*    */     }
/* 79 */     return false;
/*    */   }
/*    */ 
/*    */   boolean isPartialView() {
/* 83 */     return false;
/*    */   }
/*    */ 
/*    */   public int hashCode() {
/* 87 */     return 0;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 91 */     return "{}";
/*    */   }
/*    */ 
/*    */   Object readResolve() {
/* 95 */     return INSTANCE;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.EmptyImmutableMap
 * JD-Core Version:    0.6.2
 */