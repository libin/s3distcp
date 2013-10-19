/*    */ package com.amazonaws.auth.policy.conditions;
/*    */ 
/*    */ import com.amazonaws.auth.policy.Condition;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class StringCondition extends Condition
/*    */ {
/*    */   public StringCondition(StringComparisonType type, String key, String value)
/*    */   {
/* 73 */     this.type = type.toString();
/* 74 */     this.conditionKey = key;
/* 75 */     this.values = Arrays.asList(new String[] { value });
/*    */   }
/*    */ 
/*    */   public static enum StringComparisonType
/*    */   {
/* 32 */     StringEquals, 
/*    */ 
/* 35 */     StringEqualsIgnoreCase, 
/*    */ 
/* 42 */     StringLike, 
/*    */ 
/* 45 */     StringNotEquals, 
/*    */ 
/* 48 */     StringNotEqualsIgnoreCase, 
/*    */ 
/* 51 */     StringNotLike;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.conditions.StringCondition
 * JD-Core Version:    0.6.2
 */