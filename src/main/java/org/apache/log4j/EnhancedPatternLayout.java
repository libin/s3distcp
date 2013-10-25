/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.helpers.PatternConverter;
/*     */ import org.apache.log4j.helpers.PatternParser;
/*     */ import org.apache.log4j.pattern.BridgePatternConverter;
/*     */ import org.apache.log4j.pattern.BridgePatternParser;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class EnhancedPatternLayout extends Layout
/*     */ {
/*     */   public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
/*     */   public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";
/*     */ 
/*     */   /** @deprecated */
/* 435 */   protected final int BUF_SIZE = 256;
/*     */ 
/*     */   /** @deprecated */
/* 441 */   protected final int MAX_CAPACITY = 1024;
/*     */   public static final String PATTERN_RULE_REGISTRY = "PATTERN_RULE_REGISTRY";
/*     */   private PatternConverter head;
/*     */   private String conversionPattern;
/*     */   private boolean handlesExceptions;
/*     */ 
/*     */   public EnhancedPatternLayout()
/*     */   {
/* 471 */     this("%m%n");
/*     */   }
/*     */ 
/*     */   public EnhancedPatternLayout(String pattern)
/*     */   {
/* 479 */     this.conversionPattern = pattern;
/* 480 */     this.head = createPatternParser(pattern == null ? "%m%n" : pattern).parse();
/*     */ 
/* 482 */     if ((this.head instanceof BridgePatternConverter))
/* 483 */       this.handlesExceptions = (!((BridgePatternConverter)this.head).ignoresThrowable());
/*     */     else
/* 485 */       this.handlesExceptions = false;
/*     */   }
/*     */ 
/*     */   public void setConversionPattern(String conversionPattern)
/*     */   {
/* 497 */     this.conversionPattern = OptionConverter.convertSpecialChars(conversionPattern);
/*     */ 
/* 499 */     this.head = createPatternParser(this.conversionPattern).parse();
/* 500 */     if ((this.head instanceof BridgePatternConverter))
/* 501 */       this.handlesExceptions = (!((BridgePatternConverter)this.head).ignoresThrowable());
/*     */     else
/* 503 */       this.handlesExceptions = false;
/*     */   }
/*     */ 
/*     */   public String getConversionPattern()
/*     */   {
/* 512 */     return this.conversionPattern;
/*     */   }
/*     */ 
/*     */   protected PatternParser createPatternParser(String pattern)
/*     */   {
/* 524 */     return new BridgePatternParser(pattern);
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 542 */     StringBuffer buf = new StringBuffer();
/* 543 */     for (PatternConverter c = this.head; 
/* 544 */       c != null; 
/* 545 */       c = c.next) {
/* 546 */       c.format(buf, event);
/*     */     }
/* 548 */     return buf.toString();
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 557 */     return !this.handlesExceptions;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.EnhancedPatternLayout
 * JD-Core Version:    0.6.2
 */