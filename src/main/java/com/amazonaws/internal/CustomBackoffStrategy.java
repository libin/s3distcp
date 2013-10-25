package com.amazonaws.internal;

public abstract class CustomBackoffStrategy
{
  public abstract int getBackoffPeriod(int paramInt);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.internal.CustomBackoffStrategy
 * JD-Core Version:    0.6.2
 */