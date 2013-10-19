/*    */ package com.amazonaws.internal;
/*    */ 
/*    */ public class DynamoDBBackoffStrategy extends CustomBackoffStrategy
/*    */ {
/* 22 */   public static final CustomBackoffStrategy DEFAULT = new DynamoDBBackoffStrategy();
/*    */ 
/*    */   public int getBackoffPeriod(int retries)
/*    */   {
/* 19 */     return retries == 0 ? 0 : 50 * (int)Math.pow(2.0D, retries - 1);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.internal.DynamoDBBackoffStrategy
 * JD-Core Version:    0.6.2
 */