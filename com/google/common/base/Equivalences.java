/*    */ package com.google.common.base;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ @Beta
/*    */ @GwtCompatible
/*    */ public final class Equivalences
/*    */ {
/*    */   public static Equivalence<Object> equals()
/*    */   {
/* 49 */     return Equals.INSTANCE;
/*    */   }
/*    */ 
/*    */   public static Equivalence<Object> identity()
/*    */   {
/* 58 */     return Identity.INSTANCE;
/*    */   }
/*    */ 
/*    */   private static final class Identity extends Equivalence<Object>
/*    */     implements Serializable
/*    */   {
/* 82 */     static final Identity INSTANCE = new Identity();
/*    */     private static final long serialVersionUID = 1L;
/*    */ 
/*    */     protected boolean doEquivalent(Object a, Object b)
/*    */     {
/* 85 */       return false;
/*    */     }
/*    */ 
/*    */     protected int doHash(Object o) {
/* 89 */       return System.identityHashCode(o);
/*    */     }
/*    */ 
/*    */     private Object readResolve() {
/* 93 */       return INSTANCE;
/*    */     }
/*    */   }
/*    */ 
/*    */   private static final class Equals extends Equivalence<Object>
/*    */     implements Serializable
/*    */   {
/* 64 */     static final Equals INSTANCE = new Equals();
/*    */     private static final long serialVersionUID = 1L;
/*    */ 
/*    */     protected boolean doEquivalent(Object a, Object b)
/*    */     {
/* 67 */       return a.equals(b);
/*    */     }
/*    */     public int doHash(Object o) {
/* 70 */       return o.hashCode();
/*    */     }
/*    */ 
/*    */     private Object readResolve() {
/* 74 */       return INSTANCE;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Equivalences
 * JD-Core Version:    0.6.2
 */