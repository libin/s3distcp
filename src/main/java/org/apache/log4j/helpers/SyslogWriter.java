/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.net.DatagramPacket;
/*     */ import java.net.DatagramSocket;
/*     */ import java.net.InetAddress;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.SocketException;
/*     */ import java.net.URL;
/*     */ import java.net.UnknownHostException;
/*     */ 
/*     */ public class SyslogWriter extends Writer
/*     */ {
/*  39 */   final int SYSLOG_PORT = 514;
/*     */ 
/*     */   /** @deprecated */
/*     */   static String syslogHost;
/*     */   private InetAddress address;
/*     */   private final int port;
/*     */   private DatagramSocket ds;
/*     */ 
/*     */   public SyslogWriter(String syslogHost)
/*     */   {
/*  60 */     syslogHost = syslogHost;
/*  61 */     if (syslogHost == null) {
/*  62 */       throw new NullPointerException("syslogHost");
/*     */     }
/*     */ 
/*  65 */     String host = syslogHost;
/*  66 */     int urlPort = -1;
/*     */ 
/*  72 */     if ((host.indexOf("[") != -1) || (host.indexOf(':') == host.lastIndexOf(':'))) {
/*     */       try {
/*  74 */         URL url = new URL("http://" + host);
/*  75 */         if (url.getHost() != null) {
/*  76 */           host = url.getHost();
/*     */ 
/*  78 */           if ((host.startsWith("[")) && (host.charAt(host.length() - 1) == ']')) {
/*  79 */             host = host.substring(1, host.length() - 1);
/*     */           }
/*  81 */           urlPort = url.getPort();
/*     */         }
/*     */       } catch (MalformedURLException e) {
/*  84 */         LogLog.error("Malformed URL: will attempt to interpret as InetAddress.", e);
/*     */       }
/*     */     }
/*     */ 
/*  88 */     if (urlPort == -1) {
/*  89 */       urlPort = 514;
/*     */     }
/*  91 */     this.port = urlPort;
/*     */     try
/*     */     {
/*  94 */       this.address = InetAddress.getByName(host);
/*     */     }
/*     */     catch (UnknownHostException e) {
/*  97 */       LogLog.error("Could not find " + host + ". All logging will FAIL.", e);
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 102 */       this.ds = new DatagramSocket();
/*     */     }
/*     */     catch (SocketException e) {
/* 105 */       e.printStackTrace();
/* 106 */       LogLog.error("Could not instantiate DatagramSocket to " + host + ". All logging will FAIL.", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void write(char[] buf, int off, int len)
/*     */     throws IOException
/*     */   {
/* 115 */     write(new String(buf, off, len));
/*     */   }
/*     */ 
/*     */   public void write(String string)
/*     */     throws IOException
/*     */   {
/* 121 */     if ((this.ds != null) && (this.address != null)) {
/* 122 */       byte[] bytes = string.getBytes();
/*     */ 
/* 126 */       int bytesLength = bytes.length;
/* 127 */       if (bytesLength >= 1024) {
/* 128 */         bytesLength = 1024;
/*     */       }
/* 130 */       DatagramPacket packet = new DatagramPacket(bytes, bytesLength, this.address, this.port);
/*     */ 
/* 132 */       this.ds.send(packet);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void close() {
/* 141 */     if (this.ds != null)
/* 142 */       this.ds.close();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.SyslogWriter
 * JD-Core Version:    0.6.2
 */