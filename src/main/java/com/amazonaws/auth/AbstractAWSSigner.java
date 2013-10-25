/*     */ package com.amazonaws.auth;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.Request;
/*     */ import com.amazonaws.util.HttpUtils;
/*     */ import com.amazonaws.util.StringInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.security.DigestInputStream;
/*     */ import java.security.MessageDigest;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.SortedMap;
/*     */ import java.util.TreeMap;
/*     */ import javax.crypto.Mac;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ 
/*     */ public abstract class AbstractAWSSigner
/*     */   implements Signer
/*     */ {
/*     */   protected static final String DEFAULT_ENCODING = "UTF-8";
/*     */ 
/*     */   protected String signAndBase64Encode(String data, String key, SigningAlgorithm algorithm)
/*     */     throws AmazonClientException
/*     */   {
/*     */     try
/*     */     {
/*  58 */       return signAndBase64Encode(data.getBytes("UTF-8"), key, algorithm);
/*     */     } catch (UnsupportedEncodingException e) {
/*  60 */       throw new AmazonClientException(new StringBuilder().append("Unable to calculate a request signature: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String signAndBase64Encode(byte[] data, String key, SigningAlgorithm algorithm)
/*     */     throws AmazonClientException
/*     */   {
/*     */     try
/*     */     {
/*  71 */       byte[] signature = sign(data, key.getBytes("UTF-8"), algorithm);
/*  72 */       return new String(Base64.encodeBase64(signature));
/*     */     } catch (Exception e) {
/*  74 */       throw new AmazonClientException(new StringBuilder().append("Unable to calculate a request signature: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected byte[] sign(String stringData, byte[] key, SigningAlgorithm algorithm) throws AmazonClientException {
/*     */     try {
/*  80 */       byte[] data = stringData.getBytes("UTF-8");
/*  81 */       return sign(data, key, algorithm);
/*     */     } catch (Exception e) {
/*  83 */       throw new AmazonClientException(new StringBuilder().append("Unable to calculate a request signature: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected byte[] sign(byte[] data, byte[] key, SigningAlgorithm algorithm) throws AmazonClientException {
/*     */     try {
/*  89 */       Mac mac = Mac.getInstance(algorithm.toString());
/*  90 */       mac.init(new SecretKeySpec(key, algorithm.toString()));
/*  91 */       return mac.doFinal(data);
/*     */     } catch (Exception e) {
/*  93 */       throw new AmazonClientException(new StringBuilder().append("Unable to calculate a request signature: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected byte[] hash(String text)
/*     */     throws AmazonClientException
/*     */   {
/*     */     try
/*     */     {
/* 111 */       MessageDigest md = MessageDigest.getInstance("SHA-256");
/* 112 */       md.update(text.getBytes("UTF-8"));
/* 113 */       return md.digest();
/*     */     } catch (Exception e) {
/* 115 */       throw new AmazonClientException(new StringBuilder().append("Unable to compute hash while signing request: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected byte[] hash(InputStream input) throws AmazonClientException {
/*     */     try {
/* 121 */       MessageDigest md = MessageDigest.getInstance("SHA-256");
/* 122 */       DigestInputStream digestInputStream = new DigestInputStream(input, md);
/* 123 */       byte[] buffer = new byte[1024];
/* 124 */       while (digestInputStream.read(buffer) > -1);
/* 125 */       return digestInputStream.getMessageDigest().digest();
/*     */     } catch (Exception e) {
/* 127 */       throw new AmazonClientException(new StringBuilder().append("Unable to compute hash while signing request: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected byte[] hash(byte[] data)
/*     */     throws AmazonClientException
/*     */   {
/*     */     try
/*     */     {
/* 145 */       MessageDigest md = MessageDigest.getInstance("SHA-256");
/* 146 */       md.update(data);
/* 147 */       return md.digest();
/*     */     } catch (Exception e) {
/* 149 */       throw new AmazonClientException(new StringBuilder().append("Unable to compute hash while signing request: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getCanonicalizedQueryString(Map<String, String> parameters)
/*     */   {
/* 168 */     SortedMap sorted = new TreeMap();
/*     */ 
/* 170 */     Iterator pairs = parameters.entrySet().iterator();
/* 171 */     while (pairs.hasNext()) {
/* 172 */       Map.Entry pair = (Map.Entry)pairs.next();
/* 173 */       String key = (String)pair.getKey();
/* 174 */       String value = (String)pair.getValue();
/* 175 */       sorted.put(HttpUtils.urlEncode(key, false), HttpUtils.urlEncode(value, false));
/*     */     }
/*     */ 
/* 178 */     StringBuilder builder = new StringBuilder();
/* 179 */     pairs = sorted.entrySet().iterator();
/* 180 */     while (pairs.hasNext()) {
/* 181 */       Map.Entry pair = (Map.Entry)pairs.next();
/* 182 */       builder.append((String)pair.getKey());
/* 183 */       builder.append("=");
/* 184 */       builder.append((String)pair.getValue());
/* 185 */       if (pairs.hasNext()) {
/* 186 */         builder.append("&");
/*     */       }
/*     */     }
/*     */ 
/* 190 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   protected String getCanonicalizedQueryString(Request<?> request)
/*     */   {
/* 199 */     if (HttpUtils.usePayloadForQueryParameters(request)) return "";
/* 200 */     return getCanonicalizedQueryString(request.getParameters());
/*     */   }
/*     */ 
/*     */   protected byte[] getBinaryRequestPayload(Request<?> request)
/*     */   {
/* 211 */     if (HttpUtils.usePayloadForQueryParameters(request)) {
/* 212 */       String encodedParameters = HttpUtils.encodeParameters(request);
/* 213 */       if (encodedParameters == null) return new byte[0]; try
/*     */       {
/* 215 */         return encodedParameters.getBytes("UTF-8");
/*     */       } catch (UnsupportedEncodingException e) {
/* 217 */         throw new AmazonClientException("Unable to encode string into bytes");
/*     */       }
/*     */     }
/*     */ 
/* 221 */     return getBinaryRequestPayloadWithoutQueryParams(request);
/*     */   }
/*     */ 
/*     */   protected String getRequestPayload(Request<?> request)
/*     */   {
/* 232 */     return newString(getBinaryRequestPayload(request));
/*     */   }
/*     */ 
/*     */   protected String getRequestPayloadWithoutQueryParams(Request<?> request)
/*     */   {
/* 245 */     return newString(getBinaryRequestPayloadWithoutQueryParams(request));
/*     */   }
/*     */ 
/*     */   protected byte[] getBinaryRequestPayloadWithoutQueryParams(Request<?> request)
/*     */   {
/* 258 */     InputStream content = getBinaryRequestPayloadStreamWithoutQueryParams(request);
/*     */     try
/*     */     {
/* 261 */       content.mark(-1);
/* 262 */       ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/* 263 */       byte[] buffer = new byte[5120];
/*     */       while (true) {
/* 265 */         int bytesRead = content.read(buffer);
/* 266 */         if (bytesRead == -1)
/*     */           break;
/* 268 */         byteArrayOutputStream.write(buffer, 0, bytesRead);
/*     */       }
/*     */ 
/* 271 */       byteArrayOutputStream.close();
/* 272 */       content.reset();
/*     */ 
/* 274 */       return byteArrayOutputStream.toByteArray();
/*     */     } catch (Exception e) {
/* 276 */       throw new AmazonClientException(new StringBuilder().append("Unable to read request payload to sign request: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected InputStream getBinaryRequestPayloadStream(Request<?> request) {
/* 281 */     if (HttpUtils.usePayloadForQueryParameters(request)) {
/* 282 */       String encodedParameters = HttpUtils.encodeParameters(request);
/* 283 */       if (encodedParameters == null) return new ByteArrayInputStream(new byte[0]); try
/*     */       {
/* 285 */         return new ByteArrayInputStream(encodedParameters.getBytes("UTF-8"));
/*     */       } catch (UnsupportedEncodingException e) {
/* 287 */         throw new AmazonClientException("Unable to encode string into bytes");
/*     */       }
/*     */     }
/*     */ 
/* 291 */     return getBinaryRequestPayloadStreamWithoutQueryParams(request);
/*     */   }
/*     */ 
/*     */   protected InputStream getBinaryRequestPayloadStreamWithoutQueryParams(Request<?> request) {
/*     */     try {
/* 296 */       InputStream content = request.getContent();
/* 297 */       if (content == null) return new ByteArrayInputStream(new byte[0]);
/*     */ 
/* 299 */       if ((content instanceof StringInputStream)) {
/* 300 */         return content;
/*     */       }
/*     */ 
/* 303 */       if (!content.markSupported()) {
/* 304 */         throw new AmazonClientException("Unable to read request payload to sign request.");
/*     */       }
/*     */ 
/* 307 */       return request.getContent();
/*     */     } catch (Exception e) {
/* 309 */       throw new AmazonClientException(new StringBuilder().append("Unable to read request payload to sign request: ").append(e.getMessage()).toString(), e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String getCanonicalizedResourcePath(String resourcePath) {
/* 314 */     if ((resourcePath == null) || (resourcePath.length() == 0)) {
/* 315 */       return "/";
/*     */     }
/* 317 */     String value = HttpUtils.urlEncode(resourcePath, true);
/* 318 */     if (value.startsWith("/")) {
/* 319 */       return value;
/*     */     }
/* 321 */     return "/".concat(value);
/*     */   }
/*     */ 
/*     */   protected String getCanonicalizedEndpoint(URI endpoint)
/*     */   {
/* 327 */     String endpointForStringToSign = endpoint.getHost().toLowerCase();
/*     */ 
/* 335 */     if (HttpUtils.isUsingNonDefaultPort(endpoint)) {
/* 336 */       endpointForStringToSign = new StringBuilder().append(endpointForStringToSign).append(":").append(endpoint.getPort()).toString();
/*     */     }
/*     */ 
/* 339 */     return endpointForStringToSign;
/*     */   }
/*     */ 
/*     */   protected AWSCredentials sanitizeCredentials(AWSCredentials credentials)
/*     */   {
/* 355 */     String accessKeyId = null;
/* 356 */     String secretKey = null;
/* 357 */     String token = null;
/* 358 */     synchronized (credentials) {
/* 359 */       accessKeyId = credentials.getAWSAccessKeyId();
/* 360 */       secretKey = credentials.getAWSSecretKey();
/* 361 */       if ((credentials instanceof AWSSessionCredentials)) {
/* 362 */         token = ((AWSSessionCredentials)credentials).getSessionToken();
/*     */       }
/*     */     }
/* 365 */     if (secretKey != null) secretKey = secretKey.trim();
/* 366 */     if (accessKeyId != null) accessKeyId = accessKeyId.trim();
/* 367 */     if (token != null) token = token.trim();
/*     */ 
/* 369 */     if ((credentials instanceof AWSSessionCredentials)) {
/* 370 */       return new BasicSessionCredentials(accessKeyId, secretKey, token);
/*     */     }
/*     */ 
/* 373 */     return new BasicAWSCredentials(accessKeyId, secretKey);
/*     */   }
/*     */ 
/*     */   protected String newString(byte[] bytes)
/*     */   {
/*     */     try
/*     */     {
/* 385 */       return new String(bytes, "UTF-8");
/*     */     } catch (UnsupportedEncodingException e) {
/* 387 */       throw new AmazonClientException("Unable to encode bytes to String", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected abstract void addSessionCredentials(Request<?> paramRequest, AWSSessionCredentials paramAWSSessionCredentials);
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.AbstractAWSSigner
 * JD-Core Version:    0.6.2
 */