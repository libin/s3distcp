/*     */ package org.apache.log4j.lf5;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URL;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ import org.apache.log4j.spi.Configurator;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ 
/*     */ public class DefaultLF5Configurator
/*     */   implements Configurator
/*     */ {
/*     */   public static void configure()
/*     */     throws IOException
/*     */   {
/*  79 */     String resource = "/org/apache/log4j/lf5/config/defaultconfig.properties";
/*     */ 
/*  81 */     URL configFileResource = DefaultLF5Configurator.class.getResource(resource);
/*     */ 
/*  84 */     if (configFileResource != null)
/*  85 */       PropertyConfigurator.configure(configFileResource);
/*     */     else
/*  87 */       throw new IOException("Error: Unable to open the resource" + resource);
/*     */   }
/*     */ 
/*     */   public void doConfigure(InputStream inputStream, LoggerRepository repository)
/*     */   {
/* 100 */     throw new IllegalStateException("This class should NOT be instantiated!");
/*     */   }
/*     */ 
/*     */   public void doConfigure(URL configURL, LoggerRepository repository)
/*     */   {
/* 108 */     throw new IllegalStateException("This class should NOT be instantiated!");
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.lf5.DefaultLF5Configurator
 * JD-Core Version:    0.6.2
 */