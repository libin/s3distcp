/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.PatternConverter;
/*     */ import org.apache.log4j.helpers.PatternParser;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class PatternLayout extends Layout
/*     */ {
/*     */   public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";
/*     */   public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";
/* 413 */   protected final int BUF_SIZE = 256;
/* 414 */   protected final int MAX_CAPACITY = 1024;
/*     */ 
/* 418 */   private StringBuffer sbuf = new StringBuffer(256);
/*     */   private String pattern;
/*     */   private PatternConverter head;
/*     */ 
/*     */   public PatternLayout()
/*     */   {
/* 430 */     this("%m%n");
/*     */   }
/*     */ 
/*     */   public PatternLayout(String pattern)
/*     */   {
/* 437 */     this.pattern = pattern;
/* 438 */     this.head = createPatternParser(pattern == null ? "%m%n" : pattern).parse();
/*     */   }
/*     */ 
/*     */   public void setConversionPattern(String conversionPattern)
/*     */   {
/* 449 */     this.pattern = conversionPattern;
/* 450 */     this.head = createPatternParser(conversionPattern).parse();
/*     */   }
/*     */ 
/*     */   public String getConversionPattern()
/*     */   {
/* 458 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 477 */     return true;
/*     */   }
/*     */ 
/*     */   protected PatternParser createPatternParser(String pattern)
/*     */   {
/* 488 */     return new PatternParser(pattern);
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 497 */     if (this.sbuf.capacity() > 1024)
/* 498 */       this.sbuf = new StringBuffer(256);
/*     */     else {
/* 500 */       this.sbuf.setLength(0);
/*     */     }
/*     */ 
/* 503 */     PatternConverter c = this.head;
/*     */ 
/* 505 */     while (c != null) {
/* 506 */       c.format(this.sbuf, event);
/* 507 */       c = c.next;
/*     */     }
/* 509 */     return this.sbuf.toString();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.PatternLayout
 * JD-Core Version:    0.6.2
 */