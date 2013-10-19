/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(serializable=true, emulated=true)
/*     */ public abstract class ImmutableMap<K, V>
/*     */   implements Map<K, V>, Serializable
/*     */ {
/*     */   private transient ImmutableSet<Map.Entry<K, V>> entrySet;
/*     */   private transient ImmutableSet<K> keySet;
/*     */   private transient ImmutableCollection<V> values;
/*     */ 
/*     */   public static <K, V> ImmutableMap<K, V> of()
/*     */   {
/*  70 */     return EmptyImmutableMap.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMap<K, V> of(K k1, V v1)
/*     */   {
/*  80 */     return new SingletonImmutableMap(Preconditions.checkNotNull(k1), Preconditions.checkNotNull(v1));
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  90 */     return new RegularImmutableMap(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2) });
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/* 100 */     return new RegularImmutableMap(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3) });
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 111 */     return new RegularImmutableMap(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4) });
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 122 */     return new RegularImmutableMap(new Map.Entry[] { entryOf(k1, v1), entryOf(k2, v2), entryOf(k3, v3), entryOf(k4, v4), entryOf(k5, v5) });
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> builder()
/*     */   {
/* 133 */     return new Builder();
/*     */   }
/*     */ 
/*     */   static <K, V> Map.Entry<K, V> entryOf(K key, V value)
/*     */   {
/* 144 */     return Maps.immutableEntry(Preconditions.checkNotNull(key, "null key"), Preconditions.checkNotNull(value, "null value"));
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMap<K, V> copyOf(Map<? extends K, ? extends V> map)
/*     */   {
/* 269 */     if (((map instanceof ImmutableMap)) && (!(map instanceof ImmutableSortedMap)))
/*     */     {
/* 274 */       ImmutableMap kvMap = (ImmutableMap)map;
/* 275 */       if (!kvMap.isPartialView()) {
/* 276 */         return kvMap;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 281 */     Map.Entry[] entries = (Map.Entry[])map.entrySet().toArray(new Map.Entry[0]);
/* 282 */     switch (entries.length) {
/*     */     case 0:
/* 284 */       return of();
/*     */     case 1:
/* 286 */       return new SingletonImmutableMap(entryOf(entries[0].getKey(), entries[0].getValue()));
/*     */     }
/*     */ 
/* 289 */     for (int i = 0; i < entries.length; i++) {
/* 290 */       Object k = entries[i].getKey();
/* 291 */       Object v = entries[i].getValue();
/* 292 */       entries[i] = entryOf(k, v);
/*     */     }
/* 294 */     return new RegularImmutableMap(entries);
/*     */   }
/*     */ 
/*     */   public final V put(K k, V v)
/*     */   {
/* 307 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final V remove(Object o)
/*     */   {
/* 317 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final void putAll(Map<? extends K, ? extends V> map)
/*     */   {
/* 327 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public final void clear()
/*     */   {
/* 337 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 342 */     return size() == 0;
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/* 347 */     return get(key) != null;
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 352 */     return (value != null) && (Maps.containsValueImpl(this, value));
/*     */   }
/*     */ 
/*     */   public abstract V get(@Nullable Object paramObject);
/*     */ 
/*     */   public ImmutableSet<Map.Entry<K, V>> entrySet()
/*     */   {
/* 367 */     ImmutableSet result = this.entrySet;
/* 368 */     return result == null ? (this.entrySet = createEntrySet()) : result;
/*     */   }
/*     */ 
/*     */   abstract ImmutableSet<Map.Entry<K, V>> createEntrySet();
/*     */ 
/*     */   public ImmutableSet<K> keySet()
/*     */   {
/* 419 */     ImmutableSet result = this.keySet;
/* 420 */     return result == null ? (this.keySet = createKeySet()) : result;
/*     */   }
/*     */ 
/*     */   ImmutableSet<K> createKeySet() {
/* 424 */     return new KeySet();
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<V> values()
/*     */   {
/* 491 */     ImmutableCollection result = this.values;
/* 492 */     return result == null ? (this.values = createValues()) : result;
/*     */   }
/*     */ 
/*     */   ImmutableCollection<V> createValues() {
/* 496 */     return new Values();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 553 */     if (object == this) {
/* 554 */       return true;
/*     */     }
/* 556 */     if ((object instanceof Map)) {
/* 557 */       Map that = (Map)object;
/* 558 */       return entrySet().equals(that.entrySet());
/*     */     }
/* 560 */     return false;
/*     */   }
/*     */ 
/*     */   abstract boolean isPartialView();
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 568 */     return entrySet().hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 572 */     return Maps.toStringImpl(this);
/*     */   }
/*     */ 
/*     */   Object writeReplace()
/*     */   {
/* 607 */     return new SerializedForm(this);
/*     */   }
/*     */ 
/*     */   static class SerializedForm
/*     */     implements Serializable
/*     */   {
/*     */     private final Object[] keys;
/*     */     private final Object[] values;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SerializedForm(ImmutableMap<?, ?> map)
/*     */     {
/* 584 */       this.keys = new Object[map.size()];
/* 585 */       this.values = new Object[map.size()];
/* 586 */       int i = 0;
/* 587 */       for (Map.Entry entry : map.entrySet()) {
/* 588 */         this.keys[i] = entry.getKey();
/* 589 */         this.values[i] = entry.getValue();
/* 590 */         i++;
/*     */       }
/*     */     }
/*     */ 
/* 594 */     Object readResolve() { ImmutableMap.Builder builder = new ImmutableMap.Builder();
/* 595 */       return createMap(builder); }
/*     */ 
/*     */     Object createMap(ImmutableMap.Builder<Object, Object> builder) {
/* 598 */       for (int i = 0; i < this.keys.length; i++) {
/* 599 */         builder.put(this.keys[i], this.values[i]);
/*     */       }
/* 601 */       return builder.build();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ValuesSerializedForm<V>
/*     */     implements Serializable
/*     */   {
/*     */     final ImmutableMap<?, V> map;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     ValuesSerializedForm(ImmutableMap<?, V> map)
/*     */     {
/* 544 */       this.map = map;
/*     */     }
/*     */     Object readResolve() {
/* 547 */       return this.map.values();
/*     */     }
/*     */   }
/*     */ 
/*     */   class Values extends ImmutableCollection<V>
/*     */   {
/*     */     Values()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 502 */       return ImmutableMap.this.size();
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<V> iterator()
/*     */     {
/* 507 */       return Maps.valueIterator(ImmutableMap.this.entrySet().iterator());
/*     */     }
/*     */ 
/*     */     public boolean contains(Object object)
/*     */     {
/* 512 */       return ImmutableMap.this.containsValue(object);
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 517 */       return true;
/*     */     }
/*     */ 
/*     */     ImmutableList<V> createAsList()
/*     */     {
/* 522 */       final ImmutableList entryList = ImmutableMap.this.entrySet().asList();
/* 523 */       return new ImmutableAsList()
/*     */       {
/*     */         public V get(int index) {
/* 526 */           return ((Map.Entry)entryList.get(index)).getValue();
/*     */         }
/*     */ 
/*     */         ImmutableCollection<V> delegateCollection()
/*     */         {
/* 531 */           return ImmutableMap.Values.this;
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     Object writeReplace() {
/* 537 */       return new ImmutableMap.ValuesSerializedForm(ImmutableMap.this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class KeySetSerializedForm<K>
/*     */     implements Serializable
/*     */   {
/*     */     final ImmutableMap<K, ?> map;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     KeySetSerializedForm(ImmutableMap<K, ?> map)
/*     */     {
/* 475 */       this.map = map;
/*     */     }
/*     */     Object readResolve() {
/* 478 */       return this.map.keySet();
/*     */     }
/*     */   }
/*     */ 
/*     */   class KeySet extends ImmutableSet.TransformedImmutableSet<Map.Entry<K, V>, K>
/*     */   {
/*     */     KeySet()
/*     */     {
/* 429 */       super();
/*     */     }
/*     */ 
/*     */     KeySet(int hashCode) {
/* 433 */       super(hashCode);
/*     */     }
/*     */ 
/*     */     K transform(Map.Entry<K, V> entry)
/*     */     {
/* 438 */       return entry.getKey();
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object object)
/*     */     {
/* 443 */       return ImmutableMap.this.containsKey(object);
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 448 */       return true;
/*     */     }
/*     */ 
/*     */     ImmutableList<K> createAsList()
/*     */     {
/* 453 */       final ImmutableList entryList = ImmutableMap.this.entrySet().asList();
/* 454 */       return new ImmutableAsList()
/*     */       {
/*     */         public K get(int index) {
/* 457 */           return ((Map.Entry)entryList.get(index)).getKey();
/*     */         }
/*     */ 
/*     */         ImmutableCollection<K> delegateCollection()
/*     */         {
/* 462 */           return ImmutableMap.KeySet.this;
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     Object writeReplace() {
/* 468 */       return new ImmutableMap.KeySetSerializedForm(ImmutableMap.this);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class EntrySetSerializedForm<K, V>
/*     */     implements Serializable
/*     */   {
/*     */     final ImmutableMap<K, V> map;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     EntrySetSerializedForm(ImmutableMap<K, V> map)
/*     */     {
/* 403 */       this.map = map;
/*     */     }
/*     */     Object readResolve() {
/* 406 */       return this.map.entrySet();
/*     */     }
/*     */   }
/*     */ 
/*     */   abstract class EntrySet extends ImmutableSet<Map.Entry<K, V>>
/*     */   {
/*     */     EntrySet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 376 */       return ImmutableMap.this.size();
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object object)
/*     */     {
/* 381 */       if ((object instanceof Map.Entry)) {
/* 382 */         Map.Entry entry = (Map.Entry)object;
/* 383 */         Object value = ImmutableMap.this.get(entry.getKey());
/* 384 */         return (value != null) && (value.equals(entry.getValue()));
/*     */       }
/* 386 */       return false;
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 391 */       return ImmutableMap.this.isPartialView();
/*     */     }
/*     */ 
/*     */     Object writeReplace()
/*     */     {
/* 396 */       return new ImmutableMap.EntrySetSerializedForm(ImmutableMap.this);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class Builder<K, V>
/*     */   {
/* 170 */     final ArrayList<Map.Entry<K, V>> entries = Lists.newArrayList();
/*     */ 
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 183 */       this.entries.add(ImmutableMap.entryOf(key, value));
/* 184 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry)
/*     */     {
/* 195 */       Object key = entry.getKey();
/* 196 */       Object value = entry.getValue();
/* 197 */       if ((entry instanceof ImmutableEntry)) {
/* 198 */         Preconditions.checkNotNull(key);
/* 199 */         Preconditions.checkNotNull(value);
/*     */ 
/* 201 */         Map.Entry immutableEntry = entry;
/* 202 */         this.entries.add(immutableEntry);
/*     */       }
/*     */       else
/*     */       {
/* 206 */         this.entries.add(ImmutableMap.entryOf(key, value));
/*     */       }
/* 208 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Map<? extends K, ? extends V> map)
/*     */     {
/* 218 */       this.entries.ensureCapacity(this.entries.size() + map.size());
/* 219 */       for (Map.Entry entry : map.entrySet()) {
/* 220 */         put(entry.getKey(), entry.getValue());
/*     */       }
/* 222 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableMap<K, V> build()
/*     */     {
/* 236 */       return fromEntryList(this.entries);
/*     */     }
/*     */ 
/*     */     private static <K, V> ImmutableMap<K, V> fromEntryList(List<Map.Entry<K, V>> entries)
/*     */     {
/* 241 */       int size = entries.size();
/* 242 */       switch (size) {
/*     */       case 0:
/* 244 */         return ImmutableMap.of();
/*     */       case 1:
/* 246 */         return new SingletonImmutableMap((Map.Entry)Iterables.getOnlyElement(entries));
/*     */       }
/* 248 */       Map.Entry[] entryArray = (Map.Entry[])entries.toArray(new Map.Entry[entries.size()]);
/*     */ 
/* 250 */       return new RegularImmutableMap(entryArray);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableMap
 * JD-Core Version:    0.6.2
 */