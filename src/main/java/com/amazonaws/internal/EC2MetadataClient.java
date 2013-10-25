/*     */ package com.amazonaws.internal;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.HttpURLConnection;
/*     */ import java.net.URL;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class EC2MetadataClient
/*     */ {
/*     */   public static final String EC2_METADATA_SERVICE_OVERRIDE = "com.amazonaws.sdk.ec2MetadataServiceEndpointOverride";
/*     */   private static final String EC2_METADATA_SERVICE_URL = "http://169.254.169.254";
/*     */   public static final String SECURITY_CREDENTIALS_RESOURCE = "/latest/meta-data/iam/security-credentials/";
/*  39 */   private static final Log log = LogFactory.getLog(EC2MetadataClient.class);
/*     */ 
/*     */   public String getDefaultCredentials()
/*     */     throws IOException
/*     */   {
/*  53 */     String securityCredentialsList = readResource("/latest/meta-data/iam/security-credentials/");
/*     */ 
/*  55 */     securityCredentialsList = securityCredentialsList.trim();
/*  56 */     String[] securityCredentials = securityCredentialsList.split("\n");
/*  57 */     if (securityCredentials.length == 0) return null;
/*     */ 
/*  59 */     String securityCredentialsName = securityCredentials[0];
/*     */ 
/*  61 */     return readResource(new StringBuilder().append("/latest/meta-data/iam/security-credentials/").append(securityCredentialsName).toString());
/*     */   }
/*     */ 
/*     */   private String readResource(String resourcePath)
/*     */     throws IOException
/*     */   {
/*  79 */     URL url = getEc2MetadataServiceUrlForResource(resourcePath);
/*  80 */     log.debug(new StringBuilder().append("Connecting to EC2 instance metadata service at URL: ").append(url.toString()).toString());
/*     */ 
/*  82 */     HttpURLConnection connection = (HttpURLConnection)url.openConnection();
/*  83 */     connection.setConnectTimeout(2000);
/*  84 */     connection.setRequestMethod("GET");
/*  85 */     connection.setDoOutput(true);
/*  86 */     connection.connect();
/*     */ 
/*  88 */     return readResponse(connection);
/*     */   }
/*     */ 
/*     */   private String readResponse(HttpURLConnection connection)
/*     */     throws IOException
/*     */   {
/* 106 */     InputStream inputStream = connection.getInputStream();
/*     */     try {
/* 109 */       StringBuilder buffer = new StringBuilder();
/*     */       int c;
/*     */       while (true) { c = inputStream.read();
/* 112 */         if (c == -1) break;
/* 113 */         buffer.append((char)c);
/*     */       }
/*     */ 
/* 116 */       return buffer.toString();
/*     */     } finally {
/* 118 */       inputStream.close();
/*     */     }
/*     */   }
/*     */ 
/*     */   private URL getEc2MetadataServiceUrlForResource(String resourcePath)
/*     */     throws IOException
/*     */   {
/* 136 */     String endpoint = "http://169.254.169.254";
/* 137 */     if (System.getProperty("com.amazonaws.sdk.ec2MetadataServiceEndpointOverride") != null) {
/* 138 */       endpoint = System.getProperty("com.amazonaws.sdk.ec2MetadataServiceEndpointOverride");
/*     */     }
/*     */ 
/* 141 */     return new URL(new StringBuilder().append(endpoint).append(resourcePath).toString());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.internal.EC2MetadataClient
 * JD-Core Version:    0.6.2
 */