/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Equivalence;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public abstract class GenericMapMaker<K0, V0>
/*     */ {
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker.RemovalListener<K0, V0> removalListener;
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   abstract GenericMapMaker<K0, V0> keyEquivalence(Equivalence<Object> paramEquivalence);
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   abstract GenericMapMaker<K0, V0> valueEquivalence(Equivalence<Object> paramEquivalence);
/*     */ 
/*     */   public abstract GenericMapMaker<K0, V0> initialCapacity(int paramInt);
/*     */ 
/*     */   abstract GenericMapMaker<K0, V0> maximumSize(int paramInt);
/*     */ 
/*     */   abstract GenericMapMaker<K0, V0> strongKeys();
/*     */ 
/*     */   public abstract GenericMapMaker<K0, V0> concurrencyLevel(int paramInt);
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public abstract GenericMapMaker<K0, V0> weakKeys();
/*     */ 
/*     */   abstract GenericMapMaker<K0, V0> strongValues();
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public abstract GenericMapMaker<K0, V0> softKeys();
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public abstract GenericMapMaker<K0, V0> weakValues();
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public abstract GenericMapMaker<K0, V0> softValues();
/*     */ 
/*     */   @Deprecated
/*     */   public abstract GenericMapMaker<K0, V0> expiration(long paramLong, TimeUnit paramTimeUnit);
/*     */ 
/*     */   abstract GenericMapMaker<K0, V0> expireAfterWrite(long paramLong, TimeUnit paramTimeUnit);
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   abstract GenericMapMaker<K0, V0> expireAfterAccess(long paramLong, TimeUnit paramTimeUnit);
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   <K extends K0, V extends V0> MapMaker.RemovalListener<K, V> getRemovalListener()
/*     */   {
/* 146 */     return (MapMaker.RemovalListener)Objects.firstNonNull(this.removalListener, NullListener.INSTANCE);
/*     */   }
/*     */ 
/*     */   public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeMap();
/*     */ 
/*     */   @GwtIncompatible("MapMakerInternalMap")
/*     */   abstract <K, V> MapMakerInternalMap<K, V> makeCustomMap();
/*     */ 
/*     */   @Deprecated
/*     */   public abstract <K extends K0, V extends V0> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> paramFunction);
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   static enum NullListener
/*     */     implements MapMaker.RemovalListener<Object, Object>
/*     */   {
/*  45 */     INSTANCE;
/*     */ 
/*     */     public void onRemoval(MapMaker.RemovalNotification<Object, Object> notification)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.GenericMapMaker
 * JD-Core Version:    0.6.2
 */