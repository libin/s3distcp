/*      */ package org.apache.log4j;
/*      */ 
/*      */ import java.util.ResourceBundle;
/*      */ import org.apache.log4j.spi.LoggingEvent;
/*      */ 
/*      */ public final class LogSF extends LogXF
/*      */ {
/*  135 */   private static final String FQCN = LogSF.class.getName();
/*      */ 
/*      */   private static String format(String pattern, Object[] arguments)
/*      */   {
/*   53 */     if (pattern != null) {
/*   54 */       String retval = "";
/*   55 */       int count = 0;
/*   56 */       int prev = 0;
/*   57 */       int pos = pattern.indexOf("{");
/*   58 */       while (pos >= 0) {
/*   59 */         if ((pos == 0) || (pattern.charAt(pos - 1) != '\\')) {
/*   60 */           retval = retval + pattern.substring(prev, pos);
/*   61 */           if ((pos + 1 < pattern.length()) && (pattern.charAt(pos + 1) == '}')) {
/*   62 */             if ((arguments != null) && (count < arguments.length))
/*   63 */               retval = retval + arguments[(count++)];
/*      */             else {
/*   65 */               retval = retval + "{}";
/*      */             }
/*   67 */             prev = pos + 2;
/*      */           } else {
/*   69 */             retval = retval + "{";
/*   70 */             prev = pos + 1;
/*      */           }
/*      */         } else {
/*   73 */           retval = retval + pattern.substring(prev, pos - 1) + "{";
/*   74 */           prev = pos + 1;
/*      */         }
/*   76 */         pos = pattern.indexOf("{", prev);
/*      */       }
/*   78 */       return retval + pattern.substring(prev);
/*      */     }
/*   80 */     return null;
/*      */   }
/*      */ 
/*      */   private static String format(String pattern, Object arg0)
/*      */   {
/*   90 */     if (pattern != null)
/*      */     {
/*   93 */       if (pattern.indexOf("\\{") >= 0) {
/*   94 */         return format(pattern, new Object[] { arg0 });
/*      */       }
/*   96 */       int pos = pattern.indexOf("{}");
/*   97 */       if (pos >= 0) {
/*   98 */         return pattern.substring(0, pos) + arg0 + pattern.substring(pos + 2);
/*      */       }
/*      */     }
/*  101 */     return pattern;
/*      */   }
/*      */ 
/*      */   private static String format(String resourceBundleName, String key, Object[] arguments)
/*      */   {
/*      */     String pattern;
/*  117 */     if (resourceBundleName != null)
/*      */       try {
/*  119 */         ResourceBundle bundle = ResourceBundle.getBundle(resourceBundleName);
/*      */ 
/*  121 */         pattern = bundle.getString(key);
/*      */       } catch (Exception ex) {
/*  123 */         String pattern = key;
/*      */       }
/*      */     else {
/*  126 */       pattern = key;
/*      */     }
/*  128 */     return format(pattern, arguments);
/*      */   }
/*      */ 
/*      */   private static void forcedLog(Logger logger, Level level, String msg)
/*      */   {
/*  147 */     logger.callAppenders(new LoggingEvent(FQCN, logger, level, msg, null));
/*      */   }
/*      */ 
/*      */   private static void forcedLog(Logger logger, Level level, String msg, Throwable t)
/*      */   {
/*  162 */     logger.callAppenders(new LoggingEvent(FQCN, logger, level, msg, t));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  173 */     if (logger.isEnabledFor(TRACE))
/*  174 */       forcedLog(logger, TRACE, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  186 */     if (logger.isDebugEnabled())
/*  187 */       forcedLog(logger, Level.DEBUG, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  199 */     if (logger.isInfoEnabled())
/*  200 */       forcedLog(logger, Level.INFO, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  212 */     if (logger.isEnabledFor(Level.WARN))
/*  213 */       forcedLog(logger, Level.WARN, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void error(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  225 */     if (logger.isEnabledFor(Level.ERROR))
/*  226 */       forcedLog(logger, Level.ERROR, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void fatal(Logger logger, String pattern, Object[] arguments)
/*      */   {
/*  238 */     if (logger.isEnabledFor(Level.FATAL))
/*  239 */       forcedLog(logger, Level.FATAL, format(pattern, arguments));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  255 */     if (logger.isEnabledFor(TRACE))
/*  256 */       forcedLog(logger, TRACE, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  271 */     if (logger.isDebugEnabled())
/*  272 */       forcedLog(logger, Level.DEBUG, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  287 */     if (logger.isInfoEnabled())
/*  288 */       forcedLog(logger, Level.INFO, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  303 */     if (logger.isEnabledFor(Level.WARN))
/*  304 */       forcedLog(logger, Level.WARN, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void error(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  319 */     if (logger.isEnabledFor(Level.ERROR))
/*  320 */       forcedLog(logger, Level.ERROR, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void fatal(Logger logger, Throwable t, String pattern, Object[] arguments)
/*      */   {
/*  335 */     if (logger.isEnabledFor(Level.FATAL))
/*  336 */       forcedLog(logger, Level.FATAL, format(pattern, arguments), t);
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, boolean argument)
/*      */   {
/*  350 */     if (logger.isEnabledFor(TRACE))
/*  351 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, char argument)
/*      */   {
/*  363 */     if (logger.isEnabledFor(TRACE))
/*  364 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, byte argument)
/*      */   {
/*  376 */     if (logger.isEnabledFor(TRACE))
/*  377 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, short argument)
/*      */   {
/*  389 */     if (logger.isEnabledFor(TRACE))
/*  390 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, int argument)
/*      */   {
/*  402 */     if (logger.isEnabledFor(TRACE))
/*  403 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, long argument)
/*      */   {
/*  415 */     if (logger.isEnabledFor(TRACE))
/*  416 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, float argument)
/*      */   {
/*  428 */     if (logger.isEnabledFor(TRACE))
/*  429 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, double argument)
/*      */   {
/*  441 */     if (logger.isEnabledFor(TRACE))
/*  442 */       forcedLog(logger, TRACE, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object argument)
/*      */   {
/*  454 */     if (logger.isEnabledFor(TRACE))
/*  455 */       forcedLog(logger, TRACE, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/*  468 */     if (logger.isEnabledFor(TRACE))
/*  469 */       forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/*  484 */     if (logger.isEnabledFor(TRACE))
/*  485 */       forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void trace(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/*  502 */     if (logger.isEnabledFor(TRACE))
/*  503 */       forcedLog(logger, TRACE, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, boolean argument)
/*      */   {
/*  516 */     if (logger.isDebugEnabled())
/*  517 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, char argument)
/*      */   {
/*  529 */     if (logger.isDebugEnabled())
/*  530 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, byte argument)
/*      */   {
/*  542 */     if (logger.isDebugEnabled())
/*  543 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, short argument)
/*      */   {
/*  555 */     if (logger.isDebugEnabled())
/*  556 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, int argument)
/*      */   {
/*  568 */     if (logger.isDebugEnabled())
/*  569 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, long argument)
/*      */   {
/*  581 */     if (logger.isDebugEnabled())
/*  582 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, float argument)
/*      */   {
/*  594 */     if (logger.isDebugEnabled())
/*  595 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, double argument)
/*      */   {
/*  607 */     if (logger.isDebugEnabled())
/*  608 */       forcedLog(logger, Level.DEBUG, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object argument)
/*      */   {
/*  620 */     if (logger.isDebugEnabled())
/*  621 */       forcedLog(logger, Level.DEBUG, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/*  634 */     if (logger.isDebugEnabled())
/*  635 */       forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/*  650 */     if (logger.isDebugEnabled())
/*  651 */       forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void debug(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/*  668 */     if (logger.isDebugEnabled())
/*  669 */       forcedLog(logger, Level.DEBUG, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, boolean argument)
/*      */   {
/*  682 */     if (logger.isInfoEnabled())
/*  683 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, char argument)
/*      */   {
/*  695 */     if (logger.isInfoEnabled())
/*  696 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, byte argument)
/*      */   {
/*  708 */     if (logger.isInfoEnabled())
/*  709 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, short argument)
/*      */   {
/*  721 */     if (logger.isInfoEnabled())
/*  722 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, int argument)
/*      */   {
/*  734 */     if (logger.isInfoEnabled())
/*  735 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, long argument)
/*      */   {
/*  747 */     if (logger.isInfoEnabled())
/*  748 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, float argument)
/*      */   {
/*  760 */     if (logger.isInfoEnabled())
/*  761 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, double argument)
/*      */   {
/*  773 */     if (logger.isInfoEnabled())
/*  774 */       forcedLog(logger, Level.INFO, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object argument)
/*      */   {
/*  786 */     if (logger.isInfoEnabled())
/*  787 */       forcedLog(logger, Level.INFO, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/*  800 */     if (logger.isInfoEnabled())
/*  801 */       forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/*  815 */     if (logger.isInfoEnabled())
/*  816 */       forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void info(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/*  833 */     if (logger.isInfoEnabled())
/*  834 */       forcedLog(logger, Level.INFO, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, boolean argument)
/*      */   {
/*  847 */     if (logger.isEnabledFor(Level.WARN))
/*  848 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, char argument)
/*      */   {
/*  860 */     if (logger.isEnabledFor(Level.WARN))
/*  861 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, byte argument)
/*      */   {
/*  873 */     if (logger.isEnabledFor(Level.WARN))
/*  874 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, short argument)
/*      */   {
/*  886 */     if (logger.isEnabledFor(Level.WARN))
/*  887 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, int argument)
/*      */   {
/*  899 */     if (logger.isEnabledFor(Level.WARN))
/*  900 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, long argument)
/*      */   {
/*  912 */     if (logger.isEnabledFor(Level.WARN))
/*  913 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, float argument)
/*      */   {
/*  925 */     if (logger.isEnabledFor(Level.WARN))
/*  926 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, double argument)
/*      */   {
/*  938 */     if (logger.isEnabledFor(Level.WARN))
/*  939 */       forcedLog(logger, Level.WARN, format(pattern, valueOf(argument)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object argument)
/*      */   {
/*  951 */     if (logger.isEnabledFor(Level.WARN))
/*  952 */       forcedLog(logger, Level.WARN, format(pattern, argument));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object arg0, Object arg1)
/*      */   {
/*  965 */     if (logger.isEnabledFor(Level.WARN))
/*  966 */       forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/*  981 */     if (logger.isEnabledFor(Level.WARN))
/*  982 */       forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void warn(Logger logger, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/*  999 */     if (logger.isEnabledFor(Level.WARN))
/* 1000 */       forcedLog(logger, Level.WARN, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object[] parameters)
/*      */   {
/* 1016 */     if (logger.isEnabledFor(level))
/* 1017 */       forcedLog(logger, level, format(pattern, parameters));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, Throwable t, String pattern, Object[] parameters)
/*      */   {
/* 1035 */     if (logger.isEnabledFor(level))
/* 1036 */       forcedLog(logger, level, format(pattern, parameters), t);
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object param1)
/*      */   {
/* 1052 */     if (logger.isEnabledFor(level))
/* 1053 */       forcedLog(logger, level, format(pattern, toArray(param1)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, boolean param1)
/*      */   {
/* 1069 */     if (logger.isEnabledFor(level))
/* 1070 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, byte param1)
/*      */   {
/* 1087 */     if (logger.isEnabledFor(level))
/* 1088 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, char param1)
/*      */   {
/* 1105 */     if (logger.isEnabledFor(level))
/* 1106 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, short param1)
/*      */   {
/* 1122 */     if (logger.isEnabledFor(level))
/* 1123 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, int param1)
/*      */   {
/* 1139 */     if (logger.isEnabledFor(level))
/* 1140 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, long param1)
/*      */   {
/* 1157 */     if (logger.isEnabledFor(level))
/* 1158 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, float param1)
/*      */   {
/* 1175 */     if (logger.isEnabledFor(level))
/* 1176 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, double param1)
/*      */   {
/* 1193 */     if (logger.isEnabledFor(level))
/* 1194 */       forcedLog(logger, level, format(pattern, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1)
/*      */   {
/* 1212 */     if (logger.isEnabledFor(level))
/* 1213 */       forcedLog(logger, level, format(pattern, toArray(arg0, arg1)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1, Object arg2)
/*      */   {
/* 1231 */     if (logger.isEnabledFor(level))
/* 1232 */       forcedLog(logger, level, format(pattern, toArray(arg0, arg1, arg2)));
/*      */   }
/*      */ 
/*      */   public static void log(Logger logger, Level level, String pattern, Object arg0, Object arg1, Object arg2, Object arg3)
/*      */   {
/* 1252 */     if (logger.isEnabledFor(level))
/* 1253 */       forcedLog(logger, level, format(pattern, toArray(arg0, arg1, arg2, arg3)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object[] parameters)
/*      */   {
/* 1272 */     if (logger.isEnabledFor(level))
/* 1273 */       forcedLog(logger, level, format(bundleName, key, parameters));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, Throwable t, String bundleName, String key, Object[] parameters)
/*      */   {
/* 1293 */     if (logger.isEnabledFor(level))
/* 1294 */       forcedLog(logger, level, format(bundleName, key, parameters), t);
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param1)
/*      */   {
/* 1312 */     if (logger.isEnabledFor(level))
/* 1313 */       forcedLog(logger, level, format(bundleName, key, toArray(param1)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, boolean param1)
/*      */   {
/* 1331 */     if (logger.isEnabledFor(level))
/* 1332 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, char param1)
/*      */   {
/* 1350 */     if (logger.isEnabledFor(level))
/* 1351 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, byte param1)
/*      */   {
/* 1369 */     if (logger.isEnabledFor(level))
/* 1370 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, short param1)
/*      */   {
/* 1388 */     if (logger.isEnabledFor(level))
/* 1389 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, int param1)
/*      */   {
/* 1407 */     if (logger.isEnabledFor(level))
/* 1408 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, long param1)
/*      */   {
/* 1426 */     if (logger.isEnabledFor(level))
/* 1427 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, float param1)
/*      */   {
/* 1444 */     if (logger.isEnabledFor(level))
/* 1445 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, double param1)
/*      */   {
/* 1464 */     if (logger.isEnabledFor(level))
/* 1465 */       forcedLog(logger, level, format(bundleName, key, toArray(valueOf(param1))));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1)
/*      */   {
/* 1485 */     if (logger.isEnabledFor(level))
/* 1486 */       forcedLog(logger, level, format(bundleName, key, toArray(param0, param1)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1, Object param2)
/*      */   {
/* 1509 */     if (logger.isEnabledFor(level))
/* 1510 */       forcedLog(logger, level, format(bundleName, key, toArray(param0, param1, param2)));
/*      */   }
/*      */ 
/*      */   public static void logrb(Logger logger, Level level, String bundleName, String key, Object param0, Object param1, Object param2, Object param3)
/*      */   {
/* 1535 */     if (logger.isEnabledFor(level))
/* 1536 */       forcedLog(logger, level, format(bundleName, key, toArray(param0, param1, param2, param3)));
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.LogSF
 * JD-Core Version:    0.6.2
 */