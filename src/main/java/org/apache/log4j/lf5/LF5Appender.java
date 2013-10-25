/*     */ package org.apache.log4j.lf5;
/*     */ 
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Toolkit;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class LF5Appender extends AppenderSkeleton
/*     */ {
/*     */   protected LogBrokerMonitor _logMonitor;
/*     */   protected static LogBrokerMonitor _defaultLogMonitor;
/*     */   protected static AppenderFinalizer _finalizer;
/*     */ 
/*     */   public LF5Appender()
/*     */   {
/*  68 */     this(getDefaultInstance());
/*     */   }
/*     */ 
/*     */   public LF5Appender(LogBrokerMonitor monitor)
/*     */   {
/*  82 */     if (monitor != null)
/*  83 */       this._logMonitor = monitor;
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/*  99 */     String category = event.getLoggerName();
/* 100 */     String logMessage = event.getRenderedMessage();
/* 101 */     String nestedDiagnosticContext = event.getNDC();
/* 102 */     String threadDescription = event.getThreadName();
/* 103 */     String level = event.getLevel().toString();
/* 104 */     long time = event.timeStamp;
/* 105 */     LocationInfo locationInfo = event.getLocationInformation();
/*     */ 
/* 108 */     Log4JLogRecord record = new Log4JLogRecord();
/*     */ 
/* 110 */     record.setCategory(category);
/* 111 */     record.setMessage(logMessage);
/* 112 */     record.setLocation(locationInfo.fullInfo);
/* 113 */     record.setMillis(time);
/* 114 */     record.setThreadDescription(threadDescription);
/*     */ 
/* 116 */     if (nestedDiagnosticContext != null)
/* 117 */       record.setNDC(nestedDiagnosticContext);
/*     */     else {
/* 119 */       record.setNDC("");
/*     */     }
/*     */ 
/* 122 */     if (event.getThrowableInformation() != null) {
/* 123 */       record.setThrownStackTrace(event.getThrowableInformation());
/*     */     }
/*     */     try
/*     */     {
/* 127 */       record.setLevel(LogLevel.valueOf(level));
/*     */     }
/*     */     catch (LogLevelFormatException e)
/*     */     {
/* 131 */       record.setLevel(LogLevel.WARN);
/*     */     }
/*     */ 
/* 134 */     if (this._logMonitor != null)
/* 135 */       this._logMonitor.addMessage(record);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 152 */     return false;
/*     */   }
/*     */ 
/*     */   public void setCallSystemExitOnClose(boolean callSystemExitOnClose)
/*     */   {
/* 169 */     this._logMonitor.setCallSystemExitOnClose(callSystemExitOnClose);
/*     */   }
/*     */ 
/*     */   public boolean equals(LF5Appender compareTo)
/*     */   {
/* 183 */     return this._logMonitor == compareTo.getLogBrokerMonitor();
/*     */   }
/*     */ 
/*     */   public LogBrokerMonitor getLogBrokerMonitor() {
/* 187 */     return this._logMonitor;
/*     */   }
/*     */ 
/*     */   public static void main(String[] args) {
/* 191 */     new LF5Appender();
/*     */   }
/*     */ 
/*     */   public void setMaxNumberOfRecords(int maxNumberOfRecords) {
/* 195 */     _defaultLogMonitor.setMaxNumberOfLogRecords(maxNumberOfRecords);
/*     */   }
/*     */ 
/*     */   protected static synchronized LogBrokerMonitor getDefaultInstance()
/*     */   {
/* 205 */     if (_defaultLogMonitor == null) {
/*     */       try {
/* 207 */         _defaultLogMonitor = new LogBrokerMonitor(LogLevel.getLog4JLevels());
/*     */ 
/* 209 */         _finalizer = new AppenderFinalizer(_defaultLogMonitor);
/*     */ 
/* 211 */         _defaultLogMonitor.setFrameSize(getDefaultMonitorWidth(), getDefaultMonitorHeight());
/*     */ 
/* 213 */         _defaultLogMonitor.setFontSize(12);
/* 214 */         _defaultLogMonitor.show();
/*     */       }
/*     */       catch (SecurityException e) {
/* 217 */         _defaultLogMonitor = null;
/*     */       }
/*     */     }
/*     */ 
/* 221 */     return _defaultLogMonitor;
/*     */   }
/*     */ 
/*     */   protected static int getScreenWidth()
/*     */   {
/*     */     try
/*     */     {
/* 231 */       return Toolkit.getDefaultToolkit().getScreenSize().width; } catch (Throwable t) {
/*     */     }
/* 233 */     return 800;
/*     */   }
/*     */ 
/*     */   protected static int getScreenHeight()
/*     */   {
/*     */     try
/*     */     {
/* 244 */       return Toolkit.getDefaultToolkit().getScreenSize().height; } catch (Throwable t) {
/*     */     }
/* 246 */     return 600;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorWidth()
/*     */   {
/* 251 */     return 3 * getScreenWidth() / 4;
/*     */   }
/*     */ 
/*     */   protected static int getDefaultMonitorHeight() {
/* 255 */     return 3 * getScreenHeight() / 4;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.LF5Appender
 * JD-Core Version:    0.6.2
 */