/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.net.Socket;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ 
/*     */ class HUPNode
/*     */   implements Runnable
/*     */ {
/*     */   Socket socket;
/*     */   DataInputStream dis;
/*     */   DataOutputStream dos;
/*     */   ExternallyRolledFileAppender er;
/*     */ 
/*     */   public HUPNode(Socket socket, ExternallyRolledFileAppender er)
/*     */   {
/* 150 */     this.socket = socket;
/* 151 */     this.er = er;
/*     */     try {
/* 153 */       this.dis = new DataInputStream(socket.getInputStream());
/* 154 */       this.dos = new DataOutputStream(socket.getOutputStream());
/*     */     } catch (InterruptedIOException e) {
/* 156 */       Thread.currentThread().interrupt();
/* 157 */       e.printStackTrace();
/*     */     } catch (IOException e) {
/* 159 */       e.printStackTrace();
/*     */     } catch (RuntimeException e) {
/* 161 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   public void run() {
/*     */     try {
/* 167 */       String line = this.dis.readUTF();
/* 168 */       LogLog.debug("Got external roll over signal.");
/* 169 */       if ("RollOver".equals(line)) {
/* 170 */         synchronized (this.er) {
/* 171 */           this.er.rollOver();
/*     */         }
/* 173 */         this.dos.writeUTF("OK");
/*     */       }
/*     */       else {
/* 176 */         this.dos.writeUTF("Expecting [RollOver] string.");
/*     */       }
/* 178 */       this.dos.close();
/*     */     } catch (InterruptedIOException e) {
/* 180 */       Thread.currentThread().interrupt();
/* 181 */       LogLog.error("Unexpected exception. Exiting HUPNode.", e);
/*     */     } catch (IOException e) {
/* 183 */       LogLog.error("Unexpected exception. Exiting HUPNode.", e);
/*     */     } catch (RuntimeException e) {
/* 185 */       LogLog.error("Unexpected exception. Exiting HUPNode.", e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.varia.HUPNode
 * JD-Core Version:    0.6.2
 */