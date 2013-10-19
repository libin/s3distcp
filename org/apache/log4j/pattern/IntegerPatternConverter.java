/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ import java.util.Date;
/*    */ 
/*    */ public final class IntegerPatternConverter extends PatternConverter
/*    */ {
/* 32 */   private static final IntegerPatternConverter INSTANCE = new IntegerPatternConverter();
/*    */ 
/*    */   private IntegerPatternConverter()
/*    */   {
/* 39 */     super("Integer", "integer");
/*    */   }
/*    */ 
/*    */   public static IntegerPatternConverter newInstance(String[] options)
/*    */   {
/* 49 */     return INSTANCE;
/*    */   }
/*    */ 
/*    */   public void format(Object obj, StringBuffer toAppendTo)
/*    */   {
/* 56 */     if ((obj instanceof Integer)) {
/* 57 */       toAppendTo.append(obj.toString());
/*    */     }
/*    */ 
/* 60 */     if ((obj instanceof Date))
/* 61 */       toAppendTo.append(Long.toString(((Date)obj).getTime()));
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.IntegerPatternConverter
 * JD-Core Version:    0.6.2
 */