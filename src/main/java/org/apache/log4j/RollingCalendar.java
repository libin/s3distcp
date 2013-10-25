/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ class RollingCalendar extends GregorianCalendar
/*     */ {
/*     */   private static final long serialVersionUID = -3560331770601814177L;
/* 381 */   int type = -1;
/*     */ 
/*     */   RollingCalendar()
/*     */   {
/*     */   }
/*     */ 
/*     */   RollingCalendar(TimeZone tz, Locale locale) {
/* 388 */     super(tz, locale);
/*     */   }
/*     */ 
/*     */   void setType(int type) {
/* 392 */     this.type = type;
/*     */   }
/*     */ 
/*     */   public long getNextCheckMillis(Date now) {
/* 396 */     return getNextCheckDate(now).getTime();
/*     */   }
/*     */ 
/*     */   public Date getNextCheckDate(Date now) {
/* 400 */     setTime(now);
/*     */ 
/* 402 */     switch (this.type) {
/*     */     case 0:
/* 404 */       set(13, 0);
/* 405 */       set(14, 0);
/* 406 */       add(12, 1);
/* 407 */       break;
/*     */     case 1:
/* 409 */       set(12, 0);
/* 410 */       set(13, 0);
/* 411 */       set(14, 0);
/* 412 */       add(11, 1);
/* 413 */       break;
/*     */     case 2:
/* 415 */       set(12, 0);
/* 416 */       set(13, 0);
/* 417 */       set(14, 0);
/* 418 */       int hour = get(11);
/* 419 */       if (hour < 12) {
/* 420 */         set(11, 12);
/*     */       } else {
/* 422 */         set(11, 0);
/* 423 */         add(5, 1);
/*     */       }
/* 425 */       break;
/*     */     case 3:
/* 427 */       set(11, 0);
/* 428 */       set(12, 0);
/* 429 */       set(13, 0);
/* 430 */       set(14, 0);
/* 431 */       add(5, 1);
/* 432 */       break;
/*     */     case 4:
/* 434 */       set(7, getFirstDayOfWeek());
/* 435 */       set(11, 0);
/* 436 */       set(12, 0);
/* 437 */       set(13, 0);
/* 438 */       set(14, 0);
/* 439 */       add(3, 1);
/* 440 */       break;
/*     */     case 5:
/* 442 */       set(5, 1);
/* 443 */       set(11, 0);
/* 444 */       set(12, 0);
/* 445 */       set(13, 0);
/* 446 */       set(14, 0);
/* 447 */       add(2, 1);
/* 448 */       break;
/*     */     default:
/* 450 */       throw new IllegalStateException("Unknown periodicity type.");
/*     */     }
/* 452 */     return getTime();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.RollingCalendar
 * JD-Core Version:    0.6.2
 */