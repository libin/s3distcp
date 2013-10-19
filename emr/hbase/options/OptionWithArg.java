/*    */ package emr.hbase.options;
/*    */ 
/*    */ public class OptionWithArg extends OptionBase
/*    */   implements Option
/*    */ {
/*    */   public String value;
/*    */ 
/*    */   public OptionWithArg(String arg, String desc)
/*    */   {
/* 21 */     super(arg, desc);
/*    */   }
/*    */ 
/*    */   public int matches(String[] arguments, int matchIndex)
/*    */   {
/* 26 */     String argument = arguments[matchIndex];
/* 27 */     if (argument.equals(this.arg)) {
/* 28 */       if (matchIndex + 1 < arguments.length) {
/* 29 */         this.value = arguments[(matchIndex + 1)];
/* 30 */         return matchIndex + 2;
/*    */       }
/*    */ 
/* 33 */       throw new RuntimeException("expected argument for " + this.arg + " but no argument was given");
/*    */     }
/*    */ 
/* 36 */     if ((argument.length() >= this.arg.length() + 1) && (argument.substring(0, this.arg.length() + 1).equals(this.arg + "="))) {
/* 37 */       this.value = argument.substring(this.arg.length() + 1);
/* 38 */       return matchIndex + 1;
/*    */     }
/* 40 */     if ((argument.length() >= this.arg.length() + 2) && (argument.substring(0, this.arg.length() + 2).equals(this.arg + "=="))) {
/* 41 */       this.value = argument.substring(this.arg.length() + 2);
/* 42 */       return matchIndex + 1;
/*    */     }
/* 44 */     return matchIndex;
/*    */   }
/*    */ 
/*    */   public String helpLine()
/*    */   {
/* 49 */     return this.arg + "=VALUE   -   " + this.desc;
/*    */   }
/*    */ 
/*    */   public boolean defined()
/*    */   {
/* 54 */     return this.value != null;
/*    */   }
/*    */ 
/*    */   public String getValue(String defaultValue) {
/* 58 */     if (this.value != null) {
/* 59 */       return this.value;
/*    */     }
/* 61 */     return defaultValue;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     emr.hbase.options.OptionWithArg
 * JD-Core Version:    0.6.2
 */