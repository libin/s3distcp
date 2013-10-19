package org.apache.log4j.spi;

public abstract interface ThrowableRenderer
{
  public abstract String[] doRender(Throwable paramThrowable);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.ThrowableRenderer
 * JD-Core Version:    0.6.2
 */