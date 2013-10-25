package org.apache.log4j.rewrite;

import org.apache.log4j.spi.LoggingEvent;

public abstract interface RewritePolicy
{
  public abstract LoggingEvent rewrite(LoggingEvent paramLoggingEvent);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.rewrite.RewritePolicy
 * JD-Core Version:    0.6.2
 */