/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class DailyRollingFileAppender extends FileAppender
/*     */ {
/*     */   static final int TOP_OF_TROUBLE = -1;
/*     */   static final int TOP_OF_MINUTE = 0;
/*     */   static final int TOP_OF_HOUR = 1;
/*     */   static final int HALF_DAY = 2;
/*     */   static final int TOP_OF_DAY = 3;
/*     */   static final int TOP_OF_WEEK = 4;
/*     */   static final int TOP_OF_MONTH = 5;
/* 160 */   private String datePattern = "'.'yyyy-MM-dd";
/*     */   private String scheduledFilename;
/* 176 */   private long nextCheck = System.currentTimeMillis() - 1L;
/*     */ 
/* 178 */   Date now = new Date();
/*     */   SimpleDateFormat sdf;
/* 182 */   RollingCalendar rc = new RollingCalendar();
/*     */ 
/* 184 */   int checkPeriod = -1;
/*     */ 
/* 187 */   static final TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
/*     */ 
/*     */   public DailyRollingFileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public DailyRollingFileAppender(Layout layout, String filename, String datePattern)
/*     */     throws IOException
/*     */   {
/* 203 */     super(layout, filename, true);
/* 204 */     this.datePattern = datePattern;
/* 205 */     activateOptions();
/*     */   }
/*     */ 
/*     */   public void setDatePattern(String pattern)
/*     */   {
/* 214 */     this.datePattern = pattern;
/*     */   }
/*     */ 
/*     */   public String getDatePattern()
/*     */   {
/* 219 */     return this.datePattern;
/*     */   }
/*     */ 
/*     */   public void activateOptions() {
/* 223 */     super.activateOptions();
/* 224 */     if ((this.datePattern != null) && (this.fileName != null)) {
/* 225 */       this.now.setTime(System.currentTimeMillis());
/* 226 */       this.sdf = new SimpleDateFormat(this.datePattern);
/* 227 */       int type = computeCheckPeriod();
/* 228 */       printPeriodicity(type);
/* 229 */       this.rc.setType(type);
/* 230 */       File file = new File(this.fileName);
/* 231 */       this.scheduledFilename = (this.fileName + this.sdf.format(new Date(file.lastModified())));
/*     */     }
/*     */     else {
/* 234 */       LogLog.error("Either File or DatePattern options are not set for appender [" + this.name + "].");
/*     */     }
/*     */   }
/*     */ 
/*     */   void printPeriodicity(int type)
/*     */   {
/* 240 */     switch (type) {
/*     */     case 0:
/* 242 */       LogLog.debug("Appender [" + this.name + "] to be rolled every minute.");
/* 243 */       break;
/*     */     case 1:
/* 245 */       LogLog.debug("Appender [" + this.name + "] to be rolled on top of every hour.");
/*     */ 
/* 247 */       break;
/*     */     case 2:
/* 249 */       LogLog.debug("Appender [" + this.name + "] to be rolled at midday and midnight.");
/*     */ 
/* 251 */       break;
/*     */     case 3:
/* 253 */       LogLog.debug("Appender [" + this.name + "] to be rolled at midnight.");
/*     */ 
/* 255 */       break;
/*     */     case 4:
/* 257 */       LogLog.debug("Appender [" + this.name + "] to be rolled at start of week.");
/*     */ 
/* 259 */       break;
/*     */     case 5:
/* 261 */       LogLog.debug("Appender [" + this.name + "] to be rolled at start of every month.");
/*     */ 
/* 263 */       break;
/*     */     default:
/* 265 */       LogLog.warn("Unknown periodicity for appender [" + this.name + "].");
/*     */     }
/*     */   }
/*     */ 
/*     */   int computeCheckPeriod()
/*     */   {
/* 280 */     RollingCalendar rollingCalendar = new RollingCalendar(gmtTimeZone, Locale.getDefault());
/*     */ 
/* 282 */     Date epoch = new Date(0L);
/* 283 */     if (this.datePattern != null) {
/* 284 */       for (int i = 0; i <= 5; i++) {
/* 285 */         SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
/* 286 */         simpleDateFormat.setTimeZone(gmtTimeZone);
/* 287 */         String r0 = simpleDateFormat.format(epoch);
/* 288 */         rollingCalendar.setType(i);
/* 289 */         Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
/* 290 */         String r1 = simpleDateFormat.format(next);
/*     */ 
/* 292 */         if ((r0 != null) && (r1 != null) && (!r0.equals(r1))) {
/* 293 */           return i;
/*     */         }
/*     */       }
/*     */     }
/* 297 */     return -1;
/*     */   }
/*     */ 
/*     */   void rollOver()
/*     */     throws IOException
/*     */   {
/* 306 */     if (this.datePattern == null) {
/* 307 */       this.errorHandler.error("Missing DatePattern option in rollOver().");
/* 308 */       return;
/*     */     }
/*     */ 
/* 311 */     String datedFilename = this.fileName + this.sdf.format(this.now);
/*     */ 
/* 315 */     if (this.scheduledFilename.equals(datedFilename)) {
/* 316 */       return;
/*     */     }
/*     */ 
/* 320 */     closeFile();
/*     */ 
/* 322 */     File target = new File(this.scheduledFilename);
/* 323 */     if (target.exists()) {
/* 324 */       target.delete();
/*     */     }
/*     */ 
/* 327 */     File file = new File(this.fileName);
/* 328 */     boolean result = file.renameTo(target);
/* 329 */     if (result)
/* 330 */       LogLog.debug(this.fileName + " -> " + this.scheduledFilename);
/*     */     else {
/* 332 */       LogLog.error("Failed to rename [" + this.fileName + "] to [" + this.scheduledFilename + "].");
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 338 */       setFile(this.fileName, true, this.bufferedIO, this.bufferSize);
/*     */     }
/*     */     catch (IOException e) {
/* 341 */       this.errorHandler.error("setFile(" + this.fileName + ", true) call failed.");
/*     */     }
/* 343 */     this.scheduledFilename = datedFilename;
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 355 */     long n = System.currentTimeMillis();
/* 356 */     if (n >= this.nextCheck) {
/* 357 */       this.now.setTime(n);
/* 358 */       this.nextCheck = this.rc.getNextCheckMillis(this.now);
/*     */       try {
/* 360 */         rollOver();
/*     */       }
/*     */       catch (IOException ioe) {
/* 363 */         if ((ioe instanceof InterruptedIOException)) {
/* 364 */           Thread.currentThread().interrupt();
/*     */         }
/* 366 */         LogLog.error("rollOver() failed.", ioe);
/*     */       }
/*     */     }
/* 369 */     super.subAppend(event);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.DailyRollingFileAppender
 * JD-Core Version:    0.6.2
 */