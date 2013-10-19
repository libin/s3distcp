/*     */ package com.google.common.eventbus;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.lang.reflect.InvocationTargetException;
/*     */ import java.lang.reflect.Method;
/*     */ 
/*     */ class EventHandler
/*     */ {
/*     */   private final Object target;
/*     */   private final Method method;
/*     */ 
/*     */   EventHandler(Object target, Method method)
/*     */   {
/*  50 */     Preconditions.checkNotNull(target, "EventHandler target cannot be null.");
/*     */ 
/*  52 */     Preconditions.checkNotNull(method, "EventHandler method cannot be null.");
/*     */ 
/*  54 */     this.target = target;
/*  55 */     this.method = method;
/*  56 */     method.setAccessible(true);
/*     */   }
/*     */ 
/*     */   public void handleEvent(Object event)
/*     */     throws InvocationTargetException
/*     */   {
/*     */     try
/*     */     {
/*  69 */       this.method.invoke(this.target, new Object[] { event });
/*     */     } catch (IllegalArgumentException e) {
/*  71 */       throw new Error("Method rejected target/argument: " + event, e);
/*     */     } catch (IllegalAccessException e) {
/*  73 */       throw new Error("Method became inaccessible: " + event, e);
/*     */     } catch (InvocationTargetException e) {
/*  75 */       if ((e.getCause() instanceof Error)) {
/*  76 */         throw ((Error)e.getCause());
/*     */       }
/*  78 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String toString() {
/*  83 */     return "[wrapper " + this.method + "]";
/*     */   }
/*     */ 
/*     */   public int hashCode() {
/*  87 */     int PRIME = 31;
/*  88 */     return (31 + this.method.hashCode()) * 31 + this.target.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj) {
/*  92 */     if (this == obj) {
/*  93 */       return true;
/*     */     }
/*     */ 
/*  96 */     if (obj == null) {
/*  97 */       return false;
/*     */     }
/*     */ 
/* 100 */     if (getClass() != obj.getClass()) {
/* 101 */       return false;
/*     */     }
/*     */ 
/* 104 */     EventHandler other = (EventHandler)obj;
/*     */ 
/* 106 */     return (this.method.equals(other.method)) && (this.target == other.target);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.eventbus.EventHandler
 * JD-Core Version:    0.6.2
 */