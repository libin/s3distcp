/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.bind.JsonTreeReader;
/*     */ import com.google.gson.internal.bind.JsonTreeWriter;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public abstract class TypeAdapter<T>
/*     */ {
/*     */   public abstract void write(JsonWriter paramJsonWriter, T paramT)
/*     */     throws IOException;
/*     */ 
/*     */   final void toJson(Writer out, T value)
/*     */     throws IOException
/*     */   {
/* 140 */     JsonWriter writer = new JsonWriter(out);
/* 141 */     write(writer, value);
/*     */   }
/*     */ 
/*     */   public final TypeAdapter<T> nullSafe()
/*     */   {
/* 185 */     return new TypeAdapter() {
/*     */       public void write(JsonWriter out, T value) throws IOException {
/* 187 */         if (value == null)
/* 188 */           out.nullValue();
/*     */         else
/* 190 */           TypeAdapter.this.write(out, value);
/*     */       }
/*     */ 
/*     */       public T read(JsonReader reader) throws IOException {
/* 194 */         if (reader.peek() == JsonToken.NULL) {
/* 195 */           reader.nextNull();
/* 196 */           return null;
/*     */         }
/* 198 */         return TypeAdapter.this.read(reader);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   final String toJson(T value)
/*     */     throws IOException
/*     */   {
/* 213 */     StringWriter stringWriter = new StringWriter();
/* 214 */     toJson(stringWriter, value);
/* 215 */     return stringWriter.toString();
/*     */   }
/*     */ 
/*     */   final JsonElement toJsonTree(T value)
/*     */   {
/*     */     try
/*     */     {
/* 226 */       JsonTreeWriter jsonWriter = new JsonTreeWriter();
/* 227 */       jsonWriter.setLenient(true);
/* 228 */       write(jsonWriter, value);
/* 229 */       return jsonWriter.get();
/*     */     } catch (IOException e) {
/* 231 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public abstract T read(JsonReader paramJsonReader)
/*     */     throws IOException;
/*     */ 
/*     */   final T fromJson(Reader in)
/*     */     throws IOException
/*     */   {
/* 252 */     JsonReader reader = new JsonReader(in);
/* 253 */     reader.setLenient(true);
/* 254 */     return read(reader);
/*     */   }
/*     */ 
/*     */   final T fromJson(String json)
/*     */     throws IOException
/*     */   {
/* 266 */     return fromJson(new StringReader(json));
/*     */   }
/*     */ 
/*     */   final T fromJsonTree(JsonElement jsonTree)
/*     */   {
/*     */     try
/*     */     {
/* 276 */       JsonReader jsonReader = new JsonTreeReader(jsonTree);
/* 277 */       jsonReader.setLenient(true);
/* 278 */       return read(jsonReader);
/*     */     } catch (IOException e) {
/* 280 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.TypeAdapter
 * JD-Core Version:    0.6.2
 */