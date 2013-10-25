/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.Streams;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.StringWriter;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ public abstract class JsonElement
/*     */ {
/*     */   public boolean isJsonArray()
/*     */   {
/*  40 */     return this instanceof JsonArray;
/*     */   }
/*     */ 
/*     */   public boolean isJsonObject()
/*     */   {
/*  49 */     return this instanceof JsonObject;
/*     */   }
/*     */ 
/*     */   public boolean isJsonPrimitive()
/*     */   {
/*  58 */     return this instanceof JsonPrimitive;
/*     */   }
/*     */ 
/*     */   public boolean isJsonNull()
/*     */   {
/*  68 */     return this instanceof JsonNull;
/*     */   }
/*     */ 
/*     */   public JsonObject getAsJsonObject()
/*     */   {
/*  81 */     if (isJsonObject()) {
/*  82 */       return (JsonObject)this;
/*     */     }
/*  84 */     throw new IllegalStateException("Not a JSON Object: " + this);
/*     */   }
/*     */ 
/*     */   public JsonArray getAsJsonArray()
/*     */   {
/*  97 */     if (isJsonArray()) {
/*  98 */       return (JsonArray)this;
/*     */     }
/* 100 */     throw new IllegalStateException("This is not a JSON Array.");
/*     */   }
/*     */ 
/*     */   public JsonPrimitive getAsJsonPrimitive()
/*     */   {
/* 113 */     if (isJsonPrimitive()) {
/* 114 */       return (JsonPrimitive)this;
/*     */     }
/* 116 */     throw new IllegalStateException("This is not a JSON Primitive.");
/*     */   }
/*     */ 
/*     */   public JsonNull getAsJsonNull()
/*     */   {
/* 130 */     if (isJsonNull()) {
/* 131 */       return (JsonNull)this;
/*     */     }
/* 133 */     throw new IllegalStateException("This is not a JSON Null.");
/*     */   }
/*     */ 
/*     */   public boolean getAsBoolean()
/*     */   {
/* 146 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   Boolean getAsBooleanWrapper()
/*     */   {
/* 159 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public Number getAsNumber()
/*     */   {
/* 172 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public String getAsString()
/*     */   {
/* 185 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public double getAsDouble()
/*     */   {
/* 198 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public float getAsFloat()
/*     */   {
/* 211 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public long getAsLong()
/*     */   {
/* 224 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public int getAsInt()
/*     */   {
/* 237 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public byte getAsByte()
/*     */   {
/* 251 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public char getAsCharacter()
/*     */   {
/* 265 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public BigDecimal getAsBigDecimal()
/*     */   {
/* 279 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public BigInteger getAsBigInteger()
/*     */   {
/* 293 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public short getAsShort()
/*     */   {
/* 306 */     throw new UnsupportedOperationException(getClass().getSimpleName());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*     */     try
/*     */     {
/* 315 */       StringWriter stringWriter = new StringWriter();
/* 316 */       JsonWriter jsonWriter = new JsonWriter(stringWriter);
/* 317 */       jsonWriter.setLenient(true);
/* 318 */       Streams.write(this, jsonWriter);
/* 319 */       return stringWriter.toString();
/*     */     } catch (IOException e) {
/* 321 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonElement
 * JD-Core Version:    0.6.2
 */