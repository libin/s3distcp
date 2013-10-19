/*    */ package emr.hbase.options;
/*    */ 
/*    */ public abstract class OptionBase
/*    */   implements Option
/*    */ {
/*    */   protected String arg;
/*    */   protected String desc;
/*    */ 
/*    */   public OptionBase(String arg, String desc)
/*    */   {
/* 24 */     this.arg = arg;
/* 25 */     this.desc = desc;
/*    */   }
/*    */ 
/*    */   public void require()
/*    */   {
/* 30 */     if (!defined())
/* 31 */       throw new RuntimeException("expected argument " + this.arg);
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     emr.hbase.options.OptionBase
 * JD-Core Version:    0.6.2
 */