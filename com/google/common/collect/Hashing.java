/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ 
/*    */ @GwtCompatible
/*    */ final class Hashing
/*    */ {
/*    */   static int smear(int hashCode)
/*    */   {
/* 40 */     hashCode ^= hashCode >>> 20 ^ hashCode >>> 12;
/* 41 */     return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Hashing
 * JD-Core Version:    0.6.2
 */