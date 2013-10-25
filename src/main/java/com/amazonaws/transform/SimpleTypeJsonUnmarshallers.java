/*     */ package com.amazonaws.transform;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import com.amazonaws.util.DateUtils;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.NumberFormat;
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class SimpleTypeJsonUnmarshallers
/*     */ {
/*  36 */   private static DateUtils dateUtils = new DateUtils();
/*     */ 
/*  39 */   private static Log log = LogFactory.getLog(SimpleTypeJsonUnmarshallers.class);
/*     */ 
/*     */   public static class ByteBufferJsonUnmarshaller
/*     */     implements Unmarshaller<ByteBuffer, JsonUnmarshallerContext>
/*     */   {
/*     */     private static ByteBufferJsonUnmarshaller instance;
/*     */ 
/*     */     public ByteBuffer unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 207 */       String base64EncodedString = unmarshallerContext.readText();
/* 208 */       if (base64EncodedString == null) return null;
/*     */       try
/*     */       {
/* 211 */         byte[] base64EncodedBytes = base64EncodedString.getBytes("UTF-8");
/* 212 */         byte[] decodedBytes = Base64.decodeBase64(base64EncodedBytes);
/* 213 */         return ByteBuffer.wrap(decodedBytes);
/*     */       } catch (UnsupportedEncodingException e) {
/* 215 */         throw new AmazonClientException("Unable to unmarshall XML data into a ByteBuffer", e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public static ByteBufferJsonUnmarshaller getInstance()
/*     */     {
/* 221 */       if (instance == null) instance = new ByteBufferJsonUnmarshaller();
/* 222 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DateJsonUnmarshaller
/*     */     implements Unmarshaller<Date, JsonUnmarshallerContext>
/*     */   {
/*     */     private static DateJsonUnmarshaller instance;
/*     */ 
/*     */     public Date unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 183 */       String dateString = unmarshallerContext.readText();
/* 184 */       if (dateString == null) return null;
/*     */       try
/*     */       {
/* 187 */         Number number = DecimalFormat.getInstance(new Locale("en")).parse(dateString);
/* 188 */         return new Date(number.longValue() * 1000L);
/*     */       } catch (ParseException e) {
/* 190 */         String errorMessage = "Unable to parse date '" + dateString + "':  " + e.getMessage();
/* 191 */         throw new AmazonClientException(errorMessage, e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public static DateJsonUnmarshaller getInstance()
/*     */     {
/* 197 */       if (instance == null) instance = new DateJsonUnmarshaller();
/* 198 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ByteJsonUnmarshaller
/*     */     implements Unmarshaller<Byte, JsonUnmarshallerContext>
/*     */   {
/*     */     private static ByteJsonUnmarshaller instance;
/*     */ 
/*     */     public Byte unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 167 */       String byteString = unmarshallerContext.readText();
/* 168 */       return byteString == null ? null : Byte.valueOf(byteString);
/*     */     }
/*     */ 
/*     */     public static ByteJsonUnmarshaller getInstance()
/*     */     {
/* 173 */       if (instance == null) instance = new ByteJsonUnmarshaller();
/* 174 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class LongJsonUnmarshaller
/*     */     implements Unmarshaller<Long, JsonUnmarshallerContext>
/*     */   {
/*     */     private static LongJsonUnmarshaller instance;
/*     */ 
/*     */     public Long unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 151 */       String longString = unmarshallerContext.readText();
/* 152 */       return longString == null ? null : Long.valueOf(Long.parseLong(longString));
/*     */     }
/*     */ 
/*     */     public static LongJsonUnmarshaller getInstance()
/*     */     {
/* 157 */       if (instance == null) instance = new LongJsonUnmarshaller();
/* 158 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FloatJsonUnmarshaller
/*     */     implements Unmarshaller<Float, JsonUnmarshallerContext>
/*     */   {
/*     */     private static FloatJsonUnmarshaller instance;
/*     */ 
/*     */     public Float unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 135 */       String floatString = unmarshallerContext.readText();
/* 136 */       return floatString == null ? null : Float.valueOf(floatString);
/*     */     }
/*     */ 
/*     */     public static FloatJsonUnmarshaller getInstance()
/*     */     {
/* 141 */       if (instance == null) instance = new FloatJsonUnmarshaller();
/* 142 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BooleanJsonUnmarshaller
/*     */     implements Unmarshaller<Boolean, JsonUnmarshallerContext>
/*     */   {
/*     */     private static BooleanJsonUnmarshaller instance;
/*     */ 
/*     */     public Boolean unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 119 */       String booleanString = unmarshallerContext.readText();
/* 120 */       return booleanString == null ? null : Boolean.valueOf(Boolean.parseBoolean(booleanString));
/*     */     }
/*     */ 
/*     */     public static BooleanJsonUnmarshaller getInstance()
/*     */     {
/* 125 */       if (instance == null) instance = new BooleanJsonUnmarshaller();
/* 126 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BigDecimalJsonUnmarshaller
/*     */     implements Unmarshaller<BigDecimal, JsonUnmarshallerContext>
/*     */   {
/*     */     private static BigDecimalJsonUnmarshaller instance;
/*     */ 
/*     */     public BigDecimal unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/* 103 */       String s = unmarshallerContext.readText();
/* 104 */       return s == null ? null : new BigDecimal(s);
/*     */     }
/*     */ 
/*     */     public static BigDecimalJsonUnmarshaller getInstance()
/*     */     {
/* 109 */       if (instance == null) instance = new BigDecimalJsonUnmarshaller();
/* 110 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BigIntegerJsonUnmarshaller
/*     */     implements Unmarshaller<BigInteger, JsonUnmarshallerContext>
/*     */   {
/*     */     private static BigIntegerJsonUnmarshaller instance;
/*     */ 
/*     */     public BigInteger unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  90 */       String intString = unmarshallerContext.readText();
/*  91 */       return intString == null ? null : new BigInteger(intString);
/*     */     }
/*     */ 
/*     */     public static BigIntegerJsonUnmarshaller getInstance()
/*     */     {
/*  96 */       if (instance == null) instance = new BigIntegerJsonUnmarshaller();
/*  97 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class IntegerJsonUnmarshaller
/*     */     implements Unmarshaller<Integer, JsonUnmarshallerContext>
/*     */   {
/*     */     private static IntegerJsonUnmarshaller instance;
/*     */ 
/*     */     public Integer unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  77 */       String intString = unmarshallerContext.readText();
/*  78 */       return intString == null ? null : Integer.valueOf(Integer.parseInt(intString));
/*     */     }
/*     */ 
/*     */     public static IntegerJsonUnmarshaller getInstance()
/*     */     {
/*  83 */       if (instance == null) instance = new IntegerJsonUnmarshaller();
/*  84 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DoubleJsonUnmarshaller
/*     */     implements Unmarshaller<Double, JsonUnmarshallerContext>
/*     */   {
/*     */     private static DoubleJsonUnmarshaller instance;
/*     */ 
/*     */     public Double unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  61 */       String doubleString = unmarshallerContext.readText();
/*  62 */       return doubleString == null ? null : Double.valueOf(Double.parseDouble(doubleString));
/*     */     }
/*     */ 
/*     */     public static DoubleJsonUnmarshaller getInstance()
/*     */     {
/*  67 */       if (instance == null) instance = new DoubleJsonUnmarshaller();
/*  68 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class StringJsonUnmarshaller
/*     */     implements Unmarshaller<String, JsonUnmarshallerContext>
/*     */   {
/*     */     private static StringJsonUnmarshaller instance;
/*     */ 
/*     */     public String unmarshall(JsonUnmarshallerContext unmarshallerContext)
/*     */       throws Exception
/*     */     {
/*  46 */       return unmarshallerContext.readText();
/*     */     }
/*     */ 
/*     */     public static StringJsonUnmarshaller getInstance()
/*     */     {
/*  51 */       if (instance == null) instance = new StringJsonUnmarshaller();
/*  52 */       return instance;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.SimpleTypeJsonUnmarshallers
 * JD-Core Version:    0.6.2
 */