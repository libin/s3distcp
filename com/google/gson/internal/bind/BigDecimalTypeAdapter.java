/*    */ package com.google.gson.internal.bind;
/*    */ 
/*    */ import com.google.gson.JsonSyntaxException;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.math.BigDecimal;
/*    */ 
/*    */ public final class BigDecimalTypeAdapter extends TypeAdapter<BigDecimal>
/*    */ {
/*    */   public BigDecimal read(JsonReader in)
/*    */     throws IOException
/*    */   {
/* 37 */     if (in.peek() == JsonToken.NULL) {
/* 38 */       in.nextNull();
/* 39 */       return null;
/*    */     }
/*    */     try {
/* 42 */       return new BigDecimal(in.nextString());
/*    */     } catch (NumberFormatException e) {
/* 44 */       throw new JsonSyntaxException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void write(JsonWriter out, BigDecimal value) throws IOException
/*    */   {
/* 50 */     out.value(value);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.BigDecimalTypeAdapter
 * JD-Core Version:    0.6.2
 */