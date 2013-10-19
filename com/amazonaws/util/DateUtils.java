/*       */ package com.amazonaws.util;
/*       */ 
/*       */ import java.text.ParseException;
/*       */ import java.text.SimpleDateFormat;
/*       */ import java.util.Date;
/*       */ import java.util.Locale;
/*       */ import java.util.SimpleTimeZone;
/*       */ 
/*       */ public class DateUtils
/*       */ {
/*    36 */   protected final SimpleDateFormat iso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
/*       */ 
/*    40 */   protected final SimpleDateFormat alternateIso8601DateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
/*       */ 
/*    44 */   protected final SimpleDateFormat rfc822DateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
/*       */ 
/*       */   public DateUtils()
/*       */   {
/*    52 */     this.iso8601DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
/*    53 */     this.rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
/*    54 */     this.alternateIso8601DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
/*       */   }
/*       */ 
/*       */   public Date parseIso8601Date(String dateString)
/*       */     throws ParseException
/*       */   {
/*       */     try
/*       */     {
/*    71 */       synchronized (this.iso8601DateFormat) {
/*    72 */         return this.iso8601DateFormat.parse(dateString);
/*       */       }
/*       */     }
/*       */     catch (ParseException e)
/*       */     {
/*    77 */       synchronized (this.alternateIso8601DateFormat) {
/*    78 */         return this.alternateIso8601DateFormat.parse(dateString);
/*       */       }
/*       */     }
/*       */   }
/*       */ 
/*       */   public String formatIso8601Date(Date date)
/*       */   {
/*    92 */     synchronized (this.iso8601DateFormat) {
/*    93 */       return this.iso8601DateFormat.format(date);
/*       */     }
/*       */   }
/*       */ 
/*       */   public Date parseRfc822Date(String dateString)
/*       */     throws ParseException
/*       */   {
/*   110 */     synchronized (this.rfc822DateFormat) {
/*   111 */       return this.rfc822DateFormat.parse(dateString);
/*       */     }
/*       */   }
/*       */ 
/*       */   public String formatRfc822Date(Date date)
/*       */   {
/*   124 */     synchronized (this.rfc822DateFormat) {
/*   125 */       return this.rfc822DateFormat.format(date);
/*       */     }
/*       */   }
/*       */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.DateUtils
 * JD-Core Version:    0.6.2
 */