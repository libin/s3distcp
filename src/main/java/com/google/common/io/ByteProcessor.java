package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public abstract interface ByteProcessor<T>
{
  public abstract boolean processBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    throws IOException;

  public abstract T getResult();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.ByteProcessor
 * JD-Core Version:    0.6.2
 */