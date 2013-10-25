/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.text.FieldPosition;
/*     */ import java.text.ParsePosition;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ public class ISO8601DateFormat extends AbsoluteTimeDateFormat
/*     */ {
/*     */   private static final long serialVersionUID = -759840745298755296L;
/*     */   private static long lastTime;
/*  55 */   private static char[] lastTimeString = new char[20];
/*     */ 
/*     */   public ISO8601DateFormat()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ISO8601DateFormat(TimeZone timeZone)
/*     */   {
/*  51 */     super(timeZone);
/*     */   }
/*     */ 
/*     */   public StringBuffer format(Date date, StringBuffer sbuf, FieldPosition fieldPosition)
/*     */   {
/*  67 */     long now = date.getTime();
/*  68 */     int millis = (int)(now % 1000L);
/*     */ 
/*  70 */     if ((now - millis != lastTime) || (lastTimeString[0] == 0))
/*     */     {
/*  75 */       this.calendar.setTime(date);
/*     */ 
/*  77 */       int start = sbuf.length();
/*     */ 
/*  79 */       int year = this.calendar.get(1);
/*  80 */       sbuf.append(year);
/*     */       String month;
/*  83 */       switch (this.calendar.get(2)) { case 0:
/*  84 */         month = "-01-"; break;
/*     */       case 1:
/*  85 */         month = "-02-"; break;
/*     */       case 2:
/*  86 */         month = "-03-"; break;
/*     */       case 3:
/*  87 */         month = "-04-"; break;
/*     */       case 4:
/*  88 */         month = "-05-"; break;
/*     */       case 5:
/*  89 */         month = "-06-"; break;
/*     */       case 6:
/*  90 */         month = "-07-"; break;
/*     */       case 7:
/*  91 */         month = "-08-"; break;
/*     */       case 8:
/*  92 */         month = "-09-"; break;
/*     */       case 9:
/*  93 */         month = "-10-"; break;
/*     */       case 10:
/*  94 */         month = "-11-"; break;
/*     */       case 11:
/*  95 */         month = "-12-"; break;
/*     */       default:
/*  96 */         month = "-NA-";
/*     */       }
/*  98 */       sbuf.append(month);
/*     */ 
/* 100 */       int day = this.calendar.get(5);
/* 101 */       if (day < 10)
/* 102 */         sbuf.append('0');
/* 103 */       sbuf.append(day);
/*     */ 
/* 105 */       sbuf.append(' ');
/*     */ 
/* 107 */       int hour = this.calendar.get(11);
/* 108 */       if (hour < 10) {
/* 109 */         sbuf.append('0');
/*     */       }
/* 111 */       sbuf.append(hour);
/* 112 */       sbuf.append(':');
/*     */ 
/* 114 */       int mins = this.calendar.get(12);
/* 115 */       if (mins < 10) {
/* 116 */         sbuf.append('0');
/*     */       }
/* 118 */       sbuf.append(mins);
/* 119 */       sbuf.append(':');
/*     */ 
/* 121 */       int secs = this.calendar.get(13);
/* 122 */       if (secs < 10) {
/* 123 */         sbuf.append('0');
/*     */       }
/* 125 */       sbuf.append(secs);
/*     */ 
/* 127 */       sbuf.append(',');
/*     */ 
/* 130 */       sbuf.getChars(start, sbuf.length(), lastTimeString, 0);
/* 131 */       lastTime = now - millis;
/*     */     }
/*     */     else {
/* 134 */       sbuf.append(lastTimeString);
/*     */     }
/*     */ 
/* 138 */     if (millis < 100)
/* 139 */       sbuf.append('0');
/* 140 */     if (millis < 10) {
/* 141 */       sbuf.append('0');
/*     */     }
/* 143 */     sbuf.append(millis);
/* 144 */     return sbuf;
/*     */   }
/*     */ 
/*     */   public Date parse(String s, ParsePosition pos)
/*     */   {
/* 152 */     return null;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.ISO8601DateFormat
 * JD-Core Version:    0.6.2
 */