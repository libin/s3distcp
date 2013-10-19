/*     */ package com.amazonaws.auth.policy.conditions;
/*     */ 
/*     */ import com.amazonaws.auth.policy.Condition;
/*     */ 
/*     */ public class ConditionFactory
/*     */ {
/*     */   public static final String CURRENT_TIME_CONDITION_KEY = "aws:CurrentTime";
/*     */   public static final String SECURE_TRANSPORT_CONDITION_KEY = "aws:SecureTransport";
/*     */   public static final String SOURCE_IP_CONDITION_KEY = "aws:SourceIp";
/*     */   public static final String USER_AGENT_CONDITION_KEY = "aws:UserAgent";
/*     */   public static final String EPOCH_TIME_CONDITION_KEY = "aws:EpochTime";
/*     */   public static final String REFERER_CONDITION_KEY = "aws:Referer";
/*     */   public static final String SOURCE_ARN_CONDITION_KEY = "aws:SourceArn";
/*     */ 
/*     */   public static Condition newSourceArnCondition(String arnPattern)
/*     */   {
/* 121 */     return new ArnCondition(ArnCondition.ArnComparisonType.ArnLike, "aws:SourceArn", arnPattern);
/*     */   }
/*     */ 
/*     */   public static Condition newSecureTransportCondition()
/*     */   {
/* 132 */     return new BooleanCondition("aws:SecureTransport", true);
/*     */   }
/*     */ 
/*     */   public static Condition newUserAgentCondition(StringCondition.StringComparisonType comparisonType, String value)
/*     */   {
/* 153 */     return new StringCondition(comparisonType, "aws:UserAgent", value);
/*     */   }
/*     */ 
/*     */   public static Condition newRefererCondition(StringCondition.StringComparisonType comparisonType, String value)
/*     */   {
/* 172 */     return new StringCondition(comparisonType, "aws:Referer", value);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.conditions.ConditionFactory
 * JD-Core Version:    0.6.2
 */