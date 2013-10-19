/*     */ package com.amazonaws.http;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.ClientConfiguration;
/*     */ import java.io.IOException;
/*     */ import java.net.InetSocketAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ import java.security.cert.CertificateException;
/*     */ import java.security.cert.X509Certificate;
/*     */ import javax.net.ssl.SSLContext;
/*     */ import javax.net.ssl.SSLSocket;
/*     */ import javax.net.ssl.TrustManager;
/*     */ import javax.net.ssl.X509TrustManager;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.http.Header;
/*     */ import org.apache.http.HttpHost;
/*     */ import org.apache.http.HttpRequest;
/*     */ import org.apache.http.HttpResponse;
/*     */ import org.apache.http.ProtocolException;
/*     */ import org.apache.http.StatusLine;
/*     */ import org.apache.http.auth.AuthScope;
/*     */ import org.apache.http.auth.NTCredentials;
/*     */ import org.apache.http.client.CredentialsProvider;
/*     */ import org.apache.http.client.HttpClient;
/*     */ import org.apache.http.conn.ClientConnectionManager;
/*     */ import org.apache.http.conn.ConnectTimeoutException;
/*     */ import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
/*     */ import org.apache.http.conn.scheme.PlainSocketFactory;
/*     */ import org.apache.http.conn.scheme.Scheme;
/*     */ import org.apache.http.conn.scheme.SchemeRegistry;
/*     */ import org.apache.http.conn.scheme.SchemeSocketFactory;
/*     */ import org.apache.http.impl.client.DefaultHttpClient;
/*     */ import org.apache.http.impl.client.DefaultRedirectStrategy;
/*     */ import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
/*     */ import org.apache.http.params.BasicHttpParams;
/*     */ import org.apache.http.params.HttpConnectionParams;
/*     */ import org.apache.http.params.HttpParams;
/*     */ import org.apache.http.params.HttpProtocolParams;
/*     */ import org.apache.http.protocol.HttpContext;
/*     */ 
/*     */ class HttpClientFactory
/*     */ {
/*     */   public HttpClient createHttpClient(ClientConfiguration config)
/*     */   {
/*  74 */     String userAgent = config.getUserAgent();
/*  75 */     if (!userAgent.equals(ClientConfiguration.DEFAULT_USER_AGENT)) {
/*  76 */       userAgent = userAgent + ", " + ClientConfiguration.DEFAULT_USER_AGENT;
/*     */     }
/*     */ 
/*  80 */     HttpParams httpClientParams = new BasicHttpParams();
/*  81 */     HttpProtocolParams.setUserAgent(httpClientParams, userAgent);
/*  82 */     HttpConnectionParams.setConnectionTimeout(httpClientParams, config.getConnectionTimeout());
/*  83 */     HttpConnectionParams.setSoTimeout(httpClientParams, config.getSocketTimeout());
/*  84 */     HttpConnectionParams.setStaleCheckingEnabled(httpClientParams, true);
/*  85 */     HttpConnectionParams.setTcpNoDelay(httpClientParams, true);
/*     */ 
/*  87 */     int socketSendBufferSizeHint = config.getSocketBufferSizeHints()[0];
/*  88 */     int socketReceiveBufferSizeHint = config.getSocketBufferSizeHints()[1];
/*  89 */     if ((socketSendBufferSizeHint > 0) || (socketReceiveBufferSizeHint > 0)) {
/*  90 */       HttpConnectionParams.setSocketBufferSize(httpClientParams, Math.max(socketSendBufferSizeHint, socketReceiveBufferSizeHint));
/*     */     }
/*     */ 
/*  95 */     ThreadSafeClientConnManager connectionManager = ConnectionManagerFactory.createThreadSafeClientConnManager(config, httpClientParams);
/*  96 */     DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, httpClientParams);
/*  97 */     httpClient.setRedirectStrategy(new LocationHeaderNotRequiredRedirectStrategy(null));
/*     */     try
/*     */     {
/* 100 */       Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
/*     */ 
/* 102 */       org.apache.http.conn.ssl.SSLSocketFactory sf = new org.apache.http.conn.ssl.SSLSocketFactory(SSLContext.getDefault(), org.apache.http.conn.ssl.SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
/*     */ 
/* 105 */       Scheme https = new Scheme("https", 443, sf);
/*     */ 
/* 107 */       SchemeRegistry sr = connectionManager.getSchemeRegistry();
/* 108 */       sr.register(http);
/* 109 */       sr.register(https);
/*     */     } catch (NoSuchAlgorithmException e) {
/* 111 */       throw new AmazonClientException("Unable to access default SSL context");
/*     */     }
/*     */ 
/* 119 */     if (System.getProperty("com.amazonaws.sdk.disableCertChecking") != null) {
/* 120 */       Scheme sch = new Scheme("https", 443, new TrustingSocketFactory(null));
/* 121 */       httpClient.getConnectionManager().getSchemeRegistry().register(sch);
/*     */     }
/*     */ 
/* 125 */     String proxyHost = config.getProxyHost();
/* 126 */     int proxyPort = config.getProxyPort();
/* 127 */     if ((proxyHost != null) && (proxyPort > 0)) {
/* 128 */       AmazonHttpClient.log.info("Configuring Proxy. Proxy Host: " + proxyHost + " " + "Proxy Port: " + proxyPort);
/* 129 */       HttpHost proxyHttpHost = new HttpHost(proxyHost, proxyPort);
/* 130 */       httpClient.getParams().setParameter("http.route.default-proxy", proxyHttpHost);
/*     */ 
/* 132 */       String proxyUsername = config.getProxyUsername();
/* 133 */       String proxyPassword = config.getProxyPassword();
/* 134 */       String proxyDomain = config.getProxyDomain();
/* 135 */       String proxyWorkstation = config.getProxyWorkstation();
/*     */ 
/* 137 */       if ((proxyUsername != null) && (proxyPassword != null)) {
/* 138 */         httpClient.getCredentialsProvider().setCredentials(new AuthScope(proxyHost, proxyPort), new NTCredentials(proxyUsername, proxyPassword, proxyWorkstation, proxyDomain));
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 144 */     return httpClient;
/*     */   }
/*     */ 
/*     */   private static class TrustingX509TrustManager
/*     */     implements X509TrustManager
/*     */   {
/* 227 */     private static final X509Certificate[] X509_CERTIFICATES = new X509Certificate[0];
/*     */ 
/*     */     public X509Certificate[] getAcceptedIssuers() {
/* 230 */       return X509_CERTIFICATES;
/*     */     }
/*     */ 
/*     */     public void checkServerTrusted(X509Certificate[] chain, String authType)
/*     */       throws CertificateException
/*     */     {
/*     */     }
/*     */ 
/*     */     public void checkClientTrusted(X509Certificate[] chain, String authType)
/*     */       throws CertificateException
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class TrustingSocketFactory
/*     */     implements SchemeSocketFactory, LayeredSchemeSocketFactory
/*     */   {
/* 175 */     private SSLContext sslcontext = null;
/*     */ 
/*     */     private static SSLContext createSSLContext() throws IOException {
/*     */       try {
/* 179 */         SSLContext context = SSLContext.getInstance("TLS");
/* 180 */         context.init(null, new TrustManager[] { new HttpClientFactory.TrustingX509TrustManager(null) }, null);
/* 181 */         return context;
/*     */       } catch (Exception e) {
/* 183 */         throw new IOException(e.getMessage());
/*     */       }
/*     */     }
/*     */ 
/*     */     private SSLContext getSSLContext() throws IOException {
/* 188 */       if (this.sslcontext == null) this.sslcontext = createSSLContext();
/* 189 */       return this.sslcontext;
/*     */     }
/*     */ 
/*     */     public Socket createSocket(HttpParams params) throws IOException {
/* 193 */       return getSSLContext().getSocketFactory().createSocket();
/*     */     }
/*     */ 
/*     */     public Socket connectSocket(Socket sock, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpParams params)
/*     */       throws IOException, UnknownHostException, ConnectTimeoutException
/*     */     {
/* 201 */       int connTimeout = HttpConnectionParams.getConnectionTimeout(params);
/* 202 */       int soTimeout = HttpConnectionParams.getSoTimeout(params);
/*     */ 
/* 204 */       SSLSocket sslsock = (SSLSocket)(sock != null ? sock : createSocket(params));
/* 205 */       if (localAddress != null) sslsock.bind(localAddress);
/*     */ 
/* 207 */       sslsock.connect(remoteAddress, connTimeout);
/* 208 */       sslsock.setSoTimeout(soTimeout);
/* 209 */       return sslsock;
/*     */     }
/*     */ 
/*     */     public boolean isSecure(Socket sock) throws IllegalArgumentException {
/* 213 */       return true;
/*     */     }
/*     */ 
/*     */     public Socket createLayeredSocket(Socket arg0, String arg1, int arg2, boolean arg3) throws IOException, UnknownHostException
/*     */     {
/* 218 */       return getSSLContext().getSocketFactory().createSocket();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class LocationHeaderNotRequiredRedirectStrategy extends DefaultRedirectStrategy
/*     */   {
/*     */     private LocationHeaderNotRequiredRedirectStrategy()
/*     */     {
/*     */     }
/*     */ 
/*     */     public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)
/*     */       throws ProtocolException
/*     */     {
/* 156 */       int statusCode = response.getStatusLine().getStatusCode();
/* 157 */       Header locationHeader = response.getFirstHeader("location");
/*     */ 
/* 161 */       if ((locationHeader == null) && (statusCode == 301)) {
/* 162 */         return false;
/*     */       }
/* 164 */       return super.isRedirected(request, response, context);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.http.HttpClientFactory
 * JD-Core Version:    0.6.2
 */