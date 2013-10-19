/*     */ package com.google.common.net;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.net.InetAddress;
/*     */ import java.text.ParseException;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ public final class HostSpecifier
/*     */ {
/*     */   private final String canonicalForm;
/*     */ 
/*     */   private HostSpecifier(String canonicalForm)
/*     */   {
/*  57 */     this.canonicalForm = canonicalForm;
/*     */   }
/*     */ 
/*     */   public static HostSpecifier fromValid(String specifier)
/*     */   {
/*  78 */     HostAndPort parsedHost = HostAndPort.fromString(specifier);
/*  79 */     Preconditions.checkArgument(!parsedHost.hasPort());
/*  80 */     String host = parsedHost.getHostText();
/*     */ 
/*  86 */     InetAddress addr = null;
/*     */     try {
/*  88 */       addr = InetAddresses.forString(host);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/*     */     }
/*  93 */     if (addr != null) {
/*  94 */       return new HostSpecifier(InetAddresses.toUriString(addr));
/*     */     }
/*     */ 
/* 100 */     InternetDomainName domain = InternetDomainName.from(host);
/*     */ 
/* 102 */     if (domain.hasPublicSuffix()) {
/* 103 */       return new HostSpecifier(domain.name());
/*     */     }
/*     */ 
/* 106 */     throw new IllegalArgumentException("Domain name does not have a recognized public suffix: " + host);
/*     */   }
/*     */ 
/*     */   public static HostSpecifier from(String specifier)
/*     */     throws ParseException
/*     */   {
/*     */     try
/*     */     {
/* 121 */       return fromValid(specifier);
/*     */     }
/*     */     catch (IllegalArgumentException e)
/*     */     {
/* 127 */       ParseException parseException = new ParseException("Invalid host specifier: " + specifier, 0);
/*     */ 
/* 129 */       parseException.initCause(e);
/* 130 */       throw parseException;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean isValid(String specifier)
/*     */   {
/*     */     try
/*     */     {
/* 141 */       fromValid(specifier);
/* 142 */       return true; } catch (IllegalArgumentException e) {
/*     */     }
/* 144 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object other)
/*     */   {
/* 150 */     if (this == other) {
/* 151 */       return true;
/*     */     }
/*     */ 
/* 154 */     if ((other instanceof HostSpecifier)) {
/* 155 */       HostSpecifier that = (HostSpecifier)other;
/* 156 */       return this.canonicalForm.equals(that.canonicalForm);
/*     */     }
/*     */ 
/* 159 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 164 */     return this.canonicalForm.hashCode();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 176 */     return this.canonicalForm;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.net.HostSpecifier
 * JD-Core Version:    0.6.2
 */