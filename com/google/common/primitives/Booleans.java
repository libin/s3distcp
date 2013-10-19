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
/*     */ public final class Booleans
/*     */ {
/*     */   public static int hashCode(boolean value)
/*     */   {
/*  59 */     return value ? 1231 : 1237;
/*     */   }
/*     */ 
/*     */   public static int compare(boolean a, boolean b)
/*     */   {
/*  73 */     return a ? 1 : a == b ? 0 : -1;
/*     */   }
/*     */ 
/*     */   public static boolean contains(boolean[] array, boolean target)
/*     */   {
/*  91 */     for (boolean value : array) {
/*  92 */       if (value == target) {
/*  93 */         return true;
/*     */       }
/*     */     }
/*  96 */     return false;
/*     */   }
/*     */ 
/*     */   public static int indexOf(boolean[] array, boolean target)
/*     */   {
/* 113 */     return indexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int indexOf(boolean[] array, boolean target, int start, int end)
/*     */   {
/* 119 */     for (int i = start; i < end; i++) {
/* 120 */       if (array[i] == target) {
/* 121 */         return i;
/*     */       }
/*     */     }
/* 124 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int indexOf(boolean[] array, boolean[] target)
/*     */   {
/* 139 */     Preconditions.checkNotNull(array, "array");
/* 140 */     Preconditions.checkNotNull(target, "target");
/* 141 */     if (target.length == 0) {
/* 142 */       return 0;
/*     */     }
/*     */ 
/* 146 */     label64: for (int i = 0; i < array.length - target.length + 1; i++) {
/* 147 */       for (int j = 0; j < target.length; j++) {
/* 148 */         if (array[(i + j)] != target[j]) {
/*     */           break label64;
/*     */         }
/*     */       }
/* 152 */       return i;
/*     */     }
/* 154 */     return -1;
/*     */   }
/*     */ 
/*     */   public static int lastIndexOf(boolean[] array, boolean target)
/*     */   {
/* 167 */     return lastIndexOf(array, target, 0, array.length);
/*     */   }
/*     */ 
/*     */   private static int lastIndexOf(boolean[] array, boolean target, int start, int end)
/*     */   {
/* 173 */     for (int i = end - 1; i >= start; i--) {
/* 174 */       if (array[i] == target) {
/* 175 */         return i;
/*     */       }
/*     */     }
/* 178 */     return -1;
/*     */   }
/*     */ 
/*     */   public static boolean[] concat(boolean[][] arrays)
/*     */   {
/* 191 */     int length = 0;
/* 192 */     for (boolean[] array : arrays) {
/* 193 */       length += array.length;
/*     */     }
/* 195 */     boolean[] result = new boolean[length];
/* 196 */     int pos = 0;
/* 197 */     for (boolean[] array : arrays) {
/* 198 */       System.arraycopy(array, 0, result, pos, array.length);
/* 199 */       pos += array.length;
/*     */     }
/* 201 */     return result;
/*     */   }
/*     */ 
/*     */   public static boolean[] ensureCapacity(boolean[] array, int minLength, int padding)
/*     */   {
/* 222 */     Preconditions.checkArgument(minLength >= 0, "Invalid minLength: %s", new Object[] { Integer.valueOf(minLength) });
/* 223 */     Preconditions.checkArgument(padding >= 0, "Invalid padding: %s", new Object[] { Integer.valueOf(padding) });
/* 224 */     return array.length < minLength ? copyOf(array, minLength + padding) : array;
/*     */   }
/*     */ 
/*     */   private static boolean[] copyOf(boolean[] original, int length)
/*     */   {
/* 231 */     boolean[] copy = new boolean[length];
/* 232 */     System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
/* 233 */     return copy;
/*     */   }
/*     */ 
/*     */   public static String join(String separator, boolean[] array)
/*     */   {
/* 246 */     Preconditions.checkNotNull(separator);
/* 247 */     if (array.length == 0) {
/* 248 */       return "";
/*     */     }
/*     */ 
/* 252 */     StringBuilder builder = new StringBuilder(array.length * 7);
/* 253 */     builder.append(array[0]);
/* 254 */     for (int i = 1; i < array.length; i++) {
/* 255 */       builder.append(separator).append(array[i]);
/*     */     }
/* 257 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<boolean[]> lexicographicalComparator()
/*     */   {
/* 277 */     return LexicographicalComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static boolean[] toArray(Collection<Boolean> collection)
/*     */   {
/* 314 */     if ((collection instanceof BooleanArrayAsList)) {
/* 315 */       return ((BooleanArrayAsList)collection).toBooleanArray();
/*     */     }
/*     */ 
/* 318 */     Object[] boxedArray = collection.toArray();
/* 319 */     int len = boxedArray.length;
/* 320 */     boolean[] array = new boolean[len];
/* 321 */     for (int i = 0; i < len; i++)
/*     */     {
/* 323 */       array[i] = ((Boolean)Preconditions.checkNotNull(boxedArray[i])).booleanValue();
/*     */     }
/* 325 */     return array;
/*     */   }
/*     */ 
/*     */   public static List<Boolean> asList(boolean[] backingArray)
/*     */   {
/* 343 */     if (backingArray.length == 0) {
/* 344 */       return Collections.emptyList();
/*     */     }
/* 346 */     return new BooleanArrayAsList(backingArray);
/*     */   }
/*     */   @GwtCompatible
/*     */   private static class BooleanArrayAsList extends AbstractList<Boolean> implements RandomAccess, Serializable { final boolean[] array;
/*     */     final int start;
/*     */     final int end;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 357 */     BooleanArrayAsList(boolean[] array) { this(array, 0, array.length); }
/*     */ 
/*     */     BooleanArrayAsList(boolean[] array, int start, int end)
/*     */     {
/* 361 */       this.array = array;
/* 362 */       this.start = start;
/* 363 */       this.end = end;
/*     */     }
/*     */ 
/*     */     public int size() {
/* 367 */       return this.end - this.start;
/*     */     }
/*     */ 
/*     */     public boolean isEmpty() {
/* 371 */       return false;
/*     */     }
/*     */ 
/*     */     public Boolean get(int index) {
/* 375 */       Preconditions.checkElementIndex(index, size());
/* 376 */       return Boolean.valueOf(this.array[(this.start + index)]);
/*     */     }
/*     */ 
/*     */     public boolean contains(Object target)
/*     */     {
/* 381 */       return ((target instanceof Boolean)) && (Booleans.indexOf(this.array, ((Boolean)target).booleanValue(), this.start, this.end) != -1);
/*     */     }
/*     */ 
/*     */     public int indexOf(Object target)
/*     */     {
/* 387 */       if ((target instanceof Boolean)) {
/* 388 */         int i = Booleans.indexOf(this.array, ((Boolean)target).booleanValue(), this.start, this.end);
/* 389 */         if (i >= 0) {
/* 390 */           return i - this.start;
/*     */         }
/*     */       }
/* 393 */       return -1;
/*     */     }
/*     */ 
/*     */     public int lastIndexOf(Object target)
/*     */     {
/* 398 */       if ((target instanceof Boolean)) {
/* 399 */         int i = Booleans.lastIndexOf(this.array, ((Boolean)target).booleanValue(), this.start, this.end);
/* 400 */         if (i >= 0) {
/* 401 */           return i - this.start;
/*     */         }
/*     */       }
/* 404 */       return -1;
/*     */     }
/*     */ 
/*     */     public Boolean set(int index, Boolean element) {
/* 408 */       Preconditions.checkElementIndex(index, size());
/* 409 */       boolean oldValue = this.array[(this.start + index)];
/* 410 */       this.array[(this.start + index)] = ((Boolean)Preconditions.checkNotNull(element)).booleanValue();
/* 411 */       return Boolean.valueOf(oldValue);
/*     */     }
/*     */ 
/*     */     public List<Boolean> subList(int fromIndex, int toIndex) {
/* 415 */       int size = size();
/* 416 */       Preconditions.checkPositionIndexes(fromIndex, toIndex, size);
/* 417 */       if (fromIndex == toIndex) {
/* 418 */         return Collections.emptyList();
/*     */       }
/* 420 */       return new BooleanArrayAsList(this.array, this.start + fromIndex, this.start + toIndex);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object object) {
/* 424 */       if (object == this) {
/* 425 */         return true;
/*     */       }
/* 427 */       if ((object instanceof BooleanArrayAsList)) {
/* 428 */         BooleanArrayAsList that = (BooleanArrayAsList)object;
/* 429 */         int size = size();
/* 430 */         if (that.size() != size) {
/* 431 */           return false;
/*     */         }
/* 433 */         for (int i = 0; i < size; i++) {
/* 434 */           if (this.array[(this.start + i)] != that.array[(that.start + i)]) {
/* 435 */             return false;
/*     */           }
/*     */         }
/* 438 */         return true;
/*     */       }
/* 440 */       return super.equals(object);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 444 */       int result = 1;
/* 445 */       for (int i = this.start; i < this.end; i++) {
/* 446 */         result = 31 * result + Booleans.hashCode(this.array[i]);
/*     */       }
/* 448 */       return result;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 452 */       StringBuilder builder = new StringBuilder(size() * 7);
/* 453 */       builder.append(this.array[this.start] != 0 ? "[true" : "[false");
/* 454 */       for (int i = this.start + 1; i < this.end; i++) {
/* 455 */         builder.append(this.array[i] != 0 ? ", true" : ", false");
/*     */       }
/* 457 */       return builder.append(']').toString();
/*     */     }
/*     */ 
/*     */     boolean[] toBooleanArray()
/*     */     {
/* 462 */       int size = size();
/* 463 */       boolean[] result = new boolean[size];
/* 464 */       System.arraycopy(this.array, this.start, result, 0, size);
/* 465 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum LexicographicalComparator
/*     */     implements Comparator<boolean[]>
/*     */   {
/* 281 */     INSTANCE;
/*     */ 
/*     */     public int compare(boolean[] left, boolean[] right)
/*     */     {
/* 285 */       int minLength = Math.min(left.length, right.length);
/* 286 */       for (int i = 0; i < minLength; i++) {
/* 287 */         int result = Booleans.compare(left[i], right[i]);
/* 288 */         if (result != 0) {
/* 289 */           return result;
/*     */         }
/*     */       }
/* 292 */       return left.length - right.length;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.Booleans
 * JD-Core Version:    0.6.2
 */