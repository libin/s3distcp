/*     */ package com.google.gson;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class JsonArray extends JsonElement
/*     */   implements Iterable<JsonElement>
/*     */ {
/*     */   private final List<JsonElement> elements;
/*     */ 
/*     */   public JsonArray()
/*     */   {
/*  40 */     this.elements = new ArrayList();
/*     */   }
/*     */ 
/*     */   public void add(JsonElement element)
/*     */   {
/*  49 */     if (element == null) {
/*  50 */       element = JsonNull.INSTANCE;
/*     */     }
/*  52 */     this.elements.add(element);
/*     */   }
/*     */ 
/*     */   public void addAll(JsonArray array)
/*     */   {
/*  61 */     this.elements.addAll(array.elements);
/*     */   }
/*     */ 
/*     */   public int size()
/*     */   {
/*  70 */     return this.elements.size();
/*     */   }
/*     */ 
/*     */   public Iterator<JsonElement> iterator()
/*     */   {
/*  80 */     return this.elements.iterator();
/*     */   }
/*     */ 
/*     */   public JsonElement get(int i)
/*     */   {
/*  92 */     return (JsonElement)this.elements.get(i);
/*     */   }
/*     */ 
/*     */   public Number getAsNumber()
/*     */   {
/* 105 */     if (this.elements.size() == 1) {
/* 106 */       return ((JsonElement)this.elements.get(0)).getAsNumber();
/*     */     }
/* 108 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public String getAsString()
/*     */   {
/* 121 */     if (this.elements.size() == 1) {
/* 122 */       return ((JsonElement)this.elements.get(0)).getAsString();
/*     */     }
/* 124 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public double getAsDouble()
/*     */   {
/* 137 */     if (this.elements.size() == 1) {
/* 138 */       return ((JsonElement)this.elements.get(0)).getAsDouble();
/*     */     }
/* 140 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public BigDecimal getAsBigDecimal()
/*     */   {
/* 154 */     if (this.elements.size() == 1) {
/* 155 */       return ((JsonElement)this.elements.get(0)).getAsBigDecimal();
/*     */     }
/* 157 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public BigInteger getAsBigInteger()
/*     */   {
/* 171 */     if (this.elements.size() == 1) {
/* 172 */       return ((JsonElement)this.elements.get(0)).getAsBigInteger();
/*     */     }
/* 174 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public float getAsFloat()
/*     */   {
/* 187 */     if (this.elements.size() == 1) {
/* 188 */       return ((JsonElement)this.elements.get(0)).getAsFloat();
/*     */     }
/* 190 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public long getAsLong()
/*     */   {
/* 203 */     if (this.elements.size() == 1) {
/* 204 */       return ((JsonElement)this.elements.get(0)).getAsLong();
/*     */     }
/* 206 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public int getAsInt()
/*     */   {
/* 219 */     if (this.elements.size() == 1) {
/* 220 */       return ((JsonElement)this.elements.get(0)).getAsInt();
/*     */     }
/* 222 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public byte getAsByte()
/*     */   {
/* 227 */     if (this.elements.size() == 1) {
/* 228 */       return ((JsonElement)this.elements.get(0)).getAsByte();
/*     */     }
/* 230 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public char getAsCharacter()
/*     */   {
/* 235 */     if (this.elements.size() == 1) {
/* 236 */       return ((JsonElement)this.elements.get(0)).getAsCharacter();
/*     */     }
/* 238 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public short getAsShort()
/*     */   {
/* 251 */     if (this.elements.size() == 1) {
/* 252 */       return ((JsonElement)this.elements.get(0)).getAsShort();
/*     */     }
/* 254 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public boolean getAsBoolean()
/*     */   {
/* 267 */     if (this.elements.size() == 1) {
/* 268 */       return ((JsonElement)this.elements.get(0)).getAsBoolean();
/*     */     }
/* 270 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 275 */     return (o == this) || (((o instanceof JsonArray)) && (((JsonArray)o).elements.equals(this.elements)));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 280 */     return this.elements.hashCode();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonArray
 * JD-Core Version:    0.6.2
 */