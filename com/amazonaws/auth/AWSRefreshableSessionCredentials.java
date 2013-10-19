package com.amazonaws.auth;

public abstract interface AWSRefreshableSessionCredentials extends AWSSessionCredentials
{
  public abstract void refreshCredentials();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AWSRefreshableSessionCredentials
 * JD-Core Version:    0.6.2
 */