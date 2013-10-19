package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

@GwtCompatible
@Beta
public abstract interface RowSortedTable<R, C, V> extends Table<R, C, V>
{
  public abstract SortedSet<R> rowKeySet();

  public abstract SortedMap<R, Map<C, V>> rowMap();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.RowSortedTable
 * JD-Core Version:    0.6.2
 */