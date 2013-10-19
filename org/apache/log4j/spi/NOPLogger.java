/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.ResourceBundle;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ 
/*     */ public final class NOPLogger extends Logger
/*     */ {
/*     */   public NOPLogger(NOPLoggerRepository repo, String name)
/*     */   {
/*  39 */     super(name);
/*  40 */     this.repository = repo;
/*  41 */     this.level = Level.OFF;
/*  42 */     this.parent = this;
/*     */   }
/*     */ 
/*     */   public void addAppender(Appender newAppender)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void assertLog(boolean assertion, String msg)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void callAppenders(LoggingEvent event)
/*     */   {
/*     */   }
/*     */ 
/*     */   void closeNestedAppenders()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void debug(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void error(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void error(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fatal(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void fatal(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Enumeration getAllAppenders()
/*     */   {
/*  91 */     return new Vector().elements();
/*     */   }
/*     */ 
/*     */   public Appender getAppender(String name)
/*     */   {
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */   public Level getEffectiveLevel()
/*     */   {
/* 101 */     return Level.OFF;
/*     */   }
/*     */ 
/*     */   public Priority getChainedPriority()
/*     */   {
/* 106 */     return getEffectiveLevel();
/*     */   }
/*     */ 
/*     */   public ResourceBundle getResourceBundle()
/*     */   {
/* 111 */     return null;
/*     */   }
/*     */ 
/*     */   public void info(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void info(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isAttached(Appender appender)
/*     */   {
/* 125 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isDebugEnabled()
/*     */   {
/* 130 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isEnabledFor(Priority level)
/*     */   {
/* 135 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isInfoEnabled()
/*     */   {
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */   public void l7dlog(Priority priority, String key, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void l7dlog(Priority priority, String key, Object[] params, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void log(Priority priority, Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void log(Priority priority, Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void log(String callerFQCN, Priority level, Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void removeAllAppenders()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void removeAppender(Appender appender)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void removeAppender(String name)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setLevel(Level level)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setPriority(Priority priority)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setResourceBundle(ResourceBundle bundle)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void warn(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void warn(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(Object message)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void trace(Object message, Throwable t)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isTraceEnabled()
/*     */   {
/* 208 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.NOPLogger
 * JD-Core Version:    0.6.2
 */