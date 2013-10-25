/*     */ package com.amazonaws.transform;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.util.DateUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class SimpleTypeStaxUnmarshallers
/*     */ {
/*  37 */   private static DateUtils dateUtils = new DateUtils();
/*     */ 
/*  40 */   private static Log log = LogFactory.getLog(SimpleTypeStaxUnmarshallers.class);
/*     */ 
/*     */   public static class ByteBufferStaxUnmarshaller
/*     */     implements Unmarshaller<ByteBuffer, StaxUnmarshallerContext>
/*     */   {
/*     */     private static ByteBufferStaxUnmarshaller instance;
/*     */ 
/*     */     public ByteBuffer unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 209 */       String base64EncodedString = unmarshallerContext.readText();
/* 210 */       if (base64EncodedString == null) return null;
/*     */       try
/*     */       {
/* 213 */         byte[] base64EncodedBytes = base64EncodedString.getBytes("UTF-8");
/* 214 */         byte[] decodedBytes = Base64.decodeBase64(base64EncodedBytes);
/* 215 */         return ByteBuffer.wrap(decodedBytes);
/*     */       } catch (UnsupportedEncodingException e) {
/* 217 */         throw new AmazonClientException("Unable to unmarshall XML data into a ByteBuffer", e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public static ByteBufferStaxUnmarshaller getInstance()
/*     */     {
/* 223 */       if (instance == null) instance = new ByteBufferStaxUnmarshaller();
/* 224 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DateStaxUnmarshaller
/*     */     implements Unmarshaller<Date, StaxUnmarshallerContext>
/*     */   {
/*     */     private static DateStaxUnmarshaller instance;
/*     */ 
/*     */     public Date unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 186 */       String dateString = unmarshallerContext.readText();
/* 187 */       if (dateString == null) return null;
/*     */       try
/*     */       {
/* 190 */         return SimpleTypeStaxUnmarshallers.dateUtils.parseIso8601Date(dateString);
/*     */       } catch (ParseException e) {
/* 192 */         SimpleTypeStaxUnmarshallers.log.warn("Unable to parse date '" + dateString + "':  " + e.getMessage(), e);
/* 193 */       }return null;
/*     */     }
/*     */ 
/*     */     public static DateStaxUnmarshaller getInstance()
/*     */     {
/* 199 */       if (instance == null) instance = new DateStaxUnmarshaller();
/* 200 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ByteStaxUnmarshaller
/*     */     implements Unmarshaller<Byte, StaxUnmarshallerContext>
/*     */   {
/*     */     private static ByteStaxUnmarshaller instance;
/*     */ 
/*     */     public Byte unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 170 */       String byteString = unmarshallerContext.readText();
/* 171 */       return byteString == null ? null : Byte.valueOf(byteString);
/*     */     }
/*     */ 
/*     */     public static ByteStaxUnmarshaller getInstance()
/*     */     {
/* 176 */       if (instance == null) instance = new ByteStaxUnmarshaller();
/* 177 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class LongStaxUnmarshaller
/*     */     implements Unmarshaller<Long, StaxUnmarshallerContext>
/*     */   {
/*     */     private static LongStaxUnmarshaller instance;
/*     */ 
/*     */     public Long unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 154 */       String longString = unmarshallerContext.readText();
/* 155 */       return longString == null ? null : Long.valueOf(Long.parseLong(longString));
/*     */     }
/*     */ 
/*     */     public static LongStaxUnmarshaller getInstance()
/*     */     {
/* 160 */       if (instance == null) instance = new LongStaxUnmarshaller();
/* 161 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FloatStaxUnmarshaller
/*     */     implements Unmarshaller<Float, StaxUnmarshallerContext>
/*     */   {
/*     */     private static FloatStaxUnmarshaller instance;
/*     */ 
/*     */     public Float unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 138 */       String floatString = unmarshallerContext.readText();
/* 139 */       return floatString == null ? null : Float.valueOf(floatString);
/*     */     }
/*     */ 
/*     */     public static FloatStaxUnmarshaller getInstance()
/*     */     {
/* 144 */       if (instance == null) instance = new FloatStaxUnmarshaller();
/* 145 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BooleanStaxUnmarshaller
/*     */     implements Unmarshaller<Boolean, StaxUnmarshallerContext>
/*     */   {
/*     */     private static BooleanStaxUnmarshaller instance;
/*     */ 
/*     */     public Boolean unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 122 */       String booleanString = unmarshallerContext.readText();
/* 123 */       return booleanString == null ? null : Boolean.valueOf(Boolean.parseBoolean(booleanString));
/*     */     }
/*     */ 
/*     */     public static BooleanStaxUnmarshaller getInstance()
/*     */     {
/* 128 */       if (instance == null) instance = new BooleanStaxUnmarshaller();
/* 129 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class IntegerStaxUnmarshaller
/*     */     implements Unmarshaller<Integer, StaxUnmarshallerContext>
/*     */   {
/*     */     private static IntegerStaxUnmarshaller instance;
/*     */ 
/*     */     public Integer unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 106 */       String intString = unmarshallerContext.readText();
/* 107 */       return intString == null ? null : Integer.valueOf(Integer.parseInt(intString));
/*     */     }
/*     */ 
/*     */     public static IntegerStaxUnmarshaller getInstance()
/*     */     {
/* 112 */       if (instance == null) instance = new IntegerStaxUnmarshaller();
/* 113 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DoubleStaxUnmarshaller
/*     */     implements Unmarshaller<Double, StaxUnmarshallerContext>
/*     */   {
/*     */     private static DoubleStaxUnmarshaller instance;
/*     */ 
/*     */     public Double unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  90 */       String doubleString = unmarshallerContext.readText();
/*  91 */       return doubleString == null ? null : Double.valueOf(Double.parseDouble(doubleString));
/*     */     }
/*     */ 
/*     */     public static DoubleStaxUnmarshaller getInstance()
/*     */     {
/*  96 */       if (instance == null) instance = new DoubleStaxUnmarshaller();
/*  97 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BigIntegerStaxUnmarshaller
/*     */     implements Unmarshaller<BigInteger, StaxUnmarshallerContext>
/*     */   {
/*     */     private static BigIntegerStaxUnmarshaller instance;
/*     */ 
/*     */     public BigInteger unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  74 */       String s = unmarshallerContext.readText();
/*  75 */       return s == null ? null : new BigInteger(s);
/*     */     }
/*     */ 
/*     */     public static BigIntegerStaxUnmarshaller getInstance()
/*     */     {
/*  80 */       if (instance == null) instance = new BigIntegerStaxUnmarshaller();
/*  81 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BigDecimalStaxUnmarshaller
/*     */     implements Unmarshaller<BigDecimal, StaxUnmarshallerContext>
/*     */   {
/*     */     private static BigDecimalStaxUnmarshaller instance;
/*     */ 
/*     */     public BigDecimal unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  60 */       String s = unmarshallerContext.readText();
/*  61 */       return s == null ? null : new BigDecimal(s);
/*     */     }
/*     */ 
/*     */     public static BigDecimalStaxUnmarshaller getInstance()
/*     */     {
/*  66 */       if (instance == null) instance = new BigDecimalStaxUnmarshaller();
/*  67 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class StringStaxUnmarshaller
/*     */     implements Unmarshaller<String, StaxUnmarshallerContext>
/*     */   {
/*     */     private static StringStaxUnmarshaller instance;
/*     */ 
/*     */     public String unmarshall(StaxUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  47 */       return unmarshallerContext.readText();
/*     */     }
/*     */ 
/*     */     public static StringStaxUnmarshaller getInstance()
/*     */     {
/*  52 */       if (instance == null) instance = new StringStaxUnmarshaller();
/*  53 */       return instance;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.SimpleTypeStaxUnmarshallers
 * JD-Core Version:    0.6.2
 */