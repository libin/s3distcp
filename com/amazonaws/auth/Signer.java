package com.amazonaws.auth;

import com.amazonaws.AmazonClientException;
import com.amazonaws.Request;

public abstract interface Signer
{
  public abstract void sign(Request<?> paramRequest, AWSCredentials paramAWSCredentials)
    throws AmazonClientException;
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.Signer
 * JD-Core Version:    0.6.2
 */