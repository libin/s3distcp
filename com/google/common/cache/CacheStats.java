/*     */ package com.google.common.cache;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ public final class CacheStats
/*     */ {
/*     */   private final long hitCount;
/*     */   private final long missCount;
/*     */   private final long loadSuccessCount;
/*     */   private final long loadExceptionCount;
/*     */   private final long totalLoadTime;
/*     */   private final long evictionCount;
/*     */ 
/*     */   public CacheStats(long hitCount, long missCount, long loadSuccessCount, long loadExceptionCount, long totalLoadTime, long evictionCount)
/*     */   {
/*  72 */     Preconditions.checkArgument(hitCount >= 0L);
/*  73 */     Preconditions.checkArgument(missCount >= 0L);
/*  74 */     Preconditions.checkArgument(loadSuccessCount >= 0L);
/*  75 */     Preconditions.checkArgument(loadExceptionCount >= 0L);
/*  76 */     Preconditions.checkArgument(totalLoadTime >= 0L);
/*  77 */     Preconditions.checkArgument(evictionCount >= 0L);
/*     */ 
/*  79 */     this.hitCount = hitCount;
/*  80 */     this.missCount = missCount;
/*  81 */     this.loadSuccessCount = loadSuccessCount;
/*  82 */     this.loadExceptionCount = loadExceptionCount;
/*  83 */     this.totalLoadTime = totalLoadTime;
/*  84 */     this.evictionCount = evictionCount;
/*     */   }
/*     */ 
/*     */   public long requestCount()
/*     */   {
/*  92 */     return this.hitCount + this.missCount;
/*     */   }
/*     */ 
/*     */   public long hitCount()
/*     */   {
/*  99 */     return this.hitCount;
/*     */   }
/*     */ 
/*     */   public double hitRate()
/*     */   {
/* 108 */     long requestCount = requestCount();
/* 109 */     return requestCount == 0L ? 1.0D : this.hitCount / requestCount;
/*     */   }
/*     */ 
/*     */   public long missCount()
/*     */   {
/* 119 */     return this.missCount;
/*     */   }
/*     */ 
/*     */   public double missRate()
/*     */   {
/* 132 */     long requestCount = requestCount();
/* 133 */     return requestCount == 0L ? 0.0D : this.missCount / requestCount;
/*     */   }
/*     */ 
/*     */   public long loadCount()
/*     */   {
/* 142 */     return this.loadSuccessCount + this.loadExceptionCount;
/*     */   }
/*     */ 
/*     */   public long loadSuccessCount()
/*     */   {
/* 153 */     return this.loadSuccessCount;
/*     */   }
/*     */ 
/*     */   public long loadExceptionCount()
/*     */   {
/* 164 */     return this.loadExceptionCount;
/*     */   }
/*     */ 
/*     */   public double loadExceptionRate()
/*     */   {
/* 173 */     long totalLoadCount = this.loadSuccessCount + this.loadExceptionCount;
/* 174 */     return totalLoadCount == 0L ? 0.0D : this.loadExceptionCount / totalLoadCount;
/*     */   }
/*     */ 
/*     */   public long totalLoadTime()
/*     */   {
/* 185 */     return this.totalLoadTime;
/*     */   }
/*     */ 
/*     */   public double averageLoadPenalty()
/*     */   {
/* 193 */     long totalLoadCount = this.loadSuccessCount + this.loadExceptionCount;
/* 194 */     return totalLoadCount == 0L ? 0.0D : this.totalLoadTime / totalLoadCount;
/*     */   }
/*     */ 
/*     */   public long evictionCount()
/*     */   {
/* 204 */     return this.evictionCount;
/*     */   }
/*     */ 
/*     */   public CacheStats minus(CacheStats other)
/*     */   {
/* 213 */     return new CacheStats(Math.max(0L, this.hitCount - other.hitCount), Math.max(0L, this.missCount - other.missCount), Math.max(0L, this.loadSuccessCount - other.loadSuccessCount), Math.max(0L, this.loadExceptionCount - other.loadExceptionCount), Math.max(0L, this.totalLoadTime - other.totalLoadTime), Math.max(0L, this.evictionCount - other.evictionCount));
/*     */   }
/*     */ 
/*     */   public CacheStats plus(CacheStats other)
/*     */   {
/* 229 */     return new CacheStats(this.hitCount + other.hitCount, this.missCount + other.missCount, this.loadSuccessCount + other.loadSuccessCount, this.loadExceptionCount + other.loadExceptionCount, this.totalLoadTime + other.totalLoadTime, this.evictionCount + other.evictionCount);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 240 */     return Objects.hashCode(new Object[] { Long.valueOf(this.hitCount), Long.valueOf(this.missCount), Long.valueOf(this.loadSuccessCount), Long.valueOf(this.loadExceptionCount), Long.valueOf(this.totalLoadTime), Long.valueOf(this.evictionCount) });
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 246 */     if ((object instanceof CacheStats)) {
/* 247 */       CacheStats other = (CacheStats)object;
/* 248 */       return (this.hitCount == other.hitCount) && (this.missCount == other.missCount) && (this.loadSuccessCount == other.loadSuccessCount) && (this.loadExceptionCount == other.loadExceptionCount) && (this.totalLoadTime == other.totalLoadTime) && (this.evictionCount == other.evictionCount);
/*     */     }
/*     */ 
/* 255 */     return false;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 260 */     return Objects.toStringHelper(this).add("hitCount", this.hitCount).add("missCount", this.missCount).add("loadSuccessCount", this.loadSuccessCount).add("loadExceptionCount", this.loadExceptionCount).add("totalLoadTime", this.totalLoadTime).add("evictionCount", this.evictionCount).toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.CacheStats
 * JD-Core Version:    0.6.2
 */