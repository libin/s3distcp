/*     */ package org.apache.log4j.jmx;
/*     */ 
/*     */ import java.io.InterruptedIOException;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ import javax.management.JMException;
/*     */ import javax.management.MBeanServer;
/*     */ import javax.management.MBeanServerFactory;
/*     */ import javax.management.ObjectName;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ /** @deprecated */
/*     */ public class Agent
/*     */ {
/*     */ 
/*     */   /** @deprecated */
/*  45 */   static Logger log = Logger.getLogger(Agent.class);
/*     */ 
/*     */   private static Object createServer()
/*     */   {
/*  62 */     Object newInstance = null;
/*     */     try {
/*  64 */       newInstance = Class.forName("com.sun.jdmk.comm.HtmlAdapterServer").newInstance();
/*     */     }
/*     */     catch (ClassNotFoundException ex) {
/*  67 */       throw new RuntimeException(ex.toString());
/*     */     } catch (InstantiationException ex) {
/*  69 */       throw new RuntimeException(ex.toString());
/*     */     } catch (IllegalAccessException ex) {
/*  71 */       throw new RuntimeException(ex.toString());
/*     */     }
/*  73 */     return newInstance;
/*     */   }
/*     */ 
/*     */   private static void startServer(Object server)
/*     */   {
/*     */     try
/*     */     {
/*  84 */       server.getClass().getMethod("start", new Class[0]).invoke(server, new Object[0]);
/*     */     }
/*     */     catch (InvocationTargetException ex) {
/*  87 */       Throwable cause = ex.getTargetException();
/*  88 */       if ((cause instanceof RuntimeException))
/*  89 */         throw ((RuntimeException)cause);
/*  90 */       if (cause != null) {
/*  91 */         if (((cause instanceof InterruptedException)) || ((cause instanceof InterruptedIOException)))
/*     */         {
/*  93 */           Thread.currentThread().interrupt();
/*     */         }
/*  95 */         throw new RuntimeException(cause.toString());
/*     */       }
/*  97 */       throw new RuntimeException();
/*     */     }
/*     */     catch (NoSuchMethodException ex) {
/* 100 */       throw new RuntimeException(ex.toString());
/*     */     } catch (IllegalAccessException ex) {
/* 102 */       throw new RuntimeException(ex.toString());
/*     */     }
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void start()
/*     */   {
/* 113 */     MBeanServer server = MBeanServerFactory.createMBeanServer();
/* 114 */     Object html = createServer();
/*     */     try
/*     */     {
/* 117 */       log.info("Registering HtmlAdaptorServer instance.");
/* 118 */       server.registerMBean(html, new ObjectName("Adaptor:name=html,port=8082"));
/* 119 */       log.info("Registering HierarchyDynamicMBean instance.");
/* 120 */       HierarchyDynamicMBean hdm = new HierarchyDynamicMBean();
/* 121 */       server.registerMBean(hdm, new ObjectName("log4j:hiearchy=default"));
/*     */     } catch (JMException e) {
/* 123 */       log.error("Problem while registering MBeans instances.", e);
/* 124 */       return;
/*     */     } catch (RuntimeException e) {
/* 126 */       log.error("Problem while registering MBeans instances.", e);
/* 127 */       return;
/*     */     }
/* 129 */     startServer(html);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.jmx.Agent
 * JD-Core Version:    0.6.2
 */