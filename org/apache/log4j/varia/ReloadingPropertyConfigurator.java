/*    */ package org.apache.log4j.varia;
/*    */ 
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import org.apache.log4j.PropertyConfigurator;
/*    */ import org.apache.log4j.spi.Configurator;
/*    */ import org.apache.log4j.spi.LoggerRepository;
/*    */ 
/*    */ public class ReloadingPropertyConfigurator
/*    */   implements Configurator
/*    */ {
/* 29 */   PropertyConfigurator delegate = new PropertyConfigurator();
/*    */ 
/*    */   public void doConfigure(InputStream inputStream, LoggerRepository repository)
/*    */   {
/*    */   }
/*    */ 
/*    */   public void doConfigure(URL url, LoggerRepository repository)
/*    */   {
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.varia.ReloadingPropertyConfigurator
 * JD-Core Version:    0.6.2
 */