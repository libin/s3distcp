/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class RelativeTimePatternConverter extends LoggingEventPatternConverter
/*     */ {
/*  33 */   private CachedTimestamp lastTimestamp = new CachedTimestamp(0L, "");
/*     */ 
/*     */   public RelativeTimePatternConverter()
/*     */   {
/*  39 */     super("Time", "time");
/*     */   }
/*     */ 
/*     */   public static RelativeTimePatternConverter newInstance(String[] options)
/*     */   {
/*  49 */     return new RelativeTimePatternConverter();
/*     */   }
/*     */ 
/*     */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*     */   {
/*  56 */     long timestamp = event.timeStamp;
/*     */ 
/*  58 */     if (!this.lastTimestamp.format(timestamp, toAppendTo)) {
/*  59 */       String formatted = Long.toString(timestamp - LoggingEvent.getStartTime());
/*     */ 
/*  61 */       toAppendTo.append(formatted);
/*  62 */       this.lastTimestamp = new CachedTimestamp(timestamp, formatted);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class CachedTimestamp
/*     */   {
/*     */     private final long timestamp;
/*     */     private final String formatted;
/*     */ 
/*     */     public CachedTimestamp(long timestamp, String formatted)
/*     */     {
/*  86 */       this.timestamp = timestamp;
/*  87 */       this.formatted = formatted;
/*     */     }
/*     */ 
/*     */     public boolean format(long newTimestamp, StringBuffer toAppendTo)
/*     */     {
/*  97 */       if (newTimestamp == this.timestamp) {
/*  98 */         toAppendTo.append(this.formatted);
/*     */ 
/* 100 */         return true;
/*     */       }
/*     */ 
/* 103 */       return false;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.RelativeTimePatternConverter
 * JD-Core Version:    0.6.2
 */