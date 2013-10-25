/*    */ package com.google.common.base;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ public final class Defaults
/*    */ {
/* 44 */   private static final Map<Class<?>, Object> DEFAULTS = Collections.unmodifiableMap(map);
/*    */ 
/*    */   private static <T> void put(Map<Class<?>, Object> map, Class<T> type, T value)
/*    */   {
/* 48 */     map.put(type, value);
/*    */   }
/*    */ 
/*    */   public static <T> T defaultValue(Class<T> type)
/*    */   {
/* 58 */     return DEFAULTS.get(type);
/*    */   }
/*    */ 
/*    */   static
/*    */   {
/* 35 */     Map map = new HashMap();
/* 36 */     put(map, Boolean.TYPE, Boolean.valueOf(false));
/* 37 */     put(map, Character.TYPE, Character.valueOf('\000'));
/* 38 */     put(map, Byte.TYPE, Byte.valueOf((byte)0));
/* 39 */     put(map, Short.TYPE, Short.valueOf((short)0));
/* 40 */     put(map, Integer.TYPE, Integer.valueOf(0));
/* 41 */     put(map, Long.TYPE, Long.valueOf(0L));
/* 42 */     put(map, Float.TYPE, Float.valueOf(0.0F));
/* 43 */     put(map, Double.TYPE, Double.valueOf(0.0D));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Defaults
 * JD-Core Version:    0.6.2
 */