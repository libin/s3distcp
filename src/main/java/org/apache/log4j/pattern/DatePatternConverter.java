/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParsePosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public final class DatePatternConverter extends LoggingEventPatternConverter
/*     */ {
/*     */   private static final String ABSOLUTE_FORMAT = "ABSOLUTE";
/*     */   private static final String ABSOLUTE_TIME_PATTERN = "HH:mm:ss,SSS";
/*     */   private static final String DATE_AND_TIME_FORMAT = "DATE";
/*     */   private static final String DATE_AND_TIME_PATTERN = "dd MMM yyyy HH:mm:ss,SSS";
/*     */   private static final String ISO8601_FORMAT = "ISO8601";
/*     */   private static final String ISO8601_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";
/*     */   private final CachedDateFormat df;
/*     */ 
/*     */   private DatePatternConverter(String[] options)
/*     */   {
/* 113 */     super("Date", "date");
/*     */     String patternOption;
/*     */     String patternOption;
/* 117 */     if ((options == null) || (options.length == 0))
/*     */     {
/* 120 */       patternOption = null;
/*     */     }
/* 122 */     else patternOption = options[0];
/*     */     String pattern;
/*     */     String pattern;
/* 127 */     if ((patternOption == null) || (patternOption.equalsIgnoreCase("ISO8601")))
/*     */     {
/* 130 */       pattern = "yyyy-MM-dd HH:mm:ss,SSS";
/*     */     }
/*     */     else
/*     */     {
/*     */       String pattern;
/* 131 */       if (patternOption.equalsIgnoreCase("ABSOLUTE")) {
/* 132 */         pattern = "HH:mm:ss,SSS";
/*     */       }
/*     */       else
/*     */       {
/*     */         String pattern;
/* 133 */         if (patternOption.equalsIgnoreCase("DATE"))
/* 134 */           pattern = "dd MMM yyyy HH:mm:ss,SSS";
/*     */         else
/* 136 */           pattern = patternOption;
/*     */       }
/*     */     }
/* 139 */     int maximumCacheValidity = 1000;
/* 140 */     DateFormat simpleFormat = null;
/*     */     try
/*     */     {
/* 143 */       simpleFormat = new SimpleDateFormat(pattern);
/* 144 */       maximumCacheValidity = CachedDateFormat.getMaximumCacheValidity(pattern);
/*     */     } catch (IllegalArgumentException e) {
/* 146 */       LogLog.warn("Could not instantiate SimpleDateFormat with pattern " + patternOption, e);
/*     */ 
/* 151 */       simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
/*     */     }
/*     */ 
/* 155 */     if ((options != null) && (options.length > 1)) {
/* 156 */       TimeZone tz = TimeZone.getTimeZone(options[1]);
/* 157 */       simpleFormat.setTimeZone(tz);
/*     */     } else {
/* 159 */       simpleFormat = new DefaultZoneDateFormat(simpleFormat);
/*     */     }
/*     */ 
/* 162 */     this.df = new CachedDateFormat(simpleFormat, maximumCacheValidity);
/*     */   }
/*     */ 
/*     */   public static DatePatternConverter newInstance(String[] options)
/*     */   {
/* 172 */     return new DatePatternConverter(options);
/*     */   }
/*     */ 
/*     */   public void format(LoggingEvent event, StringBuffer output)
/*     */   {
/* 179 */     synchronized (this) {
/* 180 */       this.df.format(event.timeStamp, output);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void format(Object obj, StringBuffer output)
/*     */   {
/* 188 */     if ((obj instanceof Date)) {
/* 189 */       format((Date)obj, output);
/*     */     }
/*     */ 
/* 192 */     super.format(obj, output);
/*     */   }
/*     */ 
/*     */   public void format(Date date, StringBuffer toAppendTo)
/*     */   {
/* 201 */     synchronized (this) {
/* 202 */       this.df.format(date.getTime(), toAppendTo);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class DefaultZoneDateFormat extends DateFormat
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     private final DateFormat dateFormat;
/*     */ 
/*     */     public DefaultZoneDateFormat(DateFormat format)
/*     */     {
/*  88 */       this.dateFormat = format;
/*     */     }
/*     */ 
/*     */     public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition)
/*     */     {
/*  95 */       this.dateFormat.setTimeZone(TimeZone.getDefault());
/*  96 */       return this.dateFormat.format(date, toAppendTo, fieldPosition);
/*     */     }
/*     */ 
/*     */     public Date parse(String source, ParsePosition pos)
/*     */     {
/* 103 */       this.dateFormat.setTimeZone(TimeZone.getDefault());
/* 104 */       return this.dateFormat.parse(source, pos);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.DatePatternConverter
 * JD-Core Version:    0.6.2
 */