/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import com.amazonaws.Request;
/*     */ import com.amazonaws.http.HttpMethodName;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URI;
/*     */ import java.net.URLEncoder;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.http.client.utils.URLEncodedUtils;
/*     */ import org.apache.http.message.BasicNameValuePair;
/*     */ 
/*     */ public class HttpUtils
/*     */ {
/*     */   private static final String DEFAULT_ENCODING = "UTF-8";
/*     */ 
/*     */   public static String urlEncode(String value, boolean path)
/*     */   {
/*  36 */     if (value == null) return "";
/*     */     try
/*     */     {
/*  39 */       String encoded = URLEncoder.encode(value, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
/*     */ 
/*  42 */       if (path);
/*  43 */       return encoded.replace("%2F", "/");
/*     */     }
/*     */     catch (UnsupportedEncodingException ex)
/*     */     {
/*  48 */       throw new RuntimeException(ex);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isUsingNonDefaultPort(URI uri)
/*     */   {
/*  63 */     String scheme = uri.getScheme().toLowerCase();
/*  64 */     int port = uri.getPort();
/*     */ 
/*  66 */     if (port <= 0) return false;
/*  67 */     if ((scheme.equals("http")) && (port == 80)) return false;
/*  68 */     if ((scheme.equals("https")) && (port == 443)) return false;
/*     */ 
/*  70 */     return true;
/*     */   }
/*     */ 
/*     */   public static boolean usePayloadForQueryParameters(Request<?> request) {
/*  74 */     boolean requestIsPOST = HttpMethodName.POST.equals(request.getHttpMethod());
/*  75 */     boolean requestHasNoPayload = request.getContent() == null;
/*     */ 
/*  77 */     return (requestIsPOST) && (requestHasNoPayload);
/*     */   }
/*     */ 
/*     */   public static String encodeParameters(Request<?> request)
/*     */   {
/*  91 */     List nameValuePairs = null;
/*  92 */     if (request.getParameters().size() > 0) {
/*  93 */       nameValuePairs = new ArrayList(request.getParameters().size());
/*  94 */       for (Map.Entry entry : request.getParameters().entrySet()) {
/*  95 */         nameValuePairs.add(new BasicNameValuePair((String)entry.getKey(), (String)entry.getValue()));
/*     */       }
/*     */     }
/*     */ 
/*  99 */     String encodedParams = null;
/* 100 */     if (nameValuePairs != null) {
/* 101 */       encodedParams = URLEncodedUtils.format(nameValuePairs, "UTF-8");
/*     */     }
/*     */ 
/* 104 */     return encodedParams;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.HttpUtils
 * JD-Core Version:    0.6.2
 */