/*    */ package com.google.gson.internal.bind;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.JsonSyntaxException;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.TypeAdapterFactory;
/*    */ import com.google.gson.reflect.TypeToken;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.text.DateFormat;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ 
/*    */ public final class SqlDateTypeAdapter extends TypeAdapter<java.sql.Date>
/*    */ {
/* 39 */   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
/*    */   {
/*    */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 42 */       return typeToken.getRawType() == java.sql.Date.class ? new SqlDateTypeAdapter() : null;
/*    */     }
/* 39 */   };
/*    */ 
/* 47 */   private final DateFormat format = new SimpleDateFormat("MMM d, yyyy");
/*    */ 
/*    */   public synchronized java.sql.Date read(JsonReader in) throws IOException
/*    */   {
/* 51 */     if (in.peek() == JsonToken.NULL) {
/* 52 */       in.nextNull();
/* 53 */       return null;
/*    */     }
/*    */     try {
/* 56 */       long utilDate = this.format.parse(in.nextString()).getTime();
/* 57 */       return new java.sql.Date(utilDate);
/*    */     } catch (ParseException e) {
/* 59 */       throw new JsonSyntaxException(e);
/*    */     }
/*    */   }
/*    */ 
/*    */   public synchronized void write(JsonWriter out, java.sql.Date value) throws IOException
/*    */   {
/* 65 */     out.value(value == null ? null : this.format.format(value));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.SqlDateTypeAdapter
 * JD-Core Version:    0.6.2
 */