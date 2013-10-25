package com.google.common.io;

import java.io.IOException;

public abstract interface OutputSupplier<T>
{
  public abstract T getOutput()
    throws IOException;
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.OutputSupplier
 * JD-Core Version:    0.6.2
 */