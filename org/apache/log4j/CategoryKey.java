/*    */ package org.apache.log4j;
/*    */ 
/*    */ class CategoryKey
/*    */ {
/*    */   String name;
/*    */   int hashCache;
/*    */ 
/*    */   CategoryKey(String name)
/*    */   {
/* 31 */     this.name = name;
/* 32 */     this.hashCache = name.hashCode();
/*    */   }
/*    */ 
/*    */   public final int hashCode()
/*    */   {
/* 38 */     return this.hashCache;
/*    */   }
/*    */ 
/*    */   public final boolean equals(Object rArg)
/*    */   {
/* 44 */     if (this == rArg) {
/* 45 */       return true;
/*    */     }
/* 47 */     if ((rArg != null) && (CategoryKey.class == rArg.getClass())) {
/* 48 */       return this.name.equals(((CategoryKey)rArg).name);
/*    */     }
/* 50 */     return false;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.CategoryKey
 * JD-Core Version:    0.6.2
 */