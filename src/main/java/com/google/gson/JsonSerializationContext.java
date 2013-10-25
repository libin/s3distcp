package com.google.gson;

import java.lang.reflect.Type;

public abstract interface JsonSerializationContext
{
  public abstract JsonElement serialize(Object paramObject);

  public abstract JsonElement serialize(Object paramObject, Type paramType);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonSerializationContext
 * JD-Core Version:    0.6.2
 */