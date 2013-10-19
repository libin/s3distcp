/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public final class HashMultimap<K, V> extends AbstractSetMultimap<K, V>
/*     */ {
/*     */   private static final int DEFAULT_VALUES_PER_KEY = 2;
/*     */ 
/*     */   @VisibleForTesting
/*  53 */   transient int expectedValuesPerKey = 2;
/*     */ 
/*     */   @GwtIncompatible("Not needed in emulated source")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> HashMultimap<K, V> create()
/*     */   {
/*  61 */     return new HashMultimap();
/*     */   }
/*     */ 
/*     */   public static <K, V> HashMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey)
/*     */   {
/*  75 */     return new HashMultimap(expectedKeys, expectedValuesPerKey);
/*     */   }
/*     */ 
/*     */   public static <K, V> HashMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/*  87 */     return new HashMultimap(multimap);
/*     */   }
/*     */ 
/*     */   private HashMultimap() {
/*  91 */     super(new HashMap());
/*     */   }
/*     */ 
/*     */   private HashMultimap(int expectedKeys, int expectedValuesPerKey) {
/*  95 */     super(Maps.newHashMapWithExpectedSize(expectedKeys));
/*  96 */     Preconditions.checkArgument(expectedValuesPerKey >= 0);
/*  97 */     this.expectedValuesPerKey = expectedValuesPerKey;
/*     */   }
/*     */ 
/*     */   private HashMultimap(Multimap<? extends K, ? extends V> multimap) {
/* 101 */     super(Maps.newHashMapWithExpectedSize(multimap.keySet().size()));
/*     */ 
/* 103 */     putAll(multimap);
/*     */   }
/*     */ 
/*     */   Set<V> createCollection()
/*     */   {
/* 114 */     return Sets.newHashSetWithExpectedSize(this.expectedValuesPerKey);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 124 */     stream.defaultWriteObject();
/* 125 */     stream.writeInt(this.expectedValuesPerKey);
/* 126 */     Serialization.writeMultimap(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 132 */     stream.defaultReadObject();
/* 133 */     this.expectedValuesPerKey = stream.readInt();
/* 134 */     int distinctKeys = Serialization.readCount(stream);
/* 135 */     Map map = Maps.newHashMapWithExpectedSize(distinctKeys);
/* 136 */     setMap(map);
/* 137 */     Serialization.populateMultimap(this, stream, distinctKeys);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.HashMultimap
 * JD-Core Version:    0.6.2
 */