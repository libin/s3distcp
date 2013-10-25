/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.Properties;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class VersionInfoUtils
/*     */ {
/*     */   private static final String VERSION_INFO_FILE = "com/amazonaws/sdk/versionInfo.properties";
/*  32 */   private static String version = null;
/*     */ 
/*  35 */   private static String platform = null;
/*     */ 
/*  38 */   private static String userAgent = null;
/*     */ 
/*  41 */   private static Log log = LogFactory.getLog(VersionInfoUtils.class);
/*     */ 
/*     */   public static String getVersion()
/*     */   {
/*  54 */     if (version == null) {
/*  55 */       initializeVersion();
/*     */     }
/*     */ 
/*  58 */     return version;
/*     */   }
/*     */ 
/*     */   public static String getPlatform()
/*     */   {
/*  72 */     if (platform == null) {
/*  73 */       initializeVersion();
/*     */     }
/*     */ 
/*  76 */     return platform;
/*     */   }
/*     */ 
/*     */   public static String getUserAgent()
/*     */   {
/*  85 */     if (userAgent == null) {
/*  86 */       initializeUserAgent();
/*     */     }
/*     */ 
/*  89 */     return userAgent;
/*     */   }
/*     */ 
/*     */   private static void initializeVersion()
/*     */   {
/*  98 */     InputStream inputStream = VersionInfoUtils.class.getClassLoader().getResourceAsStream("com/amazonaws/sdk/versionInfo.properties");
/*  99 */     Properties versionInfoProperties = new Properties();
/*     */     try {
/* 101 */       if (inputStream == null) {
/* 102 */         throw new Exception("com/amazonaws/sdk/versionInfo.properties not found on classpath");
/*     */       }
/* 104 */       versionInfoProperties.load(inputStream);
/* 105 */       version = versionInfoProperties.getProperty("version");
/* 106 */       platform = versionInfoProperties.getProperty("platform");
/*     */     } catch (Exception e) {
/* 108 */       log.info(new StringBuilder().append("Unable to load version information for the running SDK: ").append(e.getMessage()).toString());
/* 109 */       version = "unknown-version";
/* 110 */       platform = "java";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void initializeUserAgent()
/*     */   {
/* 120 */     StringBuilder buffer = new StringBuilder(1024);
/* 121 */     buffer.append(new StringBuilder().append("aws-sdk-").append(getPlatform().toLowerCase()).append("/").toString());
/* 122 */     buffer.append(getVersion());
/* 123 */     buffer.append(" ");
/* 124 */     buffer.append(new StringBuilder().append(System.getProperty("os.name").replace(' ', '_')).append("/").append(System.getProperty("os.version").replace(' ', '_')).toString());
/* 125 */     buffer.append(" ");
/* 126 */     buffer.append(new StringBuilder().append(System.getProperty("java.vm.name").replace(' ', '_')).append("/").append(System.getProperty("java.vm.version").replace(' ', '_')).toString());
/*     */ 
/* 128 */     String region = "";
/*     */     try {
/* 130 */       region = new StringBuilder().append(" ").append(System.getProperty("user.language").replace(' ', '_')).append("_").append(System.getProperty("user.region").replace(' ', '_')).toString();
/*     */     }
/*     */     catch (Exception exception)
/*     */     {
/*     */     }
/* 135 */     buffer.append(region);
/*     */ 
/* 137 */     userAgent = buffer.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.VersionInfoUtils
 * JD-Core Version:    0.6.2
 */