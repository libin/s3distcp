/*     */ package org.apache.log4j;
/*     */ 
/*     */ import org.apache.log4j.helpers.DateLayout;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class TTCCLayout extends DateLayout
/*     */ {
/*  77 */   private boolean threadPrinting = true;
/*  78 */   private boolean categoryPrefixing = true;
/*  79 */   private boolean contextPrinting = true;
/*     */ 
/*  82 */   protected final StringBuffer buf = new StringBuffer(256);
/*     */ 
/*     */   public TTCCLayout()
/*     */   {
/*  92 */     setDateFormat("RELATIVE", null);
/*     */   }
/*     */ 
/*     */   public TTCCLayout(String dateFormatType)
/*     */   {
/* 105 */     setDateFormat(dateFormatType);
/*     */   }
/*     */ 
/*     */   public void setThreadPrinting(boolean threadPrinting)
/*     */   {
/* 115 */     this.threadPrinting = threadPrinting;
/*     */   }
/*     */ 
/*     */   public boolean getThreadPrinting()
/*     */   {
/* 123 */     return this.threadPrinting;
/*     */   }
/*     */ 
/*     */   public void setCategoryPrefixing(boolean categoryPrefixing)
/*     */   {
/* 132 */     this.categoryPrefixing = categoryPrefixing;
/*     */   }
/*     */ 
/*     */   public boolean getCategoryPrefixing()
/*     */   {
/* 140 */     return this.categoryPrefixing;
/*     */   }
/*     */ 
/*     */   public void setContextPrinting(boolean contextPrinting)
/*     */   {
/* 150 */     this.contextPrinting = contextPrinting;
/*     */   }
/*     */ 
/*     */   public boolean getContextPrinting()
/*     */   {
/* 158 */     return this.contextPrinting;
/*     */   }
/*     */ 
/*     */   public String format(LoggingEvent event)
/*     */   {
/* 176 */     this.buf.setLength(0);
/*     */ 
/* 178 */     dateFormat(this.buf, event);
/*     */ 
/* 180 */     if (this.threadPrinting) {
/* 181 */       this.buf.append('[');
/* 182 */       this.buf.append(event.getThreadName());
/* 183 */       this.buf.append("] ");
/*     */     }
/* 185 */     this.buf.append(event.getLevel().toString());
/* 186 */     this.buf.append(' ');
/*     */ 
/* 188 */     if (this.categoryPrefixing) {
/* 189 */       this.buf.append(event.getLoggerName());
/* 190 */       this.buf.append(' ');
/*     */     }
/*     */ 
/* 193 */     if (this.contextPrinting) {
/* 194 */       String ndc = event.getNDC();
/*     */ 
/* 196 */       if (ndc != null) {
/* 197 */         this.buf.append(ndc);
/* 198 */         this.buf.append(' ');
/*     */       }
/*     */     }
/* 201 */     this.buf.append("- ");
/* 202 */     this.buf.append(event.getRenderedMessage());
/* 203 */     this.buf.append(LINE_SEP);
/* 204 */     return this.buf.toString();
/*     */   }
/*     */ 
/*     */   public boolean ignoresThrowable()
/*     */   {
/* 215 */     return true;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.TTCCLayout
 * JD-Core Version:    0.6.2
 */