/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import org.apache.log4j.lf5.LogLevel;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ 
/*     */ public class LogMonitorAdapter
/*     */ {
/*     */   public static final int LOG4J_LOG_LEVELS = 0;
/*     */   public static final int JDK14_LOG_LEVELS = 1;
/*     */   private LogBrokerMonitor _logMonitor;
/*  49 */   private LogLevel _defaultLevel = null;
/*     */ 
/*     */   private LogMonitorAdapter(List userDefinedLevels)
/*     */   {
/*  57 */     this._defaultLevel = ((LogLevel)userDefinedLevels.get(0));
/*  58 */     this._logMonitor = new LogBrokerMonitor(userDefinedLevels);
/*     */ 
/*  60 */     this._logMonitor.setFrameSize(getDefaultMonitorWidth(), getDefaultMonitorHeight());
/*     */ 
/*  62 */     this._logMonitor.setFontSize(12);
/*  63 */     this._logMonitor.show();
/*     */   }
/*     */ 
/*     */   public static LogMonitorAdapter newInstance(int loglevels)
/*     */   {
/*     */     LogMonitorAdapter adapter;
/*  78 */     if (loglevels == 1) {
/*  79 */       LogMonitorAdapter adapter = newInstance(LogLevel.getJdk14Levels());
/*  80 */       adapter.setDefaultLevel(LogLevel.FINEST);
/*  81 */       adapter.setSevereLevel(LogLevel.SEVERE);
/*     */     } else {
/*  83 */       adapter = newInstance(LogLevel.getLog4JLevels());
/*  84 */       adapter.setDefaultLevel(LogLevel.DEBUG);
/*  85 */       adapter.setSevereLevel(LogLevel.FATAL);
/*     */     }
/*  87 */     return adapter;
/*     */   }
/*     */ 
/*     */   public static LogMonitorAdapter newInstance(LogLevel[] userDefined)
/*     */   {
/*  99 */     if (userDefined == null) {
/* 100 */       return null;
/*     */     }
/* 102 */     return newInstance(Arrays.asList(userDefined));
/*     */   }
/*     */ 
/*     */   public static LogMonitorAdapter newInstance(List userDefinedLevels)
/*     */   {
/* 114 */     return new LogMonitorAdapter(userDefinedLevels);
/*     */   }
/*     */ 
/*     */   public void addMessage(LogRecord record)
/*     */   {
/* 123 */     this._logMonitor.addMessage(record);
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfRecords(int maxNumberOfRecords)
/*     */   {
/* 132 */     this._logMonitor.setMaxNumberOfLogRecords(maxNumberOfRecords);
/*     */   }
/*     */ 
/*     */   public void setDefaultLevel(LogLevel level)
/*     */   {
/* 142 */     this._defaultLevel = level;
/*     */   }
/*     */ 
/*     */   public LogLevel getDefaultLevel()
/*     */   {
/* 151 */     return this._defaultLevel;
/*     */   }
/*     */ 
/*     */   public void setSevereLevel(LogLevel level)
/*     */   {
/* 160 */     AdapterLogRecord.setSevereLevel(level);
/*     */   }
/*     */ 
/*     */   public LogLevel getSevereLevel()
/*     */   {
/* 169 */     return AdapterLogRecord.getSevereLevel();
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message, Throwable t, String NDC)
/*     */   {
/* 184 */     AdapterLogRecord record = new AdapterLogRecord();
/* 185 */     record.setCategory(category);
/* 186 */     record.setMessage(message);
/* 187 */     record.setNDC(NDC);
/* 188 */     record.setThrown(t);
/*     */ 
/* 190 */     if (level == null)
/* 191 */       record.setLevel(getDefaultLevel());
/*     */     else {
/* 193 */       record.setLevel(level);
/*     */     }
/*     */ 
/* 196 */     addMessage(record);
/*     */   }
/*     */ 
/*     */   public void log(String category, String message)
/*     */   {
/* 206 */     log(category, null, message);
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message, String NDC)
/*     */   {
/* 218 */     log(category, level, message, null, NDC);
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message, Throwable t)
/*     */   {
/* 231 */     log(category, level, message, t, null);
/*     */   }
/*     */ 
/*     */   public void log(String category, LogLevel level, String message)
/*     */   {
/* 242 */     log(category, level, message, null, null);
/*     */   }
/*     */ 
/*     */   protected static int getScreenWidth()
/*     */   {
/*     */     try
/*     */     {
/* 255 */       return Toolkit.getDefaultToolkit().getScreenSize().width; } catch (Throwable t) {
/*     */     }
/* 257 */     return 800;
/*     */   }
/*     */ 
/*     */   protected static int getScreenHeight()
/*     */   {
/*     */     try
/*     */     {
/* 268 */       return Toolkit.getDefaultToolkit().getScreenSize().height; } catch (Throwable t) {
/*     */     }
/* 270 */     return 600;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorWidth()
/*     */   {
/* 275 */     return 3 * getScreenWidth() / 4;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorHeight() {
/* 279 */     return 3 * getScreenHeight() / 4;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.util.LogMonitorAdapter
 * JD-Core Version:    0.6.2
 */