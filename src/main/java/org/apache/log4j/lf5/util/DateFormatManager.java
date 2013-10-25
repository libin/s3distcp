/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class DateFormatManager
/*     */ {
/*  50 */   private TimeZone _timeZone = null;
/*  51 */   private Locale _locale = null;
/*     */ 
/*  53 */   private String _pattern = null;
/*  54 */   private DateFormat _dateFormat = null;
/*     */ 
/*     */   public DateFormatManager()
/*     */   {
/*  61 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone)
/*     */   {
/*  67 */     this._timeZone = timeZone;
/*  68 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(Locale locale)
/*     */   {
/*  74 */     this._locale = locale;
/*  75 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(String pattern)
/*     */   {
/*  81 */     this._pattern = pattern;
/*  82 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, Locale locale)
/*     */   {
/*  88 */     this._timeZone = timeZone;
/*  89 */     this._locale = locale;
/*  90 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, String pattern)
/*     */   {
/*  96 */     this._timeZone = timeZone;
/*  97 */     this._pattern = pattern;
/*  98 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(Locale locale, String pattern)
/*     */   {
/* 104 */     this._locale = locale;
/* 105 */     this._pattern = pattern;
/* 106 */     configure();
/*     */   }
/*     */ 
/*     */   public DateFormatManager(TimeZone timeZone, Locale locale, String pattern)
/*     */   {
/* 112 */     this._timeZone = timeZone;
/* 113 */     this._locale = locale;
/* 114 */     this._pattern = pattern;
/* 115 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized TimeZone getTimeZone()
/*     */   {
/* 123 */     if (this._timeZone == null) {
/* 124 */       return TimeZone.getDefault();
/*     */     }
/* 126 */     return this._timeZone;
/*     */   }
/*     */ 
/*     */   public synchronized void setTimeZone(TimeZone timeZone)
/*     */   {
/* 131 */     this._timeZone = timeZone;
/* 132 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized Locale getLocale() {
/* 136 */     if (this._locale == null) {
/* 137 */       return Locale.getDefault();
/*     */     }
/* 139 */     return this._locale;
/*     */   }
/*     */ 
/*     */   public synchronized void setLocale(Locale locale)
/*     */   {
/* 144 */     this._locale = locale;
/* 145 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized String getPattern() {
/* 149 */     return this._pattern;
/*     */   }
/*     */ 
/*     */   public synchronized void setPattern(String pattern)
/*     */   {
/* 156 */     this._pattern = pattern;
/* 157 */     configure();
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized String getOutputFormat()
/*     */   {
/* 166 */     return this._pattern;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public synchronized void setOutputFormat(String pattern)
/*     */   {
/* 174 */     this._pattern = pattern;
/* 175 */     configure();
/*     */   }
/*     */ 
/*     */   public synchronized DateFormat getDateFormatInstance() {
/* 179 */     return this._dateFormat;
/*     */   }
/*     */ 
/*     */   public synchronized void setDateFormatInstance(DateFormat dateFormat) {
/* 183 */     this._dateFormat = dateFormat;
/*     */   }
/*     */ 
/*     */   public String format(Date date)
/*     */   {
/* 188 */     return getDateFormatInstance().format(date);
/*     */   }
/*     */ 
/*     */   public String format(Date date, String pattern) {
/* 192 */     DateFormat formatter = null;
/* 193 */     formatter = getDateFormatInstance();
/* 194 */     if ((formatter instanceof SimpleDateFormat)) {
/* 195 */       formatter = (SimpleDateFormat)formatter.clone();
/* 196 */       ((SimpleDateFormat)formatter).applyPattern(pattern);
/*     */     }
/* 198 */     return formatter.format(date);
/*     */   }
/*     */ 
/*     */   public Date parse(String date)
/*     */     throws ParseException
/*     */   {
/* 205 */     return getDateFormatInstance().parse(date);
/*     */   }
/*     */ 
/*     */   public Date parse(String date, String pattern)
/*     */     throws ParseException
/*     */   {
/* 212 */     DateFormat formatter = null;
/* 213 */     formatter = getDateFormatInstance();
/* 214 */     if ((formatter instanceof SimpleDateFormat)) {
/* 215 */       formatter = (SimpleDateFormat)formatter.clone();
/* 216 */       ((SimpleDateFormat)formatter).applyPattern(pattern);
/*     */     }
/* 218 */     return formatter.parse(date);
/*     */   }
/*     */ 
/*     */   private synchronized void configure()
/*     */   {
/* 229 */     this._dateFormat = SimpleDateFormat.getDateTimeInstance(0, 0, getLocale());
/*     */ 
/* 232 */     this._dateFormat.setTimeZone(getTimeZone());
/*     */ 
/* 234 */     if (this._pattern != null)
/* 235 */       ((SimpleDateFormat)this._dateFormat).applyPattern(this._pattern);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.util.DateFormatManager
 * JD-Core Version:    0.6.2
 */