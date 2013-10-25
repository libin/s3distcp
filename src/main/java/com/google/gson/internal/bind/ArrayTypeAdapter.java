/*    */ package com.google.gson.internal.bind;
/*    */ 
/*    */ import com.google.gson.Gson;
/*    */ import com.google.gson.TypeAdapter;
/*    */ import com.google.gson.TypeAdapterFactory;
/*    */ import com.google.gson.internal..Gson.Types;
/*    */ import com.google.gson.reflect.TypeToken;
/*    */ import com.google.gson.stream.JsonReader;
/*    */ import com.google.gson.stream.JsonToken;
/*    */ import com.google.gson.stream.JsonWriter;
/*    */ import java.io.IOException;
/*    */ import java.lang.reflect.Array;
/*    */ import java.lang.reflect.GenericArrayType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ public final class ArrayTypeAdapter<E> extends TypeAdapter<Object>
/*    */ {
/* 39 */   public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory()
/*    */   {
/*    */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 42 */       Type type = typeToken.getType();
/* 43 */       if ((!(type instanceof GenericArrayType)) && ((!(type instanceof Class)) || (!((Class)type).isArray()))) {
/* 44 */         return null;
/*    */       }
/*    */ 
/* 47 */       Type componentType = .Gson.Types.getArrayComponentType(type);
/* 48 */       TypeAdapter componentTypeAdapter = gson.getAdapter(TypeToken.get(componentType));
/* 49 */       return new ArrayTypeAdapter(gson, componentTypeAdapter, .Gson.Types.getRawType(componentType));
/*    */     }
/* 39 */   };
/*    */   private final Class<E> componentType;
/*    */   private final TypeAdapter<E> componentTypeAdapter;
/*    */ 
/*    */   public ArrayTypeAdapter(Gson context, TypeAdapter<E> componentTypeAdapter, Class<E> componentType) {
/* 58 */     this.componentTypeAdapter = new TypeAdapterRuntimeTypeWrapper(context, componentTypeAdapter, componentType);
/*    */ 
/* 60 */     this.componentType = componentType;
/*    */   }
/*    */ 
/*    */   public Object read(JsonReader in) throws IOException {
/* 64 */     if (in.peek() == JsonToken.NULL) {
/* 65 */       in.nextNull();
/* 66 */       return null;
/*    */     }
/*    */ 
/* 69 */     List list = new ArrayList();
/* 70 */     in.beginArray();
/* 71 */     while (in.hasNext()) {
/* 72 */       Object instance = this.componentTypeAdapter.read(in);
/* 73 */       list.add(instance);
/*    */     }
/* 75 */     in.endArray();
/* 76 */     Object array = Array.newInstance(this.componentType, list.size());
/* 77 */     for (int i = 0; i < list.size(); i++) {
/* 78 */       Array.set(array, i, list.get(i));
/*    */     }
/* 80 */     return array;
/*    */   }
/*    */ 
/*    */   public void write(JsonWriter out, Object array) throws IOException
/*    */   {
/* 85 */     if (array == null) {
/* 86 */       out.nullValue();
/* 87 */       return;
/*    */     }
/*    */ 
/* 90 */     out.beginArray();
/* 91 */     int i = 0; for (int length = Array.getLength(array); i < length; i++) {
/* 92 */       Object value = Array.get(array, i);
/* 93 */       this.componentTypeAdapter.write(out, value);
/*    */     }
/* 95 */     out.endArray();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.ArrayTypeAdapter
 * JD-Core Version:    0.6.2
 */