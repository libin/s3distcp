/*    */ package com.amazonaws.auth.policy.conditions;
/*    */ 
/*    */ import com.amazonaws.auth.policy.Condition;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class ArnCondition extends Condition
/*    */ {
/*    */   public ArnCondition(ArnComparisonType type, String key, String value)
/*    */   {
/* 89 */     this.type = type.toString();
/* 90 */     this.conditionKey = key;
/* 91 */     this.values = Arrays.asList(new String[] { value });
/*    */   }
/*    */ 
/*    */   public static enum ArnComparisonType
/*    */   {
/* 54 */     ArnEquals, 
/*    */ 
/* 62 */     ArnLike, 
/*    */ 
/* 65 */     ArnNotEquals, 
/*    */ 
/* 68 */     ArnNotLike;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.conditions.ArnCondition
 * JD-Core Version:    0.6.2
 */