/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.util.Vector;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.JMException;
/*     */ import javax.management.ListenerNotFoundException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.Notification;
/*     */ import javax.management.NotificationBroadcaster;
/*     */ import javax.management.NotificationBroadcasterSupport;
/*     */ import javax.management.NotificationFilter;
/*     */ import javax.management.NotificationFilterSupport;
/*     */ import javax.management.NotificationListener;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Category;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.LogManager;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.HierarchyEventListener;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ 
/*     */ public class HierarchyDynamicMBean extends AbstractDynamicMBean
/*     */   implements HierarchyEventListener, NotificationBroadcaster
/*     */ {
/*     */   static final String ADD_APPENDER = "addAppender.";
/*     */   static final String THRESHOLD = "threshold";
/*  61 */   private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
/*  62 */   private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
/*     */ 
/*  64 */   private Vector vAttributes = new Vector();
/*  65 */   private String dClassName = getClass().getName();
/*  66 */   private String dDescription = "This MBean acts as a management facade for org.apache.log4j.Hierarchy.";
/*     */ 
/*  69 */   private NotificationBroadcasterSupport nbs = new NotificationBroadcasterSupport();
/*     */   private LoggerRepository hierarchy;
/*  74 */   private static Logger log = Logger.getLogger(HierarchyDynamicMBean.class);
/*     */ 
/*     */   public HierarchyDynamicMBean() {
/*  77 */     this.hierarchy = LogManager.getLoggerRepository();
/*  78 */     buildDynamicMBeanInfo();
/*     */   }
/*     */ 
/*     */   private void buildDynamicMBeanInfo()
/*     */   {
/*  83 */     Constructor[] constructors = getClass().getConstructors();
/*  84 */     this.dConstructors[0] = new MBeanConstructorInfo("HierarchyDynamicMBean(): Constructs a HierarchyDynamicMBean instance", constructors[0]);
/*     */ 
/*  88 */     this.vAttributes.add(new MBeanAttributeInfo("threshold", "java.lang.String", "The \"threshold\" state of the hiearchy.", true, true, false));
/*     */ 
/*  95 */     MBeanParameterInfo[] params = new MBeanParameterInfo[1];
/*  96 */     params[0] = new MBeanParameterInfo("name", "java.lang.String", "Create a logger MBean");
/*     */ 
/*  98 */     this.dOperations[0] = new MBeanOperationInfo("addLoggerMBean", "addLoggerMBean(): add a loggerMBean", params, "javax.management.ObjectName", 1);
/*     */   }
/*     */ 
/*     */   public ObjectName addLoggerMBean(String name)
/*     */   {
/* 108 */     Logger cat = LogManager.exists(name);
/*     */ 
/* 110 */     if (cat != null) {
/* 111 */       return addLoggerMBean(cat);
/*     */     }
/* 113 */     return null;
/*     */   }
/*     */ 
/*     */   ObjectName addLoggerMBean(Logger logger)
/*     */   {
/* 118 */     String name = logger.getName();
/* 119 */     ObjectName objectName = null;
/*     */     try {
/* 121 */       LoggerDynamicMBean loggerMBean = new LoggerDynamicMBean(logger);
/* 122 */       objectName = new ObjectName("log4j", "logger", name);
/*     */ 
/* 124 */       if (!this.server.isRegistered(objectName)) {
/* 125 */         registerMBean(loggerMBean, objectName);
/* 126 */         NotificationFilterSupport nfs = new NotificationFilterSupport();
/* 127 */         nfs.enableType("addAppender." + logger.getName());
/* 128 */         log.debug("---Adding logger [" + name + "] as listener.");
/* 129 */         this.nbs.addNotificationListener(loggerMBean, nfs, null);
/* 130 */         this.vAttributes.add(new MBeanAttributeInfo("logger=" + name, "javax.management.ObjectName", "The " + name + " logger.", true, true, false));
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (JMException e)
/*     */     {
/* 138 */       log.error("Could not add loggerMBean for [" + name + "].", e);
/*     */     } catch (RuntimeException e) {
/* 140 */       log.error("Could not add loggerMBean for [" + name + "].", e);
/*     */     }
/* 142 */     return objectName;
/*     */   }
/*     */ 
/*     */   public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
/*     */   {
/* 149 */     this.nbs.addNotificationListener(listener, filter, handback);
/*     */   }
/*     */ 
/*     */   protected Logger getLogger()
/*     */   {
/* 154 */     return log;
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 161 */     MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.vAttributes.size()];
/* 162 */     this.vAttributes.toArray(attribs);
/*     */ 
/* 164 */     return new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
/*     */   }
/*     */ 
/*     */   public MBeanNotificationInfo[] getNotificationInfo()
/*     */   {
/* 174 */     return this.nbs.getNotificationInfo();
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 183 */     if (operationName == null) {
/* 184 */       throw new RuntimeOperationsException(new IllegalArgumentException("Operation name cannot be null"), "Cannot invoke a null operation in " + this.dClassName);
/*     */     }
/*     */ 
/* 190 */     if (operationName.equals("addLoggerMBean")) {
/* 191 */       return addLoggerMBean((String)params[0]);
/*     */     }
/* 193 */     throw new ReflectionException(new NoSuchMethodException(operationName), "Cannot find the operation " + operationName + " in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 207 */     if (attributeName == null) {
/* 208 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 213 */     log.debug("Called getAttribute with [" + attributeName + "].");
/*     */ 
/* 216 */     if (attributeName.equals("threshold"))
/* 217 */       return this.hierarchy.getThreshold();
/* 218 */     if (attributeName.startsWith("logger")) {
/* 219 */       int k = attributeName.indexOf("%3D");
/* 220 */       String val = attributeName;
/* 221 */       if (k > 0)
/* 222 */         val = attributeName.substring(0, k) + '=' + attributeName.substring(k + 3);
/*     */       try
/*     */       {
/* 225 */         return new ObjectName("log4j:" + val);
/*     */       } catch (JMException e) {
/* 227 */         log.error("Could not create ObjectName" + val);
/*     */       } catch (RuntimeException e) {
/* 229 */         log.error("Could not create ObjectName" + val);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 236 */     throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public void addAppenderEvent(Category logger, Appender appender)
/*     */   {
/* 244 */     log.debug("addAppenderEvent called: logger=" + logger.getName() + ", appender=" + appender.getName());
/*     */ 
/* 246 */     Notification n = new Notification("addAppender." + logger.getName(), this, 0L);
/* 247 */     n.setUserData(appender);
/* 248 */     log.debug("sending notification.");
/* 249 */     this.nbs.sendNotification(n);
/*     */   }
/*     */ 
/*     */   public void removeAppenderEvent(Category cat, Appender appender)
/*     */   {
/* 254 */     log.debug("removeAppenderCalled: logger=" + cat.getName() + ", appender=" + appender.getName());
/*     */   }
/*     */ 
/*     */   public void postRegister(Boolean registrationDone)
/*     */   {
/* 260 */     log.debug("postRegister is called.");
/* 261 */     this.hierarchy.addHierarchyEventListener(this);
/* 262 */     Logger root = this.hierarchy.getRootLogger();
/* 263 */     addLoggerMBean(root);
/*     */   }
/*     */ 
/*     */   public void removeNotificationListener(NotificationListener listener)
/*     */     throws ListenerNotFoundException
/*     */   {
/* 269 */     this.nbs.removeNotificationListener(listener);
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 279 */     if (attribute == null) {
/* 280 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
/*     */     }
/*     */ 
/* 284 */     String name = attribute.getName();
/* 285 */     Object value = attribute.getValue();
/*     */ 
/* 287 */     if (name == null) {
/* 288 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 294 */     if (name.equals("threshold")) {
/* 295 */       Level l = OptionConverter.toLevel((String)value, this.hierarchy.getThreshold());
/*     */ 
/* 297 */       this.hierarchy.setThreshold(l);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.jmx.HierarchyDynamicMBean
 * JD-Core Version:    0.6.2
 */