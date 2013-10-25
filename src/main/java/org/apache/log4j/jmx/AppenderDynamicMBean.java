/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeNotFoundException;
/*     */ import javax.management.InvalidAttributeValueException;
/*     */ import javax.management.JMException;
/*     */ import javax.management.MBeanAttributeInfo;
/*     */ import javax.management.MBeanConstructorInfo;
/*     */ import javax.management.MBeanException;
/*     */ import javax.management.MBeanInfo;
/*     */ import javax.management.MBeanNotificationInfo;
/*     */ import javax.management.MBeanOperationInfo;
/*     */ import javax.management.MBeanParameterInfo;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.MalformedObjectNameException;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.ReflectionException;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Layout;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.OptionHandler;
/*     */ 
/*     */ public class AppenderDynamicMBean extends AbstractDynamicMBean
/*     */ {
/*  57 */   private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
/*  58 */   private Vector dAttributes = new Vector();
/*  59 */   private String dClassName = getClass().getName();
/*     */ 
/*  61 */   private Hashtable dynamicProps = new Hashtable(5);
/*  62 */   private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[2];
/*  63 */   private String dDescription = "This MBean acts as a management facade for log4j appenders.";
/*     */ 
/*  67 */   private static Logger cat = Logger.getLogger(AppenderDynamicMBean.class);
/*     */   private Appender appender;
/*     */ 
/*     */   public AppenderDynamicMBean(Appender appender)
/*     */     throws IntrospectionException
/*     */   {
/*  73 */     this.appender = appender;
/*  74 */     buildDynamicMBeanInfo();
/*     */   }
/*     */ 
/*     */   private void buildDynamicMBeanInfo() throws IntrospectionException
/*     */   {
/*  79 */     Constructor[] constructors = getClass().getConstructors();
/*  80 */     this.dConstructors[0] = new MBeanConstructorInfo("AppenderDynamicMBean(): Constructs a AppenderDynamicMBean instance", constructors[0]);
/*     */ 
/*  85 */     BeanInfo bi = Introspector.getBeanInfo(this.appender.getClass());
/*  86 */     PropertyDescriptor[] pd = bi.getPropertyDescriptors();
/*     */ 
/*  88 */     int size = pd.length;
/*     */ 
/*  90 */     for (int i = 0; i < size; i++) {
/*  91 */       String name = pd[i].getName();
/*  92 */       Method readMethod = pd[i].getReadMethod();
/*  93 */       Method writeMethod = pd[i].getWriteMethod();
/*  94 */       if (readMethod != null) {
/*  95 */         Class returnClass = readMethod.getReturnType();
/*  96 */         if (isSupportedType(returnClass))
/*     */         {
/*     */           String returnClassName;
/*  98 */           if (returnClass.isAssignableFrom(Priority.class))
/*  99 */             returnClassName = "java.lang.String";
/*     */           else {
/* 101 */             returnClassName = returnClass.getName();
/*     */           }
/*     */ 
/* 104 */           this.dAttributes.add(new MBeanAttributeInfo(name, returnClassName, "Dynamic", true, writeMethod != null, false));
/*     */ 
/* 110 */           this.dynamicProps.put(name, new MethodUnion(readMethod, writeMethod));
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 115 */     MBeanParameterInfo[] params = new MBeanParameterInfo[0];
/*     */ 
/* 117 */     this.dOperations[0] = new MBeanOperationInfo("activateOptions", "activateOptions(): add an appender", params, "void", 1);
/*     */ 
/* 123 */     params = new MBeanParameterInfo[1];
/* 124 */     params[0] = new MBeanParameterInfo("layout class", "java.lang.String", "layout class");
/*     */ 
/* 127 */     this.dOperations[1] = new MBeanOperationInfo("setLayout", "setLayout(): add a layout", params, "void", 1);
/*     */   }
/*     */ 
/*     */   private boolean isSupportedType(Class clazz)
/*     */   {
/* 136 */     if (clazz.isPrimitive()) {
/* 137 */       return true;
/*     */     }
/*     */ 
/* 140 */     if (clazz == String.class) {
/* 141 */       return true;
/*     */     }
/*     */ 
/* 145 */     if (clazz.isAssignableFrom(Priority.class)) {
/* 146 */       return true;
/*     */     }
/*     */ 
/* 149 */     return false;
/*     */   }
/*     */ 
/*     */   public MBeanInfo getMBeanInfo()
/*     */   {
/* 158 */     cat.debug("getMBeanInfo called.");
/*     */ 
/* 160 */     MBeanAttributeInfo[] attribs = new MBeanAttributeInfo[this.dAttributes.size()];
/* 161 */     this.dAttributes.toArray(attribs);
/*     */ 
/* 163 */     return new MBeanInfo(this.dClassName, this.dDescription, attribs, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
/*     */   }
/*     */ 
/*     */   public Object invoke(String operationName, Object[] params, String[] signature)
/*     */     throws MBeanException, ReflectionException
/*     */   {
/* 176 */     if ((operationName.equals("activateOptions")) && ((this.appender instanceof OptionHandler)))
/*     */     {
/* 178 */       OptionHandler oh = (OptionHandler)this.appender;
/* 179 */       oh.activateOptions();
/* 180 */       return "Options activated.";
/* 181 */     }if (operationName.equals("setLayout")) {
/* 182 */       Layout layout = (Layout)OptionConverter.instantiateByClassName((String)params[0], Layout.class, null);
/*     */ 
/* 186 */       this.appender.setLayout(layout);
/* 187 */       registerLayoutMBean(layout);
/*     */     }
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */   void registerLayoutMBean(Layout layout) {
/* 193 */     if (layout == null) {
/* 194 */       return;
/*     */     }
/* 196 */     String name = getAppenderName(this.appender) + ",layout=" + layout.getClass().getName();
/* 197 */     cat.debug("Adding LayoutMBean:" + name);
/* 198 */     ObjectName objectName = null;
/*     */     try {
/* 200 */       LayoutDynamicMBean appenderMBean = new LayoutDynamicMBean(layout);
/* 201 */       objectName = new ObjectName("log4j:appender=" + name);
/* 202 */       if (!this.server.isRegistered(objectName)) {
/* 203 */         registerMBean(appenderMBean, objectName);
/* 204 */         this.dAttributes.add(new MBeanAttributeInfo("appender=" + name, "javax.management.ObjectName", "The " + name + " layout.", true, true, false));
/*     */       }
/*     */     }
/*     */     catch (JMException e)
/*     */     {
/* 209 */       cat.error("Could not add DynamicLayoutMBean for [" + name + "].", e);
/*     */     } catch (IntrospectionException e) {
/* 211 */       cat.error("Could not add DynamicLayoutMBean for [" + name + "].", e);
/*     */     } catch (RuntimeException e) {
/* 213 */       cat.error("Could not add DynamicLayoutMBean for [" + name + "].", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Logger getLogger()
/*     */   {
/* 219 */     return cat;
/*     */   }
/*     */ 
/*     */   public Object getAttribute(String attributeName)
/*     */     throws AttributeNotFoundException, MBeanException, ReflectionException
/*     */   {
/* 229 */     if (attributeName == null) {
/* 230 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke a getter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 235 */     cat.debug("getAttribute called with [" + attributeName + "].");
/* 236 */     if (attributeName.startsWith("appender=" + this.appender.getName() + ",layout")) {
/*     */       try {
/* 238 */         return new ObjectName("log4j:" + attributeName);
/*     */       } catch (MalformedObjectNameException e) {
/* 240 */         cat.error("attributeName", e);
/*     */       } catch (RuntimeException e) {
/* 242 */         cat.error("attributeName", e);
/*     */       }
/*     */     }
/*     */ 
/* 246 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(attributeName);
/*     */ 
/* 250 */     if ((mu != null) && (mu.readMethod != null)) {
/*     */       try {
/* 252 */         return mu.readMethod.invoke(this.appender, null);
/*     */       } catch (IllegalAccessException e) {
/* 254 */         return null;
/*     */       } catch (InvocationTargetException e) {
/* 256 */         if (((e.getTargetException() instanceof InterruptedException)) || ((e.getTargetException() instanceof InterruptedIOException)))
/*     */         {
/* 258 */           Thread.currentThread().interrupt();
/*     */         }
/* 260 */         return null;
/*     */       } catch (RuntimeException e) {
/* 262 */         return null;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 269 */     throw new AttributeNotFoundException("Cannot find " + attributeName + " attribute in " + this.dClassName);
/*     */   }
/*     */ 
/*     */   public void setAttribute(Attribute attribute)
/*     */     throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
/*     */   {
/* 282 */     if (attribute == null) {
/* 283 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute cannot be null"), "Cannot invoke a setter of " + this.dClassName + " with null attribute");
/*     */     }
/*     */ 
/* 288 */     String name = attribute.getName();
/* 289 */     Object value = attribute.getValue();
/*     */ 
/* 291 */     if (name == null) {
/* 292 */       throw new RuntimeOperationsException(new IllegalArgumentException("Attribute name cannot be null"), "Cannot invoke the setter of " + this.dClassName + " with null attribute name");
/*     */     }
/*     */ 
/* 300 */     MethodUnion mu = (MethodUnion)this.dynamicProps.get(name);
/*     */ 
/* 302 */     if ((mu != null) && (mu.writeMethod != null)) {
/* 303 */       Object[] o = new Object[1];
/*     */ 
/* 305 */       Class[] params = mu.writeMethod.getParameterTypes();
/* 306 */       if (params[0] == Priority.class) {
/* 307 */         value = OptionConverter.toLevel((String)value, (Level)getAttribute(name));
/*     */       }
/*     */ 
/* 310 */       o[0] = value;
/*     */       try
/*     */       {
/* 313 */         mu.writeMethod.invoke(this.appender, o);
/*     */       }
/*     */       catch (InvocationTargetException e) {
/* 316 */         if (((e.getTargetException() instanceof InterruptedException)) || ((e.getTargetException() instanceof InterruptedIOException)))
/*     */         {
/* 318 */           Thread.currentThread().interrupt();
/*     */         }
/* 320 */         cat.error("FIXME", e);
/*     */       } catch (IllegalAccessException e) {
/* 322 */         cat.error("FIXME", e);
/*     */       } catch (RuntimeException e) {
/* 324 */         cat.error("FIXME", e);
/*     */       }
/* 326 */     } else if (!name.endsWith(".layout"))
/*     */     {
/* 329 */       throw new AttributeNotFoundException("Attribute " + name + " not found in " + getClass().getName());
/*     */     }
/*     */   }
/*     */ 
/*     */   public ObjectName preRegister(MBeanServer server, ObjectName name)
/*     */   {
/* 337 */     cat.debug("preRegister called. Server=" + server + ", name=" + name);
/* 338 */     this.server = server;
/* 339 */     registerLayoutMBean(this.appender.getLayout());
/*     */ 
/* 341 */     return name;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.jmx.AppenderDynamicMBean
 * JD-Core Version:    0.6.2
 */