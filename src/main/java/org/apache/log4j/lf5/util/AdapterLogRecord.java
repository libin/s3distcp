/*     */ package org.apache.log4j.lf5.util;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import org.apache.log4j.lf5.LogLevel;
/*     */ import org.apache.log4j.lf5.LogRecord;
/*     */ 
/*     */ public class AdapterLogRecord extends LogRecord
/*     */ {
/*  45 */   private static LogLevel severeLevel = null;
/*     */ 
/*  47 */   private static StringWriter sw = new StringWriter();
/*  48 */   private static PrintWriter pw = new PrintWriter(sw);
/*     */ 
/*     */   public void setCategory(String category)
/*     */   {
/*  61 */     super.setCategory(category);
/*  62 */     super.setLocation(getLocationInfo(category));
/*     */   }
/*     */ 
/*     */   public boolean isSevereLevel() {
/*  66 */     if (severeLevel == null) return false;
/*  67 */     return severeLevel.equals(getLevel());
/*     */   }
/*     */ 
/*     */   public static void setSevereLevel(LogLevel level) {
/*  71 */     severeLevel = level;
/*     */   }
/*     */ 
/*     */   public static LogLevel getSevereLevel() {
/*  75 */     return severeLevel;
/*     */   }
/*     */ 
/*     */   protected String getLocationInfo(String category)
/*     */   {
/*  82 */     String stackTrace = stackTraceToString(new Throwable());
/*  83 */     String line = parseLine(stackTrace, category);
/*  84 */     return line;
/*     */   }
/*     */ 
/*     */   protected String stackTraceToString(Throwable t) {
/*  88 */     String s = null;
/*     */ 
/*  90 */     synchronized (sw) {
/*  91 */       t.printStackTrace(pw);
/*  92 */       s = sw.toString();
/*  93 */       sw.getBuffer().setLength(0);
/*     */     }
/*     */ 
/*  96 */     return s;
/*     */   }
/*     */ 
/*     */   protected String parseLine(String trace, String category) {
/* 100 */     int index = trace.indexOf(category);
/* 101 */     if (index == -1) return null;
/* 102 */     trace = trace.substring(index);
/* 103 */     trace = trace.substring(0, trace.indexOf(")") + 1);
/* 104 */     return trace;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.util.AdapterLogRecord
 * JD-Core Version:    0.6.2
 */