/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.Streams;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.MalformedJsonException;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.util.Iterator;
/*     */ import java.util.NoSuchElementException;
/*     */ 
/*     */ public final class JsonStreamParser
/*     */   implements Iterator<JsonElement>
/*     */ {
/*     */   private final JsonReader parser;
/*     */   private final Object lock;
/*     */ 
/*     */   public JsonStreamParser(String json)
/*     */   {
/*  61 */     this(new StringReader(json));
/*     */   }
/*     */ 
/*     */   public JsonStreamParser(Reader reader)
/*     */   {
/*  69 */     this.parser = new JsonReader(reader);
/*  70 */     this.parser.setLenient(true);
/*  71 */     this.lock = new Object();
/*     */   }
/*     */ 
/*     */   public JsonElement next()
/*     */     throws JsonParseException
/*     */   {
/*  82 */     if (!hasNext()) {
/*  83 */       throw new NoSuchElementException();
/*     */     }
/*     */     try
/*     */     {
/*  87 */       return Streams.parse(this.parser);
/*     */     } catch (StackOverflowError e) {
/*  89 */       throw new JsonParseException("Failed parsing JSON source to Json", e);
/*     */     } catch (OutOfMemoryError e) {
/*  91 */       throw new JsonParseException("Failed parsing JSON source to Json", e);
/*     */     } catch (JsonParseException e) {
/*  93 */       throw ((e.getCause() instanceof EOFException) ? new NoSuchElementException() : e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean hasNext()
/*     */   {
/* 103 */     synchronized (this.lock) {
/*     */       try {
/* 105 */         return this.parser.peek() != JsonToken.END_DOCUMENT;
/*     */       } catch (MalformedJsonException e) {
/* 107 */         throw new JsonSyntaxException(e);
/*     */       } catch (IOException e) {
/* 109 */         throw new JsonIOException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void remove()
/*     */   {
/* 120 */     throw new UnsupportedOperationException();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonStreamParser
 * JD-Core Version:    0.6.2
 */