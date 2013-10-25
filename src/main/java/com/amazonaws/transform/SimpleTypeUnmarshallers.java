/*     */ package com.amazonaws.transform;
/*     */ 
/*     */ import com.amazonaws.util.XpathUtils;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Date;
/*     */ import org.w3c.dom.Node;
/*     */ 
/*     */ public class SimpleTypeUnmarshallers
/*     */ {
/*     */   public static class ByteBufferUnmarshaller
/*     */     implements Unmarshaller<ByteBuffer, Node>
/*     */   {
/*     */     private static ByteBufferUnmarshaller instance;
/*     */ 
/*     */     public ByteBuffer unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/* 154 */       return XpathUtils.asByteBuffer(".", in);
/*     */     }
/*     */ 
/*     */     public static ByteBufferUnmarshaller getInstance()
/*     */     {
/* 159 */       if (instance == null) instance = new ByteBufferUnmarshaller();
/* 160 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DateUnmarshaller
/*     */     implements Unmarshaller<Date, Node>
/*     */   {
/*     */     private static DateUnmarshaller instance;
/*     */ 
/*     */     public Date unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/* 139 */       return XpathUtils.asDate(".", in);
/*     */     }
/*     */ 
/*     */     public static DateUnmarshaller getInstance()
/*     */     {
/* 144 */       if (instance == null) instance = new DateUnmarshaller();
/* 145 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class ByteUnmarshaller
/*     */     implements Unmarshaller<Byte, Node>
/*     */   {
/*     */     private static ByteUnmarshaller instance;
/*     */ 
/*     */     public Byte unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/* 124 */       return XpathUtils.asByte(".", in);
/*     */     }
/*     */ 
/*     */     public static ByteUnmarshaller getInstance()
/*     */     {
/* 129 */       if (instance == null) instance = new ByteUnmarshaller();
/* 130 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class LongUnmarshaller
/*     */     implements Unmarshaller<Long, Node>
/*     */   {
/*     */     private static LongUnmarshaller instance;
/*     */ 
/*     */     public Long unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/* 109 */       return XpathUtils.asLong(".", in);
/*     */     }
/*     */ 
/*     */     public static LongUnmarshaller getInstance()
/*     */     {
/* 114 */       if (instance == null) instance = new LongUnmarshaller();
/* 115 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class FloatUnmarshaller
/*     */     implements Unmarshaller<Float, Node>
/*     */   {
/*     */     private static FloatUnmarshaller instance;
/*     */ 
/*     */     public Float unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/*  94 */       return XpathUtils.asFloat(".", in);
/*     */     }
/*     */ 
/*     */     public static FloatUnmarshaller getInstance()
/*     */     {
/*  99 */       if (instance == null) instance = new FloatUnmarshaller();
/* 100 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class BooleanUnmarshaller
/*     */     implements Unmarshaller<Boolean, Node>
/*     */   {
/*     */     private static BooleanUnmarshaller instance;
/*     */ 
/*     */     public Boolean unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/*  79 */       return XpathUtils.asBoolean(".", in);
/*     */     }
/*     */ 
/*     */     public static BooleanUnmarshaller getInstance()
/*     */     {
/*  84 */       if (instance == null) instance = new BooleanUnmarshaller();
/*  85 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class IntegerUnmarshaller
/*     */     implements Unmarshaller<Integer, Node>
/*     */   {
/*     */     private static IntegerUnmarshaller instance;
/*     */ 
/*     */     public Integer unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/*  64 */       return XpathUtils.asInteger(".", in);
/*     */     }
/*     */ 
/*     */     public static IntegerUnmarshaller getInstance()
/*     */     {
/*  69 */       if (instance == null) instance = new IntegerUnmarshaller();
/*  70 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class DoubleUnmarshaller
/*     */     implements Unmarshaller<Double, Node>
/*     */   {
/*     */     private static DoubleUnmarshaller instance;
/*     */ 
/*     */     public Double unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/*  49 */       return XpathUtils.asDouble(".", in);
/*     */     }
/*     */ 
/*     */     public static DoubleUnmarshaller getInstance()
/*     */     {
/*  54 */       if (instance == null) instance = new DoubleUnmarshaller();
/*  55 */       return instance;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static class StringUnmarshaller
/*     */     implements Unmarshaller<String, Node>
/*     */   {
/*     */     private static StringUnmarshaller instance;
/*     */ 
/*     */     public String unmarshall(Node in)
/*     */       throws Exception
/*     */     {
/*  34 */       return XpathUtils.asString(".", in);
/*     */     }
/*     */ 
/*     */     public static StringUnmarshaller getInstance()
/*     */     {
/*  39 */       if (instance == null) instance = new StringUnmarshaller();
/*  40 */       return instance;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.SimpleTypeUnmarshallers
 * JD-Core Version:    0.6.2
 */