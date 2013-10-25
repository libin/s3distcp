/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.BasicConfigurator;
/*     */ import org.apache.log4j.Logger;
/*     */ 
/*     */ public class Roller
/*     */ {
/*  41 */   static Logger cat = Logger.getLogger(Roller.class);
/*     */   static String host;
/*     */   static int port;
/*     */ 
/*     */   public static void main(String[] argv)
/*     */   {
/*  61 */     BasicConfigurator.configure();
/*     */ 
/*  63 */     if (argv.length == 2)
/*  64 */       init(argv[0], argv[1]);
/*     */     else {
/*  66 */       usage("Wrong number of arguments.");
/*     */     }
/*  68 */     roll();
/*     */   }
/*     */ 
/*     */   static void usage(String msg)
/*     */   {
/*  73 */     System.err.println(msg);
/*  74 */     System.err.println("Usage: java " + Roller.class.getName() + "host_name port_number");
/*     */ 
/*  76 */     System.exit(1);
/*     */   }
/*     */ 
/*     */   static void init(String hostArg, String portArg)
/*     */   {
/*  81 */     host = hostArg;
/*     */     try {
/*  83 */       port = Integer.parseInt(portArg);
/*     */     }
/*     */     catch (NumberFormatException e) {
/*  86 */       usage("Second argument " + portArg + " is not a valid integer.");
/*     */     }
/*     */   }
/*     */ 
/*     */   static void roll()
/*     */   {
/*     */     try {
/*  93 */       Socket socket = new Socket(host, port);
/*  94 */       DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
/*  95 */       DataInputStream dis = new DataInputStream(socket.getInputStream());
/*  96 */       dos.writeUTF("RollOver");
/*  97 */       String rc = dis.readUTF();
/*  98 */       if ("OK".equals(rc)) {
/*  99 */         cat.info("Roll over signal acknowledged by remote appender.");
/*     */       } else {
/* 101 */         cat.warn("Unexpected return code " + rc + " from remote entity.");
/* 102 */         System.exit(2);
/*     */       }
/*     */     } catch (IOException e) {
/* 105 */       cat.error("Could not send roll signal on host " + host + " port " + port + " .", e);
/*     */ 
/* 107 */       System.exit(2);
/*     */     }
/* 109 */     System.exit(0);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.varia.Roller
 * JD-Core Version:    0.6.2
 */