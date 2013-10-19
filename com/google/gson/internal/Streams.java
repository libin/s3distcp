/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonIOException;
/*     */ import com.google.gson.JsonNull;
/*     */ import com.google.gson.JsonParseException;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.internal.bind.TypeAdapters;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import com.google.gson.stream.MalformedJsonException;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ 
/*     */ public final class Streams
/*     */ {
/*     */   public static JsonElement parse(JsonReader reader)
/*     */     throws JsonParseException
/*     */   {
/*  40 */     boolean isEmpty = true;
/*     */     try {
/*  42 */       reader.peek();
/*  43 */       isEmpty = false;
/*  44 */       return (JsonElement)TypeAdapters.JSON_ELEMENT.read(reader);
/*     */     }
/*     */     catch (EOFException e)
/*     */     {
/*  50 */       if (isEmpty) {
/*  51 */         return JsonNull.INSTANCE;
/*     */       }
/*  53 */       throw new JsonIOException(e);
/*     */     } catch (MalformedJsonException e) {
/*  55 */       throw new JsonSyntaxException(e);
/*     */     } catch (IOException e) {
/*  57 */       throw new JsonIOException(e);
/*     */     } catch (NumberFormatException e) {
/*  59 */       throw new JsonSyntaxException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void write(JsonElement element, JsonWriter writer)
/*     */     throws IOException
/*     */   {
/*  67 */     TypeAdapters.JSON_ELEMENT.write(writer, element);
/*     */   }
/*     */ 
/*     */   public static Writer writerForAppendable(Appendable appendable) {
/*  71 */     return (appendable instanceof Writer) ? (Writer)appendable : new AppendableWriter(appendable, null);
/*     */   }
/*     */ 
/*     */   private static class AppendableWriter extends Writer
/*     */   {
/*     */     private final Appendable appendable;
/*  80 */     private final CurrentWrite currentWrite = new CurrentWrite();
/*     */ 
/*     */     private AppendableWriter(Appendable appendable) {
/*  83 */       this.appendable = appendable;
/*     */     }
/*     */ 
/*     */     public void write(char[] chars, int offset, int length) throws IOException {
/*  87 */       this.currentWrite.chars = chars;
/*  88 */       this.appendable.append(this.currentWrite, offset, offset + length);
/*     */     }
/*     */ 
/*     */     public void write(int i) throws IOException {
/*  92 */       this.appendable.append((char)i);
/*     */     }
/*     */     public void flush() {
/*     */     }
/*     */ 
/*     */     public void close() {
/*     */     }
/*     */ 
/*     */     static class CurrentWrite implements CharSequence {
/*     */       char[] chars;
/*     */ 
/*     */       public int length() {
/* 104 */         return this.chars.length;
/*     */       }
/*     */       public char charAt(int i) {
/* 107 */         return this.chars[i];
/*     */       }
/*     */       public CharSequence subSequence(int start, int end) {
/* 110 */         return new String(this.chars, start, end - start);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.Streams
 * JD-Core Version:    0.6.2
 */