/*     */ package org.apache.log4j.lf5;
/*     */ 
/*     */ import java.awt.Color;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public class LogLevel
/*     */   implements Serializable
/*     */ {
/*  48 */   public static final LogLevel FATAL = new LogLevel("FATAL", 0);
/*  49 */   public static final LogLevel ERROR = new LogLevel("ERROR", 1);
/*  50 */   public static final LogLevel WARN = new LogLevel("WARN", 2);
/*  51 */   public static final LogLevel INFO = new LogLevel("INFO", 3);
/*  52 */   public static final LogLevel DEBUG = new LogLevel("DEBUG", 4);
/*     */ 
/*  55 */   public static final LogLevel SEVERE = new LogLevel("SEVERE", 1);
/*  56 */   public static final LogLevel WARNING = new LogLevel("WARNING", 2);
/*  57 */   public static final LogLevel CONFIG = new LogLevel("CONFIG", 4);
/*  58 */   public static final LogLevel FINE = new LogLevel("FINE", 5);
/*  59 */   public static final LogLevel FINER = new LogLevel("FINER", 6);
/*  60 */   public static final LogLevel FINEST = new LogLevel("FINEST", 7);
/*     */   protected String _label;
/*     */   protected int _precedence;
/*     */   private static LogLevel[] _log4JLevels;
/*     */   private static LogLevel[] _jdk14Levels;
/*     */   private static LogLevel[] _allDefaultLevels;
/*     */   private static Map _logLevelMap;
/*     */   private static Map _logLevelColorMap;
/*  75 */   private static Map _registeredLogLevelMap = new HashMap();
/*     */ 
/*     */   public LogLevel(String label, int precedence)
/*     */   {
/* 100 */     this._label = label;
/* 101 */     this._precedence = precedence;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 112 */     return this._label;
/*     */   }
/*     */ 
/*     */   public boolean encompasses(LogLevel level)
/*     */   {
/* 122 */     if (level.getPrecedence() <= getPrecedence()) {
/* 123 */       return true;
/*     */     }
/*     */ 
/* 126 */     return false;
/*     */   }
/*     */ 
/*     */   public static LogLevel valueOf(String level)
/*     */     throws LogLevelFormatException
/*     */   {
/* 139 */     LogLevel logLevel = null;
/* 140 */     if (level != null) {
/* 141 */       level = level.trim().toUpperCase();
/* 142 */       logLevel = (LogLevel)_logLevelMap.get(level);
/*     */     }
/*     */ 
/* 146 */     if ((logLevel == null) && (_registeredLogLevelMap.size() > 0)) {
/* 147 */       logLevel = (LogLevel)_registeredLogLevelMap.get(level);
/*     */     }
/*     */ 
/* 150 */     if (logLevel == null) {
/* 151 */       StringBuffer buf = new StringBuffer();
/* 152 */       buf.append("Error while trying to parse (" + level + ") into");
/* 153 */       buf.append(" a LogLevel.");
/* 154 */       throw new LogLevelFormatException(buf.toString());
/*     */     }
/* 156 */     return logLevel;
/*     */   }
/*     */ 
/*     */   public static LogLevel register(LogLevel logLevel)
/*     */   {
/* 166 */     if (logLevel == null) return null;
/*     */ 
/* 169 */     if (_logLevelMap.get(logLevel.getLabel()) == null) {
/* 170 */       return (LogLevel)_registeredLogLevelMap.put(logLevel.getLabel(), logLevel);
/*     */     }
/*     */ 
/* 173 */     return null;
/*     */   }
/*     */ 
/*     */   public static void register(LogLevel[] logLevels) {
/* 177 */     if (logLevels != null)
/* 178 */       for (int i = 0; i < logLevels.length; i++)
/* 179 */         register(logLevels[i]);
/*     */   }
/*     */ 
/*     */   public static void register(List logLevels)
/*     */   {
/* 185 */     if (logLevels != null) {
/* 186 */       Iterator it = logLevels.iterator();
/* 187 */       while (it.hasNext())
/* 188 */         register((LogLevel)it.next());
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 194 */     boolean equals = false;
/*     */ 
/* 196 */     if (((o instanceof LogLevel)) && 
/* 197 */       (getPrecedence() == ((LogLevel)o).getPrecedence()))
/*     */     {
/* 199 */       equals = true;
/*     */     }
/*     */ 
/* 204 */     return equals;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 208 */     return this._label.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 212 */     return this._label;
/*     */   }
/*     */ 
/*     */   public void setLogLevelColorMap(LogLevel level, Color color)
/*     */   {
/* 218 */     _logLevelColorMap.remove(level);
/*     */ 
/* 220 */     if (color == null) {
/* 221 */       color = Color.black;
/*     */     }
/* 223 */     _logLevelColorMap.put(level, color);
/*     */   }
/*     */ 
/*     */   public static void resetLogLevelColorMap()
/*     */   {
/* 228 */     _logLevelColorMap.clear();
/*     */ 
/* 231 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/* 232 */       _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
/*     */   }
/*     */ 
/*     */   public static List getLog4JLevels()
/*     */   {
/* 241 */     return Arrays.asList(_log4JLevels);
/*     */   }
/*     */ 
/*     */   public static List getJdk14Levels() {
/* 245 */     return Arrays.asList(_jdk14Levels);
/*     */   }
/*     */ 
/*     */   public static List getAllDefaultLevels() {
/* 249 */     return Arrays.asList(_allDefaultLevels);
/*     */   }
/*     */ 
/*     */   public static Map getLogLevelColorMap() {
/* 253 */     return _logLevelColorMap;
/*     */   }
/*     */ 
/*     */   protected int getPrecedence()
/*     */   {
/* 261 */     return this._precedence;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  81 */     _log4JLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG };
/*  82 */     _jdk14Levels = new LogLevel[] { SEVERE, WARNING, INFO, CONFIG, FINE, FINER, FINEST };
/*     */ 
/*  84 */     _allDefaultLevels = new LogLevel[] { FATAL, ERROR, WARN, INFO, DEBUG, SEVERE, WARNING, CONFIG, FINE, FINER, FINEST };
/*     */ 
/*  87 */     _logLevelMap = new HashMap();
/*  88 */     for (int i = 0; i < _allDefaultLevels.length; i++) {
/*  89 */       _logLevelMap.put(_allDefaultLevels[i].getLabel(), _allDefaultLevels[i]);
/*     */     }
/*     */ 
/*  93 */     _logLevelColorMap = new HashMap();
/*  94 */     for (int i = 0; i < _allDefaultLevels.length; i++)
/*  95 */       _logLevelColorMap.put(_allDefaultLevels[i], Color.black);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.LogLevel
 * JD-Core Version:    0.6.2
 */