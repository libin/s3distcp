package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.OutputStream;

@Beta
public final class NullOutputStream extends OutputStream
{
  public void write(int b)
  {
  }

  public void write(byte[] b, int off, int len)
  {
  }
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.NullOutputStream
 * JD-Core Version:    0.6.2
 */