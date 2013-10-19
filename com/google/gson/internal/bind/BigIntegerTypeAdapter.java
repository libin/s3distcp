/*    */ package com.google.gson.internal.bind;
/*    */ 
/*    */ import com.google.gson.JsonSyntaxException;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ public final class BigIntegerTypeAdapter extends TypeAdapter<BigInteger>
/*    */ {
/*    */   public BigInteger read(JsonReader in)
/*    */     throws IOException
/*    */   {
/* 36 */     if (in.peek() == JsonToken.NULL) {
/* 37 */       in.nextNull();
/* 38 */       return null;
/*    */     }
/*    */     try {
/* 41 */       return new BigInteger(in.nextString());
/*    */     } catch (NumberFormatException e) {
/* 43 */       throw new JsonSyntaxException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public void write(JsonWriter out, BigInteger value) throws IOException
/*    */   {
/* 49 */     out.value(value);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.BigIntegerTypeAdapter
 * JD-Core Version:    0.6.2
 */