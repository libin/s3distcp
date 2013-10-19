/*     */ package com.google.common.eventbus;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.base.Throwables;
/*     */ import com.google.common.cache.CacheBuilder;
/*     */ import com.google.common.cache.CacheLoader;
/*     */ import com.google.common.cache.LoadingCache;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Multimap;
/*     */ import com.google.common.collect.Multimaps;
/*     */ import com.google.common.collect.SetMultimap;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.util.Collection;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.CopyOnWriteArraySet;
/*     */ import java.util.concurrent.ExecutionException;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ 
/*     */ @Beta
/*     */ public class EventBus
/*     */ {
/* 118 */   private final SetMultimap<Class<?>, EventHandler> handlersByType = Multimaps.newSetMultimap(new ConcurrentHashMap(), new Supplier()
/*     */   {
/*     */     public Set<EventHandler> get()
/*     */     {
/* 123 */       return new CopyOnWriteArraySet();
/*     */     }
/*     */   });
/*     */   private final Logger logger;
/* 138 */   private final HandlerFindingStrategy finder = new AnnotatedHandlerFinder();
/*     */ 
/* 141 */   private final ThreadLocal<ConcurrentLinkedQueue<EventWithHandler>> eventsToDispatch = new ThreadLocal()
/*     */   {
/*     */     protected ConcurrentLinkedQueue<EventBus.EventWithHandler> initialValue()
/*     */     {
/* 145 */       return new ConcurrentLinkedQueue();
/*     */     }
/* 141 */   };
/*     */ 
/* 150 */   private final ThreadLocal<Boolean> isDispatching = new ThreadLocal()
/*     */   {
/*     */     protected Boolean initialValue() {
/* 153 */       return Boolean.valueOf(false);
/*     */     }
/* 150 */   };
/*     */ 
/* 160 */   private LoadingCache<Class<?>, Set<Class<?>>> flattenHierarchyCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader()
/*     */   {
/*     */     public Set<Class<?>> load(Class<?> concreteClass)
/*     */       throws Exception
/*     */     {
/* 166 */       List parents = Lists.newLinkedList();
/* 167 */       Set classes = Sets.newHashSet();
/*     */ 
/* 169 */       parents.add(concreteClass);
/*     */ 
/* 171 */       while (!parents.isEmpty()) {
/* 172 */         Class clazz = (Class)parents.remove(0);
/* 173 */         classes.add(clazz);
/*     */ 
/* 175 */         Class parent = clazz.getSuperclass();
/* 176 */         if (parent != null) {
/* 177 */           parents.add(parent);
/*     */         }
/*     */ 
/* 180 */         for (Class iface : clazz.getInterfaces()) {
/* 181 */           parents.add(iface);
/*     */         }
/*     */       }
/*     */ 
/* 185 */       return classes;
/*     */     }
/*     */   });
/*     */ 
/*     */   public EventBus()
/*     */   {
/* 193 */     this("default");
/*     */   }
/*     */ 
/*     */   public EventBus(String identifier)
/*     */   {
/* 203 */     this.logger = Logger.getLogger(EventBus.class.getName() + "." + identifier);
/*     */   }
/*     */ 
/*     */   public void register(Object object)
/*     */   {
/* 215 */     this.handlersByType.putAll(this.finder.findAllHandlers(object));
/*     */   }
/*     */ 
/*     */   public void unregister(Object object)
/*     */   {
/* 225 */     Multimap methodsInListener = this.finder.findAllHandlers(object);
/* 226 */     for (Map.Entry entry : methodsInListener.asMap().entrySet()) {
/* 227 */       Set currentHandlers = getHandlersForEventType((Class)entry.getKey());
/* 228 */       Collection eventMethodsInListener = (Collection)entry.getValue();
/*     */ 
/* 230 */       if ((currentHandlers == null) || (!currentHandlers.containsAll((Collection)entry.getValue()))) {
/* 231 */         throw new IllegalArgumentException("missing event handler for an annotated method. Is " + object + " registered?");
/*     */       }
/*     */ 
/* 234 */       currentHandlers.removeAll(eventMethodsInListener);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void post(Object event)
/*     */   {
/* 250 */     Set dispatchTypes = flattenHierarchy(event.getClass());
/*     */ 
/* 252 */     boolean dispatched = false;
/* 253 */     for (Class eventType : dispatchTypes) {
/* 254 */       Set wrappers = getHandlersForEventType(eventType);
/*     */ 
/* 256 */       if ((wrappers != null) && (!wrappers.isEmpty())) {
/* 257 */         dispatched = true;
/* 258 */         for (EventHandler wrapper : wrappers) {
/* 259 */           enqueueEvent(event, wrapper);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 264 */     if ((!dispatched) && (!(event instanceof DeadEvent))) {
/* 265 */       post(new DeadEvent(this, event));
/*     */     }
/*     */ 
/* 268 */     dispatchQueuedEvents();
/*     */   }
/*     */ 
/*     */   protected void enqueueEvent(Object event, EventHandler handler)
/*     */   {
/* 277 */     ((ConcurrentLinkedQueue)this.eventsToDispatch.get()).offer(new EventWithHandler(event, handler));
/*     */   }
/*     */ 
/*     */   protected void dispatchQueuedEvents()
/*     */   {
/* 288 */     if (((Boolean)this.isDispatching.get()).booleanValue()) {
/* 289 */       return;
/*     */     }
/*     */ 
/* 292 */     this.isDispatching.set(Boolean.valueOf(true));
/*     */     try {
/*     */       while (true) {
/* 295 */         EventWithHandler eventWithHandler = (EventWithHandler)((ConcurrentLinkedQueue)this.eventsToDispatch.get()).poll();
/* 296 */         if (eventWithHandler == null)
/*     */         {
/*     */           break;
/*     */         }
/* 300 */         dispatch(eventWithHandler.event, eventWithHandler.handler);
/*     */       }
/*     */     } finally {
/* 303 */       this.isDispatching.set(Boolean.valueOf(false));
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void dispatch(Object event, EventHandler wrapper)
/*     */   {
/*     */     try
/*     */     {
/* 317 */       wrapper.handleEvent(event);
/*     */     } catch (InvocationTargetException e) {
/* 319 */       this.logger.log(Level.SEVERE, "Could not dispatch event: " + event + " to handler " + wrapper, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   Set<EventHandler> getHandlersForEventType(Class<?> type)
/*     */   {
/* 333 */     return this.handlersByType.get(type);
/*     */   }
/*     */ 
/*     */   protected Set<EventHandler> newHandlerSet()
/*     */   {
/* 344 */     return new CopyOnWriteArraySet();
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   Set<Class<?>> flattenHierarchy(Class<?> concreteClass)
/*     */   {
/*     */     try
/*     */     {
/* 358 */       return (Set)this.flattenHierarchyCache.get(concreteClass);
/*     */     } catch (ExecutionException e) {
/* 360 */       throw Throwables.propagate(e.getCause());
/*     */     }
/*     */   }
/*     */ 
/*     */   static class EventWithHandler {
/*     */     final Object event;
/*     */     final EventHandler handler;
/*     */ 
/* 369 */     public EventWithHandler(Object event, EventHandler handler) { this.event = event;
/* 370 */       this.handler = handler;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.eventbus.EventBus
 * JD-Core Version:    0.6.2
 */