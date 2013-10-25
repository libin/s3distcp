/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public abstract class PatternConverter
/*     */ {
/*     */   public PatternConverter next;
/*  38 */   int min = -1;
/*  39 */   int max = 2147483647;
/*  40 */   boolean leftAlign = false;
/*     */ 
/*  91 */   static String[] SPACES = { " ", "  ", "    ", "        ", "                ", "                                " };
/*     */ 
/*     */   protected PatternConverter()
/*     */   {
/*     */   }
/*     */ 
/*     */   protected PatternConverter(FormattingInfo fi)
/*     */   {
/*  47 */     this.min = fi.min;
/*  48 */     this.max = fi.max;
/*  49 */     this.leftAlign = fi.leftAlign;
/*     */   }
/*     */ 
/*     */   protected abstract String convert(LoggingEvent paramLoggingEvent);
/*     */ 
/*     */   public void format(StringBuffer sbuf, LoggingEvent e)
/*     */   {
/*  65 */     String s = convert(e);
/*     */ 
/*  67 */     if (s == null) {
/*  68 */       if (0 < this.min)
/*  69 */         spacePad(sbuf, this.min);
/*  70 */       return;
/*     */     }
/*     */ 
/*  73 */     int len = s.length();
/*     */ 
/*  75 */     if (len > this.max)
/*  76 */       sbuf.append(s.substring(len - this.max));
/*  77 */     else if (len < this.min) {
/*  78 */       if (this.leftAlign) {
/*  79 */         sbuf.append(s);
/*  80 */         spacePad(sbuf, this.min - len);
/*     */       }
/*     */       else {
/*  83 */         spacePad(sbuf, this.min - len);
/*  84 */         sbuf.append(s);
/*     */       }
/*     */     }
/*     */     else
/*  88 */       sbuf.append(s);
/*     */   }
/*     */ 
/*     */   public void spacePad(StringBuffer sbuf, int length)
/*     */   {
/* 100 */     while (length >= 32) {
/* 101 */       sbuf.append(SPACES[5]);
/* 102 */       length -= 32;
/*     */     }
/*     */ 
/* 105 */     for (int i = 4; i >= 0; i--)
/* 106 */       if ((length & 1 << i) != 0)
/* 107 */         sbuf.append(SPACES[i]);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.PatternConverter
 * JD-Core Version:    0.6.2
 */