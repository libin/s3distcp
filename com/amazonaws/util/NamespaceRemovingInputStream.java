/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ class NamespaceRemovingInputStream extends FilterInputStream
/*     */ {
/*  29 */   private byte[] lookAheadData = new byte['È'];
/*     */ 
/*  32 */   private boolean hasRemovedNamespace = false;
/*     */ 
/*     */   public NamespaceRemovingInputStream(InputStream in)
/*     */   {
/*  45 */     super(new BufferedInputStream(in));
/*     */   }
/*     */ 
/*     */   public int read()
/*     */     throws IOException
/*     */   {
/*  53 */     int b = this.in.read();
/*  54 */     if ((b == 120) && (!this.hasRemovedNamespace)) {
/*  55 */       this.lookAheadData[0] = ((byte)b);
/*  56 */       this.in.mark(this.lookAheadData.length);
/*  57 */       int bytesRead = this.in.read(this.lookAheadData, 1, this.lookAheadData.length - 1);
/*  58 */       this.in.reset();
/*     */ 
/*  60 */       String string = new String(this.lookAheadData, 0, bytesRead + 1);
/*     */ 
/*  62 */       int numberCharsMatched = matchXmlNamespaceAttribute(string);
/*  63 */       if (numberCharsMatched > 0) {
/*  64 */         for (int i = 0; i < numberCharsMatched - 1; i++) {
/*  65 */           this.in.read();
/*     */         }
/*  67 */         b = this.in.read();
/*  68 */         this.hasRemovedNamespace = true;
/*     */       }
/*     */     }
/*     */ 
/*  72 */     return b;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/*  80 */     for (int i = 0; i < len; i++) {
/*  81 */       int j = read();
/*  82 */       if (j == -1) {
/*  83 */         if (i == 0) return -1;
/*  84 */         return i;
/*     */       }
/*     */ 
/*  87 */       b[(i + off)] = ((byte)j);
/*     */     }
/*     */ 
/*  90 */     return len;
/*     */   }
/*     */ 
/*     */   public int read(byte[] b)
/*     */     throws IOException
/*     */   {
/*  98 */     return read(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   private int matchXmlNamespaceAttribute(String s)
/*     */   {
/* 115 */     StringPrefixSlicer stringSlicer = new StringPrefixSlicer(s);
/* 116 */     if (!stringSlicer.removePrefix("xmlns")) return -1;
/*     */ 
/* 118 */     stringSlicer.removeRepeatingPrefix(" ");
/* 119 */     if (!stringSlicer.removePrefix("=")) return -1;
/* 120 */     stringSlicer.removeRepeatingPrefix(" ");
/*     */ 
/* 122 */     if (!stringSlicer.removePrefix("\"")) return -1;
/* 123 */     if (!stringSlicer.removePrefixEndingWith("\"")) return -1;
/*     */ 
/* 125 */     return s.length() - stringSlicer.getString().length();
/*     */   }
/*     */ 
/*     */   private static final class StringPrefixSlicer
/*     */   {
/*     */     private String s;
/*     */ 
/*     */     public StringPrefixSlicer(String s)
/*     */     {
/* 135 */       this.s = s;
/*     */     }
/*     */ 
/*     */     public String getString()
/*     */     {
/* 143 */       return this.s;
/*     */     }
/*     */ 
/*     */     public boolean removePrefix(String prefix) {
/* 147 */       if (!this.s.startsWith(prefix)) return false;
/* 148 */       this.s = this.s.substring(prefix.length());
/* 149 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean removeRepeatingPrefix(String prefix) {
/* 153 */       if (!this.s.startsWith(prefix)) return false;
/*     */ 
/* 155 */       while (this.s.startsWith(prefix)) {
/* 156 */         this.s = this.s.substring(prefix.length());
/*     */       }
/* 158 */       return true;
/*     */     }
/*     */ 
/*     */     public boolean removePrefixEndingWith(String marker) {
/* 162 */       int i = this.s.indexOf(marker);
/* 163 */       if (i < 0) return false;
/* 164 */       this.s = this.s.substring(i + marker.length());
/* 165 */       return true;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.NamespaceRemovingInputStream
 * JD-Core Version:    0.6.2
 */