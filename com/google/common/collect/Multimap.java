package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface Multimap<K, V>
{
  public abstract int size();

  public abstract boolean isEmpty();

  public abstract boolean containsKey(@Nullable Object paramObject);

  public abstract boolean containsValue(@Nullable Object paramObject);

  public abstract boolean containsEntry(@Nullable Object paramObject1, @Nullable Object paramObject2);

  public abstract boolean put(@Nullable K paramK, @Nullable V paramV);

  public abstract boolean remove(@Nullable Object paramObject1, @Nullable Object paramObject2);

  public abstract boolean putAll(@Nullable K paramK, Iterable<? extends V> paramIterable);

  public abstract boolean putAll(Multimap<? extends K, ? extends V> paramMultimap);

  public abstract Collection<V> replaceValues(@Nullable K paramK, Iterable<? extends V> paramIterable);

  public abstract Collection<V> removeAll(@Nullable Object paramObject);

  public abstract void clear();

  public abstract Collection<V> get(@Nullable K paramK);

  public abstract Set<K> keySet();

  public abstract Multiset<K> keys();

  public abstract Collection<V> values();

  public abstract Collection<Map.Entry<K, V>> entries();

  public abstract Map<K, Collection<V>> asMap();

  public abstract boolean equals(@Nullable Object paramObject);

  public abstract int hashCode();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Multimap
 * JD-Core Version:    0.6.2
 */