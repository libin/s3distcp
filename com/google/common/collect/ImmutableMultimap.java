/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class ImmutableMultimap<K, V>
/*     */   implements Multimap<K, V>, Serializable
/*     */ {
/*     */   final transient ImmutableMap<K, ? extends ImmutableCollection<V>> map;
/*     */   final transient int size;
/*     */   private transient ImmutableCollection<Map.Entry<K, V>> entries;
/*     */   private transient ImmutableMultiset<K> keys;
/*     */   private transient ImmutableCollection<V> values;
/*     */   private static final long serialVersionUID = 0L;
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of()
/*     */   {
/*  72 */     return ImmutableListMultimap.of();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1)
/*     */   {
/*  79 */     return ImmutableListMultimap.of(k1, v1);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2)
/*     */   {
/*  86 */     return ImmutableListMultimap.of(k1, v1, k2, v2);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3)
/*     */   {
/*  94 */     return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*     */   {
/* 102 */     return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4);
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*     */   {
/* 110 */     return ImmutableListMultimap.of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
/*     */   }
/*     */ 
/*     */   public static <K, V> Builder<K, V> builder()
/*     */   {
/* 120 */     return new Builder();
/*     */   }
/*     */ 
/*     */   public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 295 */     if ((multimap instanceof ImmutableMultimap))
/*     */     {
/* 297 */       ImmutableMultimap kvMultimap = (ImmutableMultimap)multimap;
/*     */ 
/* 299 */       if (!kvMultimap.isPartialView()) {
/* 300 */         return kvMultimap;
/*     */       }
/*     */     }
/* 303 */     return ImmutableListMultimap.copyOf(multimap);
/*     */   }
/*     */ 
/*     */   ImmutableMultimap(ImmutableMap<K, ? extends ImmutableCollection<V>> map, int size)
/*     */   {
/* 324 */     this.map = map;
/* 325 */     this.size = size;
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<V> removeAll(Object key)
/*     */   {
/* 337 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<V> replaceValues(K key, Iterable<? extends V> values)
/*     */   {
/* 348 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public void clear()
/*     */   {
/* 358 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public abstract ImmutableCollection<V> get(K paramK);
/*     */ 
/*     */   @Beta
/*     */   public abstract ImmutableMultimap<V, K> inverse();
/*     */ 
/*     */   public boolean put(K key, V value)
/*     */   {
/* 387 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean putAll(K key, Iterable<? extends V> values)
/*     */   {
/* 397 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean putAll(Multimap<? extends K, ? extends V> multimap)
/*     */   {
/* 407 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   public boolean remove(Object key, Object value)
/*     */   {
/* 417 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ 
/*     */   boolean isPartialView() {
/* 421 */     return this.map.isPartialView();
/*     */   }
/*     */ 
/*     */   public boolean containsEntry(@Nullable Object key, @Nullable Object value)
/*     */   {
/* 428 */     Collection values = (Collection)this.map.get(key);
/* 429 */     return (values != null) && (values.contains(value));
/*     */   }
/*     */ 
/*     */   public boolean containsKey(@Nullable Object key)
/*     */   {
/* 434 */     return this.map.containsKey(key);
/*     */   }
/*     */ 
/*     */   public boolean containsValue(@Nullable Object value)
/*     */   {
/* 439 */     for (Collection valueCollection : this.map.values()) {
/* 440 */       if (valueCollection.contains(value)) {
/* 441 */         return true;
/*     */       }
/*     */     }
/* 444 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 449 */     return this.size == 0;
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/* 454 */     return this.size;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object) {
/* 458 */     if ((object instanceof Multimap)) {
/* 459 */       Multimap that = (Multimap)object;
/* 460 */       return this.map.equals(that.asMap());
/*     */     }
/* 462 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 466 */     return this.map.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 470 */     return this.map.toString();
/*     */   }
/*     */ 
/*     */   public ImmutableSet<K> keySet()
/*     */   {
/* 482 */     return this.map.keySet();
/*     */   }
/*     */ 
/*     */   public ImmutableMap<K, Collection<V>> asMap()
/*     */   {
/* 492 */     return this.map;
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<Map.Entry<K, V>> entries()
/*     */   {
/* 504 */     ImmutableCollection result = this.entries;
/* 505 */     return result == null ? (this.entries = new EntryCollection(this)) : result;
/*     */   }
/*     */ 
/*     */   public ImmutableMultiset<K> keys()
/*     */   {
/* 574 */     ImmutableMultiset result = this.keys;
/* 575 */     return result == null ? (this.keys = createKeys()) : result;
/*     */   }
/*     */ 
/*     */   private ImmutableMultiset<K> createKeys() {
/* 579 */     return new Keys();
/*     */   }
/*     */ 
/*     */   public ImmutableCollection<V> values()
/*     */   {
/* 655 */     ImmutableCollection result = this.values;
/* 656 */     return result == null ? (this.values = new Values(this)) : result;
/*     */   }
/*     */   private static class Values<V> extends ImmutableCollection<V> {
/*     */     final ImmutableMultimap<?, V> multimap;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 663 */     Values(ImmutableMultimap<?, V> multimap) { this.multimap = multimap; }
/*     */ 
/*     */     public UnmodifiableIterator<V> iterator()
/*     */     {
/* 667 */       return Maps.valueIterator(this.multimap.entries().iterator());
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 672 */       return this.multimap.size();
/*     */     }
/*     */ 
/*     */     boolean isPartialView() {
/* 676 */       return true;
/*     */     }
/*     */   }
/*     */ 
/*     */   class Keys extends ImmutableMultiset<K>
/*     */   {
/*     */     Keys()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean contains(@Nullable Object object)
/*     */     {
/* 586 */       return ImmutableMultimap.this.containsKey(object);
/*     */     }
/*     */ 
/*     */     public int count(@Nullable Object element)
/*     */     {
/* 591 */       Collection values = (Collection)ImmutableMultimap.this.map.get(element);
/* 592 */       return values == null ? 0 : values.size();
/*     */     }
/*     */ 
/*     */     public Set<K> elementSet()
/*     */     {
/* 597 */       return ImmutableMultimap.this.keySet();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 602 */       return ImmutableMultimap.this.size();
/*     */     }
/*     */ 
/*     */     ImmutableSet<Multiset.Entry<K>> createEntrySet()
/*     */     {
/* 607 */       return new KeysEntrySet(null);
/*     */     }
/*     */ 
/*     */     boolean isPartialView()
/*     */     {
/* 642 */       return true;
/*     */     }
/*     */ 
/*     */     private class KeysEntrySet extends ImmutableMultiset.EntrySet
/*     */     {
/*     */       private KeysEntrySet()
/*     */       {
/* 610 */         super();
/*     */       }
/*     */       public int size() {
/* 613 */         return ImmutableMultimap.this.keySet().size();
/*     */       }
/*     */ 
/*     */       public UnmodifiableIterator<Multiset.Entry<K>> iterator()
/*     */       {
/* 618 */         return asList().iterator();
/*     */       }
/*     */ 
/*     */       ImmutableList<Multiset.Entry<K>> createAsList()
/*     */       {
/* 623 */         final ImmutableList mapEntries = ImmutableMultimap.this.map.entrySet().asList();
/*     */ 
/* 625 */         return new ImmutableAsList()
/*     */         {
/*     */           public Multiset.Entry<K> get(int index) {
/* 628 */             Map.Entry entry = (Map.Entry)mapEntries.get(index);
/* 629 */             return Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size());
/*     */           }
/*     */ 
/*     */           ImmutableCollection<Multiset.Entry<K>> delegateCollection()
/*     */           {
/* 634 */             return ImmutableMultimap.Keys.KeysEntrySet.this;
/*     */           }
/*     */         };
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class EntryCollection<K, V> extends ImmutableCollection<Map.Entry<K, V>>
/*     */   {
/*     */     final ImmutableMultimap<K, V> multimap;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     EntryCollection(ImmutableMultimap<K, V> multimap)
/*     */     {
/* 514 */       this.multimap = multimap;
/*     */     }
/*     */ 
/*     */     public UnmodifiableIterator<Map.Entry<K, V>> iterator()
/*     */     {
/* 519 */       final Iterator mapIterator = this.multimap.map.entrySet().iterator();
/*     */ 
/* 521 */       return new UnmodifiableIterator() {
/*     */         K key;
/*     */         Iterator<V> valueIterator;
/*     */ 
/*     */         public boolean hasNext() {
/* 527 */           return ((this.key != null) && (this.valueIterator.hasNext())) || (mapIterator.hasNext());
/*     */         }
/*     */ 
/*     */         public Map.Entry<K, V> next()
/*     */         {
/* 533 */           if ((this.key == null) || (!this.valueIterator.hasNext())) {
/* 534 */             Map.Entry entry = (Map.Entry)mapIterator.next();
/*     */ 
/* 536 */             this.key = entry.getKey();
/* 537 */             this.valueIterator = ((ImmutableCollection)entry.getValue()).iterator();
/*     */           }
/* 539 */           return Maps.immutableEntry(this.key, this.valueIterator.next());
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*     */     boolean isPartialView() {
/* 545 */       return this.multimap.isPartialView();
/*     */     }
/*     */ 
/*     */     public int size()
/*     */     {
/* 550 */       return this.multimap.size();
/*     */     }
/*     */ 
/*     */     public boolean contains(Object object) {
/* 554 */       if ((object instanceof Map.Entry)) {
/* 555 */         Map.Entry entry = (Map.Entry)object;
/* 556 */         return this.multimap.containsEntry(entry.getKey(), entry.getValue());
/*     */       }
/* 558 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java serialization is not supported")
/*     */   static class FieldSettersHolder
/*     */   {
/* 315 */     static final Serialization.FieldSetter<ImmutableMultimap> MAP_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "map");
/*     */ 
/* 318 */     static final Serialization.FieldSetter<ImmutableMultimap> SIZE_FIELD_SETTER = Serialization.getFieldSetter(ImmutableMultimap.class, "size");
/*     */   }
/*     */ 
/*     */   public static class Builder<K, V>
/*     */   {
/* 157 */     Multimap<K, V> builderMultimap = new ImmutableMultimap.BuilderMultimap();
/*     */     Comparator<? super K> keyComparator;
/*     */     Comparator<? super V> valueComparator;
/*     */ 
/*     */     public Builder<K, V> put(K key, V value)
/*     */     {
/* 171 */       this.builderMultimap.put(Preconditions.checkNotNull(key), Preconditions.checkNotNull(value));
/* 172 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> put(Map.Entry<? extends K, ? extends V> entry)
/*     */     {
/* 181 */       this.builderMultimap.put(Preconditions.checkNotNull(entry.getKey()), Preconditions.checkNotNull(entry.getValue()));
/*     */ 
/* 183 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, Iterable<? extends V> values)
/*     */     {
/* 194 */       Collection valueList = this.builderMultimap.get(Preconditions.checkNotNull(key));
/* 195 */       for (Iterator i$ = values.iterator(); i$.hasNext(); ) { Object value = i$.next();
/* 196 */         valueList.add(Preconditions.checkNotNull(value));
/*     */       }
/* 198 */       return this;
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(K key, V[] values)
/*     */     {
/* 208 */       return putAll(key, Arrays.asList(values));
/*     */     }
/*     */ 
/*     */     public Builder<K, V> putAll(Multimap<? extends K, ? extends V> multimap)
/*     */     {
/* 222 */       for (Map.Entry entry : multimap.asMap().entrySet()) {
/* 223 */         putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */       }
/* 225 */       return this;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public Builder<K, V> orderKeysBy(Comparator<? super K> keyComparator)
/*     */     {
/* 235 */       this.keyComparator = ((Comparator)Preconditions.checkNotNull(keyComparator));
/* 236 */       return this;
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public Builder<K, V> orderValuesBy(Comparator<? super V> valueComparator)
/*     */     {
/* 246 */       this.valueComparator = ((Comparator)Preconditions.checkNotNull(valueComparator));
/* 247 */       return this;
/*     */     }
/*     */ 
/*     */     public ImmutableMultimap<K, V> build()
/*     */     {
/* 254 */       if (this.valueComparator != null) {
/* 255 */         for (Collection values : this.builderMultimap.asMap().values()) {
/* 256 */           List list = (List)values;
/* 257 */           Collections.sort(list, this.valueComparator);
/*     */         }
/*     */       }
/* 260 */       if (this.keyComparator != null) {
/* 261 */         Multimap sortedCopy = new ImmutableMultimap.BuilderMultimap();
/* 262 */         List entries = Lists.newArrayList(this.builderMultimap.asMap().entrySet());
/*     */ 
/* 264 */         Collections.sort(entries, Ordering.from(this.keyComparator).onResultOf(new Function()
/*     */         {
/*     */           public K apply(Map.Entry<K, Collection<V>> entry)
/*     */           {
/* 269 */             return entry.getKey();
/*     */           }
/*     */         }));
/* 272 */         for (Map.Entry entry : entries) {
/* 273 */           sortedCopy.putAll(entry.getKey(), (Iterable)entry.getValue());
/*     */         }
/* 275 */         this.builderMultimap = sortedCopy;
/*     */       }
/* 277 */       return ImmutableMultimap.copyOf(this.builderMultimap);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class BuilderMultimap<K, V> extends AbstractMultimap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     BuilderMultimap()
/*     */     {
/* 130 */       super();
/*     */     }
/*     */     Collection<V> createCollection() {
/* 133 */       return Lists.newArrayList();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ImmutableMultimap
 * JD-Core Version:    0.6.2
 */