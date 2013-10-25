/*     */ package com.amazonaws.auth;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.internal.EC2MetadataClient;
/*     */ import com.amazonaws.util.DateUtils;
/*     */ import com.amazonaws.util.json.JSONException;
/*     */ import com.amazonaws.util.json.JSONObject;
/*     */ import java.io.IOException;
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class InstanceProfileCredentialsProvider
/*     */   implements AWSCredentialsProvider
/*     */ {
/*     */   protected AWSCredentials credentials;
/*     */   protected Date credentialsExpiration;
/*     */ 
/*     */   public AWSCredentials getCredentials()
/*     */   {
/*  37 */     if (needsToLoadCredentials()) loadCredentials();
/*  38 */     if (expired()) {
/*  39 */       throw new AmazonClientException("The credentials received from the Amazon EC2 metadata service have expired");
/*     */     }
/*     */ 
/*  42 */     return this.credentials;
/*     */   }
/*     */ 
/*     */   public void refresh() {
/*  46 */     loadCredentials();
/*     */   }
/*     */ 
/*     */   protected boolean needsToLoadCredentials() {
/*  50 */     if (this.credentials == null) return true;
/*     */ 
/*  52 */     if (this.credentialsExpiration != null) {
/*  53 */       int thresholdInMilliseconds = 300000;
/*  54 */       boolean withinExpirationThreshold = this.credentialsExpiration.getTime() - System.currentTimeMillis() < thresholdInMilliseconds;
/*  55 */       if (withinExpirationThreshold) return true;
/*     */     }
/*     */ 
/*  58 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean expired() {
/*  62 */     if ((this.credentialsExpiration != null) && 
/*  63 */       (this.credentialsExpiration.getTime() < System.currentTimeMillis())) {
/*  64 */       return true;
/*     */     }
/*     */ 
/*  68 */     return false;
/*     */   }
/*     */ 
/*     */   private synchronized void loadCredentials() {
/*     */     try {
/*  73 */       String credentialsResponse = new EC2MetadataClient().getDefaultCredentials();
/*  74 */       JSONObject jsonObject = new JSONObject(credentialsResponse);
/*     */ 
/*  76 */       if (jsonObject.has("Token")) {
/*  77 */         this.credentials = new BasicSessionCredentials(jsonObject.getString("AccessKeyId"), jsonObject.getString("SecretAccessKey"), jsonObject.getString("Token"));
/*     */       }
/*     */       else
/*     */       {
/*  82 */         this.credentials = new BasicAWSCredentials(jsonObject.getString("AccessKeyId"), jsonObject.getString("SecretAccessKey"));
/*     */       }
/*     */ 
/*  87 */       if (jsonObject.has("Expiration"))
/*     */       {
/*  93 */         String expiration = jsonObject.getString("Expiration");
/*  94 */         expiration = expiration.replaceAll("\\+0000$", "Z");
/*     */ 
/*  96 */         this.credentialsExpiration = new DateUtils().parseIso8601Date(expiration);
/*     */       }
/*     */     } catch (IOException e) {
/*  99 */       throw new AmazonClientException("Unable to load credentials from Amazon EC2 metadata service", e);
/*     */     } catch (JSONException e) {
/* 101 */       throw new AmazonClientException("Unable to parse credentials from Amazon EC2 metadata service", e);
/*     */     } catch (ParseException e) {
/* 103 */       throw new AmazonClientException("Unable to parse credentials expiration date from Amazon EC2 metadata service", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 109 */     return getClass().getSimpleName();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.InstanceProfileCredentialsProvider
 * JD-Core Version:    0.6.2
 */