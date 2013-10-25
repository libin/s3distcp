/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class HashBiMap<K, V> extends AbstractBiMap<K, V>
/*     */ {
/*     */ 
/*     */   @GwtIncompatible("Not needed in emulated source")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> HashBiMap<K, V> create()
/*     */   {
/*  50 */     return new HashBiMap();
/*     */   }
/*     */ 
/*     */   public static <K, V> HashBiMap<K, V> create(int expectedSize)
/*     */   {
/*  61 */     return new HashBiMap(expectedSize);
/*     */   }
/*     */ 
/*     */   public static <K, V> HashBiMap<K, V> create(Map<? extends K, ? extends V> map)
/*     */   {
/*  71 */     HashBiMap bimap = create(map.size());
/*  72 */     bimap.putAll(map);
/*  73 */     return bimap;
/*     */   }
/*     */ 
/*     */   private HashBiMap() {
/*  77 */     super(new HashMap(), new HashMap());
/*     */   }
/*     */ 
/*     */   private HashBiMap(int expectedSize) {
/*  81 */     super(Maps.newHashMapWithExpectedSize(expectedSize), Maps.newHashMapWithExpectedSize(expectedSize));
/*     */   }
/*     */ 
/*     */   public V put(@Nullable K key, @Nullable V value)
/*     */   {
/*  89 */     return super.put(key, value);
/*     */   }
/*     */ 
/*     */   public V forcePut(@Nullable K key, @Nullable V value) {
/*  93 */     return super.forcePut(key, value);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 102 */     stream.defaultWriteObject();
/* 103 */     Serialization.writeMap(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 109 */     stream.defaultReadObject();
/* 110 */     int size = Serialization.readCount(stream);
/* 111 */     setDelegates(Maps.newHashMapWithExpectedSize(size), Maps.newHashMapWithExpectedSize(size));
/*     */ 
/* 113 */     Serialization.populateMap(this, stream, size);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.HashBiMap
 * JD-Core Version:    0.6.2
 */