package com.amazonaws;

import com.amazonaws.http.HttpMethodName;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public abstract interface Request<T>
{
  public abstract void addHeader(String paramString1, String paramString2);

  public abstract Map<String, String> getHeaders();

  public abstract void setHeaders(Map<String, String> paramMap);

  public abstract void setResourcePath(String paramString);

  public abstract String getResourcePath();

  public abstract void addParameter(String paramString1, String paramString2);

  public abstract Request<T> withParameter(String paramString1, String paramString2);

  public abstract Map<String, String> getParameters();

  public abstract void setParameters(Map<String, String> paramMap);

  public abstract URI getEndpoint();

  public abstract void setEndpoint(URI paramURI);

  public abstract HttpMethodName getHttpMethod();

  public abstract void setHttpMethod(HttpMethodName paramHttpMethodName);

  public abstract InputStream getContent();

  public abstract void setContent(InputStream paramInputStream);

  public abstract String getServiceName();

  public abstract AmazonWebServiceRequest getOriginalRequest();
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.Request
 * JD-Core Version:    0.6.2
 */