/*    */ package com.amazonaws.auth.policy.conditions;
/*    */ 
/*    */ import com.amazonaws.auth.policy.Condition;
/*    */ import com.amazonaws.util.DateUtils;
/*    */ import java.util.Arrays;
/*    */ import java.util.Date;
/*    */ 
/*    */ public class DateCondition extends Condition
/*    */ {
/* 30 */   private final DateUtils dateUtils = new DateUtils();
/*    */ 
/*    */   public DateCondition(DateComparisonType type, Date date)
/*    */   {
/* 57 */     this.type = type.toString();
/* 58 */     this.conditionKey = "aws:CurrentTime";
/* 59 */     this.values = Arrays.asList(new String[] { this.dateUtils.formatIso8601Date(date) });
/*    */   }
/*    */ 
/*    */   public static enum DateComparisonType
/*    */   {
/* 36 */     DateEquals, 
/* 37 */     DateGreaterThan, 
/* 38 */     DateGreaterThanEquals, 
/* 39 */     DateLessThan, 
/* 40 */     DateLessThanEquals, 
/* 41 */     DateNotEquals;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.conditions.DateCondition
 * JD-Core Version:    0.6.2
 */