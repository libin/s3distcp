/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public final class Functions
/*     */ {
/*     */   public static Function<Object, String> toStringFunction()
/*     */   {
/*  56 */     return ToStringFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <E> Function<E, E> identity()
/*     */   {
/*  79 */     return IdentityFunction.INSTANCE;
/*     */   }
/*     */ 
/*     */   public static <K, V> Function<K, V> forMap(Map<K, V> map)
/*     */   {
/* 101 */     return new FunctionForMapNoDefault(map);
/*     */   }
/*     */ 
/*     */   public static <K, V> Function<K, V> forMap(Map<K, ? extends V> map, @Nullable V defaultValue)
/*     */   {
/* 148 */     return new ForMapWithDefault(map, defaultValue);
/*     */   }
/*     */ 
/*     */   public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, ? extends B> f)
/*     */   {
/* 195 */     return new FunctionComposition(g, f);
/*     */   }
/*     */ 
/*     */   public static <T> Function<T, Boolean> forPredicate(Predicate<T> predicate)
/*     */   {
/* 238 */     return new PredicateFunction(predicate, null);
/*     */   }
/*     */ 
/*     */   public static <E> Function<Object, E> constant(@Nullable E value)
/*     */   {
/* 280 */     return new ConstantFunction(value);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static <T> Function<Object, T> forSupplier(Supplier<T> supplier)
/*     */   {
/* 322 */     return new SupplierFunction(supplier, null);
/*     */   }
/*     */ 
/*     */   private static class SupplierFunction<T> implements Function<Object, T>, Serializable {
/*     */     private final Supplier<T> supplier;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private SupplierFunction(Supplier<T> supplier) {
/* 331 */       this.supplier = ((Supplier)Preconditions.checkNotNull(supplier));
/*     */     }
/*     */ 
/*     */     public T apply(@Nullable Object input) {
/* 335 */       return this.supplier.get();
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 339 */       if ((obj instanceof SupplierFunction)) {
/* 340 */         SupplierFunction that = (SupplierFunction)obj;
/* 341 */         return this.supplier.equals(that.supplier);
/*     */       }
/* 343 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 347 */       return this.supplier.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 351 */       return "forSupplier(" + this.supplier + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ConstantFunction<E>
/*     */     implements Function<Object, E>, Serializable
/*     */   {
/*     */     private final E value;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public ConstantFunction(@Nullable E value)
/*     */     {
/* 287 */       this.value = value;
/*     */     }
/*     */ 
/*     */     public E apply(@Nullable Object from)
/*     */     {
/* 292 */       return this.value;
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 296 */       if ((obj instanceof ConstantFunction)) {
/* 297 */         ConstantFunction that = (ConstantFunction)obj;
/* 298 */         return Objects.equal(this.value, that.value);
/*     */       }
/* 300 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 304 */       return this.value == null ? 0 : this.value.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 308 */       return "constant(" + this.value + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class PredicateFunction<T>
/*     */     implements Function<T, Boolean>, Serializable
/*     */   {
/*     */     private final Predicate<T> predicate;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private PredicateFunction(Predicate<T> predicate)
/*     */     {
/* 246 */       this.predicate = ((Predicate)Preconditions.checkNotNull(predicate));
/*     */     }
/*     */ 
/*     */     public Boolean apply(T t)
/*     */     {
/* 251 */       return Boolean.valueOf(this.predicate.apply(t));
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 255 */       if ((obj instanceof PredicateFunction)) {
/* 256 */         PredicateFunction that = (PredicateFunction)obj;
/* 257 */         return this.predicate.equals(that.predicate);
/*     */       }
/* 259 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 263 */       return this.predicate.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 267 */       return "forPredicate(" + this.predicate + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FunctionComposition<A, B, C>
/*     */     implements Function<A, C>, Serializable
/*     */   {
/*     */     private final Function<B, C> g;
/*     */     private final Function<A, ? extends B> f;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     public FunctionComposition(Function<B, C> g, Function<A, ? extends B> f)
/*     */     {
/* 203 */       this.g = ((Function)Preconditions.checkNotNull(g));
/* 204 */       this.f = ((Function)Preconditions.checkNotNull(f));
/*     */     }
/*     */ 
/*     */     public C apply(A a)
/*     */     {
/* 209 */       return this.g.apply(this.f.apply(a));
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 213 */       if ((obj instanceof FunctionComposition)) {
/* 214 */         FunctionComposition that = (FunctionComposition)obj;
/* 215 */         return (this.f.equals(that.f)) && (this.g.equals(that.g));
/*     */       }
/* 217 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 221 */       return this.f.hashCode() ^ this.g.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 225 */       return this.g.toString() + "(" + this.f.toString() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ForMapWithDefault<K, V>
/*     */     implements Function<K, V>, Serializable
/*     */   {
/*     */     final Map<K, ? extends V> map;
/*     */     final V defaultValue;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     ForMapWithDefault(Map<K, ? extends V> map, @Nullable V defaultValue)
/*     */     {
/* 156 */       this.map = ((Map)Preconditions.checkNotNull(map));
/* 157 */       this.defaultValue = defaultValue;
/*     */     }
/*     */ 
/*     */     public V apply(K key)
/*     */     {
/* 162 */       Object result = this.map.get(key);
/* 163 */       return (result != null) || (this.map.containsKey(key)) ? result : this.defaultValue;
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object o) {
/* 167 */       if ((o instanceof ForMapWithDefault)) {
/* 168 */         ForMapWithDefault that = (ForMapWithDefault)o;
/* 169 */         return (this.map.equals(that.map)) && (Objects.equal(this.defaultValue, that.defaultValue));
/*     */       }
/* 171 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 175 */       return Objects.hashCode(new Object[] { this.map, this.defaultValue });
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 179 */       return "forMap(" + this.map + ", defaultValue=" + this.defaultValue + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class FunctionForMapNoDefault<K, V>
/*     */     implements Function<K, V>, Serializable
/*     */   {
/*     */     final Map<K, V> map;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     FunctionForMapNoDefault(Map<K, V> map)
/*     */     {
/* 108 */       this.map = ((Map)Preconditions.checkNotNull(map));
/*     */     }
/*     */ 
/*     */     public V apply(K key)
/*     */     {
/* 113 */       Object result = this.map.get(key);
/* 114 */       Preconditions.checkArgument((result != null) || (this.map.containsKey(key)), "Key '%s' not present in map", new Object[] { key });
/* 115 */       return result;
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object o) {
/* 119 */       if ((o instanceof FunctionForMapNoDefault)) {
/* 120 */         FunctionForMapNoDefault that = (FunctionForMapNoDefault)o;
/* 121 */         return this.map.equals(that.map);
/*     */       }
/* 123 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 127 */       return this.map.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 131 */       return "forMap(" + this.map + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum IdentityFunction
/*     */     implements Function<Object, Object>
/*     */   {
/*  84 */     INSTANCE;
/*     */ 
/*     */     public Object apply(Object o)
/*     */     {
/*  88 */       return o;
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  92 */       return "identity";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static enum ToStringFunction
/*     */     implements Function<Object, String>
/*     */   {
/*  61 */     INSTANCE;
/*     */ 
/*     */     public String apply(Object o)
/*     */     {
/*  65 */       Preconditions.checkNotNull(o);
/*  66 */       return o.toString();
/*     */     }
/*     */ 
/*     */     public String toString() {
/*  70 */       return "toString";
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Functions
 * JD-Core Version:    0.6.2
 */