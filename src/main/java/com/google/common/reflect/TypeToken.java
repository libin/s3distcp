/*     */ package com.google.common.reflect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Predicate;
/*     */ import com.google.common.collect.AbstractSequentialIterator;
/*     */ import com.google.common.collect.ForwardingSet;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableList.Builder;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableSet;
/*     */ import com.google.common.collect.ImmutableSet.Builder;
/*     */ import com.google.common.collect.ImmutableSortedSet;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Iterators;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Ordering;
/*     */ import com.google.common.collect.Sets;
/*     */ import java.io.Serializable;
/*     */ import java.lang.reflect.GenericArrayType;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.util.Comparator;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.SortedSet;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public abstract class TypeToken<T> extends TypeCapture<T>
/*     */   implements Serializable
/*     */ {
/*     */   private final Type runtimeType;
/*     */   private transient TypeResolver typeResolver;
/*     */ 
/*     */   protected TypeToken()
/*     */   {
/* 122 */     this.runtimeType = capture();
/* 123 */     Preconditions.checkState(!(this.runtimeType instanceof TypeVariable), "Cannot construct a TypeToken for a type variable.\nYou probably meant to call new TypeToken<%s>(getClass()) that can resolve the type variable for you.\nIf you do need to create a TypeToken of a type variable, please use TypeToken.of() instead.", new Object[] { this.runtimeType });
/*     */   }
/*     */ 
/*     */   protected TypeToken(Class<?> declaringClass)
/*     */   {
/* 151 */     Type captured = super.capture();
/* 152 */     if ((captured instanceof Class))
/* 153 */       this.runtimeType = captured;
/*     */     else
/* 155 */       this.runtimeType = of(declaringClass).resolveType(captured).runtimeType;
/*     */   }
/*     */ 
/*     */   private TypeToken(Type type)
/*     */   {
/* 160 */     this.runtimeType = ((Type)Preconditions.checkNotNull(type));
/*     */   }
/*     */ 
/*     */   public static <T> TypeToken<T> of(Class<T> type)
/*     */   {
/* 165 */     return new SimpleTypeToken(type);
/*     */   }
/*     */ 
/*     */   public static TypeToken<?> of(Type type)
/*     */   {
/* 170 */     return new SimpleTypeToken(type);
/*     */   }
/*     */ 
/*     */   public final Class<? super T> getRawType()
/*     */   {
/* 188 */     Class rawType = getRawType(this.runtimeType);
/*     */ 
/* 190 */     Class result = rawType;
/* 191 */     return result;
/*     */   }
/*     */ 
/*     */   public final Type getType()
/*     */   {
/* 196 */     return this.runtimeType;
/*     */   }
/*     */ 
/*     */   public final <X> TypeToken<T> where(TypeParameter<X> typeParam, TypeToken<X> typeArg)
/*     */   {
/* 217 */     TypeResolver resolver = new TypeResolver().where(ImmutableMap.of(typeParam.typeVariable, typeArg.runtimeType));
/*     */ 
/* 220 */     return new SimpleTypeToken(resolver.resolve(this.runtimeType));
/*     */   }
/*     */ 
/*     */   public final <X> TypeToken<T> where(TypeParameter<X> typeParam, Class<X> typeArg)
/*     */   {
/* 241 */     return where(typeParam, of(typeArg));
/*     */   }
/*     */ 
/*     */   public final TypeToken<?> resolveType(Type type)
/*     */   {
/* 254 */     Preconditions.checkNotNull(type);
/* 255 */     TypeResolver resolver = this.typeResolver;
/* 256 */     if (resolver == null) {
/* 257 */       resolver = this.typeResolver = TypeResolver.accordingTo(this.runtimeType);
/*     */     }
/* 259 */     return of(resolver.resolve(type));
/*     */   }
/*     */ 
/*     */   private TypeToken<?> resolveSupertype(Type type) {
/* 263 */     TypeToken supertype = resolveType(type);
/*     */ 
/* 265 */     supertype.typeResolver = this.typeResolver;
/* 266 */     return supertype;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   final TypeToken<? super T> getGenericSuperclass()
/*     */   {
/* 284 */     if ((this.runtimeType instanceof TypeVariable))
/*     */     {
/* 286 */       return boundAsSuperclass(((TypeVariable)this.runtimeType).getBounds()[0]);
/*     */     }
/* 288 */     if ((this.runtimeType instanceof WildcardType))
/*     */     {
/* 290 */       return boundAsSuperclass(((WildcardType)this.runtimeType).getUpperBounds()[0]);
/*     */     }
/* 292 */     Type superclass = getRawType().getGenericSuperclass();
/* 293 */     if (superclass == null) {
/* 294 */       return null;
/*     */     }
/*     */ 
/* 297 */     TypeToken superToken = resolveSupertype(superclass);
/* 298 */     return superToken;
/*     */   }
/*     */   @Nullable
/*     */   private TypeToken<? super T> boundAsSuperclass(Type bound) {
/* 302 */     TypeToken token = of(bound);
/* 303 */     if (token.getRawType().isInterface()) {
/* 304 */       return null;
/*     */     }
/*     */ 
/* 307 */     TypeToken superclass = token;
/* 308 */     return superclass;
/*     */   }
/*     */ 
/*     */   final ImmutableList<TypeToken<? super T>> getGenericInterfaces()
/*     */   {
/* 324 */     if ((this.runtimeType instanceof TypeVariable)) {
/* 325 */       return boundsAsInterfaces(((TypeVariable)this.runtimeType).getBounds());
/*     */     }
/* 327 */     if ((this.runtimeType instanceof WildcardType)) {
/* 328 */       return boundsAsInterfaces(((WildcardType)this.runtimeType).getUpperBounds());
/*     */     }
/* 330 */     ImmutableList.Builder builder = ImmutableList.builder();
/* 331 */     for (Type interfaceType : getRawType().getGenericInterfaces())
/*     */     {
/* 333 */       TypeToken resolvedInterface = resolveSupertype(interfaceType);
/*     */ 
/* 335 */       builder.add(resolvedInterface);
/*     */     }
/* 337 */     return builder.build();
/*     */   }
/*     */ 
/*     */   private ImmutableList<TypeToken<? super T>> boundsAsInterfaces(Type[] bounds) {
/* 341 */     ImmutableList.Builder builder = ImmutableList.builder();
/* 342 */     for (Type bound : bounds)
/*     */     {
/* 344 */       TypeToken boundType = of(bound);
/* 345 */       if (boundType.getRawType().isInterface()) {
/* 346 */         builder.add(boundType);
/*     */       }
/*     */     }
/* 349 */     return builder.build();
/*     */   }
/*     */ 
/*     */   public final TypeToken<T>.TypeSet getTypes()
/*     */   {
/* 364 */     return new TypeSet();
/*     */   }
/*     */ 
/*     */   public final TypeToken<? super T> getSupertype(Class<? super T> superclass)
/*     */   {
/* 373 */     Preconditions.checkArgument(superclass.isAssignableFrom(getRawType()), "%s is not a super class of %s", new Object[] { superclass, this });
/*     */ 
/* 375 */     if ((this.runtimeType instanceof TypeVariable)) {
/* 376 */       return getSupertypeFromUpperBounds(superclass, ((TypeVariable)this.runtimeType).getBounds());
/*     */     }
/* 378 */     if ((this.runtimeType instanceof WildcardType)) {
/* 379 */       return getSupertypeFromUpperBounds(superclass, ((WildcardType)this.runtimeType).getUpperBounds());
/*     */     }
/* 381 */     if (superclass.isArray()) {
/* 382 */       return getArraySupertype(superclass);
/*     */     }
/*     */ 
/* 385 */     TypeToken supertype = resolveSupertype(toGenericType(superclass).runtimeType);
/*     */ 
/* 387 */     return supertype;
/*     */   }
/*     */ 
/*     */   public final TypeToken<? extends T> getSubtype(Class<?> subclass)
/*     */   {
/* 396 */     Preconditions.checkArgument(!(this.runtimeType instanceof TypeVariable), "Cannot get subtype of type variable <%s>", new Object[] { this });
/*     */ 
/* 398 */     if ((this.runtimeType instanceof WildcardType)) {
/* 399 */       return getSubtypeFromLowerBounds(subclass, ((WildcardType)this.runtimeType).getLowerBounds());
/*     */     }
/* 401 */     Preconditions.checkArgument(getRawType().isAssignableFrom(subclass), "%s isn't a subclass of %s", new Object[] { subclass, this });
/*     */ 
/* 404 */     if (isArray()) {
/* 405 */       return getArraySubtype(subclass);
/*     */     }
/*     */ 
/* 408 */     TypeToken subtype = of(resolveTypeArgsForSubclass(subclass));
/*     */ 
/* 410 */     return subtype;
/*     */   }
/*     */ 
/*     */   public final boolean isAssignableFrom(TypeToken<?> type)
/*     */   {
/* 415 */     return isAssignableFrom(type.runtimeType);
/*     */   }
/*     */ 
/*     */   public final boolean isAssignableFrom(Type type)
/*     */   {
/* 420 */     return isAssignable((Type)Preconditions.checkNotNull(type), this.runtimeType);
/*     */   }
/*     */ 
/*     */   public final boolean isArray()
/*     */   {
/* 428 */     return getComponentType() != null;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   public final TypeToken<?> getComponentType()
/*     */   {
/* 436 */     Type componentType = Types.getComponentType(this.runtimeType);
/* 437 */     if (componentType == null) {
/* 438 */       return null;
/*     */     }
/* 440 */     return of(componentType);
/*     */   }
/*     */ 
/*     */   private SortedSet<TypeToken<? super T>> findAllTypes()
/*     */   {
/* 543 */     Map map = Maps.newHashMap();
/* 544 */     collectTypes(map);
/* 545 */     return sortKeysByValue(map, Ordering.natural().reverse());
/*     */   }
/*     */ 
/*     */   private int collectTypes(Map<? super TypeToken<? super T>, Integer> map)
/*     */   {
/* 550 */     Integer existing = (Integer)map.get(this);
/* 551 */     if (existing != null)
/*     */     {
/* 553 */       return existing.intValue();
/*     */     }
/* 555 */     int aboveMe = getRawType().isInterface() ? 1 : 0;
/*     */ 
/* 558 */     for (TypeToken interfaceType : getGenericInterfaces()) {
/* 559 */       aboveMe = Math.max(aboveMe, interfaceType.collectTypes(map));
/*     */     }
/* 561 */     TypeToken superclass = getGenericSuperclass();
/* 562 */     if (superclass != null) {
/* 563 */       aboveMe = Math.max(aboveMe, superclass.collectTypes(map));
/*     */     }
/*     */ 
/* 567 */     map.put(this, Integer.valueOf(aboveMe + 1));
/* 568 */     return aboveMe + 1;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object o)
/*     */   {
/* 590 */     if ((o instanceof TypeToken)) {
/* 591 */       TypeToken that = (TypeToken)o;
/* 592 */       return this.runtimeType.equals(that.runtimeType);
/*     */     }
/* 594 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/* 598 */     return this.runtimeType.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString() {
/* 602 */     return Types.toString(this.runtimeType);
/*     */   }
/*     */ 
/*     */   protected Object writeReplace()
/*     */   {
/* 609 */     return of(new TypeResolver().resolve(this.runtimeType));
/*     */   }
/*     */ 
/*     */   private static boolean isAssignable(Type from, Type to) {
/* 613 */     if (to.equals(from)) {
/* 614 */       return true;
/*     */     }
/* 616 */     if ((to instanceof WildcardType)) {
/* 617 */       return isAssignableToWildcardType(from, (WildcardType)to);
/*     */     }
/*     */ 
/* 621 */     if ((from instanceof TypeVariable)) {
/* 622 */       return isAssignableFromAny(((TypeVariable)from).getBounds(), to);
/*     */     }
/*     */ 
/* 626 */     if ((from instanceof WildcardType)) {
/* 627 */       return isAssignableFromAny(((WildcardType)from).getUpperBounds(), to);
/*     */     }
/* 629 */     if ((from instanceof GenericArrayType)) {
/* 630 */       return isAssignableFromGenericArrayType((GenericArrayType)from, to);
/*     */     }
/*     */ 
/* 633 */     if ((to instanceof Class))
/* 634 */       return isAssignableToClass(from, (Class)to);
/* 635 */     if ((to instanceof ParameterizedType))
/* 636 */       return isAssignableToParameterizedType(from, (ParameterizedType)to);
/* 637 */     if ((to instanceof GenericArrayType)) {
/* 638 */       return isAssignableToGenericArrayType(from, (GenericArrayType)to);
/*     */     }
/* 640 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableFromAny(Type[] fromTypes, Type to)
/*     */   {
/* 645 */     for (Type from : fromTypes) {
/* 646 */       if (isAssignable(from, to)) {
/* 647 */         return true;
/*     */       }
/*     */     }
/* 650 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableToClass(Type from, Class<?> to) {
/* 654 */     return to.isAssignableFrom(getRawType(from));
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableToWildcardType(Type from, WildcardType to)
/*     */   {
/* 664 */     return (isAssignable(from, supertypeBound(to))) && (isAssignableBySubtypeBound(from, to));
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableBySubtypeBound(Type from, WildcardType to) {
/* 668 */     Type toSubtypeBound = subtypeBound(to);
/* 669 */     if (toSubtypeBound == null) {
/* 670 */       return true;
/*     */     }
/* 672 */     Type fromSubtypeBound = subtypeBound(from);
/* 673 */     if (fromSubtypeBound == null) {
/* 674 */       return false;
/*     */     }
/* 676 */     return isAssignable(toSubtypeBound, fromSubtypeBound);
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableToParameterizedType(Type from, ParameterizedType to) {
/* 680 */     Class matchedClass = getRawType(to);
/* 681 */     if (!matchedClass.isAssignableFrom(getRawType(from))) {
/* 682 */       return false;
/*     */     }
/* 684 */     Type[] typeParams = matchedClass.getTypeParameters();
/* 685 */     Type[] toTypeArgs = to.getActualTypeArguments();
/* 686 */     TypeToken fromTypeToken = of(from);
/* 687 */     for (int i = 0; i < typeParams.length; i++)
/*     */     {
/* 695 */       Type fromTypeArg = fromTypeToken.resolveType(typeParams[i]).runtimeType;
/* 696 */       if (!matchTypeArgument(fromTypeArg, toTypeArgs[i])) {
/* 697 */         return false;
/*     */       }
/*     */     }
/* 700 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableToGenericArrayType(Type from, GenericArrayType to) {
/* 704 */     if ((from instanceof Class)) {
/* 705 */       Class fromClass = (Class)from;
/* 706 */       if (!fromClass.isArray()) {
/* 707 */         return false;
/*     */       }
/* 709 */       return isAssignable(fromClass.getComponentType(), to.getGenericComponentType());
/* 710 */     }if ((from instanceof GenericArrayType)) {
/* 711 */       GenericArrayType fromArrayType = (GenericArrayType)from;
/* 712 */       return isAssignable(fromArrayType.getGenericComponentType(), to.getGenericComponentType());
/*     */     }
/* 714 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean isAssignableFromGenericArrayType(GenericArrayType from, Type to)
/*     */   {
/* 719 */     if ((to instanceof Class)) {
/* 720 */       Class toClass = (Class)to;
/* 721 */       if (!toClass.isArray()) {
/* 722 */         return toClass == Object.class;
/*     */       }
/* 724 */       return isAssignable(from.getGenericComponentType(), toClass.getComponentType());
/* 725 */     }if ((to instanceof GenericArrayType)) {
/* 726 */       GenericArrayType toArrayType = (GenericArrayType)to;
/* 727 */       return isAssignable(from.getGenericComponentType(), toArrayType.getGenericComponentType());
/*     */     }
/* 729 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean matchTypeArgument(Type from, Type to)
/*     */   {
/* 734 */     if (from.equals(to)) {
/* 735 */       return true;
/*     */     }
/* 737 */     if ((to instanceof WildcardType)) {
/* 738 */       return isAssignableToWildcardType(from, (WildcardType)to);
/*     */     }
/* 740 */     return false;
/*     */   }
/*     */ 
/*     */   private static Type supertypeBound(Type type) {
/* 744 */     if ((type instanceof WildcardType)) {
/* 745 */       return supertypeBound((WildcardType)type);
/*     */     }
/* 747 */     return type;
/*     */   }
/*     */ 
/*     */   private static Type supertypeBound(WildcardType type) {
/* 751 */     Type[] upperBounds = type.getUpperBounds();
/* 752 */     if (upperBounds.length == 1)
/* 753 */       return supertypeBound(upperBounds[0]);
/* 754 */     if (upperBounds.length == 0) {
/* 755 */       return Object.class;
/*     */     }
/* 757 */     throw new AssertionError("There should be at most one upper bound for wildcard type: " + type);
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private static Type subtypeBound(Type type)
/*     */   {
/* 763 */     if ((type instanceof WildcardType)) {
/* 764 */       return subtypeBound((WildcardType)type);
/*     */     }
/* 766 */     return type;
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private static Type subtypeBound(WildcardType type) {
/* 771 */     Type[] lowerBounds = type.getLowerBounds();
/* 772 */     if (lowerBounds.length == 1)
/* 773 */       return subtypeBound(lowerBounds[0]);
/* 774 */     if (lowerBounds.length == 0) {
/* 775 */       return null;
/*     */     }
/* 777 */     throw new AssertionError("Wildcard should have at most one lower bound: " + type);
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static Class<?> getRawType(Type type)
/*     */   {
/* 783 */     if ((type instanceof Class))
/* 784 */       return (Class)type;
/* 785 */     if ((type instanceof ParameterizedType)) {
/* 786 */       ParameterizedType parameterizedType = (ParameterizedType)type;
/*     */ 
/* 788 */       return (Class)parameterizedType.getRawType();
/* 789 */     }if ((type instanceof GenericArrayType)) {
/* 790 */       GenericArrayType genericArrayType = (GenericArrayType)type;
/* 791 */       return Types.getArrayClass(getRawType(genericArrayType.getGenericComponentType()));
/* 792 */     }if ((type instanceof TypeVariable))
/*     */     {
/* 794 */       return getRawType(((TypeVariable)type).getBounds()[0]);
/* 795 */     }if ((type instanceof WildcardType))
/*     */     {
/* 797 */       return getRawType(((WildcardType)type).getUpperBounds()[0]);
/*     */     }
/* 799 */     throw new AssertionError(type + " unsupported");
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static <T> TypeToken<? extends T> toGenericType(Class<T> cls)
/*     */   {
/* 811 */     if (cls.isArray()) {
/* 812 */       Type arrayOfGenericType = Types.newArrayType(toGenericType(cls.getComponentType()).runtimeType);
/*     */ 
/* 816 */       TypeToken result = of(arrayOfGenericType);
/* 817 */       return result;
/*     */     }
/* 819 */     TypeVariable[] typeParams = cls.getTypeParameters();
/* 820 */     if (typeParams.length > 0)
/*     */     {
/* 822 */       TypeToken type = of(Types.newParameterizedType(cls, typeParams));
/*     */ 
/* 824 */       return type;
/*     */     }
/* 826 */     return of(cls);
/*     */   }
/*     */ 
/*     */   private TypeToken<? super T> getSupertypeFromUpperBounds(Class<? super T> supertype, Type[] upperBounds)
/*     */   {
/* 832 */     for (Type upperBound : upperBounds)
/*     */     {
/* 834 */       TypeToken bound = of(upperBound);
/* 835 */       if (of(supertype).isAssignableFrom(bound))
/*     */       {
/* 837 */         TypeToken result = bound.getSupertype(supertype);
/* 838 */         return result;
/*     */       }
/*     */     }
/* 841 */     throw new IllegalArgumentException(supertype + " isn't a super type of " + this);
/*     */   }
/*     */ 
/*     */   private TypeToken<? extends T> getSubtypeFromLowerBounds(Class<?> subclass, Type[] lowerBounds) {
/* 845 */     Type[] arr$ = lowerBounds; int len$ = arr$.length; int i$ = 0; if (i$ < len$) { Type lowerBound = arr$[i$];
/*     */ 
/* 847 */       TypeToken bound = of(lowerBound);
/*     */ 
/* 849 */       return bound.getSubtype(subclass);
/*     */     }
/* 851 */     throw new IllegalArgumentException(subclass + " isn't a subclass of " + this);
/*     */   }
/*     */ 
/*     */   private TypeToken<? super T> getArraySupertype(Class<? super T> supertype)
/*     */   {
/* 858 */     TypeToken componentType = (TypeToken)Preconditions.checkNotNull(getComponentType(), "%s isn't a super type of %s", new Object[] { supertype, this });
/*     */ 
/* 862 */     TypeToken componentSupertype = componentType.getSupertype(supertype.getComponentType());
/*     */ 
/* 864 */     TypeToken result = of(newArrayClassOrGenericArrayType(componentSupertype.runtimeType));
/*     */ 
/* 867 */     return result;
/*     */   }
/*     */ 
/*     */   private TypeToken<? extends T> getArraySubtype(Class<?> subclass)
/*     */   {
/* 872 */     TypeToken componentSubtype = getComponentType().getSubtype(subclass.getComponentType());
/*     */ 
/* 875 */     TypeToken result = of(newArrayClassOrGenericArrayType(componentSubtype.runtimeType));
/*     */ 
/* 878 */     return result;
/*     */   }
/*     */ 
/*     */   private Type resolveTypeArgsForSubclass(Class<?> subclass) {
/* 882 */     if ((this.runtimeType instanceof Class))
/*     */     {
/* 884 */       return subclass;
/*     */     }
/*     */ 
/* 893 */     TypeToken genericSubtype = toGenericType(subclass);
/*     */ 
/* 895 */     Type supertypeWithArgsFromSubtype = genericSubtype.getSupertype(getRawType()).runtimeType;
/*     */ 
/* 898 */     return new TypeResolver().where(supertypeWithArgsFromSubtype, this.runtimeType).resolve(genericSubtype.runtimeType);
/*     */   }
/*     */ 
/*     */   private static Type newArrayClassOrGenericArrayType(Type componentType)
/*     */   {
/* 908 */     return Types.JavaVersion.JAVA7.newArrayType(componentType);
/*     */   }
/*     */ 
/*     */   private static <K, V> ImmutableSortedSet<K> sortKeysByValue(final Map<K, V> map, Comparator<? super V> valueComparator)
/*     */   {
/* 913 */     Comparator keyComparator = new Comparator() {
/*     */       public int compare(K left, K right) {
/* 915 */         return this.val$valueComparator.compare(map.get(left), map.get(right));
/*     */       }
/*     */     };
/* 918 */     return ImmutableSortedSet.copyOf(keyComparator, map.keySet());
/*     */   }
/*     */   private static final class SimpleTypeToken<T> extends TypeToken<T> {
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     SimpleTypeToken(Type type) {
/* 924 */       super(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract enum TypeFilter
/*     */     implements Predicate<TypeToken<?>>
/*     */   {
/* 573 */     IGNORE_TYPE_VARIABLE_OR_WILDCARD, 
/*     */ 
/* 579 */     INTERFACE_ONLY;
/*     */   }
/*     */ 
/*     */   private final class ClassSet extends TypeToken.TypeSet
/*     */   {
/* 514 */     private final transient ImmutableSet<TypeToken<? super T>> classes = ImmutableSet.copyOf(Iterators.filter(new AbstractSequentialIterator(TypeToken.this.getRawType().isInterface() ? null : TypeToken.this)
/*     */     {
/*     */       protected TypeToken<? super T> computeNext(TypeToken<? super T> previous)
/*     */       {
/* 518 */         return previous.getGenericSuperclass();
/*     */       }
/*     */     }
/*     */     , TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD));
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private ClassSet()
/*     */     {
/* 512 */       super();
/*     */     }
/*     */ 
/*     */     protected Set<TypeToken<? super T>> delegate()
/*     */     {
/* 523 */       return this.classes;
/*     */     }
/*     */ 
/*     */     public TypeToken<T>.TypeSet classes() {
/* 527 */       return this;
/*     */     }
/*     */ 
/*     */     public TypeToken<T>.TypeSet interfaces() {
/* 531 */       throw new UnsupportedOperationException("classes().interfaces() not supported.");
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/* 535 */       return TypeToken.this.getTypes().classes();
/*     */     }
/*     */   }
/*     */ 
/*     */   private final class InterfaceSet extends TypeToken.TypeSet
/*     */   {
/*     */     private final transient ImmutableSet<TypeToken<? super T>> interfaces;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     InterfaceSet()
/*     */     {
/* 489 */       super();
/* 490 */       this.interfaces = ImmutableSet.copyOf(Iterables.filter(allTypes, TypeToken.TypeFilter.INTERFACE_ONLY));
/*     */     }
/*     */ 
/*     */     protected Set<TypeToken<? super T>> delegate() {
/* 494 */       return this.interfaces;
/*     */     }
/*     */ 
/*     */     public TypeToken<T>.TypeSet interfaces() {
/* 498 */       return this;
/*     */     }
/*     */ 
/*     */     public TypeToken<T>.TypeSet classes() {
/* 502 */       throw new UnsupportedOperationException("interfaces().classes() not supported.");
/*     */     }
/*     */ 
/*     */     private Object readResolve() {
/* 506 */       return TypeToken.this.getTypes().interfaces();
/*     */     }
/*     */   }
/*     */ 
/*     */   public class TypeSet extends ForwardingSet<TypeToken<? super T>>
/*     */     implements Serializable
/*     */   {
/*     */     private transient ImmutableSet<TypeToken<? super T>> types;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     TypeSet()
/*     */     {
/*     */     }
/*     */ 
/*     */     public TypeToken<T>.TypeSet interfaces()
/*     */     {
/* 455 */       return new TypeToken.InterfaceSet(TypeToken.this, this);
/*     */     }
/*     */ 
/*     */     public TypeToken<T>.TypeSet classes()
/*     */     {
/* 460 */       return new TypeToken.ClassSet(TypeToken.this, null);
/*     */     }
/*     */ 
/*     */     protected Set<TypeToken<? super T>> delegate() {
/* 464 */       ImmutableSet filteredTypes = this.types;
/* 465 */       if (filteredTypes == null) {
/* 466 */         return this.types = ImmutableSet.copyOf(Sets.filter(TypeToken.this.findAllTypes(), TypeToken.TypeFilter.IGNORE_TYPE_VARIABLE_OR_WILDCARD));
/*     */       }
/*     */ 
/* 469 */       return filteredTypes;
/*     */     }
/*     */ 
/*     */     public final Set<Class<? super T>> rawTypes()
/*     */     {
/* 475 */       ImmutableSet.Builder builder = ImmutableSet.builder();
/* 476 */       for (TypeToken type : this) {
/* 477 */         builder.add(type.getRawType());
/*     */       }
/* 479 */       return builder.build();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.reflect.TypeToken
 * JD-Core Version:    0.6.2
 */