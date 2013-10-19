/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ import org.apache.log4j.spi.Configurator;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ 
/*     */ public class OptionConverter
/*     */ {
/*  43 */   static String DELIM_START = "${";
/*  44 */   static char DELIM_STOP = '}';
/*  45 */   static int DELIM_START_LEN = 2;
/*  46 */   static int DELIM_STOP_LEN = 1;
/*     */ 
/*     */   public static String[] concatanateArrays(String[] l, String[] r)
/*     */   {
/*  54 */     int len = l.length + r.length;
/*  55 */     String[] a = new String[len];
/*     */ 
/*  57 */     System.arraycopy(l, 0, a, 0, l.length);
/*  58 */     System.arraycopy(r, 0, a, l.length, r.length);
/*     */ 
/*  60 */     return a;
/*     */   }
/*     */ 
/*     */   public static String convertSpecialChars(String s)
/*     */   {
/*  67 */     int len = s.length();
/*  68 */     StringBuffer sbuf = new StringBuffer(len);
/*     */ 
/*  70 */     int i = 0;
/*  71 */     while (i < len) {
/*  72 */       char c = s.charAt(i++);
/*  73 */       if (c == '\\') {
/*  74 */         c = s.charAt(i++);
/*  75 */         if (c == 'n') c = '\n';
/*  76 */         else if (c == 'r') c = '\r';
/*  77 */         else if (c == 't') c = '\t';
/*  78 */         else if (c == 'f') c = '\f';
/*  79 */         else if (c == '\b') c = '\b';
/*  80 */         else if (c == '"') c = '"';
/*  81 */         else if (c == '\'') c = '\'';
/*  82 */         else if (c == '\\') c = '\\';
/*     */       }
/*  84 */       sbuf.append(c);
/*     */     }
/*  86 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public static String getSystemProperty(String key, String def)
/*     */   {
/*     */     try
/*     */     {
/* 104 */       return System.getProperty(key, def);
/*     */     } catch (Throwable e) {
/* 106 */       LogLog.debug("Was not allowed to read system property \"" + key + "\".");
/* 107 */     }return def;
/*     */   }
/*     */ 
/*     */   public static Object instantiateByKey(Properties props, String key, Class superClass, Object defaultValue)
/*     */   {
/* 118 */     String className = findAndSubst(key, props);
/* 119 */     if (className == null) {
/* 120 */       LogLog.error("Could not find value for key " + key);
/* 121 */       return defaultValue;
/*     */     }
/*     */ 
/* 124 */     return instantiateByClassName(className.trim(), superClass, defaultValue);
/*     */   }
/*     */ 
/*     */   public static boolean toBoolean(String value, boolean dEfault)
/*     */   {
/* 138 */     if (value == null)
/* 139 */       return dEfault;
/* 140 */     String trimmedVal = value.trim();
/* 141 */     if ("true".equalsIgnoreCase(trimmedVal))
/* 142 */       return true;
/* 143 */     if ("false".equalsIgnoreCase(trimmedVal))
/* 144 */       return false;
/* 145 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static int toInt(String value, int dEfault)
/*     */   {
/* 151 */     if (value != null) {
/* 152 */       String s = value.trim();
/*     */       try {
/* 154 */         return Integer.valueOf(s).intValue();
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 157 */         LogLog.error("[" + s + "] is not in proper int form.");
/* 158 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 161 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static Level toLevel(String value, Level defaultValue)
/*     */   {
/* 187 */     if (value == null) {
/* 188 */       return defaultValue;
/*     */     }
/* 190 */     value = value.trim();
/*     */ 
/* 192 */     int hashIndex = value.indexOf('#');
/* 193 */     if (hashIndex == -1) {
/* 194 */       if ("NULL".equalsIgnoreCase(value)) {
/* 195 */         return null;
/*     */       }
/*     */ 
/* 198 */       return Level.toLevel(value, defaultValue);
/*     */     }
/*     */ 
/* 202 */     Level result = defaultValue;
/*     */ 
/* 204 */     String clazz = value.substring(hashIndex + 1);
/* 205 */     String levelName = value.substring(0, hashIndex);
/*     */ 
/* 208 */     if ("NULL".equalsIgnoreCase(levelName)) {
/* 209 */       return null;
/*     */     }
/*     */ 
/* 212 */     LogLog.debug("toLevel:class=[" + clazz + "]" + ":pri=[" + levelName + "]");
/*     */     try
/*     */     {
/* 216 */       Class customLevel = Loader.loadClass(clazz);
/*     */ 
/* 220 */       Class[] paramTypes = { String.class, Level.class };
/*     */ 
/* 223 */       Method toLevelMethod = customLevel.getMethod("toLevel", paramTypes);
/*     */ 
/* 227 */       Object[] params = { levelName, defaultValue };
/* 228 */       Object o = toLevelMethod.invoke(null, params);
/*     */ 
/* 230 */       result = (Level)o;
/*     */     } catch (ClassNotFoundException e) {
/* 232 */       LogLog.warn("custom level class [" + clazz + "] not found.");
/*     */     } catch (NoSuchMethodException e) {
/* 234 */       LogLog.warn("custom level class [" + clazz + "]" + " does not have a class function toLevel(String, Level)", e);
/*     */     }
/*     */     catch (InvocationTargetException e) {
/* 237 */       if (((e.getTargetException() instanceof InterruptedException)) || ((e.getTargetException() instanceof InterruptedIOException)))
/*     */       {
/* 239 */         Thread.currentThread().interrupt();
/*     */       }
/* 241 */       LogLog.warn("custom level class [" + clazz + "]" + " could not be instantiated", e);
/*     */     }
/*     */     catch (ClassCastException e) {
/* 244 */       LogLog.warn("class [" + clazz + "] is not a subclass of org.apache.log4j.Level", e);
/*     */     }
/*     */     catch (IllegalAccessException e) {
/* 247 */       LogLog.warn("class [" + clazz + "] cannot be instantiated due to access restrictions", e);
/*     */     }
/*     */     catch (RuntimeException e) {
/* 250 */       LogLog.warn("class [" + clazz + "], level [" + levelName + "] conversion failed.", e);
/*     */     }
/*     */ 
/* 253 */     return result;
/*     */   }
/*     */ 
/*     */   public static long toFileSize(String value, long dEfault)
/*     */   {
/* 259 */     if (value == null) {
/* 260 */       return dEfault;
/*     */     }
/* 262 */     String s = value.trim().toUpperCase();
/* 263 */     long multiplier = 1L;
/*     */     int index;
/* 266 */     if ((index = s.indexOf("KB")) != -1) {
/* 267 */       multiplier = 1024L;
/* 268 */       s = s.substring(0, index);
/*     */     }
/* 270 */     else if ((index = s.indexOf("MB")) != -1) {
/* 271 */       multiplier = 1048576L;
/* 272 */       s = s.substring(0, index);
/*     */     }
/* 274 */     else if ((index = s.indexOf("GB")) != -1) {
/* 275 */       multiplier = 1073741824L;
/* 276 */       s = s.substring(0, index);
/*     */     }
/* 278 */     if (s != null) {
/*     */       try {
/* 280 */         return Long.valueOf(s).longValue() * multiplier;
/*     */       }
/*     */       catch (NumberFormatException e) {
/* 283 */         LogLog.error("[" + s + "] is not in proper int form.");
/* 284 */         LogLog.error("[" + value + "] not in expected format.", e);
/*     */       }
/*     */     }
/* 287 */     return dEfault;
/*     */   }
/*     */ 
/*     */   public static String findAndSubst(String key, Properties props)
/*     */   {
/* 299 */     String value = props.getProperty(key);
/* 300 */     if (value == null)
/* 301 */       return null;
/*     */     try
/*     */     {
/* 304 */       return substVars(value, props);
/*     */     } catch (IllegalArgumentException e) {
/* 306 */       LogLog.error("Bad option value [" + value + "].", e);
/* 307 */     }return value;
/*     */   }
/*     */ 
/*     */   public static Object instantiateByClassName(String className, Class superClass, Object defaultValue)
/*     */   {
/* 325 */     if (className != null) {
/*     */       try {
/* 327 */         Class classObj = Loader.loadClass(className);
/* 328 */         if (!superClass.isAssignableFrom(classObj)) {
/* 329 */           LogLog.error("A \"" + className + "\" object is not assignable to a \"" + superClass.getName() + "\" variable.");
/*     */ 
/* 331 */           LogLog.error("The class \"" + superClass.getName() + "\" was loaded by ");
/* 332 */           LogLog.error("[" + superClass.getClassLoader() + "] whereas object of type ");
/* 333 */           LogLog.error("\"" + classObj.getName() + "\" was loaded by [" + classObj.getClassLoader() + "].");
/*     */ 
/* 335 */           return defaultValue;
/*     */         }
/* 337 */         return classObj.newInstance();
/*     */       } catch (ClassNotFoundException e) {
/* 339 */         LogLog.error("Could not instantiate class [" + className + "].", e);
/*     */       } catch (IllegalAccessException e) {
/* 341 */         LogLog.error("Could not instantiate class [" + className + "].", e);
/*     */       } catch (InstantiationException e) {
/* 343 */         LogLog.error("Could not instantiate class [" + className + "].", e);
/*     */       } catch (RuntimeException e) {
/* 345 */         LogLog.error("Could not instantiate class [" + className + "].", e);
/*     */       }
/*     */     }
/* 348 */     return defaultValue;
/*     */   }
/*     */ 
/*     */   public static String substVars(String val, Properties props)
/*     */     throws IllegalArgumentException
/*     */   {
/* 392 */     StringBuffer sbuf = new StringBuffer();
/*     */ 
/* 394 */     int i = 0;
/*     */     while (true)
/*     */     {
/* 398 */       int j = val.indexOf(DELIM_START, i);
/* 399 */       if (j == -1)
/*     */       {
/* 401 */         if (i == 0) {
/* 402 */           return val;
/*     */         }
/* 404 */         sbuf.append(val.substring(i, val.length()));
/* 405 */         return sbuf.toString();
/*     */       }
/*     */ 
/* 408 */       sbuf.append(val.substring(i, j));
/* 409 */       int k = val.indexOf(DELIM_STOP, j);
/* 410 */       if (k == -1) {
/* 411 */         throw new IllegalArgumentException('"' + val + "\" has no closing brace. Opening brace at position " + j + '.');
/*     */       }
/*     */ 
/* 415 */       j += DELIM_START_LEN;
/* 416 */       String key = val.substring(j, k);
/*     */ 
/* 418 */       String replacement = getSystemProperty(key, null);
/*     */ 
/* 420 */       if ((replacement == null) && (props != null)) {
/* 421 */         replacement = props.getProperty(key);
/*     */       }
/*     */ 
/* 424 */       if (replacement != null)
/*     */       {
/* 430 */         String recursiveReplacement = substVars(replacement, props);
/* 431 */         sbuf.append(recursiveReplacement);
/*     */       }
/* 433 */       i = k + DELIM_STOP_LEN;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void selectAndConfigure(InputStream inputStream, String clazz, LoggerRepository hierarchy)
/*     */   {
/* 463 */     Configurator configurator = null;
/*     */ 
/* 465 */     if (clazz != null) {
/* 466 */       LogLog.debug("Preferred configurator class: " + clazz);
/* 467 */       configurator = (Configurator)instantiateByClassName(clazz, Configurator.class, null);
/*     */ 
/* 470 */       if (configurator == null)
/* 471 */         LogLog.error("Could not instantiate configurator [" + clazz + "].");
/*     */     }
/*     */     else
/*     */     {
/* 475 */       configurator = new PropertyConfigurator();
/*     */     }
/*     */ 
/* 478 */     configurator.doConfigure(inputStream, hierarchy);
/*     */   }
/*     */ 
/*     */   public static void selectAndConfigure(URL url, String clazz, LoggerRepository hierarchy)
/*     */   {
/* 506 */     Configurator configurator = null;
/* 507 */     String filename = url.getFile();
/*     */ 
/* 509 */     if ((clazz == null) && (filename != null) && (filename.endsWith(".xml"))) {
/* 510 */       clazz = "org.apache.log4j.xml.DOMConfigurator";
/*     */     }
/*     */ 
/* 513 */     if (clazz != null) {
/* 514 */       LogLog.debug("Preferred configurator class: " + clazz);
/* 515 */       configurator = (Configurator)instantiateByClassName(clazz, Configurator.class, null);
/*     */ 
/* 518 */       if (configurator == null)
/* 519 */         LogLog.error("Could not instantiate configurator [" + clazz + "].");
/*     */     }
/*     */     else
/*     */     {
/* 523 */       configurator = new PropertyConfigurator();
/*     */     }
/*     */ 
/* 526 */     configurator.doConfigure(url, hierarchy);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.OptionConverter
 * JD-Core Version:    0.6.2
 */