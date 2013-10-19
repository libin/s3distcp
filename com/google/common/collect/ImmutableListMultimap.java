/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Collection;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public class ImmutableListMultimap<K, V> extends ImmutableMultimap<K, V>
/*     */   implements ListMultimap<K, V>
/*     */ {
/*     */   private transient ImmutableListMultimap<V, K> inverse;
/*     */ 
/*     */   @GwtIncompatible("Not needed in emulated source")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> ImmutableListMultimap<K, V> of()
/*     */   {
/*  65 */     return EmptyImmutableListMultimap.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1)
/*     */   {
/*  72 */     Builder builder = builder();
/*     */ 
/*  74 */     builder.put(k1, v1);
/*  75 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  82 */     Builder builder = builder();
/*     */ 
/*  84 */     builder.put(k1, v1);
/*  85 */     builder.put(k2, v2);
/*  86 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/*  94 */     Builder builder = builder();
/*     */ 
/*  96 */     builder.put(k1, v1);
/*  97 */     builder.put(k2, v2);
/*  98 */     builder.put(k3, v3);
/*  99 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 107 */     Builder builder = builder();
/*     */ 
/* 109 */     builder.put(k1, v1);
/* 110 */     builder.put(k2, v2);
/* 111 */     builder.put(k3, v3);
/* 112 */     builder.put(k4, v4);
/* 113 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableListMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 121 */     Builder builder = builder();
/*     */ 
/* 123 */     builder.put(k1, v1);
/* 124 */     builder.put(k2, v2);
/* 125 */     builder.put(k3, v3);
/* 126 */     builder.put(k4, v4);
/* 127 */     builder.put(k5, v5);
/* 128 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> builder()
/*     */   {
/* 138 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableListMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 243 */     if (multimap.isEmpty()) {
/* 244 */       return of();
/*     */     }
/*     */ 
/* 248 */     if ((multimap instanceof ImmutableListMultimap))
/*     */     {
/* 250 */       ImmutableListMultimap kvMultimap = (ImmutableListMultimap)multimap;
/*     */ 
/* 252 */       if (!kvMultimap.isPartialView()) {
/* 253 */         return kvMultimap;
/*     */       }
/*     */     }
/*     */ 
/* 257 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/* 258 */     int size = 0;
/*     */ 
/* 261 */     for (Map.Entry entry : multimap.asMap().entrySet()) {
/* 262 */       ImmutableList list = ImmutableList.copyOf((Collection)entry.getValue());
/* 263 */       if (!list.isEmpty()) {
/* 264 */         builder.put(entry.getKey(), list);
/* 265 */         size += list.size();
/*     */       }
/*     */     }
/*     */ 
/* 269 */     return new ImmutableListMultimap(builder.build(), size);
/*     */   }
/*     */ 
/*     */   ImmutableListMultimap(ImmutableMap<K, ImmutableList<V>> map, int size) {
/* 273 */     super(map, size);
/*     */   }
/*     */ 
/*     */   public ImmutableList<V> get(@Nullable K key)
/*     */   {
/* 286 */     ImmutableList list = (ImmutableList)this.map.get(key);
/* 287 */     return list == null ? ImmutableList.of() : list;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public ImmutableListMultimap<V, K> inverse()
/*     */   {
/* 303 */     ImmutableListMultimap result = this.inverse;
/* 304 */     return result == null ? (this.inverse = invert()) : result;
/*     */   }
/*     */ 
/*     */   private ImmutableListMultimap<V, K> invert() {
/* 308 */     Builder builder = builder();
/* 309 */     for (Map.Entry entry : entries()) {
/* 310 */       builder.put(entry.getValue(), entry.getKey());
/*     */     }
/* 312 */     ImmutableListMultimap invertedMultimap = builder.build();
/* 313 */     invertedMultimap.inverse = this;
/* 314 */     return invertedMultimap;
/*     */   }
/*     */ 
/*     */   public ImmutableList<V> removeAll(Object key)
/*     */   {
/* 323 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableList<V> replaceValues(K key, Iterable<? extends V> values)
/*     */   {
/* 333 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 342 */     stream.defaultWriteObject();
/* 343 */     Serialization.writeMultimap(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 349 */     stream.defaultReadObject();
/* 350 */     int keyCount = stream.readInt();
/* 351 */     if (keyCount < 0) {
/* 352 */       throw new InvalidObjectException("Invalid key count " + keyCount);
/*     */     }
/* 354 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/*     */ 
/* 356 */     int tmpSize = 0;
/*     */ 
/* 358 */     for (int i = 0; i < keyCount; i++) {
/* 359 */       Object key = stream.readObject();
/* 360 */       int valueCount = stream.readInt();
/* 361 */       if (valueCount <= 0) {
/* 362 */         throw new InvalidObjectException("Invalid value count " + valueCount);
/*     */       }
/*     */ 
/* 365 */       Object[] array = new Object[valueCount];
/* 366 */       for (int j = 0; j < valueCount; j++) {
/* 367 */         array[j] = stream.readObject();
/*     */       }
/* 369 */       builder.put(key, ImmutableList.copyOf(array));
/* 370 */       tmpSize += valueCount;
/*     */     }
/*     */     ImmutableMap tmpMap;
/*     */     try
/*     */     {
/* 375 */       tmpMap = builder.build();
/*     */     } catch (IllegalArgumentException e) {
/* 377 */       throw ((InvalidObjectException)new InvalidObjectException(e.getMessage()).initCause(e));
/*     */     }
/*     */ 
/* 381 */     ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, tmpMap);
/* 382 */     ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, tmpSize);
/*     */   }
/*     */ 
/*     */   public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V>
/*     */   {
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 168 */       super.put(key, value);
/* 169 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry)
/*     */     {
/* 179 */       super.put(entry);
/* 180 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, Iterable<? extends V> values) {
/* 184 */       super.putAll(key, values);
/* 185 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, V[] values) {
/* 189 */       super.putAll(key, values);
/* 190 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap)
/*     */     {
/* 195 */       super.putAll(multimap);
/* 196 */       return this;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator)
/*     */     {
/* 206 */       super.orderKeysBy(keyComparator);
/* 207 */       return this;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator)
/*     */     {
/* 217 */       super.orderValuesBy(valueComparator);
/* 218 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableListMultimap<K, V> build()
/*     */     {
/* 225 */       return (ImmutableListMultimap)super.build();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableListMultimap
 * JD-Core Version:    0.6.2
 */