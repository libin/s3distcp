/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ import java.util.Arrays;

import org.apache.commons.logging.Log;
/*    */ import org.apache.commons.logging.LogFactory;
/*    */ import org.apache.hadoop.util.ToolRunner;
/*    */ 
/*    */ public class Main
/*    */ {
/*  8 */   private static final Log log = LogFactory.getLog(S3DistCp.class);
/*    */ 
/*    */   public static void main(String[] args) throws Exception {
/* 11 */     log.info("Running with args: " + Arrays.toString(args));
/*    */ 
/* 13 */     System.exit(ToolRunner.run(new S3DistCp(), args));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.Main
 * JD-Core Version:    0.6.2
 */