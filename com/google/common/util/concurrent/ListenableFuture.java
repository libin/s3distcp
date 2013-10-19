package com.google.common.util.concurrent;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;

public abstract interface ListenableFuture<V> extends Future<V>
{
  public abstract void addListener(Runnable paramRunnable, Executor paramExecutor);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.ListenableFuture
 * JD-Core Version:    0.6.2
 */