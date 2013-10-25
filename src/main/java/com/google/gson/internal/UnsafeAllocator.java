/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectStreamClass;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ public abstract class UnsafeAllocator
/*     */ {
/*     */   public abstract <T> T newInstance(Class<T> paramClass)
/*     */     throws Exception;
/*     */ 
/*     */   public static UnsafeAllocator create()
/*     */   {
/*     */     try
/*     */     {
/*  39 */       Class unsafeClass = Class.forName("sun.misc.Unsafe");
/*  40 */       Field f = unsafeClass.getDeclaredField("theUnsafe");
/*  41 */       f.setAccessible(true);
/*  42 */       final Object unsafe = f.get(null);
/*  43 */       Method allocateInstance = unsafeClass.getMethod("allocateInstance", new Class[] { Class.class });
/*  44 */       return new UnsafeAllocator()
/*     */       {
/*     */         public <T> T newInstance(Class<T> c) throws Exception
/*     */         {
/*  48 */           return this.val$allocateInstance.invoke(unsafe, new Object[] { c });
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */     catch (Exception ignored)
/*     */     {
/*     */       try
/*     */       {
/*  60 */         Method newInstance = ObjectInputStream.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Class.class });
/*     */ 
/*  62 */         newInstance.setAccessible(true);
/*  63 */         return new UnsafeAllocator()
/*     */         {
/*     */           public <T> T newInstance(Class<T> c) throws Exception
/*     */           {
/*  67 */             return this.val$newInstance.invoke(null, new Object[] { c, Object.class });
/*     */           }
/*     */ 
/*     */         };
/*     */       }
/*     */       catch (Exception ignored)
/*     */       {
/*     */         try
/*     */         {
/*  79 */           Method getConstructorId = ObjectStreamClass.class.getDeclaredMethod("getConstructorId", new Class[] { Class.class });
/*     */ 
/*  81 */           getConstructorId.setAccessible(true);
/*  82 */           final int constructorId = ((Integer)getConstructorId.invoke(null, new Object[] { Object.class })).intValue();
/*  83 */           Method newInstance = ObjectStreamClass.class.getDeclaredMethod("newInstance", new Class[] { Class.class, Integer.TYPE });
/*     */ 
/*  85 */           newInstance.setAccessible(true);
/*  86 */           return new UnsafeAllocator()
/*     */           {
/*     */             public <T> T newInstance(Class<T> c) throws Exception
/*     */             {
/*  90 */               return this.val$newInstance.invoke(null, new Object[] { c, Integer.valueOf(constructorId) });
/*     */             } } ;
/*     */         }
/*     */         catch (Exception ignored) {
/*     */         }
/*     */       }
/*     */     }
/*  97 */     return new UnsafeAllocator()
/*     */     {
/*     */       public <T> T newInstance(Class<T> c) {
/* 100 */         throw new UnsupportedOperationException("Cannot allocate " + c);
/*     */       }
/*     */     };
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.UnsafeAllocator
 * JD-Core Version:    0.6.2
 */