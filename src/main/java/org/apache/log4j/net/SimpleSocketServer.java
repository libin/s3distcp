/*    */ package org.apache.log4j.net;
/*    */ 
/*    */ import java.io.PrintStream;
/*    */ import java.net.ServerSocket;
/*    */ import java.net.Socket;
/*    */ import org.apache.log4j.LogManager;
/*    */ import org.apache.log4j.Logger;
/*    */ import org.apache.log4j.PropertyConfigurator;
/*    */ import org.apache.log4j.xml.DOMConfigurator;
/*    */ 
/*    */ public class SimpleSocketServer
/*    */ {
/* 46 */   static Logger cat = Logger.getLogger(SimpleSocketServer.class);
/*    */   static int port;
/*    */ 
/*    */   public static void main(String[] argv)
/*    */   {
/* 53 */     if (argv.length == 2)
/* 54 */       init(argv[0], argv[1]);
/*    */     else {
/* 56 */       usage("Wrong number of arguments.");
/*    */     }
/*    */     try
/*    */     {
/* 60 */       cat.info("Listening on port " + port);
/* 61 */       ServerSocket serverSocket = new ServerSocket(port);
/*    */       while (true) {
/* 63 */         cat.info("Waiting to accept a new client.");
/* 64 */         Socket socket = serverSocket.accept();
/* 65 */         cat.info("Connected to client at " + socket.getInetAddress());
/* 66 */         cat.info("Starting new socket node.");
/* 67 */         new Thread(new SocketNode(socket, LogManager.getLoggerRepository()), "SimpleSocketServer-" + port).start();
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/* 71 */       e.printStackTrace();
/*    */     }
/*    */   }
/*    */ 
/*    */   static void usage(String msg)
/*    */   {
/* 77 */     System.err.println(msg);
/* 78 */     System.err.println("Usage: java " + SimpleSocketServer.class.getName() + " port configFile");
/*    */ 
/* 80 */     System.exit(1);
/*    */   }
/*    */ 
/*    */   static void init(String portStr, String configFile) {
/*    */     try {
/* 85 */       port = Integer.parseInt(portStr);
/*    */     } catch (NumberFormatException e) {
/* 87 */       e.printStackTrace();
/* 88 */       usage("Could not interpret port number [" + portStr + "].");
/*    */     }
/*    */ 
/* 91 */     if (configFile.endsWith(".xml"))
/* 92 */       DOMConfigurator.configure(configFile);
/*    */     else
/* 94 */       PropertyConfigurator.configure(configFile);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.net.SimpleSocketServer
 * JD-Core Version:    0.6.2
 */