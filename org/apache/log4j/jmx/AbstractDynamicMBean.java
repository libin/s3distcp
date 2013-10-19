/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Vector;
/*     */ import javax.management.Attribute;
/*     */ import javax.management.AttributeList;
/*     */ import javax.management.DynamicMBean;
/*     */ import javax.management.InstanceAlreadyExistsException;
/*     */ import javax.management.InstanceNotFoundException;
/*     */ import javax.management.JMException;
/*     */ import javax.management.MBeanRegistration;
/*     */ import javax.management.MBeanRegistrationException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.NotCompliantMBeanException;
/*     */ import javax.management.ObjectName;
/*     */ import javax.management.RuntimeOperationsException;
/*     */ import org.apache.log4j.Appender;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public abstract class AbstractDynamicMBean
/*     */   implements DynamicMBean, MBeanRegistration
/*     */ {
/*     */   String dClassName;
/*     */   MBeanServer server;
/*  45 */   private final Vector mbeanList = new Vector();
/*     */ 
/*     */   protected static String getAppenderName(Appender appender)
/*     */   {
/*  54 */     String name = appender.getName();
/*  55 */     if ((name == null) || (name.trim().length() == 0))
/*     */     {
/*  57 */       name = appender.toString();
/*     */     }
/*  59 */     return name;
/*     */   }
/*     */ 
/*     */   public AttributeList getAttributes(String[] attributeNames)
/*     */   {
/*  70 */     if (attributeNames == null) {
/*  71 */       throw new RuntimeOperationsException(new IllegalArgumentException("attributeNames[] cannot be null"), "Cannot invoke a getter of " + this.dClassName);
/*     */     }
/*     */ 
/*  76 */     AttributeList resultList = new AttributeList();
/*     */ 
/*  79 */     if (attributeNames.length == 0) {
/*  80 */       return resultList;
/*     */     }
/*     */ 
/*  83 */     for (int i = 0; i < attributeNames.length; i++) {
/*     */       try {
/*  85 */         Object value = getAttribute(attributeNames[i]);
/*  86 */         resultList.add(new Attribute(attributeNames[i], value));
/*     */       } catch (JMException e) {
/*  88 */         e.printStackTrace();
/*     */       } catch (RuntimeException e) {
/*  90 */         e.printStackTrace();
/*     */       }
/*     */     }
/*  93 */     return resultList;
/*     */   }
/*     */ 
/*     */   public AttributeList setAttributes(AttributeList attributes)
/*     */   {
/* 103 */     if (attributes == null) {
/* 104 */       throw new RuntimeOperationsException(new IllegalArgumentException("AttributeList attributes cannot be null"), "Cannot invoke a setter of " + this.dClassName);
/*     */     }
/*     */ 
/* 108 */     AttributeList resultList = new AttributeList();
/*     */ 
/* 111 */     if (attributes.isEmpty()) {
/* 112 */       return resultList;
/*     */     }
/*     */ 
/* 115 */     for (Iterator i = attributes.iterator(); i.hasNext(); ) {
/* 116 */       Attribute attr = (Attribute)i.next();
/*     */       try {
/* 118 */         setAttribute(attr);
/* 119 */         String name = attr.getName();
/* 120 */         Object value = getAttribute(name);
/* 121 */         resultList.add(new Attribute(name, value));
/*     */       } catch (JMException e) {
/* 123 */         e.printStackTrace();
/*     */       } catch (RuntimeException e) {
/* 125 */         e.printStackTrace();
/*     */       }
/*     */     }
/* 128 */     return resultList;
/*     */   }
/*     */ 
/*     */   protected abstract Logger getLogger();
/*     */ 
/*     */   public void postDeregister()
/*     */   {
/* 137 */     getLogger().debug("postDeregister is called.");
/*     */   }
/*     */ 
/*     */   public void postRegister(Boolean registrationDone)
/*     */   {
/*     */   }
/*     */ 
/*     */   public ObjectName preRegister(MBeanServer server, ObjectName name)
/*     */   {
/* 148 */     getLogger().debug("preRegister called. Server=" + server + ", name=" + name);
/* 149 */     this.server = server;
/* 150 */     return name;
/*     */   }
/*     */ 
/*     */   protected void registerMBean(Object mbean, ObjectName objectName)
/*     */     throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
/*     */   {
/* 160 */     this.server.registerMBean(mbean, objectName);
/* 161 */     this.mbeanList.add(objectName);
/*     */   }
/*     */ 
/*     */   public void preDeregister()
/*     */   {
/* 171 */     getLogger().debug("preDeregister called.");
/*     */ 
/* 173 */     Enumeration iterator = this.mbeanList.elements();
/* 174 */     while (iterator.hasMoreElements()) {
/* 175 */       ObjectName name = (ObjectName)iterator.nextElement();
/*     */       try {
/* 177 */         this.server.unregisterMBean(name);
/*     */       } catch (InstanceNotFoundException e) {
/* 179 */         getLogger().warn("Missing MBean " + name.getCanonicalName());
/*     */       } catch (MBeanRegistrationException e) {
/* 181 */         getLogger().warn("Failed unregistering " + name.getCanonicalName());
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.jmx.AbstractDynamicMBean
 * JD-Core Version:    0.6.2
 */