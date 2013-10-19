package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.SortedMap;

@Beta
@GwtCompatible
public abstract interface SortedMapDifference<K, V> extends MapDifference<K, V>
{
  public abstract SortedMap<K, V> entriesOnlyOnLeft();

  public abstract SortedMap<K, V> entriesOnlyOnRight();

  public abstract SortedMap<K, V> entriesInCommon();

  public abstract SortedMap<K, MapDifference.ValueDifference<V>> entriesDiffering();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SortedMapDifference
 * JD-Core Version:    0.6.2
 */