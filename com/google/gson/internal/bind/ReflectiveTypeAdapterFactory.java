/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.FieldNamingStrategy;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.annotations.SerializedName;
/*     */ import com.google.gson.internal..Gson.Types;
/*     */ import com.google.gson.internal.ConstructorConstructor;
/*     */ import com.google.gson.internal.Excluder;
/*     */ import com.google.gson.internal.ObjectConstructor;
/*     */ import com.google.gson.internal.Primitives;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Type;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class ReflectiveTypeAdapterFactory
/*     */   implements TypeAdapterFactory
/*     */ {
/*     */   private final ConstructorConstructor constructorConstructor;
/*     */   private final FieldNamingStrategy fieldNamingPolicy;
/*     */   private final Excluder excluder;
/*     */ 
/*     */   public ReflectiveTypeAdapterFactory(ConstructorConstructor constructorConstructor, FieldNamingStrategy fieldNamingPolicy, Excluder excluder)
/*     */   {
/*  50 */     this.constructorConstructor = constructorConstructor;
/*  51 */     this.fieldNamingPolicy = fieldNamingPolicy;
/*  52 */     this.excluder = excluder;
/*     */   }
/*     */ 
/*     */   public boolean excludeField(Field f, boolean serialize) {
/*  56 */     return (!this.excluder.excludeClass(f.getType(), serialize)) && (!this.excluder.excludeField(f, serialize));
/*     */   }
/*     */ 
/*     */   private String getFieldName(Field f) {
/*  60 */     SerializedName serializedName = (SerializedName)f.getAnnotation(SerializedName.class);
/*  61 */     return serializedName == null ? this.fieldNamingPolicy.translateName(f) : serializedName.value();
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
/*  65 */     Class raw = type.getRawType();
/*     */ 
/*  67 */     if (!Object.class.isAssignableFrom(raw)) {
/*  68 */       return null;
/*     */     }
/*     */ 
/*  71 */     ObjectConstructor constructor = this.constructorConstructor.getConstructor(type);
/*  72 */     return new Adapter(constructor, getBoundFields(gson, type, raw), null);
/*     */   }
/*     */ 
/*     */   private BoundField createBoundField(final Gson context, final Field field, String name, final TypeToken<?> fieldType, boolean serialize, boolean deserialize)
/*     */   {
/*  78 */     final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());
/*     */ 
/*  81 */     return new BoundField(name, serialize, deserialize) {
/*  82 */       final TypeAdapter<?> typeAdapter = context.getAdapter(fieldType);
/*     */ 
/*     */       void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException
/*     */       {
/*  86 */         Object fieldValue = field.get(value);
/*  87 */         TypeAdapter t = new TypeAdapterRuntimeTypeWrapper(context, this.typeAdapter, fieldType.getType());
/*     */ 
/*  89 */         t.write(writer, fieldValue);
/*     */       }
/*     */ 
/*     */       void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
/*  93 */         Object fieldValue = this.typeAdapter.read(reader);
/*  94 */         if ((fieldValue != null) || (!isPrimitive))
/*  95 */           field.set(value, fieldValue);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw)
/*     */   {
/* 102 */     Map result = new LinkedHashMap();
/* 103 */     if (raw.isInterface()) {
/* 104 */       return result;
/*     */     }
/*     */ 
/* 107 */     Type declaredType = type.getType();
/* 108 */     while (raw != Object.class) {
/* 109 */       Field[] fields = raw.getDeclaredFields();
/* 110 */       for (Field field : fields) {
/* 111 */         boolean serialize = excludeField(field, true);
/* 112 */         boolean deserialize = excludeField(field, false);
/* 113 */         if ((serialize) || (deserialize))
/*     */         {
/* 116 */           field.setAccessible(true);
/* 117 */           Type fieldType = .Gson.Types.resolve(type.getType(), raw, field.getGenericType());
/* 118 */           BoundField boundField = createBoundField(context, field, getFieldName(field), TypeToken.get(fieldType), serialize, deserialize);
/*     */ 
/* 120 */           BoundField previous = (BoundField)result.put(boundField.name, boundField);
/* 121 */           if (previous != null) {
/* 122 */             throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
/*     */           }
/*     */         }
/*     */       }
/* 126 */       type = TypeToken.get(.Gson.Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
/* 127 */       raw = type.getRawType();
/*     */     }
/* 129 */     return result;
/*     */   }
/*     */ 
/*     */   public final class Adapter<T> extends TypeAdapter<T>
/*     */   {
/*     */     private final ObjectConstructor<T> constructor;
/*     */     private final Map<String, ReflectiveTypeAdapterFactory.BoundField> boundFields;
/*     */ 
/*     */     private Adapter(Map<String, ReflectiveTypeAdapterFactory.BoundField> constructor)
/*     */     {
/* 152 */       this.constructor = constructor;
/* 153 */       this.boundFields = boundFields;
/*     */     }
/*     */ 
/*     */     public T read(JsonReader in) throws IOException
/*     */     {
/* 158 */       if (in.peek() == JsonToken.NULL) {
/* 159 */         in.nextNull();
/* 160 */         return null;
/*     */       }
/*     */ 
/* 163 */       Object instance = this.constructor.construct();
/*     */       try
/*     */       {
/* 168 */         in.beginObject();
/* 169 */         while (in.hasNext()) {
/* 170 */           String name = in.nextName();
/* 171 */           ReflectiveTypeAdapterFactory.BoundField field = (ReflectiveTypeAdapterFactory.BoundField)this.boundFields.get(name);
/* 172 */           if ((field == null) || (!field.deserialized))
/*     */           {
/* 174 */             in.skipValue();
/*     */           }
/* 176 */           else field.read(in, instance); 
/*     */         }
/*     */       }
/*     */       catch (IllegalStateException e)
/*     */       {
/* 180 */         throw new JsonSyntaxException(e);
/*     */       } catch (IllegalAccessException e) {
/* 182 */         throw new AssertionError(e);
/*     */       }
/* 184 */       in.endObject();
/* 185 */       return instance;
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, T value) throws IOException
/*     */     {
/* 190 */       if (value == null) {
/* 191 */         out.nullValue();
/* 192 */         return;
/*     */       }
/*     */ 
/* 195 */       out.beginObject();
/*     */       try {
/* 197 */         for (ReflectiveTypeAdapterFactory.BoundField boundField : this.boundFields.values())
/* 198 */           if (boundField.serialized) {
/* 199 */             out.name(boundField.name);
/* 200 */             boundField.write(out, value);
/*     */           }
/*     */       }
/*     */       catch (IllegalAccessException e) {
/* 204 */         throw new AssertionError();
/*     */       }
/* 206 */       out.endObject();
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class BoundField
/*     */   {
/*     */     final String name;
/*     */     final boolean serialized;
/*     */     final boolean deserialized;
/*     */ 
/*     */     protected BoundField(String name, boolean serialized, boolean deserialized)
/*     */     {
/* 138 */       this.name = name;
/* 139 */       this.serialized = serialized;
/* 140 */       this.deserialized = deserialized;
/*     */     }
/*     */ 
/*     */     abstract void write(JsonWriter paramJsonWriter, Object paramObject)
/*     */       throws IOException, IllegalAccessException;
/*     */ 
/*     */     abstract void read(JsonReader paramJsonReader, Object paramObject)
/*     */       throws IOException, IllegalAccessException;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.ReflectiveTypeAdapterFactory
 * JD-Core Version:    0.6.2
 */