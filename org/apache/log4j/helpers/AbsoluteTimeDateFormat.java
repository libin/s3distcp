/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class AbsoluteTimeDateFormat extends DateFormat
/*     */ {
/*     */   private static final long serialVersionUID = -388856345976723342L;
/*     */   public static final String ABS_TIME_DATE_FORMAT = "ABSOLUTE";
/*     */   public static final String DATE_AND_TIME_DATE_FORMAT = "DATE";
/*     */   public static final String ISO8601_DATE_FORMAT = "ISO8601";
/*     */   private static long previousTime;
/*  71 */   private static char[] previousTimeWithoutMillis = new char[9];
/*     */ 
/*     */   public AbsoluteTimeDateFormat()
/*     */   {
/*  62 */     setCalendar(Calendar.getInstance());
/*     */   }
/*     */ 
/*     */   public AbsoluteTimeDateFormat(TimeZone timeZone)
/*     */   {
/*  67 */     setCalendar(Calendar.getInstance(timeZone));
/*     */   }
/*     */ 
/*     */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*     */   {
/*  85 */     long now = date.getTime();
/*  86 */     int millis = (int)(now % 1000L);
/*     */ 
/*  88 */     if ((now - millis != previousTime) || (previousTimeWithoutMillis[0] == 0))
/*     */     {
/*  93 */       this.calendar.setTime(date);
/*     */ 
/*  95 */       int start = sbuf.length();
/*     */ 
/*  97 */       int hour = this.calendar.get(11);
/*  98 */       if (hour < 10) {
/*  99 */         sbuf.append('0');
/*     */       }
/* 101 */       sbuf.append(hour);
/* 102 */       sbuf.append(':');
/*     */ 
/* 104 */       int mins = this.calendar.get(12);
/* 105 */       if (mins < 10) {
/* 106 */         sbuf.append('0');
/*     */       }
/* 108 */       sbuf.append(mins);
/* 109 */       sbuf.append(':');
/*     */ 
/* 111 */       int secs = this.calendar.get(13);
/* 112 */       if (secs < 10) {
/* 113 */         sbuf.append('0');
/*     */       }
/* 115 */       sbuf.append(secs);
/* 116 */       sbuf.append(',');
/*     */ 
/* 119 */       sbuf.getChars(start, sbuf.length(), previousTimeWithoutMillis, 0);
/*     */ 
/* 121 */       previousTime = now - millis;
/*     */     }
/*     */     else {
/* 124 */       sbuf.append(previousTimeWithoutMillis);
/*     */     }
/*     */ 
/* 129 */     if (millis < 100)
/* 130 */       sbuf.append('0');
/* 131 */     if (millis < 10) {
/* 132 */       sbuf.append('0');
/*     */     }
/* 134 */     sbuf.append(millis);
/* 135 */     return sbuf;
/*     */   }
/*     */ 
/*     */   public Date parse(String s, ParsePosition pos)
/*     */   {
/* 143 */     return null;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.AbsoluteTimeDateFormat
 * JD-Core Version:    0.6.2
 */