/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Date;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ 
/*     */ public class StringUtils
/*     */ {
/*  30 */   private static final DateUtils dateUtils = new DateUtils();
/*     */ 
/*     */   public static Integer toInteger(StringBuilder value) {
/*  33 */     return Integer.valueOf(Integer.parseInt(value.toString()));
/*     */   }
/*     */ 
/*     */   public static String toString(StringBuilder value) {
/*  37 */     return value.toString();
/*     */   }
/*     */ 
/*     */   public static Boolean toBoolean(StringBuilder value) {
/*  41 */     return Boolean.valueOf(Boolean.getBoolean(value.toString()));
/*     */   }
/*     */ 
/*     */   public static String fromInteger(Integer value) {
/*  45 */     return Integer.toString(value.intValue());
/*     */   }
/*     */ 
/*     */   public static String fromLong(Long value) {
/*  49 */     return Long.toString(value.longValue());
/*     */   }
/*     */ 
/*     */   public static String fromString(String value) {
/*  53 */     return value;
/*     */   }
/*     */ 
/*     */   public static String fromBoolean(Boolean value) {
/*  57 */     return Boolean.toString(value.booleanValue());
/*     */   }
/*     */ 
/*     */   public static String fromBigInteger(BigInteger value) {
/*  61 */     return value.toString();
/*     */   }
/*     */ 
/*     */   public static String fromBigDecimal(BigDecimal value) {
/*  65 */     return value.toString();
/*     */   }
/*     */ 
/*     */   public static BigInteger toBigInteger(String s)
/*     */   {
/*  70 */     return new BigInteger(s);
/*     */   }
/*     */ 
/*     */   public static BigDecimal toBigDecimal(String s) {
/*  74 */     return new BigDecimal(s);
/*     */   }
/*     */ 
/*     */   public static String fromFloat(Float value) {
/*  78 */     return Float.toString(value.floatValue());
/*     */   }
/*     */ 
/*     */   public static String fromDate(Date value)
/*     */   {
/*  91 */     return dateUtils.formatIso8601Date(value);
/*     */   }
/*     */ 
/*     */   public static String fromDouble(Double d)
/*     */   {
/* 103 */     return Double.toString(d.doubleValue());
/*     */   }
/*     */ 
/*     */   public static String fromByte(Byte b)
/*     */   {
/* 115 */     return Byte.toString(b.byteValue());
/*     */   }
/*     */ 
/*     */   public static String fromByteBuffer(ByteBuffer byteBuffer)
/*     */   {
/* 128 */     byte[] encodedBytes = null;
/* 129 */     if (byteBuffer.hasArray()) {
/* 130 */       encodedBytes = Base64.encodeBase64(byteBuffer.array());
/*     */     } else {
/* 132 */       byte[] binaryData = new byte[byteBuffer.limit()];
/* 133 */       byteBuffer.get(binaryData);
/* 134 */       encodedBytes = Base64.encodeBase64(binaryData);
/*     */     }
/* 136 */     return new String(encodedBytes);
/*     */   }
/*     */ 
/*     */   public static String replace(String originalString, String partToMatch, String replacement) {
/* 140 */     StringBuffer buffer = new StringBuffer(originalString.length());
/* 141 */     buffer.append(originalString);
/*     */ 
/* 143 */     int indexOf = buffer.indexOf(partToMatch);
/* 144 */     while (indexOf != -1) {
/* 145 */       buffer = buffer.replace(indexOf, indexOf + partToMatch.length(), replacement);
/* 146 */       indexOf = buffer.indexOf(partToMatch);
/*     */     }
/*     */ 
/* 149 */     return buffer.toString();
/*     */   }
/*     */ 
/*     */   public static String join(String joiner, String[] parts)
/*     */   {
/* 159 */     StringBuilder builder = new StringBuilder();
/* 160 */     for (int i = 0; i < parts.length; i++) {
/* 161 */       builder.append(parts[i].toString());
/* 162 */       if (i < parts.length - 1) {
/* 163 */         builder.append(joiner);
/*     */       }
/*     */     }
/* 166 */     return builder.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.StringUtils
 * JD-Core Version:    0.6.2
 */