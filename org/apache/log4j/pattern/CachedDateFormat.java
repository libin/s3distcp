/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public final class CachedDateFormat extends DateFormat
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   public static final int NO_MILLISECONDS = -2;
/*     */   private static final String DIGITS = "0123456789";
/*     */   public static final int UNRECOGNIZED_MILLISECONDS = -1;
/*     */   private static final int MAGIC1 = 654;
/*     */   private static final String MAGICSTRING1 = "654";
/*     */   private static final int MAGIC2 = 987;
/*     */   private static final String MAGICSTRING2 = "987";
/*     */   private static final String ZERO_STRING = "000";
/*     */   private final DateFormat formatter;
/*     */   private int millisecondStart;
/*     */   private long slotBegin;
/* 105 */   private StringBuffer cache = new StringBuffer(50);
/*     */   private final int expiration;
/*     */   private long previousTime;
/* 122 */   private final Date tmpDate = new Date(0L);
/*     */ 
/*     */   public CachedDateFormat(DateFormat dateFormat, int expiration)
/*     */   {
/* 133 */     if (dateFormat == null) {
/* 134 */       throw new IllegalArgumentException("dateFormat cannot be null");
/*     */     }
/*     */ 
/* 137 */     if (expiration < 0) {
/* 138 */       throw new IllegalArgumentException("expiration must be non-negative");
/*     */     }
/*     */ 
/* 141 */     this.formatter = dateFormat;
/* 142 */     this.expiration = expiration;
/* 143 */     this.millisecondStart = 0;
/*     */ 
/* 148 */     this.previousTime = -9223372036854775808L;
/* 149 */     this.slotBegin = -9223372036854775808L;
/*     */   }
/*     */ 
/*     */   public static int findMillisecondStart(long time, String formatted, DateFormat formatter)
/*     */   {
/* 163 */     long slotBegin = time / 1000L * 1000L;
/*     */ 
/* 165 */     if (slotBegin > time) {
/* 166 */       slotBegin -= 1000L;
/*     */     }
/*     */ 
/* 169 */     int millis = (int)(time - slotBegin);
/*     */ 
/* 171 */     int magic = 654;
/* 172 */     String magicString = "654";
/*     */ 
/* 174 */     if (millis == 654) {
/* 175 */       magic = 987;
/* 176 */       magicString = "987";
/*     */     }
/*     */ 
/* 179 */     String plusMagic = formatter.format(new Date(slotBegin + magic));
/*     */ 
/* 185 */     if (plusMagic.length() != formatted.length()) {
/* 186 */       return -1;
/*     */     }
/*     */ 
/* 189 */     for (int i = 0; i < formatted.length(); i++) {
/* 190 */       if (formatted.charAt(i) != plusMagic.charAt(i))
/*     */       {
/* 193 */         StringBuffer formattedMillis = new StringBuffer("ABC");
/* 194 */         millisecondFormat(millis, formattedMillis, 0);
/*     */ 
/* 196 */         String plusZero = formatter.format(new Date(slotBegin));
/*     */ 
/* 200 */         if ((plusZero.length() == formatted.length()) && (magicString.regionMatches(0, plusMagic, i, magicString.length())) && (formattedMillis.toString().regionMatches(0, formatted, i, magicString.length())) && ("000".regionMatches(0, plusZero, i, "000".length())))
/*     */         {
/* 208 */           return i;
/*     */         }
/* 210 */         return -1;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 216 */     return -2;
/*     */   }
/*     */ 
/*     */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*     */   {
/* 229 */     format(date.getTime(), sbuf);
/*     */ 
/* 231 */     return sbuf;
/*     */   }
/*     */ 
/*     */   public StringBuffer format(long now, StringBuffer buf)
/*     */   {
/* 246 */     if (now == this.previousTime) {
/* 247 */       buf.append(this.cache);
/*     */ 
/* 249 */       return buf;
/*     */     }
/*     */ 
/* 256 */     if ((this.millisecondStart != -1) && (now < this.slotBegin + this.expiration) && (now >= this.slotBegin) && (now < this.slotBegin + 1000L))
/*     */     {
/* 265 */       if (this.millisecondStart >= 0) {
/* 266 */         millisecondFormat((int)(now - this.slotBegin), this.cache, this.millisecondStart);
/*     */       }
/*     */ 
/* 272 */       this.previousTime = now;
/* 273 */       buf.append(this.cache);
/*     */ 
/* 275 */       return buf;
/*     */     }
/*     */ 
/* 281 */     this.cache.setLength(0);
/* 282 */     this.tmpDate.setTime(now);
/* 283 */     this.cache.append(this.formatter.format(this.tmpDate));
/* 284 */     buf.append(this.cache);
/* 285 */     this.previousTime = now;
/* 286 */     this.slotBegin = (this.previousTime / 1000L * 1000L);
/*     */ 
/* 288 */     if (this.slotBegin > this.previousTime) {
/* 289 */       this.slotBegin -= 1000L;
/*     */     }
/*     */ 
/* 296 */     if (this.millisecondStart >= 0) {
/* 297 */       this.millisecondStart = findMillisecondStart(now, this.cache.toString(), this.formatter);
/*     */     }
/*     */ 
/* 301 */     return buf;
/*     */   }
/*     */ 
/*     */   private static void millisecondFormat(int millis, StringBuffer buf, int offset)
/*     */   {
/* 313 */     buf.setCharAt(offset, "0123456789".charAt(millis / 100));
/* 314 */     buf.setCharAt(offset + 1, "0123456789".charAt(millis / 10 % 10));
/* 315 */     buf.setCharAt(offset + 2, "0123456789".charAt(millis % 10));
/*     */   }
/*     */ 
/*     */   public void setTimeZone(TimeZone timeZone)
/*     */   {
/* 326 */     this.formatter.setTimeZone(timeZone);
/* 327 */     this.previousTime = -9223372036854775808L;
/* 328 */     this.slotBegin = -9223372036854775808L;
/*     */   }
/*     */ 
/*     */   public Date parse(String s, ParsePosition pos)
/*     */   {
/* 339 */     return this.formatter.parse(s, pos);
/*     */   }
/*     */ 
/*     */   public NumberFormat getNumberFormat()
/*     */   {
/* 348 */     return this.formatter.getNumberFormat();
/*     */   }
/*     */ 
/*     */   public static int getMaximumCacheValidity(String pattern)
/*     */   {
/* 364 */     int firstS = pattern.indexOf('S');
/*     */ 
/* 366 */     if ((firstS >= 0) && (firstS != pattern.lastIndexOf("SSS"))) {
/* 367 */       return 1;
/*     */     }
/*     */ 
/* 370 */     return 1000;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.CachedDateFormat
 * JD-Core Version:    0.6.2
 */