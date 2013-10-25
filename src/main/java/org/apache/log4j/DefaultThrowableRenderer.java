/*    */ package org.apache.log4j;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InterruptedIOException;
/*    */ import java.io.LineNumberReader;
/*    */ import java.io.PrintWriter;
/*    */ import java.io.StringReader;
/*    */ import java.io.StringWriter;
/*    */ import java.util.ArrayList;
/*    */ import org.apache.log4j.spi.ThrowableRenderer;
/*    */ 
/*    */ public final class DefaultThrowableRenderer
/*    */   implements ThrowableRenderer
/*    */ {
/*    */   public String[] doRender(Throwable throwable)
/*    */   {
/* 48 */     return render(throwable);
/*    */   }
/*    */ 
/*    */   public static String[] render(Throwable throwable)
/*    */   {
/* 57 */     StringWriter sw = new StringWriter();
/* 58 */     PrintWriter pw = new PrintWriter(sw);
/*    */     try {
/* 60 */       throwable.printStackTrace(pw);
/*    */     } catch (RuntimeException ex) {
/*    */     }
/* 63 */     pw.flush();
/* 64 */     LineNumberReader reader = new LineNumberReader(new StringReader(sw.toString()));
/*    */ 
/* 66 */     ArrayList lines = new ArrayList();
/*    */     try {
/* 68 */       String line = reader.readLine();
/* 69 */       while (line != null) {
/* 70 */         lines.add(line);
/* 71 */         line = reader.readLine();
/*    */       }
/*    */     } catch (IOException ex) {
/* 74 */       if ((ex instanceof InterruptedIOException)) {
/* 75 */         Thread.currentThread().interrupt();
/*    */       }
/* 77 */       lines.add(ex.toString());
/*    */     }
/* 79 */     String[] tempRep = new String[lines.size()];
/* 80 */     lines.toArray(tempRep);
/* 81 */     return tempRep;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.DefaultThrowableRenderer
 * JD-Core Version:    0.6.2
 */