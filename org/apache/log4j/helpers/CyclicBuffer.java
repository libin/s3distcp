/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class CyclicBuffer
/*     */ {
/*     */   LoggingEvent[] ea;
/*     */   int first;
/*     */   int last;
/*     */   int numElems;
/*     */   int maxSize;
/*     */ 
/*     */   public CyclicBuffer(int maxSize)
/*     */     throws IllegalArgumentException
/*     */   {
/*  50 */     if (maxSize < 1) {
/*  51 */       throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
/*     */     }
/*     */ 
/*  54 */     this.maxSize = maxSize;
/*  55 */     this.ea = new LoggingEvent[maxSize];
/*  56 */     this.first = 0;
/*  57 */     this.last = 0;
/*  58 */     this.numElems = 0;
/*     */   }
/*     */ 
/*     */   public void add(LoggingEvent event)
/*     */   {
/*  67 */     this.ea[this.last] = event;
/*  68 */     if (++this.last == this.maxSize) {
/*  69 */       this.last = 0;
/*     */     }
/*  71 */     if (this.numElems < this.maxSize)
/*  72 */       this.numElems += 1;
/*  73 */     else if (++this.first == this.maxSize)
/*  74 */       this.first = 0;
/*     */   }
/*     */ 
/*     */   public LoggingEvent get(int i)
/*     */   {
/*  87 */     if ((i < 0) || (i >= this.numElems)) {
/*  88 */       return null;
/*     */     }
/*  90 */     return this.ea[((this.first + i) % this.maxSize)];
/*     */   }
/*     */ 
/*     */   public int getMaxSize()
/*     */   {
/*  95 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   public LoggingEvent get()
/*     */   {
/* 104 */     LoggingEvent r = null;
/* 105 */     if (this.numElems > 0) {
/* 106 */       this.numElems -= 1;
/* 107 */       r = this.ea[this.first];
/* 108 */       this.ea[this.first] = null;
/* 109 */       if (++this.first == this.maxSize)
/* 110 */         this.first = 0;
/*     */     }
/* 112 */     return r;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 122 */     return this.numElems;
/*     */   }
/*     */ 
/*     */   public void resize(int newSize)
/*     */   {
/* 132 */     if (newSize < 0) {
/* 133 */       throw new IllegalArgumentException("Negative array size [" + newSize + "] not allowed.");
/*     */     }
/*     */ 
/* 136 */     if (newSize == this.numElems) {
/* 137 */       return;
/*     */     }
/* 139 */     LoggingEvent[] temp = new LoggingEvent[newSize];
/*     */ 
/* 141 */     int loopLen = newSize < this.numElems ? newSize : this.numElems;
/*     */ 
/* 143 */     for (int i = 0; i < loopLen; i++) {
/* 144 */       temp[i] = this.ea[this.first];
/* 145 */       this.ea[this.first] = null;
/* 146 */       if (++this.first == this.numElems)
/* 147 */         this.first = 0;
/*     */     }
/* 149 */     this.ea = temp;
/* 150 */     this.first = 0;
/* 151 */     this.numElems = loopLen;
/* 152 */     this.maxSize = newSize;
/* 153 */     if (loopLen == newSize)
/* 154 */       this.last = 0;
/*     */     else
/* 156 */       this.last = loopLen;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.CyclicBuffer
 * JD-Core Version:    0.6.2
 */