/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Equivalences;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ 
/*     */ @Beta
/*     */ public final class Interners
/*     */ {
/*     */   public static <E> Interner<E> newStrongInterner()
/*     */   {
/*  45 */     ConcurrentMap map = new MapMaker().makeMap();
/*  46 */     return new Object() {
/*     */       public E intern(E sample) {
/*  48 */         Object canonical = this.val$map.putIfAbsent(Preconditions.checkNotNull(sample), sample);
/*  49 */         return canonical == null ? sample : canonical;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public static <E> Interner<E> newWeakInterner()
/*     */   {
/*  63 */     return new WeakInterner(null);
/*     */   }
/*     */ 
/*     */   public static <E> Function<E, E> asFunction(Interner<E> interner)
/*     */   {
/* 108 */     return new InternerFunction((Interner)Preconditions.checkNotNull(interner));
/*     */   }
/*     */ 
/*     */   private static class InternerFunction<E> implements Function<E, E>
/*     */   {
/*     */     private final Interner<E> interner;
/*     */ 
/*     */     public InternerFunction(Interner<E> interner) {
/* 116 */       this.interner = interner;
/*     */     }
/*     */ 
/*     */     public E apply(E input) {
/* 120 */       return this.interner.intern(input);
/*     */     }
/*     */ 
/*     */     public int hashCode() {
/* 124 */       return this.interner.hashCode();
/*     */     }
/*     */ 
/*     */     public boolean equals(Object other) {
/* 128 */       if ((other instanceof InternerFunction)) {
/* 129 */         InternerFunction that = (InternerFunction)other;
/* 130 */         return this.interner.equals(that.interner);
/*     */       }
/*     */ 
/* 133 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class WeakInterner<E>
/*     */     implements Interner<E>
/*     */   {
/*     */     private final MapMakerInternalMap<E, Dummy> map;
/*     */ 
/*     */     private WeakInterner()
/*     */     {
/*  68 */       this.map = new MapMaker().weakKeys().keyEquivalence(Equivalences.equals()).makeCustomMap();
/*     */     }
/*     */ 
/*     */     public E intern(E sample)
/*     */     {
/*     */       while (true)
/*     */       {
/*  76 */         MapMakerInternalMap.ReferenceEntry entry = this.map.getEntry(sample);
/*  77 */         if (entry != null) {
/*  78 */           Object canonical = entry.getKey();
/*  79 */           if (canonical != null) {
/*  80 */             return canonical;
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*  85 */         Dummy sneaky = (Dummy)this.map.putIfAbsent(sample, Dummy.VALUE);
/*  86 */         if (sneaky == null)
/*  87 */           return sample;
/*     */       }
/*     */     }
/*     */ 
/*     */     private static enum Dummy
/*     */     {
/*  99 */       VALUE;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Interners
 * JD-Core Version:    0.6.2
 */