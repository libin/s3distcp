package com.amazonaws.auth;

public abstract interface AWSCredentials
{
  public abstract String getAWSAccessKeyId();

  public abstract String getAWSSecretKey();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AWSCredentials
 * JD-Core Version:    0.6.2
 */