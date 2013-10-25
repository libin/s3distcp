package com.amazonaws.auth;

public abstract interface AWSCredentialsProvider
{
  public abstract AWSCredentials getCredentials();

  public abstract void refresh();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AWSCredentialsProvider
 * JD-Core Version:    0.6.2
 */