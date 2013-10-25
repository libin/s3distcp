/*    */ package org.apache.log4j.lf5;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import org.apache.log4j.lf5.viewer.LogBrokerMonitor;
/*    */ 
/*    */ public class AppenderFinalizer
/*    */ {
/* 41 */   protected LogBrokerMonitor _defaultMonitor = null;
/*    */ 
/*    */   public AppenderFinalizer(LogBrokerMonitor defaultMonitor)
/*    */   {
/* 52 */     this._defaultMonitor = defaultMonitor;
/*    */   }
/*    */ 
/*    */   protected void finalize()
/*    */     throws Throwable
/*    */   {
/* 66 */     System.out.println("Disposing of the default LogBrokerMonitor instance");
/* 67 */     this._defaultMonitor.dispose();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.AppenderFinalizer
 * JD-Core Version:    0.6.2
 */