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
/*     */ public final class Shorts
/*     */ {
/*     */   public static final int BYTES = 2;
/*     */   public static final short MAX_POWER_OF_TWO = 16384;
/*     */ 
/*     */   public static int hashCode(short value)
/*     */   {
/*  72 */     return value;
/*     */   }
/*     */ 
/*     */   public static short checkedCast(long value)
/*     */   {
/*  85 */     short result = (short)(int)value;
/*  86 */     Preconditions.checkArgument(result == value, "Out of range: %s", new Object[] { Long.valueOf(value) });
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   public static short saturatedCast(long value)
/*     */   {
/*  99 */     if (value > 32767L) {
/* 100 */       return 32767;
/*     */     }
/* 102 */     if (value < -32768L) {
/* 103 */       return -32768;
/*     */     }
/* 105 */     return (short)(int)value;
/*     */   }
/*     */ 
/*     */   public static int compare(short a, short b)
/*     */   {
/* 118 */     return a - b;
/*     */   }
/*     */ 
/*     */   public static boolean contains(short[] array, short target)
/*     */   {
/* 131 */     for (short value : array) {
/* 132 */       if (value == target) {
/* 133 */         return true;
/*     */       }
/*     */     }
/* 136 */     return false;
/*     */   }
/*     */ 
/*     */   public static int indexOf(short[] array, short target)
/*     */   {
/* 149 */     return indexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int indexOf(short[] array, short target, int start, int end)
/*     */   {
/* 155 */     for (int i = start; i < end; i++) {
/* 156 */       if (array[i] == target) {
/* 157 */         return i;
/*     */       }
/*     */     }
/* 160 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int indexOf(short[] array, short[] target)
/*     */   {
/* 175 */     Preconditions.checkNotNull(array, "array");
/* 176 */     Preconditions.checkNotNull(target, "target");
/* 177 */     if (target.length == 0) {
/* 178 */       return 0;
/*     */     }
/*     */ 
/* 182 */     label64: for (int i = 0; i < array.length - target.length + 1; i++) {
/* 183 */       for (int j = 0; j < target.length; j++) {
/* 184 */         if (array[(i + j)] != target[j]) {
/*     */           break label64;
/*     */         }
/*     */       }
/* 188 */       return i;
/*     */     }
/* 190 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int lastIndexOf(short[] array, short target)
/*     */   {
/* 203 */     return lastIndexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int lastIndexOf(short[] array, short target, int start, int end)
/*     */   {
/* 209 */     for (int i = end - 1; i >= start; i--) {
/* 210 */       if (array[i] == target) {
/* 211 */         return i;
/*     */       }
/*     */     }
/* 214 */     return -1;
/*     */   }
/*     */ 
/*     */   public static short min(short[] array)
/*     */   {
/* 226 */     Preconditions.checkArgument(array.length > 0);
/* 227 */     short min = array[0];
/* 228 */     for (int i = 1; i < array.length; i++) {
/* 229 */       if (array[i] < min) {
/* 230 */         min = array[i];
/*     */       }
/*     */     }
/* 233 */     return min;
/*     */   }
/*     */ 
/*     */   public static short max(short[] array)
/*     */   {
/* 245 */     Preconditions.checkArgument(array.length > 0);
/* 246 */     short max = array[0];
/* 247 */     for (int i = 1; i < array.length; i++) {
/* 248 */       if (array[i] > max) {
/* 249 */         max = array[i];
/*     */       }
/*     */     }
/* 252 */     return max;
/*     */   }
/*     */ 
/*     */   public static short[] concat(short[][] arrays)
/*     */   {
/* 265 */     int length = 0;
/* 266 */     for (short[] array : arrays) {
/* 267 */       length += array.length;
/*     */     }
/* 269 */     short[] result = new short[length];
/* 270 */     int pos = 0;
/* 271 */     for (short[] array : arrays) {
/* 272 */       System.arraycopy(array, 0, result, pos, array.length);
/* 273 */       pos += array.length;
/*     */     }
/* 275 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static byte[] toByteArray(short value)
/*     */   {
/* 292 */     return new byte[] { (byte)(value >> 8), (byte)value };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static short fromByteArray(byte[] bytes)
/*     */   {
/* 311 */     Preconditions.checkArgument(bytes.length >= 2, "array too small: %s < %s", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(2) });
/*     */ 
/* 313 */     return fromBytes(bytes[0], bytes[1]);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static short fromBytes(byte b1, byte b2)
/*     */   {
/* 325 */     return (short)(b1 << 8 | b2 & 0xFF);
/*     */   }
/*     */ 
/*     */   public static short[] ensureCapacity(short[] array, int minLength, int padding)
/*     */   {
/* 346 */     Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
/* 347 */     Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
/* 348 */     return array.length < minLength ? copyOf(array, minLength + padding) : array;
/*     */   }
/*     */ 
/*     */   private static short[] copyOf(short[] original, int length)
/*     */   {
/* 355 */     short[] copy = new short[length];
/* 356 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
/* 357 */     return copy;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, short[] array)
/*     */   {
/* 370 */     Preconditions.checkNotNull(separator);
/* 371 */     if (array.length == 0) {
/* 372 */       return "";
/*     */     }
/*     */ 
/* 376 */     StringBuilder builder = new StringBuilder(array.length * 6);
/* 377 */     builder.append(array[0]);
/* 378 */     for (int i = 1; i < array.length; i++) {
/* 379 */       builder.append(separator).append(array[i]);
/*     */     }
/* 381 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<short[]> lexicographicalComparator()
/*     */   {
/* 401 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static short[] toArray(Collection<? extends Number> collection)
/*     */   {
/* 436 */     if ((collection instanceof ShortArrayAsList)) {
/* 437 */       return ((ShortArrayAsList)collection).toShortArray();
/*     */     }
/*     */ 
/* 440 */     Object[] boxedArray = collection.toArray();
/* 441 */     int len = boxedArray.length;
/* 442 */     short[] array = new short[len];
/* 443 */     for (int i = 0; i < len; i++)
/*     */     {
/* 445 */       array[i] = ((Number)Preconditions.checkNotNull(boxedArray[i])).shortValue();
/*     */     }
/* 447 */     return array;
/*     */   }
/*     */ 
/*     */   public static List<Short> asList(short[] backingArray)
/*     */   {
/* 465 */     if (backingArray.length == 0) {
/* 466 */       return Collections.emptyList();
/*     */     }
/* 468 */     return new ShortArrayAsList(backingArray);
/*     */   }
/*     */   @GwtCompatible
/*     */   private static class ShortArrayAsList extends AbstractList<Short> implements RandomAccess, Serializable { final short[] array;
/*     */     final int start;
/*     */     final int end;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 479 */     ShortArrayAsList(short[] array) { this(array, 0, array.length); }
/*     */ 
/*     */     ShortArrayAsList(short[] array, int start, int end)
/*     */     {
/* 483 */       this.array = array;
/* 484 */       this.start = start;
/* 485 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 489 */       return this.end - this.start;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 493 */       return false;
/*     */     }
/*     */ 
/*     */     public Short get(int index) {
/* 497 */       Preconditions.checkElementIndex(index, size());
/* 498 */       return Short.valueOf(this.array[(this.start + index)]);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object target)
/*     */     {
/* 503 */       return ((target instanceof Short)) && (Shorts.indexOf(this.array, ((Short)target).shortValue(), this.start, this.end) != -1);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object target)
/*     */     {
/* 509 */       if ((target instanceof Short)) {
/* 510 */         int i = Shorts.indexOf(this.array, ((Short)target).shortValue(), this.start, this.end);
/* 511 */         if (i >= 0) {
/* 512 */           return i - this.start;
/*     */         }
/*     */       }
/* 515 */       return -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object target)
/*     */     {
/* 520 */       if ((target instanceof Short)) {
/* 521 */         int i = Shorts.lastIndexOf(this.array, ((Short)target).shortValue(), this.start, this.end);
/* 522 */         if (i >= 0) {
/* 523 */           return i - this.start;
/*     */         }
/*     */       }
/* 526 */       return -1;
/*     */     }
/*     */ 
/*     */     public Short set(int index, Short element) {
/* 530 */       Preconditions.checkElementIndex(index, size());
/* 531 */       short oldValue = this.array[(this.start + index)];
/* 532 */       this.array[(this.start + index)] = ((Short)Preconditions.checkNotNull(element)).shortValue();
/* 533 */       return Short.valueOf(oldValue);
/*     */     }
/*     */ 
/*     */     public List<Short> subList(int fromIndex, int toIndex) {
/* 537 */       int size = size();
/* 538 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
/* 539 */       if (fromIndex == toIndex) {
/* 540 */         return Collections.emptyList();
/*     */       }
/* 542 */       return new ShortArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object) {
/* 546 */       if (object == this) {
/* 547 */         return true;
/*     */       }
/* 549 */       if ((object instanceof ShortArrayAsList)) {
/* 550 */         ShortArrayAsList that = (ShortArrayAsList)object;
/* 551 */         int size = size();
/* 552 */         if (that.size() != size) {
/* 553 */           return false;
/*     */         }
/* 555 */         for (int i = 0; i < size; i++) {
/* 556 */           if (this.array[(this.start + i)] != that.array[(that.start + i)]) {
/* 557 */             return false;
/*     */           }
/*     */         }
/* 560 */         return true;
/*     */       }
/* 562 */       return super.equals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 566 */       int result = 1;
/* 567 */       for (int i = this.start; i < this.end; i++) {
/* 568 */         result = 31 * result + Shorts.hashCode(this.array[i]);
/*     */       }
/* 570 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 574 */       StringBuilder builder = new StringBuilder(size() * 6);
/* 575 */       builder.append('[').append(this.array[this.start]);
/* 576 */       for (int i = this.start + 1; i < this.end; i++) {
/* 577 */         builder.append(", ").append(this.array[i]);
/*     */       }
/* 579 */       return builder.append(']').toString();
/*     */     }
/*     */ 
/*     */     short[] toShortArray()
/*     */     {
/* 584 */       int size = size();
/* 585 */       short[] result = new short[size];
/* 586 */       System.arraycopy(this.array, this.start, result, 0, size);
/* 587 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator
/*     */     implements Comparator<short[]>
/*     */   {
/* 405 */     INSTANCE;
/*     */ 
/*     */     public int compare(short[] left, short[] right)
/*     */     {
/* 409 */       int minLength = Math.min(left.length, right.length);
/* 410 */       for (int i = 0; i < minLength; i++) {
/* 411 */         int result = Shorts.compare(left[i], right[i]);
/* 412 */         if (result != 0) {
/* 413 */           return result;
/*     */         }
/*     */       }
/* 416 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.Shorts
 * JD-Core Version:    0.6.2
 */