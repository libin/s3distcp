/*     */ package com.google.gson;
/*     */ 
/*     */ import java.lang.reflect.Type;
/*     */ import java.sql.Timestamp;
/*     */ import java.text.DateFormat;
/*     */ import java.text.ParseException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Locale;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ final class DefaultDateTypeAdapter
/*     */   implements JsonSerializer<java.util.Date>, JsonDeserializer<java.util.Date>
/*     */ {
/*     */   private final DateFormat enUsFormat;
/*     */   private final DateFormat localFormat;
/*     */   private final DateFormat iso8601Format;
/*     */ 
/*     */   DefaultDateTypeAdapter()
/*     */   {
/*  44 */     this(DateFormat.getDateTimeInstance(2, 2, Locale.US), DateFormat.getDateTimeInstance(2, 2));
/*     */   }
/*     */ 
/*     */   DefaultDateTypeAdapter(String datePattern)
/*     */   {
/*  49 */     this(new SimpleDateFormat(datePattern, Locale.US), new SimpleDateFormat(datePattern));
/*     */   }
/*     */ 
/*     */   DefaultDateTypeAdapter(int style) {
/*  53 */     this(DateFormat.getDateInstance(style, Locale.US), DateFormat.getDateInstance(style));
/*     */   }
/*     */ 
/*     */   public DefaultDateTypeAdapter(int dateStyle, int timeStyle) {
/*  57 */     this(DateFormat.getDateTimeInstance(dateStyle, timeStyle, Locale.US), DateFormat.getDateTimeInstance(dateStyle, timeStyle));
/*     */   }
/*     */ 
/*     */   DefaultDateTypeAdapter(DateFormat enUsFormat, DateFormat localFormat)
/*     */   {
/*  62 */     this.enUsFormat = enUsFormat;
/*  63 */     this.localFormat = localFormat;
/*  64 */     this.iso8601Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
/*  65 */     this.iso8601Format.setTimeZone(TimeZone.getTimeZone("UTC"));
/*     */   }
/*     */ 
/*     */   public JsonElement serialize(java.util.Date src, Type typeOfSrc, JsonSerializationContext context)
/*     */   {
/*  71 */     synchronized (this.localFormat) {
/*  72 */       String dateFormatAsString = this.enUsFormat.format(src);
/*  73 */       return new JsonPrimitive(dateFormatAsString);
/*     */     }
/*     */   }
/*     */ 
/*     */   public java.util.Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
/*     */   {
/*  79 */     if (!(json instanceof JsonPrimitive)) {
/*  80 */       throw new JsonParseException("The date should be a string value");
/*     */     }
/*  82 */     java.util.Date date = deserializeToDate(json);
/*  83 */     if (typeOfT == java.util.Date.class)
/*  84 */       return date;
/*  85 */     if (typeOfT == Timestamp.class)
/*  86 */       return new Timestamp(date.getTime());
/*  87 */     if (typeOfT == java.sql.Date.class) {
/*  88 */       return new java.sql.Date(date.getTime());
/*     */     }
/*  90 */     throw new IllegalArgumentException(getClass() + " cannot deserialize to " + typeOfT);
/*     */   }
/*     */ 
/*     */   private java.util.Date deserializeToDate(JsonElement json)
/*     */   {
/*  95 */     synchronized (this.localFormat) {
/*     */       try {
/*  97 */         return this.localFormat.parse(json.getAsString());
/*     */       }
/*     */       catch (ParseException ignored) {
/*     */         try {
/* 101 */           return this.enUsFormat.parse(json.getAsString());
/*     */         }
/*     */         catch (ParseException ignored) {
/*     */           try {
/* 105 */             return this.iso8601Format.parse(json.getAsString());
/*     */           } catch (ParseException e) {
/* 107 */             throw new JsonSyntaxException(json.getAsString(), e);
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/* 114 */   public String toString() { StringBuilder sb = new StringBuilder();
/* 115 */     sb.append(DefaultDateTypeAdapter.class.getSimpleName());
/* 116 */     sb.append('(').append(this.localFormat.getClass().getSimpleName()).append(')');
/* 117 */     return sb.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.DefaultDateTypeAdapter
 * JD-Core Version:    0.6.2
 */