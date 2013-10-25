package com.amazonaws.handlers;

import com.amazonaws.Request;
import com.amazonaws.util.TimingInfo;

public abstract interface RequestHandler
{
  public abstract void beforeRequest(Request<?> paramRequest);

  public abstract void afterResponse(Request<?> paramRequest, Object paramObject, TimingInfo paramTimingInfo);

  public abstract void afterError(Request<?> paramRequest, Exception paramException);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.handlers.RequestHandler
 * JD-Core Version:    0.6.2
 */