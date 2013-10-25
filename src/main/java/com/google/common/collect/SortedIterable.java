package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Comparator;
import java.util.Iterator;

@GwtCompatible
abstract interface SortedIterable<T> extends Iterable<T>
{
  public abstract Comparator<? super T> comparator();

  public abstract Iterator<T> iterator();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.SortedIterable
 * JD-Core Version:    0.6.2
 */