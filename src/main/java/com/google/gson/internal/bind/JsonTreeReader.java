/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonNull;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ 
/*     */ public final class JsonTreeReader extends JsonReader
/*     */ {
/*  40 */   private static final Reader UNREADABLE_READER = new Reader() {
/*     */     public int read(char[] buffer, int offset, int count) throws IOException {
/*  42 */       throw new AssertionError();
/*     */     }
/*     */     public void close() throws IOException {
/*  45 */       throw new AssertionError();
/*     */     }
/*  40 */   };
/*     */ 
/*  48 */   private static final Object SENTINEL_CLOSED = new Object();
/*     */ 
/*  50 */   private final List<Object> stack = new ArrayList();
/*     */ 
/*     */   public JsonTreeReader(JsonElement element) {
/*  53 */     super(UNREADABLE_READER);
/*  54 */     this.stack.add(element);
/*     */   }
/*     */ 
/*     */   public void beginArray() throws IOException {
/*  58 */     expect(JsonToken.BEGIN_ARRAY);
/*  59 */     JsonArray array = (JsonArray)peekStack();
/*  60 */     this.stack.add(array.iterator());
/*     */   }
/*     */ 
/*     */   public void endArray() throws IOException {
/*  64 */     expect(JsonToken.END_ARRAY);
/*  65 */     popStack();
/*  66 */     popStack();
/*     */   }
/*     */ 
/*     */   public void beginObject() throws IOException {
/*  70 */     expect(JsonToken.BEGIN_OBJECT);
/*  71 */     JsonObject object = (JsonObject)peekStack();
/*  72 */     this.stack.add(object.entrySet().iterator());
/*     */   }
/*     */ 
/*     */   public void endObject() throws IOException {
/*  76 */     expect(JsonToken.END_OBJECT);
/*  77 */     popStack();
/*  78 */     popStack();
/*     */   }
/*     */ 
/*     */   public boolean hasNext() throws IOException {
/*  82 */     JsonToken token = peek();
/*  83 */     return (token != JsonToken.END_OBJECT) && (token != JsonToken.END_ARRAY);
/*     */   }
/*     */ 
/*     */   public JsonToken peek() throws IOException {
/*  87 */     if (this.stack.isEmpty()) {
/*  88 */       return JsonToken.END_DOCUMENT;
/*     */     }
/*     */ 
/*  91 */     Object o = peekStack();
/*  92 */     if ((o instanceof Iterator)) {
/*  93 */       boolean isObject = this.stack.get(this.stack.size() - 2) instanceof JsonObject;
/*  94 */       Iterator iterator = (Iterator)o;
/*  95 */       if (iterator.hasNext()) {
/*  96 */         if (isObject) {
/*  97 */           return JsonToken.NAME;
/*     */         }
/*  99 */         this.stack.add(iterator.next());
/* 100 */         return peek();
/*     */       }
/*     */ 
/* 103 */       return isObject ? JsonToken.END_OBJECT : JsonToken.END_ARRAY;
/*     */     }
/* 105 */     if ((o instanceof JsonObject))
/* 106 */       return JsonToken.BEGIN_OBJECT;
/* 107 */     if ((o instanceof JsonArray))
/* 108 */       return JsonToken.BEGIN_ARRAY;
/* 109 */     if ((o instanceof JsonPrimitive)) {
/* 110 */       JsonPrimitive primitive = (JsonPrimitive)o;
/* 111 */       if (primitive.isString())
/* 112 */         return JsonToken.STRING;
/* 113 */       if (primitive.isBoolean())
/* 114 */         return JsonToken.BOOLEAN;
/* 115 */       if (primitive.isNumber()) {
/* 116 */         return JsonToken.NUMBER;
/*     */       }
/* 118 */       throw new AssertionError();
/*     */     }
/* 120 */     if ((o instanceof JsonNull))
/* 121 */       return JsonToken.NULL;
/* 122 */     if (o == SENTINEL_CLOSED) {
/* 123 */       throw new IllegalStateException("JsonReader is closed");
/*     */     }
/* 125 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   private Object peekStack()
/*     */   {
/* 130 */     return this.stack.get(this.stack.size() - 1);
/*     */   }
/*     */ 
/*     */   private Object popStack() {
/* 134 */     return this.stack.remove(this.stack.size() - 1);
/*     */   }
/*     */ 
/*     */   private void expect(JsonToken expected) throws IOException {
/* 138 */     if (peek() != expected)
/* 139 */       throw new IllegalStateException("Expected " + expected + " but was " + peek());
/*     */   }
/*     */ 
/*     */   public String nextName() throws IOException
/*     */   {
/* 144 */     expect(JsonToken.NAME);
/* 145 */     Iterator i = (Iterator)peekStack();
/* 146 */     Map.Entry entry = (Map.Entry)i.next();
/* 147 */     this.stack.add(entry.getValue());
/* 148 */     return (String)entry.getKey();
/*     */   }
/*     */ 
/*     */   public String nextString() throws IOException {
/* 152 */     JsonToken token = peek();
/* 153 */     if ((token != JsonToken.STRING) && (token != JsonToken.NUMBER)) {
/* 154 */       throw new IllegalStateException("Expected " + JsonToken.STRING + " but was " + token);
/*     */     }
/* 156 */     return ((JsonPrimitive)popStack()).getAsString();
/*     */   }
/*     */ 
/*     */   public boolean nextBoolean() throws IOException {
/* 160 */     expect(JsonToken.BOOLEAN);
/* 161 */     return ((JsonPrimitive)popStack()).getAsBoolean();
/*     */   }
/*     */ 
/*     */   public void nextNull() throws IOException {
/* 165 */     expect(JsonToken.NULL);
/* 166 */     popStack();
/*     */   }
/*     */ 
/*     */   public double nextDouble() throws IOException {
/* 170 */     JsonToken token = peek();
/* 171 */     if ((token != JsonToken.NUMBER) && (token != JsonToken.STRING)) {
/* 172 */       throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token);
/*     */     }
/* 174 */     double result = ((JsonPrimitive)peekStack()).getAsDouble();
/* 175 */     if ((!isLenient()) && ((Double.isNaN(result)) || (Double.isInfinite(result)))) {
/* 176 */       throw new NumberFormatException("JSON forbids NaN and infinities: " + result);
/*     */     }
/* 178 */     popStack();
/* 179 */     return result;
/*     */   }
/*     */ 
/*     */   public long nextLong() throws IOException {
/* 183 */     JsonToken token = peek();
/* 184 */     if ((token != JsonToken.NUMBER) && (token != JsonToken.STRING)) {
/* 185 */       throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token);
/*     */     }
/* 187 */     long result = ((JsonPrimitive)peekStack()).getAsLong();
/* 188 */     popStack();
/* 189 */     return result;
/*     */   }
/*     */ 
/*     */   public int nextInt() throws IOException {
/* 193 */     JsonToken token = peek();
/* 194 */     if ((token != JsonToken.NUMBER) && (token != JsonToken.STRING)) {
/* 195 */       throw new IllegalStateException("Expected " + JsonToken.NUMBER + " but was " + token);
/*     */     }
/* 197 */     int result = ((JsonPrimitive)peekStack()).getAsInt();
/* 198 */     popStack();
/* 199 */     return result;
/*     */   }
/*     */ 
/*     */   public void close() throws IOException {
/* 203 */     this.stack.clear();
/* 204 */     this.stack.add(SENTINEL_CLOSED);
/*     */   }
/*     */ 
/*     */   public void skipValue() throws IOException {
/* 208 */     if (peek() == JsonToken.NAME)
/* 209 */       nextName();
/*     */     else
/* 211 */       popStack();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 216 */     return getClass().getSimpleName();
/*     */   }
/*     */ 
/*     */   public void promoteNameToValue() throws IOException {
/* 220 */     expect(JsonToken.NAME);
/* 221 */     Iterator i = (Iterator)peekStack();
/* 222 */     Map.Entry entry = (Map.Entry)i.next();
/* 223 */     this.stack.add(entry.getValue());
/* 224 */     this.stack.add(new JsonPrimitive((String)entry.getKey()));
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.JsonTreeReader
 * JD-Core Version:    0.6.2
 */