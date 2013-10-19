package com.amazonaws.handlers;

import com.amazonaws.Request;
import com.amazonaws.util.TimingInfo;

public abstract class AbstractRequestHandler
  implements RequestHandler
{
  public void beforeRequest(Request<?> request)
  {
  }

  public void afterResponse(Request<?> request, Object response, TimingInfo timingInfo)
  {
  }

  public void afterError(Request<?> request, Exception e)
  {
  }
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.handlers.AbstractRequestHandler
 * JD-Core Version:    0.6.2
 */