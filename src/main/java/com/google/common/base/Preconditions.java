/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Preconditions
/*     */ {
/*     */   public static void checkArgument(boolean expression)
/*     */   {
/*  75 */     if (!expression)
/*  76 */       throw new IllegalArgumentException();
/*     */   }
/*     */ 
/*     */   public static void checkArgument(boolean expression, @Nullable Object errorMessage)
/*     */   {
/*  91 */     if (!expression)
/*  92 */       throw new IllegalArgumentException(String.valueOf(errorMessage));
/*     */   }
/*     */ 
/*     */   public static void checkArgument(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object[] errorMessageArgs)
/*     */   {
/* 118 */     if (!expression)
/* 119 */       throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
/*     */   }
/*     */ 
/*     */   public static void checkState(boolean expression)
/*     */   {
/* 132 */     if (!expression)
/* 133 */       throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public static void checkState(boolean expression, @Nullable Object errorMessage)
/*     */   {
/* 148 */     if (!expression)
/* 149 */       throw new IllegalStateException(String.valueOf(errorMessage));
/*     */   }
/*     */ 
/*     */   public static void checkState(boolean expression, @Nullable String errorMessageTemplate, @Nullable Object[] errorMessageArgs)
/*     */   {
/* 175 */     if (!expression)
/* 176 */       throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
/*     */   }
/*     */ 
/*     */   public static <T> T checkNotNull(T reference)
/*     */   {
/* 190 */     if (reference == null) {
/* 191 */       throw new NullPointerException();
/*     */     }
/* 193 */     return reference;
/*     */   }
/*     */ 
/*     */   public static <T> T checkNotNull(T reference, @Nullable Object errorMessage)
/*     */   {
/* 207 */     if (reference == null) {
/* 208 */       throw new NullPointerException(String.valueOf(errorMessage));
/*     */     }
/* 210 */     return reference;
/*     */   }
/*     */ 
/*     */   public static <T> T checkNotNull(T reference, @Nullable String errorMessageTemplate, @Nullable Object[] errorMessageArgs)
/*     */   {
/* 233 */     if (reference == null)
/*     */     {
/* 235 */       throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
/*     */     }
/*     */ 
/* 238 */     return reference;
/*     */   }
/*     */ 
/*     */   public static int checkElementIndex(int index, int size)
/*     */   {
/* 284 */     return checkElementIndex(index, size, "index");
/*     */   }
/*     */ 
/*     */   public static int checkElementIndex(int index, int size, @Nullable String desc)
/*     */   {
/* 304 */     if ((index < 0) || (index >= size)) {
/* 305 */       throw new IndexOutOfBoundsException(badElementIndex(index, size, desc));
/*     */     }
/* 307 */     return index;
/*     */   }
/*     */ 
/*     */   private static String badElementIndex(int index, int size, String desc) {
/* 311 */     if (index < 0)
/* 312 */       return format("%s (%s) must not be negative", new Object[] { desc, Integer.valueOf(index) });
/* 313 */     if (size < 0) {
/* 314 */       throw new IllegalArgumentException(new StringBuilder().append("negative size: ").append(size).toString());
/*     */     }
/* 316 */     return format("%s (%s) must be less than size (%s)", new Object[] { desc, Integer.valueOf(index), Integer.valueOf(size) });
/*     */   }
/*     */ 
/*     */   public static int checkPositionIndex(int index, int size)
/*     */   {
/* 334 */     return checkPositionIndex(index, size, "index");
/*     */   }
/*     */ 
/*     */   public static int checkPositionIndex(int index, int size, @Nullable String desc)
/*     */   {
/* 354 */     if ((index < 0) || (index > size)) {
/* 355 */       throw new IndexOutOfBoundsException(badPositionIndex(index, size, desc));
/*     */     }
/* 357 */     return index;
/*     */   }
/*     */ 
/*     */   private static String badPositionIndex(int index, int size, String desc) {
/* 361 */     if (index < 0)
/* 362 */       return format("%s (%s) must not be negative", new Object[] { desc, Integer.valueOf(index) });
/* 363 */     if (size < 0) {
/* 364 */       throw new IllegalArgumentException(new StringBuilder().append("negative size: ").append(size).toString());
/*     */     }
/* 366 */     return format("%s (%s) must not be greater than size (%s)", new Object[] { desc, Integer.valueOf(index), Integer.valueOf(size) });
/*     */   }
/*     */ 
/*     */   public static void checkPositionIndexes(int start, int end, int size)
/*     */   {
/* 387 */     if ((start < 0) || (end < start) || (end > size))
/* 388 */       throw new IndexOutOfBoundsException(badPositionIndexes(start, end, size));
/*     */   }
/*     */ 
/*     */   private static String badPositionIndexes(int start, int end, int size)
/*     */   {
/* 393 */     if ((start < 0) || (start > size)) {
/* 394 */       return badPositionIndex(start, size, "start index");
/*     */     }
/* 396 */     if ((end < 0) || (end > size)) {
/* 397 */       return badPositionIndex(end, size, "end index");
/*     */     }
/*     */ 
/* 400 */     return format("end index (%s) must not be less than start index (%s)", new Object[] { Integer.valueOf(end), Integer.valueOf(start) });
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static String format(String template, @Nullable Object[] args)
/*     */   {
/* 418 */     template = String.valueOf(template);
/*     */ 
/* 421 */     StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
/*     */ 
/* 423 */     int templateStart = 0;
/* 424 */     int i = 0;
/* 425 */     while (i < args.length) {
/* 426 */       int placeholderStart = template.indexOf("%s", templateStart);
/* 427 */       if (placeholderStart == -1) {
/*     */         break;
/*     */       }
/* 430 */       builder.append(template.substring(templateStart, placeholderStart));
/* 431 */       builder.append(args[(i++)]);
/* 432 */       templateStart = placeholderStart + 2;
/*     */     }
/* 434 */     builder.append(template.substring(templateStart));
/*     */ 
/* 437 */     if (i < args.length) {
/* 438 */       builder.append(" [");
/* 439 */       builder.append(args[(i++)]);
/* 440 */       while (i < args.length) {
/* 441 */         builder.append(", ");
/* 442 */         builder.append(args[(i++)]);
/*     */       }
/* 444 */       builder.append(']');
/*     */     }
/*     */ 
/* 447 */     return builder.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Preconditions
 * JD-Core Version:    0.6.2
 */