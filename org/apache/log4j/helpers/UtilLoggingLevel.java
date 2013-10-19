/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.apache.log4j.Level;
/*     */ 
/*     */ public class UtilLoggingLevel extends Level
/*     */ {
/*     */   private static final long serialVersionUID = 909301162611820211L;
/*     */   public static final int SEVERE_INT = 22000;
/*     */   public static final int WARNING_INT = 21000;
/*     */   public static final int CONFIG_INT = 14000;
/*     */   public static final int FINE_INT = 13000;
/*     */   public static final int FINER_INT = 12000;
/*     */   public static final int FINEST_INT = 11000;
/*     */   public static final int UNKNOWN_INT = 10000;
/*  75 */   public static final UtilLoggingLevel SEVERE = new UtilLoggingLevel(22000, "SEVERE", 0);
/*     */ 
/*  80 */   public static final UtilLoggingLevel WARNING = new UtilLoggingLevel(21000, "WARNING", 4);
/*     */ 
/*  86 */   public static final UtilLoggingLevel INFO = new UtilLoggingLevel(20000, "INFO", 5);
/*     */ 
/*  91 */   public static final UtilLoggingLevel CONFIG = new UtilLoggingLevel(14000, "CONFIG", 6);
/*     */ 
/*  96 */   public static final UtilLoggingLevel FINE = new UtilLoggingLevel(13000, "FINE", 7);
/*     */ 
/* 101 */   public static final UtilLoggingLevel FINER = new UtilLoggingLevel(12000, "FINER", 8);
/*     */ 
/* 106 */   public static final UtilLoggingLevel FINEST = new UtilLoggingLevel(11000, "FINEST", 9);
/*     */ 
/*     */   protected UtilLoggingLevel(int level, String levelStr, int syslogEquivalent)
/*     */   {
/* 118 */     super(level, levelStr, syslogEquivalent);
/*     */   }
/*     */ 
/*     */   public static UtilLoggingLevel toLevel(int val, UtilLoggingLevel defaultLevel)
/*     */   {
/* 131 */     switch (val) {
/*     */     case 22000:
/* 133 */       return SEVERE;
/*     */     case 21000:
/* 136 */       return WARNING;
/*     */     case 20000:
/* 139 */       return INFO;
/*     */     case 14000:
/* 142 */       return CONFIG;
/*     */     case 13000:
/* 145 */       return FINE;
/*     */     case 12000:
/* 148 */       return FINER;
/*     */     case 11000:
/* 151 */       return FINEST;
/*     */     }
/*     */ 
/* 154 */     return defaultLevel;
/*     */   }
/*     */ 
/*     */   public static Level toLevel(int val)
/*     */   {
/* 164 */     return toLevel(val, FINEST);
/*     */   }
/*     */ 
/*     */   public static List getAllPossibleLevels()
/*     */   {
/* 172 */     ArrayList list = new ArrayList();
/* 173 */     list.add(FINE);
/* 174 */     list.add(FINER);
/* 175 */     list.add(FINEST);
/* 176 */     list.add(INFO);
/* 177 */     list.add(CONFIG);
/* 178 */     list.add(WARNING);
/* 179 */     list.add(SEVERE);
/* 180 */     return list;
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String s)
/*     */   {
/* 189 */     return toLevel(s, Level.DEBUG);
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String sArg, Level defaultLevel)
/*     */   {
/* 201 */     if (sArg == null) {
/* 202 */       return defaultLevel;
/*     */     }
/*     */ 
/* 205 */     String s = sArg.toUpperCase();
/*     */ 
/* 207 */     if (s.equals("SEVERE")) {
/* 208 */       return SEVERE;
/*     */     }
/*     */ 
/* 212 */     if (s.equals("WARNING")) {
/* 213 */       return WARNING;
/*     */     }
/*     */ 
/* 216 */     if (s.equals("INFO")) {
/* 217 */       return INFO;
/*     */     }
/*     */ 
/* 220 */     if (s.equals("CONFI")) {
/* 221 */       return CONFIG;
/*     */     }
/*     */ 
/* 224 */     if (s.equals("FINE")) {
/* 225 */       return FINE;
/*     */     }
/*     */ 
/* 228 */     if (s.equals("FINER")) {
/* 229 */       return FINER;
/*     */     }
/*     */ 
/* 232 */     if (s.equals("FINEST")) {
/* 233 */       return FINEST;
/*     */     }
/* 235 */     return defaultLevel;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.UtilLoggingLevel
 * JD-Core Version:    0.6.2
 */