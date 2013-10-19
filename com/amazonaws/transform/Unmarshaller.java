package com.amazonaws.transform;

public abstract interface Unmarshaller<T, R>
{
  public abstract T unmarshall(R paramR)
    throws Exception;
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.Unmarshaller
 * JD-Core Version:    0.6.2
 */