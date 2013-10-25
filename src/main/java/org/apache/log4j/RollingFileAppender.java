/*     */ package org.apache.log4j;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.Writer;
/*     */ import org.apache.log4j.helpers.CountingQuietWriter;
/*     */ import org.apache.log4j.helpers.LogLog;
/*     */ import org.apache.log4j.helpers.OptionConverter;
/*     */ import org.apache.log4j.spi.LoggingEvent;
/*     */ 
/*     */ public class RollingFileAppender extends FileAppender
/*     */ {
/*  50 */   protected long maxFileSize = 10485760L;
/*     */ 
/*  55 */   protected int maxBackupIndex = 1;
/*     */ 
/*  57 */   private long nextRollover = 0L;
/*     */ 
/*     */   public RollingFileAppender()
/*     */   {
/*     */   }
/*     */ 
/*     */   public RollingFileAppender(Layout layout, String filename, boolean append)
/*     */     throws IOException
/*     */   {
/*  79 */     super(layout, filename, append);
/*     */   }
/*     */ 
/*     */   public RollingFileAppender(Layout layout, String filename)
/*     */     throws IOException
/*     */   {
/*  90 */     super(layout, filename);
/*     */   }
/*     */ 
/*     */   public int getMaxBackupIndex()
/*     */   {
/*  98 */     return this.maxBackupIndex;
/*     */   }
/*     */ 
/*     */   public long getMaximumFileSize()
/*     */   {
/* 109 */     return this.maxFileSize;
/*     */   }
/*     */ 
/*     */   public void rollOver()
/*     */   {
/* 131 */     if (this.qw != null) {
/* 132 */       long size = ((CountingQuietWriter)this.qw).getCount();
/* 133 */       LogLog.debug("rolling over count=" + size);
/*     */ 
/* 136 */       this.nextRollover = (size + this.maxFileSize);
/*     */     }
/* 138 */     LogLog.debug("maxBackupIndex=" + this.maxBackupIndex);
/*     */ 
/* 140 */     boolean renameSucceeded = true;
/*     */ 
/* 142 */     if (this.maxBackupIndex > 0)
/*     */     {
/* 144 */       File file = new File(this.fileName + '.' + this.maxBackupIndex);
/* 145 */       if (file.exists()) {
/* 146 */         renameSucceeded = file.delete();
/*     */       }
/*     */ 
/* 149 */       for (int i = this.maxBackupIndex - 1; (i >= 1) && (renameSucceeded); i--) {
/* 150 */         file = new File(this.fileName + "." + i);
/* 151 */         if (file.exists()) {
/* 152 */           File target = new File(this.fileName + '.' + (i + 1));
/* 153 */           LogLog.debug("Renaming file " + file + " to " + target);
/* 154 */           renameSucceeded = file.renameTo(target);
/*     */         }
/*     */       }
/*     */ 
/* 158 */       if (renameSucceeded)
/*     */       {
/* 160 */         File target = new File(this.fileName + "." + 1);
/*     */ 
/* 162 */         closeFile();
/*     */ 
/* 164 */         file = new File(this.fileName);
/* 165 */         LogLog.debug("Renaming file " + file + " to " + target);
/* 166 */         renameSucceeded = file.renameTo(target);
/*     */ 
/* 170 */         if (!renameSucceeded) {
/*     */           try {
/* 172 */             setFile(this.fileName, true, this.bufferedIO, this.bufferSize);
/*     */           }
/*     */           catch (IOException e) {
/* 175 */             if ((e instanceof InterruptedIOException)) {
/* 176 */               Thread.currentThread().interrupt();
/*     */             }
/* 178 */             LogLog.error("setFile(" + this.fileName + ", true) call failed.", e);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 187 */     if (renameSucceeded)
/*     */     {
/*     */       try
/*     */       {
/* 191 */         setFile(this.fileName, false, this.bufferedIO, this.bufferSize);
/* 192 */         this.nextRollover = 0L;
/*     */       }
/*     */       catch (IOException e) {
/* 195 */         if ((e instanceof InterruptedIOException)) {
/* 196 */           Thread.currentThread().interrupt();
/*     */         }
/* 198 */         LogLog.error("setFile(" + this.fileName + ", false) call failed.", e);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
/*     */     throws IOException
/*     */   {
/* 207 */     super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
/* 208 */     if (append) {
/* 209 */       File f = new File(fileName);
/* 210 */       ((CountingQuietWriter)this.qw).setCount(f.length());
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setMaxBackupIndex(int maxBackups)
/*     */   {
/* 226 */     this.maxBackupIndex = maxBackups;
/*     */   }
/*     */ 
/*     */   public void setMaximumFileSize(long maxFileSize)
/*     */   {
/* 243 */     this.maxFileSize = maxFileSize;
/*     */   }
/*     */ 
/*     */   public void setMaxFileSize(String value)
/*     */   {
/* 260 */     this.maxFileSize = OptionConverter.toFileSize(value, this.maxFileSize + 1L);
/*     */   }
/*     */ 
/*     */   protected void setQWForFiles(Writer writer)
/*     */   {
/* 265 */     this.qw = new CountingQuietWriter(writer, this.errorHandler);
/*     */   }
/*     */ 
/*     */   protected void subAppend(LoggingEvent event)
/*     */   {
/* 276 */     super.subAppend(event);
/* 277 */     if ((this.fileName != null) && (this.qw != null)) {
/* 278 */       long size = ((CountingQuietWriter)this.qw).getCount();
/* 279 */       if ((size >= this.maxFileSize) && (size >= this.nextRollover))
/* 280 */         rollOver();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.RollingFileAppender
 * JD-Core Version:    0.6.2
 */