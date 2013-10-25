/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import com.google.common.base.Ascii;
/*     */ import com.google.common.base.Equivalence;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Supplier;
/*     */ import com.google.common.base.Suppliers;
/*     */ import com.google.common.base.Ticker;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.logging.Level;
/*     */ import java.util.logging.Logger;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class CacheBuilder<K, V>
/*     */ {
/*     */   private static final int DEFAULT_INITIAL_CAPACITY = 16;
/*     */   private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
/*     */   private static final int DEFAULT_EXPIRATION_NANOS = 0;
/*     */   private static final int DEFAULT_REFRESH_NANOS = 0;
/* 160 */   static final Supplier<? extends AbstractCache.StatsCounter> NULL_STATS_COUNTER = Suppliers.ofInstance(new AbstractCache.StatsCounter()
/*     */   {
/*     */     public void recordHits(int count)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void recordMisses(int count) {
/*     */     }
/*     */ 
/*     */     public void recordLoadSuccess(long loadTime) {
/*     */     }
/*     */ 
/*     */     public void recordLoadException(long loadTime) {
/*     */     }
/*     */ 
/*     */     public void recordEviction() {
/*     */     }
/*     */ 
/*     */     public CacheStats snapshot() {
/* 179 */       return CacheBuilder.EMPTY_STATS;
/*     */     }
/*     */   });
/*     */ 
/* 182 */   static final CacheStats EMPTY_STATS = new CacheStats(0L, 0L, 0L, 0L, 0L, 0L);
/*     */ 
/* 184 */   static final Supplier<AbstractCache.StatsCounter> CACHE_STATS_COUNTER = new Supplier()
/*     */   {
/*     */     public AbstractCache.StatsCounter get()
/*     */     {
/* 188 */       return new AbstractCache.SimpleStatsCounter();
/*     */     }
/* 184 */   };
/*     */ 
/* 208 */   static final Ticker NULL_TICKER = new Ticker()
/*     */   {
/*     */     public long read() {
/* 211 */       return 0L;
/*     */     }
/* 208 */   };
/*     */ 
/* 215 */   private static final Logger logger = Logger.getLogger(CacheBuilder.class.getName());
/*     */   static final int UNSET_INT = -1;
/* 219 */   boolean strictParsing = true;
/*     */ 
/* 221 */   int initialCapacity = -1;
/* 222 */   int concurrencyLevel = -1;
/* 223 */   long maximumSize = -1L;
/* 224 */   long maximumWeight = -1L;
/*     */   Weigher<? super K, ? super V> weigher;
/*     */   LocalCache.Strength keyStrength;
/*     */   LocalCache.Strength valueStrength;
/* 230 */   long expireAfterWriteNanos = -1L;
/* 231 */   long expireAfterAccessNanos = -1L;
/* 232 */   long refreshNanos = -1L;
/*     */   Equivalence<Object> keyEquivalence;
/*     */   Equivalence<Object> valueEquivalence;
/*     */   RemovalListener<? super K, ? super V> removalListener;
/*     */   Ticker ticker;
/* 240 */   Supplier<? extends AbstractCache.StatsCounter> statsCounterSupplier = NULL_STATS_COUNTER;
/*     */ 
/*     */   public static CacheBuilder<Object, Object> newBuilder()
/*     */   {
/* 250 */     return new CacheBuilder();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   @GwtIncompatible("To be supported")
/*     */   public static CacheBuilder<Object, Object> from(CacheBuilderSpec spec)
/*     */   {
/* 261 */     return spec.toCacheBuilder().lenientParsing();
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   @GwtIncompatible("To be supported")
/*     */   public static CacheBuilder<Object, Object> from(String spec)
/*     */   {
/* 275 */     return from(CacheBuilderSpec.parse(spec));
/*     */   }
/*     */ 
/*     */   CacheBuilder<K, V> lenientParsing()
/*     */   {
/* 282 */     this.strictParsing = false;
/* 283 */     return this;
/*     */   }
/*     */ 
/*     */   CacheBuilder<K, V> keyEquivalence(Equivalence<Object> equivalence)
/*     */   {
/* 293 */     Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", new Object[] { this.keyEquivalence });
/* 294 */     this.keyEquivalence = ((Equivalence)Preconditions.checkNotNull(equivalence));
/* 295 */     return this;
/*     */   }
/*     */ 
/*     */   Equivalence<Object> getKeyEquivalence() {
/* 299 */     return (Equivalence)Objects.firstNonNull(this.keyEquivalence, getKeyStrength().defaultEquivalence());
/*     */   }
/*     */ 
/*     */   CacheBuilder<K, V> valueEquivalence(Equivalence<Object> equivalence)
/*     */   {
/* 310 */     Preconditions.checkState(this.valueEquivalence == null, "value equivalence was already set to %s", new Object[] { this.valueEquivalence });
/*     */ 
/* 312 */     this.valueEquivalence = ((Equivalence)Preconditions.checkNotNull(equivalence));
/* 313 */     return this;
/*     */   }
/*     */ 
/*     */   Equivalence<Object> getValueEquivalence() {
/* 317 */     return (Equivalence)Objects.firstNonNull(this.valueEquivalence, getValueStrength().defaultEquivalence());
/*     */   }
/*     */ 
/*     */   public CacheBuilder<K, V> initialCapacity(int initialCapacity)
/*     */   {
/* 331 */     Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", new Object[] { Integer.valueOf(this.initialCapacity) });
/*     */ 
/* 333 */     Preconditions.checkArgument(initialCapacity >= 0);
/* 334 */     this.initialCapacity = initialCapacity;
/* 335 */     return this;
/*     */   }
/*     */ 
/*     */   int getInitialCapacity() {
/* 339 */     return this.initialCapacity == -1 ? 16 : this.initialCapacity;
/*     */   }
/*     */ 
/*     */   public CacheBuilder<K, V> concurrencyLevel(int concurrencyLevel)
/*     */   {
/* 373 */     Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", new Object[] { Integer.valueOf(this.concurrencyLevel) });
/*     */ 
/* 375 */     Preconditions.checkArgument(concurrencyLevel > 0);
/* 376 */     this.concurrencyLevel = concurrencyLevel;
/* 377 */     return this;
/*     */   }
/*     */ 
/*     */   int getConcurrencyLevel() {
/* 381 */     return this.concurrencyLevel == -1 ? 4 : this.concurrencyLevel;
/*     */   }
/*     */ 
/*     */   public CacheBuilder<K, V> maximumSize(long size)
/*     */   {
/* 398 */     Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", new Object[] { Long.valueOf(this.maximumSize) });
/*     */ 
/* 400 */     Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", new Object[] { Long.valueOf(this.maximumWeight) });
/*     */ 
/* 402 */     Preconditions.checkState(this.weigher == null, "maximum size can not be combined with weigher");
/* 403 */     Preconditions.checkArgument(size >= 0L, "maximum size must not be negative");
/* 404 */     this.maximumSize = size;
/* 405 */     return this;
/*     */   }
/*     */ 
/*     */   public CacheBuilder<K, V> maximumWeight(long weight)
/*     */   {
/* 431 */     Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", new Object[] { Long.valueOf(this.maximumWeight) });
/*     */ 
/* 433 */     Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", new Object[] { Long.valueOf(this.maximumSize) });
/*     */ 
/* 435 */     this.maximumWeight = weight;
/* 436 */     Preconditions.checkArgument(weight >= 0L, "maximum weight must not be negative");
/* 437 */     return this;
/*     */   }
/*     */ 
/*     */   public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> weigher(Weigher<? super K1, ? super V1> weigher)
/*     */   {
/* 470 */     Preconditions.checkState(this.weigher == null);
/* 471 */     if (this.strictParsing) {
/* 472 */       Preconditions.checkState(this.maximumSize == -1L, "weigher can not be combined with maximum size", new Object[] { Long.valueOf(this.maximumSize) });
/*     */     }
/*     */ 
/* 478 */     CacheBuilder me = this;
/* 479 */     me.weigher = ((Weigher)Preconditions.checkNotNull(weigher));
/* 480 */     return me;
/*     */   }
/*     */ 
/*     */   long getMaximumWeight() {
/* 484 */     if ((this.expireAfterWriteNanos == 0L) || (this.expireAfterAccessNanos == 0L)) {
/* 485 */       return 0L;
/*     */     }
/* 487 */     return this.weigher == null ? this.maximumSize : this.maximumWeight;
/*     */   }
/*     */ 
/*     */   <K1 extends K, V1 extends V> Weigher<K1, V1> getWeigher()
/*     */   {
/* 493 */     return (Weigher)Objects.firstNonNull(this.weigher, OneWeigher.INSTANCE);
/*     */   }
/*     */ 
/*     */   CacheBuilder<K, V> strongKeys()
/*     */   {
/* 502 */     return setKeyStrength(LocalCache.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public CacheBuilder<K, V> weakKeys()
/*     */   {
/* 520 */     return setKeyStrength(LocalCache.Strength.WEAK);
/*     */   }
/*     */ 
/*     */   CacheBuilder<K, V> setKeyStrength(LocalCache.Strength strength) {
/* 524 */     Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", new Object[] { this.keyStrength });
/* 525 */     this.keyStrength = ((LocalCache.Strength)Preconditions.checkNotNull(strength));
/* 526 */     return this;
/*     */   }
/*     */ 
/*     */   LocalCache.Strength getKeyStrength() {
/* 530 */     return (LocalCache.Strength)Objects.firstNonNull(this.keyStrength, LocalCache.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   CacheBuilder<K, V> strongValues()
/*     */   {
/* 539 */     return setValueStrength(LocalCache.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.WeakReference")
/*     */   public CacheBuilder<K, V> weakValues()
/*     */   {
/* 560 */     return setValueStrength(LocalCache.Strength.WEAK);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.lang.ref.SoftReference")
/*     */   public CacheBuilder<K, V> softValues()
/*     */   {
/* 584 */     return setValueStrength(LocalCache.Strength.SOFT);
/*     */   }
/*     */ 
/*     */   CacheBuilder<K, V> setValueStrength(LocalCache.Strength strength) {
/* 588 */     Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", new Object[] { this.valueStrength });
/* 589 */     this.valueStrength = ((LocalCache.Strength)Preconditions.checkNotNull(strength));
/* 590 */     return this;
/*     */   }
/*     */ 
/*     */   LocalCache.Strength getValueStrength() {
/* 594 */     return (LocalCache.Strength)Objects.firstNonNull(this.valueStrength, LocalCache.Strength.STRONG);
/*     */   }
/*     */ 
/*     */   public CacheBuilder<K, V> expireAfterWrite(long duration, TimeUnit unit)
/*     */   {
/* 617 */     Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterWriteNanos) });
/*     */ 
/* 619 */     Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", new Object[] { Long.valueOf(duration), unit });
/* 620 */     this.expireAfterWriteNanos = unit.toNanos(duration);
/* 621 */     return this;
/*     */   }
/*     */ 
/*     */   long getExpireAfterWriteNanos() {
/* 625 */     return this.expireAfterWriteNanos == -1L ? 0L : this.expireAfterWriteNanos;
/*     */   }
/*     */ 
/*     */   public CacheBuilder<K, V> expireAfterAccess(long duration, TimeUnit unit)
/*     */   {
/* 651 */     Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", new Object[] { Long.valueOf(this.expireAfterAccessNanos) });
/*     */ 
/* 653 */     Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", new Object[] { Long.valueOf(duration), unit });
/* 654 */     this.expireAfterAccessNanos = unit.toNanos(duration);
/* 655 */     return this;
/*     */   }
/*     */ 
/*     */   long getExpireAfterAccessNanos() {
/* 659 */     return this.expireAfterAccessNanos == -1L ? 0L : this.expireAfterAccessNanos;
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   @GwtIncompatible("To be supported")
/*     */   public CacheBuilder<K, V> refreshAfterWrite(long duration, TimeUnit unit)
/*     */   {
/* 691 */     Preconditions.checkNotNull(unit);
/* 692 */     Preconditions.checkState(this.refreshNanos == -1L, "refresh was already set to %s ns", new Object[] { Long.valueOf(this.refreshNanos) });
/* 693 */     Preconditions.checkArgument(duration > 0L, "duration must be positive: %s %s", new Object[] { Long.valueOf(duration), unit });
/* 694 */     this.refreshNanos = unit.toNanos(duration);
/* 695 */     return this;
/*     */   }
/*     */ 
/*     */   long getRefreshNanos() {
/* 699 */     return this.refreshNanos == -1L ? 0L : this.refreshNanos;
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("To be supported")
/*     */   public CacheBuilder<K, V> ticker(Ticker ticker)
/*     */   {
/* 713 */     Preconditions.checkState(this.ticker == null);
/* 714 */     this.ticker = ((Ticker)Preconditions.checkNotNull(ticker));
/* 715 */     return this;
/*     */   }
/*     */ 
/*     */   Ticker getTicker(boolean recordsTime) {
/* 719 */     if (this.ticker != null) {
/* 720 */       return this.ticker;
/*     */     }
/* 722 */     return recordsTime ? Ticker.systemTicker() : NULL_TICKER;
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   @GwtIncompatible("To be supported")
/*     */   public <K1 extends K, V1 extends V> CacheBuilder<K1, V1> removalListener(RemovalListener<? super K1, ? super V1> listener)
/*     */   {
/* 756 */     Preconditions.checkState(this.removalListener == null);
/*     */ 
/* 760 */     CacheBuilder me = this;
/* 761 */     me.removalListener = ((RemovalListener)Preconditions.checkNotNull(listener));
/* 762 */     return me;
/*     */   }
/*     */ 
/*     */   <K1 extends K, V1 extends V> RemovalListener<K1, V1> getRemovalListener()
/*     */   {
/* 768 */     return (RemovalListener)Objects.firstNonNull(this.removalListener, NullListener.INSTANCE);
/*     */   }
/*     */ 
/*     */   public CacheBuilder<K, V> recordStats()
/*     */   {
/* 780 */     this.statsCounterSupplier = CACHE_STATS_COUNTER;
/* 781 */     return this;
/*     */   }
/*     */ 
/*     */   Supplier<? extends AbstractCache.StatsCounter> getStatsCounterSupplier() {
/* 785 */     return this.statsCounterSupplier;
/*     */   }
/*     */ 
/*     */   public <K1 extends K, V1 extends V> LoadingCache<K1, V1> build(CacheLoader<? super K1, V1> loader)
/*     */   {
/* 802 */     checkWeightWithWeigher();
/* 803 */     return new LocalCache.LocalLoadingCache(this, loader);
/*     */   }
/*     */ 
/*     */   public <K1 extends K, V1 extends V> Cache<K1, V1> build()
/*     */   {
/* 819 */     checkWeightWithWeigher();
/* 820 */     checkNonLoadingCache();
/* 821 */     return new LocalCache.LocalManualCache(this);
/*     */   }
/*     */ 
/*     */   private void checkNonLoadingCache() {
/* 825 */     Preconditions.checkState(this.refreshNanos == -1L, "refreshAfterWrite requires a LoadingCache");
/*     */   }
/*     */ 
/*     */   private void checkWeightWithWeigher() {
/* 829 */     if (this.weigher == null) {
/* 830 */       Preconditions.checkState(this.maximumWeight == -1L, "maximumWeight requires weigher");
/*     */     }
/* 832 */     else if (this.strictParsing) {
/* 833 */       Preconditions.checkState(this.maximumWeight != -1L, "weigher requires maximumWeight");
/*     */     }
/* 835 */     else if (this.maximumWeight == -1L)
/* 836 */       logger.log(Level.WARNING, "ignoring weigher specified without maximumWeight");
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 848 */     Objects.ToStringHelper s = Objects.toStringHelper(this);
/* 849 */     if (this.initialCapacity != -1) {
/* 850 */       s.add("initialCapacity", this.initialCapacity);
/*     */     }
/* 852 */     if (this.concurrencyLevel != -1) {
/* 853 */       s.add("concurrencyLevel", this.concurrencyLevel);
/*     */     }
/* 855 */     if (this.maximumWeight != -1L) {
/* 856 */       if (this.weigher == null)
/* 857 */         s.add("maximumSize", this.maximumWeight);
/*     */       else {
/* 859 */         s.add("maximumWeight", this.maximumWeight);
/*     */       }
/*     */     }
/* 862 */     if (this.expireAfterWriteNanos != -1L) {
/* 863 */       s.add("expireAfterWrite", this.expireAfterWriteNanos + "ns");
/*     */     }
/* 865 */     if (this.expireAfterAccessNanos != -1L) {
/* 866 */       s.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
/*     */     }
/* 868 */     if (this.keyStrength != null) {
/* 869 */       s.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
/*     */     }
/* 871 */     if (this.valueStrength != null) {
/* 872 */       s.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
/*     */     }
/* 874 */     if (this.keyEquivalence != null) {
/* 875 */       s.addValue("keyEquivalence");
/*     */     }
/* 877 */     if (this.valueEquivalence != null) {
/* 878 */       s.addValue("valueEquivalence");
/*     */     }
/* 880 */     if (this.removalListener != null) {
/* 881 */       s.addValue("removalListener");
/*     */     }
/* 883 */     return s.toString();
/*     */   }
/*     */ 
/*     */   static enum OneWeigher
/*     */     implements Weigher<Object, Object>
/*     */   {
/* 200 */     INSTANCE;
/*     */ 
/*     */     public int weigh(Object key, Object value)
/*     */     {
/* 204 */       return 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum NullListener
/*     */     implements RemovalListener<Object, Object>
/*     */   {
/* 193 */     INSTANCE;
/*     */ 
/*     */     public void onRemoval(RemovalNotification<Object, Object> notification)
/*     */     {
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.CacheBuilder
 * JD-Core Version:    0.6.2
 */