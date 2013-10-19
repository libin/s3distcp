/*    */ package com.google.gson;
/*    */ 
/*    */ public final class JsonNull extends JsonElement
/*    */ {
/* 32 */   public static final JsonNull INSTANCE = new JsonNull();
/*    */ 
/*    */   public int hashCode()
/*    */   {
/* 48 */     return JsonNull.class.hashCode();
/*    */   }
/*    */ 
/*    */   public boolean equals(Object other)
/*    */   {
/* 56 */     return (this == other) || ((other instanceof JsonNull));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.JsonNull
 * JD-Core Version:    0.6.2
 */