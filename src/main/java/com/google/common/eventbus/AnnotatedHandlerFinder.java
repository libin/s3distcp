/*     */ package com.google.common.eventbus;
/*     */ 
/*     */ import com.google.common.collect.HashMultimap;
/*     */ import com.google.common.collect.Lists;
/*     */ import com.google.common.collect.Multimap;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Arrays;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ 
/*     */ class AnnotatedHandlerFinder
/*     */   implements HandlerFindingStrategy
/*     */ {
/*     */   static Set<Class<?>> getAllSuperclasses(Class<?> clazz)
/*     */   {
/*  43 */     Queue queue = Lists.newLinkedList();
/*  44 */     Set supers = Sets.newHashSet();
/*  45 */     queue.add(clazz);
/*  46 */     while (!queue.isEmpty()) {
/*  47 */       Class c = (Class)queue.poll();
/*  48 */       if (supers.add(c)) {
/*  49 */         queue.addAll(Arrays.asList(c.getInterfaces()));
/*  50 */         if (c.getSuperclass() != null) {
/*  51 */           queue.add(c.getSuperclass());
/*     */         }
/*     */       }
/*     */     }
/*  55 */     return supers;
/*     */   }
/*     */ 
/*     */   public Multimap<Class<?>, EventHandler> findAllHandlers(Object listener)
/*     */   {
/*  65 */     Multimap methodsInListener = HashMultimap.create();
/*  66 */     Class clazz = listener.getClass();
/*  67 */     Set supers = getAllSuperclasses(clazz);
/*     */     Method method;
/*  69 */     for (method : clazz.getMethods())
/*     */     {
/*  74 */       for (Class c : supers)
/*     */         try {
/*  76 */           Method m = c.getMethod(method.getName(), method.getParameterTypes());
/*  77 */           if (m.isAnnotationPresent(Subscribe.class)) {
/*  78 */             Class[] parameterTypes = method.getParameterTypes();
/*  79 */             if (parameterTypes.length != 1) {
/*  80 */               throw new IllegalArgumentException("Method " + method + " has @Subscribe annotation, but requires " + parameterTypes.length + " arguments.  Event handler methods must require a single argument.");
/*     */             }
/*     */ 
/*  84 */             Class eventType = parameterTypes[0];
/*  85 */             EventHandler handler = makeHandler(listener, method);
/*     */ 
/*  87 */             methodsInListener.put(eventType, handler);
/*  88 */             break;
/*     */           }
/*     */         }
/*     */         catch (NoSuchMethodException ignored)
/*     */         {
/*     */         }
/*     */     }
/*  95 */     return methodsInListener;
/*     */   }
/*     */ 
/*     */   private static EventHandler makeHandler(Object listener, Method method)
/*     */   {
/*     */     EventHandler wrapper;
/*     */     EventHandler wrapper;
/* 111 */     if (methodIsDeclaredThreadSafe(method))
/* 112 */       wrapper = new EventHandler(listener, method);
/*     */     else {
/* 114 */       wrapper = new SynchronizedEventHandler(listener, method);
/*     */     }
/* 116 */     return wrapper;
/*     */   }
/*     */ 
/*     */   private static boolean methodIsDeclaredThreadSafe(Method method)
/*     */   {
/* 128 */     return method.getAnnotation(AllowConcurrentEvents.class) != null;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.eventbus.AnnotatedHandlerFinder
 * JD-Core Version:    0.6.2
 */