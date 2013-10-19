/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ 
/*     */ @GwtCompatible
/*     */ public enum CaseFormat
/*     */ {
/*  32 */   LOWER_HYPHEN(CharMatcher.is('-'), "-"), 
/*     */ 
/*  37 */   LOWER_UNDERSCORE(CharMatcher.is('_'), "_"), 
/*     */ 
/*  42 */   LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), ""), 
/*     */ 
/*  47 */   UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), ""), 
/*     */ 
/*  52 */   UPPER_UNDERSCORE(CharMatcher.is('_'), "_");
/*     */ 
/*     */   private final CharMatcher wordBoundary;
/*     */   private final String wordSeparator;
/*     */ 
/*  58 */   private CaseFormat(CharMatcher wordBoundary, String wordSeparator) { this.wordBoundary = wordBoundary;
/*  59 */     this.wordSeparator = wordSeparator;
/*     */   }
/*     */ 
/*     */   public String to(CaseFormat format, String s)
/*     */   {
/*  68 */     if (format == null) {
/*  69 */       throw new NullPointerException();
/*     */     }
/*  71 */     if (s == null) {
/*  72 */       throw new NullPointerException();
/*     */     }
/*     */ 
/*  75 */     if (format == this) {
/*  76 */       return s;
/*     */     }
/*     */ 
/*  80 */     switch (1.$SwitchMap$com$google$common$base$CaseFormat[ordinal()]) {
/*     */     case 3:
/*  82 */       switch (1.$SwitchMap$com$google$common$base$CaseFormat[format.ordinal()]) {
/*     */       case 1:
/*  84 */         return s.replace('-', '_');
/*     */       case 2:
/*  86 */         return Ascii.toUpperCase(s.replace('-', '_'));
/*     */       }
/*  88 */       break;
/*     */     case 1:
/*  90 */       switch (1.$SwitchMap$com$google$common$base$CaseFormat[format.ordinal()]) {
/*     */       case 3:
/*  92 */         return s.replace('_', '-');
/*     */       case 2:
/*  94 */         return Ascii.toUpperCase(s);
/*     */       }
/*  96 */       break;
/*     */     case 2:
/*  98 */       switch (1.$SwitchMap$com$google$common$base$CaseFormat[format.ordinal()]) {
/*     */       case 3:
/* 100 */         return Ascii.toLowerCase(s.replace('_', '-'));
/*     */       case 1:
/* 102 */         return Ascii.toLowerCase(s);
/*     */       }
/*     */ 
/*     */       break;
/*     */     }
/*     */ 
/* 108 */     StringBuilder out = null;
/* 109 */     int i = 0;
/* 110 */     int j = -1;
/* 111 */     while ((j = this.wordBoundary.indexIn(s, ++j)) != -1) {
/* 112 */       if (i == 0)
/*     */       {
/* 114 */         out = new StringBuilder(s.length() + 4 * this.wordSeparator.length());
/* 115 */         out.append(format.normalizeFirstWord(s.substring(i, j)));
/*     */       } else {
/* 117 */         out.append(format.normalizeWord(s.substring(i, j)));
/*     */       }
/* 119 */       out.append(format.wordSeparator);
/* 120 */       i = j + this.wordSeparator.length();
/*     */     }
/* 122 */     if (i == 0) {
/* 123 */       return format.normalizeFirstWord(s);
/*     */     }
/* 125 */     out.append(format.normalizeWord(s.substring(i)));
/* 126 */     return out.toString();
/*     */   }
/*     */ 
/*     */   private String normalizeFirstWord(String word) {
/* 130 */     switch (1.$SwitchMap$com$google$common$base$CaseFormat[ordinal()]) {
/*     */     case 4:
/* 132 */       return Ascii.toLowerCase(word);
/*     */     }
/* 134 */     return normalizeWord(word);
/*     */   }
/*     */ 
/*     */   private String normalizeWord(String word)
/*     */   {
/* 139 */     switch (1.$SwitchMap$com$google$common$base$CaseFormat[ordinal()]) {
/*     */     case 3:
/* 141 */       return Ascii.toLowerCase(word);
/*     */     case 1:
/* 143 */       return Ascii.toLowerCase(word);
/*     */     case 4:
/* 145 */       return firstCharOnlyToUpper(word);
/*     */     case 5:
/* 147 */       return firstCharOnlyToUpper(word);
/*     */     case 2:
/* 149 */       return Ascii.toUpperCase(word);
/*     */     }
/* 151 */     throw new RuntimeException(new StringBuilder().append("unknown case: ").append(this).toString());
/*     */   }
/*     */ 
/*     */   private static String firstCharOnlyToUpper(String word) {
/* 155 */     int length = word.length();
/* 156 */     if (length == 0) {
/* 157 */       return word;
/*     */     }
/* 159 */     return new StringBuilder(length).append(Ascii.toUpperCase(word.charAt(0))).append(Ascii.toLowerCase(word.substring(1))).toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.CaseFormat
 * JD-Core Version:    0.6.2
 */