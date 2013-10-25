/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Properties;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.config.PropertySetter;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.or.RendererMap;
/*     */ import org.apache.log4j.spi.Configurator;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.Filter;
/*     */ import org.apache.log4j.spi.LoggerFactory;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ import org.apache.log4j.spi.RendererSupport;
/*     */ import org.apache.log4j.spi.ThrowableRenderer;
/*     */ import org.apache.log4j.spi.ThrowableRendererSupport;
/*     */ 
/*     */ public class PropertyConfigurator
/*     */   implements Configurator
/*     */ {
/*  98 */   protected Hashtable registry = new Hashtable(11);
/*     */   private LoggerRepository repository;
/* 100 */   protected LoggerFactory loggerFactory = new DefaultCategoryFactory();
/*     */   static final String CATEGORY_PREFIX = "log4j.category.";
/*     */   static final String LOGGER_PREFIX = "log4j.logger.";
/*     */   static final String FACTORY_PREFIX = "log4j.factory";
/*     */   static final String ADDITIVITY_PREFIX = "log4j.additivity.";
/*     */   static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
/*     */   static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
/*     */   static final String APPENDER_PREFIX = "log4j.appender.";
/*     */   static final String RENDERER_PREFIX = "log4j.renderer.";
/*     */   static final String THRESHOLD_PREFIX = "log4j.threshold";
/*     */   private static final String THROWABLE_RENDERER_PREFIX = "log4j.throwableRenderer";
/*     */   private static final String LOGGER_REF = "logger-ref";
/*     */   private static final String ROOT_REF = "root-ref";
/*     */   private static final String APPENDER_REF_TAG = "appender-ref";
/*     */   public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";
/*     */   private static final String RESET_KEY = "log4j.reset";
/*     */   private static final String INTERNAL_ROOT_NAME = "root";
/*     */ 
/*     */   public void doConfigure(String configFileName, LoggerRepository hierarchy)
/*     */   {
/* 369 */     Properties props = new Properties();
/* 370 */     FileInputStream istream = null;
/*     */     try {
/* 372 */       istream = new FileInputStream(configFileName);
/* 373 */       props.load(istream);
/* 374 */       istream.close();
/*     */     }
/*     */     catch (Exception e) {
/* 377 */       if (((e instanceof InterruptedIOException)) || ((e instanceof InterruptedException))) {
/* 378 */         Thread.currentThread().interrupt();
/* 380 */       }LogLog.error("Could not read configuration file [" + configFileName + "].", e);
/* 381 */       LogLog.error("Ignoring configuration file [" + configFileName + "].");
/*     */       return; } finally {
/* 384 */       if (istream != null) {
/*     */         try {
/* 386 */           istream.close();
/*     */         } catch (InterruptedIOException ignore) {
/* 388 */           Thread.currentThread().interrupt();
/*     */         }
/*     */         catch (Throwable ignore)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/* 395 */     doConfigure(props, hierarchy);
/*     */   }
/*     */ 
/*     */   public static void configure(String configFilename)
/*     */   {
/* 403 */     new PropertyConfigurator().doConfigure(configFilename, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(URL configURL)
/*     */   {
/* 415 */     new PropertyConfigurator().doConfigure(configURL, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(InputStream inputStream)
/*     */   {
/* 427 */     new PropertyConfigurator().doConfigure(inputStream, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configure(Properties properties)
/*     */   {
/* 440 */     new PropertyConfigurator().doConfigure(properties, LogManager.getLoggerRepository());
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename)
/*     */   {
/* 455 */     configureAndWatch(configFilename, 60000L);
/*     */   }
/*     */ 
/*     */   public static void configureAndWatch(String configFilename, long delay)
/*     */   {
/* 473 */     PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
/* 474 */     pdog.setDelay(delay);
/* 475 */     pdog.start();
/*     */   }
/*     */ 
/*     */   public void doConfigure(Properties properties, LoggerRepository hierarchy)
/*     */   {
/* 486 */     this.repository = hierarchy;
/* 487 */     String value = properties.getProperty("log4j.debug");
/* 488 */     if (value == null) {
/* 489 */       value = properties.getProperty("log4j.configDebug");
/* 490 */       if (value != null) {
/* 491 */         LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
/*     */       }
/*     */     }
/* 494 */     if (value != null) {
/* 495 */       LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
/*     */     }
/*     */ 
/* 501 */     String reset = properties.getProperty("log4j.reset");
/* 502 */     if ((reset != null) && (OptionConverter.toBoolean(reset, false))) {
/* 503 */       hierarchy.resetConfiguration();
/*     */     }
/*     */ 
/* 506 */     String thresholdStr = OptionConverter.findAndSubst("log4j.threshold", properties);
/*     */ 
/* 508 */     if (thresholdStr != null) {
/* 509 */       hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr, Level.ALL));
/*     */ 
/* 511 */       LogLog.debug("Hierarchy threshold set to [" + hierarchy.getThreshold() + "].");
/*     */     }
/*     */ 
/* 514 */     configureRootCategory(properties, hierarchy);
/* 515 */     configureLoggerFactory(properties);
/* 516 */     parseCatsAndRenderers(properties, hierarchy);
/*     */ 
/* 518 */     LogLog.debug("Finished configuring.");
/*     */ 
/* 521 */     this.registry.clear();
/*     */   }
/*     */ 
/*     */   public void doConfigure(InputStream inputStream, LoggerRepository hierarchy)
/*     */   {
/* 530 */     Properties props = new Properties();
/*     */     try {
/* 532 */       props.load(inputStream);
/*     */     } catch (IOException e) {
/* 534 */       if ((e instanceof InterruptedIOException)) {
/* 535 */         Thread.currentThread().interrupt();
/*     */       }
/* 537 */       LogLog.error("Could not read configuration file from InputStream [" + inputStream + "].", e);
/*     */ 
/* 539 */       LogLog.error("Ignoring configuration InputStream [" + inputStream + "].");
/* 540 */       return;
/*     */     }
/* 542 */     doConfigure(props, hierarchy);
/*     */   }
/*     */ 
/*     */   public void doConfigure(URL configURL, LoggerRepository hierarchy)
/*     */   {
/* 550 */     Properties props = new Properties();
/* 551 */     LogLog.debug("Reading configuration from URL " + configURL);
/* 552 */     InputStream istream = null;
/* 553 */     URLConnection uConn = null;
/*     */     try {
/* 555 */       uConn = configURL.openConnection();
/* 556 */       uConn.setUseCaches(false);
/* 557 */       istream = uConn.getInputStream();
/* 558 */       props.load(istream);
/*     */     }
/*     */     catch (Exception e) {
/* 561 */       if (((e instanceof InterruptedIOException)) || ((e instanceof InterruptedException))) {
/* 562 */         Thread.currentThread().interrupt();
/* 564 */       }LogLog.error("Could not read configuration file from URL [" + configURL + "].", e);
/*     */ 
/* 566 */       LogLog.error("Ignoring configuration file [" + configURL + "].");
/*     */       return;
/*     */     } finally {
/* 570 */       if (istream != null)
/*     */         try {
/* 572 */           istream.close();
/*     */         } catch (InterruptedIOException ignore) {
/* 574 */           Thread.currentThread().interrupt();
/*     */         } catch (IOException ignore) {
/*     */         }
/*     */         catch (RuntimeException ignore) {
/*     */         }
/*     */     }
/* 580 */     doConfigure(props, hierarchy);
/*     */   }
/*     */ 
/*     */   protected void configureLoggerFactory(Properties props)
/*     */   {
/* 599 */     String factoryClassName = OptionConverter.findAndSubst("log4j.loggerFactory", props);
/*     */ 
/* 601 */     if (factoryClassName != null) {
/* 602 */       LogLog.debug("Setting category factory to [" + factoryClassName + "].");
/* 603 */       this.loggerFactory = ((LoggerFactory)OptionConverter.instantiateByClassName(factoryClassName, LoggerFactory.class, this.loggerFactory));
/*     */ 
/* 607 */       PropertySetter.setProperties(this.loggerFactory, props, "log4j.factory.");
/*     */     }
/*     */   }
/*     */ 
/*     */   void configureRootCategory(Properties props, LoggerRepository hierarchy)
/*     */   {
/* 635 */     String effectiveFrefix = "log4j.rootLogger";
/* 636 */     String value = OptionConverter.findAndSubst("log4j.rootLogger", props);
/*     */ 
/* 638 */     if (value == null) {
/* 639 */       value = OptionConverter.findAndSubst("log4j.rootCategory", props);
/* 640 */       effectiveFrefix = "log4j.rootCategory";
/*     */     }
/*     */ 
/* 643 */     if (value == null) {
/* 644 */       LogLog.debug("Could not find root logger information. Is this OK?");
/*     */     } else {
/* 646 */       Logger root = hierarchy.getRootLogger();
/* 647 */       synchronized (root) {
/* 648 */         parseCategory(props, root, effectiveFrefix, "root", value);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy)
/*     */   {
/* 659 */     Enumeration enumeration = props.propertyNames();
/* 660 */     while (enumeration.hasMoreElements()) {
/* 661 */       String key = (String)enumeration.nextElement();
/* 662 */       if ((key.startsWith("log4j.category.")) || (key.startsWith("log4j.logger."))) {
/* 663 */         String loggerName = null;
/* 664 */         if (key.startsWith("log4j.category."))
/* 665 */           loggerName = key.substring("log4j.category.".length());
/* 666 */         else if (key.startsWith("log4j.logger.")) {
/* 667 */           loggerName = key.substring("log4j.logger.".length());
/*     */         }
/* 669 */         String value = OptionConverter.findAndSubst(key, props);
/* 670 */         Logger logger = hierarchy.getLogger(loggerName, this.loggerFactory);
/* 671 */         synchronized (logger) {
/* 672 */           parseCategory(props, logger, key, loggerName, value);
/* 673 */           parseAdditivityForLogger(props, logger, loggerName);
/*     */         }
/* 675 */       } else if (key.startsWith("log4j.renderer.")) {
/* 676 */         String renderedClass = key.substring("log4j.renderer.".length());
/* 677 */         String renderingClass = OptionConverter.findAndSubst(key, props);
/* 678 */         if ((hierarchy instanceof RendererSupport)) {
/* 679 */           RendererMap.addRenderer((RendererSupport)hierarchy, renderedClass, renderingClass);
/*     */         }
/*     */       }
/* 682 */       else if ((key.equals("log4j.throwableRenderer")) && 
/* 683 */         ((hierarchy instanceof ThrowableRendererSupport))) {
/* 684 */         ThrowableRenderer tr = (ThrowableRenderer)OptionConverter.instantiateByKey(props, "log4j.throwableRenderer", ThrowableRenderer.class, null);
/*     */ 
/* 689 */         if (tr == null) {
/* 690 */           LogLog.error("Could not instantiate throwableRenderer.");
/*     */         }
/*     */         else {
/* 693 */           PropertySetter setter = new PropertySetter(tr);
/* 694 */           setter.setProperties(props, "log4j.throwableRenderer.");
/* 695 */           ((ThrowableRendererSupport)hierarchy).setThrowableRenderer(tr);
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void parseAdditivityForLogger(Properties props, Logger cat, String loggerName)
/*     */   {
/* 708 */     String value = OptionConverter.findAndSubst("log4j.additivity." + loggerName, props);
/*     */ 
/* 710 */     LogLog.debug("Handling log4j.additivity." + loggerName + "=[" + value + "]");
/*     */ 
/* 712 */     if ((value != null) && (!value.equals(""))) {
/* 713 */       boolean additivity = OptionConverter.toBoolean(value, true);
/* 714 */       LogLog.debug("Setting additivity for \"" + loggerName + "\" to " + additivity);
/*     */ 
/* 716 */       cat.setAdditivity(additivity);
/*     */     }
/*     */   }
/*     */ 
/*     */   void parseCategory(Properties props, Logger logger, String optionKey, String loggerName, String value)
/*     */   {
/* 726 */     LogLog.debug("Parsing for [" + loggerName + "] with value=[" + value + "].");
/*     */ 
/* 728 */     StringTokenizer st = new StringTokenizer(value, ",");
/*     */ 
/* 733 */     if ((!value.startsWith(",")) && (!value.equals("")))
/*     */     {
/* 736 */       if (!st.hasMoreTokens()) {
/* 737 */         return;
/*     */       }
/* 739 */       String levelStr = st.nextToken();
/* 740 */       LogLog.debug("Level token is [" + levelStr + "].");
/*     */ 
/* 745 */       if (("inherited".equalsIgnoreCase(levelStr)) || ("null".equalsIgnoreCase(levelStr)))
/*     */       {
/* 747 */         if (loggerName.equals("root"))
/* 748 */           LogLog.warn("The root logger cannot be set to null.");
/*     */         else
/* 750 */           logger.setLevel(null);
/*     */       }
/*     */       else {
/* 753 */         logger.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
/*     */       }
/* 755 */       LogLog.debug("Category " + loggerName + " set to " + logger.getLevel());
/*     */     }
/*     */ 
/* 759 */     logger.removeAllAppenders();
/*     */ 
/* 763 */     while (st.hasMoreTokens()) {
/* 764 */       String appenderName = st.nextToken().trim();
/* 765 */       if ((appenderName != null) && (!appenderName.equals(",")))
/*     */       {
/* 767 */         LogLog.debug("Parsing appender named \"" + appenderName + "\".");
/* 768 */         Appender appender = parseAppender(props, appenderName);
/* 769 */         if (appender != null)
/* 770 */           logger.addAppender(appender);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   Appender parseAppender(Properties props, String appenderName) {
/* 776 */     Appender appender = registryGet(appenderName);
/* 777 */     if (appender != null) {
/* 778 */       LogLog.debug("Appender \"" + appenderName + "\" was already parsed.");
/* 779 */       return appender;
/*     */     }
/*     */ 
/* 782 */     String prefix = "log4j.appender." + appenderName;
/* 783 */     String layoutPrefix = prefix + ".layout";
/*     */ 
/* 785 */     appender = (Appender)OptionConverter.instantiateByKey(props, prefix, Appender.class, null);
/*     */ 
/* 788 */     if (appender == null) {
/* 789 */       LogLog.error("Could not instantiate appender named \"" + appenderName + "\".");
/*     */ 
/* 791 */       return null;
/*     */     }
/* 793 */     appender.setName(appenderName);
/*     */ 
/* 795 */     if ((appender instanceof OptionHandler)) {
/* 796 */       if (appender.requiresLayout()) {
/* 797 */         Layout layout = (Layout)OptionConverter.instantiateByKey(props, layoutPrefix, Layout.class, null);
/*     */ 
/* 801 */         if (layout != null) {
/* 802 */           appender.setLayout(layout);
/* 803 */           LogLog.debug("Parsing layout options for \"" + appenderName + "\".");
/*     */ 
/* 805 */           PropertySetter.setProperties(layout, props, layoutPrefix + ".");
/* 806 */           LogLog.debug("End of parsing for \"" + appenderName + "\".");
/*     */         }
/*     */       }
/* 809 */       String errorHandlerPrefix = prefix + ".errorhandler";
/* 810 */       String errorHandlerClass = OptionConverter.findAndSubst(errorHandlerPrefix, props);
/* 811 */       if (errorHandlerClass != null) {
/* 812 */         ErrorHandler eh = (ErrorHandler)OptionConverter.instantiateByKey(props, errorHandlerPrefix, ErrorHandler.class, null);
/*     */ 
/* 816 */         if (eh != null) {
/* 817 */           appender.setErrorHandler(eh);
/* 818 */           LogLog.debug("Parsing errorhandler options for \"" + appenderName + "\".");
/* 819 */           parseErrorHandler(eh, errorHandlerPrefix, props, this.repository);
/* 820 */           Properties edited = new Properties();
/* 821 */           String[] keys = { errorHandlerPrefix + "." + "root-ref", errorHandlerPrefix + "." + "logger-ref", errorHandlerPrefix + "." + "appender-ref" };
/*     */ 
/* 826 */           for (Iterator iter = props.entrySet().iterator(); iter.hasNext(); ) {
/* 827 */             Map.Entry entry = (Map.Entry)iter.next();
/* 828 */             int i = 0;
/* 829 */             while ((i < keys.length) && 
/* 830 */               (!keys[i].equals(entry.getKey()))) {
/* 829 */               i++;
/*     */             }
/*     */ 
/* 832 */             if (i == keys.length) {
/* 833 */               edited.put(entry.getKey(), entry.getValue());
/*     */             }
/*     */           }
/* 836 */           PropertySetter.setProperties(eh, edited, errorHandlerPrefix + ".");
/* 837 */           LogLog.debug("End of errorhandler parsing for \"" + appenderName + "\".");
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 842 */       PropertySetter.setProperties(appender, props, prefix + ".");
/* 843 */       LogLog.debug("Parsed \"" + appenderName + "\" options.");
/*     */     }
/* 845 */     parseAppenderFilters(props, appenderName, appender);
/* 846 */     registryPut(appender);
/* 847 */     return appender;
/*     */   }
/*     */ 
/*     */   private void parseErrorHandler(ErrorHandler eh, String errorHandlerPrefix, Properties props, LoggerRepository hierarchy)
/*     */   {
/* 855 */     boolean rootRef = OptionConverter.toBoolean(OptionConverter.findAndSubst(errorHandlerPrefix + "root-ref", props), false);
/*     */ 
/* 857 */     if (rootRef) {
/* 858 */       eh.setLogger(hierarchy.getRootLogger());
/*     */     }
/* 860 */     String loggerName = OptionConverter.findAndSubst(errorHandlerPrefix + "logger-ref", props);
/* 861 */     if (loggerName != null) {
/* 862 */       Logger logger = this.loggerFactory == null ? hierarchy.getLogger(loggerName) : hierarchy.getLogger(loggerName, this.loggerFactory);
/*     */ 
/* 864 */       eh.setLogger(logger);
/*     */     }
/* 866 */     String appenderName = OptionConverter.findAndSubst(errorHandlerPrefix + "appender-ref", props);
/* 867 */     if (appenderName != null) {
/* 868 */       Appender backup = parseAppender(props, appenderName);
/* 869 */       if (backup != null)
/* 870 */         eh.setBackupAppender(backup);
/*     */     }
/*     */   }
/*     */ 
/*     */   void parseAppenderFilters(Properties props, String appenderName, Appender appender)
/*     */   {
/* 880 */     String filterPrefix = "log4j.appender." + appenderName + ".filter.";
/* 881 */     int fIdx = filterPrefix.length();
/* 882 */     Hashtable filters = new Hashtable();
/* 883 */     Enumeration e = props.keys();
/* 884 */     String name = "";
/* 885 */     while (e.hasMoreElements()) {
/* 886 */       String key = (String)e.nextElement();
/* 887 */       if (key.startsWith(filterPrefix)) {
/* 888 */         int dotIdx = key.indexOf('.', fIdx);
/* 889 */         String filterKey = key;
/* 890 */         if (dotIdx != -1) {
/* 891 */           filterKey = key.substring(0, dotIdx);
/* 892 */           name = key.substring(dotIdx + 1);
/*     */         }
/* 894 */         Vector filterOpts = (Vector)filters.get(filterKey);
/* 895 */         if (filterOpts == null) {
/* 896 */           filterOpts = new Vector();
/* 897 */           filters.put(filterKey, filterOpts);
/*     */         }
/* 899 */         if (dotIdx != -1) {
/* 900 */           String value = OptionConverter.findAndSubst(key, props);
/* 901 */           filterOpts.add(new NameValue(name, value));
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 908 */     Enumeration g = new SortedKeyEnumeration(filters);
/* 909 */     while (g.hasMoreElements()) {
/* 910 */       String key = (String)g.nextElement();
/* 911 */       String clazz = props.getProperty(key);
/* 912 */       if (clazz != null) {
/* 913 */         LogLog.debug("Filter key: [" + key + "] class: [" + props.getProperty(key) + "] props: " + filters.get(key));
/* 914 */         Filter filter = (Filter)OptionConverter.instantiateByClassName(clazz, Filter.class, null);
/* 915 */         if (filter != null) {
/* 916 */           PropertySetter propSetter = new PropertySetter(filter);
/* 917 */           Vector v = (Vector)filters.get(key);
/* 918 */           Enumeration filterProps = v.elements();
/* 919 */           while (filterProps.hasMoreElements()) {
/* 920 */             NameValue kv = (NameValue)filterProps.nextElement();
/* 921 */             propSetter.setProperty(kv.key, kv.value);
/*     */           }
/* 923 */           propSetter.activate();
/* 924 */           LogLog.debug("Adding filter of type [" + filter.getClass() + "] to appender named [" + appender.getName() + "].");
/*     */ 
/* 926 */           appender.addFilter(filter);
/*     */         }
/*     */       } else {
/* 929 */         LogLog.warn("Missing class definition for filter: [" + key + "]");
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   void registryPut(Appender appender)
/*     */   {
/* 936 */     this.registry.put(appender.getName(), appender);
/*     */   }
/*     */ 
/*     */   Appender registryGet(String name) {
/* 940 */     return (Appender)this.registry.get(name);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.PropertyConfigurator
 * JD-Core Version:    0.6.2
 */