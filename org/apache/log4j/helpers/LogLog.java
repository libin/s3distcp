/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class LogLog
/*     */ {
/*     */   public static final String DEBUG_KEY = "log4j.debug";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";
/*  60 */   protected static boolean debugEnabled = false;
/*     */ 
/*  65 */   private static boolean quietMode = false;
/*     */   private static final String PREFIX = "log4j: ";
/*     */   private static final String ERR_PREFIX = "log4j:ERROR ";
/*     */   private static final String WARN_PREFIX = "log4j:WARN ";
/*     */ 
/*     */   public static void setInternalDebugging(boolean enabled)
/*     */   {
/*  89 */     debugEnabled = enabled;
/*     */   }
/*     */ 
/*     */   public static void debug(String msg)
/*     */   {
/*  99 */     if ((debugEnabled) && (!quietMode))
/* 100 */       System.out.println("log4j: " + msg);
/*     */   }
/*     */ 
/*     */   public static void debug(String msg, Throwable t)
/*     */   {
/* 111 */     if ((debugEnabled) && (!quietMode)) {
/* 112 */       System.out.println("log4j: " + msg);
/* 113 */       if (t != null)
/* 114 */         t.printStackTrace(System.out);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void error(String msg)
/*     */   {
/* 127 */     if (quietMode)
/* 128 */       return;
/* 129 */     System.err.println("log4j:ERROR " + msg);
/*     */   }
/*     */ 
/*     */   public static void error(String msg, Throwable t)
/*     */   {
/* 140 */     if (quietMode) {
/* 141 */       return;
/*     */     }
/* 143 */     System.err.println("log4j:ERROR " + msg);
/* 144 */     if (t != null)
/* 145 */       t.printStackTrace();
/*     */   }
/*     */ 
/*     */   public static void setQuietMode(boolean quietMode)
/*     */   {
/* 158 */     quietMode = quietMode;
/*     */   }
/*     */ 
/*     */   public static void warn(String msg)
/*     */   {
/* 168 */     if (quietMode) {
/* 169 */       return;
/*     */     }
/* 171 */     System.err.println("log4j:WARN " + msg);
/*     */   }
/*     */ 
/*     */   public static void warn(String msg, Throwable t)
/*     */   {
/* 181 */     if (quietMode) {
/* 182 */       return;
/*     */     }
/* 184 */     System.err.println("log4j:WARN " + msg);
/* 185 */     if (t != null)
/* 186 */       t.printStackTrace();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  72 */     String key = OptionConverter.getSystemProperty("log4j.debug", null);
/*     */ 
/*  74 */     if (key == null) {
/*  75 */       key = OptionConverter.getSystemProperty("log4j.configDebug", null);
/*     */     }
/*     */ 
/*  78 */     if (key != null)
/*  79 */       debugEnabled = OptionConverter.toBoolean(key, true);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.LogLog
 * JD-Core Version:    0.6.2
 */