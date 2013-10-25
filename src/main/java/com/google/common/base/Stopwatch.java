/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class Stopwatch
/*     */ {
/*     */   private final Ticker ticker;
/*     */   private boolean isRunning;
/*     */   private long elapsedNanos;
/*     */   private long startTick;
/*     */ 
/*     */   public Stopwatch()
/*     */   {
/*  84 */     this(Ticker.systemTicker());
/*     */   }
/*     */ 
/*     */   public Stopwatch(Ticker ticker)
/*     */   {
/*  92 */     this.ticker = ((Ticker)Preconditions.checkNotNull(ticker));
/*     */   }
/*     */ 
/*     */   public boolean isRunning()
/*     */   {
/* 101 */     return this.isRunning;
/*     */   }
/*     */ 
/*     */   public Stopwatch start()
/*     */   {
/* 111 */     Preconditions.checkState(!this.isRunning);
/* 112 */     this.isRunning = true;
/* 113 */     this.startTick = this.ticker.read();
/* 114 */     return this;
/*     */   }
/*     */ 
/*     */   public Stopwatch stop()
/*     */   {
/* 125 */     long tick = this.ticker.read();
/* 126 */     Preconditions.checkState(this.isRunning);
/* 127 */     this.isRunning = false;
/* 128 */     this.elapsedNanos += tick - this.startTick;
/* 129 */     return this;
/*     */   }
/*     */ 
/*     */   public Stopwatch reset()
/*     */   {
/* 139 */     this.elapsedNanos = 0L;
/* 140 */     this.isRunning = false;
/* 141 */     return this;
/*     */   }
/*     */ 
/*     */   private long elapsedNanos() {
/* 145 */     return this.isRunning ? this.ticker.read() - this.startTick + this.elapsedNanos : this.elapsedNanos;
/*     */   }
/*     */ 
/*     */   public long elapsedTime(TimeUnit desiredUnit)
/*     */   {
/* 157 */     return desiredUnit.convert(elapsedNanos(), TimeUnit.NANOSECONDS);
/*     */   }
/*     */ 
/*     */   public long elapsedMillis()
/*     */   {
/* 166 */     return elapsedTime(TimeUnit.MILLISECONDS);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("String.format()")
/*     */   public String toString()
/*     */   {
/* 175 */     return toString(4);
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("String.format()")
/*     */   public String toString(int significantDigits)
/*     */   {
/* 186 */     long nanos = elapsedNanos();
/*     */ 
/* 188 */     TimeUnit unit = chooseUnit(nanos);
/* 189 */     double value = nanos / TimeUnit.NANOSECONDS.convert(1L, unit);
/*     */ 
/* 192 */     return String.format("%." + significantDigits + "g %s", new Object[] { Double.valueOf(value), abbreviate(unit) });
/*     */   }
/*     */ 
/*     */   private static TimeUnit chooseUnit(long nanos)
/*     */   {
/* 197 */     if (TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
/* 198 */       return TimeUnit.SECONDS;
/*     */     }
/* 200 */     if (TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
/* 201 */       return TimeUnit.MILLISECONDS;
/*     */     }
/* 203 */     if (TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
/* 204 */       return TimeUnit.MICROSECONDS;
/*     */     }
/* 206 */     return TimeUnit.NANOSECONDS;
/*     */   }
/*     */ 
/*     */   private static String abbreviate(TimeUnit unit) {
/* 210 */     switch (1.$SwitchMap$java$util$concurrent$TimeUnit[unit.ordinal()]) {
/*     */     case 1:
/* 212 */       return "ns";
/*     */     case 2:
/* 214 */       return "μs";
/*     */     case 3:
/* 216 */       return "ms";
/*     */     case 4:
/* 218 */       return "s";
/*     */     }
/* 220 */     throw new AssertionError();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Stopwatch
 * JD-Core Version:    0.6.2
 */