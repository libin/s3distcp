/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.Locale;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class BinaryUtils
/*     */ {
/*     */   private static final String DEFAULT_ENCODING = "UTF-8";
/*  35 */   private static final Log log = LogFactory.getLog(BinaryUtils.class);
/*     */ 
/*     */   public static String toHex(byte[] data)
/*     */   {
/*  46 */     StringBuilder sb = new StringBuilder(data.length * 2);
/*  47 */     for (int i = 0; i < data.length; i++) {
/*  48 */       String hex = Integer.toHexString(data[i]);
/*  49 */       if (hex.length() == 1)
/*     */       {
/*  51 */         sb.append("0");
/*  52 */       } else if (hex.length() == 8)
/*     */       {
/*  54 */         hex = hex.substring(6);
/*     */       }
/*  56 */       sb.append(hex);
/*     */     }
/*  58 */     return sb.toString().toLowerCase(Locale.getDefault());
/*     */   }
/*     */ 
/*     */   public static byte[] fromHex(String hexData)
/*     */   {
/*  69 */     byte[] result = new byte[(hexData.length() + 1) / 2];
/*  70 */     String hexNumber = null;
/*  71 */     int stringOffset = 0;
/*  72 */     int byteOffset = 0;
/*  73 */     while (stringOffset < hexData.length()) {
/*  74 */       hexNumber = hexData.substring(stringOffset, stringOffset + 2);
/*  75 */       stringOffset += 2;
/*  76 */       result[(byteOffset++)] = ((byte)Integer.parseInt(hexNumber, 16));
/*     */     }
/*  78 */     return result;
/*     */   }
/*     */ 
/*     */   public static String toBase64(byte[] data)
/*     */   {
/*  89 */     byte[] b64 = Base64.encodeBase64(data);
/*  90 */     return new String(b64);
/*     */   }
/*     */ 
/*     */   public static byte[] fromBase64(String b64Data)
/*     */   {
/*     */     byte[] decoded;
/*     */     try
/*     */     {
/* 104 */       decoded = Base64.decodeBase64(b64Data.getBytes("UTF-8"));
/*     */     }
/*     */     catch (UnsupportedEncodingException uee) {
/* 107 */       log.warn("Tried to Base64-decode a String with the wrong encoding: ", uee);
/* 108 */       decoded = Base64.decodeBase64(b64Data.getBytes());
/*     */     }
/* 110 */     return decoded;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.BinaryUtils
 * JD-Core Version:    0.6.2
 */