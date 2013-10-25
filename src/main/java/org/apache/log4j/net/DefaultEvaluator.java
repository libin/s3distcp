/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ import org.apache.log4j.spi.TriggeringEventEvaluator;
/*     */ 
/*     */ class DefaultEvaluator
/*     */   implements TriggeringEventEvaluator
/*     */ {
/*     */   public boolean isTriggeringEvent(LoggingEvent event)
/*     */   {
/* 785 */     return event.getLevel().isGreaterOrEqual(Level.ERROR);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.DefaultEvaluator
 * JD-Core Version:    0.6.2
 */