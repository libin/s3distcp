/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ 
/*     */ @GwtCompatible
/*     */ final class SmallCharMatcher extends CharMatcher
/*     */ {
/*     */   static final int MAX_SIZE = 63;
/*     */   static final int MAX_TABLE_SIZE = 128;
/*     */   private final boolean reprobe;
/*     */   private final char[] table;
/*     */   private final boolean containsZero;
/*     */   final long filter;
/*     */ 
/*     */   private SmallCharMatcher(char[] table, long filter, boolean containsZero, boolean reprobe)
/*     */   {
/*  39 */     this.table = table;
/*  40 */     this.filter = filter;
/*  41 */     this.containsZero = containsZero;
/*  42 */     this.reprobe = reprobe;
/*     */   }
/*     */ 
/*     */   private boolean checkFilter(int c) {
/*  46 */     return 1L == (1L & this.filter >> c);
/*     */   }
/*     */ 
/*     */   public CharMatcher precomputed()
/*     */   {
/*  51 */     return this;
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static char[] buildTable(int modulus, char[] allChars, boolean reprobe) {
/*  56 */     char[] table = new char[modulus];
/*  57 */     for (int i = 0; i < allChars.length; i++) {
/*  58 */       char c = allChars[i];
/*  59 */       int index = c % modulus;
/*  60 */       if (index < 0) {
/*  61 */         index += modulus;
/*     */       }
/*  63 */       if ((table[index] != 0) && (!reprobe))
/*  64 */         return null;
/*  65 */       if (reprobe) {
/*  66 */         while (table[index] != 0) {
/*  67 */           index = (index + 1) % modulus;
/*     */         }
/*     */       }
/*  70 */       table[index] = c;
/*     */     }
/*  72 */     return table;
/*     */   }
/*     */ 
/*     */   static CharMatcher from(char[] chars) {
/*  76 */     long filter = 0L;
/*  77 */     int size = chars.length;
/*  78 */     boolean containsZero = false;
/*  79 */     boolean reprobe = false;
/*  80 */     containsZero = chars[0] == 0;
/*     */ 
/*  83 */     for (char c : chars) {
/*  84 */       filter |= 1L << c;
/*     */     }
/*  86 */     char[] table = null;
/*  87 */     for (int i = size; i < 128; i++) {
/*  88 */       table = buildTable(i, chars, false);
/*  89 */       if (table != null)
/*     */       {
/*     */         break;
/*     */       }
/*     */     }
/*  94 */     if (table == null) {
/*  95 */       table = buildTable(128, chars, true);
/*  96 */       reprobe = true;
/*     */     }
/*  98 */     return new SmallCharMatcher(table, filter, containsZero, reprobe);
/*     */   }
/*     */ 
/*     */   public boolean matches(char c)
/*     */   {
/* 103 */     if (c == 0) {
/* 104 */       return this.containsZero;
/*     */     }
/* 106 */     if (!checkFilter(c)) {
/* 107 */       return false;
/*     */     }
/* 109 */     int index = c % this.table.length;
/* 110 */     if (index < 0) {
/* 111 */       index += this.table.length;
/*     */     }
/*     */     while (true)
/*     */     {
/* 115 */       if (this.table[index] == 0)
/* 116 */         return false;
/* 117 */       if (this.table[index] == c)
/* 118 */         return true;
/* 119 */       if (!this.reprobe)
/*     */         break;
/* 121 */       index = (index + 1) % this.table.length;
/*     */     }
/* 123 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.SmallCharMatcher
 * JD-Core Version:    0.6.2
 */