/*    */ package org.apache.log4j.pattern;
/*    */ 
/*    */ public abstract class NamePatternConverter extends LoggingEventPatternConverter
/*    */ {
/*    */   private final NameAbbreviator abbreviator;
/*    */ 
/*    */   protected NamePatternConverter(String name, String style, String[] options)
/*    */   {
/* 44 */     super(name, style);
/*    */ 
/* 46 */     if ((options != null) && (options.length > 0))
/* 47 */       this.abbreviator = NameAbbreviator.getAbbreviator(options[0]);
/*    */     else
/* 49 */       this.abbreviator = NameAbbreviator.getDefaultAbbreviator();
/*    */   }
/*    */ 
/*    */   protected final void abbreviate(int nameStart, StringBuffer buf)
/*    */   {
/* 59 */     this.abbreviator.abbreviate(nameStart, buf);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.NamePatternConverter
 * JD-Core Version:    0.6.2
 */