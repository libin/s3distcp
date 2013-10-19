/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ public final class FormattingInfo
/*     */ {
/*  35 */   private static final char[] SPACES = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };
/*     */ 
/*  41 */   private static final FormattingInfo DEFAULT = new FormattingInfo(false, 0, 2147483647);
/*     */   private final int minLength;
/*     */   private final int maxLength;
/*     */   private final boolean leftAlign;
/*     */ 
/*     */   public FormattingInfo(boolean leftAlign, int minLength, int maxLength)
/*     */   {
/*  67 */     this.leftAlign = leftAlign;
/*  68 */     this.minLength = minLength;
/*  69 */     this.maxLength = maxLength;
/*     */   }
/*     */ 
/*     */   public static FormattingInfo getDefault()
/*     */   {
/*  77 */     return DEFAULT;
/*     */   }
/*     */ 
/*     */   public boolean isLeftAligned()
/*     */   {
/*  85 */     return this.leftAlign;
/*     */   }
/*     */ 
/*     */   public int getMinLength()
/*     */   {
/*  93 */     return this.minLength;
/*     */   }
/*     */ 
/*     */   public int getMaxLength()
/*     */   {
/* 101 */     return this.maxLength;
/*     */   }
/*     */ 
/*     */   public void format(int fieldStart, StringBuffer buffer)
/*     */   {
/* 111 */     int rawLength = buffer.length() - fieldStart;
/*     */ 
/* 113 */     if (rawLength > this.maxLength)
/* 114 */       buffer.delete(fieldStart, buffer.length() - this.maxLength);
/* 115 */     else if (rawLength < this.minLength)
/* 116 */       if (this.leftAlign) {
/* 117 */         int fieldEnd = buffer.length();
/* 118 */         buffer.setLength(fieldStart + this.minLength);
/*     */ 
/* 120 */         for (int i = fieldEnd; i < buffer.length(); i++)
/* 121 */           buffer.setCharAt(i, ' ');
/*     */       }
/*     */       else {
/* 124 */         for (int padLength = this.minLength - rawLength; 
/* 126 */           padLength > 8; padLength -= 8) {
/* 127 */           buffer.insert(fieldStart, SPACES);
/*     */         }
/*     */ 
/* 130 */         buffer.insert(fieldStart, SPACES, 0, padLength);
/*     */       }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.FormattingInfo
 * JD-Core Version:    0.6.2
 */