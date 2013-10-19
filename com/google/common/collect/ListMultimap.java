package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

@GwtCompatible
public abstract interface ListMultimap<K, V> extends Multimap<K, V>
{
  public abstract List<V> get(@Nullable K paramK);

  public abstract List<V> removeAll(@Nullable Object paramObject);

  public abstract List<V> replaceValues(K paramK, Iterable<? extends V> paramIterable);

  public abstract Map<K, Collection<V>> asMap();

  public abstract boolean equals(@Nullable Object paramObject);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ListMultimap
 * JD-Core Version:    0.6.2
 */