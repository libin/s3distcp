/*    */ package com.google.common.collect;
/*    */ 
/*    */ import com.google.common.annotations.GwtCompatible;
/*    */ import java.lang.reflect.Array;
/*    */ 
/*    */ @GwtCompatible(emulated=true)
/*    */ class Platform
/*    */ {
/*    */   static <T> T[] clone(T[] array)
/*    */   {
/* 35 */     return (Object[])array.clone();
/*    */   }
/*    */ 
/*    */   static <T> T[] newArray(T[] reference, int length)
/*    */   {
/* 46 */     Class type = reference.getClass().getComponentType();
/*    */ 
/* 51 */     Object[] result = (Object[])Array.newInstance(type, length);
/* 52 */     return result;
/*    */   }
/*    */ 
/*    */   static MapMaker tryWeakKeys(MapMaker mapMaker)
/*    */   {
/* 62 */     return mapMaker.weakKeys();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.collect.Platform
 * JD-Core Version:    0.6.2
 */