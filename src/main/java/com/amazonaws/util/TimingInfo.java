/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ 
/*     */ public class TimingInfo
/*     */ {
/*     */   private final long startTime;
/*     */   private long endTime;
/*  27 */   private final Map<String, List<TimingInfo>> subMeasurementsByName = new HashMap();
/*  28 */   private final Map<String, Number> countersByName = new HashMap();
/*     */ 
/*     */   public TimingInfo() {
/*  31 */     this(System.currentTimeMillis(), -1L);
/*     */   }
/*     */ 
/*     */   public TimingInfo(long startTime) {
/*  35 */     this(startTime, -1L);
/*     */   }
/*     */ 
/*     */   public TimingInfo(long startTime, long endTime) {
/*  39 */     this.startTime = startTime;
/*  40 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public long getStartTime()
/*     */   {
/*  49 */     return this.startTime;
/*     */   }
/*     */ 
/*     */   public long getEndTime() {
/*  53 */     return this.endTime;
/*     */   }
/*     */ 
/*     */   public double getTimeTakenMillis()
/*     */   {
/*  60 */     return TimeUnit.NANOSECONDS.toMicros(this.endTime - this.startTime) / 1000.0D;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  65 */     return String.valueOf(getTimeTakenMillis());
/*     */   }
/*     */ 
/*     */   public void setEndTime(long endTime) {
/*  69 */     this.endTime = endTime;
/*     */   }
/*     */ 
/*     */   public void addSubMeasurement(String subMeasurementName, TimingInfo timingInfo)
/*     */   {
/*  74 */     List timings = (List)this.subMeasurementsByName.get(subMeasurementName);
/*  75 */     if (timings == null) {
/*  76 */       timings = new ArrayList();
/*  77 */       this.subMeasurementsByName.put(subMeasurementName, timings);
/*     */     }
/*     */ 
/*  80 */     timings.add(timingInfo);
/*     */   }
/*     */ 
/*     */   public TimingInfo getSubMeasurement(String subMeasurementName) {
/*  84 */     return getSubMeasurement(subMeasurementName, 0);
/*     */   }
/*     */ 
/*     */   public TimingInfo getSubMeasurement(String subMesurementName, int index)
/*     */   {
/*  89 */     List timings = (List)this.subMeasurementsByName.get(subMesurementName);
/*  90 */     if ((index < 0) || (timings == null) || (timings.size() == 0) || (index >= timings.size()))
/*     */     {
/*  92 */       return null;
/*     */     }
/*     */ 
/*  95 */     return (TimingInfo)timings.get(index);
/*     */   }
/*     */ 
/*     */   public TimingInfo getLastSubMeasurement(String subMeasurementName)
/*     */   {
/* 100 */     if ((this.subMeasurementsByName == null) || (this.subMeasurementsByName.size() == 0)) {
/* 101 */       return null;
/*     */     }
/*     */ 
/* 104 */     return getSubMeasurement(subMeasurementName, this.subMeasurementsByName.size() - 1);
/*     */   }
/*     */ 
/*     */   public List<TimingInfo> getAllSubMeasurements(String subMeasurementName) {
/* 108 */     return (List)this.subMeasurementsByName.get(subMeasurementName);
/*     */   }
/*     */ 
/*     */   public Map<String, List<TimingInfo>> getSubMeasurementsByName() {
/* 112 */     return this.subMeasurementsByName;
/*     */   }
/*     */ 
/*     */   public Number getCounter(String key) {
/* 116 */     return (Number)this.countersByName.get(key);
/*     */   }
/*     */ 
/*     */   public Map<String, Number> getAllCounters() {
/* 120 */     return this.countersByName;
/*     */   }
/*     */ 
/*     */   public void setCounter(String key, long count) {
/* 124 */     this.countersByName.put(key, Long.valueOf(count));
/*     */   }
/*     */ 
/*     */   public void incrementCounter(String key)
/*     */   {
/* 129 */     int count = 0;
/* 130 */     Number counter = getCounter(key);
/*     */ 
/* 132 */     if (counter != null) {
/* 133 */       count = counter.intValue();
/*     */     }
/*     */ 
/* 136 */     setCounter(key, ++count);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.TimingInfo
 * JD-Core Version:    0.6.2
 */