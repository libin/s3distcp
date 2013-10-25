/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.RandomAccess;
/*     */ import javax.annotation.CheckForNull;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class Ints
/*     */ {
/*     */   public static final int BYTES = 4;
/*     */   public static final int MAX_POWER_OF_TWO = 1073741824;
/*     */ 
/*     */   public static int hashCode(int value)
/*     */   {
/*  75 */     return value;
/*     */   }
/*     */ 
/*     */   public static int checkedCast(long value)
/*     */   {
/*  87 */     int result = (int)value;
/*  88 */     Preconditions.checkArgument(result == value, "Out of range: %s", new Object[] { Long.valueOf(value) });
/*  89 */     return result;
/*     */   }
/*     */ 
/*     */   public static int saturatedCast(long value)
/*     */   {
/* 101 */     if (value > 2147483647L) {
/* 102 */       return 2147483647;
/*     */     }
/* 104 */     if (value < -2147483648L) {
/* 105 */       return -2147483648;
/*     */     }
/* 107 */     return (int)value;
/*     */   }
/*     */ 
/*     */   public static int compare(int a, int b)
/*     */   {
/* 120 */     return a > b ? 1 : a < b ? -1 : 0;
/*     */   }
/*     */ 
/*     */   public static boolean contains(int[] array, int target)
/*     */   {
/* 133 */     for (int value : array) {
/* 134 */       if (value == target) {
/* 135 */         return true;
/*     */       }
/*     */     }
/* 138 */     return false;
/*     */   }
/*     */ 
/*     */   public static int indexOf(int[] array, int target)
/*     */   {
/* 151 */     return indexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int indexOf(int[] array, int target, int start, int end)
/*     */   {
/* 157 */     for (int i = start; i < end; i++) {
/* 158 */       if (array[i] == target) {
/* 159 */         return i;
/*     */       }
/*     */     }
/* 162 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int indexOf(int[] array, int[] target)
/*     */   {
/* 177 */     Preconditions.checkNotNull(array, "array");
/* 178 */     Preconditions.checkNotNull(target, "target");
/* 179 */     if (target.length == 0) {
/* 180 */       return 0;
/*     */     }
/*     */ 
/* 184 */     label64: for (int i = 0; i < array.length - target.length + 1; i++) {
/* 185 */       for (int j = 0; j < target.length; j++) {
/* 186 */         if (array[(i + j)] != target[j]) {
/*     */           break label64;
/*     */         }
/*     */       }
/* 190 */       return i;
/*     */     }
/* 192 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int lastIndexOf(int[] array, int target)
/*     */   {
/* 205 */     return lastIndexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int lastIndexOf(int[] array, int target, int start, int end)
/*     */   {
/* 211 */     for (int i = end - 1; i >= start; i--) {
/* 212 */       if (array[i] == target) {
/* 213 */         return i;
/*     */       }
/*     */     }
/* 216 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int min(int[] array)
/*     */   {
/* 228 */     Preconditions.checkArgument(array.length > 0);
/* 229 */     int min = array[0];
/* 230 */     for (int i = 1; i < array.length; i++) {
/* 231 */       if (array[i] < min) {
/* 232 */         min = array[i];
/*     */       }
/*     */     }
/* 235 */     return min;
/*     */   }
/*     */ 
/*     */   public static int max(int[] array)
/*     */   {
/* 247 */     Preconditions.checkArgument(array.length > 0);
/* 248 */     int max = array[0];
/* 249 */     for (int i = 1; i < array.length; i++) {
/* 250 */       if (array[i] > max) {
/* 251 */         max = array[i];
/*     */       }
/*     */     }
/* 254 */     return max;
/*     */   }
/*     */ 
/*     */   public static int[] concat(int[][] arrays)
/*     */   {
/* 267 */     int length = 0;
/* 268 */     for (int[] array : arrays) {
/* 269 */       length += array.length;
/*     */     }
/* 271 */     int[] result = new int[length];
/* 272 */     int pos = 0;
/* 273 */     for (int[] array : arrays) {
/* 274 */       System.arraycopy(array, 0, result, pos, array.length);
/* 275 */       pos += array.length;
/*     */     }
/* 277 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static byte[] toByteArray(int value)
/*     */   {
/* 293 */     return new byte[] { (byte)(value >> 24), (byte)(value >> 16), (byte)(value >> 8), (byte)value };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static int fromByteArray(byte[] bytes)
/*     */   {
/* 314 */     Preconditions.checkArgument(bytes.length >= 4, "array too small: %s < %s", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(4) });
/*     */ 
/* 316 */     return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3]);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static int fromBytes(byte b1, byte b2, byte b3, byte b4)
/*     */   {
/* 328 */     return b1 << 24 | (b2 & 0xFF) << 16 | (b3 & 0xFF) << 8 | b4 & 0xFF;
/*     */   }
/*     */ 
/*     */   public static int[] ensureCapacity(int[] array, int minLength, int padding)
/*     */   {
/* 349 */     Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
/* 350 */     Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
/* 351 */     return array.length < minLength ? copyOf(array, minLength + padding) : array;
/*     */   }
/*     */ 
/*     */   private static int[] copyOf(int[] original, int length)
/*     */   {
/* 358 */     int[] copy = new int[length];
/* 359 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
/* 360 */     return copy;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, int[] array)
/*     */   {
/* 373 */     Preconditions.checkNotNull(separator);
/* 374 */     if (array.length == 0) {
/* 375 */       return "";
/*     */     }
/*     */ 
/* 379 */     StringBuilder builder = new StringBuilder(array.length * 5);
/* 380 */     builder.append(array[0]);
/* 381 */     for (int i = 1; i < array.length; i++) {
/* 382 */       builder.append(separator).append(array[i]);
/*     */     }
/* 384 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<int[]> lexicographicalComparator()
/*     */   {
/* 403 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static int[] toArray(Collection<? extends Number> collection)
/*     */   {
/* 438 */     if ((collection instanceof IntArrayAsList)) {
/* 439 */       return ((IntArrayAsList)collection).toIntArray();
/*     */     }
/*     */ 
/* 442 */     Object[] boxedArray = collection.toArray();
/* 443 */     int len = boxedArray.length;
/* 444 */     int[] array = new int[len];
/* 445 */     for (int i = 0; i < len; i++)
/*     */     {
/* 447 */       array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).intValue();
/*     */     }
/* 449 */     return array;
/*     */   }
/*     */ 
/*     */   public static List<Integer> asList(int[] backingArray)
/*     */   {
/* 467 */     if (backingArray.length == 0) {
/* 468 */       return Collections.emptyList();
/*     */     }
/* 470 */     return new IntArrayAsList(backingArray);
/*     */   }
/*     */ 
/*     */   @CheckForNull
/*     */   @Beta
/*     */   @GwtIncompatible("TODO")
/*     */   public static Integer tryParse(String string)
/*     */   {
/* 617 */     return AndroidInteger.tryParse(string, 10);
/*     */   }
/*     */ 
/*     */   @GwtCompatible
/*     */   private static class IntArrayAsList extends AbstractList<Integer>
/*     */     implements RandomAccess, Serializable
/*     */   {
/*     */     final int[] array;
/*     */     final int start;
/*     */     final int end;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     IntArrayAsList(int[] array)
/*     */     {
/* 481 */       this(array, 0, array.length);
/*     */     }
/*     */ 
/*     */     IntArrayAsList(int[] array, int start, int end) {
/* 485 */       this.array = array;
/* 486 */       this.start = start;
/* 487 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 491 */       return this.end - this.start;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 495 */       return false;
/*     */     }
/*     */ 
/*     */     public Integer get(int index) {
/* 499 */       Preconditions.checkElementIndex(index, size());
/* 500 */       return Integer.valueOf(this.array[(this.start + index)]);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object target)
/*     */     {
/* 505 */       return ((target instanceof Integer)) && (Ints.indexOf(this.array, ((Integer)target).intValue(), this.start, this.end) != -1);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object target)
/*     */     {
/* 511 */       if ((target instanceof Integer)) {
/* 512 */         int i = Ints.indexOf(this.array, ((Integer)target).intValue(), this.start, this.end);
/* 513 */         if (i >= 0) {
/* 514 */           return i - this.start;
/*     */         }
/*     */       }
/* 517 */       return -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object target)
/*     */     {
/* 522 */       if ((target instanceof Integer)) {
/* 523 */         int i = Ints.lastIndexOf(this.array, ((Integer)target).intValue(), this.start, this.end);
/* 524 */         if (i >= 0) {
/* 525 */           return i - this.start;
/*     */         }
/*     */       }
/* 528 */       return -1;
/*     */     }
/*     */ 
/*     */     public Integer set(int index, Integer element) {
/* 532 */       Preconditions.checkElementIndex(index, size());
/* 533 */       int oldValue = this.array[(this.start + index)];
/* 534 */       this.array[(this.start + index)] = ((Integer)Preconditions.checkNotNull(element)).intValue();
/* 535 */       return Integer.valueOf(oldValue);
/*     */     }
/*     */ 
/*     */     public List<Integer> subList(int fromIndex, int toIndex) {
/* 539 */       int size = size();
/* 540 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
/* 541 */       if (fromIndex == toIndex) {
/* 542 */         return Collections.emptyList();
/*     */       }
/* 544 */       return new IntArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object) {
/* 548 */       if (object == this) {
/* 549 */         return true;
/*     */       }
/* 551 */       if ((object instanceof IntArrayAsList)) {
/* 552 */         IntArrayAsList that = (IntArrayAsList)object;
/* 553 */         int size = size();
/* 554 */         if (that.size() != size) {
/* 555 */           return false;
/*     */         }
/* 557 */         for (int i = 0; i < size; i++) {
/* 558 */           if (this.array[(this.start + i)] != that.array[(that.start + i)]) {
/* 559 */             return false;
/*     */           }
/*     */         }
/* 562 */         return true;
/*     */       }
/* 564 */       return super.equals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 568 */       int result = 1;
/* 569 */       for (int i = this.start; i < this.end; i++) {
/* 570 */         result = 31 * result + Ints.hashCode(this.array[i]);
/*     */       }
/* 572 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 576 */       StringBuilder builder = new StringBuilder(size() * 5);
/* 577 */       builder.append('[').append(this.array[this.start]);
/* 578 */       for (int i = this.start + 1; i < this.end; i++) {
/* 579 */         builder.append(", ").append(this.array[i]);
/*     */       }
/* 581 */       return builder.append(']').toString();
/*     */     }
/*     */ 
/*     */     int[] toIntArray()
/*     */     {
/* 586 */       int size = size();
/* 587 */       int[] result = new int[size];
/* 588 */       System.arraycopy(this.array, this.start, result, 0, size);
/* 589 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator
/*     */     implements Comparator<int[]>
/*     */   {
/* 407 */     INSTANCE;
/*     */ 
/*     */     public int compare(int[] left, int[] right)
/*     */     {
/* 411 */       int minLength = Math.min(left.length, right.length);
/* 412 */       for (int i = 0; i < minLength; i++) {
/* 413 */         int result = Ints.compare(left[i], right[i]);
/* 414 */         if (result != 0) {
/* 415 */           return result;
/*     */         }
/*     */       }
/* 418 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.Ints
 * JD-Core Version:    0.6.2
 */