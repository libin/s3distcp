package com.google.gson;

public abstract interface ExclusionStrategy
{
  public abstract boolean shouldSkipField(FieldAttributes paramFieldAttributes);

  public abstract boolean shouldSkipClass(Class<?> paramClass);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.ExclusionStrategy
 * JD-Core Version:    0.6.2
 */