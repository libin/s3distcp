/*     */ package com.google.common.primitives;
/*     */ 
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
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class Longs
/*     */ {
/*     */   public static final int BYTES = 8;
/*     */   public static final long MAX_POWER_OF_TWO = 4611686018427387904L;
/*     */ 
/*     */   public static int hashCode(long value)
/*     */   {
/*  77 */     return (int)(value ^ value >>> 32);
/*     */   }
/*     */ 
/*     */   public static int compare(long a, long b)
/*     */   {
/*  90 */     return a > b ? 1 : a < b ? -1 : 0;
/*     */   }
/*     */ 
/*     */   public static boolean contains(long[] array, long target)
/*     */   {
/* 103 */     for (long value : array) {
/* 104 */       if (value == target) {
/* 105 */         return true;
/*     */       }
/*     */     }
/* 108 */     return false;
/*     */   }
/*     */ 
/*     */   public static int indexOf(long[] array, long target)
/*     */   {
/* 121 */     return indexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int indexOf(long[] array, long target, int start, int end)
/*     */   {
/* 127 */     for (int i = start; i < end; i++) {
/* 128 */       if (array[i] == target) {
/* 129 */         return i;
/*     */       }
/*     */     }
/* 132 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int indexOf(long[] array, long[] target)
/*     */   {
/* 147 */     Preconditions.checkNotNull(array, "array");
/* 148 */     Preconditions.checkNotNull(target, "target");
/* 149 */     if (target.length == 0) {
/* 150 */       return 0;
/*     */     }
/*     */ 
/* 154 */     label65: for (int i = 0; i < array.length - target.length + 1; i++) {
/* 155 */       for (int j = 0; j < target.length; j++) {
/* 156 */         if (array[(i + j)] != target[j]) {
/*     */           break label65;
/*     */         }
/*     */       }
/* 160 */       return i;
/*     */     }
/* 162 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int lastIndexOf(long[] array, long target)
/*     */   {
/* 175 */     return lastIndexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int lastIndexOf(long[] array, long target, int start, int end)
/*     */   {
/* 181 */     for (int i = end - 1; i >= start; i--) {
/* 182 */       if (array[i] == target) {
/* 183 */         return i;
/*     */       }
/*     */     }
/* 186 */     return -1;
/*     */   }
/*     */ 
/*     */   public static long min(long[] array)
/*     */   {
/* 198 */     Preconditions.checkArgument(array.length > 0);
/* 199 */     long min = array[0];
/* 200 */     for (int i = 1; i < array.length; i++) {
/* 201 */       if (array[i] < min) {
/* 202 */         min = array[i];
/*     */       }
/*     */     }
/* 205 */     return min;
/*     */   }
/*     */ 
/*     */   public static long max(long[] array)
/*     */   {
/* 217 */     Preconditions.checkArgument(array.length > 0);
/* 218 */     long max = array[0];
/* 219 */     for (int i = 1; i < array.length; i++) {
/* 220 */       if (array[i] > max) {
/* 221 */         max = array[i];
/*     */       }
/*     */     }
/* 224 */     return max;
/*     */   }
/*     */ 
/*     */   public static long[] concat(long[][] arrays)
/*     */   {
/* 237 */     int length = 0;
/* 238 */     for (long[] array : arrays) {
/* 239 */       length += array.length;
/*     */     }
/* 241 */     long[] result = new long[length];
/* 242 */     int pos = 0;
/* 243 */     for (long[] array : arrays) {
/* 244 */       System.arraycopy(array, 0, result, pos, array.length);
/* 245 */       pos += array.length;
/*     */     }
/* 247 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static byte[] toByteArray(long value)
/*     */   {
/* 263 */     return new byte[] { (byte)(int)(value >> 56), (byte)(int)(value >> 48), (byte)(int)(value >> 40), (byte)(int)(value >> 32), (byte)(int)(value >> 24), (byte)(int)(value >> 16), (byte)(int)(value >> 8), (byte)(int)value };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static long fromByteArray(byte[] bytes)
/*     */   {
/* 289 */     Preconditions.checkArgument(bytes.length >= 8, "array too small: %s < %s", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(8) });
/*     */ 
/* 291 */     return fromBytes(bytes[0], bytes[1], bytes[2], bytes[3], bytes[4], bytes[5], bytes[6], bytes[7]);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static long fromBytes(byte b1, byte b2, byte b3, byte b4, byte b5, byte b6, byte b7, byte b8)
/*     */   {
/* 305 */     return (b1 & 0xFF) << 56 | (b2 & 0xFF) << 48 | (b3 & 0xFF) << 40 | (b4 & 0xFF) << 32 | (b5 & 0xFF) << 24 | (b6 & 0xFF) << 16 | (b7 & 0xFF) << 8 | b8 & 0xFF;
/*     */   }
/*     */ 
/*     */   public static long[] ensureCapacity(long[] array, int minLength, int padding)
/*     */   {
/* 333 */     Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
/* 334 */     Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
/* 335 */     return array.length < minLength ? copyOf(array, minLength + padding) : array;
/*     */   }
/*     */ 
/*     */   private static long[] copyOf(long[] original, int length)
/*     */   {
/* 342 */     long[] copy = new long[length];
/* 343 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
/* 344 */     return copy;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, long[] array)
/*     */   {
/* 357 */     Preconditions.checkNotNull(separator);
/* 358 */     if (array.length == 0) {
/* 359 */       return "";
/*     */     }
/*     */ 
/* 363 */     StringBuilder builder = new StringBuilder(array.length * 10);
/* 364 */     builder.append(array[0]);
/* 365 */     for (int i = 1; i < array.length; i++) {
/* 366 */       builder.append(separator).append(array[i]);
/*     */     }
/* 368 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<long[]> lexicographicalComparator()
/*     */   {
/* 388 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static long[] toArray(Collection<? extends Number> collection)
/*     */   {
/* 423 */     if ((collection instanceof LongArrayAsList)) {
/* 424 */       return ((LongArrayAsList)collection).toLongArray();
/*     */     }
/*     */ 
/* 427 */     Object[] boxedArray = collection.toArray();
/* 428 */     int len = boxedArray.length;
/* 429 */     long[] array = new long[len];
/* 430 */     for (int i = 0; i < len; i++)
/*     */     {
/* 432 */       array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).longValue();
/*     */     }
/* 434 */     return array;
/*     */   }
/*     */ 
/*     */   public static List<Long> asList(long[] backingArray)
/*     */   {
/* 452 */     if (backingArray.length == 0) {
/* 453 */       return Collections.emptyList();
/*     */     }
/* 455 */     return new LongArrayAsList(backingArray);
/*     */   }
/*     */   @GwtCompatible
/*     */   private static class LongArrayAsList extends AbstractList<Long> implements RandomAccess, Serializable { final long[] array;
/*     */     final int start;
/*     */     final int end;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 466 */     LongArrayAsList(long[] array) { this(array, 0, array.length); }
/*     */ 
/*     */     LongArrayAsList(long[] array, int start, int end)
/*     */     {
/* 470 */       this.array = array;
/* 471 */       this.start = start;
/* 472 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 476 */       return this.end - this.start;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 480 */       return false;
/*     */     }
/*     */ 
/*     */     public Long get(int index) {
/* 484 */       Preconditions.checkElementIndex(index, size());
/* 485 */       return Long.valueOf(this.array[(this.start + index)]);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object target)
/*     */     {
/* 490 */       return ((target instanceof Long)) && (Longs.indexOf(this.array, ((Long)target).longValue(), this.start, this.end) != -1);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object target)
/*     */     {
/* 496 */       if ((target instanceof Long)) {
/* 497 */         int i = Longs.indexOf(this.array, ((Long)target).longValue(), this.start, this.end);
/* 498 */         if (i >= 0) {
/* 499 */           return i - this.start;
/*     */         }
/*     */       }
/* 502 */       return -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object target)
/*     */     {
/* 507 */       if ((target instanceof Long)) {
/* 508 */         int i = Longs.lastIndexOf(this.array, ((Long)target).longValue(), this.start, this.end);
/* 509 */         if (i >= 0) {
/* 510 */           return i - this.start;
/*     */         }
/*     */       }
/* 513 */       return -1;
/*     */     }
/*     */ 
/*     */     public Long set(int index, Long element) {
/* 517 */       Preconditions.checkElementIndex(index, size());
/* 518 */       long oldValue = this.array[(this.start + index)];
/* 519 */       this.array[(this.start + index)] = ((Long)Preconditions.checkNotNull(element)).longValue();
/* 520 */       return Long.valueOf(oldValue);
/*     */     }
/*     */ 
/*     */     public List<Long> subList(int fromIndex, int toIndex) {
/* 524 */       int size = size();
/* 525 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
/* 526 */       if (fromIndex == toIndex) {
/* 527 */         return Collections.emptyList();
/*     */       }
/* 529 */       return new LongArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object) {
/* 533 */       if (object == this) {
/* 534 */         return true;
/*     */       }
/* 536 */       if ((object instanceof LongArrayAsList)) {
/* 537 */         LongArrayAsList that = (LongArrayAsList)object;
/* 538 */         int size = size();
/* 539 */         if (that.size() != size) {
/* 540 */           return false;
/*     */         }
/* 542 */         for (int i = 0; i < size; i++) {
/* 543 */           if (this.array[(this.start + i)] != that.array[(that.start + i)]) {
/* 544 */             return false;
/*     */           }
/*     */         }
/* 547 */         return true;
/*     */       }
/* 549 */       return super.equals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 553 */       int result = 1;
/* 554 */       for (int i = this.start; i < this.end; i++) {
/* 555 */         result = 31 * result + Longs.hashCode(this.array[i]);
/*     */       }
/* 557 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 561 */       StringBuilder builder = new StringBuilder(size() * 10);
/* 562 */       builder.append('[').append(this.array[this.start]);
/* 563 */       for (int i = this.start + 1; i < this.end; i++) {
/* 564 */         builder.append(", ").append(this.array[i]);
/*     */       }
/* 566 */       return builder.append(']').toString();
/*     */     }
/*     */ 
/*     */     long[] toLongArray()
/*     */     {
/* 571 */       int size = size();
/* 572 */       long[] result = new long[size];
/* 573 */       System.arraycopy(this.array, this.start, result, 0, size);
/* 574 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator
/*     */     implements Comparator<long[]>
/*     */   {
/* 392 */     INSTANCE;
/*     */ 
/*     */     public int compare(long[] left, long[] right)
/*     */     {
/* 396 */       int minLength = Math.min(left.length, right.length);
/* 397 */       for (int i = 0; i < minLength; i++) {
/* 398 */         int result = Longs.compare(left[i], right[i]);
/* 399 */         if (result != 0) {
/* 400 */           return result;
/*     */         }
/*     */       }
/* 403 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.Longs
 * JD-Core Version:    0.6.2
 */