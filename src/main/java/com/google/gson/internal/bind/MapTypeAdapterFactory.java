/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonIOException;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.internal..Gson.Types;
/*     */ import com.google.gson.internal.ConstructorConstructor;
/*     */ import com.google.gson.internal.JsonReaderInternalAccess;
/*     */ import com.google.gson.internal.ObjectConstructor;
/*     */ import com.google.gson.internal.Streams;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ 
/*     */ public final class MapTypeAdapterFactory
/*     */   implements TypeAdapterFactory
/*     */ {
/*     */   private final ConstructorConstructor constructorConstructor;
/*     */   private final boolean complexMapKeySerialization;
/*     */ 
/*     */   public MapTypeAdapterFactory(ConstructorConstructor constructorConstructor, boolean complexMapKeySerialization)
/*     */   {
/* 112 */     this.constructorConstructor = constructorConstructor;
/* 113 */     this.complexMapKeySerialization = complexMapKeySerialization;
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 117 */     Type type = typeToken.getType();
/*     */ 
/* 119 */     Class rawType = typeToken.getRawType();
/* 120 */     if (!Map.class.isAssignableFrom(rawType)) {
/* 121 */       return null;
/*     */     }
/*     */ 
/* 124 */     Class rawTypeOfSrc = .Gson.Types.getRawType(type);
/* 125 */     Type[] keyAndValueTypes = .Gson.Types.getMapKeyAndValueTypes(type, rawTypeOfSrc);
/* 126 */     TypeAdapter keyAdapter = getKeyAdapter(gson, keyAndValueTypes[0]);
/* 127 */     TypeAdapter valueAdapter = gson.getAdapter(TypeToken.get(keyAndValueTypes[1]));
/* 128 */     ObjectConstructor constructor = this.constructorConstructor.getConstructor(typeToken);
/*     */ 
/* 132 */     TypeAdapter result = new Adapter(gson, keyAndValueTypes[0], keyAdapter, keyAndValueTypes[1], valueAdapter, constructor);
/*     */ 
/* 134 */     return result;
/*     */   }
/*     */ 
/*     */   private TypeAdapter<?> getKeyAdapter(Gson context, Type keyType)
/*     */   {
/* 141 */     return (keyType == Boolean.TYPE) || (keyType == Boolean.class) ? TypeAdapters.BOOLEAN_AS_STRING : context.getAdapter(TypeToken.get(keyType));
/*     */   }
/*     */ 
/*     */   private static <T> JsonElement toJsonTree(TypeAdapter<T> typeAdapter, T value)
/*     */   {
/*     */     try
/*     */     {
/* 269 */       JsonTreeWriter jsonWriter = new JsonTreeWriter();
/* 270 */       jsonWriter.setLenient(true);
/* 271 */       typeAdapter.write(jsonWriter, value);
/* 272 */       return jsonWriter.get();
/*     */     } catch (IOException e) {
/* 274 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class Adapter<K, V> extends TypeAdapter<Map<K, V>>
/*     */   {
/*     */     private final TypeAdapter<K> keyTypeAdapter;
/*     */     private final TypeAdapter<V> valueTypeAdapter;
/*     */     private final ObjectConstructor<? extends Map<K, V>> constructor;
/*     */ 
/*     */     public Adapter(Type context, TypeAdapter<K> keyType, Type keyTypeAdapter, TypeAdapter<V> valueType, ObjectConstructor<? extends Map<K, V>> valueTypeAdapter)
/*     */     {
/* 154 */       this.keyTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, keyTypeAdapter, keyType);
/*     */ 
/* 156 */       this.valueTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, valueTypeAdapter, valueType);
/*     */ 
/* 158 */       this.constructor = constructor;
/*     */     }
/*     */ 
/*     */     public Map<K, V> read(JsonReader in) throws IOException {
/* 162 */       JsonToken peek = in.peek();
/* 163 */       if (peek == JsonToken.NULL) {
/* 164 */         in.nextNull();
/* 165 */         return null;
/*     */       }
/*     */ 
/* 168 */       Map map = (Map)this.constructor.construct();
/*     */ 
/* 170 */       if (peek == JsonToken.BEGIN_ARRAY) {
/* 171 */         in.beginArray();
/* 172 */         while (in.hasNext()) {
/* 173 */           in.beginArray();
/* 174 */           Object key = this.keyTypeAdapter.read(in);
/* 175 */           Object value = this.valueTypeAdapter.read(in);
/* 176 */           Object replaced = map.put(key, value);
/* 177 */           if (replaced != null) {
/* 178 */             throw new JsonSyntaxException("duplicate key: " + key);
/*     */           }
/* 180 */           in.endArray();
/*     */         }
/* 182 */         in.endArray();
/*     */       } else {
/* 184 */         in.beginObject();
/* 185 */         while (in.hasNext()) {
/* 186 */           JsonReaderInternalAccess.INSTANCE.promoteNameToValue(in);
/* 187 */           Object key = this.keyTypeAdapter.read(in);
/* 188 */           Object value = this.valueTypeAdapter.read(in);
/* 189 */           Object replaced = map.put(key, value);
/* 190 */           if (replaced != null) {
/* 191 */             throw new JsonSyntaxException("duplicate key: " + key);
/*     */           }
/*     */         }
/* 194 */         in.endObject();
/*     */       }
/* 196 */       return map;
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Map<K, V> map) throws IOException {
/* 200 */       if (map == null) {
/* 201 */         out.nullValue();
/* 202 */         return;
/*     */       }
/*     */ 
/* 205 */       if (!MapTypeAdapterFactory.this.complexMapKeySerialization) {
/* 206 */         out.beginObject();
/* 207 */         for (Map.Entry entry : map.entrySet()) {
/* 208 */           out.name(String.valueOf(entry.getKey()));
/* 209 */           this.valueTypeAdapter.write(out, entry.getValue());
/*     */         }
/* 211 */         out.endObject();
/* 212 */         return;
/*     */       }
/*     */ 
/* 215 */       boolean hasComplexKeys = false;
/* 216 */       List keys = new ArrayList(map.size());
/*     */ 
/* 218 */       List values = new ArrayList(map.size());
/* 219 */       for (Map.Entry entry : map.entrySet()) {
/* 220 */         JsonElement keyElement = MapTypeAdapterFactory.toJsonTree(this.keyTypeAdapter, entry.getKey());
/* 221 */         keys.add(keyElement);
/* 222 */         values.add(entry.getValue());
/* 223 */         hasComplexKeys |= ((keyElement.isJsonArray()) || (keyElement.isJsonObject()));
/*     */       }
/*     */ 
/* 226 */       if (hasComplexKeys) {
/* 227 */         out.beginArray();
/* 228 */         for (int i = 0; i < keys.size(); i++) {
/* 229 */           out.beginArray();
/* 230 */           Streams.write((JsonElement)keys.get(i), out);
/* 231 */           this.valueTypeAdapter.write(out, values.get(i));
/* 232 */           out.endArray();
/*     */         }
/* 234 */         out.endArray();
/*     */       } else {
/* 236 */         out.beginObject();
/* 237 */         for (int i = 0; i < keys.size(); i++) {
/* 238 */           JsonElement keyElement = (JsonElement)keys.get(i);
/* 239 */           out.name(keyToString(keyElement));
/* 240 */           this.valueTypeAdapter.write(out, values.get(i));
/*     */         }
/* 242 */         out.endObject();
/*     */       }
/*     */     }
/*     */ 
/*     */     private String keyToString(JsonElement keyElement) {
/* 247 */       if (keyElement.isJsonPrimitive()) {
/* 248 */         JsonPrimitive primitive = keyElement.getAsJsonPrimitive();
/* 249 */         if (primitive.isNumber())
/* 250 */           return String.valueOf(primitive.getAsNumber());
/* 251 */         if (primitive.isBoolean())
/* 252 */           return Boolean.toString(primitive.getAsBoolean());
/* 253 */         if (primitive.isString()) {
/* 254 */           return primitive.getAsString();
/*     */         }
/* 256 */         throw new AssertionError();
/*     */       }
/* 258 */       if (keyElement.isJsonNull()) {
/* 259 */         return "null";
/*     */       }
/* 261 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.MapTypeAdapterFactory
 * JD-Core Version:    0.6.2
 */