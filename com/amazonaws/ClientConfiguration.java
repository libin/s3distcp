/*     */ package com.amazonaws;
/*     */ 
/*     */ import com.amazonaws.util.VersionInfoUtils;
/*     */ 
/*     */ public class ClientConfiguration
/*     */ {
/*     */   public static final int DEFAULT_SOCKET_TIMEOUT = 50000;
/*     */   public static final int DEFAULT_MAX_CONNECTIONS = 50;
/*  31 */   public static final String DEFAULT_USER_AGENT = VersionInfoUtils.getUserAgent();
/*     */   public static final int DEFAULT_MAX_RETRIES = 3;
/*  37 */   private String userAgent = DEFAULT_USER_AGENT;
/*     */ 
/*  43 */   private int maxErrorRetry = 3;
/*     */ 
/*  51 */   private Protocol protocol = Protocol.HTTPS;
/*     */ 
/*  54 */   private String proxyHost = null;
/*     */ 
/*  57 */   private int proxyPort = -1;
/*     */ 
/*  60 */   private String proxyUsername = null;
/*     */ 
/*  63 */   private String proxyPassword = null;
/*     */ 
/*  66 */   private String proxyDomain = null;
/*     */ 
/*  69 */   private String proxyWorkstation = null;
/*     */ 
/*  72 */   private int maxConnections = 50;
/*     */ 
/*  79 */   private int socketTimeout = 50000;
/*     */ 
/*  86 */   private int connectionTimeout = 50000;
/*     */ 
/*  93 */   private int socketSendBufferSizeHint = 0;
/*     */ 
/* 100 */   private int socketReceiveBufferSizeHint = 0;
/*     */ 
/*     */   public ClientConfiguration() {
/*     */   }
/*     */ 
/*     */   public ClientConfiguration(ClientConfiguration other) {
/* 106 */     this.connectionTimeout = other.connectionTimeout;
/* 107 */     this.maxConnections = other.maxConnections;
/* 108 */     this.maxErrorRetry = other.maxErrorRetry;
/* 109 */     this.protocol = other.protocol;
/* 110 */     this.proxyDomain = other.proxyDomain;
/* 111 */     this.proxyHost = other.proxyHost;
/* 112 */     this.proxyPassword = other.proxyPassword;
/* 113 */     this.proxyPort = other.proxyPort;
/* 114 */     this.proxyUsername = other.proxyUsername;
/* 115 */     this.proxyWorkstation = other.proxyWorkstation;
/* 116 */     this.socketTimeout = other.socketTimeout;
/* 117 */     this.userAgent = other.userAgent;
/*     */ 
/* 119 */     this.socketReceiveBufferSizeHint = other.socketReceiveBufferSizeHint;
/* 120 */     this.socketSendBufferSizeHint = other.socketSendBufferSizeHint;
/*     */   }
/*     */ 
/*     */   public Protocol getProtocol()
/*     */   {
/* 137 */     return this.protocol;
/*     */   }
/*     */ 
/*     */   public void setProtocol(Protocol protocol)
/*     */   {
/* 155 */     this.protocol = protocol;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withProtocol(Protocol protocol)
/*     */   {
/* 177 */     setProtocol(protocol);
/* 178 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMaxConnections()
/*     */   {
/* 187 */     return this.maxConnections;
/*     */   }
/*     */ 
/*     */   public void setMaxConnections(int maxConnections)
/*     */   {
/* 197 */     this.maxConnections = maxConnections;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withMaxConnections(int maxConnections)
/*     */   {
/* 210 */     setMaxConnections(maxConnections);
/* 211 */     return this;
/*     */   }
/*     */ 
/*     */   public String getUserAgent()
/*     */   {
/* 220 */     return this.userAgent;
/*     */   }
/*     */ 
/*     */   public void setUserAgent(String userAgent)
/*     */   {
/* 230 */     this.userAgent = userAgent;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withUserAgent(String userAgent)
/*     */   {
/* 243 */     setUserAgent(userAgent);
/* 244 */     return this;
/*     */   }
/*     */ 
/*     */   public String getProxyHost()
/*     */   {
/* 253 */     return this.proxyHost;
/*     */   }
/*     */ 
/*     */   public void setProxyHost(String proxyHost)
/*     */   {
/* 263 */     this.proxyHost = proxyHost;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withProxyHost(String proxyHost)
/*     */   {
/* 276 */     setProxyHost(proxyHost);
/* 277 */     return this;
/*     */   }
/*     */ 
/*     */   public int getProxyPort()
/*     */   {
/* 286 */     return this.proxyPort;
/*     */   }
/*     */ 
/*     */   public void setProxyPort(int proxyPort)
/*     */   {
/* 296 */     this.proxyPort = proxyPort;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withProxyPort(int proxyPort)
/*     */   {
/* 309 */     setProxyPort(proxyPort);
/* 310 */     return this;
/*     */   }
/*     */ 
/*     */   public String getProxyUsername()
/*     */   {
/* 321 */     return this.proxyUsername;
/*     */   }
/*     */ 
/*     */   public void setProxyUsername(String proxyUsername)
/*     */   {
/* 331 */     this.proxyUsername = proxyUsername;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withProxyUsername(String proxyUsername)
/*     */   {
/* 344 */     setProxyUsername(proxyUsername);
/* 345 */     return this;
/*     */   }
/*     */ 
/*     */   public String getProxyPassword()
/*     */   {
/* 355 */     return this.proxyPassword;
/*     */   }
/*     */ 
/*     */   public void setProxyPassword(String proxyPassword)
/*     */   {
/* 365 */     this.proxyPassword = proxyPassword;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withProxyPassword(String proxyPassword)
/*     */   {
/* 378 */     setProxyPassword(proxyPassword);
/* 379 */     return this;
/*     */   }
/*     */ 
/*     */   public String getProxyDomain()
/*     */   {
/* 390 */     return this.proxyDomain;
/*     */   }
/*     */ 
/*     */   public void setProxyDomain(String proxyDomain)
/*     */   {
/* 403 */     this.proxyDomain = proxyDomain;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withProxyDomain(String proxyDomain)
/*     */   {
/* 419 */     setProxyDomain(proxyDomain);
/* 420 */     return this;
/*     */   }
/*     */ 
/*     */   public String getProxyWorkstation()
/*     */   {
/* 432 */     return this.proxyWorkstation;
/*     */   }
/*     */ 
/*     */   public void setProxyWorkstation(String proxyWorkstation)
/*     */   {
/* 445 */     this.proxyWorkstation = proxyWorkstation;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withProxyWorkstation(String proxyWorkstation)
/*     */   {
/* 461 */     setProxyWorkstation(proxyWorkstation);
/* 462 */     return this;
/*     */   }
/*     */ 
/*     */   public int getMaxErrorRetry()
/*     */   {
/* 473 */     return this.maxErrorRetry;
/*     */   }
/*     */ 
/*     */   public void setMaxErrorRetry(int maxErrorRetry)
/*     */   {
/* 485 */     this.maxErrorRetry = maxErrorRetry;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withMaxErrorRetry(int maxErrorRetry)
/*     */   {
/* 500 */     setMaxErrorRetry(maxErrorRetry);
/* 501 */     return this;
/*     */   }
/*     */ 
/*     */   public int getSocketTimeout()
/*     */   {
/* 515 */     return this.socketTimeout;
/*     */   }
/*     */ 
/*     */   public void setSocketTimeout(int socketTimeout)
/*     */   {
/* 529 */     this.socketTimeout = socketTimeout;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withSocketTimeout(int socketTimeout)
/*     */   {
/* 546 */     setSocketTimeout(socketTimeout);
/* 547 */     return this;
/*     */   }
/*     */ 
/*     */   public int getConnectionTimeout()
/*     */   {
/* 559 */     return this.connectionTimeout;
/*     */   }
/*     */ 
/*     */   public void setConnectionTimeout(int connectionTimeout)
/*     */   {
/* 572 */     this.connectionTimeout = connectionTimeout;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withConnectionTimeout(int connectionTimeout)
/*     */   {
/* 588 */     setConnectionTimeout(connectionTimeout);
/* 589 */     return this;
/*     */   }
/*     */ 
/*     */   public int[] getSocketBufferSizeHints()
/*     */   {
/* 626 */     return new int[] { this.socketSendBufferSizeHint, this.socketReceiveBufferSizeHint };
/*     */   }
/*     */ 
/*     */   public void setSocketBufferSizeHints(int socketSendBufferSizeHint, int socketReceiveBufferSizeHint)
/*     */   {
/* 666 */     this.socketSendBufferSizeHint = socketSendBufferSizeHint;
/* 667 */     this.socketReceiveBufferSizeHint = socketReceiveBufferSizeHint;
/*     */   }
/*     */ 
/*     */   public ClientConfiguration withSocketBufferSizeHints(int socketSendBufferSizeHint, int socketReceiveBufferSizeHint)
/*     */   {
/* 712 */     setSocketBufferSizeHints(socketSendBufferSizeHint, socketReceiveBufferSizeHint);
/* 713 */     return this;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.ClientConfiguration
 * JD-Core Version:    0.6.2
 */