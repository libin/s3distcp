/*     */ package org.apache.log4j.varia;
/*     */ 
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.Filter;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class StringMatchFilter extends Filter
/*     */ {
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String STRING_TO_MATCH_OPTION = "StringToMatch";
/*     */ 
/*     */   /** @deprecated */
/*     */   public static final String ACCEPT_ON_MATCH_OPTION = "AcceptOnMatch";
/*  54 */   boolean acceptOnMatch = true;
/*     */   String stringToMatch;
/*     */ 
/*     */   /** @deprecated */
/*     */   public String[] getOptionStrings()
/*     */   {
/*  63 */     return new String[] { "StringToMatch", "AcceptOnMatch" };
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   public void setOption(String key, String value)
/*     */   {
/*  73 */     if (key.equalsIgnoreCase("StringToMatch"))
/*  74 */       this.stringToMatch = value;
/*  75 */     else if (key.equalsIgnoreCase("AcceptOnMatch"))
/*  76 */       this.acceptOnMatch = OptionConverter.toBoolean(value, this.acceptOnMatch);
/*     */   }
/*     */ 
/*     */   public void setStringToMatch(String s)
/*     */   {
/*  82 */     this.stringToMatch = s;
/*     */   }
/*     */ 
/*     */   public String getStringToMatch()
/*     */   {
/*  87 */     return this.stringToMatch;
/*     */   }
/*     */ 
/*     */   public void setAcceptOnMatch(boolean acceptOnMatch)
/*     */   {
/*  92 */     this.acceptOnMatch = acceptOnMatch;
/*     */   }
/*     */ 
/*     */   public boolean getAcceptOnMatch()
/*     */   {
/*  97 */     return this.acceptOnMatch;
/*     */   }
/*     */ 
/*     */   public int decide(LoggingEvent event)
/*     */   {
/* 105 */     String msg = event.getRenderedMessage();
/*     */ 
/* 107 */     if ((msg == null) || (this.stringToMatch == null)) {
/* 108 */       return 0;
/*     */     }
/*     */ 
/* 111 */     if (msg.indexOf(this.stringToMatch) == -1) {
/* 112 */       return 0;
/*     */     }
/* 114 */     if (this.acceptOnMatch) {
/* 115 */       return 1;
/*     */     }
/* 117 */     return -1;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.varia.StringMatchFilter
 * JD-Core Version:    0.6.2
 */