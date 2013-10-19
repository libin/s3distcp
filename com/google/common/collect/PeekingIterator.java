package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.Iterator;

@GwtCompatible
public abstract interface PeekingIterator<E> extends Iterator<E>
{
  public abstract E peek();

  public abstract E next();

  public abstract void remove();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.PeekingIterator
 * JD-Core Version:    0.6.2
 */