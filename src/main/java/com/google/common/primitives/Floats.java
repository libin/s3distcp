/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.RandomAccess;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Floats
/*     */ {
/*     */   public static final int BYTES = 4;
/*     */ 
/*     */   public static int hashCode(float value)
/*     */   {
/*  69 */     return Float.valueOf(value).hashCode();
/*     */   }
/*     */ 
/*     */   public static int compare(float a, float b)
/*     */   {
/*  83 */     return Float.compare(a, b);
/*     */   }
/*     */ 
/*     */   public static boolean isFinite(float value)
/*     */   {
/*  94 */     return ((1.0F / -1.0F) < value ? 1 : 0) & (value < (1.0F / 1.0F) ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public static boolean contains(float[] array, float target)
/*     */   {
/* 108 */     for (float value : array) {
/* 109 */       if (value == target) {
/* 110 */         return true;
/*     */       }
/*     */     }
/* 113 */     return false;
/*     */   }
/*     */ 
/*     */   public static int indexOf(float[] array, float target)
/*     */   {
/* 127 */     return indexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int indexOf(float[] array, float target, int start, int end)
/*     */   {
/* 133 */     for (int i = start; i < end; i++) {
/* 134 */       if (array[i] == target) {
/* 135 */         return i;
/*     */       }
/*     */     }
/* 138 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int indexOf(float[] array, float[] target)
/*     */   {
/* 156 */     Preconditions.checkNotNull(array, "array");
/* 157 */     Preconditions.checkNotNull(target, "target");
/* 158 */     if (target.length == 0) {
/* 159 */       return 0;
/*     */     }
/*     */ 
/* 163 */     label65: for (int i = 0; i < array.length - target.length + 1; i++) {
/* 164 */       for (int j = 0; j < target.length; j++) {
/* 165 */         if (array[(i + j)] != target[j]) {
/*     */           break label65;
/*     */         }
/*     */       }
/* 169 */       return i;
/*     */     }
/* 171 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int lastIndexOf(float[] array, float target)
/*     */   {
/* 185 */     return lastIndexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int lastIndexOf(float[] array, float target, int start, int end)
/*     */   {
/* 191 */     for (int i = end - 1; i >= start; i--) {
/* 192 */       if (array[i] == target) {
/* 193 */         return i;
/*     */       }
/*     */     }
/* 196 */     return -1;
/*     */   }
/*     */ 
/*     */   public static float min(float[] array)
/*     */   {
/* 209 */     Preconditions.checkArgument(array.length > 0);
/* 210 */     float min = array[0];
/* 211 */     for (int i = 1; i < array.length; i++) {
/* 212 */       min = Math.min(min, array[i]);
/*     */     }
/* 214 */     return min;
/*     */   }
/*     */ 
/*     */   public static float max(float[] array)
/*     */   {
/* 227 */     Preconditions.checkArgument(array.length > 0);
/* 228 */     float max = array[0];
/* 229 */     for (int i = 1; i < array.length; i++) {
/* 230 */       max = Math.max(max, array[i]);
/*     */     }
/* 232 */     return max;
/*     */   }
/*     */ 
/*     */   public static float[] concat(float[][] arrays)
/*     */   {
/* 245 */     int length = 0;
/* 246 */     for (float[] array : arrays) {
/* 247 */       length += array.length;
/*     */     }
/* 249 */     float[] result = new float[length];
/* 250 */     int pos = 0;
/* 251 */     for (float[] array : arrays) {
/* 252 */       System.arraycopy(array, 0, result, pos, array.length);
/* 253 */       pos += array.length;
/*     */     }
/* 255 */     return result;
/*     */   }
/*     */ 
/*     */   public static float[] ensureCapacity(float[] array, int minLength, int padding)
/*     */   {
/* 276 */     Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
/* 277 */     Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
/* 278 */     return array.length < minLength ? copyOf(array, minLength + padding) : array;
/*     */   }
/*     */ 
/*     */   private static float[] copyOf(float[] original, int length)
/*     */   {
/* 285 */     float[] copy = new float[length];
/* 286 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
/* 287 */     return copy;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, float[] array)
/*     */   {
/* 305 */     Preconditions.checkNotNull(separator);
/* 306 */     if (array.length == 0) {
/* 307 */       return "";
/*     */     }
/*     */ 
/* 311 */     StringBuilder builder = new StringBuilder(array.length * 12);
/* 312 */     builder.append(array[0]);
/* 313 */     for (int i = 1; i < array.length; i++) {
/* 314 */       builder.append(separator).append(array[i]);
/*     */     }
/* 316 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<float[]> lexicographicalComparator()
/*     */   {
/* 336 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static float[] toArray(Collection<? extends Number> collection)
/*     */   {
/* 371 */     if ((collection instanceof FloatArrayAsList)) {
/* 372 */       return ((FloatArrayAsList)collection).toFloatArray();
/*     */     }
/*     */ 
/* 375 */     Object[] boxedArray = collection.toArray();
/* 376 */     int len = boxedArray.length;
/* 377 */     float[] array = new float[len];
/* 378 */     for (int i = 0; i < len; i++)
/*     */     {
/* 380 */       array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).floatValue();
/*     */     }
/* 382 */     return array;
/*     */   }
/*     */ 
/*     */   public static List<Float> asList(float[] backingArray)
/*     */   {
/* 403 */     if (backingArray.length == 0) {
/* 404 */       return Collections.emptyList();
/*     */     }
/* 406 */     return new FloatArrayAsList(backingArray);
/*     */   }
/*     */   @GwtCompatible
/*     */   private static class FloatArrayAsList extends AbstractList<Float> implements RandomAccess, Serializable { final float[] array;
/*     */     final int start;
/*     */     final int end;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 417 */     FloatArrayAsList(float[] array) { this(array, 0, array.length); }
/*     */ 
/*     */     FloatArrayAsList(float[] array, int start, int end)
/*     */     {
/* 421 */       this.array = array;
/* 422 */       this.start = start;
/* 423 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 427 */       return this.end - this.start;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 431 */       return false;
/*     */     }
/*     */ 
/*     */     public Float get(int index) {
/* 435 */       Preconditions.checkElementIndex(index, size());
/* 436 */       return Float.valueOf(this.array[(this.start + index)]);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object target)
/*     */     {
/* 441 */       return ((target instanceof Float)) && (Floats.indexOf(this.array, ((Float)target).floatValue(), this.start, this.end) != -1);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object target)
/*     */     {
/* 447 */       if ((target instanceof Float)) {
/* 448 */         int i = Floats.indexOf(this.array, ((Float)target).floatValue(), this.start, this.end);
/* 449 */         if (i >= 0) {
/* 450 */           return i - this.start;
/*     */         }
/*     */       }
/* 453 */       return -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object target)
/*     */     {
/* 458 */       if ((target instanceof Float)) {
/* 459 */         int i = Floats.lastIndexOf(this.array, ((Float)target).floatValue(), this.start, this.end);
/* 460 */         if (i >= 0) {
/* 461 */           return i - this.start;
/*     */         }
/*     */       }
/* 464 */       return -1;
/*     */     }
/*     */ 
/*     */     public Float set(int index, Float element) {
/* 468 */       Preconditions.checkElementIndex(index, size());
/* 469 */       float oldValue = this.array[(this.start + index)];
/* 470 */       this.array[(this.start + index)] = ((Float)Preconditions.checkNotNull(element)).floatValue();
/* 471 */       return Float.valueOf(oldValue);
/*     */     }
/*     */ 
/*     */     public List<Float> subList(int fromIndex, int toIndex) {
/* 475 */       int size = size();
/* 476 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
/* 477 */       if (fromIndex == toIndex) {
/* 478 */         return Collections.emptyList();
/*     */       }
/* 480 */       return new FloatArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object) {
/* 484 */       if (object == this) {
/* 485 */         return true;
/*     */       }
/* 487 */       if ((object instanceof FloatArrayAsList)) {
/* 488 */         FloatArrayAsList that = (FloatArrayAsList)object;
/* 489 */         int size = size();
/* 490 */         if (that.size() != size) {
/* 491 */           return false;
/*     */         }
/* 493 */         for (int i = 0; i < size; i++) {
/* 494 */           if (this.array[(this.start + i)] != that.array[(that.start + i)]) {
/* 495 */             return false;
/*     */           }
/*     */         }
/* 498 */         return true;
/*     */       }
/* 500 */       return super.equals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 504 */       int result = 1;
/* 505 */       for (int i = this.start; i < this.end; i++) {
/* 506 */         result = 31 * result + Floats.hashCode(this.array[i]);
/*     */       }
/* 508 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 512 */       StringBuilder builder = new StringBuilder(size() * 12);
/* 513 */       builder.append('[').append(this.array[this.start]);
/* 514 */       for (int i = this.start + 1; i < this.end; i++) {
/* 515 */         builder.append(", ").append(this.array[i]);
/*     */       }
/* 517 */       return builder.append(']').toString();
/*     */     }
/*     */ 
/*     */     float[] toFloatArray()
/*     */     {
/* 522 */       int size = size();
/* 523 */       float[] result = new float[size];
/* 524 */       System.arraycopy(this.array, this.start, result, 0, size);
/* 525 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator
/*     */     implements Comparator<float[]>
/*     */   {
/* 340 */     INSTANCE;
/*     */ 
/*     */     public int compare(float[] left, float[] right)
/*     */     {
/* 344 */       int minLength = Math.min(left.length, right.length);
/* 345 */       for (int i = 0; i < minLength; i++) {
/* 346 */         int result = Floats.compare(left[i], right[i]);
/* 347 */         if (result != 0) {
/* 348 */           return result;
/*     */         }
/*     */       }
/* 351 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.Floats
 * JD-Core Version:    0.6.2
 */