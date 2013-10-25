package com.amazonaws.handlers;

import com.amazonaws.AmazonWebServiceRequest;

public abstract interface AsyncHandler<REQUEST extends AmazonWebServiceRequest, RESULT>
{
  public abstract void onError(Exception paramException);

  public abstract void onSuccess(REQUEST paramREQUEST, RESULT paramRESULT);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.handlers.AsyncHandler
 * JD-Core Version:    0.6.2
 */