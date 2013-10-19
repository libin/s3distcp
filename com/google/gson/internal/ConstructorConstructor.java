/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import com.google.gson.InstanceCreator;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.LinkedHashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Map;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import java.util.TreeSet;
/*     */ 
/*     */ public final class ConstructorConstructor
/*     */ {
/*     */   private final Map<Type, InstanceCreator<?>> instanceCreators;
/*     */ 
/*     */   public ConstructorConstructor(Map<Type, InstanceCreator<?>> instanceCreators)
/*     */   {
/*  43 */     this.instanceCreators = instanceCreators;
/*     */   }
/*     */ 
/*     */   public ConstructorConstructor() {
/*  47 */     this(Collections.emptyMap());
/*     */   }
/*     */ 
/*     */   public <T> ObjectConstructor<T> getConstructor(TypeToken<T> typeToken) {
/*  51 */     final Type type = typeToken.getType();
/*  52 */     Class rawType = typeToken.getRawType();
/*     */ 
/*  57 */     final InstanceCreator creator = (InstanceCreator)this.instanceCreators.get(type);
/*  58 */     if (creator != null) {
/*  59 */       return new ObjectConstructor() {
/*     */         public T construct() {
/*  61 */           return creator.createInstance(type);
/*     */         }
/*     */       };
/*     */     }
/*     */ 
/*  66 */     ObjectConstructor defaultConstructor = newDefaultConstructor(rawType);
/*  67 */     if (defaultConstructor != null) {
/*  68 */       return defaultConstructor;
/*     */     }
/*     */ 
/*  71 */     ObjectConstructor defaultImplementation = newDefaultImplementationConstructor(rawType);
/*  72 */     if (defaultImplementation != null) {
/*  73 */       return defaultImplementation;
/*     */     }
/*     */ 
/*  77 */     return newUnsafeAllocator(type, rawType);
/*     */   }
/*     */ 
/*     */   private <T> ObjectConstructor<T> newDefaultConstructor(Class<? super T> rawType) {
/*     */     try {
/*  82 */       final Constructor constructor = rawType.getDeclaredConstructor(new Class[0]);
/*  83 */       if (!constructor.isAccessible()) {
/*  84 */         constructor.setAccessible(true);
/*     */       }
/*  86 */       return new ObjectConstructor()
/*     */       {
/*     */         public T construct() {
/*     */           try {
/*  90 */             Object[] args = null;
/*  91 */             return constructor.newInstance(args);
/*     */           }
/*     */           catch (InstantiationException e) {
/*  94 */             throw new RuntimeException("Failed to invoke " + constructor + " with no args", e);
/*     */           }
/*     */           catch (InvocationTargetException e)
/*     */           {
/*  98 */             throw new RuntimeException("Failed to invoke " + constructor + " with no args", e.getTargetException());
/*     */           }
/*     */           catch (IllegalAccessException e) {
/* 101 */             throw new AssertionError(e);
/*     */           }
/*     */         } } ;
/*     */     } catch (NoSuchMethodException e) {
/*     */     }
/* 106 */     return null;
/*     */   }
/*     */ 
/*     */   private <T> ObjectConstructor<T> newDefaultImplementationConstructor(Class<? super T> rawType)
/*     */   {
/* 116 */     if (Collection.class.isAssignableFrom(rawType)) {
/* 117 */       if (SortedSet.class.isAssignableFrom(rawType))
/* 118 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 120 */             return new TreeSet();
/*     */           }
/*     */         };
/* 123 */       if (Set.class.isAssignableFrom(rawType))
/* 124 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 126 */             return new LinkedHashSet();
/*     */           }
/*     */         };
/* 129 */       if (Queue.class.isAssignableFrom(rawType)) {
/* 130 */         return new ObjectConstructor() {
/*     */           public T construct() {
/* 132 */             return new LinkedList();
/*     */           }
/*     */         };
/*     */       }
/* 136 */       return new ObjectConstructor() {
/*     */         public T construct() {
/* 138 */           return new ArrayList();
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/* 144 */     if (Map.class.isAssignableFrom(rawType)) {
/* 145 */       return new ObjectConstructor() {
/*     */         public T construct() {
/* 147 */           return new LinkedHashMap();
/*     */         }
/*     */ 
/*     */       };
/*     */     }
/*     */ 
/* 153 */     return null;
/*     */   }
/*     */ 
/*     */   private <T> ObjectConstructor<T> newUnsafeAllocator(final Type type, final Class<? super T> rawType)
/*     */   {
/* 158 */     return new ObjectConstructor() {
/* 159 */       private final UnsafeAllocator unsafeAllocator = UnsafeAllocator.create();
/*     */ 
/*     */       public T construct() {
/*     */         try {
/* 163 */           return this.unsafeAllocator.newInstance(rawType);
/*     */         }
/*     */         catch (Exception e) {
/* 166 */           throw new RuntimeException("Unable to invoke no-args constructor for " + type + ". " + "Register an InstanceCreator with Gson for this type may fix this problem.", e);
/*     */         }
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 174 */     return this.instanceCreators.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.ConstructorConstructor
 * JD-Core Version:    0.6.2
 */