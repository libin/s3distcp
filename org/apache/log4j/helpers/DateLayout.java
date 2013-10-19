/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.text.DateFormat;
/*     */ import java.text.FieldPosition;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.TimeZone;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public abstract class DateLayout extends Layout
/*     */ {
/*     */   public static final String NULL_DATE_FORMAT = "NULL";
/*     */   public static final String RELATIVE_TIME_DATE_FORMAT = "RELATIVE";
/*  51 */   protected FieldPosition pos = new FieldPosition(0);
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DATE_FORMAT_OPTION = "DateFormat";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String TIMEZONE_OPTION = "TimeZone";
/*     */   private String timeZoneID;
/*     */   private String dateFormatOption;
/*     */   protected DateFormat dateFormat;
/*  71 */   protected Date date = new Date();
/*     */ 
/*     */   /** @deprecated */
/*     */   public String[] getOptionStrings()
/*     */   {
/*  79 */     return new String[] { "DateFormat", "TimeZone" };
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setOption(String option, String value)
/*     */   {
/*  88 */     if (option.equalsIgnoreCase("DateFormat"))
/*  89 */       this.dateFormatOption = value.toUpperCase();
/*  90 */     else if (option.equalsIgnoreCase("TimeZone"))
/*  91 */       this.timeZoneID = value;
/*     */   }
/*     */ 
/*     */   public void setDateFormat(String dateFormat)
/*     */   {
/* 103 */     if (dateFormat != null) {
/* 104 */       this.dateFormatOption = dateFormat;
/*     */     }
/* 106 */     setDateFormat(this.dateFormatOption, TimeZone.getDefault());
/*     */   }
/*     */ 
/*     */   public String getDateFormat()
/*     */   {
/* 114 */     return this.dateFormatOption;
/*     */   }
/*     */ 
/*     */   public void setTimeZone(String timeZone)
/*     */   {
/* 123 */     this.timeZoneID = timeZone;
/*     */   }
/*     */ 
/*     */   public String getTimeZone()
/*     */   {
/* 131 */     return this.timeZoneID;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 136 */     setDateFormat(this.dateFormatOption);
/* 137 */     if ((this.timeZoneID != null) && (this.dateFormat != null))
/* 138 */       this.dateFormat.setTimeZone(TimeZone.getTimeZone(this.timeZoneID));
/*     */   }
/*     */ 
/*     */   public void dateFormat(StringBuffer buf, LoggingEvent event)
/*     */   {
/* 144 */     if (this.dateFormat != null) {
/* 145 */       this.date.setTime(event.timeStamp);
/* 146 */       this.dateFormat.format(this.date, buf, this.pos);
/* 147 */       buf.append(' ');
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setDateFormat(DateFormat dateFormat, TimeZone timeZone)
/*     */   {
/* 157 */     this.dateFormat = dateFormat;
/* 158 */     this.dateFormat.setTimeZone(timeZone);
/*     */   }
/*     */ 
/*     */   public void setDateFormat(String dateFormatType, TimeZone timeZone)
/*     */   {
/* 177 */     if (dateFormatType == null) {
/* 178 */       this.dateFormat = null;
/* 179 */       return;
/*     */     }
/*     */ 
/* 182 */     if (dateFormatType.equalsIgnoreCase("NULL")) {
/* 183 */       this.dateFormat = null;
/* 184 */     } else if (dateFormatType.equalsIgnoreCase("RELATIVE")) {
/* 185 */       this.dateFormat = new RelativeTimeDateFormat();
/* 186 */     } else if (dateFormatType.equalsIgnoreCase("ABSOLUTE"))
/*     */     {
/* 188 */       this.dateFormat = new AbsoluteTimeDateFormat(timeZone);
/* 189 */     } else if (dateFormatType.equalsIgnoreCase("DATE"))
/*     */     {
/* 191 */       this.dateFormat = new DateTimeDateFormat(timeZone);
/* 192 */     } else if (dateFormatType.equalsIgnoreCase("ISO8601"))
/*     */     {
/* 194 */       this.dateFormat = new ISO8601DateFormat(timeZone);
/*     */     } else {
/* 196 */       this.dateFormat = new SimpleDateFormat(dateFormatType);
/* 197 */       this.dateFormat.setTimeZone(timeZone);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.DateLayout
 * JD-Core Version:    0.6.2
 */