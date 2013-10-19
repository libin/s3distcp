/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class Primitives
/*     */ {
/*  57 */   private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
/*  58 */   private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
/*     */ 
/*     */   private static void add(Map<Class<?>, Class<?>> forward, Map<Class<?>, Class<?>> backward, Class<?> key, Class<?> value)
/*     */   {
/*  63 */     forward.put(key, value);
/*  64 */     backward.put(value, key);
/*     */   }
/*     */ 
/*     */   public static boolean isPrimitive(Type type)
/*     */   {
/*  71 */     return PRIMITIVE_TO_WRAPPER_TYPE.containsKey(type);
/*     */   }
/*     */ 
/*     */   public static boolean isWrapperType(Type type)
/*     */   {
/*  81 */     return WRAPPER_TO_PRIMITIVE_TYPE.containsKey(.Gson.Preconditions.checkNotNull(type));
/*     */   }
/*     */ 
/*     */   public static <T> Class<T> wrap(Class<T> type)
/*     */   {
/*  97 */     Class wrapped = (Class)PRIMITIVE_TO_WRAPPER_TYPE.get(.Gson.Preconditions.checkNotNull(type));
/*     */ 
/*  99 */     return wrapped == null ? type : wrapped;
/*     */   }
/*     */ 
/*     */   public static <T> Class<T> unwrap(Class<T> type)
/*     */   {
/* 114 */     Class unwrapped = (Class)WRAPPER_TO_PRIMITIVE_TYPE.get(.Gson.Preconditions.checkNotNull(type));
/*     */ 
/* 116 */     return unwrapped == null ? type : unwrapped;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  44 */     Map primToWrap = new HashMap(16);
/*  45 */     Map wrapToPrim = new HashMap(16);
/*     */ 
/*  47 */     add(primToWrap, wrapToPrim, Boolean.TYPE, Boolean.class);
/*  48 */     add(primToWrap, wrapToPrim, Byte.TYPE, Byte.class);
/*  49 */     add(primToWrap, wrapToPrim, Character.TYPE, Character.class);
/*  50 */     add(primToWrap, wrapToPrim, Double.TYPE, Double.class);
/*  51 */     add(primToWrap, wrapToPrim, Float.TYPE, Float.class);
/*  52 */     add(primToWrap, wrapToPrim, Integer.TYPE, Integer.class);
/*  53 */     add(primToWrap, wrapToPrim, Long.TYPE, Long.class);
/*  54 */     add(primToWrap, wrapToPrim, Short.TYPE, Short.class);
/*  55 */     add(primToWrap, wrapToPrim, Void.TYPE, Void.class);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.Primitives
 * JD-Core Version:    0.6.2
 */