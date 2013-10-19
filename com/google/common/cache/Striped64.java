/*     */ package com.google.common.cache;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ import java.util.Random;
/*     */ import sun.misc.Unsafe;
/*     */ 
/*     */ abstract class Striped64 extends Number
/*     */ {
/* 144 */   static final ThreadHashCode threadHashCode = new ThreadHashCode();
/*     */ 
/* 147 */   static final int NCPU = Runtime.getRuntime().availableProcessors();
/*     */   volatile transient Cell[] cells;
/*     */   volatile transient long base;
/*     */   volatile transient int busy;
/*     */   private static final Unsafe UNSAFE;
/*     */   private static final long baseOffset;
/*     */   private static final long busyOffset;
/*     */ 
/*     */   final boolean casBase(long cmp, long val)
/*     */   {
/* 175 */     return UNSAFE.compareAndSwapLong(this, baseOffset, cmp, val);
/*     */   }
/*     */ 
/*     */   final boolean casBusy()
/*     */   {
/* 182 */     return UNSAFE.compareAndSwapInt(this, busyOffset, 0, 1);
/*     */   }
/*     */ 
/*     */   abstract long fn(long paramLong1, long paramLong2);
/*     */ 
/*     */   final void retryUpdate(long x, HashCode hc, boolean wasUncontended)
/*     */   {
/* 208 */     int h = hc.code;
/* 209 */     boolean collide = false;
/*     */     while (true)
/*     */     {
/*     */       Cell[] as;
/*     */       int n;
/* 212 */       if (((as = this.cells) != null) && ((n = as.length) > 0))
/*     */       {
/*     */         Cell a;
/* 213 */         if ((a = as[(n - 1 & h)]) == null) {
/* 214 */           if (this.busy == 0) {
/* 215 */             Cell r = new Cell(x);
/* 216 */             if ((this.busy == 0) && (casBusy())) {
/* 217 */               boolean created = false;
/*     */               try
/*     */               {
/*     */                 Cell[] rs;
/*     */                 int m;
/*     */                 int j;
/* 220 */                 if (((rs = this.cells) != null) && ((m = rs.length) > 0) && (rs[(j = m - 1 & h)] == null))
/*     */                 {
/* 223 */                   rs[j] = r;
/* 224 */                   created = true;
/*     */                 }
/*     */               } finally {
/* 227 */                 this.busy = 0;
/*     */               }
/* 229 */               if (!created) continue;
/* 230 */               break;
/*     */             }
/*     */           }
/*     */           else {
/* 234 */             collide = false;
/*     */           }
/*     */         } else { if (!wasUncontended) {
/* 237 */             wasUncontended = true;
/*     */           }
/*     */           else
/*     */           {
/*     */             long v;
/* 238 */             if (a.cas(v = a.value, fn(v, x)))
/*     */               break;
/* 240 */             if ((n >= NCPU) || (this.cells != as)) {
/* 241 */               collide = false;
/* 242 */             } else if (!collide) {
/* 243 */               collide = true;
/* 244 */             } else if ((this.busy == 0) && (casBusy())) {
/*     */               try {
/* 246 */                 if (this.cells == as) {
/* 247 */                   Cell[] rs = new Cell[n << 1];
/* 248 */                   for (int i = 0; i < n; i++)
/* 249 */                     rs[i] = as[i];
/* 250 */                   this.cells = rs;
/*     */                 }
/*     */               } finally {
/* 253 */                 this.busy = 0;
/*     */               }
/* 255 */               collide = false;
/* 256 */               continue;
/*     */             }
/*     */           }
/* 258 */           h ^= h << 13;
/* 259 */           h ^= h >>> 17;
/* 260 */           h ^= h << 5; }
/*     */       }
/* 262 */       else if ((this.busy == 0) && (this.cells == as) && (casBusy())) {
/* 263 */         boolean init = false;
/*     */         try {
/* 265 */           if (this.cells == as) {
/* 266 */             Cell[] rs = new Cell[2];
/* 267 */             rs[(h & 0x1)] = new Cell(x);
/* 268 */             this.cells = rs;
/* 269 */             init = true;
/*     */           }
/*     */         } finally {
/* 272 */           this.busy = 0;
/*     */         }
/* 274 */         if (init)
/*     */           break;
/*     */       }
/*     */       else
/*     */       {
/*     */         long v;
/* 277 */         if (casBase(v = this.base, fn(v, x))) break;
/*     */       }
/*     */     }
/* 280 */     hc.code = h;
/*     */   }
/*     */ 
/*     */   final void internalReset(long initialValue)
/*     */   {
/* 287 */     Cell[] as = this.cells;
/* 288 */     this.base = initialValue;
/* 289 */     if (as != null) {
/* 290 */       int n = as.length;
/* 291 */       for (int i = 0; i < n; i++) {
/* 292 */         Cell a = as[i];
/* 293 */         if (a != null)
/* 294 */           a.value = initialValue;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static Unsafe getUnsafe()
/*     */   {
/*     */     try
/*     */     {
/* 325 */       return Unsafe.getUnsafe();
/*     */     } catch (SecurityException se) {
/*     */       try {
/* 328 */         return (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction()
/*     */         {
/*     */           public Unsafe run() throws Exception
/*     */           {
/* 332 */             Field f = Unsafe.class.getDeclaredField("theUnsafe");
/*     */ 
/* 334 */             f.setAccessible(true);
/* 335 */             return (Unsafe)f.get(null);
/*     */           } } );
/*     */       } catch (PrivilegedActionException e) {
/* 338 */         throw new RuntimeException("Could not initialize intrinsics", e.getCause());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*     */     try
/*     */     {
/* 305 */       UNSAFE = getUnsafe();
/* 306 */       Class sk = Striped64.class;
/* 307 */       baseOffset = UNSAFE.objectFieldOffset(sk.getDeclaredField("base"));
/*     */ 
/* 309 */       busyOffset = UNSAFE.objectFieldOffset(sk.getDeclaredField("busy"));
/*     */     }
/*     */     catch (Exception e) {
/* 312 */       throw new Error(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class ThreadHashCode extends ThreadLocal<Striped64.HashCode>
/*     */   {
/*     */     public Striped64.HashCode initialValue()
/*     */     {
/* 135 */       return new Striped64.HashCode();
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class HashCode
/*     */   {
/* 123 */     static final Random rng = new Random();
/*     */     int code;
/*     */ 
/*     */     HashCode()
/*     */     {
/* 126 */       int h = rng.nextInt();
/* 127 */       this.code = (h == 0 ? 1 : h);
/*     */     }
/*     */   }
/*     */ 
/*     */   static final class Cell
/*     */   {
/*     */     volatile long p0;
/*     */     volatile long p1;
/*     */     volatile long p2;
/*     */     volatile long p3;
/*     */     volatile long p4;
/*     */     volatile long p5;
/*     */     volatile long p6;
/*     */     volatile long value;
/*     */     volatile long q0;
/*     */     volatile long q1;
/*     */     volatile long q2;
/*     */     volatile long q3;
/*     */     volatile long q4;
/*     */     volatile long q5;
/*     */     volatile long q6;
/*     */     private static final Unsafe UNSAFE;
/*     */     private static final long valueOffset;
/*     */ 
/*     */     Cell(long x)
/*     */     {
/*  96 */       this.value = x;
/*     */     }
/*     */     final boolean cas(long cmp, long val) {
/*  99 */       return UNSAFE.compareAndSwapLong(this, valueOffset, cmp, val);
/*     */     }
/*     */ 
/*     */     static
/*     */     {
/*     */       try
/*     */       {
/* 107 */         UNSAFE = Striped64.access$000();
/* 108 */         Class ak = Cell.class;
/* 109 */         valueOffset = UNSAFE.objectFieldOffset(ak.getDeclaredField("value"));
/*     */       }
/*     */       catch (Exception e) {
/* 112 */         throw new Error(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.cache.Striped64
 * JD-Core Version:    0.6.2
 */