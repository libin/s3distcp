/*    */ package com.amazonaws.auth.policy.conditions;
/*    */ 
/*    */ import com.amazonaws.auth.policy.Condition;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class NumericCondition extends Condition
/*    */ {
/*    */   public NumericCondition(NumericComparisonType type, String key, String value)
/*    */   {
/* 49 */     this.type = type.toString();
/* 50 */     this.conditionKey = key;
/* 51 */     this.values = Arrays.asList(new String[] { value });
/*    */   }
/*    */ 
/*    */   public static enum NumericComparisonType
/*    */   {
/* 29 */     NumericEquals, 
/* 30 */     NumericGreaterThan, 
/* 31 */     NumericGreaterThanEquals, 
/* 32 */     NumericLessThan, 
/* 33 */     NumericLessThanEquals, 
/* 34 */     NumericNotEquals;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.conditions.NumericCondition
 * JD-Core Version:    0.6.2
 */