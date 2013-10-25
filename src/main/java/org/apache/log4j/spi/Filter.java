/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ public abstract class Filter
/*     */   implements OptionHandler
/*     */ {
/*     */ 
/*     */   /** @deprecated */
/*     */   public Filter next;
/*     */   public static final int DENY = -1;
/*     */   public static final int NEUTRAL = 0;
/*     */   public static final int ACCEPT = 1;
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public abstract int decide(LoggingEvent paramLoggingEvent);
/*     */ 
/*     */   public void setNext(Filter next)
/*     */   {
/* 114 */     this.next = next;
/*     */   }
/*     */ 
/*     */   public Filter getNext()
/*     */   {
/* 121 */     return this.next;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.Filter
 * JD-Core Version:    0.6.2
 */