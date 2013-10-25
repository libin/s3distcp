/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.util.Enumeration;
/*     */ import org.apache.log4j.helpers.Loader;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.DefaultRepositorySelector;
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.NOPLoggerRepository;
/*     */ import org.apache.log4j.spi.RepositorySelector;
/*     */ import org.apache.log4j.spi.RootLogger;
/*     */ 
/*     */ public class LogManager
/*     */ {
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
/*     */   static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";
/*  77 */   private static Object guard = null;
/*     */   private static RepositorySelector repositorySelector;
/*     */ 
/*     */   public static void setRepositorySelector(RepositorySelector selector, Object guard)
/*     */     throws IllegalArgumentException
/*     */   {
/* 163 */     if ((guard != null) && (guard != guard)) {
/* 164 */       throw new IllegalArgumentException("Attempted to reset the LoggerFactory without possessing the guard.");
/*     */     }
/*     */ 
/* 168 */     if (selector == null) {
/* 169 */       throw new IllegalArgumentException("RepositorySelector must be non-null.");
/*     */     }
/*     */ 
/* 172 */     guard = guard;
/* 173 */     repositorySelector = selector;
/*     */   }
/*     */ 
/*     */   private static boolean isLikelySafeScenario(Exception ex)
/*     */   {
/* 187 */     StringWriter stringWriter = new StringWriter();
/* 188 */     ex.printStackTrace(new PrintWriter(stringWriter));
/* 189 */     String msg = stringWriter.toString();
/* 190 */     return msg.indexOf("org.apache.catalina.loader.WebappClassLoader.stop") != -1;
/*     */   }
/*     */ 
/*     */   public static LoggerRepository getLoggerRepository()
/*     */   {
/* 196 */     if (repositorySelector == null) {
/* 197 */       repositorySelector = new DefaultRepositorySelector(new NOPLoggerRepository());
/* 198 */       guard = null;
/* 199 */       Exception ex = new IllegalStateException("Class invariant violation");
/* 200 */       String msg = "log4j called after unloading, see http://logging.apache.org/log4j/1.2/faq.html#unload.";
/*     */ 
/* 202 */       if (isLikelySafeScenario(ex))
/* 203 */         LogLog.debug(msg, ex);
/*     */       else {
/* 205 */         LogLog.error(msg, ex);
/*     */       }
/*     */     }
/* 208 */     return repositorySelector.getLoggerRepository();
/*     */   }
/*     */ 
/*     */   public static Logger getRootLogger()
/*     */   {
/* 218 */     return getLoggerRepository().getRootLogger();
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name)
/*     */   {
/* 228 */     return getLoggerRepository().getLogger(name);
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(Class clazz)
/*     */   {
/* 238 */     return getLoggerRepository().getLogger(clazz.getName());
/*     */   }
/*     */ 
/*     */   public static Logger getLogger(String name, LoggerFactory factory)
/*     */   {
/* 249 */     return getLoggerRepository().getLogger(name, factory);
/*     */   }
/*     */ 
/*     */   public static Logger exists(String name)
/*     */   {
/* 255 */     return getLoggerRepository().exists(name);
/*     */   }
/*     */ 
/*     */   public static Enumeration getCurrentLoggers()
/*     */   {
/* 261 */     return getLoggerRepository().getCurrentLoggers();
/*     */   }
/*     */ 
/*     */   public static void shutdown()
/*     */   {
/* 267 */     getLoggerRepository().shutdown();
/*     */   }
/*     */ 
/*     */   public static void resetConfiguration()
/*     */   {
/* 273 */     getLoggerRepository().resetConfiguration();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  82 */     Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
/*  83 */     repositorySelector = new DefaultRepositorySelector(h);
/*     */ 
/*  86 */     String override = OptionConverter.getSystemProperty("log4j.defaultInitOverride", null);
/*     */ 
/*  91 */     if ((override == null) || ("false".equalsIgnoreCase(override)))
/*     */     {
/*  93 */       String configurationOptionStr = OptionConverter.getSystemProperty("log4j.configuration", null);
/*     */ 
/*  97 */       String configuratorClassName = OptionConverter.getSystemProperty("log4j.configuratorClass", null);
/*     */ 
/* 101 */       URL url = null;
/*     */ 
/* 106 */       if (configurationOptionStr == null) {
/* 107 */         url = Loader.getResource("log4j.xml");
/* 108 */         if (url == null)
/* 109 */           url = Loader.getResource("log4j.properties");
/*     */       }
/*     */       else {
/*     */         try {
/* 113 */           url = new URL(configurationOptionStr);
/*     */         }
/*     */         catch (MalformedURLException ex)
/*     */         {
/* 117 */           url = Loader.getResource(configurationOptionStr);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 124 */       if (url != null) {
/* 125 */         LogLog.debug("Using URL [" + url + "] for automatic log4j configuration.");
/*     */         try {
/* 127 */           OptionConverter.selectAndConfigure(url, configuratorClassName, getLoggerRepository());
/*     */         }
/*     */         catch (NoClassDefFoundError e) {
/* 130 */           LogLog.warn("Error during default initialization", e);
/*     */         }
/*     */       } else {
/* 133 */         LogLog.debug("Could not find resource: [" + configurationOptionStr + "].");
/*     */       }
/*     */     } else {
/* 136 */       LogLog.debug("Default initialization of overridden by log4j.defaultInitOverrideproperty.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.LogManager
 * JD-Core Version:    0.6.2
 */