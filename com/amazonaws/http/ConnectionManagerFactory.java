/*    */ package com.amazonaws.http;
/*    */ 
/*    */ import com.amazonaws.ClientConfiguration;
/*    */ import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
/*    */ import org.apache.http.params.HttpParams;
/*    */ 
/*    */ class ConnectionManagerFactory
/*    */ {
/*    */   public static ThreadSafeClientConnManager createThreadSafeClientConnManager(ClientConfiguration config, HttpParams httpClientParams)
/*    */   {
/* 26 */     ThreadSafeClientConnManager connectionManager = new ThreadSafeClientConnManager();
/* 27 */     connectionManager.setDefaultMaxPerRoute(config.getMaxConnections());
/* 28 */     connectionManager.setMaxTotal(config.getMaxConnections());
/*    */ 
/* 30 */     IdleConnectionReaper.registerConnectionManager(connectionManager);
/* 31 */     return connectionManager;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.ConnectionManagerFactory
 * JD-Core Version:    0.6.2
 */