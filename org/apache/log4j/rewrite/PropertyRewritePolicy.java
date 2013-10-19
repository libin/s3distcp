/*    */ package org.apache.log4j.rewrite;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Iterator;
/*    */ import java.util.Map;
/*    */ import java.util.Map.Entry;
/*    */ import java.util.Set;
/*    */ import java.util.StringTokenizer;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.spi.LoggingEvent;
/*    */ 
/*    */ public class PropertyRewritePolicy
/*    */   implements RewritePolicy
/*    */ {
/* 38 */   private Map properties = Collections.EMPTY_MAP;
/*    */ 
/*    */   public void setProperties(String props)
/*    */   {
/* 50 */     Map hashTable = new HashMap();
/* 51 */     StringTokenizer pairs = new StringTokenizer(props, ",");
/* 52 */     while (pairs.hasMoreTokens()) {
/* 53 */       StringTokenizer entry = new StringTokenizer(pairs.nextToken(), "=");
/* 54 */       hashTable.put(entry.nextElement().toString().trim(), entry.nextElement().toString().trim());
/*    */     }
/* 56 */     synchronized (this) {
/* 57 */       this.properties = hashTable;
/*    */     }
/*    */   }
/*    */ 
/*    */   public LoggingEvent rewrite(LoggingEvent source)
/*    */   {
/* 65 */     if (!this.properties.isEmpty()) {
/* 66 */       Map rewriteProps = new HashMap(source.getProperties());
/* 67 */       Iterator iter = this.properties.entrySet().iterator();
/* 68 */       while (iter.hasNext())
/*    */       {
/* 70 */         Map.Entry entry = (Map.Entry)iter.next();
/* 71 */         if (!rewriteProps.containsKey(entry.getKey())) {
/* 72 */           rewriteProps.put(entry.getKey(), entry.getValue());
/*    */         }
/*    */       }
/*    */ 
/* 76 */       return new LoggingEvent(source.getFQNOfLoggerClass(), source.getLogger() != null ? source.getLogger() : Logger.getLogger(source.getLoggerName()), source.getTimeStamp(), source.getLevel(), source.getMessage(), source.getThreadName(), source.getThrowableInformation(), source.getNDC(), source.getLocationInformation(), rewriteProps);
/*    */     }
/*    */ 
/* 88 */     return source;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.rewrite.PropertyRewritePolicy
 * JD-Core Version:    0.6.2
 */