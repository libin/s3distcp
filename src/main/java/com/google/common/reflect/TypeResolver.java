/*     */ package com.google.common.reflect;
/*     */ 
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.atomic.AtomicInteger;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ class TypeResolver
/*     */ {
/*     */   private final ImmutableMap<TypeVariable<?>, Type> typeTable;
/*     */ 
/*     */   static TypeResolver accordingTo(Type type)
/*     */   {
/*  51 */     return new TypeResolver().where(TypeMappingIntrospector.getTypeMappings(type));
/*     */   }
/*     */ 
/*     */   TypeResolver() {
/*  55 */     this.typeTable = ImmutableMap.of();
/*     */   }
/*     */ 
/*     */   private TypeResolver(ImmutableMap<TypeVariable<?>, Type> typeTable) {
/*  59 */     this.typeTable = typeTable;
/*     */   }
/*     */ 
/*     */   final TypeResolver where(Map<? extends TypeVariable<?>, ? extends Type> mappings)
/*     */   {
/*  64 */     ImmutableMap.Builder builder = ImmutableMap.builder();
/*  65 */     builder.putAll(this.typeTable);
/*  66 */     for (Map.Entry mapping : mappings.entrySet()) {
/*  67 */       TypeVariable variable = (TypeVariable)mapping.getKey();
/*  68 */       Type type = (Type)mapping.getValue();
/*  69 */       Preconditions.checkArgument(!variable.equals(type), "Type variable %s bound to itself", new Object[] { variable });
/*  70 */       builder.put(variable, type);
/*     */     }
/*  72 */     return new TypeResolver(builder.build());
/*     */   }
/*     */ 
/*     */   final TypeResolver where(Type mapFrom, Type mapTo)
/*     */   {
/*  82 */     Map mappings = Maps.newHashMap();
/*  83 */     populateTypeMappings(mappings, mapFrom, mapTo);
/*  84 */     return where(mappings);
/*     */   }
/*     */ 
/*     */   private static void populateTypeMappings(Map<TypeVariable<?>, Type> mappings, Type from, Type to)
/*     */   {
/*  89 */     if ((from instanceof TypeVariable)) {
/*  90 */       mappings.put((TypeVariable)from, to);
/*  91 */     } else if ((from instanceof GenericArrayType)) {
/*  92 */       populateTypeMappings(mappings, ((GenericArrayType)from).getGenericComponentType(), Types.getComponentType(to));
/*     */     }
/*  94 */     else if ((from instanceof ParameterizedType)) {
/*  95 */       Type[] fromArgs = ((ParameterizedType)from).getActualTypeArguments();
/*  96 */       Type[] toArgs = ((ParameterizedType)to).getActualTypeArguments();
/*  97 */       Preconditions.checkArgument(fromArgs.length == toArgs.length);
/*  98 */       for (int i = 0; i < fromArgs.length; i++)
/*  99 */         populateTypeMappings(mappings, fromArgs[i], toArgs[i]);
/*     */     }
/*     */   }
/*     */ 
/*     */   final Type resolve(Type type)
/*     */   {
/* 109 */     if ((type instanceof TypeVariable))
/* 110 */       return resolveTypeVariable((TypeVariable)type);
/* 111 */     if ((type instanceof ParameterizedType))
/* 112 */       return resolveParameterizedType((ParameterizedType)type);
/* 113 */     if ((type instanceof GenericArrayType))
/* 114 */       return resolveGenericArrayType((GenericArrayType)type);
/* 115 */     if ((type instanceof WildcardType)) {
/* 116 */       WildcardType wildcardType = (WildcardType)type;
/* 117 */       return new Types.WildcardTypeImpl(resolve(wildcardType.getLowerBounds()), resolve(wildcardType.getUpperBounds()));
/*     */     }
/*     */ 
/* 122 */     return type;
/*     */   }
/*     */ 
/*     */   private Type[] resolve(Type[] types)
/*     */   {
/* 127 */     Type[] result = new Type[types.length];
/* 128 */     for (int i = 0; i < types.length; i++) {
/* 129 */       result[i] = resolve(types[i]);
/*     */     }
/* 131 */     return result;
/*     */   }
/*     */ 
/*     */   private Type resolveGenericArrayType(GenericArrayType type) {
/* 135 */     Type componentType = resolve(type.getGenericComponentType());
/* 136 */     return Types.newArrayType(componentType);
/*     */   }
/*     */ 
/*     */   private Type resolveTypeVariable(final TypeVariable<?> var) {
/* 140 */     final TypeResolver unguarded = this;
/* 141 */     TypeResolver guarded = new TypeResolver(this.typeTable, var)
/*     */     {
/*     */       Type resolveTypeVariable(TypeVariable<?> intermediateVar, TypeResolver guardedResolver) {
/* 144 */         if (intermediateVar.getGenericDeclaration().equals(var.getGenericDeclaration())) {
/* 145 */           return intermediateVar;
/*     */         }
/* 147 */         return unguarded.resolveTypeVariable(intermediateVar, guardedResolver);
/*     */       }
/*     */     };
/* 150 */     return resolveTypeVariable(var, guarded);
/*     */   }
/*     */ 
/*     */   Type resolveTypeVariable(TypeVariable<?> var, TypeResolver guardedResolver)
/*     */   {
/* 159 */     Type type = (Type)this.typeTable.get(var);
/* 160 */     if (type == null) {
/* 161 */       Type[] bounds = var.getBounds();
/* 162 */       if (bounds.length == 0) {
/* 163 */         return var;
/*     */       }
/* 165 */       return Types.newTypeVariable(var.getGenericDeclaration(), var.getName(), guardedResolver.resolve(bounds));
/*     */     }
/*     */ 
/* 170 */     return guardedResolver.resolve(type);
/*     */   }
/*     */ 
/*     */   private ParameterizedType resolveParameterizedType(ParameterizedType type) {
/* 174 */     Type owner = type.getOwnerType();
/* 175 */     Type resolvedOwner = owner == null ? null : resolve(owner);
/* 176 */     Type resolvedRawType = resolve(type.getRawType());
/*     */ 
/* 178 */     Type[] vars = type.getActualTypeArguments();
/* 179 */     Type[] resolvedArgs = new Type[vars.length];
/* 180 */     for (int i = 0; i < vars.length; i++) {
/* 181 */       resolvedArgs[i] = resolve(vars[i]);
/*     */     }
/* 183 */     return Types.newParameterizedTypeWithOwner(resolvedOwner, (Class)resolvedRawType, resolvedArgs);
/*     */   }
/*     */ 
/*     */   private static final class WildcardCapturer
/*     */   {
/* 277 */     private final AtomicInteger id = new AtomicInteger();
/*     */ 
/*     */     Type capture(Type type) {
/* 280 */       Preconditions.checkNotNull(type);
/* 281 */       if ((type instanceof Class)) {
/* 282 */         return type;
/*     */       }
/* 284 */       if ((type instanceof TypeVariable)) {
/* 285 */         return type;
/*     */       }
/* 287 */       if ((type instanceof GenericArrayType)) {
/* 288 */         GenericArrayType arrayType = (GenericArrayType)type;
/* 289 */         return Types.newArrayType(capture(arrayType.getGenericComponentType()));
/*     */       }
/* 291 */       if ((type instanceof ParameterizedType)) {
/* 292 */         ParameterizedType parameterizedType = (ParameterizedType)type;
/* 293 */         return Types.newParameterizedTypeWithOwner(captureNullable(parameterizedType.getOwnerType()), (Class)parameterizedType.getRawType(), capture(parameterizedType.getActualTypeArguments()));
/*     */       }
/*     */ 
/* 298 */       if ((type instanceof WildcardType)) {
/* 299 */         WildcardType wildcardType = (WildcardType)type;
/* 300 */         Type[] lowerBounds = wildcardType.getLowerBounds();
/* 301 */         if (lowerBounds.length == 0) {
/* 302 */           Type[] upperBounds = wildcardType.getUpperBounds();
/* 303 */           String name = "capture#" + this.id.incrementAndGet() + "-of ? extends " + Joiner.on('&').join(upperBounds);
/*     */ 
/* 305 */           return Types.newTypeVariable(WildcardCapturer.class, name, wildcardType.getUpperBounds());
/*     */         }
/*     */ 
/* 309 */         return type;
/*     */       }
/*     */ 
/* 312 */       throw new AssertionError("must have been one of the known types");
/*     */     }
/*     */ 
/*     */     private Type captureNullable(@Nullable Type type) {
/* 316 */       if (type == null) {
/* 317 */         return null;
/*     */       }
/* 319 */       return capture(type);
/*     */     }
/*     */ 
/*     */     private Type[] capture(Type[] types) {
/* 323 */       Type[] result = new Type[types.length];
/* 324 */       for (int i = 0; i < types.length; i++) {
/* 325 */         result[i] = capture(types[i]);
/*     */       }
/* 327 */       return result;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class TypeMappingIntrospector
/*     */   {
/* 189 */     private static final TypeResolver.WildcardCapturer wildcardCapturer = new TypeResolver.WildcardCapturer(null);
/*     */ 
/* 191 */     private final Map<TypeVariable<?>, Type> mappings = Maps.newHashMap();
/* 192 */     private final Set<Type> introspectedTypes = Sets.newHashSet();
/*     */ 
/*     */     static ImmutableMap<TypeVariable<?>, Type> getTypeMappings(Type contextType)
/*     */     {
/* 200 */       TypeMappingIntrospector introspector = new TypeMappingIntrospector();
/* 201 */       introspector.introspect(wildcardCapturer.capture(contextType));
/* 202 */       return ImmutableMap.copyOf(introspector.mappings);
/*     */     }
/*     */ 
/*     */     private void introspect(Type type) {
/* 206 */       if (!this.introspectedTypes.add(type)) {
/* 207 */         return;
/*     */       }
/* 209 */       if ((type instanceof ParameterizedType))
/* 210 */         introspectParameterizedType((ParameterizedType)type);
/* 211 */       else if ((type instanceof Class))
/* 212 */         introspectClass((Class)type);
/* 213 */       else if ((type instanceof TypeVariable)) {
/* 214 */         for (Type bound : ((TypeVariable)type).getBounds())
/* 215 */           introspect(bound);
/*     */       }
/* 217 */       else if ((type instanceof WildcardType))
/* 218 */         for (Type bound : ((WildcardType)type).getUpperBounds())
/* 219 */           introspect(bound);
/*     */     }
/*     */ 
/*     */     private void introspectClass(Class<?> clazz)
/*     */     {
/* 225 */       introspect(clazz.getGenericSuperclass());
/* 226 */       for (Type interfaceType : clazz.getGenericInterfaces())
/* 227 */         introspect(interfaceType);
/*     */     }
/*     */ 
/*     */     private void introspectParameterizedType(ParameterizedType parameterizedType)
/*     */     {
/* 233 */       Class rawClass = (Class)parameterizedType.getRawType();
/* 234 */       TypeVariable[] vars = rawClass.getTypeParameters();
/* 235 */       Type[] typeArgs = parameterizedType.getActualTypeArguments();
/* 236 */       Preconditions.checkState(vars.length == typeArgs.length);
/* 237 */       for (int i = 0; i < vars.length; i++) {
/* 238 */         map(vars[i], typeArgs[i]);
/*     */       }
/* 240 */       introspectClass(rawClass);
/* 241 */       introspect(parameterizedType.getOwnerType());
/*     */     }
/*     */ 
/*     */     private void map(TypeVariable<?> var, Type arg) {
/* 245 */       if (this.mappings.containsKey(var))
/*     */       {
/* 251 */         return;
/*     */       }
/*     */ 
/* 254 */       for (Type t = arg; t != null; t = (Type)this.mappings.get(t)) {
/* 255 */         if (var.equals(t))
/*     */         {
/* 260 */           for (Type x = arg; x != null; x = (Type)this.mappings.remove(x));
/* 261 */           return;
/*     */         }
/*     */       }
/* 264 */       this.mappings.put(var, arg);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.reflect.TypeResolver
 * JD-Core Version:    0.6.2
 */