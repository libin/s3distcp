/*    */ package com.amazonaws.util;
/*    */ 
/*    */ import java.io.BufferedInputStream;
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.PrintStream;
/*    */ import java.security.MessageDigest;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ 
/*    */ public class Md5Utils
/*    */ {
/*    */   public static byte[] computeMD5Hash(InputStream is)
/*    */     throws NoSuchAlgorithmException, IOException
/*    */   {
/* 37 */     BufferedInputStream bis = new BufferedInputStream(is);
/*    */     try {
/* 39 */       MessageDigest messageDigest = MessageDigest.getInstance("MD5");
/* 40 */       byte[] buffer = new byte[16384];
/* 41 */       int bytesRead = -1;
/* 42 */       while ((bytesRead = bis.read(buffer, 0, buffer.length)) != -1) {
/* 43 */         messageDigest.update(buffer, 0, bytesRead);
/*    */       }
/* 45 */       return messageDigest.digest();
/*    */     } finally {
/*    */       try {
/* 48 */         bis.close();
/*    */       } catch (Exception e) {
/* 50 */         System.err.println("Unable to close input stream of hash candidate: " + e);
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static byte[] computeMD5Hash(byte[] data)
/*    */     throws NoSuchAlgorithmException, IOException
/*    */   {
/* 60 */     return computeMD5Hash(new ByteArrayInputStream(data));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.Md5Utils
 * JD-Core Version:    0.6.2
 */