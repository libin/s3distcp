/*     */ package com.amazonaws.auth;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.Request;
/*     */ import java.net.URI;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Date;
/*     */ import java.util.Map;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TimeZone;
/*     */ import java.util.TreeMap;
/*     */ 
/*     */ public class QueryStringSigner extends AbstractAWSSigner
/*     */   implements Signer
/*     */ {
/*     */   private Date overriddenDate;
/*     */ 
/*     */   public void sign(Request<?> request, AWSCredentials credentials)
/*     */     throws AmazonClientException
/*     */   {
/*  49 */     sign(request, SignatureVersion.V2, SigningAlgorithm.HmacSHA256, credentials);
/*     */   }
/*     */ 
/*     */   public void sign(Request<?> request, SignatureVersion version, SigningAlgorithm algorithm, AWSCredentials credentials)
/*     */     throws AmazonClientException
/*     */   {
/*  68 */     if ((credentials instanceof AnonymousAWSCredentials)) {
/*  69 */       return;
/*     */     }
/*     */ 
/*  72 */     AWSCredentials sanitizedCredentials = sanitizeCredentials(credentials);
/*  73 */     request.addParameter("AWSAccessKeyId", sanitizedCredentials.getAWSAccessKeyId());
/*  74 */     request.addParameter("SignatureVersion", version.toString());
/*  75 */     request.addParameter("Timestamp", getFormattedTimestamp());
/*     */ 
/*  77 */     if ((sanitizedCredentials instanceof AWSSessionCredentials)) {
/*  78 */       addSessionCredentials(request, (AWSSessionCredentials)sanitizedCredentials);
/*     */     }
/*     */ 
/*  81 */     String stringToSign = null;
/*  82 */     if (version.equals(SignatureVersion.V1)) {
/*  83 */       stringToSign = calculateStringToSignV1(request.getParameters());
/*  84 */     } else if (version.equals(SignatureVersion.V2)) {
/*  85 */       request.addParameter("SignatureMethod", algorithm.toString());
/*  86 */       stringToSign = calculateStringToSignV2(request);
/*     */     } else {
/*  88 */       throw new AmazonClientException("Invalid Signature Version specified");
/*     */     }
/*     */ 
/*  91 */     String signatureValue = signAndBase64Encode(stringToSign, sanitizedCredentials.getAWSSecretKey(), algorithm);
/*  92 */     request.addParameter("Signature", signatureValue);
/*     */   }
/*     */ 
/*     */   private String calculateStringToSignV1(Map<String, String> parameters)
/*     */   {
/* 104 */     StringBuilder data = new StringBuilder();
/* 105 */     SortedMap<String,String> sorted = new TreeMap(String.CASE_INSENSITIVE_ORDER);
/*     */ 
/* 107 */     sorted.putAll(parameters);
/*     */ 
/* 109 */     for (String key : sorted.keySet()) {
/* 110 */       data.append(key);
/* 111 */       data.append((String)sorted.get(key));
/*     */     }
/*     */ 
/* 114 */     return data.toString();
/*     */   }
/*     */ 
/*     */   private String calculateStringToSignV2(Request<?> request)
/*     */     throws AmazonClientException
/*     */   {
/* 129 */     URI endpoint = request.getEndpoint();
/* 130 */     Map parameters = request.getParameters();
/*     */ 
/* 132 */     StringBuilder data = new StringBuilder();
/* 133 */     data.append("POST").append("\n");
/* 134 */     data.append(getCanonicalizedEndpoint(endpoint)).append("\n");
/* 135 */     data.append(getCanonicalizedResourcePath(request)).append("\n");
/* 136 */     data.append(getCanonicalizedQueryString(parameters));
/* 137 */     return data.toString();
/*     */   }
/*     */ 
/*     */   private String getCanonicalizedResourcePath(Request<?> request) {
/* 141 */     String resourcePath = "";
/*     */ 
/* 143 */     if (request.getEndpoint().getPath() != null) {
/* 144 */       resourcePath = new StringBuilder().append(resourcePath).append(request.getEndpoint().getPath()).toString();
/*     */     }
/*     */ 
/* 147 */     if (request.getResourcePath() != null) {
/* 148 */       if ((resourcePath.length() > 0) && (!resourcePath.endsWith("/")) && (!request.getResourcePath().startsWith("/")))
/*     */       {
/* 151 */         resourcePath = new StringBuilder().append(resourcePath).append("/").toString();
/*     */       }
/*     */ 
/* 154 */       resourcePath = new StringBuilder().append(resourcePath).append(request.getResourcePath()).toString();
/*     */     }
/*     */ 
/* 157 */     if (!resourcePath.startsWith("/")) {
/* 158 */       resourcePath = new StringBuilder().append("/").append(resourcePath).toString();
/*     */     }
/*     */ 
/* 161 */     if (resourcePath.startsWith("//")) {
/* 162 */       resourcePath = resourcePath.substring(1);
/*     */     }
/*     */ 
/* 165 */     return resourcePath;
/*     */   }
/*     */ 
/*     */   private String getFormattedTimestamp()
/*     */   {
/* 172 */     SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
/*     */ 
/* 174 */     df.setTimeZone(TimeZone.getTimeZone("UTC"));
/*     */ 
/* 176 */     if (this.overriddenDate != null) {
/* 177 */       return df.format(this.overriddenDate);
/*     */     }
/* 179 */     return df.format(new Date());
/*     */   }
/*     */ 
/*     */   void overrideDate(Date date)
/*     */   {
/* 185 */     this.overriddenDate = date;
/*     */   }
/*     */ 
/*     */   protected void addSessionCredentials(Request<?> request, AWSSessionCredentials credentials)
/*     */   {
/* 190 */     request.addParameter("SecurityToken", credentials.getSessionToken());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.QueryStringSigner
 * JD-Core Version:    0.6.2
 */