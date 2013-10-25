/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal..Gson.Preconditions;
/*     */ import java.lang.annotation.Annotation;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ 
/*     */ public final class FieldAttributes
/*     */ {
/*     */   private final Field field;
/*     */ 
/*     */   public FieldAttributes(Field f)
/*     */   {
/*  45 */     .Gson.Preconditions.checkNotNull(f);
/*  46 */     this.field = f;
/*     */   }
/*     */ 
/*     */   public Class<?> getDeclaringClass()
/*     */   {
/*  53 */     return this.field.getDeclaringClass();
/*     */   }
/*     */ 
/*     */   public String getName()
/*     */   {
/*  60 */     return this.field.getName();
/*     */   }
/*     */ 
/*     */   public Type getDeclaredType()
/*     */   {
/*  80 */     return this.field.getGenericType();
/*     */   }
/*     */ 
/*     */   public Class<?> getDeclaredClass()
/*     */   {
/* 100 */     return this.field.getType();
/*     */   }
/*     */ 
/*     */   public <T extends Annotation> T getAnnotation(Class<T> annotation)
/*     */   {
/* 111 */     return this.field.getAnnotation(annotation);
/*     */   }
/*     */ 
/*     */   public Collection<Annotation> getAnnotations()
/*     */   {
/* 121 */     return Arrays.asList(this.field.getAnnotations());
/*     */   }
/*     */ 
/*     */   public boolean hasModifier(int modifier)
/*     */   {
/* 135 */     return (this.field.getModifiers() & modifier) != 0;
/*     */   }
/*     */ 
/*     */   Object get(Object instance)
/*     */     throws IllegalAccessException
/*     */   {
/* 146 */     return this.field.get(instance);
/*     */   }
/*     */ 
/*     */   boolean isSynthetic()
/*     */   {
/* 155 */     return this.field.isSynthetic();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.FieldAttributes
 * JD-Core Version:    0.6.2
 */