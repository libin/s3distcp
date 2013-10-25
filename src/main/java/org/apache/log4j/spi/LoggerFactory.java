package org.apache.log4j.spi;

import org.apache.log4j.Logger;

public abstract interface LoggerFactory
{
  public abstract Logger makeNewLoggerInstance(String paramString);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.LoggerFactory
 * JD-Core Version:    0.6.2
 */