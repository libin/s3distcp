/*     */ package com.google.common.io;
/*     */ 
/*     */ import java.io.IOException;
/*     */ 
/*     */ abstract class LineBuffer
/*     */ {
/*  35 */   private StringBuilder line = new StringBuilder();
/*     */   private boolean sawReturn;
/*     */ 
/*     */   protected void add(char[] cbuf, int off, int len)
/*     */     throws IOException
/*     */   {
/*  51 */     int pos = off;
/*  52 */     if ((this.sawReturn) && (len > 0))
/*     */     {
/*  54 */       if (finishLine(cbuf[pos] == '\n')) {
/*  55 */         pos++;
/*     */       }
/*     */     }
/*     */ 
/*  59 */     int start = pos;
/*  60 */     for (int end = off + len; pos < end; pos++) {
/*  61 */       switch (cbuf[pos]) {
/*     */       case '\r':
/*  63 */         this.line.append(cbuf, start, pos - start);
/*  64 */         this.sawReturn = true;
/*  65 */         if (pos + 1 < end) {
/*  66 */           if (finishLine(cbuf[(pos + 1)] == '\n')) {
/*  67 */             pos++;
/*     */           }
/*     */         }
/*  70 */         start = pos + 1;
/*  71 */         break;
/*     */       case '\n':
/*  74 */         this.line.append(cbuf, start, pos - start);
/*  75 */         finishLine(true);
/*  76 */         start = pos + 1;
/*     */       }
/*     */     }
/*     */ 
/*  80 */     this.line.append(cbuf, start, off + len - start);
/*     */   }
/*     */ 
/*     */   private boolean finishLine(boolean sawNewline) throws IOException
/*     */   {
/*  85 */     handleLine(this.line.toString(), sawNewline ? "\n" : this.sawReturn ? "\r" : sawNewline ? "\r\n" : "");
/*     */ 
/*  88 */     this.line = new StringBuilder();
/*  89 */     this.sawReturn = false;
/*  90 */     return sawNewline;
/*     */   }
/*     */ 
/*     */   protected void finish()
/*     */     throws IOException
/*     */   {
/* 101 */     if ((this.sawReturn) || (this.line.length() > 0))
/* 102 */       finishLine(false);
/*     */   }
/*     */ 
/*     */   protected abstract void handleLine(String paramString1, String paramString2)
/*     */     throws IOException;
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.LineBuffer
 * JD-Core Version:    0.6.2
 */