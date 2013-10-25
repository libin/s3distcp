/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collection;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class Predicates
/*     */ {
/* 330 */   private static final Joiner COMMA_JOINER = Joiner.on(",");
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> alwaysTrue()
/*     */   {
/*  59 */     return ObjectPredicate.ALWAYS_TRUE.withNarrowedType();
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> alwaysFalse()
/*     */   {
/*  67 */     return ObjectPredicate.ALWAYS_FALSE.withNarrowedType();
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> isNull()
/*     */   {
/*  76 */     return ObjectPredicate.IS_NULL.withNarrowedType();
/*     */   }
/*     */ 
/*     */   @GwtCompatible(serializable=true)
/*     */   public static <T> Predicate<T> notNull()
/*     */   {
/*  85 */     return ObjectPredicate.NOT_NULL.withNarrowedType();
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> not(Predicate<T> predicate)
/*     */   {
/*  93 */     return new NotPredicate(predicate);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components)
/*     */   {
/* 107 */     return new AndPredicate(defensiveCopy(components), null);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> and(Predicate<? super T>[] components)
/*     */   {
/* 120 */     return new AndPredicate(defensiveCopy(components), null);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> and(Predicate<? super T> first, Predicate<? super T> second)
/*     */   {
/* 131 */     return new AndPredicate(asList((Predicate)Preconditions.checkNotNull(first), (Predicate)Preconditions.checkNotNull(second)), null);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components)
/*     */   {
/* 146 */     return new OrPredicate(defensiveCopy(components), null);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> or(Predicate<? super T>[] components)
/*     */   {
/* 159 */     return new OrPredicate(defensiveCopy(components), null);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> or(Predicate<? super T> first, Predicate<? super T> second)
/*     */   {
/* 170 */     return new OrPredicate(asList((Predicate)Preconditions.checkNotNull(first), (Predicate)Preconditions.checkNotNull(second)), null);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> equalTo(@Nullable T target)
/*     */   {
/* 179 */     return target == null ? isNull() : new IsEqualToPredicate(target, null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Class.isInstance")
/*     */   public static Predicate<Object> instanceOf(Class<?> clazz)
/*     */   {
/* 201 */     return new InstanceOfPredicate(clazz, null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Class.isAssignableFrom")
/*     */   @Beta
/*     */   public static Predicate<Class<?>> assignableFrom(Class<?> clazz)
/*     */   {
/* 214 */     return new AssignableFromPredicate(clazz, null);
/*     */   }
/*     */ 
/*     */   public static <T> Predicate<T> in(Collection<? extends T> target)
/*     */   {
/* 231 */     return new InPredicate(target, null);
/*     */   }
/*     */ 
/*     */   public static <A, B> Predicate<A> compose(Predicate<B> predicate, Function<A, ? extends B> function)
/*     */   {
/* 242 */     return new CompositionPredicate(predicate, function, null);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.util.regex.Pattern")
/*     */   public static Predicate<CharSequence> containsPattern(String pattern)
/*     */   {
/* 256 */     return new ContainsPatternPredicate(pattern);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.util.regex.Pattern")
/*     */   public static Predicate<CharSequence> contains(Pattern pattern)
/*     */   {
/* 269 */     return new ContainsPatternPredicate(pattern);
/*     */   }
/*     */ 
/*     */   private static <T> List<Predicate<? super T>> asList(Predicate<? super T> first, Predicate<? super T> second)
/*     */   {
/* 610 */     return Arrays.asList(new Predicate[] { first, second });
/*     */   }
/*     */ 
/*     */   private static <T> List<T> defensiveCopy(T[] array) {
/* 614 */     return defensiveCopy(Arrays.asList(array));
/*     */   }
/*     */ 
/*     */   static <T> List<T> defensiveCopy(Iterable<T> iterable) {
/* 618 */     ArrayList list = new ArrayList();
/* 619 */     for (Iterator i$ = iterable.iterator(); i$.hasNext(); ) { Object element = i$.next();
/* 620 */       list.add(Preconditions.checkNotNull(element));
/*     */     }
/* 622 */     return list;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Only used by other GWT-incompatible code.")
/*     */   private static class ContainsPatternPredicate
/*     */     implements Predicate<CharSequence>, Serializable
/*     */   {
/*     */     final Pattern pattern;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     ContainsPatternPredicate(Pattern pattern)
/*     */     {
/* 566 */       this.pattern = ((Pattern)Preconditions.checkNotNull(pattern));
/*     */     }
/*     */ 
/*     */     ContainsPatternPredicate(String patternStr) {
/* 570 */       this(Pattern.compile(patternStr));
/*     */     }
/*     */ 
/*     */     public boolean apply(CharSequence t)
/*     */     {
/* 575 */       return this.pattern.matcher(t).find();
/*     */     }
/*     */ 
/*     */     public int hashCode()
/*     */     {
/* 582 */       return Objects.hashCode(new Object[] { this.pattern.pattern(), Integer.valueOf(this.pattern.flags()) });
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 586 */       if ((obj instanceof ContainsPatternPredicate)) {
/* 587 */         ContainsPatternPredicate that = (ContainsPatternPredicate)obj;
/*     */ 
/* 591 */         return (Objects.equal(this.pattern.pattern(), that.pattern.pattern())) && (Objects.equal(Integer.valueOf(this.pattern.flags()), Integer.valueOf(that.pattern.flags())));
/*     */       }
/*     */ 
/* 594 */       return false;
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 598 */       return Objects.toStringHelper(this).add("pattern", this.pattern).add("pattern.flags", Integer.toHexString(this.pattern.flags())).toString();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class CompositionPredicate<A, B>
/*     */     implements Predicate<A>, Serializable
/*     */   {
/*     */     final Predicate<B> p;
/*     */     final Function<A, ? extends B> f;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private CompositionPredicate(Predicate<B> p, Function<A, ? extends B> f)
/*     */     {
/* 528 */       this.p = ((Predicate)Preconditions.checkNotNull(p));
/* 529 */       this.f = ((Function)Preconditions.checkNotNull(f));
/*     */     }
/*     */ 
/*     */     public boolean apply(A a)
/*     */     {
/* 534 */       return this.p.apply(this.f.apply(a));
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj) {
/* 538 */       if ((obj instanceof CompositionPredicate)) {
/* 539 */         CompositionPredicate that = (CompositionPredicate)obj;
/* 540 */         return (this.f.equals(that.f)) && (this.p.equals(that.p));
/*     */       }
/* 542 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 546 */       return this.f.hashCode() ^ this.p.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 550 */       return this.p.toString() + "(" + this.f.toString() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class InPredicate<T>
/*     */     implements Predicate<T>, Serializable
/*     */   {
/*     */     private final Collection<?> target;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private InPredicate(Collection<?> target)
/*     */     {
/* 489 */       this.target = ((Collection)Preconditions.checkNotNull(target));
/*     */     }
/*     */ 
/*     */     public boolean apply(T t)
/*     */     {
/*     */       try {
/* 495 */         return this.target.contains(t);
/*     */       } catch (NullPointerException e) {
/* 497 */         return false; } catch (ClassCastException e) {
/*     */       }
/* 499 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean equals(@Nullable Object obj)
/*     */     {
/* 504 */       if ((obj instanceof InPredicate)) {
/* 505 */         InPredicate that = (InPredicate)obj;
/* 506 */         return this.target.equals(that.target);
/*     */       }
/* 508 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 512 */       return this.target.hashCode();
/*     */     }
/*     */ 
/*     */     public String toString() {
/* 516 */       return "In(" + this.target + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Class.isAssignableFrom")
/*     */   private static class AssignableFromPredicate
/*     */     implements Predicate<Class<?>>, Serializable
/*     */   {
/*     */     private final Class<?> clazz;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private AssignableFromPredicate(Class<?> clazz)
/*     */     {
/* 462 */       this.clazz = ((Class)Preconditions.checkNotNull(clazz));
/*     */     }
/*     */ 
/*     */     public boolean apply(Class<?> input) {
/* 466 */       return this.clazz.isAssignableFrom(input);
/*     */     }
/*     */     public int hashCode() {
/* 469 */       return this.clazz.hashCode();
/*     */     }
/*     */     public boolean equals(@Nullable Object obj) {
/* 472 */       if ((obj instanceof AssignableFromPredicate)) {
/* 473 */         AssignableFromPredicate that = (AssignableFromPredicate)obj;
/* 474 */         return this.clazz == that.clazz;
/*     */       }
/* 476 */       return false;
/*     */     }
/*     */     public String toString() {
/* 479 */       return "IsAssignableFrom(" + this.clazz.getName() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("Class.isInstance")
/*     */   private static class InstanceOfPredicate
/*     */     implements Predicate<Object>, Serializable
/*     */   {
/*     */     private final Class<?> clazz;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private InstanceOfPredicate(Class<?> clazz)
/*     */     {
/* 433 */       this.clazz = ((Class)Preconditions.checkNotNull(clazz));
/*     */     }
/*     */ 
/*     */     public boolean apply(@Nullable Object o) {
/* 437 */       return this.clazz.isInstance(o);
/*     */     }
/*     */     public int hashCode() {
/* 440 */       return this.clazz.hashCode();
/*     */     }
/*     */     public boolean equals(@Nullable Object obj) {
/* 443 */       if ((obj instanceof InstanceOfPredicate)) {
/* 444 */         InstanceOfPredicate that = (InstanceOfPredicate)obj;
/* 445 */         return this.clazz == that.clazz;
/*     */       }
/* 447 */       return false;
/*     */     }
/*     */     public String toString() {
/* 450 */       return "IsInstanceOf(" + this.clazz.getName() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class IsEqualToPredicate<T>
/*     */     implements Predicate<T>, Serializable
/*     */   {
/*     */     private final T target;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private IsEqualToPredicate(T target)
/*     */     {
/* 404 */       this.target = target;
/*     */     }
/*     */ 
/*     */     public boolean apply(T t) {
/* 408 */       return this.target.equals(t);
/*     */     }
/*     */     public int hashCode() {
/* 411 */       return this.target.hashCode();
/*     */     }
/*     */     public boolean equals(@Nullable Object obj) {
/* 414 */       if ((obj instanceof IsEqualToPredicate)) {
/* 415 */         IsEqualToPredicate that = (IsEqualToPredicate)obj;
/* 416 */         return this.target.equals(that.target);
/*     */       }
/* 418 */       return false;
/*     */     }
/*     */     public String toString() {
/* 421 */       return "IsEqualTo(" + this.target + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class OrPredicate<T>
/*     */     implements Predicate<T>, Serializable
/*     */   {
/*     */     private final List<? extends Predicate<? super T>> components;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private OrPredicate(List<? extends Predicate<? super T>> components)
/*     */     {
/* 370 */       this.components = components;
/*     */     }
/*     */ 
/*     */     public boolean apply(T t) {
/* 374 */       for (int i = 0; i < this.components.size(); i++) {
/* 375 */         if (((Predicate)this.components.get(i)).apply(t)) {
/* 376 */           return true;
/*     */         }
/*     */       }
/* 379 */       return false;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 383 */       return this.components.hashCode() + 87855567;
/*     */     }
/*     */     public boolean equals(@Nullable Object obj) {
/* 386 */       if ((obj instanceof OrPredicate)) {
/* 387 */         OrPredicate that = (OrPredicate)obj;
/* 388 */         return this.components.equals(that.components);
/*     */       }
/* 390 */       return false;
/*     */     }
/*     */     public String toString() {
/* 393 */       return "Or(" + Predicates.COMMA_JOINER.join(this.components) + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class AndPredicate<T>
/*     */     implements Predicate<T>, Serializable
/*     */   {
/*     */     private final List<? extends Predicate<? super T>> components;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     private AndPredicate(List<? extends Predicate<? super T>> components)
/*     */     {
/* 337 */       this.components = components;
/*     */     }
/*     */ 
/*     */     public boolean apply(T t) {
/* 341 */       for (int i = 0; i < this.components.size(); i++) {
/* 342 */         if (!((Predicate)this.components.get(i)).apply(t)) {
/* 343 */           return false;
/*     */         }
/*     */       }
/* 346 */       return true;
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 350 */       return this.components.hashCode() + 306654252;
/*     */     }
/*     */     public boolean equals(@Nullable Object obj) {
/* 353 */       if ((obj instanceof AndPredicate)) {
/* 354 */         AndPredicate that = (AndPredicate)obj;
/* 355 */         return this.components.equals(that.components);
/*     */       }
/* 357 */       return false;
/*     */     }
/*     */     public String toString() {
/* 360 */       return "And(" + Predicates.COMMA_JOINER.join(this.components) + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class NotPredicate<T>
/*     */     implements Predicate<T>, Serializable
/*     */   {
/*     */     final Predicate<T> predicate;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     NotPredicate(Predicate<T> predicate)
/*     */     {
/* 308 */       this.predicate = ((Predicate)Preconditions.checkNotNull(predicate));
/*     */     }
/*     */ 
/*     */     public boolean apply(T t) {
/* 312 */       return !this.predicate.apply(t);
/*     */     }
/*     */     public int hashCode() {
/* 315 */       return this.predicate.hashCode() ^ 0xFFFFFFFF;
/*     */     }
/*     */     public boolean equals(@Nullable Object obj) {
/* 318 */       if ((obj instanceof NotPredicate)) {
/* 319 */         NotPredicate that = (NotPredicate)obj;
/* 320 */         return this.predicate.equals(that.predicate);
/*     */       }
/* 322 */       return false;
/*     */     }
/*     */     public String toString() {
/* 325 */       return "Not(" + this.predicate.toString() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract enum ObjectPredicate
/*     */     implements Predicate<Object>
/*     */   {
/* 276 */     ALWAYS_TRUE, 
/*     */ 
/* 281 */     ALWAYS_FALSE, 
/*     */ 
/* 286 */     IS_NULL, 
/*     */ 
/* 291 */     NOT_NULL;
/*     */ 
/*     */     <T> Predicate<T> withNarrowedType()
/*     */     {
/* 299 */       return this;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Predicates
 * JD-Core Version:    0.6.2
 */