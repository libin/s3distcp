/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ 
/*     */ @GwtCompatible
/*     */ final class MediumCharMatcher extends CharMatcher
/*     */ {
/*     */   static final int MAX_SIZE = 1023;
/*     */   private final char[] table;
/*     */   private final boolean containsZero;
/*     */   private final long filter;
/*     */   private static final double DESIRED_LOAD_FACTOR = 0.5D;
/*     */ 
/*     */   private MediumCharMatcher(char[] table, long filter, boolean containsZero)
/*     */   {
/*  36 */     this.table = table;
/*  37 */     this.filter = filter;
/*  38 */     this.containsZero = containsZero;
/*     */   }
/*     */ 
/*     */   private boolean checkFilter(int c) {
/*  42 */     return 1L == (1L & this.filter >> c);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static int chooseTableSize(int setSize)
/*     */   {
/*  58 */     if (setSize == 1) {
/*  59 */       return 2;
/*     */     }
/*     */ 
/*  63 */     int tableSize = Integer.highestOneBit(setSize - 1) << 1;
/*  64 */     while (tableSize * 0.5D < setSize) {
/*  65 */       tableSize <<= 1;
/*     */     }
/*  67 */     return tableSize;
/*     */   }
/*     */ 
/*     */   public CharMatcher precomputed()
/*     */   {
/*  75 */     return this;
/*     */   }
/*     */ 
/*     */   static CharMatcher from(char[] chars)
/*     */   {
/*  80 */     long filter = 0L;
/*  81 */     int size = chars.length;
/*  82 */     boolean containsZero = chars[0] == 0;
/*     */ 
/*  84 */     for (char c : chars) {
/*  85 */       filter |= 1L << c;
/*     */     }
/*     */ 
/*  88 */     char[] table = new char[chooseTableSize(size)];
/*  89 */     int mask = table.length - 1;
/*  90 */     for (char c : chars) {
/*  91 */       int index = c & mask;
/*     */       while (true)
/*     */       {
/*  94 */         if (table[index] == 0) {
/*  95 */           table[index] = c;
/*  96 */           break;
/*     */         }
/*     */ 
/*  99 */         index = index + 1 & mask;
/*     */       }
/*     */     }
/* 102 */     return new MediumCharMatcher(table, filter, containsZero);
/*     */   }
/*     */ 
/*     */   public boolean matches(char c)
/*     */   {
/* 107 */     if (c == 0) {
/* 108 */       return this.containsZero;
/*     */     }
/* 110 */     if (!checkFilter(c)) {
/* 111 */       return false;
/*     */     }
/* 113 */     int mask = this.table.length - 1;
/* 114 */     int startingIndex = c & mask;
/* 115 */     int index = startingIndex;
/*     */     do
/*     */     {
/* 118 */       if (this.table[index] == 0) {
/* 119 */         return false;
/*     */       }
/* 121 */       if (this.table[index] == c) {
/* 122 */         return true;
/*     */       }
/*     */ 
/* 125 */       index = index + 1 & mask;
/*     */     }
/*     */ 
/* 128 */     while (index != startingIndex);
/* 129 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.MediumCharMatcher
 * JD-Core Version:    0.6.2
 */