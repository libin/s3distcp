/*     */ package org.apache.log4j.helpers;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class BoundedFIFO
/*     */ {
/*     */   LoggingEvent[] buf;
/*  34 */   int numElements = 0;
/*  35 */   int first = 0;
/*  36 */   int next = 0;
/*     */   int maxSize;
/*     */ 
/*     */   public BoundedFIFO(int maxSize)
/*     */   {
/*  44 */     if (maxSize < 1) {
/*  45 */       throw new IllegalArgumentException("The maxSize argument (" + maxSize + ") is not a positive integer.");
/*     */     }
/*     */ 
/*  48 */     this.maxSize = maxSize;
/*  49 */     this.buf = new LoggingEvent[maxSize];
/*     */   }
/*     */ 
/*     */   public LoggingEvent get()
/*     */   {
/*  57 */     if (this.numElements == 0) {
/*  58 */       return null;
/*     */     }
/*  60 */     LoggingEvent r = this.buf[this.first];
/*  61 */     this.buf[this.first] = null;
/*     */ 
/*  63 */     if (++this.first == this.maxSize) {
/*  64 */       this.first = 0;
/*     */     }
/*  66 */     this.numElements -= 1;
/*  67 */     return r;
/*     */   }
/*     */ 
/*     */   public void put(LoggingEvent o)
/*     */   {
/*  76 */     if (this.numElements != this.maxSize) {
/*  77 */       this.buf[this.next] = o;
/*  78 */       if (++this.next == this.maxSize) {
/*  79 */         this.next = 0;
/*     */       }
/*  81 */       this.numElements += 1;
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getMaxSize()
/*     */   {
/*  90 */     return this.maxSize;
/*     */   }
/*     */ 
/*     */   public boolean isFull()
/*     */   {
/*  98 */     return this.numElements == this.maxSize;
/*     */   }
/*     */ 
/*     */   public int length()
/*     */   {
/* 108 */     return this.numElements;
/*     */   }
/*     */ 
/*     */   int min(int a, int b)
/*     */   {
/* 113 */     return a < b ? a : b;
/*     */   }
/*     */ 
/*     */   public synchronized void resize(int newSize)
/*     */   {
/* 126 */     if (newSize == this.maxSize) {
/* 127 */       return;
/*     */     }
/*     */ 
/* 130 */     LoggingEvent[] tmp = new LoggingEvent[newSize];
/*     */ 
/* 133 */     int len1 = this.maxSize - this.first;
/*     */ 
/* 136 */     len1 = min(len1, newSize);
/*     */ 
/* 140 */     len1 = min(len1, this.numElements);
/*     */ 
/* 143 */     System.arraycopy(this.buf, this.first, tmp, 0, len1);
/*     */ 
/* 146 */     int len2 = 0;
/* 147 */     if ((len1 < this.numElements) && (len1 < newSize)) {
/* 148 */       len2 = this.numElements - len1;
/* 149 */       len2 = min(len2, newSize - len1);
/* 150 */       System.arraycopy(this.buf, 0, tmp, len1, len2);
/*     */     }
/*     */ 
/* 153 */     this.buf = tmp;
/* 154 */     this.maxSize = newSize;
/* 155 */     this.first = 0;
/* 156 */     this.numElements = (len1 + len2);
/* 157 */     this.next = this.numElements;
/* 158 */     if (this.next == this.maxSize)
/* 159 */       this.next = 0;
/*     */   }
/*     */ 
/*     */   public boolean wasEmpty()
/*     */   {
/* 169 */     return this.numElements == 1;
/*     */   }
/*     */ 
/*     */   public boolean wasFull()
/*     */   {
/* 178 */     return this.numElements + 1 == this.maxSize;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.BoundedFIFO
 * JD-Core Version:    0.6.2
 */