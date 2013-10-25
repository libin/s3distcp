/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonNull;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class JsonTreeWriter extends JsonWriter
/*     */ {
/*  34 */   private static final Writer UNWRITABLE_WRITER = new Writer() {
/*     */     public void write(char[] buffer, int offset, int counter) {
/*  36 */       throw new AssertionError();
/*     */     }
/*     */     public void flush() throws IOException {
/*  39 */       throw new AssertionError();
/*     */     }
/*     */     public void close() throws IOException {
/*  42 */       throw new AssertionError();
/*     */     }
/*  34 */   };
/*     */ 
/*  46 */   private static final JsonPrimitive SENTINEL_CLOSED = new JsonPrimitive("closed");
/*     */ 
/*  49 */   private final List<JsonElement> stack = new ArrayList();
/*     */   private String pendingName;
/*  55 */   private JsonElement product = JsonNull.INSTANCE;
/*     */ 
/*     */   public JsonTreeWriter() {
/*  58 */     super(UNWRITABLE_WRITER);
/*     */   }
/*     */ 
/*     */   public JsonElement get()
/*     */   {
/*  65 */     if (!this.stack.isEmpty()) {
/*  66 */       throw new IllegalStateException("Expected one JSON element but was " + this.stack);
/*     */     }
/*  68 */     return this.product;
/*     */   }
/*     */ 
/*     */   private JsonElement peek() {
/*  72 */     return (JsonElement)this.stack.get(this.stack.size() - 1);
/*     */   }
/*     */ 
/*     */   private void put(JsonElement value) {
/*  76 */     if (this.pendingName != null) {
/*  77 */       if ((!value.isJsonNull()) || (getSerializeNulls())) {
/*  78 */         JsonObject object = (JsonObject)peek();
/*  79 */         object.add(this.pendingName, value);
/*     */       }
/*  81 */       this.pendingName = null;
/*  82 */     } else if (this.stack.isEmpty()) {
/*  83 */       this.product = value;
/*     */     } else {
/*  85 */       JsonElement element = peek();
/*  86 */       if ((element instanceof JsonArray))
/*  87 */         ((JsonArray)element).add(value);
/*     */       else
/*  89 */         throw new IllegalStateException();
/*     */     }
/*     */   }
/*     */ 
/*     */   public JsonWriter beginArray() throws IOException
/*     */   {
/*  95 */     JsonArray array = new JsonArray();
/*  96 */     put(array);
/*  97 */     this.stack.add(array);
/*  98 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter endArray() throws IOException {
/* 102 */     if ((this.stack.isEmpty()) || (this.pendingName != null)) {
/* 103 */       throw new IllegalStateException();
/*     */     }
/* 105 */     JsonElement element = peek();
/* 106 */     if ((element instanceof JsonArray)) {
/* 107 */       this.stack.remove(this.stack.size() - 1);
/* 108 */       return this;
/*     */     }
/* 110 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public JsonWriter beginObject() throws IOException {
/* 114 */     JsonObject object = new JsonObject();
/* 115 */     put(object);
/* 116 */     this.stack.add(object);
/* 117 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter endObject() throws IOException {
/* 121 */     if ((this.stack.isEmpty()) || (this.pendingName != null)) {
/* 122 */       throw new IllegalStateException();
/*     */     }
/* 124 */     JsonElement element = peek();
/* 125 */     if ((element instanceof JsonObject)) {
/* 126 */       this.stack.remove(this.stack.size() - 1);
/* 127 */       return this;
/*     */     }
/* 129 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public JsonWriter name(String name) throws IOException {
/* 133 */     if ((this.stack.isEmpty()) || (this.pendingName != null)) {
/* 134 */       throw new IllegalStateException();
/*     */     }
/* 136 */     JsonElement element = peek();
/* 137 */     if ((element instanceof JsonObject)) {
/* 138 */       this.pendingName = name;
/* 139 */       return this;
/*     */     }
/* 141 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public JsonWriter value(String value) throws IOException {
/* 145 */     if (value == null) {
/* 146 */       return nullValue();
/*     */     }
/* 148 */     put(new JsonPrimitive(value));
/* 149 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter nullValue() throws IOException {
/* 153 */     put(JsonNull.INSTANCE);
/* 154 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(boolean value) throws IOException {
/* 158 */     put(new JsonPrimitive(Boolean.valueOf(value)));
/* 159 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(double value) throws IOException {
/* 163 */     if ((!isLenient()) && ((Double.isNaN(value)) || (Double.isInfinite(value)))) {
/* 164 */       throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
/*     */     }
/* 166 */     put(new JsonPrimitive(Double.valueOf(value)));
/* 167 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(long value) throws IOException {
/* 171 */     put(new JsonPrimitive(Long.valueOf(value)));
/* 172 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(Number value) throws IOException {
/* 176 */     if (value == null) {
/* 177 */       return nullValue();
/*     */     }
/*     */ 
/* 180 */     if (!isLenient()) {
/* 181 */       double d = value.doubleValue();
/* 182 */       if ((Double.isNaN(d)) || (Double.isInfinite(d))) {
/* 183 */         throw new IllegalArgumentException("JSON forbids NaN and infinities: " + value);
/*     */       }
/*     */     }
/*     */ 
/* 187 */     put(new JsonPrimitive(value));
/* 188 */     return this;
/*     */   }
/*     */ 
/*     */   public void flush() throws IOException {
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 195 */     if (!this.stack.isEmpty()) {
/* 196 */       throw new IOException("Incomplete document");
/*     */     }
/* 198 */     this.stack.add(SENTINEL_CLOSED);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.JsonTreeWriter
 * JD-Core Version:    0.6.2
 */