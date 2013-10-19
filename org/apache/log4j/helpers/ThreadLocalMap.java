/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.util.Hashtable;
/*    */ 
/*    */ public final class ThreadLocalMap extends InheritableThreadLocal
/*    */ {
/*    */   public final Object childValue(Object parentValue)
/*    */   {
/* 35 */     Hashtable ht = (Hashtable)parentValue;
/* 36 */     if (ht != null) {
/* 37 */       return ht.clone();
/*    */     }
/* 39 */     return null;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.ThreadLocalMap
 * JD-Core Version:    0.6.2
 */