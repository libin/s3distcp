package com.google.common.eventbus;

import com.google.common.collect.Multimap;

abstract interface HandlerFindingStrategy
{
  public abstract Multimap<Class<?>, EventHandler> findAllHandlers(Object paramObject);
}

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.eventbus.HandlerFindingStrategy
 * JD-Core Version:    0.6.2
 */