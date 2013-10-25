/*    */ package com.amazonaws.auth.policy.conditions;
/*    */ 
/*    */ import com.amazonaws.auth.policy.Condition;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class BooleanCondition extends Condition
/*    */ {
/*    */   public BooleanCondition(String key, boolean value)
/*    */   {
/* 39 */     this.type = "Bool";
/* 40 */     this.conditionKey = key;
/* 41 */     this.values = Arrays.asList(new String[] { Boolean.toString(value) });
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.conditions.BooleanCondition
 * JD-Core Version:    0.6.2
 */