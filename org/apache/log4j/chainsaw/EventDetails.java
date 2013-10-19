/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import org.apache.log4j.Priority;
/*     */ import org.apache.log4j.spi.LocationInfo;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ class EventDetails
/*     */ {
/*     */   private final long mTimeStamp;
/*     */   private final Priority mPriority;
/*     */   private final String mCategoryName;
/*     */   private final String mNDC;
/*     */   private final String mThreadName;
/*     */   private final String mMessage;
/*     */   private final String[] mThrowableStrRep;
/*     */   private final String mLocationDetails;
/*     */ 
/*     */   EventDetails(long aTimeStamp, Priority aPriority, String aCategoryName, String aNDC, String aThreadName, String aMessage, String[] aThrowableStrRep, String aLocationDetails)
/*     */   {
/*  68 */     this.mTimeStamp = aTimeStamp;
/*  69 */     this.mPriority = aPriority;
/*  70 */     this.mCategoryName = aCategoryName;
/*  71 */     this.mNDC = aNDC;
/*  72 */     this.mThreadName = aThreadName;
/*  73 */     this.mMessage = aMessage;
/*  74 */     this.mThrowableStrRep = aThrowableStrRep;
/*  75 */     this.mLocationDetails = aLocationDetails;
/*     */   }
/*     */ 
/*     */   EventDetails(LoggingEvent aEvent)
/*     */   {
/*  85 */     this(aEvent.timeStamp, aEvent.getLevel(), aEvent.getLoggerName(), aEvent.getNDC(), aEvent.getThreadName(), aEvent.getRenderedMessage(), aEvent.getThrowableStrRep(), aEvent.getLocationInformation() == null ? null : aEvent.getLocationInformation().fullInfo);
/*     */   }
/*     */ 
/*     */   long getTimeStamp()
/*     */   {
/*  98 */     return this.mTimeStamp;
/*     */   }
/*     */ 
/*     */   Priority getPriority()
/*     */   {
/* 103 */     return this.mPriority;
/*     */   }
/*     */ 
/*     */   String getCategoryName()
/*     */   {
/* 108 */     return this.mCategoryName;
/*     */   }
/*     */ 
/*     */   String getNDC()
/*     */   {
/* 113 */     return this.mNDC;
/*     */   }
/*     */ 
/*     */   String getThreadName()
/*     */   {
/* 118 */     return this.mThreadName;
/*     */   }
/*     */ 
/*     */   String getMessage()
/*     */   {
/* 123 */     return this.mMessage;
/*     */   }
/*     */ 
/*     */   String getLocationDetails()
/*     */   {
/* 128 */     return this.mLocationDetails;
/*     */   }
/*     */ 
/*     */   String[] getThrowableStrRep()
/*     */   {
/* 133 */     return this.mThrowableStrRep;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.chainsaw.EventDetails
 * JD-Core Version:    0.6.2
 */