/*     */ package com.google.common.util.concurrent;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import java.io.IOException;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.util.concurrent.atomic.AtomicLongFieldUpdater;
/*     */ 
/*     */ @Beta
/*     */ public class AtomicDouble extends Number
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 0L;
/*     */   private volatile transient long value;
/*  63 */   private static final AtomicLongFieldUpdater<AtomicDouble> updater = AtomicLongFieldUpdater.newUpdater(AtomicDouble.class, "value");
/*     */ 
/*     */   public AtomicDouble(double initialValue)
/*     */   {
/*  72 */     this.value = Double.doubleToRawLongBits(initialValue);
/*     */   }
/*     */ 
/*     */   public AtomicDouble()
/*     */   {
/*     */   }
/*     */ 
/*     */   public final double get()
/*     */   {
/*  88 */     return Double.longBitsToDouble(this.value);
/*     */   }
/*     */ 
/*     */   public final void set(double newValue)
/*     */   {
/*  97 */     long next = Double.doubleToRawLongBits(newValue);
/*  98 */     this.value = next;
/*     */   }
/*     */ 
/*     */   public final void lazySet(double newValue)
/*     */   {
/* 107 */     set(newValue);
/*     */   }
/*     */ 
/*     */   public final double getAndSet(double newValue)
/*     */   {
/* 120 */     long next = Double.doubleToRawLongBits(newValue);
/* 121 */     return Double.longBitsToDouble(updater.getAndSet(this, next));
/*     */   }
/*     */ 
/*     */   public final boolean compareAndSet(double expect, double update)
/*     */   {
/* 135 */     return updater.compareAndSet(this, Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
/*     */   }
/*     */ 
/*     */   public final boolean weakCompareAndSet(double expect, double update)
/*     */   {
/* 156 */     return updater.weakCompareAndSet(this, Double.doubleToRawLongBits(expect), Double.doubleToRawLongBits(update));
/*     */   }
/*     */ 
/*     */   public final double getAndAdd(double delta)
/*     */   {
/*     */     while (true)
/*     */     {
/* 169 */       long current = this.value;
/* 170 */       double currentVal = Double.longBitsToDouble(current);
/* 171 */       double nextVal = currentVal + delta;
/* 172 */       long next = Double.doubleToRawLongBits(nextVal);
/* 173 */       if (updater.compareAndSet(this, current, next))
/* 174 */         return currentVal;
/*     */     }
/*     */   }
/*     */ 
/*     */   public final double addAndGet(double delta)
/*     */   {
/*     */     while (true)
/*     */     {
/* 187 */       long current = this.value;
/* 188 */       double currentVal = Double.longBitsToDouble(current);
/* 189 */       double nextVal = currentVal + delta;
/* 190 */       long next = Double.doubleToRawLongBits(nextVal);
/* 191 */       if (updater.compareAndSet(this, current, next))
/* 192 */         return nextVal;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 202 */     return Double.toString(get());
/*     */   }
/*     */ 
/*     */   public int intValue()
/*     */   {
/* 210 */     return (int)get();
/*     */   }
/*     */ 
/*     */   public long longValue()
/*     */   {
/* 218 */     return ()get();
/*     */   }
/*     */ 
/*     */   public float floatValue()
/*     */   {
/* 226 */     return (float)get();
/*     */   }
/*     */ 
/*     */   public double doubleValue()
/*     */   {
/* 233 */     return get();
/*     */   }
/*     */ 
/*     */   private void writeObject(ObjectOutputStream s)
/*     */     throws IOException
/*     */   {
/* 243 */     s.defaultWriteObject();
/*     */ 
/* 245 */     s.writeDouble(get());
/*     */   }
/*     */ 
/*     */   private void readObject(ObjectInputStream s)
/*     */     throws IOException, ClassNotFoundException
/*     */   {
/* 253 */     s.defaultReadObject();
/*     */ 
/* 255 */     set(s.readDouble());
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.util.concurrent.AtomicDouble
 * JD-Core Version:    0.6.2
 */