package com.amazonaws.transform;

public abstract interface Marshaller<T, R>
{
  public abstract T marshall(R paramR)
    throws Exception;
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.Marshaller
 * JD-Core Version:    0.6.2
 */