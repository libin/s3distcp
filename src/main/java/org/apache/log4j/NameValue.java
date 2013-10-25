/*     */ package org.apache.log4j;
/*     */ 
/*     */ class NameValue
/*     */ {
/*     */   String key;
/*     */   String value;
/*     */ 
/*     */   public NameValue(String key, String value)
/*     */   {
/* 963 */     this.key = key;
/* 964 */     this.value = value;
/*     */   }
/*     */   public String toString() {
/* 967 */     return this.key + "=" + this.value;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.NameValue
 * JD-Core Version:    0.6.2
 */