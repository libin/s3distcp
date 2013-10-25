package com.google.common.hash;

import com.google.common.annotations.Beta;
import java.io.Serializable;

@Beta
public abstract interface Funnel<T> extends Serializable
{
  public abstract void funnel(T paramT, PrimitiveSink paramPrimitiveSink);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.Funnel
 * JD-Core Version:    0.6.2
 */