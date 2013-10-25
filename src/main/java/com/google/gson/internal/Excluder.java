/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import com.google.gson.ExclusionStrategy;
/*     */ import com.google.gson.FieldAttributes;
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.annotations.Expose;
/*     */ import com.google.gson.annotations.Since;
/*     */ import com.google.gson.annotations.Until;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ 
/*     */ public final class Excluder
/*     */   implements TypeAdapterFactory, Cloneable
/*     */ {
/*     */   private static final double IGNORE_VERSIONS = -1.0D;
/*  52 */   public static final Excluder DEFAULT = new Excluder();
/*     */ 
/*  54 */   private double version = -1.0D;
/*  55 */   private int modifiers = 136;
/*  56 */   private boolean serializeInnerClasses = true;
/*     */   private boolean requireExpose;
/*  58 */   private List<ExclusionStrategy> serializationStrategies = Collections.emptyList();
/*  59 */   private List<ExclusionStrategy> deserializationStrategies = Collections.emptyList();
/*     */ 
/*     */   protected Excluder clone() {
/*     */     try {
/*  63 */       return (Excluder)super.clone(); } catch (CloneNotSupportedException e) {
/*     */     }
/*  65 */     throw new AssertionError();
/*     */   }
/*     */ 
/*     */   public Excluder withVersion(double ignoreVersionsAfter)
/*     */   {
/*  70 */     Excluder result = clone();
/*  71 */     result.version = ignoreVersionsAfter;
/*  72 */     return result;
/*     */   }
/*     */ 
/*     */   public Excluder withModifiers(int[] modifiers) {
/*  76 */     Excluder result = clone();
/*  77 */     result.modifiers = 0;
/*  78 */     for (int modifier : modifiers) {
/*  79 */       result.modifiers |= modifier;
/*     */     }
/*  81 */     return result;
/*     */   }
/*     */ 
/*     */   public Excluder disableInnerClassSerialization() {
/*  85 */     Excluder result = clone();
/*  86 */     result.serializeInnerClasses = false;
/*  87 */     return result;
/*     */   }
/*     */ 
/*     */   public Excluder excludeFieldsWithoutExposeAnnotation() {
/*  91 */     Excluder result = clone();
/*  92 */     result.requireExpose = true;
/*  93 */     return result;
/*     */   }
/*     */ 
/*     */   public Excluder withExclusionStrategy(ExclusionStrategy exclusionStrategy, boolean serialization, boolean deserialization)
/*     */   {
/*  98 */     Excluder result = clone();
/*  99 */     if (serialization) {
/* 100 */       result.serializationStrategies = new ArrayList(this.serializationStrategies);
/* 101 */       result.serializationStrategies.add(exclusionStrategy);
/*     */     }
/* 103 */     if (deserialization) {
/* 104 */       result.deserializationStrategies = new ArrayList(this.deserializationStrategies);
/*     */ 
/* 106 */       result.deserializationStrategies.add(exclusionStrategy);
/*     */     }
/* 108 */     return result;
/*     */   }
/*     */ 
/*     */   public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
/* 112 */     Class rawType = type.getRawType();
/* 113 */     final boolean skipSerialize = excludeClass(rawType, true);
/* 114 */     final boolean skipDeserialize = excludeClass(rawType, false);
/*     */ 
/* 116 */     if ((!skipSerialize) && (!skipDeserialize)) {
/* 117 */       return null;
/*     */     }
/*     */ 
/* 120 */     return new TypeAdapter()
/*     */     {
/*     */       private TypeAdapter<T> delegate;
/*     */ 
/*     */       public T read(JsonReader in) throws IOException {
/* 125 */         if (skipDeserialize) {
/* 126 */           in.skipValue();
/* 127 */           return null;
/*     */         }
/* 129 */         return delegate().read(in);
/*     */       }
/*     */ 
/*     */       public void write(JsonWriter out, T value) throws IOException {
/* 133 */         if (skipSerialize) {
/* 134 */           out.nullValue();
/* 135 */           return;
/*     */         }
/* 137 */         delegate().write(out, value);
/*     */       }
/*     */ 
/*     */       private TypeAdapter<T> delegate() {
/* 141 */         TypeAdapter d = this.delegate;
/* 142 */         return this.delegate = GsonInternalAccess.INSTANCE.getNextAdapter(gson, Excluder.this, type);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public boolean excludeField(Field field, boolean serialize)
/*     */   {
/* 150 */     if ((this.modifiers & field.getModifiers()) != 0) {
/* 151 */       return true;
/*     */     }
/*     */ 
/* 154 */     if ((this.version != -1.0D) && (!isValidVersion((Since)field.getAnnotation(Since.class), (Until)field.getAnnotation(Until.class))))
/*     */     {
/* 156 */       return true;
/*     */     }
/*     */ 
/* 159 */     if (field.isSynthetic()) {
/* 160 */       return true;
/*     */     }
/*     */ 
/* 163 */     if (this.requireExpose) {
/* 164 */       Expose annotation = (Expose)field.getAnnotation(Expose.class);
/* 165 */       if ((annotation == null) || (serialize ? !annotation.serialize() : !annotation.deserialize())) {
/* 166 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 170 */     if ((!this.serializeInnerClasses) && (isInnerClass(field.getType()))) {
/* 171 */       return true;
/*     */     }
/*     */ 
/* 174 */     if (isAnonymousOrLocal(field.getType())) {
/* 175 */       return true;
/*     */     }
/*     */ 
/* 178 */     List list = serialize ? this.serializationStrategies : this.deserializationStrategies;
/*     */     FieldAttributes fieldAttributes;
/* 179 */     if (!list.isEmpty()) {
/* 180 */       fieldAttributes = new FieldAttributes(field);
/* 181 */       for (ExclusionStrategy exclusionStrategy : list) {
/* 182 */         if (exclusionStrategy.shouldSkipField(fieldAttributes)) {
/* 183 */           return true;
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 188 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean excludeClass(Class<?> clazz, boolean serialize) {
/* 192 */     if ((this.version != -1.0D) && (!isValidVersion((Since)clazz.getAnnotation(Since.class), (Until)clazz.getAnnotation(Until.class))))
/*     */     {
/* 194 */       return true;
/*     */     }
/*     */ 
/* 197 */     if ((!this.serializeInnerClasses) && (isInnerClass(clazz))) {
/* 198 */       return true;
/*     */     }
/*     */ 
/* 201 */     if (isAnonymousOrLocal(clazz)) {
/* 202 */       return true;
/*     */     }
/*     */ 
/* 205 */     List list = serialize ? this.serializationStrategies : this.deserializationStrategies;
/* 206 */     for (ExclusionStrategy exclusionStrategy : list) {
/* 207 */       if (exclusionStrategy.shouldSkipClass(clazz)) {
/* 208 */         return true;
/*     */       }
/*     */     }
/*     */ 
/* 212 */     return false;
/*     */   }
/*     */ 
/*     */   private boolean isAnonymousOrLocal(Class<?> clazz) {
/* 216 */     return (!Enum.class.isAssignableFrom(clazz)) && ((clazz.isAnonymousClass()) || (clazz.isLocalClass()));
/*     */   }
/*     */ 
/*     */   private boolean isInnerClass(Class<?> clazz)
/*     */   {
/* 221 */     return (clazz.isMemberClass()) && (!isStatic(clazz));
/*     */   }
/*     */ 
/*     */   private boolean isStatic(Class<?> clazz) {
/* 225 */     return (clazz.getModifiers() & 0x8) != 0;
/*     */   }
/*     */ 
/*     */   private boolean isValidVersion(Since since, Until until) {
/* 229 */     return (isValidSince(since)) && (isValidUntil(until));
/*     */   }
/*     */ 
/*     */   private boolean isValidSince(Since annotation) {
/* 233 */     if (annotation != null) {
/* 234 */       double annotationVersion = annotation.value();
/* 235 */       if (annotationVersion > this.version) {
/* 236 */         return false;
/*     */       }
/*     */     }
/* 239 */     return true;
/*     */   }
/*     */ 
/*     */   private boolean isValidUntil(Until annotation) {
/* 243 */     if (annotation != null) {
/* 244 */       double annotationVersion = annotation.value();
/* 245 */       if (annotationVersion <= this.version) {
/* 246 */         return false;
/*     */       }
/*     */     }
/* 249 */     return true;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.Excluder
 * JD-Core Version:    0.6.2
 */