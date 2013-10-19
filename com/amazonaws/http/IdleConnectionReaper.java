/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ 
/*     */ public class IdleConnectionReaper extends Thread
/*     */ {
/*     */   private static final int PERIOD_MILLISECONDS = 60000;
/*  46 */   private static ArrayList<ClientConnectionManager> connectionManagers = new ArrayList();
/*     */   private static IdleConnectionReaper instance;
/*  52 */   static final Log log = LogFactory.getLog(IdleConnectionReaper.class);
/*     */ 
/*     */   private IdleConnectionReaper()
/*     */   {
/*  56 */     super("java-sdk-http-connection-reaper");
/*  57 */     setDaemon(true);
/*  58 */     start();
/*     */   }
/*     */ 
/*     */   public static synchronized void registerConnectionManager(ClientConnectionManager connectionManager) {
/*  62 */     if (instance == null) instance = new IdleConnectionReaper();
/*  63 */     connectionManagers.add(connectionManager);
/*     */   }
/*     */ 
/*     */   public static synchronized void removeConnectionManager(ClientConnectionManager connectionManager) {
/*  67 */     connectionManagers.remove(connectionManager);
/*     */   }
/*     */ 
/*     */   public void run()
/*     */   {
/*     */     try
/*     */     {
/*     */       while (true) {
/*  75 */         Thread.sleep(60000L);
/*     */ 
/*  81 */         List connectionManagers = null;
/*  82 */         synchronized (IdleConnectionReaper.class) {
/*  83 */           connectionManagers = (List)connectionManagers.clone();
/*     */         }
/*  85 */         for (ClientConnectionManager connectionManager : connectionManagers)
/*     */         {
/*     */           try
/*     */           {
/*  90 */             connectionManager.closeIdleConnections(60L, TimeUnit.SECONDS);
/*     */           } catch (Throwable t) {
/*  92 */             log.warn("Unable to close idle connections", t);
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
/*     */ 
/*     */   }
/*     */ 
/*     */   public static synchronized void shutdown()
/*     */   {
/* 112 */     if (instance != null) {
/* 113 */       instance.interrupt();
/* 114 */       connectionManagers.clear();
/* 115 */       instance = null;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.IdleConnectionReaper
 * JD-Core Version:    0.6.2
 */