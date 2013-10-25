/*    */ package emr.hbase.options;
/*    */ 
/*    */ import com.google.common.collect.Lists;
/*    */ import java.io.PrintStream;
/*    */ import java.util.List;
/*    */ 
/*    */ public class Options
/*    */ {
/* 23 */   List<Option> options = Lists.newArrayList();
/* 24 */   List<String> extrArgs = Lists.newArrayList();
/*    */ 
/*    */   public OptionWithArg withArg(String arg, String description) {
/* 27 */     OptionWithArg option = new OptionWithArg(arg, description);
/* 28 */     this.options.add(option);
/* 29 */     return option;
/*    */   }
/*    */ 
/*    */   public SimpleOption noArg(String arg, String description) {
/* 33 */     SimpleOption option = new SimpleOption(arg, description);
/* 34 */     this.options.add(option);
/* 35 */     return option;
/*    */   }
/*    */ 
/*    */   public Option add(Option option) {
/* 39 */     this.options.add(option);
/* 40 */     return option;
/*    */   }
/*    */ 
/*    */   public String helpText() {
/* 44 */     StringBuffer result = new StringBuffer();
/* 45 */     result.append("Options:\n");
/* 46 */     for (Option option : this.options) {
/* 47 */       result.append("     ");
/* 48 */       result.append(option.helpLine());
/* 49 */       result.append("\n");
/*    */     }
/* 51 */     result.append("\n");
/* 52 */     return result.toString();
/*    */   }
/*    */ 
/*    */   public void parseArguments(String[] args)
/*    */   {
/* 57 */     parseArguments(args, false);
/*    */   }
/*    */ 
/*    */   public void parseArguments(String[] args, boolean allowExtraArguments) {
/* 61 */     int matchIndex = 0; int prevMatchIndex = 0;
/* 62 */     while (matchIndex < args.length) {
/* 63 */       prevMatchIndex = matchIndex;
/* 64 */       for (Option option : this.options) {
/* 65 */         matchIndex = option.matches(args, matchIndex);
/* 66 */         if (matchIndex >= args.length) {
/*    */           break;
/*    */         }
/*    */       }
/* 70 */       if (prevMatchIndex == matchIndex)
/* 71 */         if (allowExtraArguments) {
/* 72 */           this.extrArgs.add(args[matchIndex]);
/* 73 */           matchIndex++;
/*    */         } else {
/* 75 */           throw new RuntimeException("Argument " + args[matchIndex] + " doesn't match.");
/*    */         }
/*    */     }
/*    */   }
/*    */ 
/*    */   public static void require(Option[] options)
/*    */   {
/*    */     try
/*    */     {
/* 84 */       for (Option option : options)
/* 85 */         option.require();
/*    */     }
/*    */     catch (RuntimeException e)
/*    */     {
/* 89 */       System.out.println("Error: " + e.getMessage());
/* 90 */       System.exit(1);
/*    */     }
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     emr.hbase.options.Options
 * JD-Core Version:    0.6.2
 */