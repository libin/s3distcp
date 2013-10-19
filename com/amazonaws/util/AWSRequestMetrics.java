/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ 
/*     */ public class AWSRequestMetrics
/*     */ {
/*     */   private final TimingInfo timingInfo;
/*     */   private final boolean profilingSystemPropertyEnabled;
/*  58 */   private final Map<String, List<Object>> properties = new HashMap();
/*     */ 
/*  61 */   private final Map<String, Long> eventsBeingProfiled = new HashMap();
/*     */ 
/*  63 */   private static final Log latencyLogger = LogFactory.getLog("com.amazonaws.latency");
/*  64 */   private static final Object KEY_VALUE_SEPARATOR = "=";
/*  65 */   private static final Object COMMA_SEPARATOR = ", ";
/*     */ 
/*     */   public AWSRequestMetrics()
/*     */   {
/*  69 */     this.timingInfo = new TimingInfo();
/*  70 */     this.profilingSystemPropertyEnabled = isProfilingEnabled();
/*     */   }
/*     */ 
/*     */   private static boolean isProfilingEnabled()
/*     */   {
/*  75 */     return System.getProperty("com.amazonaws.sdk.enableRuntimeProfiling") != null;
/*     */   }
/*     */ 
/*     */   public void startEvent(String eventName)
/*     */   {
/*  90 */     if (this.profilingSystemPropertyEnabled)
/*     */     {
/*  92 */       this.eventsBeingProfiled.put(eventName, Long.valueOf(System.nanoTime()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void endEvent(String eventName)
/*     */   {
/* 105 */     if (this.profilingSystemPropertyEnabled) {
/* 106 */       Long startTime = (Long)this.eventsBeingProfiled.get(eventName);
/*     */ 
/* 108 */       if (startTime == null) {
/* 109 */         throw new IllegalStateException(new StringBuilder().append("Trying to end an event which was never started. ").append(eventName).toString());
/*     */       }
/*     */ 
/* 112 */       this.timingInfo.addSubMeasurement(eventName, new TimingInfo(startTime.longValue(), System.nanoTime()));
/*     */     }
/*     */   }
/*     */ 
/*     */   public void incrementCounter(String event)
/*     */   {
/* 125 */     if (this.profilingSystemPropertyEnabled)
/* 126 */       this.timingInfo.incrementCounter(event);
/*     */   }
/*     */ 
/*     */   public void setCounter(String counterName, long count)
/*     */   {
/* 131 */     if (this.profilingSystemPropertyEnabled)
/* 132 */       this.timingInfo.setCounter(counterName, count);
/*     */   }
/*     */ 
/*     */   public void addProperty(String propertyName, Object value)
/*     */   {
/* 143 */     List propertyList = (List)this.properties.get(propertyName);
/* 144 */     if (propertyList == null) {
/* 145 */       propertyList = new ArrayList();
/* 146 */       this.properties.put(propertyName, propertyList);
/*     */     }
/*     */ 
/* 149 */     propertyList.add(value);
/*     */   }
/*     */ 
/*     */   public void log() {
/* 153 */     if (!this.profilingSystemPropertyEnabled) {
/* 154 */       return;
/*     */     }
/*     */ 
/* 157 */     StringBuilder builder = new StringBuilder();
/*     */ 
/* 159 */     for (Map.Entry entry : this.properties.entrySet()) {
/* 160 */       keyValueFormat(entry.getKey(), entry.getValue(), builder);
/*     */     }
/*     */ 
/* 163 */     for (Map.Entry entry : this.timingInfo.getAllCounters().entrySet()) {
/* 164 */       keyValueFormat(entry.getKey(), entry.getValue(), builder);
/*     */     }
/*     */ 
/* 167 */     for (Map.Entry entry : this.timingInfo.getSubMeasurementsByName().entrySet()) {
/* 168 */       keyValueFormat(entry.getKey(), entry.getValue(), builder);
/*     */     }
/*     */ 
/* 171 */     latencyLogger.info(builder.toString());
/*     */   }
/*     */ 
/*     */   private void keyValueFormat(Object key, Object value, StringBuilder builder) {
/* 175 */     builder.append(key).append(KEY_VALUE_SEPARATOR).append(value).append(COMMA_SEPARATOR);
/*     */   }
/*     */ 
/*     */   public TimingInfo getTimingInfo() {
/* 179 */     return this.timingInfo;
/*     */   }
/*     */ 
/*     */   public static enum Field
/*     */   {
/*  35 */     StatusCode, 
/*  36 */     AWSErrorCode, 
/*  37 */     AWSRequestID, 
/*  38 */     BytesProcessed, 
/*  39 */     AttemptCount, 
/*  40 */     ResponseProcessingTime, 
/*  41 */     ClientExecuteTime, 
/*  42 */     RequestSigningTime, 
/*  43 */     HttpRequestTime, 
/*  44 */     RequestMarshallTime, 
/*  45 */     RetryPauseTime, 
/*  46 */     RedirectLocation, 
/*  47 */     Exception, 
/*  48 */     CredentialsRequestTime, 
/*  49 */     ServiceEndpoint, 
/*  50 */     ServiceName;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.AWSRequestMetrics
 * JD-Core Version:    0.6.2
 */