/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import java.io.InterruptedIOException;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class FallbackErrorHandler
/*     */   implements ErrorHandler
/*     */ {
/*     */   Appender backup;
/*     */   Appender primary;
/*     */   Vector loggers;
/*     */ 
/*     */   public void setLogger(Logger logger)
/*     */   {
/*  57 */     LogLog.debug("FB: Adding logger [" + logger.getName() + "].");
/*  58 */     if (this.loggers == null) {
/*  59 */       this.loggers = new Vector();
/*     */     }
/*  61 */     this.loggers.addElement(logger);
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void error(String message, Exception e, int errorCode)
/*     */   {
/*  78 */     error(message, e, errorCode, null);
/*     */   }
/*     */ 
/*     */   public void error(String message, Exception e, int errorCode, LoggingEvent event)
/*     */   {
/*  87 */     if ((e instanceof InterruptedIOException)) {
/*  88 */       Thread.currentThread().interrupt();
/*     */     }
/*  90 */     LogLog.debug("FB: The following error reported: " + message, e);
/*  91 */     LogLog.debug("FB: INITIATING FALLBACK PROCEDURE.");
/*  92 */     if (this.loggers != null)
/*  93 */       for (int i = 0; i < this.loggers.size(); i++) {
/*  94 */         Logger l = (Logger)this.loggers.elementAt(i);
/*  95 */         LogLog.debug("FB: Searching for [" + this.primary.getName() + "] in logger [" + l.getName() + "].");
/*     */ 
/*  97 */         LogLog.debug("FB: Replacing [" + this.primary.getName() + "] by [" + this.backup.getName() + "] in logger [" + l.getName() + "].");
/*     */ 
/*  99 */         l.removeAppender(this.primary);
/* 100 */         LogLog.debug("FB: Adding appender [" + this.backup.getName() + "] to logger " + l.getName());
/*     */ 
/* 102 */         l.addAppender(this.backup);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void error(String message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setAppender(Appender primary)
/*     */   {
/* 125 */     LogLog.debug("FB: Setting primary appender to [" + primary.getName() + "].");
/* 126 */     this.primary = primary;
/*     */   }
/*     */ 
/*     */   public void setBackupAppender(Appender backup)
/*     */   {
/* 134 */     LogLog.debug("FB: Setting backup appender to [" + backup.getName() + "].");
/* 135 */     this.backup = backup;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.varia.FallbackErrorHandler
 * JD-Core Version:    0.6.2
 */