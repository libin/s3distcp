/*      */ package org.apache.log4j;
/*      */ 
/*      */ import java.text.DateFormat;
/*      */ import java.text.MessageFormat;
/*      */ import java.text.NumberFormat;
/*      */ import java.util.Date;
/*      */ import java.util.Locale;
/*      */ import java.util.ResourceBundle;
/*      */ import org.apache.log4j.spi.LoggingEvent;
/*      */ 
/*      */ public final class LogMF extends LogXF
/*      */ {
/*   56 */   private static NumberFormat numberFormat = null;
/*      */ 
/*   60 */   private static Locale numberLocale = null;
/*      */ 
/*   64 */   private static DateFormat dateFormat = null;
/*      */ 
/*   68 */   private static Locale dateLocale = null;
/*      */ 
/*  270 */   private static final String FQCN = LogMF.class.getName();
/*      */ 
/*      */   private static synchronized String formatNumber(Object n)
/*      */   {
/*   76 */     Locale currentLocale = Locale.getDefault();
/*   77 */     if ((currentLocale != numberLocale) || (numberFormat == null)) {
/*   78 */       numberLocale = currentLocale;
/*   79 */       numberFormat = NumberFormat.getInstance(currentLocale);
/*      */     }
/*   81 */     return numberFormat.format(n);
/*      */   }
/*      */ 
/*      */   private static synchronized String formatDate(Object d)
/*      */   {
/*   91 */     Locale currentLocale = Locale.getDefault();
/*   92 */     if ((currentLocale != dateLocale) || (dateFormat == null)) {
/*   93 */       dateLocale = currentLocale;
/*   94 */       dateFormat = DateFormat.getDateTimeInstance(3, 3, currentLocale);
/*      */     }
/*      */ 
/*   99 */     return dateFormat.format(d);
/*      */   }
/*      */ 
/*      */   private static String formatObject(Object arg0)
/*      */   {
/*  109 */     if ((arg0 instanceof String))
/*  110 */       return arg0.toString();
/*  111 */     if (((arg0 instanceof Double)) || ((arg0 instanceof Float)))
/*      */     {
/*  113 */       return formatNumber(arg0);
/*  114 */     }if ((arg0 instanceof Date)) {
/*  115 */       return formatDate(arg0);
/*      */     }
/*  117 */     return String.valueOf(arg0);
/*      */   }
/*      */ 
/*      */   private static boolean isSimple(String pattern)
/*      */   {
/*  129 */     if (pattern.indexOf('\'') != -1) {
/*  130 */       return false;
/*      */     }
/*  132 */     for (int pos = pattern.indexOf('{'); 
/*  133 */       pos != -1; 
/*  134 */       pos = pattern.indexOf('{', pos + 1)) {
/*  135 */       if ((pos + 2 >= pattern.length()) || (pattern.charAt(pos + 2) != '}') || (pattern.charAt(pos + 1) < '0') || (pattern.charAt(pos + 1) > '9'))
/*      */       {
/*  139 */         return false;
/*      */       }
/*      */     }
/*  142 */     return true;
/*      */   }
/*      */ 
/*      */   private static String format(String pattern, Object[] arguments)
/*      */   {
/*  154 */     if (pattern == null)
/*  155 */       return null;
/*  156 */     if (isSimple(pattern)) {
/*  157 */       String[] formatted = new String[10];
/*  158 */       int prev = 0;
/*  159 */       String retval = "";
/*  160 */       int pos = pattern.indexOf('{');
/*  161 */       while (pos >= 0) {
/*  162 */         if ((pos + 2 < pattern.length()) && (pattern.charAt(pos + 2) == '}') && (pattern.charAt(pos + 1) >= '0') && (pattern.charAt(pos + 1) <= '9'))
/*      */         {
/*  166 */           int index = pattern.charAt(pos + 1) - '0';
/*  167 */           retval = retval + pattern.substring(prev, pos);
/*  168 */           if (formatted[index] == null) {
/*  169 */             if ((arguments == null) || (index >= arguments.length))
/*  170 */               formatted[index] = pattern.substring(pos, pos + 3);
/*      */             else {
/*  172 */               formatted[index] = formatObject(arguments[index]);
/*      */             }
/*      */           }
/*  175 */           retval = retval + formatted[index];
/*  176 */           prev = pos + 3;
/*  177 */           pos = pattern.indexOf('{', prev);
/*      */         } else {
/*  179 */           pos = pattern.indexOf('{', pos + 1);
/*      */         }
/*      */       }
/*  182 */       retval = retval + pattern.substring(prev);
/*  183 */       return retval;
/*      */     }
/*      */     try {
/*  186 */       return MessageFormat.format(pattern, arguments); } catch (IllegalArgumentException ex) {
/*      */     }
/*  188 */     return pattern;
/*      */   }
/*      */ 
/*      */   private static String format(String pattern, Object arg0)
/*      */   {
/*  200 */     if (pattern == null)
/*  201 */       return null;
/*  202 */     if (isSimple(pattern)) {
/*  203 */       String formatted = null;
/*  204 */       int prev = 0;
/*  205 */       String retval = "";
/*  206 */       int pos = pattern.indexOf('{');
/*  207 */       while (pos >= 0) {
/*  208 */         if ((pos + 2 < pattern.length()) && (pattern.charAt(pos + 2) == '}') && (pattern.charAt(pos + 1) >= '0') && (pattern.charAt(pos + 1) <= '9'))
/*      */         {
/*  212 */           int index = pattern.charAt(pos + 1) - '0';
/*  213 */           retval = retval + pattern.substring(prev, pos);
/*  214 */           if (index != 0) {
/*  215 */             retval = retval + pattern.substring(pos, pos + 3);
/*      */           } else {
/*  217 */             if (formatted == null) {
/*  218 */               formatted = formatObject(arg0);
/*      */             }
/*  220 */             retval = retval + formatted;
/*      */           }
/*  222 */           prev = pos + 3;
/*  223 */           pos = pattern.indexOf('{', prev);
/*      */         } else {
/*  225 */           pos = pattern.indexOf('{', pos + 1);
/*      */         }
/*      */       }
/*  228 */       retval = retval + pattern.substring(prev);
/*  229 */       return retval;
/*      */     }
/*      */     try {
/*  232 */       return MessageFormat.format(pattern, new Object[] { arg0 }); } catch (IllegalArgumentException ex) {
/*      */     }
/*  234 */     return pattern;
/*      */   }
/*      */ 
/*      */   private static String format(String resourceBundleName, String key, Object[] arguments)
/*      */   {
/*      */     String pattern;
/*  252 */     if (resourceBundleName != null)
/*      */       try {
/*  254 */         ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName);
/*      */ 
/*  256 */         pattern = bundle.getString(key);
/*      */       } catch (Exception ex) {
/*  258 */         String pattern = key;
/*      */       }
/*      */     else {
/*  261 */       pattern = key;
/*      */     }
/*  263 */     return format(pattern, arguments);
/*      */   }
/*      */ 
/*      */   private static void forcedLog(Logger logger, Level level, String msg)
/*      */   {
/*  282 */     logger.callAppenders(new LoggingEvent(FQCN, logger, level, msg, null));
/*      */   }
/*      */ 
/*      */   private static void forcedLog(Logger logger, Level level, String msg, Throwable t)
/*      */   {
/*  297 */     logger.callAppenders(new LoggingEvent(FQCN, logger, level, msg, t));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  308 */     if (logger.isEnabledFor(TRACE))
/*  309 */       forcedLog(logger, TRACE, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  321 */     if (logger.isDebugEnabled())
/*  322 */       forcedLog(logger, Level.DEBUG, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  334 */     if (logger.isInfoEnabled())
/*  335 */       forcedLog(logger, Level.INFO, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  347 */     if (logger.isEnabledFor(Level.WARN))
/*  348 */       forcedLog(logger, Level.WARN, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void error(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  360 */     if (logger.isEnabledFor(Level.ERROR))
/*  361 */       forcedLog(logger, Level.ERROR, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void fatal(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  373 */     if (logger.isEnabledFor(Level.FATAL))
/*  374 */       forcedLog(logger, Level.FATAL, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  390 */     if (logger.isEnabledFor(TRACE))
/*  391 */       forcedLog(logger, TRACE, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  406 */     if (logger.isDebugEnabled())
/*  407 */       forcedLog(logger, Level.DEBUG, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  422 */     if (logger.isInfoEnabled())
/*  423 */       forcedLog(logger, Level.INFO, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  438 */     if (logger.isEnabledFor(Level.WARN))
/*  439 */       forcedLog(logger, Level.WARN, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void error(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  454 */     if (logger.isEnabledFor(Level.ERROR))
/*  455 */       forcedLog(logger, Level.ERROR, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void fatal(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  470 */     if (logger.isEnabledFor(Level.FATAL))
/*  471 */       forcedLog(logger, Level.FATAL, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, boolean argument)
/*      */   {
/*  485 */     if (logger.isEnabledFor(TRACE))
/*  486 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, char argument)
/*      */   {
/*  498 */     if (logger.isEnabledFor(TRACE))
/*  499 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, byte argument)
/*      */   {
/*  511 */     if (logger.isEnabledFor(TRACE))
/*  512 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, short argument)
/*      */   {
/*  524 */     if (logger.isEnabledFor(TRACE))
/*  525 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, int argument)
/*      */   {
/*  537 */     if (logger.isEnabledFor(TRACE))
/*  538 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, long argument)
/*      */   {
/*  550 */     if (logger.isEnabledFor(TRACE))
/*  551 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, float argument)
/*      */   {
/*  563 */     if (logger.isEnabledFor(TRACE))
/*  564 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, double argument)
/*      */   {
/*  576 */     if (logger.isEnabledFor(TRACE))
/*  577 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object argument)
/*      */   {
/*  589 */     if (logger.isEnabledFor(TRACE))
/*  590 */       forcedLog(logger, TRACE, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/*  603 */     if (logger.isEnabledFor(TRACE))
/*  604 */       forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/*  619 */     if (logger.isEnabledFor(TRACE))
/*  620 */       forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/*  637 */     if (logger.isEnabledFor(TRACE))
/*  638 */       forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, boolean argument)
/*      */   {
/*  651 */     if (logger.isDebugEnabled())
/*  652 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, char argument)
/*      */   {
/*  664 */     if (logger.isDebugEnabled())
/*  665 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, byte argument)
/*      */   {
/*  677 */     if (logger.isDebugEnabled())
/*  678 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, short argument)
/*      */   {
/*  690 */     if (logger.isDebugEnabled())
/*  691 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, int argument)
/*      */   {
/*  703 */     if (logger.isDebugEnabled())
/*  704 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, long argument)
/*      */   {
/*  716 */     if (logger.isDebugEnabled())
/*  717 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, float argument)
/*      */   {
/*  729 */     if (logger.isDebugEnabled())
/*  730 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, double argument)
/*      */   {
/*  742 */     if (logger.isDebugEnabled())
/*  743 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object argument)
/*      */   {
/*  755 */     if (logger.isDebugEnabled())
/*  756 */       forcedLog(logger, Level.DEBUG, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/*  769 */     if (logger.isDebugEnabled())
/*  770 */       forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/*  785 */     if (logger.isDebugEnabled())
/*  786 */       forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/*  803 */     if (logger.isDebugEnabled())
/*  804 */       forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, boolean argument)
/*      */   {
/*  817 */     if (logger.isInfoEnabled())
/*  818 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, char argument)
/*      */   {
/*  830 */     if (logger.isInfoEnabled())
/*  831 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, byte argument)
/*      */   {
/*  843 */     if (logger.isInfoEnabled())
/*  844 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, short argument)
/*      */   {
/*  856 */     if (logger.isInfoEnabled())
/*  857 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, int argument)
/*      */   {
/*  869 */     if (logger.isInfoEnabled())
/*  870 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, long argument)
/*      */   {
/*  882 */     if (logger.isInfoEnabled())
/*  883 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, float argument)
/*      */   {
/*  895 */     if (logger.isInfoEnabled())
/*  896 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, double argument)
/*      */   {
/*  908 */     if (logger.isInfoEnabled())
/*  909 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object argument)
/*      */   {
/*  921 */     if (logger.isInfoEnabled())
/*  922 */       forcedLog(logger, Level.INFO, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/*  935 */     if (logger.isInfoEnabled())
/*  936 */       forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/*  950 */     if (logger.isInfoEnabled())
/*  951 */       forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/*  968 */     if (logger.isInfoEnabled())
/*  969 */       forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, boolean argument)
/*      */   {
/*  982 */     if (logger.isEnabledFor(Level.WARN))
/*  983 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, char argument)
/*      */   {
/*  995 */     if (logger.isEnabledFor(Level.WARN))
/*  996 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, byte argument)
/*      */   {
/* 1008 */     if (logger.isEnabledFor(Level.WARN))
/* 1009 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, short argument)
/*      */   {
/* 1021 */     if (logger.isEnabledFor(Level.WARN))
/* 1022 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, int argument)
/*      */   {
/* 1034 */     if (logger.isEnabledFor(Level.WARN))
/* 1035 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, long argument)
/*      */   {
/* 1047 */     if (logger.isEnabledFor(Level.WARN))
/* 1048 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, float argument)
/*      */   {
/* 1060 */     if (logger.isEnabledFor(Level.WARN))
/* 1061 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, double argument)
/*      */   {
/* 1073 */     if (logger.isEnabledFor(Level.WARN))
/* 1074 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object argument)
/*      */   {
/* 1086 */     if (logger.isEnabledFor(Level.WARN))
/* 1087 */       forcedLog(logger, Level.WARN, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/* 1100 */     if (logger.isEnabledFor(Level.WARN))
/* 1101 */       forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/* 1116 */     if (logger.isEnabledFor(Level.WARN))
/* 1117 */       forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/* 1134 */     if (logger.isEnabledFor(Level.WARN))
/* 1135 */       forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object[] parameters)
/*      */   {
/* 1151 */     if (logger.isEnabledFor(level))
/* 1152 */       forcedLog(logger, level, format(pattern, parameters));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, Throwable t, String pattern, Object[] parameters)
/*      */   {
/* 1170 */     if (logger.isEnabledFor(level))
/* 1171 */       forcedLog(logger, level, format(pattern, parameters), t);
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object param1)
/*      */   {
/* 1187 */     if (logger.isEnabledFor(level))
/* 1188 */       forcedLog(logger, level, format(pattern, toArray(param1)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, boolean param1)
/*      */   {
/* 1204 */     if (logger.isEnabledFor(level))
/* 1205 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, byte param1)
/*      */   {
/* 1222 */     if (logger.isEnabledFor(level))
/* 1223 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, char param1)
/*      */   {
/* 1240 */     if (logger.isEnabledFor(level))
/* 1241 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, short param1)
/*      */   {
/* 1257 */     if (logger.isEnabledFor(level))
/* 1258 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, int param1)
/*      */   {
/* 1274 */     if (logger.isEnabledFor(level))
/* 1275 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, long param1)
/*      */   {
/* 1292 */     if (logger.isEnabledFor(level))
/* 1293 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, float param1)
/*      */   {
/* 1310 */     if (logger.isEnabledFor(level))
/* 1311 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, double param1)
/*      */   {
/* 1328 */     if (logger.isEnabledFor(level))
/* 1329 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1)
/*      */   {
/* 1347 */     if (logger.isEnabledFor(level))
/* 1348 */       forcedLog(logger, level, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/* 1366 */     if (logger.isEnabledFor(level))
/* 1367 */       forcedLog(logger, level, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/* 1387 */     if (logger.isEnabledFor(level))
/* 1388 */       forcedLog(logger, level, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object[] parameters)
/*      */   {
/* 1407 */     if (logger.isEnabledFor(level))
/* 1408 */       forcedLog(logger, level, format(bundleName, key, parameters));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, Throwable t, String bundleName, String key, Object[] parameters)
/*      */   {
/* 1428 */     if (logger.isEnabledFor(level))
/* 1429 */       forcedLog(logger, level, format(bundleName, key, parameters), t);
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param1)
/*      */   {
/* 1447 */     if (logger.isEnabledFor(level))
/* 1448 */       forcedLog(logger, level, format(bundleName, key, toArray(param1)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, boolean param1)
/*      */   {
/* 1466 */     if (logger.isEnabledFor(level))
/* 1467 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, char param1)
/*      */   {
/* 1485 */     if (logger.isEnabledFor(level))
/* 1486 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, byte param1)
/*      */   {
/* 1504 */     if (logger.isEnabledFor(level))
/* 1505 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, short param1)
/*      */   {
/* 1523 */     if (logger.isEnabledFor(level))
/* 1524 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, int param1)
/*      */   {
/* 1542 */     if (logger.isEnabledFor(level))
/* 1543 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, long param1)
/*      */   {
/* 1561 */     if (logger.isEnabledFor(level))
/* 1562 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, float param1)
/*      */   {
/* 1579 */     if (logger.isEnabledFor(level))
/* 1580 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, double param1)
/*      */   {
/* 1599 */     if (logger.isEnabledFor(level))
/* 1600 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1)
/*      */   {
/* 1620 */     if (logger.isEnabledFor(level))
/* 1621 */       forcedLog(logger, level, format(bundleName, key, toArray(param0, param1)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1, Object param2)
/*      */   {
/* 1644 */     if (logger.isEnabledFor(level))
/* 1645 */       forcedLog(logger, level, format(bundleName, key, toArray(param0, param1, param2)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1, Object param2, Object param3)
/*      */   {
/* 1670 */     if (logger.isEnabledFor(level))
/* 1671 */       forcedLog(logger, level, format(bundleName, key, toArray(param0, param1, param2, param3)));
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.LogMF
 * JD-Core Version:    0.6.2
 */