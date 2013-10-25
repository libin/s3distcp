/*    */ package com.google.gson;
/*    */ 
/*    */ import com.google.gson.internal.Streams;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.MalformedJsonException;
/*    */ import java.io.EOFException;
/*    */ import java.io.IOException;
/*    */ import java.io.Reader;
/*    */ import java.io.StringReader;
/*    */ 
/*    */ public final class JsonParser
/*    */ {
/*    */   public JsonElement parse(String json)
/*    */     throws JsonSyntaxException
/*    */   {
/* 45 */     return parse(new StringReader(json));
/*    */   }
/*    */ 
/*    */   public JsonElement parse(Reader json)
/*    */     throws JsonIOException, JsonSyntaxException
/*    */   {
/*    */     try
/*    */     {
/* 58 */       JsonReader jsonReader = new JsonReader(json);
/* 59 */       JsonElement element = parse(jsonReader);
/* 60 */       if ((!element.isJsonNull()) && (jsonReader.peek() != JsonToken.END_DOCUMENT)) {
/* 61 */         throw new JsonSyntaxException("Did not consume the entire document.");
/*    */       }
/* 63 */       return element;
/*    */     } catch (MalformedJsonException e) {
/* 65 */       throw new JsonSyntaxException(e);
/*    */     } catch (IOException e) {
/* 67 */       throw new JsonIOException(e);
/*    */     } catch (NumberFormatException e) {
/* 69 */       throw new JsonSyntaxException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public JsonElement parse(JsonReader json)
/*    */     throws JsonIOException, JsonSyntaxException
/*    */   {
/* 81 */     boolean lenient = json.isLenient();
/* 82 */     json.setLenient(true);
/*    */     try {
/* 84 */       return Streams.parse(json);
/*    */     } catch (StackOverflowError e) {
/* 86 */       throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", e);
/*    */     } catch (OutOfMemoryError e) {
/* 88 */       throw new JsonParseException("Failed parsing JSON source: " + json + " to Json", e);
/*    */     } catch (JsonParseException e) {
/* 90 */       if ((e.getCause() instanceof EOFException)) {
/* 91 */         return JsonNull.INSTANCE;
/*    */       }
/* 93 */       throw e;
/*    */     } finally {
/* 95 */       json.setLenient(lenient);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonParser
 * JD-Core Version:    0.6.2
 */