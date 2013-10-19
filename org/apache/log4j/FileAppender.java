/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.Writer;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.QuietWriter;
/*     */ import org.apache.log4j.spi.ErrorHandler;
/*     */ 
/*     */ public class FileAppender extends WriterAppender
/*     */ {
/*  54 */   protected boolean fileAppend = true;
/*     */ 
/*  58 */   protected String fileName = null;
/*     */ 
/*  62 */   protected boolean bufferedIO = false;
/*     */ 
/*  67 */   protected int bufferSize = 8192;
/*     */ 
/*     */   public FileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/*  93 */     this.layout = layout;
/*  94 */     setFile(filename, append, bufferedIO, bufferSize);
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename, boolean append)
/*     */     throws IOException
/*     */   {
/* 109 */     this.layout = layout;
/* 110 */     setFile(filename, append, false, this.bufferSize);
/*     */   }
/*     */ 
/*     */   public FileAppender(Layout layout, String filename)
/*     */     throws IOException
/*     */   {
/* 121 */     this(layout, filename, true);
/*     */   }
/*     */ 
/*     */   public void setFile(String file)
/*     */   {
/* 136 */     String val = file.trim();
/* 137 */     this.fileName = val;
/*     */   }
/*     */ 
/*     */   public boolean getAppend()
/*     */   {
/* 145 */     return this.fileAppend;
/*     */   }
/*     */ 
/*     */   public String getFile()
/*     */   {
/* 152 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   public void activateOptions()
/*     */   {
/* 163 */     if (this.fileName != null) {
/*     */       try {
/* 165 */         setFile(this.fileName, this.fileAppend, this.bufferedIO, this.bufferSize);
/*     */       }
/*     */       catch (IOException e) {
/* 168 */         this.errorHandler.error("setFile(" + this.fileName + "," + this.fileAppend + ") call failed.", e, 4);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 173 */       LogLog.warn("File option not set for appender [" + this.name + "].");
/* 174 */       LogLog.warn("Are you using FileAppender instead of ConsoleAppender?");
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void closeFile()
/*     */   {
/* 183 */     if (this.qw != null)
/*     */       try {
/* 185 */         this.qw.close();
/*     */       }
/*     */       catch (IOException e) {
/* 188 */         if ((e instanceof InterruptedIOException)) {
/* 189 */           Thread.currentThread().interrupt();
/*     */         }
/*     */ 
/* 193 */         LogLog.error("Could not close " + this.qw, e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public boolean getBufferedIO()
/*     */   {
/* 207 */     return this.bufferedIO;
/*     */   }
/*     */ 
/*     */   public int getBufferSize()
/*     */   {
/* 216 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   public void setAppend(boolean flag)
/*     */   {
/* 233 */     this.fileAppend = flag;
/*     */   }
/*     */ 
/*     */   public void setBufferedIO(boolean bufferedIO)
/*     */   {
/* 248 */     this.bufferedIO = bufferedIO;
/* 249 */     if (bufferedIO)
/* 250 */       this.immediateFlush = false;
/*     */   }
/*     */ 
/*     */   public void setBufferSize(int bufferSize)
/*     */   {
/* 260 */     this.bufferSize = bufferSize;
/*     */   }
/*     */ 
/*     */   public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 281 */     LogLog.debug("setFile called: " + fileName + ", " + append);
/*     */ 
/* 284 */     if (bufferedIO) {
/* 285 */       setImmediateFlush(false);
/*     */     }
/*     */ 
/* 288 */     reset();
/* 289 */     FileOutputStream ostream = null;
/*     */     try
/*     */     {
/* 294 */       ostream = new FileOutputStream(fileName, append);
/*     */     }
/*     */     catch (FileNotFoundException ex)
/*     */     {
/* 301 */       String parentName = new File(fileName).getParent();
/* 302 */       if (parentName != null) {
/* 303 */         File parentDir = new File(parentName);
/* 304 */         if ((!parentDir.exists()) && (parentDir.mkdirs()))
/* 305 */           ostream = new FileOutputStream(fileName, append);
/*     */         else
/* 307 */           throw ex;
/*     */       }
/*     */       else {
/* 310 */         throw ex;
/*     */       }
/*     */     }
/* 313 */     Writer fw = createWriter(ostream);
/* 314 */     if (bufferedIO) {
/* 315 */       fw = new BufferedWriter(fw, bufferSize);
/*     */     }
/* 317 */     setQWForFiles(fw);
/* 318 */     this.fileName = fileName;
/* 319 */     this.fileAppend = append;
/* 320 */     this.bufferedIO = bufferedIO;
/* 321 */     this.bufferSize = bufferSize;
/* 322 */     writeHeader();
/* 323 */     LogLog.debug("setFile ended");
/*     */   }
/*     */ 
/*     */   protected void setQWForFiles(Writer writer)
/*     */   {
/* 334 */     this.qw = new QuietWriter(writer, this.errorHandler);
/*     */   }
/*     */ 
/*     */   protected void reset()
/*     */   {
/* 343 */     closeFile();
/* 344 */     this.fileName = null;
/* 345 */     super.reset();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.FileAppender
 * JD-Core Version:    0.6.2
 */