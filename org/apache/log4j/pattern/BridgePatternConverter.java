/*     */ package org.apache.log4j.pattern;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.apache.log4j.helpers.PatternConverter;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public final class BridgePatternConverter extends PatternConverter
/*     */ {
/*     */   private LoggingEventPatternConverter[] patternConverters;
/*     */   private FormattingInfo[] patternFields;
/*     */   private boolean handlesExceptions;
/*     */ 
/*     */   public BridgePatternConverter(String pattern)
/*     */   {
/*  59 */     this.next = null;
/*  60 */     this.handlesExceptions = false;
/*     */ 
/*  62 */     List converters = new ArrayList();
/*  63 */     List fields = new ArrayList();
/*  64 */     Map converterRegistry = null;
/*     */ 
/*  66 */     PatternParser.parse(pattern, converters, fields, converterRegistry, PatternParser.getPatternLayoutRules());
/*     */ 
/*  70 */     this.patternConverters = new LoggingEventPatternConverter[converters.size()];
/*  71 */     this.patternFields = new FormattingInfo[converters.size()];
/*     */ 
/*  73 */     int i = 0;
/*  74 */     Iterator converterIter = converters.iterator();
/*  75 */     Iterator fieldIter = fields.iterator();
/*     */ 
/*  77 */     while (converterIter.hasNext()) {
/*  78 */       Object converter = converterIter.next();
/*     */ 
/*  80 */       if ((converter instanceof LoggingEventPatternConverter)) {
/*  81 */         this.patternConverters[i] = ((LoggingEventPatternConverter)converter);
/*  82 */         this.handlesExceptions |= this.patternConverters[i].handlesThrowable();
/*     */       } else {
/*  84 */         this.patternConverters[i] = new LiteralPatternConverter("");
/*     */       }
/*     */ 
/*  88 */       if (fieldIter.hasNext())
/*  89 */         this.patternFields[i] = ((FormattingInfo)fieldIter.next());
/*     */       else {
/*  91 */         this.patternFields[i] = FormattingInfo.getDefault();
/*     */       }
/*     */ 
/*  94 */       i++;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected String convert(LoggingEvent event)
/*     */   {
/* 105 */     StringBuffer sbuf = new StringBuffer();
/* 106 */     format(sbuf, event);
/*     */ 
/* 108 */     return sbuf.toString();
/*     */   }
/*     */ 
/*     */   public void format(StringBuffer sbuf, LoggingEvent e)
/*     */   {
/* 117 */     for (int i = 0; i < this.patternConverters.length; i++) {
/* 118 */       int startField = sbuf.length();
/* 119 */       this.patternConverters[i].format(e, sbuf);
/* 120 */       this.patternFields[i].format(startField, sbuf);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 130 */     return !this.handlesExceptions;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.pattern.BridgePatternConverter
 * JD-Core Version:    0.6.2
 */