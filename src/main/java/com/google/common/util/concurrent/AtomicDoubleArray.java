/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.concurrent.atomic.AtomicLongArray;
/*     */ 
/*     */ @Beta
/*     */ public class AtomicDoubleArray
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   private transient AtomicLongArray longs;
/*     */ 
/*     */   public AtomicDoubleArray(int length)
/*     */   {
/*  59 */     this.longs = new AtomicLongArray(length);
/*     */   }
/*     */ 
/*     */   public AtomicDoubleArray(double[] array)
/*     */   {
/*  70 */     int len = array.length;
/*  71 */     long[] longArray = new long[len];
/*  72 */     for (int i = 0; i < len; i++) {
/*  73 */       longArray[i] = Double.doubleToRawLongBits(array[i]);
/*     */     }
/*  75 */     this.longs = new AtomicLongArray(longArray);
/*     */   }
/*     */ 
/*     */   public final int length()
/*     */   {
/*  84 */     return this.longs.length();
/*     */   }
/*     */ 
/*     */   public final double get(int i)
/*     */   {
/*  94 */     return Double.longBitsToDouble(this.longs.get(i));
/*     */   }
/*     */ 
/*     */   public final void set(int i, double newValue)
/*     */   {
/* 104 */     long next = Double.doubleToRawLongBits(newValue);
/* 105 */     this.longs.set(i, next);
/*     */   }
/*     */ 
/*     */   public final void lazySet(int i, double newValue)
/*     */   {
/* 115 */     set(i, newValue);
/*     */   }
/*     */ 
/*     */   public final double getAndSet(int i, double newValue)
/*     */   {
/* 130 */     long next = Double.doubleToRawLongBits(newValue);
/* 131 */     return Double.longBitsToDouble(this.longs.getAndSet(i, next));
/*     */   }
/*     */ 
/*     */   public final boolean compareAndSet(int i, double expect, double update)
/*     */   {
/* 147 */     return this.longs.compareAndSet(i, Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
/*     */   }
/*     */ 
/*     */   public final boolean weakCompareAndSet(int i, double expect, double update)
/*     */   {
/* 170 */     return this.longs.weakCompareAndSet(i, Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
/*     */   }
/*     */ 
/*     */   public final double getAndAdd(int i, double delta)
/*     */   {
/*     */     while (true)
/*     */     {
/* 184 */       long current = this.longs.get(i);
/* 185 */       double currentVal = Double.longBitsToDouble(current);
/* 186 */       double nextVal = currentVal + delta;
/* 187 */       long next = Double.doubleToRawLongBits(nextVal);
/* 188 */       if (this.longs.compareAndSet(i, current, next))
/* 189 */         return currentVal;
/*     */     }
/*     */   }
/*     */ 
/*     */   public double addAndGet(int i, double delta)
/*     */   {
/*     */     while (true)
/*     */     {
/* 203 */       long current = this.longs.get(i);
/* 204 */       double currentVal = Double.longBitsToDouble(current);
/* 205 */       double nextVal = currentVal + delta;
/* 206 */       long next = Double.doubleToRawLongBits(nextVal);
/* 207 */       if (this.longs.compareAndSet(i, current, next))
/* 208 */         return nextVal;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 218 */     int iMax = length() - 1;
/* 219 */     if (iMax == -1) {
/* 220 */       return "[]";
/*     */     }
/*     */ 
/* 224 */     StringBuilder b = new StringBuilder(19 * (iMax + 1));
/* 225 */     b.append('[');
/* 226 */     for (int i = 0; ; i++) {
/* 227 */       b.append(Double.longBitsToDouble(this.longs.get(i)));
/* 228 */       if (i == iMax) {
/* 229 */         return b.append(']').toString();
/*     */       }
/* 231 */       b.append(',').append(' ');
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream s)
/*     */     throws IOException
/*     */   {
/* 243 */     s.defaultWriteObject();
/*     */ 
/* 246 */     int length = length();
/* 247 */     s.writeInt(length);
/*     */ 
/* 250 */     for (int i = 0; i < length; i++)
/* 251 */       s.writeDouble(get(i));
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 260 */     s.defaultReadObject();
/*     */ 
/* 263 */     int length = s.readInt();
/* 264 */     this.longs = new AtomicLongArray(length);
/*     */ 
/* 267 */     for (int i = 0; i < length; i++)
/* 268 */       set(i, s.readDouble());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AtomicDoubleArray
 * JD-Core Version:    0.6.2
 */