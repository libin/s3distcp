/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.io.FilterWriter;
/*    */ import java.io.Writer;
/*    */ import org.apache.log4j.spi.ErrorHandler;
/*    */ 
/*    */ public class QuietWriter extends FilterWriter
/*    */ {
/*    */   protected ErrorHandler errorHandler;
/*    */ 
/*    */   public QuietWriter(Writer writer, ErrorHandler errorHandler)
/*    */   {
/* 40 */     super(writer);
/* 41 */     setErrorHandler(errorHandler);
/*    */   }
/*    */ 
/*    */   public void write(String string)
/*    */   {
/* 46 */     if (string != null)
/*    */       try {
/* 48 */         this.out.write(string);
/*    */       } catch (Exception e) {
/* 50 */         this.errorHandler.error("Failed to write [" + string + "].", e, 1);
/*    */       }
/*    */   }
/*    */ 
/*    */   public void flush()
/*    */   {
/*    */     try
/*    */     {
/* 59 */       this.out.flush();
/*    */     } catch (Exception e) {
/* 61 */       this.errorHandler.error("Failed to flush writer,", e, 2);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void setErrorHandler(ErrorHandler eh)
/*    */   {
/* 69 */     if (eh == null)
/*    */     {
/* 71 */       throw new IllegalArgumentException("Attempted to set null ErrorHandler.");
/*    */     }
/* 73 */     this.errorHandler = eh;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.QuietWriter
 * JD-Core Version:    0.6.2
 */