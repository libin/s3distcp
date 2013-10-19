package org.apache.hadoop.fs.common;

import java.io.IOException;

public abstract interface Abortable
{
  public abstract void abort()
    throws IOException;
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.hadoop.fs.common.Abortable
 * JD-Core Version:    0.6.2
 */