/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.FileWatchdog;
/*     */ 
/*     */ class PropertyWatchdog extends FileWatchdog
/*     */ {
/*     */   PropertyWatchdog(String filename)
/*     */   {
/* 947 */     super(filename);
/*     */   }
/*     */ 
/*     */   public void doOnChange()
/*     */   {
/* 955 */     new PropertyConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.PropertyWatchdog
 * JD-Core Version:    0.6.2
 */