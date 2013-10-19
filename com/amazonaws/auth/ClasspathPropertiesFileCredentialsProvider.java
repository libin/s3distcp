/*    */ package com.amazonaws.auth;
/*    */ 
/*    */ import com.amazonaws.AmazonClientException;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ 
/*    */ public class ClasspathPropertiesFileCredentialsProvider
/*    */   implements AWSCredentialsProvider
/*    */ {
/* 37 */   private static String DEFAULT_PROPERTIES_FILE = "AwsCredentials.properties";
/*    */   private final String credentialsFilePath;
/*    */ 
/*    */   public ClasspathPropertiesFileCredentialsProvider()
/*    */   {
/* 47 */     this(DEFAULT_PROPERTIES_FILE);
/*    */   }
/*    */ 
/*    */   public ClasspathPropertiesFileCredentialsProvider(String credentialsFilePath)
/*    */   {
/* 67 */     if (credentialsFilePath == null) {
/* 68 */       throw new IllegalArgumentException("Credentials file path cannot be null");
/*    */     }
/*    */ 
/* 71 */     if (!credentialsFilePath.startsWith("/"))
/* 72 */       this.credentialsFilePath = ("/" + credentialsFilePath);
/*    */     else
/* 74 */       this.credentialsFilePath = credentialsFilePath;
/*    */   }
/*    */ 
/*    */   public AWSCredentials getCredentials()
/*    */   {
/* 79 */     InputStream inputStream = getClass().getResourceAsStream(this.credentialsFilePath);
/* 80 */     if (inputStream == null) {
/* 81 */       throw new AmazonClientException("Unable to load AWS credentials from the " + this.credentialsFilePath + " file on the classpath");
/*    */     }
/*    */     try
/*    */     {
/* 85 */       return new PropertiesCredentials(inputStream);
/*    */     } catch (IOException e) {
/* 87 */       throw new AmazonClientException("Unable to load AWS credentials from the " + this.credentialsFilePath + " file on the classpath", e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void refresh() {
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 95 */     return getClass().getSimpleName() + "(" + this.credentialsFilePath + ")";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider
 * JD-Core Version:    0.6.2
 */