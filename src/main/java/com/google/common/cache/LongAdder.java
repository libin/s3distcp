/*     */ package com.google.common.cache;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ class LongAdder extends Striped64
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 7249069246863182397L;
/*     */ 
/*     */   final long fn(long v, long x)
/*     */   {
/*  52 */     return v + x;
/*     */   }
/*     */ 
/*     */   public void add(long x)
/*     */   {
/*     */     Striped64.Cell[] as;
/*     */     long b;
/*  67 */     if (((as = this.cells) != null) || (!casBase(b = this.base, b + x))) {
/*  68 */       boolean uncontended = true;
/*     */       Striped64.HashCode hc;
/*  69 */       int h = (hc = (Striped64.HashCode)threadHashCode.get()).code;
/*     */       int n;
/*     */       Striped64.Cell a;
/*     */       long v;
/*  70 */       if ((as == null) || ((n = as.length) < 1) || ((a = as[(n - 1 & h)]) == null) || (!(uncontended = a.cas(v = a.value, v + x))))
/*     */       {
/*  73 */         retryUpdate(x, hc, uncontended);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void increment()
/*     */   {
/*  81 */     add(1L);
/*     */   }
/*     */ 
/*     */   public void decrement()
/*     */   {
/*  88 */     add(-1L);
/*     */   }
/*     */ 
/*     */   public long sum()
/*     */   {
/* 101 */     long sum = this.base;
/* 102 */     Striped64.Cell[] as = this.cells;
/* 103 */     if (as != null) {
/* 104 */       int n = as.length;
/* 105 */       for (int i = 0; i < n; i++) {
/* 106 */         Striped64.Cell a = as[i];
/* 107 */         if (a != null)
/* 108 */           sum += a.value;
/*     */       }
/*     */     }
/* 111 */     return sum;
/*     */   }
/*     */ 
/*     */   public void reset()
/*     */   {
/* 122 */     internalReset(0L);
/*     */   }
/*     */ 
/*     */   public long sumThenReset()
/*     */   {
/* 136 */     long sum = this.base;
/* 137 */     Striped64.Cell[] as = this.cells;
/* 138 */     this.base = 0L;
/* 139 */     if (as != null) {
/* 140 */       int n = as.length;
/* 141 */       for (int i = 0; i < n; i++) {
/* 142 */         Striped64.Cell a = as[i];
/* 143 */         if (a != null) {
/* 144 */           sum += a.value;
/* 145 */           a.value = 0L;
/*     */         }
/*     */       }
/*     */     }
/* 149 */     return sum;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 157 */     return Long.toString(sum());
/*     */   }
/*     */ 
/*     */   public long longValue()
/*     */   {
/* 166 */     return sum();
/*     */   }
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 174 */     return (int)sum();
/*     */   }
/*     */ 
/*     */   public float floatValue()
/*     */   {
/* 182 */     return (float)sum();
/*     */   }
/*     */ 
/*     */   public double doubleValue()
/*     */   {
/* 190 */     return sum();
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream s) throws IOException
/*     */   {
/* 195 */     s.defaultWriteObject();
/* 196 */     s.writeLong(sum());
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException
/*     */   {
/* 201 */     s.defaultReadObject();
/* 202 */     this.busy = 0;
/* 203 */     this.cells = null;
/* 204 */     this.base = s.readLong();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.LongAdder
 * JD-Core Version:    0.6.2
 */