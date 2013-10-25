/*     */ package org.apache.log4j.net;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.util.Hashtable;
/*     */ import org.apache.log4j.Hierarchy;
/*     */ import org.apache.log4j.Level;
/*     */ import org.apache.log4j.LogManager;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.apache.log4j.PropertyConfigurator;
/*     */ import org.apache.log4j.spi.LoggerRepository;
/*     */ import org.apache.log4j.spi.RootLogger;
/*     */ 
/*     */ public class SocketServer
/*     */ {
/*  90 */   static String GENERIC = "generic";
/*  91 */   static String CONFIG_FILE_EXT = ".lcf";
/*     */ 
/*  93 */   static Logger cat = Logger.getLogger(SocketServer.class);
/*     */   static SocketServer server;
/*     */   static int port;
/*     */   Hashtable hierarchyMap;
/*     */   LoggerRepository genericHierarchy;
/*     */   File dir;
/*     */ 
/*     */   public static void main(String[] argv)
/*     */   {
/* 105 */     if (argv.length == 3)
/* 106 */       init(argv[0], argv[1], argv[2]);
/*     */     else
/* 108 */       usage("Wrong number of arguments.");
/*     */     try
/*     */     {
/* 111 */       cat.info("Listening on port " + port);
/* 112 */       ServerSocket serverSocket = new ServerSocket(port);
/*     */       while (true) {
/* 114 */         cat.info("Waiting to accept a new client.");
/* 115 */         Socket socket = serverSocket.accept();
/* 116 */         InetAddress inetAddress = socket.getInetAddress();
/* 117 */         cat.info("Connected to client at " + inetAddress);
/*     */ 
/* 119 */         LoggerRepository h = (LoggerRepository)server.hierarchyMap.get(inetAddress);
/* 120 */         if (h == null) {
/* 121 */           h = server.configureHierarchy(inetAddress);
/*     */         }
/*     */ 
/* 124 */         cat.info("Starting new socket node.");
/* 125 */         new Thread(new SocketNode(socket, h)).start();
/*     */       }
/*     */     }
/*     */     catch (Exception e) {
/* 129 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   static void usage(String msg)
/*     */   {
/* 136 */     System.err.println(msg);
/* 137 */     System.err.println("Usage: java " + SocketServer.class.getName() + " port configFile directory");
/*     */ 
/* 139 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   static void init(String portStr, String configFile, String dirStr)
/*     */   {
/*     */     try {
/* 145 */       port = Integer.parseInt(portStr);
/*     */     }
/*     */     catch (NumberFormatException e) {
/* 148 */       e.printStackTrace();
/* 149 */       usage("Could not interpret port number [" + portStr + "].");
/*     */     }
/*     */ 
/* 152 */     PropertyConfigurator.configure(configFile);
/*     */ 
/* 154 */     File dir = new File(dirStr);
/* 155 */     if (!dir.isDirectory()) {
/* 156 */       usage("[" + dirStr + "] is not a directory.");
/*     */     }
/* 158 */     server = new SocketServer(dir);
/*     */   }
/*     */ 
/*     */   public SocketServer(File directory)
/*     */   {
/* 164 */     this.dir = directory;
/* 165 */     this.hierarchyMap = new Hashtable(11);
/*     */   }
/*     */ 
/*     */   LoggerRepository configureHierarchy(InetAddress inetAddress)
/*     */   {
/* 171 */     cat.info("Locating configuration file for " + inetAddress);
/*     */ 
/* 174 */     String s = inetAddress.toString();
/* 175 */     int i = s.indexOf("/");
/* 176 */     if (i == -1) {
/* 177 */       cat.warn("Could not parse the inetAddress [" + inetAddress + "]. Using default hierarchy.");
/*     */ 
/* 179 */       return genericHierarchy();
/*     */     }
/* 181 */     String key = s.substring(0, i);
/*     */ 
/* 183 */     File configFile = new File(this.dir, key + CONFIG_FILE_EXT);
/* 184 */     if (configFile.exists()) {
/* 185 */       Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
/* 186 */       this.hierarchyMap.put(inetAddress, h);
/*     */ 
/* 188 */       new PropertyConfigurator().doConfigure(configFile.getAbsolutePath(), h);
/*     */ 
/* 190 */       return h;
/*     */     }
/* 192 */     cat.warn("Could not find config file [" + configFile + "].");
/* 193 */     return genericHierarchy();
/*     */   }
/*     */ 
/*     */   LoggerRepository genericHierarchy()
/*     */   {
/* 199 */     if (this.genericHierarchy == null) {
/* 200 */       File f = new File(this.dir, GENERIC + CONFIG_FILE_EXT);
/* 201 */       if (f.exists()) {
/* 202 */         this.genericHierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
/* 203 */         new PropertyConfigurator().doConfigure(f.getAbsolutePath(), this.genericHierarchy);
/*     */       } else {
/* 205 */         cat.warn("Could not find config file [" + f + "]. Will use the default hierarchy.");
/*     */ 
/* 207 */         this.genericHierarchy = LogManager.getLoggerRepository();
/*     */       }
/*     */     }
/* 210 */     return this.genericHierarchy;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.SocketServer
 * JD-Core Version:    0.6.2
 */