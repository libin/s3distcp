/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ @GwtCompatible(serializable=true)
/*    */ final class UsingToStringOrdering extends Ordering<Object>
/*    */   implements Serializable
/*    */ {
/* 27 */   static final UsingToStringOrdering INSTANCE = new UsingToStringOrdering();
/*    */   private static final long serialVersionUID = 0L;
/*    */ 
/*    */   public int compare(Object left, Object right)
/*    */   {
/* 30 */     return left.toString().compareTo(right.toString());
/*    */   }
/*    */ 
/*    */   private Object readResolve()
/*    */   {
/* 35 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 39 */     return "Ordering.usingToString()";
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.UsingToStringOrdering
 * JD-Core Version:    0.6.2
 */