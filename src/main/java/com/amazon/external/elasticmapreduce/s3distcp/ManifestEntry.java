/*    */ package com.amazon.external.elasticmapreduce.s3distcp;
/*    */ 
/*    */ public class ManifestEntry
/*    */ {
/*    */   public String path;
/*    */   public String baseName;
/*    */   public String srcDir;
public String srcPath;
/*    */   public long size;
/*    */ 
/*    */   public ManifestEntry()
/*    */   {
/*    */   }
/*    */ 
/*    */   public ManifestEntry(String path, String baseName, String srcDir, String srcPath, long size)
/*    */   {
/* 14 */     this.path = path;
/* 15 */     this.baseName = baseName;
/* 16 */     this.srcDir = srcDir;
/* 16 */     this.srcPath = srcPath;
/* 17 */     this.size = size;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazon.external.elasticmapreduce.s3distcp.ManifestEntry
 * JD-Core Version:    0.6.2
 */