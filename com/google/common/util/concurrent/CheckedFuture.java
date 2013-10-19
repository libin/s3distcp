package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Beta
public abstract interface CheckedFuture<V, X extends Exception> extends ListenableFuture<V>
{
  public abstract V checkedGet()
    throws Exception;

  public abstract V checkedGet(long paramLong, TimeUnit paramTimeUnit)
    throws TimeoutException, Exception;
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.CheckedFuture
 * JD-Core Version:    0.6.2
 */