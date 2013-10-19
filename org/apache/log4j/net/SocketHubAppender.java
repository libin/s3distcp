/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.SocketException;
/*     */ import java.util.Vector;
/*     */ import org.apache.log4j.AppenderSkeleton;
/*     */ import org.apache.log4j.helpers.CyclicBuffer;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class SocketHubAppender extends AppenderSkeleton
/*     */ {
/*     */   static final int DEFAULT_PORT = 4560;
/* 114 */   private int port = 4560;
/* 115 */   private Vector oosList = new Vector();
/* 116 */   private ServerMonitor serverMonitor = null;
/* 117 */   private boolean locationInfo = false;
/* 118 */   private CyclicBuffer buffer = null;
/*     */   private String application;
/*     */   private boolean advertiseViaMulticastDNS;
/*     */   private ZeroConfSupport zeroConf;
/*     */   public static final String ZONE = "_log4j_obj_tcpaccept_appender.local.";
/*     */   private ServerSocket serverSocket;
/*     */ 
/*     */   public SocketHubAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public SocketHubAppender(int _port)
/*     */   {
/* 136 */     this.port = _port;
/* 137 */     startServer();
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 144 */     if (this.advertiseViaMulticastDNS) {
/* 145 */       this.zeroConf = new ZeroConfSupport("_log4j_obj_tcpaccept_appender.local.", this.port, getName());
/* 146 */       this.zeroConf.advertise();
/*     */     }
/* 148 */     startServer();
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 158 */     if (this.closed) {
/* 159 */       return;
/*     */     }
/* 161 */     LogLog.debug("closing SocketHubAppender " + getName());
/* 162 */     this.closed = true;
/* 163 */     if (this.advertiseViaMulticastDNS) {
/* 164 */       this.zeroConf.unadvertise();
/*     */     }
/* 166 */     cleanUp();
/*     */ 
/* 168 */     LogLog.debug("SocketHubAppender " + getName() + " closed");
/*     */   }
/*     */ 
/*     */   public void cleanUp()
/*     */   {
/* 177 */     LogLog.debug("stopping ServerSocket");
/* 178 */     this.serverMonitor.stopMonitor();
/* 179 */     this.serverMonitor = null;
/*     */ 
/* 182 */     LogLog.debug("closing client connections");
/* 183 */     while (this.oosList.size() != 0) {
/* 184 */       ObjectOutputStream oos = (ObjectOutputStream)this.oosList.elementAt(0);
/* 185 */       if (oos != null) {
/*     */         try {
/* 187 */           oos.close();
/*     */         } catch (InterruptedIOException e) {
/* 189 */           Thread.currentThread().interrupt();
/* 190 */           LogLog.error("could not close oos.", e);
/*     */         } catch (IOException e) {
/* 192 */           LogLog.error("could not close oos.", e);
/*     */         }
/*     */ 
/* 195 */         this.oosList.removeElementAt(0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 204 */     if (event != null)
/*     */     {
/* 206 */       if (this.locationInfo) {
/* 207 */         event.getLocationInformation();
/*     */       }
/* 209 */       if (this.application != null) {
/* 210 */         event.setProperty("application", this.application);
/*     */       }
/* 212 */       event.getNDC();
/* 213 */       event.getThreadName();
/* 214 */       event.getMDCCopy();
/* 215 */       event.getRenderedMessage();
/* 216 */       event.getThrowableStrRep();
/*     */ 
/* 218 */       if (this.buffer != null) {
/* 219 */         this.buffer.add(event);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 224 */     if ((event == null) || (this.oosList.size() == 0)) {
/* 225 */       return;
/*     */     }
/*     */ 
/* 229 */     for (int streamCount = 0; streamCount < this.oosList.size(); streamCount++)
/*     */     {
/* 231 */       ObjectOutputStream oos = null;
/*     */       try {
/* 233 */         oos = (ObjectOutputStream)this.oosList.elementAt(streamCount);
/*     */       }
/*     */       catch (ArrayIndexOutOfBoundsException e)
/*     */       {
/*     */       }
/*     */ 
/* 242 */       if (oos == null)
/*     */         break;
/*     */       try
/*     */       {
/* 246 */         oos.writeObject(event);
/* 247 */         oos.flush();
/*     */ 
/* 251 */         oos.reset();
/*     */       }
/*     */       catch (IOException e) {
/* 254 */         if ((e instanceof InterruptedIOException)) {
/* 255 */           Thread.currentThread().interrupt();
/*     */         }
/*     */ 
/* 258 */         this.oosList.removeElementAt(streamCount);
/* 259 */         LogLog.debug("dropped connection");
/*     */ 
/* 262 */         streamCount--;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 272 */     return false;
/*     */   }
/*     */ 
/*     */   public void setPort(int _port)
/*     */   {
/* 280 */     this.port = _port;
/*     */   }
/*     */ 
/*     */   public void setApplication(String lapp)
/*     */   {
/* 289 */     this.application = lapp;
/*     */   }
/*     */ 
/*     */   public String getApplication()
/*     */   {
/* 297 */     return this.application;
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 304 */     return this.port;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int _bufferSize)
/*     */   {
/* 313 */     this.buffer = new CyclicBuffer(_bufferSize);
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 321 */     if (this.buffer == null) {
/* 322 */       return 0;
/*     */     }
/* 324 */     return this.buffer.getMaxSize();
/*     */   }
/*     */ 
/*     */   public void setLocationInfo(boolean _locationInfo)
/*     */   {
/* 334 */     this.locationInfo = _locationInfo;
/*     */   }
/*     */ 
/*     */   public boolean getLocationInfo()
/*     */   {
/* 341 */     return this.locationInfo;
/*     */   }
/*     */ 
/*     */   public void setAdvertiseViaMulticastDNS(boolean advertiseViaMulticastDNS) {
/* 345 */     this.advertiseViaMulticastDNS = advertiseViaMulticastDNS;
/*     */   }
/*     */ 
/*     */   public boolean isAdvertiseViaMulticastDNS() {
/* 349 */     return this.advertiseViaMulticastDNS;
/*     */   }
/*     */ 
/*     */   private void startServer()
/*     */   {
/* 356 */     this.serverMonitor = new ServerMonitor(this.port, this.oosList);
/*     */   }
/*     */ 
/*     */   protected ServerSocket createServerSocket(int socketPort)
/*     */     throws IOException
/*     */   {
/* 366 */     return new ServerSocket(socketPort);
/*     */   }
/*     */ 
/*     */   private class ServerMonitor
/*     */     implements Runnable
/*     */   {
/*     */     private int port;
/*     */     private Vector oosList;
/*     */     private boolean keepRunning;
/*     */     private Thread monitorThread;
/*     */ 
/*     */     public ServerMonitor(int _port, Vector _oosList)
/*     */     {
/* 383 */       this.port = _port;
/* 384 */       this.oosList = _oosList;
/* 385 */       this.keepRunning = true;
/* 386 */       this.monitorThread = new Thread(this);
/* 387 */       this.monitorThread.setDaemon(true);
/* 388 */       this.monitorThread.setName("SocketHubAppender-Monitor-" + this.port);
/* 389 */       this.monitorThread.start();
/*     */     }
/*     */ 
/*     */     public synchronized void stopMonitor()
/*     */     {
/* 396 */       if (this.keepRunning) {
/* 397 */         LogLog.debug("server monitor thread shutting down");
/* 398 */         this.keepRunning = false;
/*     */         try {
/* 400 */           if (SocketHubAppender.this.serverSocket != null) {
/* 401 */             SocketHubAppender.this.serverSocket.close();
/* 402 */             SocketHubAppender.this.serverSocket = null;
/*     */           }
/*     */         } catch (IOException ioe) {
/*     */         }
/*     */         try {
/* 407 */           this.monitorThread.join();
/*     */         }
/*     */         catch (InterruptedException e) {
/* 410 */           Thread.currentThread().interrupt();
/*     */         }
/*     */ 
/* 415 */         this.monitorThread = null;
/* 416 */         LogLog.debug("server monitor thread shut down");
/*     */       }
/*     */     }
/*     */ 
/*     */     private void sendCachedEvents(ObjectOutputStream stream) throws IOException
/*     */     {
/* 422 */       if (SocketHubAppender.this.buffer != null) {
/* 423 */         for (int i = 0; i < SocketHubAppender.this.buffer.length(); i++) {
/* 424 */           stream.writeObject(SocketHubAppender.this.buffer.get(i));
/*     */         }
/* 426 */         stream.flush();
/* 427 */         stream.reset();
/*     */       }
/*     */     }
/*     */ 
/*     */     public void run()
/*     */     {
/* 436 */       SocketHubAppender.this.serverSocket = null;
/*     */       try {
/* 438 */         SocketHubAppender.this.serverSocket = SocketHubAppender.this.createServerSocket(this.port);
/* 439 */         SocketHubAppender.this.serverSocket.setSoTimeout(1000);
/*     */       }
/*     */       catch (Exception e) {
/* 442 */         if (((e instanceof InterruptedIOException)) || ((e instanceof InterruptedException))) {
/* 443 */           Thread.currentThread().interrupt();
/*     */         }
/* 445 */         LogLog.error("exception setting timeout, shutting down server socket.", e);
/* 446 */         this.keepRunning = false;
/* 447 */         return;
/*     */       }
/*     */       try
/*     */       {
/*     */         try {
/* 452 */           SocketHubAppender.this.serverSocket.setSoTimeout(1000);
/*     */         }
/*     */         catch (SocketException e) {
/* 455 */           LogLog.error("exception setting timeout, shutting down server socket.", e);
/*     */           return;
/*     */         }
/* 459 */         while (this.keepRunning) {
/* 460 */           Socket socket = null;
/*     */           try {
/* 462 */             socket = SocketHubAppender.this.serverSocket.accept();
/*     */           }
/*     */           catch (InterruptedIOException e)
/*     */           {
/*     */           }
/*     */           catch (SocketException e) {
/* 468 */             LogLog.error("exception accepting socket, shutting down server socket.", e);
/* 469 */             this.keepRunning = false;
/*     */           }
/*     */           catch (IOException e) {
/* 472 */             LogLog.error("exception accepting socket.", e);
/*     */           }
/*     */ 
/* 476 */           if (socket != null)
/*     */             try {
/* 478 */               InetAddress remoteAddress = socket.getInetAddress();
/* 479 */               LogLog.debug("accepting connection from " + remoteAddress.getHostName() + " (" + remoteAddress.getHostAddress() + ")");
/*     */ 
/* 483 */               ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
/* 484 */               if ((SocketHubAppender.this.buffer != null) && (SocketHubAppender.this.buffer.length() > 0)) {
/* 485 */                 sendCachedEvents(oos);
/*     */               }
/*     */ 
/* 489 */               this.oosList.addElement(oos);
/*     */             } catch (IOException e) {
/* 491 */               if ((e instanceof InterruptedIOException)) {
/* 492 */                 Thread.currentThread().interrupt();
/*     */               }
/* 494 */               LogLog.error("exception creating output stream on socket.", e);
/*     */             }
/*     */         }
/*     */       }
/*     */       finally
/*     */       {
/*     */         try
/*     */         {
/* 502 */           SocketHubAppender.this.serverSocket.close();
/*     */         } catch (InterruptedIOException e) {
/* 504 */           Thread.currentThread().interrupt();
/*     */         }
/*     */         catch (IOException e)
/*     */         {
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.SocketHubAppender
 * JD-Core Version:    0.6.2
 */