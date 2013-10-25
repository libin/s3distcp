package org.apache.log4j.xml;

import java.util.Properties;
import org.w3c.dom.Element;

public abstract interface UnrecognizedElementHandler
{
  public abstract boolean parseUnrecognizedElement(Element paramElement, Properties paramProperties)
    throws Exception;
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.xml.UnrecognizedElementHandler
 * JD-Core Version:    0.6.2
 */