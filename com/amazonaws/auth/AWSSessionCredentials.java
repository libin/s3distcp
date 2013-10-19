package com.amazonaws.auth;

public abstract interface AWSSessionCredentials extends AWSCredentials
{
  public abstract String getSessionToken();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AWSSessionCredentials
 * JD-Core Version:    0.6.2
 */