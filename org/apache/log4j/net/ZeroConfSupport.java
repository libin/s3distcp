/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.lang.reflect.Constructor;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Map;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ public class ZeroConfSupport
/*     */ {
/*  29 */   private static Object jmDNS = initializeJMDNS();
/*     */   Object serviceInfo;
/*     */   private static Class jmDNSClass;
/*     */   private static Class serviceInfoClass;
/*     */ 
/*     */   public ZeroConfSupport(String zone, int port, String name, Map properties)
/*     */   {
/*  37 */     boolean isVersion3 = false;
/*     */     try
/*     */     {
/*  40 */       jmDNSClass.getMethod("create", null);
/*  41 */       isVersion3 = true;
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/*     */     }
/*  46 */     if (isVersion3) {
/*  47 */       LogLog.debug("using JmDNS version 3 to construct serviceInfo instance");
/*  48 */       this.serviceInfo = buildServiceInfoVersion3(zone, port, name, properties);
/*     */     } else {
/*  50 */       LogLog.debug("using JmDNS version 1.0 to construct serviceInfo instance");
/*  51 */       this.serviceInfo = buildServiceInfoVersion1(zone, port, name, properties);
/*     */     }
/*     */   }
/*     */ 
/*     */   public ZeroConfSupport(String zone, int port, String name) {
/*  56 */     this(zone, port, name, new HashMap());
/*     */   }
/*     */ 
/*     */   private static Object createJmDNSVersion1()
/*     */   {
/*     */     try {
/*  62 */       return jmDNSClass.newInstance();
/*     */     } catch (InstantiationException e) {
/*  64 */       LogLog.warn("Unable to instantiate JMDNS", e);
/*     */     } catch (IllegalAccessException e) {
/*  66 */       LogLog.warn("Unable to instantiate JMDNS", e);
/*     */     }
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   private static Object createJmDNSVersion3()
/*     */   {
/*     */     try {
/*  74 */       Method jmDNSCreateMethod = jmDNSClass.getMethod("create", null);
/*  75 */       return jmDNSCreateMethod.invoke(null, null);
/*     */     } catch (IllegalAccessException e) {
/*  77 */       LogLog.warn("Unable to instantiate jmdns class", e);
/*     */     } catch (NoSuchMethodException e) {
/*  79 */       LogLog.warn("Unable to access constructor", e);
/*     */     } catch (InvocationTargetException e) {
/*  81 */       LogLog.warn("Unable to call constructor", e);
/*     */     }
/*  83 */     return null;
/*     */   }
/*     */ 
/*     */   private Object buildServiceInfoVersion1(String zone, int port, String name, Map properties)
/*     */   {
/*  88 */     Hashtable hashtableProperties = new Hashtable(properties);
/*     */     try {
/*  90 */       Class[] args = new Class[6];
/*  91 */       args[0] = String.class;
/*  92 */       args[1] = String.class;
/*  93 */       args[2] = Integer.TYPE;
/*  94 */       args[3] = Integer.TYPE;
/*  95 */       args[4] = Integer.TYPE;
/*  96 */       args[5] = Hashtable.class;
/*  97 */       Constructor constructor = serviceInfoClass.getConstructor(args);
/*  98 */       Object[] values = new Object[6];
/*  99 */       values[0] = zone;
/* 100 */       values[1] = name;
/* 101 */       values[2] = new Integer(port);
/* 102 */       values[3] = new Integer(0);
/* 103 */       values[4] = new Integer(0);
/* 104 */       values[5] = hashtableProperties;
/* 105 */       Object result = constructor.newInstance(values);
/* 106 */       LogLog.debug("created serviceinfo: " + result);
/* 107 */       return result;
/*     */     } catch (IllegalAccessException e) {
/* 109 */       LogLog.warn("Unable to construct ServiceInfo instance", e);
/*     */     } catch (NoSuchMethodException e) {
/* 111 */       LogLog.warn("Unable to get ServiceInfo constructor", e);
/*     */     } catch (InstantiationException e) {
/* 113 */       LogLog.warn("Unable to construct ServiceInfo instance", e);
/*     */     } catch (InvocationTargetException e) {
/* 115 */       LogLog.warn("Unable to construct ServiceInfo instance", e);
/*     */     }
/* 117 */     return null;
/*     */   }
/*     */ 
/*     */   private Object buildServiceInfoVersion3(String zone, int port, String name, Map properties) {
/*     */     try {
/* 122 */       Class[] args = new Class[6];
/* 123 */       args[0] = String.class;
/* 124 */       args[1] = String.class;
/* 125 */       args[2] = Integer.TYPE;
/* 126 */       args[3] = Integer.TYPE;
/* 127 */       args[4] = Integer.TYPE;
/* 128 */       args[5] = Map.class;
/* 129 */       Method serviceInfoCreateMethod = serviceInfoClass.getMethod("create", args);
/* 130 */       Object[] values = new Object[6];
/* 131 */       values[0] = zone;
/* 132 */       values[1] = name;
/* 133 */       values[2] = new Integer(port);
/* 134 */       values[3] = new Integer(0);
/* 135 */       values[4] = new Integer(0);
/* 136 */       values[5] = properties;
/* 137 */       Object result = serviceInfoCreateMethod.invoke(null, values);
/* 138 */       LogLog.debug("created serviceinfo: " + result);
/* 139 */       return result;
/*     */     } catch (IllegalAccessException e) {
/* 141 */       LogLog.warn("Unable to invoke create method", e);
/*     */     } catch (NoSuchMethodException e) {
/* 143 */       LogLog.warn("Unable to find create method", e);
/*     */     } catch (InvocationTargetException e) {
/* 145 */       LogLog.warn("Unable to invoke create method", e);
/*     */     }
/* 147 */     return null;
/*     */   }
/*     */ 
/*     */   public void advertise() {
/*     */     try {
/* 152 */       Method method = jmDNSClass.getMethod("registerService", new Class[] { serviceInfoClass });
/* 153 */       method.invoke(jmDNS, new Object[] { this.serviceInfo });
/* 154 */       LogLog.debug("registered serviceInfo: " + this.serviceInfo);
/*     */     } catch (IllegalAccessException e) {
/* 156 */       LogLog.warn("Unable to invoke registerService method", e);
/*     */     } catch (NoSuchMethodException e) {
/* 158 */       LogLog.warn("No registerService method", e);
/*     */     } catch (InvocationTargetException e) {
/* 160 */       LogLog.warn("Unable to invoke registerService method", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void unadvertise() {
/*     */     try {
/* 166 */       Method method = jmDNSClass.getMethod("unregisterService", new Class[] { serviceInfoClass });
/* 167 */       method.invoke(jmDNS, new Object[] { this.serviceInfo });
/* 168 */       LogLog.debug("unregistered serviceInfo: " + this.serviceInfo);
/*     */     } catch (IllegalAccessException e) {
/* 170 */       LogLog.warn("Unable to invoke unregisterService method", e);
/*     */     } catch (NoSuchMethodException e) {
/* 172 */       LogLog.warn("No unregisterService method", e);
/*     */     } catch (InvocationTargetException e) {
/* 174 */       LogLog.warn("Unable to invoke unregisterService method", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Object initializeJMDNS() {
/*     */     try {
/* 180 */       jmDNSClass = Class.forName("javax.jmdns.JmDNS");
/* 181 */       serviceInfoClass = Class.forName("javax.jmdns.ServiceInfo");
/*     */     } catch (ClassNotFoundException e) {
/* 183 */       LogLog.warn("JmDNS or serviceInfo class not found", e);
/*     */     }
/*     */ 
/* 187 */     boolean isVersion3 = false;
/*     */     try
/*     */     {
/* 190 */       jmDNSClass.getMethod("create", null);
/* 191 */       isVersion3 = true;
/*     */     }
/*     */     catch (NoSuchMethodException e)
/*     */     {
/*     */     }
/* 196 */     if (isVersion3) {
/* 197 */       return createJmDNSVersion3();
/*     */     }
/* 199 */     return createJmDNSVersion1();
/*     */   }
/*     */ 
/*     */   public static Object getJMDNSInstance()
/*     */   {
/* 204 */     return jmDNS;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.ZeroConfSupport
 * JD-Core Version:    0.6.2
 */