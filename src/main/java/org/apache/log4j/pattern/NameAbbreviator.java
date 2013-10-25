/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public abstract class NameAbbreviator
/*     */ {
/*  32 */   private static final NameAbbreviator DEFAULT = new NOPAbbreviator();
/*     */ 
/*     */   public static NameAbbreviator getAbbreviator(String pattern)
/*     */   {
/*  47 */     if (pattern.length() > 0)
/*     */     {
/*  50 */       String trimmed = pattern.trim();
/*     */ 
/*  52 */       if (trimmed.length() == 0) {
/*  53 */         return DEFAULT;
/*     */       }
/*     */ 
/*  56 */       int i = 0;
/*  57 */       if (trimmed.length() > 0) {
/*  58 */         if (trimmed.charAt(0) == '-') {
/*  59 */           i++;
/*     */         }
/*     */ 
/*  63 */         while ((i < trimmed.length()) && (trimmed.charAt(i) >= '0') && (trimmed.charAt(i) <= '9'))
/*     */         {
/*  65 */           i++;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  73 */       if (i == trimmed.length()) {
/*  74 */         int elements = Integer.parseInt(trimmed);
/*  75 */         if (elements >= 0) {
/*  76 */           return new MaxElementAbbreviator(elements);
/*     */         }
/*  78 */         return new DropElementAbbreviator(-elements);
/*     */       }
/*     */ 
/*  82 */       ArrayList fragments = new ArrayList(5);
/*     */ 
/*  85 */       int pos = 0;
/*     */ 
/*  87 */       while ((pos < trimmed.length()) && (pos >= 0)) {
/*  88 */         int ellipsisPos = pos;
/*     */         int charCount;
/*  90 */         if (trimmed.charAt(pos) == '*') {
/*  91 */           int charCount = 2147483647;
/*  92 */           ellipsisPos++;
/*     */         }
/*  94 */         else if ((trimmed.charAt(pos) >= '0') && (trimmed.charAt(pos) <= '9')) {
/*  95 */           int charCount = trimmed.charAt(pos) - '0';
/*  96 */           ellipsisPos++;
/*     */         } else {
/*  98 */           charCount = 0;
/*     */         }
/*     */ 
/* 102 */         char ellipsis = '\000';
/*     */ 
/* 104 */         if (ellipsisPos < trimmed.length()) {
/* 105 */           ellipsis = trimmed.charAt(ellipsisPos);
/*     */ 
/* 107 */           if (ellipsis == '.') {
/* 108 */             ellipsis = '\000';
/*     */           }
/*     */         }
/*     */ 
/* 112 */         fragments.add(new PatternAbbreviatorFragment(charCount, ellipsis));
/* 113 */         pos = trimmed.indexOf(".", pos);
/*     */ 
/* 115 */         if (pos == -1)
/*     */         {
/*     */           break;
/*     */         }
/* 119 */         pos++;
/*     */       }
/*     */ 
/* 122 */       return new PatternAbbreviator(fragments);
/*     */     }
/*     */ 
/* 128 */     return DEFAULT;
/*     */   }
/*     */ 
/*     */   public static NameAbbreviator getDefaultAbbreviator()
/*     */   {
/* 137 */     return DEFAULT;
/*     */   }
/*     */ 
/*     */   public abstract void abbreviate(int paramInt, StringBuffer paramStringBuffer);
/*     */ 
/*     */   private static class PatternAbbreviator extends NameAbbreviator
/*     */   {
/*     */     private final NameAbbreviator.PatternAbbreviatorFragment[] fragments;
/*     */ 
/*     */     public PatternAbbreviator(List fragments)
/*     */     {
/* 314 */       if (fragments.size() == 0) {
/* 315 */         throw new IllegalArgumentException("fragments must have at least one element");
/*     */       }
/*     */ 
/* 319 */       this.fragments = new NameAbbreviator.PatternAbbreviatorFragment[fragments.size()];
/* 320 */       fragments.toArray(this.fragments);
/*     */     }
/*     */ 
/*     */     public void abbreviate(int nameStart, StringBuffer buf)
/*     */     {
/* 332 */       int pos = nameStart;
/*     */ 
/* 334 */       for (int i = 0; (i < this.fragments.length - 1) && (pos < buf.length()); 
/* 335 */         i++) {
/* 336 */         pos = this.fragments[i].abbreviate(buf, pos);
/*     */       }
/*     */ 
/* 342 */       NameAbbreviator.PatternAbbreviatorFragment terminalFragment = this.fragments[(this.fragments.length - 1)];
/*     */ 
/* 345 */       while ((pos < buf.length()) && (pos >= 0))
/* 346 */         pos = terminalFragment.abbreviate(buf, pos);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class PatternAbbreviatorFragment
/*     */   {
/*     */     private final int charCount;
/*     */     private final char ellipsis;
/*     */ 
/*     */     public PatternAbbreviatorFragment(int charCount, char ellipsis)
/*     */     {
/* 266 */       this.charCount = charCount;
/* 267 */       this.ellipsis = ellipsis;
/*     */     }
/*     */ 
/*     */     public int abbreviate(StringBuffer buf, int startPos)
/*     */     {
/* 277 */       int nextDot = buf.toString().indexOf(".", startPos);
/*     */ 
/* 279 */       if (nextDot != -1) {
/* 280 */         if (nextDot - startPos > this.charCount) {
/* 281 */           buf.delete(startPos + this.charCount, nextDot);
/* 282 */           nextDot = startPos + this.charCount;
/*     */ 
/* 284 */           if (this.ellipsis != 0) {
/* 285 */             buf.insert(nextDot, this.ellipsis);
/* 286 */             nextDot++;
/*     */           }
/*     */         }
/*     */ 
/* 290 */         nextDot++;
/*     */       }
/*     */ 
/* 293 */       return nextDot;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class DropElementAbbreviator extends NameAbbreviator
/*     */   {
/*     */     private final int count;
/*     */ 
/*     */     public DropElementAbbreviator(int count)
/*     */     {
/* 220 */       this.count = count;
/*     */     }
/*     */ 
/*     */     public void abbreviate(int nameStart, StringBuffer buf)
/*     */     {
/* 229 */       int i = this.count;
/* 230 */       for (int pos = buf.indexOf(".", nameStart); 
/* 231 */         pos != -1; 
/* 232 */         pos = buf.indexOf(".", pos + 1)) {
/* 233 */         i--; if (i == 0) {
/* 234 */           buf.delete(nameStart, pos + 1);
/* 235 */           break;
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MaxElementAbbreviator extends NameAbbreviator
/*     */   {
/*     */     private final int count;
/*     */ 
/*     */     public MaxElementAbbreviator(int count)
/*     */     {
/* 179 */       this.count = count;
/*     */     }
/*     */ 
/*     */     public void abbreviate(int nameStart, StringBuffer buf)
/*     */     {
/* 191 */       int end = buf.length() - 1;
/*     */ 
/* 193 */       String bufString = buf.toString();
/* 194 */       for (int i = this.count; i > 0; i--) {
/* 195 */         end = bufString.lastIndexOf(".", end - 1);
/*     */ 
/* 197 */         if ((end == -1) || (end < nameStart)) {
/* 198 */           return;
/*     */         }
/*     */       }
/*     */ 
/* 202 */       buf.delete(nameStart, end + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class NOPAbbreviator extends NameAbbreviator
/*     */   {
/*     */     public void abbreviate(int nameStart, StringBuffer buf)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.NameAbbreviator
 * JD-Core Version:    0.6.2
 */