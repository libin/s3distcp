/*     */ package org.apache.log4j;
/*     */ 
/*     */ public class Priority
/*     */ {
/*     */   transient int level;
/*     */   transient String levelStr;
/*     */   transient int syslogEquivalent;
/*     */   public static final int OFF_INT = 2147483647;
/*     */   public static final int FATAL_INT = 50000;
/*     */   public static final int ERROR_INT = 40000;
/*     */   public static final int WARN_INT = 30000;
/*     */   public static final int INFO_INT = 20000;
/*     */   public static final int DEBUG_INT = 10000;
/*     */   public static final int ALL_INT = -2147483648;
/*     */ 
/*     */   /** @deprecated */
/*  45 */   public static final Priority FATAL = new Level(50000, "FATAL", 0);
/*     */ 
/*     */   /** @deprecated */
/*  50 */   public static final Priority ERROR = new Level(40000, "ERROR", 3);
/*     */ 
/*     */   /** @deprecated */
/*  55 */   public static final Priority WARN = new Level(30000, "WARN", 4);
/*     */ 
/*     */   /** @deprecated */
/*  60 */   public static final Priority INFO = new Level(20000, "INFO", 6);
/*     */ 
/*     */   /** @deprecated */
/*  65 */   public static final Priority DEBUG = new Level(10000, "DEBUG", 7);
/*     */ 
/*     */   protected Priority()
/*     */   {
/*  72 */     this.level = 10000;
/*  73 */     this.levelStr = "DEBUG";
/*  74 */     this.syslogEquivalent = 7;
/*     */   }
/*     */ 
/*     */   protected Priority(int level, String levelStr, int syslogEquivalent)
/*     */   {
/*  82 */     this.level = level;
/*  83 */     this.levelStr = levelStr;
/*  84 */     this.syslogEquivalent = syslogEquivalent;
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/*  93 */     if ((o instanceof Priority)) {
/*  94 */       Priority r = (Priority)o;
/*  95 */       return this.level == r.level;
/*     */     }
/*  97 */     return false;
/*     */   }
/*     */ 
/*     */   public final int getSyslogEquivalent()
/*     */   {
/* 107 */     return this.syslogEquivalent;
/*     */   }
/*     */ 
/*     */   public boolean isGreaterOrEqual(Priority r)
/*     */   {
/* 123 */     return this.level >= r.level;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Priority[] getAllPossiblePriorities()
/*     */   {
/* 135 */     return new Priority[] { FATAL, ERROR, Level.WARN, INFO, DEBUG };
/*     */   }
/*     */ 
/*     */   public final String toString()
/*     */   {
/* 146 */     return this.levelStr;
/*     */   }
/*     */ 
/*     */   public final int toInt()
/*     */   {
/* 155 */     return this.level;
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Priority toPriority(String sArg)
/*     */   {
/* 164 */     return Level.toLevel(sArg);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Priority toPriority(int val)
/*     */   {
/* 173 */     return toPriority(val, DEBUG);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Priority toPriority(int val, Priority defaultPriority)
/*     */   {
/* 182 */     return Level.toLevel(val, (Level)defaultPriority);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public static Priority toPriority(String sArg, Priority defaultPriority)
/*     */   {
/* 191 */     return Level.toLevel(sArg, (Level)defaultPriority);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.Priority
 * JD-Core Version:    0.6.2
 */