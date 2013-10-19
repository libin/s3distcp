package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;

@Beta
public abstract interface FutureCallback<V>
{
  public abstract void onSuccess(V paramV);

  public abstract void onFailure(Throwable paramThrowable);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.FutureCallback
 * JD-Core Version:    0.6.2
 */