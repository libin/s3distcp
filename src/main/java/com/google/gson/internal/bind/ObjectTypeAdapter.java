/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class ObjectTypeAdapter extends TypeAdapter<Object>
/*     */ {
/*  37 */   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
/*     */   {
/*     */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
/*  40 */       if (type.getRawType() == Object.class) {
/*  41 */         return new ObjectTypeAdapter(gson, null);
/*     */       }
/*  43 */       return null;
/*     */     }
/*  37 */   };
/*     */   private final Gson gson;
/*     */ 
/*     */   private ObjectTypeAdapter(Gson gson) {
/*  50 */     this.gson = gson;
/*     */   }
/*     */ 
/*     */   public Object read(JsonReader in) throws IOException {
/*  54 */     JsonToken token = in.peek();
/*  55 */     switch (2.$SwitchMap$com$google$gson$stream$JsonToken[token.ordinal()]) {
/*     */     case 1:
/*  57 */       List list = new ArrayList();
/*  58 */       in.beginArray();
/*  59 */       while (in.hasNext()) {
/*  60 */         list.add(read(in));
/*     */       }
/*  62 */       in.endArray();
/*  63 */       return list;
/*     */     case 2:
/*  66 */       Map map = new LinkedHashMap();
/*  67 */       in.beginObject();
/*  68 */       while (in.hasNext()) {
/*  69 */         map.put(in.nextName(), read(in));
/*     */       }
/*  71 */       in.endObject();
/*  72 */       return map;
/*     */     case 3:
/*  75 */       return in.nextString();
/*     */     case 4:
/*  78 */       return Double.valueOf(in.nextDouble());
/*     */     case 5:
/*  81 */       return Boolean.valueOf(in.nextBoolean());
/*     */     case 6:
/*  84 */       in.nextNull();
/*  85 */       return null;
/*     */     }
/*     */ 
/*  88 */     throw new IllegalStateException();
/*     */   }
/*     */ 
/*     */   public void write(JsonWriter out, Object value) throws IOException
/*     */   {
/*  93 */     if (value == null) {
/*  94 */       out.nullValue();
/*  95 */       return;
/*     */     }
/*     */ 
/*  98 */     TypeAdapter typeAdapter = this.gson.getAdapter(value.getClass());
/*  99 */     if ((typeAdapter instanceof ObjectTypeAdapter)) {
/* 100 */       out.beginObject();
/* 101 */       out.endObject();
/* 102 */       return;
/*     */     }
/*     */ 
/* 105 */     typeAdapter.write(out, value);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.ObjectTypeAdapter
 * JD-Core Version:    0.6.2
 */