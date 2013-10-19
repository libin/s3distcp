/*     */ package com.google.common.collect;
/*     */ 
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Ascii;
/*     */ import com.google.common.base.Equivalence;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Ticker;
/*     */ import java.io.Serializable;
/*     */ import java.util.AbstractMap;
/*     */ import java.util.Collections;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class MapMaker extends GenericMapMaker<Object, Object>
/*     */ {
/*     */   private static final int DEFAULT_INITIAL_CAPACITY = 16;
/*     */   private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
/*     */   private static final int DEFAULT_EXPIRATION_NANOS = 0;
/*     */   static final int UNSET_INT = -1;
/*     */   boolean useCustomMap;
/* 123 */   int initialCapacity = -1;
/* 124 */   int concurrencyLevel = -1;
/* 125 */   int maximumSize = -1;
/*     */   MapMakerInternalMap.Strength keyStrength;
/*     */   MapMakerInternalMap.Strength valueStrength;
/* 130 */   long expireAfterWriteNanos = -1L;
/* 131 */   long expireAfterAccessNanos = -1L;
/*     */   RemovalCause nullRemovalCause;
/*     */   Equivalence<Object> keyEquivalence;
/*     */   Equivalence<Object> valueEquivalence;
/*     */   Ticker ticker;
/*     */ 
/*     */   private boolean useNullMap()
/*     */   {
/* 147 */     return this.nullRemovalCause == null;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker keyEquivalence(Equivalence<Object> equivalence)
/*     */   {
/* 160 */     Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", new Object[] { this.keyEquivalence });
/* 161 */     this.keyEquivalence = ((Equivalence)Preconditions.checkNotNull(equivalence));
/* 162 */     this.useCustomMap = true;
/* 163 */     return this;
/*     */   }
/*     */ 
/*     */   Equivalence<Object> getKeyEquivalence() {
/* 167 */     return (Equivalence)Objects.firstNonNull(this.keyEquivalence, getKeyStrength().defaultEquivalence());
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker valueEquivalence(Equivalence<Object> equivalence)
/*     */   {
/* 180 */     Preconditions.checkState(this.valueEquivalence == null, "value equivalence was already set to %s", new Object[] { this.valueEquivalence });
/*     */ 
/* 182 */     this.valueEquivalence = ((Equivalence)Preconditions.checkNotNull(equivalence));
/* 183 */     this.useCustomMap = true;
/* 184 */     return this;
/*     */   }
/*     */ 
/*     */   Equivalence<Object> getValueEquivalence() {
/* 188 */     return (Equivalence)Objects.firstNonNull(this.valueEquivalence, getValueStrength().defaultEquivalence());
/*     */   }
/*     */ 
/*     */   public MapMaker initialCapacity(int initialCapacity)
/*     */   {
/* 203 */     Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", new Object[] { Integer.valueOf(this.initialCapacity) });
/*     */ 
/* 205 */     Preconditions.checkArgument(initialCapacity >= 0);
/* 206 */     this.initialCapacity = initialCapacity;
/* 207 */     return this;
/*     */   }
/*     */ 
/*     */   int getInitialCapacity() {
/* 211 */     return this.initialCapacity == -1 ? 16 : this.initialCapacity;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   MapMaker maximumSize(int size)
/*     */   {
/* 240 */     Preconditions.checkState(this.maximumSize == -1, "maximum size was already set to %s", new Object[] { Integer.valueOf(this.maximumSize) });
/*     */ 
/* 242 */     Preconditions.checkArgument(size >= 0, "maximum size must not be negative");
/* 243 */     this.maximumSize = size;
/* 244 */     this.useCustomMap = true;
/* 245 */     if (this.maximumSize == 0)
/*     */     {
/* 247 */       this.nullRemovalCause = RemovalCause.SIZE;
/*     */     }
/* 249 */     return this;
/*     */   }
/*     */ 
/*     */   public MapMaker concurrencyLevel(int concurrencyLevel)
/*     */   {
/* 273 */     Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", new Object[] { Integer.valueOf(this.concurrencyLevel) });
/*     */ 
/* 275 */     Preconditions.checkArgument(concurrencyLevel > 0);
/* 276 */     this.concurrencyLevel = concurrencyLevel;
/* 277 */     return this;
/*     */   }
/*     */ 
/*     */   int getConcurrencyLevel() {
/* 281 */     return this.concurrencyLevel == -1 ? 4 : this.concurrencyLevel;
/*     */   }
/*     */ 
/*     */   MapMaker strongKeys()
/*     */   {
/* 291 */     return setKeyStrength(MapMakerInternalMap.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public MapMaker weakKeys()
/*     */   {
/* 308 */     return setKeyStrength(MapMakerInternalMap.Strength.WEAK);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public MapMaker softKeys()
/*     */   {
/* 335 */     return setKeyStrength(MapMakerInternalMap.Strength.SOFT);
/*     */   }
/*     */ 
/*     */   MapMaker setKeyStrength(MapMakerInternalMap.Strength strength) {
/* 339 */     Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", new Object[] { this.keyStrength });
/* 340 */     this.keyStrength = ((MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength));
/* 341 */     if (strength != MapMakerInternalMap.Strength.STRONG)
/*     */     {
/* 343 */       this.useCustomMap = true;
/*     */     }
/* 345 */     return this;
/*     */   }
/*     */ 
/*     */   MapMakerInternalMap.Strength getKeyStrength() {
/* 349 */     return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   MapMaker strongValues()
/*     */   {
/* 359 */     return setValueStrength(MapMakerInternalMap.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public MapMaker weakValues()
/*     */   {
/* 382 */     return setValueStrength(MapMakerInternalMap.Strength.WEAK);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public MapMaker softValues()
/*     */   {
/* 408 */     return setValueStrength(MapMakerInternalMap.Strength.SOFT);
/*     */   }
/*     */ 
/*     */   MapMaker setValueStrength(MapMakerInternalMap.Strength strength) {
/* 412 */     Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", new Object[] { this.valueStrength });
/* 413 */     this.valueStrength = ((MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength));
/* 414 */     if (strength != MapMakerInternalMap.Strength.STRONG)
/*     */     {
/* 416 */       this.useCustomMap = true;
/*     */     }
/* 418 */     return this;
/*     */   }
/*     */ 
/*     */   MapMakerInternalMap.Strength getValueStrength() {
/* 422 */     return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public MapMaker expiration(long duration, TimeUnit unit)
/*     */   {
/* 440 */     return expireAfterWrite(duration, unit);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   MapMaker expireAfterWrite(long duration, TimeUnit unit)
/*     */   {
/* 471 */     checkExpiration(duration, unit);
/* 472 */     this.expireAfterWriteNanos = unit.toNanos(duration);
/* 473 */     if ((duration == 0L) && (this.nullRemovalCause == null))
/*     */     {
/* 475 */       this.nullRemovalCause = RemovalCause.EXPIRED;
/*     */     }
/* 477 */     this.useCustomMap = true;
/* 478 */     return this;
/*     */   }
/*     */ 
/*     */   private void checkExpiration(long duration, TimeUnit unit) {
/* 482 */     Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterWriteNanos) });
/*     */ 
/* 484 */     Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterAccessNanos) });
/*     */ 
/* 486 */     Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", new Object[] { Long.valueOf(duration), unit });
/*     */   }
/*     */ 
/*     */   long getExpireAfterWriteNanos() {
/* 490 */     return this.expireAfterWriteNanos == -1L ? 0L : this.expireAfterWriteNanos;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("To be supported")
/*     */   MapMaker expireAfterAccess(long duration, TimeUnit unit)
/*     */   {
/* 522 */     checkExpiration(duration, unit);
/* 523 */     this.expireAfterAccessNanos = unit.toNanos(duration);
/* 524 */     if ((duration == 0L) && (this.nullRemovalCause == null))
/*     */     {
/* 526 */       this.nullRemovalCause = RemovalCause.EXPIRED;
/*     */     }
/* 528 */     this.useCustomMap = true;
/* 529 */     return this;
/*     */   }
/*     */ 
/*     */   long getExpireAfterAccessNanos() {
/* 533 */     return this.expireAfterAccessNanos == -1L ? 0L : this.expireAfterAccessNanos;
/*     */   }
/*     */ 
/*     */   Ticker getTicker()
/*     */   {
/* 538 */     return (Ticker)Objects.firstNonNull(this.ticker, Ticker.systemTicker());
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @GwtIncompatible("To be supported")
/*     */   <K, V> GenericMapMaker<K, V> removalListener(RemovalListener<K, V> listener)
/*     */   {
/* 573 */     Preconditions.checkState(this.removalListener == null);
/*     */ 
/* 577 */     GenericMapMaker me = this;
/* 578 */     me.removalListener = ((RemovalListener)Preconditions.checkNotNull(listener));
/* 579 */     this.useCustomMap = true;
/* 580 */     return me;
/*     */   }
/*     */ 
/*     */   public <K, V> ConcurrentMap<K, V> makeMap()
/*     */   {
/* 597 */     if (!this.useCustomMap) {
/* 598 */       return new ConcurrentHashMap(getInitialCapacity(), 0.75F, getConcurrencyLevel());
/*     */     }
/* 600 */     return (ConcurrentMap)(this.nullRemovalCause == null ? new MapMakerInternalMap(this) : new NullConcurrentMap(this));
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("MapMakerInternalMap")
/*     */   <K, V> MapMakerInternalMap<K, V> makeCustomMap()
/*     */   {
/* 612 */     return new MapMakerInternalMap(this);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public <K, V> ConcurrentMap<K, V> makeComputingMap(Function<? super K, ? extends V> computingFunction)
/*     */   {
/* 677 */     return (ConcurrentMap)(useNullMap() ? new ComputingConcurrentHashMap.ComputingMapAdapter(this, computingFunction) : new NullComputingConcurrentMap(this, computingFunction));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 688 */     Objects.ToStringHelper s = Objects.toStringHelper(this);
/* 689 */     if (this.initialCapacity != -1) {
/* 690 */       s.add("initialCapacity", this.initialCapacity);
/*     */     }
/* 692 */     if (this.concurrencyLevel != -1) {
/* 693 */       s.add("concurrencyLevel", this.concurrencyLevel);
/*     */     }
/* 695 */     if (this.maximumSize != -1) {
/* 696 */       s.add("maximumSize", this.maximumSize);
/*     */     }
/* 698 */     if (this.expireAfterWriteNanos != -1L) {
/* 699 */       s.add("expireAfterWrite", this.expireAfterWriteNanos + "ns");
/*     */     }
/* 701 */     if (this.expireAfterAccessNanos != -1L) {
/* 702 */       s.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
/*     */     }
/* 704 */     if (this.keyStrength != null) {
/* 705 */       s.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
/*     */     }
/* 707 */     if (this.valueStrength != null) {
/* 708 */       s.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
/*     */     }
/* 710 */     if (this.keyEquivalence != null) {
/* 711 */       s.addValue("keyEquivalence");
/*     */     }
/* 713 */     if (this.valueEquivalence != null) {
/* 714 */       s.addValue("valueEquivalence");
/*     */     }
/* 716 */     if (this.removalListener != null) {
/* 717 */       s.addValue("removalListener");
/*     */     }
/* 719 */     return s.toString();
/*     */   }
/*     */ 
/*     */   static final class NullComputingConcurrentMap<K, V> extends MapMaker.NullConcurrentMap<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     final Function<? super K, ? extends V> computingFunction;
/*     */ 
/*     */     NullComputingConcurrentMap(MapMaker mapMaker, Function<? super K, ? extends V> computingFunction)
/*     */     {
/* 933 */       super();
/* 934 */       this.computingFunction = ((Function)Preconditions.checkNotNull(computingFunction));
/*     */     }
/*     */ 
/*     */     public V get(Object k)
/*     */     {
/* 940 */       Object key = k;
/* 941 */       Object value = compute(key);
/* 942 */       Preconditions.checkNotNull(value, this.computingFunction + " returned null for key " + key + ".");
/* 943 */       notifyRemoval(key, value);
/* 944 */       return value;
/*     */     }
/*     */ 
/*     */     private V compute(K key) {
/* 948 */       Preconditions.checkNotNull(key);
/*     */       try {
/* 950 */         return this.computingFunction.apply(key);
/*     */       } catch (ComputationException e) {
/* 952 */         throw e;
/*     */       } catch (Throwable t) {
/* 954 */         throw new ComputationException(t);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class NullConcurrentMap<K, V> extends AbstractMap<K, V>
/*     */     implements ConcurrentMap<K, V>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     private final MapMaker.RemovalListener<K, V> removalListener;
/*     */     private final MapMaker.RemovalCause removalCause;
/*     */ 
/*     */     NullConcurrentMap(MapMaker mapMaker)
/*     */     {
/* 855 */       this.removalListener = mapMaker.getRemovalListener();
/* 856 */       this.removalCause = mapMaker.nullRemovalCause;
/*     */     }
/*     */ 
/*     */     public boolean containsKey(@Nullable Object key)
/*     */     {
/* 863 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean containsValue(@Nullable Object value)
/*     */     {
/* 868 */       return false;
/*     */     }
/*     */ 
/*     */     public V get(@Nullable Object key)
/*     */     {
/* 873 */       return null;
/*     */     }
/*     */ 
/*     */     void notifyRemoval(K key, V value) {
/* 877 */       MapMaker.RemovalNotification notification = new MapMaker.RemovalNotification(key, value, this.removalCause);
/*     */ 
/* 879 */       this.removalListener.onRemoval(notification);
/*     */     }
/*     */ 
/*     */     public V put(K key, V value)
/*     */     {
/* 884 */       Preconditions.checkNotNull(key);
/* 885 */       Preconditions.checkNotNull(value);
/* 886 */       notifyRemoval(key, value);
/* 887 */       return null;
/*     */     }
/*     */ 
/*     */     public V putIfAbsent(K key, V value)
/*     */     {
/* 892 */       return put(key, value);
/*     */     }
/*     */ 
/*     */     public V remove(@Nullable Object key)
/*     */     {
/* 897 */       return null;
/*     */     }
/*     */ 
/*     */     public boolean remove(@Nullable Object key, @Nullable Object value)
/*     */     {
/* 902 */       return false;
/*     */     }
/*     */ 
/*     */     public V replace(K key, V value)
/*     */     {
/* 907 */       Preconditions.checkNotNull(key);
/* 908 */       Preconditions.checkNotNull(value);
/* 909 */       return null;
/*     */     }
/*     */ 
/*     */     public boolean replace(K key, @Nullable V oldValue, V newValue)
/*     */     {
/* 914 */       Preconditions.checkNotNull(key);
/* 915 */       Preconditions.checkNotNull(newValue);
/* 916 */       return false;
/*     */     }
/*     */ 
/*     */     public Set<Map.Entry<K, V>> entrySet()
/*     */     {
/* 921 */       return Collections.emptySet();
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract enum RemovalCause
/*     */   {
/* 785 */     EXPLICIT, 
/*     */ 
/* 798 */     REPLACED, 
/*     */ 
/* 810 */     COLLECTED, 
/*     */ 
/* 821 */     EXPIRED, 
/*     */ 
/* 832 */     SIZE;
/*     */ 
/*     */     abstract boolean wasEvicted();
/*     */   }
/*     */ 
/*     */   static final class RemovalNotification<K, V> extends ImmutableEntry<K, V>
/*     */   {
/*     */     private static final long serialVersionUID = 0L;
/*     */     private final MapMaker.RemovalCause cause;
/*     */ 
/*     */     RemovalNotification(@Nullable K key, @Nullable V value, MapMaker.RemovalCause cause)
/*     */     {
/* 757 */       super(value);
/* 758 */       this.cause = cause;
/*     */     }
/*     */ 
/*     */     public MapMaker.RemovalCause getCause()
/*     */     {
/* 765 */       return this.cause;
/*     */     }
/*     */ 
/*     */     public boolean wasEvicted()
/*     */     {
/* 773 */       return this.cause.wasEvicted();
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface RemovalListener<K, V>
/*     */   {
/*     */     public abstract void onRemoval(MapMaker.RemovalNotification<K, V> paramRemovalNotification);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.MapMaker
 * JD-Core Version:    0.6.2
 */