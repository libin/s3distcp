/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.IOException;
/*     */ import java.io.InvalidObjectException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.TreeMap;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public class ImmutableSetMultimap<K, V> extends ImmutableMultimap<K, V>
/*     */   implements SetMultimap<K, V>
/*     */ {
/*     */   private final transient ImmutableSortedSet<V> emptySet;
/*     */   private transient ImmutableSetMultimap<V, K> inverse;
/*     */   private transient ImmutableSet<Map.Entry<K, V>> entries;
/*     */ 
/*     */   @GwtIncompatible("not needed in emulated source.")
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of()
/*     */   {
/*  74 */     return EmptyImmutableSetMultimap.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1)
/*     */   {
/*  81 */     Builder builder = builder();
/*  82 */     builder.put(k1, v1);
/*  83 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  92 */     Builder builder = builder();
/*  93 */     builder.put(k1, v1);
/*  94 */     builder.put(k2, v2);
/*  95 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/* 105 */     Builder builder = builder();
/* 106 */     builder.put(k1, v1);
/* 107 */     builder.put(k2, v2);
/* 108 */     builder.put(k3, v3);
/* 109 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 119 */     Builder builder = builder();
/* 120 */     builder.put(k1, v1);
/* 121 */     builder.put(k2, v2);
/* 122 */     builder.put(k3, v3);
/* 123 */     builder.put(k4, v4);
/* 124 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 134 */     Builder builder = builder();
/* 135 */     builder.put(k1, v1);
/* 136 */     builder.put(k2, v2);
/* 137 */     builder.put(k3, v3);
/* 138 */     builder.put(k4, v4);
/* 139 */     builder.put(k5, v5);
/* 140 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> builder()
/*     */   {
/* 149 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 322 */     return copyOf(multimap, null);
/*     */   }
/*     */ 
/*     */   private static <K, V> ImmutableSetMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap, Comparator<? super V> valueComparator)
/*     */   {
/* 328 */     Preconditions.checkNotNull(multimap);
/* 329 */     if ((multimap.isEmpty()) && (valueComparator == null)) {
/* 330 */       return of();
/*     */     }
/*     */ 
/* 333 */     if ((multimap instanceof ImmutableSetMultimap))
/*     */     {
/* 335 */       ImmutableSetMultimap kvMultimap = (ImmutableSetMultimap)multimap;
/*     */ 
/* 337 */       if (!kvMultimap.isPartialView()) {
/* 338 */         return kvMultimap;
/*     */       }
/*     */     }
/*     */ 
/* 342 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/* 343 */     int size = 0;
/*     */ 
/* 346 */     for (Map.Entry entry : multimap.asMap().entrySet()) {
/* 347 */       Object key = entry.getKey();
/* 348 */       Collection values = (Collection)entry.getValue();
/* 349 */       ImmutableSet set = valueComparator == null ? ImmutableSet.copyOf(values) : ImmutableSortedSet.copyOf(valueComparator, values);
/*     */ 
/* 352 */       if (!set.isEmpty()) {
/* 353 */         builder.put(key, set);
/* 354 */         size += set.size();
/*     */       }
/*     */     }
/*     */ 
/* 358 */     return new ImmutableSetMultimap(builder.build(), size, valueComparator);
/*     */   }
/*     */ 
/*     */   ImmutableSetMultimap(ImmutableMap<K, ImmutableSet<V>> map, int size, @Nullable Comparator<? super V> valueComparator)
/*     */   {
/* 367 */     super(map, size);
/* 368 */     this.emptySet = (valueComparator == null ? null : ImmutableSortedSet.emptySet(valueComparator));
/*     */   }
/*     */ 
/*     */   public ImmutableSet<V> get(@Nullable K key)
/*     */   {
/* 382 */     ImmutableSet set = (ImmutableSet)this.map.get(key);
/* 383 */     if (set != null)
/* 384 */       return set;
/* 385 */     if (this.emptySet != null) {
/* 386 */       return this.emptySet;
/*     */     }
/* 388 */     return ImmutableSet.of();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public ImmutableSetMultimap<V, K> inverse()
/*     */   {
/* 405 */     ImmutableSetMultimap result = this.inverse;
/* 406 */     return result == null ? (this.inverse = invert()) : result;
/*     */   }
/*     */ 
/*     */   private ImmutableSetMultimap<V, K> invert() {
/* 410 */     Builder builder = builder();
/* 411 */     for (Map.Entry entry : entries()) {
/* 412 */       builder.put(entry.getValue(), entry.getKey());
/*     */     }
/* 414 */     ImmutableSetMultimap invertedMultimap = builder.build();
/* 415 */     invertedMultimap.inverse = this;
/* 416 */     return invertedMultimap;
/*     */   }
/*     */ 
/*     */   public ImmutableSet<V> removeAll(Object key)
/*     */   {
/* 425 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<V> replaceValues(K key, Iterable<? extends V> values)
/*     */   {
/* 435 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<Map.Entry<K, V>> entries()
/*     */   {
/* 447 */     ImmutableSet result = this.entries;
/* 448 */     return result == null ? (this.entries = ImmutableSet.copyOf(super.entries())) : result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectOutputStream")
/*     */   private void writeObject(ObjectOutputStream stream)
/*     */     throws IOException
/*     */   {
/* 459 */     stream.defaultWriteObject();
/* 460 */     Serialization.writeMultimap(this, stream);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.io.ObjectInputStream")
/*     */   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException
/*     */   {
/* 466 */     stream.defaultReadObject();
/* 467 */     int keyCount = stream.readInt();
/* 468 */     if (keyCount < 0) {
/* 469 */       throw new InvalidObjectException("Invalid key count " + keyCount);
/*     */     }
/* 471 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/*     */ 
/* 473 */     int tmpSize = 0;
/*     */ 
/* 475 */     for (int i = 0; i < keyCount; i++) {
/* 476 */       Object key = stream.readObject();
/* 477 */       int valueCount = stream.readInt();
/* 478 */       if (valueCount <= 0) {
/* 479 */         throw new InvalidObjectException("Invalid value count " + valueCount);
/*     */       }
/*     */ 
/* 482 */       Object[] array = new Object[valueCount];
/* 483 */       for (int j = 0; j < valueCount; j++) {
/* 484 */         array[j] = stream.readObject();
/*     */       }
/* 486 */       ImmutableSet valueSet = ImmutableSet.copyOf(array);
/* 487 */       if (valueSet.size() != array.length) {
/* 488 */         throw new InvalidObjectException("Duplicate key-value pairs exist for key " + key);
/*     */       }
/*     */ 
/* 491 */       builder.put(key, valueSet);
/* 492 */       tmpSize += valueCount;
/*     */     }
/*     */     ImmutableMap tmpMap;
/*     */     try
/*     */     {
/* 497 */       tmpMap = builder.build();
/*     */     } catch (IllegalArgumentException e) {
/* 499 */       throw ((InvalidObjectException)new InvalidObjectException(e.getMessage()).initCause(e));
/*     */     }
/*     */ 
/* 503 */     ImmutableMultimap.FieldSettersHolder.MAP_FIELD_SETTER.set(this, tmpMap);
/* 504 */     ImmutableMultimap.FieldSettersHolder.SIZE_FIELD_SETTER.set(this, tmpSize);
/*     */   }
/*     */ 
/*     */   public static final class Builder<K, V> extends ImmutableMultimap.Builder<K, V>
/*     */   {
/*     */     public Builder()
/*     */     {
/* 208 */       this.builderMultimap = new ImmutableSetMultimap.BuilderMultimap();
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 216 */       this.builderMultimap.put(Preconditions.checkNotNull(key), Preconditions.checkNotNull(value));
/* 217 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry)
/*     */     {
/* 226 */       this.builderMultimap.put(Preconditions.checkNotNull(entry.getKey()), Preconditions.checkNotNull(entry.getValue()));
/*     */ 
/* 228 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, Iterable<? extends V> values) {
/* 232 */       Collection collection = this.builderMultimap.get(Preconditions.checkNotNull(key));
/* 233 */       for (Iterator i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 234 */         collection.add(Preconditions.checkNotNull(value));
/*     */       }
/* 236 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, V[] values) {
/* 240 */       return putAll(key, Arrays.asList(values));
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap)
/*     */     {
/* 246 */       for (Map.Entry entry : multimap.asMap().entrySet()) {
/* 247 */         putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */       }
/* 249 */       return this;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator)
/*     */     {
/* 259 */       this.keyComparator = ((Comparator)Preconditions.checkNotNull(keyComparator));
/* 260 */       return this;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator)
/*     */     {
/* 277 */       super.orderValuesBy(valueComparator);
/* 278 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableSetMultimap<K, V> build()
/*     */     {
/* 285 */       if (this.keyComparator != null) {
/* 286 */         Multimap sortedCopy = new ImmutableSetMultimap.BuilderMultimap();
/* 287 */         List entries = Lists.newArrayList(this.builderMultimap.asMap().entrySet());
/*     */ 
/* 289 */         Collections.sort(entries, Ordering.from(this.keyComparator).onResultOf(new Function()
/*     */         {
/*     */           public K apply(Map.Entry<K, Collection<V>> entry)
/*     */           {
/* 294 */             return entry.getKey();
/*     */           }
/*     */         }));
/* 297 */         for (Map.Entry entry : entries) {
/* 298 */           sortedCopy.putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */         }
/* 300 */         this.builderMultimap = sortedCopy;
/*     */       }
/* 302 */       return ImmutableSetMultimap.copyOf(this.builderMultimap, this.valueComparator);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class SortedKeyBuilderMultimap<K, V> extends AbstractMultimap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SortedKeyBuilderMultimap(Comparator<? super K> keyComparator, Multimap<K, V> multimap)
/*     */     {
/* 174 */       super();
/* 175 */       putAll(multimap);
/*     */     }
/*     */     Collection<V> createCollection() {
/* 178 */       return Sets.newLinkedHashSet();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class BuilderMultimap<K, V> extends AbstractMultimap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     BuilderMultimap()
/*     */     {
/* 158 */       super();
/*     */     }
/*     */     Collection<V> createCollection() {
/* 161 */       return Sets.newLinkedHashSet();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableSetMultimap
 * JD-Core Version:    0.6.2
 */