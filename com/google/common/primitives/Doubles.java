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
/*     */ public final class Doubles
/*     */ {
/*     */   public static final int BYTES = 8;
/*     */ 
/*     */   public static int hashCode(double value)
/*     */   {
/*  68 */     return Double.valueOf(value).hashCode();
/*     */   }
/*     */ 
/*     */   public static int compare(double a, double b)
/*     */   {
/*  86 */     return Double.compare(a, b);
/*     */   }
/*     */ 
/*     */   public static boolean isFinite(double value)
/*     */   {
/*  97 */     return ((-1.0D / 0.0D) < value ? 1 : 0) & (value < (1.0D / 0.0D) ? 1 : 0);
/*     */   }
/*     */ 
/*     */   public static boolean contains(double[] array, double target)
/*     */   {
/* 111 */     for (double value : array) {
/* 112 */       if (value == target) {
/* 113 */         return true;
/*     */       }
/*     */     }
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */   public static int indexOf(double[] array, double target)
/*     */   {
/* 130 */     return indexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int indexOf(double[] array, double target, int start, int end)
/*     */   {
/* 136 */     for (int i = start; i < end; i++) {
/* 137 */       if (array[i] == target) {
/* 138 */         return i;
/*     */       }
/*     */     }
/* 141 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int indexOf(double[] array, double[] target)
/*     */   {
/* 159 */     Preconditions.checkNotNull(array, "array");
/* 160 */     Preconditions.checkNotNull(target, "target");
/* 161 */     if (target.length == 0) {
/* 162 */       return 0;
/*     */     }
/*     */ 
/* 166 */     label65: for (int i = 0; i < array.length - target.length + 1; i++) {
/* 167 */       for (int j = 0; j < target.length; j++) {
/* 168 */         if (array[(i + j)] != target[j]) {
/*     */           break label65;
/*     */         }
/*     */       }
/* 172 */       return i;
/*     */     }
/* 174 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int lastIndexOf(double[] array, double target)
/*     */   {
/* 188 */     return lastIndexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int lastIndexOf(double[] array, double target, int start, int end)
/*     */   {
/* 194 */     for (int i = end - 1; i >= start; i--) {
/* 195 */       if (array[i] == target) {
/* 196 */         return i;
/*     */       }
/*     */     }
/* 199 */     return -1;
/*     */   }
/*     */ 
/*     */   public static double min(double[] array)
/*     */   {
/* 212 */     Preconditions.checkArgument(array.length > 0);
/* 213 */     double min = array[0];
/* 214 */     for (int i = 1; i < array.length; i++) {
/* 215 */       min = Math.min(min, array[i]);
/*     */     }
/* 217 */     return min;
/*     */   }
/*     */ 
/*     */   public static double max(double[] array)
/*     */   {
/* 230 */     Preconditions.checkArgument(array.length > 0);
/* 231 */     double max = array[0];
/* 232 */     for (int i = 1; i < array.length; i++) {
/* 233 */       max = Math.max(max, array[i]);
/*     */     }
/* 235 */     return max;
/*     */   }
/*     */ 
/*     */   public static double[] concat(double[][] arrays)
/*     */   {
/* 248 */     int length = 0;
/* 249 */     for (double[] array : arrays) {
/* 250 */       length += array.length;
/*     */     }
/* 252 */     double[] result = new double[length];
/* 253 */     int pos = 0;
/* 254 */     for (double[] array : arrays) {
/* 255 */       System.arraycopy(array, 0, result, pos, array.length);
/* 256 */       pos += array.length;
/*     */     }
/* 258 */     return result;
/*     */   }
/*     */ 
/*     */   public static double[] ensureCapacity(double[] array, int minLength, int padding)
/*     */   {
/* 279 */     Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
/* 280 */     Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
/* 281 */     return array.length < minLength ? copyOf(array, minLength + padding) : array;
/*     */   }
/*     */ 
/*     */   private static double[] copyOf(double[] original, int length)
/*     */   {
/* 288 */     double[] copy = new double[length];
/* 289 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
/* 290 */     return copy;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, double[] array)
/*     */   {
/* 308 */     Preconditions.checkNotNull(separator);
/* 309 */     if (array.length == 0) {
/* 310 */       return "";
/*     */     }
/*     */ 
/* 314 */     StringBuilder builder = new StringBuilder(array.length * 12);
/* 315 */     builder.append(array[0]);
/* 316 */     for (int i = 1; i < array.length; i++) {
/* 317 */       builder.append(separator).append(array[i]);
/*     */     }
/* 319 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<double[]> lexicographicalComparator()
/*     */   {
/* 339 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static double[] toArray(Collection<? extends Number> collection)
/*     */   {
/* 374 */     if ((collection instanceof DoubleArrayAsList)) {
/* 375 */       return ((DoubleArrayAsList)collection).toDoubleArray();
/*     */     }
/*     */ 
/* 378 */     Object[] boxedArray = collection.toArray();
/* 379 */     int len = boxedArray.length;
/* 380 */     double[] array = new double[len];
/* 381 */     for (int i = 0; i < len; i++)
/*     */     {
/* 383 */       array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).doubleValue();
/*     */     }
/* 385 */     return array;
/*     */   }
/*     */ 
/*     */   public static List<Double> asList(double[] backingArray)
/*     */   {
/* 406 */     if (backingArray.length == 0) {
/* 407 */       return Collections.emptyList();
/*     */     }
/* 409 */     return new DoubleArrayAsList(backingArray);
/*     */   }
/*     */   @GwtCompatible
/*     */   private static class DoubleArrayAsList extends AbstractList<Double> implements RandomAccess, Serializable { final double[] array;
/*     */     final int start;
/*     */     final int end;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 420 */     DoubleArrayAsList(double[] array) { this(array, 0, array.length); }
/*     */ 
/*     */     DoubleArrayAsList(double[] array, int start, int end)
/*     */     {
/* 424 */       this.array = array;
/* 425 */       this.start = start;
/* 426 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 430 */       return this.end - this.start;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 434 */       return false;
/*     */     }
/*     */ 
/*     */     public Double get(int index) {
/* 438 */       Preconditions.checkElementIndex(index, size());
/* 439 */       return Double.valueOf(this.array[(this.start + index)]);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object target)
/*     */     {
/* 444 */       return ((target instanceof Double)) && (Doubles.indexOf(this.array, ((Double)target).doubleValue(), this.start, this.end) != -1);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object target)
/*     */     {
/* 450 */       if ((target instanceof Double)) {
/* 451 */         int i = Doubles.indexOf(this.array, ((Double)target).doubleValue(), this.start, this.end);
/* 452 */         if (i >= 0) {
/* 453 */           return i - this.start;
/*     */         }
/*     */       }
/* 456 */       return -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object target)
/*     */     {
/* 461 */       if ((target instanceof Double)) {
/* 462 */         int i = Doubles.lastIndexOf(this.array, ((Double)target).doubleValue(), this.start, this.end);
/* 463 */         if (i >= 0) {
/* 464 */           return i - this.start;
/*     */         }
/*     */       }
/* 467 */       return -1;
/*     */     }
/*     */ 
/*     */     public Double set(int index, Double element) {
/* 471 */       Preconditions.checkElementIndex(index, size());
/* 472 */       double oldValue = this.array[(this.start + index)];
/* 473 */       this.array[(this.start + index)] = ((Double)Preconditions.checkNotNull(element)).doubleValue();
/* 474 */       return Double.valueOf(oldValue);
/*     */     }
/*     */ 
/*     */     public List<Double> subList(int fromIndex, int toIndex) {
/* 478 */       int size = size();
/* 479 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
/* 480 */       if (fromIndex == toIndex) {
/* 481 */         return Collections.emptyList();
/*     */       }
/* 483 */       return new DoubleArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object) {
/* 487 */       if (object == this) {
/* 488 */         return true;
/*     */       }
/* 490 */       if ((object instanceof DoubleArrayAsList)) {
/* 491 */         DoubleArrayAsList that = (DoubleArrayAsList)object;
/* 492 */         int size = size();
/* 493 */         if (that.size() != size) {
/* 494 */           return false;
/*     */         }
/* 496 */         for (int i = 0; i < size; i++) {
/* 497 */           if (this.array[(this.start + i)] != that.array[(that.start + i)]) {
/* 498 */             return false;
/*     */           }
/*     */         }
/* 501 */         return true;
/*     */       }
/* 503 */       return super.equals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 507 */       int result = 1;
/* 508 */       for (int i = this.start; i < this.end; i++) {
/* 509 */         result = 31 * result + Doubles.hashCode(this.array[i]);
/*     */       }
/* 511 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 515 */       StringBuilder builder = new StringBuilder(size() * 12);
/* 516 */       builder.append('[').append(this.array[this.start]);
/* 517 */       for (int i = this.start + 1; i < this.end; i++) {
/* 518 */         builder.append(", ").append(this.array[i]);
/*     */       }
/* 520 */       return builder.append(']').toString();
/*     */     }
/*     */ 
/*     */     double[] toDoubleArray()
/*     */     {
/* 525 */       int size = size();
/* 526 */       double[] result = new double[size];
/* 527 */       System.arraycopy(this.array, this.start, result, 0, size);
/* 528 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator
/*     */     implements Comparator<double[]>
/*     */   {
/* 343 */     INSTANCE;
/*     */ 
/*     */     public int compare(double[] left, double[] right)
/*     */     {
/* 347 */       int minLength = Math.min(left.length, right.length);
/* 348 */       for (int i = 0; i < minLength; i++) {
/* 349 */         int result = Doubles.compare(left[i], right[i]);
/* 350 */         if (result != 0) {
/* 351 */           return result;
/*     */         }
/*     */       }
/* 354 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.Doubles
 * JD-Core Version:    0.6.2
 */