/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ import org.apache.log4j.spi.ThrowableInformation;
/*     */ 
/*     */ public class ThrowableInformationPatternConverter extends LoggingEventPatternConverter
/*     */ {
/*  41 */   private int maxLines = 2147483647;
/*     */ 
/*     */   private ThrowableInformationPatternConverter(String[] options)
/*     */   {
/*  49 */     super("Throwable", "throwable");
/*     */ 
/*  51 */     if ((options != null) && (options.length > 0))
/*  52 */       if ("none".equals(options[0]))
/*  53 */         this.maxLines = 0;
/*  54 */       else if ("short".equals(options[0]))
/*  55 */         this.maxLines = 1;
/*     */       else
/*     */         try {
/*  58 */           this.maxLines = Integer.parseInt(options[0]);
/*     */         }
/*     */         catch (NumberFormatException ex)
/*     */         {
/*     */         }
/*     */   }
/*     */ 
/*     */   public static ThrowableInformationPatternConverter newInstance(String[] options)
/*     */   {
/*  73 */     return new ThrowableInformationPatternConverter(options);
/*     */   }
/*     */ 
/*     */   public void format(LoggingEvent event, StringBuffer toAppendTo)
/*     */   {
/*  80 */     if (this.maxLines != 0) {
/*  81 */       ThrowableInformation information = event.getThrowableInformation();
/*     */ 
/*  83 */       if (information != null) {
/*  84 */         String[] stringRep = information.getThrowableStrRep();
/*     */ 
/*  86 */         int length = stringRep.length;
/*  87 */         if (this.maxLines < 0)
/*  88 */           length += this.maxLines;
/*  89 */         else if (length > this.maxLines) {
/*  90 */           length = this.maxLines;
/*     */         }
/*     */ 
/*  93 */         for (int i = 0; i < length; i++) {
/*  94 */           String string = stringRep[i];
/*  95 */           toAppendTo.append(string).append("\n");
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean handlesThrowable()
/*     */   {
/* 106 */     return true;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.ThrowableInformationPatternConverter
 * JD-Core Version:    0.6.2
 */