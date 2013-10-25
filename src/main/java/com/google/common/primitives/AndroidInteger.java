/*    */ package com.google.common.primitives;
/*    */ 
/*    */ import com.google.common.base.Preconditions;
/*    */ import javax.annotation.CheckForNull;
/*    */ 
/*    */ final class AndroidInteger
/*    */ {
/*    */   @CheckForNull
/*    */   static Integer tryParse(String string)
/*    */   {
/* 34 */     return tryParse(string, 10);
/*    */   }
/*    */ 
/*    */   @CheckForNull
/*    */   static Integer tryParse(String string, int radix)
/*    */   {
/* 42 */     Preconditions.checkNotNull(string);
/* 43 */     Preconditions.checkArgument(radix >= 2, "Invalid radix %s, min radix is %s", new Object[] { Integer.valueOf(radix), Integer.valueOf(2) });
/*    */ 
/* 45 */     Preconditions.checkArgument(radix <= 36, "Invalid radix %s, max radix is %s", new Object[] { Integer.valueOf(radix), Integer.valueOf(36) });
/*    */ 
/* 47 */     int length = string.length(); int i = 0;
/* 48 */     if (length == 0) {
/* 49 */       return null;
/*    */     }
/* 51 */     boolean negative = string.charAt(i) == '-';
/* 52 */     if (negative) { i++; if (i == length)
/* 53 */         return null;
/*    */     }
/* 55 */     return tryParse(string, i, radix, negative);
/*    */   }
/*    */ 
/*    */   @CheckForNull
/*    */   private static Integer tryParse(String string, int offset, int radix, boolean negative)
/*    */   {
/* 61 */     int max = -2147483648 / radix;
/* 62 */     int result = 0; int length = string.length();
/* 63 */     while (offset < length) {
/* 64 */       int digit = Character.digit(string.charAt(offset++), radix);
/* 65 */       if (digit == -1) {
/* 66 */         return null;
/*    */       }
/* 68 */       if (max > result) {
/* 69 */         return null;
/*    */       }
/* 71 */       int next = result * radix - digit;
/* 72 */       if (next > result) {
/* 73 */         return null;
/*    */       }
/* 75 */       result = next;
/*    */     }
/* 77 */     if (!negative) {
/* 78 */       result = -result;
/* 79 */       if (result < 0) {
/* 80 */         return null;
/*    */       }
/*    */     }
/*    */ 
/* 84 */     if ((result > 2147483647) || (result < -2147483648)) {
/* 85 */       return null;
/*    */     }
/* 87 */     return Integer.valueOf(result);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.primitives.AndroidInteger
 * JD-Core Version:    0.6.2
 */