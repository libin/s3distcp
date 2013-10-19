/*    */ package com.amazonaws.util;
/*    */ 
/*    */ import java.net.URI;
/*    */ 
/*    */ public class AwsHostNameUtils
/*    */ {
/*    */   public static String parseRegionName(URI endpoint)
/*    */   {
/* 21 */     String host = endpoint.getHost();
/*    */ 
/* 24 */     if (!host.endsWith(".amazonaws.com")) return "us-east-1";
/*    */ 
/* 26 */     String serviceAndRegion = host.substring(0, host.indexOf(".amazonaws.com"));
/*    */ 
/* 28 */     char separator = '.';
/* 29 */     if (serviceAndRegion.startsWith("s3")) separator = '-';
/*    */ 
/* 31 */     if (serviceAndRegion.indexOf(separator) == -1) return "us-east-1";
/*    */ 
/* 33 */     String region = serviceAndRegion.substring(serviceAndRegion.indexOf(separator) + 1);
/* 34 */     if ("us-gov".equals(region)) {
/* 35 */       return "us-gov-west-1";
/*    */     }
/*    */ 
/* 38 */     return region;
/*    */   }
/*    */ 
/*    */   public static String parseServiceName(URI endpoint) {
/* 42 */     String host = endpoint.getHost();
/*    */ 
/* 45 */     if (!host.endsWith(".amazonaws.com")) return "us-east-1";
/*    */ 
/* 47 */     String serviceAndRegion = host.substring(0, host.indexOf(".amazonaws.com"));
/*    */ 
/* 49 */     char separator = '.';
/* 50 */     if (serviceAndRegion.startsWith("s3")) separator = '-';
/*    */ 
/* 55 */     if (serviceAndRegion.indexOf(separator) == -1) return serviceAndRegion;
/*    */ 
/* 57 */     String service = serviceAndRegion.substring(0, serviceAndRegion.indexOf(separator));
/* 58 */     return service;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.AwsHostNameUtils
 * JD-Core Version:    0.6.2
 */