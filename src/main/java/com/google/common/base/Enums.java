/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Field;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ @Beta
/*     */ public final class Enums
/*     */ {
/*     */   @GwtIncompatible("reflection")
/*     */   public static Field getField(Enum<?> enumValue)
/*     */   {
/*  53 */     Class clazz = enumValue.getDeclaringClass();
/*     */     try {
/*  55 */       return clazz.getDeclaredField(enumValue.name());
/*     */     } catch (NoSuchFieldException impossible) {
/*  57 */       throw new AssertionError(impossible);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T extends Enum<T>> Function<String, T> valueOfFunction(Class<T> enumClass)
/*     */   {
/*  70 */     return new ValueOfFunction(enumClass, null);
/*     */   }
/*     */ 
/*     */   public static <T extends Enum<T>> Optional<T> getIfPresent(Class<T> enumClass, String value)
/*     */   {
/* 120 */     Preconditions.checkNotNull(enumClass);
/* 121 */     Preconditions.checkNotNull(value);
/*     */     try {
/* 123 */       return Optional.of(Enum.valueOf(enumClass, value)); } catch (IllegalArgumentException iae) {
/*     */     }
/* 125 */     return Optional.absent();
/*     */   }
/*     */ 
/*     */   private static final class ValueOfFunction<T extends Enum<T>>
/*     */     implements Function<String, T>, Serializable
/*     */   {
/*     */     private final Class<T> enumClass;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private ValueOfFunction(Class<T> enumClass)
/*     */     {
/*  83 */       this.enumClass = ((Class)Preconditions.checkNotNull(enumClass));
/*     */     }
/*     */ 
/*     */     public T apply(String value)
/*     */     {
/*     */       try {
/*  89 */         return Enum.valueOf(this.enumClass, value); } catch (IllegalArgumentException e) {
/*     */       }
/*  91 */       return null;
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj)
/*     */     {
/*  96 */       return ((obj instanceof ValueOfFunction)) && (this.enumClass.equals(((ValueOfFunction)obj).enumClass));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 101 */       return this.enumClass.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 105 */       return "Enums.valueOf(" + this.enumClass + ")";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Enums
 * JD-Core Version:    0.6.2
 */