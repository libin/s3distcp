/*    */ package com.google.gson;
/*    */ 
/*    */ public enum LongSerializationPolicy
/*    */ {
/* 34 */   DEFAULT, 
/*    */ 
/* 45 */   STRING;
/*    */ 
/*    */   public abstract JsonElement serialize(Long paramLong);
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.LongSerializationPolicy
 * JD-Core Version:    0.6.2
 */