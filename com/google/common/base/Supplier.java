package com.google.common.base;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public abstract interface Supplier<T>
{
  public abstract T get();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Supplier
 * JD-Core Version:    0.6.2
 */