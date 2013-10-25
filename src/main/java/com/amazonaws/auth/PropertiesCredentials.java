/*     */ package com.amazonaws.auth;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class PropertiesCredentials
/*     */   implements AWSCredentials
/*     */ {
/*     */   private final String accessKey;
/*     */   private final String secretAccessKey;
/*     */ 
/*     */   public PropertiesCredentials(File file)
/*     */     throws FileNotFoundException, IOException, IllegalArgumentException
/*     */   {
/*  55 */     if (!file.exists()) {
/*  56 */       throw new FileNotFoundException("File doesn't exist:  " + file.getAbsolutePath());
/*     */     }
/*     */ 
/*  59 */     Properties accountProperties = new Properties();
/*  60 */     accountProperties.load(new FileInputStream(file));
/*     */ 
/*  62 */     if ((accountProperties.getProperty("accessKey") == null) || (accountProperties.getProperty("secretKey") == null))
/*     */     {
/*  64 */       throw new IllegalArgumentException("The specified file (" + file.getAbsolutePath() + ") " + "doesn't contain the expected properties 'accessKey' and 'secretKey'.");
/*     */     }
/*     */ 
/*  68 */     this.accessKey = accountProperties.getProperty("accessKey");
/*  69 */     this.secretAccessKey = accountProperties.getProperty("secretKey");
/*     */   }
/*     */ 
/*     */   public PropertiesCredentials(InputStream inputStream)
/*     */     throws IOException
/*     */   {
/*  84 */     Properties accountProperties = new Properties();
/*     */     try {
/*  86 */       accountProperties.load(inputStream); } finally {
/*     */       try {
/*  88 */         inputStream.close(); } catch (Exception e) {
/*     */       }
/*     */     }
/*  91 */     if ((accountProperties.getProperty("accessKey") == null) || (accountProperties.getProperty("secretKey") == null))
/*     */     {
/*  93 */       throw new IllegalArgumentException("The specified properties data doesn't contain the expected properties 'accessKey' and 'secretKey'.");
/*     */     }
/*     */ 
/*  97 */     this.accessKey = accountProperties.getProperty("accessKey");
/*  98 */     this.secretAccessKey = accountProperties.getProperty("secretKey");
/*     */   }
/*     */ 
/*     */   public String getAWSAccessKeyId()
/*     */   {
/* 105 */     return this.accessKey;
/*     */   }
/*     */ 
/*     */   public String getAWSSecretKey()
/*     */   {
/* 112 */     return this.secretAccessKey;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.PropertiesCredentials
 * JD-Core Version:    0.6.2
 */