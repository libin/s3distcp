/*     */ package org.apache.log4j.spi;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public final class NOPLoggerRepository
/*     */   implements LoggerRepository
/*     */ {
/*     */   public void addHierarchyEventListener(HierarchyEventListener listener)
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean isDisabled(int level)
/*     */   {
/*  43 */     return true;
/*     */   }
/*     */ 
/*     */   public void setThreshold(Level level)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void setThreshold(String val)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void emitNoAppenderWarning(Category cat)
/*     */   {
/*     */   }
/*     */ 
/*     */   public Level getThreshold()
/*     */   {
/*  68 */     return Level.OFF;
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name)
/*     */   {
/*  75 */     return new NOPLogger(this, name);
/*     */   }
/*     */ 
/*     */   public Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/*  82 */     return new NOPLogger(this, name);
/*     */   }
/*     */ 
/*     */   public Logger getRootLogger()
/*     */   {
/*  89 */     return new NOPLogger(this, "root");
/*     */   }
/*     */ 
/*     */   public Logger exists(String name)
/*     */   {
/*  96 */     return null;
/*     */   }
/*     */ 
/*     */   public void shutdown()
/*     */   {
/*     */   }
/*     */ 
/*     */   public Enumeration getCurrentLoggers()
/*     */   {
/* 109 */     return new Vector().elements();
/*     */   }
/*     */ 
/*     */   public Enumeration getCurrentCategories()
/*     */   {
/* 116 */     return getCurrentLoggers();
/*     */   }
/*     */ 
/*     */   public void fireAddAppenderEvent(Category logger, Appender appender)
/*     */   {
/*     */   }
/*     */ 
/*     */   public void resetConfiguration()
/*     */   {
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.spi.NOPLoggerRepository
 * JD-Core Version:    0.6.2
 */