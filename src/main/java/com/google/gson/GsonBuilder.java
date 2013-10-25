/*     */ package com.google.gson;
/*     */ 
/*     */ import com.google.gson.internal..Gson.Preconditions;
/*     */ import com.google.gson.internal.Excluder;
/*     */ import com.google.gson.internal.Primitives;
/*     */ import com.google.gson.internal.bind.TypeAdapters;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import java.lang.reflect.Type;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ public final class GsonBuilder
/*     */ {
/*  70 */   private Excluder excluder = Excluder.DEFAULT;
/*  71 */   private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
/*  72 */   private FieldNamingStrategy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
/*  73 */   private final Map<Type, InstanceCreator<?>> instanceCreators = new HashMap();
/*     */ 
/*  75 */   private final List<TypeAdapterFactory> factories = new ArrayList();
/*     */ 
/*  77 */   private final List<TypeAdapterFactory> hierarchyFactories = new ArrayList();
/*     */   private boolean serializeNulls;
/*     */   private String datePattern;
/*  80 */   private int dateStyle = 2;
/*  81 */   private int timeStyle = 2;
/*     */   private boolean complexMapKeySerialization;
/*     */   private boolean serializeSpecialFloatingPointValues;
/*  84 */   private boolean escapeHtmlChars = true;
/*     */   private boolean prettyPrinting;
/*     */   private boolean generateNonExecutableJson;
/*     */ 
/*     */   public GsonBuilder setVersion(double ignoreVersionsAfter)
/*     */   {
/* 105 */     this.excluder = this.excluder.withVersion(ignoreVersionsAfter);
/* 106 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder excludeFieldsWithModifiers(int[] modifiers)
/*     */   {
/* 121 */     this.excluder = this.excluder.withModifiers(modifiers);
/* 122 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder generateNonExecutableJson()
/*     */   {
/* 135 */     this.generateNonExecutableJson = true;
/* 136 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder excludeFieldsWithoutExposeAnnotation()
/*     */   {
/* 146 */     this.excluder = this.excluder.excludeFieldsWithoutExposeAnnotation();
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder serializeNulls()
/*     */   {
/* 158 */     this.serializeNulls = true;
/* 159 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder enableComplexMapKeySerialization()
/*     */   {
/* 239 */     this.complexMapKeySerialization = true;
/* 240 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder disableInnerClassSerialization()
/*     */   {
/* 250 */     this.excluder = this.excluder.disableInnerClassSerialization();
/* 251 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setLongSerializationPolicy(LongSerializationPolicy serializationPolicy)
/*     */   {
/* 263 */     this.longSerializationPolicy = serializationPolicy;
/* 264 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setFieldNamingPolicy(FieldNamingPolicy namingConvention)
/*     */   {
/* 276 */     this.fieldNamingPolicy = namingConvention;
/* 277 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setFieldNamingStrategy(FieldNamingStrategy fieldNamingStrategy)
/*     */   {
/* 289 */     this.fieldNamingPolicy = fieldNamingStrategy;
/* 290 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setExclusionStrategies(ExclusionStrategy[] strategies)
/*     */   {
/* 304 */     for (ExclusionStrategy strategy : strategies) {
/* 305 */       this.excluder = this.excluder.withExclusionStrategy(strategy, true, true);
/*     */     }
/* 307 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder addSerializationExclusionStrategy(ExclusionStrategy strategy)
/*     */   {
/* 323 */     this.excluder = this.excluder.withExclusionStrategy(strategy, true, false);
/* 324 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder addDeserializationExclusionStrategy(ExclusionStrategy strategy)
/*     */   {
/* 340 */     this.excluder = this.excluder.withExclusionStrategy(strategy, false, true);
/* 341 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setPrettyPrinting()
/*     */   {
/* 351 */     this.prettyPrinting = true;
/* 352 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder disableHtmlEscaping()
/*     */   {
/* 363 */     this.escapeHtmlChars = false;
/* 364 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setDateFormat(String pattern)
/*     */   {
/* 385 */     this.datePattern = pattern;
/* 386 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setDateFormat(int style)
/*     */   {
/* 404 */     this.dateStyle = style;
/* 405 */     this.datePattern = null;
/* 406 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder setDateFormat(int dateStyle, int timeStyle)
/*     */   {
/* 425 */     this.dateStyle = dateStyle;
/* 426 */     this.timeStyle = timeStyle;
/* 427 */     this.datePattern = null;
/* 428 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder registerTypeAdapter(Type type, Object typeAdapter)
/*     */   {
/* 445 */     .Gson.Preconditions.checkArgument(((typeAdapter instanceof JsonSerializer)) || ((typeAdapter instanceof JsonDeserializer)) || ((typeAdapter instanceof InstanceCreator)) || ((typeAdapter instanceof TypeAdapter)));
/*     */ 
/* 449 */     if ((Primitives.isPrimitive(type)) || (Primitives.isWrapperType(type))) {
/* 450 */       throw new IllegalArgumentException("Cannot register type adapters for " + type);
/*     */     }
/*     */ 
/* 453 */     if ((typeAdapter instanceof InstanceCreator)) {
/* 454 */       this.instanceCreators.put(type, (InstanceCreator)typeAdapter);
/*     */     }
/* 456 */     if (((typeAdapter instanceof JsonSerializer)) || ((typeAdapter instanceof JsonDeserializer))) {
/* 457 */       TypeToken typeToken = TypeToken.get(type);
/* 458 */       this.factories.add(TreeTypeAdapter.newFactoryWithMatchRawType(typeToken, typeAdapter));
/*     */     }
/* 460 */     if ((typeAdapter instanceof TypeAdapter)) {
/* 461 */       this.factories.add(TypeAdapters.newFactory(TypeToken.get(type), (TypeAdapter)typeAdapter));
/*     */     }
/* 463 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder registerTypeAdapterFactory(TypeAdapterFactory factory)
/*     */   {
/* 475 */     this.factories.add(factory);
/* 476 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder registerTypeHierarchyAdapter(Class<?> baseType, Object typeAdapter)
/*     */   {
/* 495 */     .Gson.Preconditions.checkArgument(((typeAdapter instanceof JsonSerializer)) || ((typeAdapter instanceof JsonDeserializer)) || ((typeAdapter instanceof TypeAdapter)));
/*     */ 
/* 498 */     if (((typeAdapter instanceof JsonDeserializer)) || ((typeAdapter instanceof JsonSerializer))) {
/* 499 */       this.hierarchyFactories.add(0, TreeTypeAdapter.newTypeHierarchyFactory(baseType, typeAdapter));
/*     */     }
/*     */ 
/* 502 */     if ((typeAdapter instanceof TypeAdapter)) {
/* 503 */       this.factories.add(TypeAdapters.newTypeHierarchyFactory(baseType, (TypeAdapter)typeAdapter));
/*     */     }
/* 505 */     return this;
/*     */   }
/*     */ 
/*     */   public GsonBuilder serializeSpecialFloatingPointValues()
/*     */   {
/* 529 */     this.serializeSpecialFloatingPointValues = true;
/* 530 */     return this;
/*     */   }
/*     */ 
/*     */   public Gson create()
/*     */   {
/* 540 */     List factories = new ArrayList();
/* 541 */     factories.addAll(this.factories);
/* 542 */     Collections.reverse(factories);
/* 543 */     factories.addAll(this.hierarchyFactories);
/* 544 */     addTypeAdaptersForDate(this.datePattern, this.dateStyle, this.timeStyle, factories);
/*     */ 
/* 546 */     return new Gson(this.excluder, this.fieldNamingPolicy, this.instanceCreators, this.serializeNulls, this.complexMapKeySerialization, this.generateNonExecutableJson, this.escapeHtmlChars, this.prettyPrinting, this.serializeSpecialFloatingPointValues, this.longSerializationPolicy, factories);
/*     */   }
/*     */ 
/*     */   private void addTypeAdaptersForDate(String datePattern, int dateStyle, int timeStyle, List<TypeAdapterFactory> factories)
/*     */   {
/*     */     DefaultDateTypeAdapter dateTypeAdapter;
/* 555 */     if ((datePattern != null) && (!"".equals(datePattern.trim()))) {
/* 556 */       dateTypeAdapter = new DefaultDateTypeAdapter(datePattern);
/*     */     }
/*     */     else
/*     */     {
/*     */       DefaultDateTypeAdapter dateTypeAdapter;
/* 557 */       if ((dateStyle != 2) && (timeStyle != 2))
/* 558 */         dateTypeAdapter = new DefaultDateTypeAdapter(dateStyle, timeStyle);
/*     */       else
/*     */         return;
/*     */     }
/*     */     DefaultDateTypeAdapter dateTypeAdapter;
/* 563 */     factories.add(TreeTypeAdapter.newFactory(TypeToken.get(java.util.Date.class), dateTypeAdapter));
/* 564 */     factories.add(TreeTypeAdapter.newFactory(TypeToken.get(Timestamp.class), dateTypeAdapter));
/* 565 */     factories.add(TreeTypeAdapter.newFactory(TypeToken.get(java.sql.Date.class), dateTypeAdapter));
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.GsonBuilder
 * JD-Core Version:    0.6.2
 */