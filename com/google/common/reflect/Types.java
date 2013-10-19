/*     */ package com.google.common.reflect;
/*     */ 
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicates;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableList.Builder;
/*     */ import com.google.common.collect.Iterables;
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
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ final class Types
/*     */ {
/*  52 */   private static final Function<Type, String> TYPE_TO_STRING = new Function()
/*     */   {
/*     */     public String apply(Type from) {
/*  55 */       return Types.toString(from);
/*     */     }
/*  52 */   };
/*     */ 
/*  59 */   private static final Joiner COMMA_JOINER = Joiner.on(", ").useForNull("null");
/*     */ 
/*     */   static Type newArrayType(Type componentType)
/*     */   {
/*  63 */     if ((componentType instanceof WildcardType)) {
/*  64 */       WildcardType wildcard = (WildcardType)componentType;
/*  65 */       Type[] lowerBounds = wildcard.getLowerBounds();
/*  66 */       Preconditions.checkArgument(lowerBounds.length <= 1, "Wildcard cannot have more than one lower bounds.");
/*  67 */       if (lowerBounds.length == 1) {
/*  68 */         return supertypeOf(newArrayType(lowerBounds[0]));
/*     */       }
/*  70 */       Type[] upperBounds = wildcard.getUpperBounds();
/*  71 */       Preconditions.checkArgument(upperBounds.length == 1, "Wildcard should have only one upper bound.");
/*  72 */       return subtypeOf(newArrayType(upperBounds[0]));
/*     */     }
/*     */ 
/*  75 */     return JavaVersion.CURRENT.newArrayType(componentType);
/*     */   }
/*     */ 
/*     */   static ParameterizedType newParameterizedTypeWithOwner(@Nullable Type ownerType, Class<?> rawType, Type[] arguments)
/*     */   {
/*  84 */     if (ownerType == null) {
/*  85 */       return newParameterizedType(rawType, arguments);
/*     */     }
/*     */ 
/*  88 */     Preconditions.checkNotNull(arguments);
/*  89 */     Preconditions.checkArgument(rawType.getEnclosingClass() != null, "Owner type for unenclosed %s", new Object[] { rawType });
/*  90 */     return new ParameterizedTypeImpl(ownerType, rawType, arguments);
/*     */   }
/*     */ 
/*     */   static ParameterizedType newParameterizedType(Class<?> rawType, Type[] arguments)
/*     */   {
/*  98 */     return new ParameterizedTypeImpl(ClassOwnership.JVM_BEHAVIOR.getOwnerType(rawType), rawType, arguments);
/*     */   }
/*     */ 
/*     */   static <D extends GenericDeclaration> TypeVariable<D> newTypeVariable(D declaration, String name, Type[] bounds)
/*     */   {
/* 148 */     return new TypeVariableImpl(declaration, name, bounds.length == 0 ? new Type[] { Object.class } : bounds);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static WildcardType subtypeOf(Type upperBound)
/*     */   {
/* 158 */     return new WildcardTypeImpl(new Type[0], new Type[] { upperBound });
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static WildcardType supertypeOf(Type lowerBound) {
/* 163 */     return new WildcardTypeImpl(new Type[] { lowerBound }, new Type[] { Object.class });
/*     */   }
/*     */ 
/*     */   static String toString(Type type)
/*     */   {
/* 176 */     return (type instanceof Class) ? ((Class)type).getName() : type.toString();
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   static Type getComponentType(Type type)
/*     */   {
/* 182 */     Preconditions.checkNotNull(type);
/* 183 */     if ((type instanceof Class))
/* 184 */       return ((Class)type).getComponentType();
/* 185 */     if ((type instanceof GenericArrayType))
/* 186 */       return ((GenericArrayType)type).getGenericComponentType();
/* 187 */     if ((type instanceof WildcardType))
/* 188 */       return subtypeOfComponentType(((WildcardType)type).getUpperBounds());
/* 189 */     if ((type instanceof TypeVariable)) {
/* 190 */       return subtypeOfComponentType(((TypeVariable)type).getBounds());
/*     */     }
/* 192 */     return null;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private static Type subtypeOfComponentType(Type[] bounds)
/*     */   {
/* 201 */     for (Type bound : bounds) {
/* 202 */       Type componentType = getComponentType(bound);
/* 203 */       if (componentType != null)
/*     */       {
/* 206 */         if ((componentType instanceof Class)) {
/* 207 */           Class componentClass = (Class)componentType;
/* 208 */           if (componentClass.isPrimitive()) {
/* 209 */             return componentClass;
/*     */           }
/*     */         }
/* 212 */         return subtypeOf(componentType);
/*     */       }
/*     */     }
/* 215 */     return null;
/*     */   }
/*     */ 
/*     */   private static Type[] toArray(Collection<Type> types)
/*     */   {
/* 403 */     return (Type[])types.toArray(new Type[types.size()]);
/*     */   }
/*     */ 
/*     */   private static Iterable<Type> filterUpperBounds(Iterable<Type> bounds) {
/* 407 */     return Iterables.filter(bounds, Predicates.not(Predicates.equalTo(Object.class)));
/*     */   }
/*     */ 
/*     */   private static void disallowPrimitiveType(Type[] types, String usedAs)
/*     */   {
/* 412 */     for (Type type : types)
/* 413 */       if ((type instanceof Class)) {
/* 414 */         Class cls = (Class)type;
/* 415 */         Preconditions.checkArgument(!cls.isPrimitive(), "Primitive type '%s' used as %s", new Object[] { cls, usedAs });
/*     */       }
/*     */   }
/*     */ 
/*     */   static IllegalArgumentException buildUnexpectedTypeException(Type type, Class<?>[] expected)
/*     */   {
/* 424 */     StringBuilder exceptionMessage = new StringBuilder("Unexpected type. Expected one of: ");
/*     */ 
/* 426 */     for (Class clazz : expected) {
/* 427 */       exceptionMessage.append(clazz.getName()).append(", ");
/*     */     }
/* 429 */     exceptionMessage.append("but got: ").append(type.getClass().getName()).append(", for type: ").append(toString(type)).append('.');
/*     */ 
/* 432 */     return new IllegalArgumentException(exceptionMessage.toString());
/*     */   }
/*     */ 
/*     */   static Class<?> getArrayClass(Class<?> componentType)
/*     */   {
/* 440 */     return Array.newInstance(componentType, 0).getClass();
/*     */   }
/*     */ 
/*     */   static abstract enum JavaVersion
/*     */   {
/* 446 */     JAVA6, 
/*     */ 
/* 461 */     JAVA7;
/*     */ 
/* 475 */     static final JavaVersion CURRENT = (new TypeCapture() {  } .capture() instanceof Class) ? JAVA7 : JAVA6;
/*     */ 
/*     */     abstract Type newArrayType(Type paramType);
/*     */ 
/*     */     abstract Type usedInGenericType(Type paramType);
/*     */ 
/*     */     final ImmutableList<Type> usedInGenericType(Type[] types) {
/* 482 */       ImmutableList.Builder builder = ImmutableList.builder();
/* 483 */       for (Type type : types) {
/* 484 */         builder.add(usedInGenericType(type));
/*     */       }
/* 486 */       return builder.build();
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class WildcardTypeImpl
/*     */     implements WildcardType, Serializable
/*     */   {
/*     */     private final ImmutableList<Type> lowerBounds;
/*     */     private final ImmutableList<Type> upperBounds;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     WildcardTypeImpl(Type[] lowerBounds, Type[] upperBounds)
/*     */     {
/* 361 */       Types.disallowPrimitiveType(lowerBounds, "lower bound for wildcard");
/* 362 */       Types.disallowPrimitiveType(upperBounds, "upper bound for wildcard");
/* 363 */       this.lowerBounds = Types.JavaVersion.CURRENT.usedInGenericType(lowerBounds);
/* 364 */       this.upperBounds = Types.JavaVersion.CURRENT.usedInGenericType(upperBounds);
/*     */     }
/*     */ 
/*     */     public Type[] getLowerBounds() {
/* 368 */       return Types.toArray(this.lowerBounds);
/*     */     }
/*     */ 
/*     */     public Type[] getUpperBounds() {
/* 372 */       return Types.toArray(this.upperBounds);
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 376 */       if ((obj instanceof Serializable)) {
/* 377 */         WildcardType that = (Serializable)obj;
/* 378 */         return (this.lowerBounds.equals(Arrays.asList(that.getLowerBounds()))) && (this.upperBounds.equals(Arrays.asList(that.getUpperBounds())));
/*     */       }
/*     */ 
/* 381 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 385 */       return this.lowerBounds.hashCode() ^ this.upperBounds.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 389 */       StringBuilder builder = new StringBuilder("?");
/* 390 */       for (Type lowerBound : this.lowerBounds) {
/* 391 */         builder.append(" super ").append(Types.toString(lowerBound));
/*     */       }
/* 393 */       for (Type upperBound : Types.filterUpperBounds(this.upperBounds)) {
/* 394 */         builder.append(" extends ").append(Types.toString(upperBound));
/*     */       }
/* 396 */       return builder.toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class TypeVariableImpl<D extends GenericDeclaration>
/*     */     implements TypeVariable<D>
/*     */   {
/*     */     private final D genericDeclaration;
/*     */     private final String name;
/*     */     private final ImmutableList<Type> bounds;
/*     */ 
/*     */     TypeVariableImpl(D genericDeclaration, String name, Type[] bounds)
/*     */     {
/* 319 */       Types.disallowPrimitiveType(bounds, "bound for type variable");
/* 320 */       this.genericDeclaration = ((GenericDeclaration)Preconditions.checkNotNull(genericDeclaration));
/* 321 */       this.name = ((String)Preconditions.checkNotNull(name));
/* 322 */       this.bounds = ImmutableList.copyOf(bounds);
/*     */     }
/*     */ 
/*     */     public Type[] getBounds() {
/* 326 */       return Types.toArray(this.bounds);
/*     */     }
/*     */ 
/*     */     public D getGenericDeclaration() {
/* 330 */       return this.genericDeclaration;
/*     */     }
/*     */ 
/*     */     public String getName() {
/* 334 */       return this.name;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 338 */       return this.name;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 342 */       return this.genericDeclaration.hashCode() ^ this.name.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 346 */       if ((obj instanceof TypeVariable)) {
/* 347 */         TypeVariable that = (TypeVariable)obj;
/* 348 */         return (this.name.equals(that.getName())) && (this.genericDeclaration.equals(that.getGenericDeclaration()));
/*     */       }
/*     */ 
/* 351 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class ParameterizedTypeImpl
/*     */     implements ParameterizedType, Serializable
/*     */   {
/*     */     private final Type ownerType;
/*     */     private final ImmutableList<Type> argumentsList;
/*     */     private final Class<?> rawType;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     ParameterizedTypeImpl(@Nullable Type ownerType, Class<?> rawType, Type[] typeArguments)
/*     */     {
/* 260 */       Preconditions.checkNotNull(rawType);
/* 261 */       Preconditions.checkArgument(typeArguments.length == rawType.getTypeParameters().length);
/* 262 */       Types.disallowPrimitiveType(typeArguments, "type parameter");
/* 263 */       this.ownerType = ownerType;
/* 264 */       this.rawType = rawType;
/* 265 */       this.argumentsList = Types.JavaVersion.CURRENT.usedInGenericType(typeArguments);
/*     */     }
/*     */ 
/*     */     public Type[] getActualTypeArguments() {
/* 269 */       return Types.toArray(this.argumentsList);
/*     */     }
/*     */ 
/*     */     public Type getRawType() {
/* 273 */       return this.rawType;
/*     */     }
/*     */ 
/*     */     public Type getOwnerType() {
/* 277 */       return this.ownerType;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 281 */       StringBuilder builder = new StringBuilder();
/* 282 */       if (this.ownerType != null) {
/* 283 */         builder.append(Types.toString(this.ownerType)).append('.');
/*     */       }
/* 285 */       builder.append(this.rawType.getName()).append('<').append(Types.COMMA_JOINER.join(Iterables.transform(this.argumentsList, Types.TYPE_TO_STRING))).append('>');
/*     */ 
/* 289 */       return builder.toString();
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 293 */       return (this.ownerType == null ? 0 : this.ownerType.hashCode()) ^ this.argumentsList.hashCode() ^ this.rawType.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other)
/*     */     {
/* 298 */       if (!(other instanceof Serializable)) {
/* 299 */         return false;
/*     */       }
/* 301 */       ParameterizedType that = (Serializable)other;
/* 302 */       return (getRawType().equals(that.getRawType())) && (Objects.equal(getOwnerType(), that.getOwnerType())) && (Arrays.equals(getActualTypeArguments(), that.getActualTypeArguments()));
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class GenericArrayTypeImpl
/*     */     implements GenericArrayType, Serializable
/*     */   {
/*     */     private final Type componentType;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     GenericArrayTypeImpl(Type componentType)
/*     */     {
/* 224 */       this.componentType = Types.JavaVersion.CURRENT.usedInGenericType(componentType);
/*     */     }
/*     */ 
/*     */     public Type getGenericComponentType() {
/* 228 */       return this.componentType;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 232 */       return Types.toString(this.componentType) + "[]";
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 236 */       return this.componentType.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object obj) {
/* 240 */       if ((obj instanceof Serializable)) {
/* 241 */         GenericArrayType that = (Serializable)obj;
/* 242 */         return Objects.equal(getGenericComponentType(), that.getGenericComponentType());
/*     */       }
/*     */ 
/* 245 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract enum ClassOwnership
/*     */   {
/* 105 */     OWNED_BY_ENCLOSING_CLASS, 
/*     */ 
/* 112 */     LOCAL_CLASS_HAS_NO_OWNER;
/*     */ 
/* 126 */     static final ClassOwnership JVM_BEHAVIOR = detectJvmBehavior();
/*     */ 
/*     */     @Nullable
/*     */     abstract Class<?> getOwnerType(Class<?> paramClass);
/*     */ 
/* 130 */     private static ClassOwnership detectJvmBehavior() { Class subclass = new 1LocalClass() {  } .getClass();
/* 131 */       ParameterizedType parameterizedType = (ParameterizedType)subclass.getGenericSuperclass();
/*     */ 
/* 133 */       for (ClassOwnership behavior : values()) {
/* 134 */         if (behavior.getOwnerType(1LocalClass.class) == parameterizedType.getOwnerType()) {
/* 135 */           return behavior;
/*     */         }
/*     */       }
/* 138 */       throw new AssertionError();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.reflect.Types
 * JD-Core Version:    0.6.2
 */