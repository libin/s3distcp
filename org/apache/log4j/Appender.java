package org.apache.log4j;

import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public abstract interface Appender
{
  public abstract void addFilter(Filter paramFilter);

  public abstract Filter getFilter();

  public abstract void clearFilters();

  public abstract void close();

  public abstract void doAppend(LoggingEvent paramLoggingEvent);

  public abstract String getName();

  public abstract void setErrorHandler(ErrorHandler paramErrorHandler);

  public abstract ErrorHandler getErrorHandler();

  public abstract void setLayout(Layout paramLayout);

  public abstract Layout getLayout();

  public abstract void setName(String paramString);

  public abstract boolean requiresLayout();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.Appender
 * JD-Core Version:    0.6.2
 */