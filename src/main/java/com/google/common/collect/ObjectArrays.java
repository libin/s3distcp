/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.lang.reflect.Array;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class ObjectArrays
/*     */ {
/*     */   @GwtIncompatible("Array.newInstance(Class, int)")
/*     */   public static <T> T[] newArray(Class<T> type, int length)
/*     */   {
/*  46 */     return (Object[])Array.newInstance(type, length);
/*     */   }
/*     */ 
/*     */   public static <T> T[] newArray(T[] reference, int length)
/*     */   {
/*  57 */     return Platform.newArray(reference, length);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Array.newInstance(Class, int)")
/*     */   public static <T> T[] concat(T[] first, T[] second, Class<T> type)
/*     */   {
/*  69 */     Object[] result = newArray(type, first.length + second.length);
/*  70 */     System.arraycopy(first, 0, result, 0, first.length);
/*  71 */     System.arraycopy(second, 0, result, first.length, second.length);
/*  72 */     return result;
/*     */   }
/*     */ 
/*     */   public static <T> T[] concat(@Nullable T element, T[] array)
/*     */   {
/*  85 */     Object[] result = newArray(array, array.length + 1);
/*  86 */     result[0] = element;
/*  87 */     System.arraycopy(array, 0, result, 1, array.length);
/*  88 */     return result;
/*     */   }
/*     */ 
/*     */   public static <T> T[] concat(T[] array, @Nullable T element)
/*     */   {
/* 101 */     Object[] result = arraysCopyOf(array, array.length + 1);
/* 102 */     result[array.length] = element;
/* 103 */     return result;
/*     */   }
/*     */ 
/*     */   static <T> T[] arraysCopyOf(T[] original, int newLength)
/*     */   {
/* 108 */     Object[] copy = newArray(original, newLength);
/* 109 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
/*     */ 
/* 111 */     return copy;
/*     */   }
/*     */ 
/*     */   static <T> T[] toArrayImpl(Collection<?> c, T[] array)
/*     */   {
/* 139 */     int size = c.size();
/* 140 */     if (array.length < size) {
/* 141 */       array = newArray(array, size);
/*     */     }
/* 143 */     fillArray(c, array);
/* 144 */     if (array.length > size) {
/* 145 */       array[size] = null;
/*     */     }
/* 147 */     return array;
/*     */   }
/*     */ 
/*     */   static Object[] toArrayImpl(Collection<?> c)
/*     */   {
/* 165 */     return fillArray(c, new Object[c.size()]);
/*     */   }
/*     */ 
/*     */   private static Object[] fillArray(Iterable<?> elements, Object[] array) {
/* 169 */     int i = 0;
/* 170 */     for (Iterator i$ = elements.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 171 */       array[(i++)] = element;
/*     */     }
/* 173 */     return array;
/*     */   }
/*     */ 
/*     */   static void swap(Object[] array, int i, int j)
/*     */   {
/* 180 */     Object temp = array[i];
/* 181 */     array[i] = array[j];
/* 182 */     array[j] = temp;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.ObjectArrays
 * JD-Core Version:    0.6.2
 */