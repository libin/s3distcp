/*    */ package com.amazonaws.util;
/*    */ 
/*    */ import com.amazonaws.ResponseMetadata;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ 
/*    */ public class ResponseMetadataCache
/*    */ {
/*    */   private final int maxEntries;
/*    */   private Map<Integer, ResponseMetadata> map;
/*    */   private List<Object> objectList;
/*    */ 
/*    */   public ResponseMetadataCache(int maxEntries)
/*    */   {
/* 42 */     this.maxEntries = maxEntries;
/*    */ 
/* 44 */     this.objectList = new ArrayList(maxEntries);
/* 45 */     this.map = new HashMap();
/*    */   }
/*    */ 
/*    */   public synchronized void add(Object obj, ResponseMetadata metadata)
/*    */   {
/* 58 */     if (obj == null) return;
/*    */ 
/* 60 */     if (this.map.size() >= this.maxEntries) evictOldest();
/* 61 */     store(System.identityHashCode(obj), metadata);
/*    */   }
/*    */ 
/*    */   public ResponseMetadata get(Object obj)
/*    */   {
/* 79 */     return (ResponseMetadata)this.map.get(Integer.valueOf(System.identityHashCode(obj)));
/*    */   }
/*    */ 
/*    */   private void evictOldest() {
/* 83 */     this.map.remove(this.objectList.remove(0));
/*    */   }
/*    */ 
/*    */   private void store(int id, ResponseMetadata metadata) {
/* 87 */     this.map.put(Integer.valueOf(id), metadata);
/* 88 */     this.objectList.add(Integer.valueOf(id));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.ResponseMetadataCache
 * JD-Core Version:    0.6.2
 */