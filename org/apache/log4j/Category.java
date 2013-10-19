/*      */ package org.apache.log4j;
/*      */ 
/*      */ import java.text.MessageFormat;
/*      */ import java.util.Enumeration;
/*      */ import java.util.MissingResourceException;
/*      */ import java.util.ResourceBundle;
/*      */ import java.util.Vector;
/*      */ import org.apache.log4j.helpers.AppenderAttachableImpl;
/*      */ import org.apache.log4j.helpers.NullEnumeration;
/*      */ import org.apache.log4j.spi.AppenderAttachable;
/*      */ import org.apache.log4j.spi.HierarchyEventListener;
/*      */ import org.apache.log4j.spi.LoggerRepository;
/*      */ import org.apache.log4j.spi.LoggingEvent;
/*      */ 
/*      */ public class Category
/*      */   implements AppenderAttachable
/*      */ {
/*      */   protected String name;
/*      */   protected volatile Level level;
/*      */   protected volatile Category parent;
/*  118 */   private static final String FQCN = Category.class.getName();
/*      */   protected ResourceBundle resourceBundle;
/*      */   protected LoggerRepository repository;
/*      */   AppenderAttachableImpl aai;
/*  135 */   protected boolean additive = true;
/*      */ 
/*      */   protected Category(String name)
/*      */   {
/*  148 */     this.name = name;
/*      */   }
/*      */ 
/*      */   public synchronized void addAppender(Appender newAppender)
/*      */   {
/*  161 */     if (this.aai == null) {
/*  162 */       this.aai = new AppenderAttachableImpl();
/*      */     }
/*  164 */     this.aai.addAppender(newAppender);
/*  165 */     this.repository.fireAddAppenderEvent(this, newAppender);
/*      */   }
/*      */ 
/*      */   public void assertLog(boolean assertion, String msg)
/*      */   {
/*  183 */     if (!assertion)
/*  184 */       error(msg);
/*      */   }
/*      */ 
/*      */   public void callAppenders(LoggingEvent event)
/*      */   {
/*  200 */     int writes = 0;
/*      */ 
/*  202 */     for (Category c = this; c != null; c = c.parent)
/*      */     {
/*  204 */       synchronized (c) {
/*  205 */         if (c.aai != null) {
/*  206 */           writes += c.aai.appendLoopOnAppenders(event);
/*      */         }
/*  208 */         if (!c.additive) {
/*  209 */           break;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*  214 */     if (writes == 0)
/*  215 */       this.repository.emitNoAppenderWarning(this);
/*      */   }
/*      */ 
/*      */   synchronized void closeNestedAppenders()
/*      */   {
/*  226 */     Enumeration enumeration = getAllAppenders();
/*  227 */     if (enumeration != null)
/*  228 */       while (enumeration.hasMoreElements()) {
/*  229 */         Appender a = (Appender)enumeration.nextElement();
/*  230 */         if ((a instanceof AppenderAttachable))
/*  231 */           a.close();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void debug(Object message)
/*      */   {
/*  257 */     if (this.repository.isDisabled(10000))
/*  258 */       return;
/*  259 */     if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel()))
/*  260 */       forcedLog(FQCN, Level.DEBUG, message, null);
/*      */   }
/*      */ 
/*      */   public void debug(Object message, Throwable t)
/*      */   {
/*  276 */     if (this.repository.isDisabled(10000))
/*  277 */       return;
/*  278 */     if (Level.DEBUG.isGreaterOrEqual(getEffectiveLevel()))
/*  279 */       forcedLog(FQCN, Level.DEBUG, message, t);
/*      */   }
/*      */ 
/*      */   public void error(Object message)
/*      */   {
/*  302 */     if (this.repository.isDisabled(40000))
/*  303 */       return;
/*  304 */     if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel()))
/*  305 */       forcedLog(FQCN, Level.ERROR, message, null);
/*      */   }
/*      */ 
/*      */   public void error(Object message, Throwable t)
/*      */   {
/*  319 */     if (this.repository.isDisabled(40000))
/*  320 */       return;
/*  321 */     if (Level.ERROR.isGreaterOrEqual(getEffectiveLevel()))
/*  322 */       forcedLog(FQCN, Level.ERROR, message, t);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Logger exists(String name)
/*      */   {
/*  338 */     return LogManager.exists(name);
/*      */   }
/*      */ 
/*      */   public void fatal(Object message)
/*      */   {
/*  362 */     if (this.repository.isDisabled(50000))
/*  363 */       return;
/*  364 */     if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel()))
/*  365 */       forcedLog(FQCN, Level.FATAL, message, null);
/*      */   }
/*      */ 
/*      */   public void fatal(Object message, Throwable t)
/*      */   {
/*  379 */     if (this.repository.isDisabled(50000))
/*  380 */       return;
/*  381 */     if (Level.FATAL.isGreaterOrEqual(getEffectiveLevel()))
/*  382 */       forcedLog(FQCN, Level.FATAL, message, t);
/*      */   }
/*      */ 
/*      */   protected void forcedLog(String fqcn, Priority level, Object message, Throwable t)
/*      */   {
/*  391 */     callAppenders(new LoggingEvent(fqcn, this, level, message, t));
/*      */   }
/*      */ 
/*      */   public boolean getAdditivity()
/*      */   {
/*  400 */     return this.additive;
/*      */   }
/*      */ 
/*      */   public synchronized Enumeration getAllAppenders()
/*      */   {
/*  412 */     if (this.aai == null) {
/*  413 */       return NullEnumeration.getInstance();
/*      */     }
/*  415 */     return this.aai.getAllAppenders();
/*      */   }
/*      */ 
/*      */   public synchronized Appender getAppender(String name)
/*      */   {
/*  426 */     if ((this.aai == null) || (name == null)) {
/*  427 */       return null;
/*      */     }
/*  429 */     return this.aai.getAppender(name);
/*      */   }
/*      */ 
/*      */   public Level getEffectiveLevel()
/*      */   {
/*  442 */     for (Category c = this; c != null; c = c.parent) {
/*  443 */       if (c.level != null)
/*  444 */         return c.level;
/*      */     }
/*  446 */     return null;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public Priority getChainedPriority()
/*      */   {
/*  456 */     for (Category c = this; c != null; c = c.parent) {
/*  457 */       if (c.level != null)
/*  458 */         return c.level;
/*      */     }
/*  460 */     return null;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Enumeration getCurrentCategories()
/*      */   {
/*  476 */     return LogManager.getCurrentLoggers();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static LoggerRepository getDefaultHierarchy()
/*      */   {
/*  490 */     return LogManager.getLoggerRepository();
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public LoggerRepository getHierarchy()
/*      */   {
/*  502 */     return this.repository;
/*      */   }
/*      */ 
/*      */   public LoggerRepository getLoggerRepository()
/*      */   {
/*  512 */     return this.repository;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Category getInstance(String name)
/*      */   {
/*  522 */     return LogManager.getLogger(name);
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static Category getInstance(Class clazz)
/*      */   {
/*  531 */     return LogManager.getLogger(clazz);
/*      */   }
/*      */ 
/*      */   public final String getName()
/*      */   {
/*  540 */     return this.name;
/*      */   }
/*      */ 
/*      */   public final Category getParent()
/*      */   {
/*  555 */     return this.parent;
/*      */   }
/*      */ 
/*      */   public final Level getLevel()
/*      */   {
/*  567 */     return this.level;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public final Level getPriority()
/*      */   {
/*  576 */     return this.level;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static final Category getRoot()
/*      */   {
/*  587 */     return LogManager.getRootLogger();
/*      */   }
/*      */ 
/*      */   public ResourceBundle getResourceBundle()
/*      */   {
/*  603 */     for (Category c = this; c != null; c = c.parent) {
/*  604 */       if (c.resourceBundle != null) {
/*  605 */         return c.resourceBundle;
/*      */       }
/*      */     }
/*  608 */     return null;
/*      */   }
/*      */ 
/*      */   protected String getResourceBundleString(String key)
/*      */   {
/*  621 */     ResourceBundle rb = getResourceBundle();
/*      */ 
/*  624 */     if (rb == null)
/*      */     {
/*  629 */       return null;
/*      */     }
/*      */     try
/*      */     {
/*  633 */       return rb.getString(key);
/*      */     }
/*      */     catch (MissingResourceException mre) {
/*  636 */       error("No resource is associated with key \"" + key + "\".");
/*  637 */     }return null;
/*      */   }
/*      */ 
/*      */   public void info(Object message)
/*      */   {
/*  663 */     if (this.repository.isDisabled(20000))
/*  664 */       return;
/*  665 */     if (Level.INFO.isGreaterOrEqual(getEffectiveLevel()))
/*  666 */       forcedLog(FQCN, Level.INFO, message, null);
/*      */   }
/*      */ 
/*      */   public void info(Object message, Throwable t)
/*      */   {
/*  680 */     if (this.repository.isDisabled(20000))
/*  681 */       return;
/*  682 */     if (Level.INFO.isGreaterOrEqual(getEffectiveLevel()))
/*  683 */       forcedLog(FQCN, Level.INFO, message, t);
/*      */   }
/*      */ 
/*      */   public boolean isAttached(Appender appender)
/*      */   {
/*  691 */     if ((appender == null) || (this.aai == null)) {
/*  692 */       return false;
/*      */     }
/*  694 */     return this.aai.isAttached(appender);
/*      */   }
/*      */ 
/*      */   public boolean isDebugEnabled()
/*      */   {
/*  734 */     if (this.repository.isDisabled(10000))
/*  735 */       return false;
/*  736 */     return Level.DEBUG.isGreaterOrEqual(getEffectiveLevel());
/*      */   }
/*      */ 
/*      */   public boolean isEnabledFor(Priority level)
/*      */   {
/*  749 */     if (this.repository.isDisabled(level.level))
/*  750 */       return false;
/*  751 */     return level.isGreaterOrEqual(getEffectiveLevel());
/*      */   }
/*      */ 
/*      */   public boolean isInfoEnabled()
/*      */   {
/*  763 */     if (this.repository.isDisabled(20000))
/*  764 */       return false;
/*  765 */     return Level.INFO.isGreaterOrEqual(getEffectiveLevel());
/*      */   }
/*      */ 
/*      */   public void l7dlog(Priority priority, String key, Throwable t)
/*      */   {
/*  779 */     if (this.repository.isDisabled(priority.level)) {
/*  780 */       return;
/*      */     }
/*  782 */     if (priority.isGreaterOrEqual(getEffectiveLevel())) {
/*  783 */       String msg = getResourceBundleString(key);
/*      */ 
/*  786 */       if (msg == null) {
/*  787 */         msg = key;
/*      */       }
/*  789 */       forcedLog(FQCN, priority, msg, t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void l7dlog(Priority priority, String key, Object[] params, Throwable t)
/*      */   {
/*  803 */     if (this.repository.isDisabled(priority.level)) {
/*  804 */       return;
/*      */     }
/*  806 */     if (priority.isGreaterOrEqual(getEffectiveLevel())) {
/*  807 */       String pattern = getResourceBundleString(key);
/*      */       String msg;
/*      */       String msg;
/*  809 */       if (pattern == null)
/*  810 */         msg = key;
/*      */       else
/*  812 */         msg = MessageFormat.format(pattern, params);
/*  813 */       forcedLog(FQCN, priority, msg, t);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void log(Priority priority, Object message, Throwable t)
/*      */   {
/*  822 */     if (this.repository.isDisabled(priority.level)) {
/*  823 */       return;
/*      */     }
/*  825 */     if (priority.isGreaterOrEqual(getEffectiveLevel()))
/*  826 */       forcedLog(FQCN, priority, message, t);
/*      */   }
/*      */ 
/*      */   public void log(Priority priority, Object message)
/*      */   {
/*  834 */     if (this.repository.isDisabled(priority.level)) {
/*  835 */       return;
/*      */     }
/*  837 */     if (priority.isGreaterOrEqual(getEffectiveLevel()))
/*  838 */       forcedLog(FQCN, priority, message, null);
/*      */   }
/*      */ 
/*      */   public void log(String callerFQCN, Priority level, Object message, Throwable t)
/*      */   {
/*  852 */     if (this.repository.isDisabled(level.level)) {
/*  853 */       return;
/*      */     }
/*  855 */     if (level.isGreaterOrEqual(getEffectiveLevel()))
/*  856 */       forcedLog(callerFQCN, level, message, t);
/*      */   }
/*      */ 
/*      */   private void fireRemoveAppenderEvent(Appender appender)
/*      */   {
/*  868 */     if (appender != null)
/*  869 */       if ((this.repository instanceof Hierarchy))
/*  870 */         ((Hierarchy)this.repository).fireRemoveAppenderEvent(this, appender);
/*  871 */       else if ((this.repository instanceof HierarchyEventListener))
/*  872 */         ((HierarchyEventListener)this.repository).removeAppenderEvent(this, appender);
/*      */   }
/*      */ 
/*      */   public synchronized void removeAllAppenders()
/*      */   {
/*  886 */     if (this.aai != null) {
/*  887 */       Vector appenders = new Vector();
/*  888 */       for (Enumeration iter = this.aai.getAllAppenders(); (iter != null) && (iter.hasMoreElements()); ) {
/*  889 */         appenders.add(iter.nextElement());
/*      */       }
/*  891 */       this.aai.removeAllAppenders();
/*  892 */       for (Enumeration iter = appenders.elements(); iter.hasMoreElements(); ) {
/*  893 */         fireRemoveAppenderEvent((Appender)iter.nextElement());
/*      */       }
/*  895 */       this.aai = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public synchronized void removeAppender(Appender appender)
/*      */   {
/*  908 */     if ((appender == null) || (this.aai == null))
/*  909 */       return;
/*  910 */     boolean wasAttached = this.aai.isAttached(appender);
/*  911 */     this.aai.removeAppender(appender);
/*  912 */     if (wasAttached)
/*  913 */       fireRemoveAppenderEvent(appender);
/*      */   }
/*      */ 
/*      */   public synchronized void removeAppender(String name)
/*      */   {
/*  925 */     if ((name == null) || (this.aai == null)) return;
/*  926 */     Appender appender = this.aai.getAppender(name);
/*  927 */     this.aai.removeAppender(name);
/*  928 */     if (appender != null)
/*  929 */       fireRemoveAppenderEvent(appender);
/*      */   }
/*      */ 
/*      */   public void setAdditivity(boolean additive)
/*      */   {
/*  939 */     this.additive = additive;
/*      */   }
/*      */ 
/*      */   final void setHierarchy(LoggerRepository repository)
/*      */   {
/*  947 */     this.repository = repository;
/*      */   }
/*      */ 
/*      */   public void setLevel(Level level)
/*      */   {
/*  963 */     this.level = level;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public void setPriority(Priority priority)
/*      */   {
/*  976 */     this.level = ((Level)priority);
/*      */   }
/*      */ 
/*      */   public void setResourceBundle(ResourceBundle bundle)
/*      */   {
/*  989 */     this.resourceBundle = bundle;
/*      */   }
/*      */ 
/*      */   /** @deprecated */
/*      */   public static void shutdown()
/*      */   {
/* 1014 */     LogManager.shutdown();
/*      */   }
/*      */ 
/*      */   public void warn(Object message)
/*      */   {
/* 1039 */     if (this.repository.isDisabled(30000)) {
/* 1040 */       return;
/*      */     }
/* 1042 */     if (Level.WARN.isGreaterOrEqual(getEffectiveLevel()))
/* 1043 */       forcedLog(FQCN, Level.WARN, message, null);
/*      */   }
/*      */ 
/*      */   public void warn(Object message, Throwable t)
/*      */   {
/* 1057 */     if (this.repository.isDisabled(30000))
/* 1058 */       return;
/* 1059 */     if (Level.WARN.isGreaterOrEqual(getEffectiveLevel()))
/* 1060 */       forcedLog(FQCN, Level.WARN, message, t);
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.Category
 * JD-Core Version:    0.6.2
 */