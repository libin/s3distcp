/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal..Gson.Preconditions;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public final class JsonObject extends JsonElement
/*     */ {
/*  37 */   private final Map<String, JsonElement> members = new LinkedHashMap();
/*     */ 
/*     */   public void add(String property, JsonElement value)
/*     */   {
/*  54 */     if (value == null) {
/*  55 */       value = JsonNull.INSTANCE;
/*     */     }
/*  57 */     this.members.put(.Gson.Preconditions.checkNotNull(property), value);
/*     */   }
/*     */ 
/*     */   public JsonElement remove(String property)
/*     */   {
/*  68 */     return (JsonElement)this.members.remove(property);
/*     */   }
/*     */ 
/*     */   public void addProperty(String property, String value)
/*     */   {
/*  79 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */   public void addProperty(String property, Number value)
/*     */   {
/*  90 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */   public void addProperty(String property, Boolean value)
/*     */   {
/* 101 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */   public void addProperty(String property, Character value)
/*     */   {
/* 112 */     add(property, createJsonElement(value));
/*     */   }
/*     */ 
/*     */   private JsonElement createJsonElement(Object value)
/*     */   {
/* 122 */     return value == null ? JsonNull.INSTANCE : new JsonPrimitive(value);
/*     */   }
/*     */ 
/*     */   public Set<Map.Entry<String, JsonElement>> entrySet()
/*     */   {
/* 132 */     return this.members.entrySet();
/*     */   }
/*     */ 
/*     */   public boolean has(String memberName)
/*     */   {
/* 142 */     return this.members.containsKey(memberName);
/*     */   }
/*     */ 
/*     */   public JsonElement get(String memberName)
/*     */   {
/* 152 */     if (this.members.containsKey(memberName)) {
/* 153 */       JsonElement member = (JsonElement)this.members.get(memberName);
/* 154 */       return member == null ? JsonNull.INSTANCE : member;
/*     */     }
/* 156 */     return null;
/*     */   }
/*     */ 
/*     */   public JsonPrimitive getAsJsonPrimitive(String memberName)
/*     */   {
/* 166 */     return (JsonPrimitive)this.members.get(memberName);
/*     */   }
/*     */ 
/*     */   public JsonArray getAsJsonArray(String memberName)
/*     */   {
/* 176 */     return (JsonArray)this.members.get(memberName);
/*     */   }
/*     */ 
/*     */   public JsonObject getAsJsonObject(String memberName)
/*     */   {
/* 186 */     return (JsonObject)this.members.get(memberName);
/*     */   }
/*     */ 
/*     */   public boolean equals(Object o)
/*     */   {
/* 191 */     return (o == this) || (((o instanceof JsonObject)) && (((JsonObject)o).members.equals(this.members)));
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 197 */     return this.members.hashCode();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonObject
 * JD-Core Version:    0.6.2
 */