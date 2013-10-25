/*    */ package com.google.common.hash;
/*    */ 
/*    */ import com.google.common.annotations.Beta;
/*    */ 
/*    */ @Beta
/*    */ public final class Funnels
/*    */ {
/*    */   public static Funnel<byte[]> byteArrayFunnel()
/*    */   {
/* 33 */     return ByteArrayFunnel.INSTANCE;
/*    */   }
/*    */ 
/*    */   public static Funnel<CharSequence> stringFunnel()
/*    */   {
/* 52 */     return StringFunnel.INSTANCE;
/*    */   }
/*    */ 
/*    */   private static enum StringFunnel implements Funnel<CharSequence> {
/* 56 */     INSTANCE;
/*    */ 
/*    */     public void funnel(CharSequence from, PrimitiveSink into) {
/* 59 */       into.putString(from);
/*    */     }
/*    */ 
/*    */     public String toString() {
/* 63 */       return "Funnels.stringFunnel()";
/*    */     }
/*    */   }
/*    */ 
/*    */   private static enum ByteArrayFunnel
/*    */     implements Funnel<byte[]>
/*    */   {
/* 37 */     INSTANCE;
/*    */ 
/*    */     public void funnel(byte[] from, PrimitiveSink into) {
/* 40 */       into.putBytes(from);
/*    */     }
/*    */ 
/*    */     public String toString() {
/* 44 */       return "Funnels.byteArrayFunnel()";
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.Funnels
 * JD-Core Version:    0.6.2
 */