package com.amazonaws.http;

public abstract interface HttpResponseHandler<T>
{
  public abstract T handle(HttpResponse paramHttpResponse)
    throws Exception;

  public abstract boolean needsConnectionLeftOpen();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.HttpResponseHandler
 * JD-Core Version:    0.6.2
 */