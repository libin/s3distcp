/*    */ package emr.hbase.options;
/*    */ 
/*    */ public class SimpleOption extends OptionBase
/*    */   implements Option
/*    */ {
/*    */   public boolean value;
/*    */ 
/*    */   SimpleOption(String arg, String desc)
/*    */   {
/* 22 */     super(arg, desc);
/* 23 */     this.value = false;
/*    */   }
/*    */ 
/*    */   public int matches(String[] arguments, int matchIndex)
/*    */   {
/* 28 */     String argument = arguments[matchIndex];
/* 29 */     if (argument.equals(this.arg)) {
/* 30 */       this.value = true;
/* 31 */       return matchIndex + 1;
/*    */     }
/* 33 */     return matchIndex;
/*    */   }
/*    */ 
/*    */   public String helpLine()
/*    */   {
/* 38 */     return this.arg + "   -   " + this.desc;
/*    */   }
/*    */ 
/*    */   public void require()
/*    */   {
/* 43 */     if (!this.value)
/* 44 */       throw new RuntimeException("expected argument " + this.arg);
/*    */   }
/*    */ 
/*    */   public boolean defined()
/*    */   {
/* 50 */     return this.value;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     emr.hbase.options.SimpleOption
 * JD-Core Version:    0.6.2
 */