/*    */ package com.google.common.io;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.VisibleForTesting;
/*    */ import java.io.Closeable;
/*    */ import java.io.IOException;
/*    */ import java.util.logging.Level;
/*    */ import java.util.logging.Logger;
/*    */ import javax.annotation.Nullable;
/*    */ 
/*    */ @Beta
/*    */ public final class Closeables
/*    */ {
/*    */ 
/*    */   @VisibleForTesting
/* 37 */   static final Logger logger = Logger.getLogger(Closeables.class.getName());
/*    */ 
/*    */   public static void close(@Nullable Closeable closeable, boolean swallowIOException)
/*    */     throws IOException
/*    */   {
/* 76 */     if (closeable == null)
/* 77 */       return;
/*    */     try
/*    */     {
/* 80 */       closeable.close();
/*    */     } catch (IOException e) {
/* 82 */       if (swallowIOException) {
/* 83 */         logger.log(Level.WARNING, "IOException thrown while closing Closeable.", e);
/*    */       }
/*    */       else
/* 86 */         throw e;
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void closeQuietly(@Nullable Closeable closeable)
/*    */   {
/*    */     try
/*    */     {
/* 99 */       close(closeable, true);
/*    */     } catch (IOException e) {
/* 101 */       logger.log(Level.SEVERE, "IOException should not have been thrown.", e);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.Closeables
 * JD-Core Version:    0.6.2
 */