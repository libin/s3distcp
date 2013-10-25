/*    */ package com.google.common.hash;
/*    */ 
/*    */ import com.google.common.base.Charsets;
/*    */ import java.nio.charset.Charset;
/*    */ 
/*    */ abstract class AbstractHasher
/*    */   implements Hasher
/*    */ {
/*    */   public final Hasher putBoolean(boolean b)
/*    */   {
/* 30 */     return putByte((byte)(b ? 1 : 0));
/*    */   }
/*    */ 
/*    */   public final Hasher putDouble(double d) {
/* 34 */     return putLong(Double.doubleToRawLongBits(d));
/*    */   }
/*    */ 
/*    */   public final Hasher putFloat(float f) {
/* 38 */     return putInt(Float.floatToRawIntBits(f));
/*    */   }
/*    */ 
/*    */   public Hasher putString(CharSequence charSequence)
/*    */   {
/* 43 */     return putString(charSequence, Charsets.UTF_16LE);
/*    */   }
/*    */ 
/*    */   public Hasher putString(CharSequence charSequence, Charset charset) {
/* 47 */     return putBytes(charSequence.toString().getBytes(charset));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.AbstractHasher
 * JD-Core Version:    0.6.2
 */