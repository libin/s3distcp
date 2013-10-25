/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Splitter;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public final class CacheBuilderSpec
/*     */ {
/*  88 */   private static final Splitter KEYS_SPLITTER = Splitter.on(',').trimResults();
/*     */ 
/*  91 */   private static final Splitter KEY_VALUE_SPLITTER = Splitter.on('=').trimResults();
/*     */ 
/*  94 */   private static final ImmutableMap<String, ValueParser> VALUE_PARSERS = ImmutableMap.builder().put("initialCapacity", new InitialCapacityParser()).put("maximumSize", new MaximumSizeParser()).put("maximumWeight", new MaximumWeightParser()).put("concurrencyLevel", new ConcurrencyLevelParser()).put("weakKeys", new KeyStrengthParser(LocalCache.Strength.WEAK)).put("softValues", new ValueStrengthParser(LocalCache.Strength.SOFT)).put("weakValues", new ValueStrengthParser(LocalCache.Strength.WEAK)).put("expireAfterAccess", new AccessDurationParser()).put("expireAfterWrite", new WriteDurationParser()).put("refreshAfterWrite", new RefreshDurationParser()).put("refreshInterval", new RefreshDurationParser()).build();
/*     */ 
/*     */   @VisibleForTesting
/*     */   Integer initialCapacity;
/*     */ 
/*     */   @VisibleForTesting
/*     */   Long maximumSize;
/*     */ 
/*     */   @VisibleForTesting
/*     */   Long maximumWeight;
/*     */ 
/*     */   @VisibleForTesting
/*     */   Integer concurrencyLevel;
/*     */ 
/*     */   @VisibleForTesting
/*     */   LocalCache.Strength keyStrength;
/*     */ 
/*     */   @VisibleForTesting
/*     */   LocalCache.Strength valueStrength;
/*     */ 
/*     */   @VisibleForTesting
/*     */   long writeExpirationDuration;
/*     */ 
/*     */   @VisibleForTesting
/*     */   TimeUnit writeExpirationTimeUnit;
/*     */ 
/*     */   @VisibleForTesting
/*     */   long accessExpirationDuration;
/*     */ 
/*     */   @VisibleForTesting
/*     */   TimeUnit accessExpirationTimeUnit;
/*     */ 
/*     */   @VisibleForTesting
/*     */   long refreshDuration;
/*     */ 
/*     */   @VisibleForTesting
/*     */   TimeUnit refreshTimeUnit;
/*     */   private final String specification;
/*     */ 
/* 125 */   private CacheBuilderSpec(String specification) { this.specification = specification; }
/*     */ 
/*     */ 
/*     */   public static CacheBuilderSpec parse(String cacheBuilderSpecification)
/*     */   {
/* 134 */     CacheBuilderSpec spec = new CacheBuilderSpec(cacheBuilderSpecification);
/* 135 */     if (!cacheBuilderSpecification.isEmpty()) {
/* 136 */       for (String keyValuePair : KEYS_SPLITTER.split(cacheBuilderSpecification)) {
/* 137 */         List keyAndValue = ImmutableList.copyOf(KEY_VALUE_SPLITTER.split(keyValuePair));
/* 138 */         Preconditions.checkArgument(!keyAndValue.isEmpty(), "blank key-value pair");
/* 139 */         Preconditions.checkArgument(keyAndValue.size() <= 2, "key-value pair %s with more than one equals sign", new Object[] { keyValuePair });
/*     */ 
/* 143 */         String key = (String)keyAndValue.get(0);
/* 144 */         ValueParser valueParser = (ValueParser)VALUE_PARSERS.get(key);
/* 145 */         Preconditions.checkArgument(valueParser != null, "unknown key %s", new Object[] { key });
/*     */ 
/* 147 */         String value = keyAndValue.size() == 1 ? null : (String)keyAndValue.get(1);
/* 148 */         valueParser.parse(spec, key, value);
/*     */       }
/*     */     }
/*     */ 
/* 152 */     return spec;
/*     */   }
/*     */ 
/*     */   public static CacheBuilderSpec disableCaching()
/*     */   {
/* 160 */     return parse("maximumSize=0");
/*     */   }
/*     */ 
/*     */   CacheBuilder<Object, Object> toCacheBuilder()
/*     */   {
/* 167 */     CacheBuilder builder = CacheBuilder.newBuilder();
/* 168 */     if (this.initialCapacity != null) {
/* 169 */       builder.initialCapacity(this.initialCapacity.intValue());
/*     */     }
/* 171 */     if (this.maximumSize != null) {
/* 172 */       builder.maximumSize(this.maximumSize.longValue());
/*     */     }
/* 174 */     if (this.maximumWeight != null) {
/* 175 */       builder.maximumWeight(this.maximumWeight.longValue());
/*     */     }
/* 177 */     if (this.concurrencyLevel != null) {
/* 178 */       builder.concurrencyLevel(this.concurrencyLevel.intValue());
/*     */     }
/* 180 */     if (this.keyStrength != null) {
/* 181 */       switch (1.$SwitchMap$com$google$common$cache$LocalCache$Strength[this.keyStrength.ordinal()]) {
/*     */       case 1:
/* 183 */         builder.weakKeys();
/* 184 */         break;
/*     */       default:
/* 186 */         throw new AssertionError();
/*     */       }
/*     */     }
/* 189 */     if (this.valueStrength != null) {
/* 190 */       switch (1.$SwitchMap$com$google$common$cache$LocalCache$Strength[this.valueStrength.ordinal()]) {
/*     */       case 2:
/* 192 */         builder.softValues();
/* 193 */         break;
/*     */       case 1:
/* 195 */         builder.weakValues();
/* 196 */         break;
/*     */       default:
/* 198 */         throw new AssertionError();
/*     */       }
/*     */     }
/* 201 */     if (this.writeExpirationTimeUnit != null) {
/* 202 */       builder.expireAfterWrite(this.writeExpirationDuration, this.writeExpirationTimeUnit);
/*     */     }
/* 204 */     if (this.accessExpirationTimeUnit != null) {
/* 205 */       builder.expireAfterAccess(this.accessExpirationDuration, this.accessExpirationTimeUnit);
/*     */     }
/* 207 */     if (this.refreshTimeUnit != null) {
/* 208 */       builder.refreshAfterWrite(this.refreshDuration, this.refreshTimeUnit);
/*     */     }
/*     */ 
/* 211 */     return builder;
/*     */   }
/*     */ 
/*     */   public String toParsableString()
/*     */   {
/* 221 */     return this.specification;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 230 */     return Objects.toStringHelper(this).addValue(toParsableString()).toString();
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 235 */     return Objects.hashCode(new Object[] { this.initialCapacity, this.maximumSize, this.maximumWeight, this.concurrencyLevel, this.keyStrength, this.valueStrength, durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(this.refreshDuration, this.refreshTimeUnit) });
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 249 */     if (this == obj) {
/* 250 */       return true;
/*     */     }
/* 252 */     if (!(obj instanceof CacheBuilderSpec)) {
/* 253 */       return false;
/*     */     }
/* 255 */     CacheBuilderSpec that = (CacheBuilderSpec)obj;
/* 256 */     return (Objects.equal(this.initialCapacity, that.initialCapacity)) && (Objects.equal(this.maximumSize, that.maximumSize)) && (Objects.equal(this.maximumWeight, that.maximumWeight)) && (Objects.equal(this.concurrencyLevel, that.concurrencyLevel)) && (Objects.equal(this.keyStrength, that.keyStrength)) && (Objects.equal(this.valueStrength, that.valueStrength)) && (Objects.equal(durationInNanos(this.writeExpirationDuration, this.writeExpirationTimeUnit), durationInNanos(that.writeExpirationDuration, that.writeExpirationTimeUnit))) && (Objects.equal(durationInNanos(this.accessExpirationDuration, this.accessExpirationTimeUnit), durationInNanos(that.accessExpirationDuration, that.accessExpirationTimeUnit))) && (Objects.equal(durationInNanos(this.refreshDuration, this.refreshTimeUnit), durationInNanos(that.refreshDuration, that.refreshTimeUnit)));
/*     */   }
/*     */ 
/*     */   @Nullable
/*     */   private static Long durationInNanos(long duration, @Nullable TimeUnit unit)
/*     */   {
/* 275 */     return unit == null ? null : Long.valueOf(unit.toNanos(duration));
/*     */   }
/*     */ 
/*     */   static class RefreshDurationParser extends CacheBuilderSpec.DurationParser
/*     */   {
/*     */     protected void parseDuration(CacheBuilderSpec spec, long duration, TimeUnit unit)
/*     */     {
/* 450 */       Preconditions.checkArgument(spec.refreshTimeUnit == null, "refreshAfterWrite already set");
/* 451 */       spec.refreshDuration = duration;
/* 452 */       spec.refreshTimeUnit = unit;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class WriteDurationParser extends CacheBuilderSpec.DurationParser
/*     */   {
/*     */     protected void parseDuration(CacheBuilderSpec spec, long duration, TimeUnit unit)
/*     */     {
/* 441 */       Preconditions.checkArgument(spec.writeExpirationTimeUnit == null, "expireAfterWrite already set");
/* 442 */       spec.writeExpirationDuration = duration;
/* 443 */       spec.writeExpirationTimeUnit = unit;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class AccessDurationParser extends CacheBuilderSpec.DurationParser
/*     */   {
/*     */     protected void parseDuration(CacheBuilderSpec spec, long duration, TimeUnit unit)
/*     */     {
/* 432 */       Preconditions.checkArgument(spec.accessExpirationTimeUnit == null, "expireAfterAccess already set");
/* 433 */       spec.accessExpirationDuration = duration;
/* 434 */       spec.accessExpirationTimeUnit = unit;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class DurationParser
/*     */     implements CacheBuilderSpec.ValueParser
/*     */   {
/*     */     protected abstract void parseDuration(CacheBuilderSpec paramCacheBuilderSpec, long paramLong, TimeUnit paramTimeUnit);
/*     */ 
/*     */     public void parse(CacheBuilderSpec spec, String key, String value)
/*     */     {
/* 397 */       Preconditions.checkArgument((value != null) && (!value.isEmpty()), "value of key %s omitted", new Object[] { key });
/*     */       try {
/* 399 */         char lastChar = value.charAt(value.length() - 1);
/*     */         TimeUnit timeUnit;
/* 401 */         switch (lastChar) {
/*     */         case 'd':
/* 403 */           timeUnit = TimeUnit.DAYS;
/* 404 */           break;
/*     */         case 'h':
/* 406 */           timeUnit = TimeUnit.HOURS;
/* 407 */           break;
/*     */         case 'm':
/* 409 */           timeUnit = TimeUnit.MINUTES;
/* 410 */           break;
/*     */         case 's':
/* 412 */           timeUnit = TimeUnit.SECONDS;
/* 413 */           break;
/*     */         default:
/* 415 */           throw new IllegalArgumentException(String.format("key %s invalid format.  was %s, must end with one of [dDhHmMsS]", new Object[] { key, value }));
/*     */         }
/*     */ 
/* 420 */         long duration = Long.parseLong(value.substring(0, value.length() - 1));
/* 421 */         parseDuration(spec, duration, timeUnit);
/*     */       } catch (NumberFormatException e) {
/* 423 */         throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", new Object[] { key, value }));
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ValueStrengthParser
/*     */     implements CacheBuilderSpec.ValueParser
/*     */   {
/*     */     private final LocalCache.Strength strength;
/*     */ 
/*     */     public ValueStrengthParser(LocalCache.Strength strength)
/*     */     {
/* 375 */       this.strength = strength;
/*     */     }
/*     */ 
/*     */     public void parse(CacheBuilderSpec spec, String key, @Nullable String value)
/*     */     {
/* 380 */       Preconditions.checkArgument(value == null, "key %s does not take values", new Object[] { key });
/* 381 */       Preconditions.checkArgument(spec.valueStrength == null, "%s was already set to %s", new Object[] { key, spec.valueStrength });
/*     */ 
/* 384 */       spec.valueStrength = this.strength;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class KeyStrengthParser
/*     */     implements CacheBuilderSpec.ValueParser
/*     */   {
/*     */     private final LocalCache.Strength strength;
/*     */ 
/*     */     public KeyStrengthParser(LocalCache.Strength strength)
/*     */     {
/* 359 */       this.strength = strength;
/*     */     }
/*     */ 
/*     */     public void parse(CacheBuilderSpec spec, String key, @Nullable String value)
/*     */     {
/* 364 */       Preconditions.checkArgument(value == null, "key %s does not take values", new Object[] { key });
/* 365 */       Preconditions.checkArgument(spec.keyStrength == null, "%s was already set to %s", new Object[] { key, spec.keyStrength });
/* 366 */       spec.keyStrength = this.strength;
/*     */     }
/*     */   }
/*     */ 
/*     */   static class ConcurrencyLevelParser extends CacheBuilderSpec.IntegerParser
/*     */   {
/*     */     protected void parseInteger(CacheBuilderSpec spec, int value)
/*     */     {
/* 348 */       Preconditions.checkArgument(spec.concurrencyLevel == null, "concurrency level was already set to ", new Object[] { spec.concurrencyLevel });
/*     */ 
/* 350 */       spec.concurrencyLevel = Integer.valueOf(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class MaximumWeightParser extends CacheBuilderSpec.LongParser
/*     */   {
/*     */     protected void parseLong(CacheBuilderSpec spec, long value)
/*     */     {
/* 336 */       Preconditions.checkArgument(spec.maximumWeight == null, "maximum weight was already set to ", new Object[] { spec.maximumWeight });
/*     */ 
/* 338 */       Preconditions.checkArgument(spec.maximumSize == null, "maximum size was already set to ", new Object[] { spec.maximumSize });
/*     */ 
/* 340 */       spec.maximumWeight = Long.valueOf(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class MaximumSizeParser extends CacheBuilderSpec.LongParser
/*     */   {
/*     */     protected void parseLong(CacheBuilderSpec spec, long value)
/*     */     {
/* 324 */       Preconditions.checkArgument(spec.maximumSize == null, "maximum size was already set to ", new Object[] { spec.maximumSize });
/*     */ 
/* 326 */       Preconditions.checkArgument(spec.maximumWeight == null, "maximum weight was already set to ", new Object[] { spec.maximumWeight });
/*     */ 
/* 328 */       spec.maximumSize = Long.valueOf(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class InitialCapacityParser extends CacheBuilderSpec.IntegerParser
/*     */   {
/*     */     protected void parseInteger(CacheBuilderSpec spec, int value)
/*     */     {
/* 314 */       Preconditions.checkArgument(spec.initialCapacity == null, "initial capacity was already set to ", new Object[] { spec.initialCapacity });
/*     */ 
/* 316 */       spec.initialCapacity = Integer.valueOf(value);
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class LongParser
/*     */     implements CacheBuilderSpec.ValueParser
/*     */   {
/*     */     protected abstract void parseLong(CacheBuilderSpec paramCacheBuilderSpec, long paramLong);
/*     */ 
/*     */     public void parse(CacheBuilderSpec spec, String key, String value)
/*     */     {
/* 300 */       Preconditions.checkArgument((value != null) && (!value.isEmpty()), "value of key %s omitted", new Object[] { key });
/*     */       try {
/* 302 */         parseLong(spec, Long.parseLong(value));
/*     */       } catch (NumberFormatException e) {
/* 304 */         throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", new Object[] { key, value }), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract class IntegerParser
/*     */     implements CacheBuilderSpec.ValueParser
/*     */   {
/*     */     protected abstract void parseInteger(CacheBuilderSpec paramCacheBuilderSpec, int paramInt);
/*     */ 
/*     */     public void parse(CacheBuilderSpec spec, String key, String value)
/*     */     {
/* 284 */       Preconditions.checkArgument((value != null) && (!value.isEmpty()), "value of key %s omitted", new Object[] { key });
/*     */       try {
/* 286 */         parseInteger(spec, Integer.parseInt(value));
/*     */       } catch (NumberFormatException e) {
/* 288 */         throw new IllegalArgumentException(String.format("key %s value set to %s, must be integer", new Object[] { key, value }), e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface ValueParser
/*     */   {
/*     */     public abstract void parse(CacheBuilderSpec paramCacheBuilderSpec, String paramString1, @Nullable String paramString2);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.CacheBuilderSpec
 * JD-Core Version:    0.6.2
 */