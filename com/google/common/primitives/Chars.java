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
/*     */ public final class Chars
/*     */ {
/*     */   public static final int BYTES = 2;
/*     */ 
/*     */   public static int hashCode(char value)
/*     */   {
/*  68 */     return value;
/*     */   }
/*     */ 
/*     */   public static char checkedCast(long value)
/*     */   {
/*  80 */     char result = (char)(int)value;
/*  81 */     Preconditions.checkArgument(result == value, "Out of range: %s", new Object[] { Long.valueOf(value) });
/*  82 */     return result;
/*     */   }
/*     */ 
/*     */   public static char saturatedCast(long value)
/*     */   {
/*  94 */     if (value > 65535L) {
/*  95 */       return 65535;
/*     */     }
/*  97 */     if (value < 0L) {
/*  98 */       return '\000';
/*     */     }
/* 100 */     return (char)(int)value;
/*     */   }
/*     */ 
/*     */   public static int compare(char a, char b)
/*     */   {
/* 113 */     return a - b;
/*     */   }
/*     */ 
/*     */   public static boolean contains(char[] array, char target)
/*     */   {
/* 126 */     for (char value : array) {
/* 127 */       if (value == target) {
/* 128 */         return true;
/*     */       }
/*     */     }
/* 131 */     return false;
/*     */   }
/*     */ 
/*     */   public static int indexOf(char[] array, char target)
/*     */   {
/* 144 */     return indexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int indexOf(char[] array, char target, int start, int end)
/*     */   {
/* 150 */     for (int i = start; i < end; i++) {
/* 151 */       if (array[i] == target) {
/* 152 */         return i;
/*     */       }
/*     */     }
/* 155 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int indexOf(char[] array, char[] target)
/*     */   {
/* 170 */     Preconditions.checkNotNull(array, "array");
/* 171 */     Preconditions.checkNotNull(target, "target");
/* 172 */     if (target.length == 0) {
/* 173 */       return 0;
/*     */     }
/*     */ 
/* 177 */     label64: for (int i = 0; i < array.length - target.length + 1; i++) {
/* 178 */       for (int j = 0; j < target.length; j++) {
/* 179 */         if (array[(i + j)] != target[j]) {
/*     */           break label64;
/*     */         }
/*     */       }
/* 183 */       return i;
/*     */     }
/* 185 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int lastIndexOf(char[] array, char target)
/*     */   {
/* 198 */     return lastIndexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int lastIndexOf(char[] array, char target, int start, int end)
/*     */   {
/* 204 */     for (int i = end - 1; i >= start; i--) {
/* 205 */       if (array[i] == target) {
/* 206 */         return i;
/*     */       }
/*     */     }
/* 209 */     return -1;
/*     */   }
/*     */ 
/*     */   public static char min(char[] array)
/*     */   {
/* 221 */     Preconditions.checkArgument(array.length > 0);
/* 222 */     char min = array[0];
/* 223 */     for (int i = 1; i < array.length; i++) {
/* 224 */       if (array[i] < min) {
/* 225 */         min = array[i];
/*     */       }
/*     */     }
/* 228 */     return min;
/*     */   }
/*     */ 
/*     */   public static char max(char[] array)
/*     */   {
/* 240 */     Preconditions.checkArgument(array.length > 0);
/* 241 */     char max = array[0];
/* 242 */     for (int i = 1; i < array.length; i++) {
/* 243 */       if (array[i] > max) {
/* 244 */         max = array[i];
/*     */       }
/*     */     }
/* 247 */     return max;
/*     */   }
/*     */ 
/*     */   public static char[] concat(char[][] arrays)
/*     */   {
/* 260 */     int length = 0;
/* 261 */     for (char[] array : arrays) {
/* 262 */       length += array.length;
/*     */     }
/* 264 */     char[] result = new char[length];
/* 265 */     int pos = 0;
/* 266 */     for (char[] array : arrays) {
/* 267 */       System.arraycopy(array, 0, result, pos, array.length);
/* 268 */       pos += array.length;
/*     */     }
/* 270 */     return result;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static byte[] toByteArray(char value)
/*     */   {
/* 286 */     return new byte[] { (byte)(value >> '\b'), (byte)value };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static char fromByteArray(byte[] bytes)
/*     */   {
/* 305 */     Preconditions.checkArgument(bytes.length >= 2, "array too small: %s < %s", new Object[] { Integer.valueOf(bytes.length), Integer.valueOf(2) });
/*     */ 
/* 307 */     return fromBytes(bytes[0], bytes[1]);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("doesn't work")
/*     */   public static char fromBytes(byte b1, byte b2)
/*     */   {
/* 319 */     return (char)(b1 << 8 | b2 & 0xFF);
/*     */   }
/*     */ 
/*     */   public static char[] ensureCapacity(char[] array, int minLength, int padding)
/*     */   {
/* 340 */     Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
/* 341 */     Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
/* 342 */     return array.length < minLength ? copyOf(array, minLength + padding) : array;
/*     */   }
/*     */ 
/*     */   private static char[] copyOf(char[] original, int length)
/*     */   {
/* 349 */     char[] copy = new char[length];
/* 350 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
/* 351 */     return copy;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, char[] array)
/*     */   {
/* 364 */     Preconditions.checkNotNull(separator);
/* 365 */     int len = array.length;
/* 366 */     if (len == 0) {
/* 367 */       return "";
/*     */     }
/*     */ 
/* 370 */     StringBuilder builder = new StringBuilder(len + separator.length() * (len - 1));
/*     */ 
/* 372 */     builder.append(array[0]);
/* 373 */     for (int i = 1; i < len; i++) {
/* 374 */       builder.append(separator).append(array[i]);
/*     */     }
/* 376 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<char[]> lexicographicalComparator()
/*     */   {
/* 396 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static char[] toArray(Collection<Character> collection)
/*     */   {
/* 430 */     if ((collection instanceof CharArrayAsList)) {
/* 431 */       return ((CharArrayAsList)collection).toCharArray();
/*     */     }
/*     */ 
/* 434 */     Object[] boxedArray = collection.toArray();
/* 435 */     int len = boxedArray.length;
/* 436 */     char[] array = new char[len];
/* 437 */     for (int i = 0; i < len; i++)
/*     */     {
/* 439 */       array[i] = ((Character)Preconditions.checkNotNull(boxedArray[i])).charValue();
/*     */     }
/* 441 */     return array;
/*     */   }
/*     */ 
/*     */   public static List<Character> asList(char[] backingArray)
/*     */   {
/* 459 */     if (backingArray.length == 0) {
/* 460 */       return Collections.emptyList();
/*     */     }
/* 462 */     return new CharArrayAsList(backingArray);
/*     */   }
/*     */   @GwtCompatible
/*     */   private static class CharArrayAsList extends AbstractList<Character> implements RandomAccess, Serializable { final char[] array;
/*     */     final int start;
/*     */     final int end;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 473 */     CharArrayAsList(char[] array) { this(array, 0, array.length); }
/*     */ 
/*     */     CharArrayAsList(char[] array, int start, int end)
/*     */     {
/* 477 */       this.array = array;
/* 478 */       this.start = start;
/* 479 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 483 */       return this.end - this.start;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 487 */       return false;
/*     */     }
/*     */ 
/*     */     public Character get(int index) {
/* 491 */       Preconditions.checkElementIndex(index, size());
/* 492 */       return Character.valueOf(this.array[(this.start + index)]);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object target)
/*     */     {
/* 497 */       return ((target instanceof Character)) && (Chars.indexOf(this.array, ((Character)target).charValue(), this.start, this.end) != -1);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object target)
/*     */     {
/* 503 */       if ((target instanceof Character)) {
/* 504 */         int i = Chars.indexOf(this.array, ((Character)target).charValue(), this.start, this.end);
/* 505 */         if (i >= 0) {
/* 506 */           return i - this.start;
/*     */         }
/*     */       }
/* 509 */       return -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object target)
/*     */     {
/* 514 */       if ((target instanceof Character)) {
/* 515 */         int i = Chars.lastIndexOf(this.array, ((Character)target).charValue(), this.start, this.end);
/* 516 */         if (i >= 0) {
/* 517 */           return i - this.start;
/*     */         }
/*     */       }
/* 520 */       return -1;
/*     */     }
/*     */ 
/*     */     public Character set(int index, Character element) {
/* 524 */       Preconditions.checkElementIndex(index, size());
/* 525 */       char oldValue = this.array[(this.start + index)];
/* 526 */       this.array[(this.start + index)] = ((Character)Preconditions.checkNotNull(element)).charValue();
/* 527 */       return Character.valueOf(oldValue);
/*     */     }
/*     */ 
/*     */     public List<Character> subList(int fromIndex, int toIndex) {
/* 531 */       int size = size();
/* 532 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
/* 533 */       if (fromIndex == toIndex) {
/* 534 */         return Collections.emptyList();
/*     */       }
/* 536 */       return new CharArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object) {
/* 540 */       if (object == this) {
/* 541 */         return true;
/*     */       }
/* 543 */       if ((object instanceof CharArrayAsList)) {
/* 544 */         CharArrayAsList that = (CharArrayAsList)object;
/* 545 */         int size = size();
/* 546 */         if (that.size() != size) {
/* 547 */           return false;
/*     */         }
/* 549 */         for (int i = 0; i < size; i++) {
/* 550 */           if (this.array[(this.start + i)] != that.array[(that.start + i)]) {
/* 551 */             return false;
/*     */           }
/*     */         }
/* 554 */         return true;
/*     */       }
/* 556 */       return super.equals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 560 */       int result = 1;
/* 561 */       for (int i = this.start; i < this.end; i++) {
/* 562 */         result = 31 * result + Chars.hashCode(this.array[i]);
/*     */       }
/* 564 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 568 */       StringBuilder builder = new StringBuilder(size() * 3);
/* 569 */       builder.append('[').append(this.array[this.start]);
/* 570 */       for (int i = this.start + 1; i < this.end; i++) {
/* 571 */         builder.append(", ").append(this.array[i]);
/*     */       }
/* 573 */       return builder.append(']').toString();
/*     */     }
/*     */ 
/*     */     char[] toCharArray()
/*     */     {
/* 578 */       int size = size();
/* 579 */       char[] result = new char[size];
/* 580 */       System.arraycopy(this.array, this.start, result, 0, size);
/* 581 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator
/*     */     implements Comparator<char[]>
/*     */   {
/* 400 */     INSTANCE;
/*     */ 
/*     */     public int compare(char[] left, char[] right)
/*     */     {
/* 404 */       int minLength = Math.min(left.length, right.length);
/* 405 */       for (int i = 0; i < minLength; i++) {
/* 406 */         int result = Chars.compare(left[i], right[i]);
/* 407 */         if (result != 0) {
/* 408 */           return result;
/*     */         }
/*     */       }
/* 411 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.Chars
 * JD-Core Version:    0.6.2
 */