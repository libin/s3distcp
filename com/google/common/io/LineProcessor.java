package com.google.common.io;

import com.google.common.annotations.Beta;
import java.io.IOException;

@Beta
public abstract interface LineProcessor<T>
{
  public abstract boolean processLine(String paramString)
    throws IOException;

  public abstract T getResult();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.LineProcessor
 * JD-Core Version:    0.6.2
 */