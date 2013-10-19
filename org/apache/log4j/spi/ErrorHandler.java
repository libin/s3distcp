package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

public abstract interface ErrorHandler extends OptionHandler
{
  public abstract void setLogger(Logger paramLogger);

  public abstract void error(String paramString, Exception paramException, int paramInt);

  public abstract void error(String paramString);

  public abstract void error(String paramString, Exception paramException, int paramInt, LoggingEvent paramLoggingEvent);

  public abstract void setAppender(Appender paramAppender);

  public abstract void setBackupAppender(Appender paramAppender);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.ErrorHandler
 * JD-Core Version:    0.6.2
 */