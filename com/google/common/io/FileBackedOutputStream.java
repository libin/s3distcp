/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ 
/*     */ @Beta
/*     */ public final class FileBackedOutputStream extends OutputStream
/*     */ {
/*     */   private final int fileThreshold;
/*     */   private final boolean resetOnFinalize;
/*     */   private final InputSupplier<InputStream> supplier;
/*     */   private OutputStream out;
/*     */   private MemoryOutput memory;
/*     */   private File file;
/*     */ 
/*     */   @VisibleForTesting
/*     */   synchronized File getFile()
/*     */   {
/*  64 */     return this.file;
/*     */   }
/*     */ 
/*     */   public FileBackedOutputStream(int fileThreshold)
/*     */   {
/*  76 */     this(fileThreshold, false);
/*     */   }
/*     */ 
/*     */   public FileBackedOutputStream(int fileThreshold, boolean resetOnFinalize)
/*     */   {
/*  91 */     this.fileThreshold = fileThreshold;
/*  92 */     this.resetOnFinalize = resetOnFinalize;
/*  93 */     this.memory = new MemoryOutput(null);
/*  94 */     this.out = this.memory;
/*     */ 
/*  96 */     if (resetOnFinalize)
/*  97 */       this.supplier = new InputSupplier()
/*     */       {
/*     */         public InputStream getInput() throws IOException {
/* 100 */           return FileBackedOutputStream.this.openStream();
/*     */         }
/*     */ 
/*     */         protected void finalize() {
/*     */           try {
/* 105 */             FileBackedOutputStream.this.reset();
/*     */           } catch (Throwable t) {
/* 107 */             t.printStackTrace(System.err);
/*     */           }
/*     */         }
/*     */       };
/*     */     else
/* 112 */       this.supplier = new InputSupplier()
/*     */       {
/*     */         public InputStream getInput() throws IOException {
/* 115 */           return FileBackedOutputStream.this.openStream();
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   public InputSupplier<InputStream> getSupplier()
/*     */   {
/* 126 */     return this.supplier;
/*     */   }
/*     */ 
/*     */   private synchronized InputStream openStream() throws IOException {
/* 130 */     if (this.file != null) {
/* 131 */       return new FileInputStream(this.file);
/*     */     }
/* 133 */     return new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount());
/*     */   }
/*     */ 
/*     */   public synchronized void reset()
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 147 */       close();
/*     */     }
/*     */     finally
/*     */     {
/*     */       File deleteMe;
/* 149 */       if (this.memory == null)
/* 150 */         this.memory = new MemoryOutput(null);
/*     */       else {
/* 152 */         this.memory.reset();
/*     */       }
/* 154 */       this.out = this.memory;
/* 155 */       if (this.file != null) {
/* 156 */         File deleteMe = this.file;
/* 157 */         this.file = null;
/* 158 */         if (!deleteMe.delete())
/* 159 */           throw new IOException("Could not delete: " + deleteMe);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public synchronized void write(int b) throws IOException
/*     */   {
/* 166 */     update(1);
/* 167 */     this.out.write(b);
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] b) throws IOException {
/* 171 */     write(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public synchronized void write(byte[] b, int off, int len) throws IOException
/*     */   {
/* 176 */     update(len);
/* 177 */     this.out.write(b, off, len);
/*     */   }
/*     */ 
/*     */   public synchronized void close() throws IOException {
/* 181 */     this.out.close();
/*     */   }
/*     */ 
/*     */   public synchronized void flush() throws IOException {
/* 185 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   private void update(int len)
/*     */     throws IOException
/*     */   {
/* 193 */     if ((this.file == null) && (this.memory.getCount() + len > this.fileThreshold)) {
/* 194 */       File temp = File.createTempFile("FileBackedOutputStream", null);
/* 195 */       if (this.resetOnFinalize)
/*     */       {
/* 198 */         temp.deleteOnExit();
/*     */       }
/* 200 */       FileOutputStream transfer = new FileOutputStream(temp);
/* 201 */       transfer.write(this.memory.getBuffer(), 0, this.memory.getCount());
/* 202 */       transfer.flush();
/*     */ 
/* 205 */       this.out = transfer;
/* 206 */       this.file = temp;
/* 207 */       this.memory = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class MemoryOutput extends ByteArrayOutputStream
/*     */   {
/*     */     byte[] getBuffer()
/*     */     {
/*  54 */       return this.buf;
/*     */     }
/*     */ 
/*     */     int getCount() {
/*  58 */       return this.count;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.FileBackedOutputStream
 * JD-Core Version:    0.6.2
 */