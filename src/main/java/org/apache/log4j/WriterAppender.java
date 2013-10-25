/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Writer;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.QuietWriter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class WriterAppender extends AppenderSkeleton
/*     */ {
/*  57 */   protected boolean immediateFlush = true;
/*     */   protected String encoding;
/*     */   protected QuietWriter qw;
/*     */ 
/*     */   public WriterAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public WriterAppender(Layout layout, OutputStream os)
/*     */   {
/*  85 */     this(layout, new OutputStreamWriter(os));
/*     */   }
/*     */ 
/*     */   public WriterAppender(Layout layout, Writer writer)
/*     */   {
/*  96 */     this.layout = layout;
/*  97 */     setWriter(writer);
/*     */   }
/*     */ 
/*     */   public void setImmediateFlush(boolean value)
/*     */   {
/* 116 */     this.immediateFlush = value;
/*     */   }
/*     */ 
/*     */   public boolean getImmediateFlush()
/*     */   {
/* 124 */     return this.immediateFlush;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/*     */   }
/*     */ 
/*     */   public void append(LoggingEvent event)
/*     */   {
/* 159 */     if (!checkEntryConditions()) {
/* 160 */       return;
/*     */     }
/* 162 */     subAppend(event);
/*     */   }
/*     */ 
/*     */   protected boolean checkEntryConditions()
/*     */   {
/* 173 */     if (this.closed) {
/* 174 */       LogLog.warn("Not allowed to write to a closed appender.");
/* 175 */       return false;
/*     */     }
/*     */ 
/* 178 */     if (this.qw == null) {
/* 179 */       this.errorHandler.error("No output stream or file set for the appender named [" + this.name + "].");
/*     */ 
/* 181 */       return false;
/*     */     }
/*     */ 
/* 184 */     if (this.layout == null) {
/* 185 */       this.errorHandler.error("No layout set for the appender named [" + this.name + "].");
/* 186 */       return false;
/*     */     }
/* 188 */     return true;
/*     */   }
/*     */ 
/*     */   public synchronized void close()
/*     */   {
/* 203 */     if (this.closed)
/* 204 */       return;
/* 205 */     this.closed = true;
/* 206 */     writeFooter();
/* 207 */     reset();
/*     */   }
/*     */ 
/*     */   protected void closeWriter()
/*     */   {
/* 214 */     if (this.qw != null)
/*     */       try {
/* 216 */         this.qw.close();
/*     */       } catch (IOException e) {
/* 218 */         if ((e instanceof InterruptedIOException)) {
/* 219 */           Thread.currentThread().interrupt();
/*     */         }
/*     */ 
/* 223 */         LogLog.error("Could not close " + this.qw, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   protected OutputStreamWriter createWriter(OutputStream os)
/*     */   {
/* 236 */     OutputStreamWriter retval = null;
/*     */ 
/* 238 */     String enc = getEncoding();
/* 239 */     if (enc != null) {
/*     */       try {
/* 241 */         retval = new OutputStreamWriter(os, enc);
/*     */       } catch (IOException e) {
/* 243 */         if ((e instanceof InterruptedIOException)) {
/* 244 */           Thread.currentThread().interrupt();
/*     */         }
/* 246 */         LogLog.warn("Error initializing output writer.");
/* 247 */         LogLog.warn("Unsupported encoding?");
/*     */       }
/*     */     }
/* 250 */     if (retval == null) {
/* 251 */       retval = new OutputStreamWriter(os);
/*     */     }
/* 253 */     return retval;
/*     */   }
/*     */ 
/*     */   public String getEncoding() {
/* 257 */     return this.encoding;
/*     */   }
/*     */ 
/*     */   public void setEncoding(String value) {
/* 261 */     this.encoding = value;
/*     */   }
/*     */ 
/*     */   public synchronized void setErrorHandler(ErrorHandler eh)
/*     */   {
/* 271 */     if (eh == null) {
/* 272 */       LogLog.warn("You have tried to set a null error-handler.");
/*     */     } else {
/* 274 */       this.errorHandler = eh;
/* 275 */       if (this.qw != null)
/* 276 */         this.qw.setErrorHandler(eh);
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setWriter(Writer writer)
/*     */   {
/* 294 */     reset();
/* 295 */     this.qw = new QuietWriter(writer, this.errorHandler);
/*     */ 
/* 297 */     writeHeader();
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 310 */     this.qw.write(this.layout.format(event));
/*     */ 
/* 312 */     if (this.layout.ignoresThrowable()) {
/* 313 */       String[] s = event.getThrowableStrRep();
/* 314 */       if (s != null) {
/* 315 */         int len = s.length;
/* 316 */         for (int i = 0; i < len; i++) {
/* 317 */           this.qw.write(s[i]);
/* 318 */           this.qw.write(Layout.LINE_SEP);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 323 */     if (shouldFlush(event))
/* 324 */       this.qw.flush();
/*     */   }
/*     */ 
/*     */   public boolean requiresLayout()
/*     */   {
/* 336 */     return true;
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 346 */     closeWriter();
/* 347 */     this.qw = null;
/*     */   }
/*     */ 
/*     */   protected void writeFooter()
/*     */   {
/* 357 */     if (this.layout != null) {
/* 358 */       String f = this.layout.getFooter();
/* 359 */       if ((f != null) && (this.qw != null)) {
/* 360 */         this.qw.write(f);
/* 361 */         this.qw.flush();
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void writeHeader()
/*     */   {
/* 371 */     if (this.layout != null) {
/* 372 */       String h = this.layout.getHeader();
/* 373 */       if ((h != null) && (this.qw != null))
/* 374 */         this.qw.write(h);
/*     */     }
/*     */   }
/*     */ 
/*     */   protected boolean shouldFlush(LoggingEvent event)
/*     */   {
/* 385 */     return this.immediateFlush;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.WriterAppender
 * JD-Core Version:    0.6.2
 */