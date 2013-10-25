/*    */ package com.amazonaws.util;
/*    */ 
/*    */ import java.io.ByteArrayInputStream;
/*    */ import java.io.UnsupportedEncodingException;
/*    */ 
/*    */ public class StringInputStream extends ByteArrayInputStream
/*    */ {
/*    */   private final String string;
/*    */ 
/*    */   public StringInputStream(String s)
/*    */     throws UnsupportedEncodingException
/*    */   {
/* 29 */     super(s.getBytes("UTF-8"));
/* 30 */     this.string = s;
/*    */   }
/*    */ 
/*    */   public String getString()
/*    */   {
/* 41 */     return this.string;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.StringInputStream
 * JD-Core Version:    0.6.2
 */