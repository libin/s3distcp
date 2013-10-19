/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ public abstract class PatternConverter
/*    */ {
/*    */   private final String name;
/*    */   private final String style;
/*    */ 
/*    */   protected PatternConverter(String name, String style)
/*    */   {
/* 53 */     this.name = name;
/* 54 */     this.style = style;
/*    */   }
/*    */ 
/*    */   public abstract void format(Object paramObject, StringBuffer paramStringBuffer);
/*    */ 
/*    */   public final String getName()
/*    */   {
/* 72 */     return this.name;
/*    */   }
/*    */ 
/*    */   public String getStyleClass(Object e)
/*    */   {
/* 85 */     return this.style;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.PatternConverter
 * JD-Core Version:    0.6.2
 */