/*     */ package com.amazonaws.auth;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.Request;
/*     */ import com.amazonaws.http.HttpMethodName;
/*     */ import com.amazonaws.util.DateUtils;
/*     */ import com.amazonaws.util.HttpUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URI;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import java.util.UUID;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class AWS3Signer extends AbstractAWSSigner
/*     */ {
/*     */   private static final String AUTHORIZATION_HEADER = "X-Amzn-Authorization";
/*     */   private static final String NONCE_HEADER = "x-amz-nonce";
/*     */   private static final String HTTP_SCHEME = "AWS3";
/*     */   private static final String HTTPS_SCHEME = "AWS3-HTTPS";
/*     */   private String overriddenDate;
/*  49 */   protected static final DateUtils dateUtils = new DateUtils();
/*  50 */   private static final Log log = LogFactory.getLog(AWS3Signer.class);
/*     */ 
/*     */   public void sign(Request<?> request, AWSCredentials credentials)
/*     */     throws AmazonClientException
/*     */   {
/*  63 */     if ((credentials instanceof AnonymousAWSCredentials)) {
/*  64 */       return;
/*     */     }
/*     */ 
/*  67 */     AWSCredentials sanitizedCredentials = sanitizeCredentials(credentials);
/*     */ 
/*  69 */     SigningAlgorithm algorithm = SigningAlgorithm.HmacSHA256;
/*  70 */     String nonce = UUID.randomUUID().toString();
/*  71 */     String date = dateUtils.formatRfc822Date(new Date());
/*  72 */     boolean isHttps = false;
/*     */ 
/*  74 */     if (this.overriddenDate != null) date = this.overriddenDate;
/*  75 */     request.addHeader("Date", date);
/*  76 */     request.addHeader("X-Amz-Date", date);
/*     */ 
/*  80 */     String hostHeader = request.getEndpoint().getHost();
/*  81 */     if (HttpUtils.isUsingNonDefaultPort(request.getEndpoint())) {
/*  82 */       hostHeader = new StringBuilder().append(hostHeader).append(":").append(request.getEndpoint().getPort()).toString();
/*     */     }
/*  84 */     request.addHeader("Host", hostHeader);
/*     */ 
/*  86 */     if ((sanitizedCredentials instanceof AWSSessionCredentials))
/*  87 */       addSessionCredentials(request, (AWSSessionCredentials)sanitizedCredentials);
/*     */     byte[] bytesToSign;
/*     */     String stringToSign;
/*  91 */     if (isHttps) {
/*  92 */       request.addHeader("x-amz-nonce", nonce);
/*  93 */       String stringToSign = new StringBuilder().append(date).append(nonce).toString();
/*     */       try {
/*  95 */         bytesToSign = stringToSign.getBytes("UTF-8");
/*     */       } catch (UnsupportedEncodingException e) {
/*  97 */         throw new AmazonClientException(new StringBuilder().append("Unable to serialize string to bytes: ").append(e.getMessage()).toString(), e);
/*     */       }
/*     */ 
/*     */     }
/*     */     else
/*     */     {
/* 106 */       stringToSign = new StringBuilder().append(request.getHttpMethod().toString()).append("\n").append(getCanonicalizedResourcePath(request.getResourcePath())).append("\n").append(getCanonicalizedQueryString(request.getParameters())).append("\n").append(getCanonicalizedHeadersForStringToSign(request)).append("\n").append(getRequestPayloadWithoutQueryParams(request)).toString();
/*     */ 
/* 111 */       bytesToSign = hash(stringToSign);
/*     */     }
/* 113 */     log.debug(new StringBuilder().append("Calculated StringToSign: ").append(stringToSign).toString());
/*     */ 
/* 115 */     String signature = signAndBase64Encode(bytesToSign, sanitizedCredentials.getAWSSecretKey(), algorithm);
/*     */ 
/* 117 */     StringBuilder builder = new StringBuilder();
/* 118 */     builder.append(isHttps ? "AWS3-HTTPS" : "AWS3").append(" ");
/* 119 */     builder.append(new StringBuilder().append("AWSAccessKeyId=").append(sanitizedCredentials.getAWSAccessKeyId()).append(",").toString());
/* 120 */     builder.append(new StringBuilder().append("Algorithm=").append(algorithm.toString()).append(",").toString());
/*     */ 
/* 122 */     if (!isHttps) {
/* 123 */       builder.append(new StringBuilder().append(getSignedHeadersComponent(request)).append(",").toString());
/*     */     }
/*     */ 
/* 126 */     builder.append(new StringBuilder().append("Signature=").append(signature).toString());
/* 127 */     request.addHeader("X-Amzn-Authorization", builder.toString());
/*     */   }
/*     */ 
/*     */   private String getSignedHeadersComponent(Request<?> request) {
/* 131 */     StringBuilder builder = new StringBuilder();
/* 132 */     builder.append("SignedHeaders=");
/* 133 */     boolean first = true;
/* 134 */     for (String header : getHeadersForStringToSign(request)) {
/* 135 */       if (!first) builder.append(";");
/* 136 */       builder.append(header);
/* 137 */       first = false;
/*     */     }
/* 139 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   protected List<String> getHeadersForStringToSign(Request<?> request) {
/* 143 */     List headersToSign = new ArrayList();
/* 144 */     for (Map.Entry entry : request.getHeaders().entrySet()) {
/* 145 */       String key = (String)entry.getKey();
/* 146 */       String lowerCaseKey = key.toLowerCase();
/* 147 */       if ((lowerCaseKey.startsWith("x-amz")) || (lowerCaseKey.equals("host")))
/*     */       {
/* 149 */         headersToSign.add(key);
/*     */       }
/*     */     }
/*     */ 
/* 153 */     Collections.sort(headersToSign);
/* 154 */     return headersToSign;
/*     */   }
/*     */ 
/*     */   void overrideDate(String date)
/*     */   {
/* 165 */     this.overriddenDate = date;
/*     */   }
/*     */ 
/*     */   protected String getCanonicalizedHeadersForStringToSign(Request<?> request) {
/* 169 */     List headersToSign = getHeadersForStringToSign(request);
/*     */ 
/* 171 */     for (int i = 0; i < headersToSign.size(); i++) {
/* 172 */       headersToSign.set(i, ((String)headersToSign.get(i)).toLowerCase());
/*     */     }
/*     */ 
/* 175 */     SortedMap sortedHeaderMap = new TreeMap();
/* 176 */     for (Map.Entry entry : request.getHeaders().entrySet()) {
/* 177 */       if (headersToSign.contains(((String)entry.getKey()).toLowerCase())) {
/* 178 */         sortedHeaderMap.put(((String)entry.getKey()).toLowerCase(), entry.getValue());
/*     */       }
/*     */     }
/*     */ 
/* 182 */     StringBuilder builder = new StringBuilder();
/* 183 */     for (Map.Entry entry : sortedHeaderMap.entrySet()) {
/* 184 */       builder.append(((String)entry.getKey()).toLowerCase()).append(":").append((String)entry.getValue()).append("\n");
/*     */     }
/*     */ 
/* 188 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   protected boolean shouldUseHttpsScheme(Request<?> request) throws AmazonClientException {
/*     */     try {
/* 193 */       String protocol = request.getEndpoint().toURL().getProtocol().toLowerCase();
/* 194 */       if (protocol.equals("http"))
/* 195 */         return false;
/* 196 */       if (protocol.equals("https")) {
/* 197 */         return true;
/*     */       }
/* 199 */       throw new AmazonClientException(new StringBuilder().append("Unknown request endpoint protocol encountered while signing request: ").append(protocol).toString());
/*     */     }
/*     */     catch (MalformedURLException e)
/*     */     {
/* 203 */       throw new AmazonClientException("Unable to parse request endpoint during signing", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void addSessionCredentials(Request<?> request, AWSSessionCredentials credentials)
/*     */   {
/* 209 */     request.addHeader("x-amz-security-token", credentials.getSessionToken());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AWS3Signer
 * JD-Core Version:    0.6.2
 */