/*     */ package com.google.gson.internal;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.Array;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.GenericDeclaration;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public final class $Gson$Types
/*     */ {
/*  43 */   static final Type[] EMPTY_TYPE_ARRAY = new Type[0];
/*     */ 
/*     */   public static ParameterizedType newParameterizedTypeWithOwner(Type ownerType, Type rawType, Type[] typeArguments)
/*     */   {
/*  55 */     return new ParameterizedTypeImpl(ownerType, rawType, typeArguments);
/*     */   }
/*     */ 
/*     */   public static GenericArrayType arrayOf(Type componentType)
/*     */   {
/*  65 */     return new GenericArrayTypeImpl(componentType);
/*     */   }
/*     */ 
/*     */   public static WildcardType subtypeOf(Type bound)
/*     */   {
/*  75 */     return new WildcardTypeImpl(new Type[] { bound }, EMPTY_TYPE_ARRAY);
/*     */   }
/*     */ 
/*     */   public static WildcardType supertypeOf(Type bound)
/*     */   {
/*  84 */     return new WildcardTypeImpl(new Type[] { Object.class }, new Type[] { bound });
/*     */   }
/*     */ 
/*     */   public static Type canonicalize(Type type)
/*     */   {
/*  93 */     if ((type instanceof Class)) {
/*  94 */       Class c = (Class)type;
/*  95 */       return c.isArray() ? new GenericArrayTypeImpl(canonicalize(c.getComponentType())) : c;
/*     */     }
/*  97 */     if ((type instanceof ParameterizedType)) {
/*  98 */       ParameterizedType p = (ParameterizedType)type;
/*  99 */       return new ParameterizedTypeImpl(p.getOwnerType(), p.getRawType(), p.getActualTypeArguments());
/*     */     }
/*     */ 
/* 102 */     if ((type instanceof GenericArrayType)) {
/* 103 */       GenericArrayType g = (GenericArrayType)type;
/* 104 */       return new GenericArrayTypeImpl(g.getGenericComponentType());
/*     */     }
/* 106 */     if ((type instanceof WildcardType)) {
/* 107 */       WildcardType w = (WildcardType)type;
/* 108 */       return new WildcardTypeImpl(w.getUpperBounds(), w.getLowerBounds());
/*     */     }
/*     */ 
/* 112 */     return type;
/*     */   }
/*     */ 
/*     */   public static Class<?> getRawType(Type type)
/*     */   {
/* 117 */     if ((type instanceof Class))
/*     */     {
/* 119 */       return (Class)type;
/*     */     }
/* 121 */     if ((type instanceof ParameterizedType)) {
/* 122 */       ParameterizedType parameterizedType = (ParameterizedType)type;
/*     */ 
/* 127 */       Type rawType = parameterizedType.getRawType();
/* 128 */       .Gson.Preconditions.checkArgument(rawType instanceof Class);
/* 129 */       return (Class)rawType;
/*     */     }
/* 131 */     if ((type instanceof GenericArrayType)) {
/* 132 */       Type componentType = ((GenericArrayType)type).getGenericComponentType();
/* 133 */       return Array.newInstance(getRawType(componentType), 0).getClass();
/*     */     }
/* 135 */     if ((type instanceof TypeVariable))
/*     */     {
/* 138 */       return Object.class;
/*     */     }
/* 140 */     if ((type instanceof WildcardType)) {
/* 141 */       return getRawType(((WildcardType)type).getUpperBounds()[0]);
/*     */     }
/*     */ 
/* 144 */     String className = type == null ? "null" : type.getClass().getName();
/* 145 */     throw new IllegalArgumentException("Expected a Class, ParameterizedType, or GenericArrayType, but <" + type + "> is of type " + className);
/*     */   }
/*     */ 
/*     */   static boolean equal(Object a, Object b)
/*     */   {
/* 151 */     return (a == b) || ((a != null) && (a.equals(b)));
/*     */   }
/*     */ 
/*     */   public static boolean equals(Type a, Type b)
/*     */   {
/* 158 */     if (a == b)
/*     */     {
/* 160 */       return true;
/*     */     }
/* 162 */     if ((a instanceof Class))
/*     */     {
/* 164 */       return a.equals(b);
/*     */     }
/* 166 */     if ((a instanceof ParameterizedType)) {
/* 167 */       if (!(b instanceof ParameterizedType)) {
/* 168 */         return false;
/*     */       }
/*     */ 
/* 172 */       ParameterizedType pa = (ParameterizedType)a;
/* 173 */       ParameterizedType pb = (ParameterizedType)b;
/* 174 */       return (equal(pa.getOwnerType(), pb.getOwnerType())) && (pa.getRawType().equals(pb.getRawType())) && (Arrays.equals(pa.getActualTypeArguments(), pb.getActualTypeArguments()));
/*     */     }
/*     */ 
/* 178 */     if ((a instanceof GenericArrayType)) {
/* 179 */       if (!(b instanceof GenericArrayType)) {
/* 180 */         return false;
/*     */       }
/*     */ 
/* 183 */       GenericArrayType ga = (GenericArrayType)a;
/* 184 */       GenericArrayType gb = (GenericArrayType)b;
/* 185 */       return equals(ga.getGenericComponentType(), gb.getGenericComponentType());
/*     */     }
/* 187 */     if ((a instanceof WildcardType)) {
/* 188 */       if (!(b instanceof WildcardType)) {
/* 189 */         return false;
/*     */       }
/*     */ 
/* 192 */       WildcardType wa = (WildcardType)a;
/* 193 */       WildcardType wb = (WildcardType)b;
/* 194 */       return (Arrays.equals(wa.getUpperBounds(), wb.getUpperBounds())) && (Arrays.equals(wa.getLowerBounds(), wb.getLowerBounds()));
/*     */     }
/*     */ 
/* 197 */     if ((a instanceof TypeVariable)) {
/* 198 */       if (!(b instanceof TypeVariable)) {
/* 199 */         return false;
/*     */       }
/* 201 */       TypeVariable va = (TypeVariable)a;
/* 202 */       TypeVariable vb = (TypeVariable)b;
/* 203 */       return (va.getGenericDeclaration() == vb.getGenericDeclaration()) && (va.getName().equals(vb.getName()));
/*     */     }
/*     */ 
/* 208 */     return false;
/*     */   }
/*     */ 
/*     */   private static int hashCodeOrZero(Object o)
/*     */   {
/* 213 */     return o != null ? o.hashCode() : 0;
/*     */   }
/*     */ 
/*     */   public static String typeToString(Type type) {
/* 217 */     return (type instanceof Class) ? ((Class)type).getName() : type.toString();
/*     */   }
/*     */ 
/*     */   static Type getGenericSupertype(Type context, Class<?> rawType, Class<?> toResolve)
/*     */   {
/* 226 */     if (toResolve == rawType) {
/* 227 */       return context;
/*     */     }
/*     */ 
/* 231 */     if (toResolve.isInterface()) {
/* 232 */       Class[] interfaces = rawType.getInterfaces();
/* 233 */       int i = 0; for (int length = interfaces.length; i < length; i++) {
/* 234 */         if (interfaces[i] == toResolve)
/* 235 */           return rawType.getGenericInterfaces()[i];
/* 236 */         if (toResolve.isAssignableFrom(interfaces[i])) {
/* 237 */           return getGenericSupertype(rawType.getGenericInterfaces()[i], interfaces[i], toResolve);
/*     */         }
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 243 */     if (!rawType.isInterface()) {
/* 244 */       while (rawType != Object.class) {
/* 245 */         Class rawSupertype = rawType.getSuperclass();
/* 246 */         if (rawSupertype == toResolve)
/* 247 */           return rawType.getGenericSuperclass();
/* 248 */         if (toResolve.isAssignableFrom(rawSupertype)) {
/* 249 */           return getGenericSupertype(rawType.getGenericSuperclass(), rawSupertype, toResolve);
/*     */         }
/* 251 */         rawType = rawSupertype;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 256 */     return toResolve;
/*     */   }
/*     */ 
/*     */   static Type getSupertype(Type context, Class<?> contextRawType, Class<?> supertype)
/*     */   {
/* 267 */     .Gson.Preconditions.checkArgument(supertype.isAssignableFrom(contextRawType));
/* 268 */     return resolve(context, contextRawType, getGenericSupertype(context, contextRawType, supertype));
/*     */   }
/*     */ 
/*     */   public static Type getArrayComponentType(Type array)
/*     */   {
/* 277 */     return (array instanceof GenericArrayType) ? ((GenericArrayType)array).getGenericComponentType() : ((Class)array).getComponentType();
/*     */   }
/*     */ 
/*     */   public static Type getCollectionElementType(Type context, Class<?> contextRawType)
/*     */   {
/* 287 */     Type collectionType = getSupertype(context, contextRawType, Collection.class);
/*     */ 
/* 289 */     if ((collectionType instanceof WildcardType)) {
/* 290 */       collectionType = ((WildcardType)collectionType).getUpperBounds()[0];
/*     */     }
/* 292 */     if ((collectionType instanceof ParameterizedType)) {
/* 293 */       return ((ParameterizedType)collectionType).getActualTypeArguments()[0];
/*     */     }
/* 295 */     return Object.class;
/*     */   }
/*     */ 
/*     */   public static Type[] getMapKeyAndValueTypes(Type context, Class<?> contextRawType)
/*     */   {
/* 308 */     if (context == Properties.class) {
/* 309 */       return new Type[] { String.class, String.class };
/*     */     }
/*     */ 
/* 312 */     Type mapType = getSupertype(context, contextRawType, Map.class);
/*     */ 
/* 314 */     if ((mapType instanceof ParameterizedType)) {
/* 315 */       ParameterizedType mapParameterizedType = (ParameterizedType)mapType;
/* 316 */       return mapParameterizedType.getActualTypeArguments();
/*     */     }
/* 318 */     return new Type[] { Object.class, Object.class };
/*     */   }
/*     */ 
/*     */   public static Type resolve(Type context, Class<?> contextRawType, Type toResolve)
/*     */   {
/* 324 */     while ((toResolve instanceof TypeVariable)) {
/* 325 */       TypeVariable typeVariable = (TypeVariable)toResolve;
/* 326 */       toResolve = resolveTypeVariable(context, contextRawType, typeVariable);
/* 327 */       if (toResolve == typeVariable) {
/* 328 */         return toResolve;
/*     */       }
/*     */     }
/* 331 */     if (((toResolve instanceof Class)) && (((Class)toResolve).isArray())) {
/* 332 */       Class original = (Class)toResolve;
/* 333 */       Type componentType = original.getComponentType();
/* 334 */       Type newComponentType = resolve(context, contextRawType, componentType);
/* 335 */       return componentType == newComponentType ? original : arrayOf(newComponentType);
/*     */     }
/*     */ 
/* 339 */     if ((toResolve instanceof GenericArrayType)) {
/* 340 */       GenericArrayType original = (GenericArrayType)toResolve;
/* 341 */       Type componentType = original.getGenericComponentType();
/* 342 */       Type newComponentType = resolve(context, contextRawType, componentType);
/* 343 */       return componentType == newComponentType ? original : arrayOf(newComponentType);
/*     */     }
/*     */ 
/* 347 */     if ((toResolve instanceof ParameterizedType)) {
/* 348 */       ParameterizedType original = (ParameterizedType)toResolve;
/* 349 */       Type ownerType = original.getOwnerType();
/* 350 */       Type newOwnerType = resolve(context, contextRawType, ownerType);
/* 351 */       boolean changed = newOwnerType != ownerType;
/*     */ 
/* 353 */       Type[] args = original.getActualTypeArguments();
/* 354 */       int t = 0; for (int length = args.length; t < length; t++) {
/* 355 */         Type resolvedTypeArgument = resolve(context, contextRawType, args[t]);
/* 356 */         if (resolvedTypeArgument != args[t]) {
/* 357 */           if (!changed) {
/* 358 */             args = (Type[])args.clone();
/* 359 */             changed = true;
/*     */           }
/* 361 */           args[t] = resolvedTypeArgument;
/*     */         }
/*     */       }
/*     */ 
/* 365 */       return changed ? newParameterizedTypeWithOwner(newOwnerType, original.getRawType(), args) : original;
/*     */     }
/*     */ 
/* 369 */     if ((toResolve instanceof WildcardType)) {
/* 370 */       WildcardType original = (WildcardType)toResolve;
/* 371 */       Type[] originalLowerBound = original.getLowerBounds();
/* 372 */       Type[] originalUpperBound = original.getUpperBounds();
/*     */ 
/* 374 */       if (originalLowerBound.length == 1) {
/* 375 */         Type lowerBound = resolve(context, contextRawType, originalLowerBound[0]);
/* 376 */         if (lowerBound != originalLowerBound[0])
/* 377 */           return supertypeOf(lowerBound);
/*     */       }
/* 379 */       else if (originalUpperBound.length == 1) {
/* 380 */         Type upperBound = resolve(context, contextRawType, originalUpperBound[0]);
/* 381 */         if (upperBound != originalUpperBound[0]) {
/* 382 */           return subtypeOf(upperBound);
/*     */         }
/*     */       }
/* 385 */       return original;
/*     */     }
/*     */ 
/* 388 */     return toResolve;
/*     */   }
/*     */ 
/*     */   static Type resolveTypeVariable(Type context, Class<?> contextRawType, TypeVariable<?> unknown)
/*     */   {
/* 394 */     Class declaredByRaw = declaringClassOf(unknown);
/*     */ 
/* 397 */     if (declaredByRaw == null) {
/* 398 */       return unknown;
/*     */     }
/*     */ 
/* 401 */     Type declaredBy = getGenericSupertype(context, contextRawType, declaredByRaw);
/* 402 */     if ((declaredBy instanceof ParameterizedType)) {
/* 403 */       int index = indexOf(declaredByRaw.getTypeParameters(), unknown);
/* 404 */       return ((ParameterizedType)declaredBy).getActualTypeArguments()[index];
/*     */     }
/*     */ 
/* 407 */     return unknown;
/*     */   }
/*     */ 
/*     */   private static int indexOf(Object[] array, Object toFind) {
/* 411 */     for (int i = 0; i < array.length; i++) {
/* 412 */       if (toFind.equals(array[i])) {
/* 413 */         return i;
/*     */       }
/*     */     }
/* 416 */     throw new NoSuchElementException();
/*     */   }
/*     */ 
/*     */   private static Class<?> declaringClassOf(TypeVariable<?> typeVariable)
/*     */   {
/* 424 */     GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
/* 425 */     return (genericDeclaration instanceof Class) ? (Class)genericDeclaration : null;
/*     */   }
/*     */ 
/*     */   private static void checkNotPrimitive(Type type)
/*     */   {
/* 431 */     .Gson.Preconditions.checkArgument((!(type instanceof Class)) || (!((Class)type).isPrimitive()));
/*     */   }
/*     */ 
/*     */   private static final class WildcardTypeImpl
/*     */     implements WildcardType, Serializable
/*     */   {
/*     */     private final Type upperBound;
/*     */     private final Type lowerBound;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public WildcardTypeImpl(Type[] upperBounds, Type[] lowerBounds)
/*     */     {
/* 535 */       .Gson.Preconditions.checkArgument(lowerBounds.length <= 1);
/* 536 */       .Gson.Preconditions.checkArgument(upperBounds.length == 1);
/*     */ 
/* 538 */       if (lowerBounds.length == 1) {
/* 539 */         .Gson.Preconditions.checkNotNull(lowerBounds[0]);
/* 540 */         .Gson.Types.checkNotPrimitive(lowerBounds[0]);
/* 541 */         .Gson.Preconditions.checkArgument(upperBounds[0] == Object.class);
/* 542 */         this.lowerBound = .Gson.Types.canonicalize(lowerBounds[0]);
/* 543 */         this.upperBound = Object.class;
/*     */       }
/*     */       else {
/* 546 */         .Gson.Preconditions.checkNotNull(upperBounds[0]);
/* 547 */         .Gson.Types.checkNotPrimitive(upperBounds[0]);
/* 548 */         this.lowerBound = null;
/* 549 */         this.upperBound = .Gson.Types.canonicalize(upperBounds[0]);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Type[] getUpperBounds() {
/* 554 */       return new Type[] { this.upperBound };
/*     */     }
/*     */ 
/*     */     public Type[] getLowerBounds() {
/* 558 */       return this.lowerBound != null ? new Type[] { this.lowerBound } : .Gson.Types.EMPTY_TYPE_ARRAY;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other) {
/* 562 */       return ((other instanceof Serializable)) && (.Gson.Types.equals(this, (Serializable)other));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 568 */       return (this.lowerBound != null ? 31 + this.lowerBound.hashCode() : 1) ^ 31 + this.upperBound.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 573 */       if (this.lowerBound != null)
/* 574 */         return "? super " + .Gson.Types.typeToString(this.lowerBound);
/* 575 */       if (this.upperBound == Object.class) {
/* 576 */         return "?";
/*     */       }
/* 578 */       return "? extends " + .Gson.Types.typeToString(this.upperBound);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class GenericArrayTypeImpl
/*     */     implements GenericArrayType, Serializable
/*     */   {
/*     */     private final Type componentType;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public GenericArrayTypeImpl(Type componentType)
/*     */     {
/* 502 */       this.componentType = .Gson.Types.canonicalize(componentType);
/*     */     }
/*     */ 
/*     */     public Type getGenericComponentType() {
/* 506 */       return this.componentType;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object o) {
/* 510 */       return ((o instanceof Serializable)) && (.Gson.Types.equals(this, (Serializable)o));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 515 */       return this.componentType.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 519 */       return .Gson.Types.typeToString(this.componentType) + "[]";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ParameterizedTypeImpl
/*     */     implements ParameterizedType, Serializable
/*     */   {
/*     */     private final Type ownerType;
/*     */     private final Type rawType;
/*     */     private final Type[] typeArguments;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public ParameterizedTypeImpl(Type ownerType, Type rawType, Type[] typeArguments)
/*     */     {
/* 441 */       if ((rawType instanceof Class)) {
/* 442 */         Class rawTypeAsClass = (Class)rawType;
/* 443 */         .Gson.Preconditions.checkArgument((ownerType != null) || (rawTypeAsClass.getEnclosingClass() == null));
/* 444 */         .Gson.Preconditions.checkArgument((ownerType == null) || (rawTypeAsClass.getEnclosingClass() != null));
/*     */       }
/*     */ 
/* 447 */       this.ownerType = (ownerType == null ? null : .Gson.Types.canonicalize(ownerType));
/* 448 */       this.rawType = .Gson.Types.canonicalize(rawType);
/* 449 */       this.typeArguments = ((Type[])typeArguments.clone());
/* 450 */       for (int t = 0; t < this.typeArguments.length; t++) {
/* 451 */         .Gson.Preconditions.checkNotNull(this.typeArguments[t]);
/* 452 */         .Gson.Types.checkNotPrimitive(this.typeArguments[t]);
/* 453 */         this.typeArguments[t] = .Gson.Types.canonicalize(this.typeArguments[t]);
/*     */       }
/*     */     }
/*     */ 
/*     */     public Type[] getActualTypeArguments() {
/* 458 */       return (Type[])this.typeArguments.clone();
/*     */     }
/*     */ 
/*     */     public Type getRawType() {
/* 462 */       return this.rawType;
/*     */     }
/*     */ 
/*     */     public Type getOwnerType() {
/* 466 */       return this.ownerType;
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other) {
/* 470 */       return ((other instanceof Serializable)) && (.Gson.Types.equals(this, (Serializable)other));
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 475 */       return Arrays.hashCode(this.typeArguments) ^ this.rawType.hashCode() ^ .Gson.Types.hashCodeOrZero(this.ownerType);
/*     */     }
/*     */ 
/*     */     public String toString()
/*     */     {
/* 481 */       StringBuilder stringBuilder = new StringBuilder(30 * (this.typeArguments.length + 1));
/* 482 */       stringBuilder.append(.Gson.Types.typeToString(this.rawType));
/*     */ 
/* 484 */       if (this.typeArguments.length == 0) {
/* 485 */         return stringBuilder.toString();
/*     */       }
/*     */ 
/* 488 */       stringBuilder.append("<").append(.Gson.Types.typeToString(this.typeArguments[0]));
/* 489 */       for (int i = 1; i < this.typeArguments.length; i++) {
/* 490 */         stringBuilder.append(", ").append(.Gson.Types.typeToString(this.typeArguments[i]));
/*     */       }
/* 492 */       return ">";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal..Gson.Types
 * JD-Core Version:    0.6.2
 */