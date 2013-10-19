/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.ConnectException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class SocketAppender extends AppenderSkeleton
/*     */ {
/*     */   public static final int DEFAULT_PORT = 4560;
/*     */   static final int DEFAULT_RECONNECTION_DELAY = 30000;
/*     */   String remoteHost;
/*     */   public static final String ZONE = "_log4j_obj_tcpconnect_appender.local.";
/*     */   InetAddress address;
/* 127 */   int port = 4560;
/*     */   ObjectOutputStream oos;
/* 129 */   int reconnectionDelay = 30000;
/* 130 */   boolean locationInfo = false;
/*     */   private String application;
/*     */   private Connector connector;
/* 135 */   int counter = 0;
/*     */   private static final int RESET_FREQUENCY = 1;
/*     */   private boolean advertiseViaMulticastDNS;
/*     */   private ZeroConfSupport zeroConf;
/*     */ 
/*     */   public SocketAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SocketAppender(InetAddress address, int port)
/*     */   {
/* 150 */     this.address = address;
/* 151 */     this.remoteHost = address.getHostName();
/* 152 */     this.port = port;
/* 153 */     connect(address, port);
/*     */   }
/*     */ 
/*     */   public SocketAppender(String host, int port)
/*     */   {
/* 160 */     this.port = port;
/* 161 */     this.address = getAddressByName(host);
/* 162 */     this.remoteHost = host;
/* 163 */     connect(this.address, port);
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 170 */     if (this.advertiseViaMulticastDNS) {
/* 171 */       this.zeroConf = new ZeroConfSupport("_log4j_obj_tcpconnect_appender.local.", this.port, getName());
/* 172 */       this.zeroConf.advertise();
/*     */     }
/* 174 */     connect(this.address, this.port);
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 184 */     if (this.closed) {
/* 185 */       return;
/*     */     }
/* 187 */     this.closed = true;
/* 188 */     if (this.advertiseViaMulticastDNS) {
/* 189 */       this.zeroConf.unadvertise();
/*     */     }
/*     */ 
/* 192 */     cleanUp();
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 200 */     if (this.oos != null) {
/*     */       try {
/* 202 */         this.oos.close();
/*     */       } catch (IOException e) {
/* 204 */         if ((e instanceof InterruptedIOException)) {
/* 205 */           Thread.currentThread().interrupt();
/*     */         }
/* 207 */         LogLog.error("Could not close oos.", e);
/*     */       }
/* 209 */       this.oos = null;
/*     */     }
/* 211 */     if (this.connector != null)
/*     */     {
/* 213 */       this.connector.interrupted = true;
/* 214 */       this.connector = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   void connect(InetAddress address, int port) {
/* 219 */     if (this.address == null)
/* 220 */       return;
/*     */     try
/*     */     {
/* 223 */       cleanUp();
/* 224 */       this.oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
/*     */     } catch (IOException e) {
/* 226 */       if ((e instanceof InterruptedIOException)) {
/* 227 */         Thread.currentThread().interrupt();
/*     */       }
/* 229 */       String msg = "Could not connect to remote log4j server at [" + address.getHostName() + "].";
/*     */ 
/* 231 */       if (this.reconnectionDelay > 0) {
/* 232 */         msg = msg + " We will try again later.";
/* 233 */         fireConnector();
/*     */       } else {
/* 235 */         msg = msg + " We are not retrying.";
/* 236 */         this.errorHandler.error(msg, e, 0);
/*     */       }
/* 238 */       LogLog.error(msg);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 244 */     if (event == null) {
/* 245 */       return;
/*     */     }
/* 247 */     if (this.address == null) {
/* 248 */       this.errorHandler.error("No remote host is set for SocketAppender named \"" + this.name + "\".");
/*     */ 
/* 250 */       return;
/*     */     }
/*     */ 
/* 253 */     if (this.oos != null)
/*     */       try
/*     */       {
/* 256 */         if (this.locationInfo) {
/* 257 */           event.getLocationInformation();
/*     */         }
/* 259 */         if (this.application != null) {
/* 260 */           event.setProperty("application", this.application);
/*     */         }
/* 262 */         event.getNDC();
/* 263 */         event.getThreadName();
/* 264 */         event.getMDCCopy();
/* 265 */         event.getRenderedMessage();
/* 266 */         event.getThrowableStrRep();
/*     */ 
/* 268 */         this.oos.writeObject(event);
/*     */ 
/* 270 */         this.oos.flush();
/* 271 */         if (++this.counter >= 1) {
/* 272 */           this.counter = 0;
/*     */ 
/* 276 */           this.oos.reset();
/*     */         }
/*     */       } catch (IOException e) {
/* 279 */         if ((e instanceof InterruptedIOException)) {
/* 280 */           Thread.currentThread().interrupt();
/*     */         }
/* 282 */         this.oos = null;
/* 283 */         LogLog.warn("Detected problem with connection: " + e);
/* 284 */         if (this.reconnectionDelay > 0)
/* 285 */           fireConnector();
/*     */         else
/* 287 */           this.errorHandler.error("Detected problem with connection, not reconnecting.", e, 0);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void setAdvertiseViaMulticastDNS(boolean advertiseViaMulticastDNS)
/*     */   {
/* 295 */     this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
/*     */   }
/*     */ 
/*     */   public boolean isAdvertiseViaMulticastDNS() {
/* 299 */     return this.advertiseViaMulticastDNS;
/*     */   }
/*     */ 
/*     */   void fireConnector() {
/* 303 */     if (this.connector == null) {
/* 304 */       LogLog.debug("Starting a new connector thread.");
/* 305 */       this.connector = new Connector();
/* 306 */       this.connector.setDaemon(true);
/* 307 */       this.connector.setPriority(1);
/* 308 */       this.connector.start();
/*     */     }
/*     */   }
/*     */ 
/*     */   static InetAddress getAddressByName(String host)
/*     */   {
/*     */     try {
/* 315 */       return InetAddress.getByName(host);
/*     */     } catch (Exception e) {
/* 317 */       if (((e instanceof InterruptedIOException)) || ((e instanceof InterruptedException))) {
/* 318 */         Thread.currentThread().interrupt();
/*     */       }
/* 320 */       LogLog.error("Could not find address of [" + host + "].", e);
/* 321 */     }return null;
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 330 */     return false;
/*     */   }
/*     */ 
/*     */   public void setRemoteHost(String host)
/*     */   {
/* 339 */     this.address = getAddressByName(host);
/* 340 */     this.remoteHost = host;
/*     */   }
/*     */ 
/*     */   public String getRemoteHost()
/*     */   {
/* 347 */     return this.remoteHost;
/*     */   }
/*     */ 
/*     */   public void setPort(int port)
/*     */   {
/* 355 */     this.port = port;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 362 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean locationInfo)
/*     */   {
/* 371 */     this.locationInfo = locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 378 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setApplication(String lapp)
/*     */   {
/* 388 */     this.application = lapp;
/*     */   }
/*     */ 
/*     */   public String getApplication()
/*     */   {
/* 396 */     return this.application;
/*     */   }
/*     */ 
/*     */   public void setReconnectionDelay(int delay)
/*     */   {
/* 409 */     this.reconnectionDelay = delay;
/*     */   }
/*     */ 
/*     */   public int getReconnectionDelay()
/*     */   {
/* 416 */     return this.reconnectionDelay;
/*     */   }
/*     */ 
/*     */   class Connector extends Thread
/*     */   {
/* 433 */     boolean interrupted = false;
/*     */ 
/*     */     Connector() {
/*     */     }
/*     */     public void run() {
/* 438 */       while (!this.interrupted)
/*     */         try {
/* 440 */           sleep(SocketAppender.this.reconnectionDelay);
/* 441 */           LogLog.debug("Attempting connection to " + SocketAppender.this.address.getHostName());
/* 442 */           Socket socket = new Socket(SocketAppender.this.address, SocketAppender.this.port);
/* 443 */           synchronized (this) {
/* 444 */             SocketAppender.this.oos = new ObjectOutputStream(socket.getOutputStream());
/* 445 */             SocketAppender.this.connector = null;
/* 446 */             LogLog.debug("Connection established. Exiting connector thread.");
/*     */           }
/*     */         }
/*     */         catch (InterruptedException e) {
/* 450 */           LogLog.debug("Connector interrupted. Leaving loop.");
/* 451 */           return;
/*     */         } catch (ConnectException e) {
/* 453 */           LogLog.debug("Remote host " + SocketAppender.this.address.getHostName() + " refused connection.");
/*     */         }
/*     */         catch (IOException e) {
/* 456 */           if ((e instanceof InterruptedIOException)) {
/* 457 */             Thread.currentThread().interrupt();
/*     */           }
/* 459 */           LogLog.debug("Could not connect to " + SocketAppender.this.address.getHostName() + ". Exception is " + e);
/*     */         }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.SocketAppender
 * JD-Core Version:    0.6.2
 */