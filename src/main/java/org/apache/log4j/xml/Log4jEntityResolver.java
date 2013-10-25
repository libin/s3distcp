/*    */ package org.apache.log4j.xml;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.InputStream;
/*    */ import org.apache.log4j.helpers.LogLog;
/*    */ import org.xml.sax.EntityResolver;
/*    */ import org.xml.sax.InputSource;
/*    */ 
/*    */ public class Log4jEntityResolver
/*    */   implements EntityResolver
/*    */ {
/*    */   private static final String PUBLIC_ID = "-//APACHE//DTD LOG4J 1.2//EN";
/*    */ 
/*    */   public InputSource resolveEntity(String publicId, String systemId)
/*    */   {
/* 38 */     if ((systemId.endsWith("log4j.dtd")) || ("-//APACHE//DTD LOG4J 1.2//EN".equals(publicId))) {
/* 39 */       Class clazz = getClass();
/* 40 */       InputStream in = clazz.getResourceAsStream("/org/apache/log4j/xml/log4j.dtd");
/* 41 */       if (in == null) {
/* 42 */         LogLog.warn("Could not find [log4j.dtd] using [" + clazz.getClassLoader() + "] class loader, parsed without DTD.");
/*    */ 
/* 44 */         in = new ByteArrayInputStream(new byte[0]);
/*    */       }
/* 46 */       return new InputSource(in);
/*    */     }
/* 48 */     return null;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.xml.Log4jEntityResolver
 * JD-Core Version:    0.6.2
 */