/*      */ package org.apache.log4j.xml;
/*      */ 
/*      */ import org.apache.log4j.LogManager;
/*      */ import org.apache.log4j.helpers.FileWatchdog;
/*      */ 
/*      */ class XMLWatchdog extends FileWatchdog
/*      */ {
/*      */   XMLWatchdog(String filename)
/*      */   {
/* 1117 */     super(filename);
/*      */   }
/*      */ 
/*      */   public void doOnChange()
/*      */   {
/* 1125 */     new DOMConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository());
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.xml.XMLWatchdog
 * JD-Core Version:    0.6.2
 */