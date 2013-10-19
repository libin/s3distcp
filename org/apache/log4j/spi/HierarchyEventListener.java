package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;

public abstract interface HierarchyEventListener
{
  public abstract void addAppenderEvent(Category paramCategory, Appender paramAppender);

  public abstract void removeAppenderEvent(Category paramCategory, Appender paramAppender);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.HierarchyEventListener
 * JD-Core Version:    0.6.2
 */