/*    */ package com.amazonaws.auth.policy.conditions;
/*    */ 
/*    */ import com.amazonaws.auth.policy.Condition;
/*    */ import java.util.Arrays;
/*    */ 
/*    */ public class IpAddressCondition extends Condition
/*    */ {
/*    */   public IpAddressCondition(String ipAddressRange)
/*    */   {
/* 71 */     this(IpAddressComparisonType.IpAddress, ipAddressRange);
/*    */   }
/*    */ 
/*    */   public IpAddressCondition(IpAddressComparisonType type, String ipAddressRange)
/*    */   {
/* 91 */     this.type = type.toString();
/* 92 */     this.conditionKey = "aws:SourceIp";
/* 93 */     this.values = Arrays.asList(new String[] { ipAddressRange });
/*    */   }
/*    */ 
/*    */   public static enum IpAddressComparisonType
/*    */   {
/* 45 */     IpAddress, 
/*    */ 
/* 48 */     NotIpAddress;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.auth.policy.conditions.IpAddressCondition
 * JD-Core Version:    0.6.2
 */