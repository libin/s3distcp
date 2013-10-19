/*     */ package com.google.common.primitives;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.lang.reflect.Field;
/*     */ import java.nio.ByteOrder;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Comparator;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ public final class UnsignedBytes
/*     */ {
/*     */   public static final byte MAX_POWER_OF_TWO = -128;
/*     */   public static final byte MAX_VALUE = -1;
/*     */   private static final int UNSIGNED_MASK = 255;
/*     */ 
/*     */   public static int toInt(byte value)
/*     */   {
/*  77 */     return value & 0xFF;
/*     */   }
/*     */ 
/*     */   public static byte checkedCast(long value)
/*     */   {
/*  91 */     Preconditions.checkArgument(value >> 8 == 0L, "out of range: %s", new Object[] { Long.valueOf(value) });
/*  92 */     return (byte)(int)value;
/*     */   }
/*     */ 
/*     */   public static byte saturatedCast(long value)
/*     */   {
/* 104 */     if (value > toInt((byte)-1)) {
/* 105 */       return -1;
/*     */     }
/* 107 */     if (value < 0L) {
/* 108 */       return 0;
/*     */     }
/* 110 */     return (byte)(int)value;
/*     */   }
/*     */ 
/*     */   public static int compare(byte a, byte b)
/*     */   {
/* 125 */     return toInt(a) - toInt(b);
/*     */   }
/*     */ 
/*     */   public static byte min(byte[] array)
/*     */   {
/* 137 */     Preconditions.checkArgument(array.length > 0);
/* 138 */     int min = toInt(array[0]);
/* 139 */     for (int i = 1; i < array.length; i++) {
/* 140 */       int next = toInt(array[i]);
/* 141 */       if (next < min) {
/* 142 */         min = next;
/*     */       }
/*     */     }
/* 145 */     return (byte)min;
/*     */   }
/*     */ 
/*     */   public static byte max(byte[] array)
/*     */   {
/* 157 */     Preconditions.checkArgument(array.length > 0);
/* 158 */     int max = toInt(array[0]);
/* 159 */     for (int i = 1; i < array.length; i++) {
/* 160 */       int next = toInt(array[i]);
/* 161 */       if (next > max) {
/* 162 */         max = next;
/*     */       }
/*     */     }
/* 165 */     return (byte)max;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static String toString(byte x)
/*     */   {
/* 175 */     return toString(x, 10);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static String toString(byte x, int radix)
/*     */   {
/* 190 */     Preconditions.checkArgument((radix >= 2) && (radix <= 36), "radix (%s) must be between Character.MIN_RADIX and Character.MAX_RADIX", new Object[] { Integer.valueOf(radix) });
/*     */ 
/* 193 */     return Integer.toString(toInt(x), radix);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static byte parseUnsignedByte(String string)
/*     */   {
/* 205 */     return parseUnsignedByte(string, 10);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static byte parseUnsignedByte(String string, int radix)
/*     */   {
/* 220 */     int parse = Integer.parseInt((String)Preconditions.checkNotNull(string), radix);
/*     */ 
/* 222 */     if (parse >> 8 == 0) {
/* 223 */       return (byte)parse;
/*     */     }
/* 225 */     throw new NumberFormatException(new StringBuilder().append("out of range: ").append(parse).toString());
/*     */   }
/*     */ 
/*     */   public static String join(String separator, byte[] array)
/*     */   {
/* 239 */     Preconditions.checkNotNull(separator);
/* 240 */     if (array.length == 0) {
/* 241 */       return "";
/*     */     }
/*     */ 
/* 245 */     StringBuilder builder = new StringBuilder(array.length * (3 + separator.length()));
/* 246 */     builder.append(toInt(array[0]));
/* 247 */     for (int i = 1; i < array.length; i++) {
/* 248 */       builder.append(separator).append(toString(array[i]));
/*     */     }
/* 250 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   public static Comparator<byte[]> lexicographicalComparator()
/*     */   {
/* 270 */     return LexicographicalComparatorHolder.BEST_COMPARATOR;
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static Comparator<byte[]> lexicographicalComparatorJavaImpl() {
/* 275 */     return UnsignedBytes.LexicographicalComparatorHolder.PureJavaComparator.INSTANCE;
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static class LexicographicalComparatorHolder
/*     */   {
/* 287 */     static final String UNSAFE_COMPARATOR_NAME = LexicographicalComparatorHolder.class.getName() + "$UnsafeComparator";
/*     */ 
/* 290 */     static final Comparator<byte[]> BEST_COMPARATOR = getBestComparator();
/*     */ 
/*     */     static Comparator<byte[]> getBestComparator()
/*     */     {
/*     */       try
/*     */       {
/* 425 */         Class theClass = Class.forName(UNSAFE_COMPARATOR_NAME);
/*     */ 
/* 429 */         return (Comparator)theClass.getEnumConstants()[0];
/*     */       }
/*     */       catch (Throwable t) {
/*     */       }
/* 433 */       return UnsignedBytes.lexicographicalComparatorJavaImpl();
/*     */     }
/*     */ 
/*     */     static enum PureJavaComparator
/*     */       implements Comparator<byte[]>
/*     */     {
/* 405 */       INSTANCE;
/*     */ 
/*     */       public int compare(byte[] left, byte[] right) {
/* 408 */         int minLength = Math.min(left.length, right.length);
/* 409 */         for (int i = 0; i < minLength; i++) {
/* 410 */           int result = UnsignedBytes.compare(left[i], right[i]);
/* 411 */           if (result != 0) {
/* 412 */             return result;
/*     */           }
/*     */         }
/* 415 */         return left.length - right.length;
/*     */       }
/*     */     }
/*     */ 
/*     */     @VisibleForTesting
/*     */     static enum UnsafeComparator
/*     */       implements Comparator<byte[]>
/*     */     {
/* 294 */       INSTANCE;
/*     */ 
/*     */       static final boolean littleEndian;
/*     */       static final Unsafe theUnsafe;
/*     */       static final int BYTE_ARRAY_BASE_OFFSET;
/*     */ 
/*     */       public int compare(byte[] left, byte[] right)
/*     */       {
/* 351 */         int minLength = Math.min(left.length, right.length);
/* 352 */         int minWords = minLength / 8;
/*     */ 
/* 359 */         for (int i = 0; i < minWords * 8; i += 8) {
/* 360 */           long lw = theUnsafe.getLong(left, BYTE_ARRAY_BASE_OFFSET + i);
/* 361 */           long rw = theUnsafe.getLong(right, BYTE_ARRAY_BASE_OFFSET + i);
/* 362 */           long diff = lw ^ rw;
/*     */ 
/* 364 */           if (diff != 0L) {
/* 365 */             if (!littleEndian) {
/* 366 */               return UnsignedLongs.compare(lw, rw);
/*     */             }
/*     */ 
/* 370 */             int n = 0;
/*     */ 
/* 372 */             int x = (int)diff;
/* 373 */             if (x == 0) {
/* 374 */               x = (int)(diff >>> 32);
/* 375 */               n = 32;
/*     */             }
/*     */ 
/* 378 */             int y = x << 16;
/* 379 */             if (y == 0)
/* 380 */               n += 16;
/*     */             else {
/* 382 */               x = y;
/*     */             }
/*     */ 
/* 385 */             y = x << 8;
/* 386 */             if (y == 0) {
/* 387 */               n += 8;
/*     */             }
/* 389 */             return (int)((lw >>> n & 0xFF) - (rw >>> n & 0xFF));
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/* 394 */         for (int i = minWords * 8; i < minLength; i++) {
/* 395 */           int result = UnsignedBytes.compare(left[i], right[i]);
/* 396 */           if (result != 0) {
/* 397 */             return result;
/*     */           }
/*     */         }
/* 400 */         return left.length - right.length;
/*     */       }
/*     */ 
/*     */       static
/*     */       {
/* 296 */         littleEndian = ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN);
/*     */ 
/* 324 */         theUnsafe = (Unsafe)AccessController.doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run()
/*     */           {
/*     */             try {
/* 329 */               Field f = Unsafe.class.getDeclaredField("theUnsafe");
/* 330 */               f.setAccessible(true);
/* 331 */               return f.get(null);
/*     */             }
/*     */             catch (NoSuchFieldException e)
/*     */             {
/* 335 */               throw new Error(); } catch (IllegalAccessException e) {
/*     */             }
/* 337 */             throw new Error();
/*     */           }
/*     */         });
/* 342 */         BYTE_ARRAY_BASE_OFFSET = theUnsafe.arrayBaseOffset([B.class);
/*     */ 
/* 345 */         if (theUnsafe.arrayIndexScale([B.class) != 1)
/* 346 */           throw new AssertionError();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.UnsignedBytes
 * JD-Core Version:    0.6.2
 */