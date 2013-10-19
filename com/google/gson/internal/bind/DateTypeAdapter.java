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
/*    */ import java.util.Date;
/*    */ import java.util.Locale;
/*    */ import java.util.TimeZone;
/*    */ 
/*    */ public final class DateTypeAdapter extends TypeAdapter<Date>
/*    */ {
/* 42 */   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
/*    */   {
/*    */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 45 */       return typeToken.getRawType() == Date.class ? new DateTypeAdapter() : null;
/*    */     }
/* 42 */   };
/*    */ 
/* 49 */   private final DateFormat enUsFormat = DateFormat.getDateTimeInstance(2, 2, Locale.US);
/*    */ 
/* 51 */   private final DateFormat localFormat = DateFormat.getDateTimeInstance(2, 2);
/*    */ 
/* 53 */   private final DateFormat iso8601Format = buildIso8601Format();
/*    */ 
/*    */   private static DateFormat buildIso8601Format() {
/* 56 */     DateFormat iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
/* 57 */     iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
/* 58 */     return iso8601Format;
/*    */   }
/*    */ 
/*    */   public Date read(JsonReader in) throws IOException {
/* 62 */     if (in.peek() == JsonToken.NULL) {
/* 63 */       in.nextNull();
/* 64 */       return null;
/*    */     }
/* 66 */     return deserializeToDate(in.nextString());
/*    */   }
/*    */ 
/*    */   private synchronized Date deserializeToDate(String json) {
/*    */     try {
/* 71 */       return this.localFormat.parse(json);
/*    */     }
/*    */     catch (ParseException ignored) {
/*    */       try {
/* 75 */         return this.enUsFormat.parse(json);
/*    */       }
/*    */       catch (ParseException ignored) {
/*    */         try {
/* 79 */           return this.iso8601Format.parse(json);
/*    */         } catch (ParseException e) {
/* 81 */           throw new JsonSyntaxException(json, e); } 
/*    */       }
/*    */     }
/*    */   }
/*    */ 
/* 86 */   public synchronized void write(JsonWriter out, Date value) throws IOException { if (value == null) {
/* 87 */       out.nullValue();
/* 88 */       return;
/*    */     }
/* 90 */     String dateFormatAsString = this.enUsFormat.format(value);
/* 91 */     out.value(dateFormatAsString);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.DateTypeAdapter
 * JD-Core Version:    0.6.2
 */