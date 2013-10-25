/*    */ package com.google.gson.internal;
/*    */ 
/*    */ public final class $Gson$Preconditions
/*    */ {
/*    */   public static <T> T checkNotNull(T obj)
/*    */   {
/* 34 */     if (obj == null) {
/* 35 */       throw new NullPointerException();
/*    */     }
/* 37 */     return obj;
/*    */   }
/*    */ 
/*    */   public static void checkArgument(boolean condition) {
/* 41 */     if (!condition)
/* 42 */       throw new IllegalArgumentException();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal..Gson.Preconditions
 * JD-Core Version:    0.6.2
 */