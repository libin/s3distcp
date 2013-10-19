/*    */ package org.apache.log4j.helpers;
/*    */ 
/*    */ import java.util.Enumeration;
/*    */ import java.util.NoSuchElementException;
/*    */ 
/*    */ public class NullEnumeration
/*    */   implements Enumeration
/*    */ {
/* 31 */   private static final NullEnumeration instance = new NullEnumeration();
/*    */ 
/*    */   public static NullEnumeration getInstance()
/*    */   {
/* 38 */     return instance;
/*    */   }
/*    */ 
/*    */   public boolean hasMoreElements()
/*    */   {
/* 43 */     return false;
/*    */   }
/*    */ 
/*    */   public Object nextElement()
/*    */   {
/* 48 */     throw new NoSuchElementException();
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.helpers.NullEnumeration
 * JD-Core Version:    0.6.2
 */