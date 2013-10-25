/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal.ConstructorConstructor;
/*     */ import com.google.gson.internal.Excluder;
/*     */ import com.google.gson.internal.GsonInternalAccess;
/*     */ import com.google.gson.internal.Primitives;
/*     */ import com.google.gson.internal.Streams;
/*     */ import com.google.gson.internal.bind.ArrayTypeAdapter;
/*     */ import com.google.gson.internal.bind.BigDecimalTypeAdapter;
/*     */ import com.google.gson.internal.bind.BigIntegerTypeAdapter;
/*     */ import com.google.gson.internal.bind.CollectionTypeAdapterFactory;
/*     */ import com.google.gson.internal.bind.DateTypeAdapter;
/*     */ import com.google.gson.internal.bind.JsonTreeReader;
/*     */ import com.google.gson.internal.bind.JsonTreeWriter;
/*     */ import com.google.gson.internal.bind.MapTypeAdapterFactory;
/*     */ import com.google.gson.internal.bind.ObjectTypeAdapter;
/*     */ import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
/*     */ import com.google.gson.internal.bind.SqlDateTypeAdapter;
/*     */ import com.google.gson.internal.bind.TimeTypeAdapter;
/*     */ import com.google.gson.internal.bind.TypeAdapters;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import com.google.gson.stream.MalformedJsonException;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
/*     */ import java.lang.reflect.Type;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class Gson
/*     */ {
/*     */   static final boolean DEFAULT_JSON_NON_EXECUTABLE = false;
/*     */   private static final String JSON_NON_EXECUTABLE_PREFIX = ")]}'\n";
/* 111 */   private final ThreadLocal<Map<TypeToken<?>, FutureTypeAdapter<?>>> calls = new ThreadLocal()
/*     */   {
/*     */     protected Map<TypeToken<?>, Gson.FutureTypeAdapter<?>> initialValue() {
/* 114 */       return new HashMap();
/*     */     }
/* 111 */   };
/*     */ 
/* 118 */   private final Map<TypeToken<?>, TypeAdapter<?>> typeTokenCache = Collections.synchronizedMap(new HashMap());
/*     */   private final List<TypeAdapterFactory> factories;
/*     */   private final ConstructorConstructor constructorConstructor;
/*     */   private final boolean serializeNulls;
/*     */   private final boolean htmlSafe;
/*     */   private final boolean generateNonExecutableJson;
/*     */   private final boolean prettyPrinting;
/* 129 */   final JsonDeserializationContext deserializationContext = new JsonDeserializationContext()
/*     */   {
/*     */     public <T> T deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
/* 132 */       return Gson.this.fromJson(json, typeOfT);
/*     */     }
/* 129 */   };
/*     */ 
/* 136 */   final JsonSerializationContext serializationContext = new JsonSerializationContext() {
/*     */     public JsonElement serialize(Object src) {
/* 138 */       return Gson.this.toJsonTree(src);
/*     */     }
/*     */     public JsonElement serialize(Object src, Type typeOfSrc) {
/* 141 */       return Gson.this.toJsonTree(src, typeOfSrc);
/*     */     }
/* 136 */   };
/*     */ 
/*     */   public Gson()
/*     */   {
/* 180 */     this(Excluder.DEFAULT, FieldNamingPolicy.IDENTITY, Collections.emptyMap(), false, false, false, true, false, false, LongSerializationPolicy.DEFAULT, Collections.emptyList());
/*     */   }
/*     */ 
/*     */   Gson(Excluder excluder, FieldNamingStrategy fieldNamingPolicy, Map<Type, InstanceCreator<?>> instanceCreators, boolean serializeNulls, boolean complexMapKeySerialization, boolean generateNonExecutableGson, boolean htmlSafe, boolean prettyPrinting, boolean serializeSpecialFloatingPointValues, LongSerializationPolicy longSerializationPolicy, List<TypeAdapterFactory> typeAdapterFactories)
/*     */   {
/* 192 */     this.constructorConstructor = new ConstructorConstructor(instanceCreators);
/* 193 */     this.serializeNulls = serializeNulls;
/* 194 */     this.generateNonExecutableJson = generateNonExecutableGson;
/* 195 */     this.htmlSafe = htmlSafe;
/* 196 */     this.prettyPrinting = prettyPrinting;
/*     */ 
/* 198 */     TypeAdapterFactory reflectiveTypeAdapterFactory = new ReflectiveTypeAdapterFactory(this.constructorConstructor, fieldNamingPolicy, excluder);
/*     */ 
/* 201 */     ConstructorConstructor constructorConstructor = new ConstructorConstructor();
/* 202 */     List factories = new ArrayList();
/*     */ 
/* 205 */     factories.add(TypeAdapters.STRING_FACTORY);
/* 206 */     factories.add(TypeAdapters.INTEGER_FACTORY);
/* 207 */     factories.add(TypeAdapters.BOOLEAN_FACTORY);
/* 208 */     factories.add(TypeAdapters.BYTE_FACTORY);
/* 209 */     factories.add(TypeAdapters.SHORT_FACTORY);
/* 210 */     factories.add(TypeAdapters.newFactory(Long.TYPE, Long.class, longAdapter(longSerializationPolicy)));
/*     */ 
/* 212 */     factories.add(TypeAdapters.newFactory(Double.TYPE, Double.class, doubleAdapter(serializeSpecialFloatingPointValues)));
/*     */ 
/* 214 */     factories.add(TypeAdapters.newFactory(Float.TYPE, Float.class, floatAdapter(serializeSpecialFloatingPointValues)));
/*     */ 
/* 216 */     factories.add(excluder);
/* 217 */     factories.add(TypeAdapters.NUMBER_FACTORY);
/* 218 */     factories.add(TypeAdapters.CHARACTER_FACTORY);
/* 219 */     factories.add(TypeAdapters.STRING_BUILDER_FACTORY);
/* 220 */     factories.add(TypeAdapters.STRING_BUFFER_FACTORY);
/* 221 */     factories.add(TypeAdapters.newFactory(BigDecimal.class, new BigDecimalTypeAdapter()));
/* 222 */     factories.add(TypeAdapters.newFactory(BigInteger.class, new BigIntegerTypeAdapter()));
/* 223 */     factories.add(TypeAdapters.JSON_ELEMENT_FACTORY);
/* 224 */     factories.add(ObjectTypeAdapter.FACTORY);
/*     */ 
/* 227 */     factories.addAll(typeAdapterFactories);
/*     */ 
/* 230 */     factories.add(new CollectionTypeAdapterFactory(constructorConstructor));
/* 231 */     factories.add(TypeAdapters.URL_FACTORY);
/* 232 */     factories.add(TypeAdapters.URI_FACTORY);
/* 233 */     factories.add(TypeAdapters.UUID_FACTORY);
/* 234 */     factories.add(TypeAdapters.LOCALE_FACTORY);
/* 235 */     factories.add(TypeAdapters.INET_ADDRESS_FACTORY);
/* 236 */     factories.add(TypeAdapters.BIT_SET_FACTORY);
/* 237 */     factories.add(DateTypeAdapter.FACTORY);
/* 238 */     factories.add(TypeAdapters.CALENDAR_FACTORY);
/* 239 */     factories.add(TimeTypeAdapter.FACTORY);
/* 240 */     factories.add(SqlDateTypeAdapter.FACTORY);
/* 241 */     factories.add(TypeAdapters.TIMESTAMP_FACTORY);
/* 242 */     factories.add(new MapTypeAdapterFactory(constructorConstructor, complexMapKeySerialization));
/* 243 */     factories.add(ArrayTypeAdapter.FACTORY);
/* 244 */     factories.add(TypeAdapters.ENUM_FACTORY);
/* 245 */     factories.add(TypeAdapters.CLASS_FACTORY);
/* 246 */     factories.add(reflectiveTypeAdapterFactory);
/*     */ 
/* 248 */     this.factories = Collections.unmodifiableList(factories);
/*     */   }
/*     */ 
/*     */   private TypeAdapter<Number> doubleAdapter(boolean serializeSpecialFloatingPointValues) {
/* 252 */     if (serializeSpecialFloatingPointValues) {
/* 253 */       return TypeAdapters.DOUBLE;
/*     */     }
/* 255 */     return new TypeAdapter() {
/*     */       public Double read(JsonReader in) throws IOException {
/* 257 */         if (in.peek() == JsonToken.NULL) {
/* 258 */           in.nextNull();
/* 259 */           return null;
/*     */         }
/* 261 */         return Double.valueOf(in.nextDouble());
/*     */       }
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 264 */         if (value == null) {
/* 265 */           out.nullValue();
/* 266 */           return;
/*     */         }
/* 268 */         double doubleValue = value.doubleValue();
/* 269 */         Gson.this.checkValidFloatingPoint(doubleValue);
/* 270 */         out.value(value);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private TypeAdapter<Number> floatAdapter(boolean serializeSpecialFloatingPointValues) {
/* 276 */     if (serializeSpecialFloatingPointValues) {
/* 277 */       return TypeAdapters.FLOAT;
/*     */     }
/* 279 */     return new TypeAdapter() {
/*     */       public Float read(JsonReader in) throws IOException {
/* 281 */         if (in.peek() == JsonToken.NULL) {
/* 282 */           in.nextNull();
/* 283 */           return null;
/*     */         }
/* 285 */         return Float.valueOf((float)in.nextDouble());
/*     */       }
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 288 */         if (value == null) {
/* 289 */           out.nullValue();
/* 290 */           return;
/*     */         }
/* 292 */         float floatValue = value.floatValue();
/* 293 */         Gson.this.checkValidFloatingPoint(floatValue);
/* 294 */         out.value(value);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private void checkValidFloatingPoint(double value) {
/* 300 */     if ((Double.isNaN(value)) || (Double.isInfinite(value)))
/* 301 */       throw new IllegalArgumentException(value + " is not a valid double value as per JSON specification. To override this" + " behavior, use GsonBuilder.serializeSpecialDoubleValues() method.");
/*     */   }
/*     */ 
/*     */   private TypeAdapter<Number> longAdapter(LongSerializationPolicy longSerializationPolicy)
/*     */   {
/* 308 */     if (longSerializationPolicy == LongSerializationPolicy.DEFAULT) {
/* 309 */       return TypeAdapters.LONG;
/*     */     }
/* 311 */     return new TypeAdapter() {
/*     */       public Number read(JsonReader in) throws IOException {
/* 313 */         if (in.peek() == JsonToken.NULL) {
/* 314 */           in.nextNull();
/* 315 */           return null;
/*     */         }
/* 317 */         return Long.valueOf(in.nextLong());
/*     */       }
/*     */       public void write(JsonWriter out, Number value) throws IOException {
/* 320 */         if (value == null) {
/* 321 */           out.nullValue();
/* 322 */           return;
/*     */         }
/* 324 */         out.value(value.toString());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> getAdapter(TypeToken<T> type)
/*     */   {
/* 337 */     TypeAdapter cached = (TypeAdapter)this.typeTokenCache.get(type);
/* 338 */     if (cached != null) {
/* 339 */       return cached;
/*     */     }
/*     */ 
/* 342 */     Map threadCalls = (Map)this.calls.get();
/*     */ 
/* 344 */     FutureTypeAdapter ongoingCall = (FutureTypeAdapter)threadCalls.get(type);
/* 345 */     if (ongoingCall != null) {
/* 346 */       return ongoingCall;
/*     */     }
/*     */ 
/* 349 */     FutureTypeAdapter call = new FutureTypeAdapter();
/* 350 */     threadCalls.put(type, call);
/*     */     try {
/* 352 */       for (TypeAdapterFactory factory : this.factories) {
/* 353 */         TypeAdapter candidate = factory.create(this, type);
/* 354 */         if (candidate != null) {
/* 355 */           call.setDelegate(candidate);
/* 356 */           this.typeTokenCache.put(type, candidate);
/* 357 */           return candidate;
/*     */         }
/*     */       }
/* 360 */       throw new IllegalArgumentException("GSON cannot handle " + type);
/*     */     } finally {
/* 362 */       threadCalls.remove(type);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> getAdapter(Class<T> type)
/*     */   {
/* 398 */     return getAdapter(TypeToken.get(type));
/*     */   }
/*     */ 
/*     */   public JsonElement toJsonTree(Object src)
/*     */   {
/* 415 */     if (src == null) {
/* 416 */       return JsonNull.INSTANCE;
/*     */     }
/* 418 */     return toJsonTree(src, src.getClass());
/*     */   }
/*     */ 
/*     */   public JsonElement toJsonTree(Object src, Type typeOfSrc)
/*     */   {
/* 438 */     JsonTreeWriter writer = new JsonTreeWriter();
/* 439 */     toJson(src, typeOfSrc, writer);
/* 440 */     return writer.get();
/*     */   }
/*     */ 
/*     */   public String toJson(Object src)
/*     */   {
/* 457 */     if (src == null) {
/* 458 */       return toJson(JsonNull.INSTANCE);
/*     */     }
/* 460 */     return toJson(src, src.getClass());
/*     */   }
/*     */ 
/*     */   public String toJson(Object src, Type typeOfSrc)
/*     */   {
/* 479 */     StringWriter writer = new StringWriter();
/* 480 */     toJson(src, typeOfSrc, writer);
/* 481 */     return writer.toString();
/*     */   }
/*     */ 
/*     */   public void toJson(Object src, Appendable writer)
/*     */     throws JsonIOException
/*     */   {
/* 499 */     if (src != null)
/* 500 */       toJson(src, src.getClass(), writer);
/*     */     else
/* 502 */       toJson(JsonNull.INSTANCE, writer);
/*     */   }
/*     */ 
/*     */   public void toJson(Object src, Type typeOfSrc, Appendable writer)
/*     */     throws JsonIOException
/*     */   {
/*     */     try
/*     */     {
/* 524 */       JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
/* 525 */       toJson(src, typeOfSrc, jsonWriter);
/*     */     } catch (IOException e) {
/* 527 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void toJson(Object src, Type typeOfSrc, JsonWriter writer)
/*     */     throws JsonIOException
/*     */   {
/* 538 */     TypeAdapter adapter = getAdapter(TypeToken.get(typeOfSrc));
/* 539 */     boolean oldLenient = writer.isLenient();
/* 540 */     writer.setLenient(true);
/* 541 */     boolean oldHtmlSafe = writer.isHtmlSafe();
/* 542 */     writer.setHtmlSafe(this.htmlSafe);
/* 543 */     boolean oldSerializeNulls = writer.getSerializeNulls();
/* 544 */     writer.setSerializeNulls(this.serializeNulls);
/*     */     try {
/* 546 */       adapter.write(writer, src);
/*     */     } catch (IOException e) {
/* 548 */       throw new JsonIOException(e);
/*     */     } finally {
/* 550 */       writer.setLenient(oldLenient);
/* 551 */       writer.setHtmlSafe(oldHtmlSafe);
/* 552 */       writer.setSerializeNulls(oldSerializeNulls);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toJson(JsonElement jsonElement)
/*     */   {
/* 564 */     StringWriter writer = new StringWriter();
/* 565 */     toJson(jsonElement, writer);
/* 566 */     return writer.toString();
/*     */   }
/*     */ 
/*     */   public void toJson(JsonElement jsonElement, Appendable writer)
/*     */     throws JsonIOException
/*     */   {
/*     */     try
/*     */     {
/* 579 */       JsonWriter jsonWriter = newJsonWriter(Streams.writerForAppendable(writer));
/* 580 */       toJson(jsonElement, jsonWriter);
/*     */     } catch (IOException e) {
/* 582 */       throw new RuntimeException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   private JsonWriter newJsonWriter(Writer writer)
/*     */     throws IOException
/*     */   {
/* 591 */     if (this.generateNonExecutableJson) {
/* 592 */       writer.write(")]}'\n");
/*     */     }
/* 594 */     JsonWriter jsonWriter = new JsonWriter(writer);
/* 595 */     if (this.prettyPrinting) {
/* 596 */       jsonWriter.setIndent("  ");
/*     */     }
/* 598 */     jsonWriter.setSerializeNulls(this.serializeNulls);
/* 599 */     return jsonWriter;
/*     */   }
/*     */ 
/*     */   public void toJson(JsonElement jsonElement, JsonWriter writer)
/*     */     throws JsonIOException
/*     */   {
/* 607 */     boolean oldLenient = writer.isLenient();
/* 608 */     writer.setLenient(true);
/* 609 */     boolean oldHtmlSafe = writer.isHtmlSafe();
/* 610 */     writer.setHtmlSafe(this.htmlSafe);
/* 611 */     boolean oldSerializeNulls = writer.getSerializeNulls();
/* 612 */     writer.setSerializeNulls(this.serializeNulls);
/*     */     try {
/* 614 */       Streams.write(jsonElement, writer);
/*     */     } catch (IOException e) {
/* 616 */       throw new JsonIOException(e);
/*     */     } finally {
/* 618 */       writer.setLenient(oldLenient);
/* 619 */       writer.setHtmlSafe(oldHtmlSafe);
/* 620 */       writer.setSerializeNulls(oldSerializeNulls);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(String json, Class<T> classOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 642 */     Object object = fromJson(json, classOfT);
/* 643 */     return Primitives.wrap(classOfT).cast(object);
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(String json, Type typeOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 666 */     if (json == null) {
/* 667 */       return null;
/*     */     }
/* 669 */     StringReader reader = new StringReader(json);
/* 670 */     Object target = fromJson(reader, typeOfT);
/* 671 */     return target;
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(Reader json, Class<T> classOfT)
/*     */     throws JsonSyntaxException, JsonIOException
/*     */   {
/* 693 */     JsonReader jsonReader = new JsonReader(json);
/* 694 */     Object object = fromJson(jsonReader, classOfT);
/* 695 */     assertFullConsumption(object, jsonReader);
/* 696 */     return Primitives.wrap(classOfT).cast(object);
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(Reader json, Type typeOfT)
/*     */     throws JsonIOException, JsonSyntaxException
/*     */   {
/* 720 */     JsonReader jsonReader = new JsonReader(json);
/* 721 */     Object object = fromJson(jsonReader, typeOfT);
/* 722 */     assertFullConsumption(object, jsonReader);
/* 723 */     return object;
/*     */   }
/*     */ 
/*     */   private static void assertFullConsumption(Object obj, JsonReader reader) {
/*     */     try {
/* 728 */       if ((obj != null) && (reader.peek() != JsonToken.END_DOCUMENT))
/* 729 */         throw new JsonIOException("JSON document was not fully consumed.");
/*     */     }
/*     */     catch (MalformedJsonException e) {
/* 732 */       throw new JsonSyntaxException(e);
/*     */     } catch (IOException e) {
/* 734 */       throw new JsonIOException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(JsonReader reader, Type typeOfT)
/*     */     throws JsonIOException, JsonSyntaxException
/*     */   {
/* 748 */     boolean isEmpty = true;
/* 749 */     boolean oldLenient = reader.isLenient();
/* 750 */     reader.setLenient(true);
/*     */     try {
/* 752 */       reader.peek();
/* 753 */       isEmpty = false;
/* 754 */       TypeAdapter typeAdapter = getAdapter(TypeToken.get(typeOfT));
/* 755 */       return typeAdapter.read(reader);
/*     */     }
/*     */     catch (EOFException e)
/*     */     {
/*     */       Object localObject1;
/* 761 */       if (isEmpty) {
/* 762 */         return null;
/*     */       }
/* 764 */       throw new JsonSyntaxException(e);
/*     */     } catch (IllegalStateException e) {
/* 766 */       throw new JsonSyntaxException(e);
/*     */     }
/*     */     catch (IOException e) {
/* 769 */       throw new JsonSyntaxException(e);
/*     */     } finally {
/* 771 */       reader.setLenient(oldLenient);
/*     */     }
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(JsonElement json, Class<T> classOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 792 */     Object object = fromJson(json, classOfT);
/* 793 */     return Primitives.wrap(classOfT).cast(object);
/*     */   }
/*     */ 
/*     */   public <T> T fromJson(JsonElement json, Type typeOfT)
/*     */     throws JsonSyntaxException
/*     */   {
/* 816 */     if (json == null) {
/* 817 */       return null;
/*     */     }
/* 819 */     return fromJson(new JsonTreeReader(json), typeOfT);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 849 */     StringBuilder sb = new StringBuilder("{").append("serializeNulls:").append(this.serializeNulls).append("factories:").append(this.factories).append(",instanceCreators:").append(this.constructorConstructor).append("}");
/*     */ 
/* 854 */     return sb.toString();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/* 367 */     GsonInternalAccess.INSTANCE = new GsonInternalAccess()
/*     */     {
/*     */       public <T> TypeAdapter<T> getNextAdapter(Gson gson, TypeAdapterFactory skipPast, TypeToken<T> type) {
/* 370 */         boolean skipPastFound = false;
/*     */ 
/* 372 */         for (TypeAdapterFactory factory : gson.factories) {
/* 373 */           if (!skipPastFound) {
/* 374 */             if (factory == skipPast) {
/* 375 */               skipPastFound = true;
/*     */             }
/*     */           }
/*     */           else
/*     */           {
/* 380 */             TypeAdapter candidate = factory.create(gson, type);
/* 381 */             if (candidate != null) {
/* 382 */               return candidate;
/*     */             }
/*     */           }
/*     */         }
/* 386 */         throw new IllegalArgumentException("GSON cannot serialize " + type);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   static class FutureTypeAdapter<T> extends TypeAdapter<T>
/*     */   {
/*     */     private TypeAdapter<T> delegate;
/*     */ 
/*     */     public void setDelegate(TypeAdapter<T> typeAdapter)
/*     */     {
/* 826 */       if (this.delegate != null) {
/* 827 */         throw new AssertionError();
/*     */       }
/* 829 */       this.delegate = typeAdapter;
/*     */     }
/*     */ 
/*     */     public T read(JsonReader in) throws IOException {
/* 833 */       if (this.delegate == null) {
/* 834 */         throw new IllegalStateException();
/*     */       }
/* 836 */       return this.delegate.read(in);
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, T value) throws IOException {
/* 840 */       if (this.delegate == null) {
/* 841 */         throw new IllegalStateException();
/*     */       }
/* 843 */       this.delegate.write(out, value);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.Gson
 * JD-Core Version:    0.6.2
 */