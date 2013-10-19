/*     */ package com.amazonaws.auth;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.Request;
/*     */ import com.amazonaws.http.HttpMethodName;
/*     */ import com.amazonaws.util.AwsHostNameUtils;
/*     */ import com.amazonaws.util.BinaryUtils;
/*     */ import com.amazonaws.util.HttpUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.URI;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.SimpleTimeZone;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class AWS4Signer extends AbstractAWSSigner
/*     */ {
/*     */   private static final String ALGORITHM = "AWS4-HMAC-SHA256";
/*     */   private static final String TERMINATOR = "aws4_request";
/*     */   private String serviceName;
/*     */   private String regionName;
/*     */   private Date overriddenDate;
/*  59 */   private static final Log log = LogFactory.getLog(AWS4Signer.class);
/*     */ 
/*     */   public void sign(Request<?> request, AWSCredentials credentials)
/*     */     throws AmazonClientException
/*     */   {
/*  67 */     if ((credentials instanceof AnonymousAWSCredentials)) {
/*  68 */       return;
/*     */     }
/*     */ 
/*  71 */     AWSCredentials sanitizedCredentials = sanitizeCredentials(credentials);
/*  72 */     if ((sanitizedCredentials instanceof AWSSessionCredentials)) {
/*  73 */       addSessionCredentials(request, (AWSSessionCredentials)sanitizedCredentials);
/*     */     }
/*     */ 
/*  76 */     SimpleDateFormat dateStampFormat = new SimpleDateFormat("yyyyMMdd");
/*  77 */     dateStampFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
/*     */ 
/*  79 */     SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
/*  80 */     dateTimeFormat.setTimeZone(new SimpleTimeZone(0, "UTC"));
/*     */ 
/*  82 */     String regionName = extractRegionName(request.getEndpoint());
/*  83 */     String serviceName = extractServiceName(request.getEndpoint());
/*     */ 
/*  87 */     String hostHeader = request.getEndpoint().getHost();
/*  88 */     if (HttpUtils.isUsingNonDefaultPort(request.getEndpoint())) {
/*  89 */       hostHeader = new StringBuilder().append(hostHeader).append(":").append(request.getEndpoint().getPort()).toString();
/*     */     }
/*  91 */     request.addHeader("Host", hostHeader);
/*     */ 
/*  93 */     Date date = new Date();
/*  94 */     if (this.overriddenDate != null) date = this.overriddenDate;
/*     */ 
/*  96 */     String dateTime = dateTimeFormat.format(date);
/*  97 */     String dateStamp = dateStampFormat.format(date);
/*     */ 
/*  99 */     InputStream payloadStream = getBinaryRequestPayloadStream(request);
/* 100 */     payloadStream.mark(-1);
/* 101 */     String contentSha256 = BinaryUtils.toHex(hash(payloadStream));
/*     */     try {
/* 103 */       payloadStream.reset();
/*     */     } catch (IOException e) {
/* 105 */       throw new AmazonClientException("Unable to reset stream after calculating AWS4 signature", e);
/*     */     }
/*     */ 
/* 108 */     request.addHeader("X-Amz-Date", dateTime);
/* 109 */     request.addHeader("x-amz-content-sha256", contentSha256);
/*     */ 
/* 111 */     String canonicalRequest = new StringBuilder().append(request.getHttpMethod().toString()).append("\n").append(super.getCanonicalizedResourcePath(request.getResourcePath())).append("\n").append(getCanonicalizedQueryString(request)).append("\n").append(getCanonicalizedHeaderString(request)).append("\n").append(getSignedHeadersString(request)).append("\n").append(contentSha256).toString();
/*     */ 
/* 119 */     log.debug(new StringBuilder().append("AWS4 Canonical Request: '\"").append(canonicalRequest).append("\"").toString());
/*     */ 
/* 121 */     String scope = new StringBuilder().append(dateStamp).append("/").append(regionName).append("/").append(serviceName).append("/").append("aws4_request").toString();
/* 122 */     String signingCredentials = new StringBuilder().append(sanitizedCredentials.getAWSAccessKeyId()).append("/").append(scope).toString();
/* 123 */     String stringToSign = new StringBuilder().append("AWS4-HMAC-SHA256\n").append(dateTime).append("\n").append(scope).append("\n").append(BinaryUtils.toHex(hash(canonicalRequest))).toString();
/*     */ 
/* 128 */     log.debug(new StringBuilder().append("AWS4 String to Sign: '\"").append(stringToSign).append("\"").toString());
/*     */ 
/* 131 */     byte[] kSecret = new StringBuilder().append("AWS4").append(sanitizedCredentials.getAWSSecretKey()).toString().getBytes();
/* 132 */     byte[] kDate = sign(dateStamp, kSecret, SigningAlgorithm.HmacSHA256);
/* 133 */     byte[] kRegion = sign(regionName, kDate, SigningAlgorithm.HmacSHA256);
/* 134 */     byte[] kService = sign(serviceName, kRegion, SigningAlgorithm.HmacSHA256);
/* 135 */     byte[] kSigning = sign("aws4_request", kService, SigningAlgorithm.HmacSHA256);
/*     */ 
/* 137 */     byte[] signature = sign(stringToSign.getBytes(), kSigning, SigningAlgorithm.HmacSHA256);
/*     */ 
/* 139 */     String credentialsAuthorizationHeader = new StringBuilder().append("Credential=").append(signingCredentials).toString();
/*     */ 
/* 141 */     String signedHeadersAuthorizationHeader = new StringBuilder().append("SignedHeaders=").append(getSignedHeadersString(request)).toString();
/*     */ 
/* 143 */     String signatureAuthorizationHeader = new StringBuilder().append("Signature=").append(BinaryUtils.toHex(signature)).toString();
/*     */ 
/* 146 */     String authorizationHeader = new StringBuilder().append("AWS4-HMAC-SHA256 ").append(credentialsAuthorizationHeader).append(", ").append(signedHeadersAuthorizationHeader).append(", ").append(signatureAuthorizationHeader).toString();
/*     */ 
/* 151 */     request.addHeader("Authorization", authorizationHeader);
/*     */   }
/*     */ 
/*     */   public void setServiceName(String serviceName)
/*     */   {
/* 165 */     this.serviceName = serviceName;
/*     */   }
/*     */ 
/*     */   public void setRegionName(String regionName)
/*     */   {
/* 179 */     this.regionName = regionName;
/*     */   }
/*     */ 
/*     */   protected void addSessionCredentials(Request<?> request, AWSSessionCredentials credentials)
/*     */   {
/* 184 */     request.addHeader("x-amz-security-token", credentials.getSessionToken());
/*     */   }
/*     */ 
/*     */   private String extractRegionName(URI endpoint) {
/* 188 */     if (this.regionName != null) return this.regionName;
/*     */ 
/* 190 */     return AwsHostNameUtils.parseRegionName(endpoint);
/*     */   }
/*     */ 
/*     */   private String extractServiceName(URI endpoint) {
/* 194 */     if (this.serviceName != null) return this.serviceName;
/*     */ 
/* 196 */     return AwsHostNameUtils.parseServiceName(endpoint);
/*     */   }
/*     */ 
/*     */   void overrideDate(Date overriddenDate)
/*     */   {
/* 201 */     this.overriddenDate = overriddenDate;
/*     */   }
/*     */ 
/*     */   private String getCanonicalizedHeaderString(Request<?> request) {
/* 205 */     List sortedHeaders = new ArrayList();
/* 206 */     sortedHeaders.addAll(request.getHeaders().keySet());
/* 207 */     Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);
/*     */ 
/* 209 */     StringBuilder buffer = new StringBuilder();
/* 210 */     for (String header : sortedHeaders) {
/* 211 */       buffer.append(new StringBuilder().append(header.toLowerCase().replaceAll("\\s+", " ")).append(":").append(((String)request.getHeaders().get(header)).replaceAll("\\s+", " ")).toString());
/* 212 */       buffer.append("\n");
/*     */     }
/*     */ 
/* 215 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   private String getSignedHeadersString(Request<?> request) {
/* 219 */     List sortedHeaders = new ArrayList();
/* 220 */     sortedHeaders.addAll(request.getHeaders().keySet());
/* 221 */     Collections.sort(sortedHeaders, String.CASE_INSENSITIVE_ORDER);
/*     */ 
/* 223 */     StringBuilder buffer = new StringBuilder();
/* 224 */     for (String header : sortedHeaders) {
/* 225 */       if (buffer.length() > 0) buffer.append(";");
/* 226 */       buffer.append(header.toLowerCase());
/*     */     }
/*     */ 
/* 229 */     return buffer.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AWS4Signer
 * JD-Core Version:    0.6.2
 */