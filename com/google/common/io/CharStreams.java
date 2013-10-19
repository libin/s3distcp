/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Closeable;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.Writer;
/*     */ import java.nio.CharBuffer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ 
/*     */ @Beta
/*     */ public final class CharStreams
/*     */ {
/*     */   private static final int BUF_SIZE = 2048;
/*     */ 
/*     */   public static InputSupplier<StringReader> newReaderSupplier(String value)
/*     */   {
/*  68 */     Preconditions.checkNotNull(value);
/*  69 */     return new InputSupplier()
/*     */     {
/*     */       public StringReader getInput() {
/*  72 */         return new StringReader(this.val$value);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static InputSupplier<InputStreamReader> newReaderSupplier(InputSupplier<? extends InputStream> in, final Charset charset)
/*     */   {
/*  88 */     Preconditions.checkNotNull(in);
/*  89 */     Preconditions.checkNotNull(charset);
/*  90 */     return new InputSupplier()
/*     */     {
/*     */       public InputStreamReader getInput() throws IOException {
/*  93 */         return new InputStreamReader((InputStream)this.val$in.getInput(), charset);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static OutputSupplier<OutputStreamWriter> newWriterSupplier(OutputSupplier<? extends OutputStream> out, final Charset charset)
/*     */   {
/* 109 */     Preconditions.checkNotNull(out);
/* 110 */     Preconditions.checkNotNull(charset);
/* 111 */     return new OutputSupplier()
/*     */     {
/*     */       public OutputStreamWriter getOutput() throws IOException {
/* 114 */         return new OutputStreamWriter((OutputStream)this.val$out.getOutput(), charset);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <W extends Appendable,  extends Closeable> void write(CharSequence from, OutputSupplier<W> to)
/*     */     throws IOException
/*     */   {
/* 129 */     Preconditions.checkNotNull(from);
/* 130 */     boolean threw = true;
/* 131 */     Appendable out = (Appendable)to.getOutput();
/*     */     try {
/* 133 */       out.append(from);
/* 134 */       threw = false;
/*     */     } finally {
/* 136 */       Closeables.close((Closeable)out, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <R extends Readable,  extends Closeable, W extends Appendable,  extends Closeable> long copy(InputSupplier<R> from, OutputSupplier<W> to)
/*     */     throws IOException
/*     */   {
/* 153 */     int successfulOps = 0;
/* 154 */     Readable in = (Readable)from.getInput();
/*     */     try {
/* 156 */       Appendable out = (Appendable)to.getOutput();
/*     */       try {
/* 158 */         long count = copy(in, out);
/* 159 */         successfulOps++;
/* 160 */         long l1 = count;
/*     */ 
/* 162 */         Closeables.close((Closeable)out, successfulOps < 1);
/* 163 */         successfulOps++;
/*     */ 
/* 166 */         Closeables.close((Closeable)in, successfulOps < 2); return l1;
/*     */       }
/*     */       finally
/*     */       {
/* 162 */         Closeables.close((Closeable)out, successfulOps < 1);
/* 163 */         successfulOps++;
/*     */       }
/*     */     } finally {
/* 166 */       Closeables.close((Closeable)in, successfulOps < 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <R extends Readable,  extends Closeable> long copy(InputSupplier<R> from, Appendable to)
/*     */     throws IOException
/*     */   {
/* 182 */     boolean threw = true;
/* 183 */     Readable in = (Readable)from.getInput();
/*     */     try {
/* 185 */       long count = copy(in, to);
/* 186 */       threw = false;
/* 187 */       return count;
/*     */     } finally {
/* 189 */       Closeables.close((Closeable)in, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static long copy(Readable from, Appendable to)
/*     */     throws IOException
/*     */   {
/* 203 */     CharBuffer buf = CharBuffer.allocate(2048);
/* 204 */     long total = 0L;
/*     */     while (true) {
/* 206 */       int r = from.read(buf);
/* 207 */       if (r == -1) {
/*     */         break;
/*     */       }
/* 210 */       buf.flip();
/* 211 */       to.append(buf, 0, r);
/* 212 */       total += r;
/*     */     }
/* 214 */     return total;
/*     */   }
/*     */ 
/*     */   public static String toString(Readable r)
/*     */     throws IOException
/*     */   {
/* 226 */     return toStringBuilder(r).toString();
/*     */   }
/*     */ 
/*     */   public static <R extends Readable,  extends Closeable> String toString(InputSupplier<R> supplier)
/*     */     throws IOException
/*     */   {
/* 239 */     return toStringBuilder(supplier).toString();
/*     */   }
/*     */ 
/*     */   private static StringBuilder toStringBuilder(Readable r)
/*     */     throws IOException
/*     */   {
/* 251 */     StringBuilder sb = new StringBuilder();
/* 252 */     copy(r, sb);
/* 253 */     return sb;
/*     */   }
/*     */ 
/*     */   private static <R extends Readable,  extends Closeable> StringBuilder toStringBuilder(InputSupplier<R> supplier)
/*     */     throws IOException
/*     */   {
/* 265 */     boolean threw = true;
/* 266 */     Readable r = (Readable)supplier.getInput();
/*     */     try {
/* 268 */       StringBuilder result = toStringBuilder(r);
/* 269 */       threw = false;
/* 270 */       return result;
/*     */     } finally {
/* 272 */       Closeables.close((Closeable)r, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <R extends Readable,  extends Closeable> String readFirstLine(InputSupplier<R> supplier)
/*     */     throws IOException
/*     */   {
/* 287 */     boolean threw = true;
/* 288 */     Readable r = (Readable)supplier.getInput();
/*     */     try {
/* 290 */       String line = new LineReader(r).readLine();
/* 291 */       threw = false;
/* 292 */       return line;
/*     */     } finally {
/* 294 */       Closeables.close((Closeable)r, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <R extends Readable,  extends Closeable> List<String> readLines(InputSupplier<R> supplier)
/*     */     throws IOException
/*     */   {
/* 309 */     boolean threw = true;
/* 310 */     Readable r = (Readable)supplier.getInput();
/*     */     try {
/* 312 */       List result = readLines(r);
/* 313 */       threw = false;
/* 314 */       return result;
/*     */     } finally {
/* 316 */       Closeables.close((Closeable)r, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static List<String> readLines(Readable r)
/*     */     throws IOException
/*     */   {
/* 334 */     List result = new ArrayList();
/* 335 */     LineReader lineReader = new LineReader(r);
/*     */     String line;
/* 337 */     while ((line = lineReader.readLine()) != null) {
/* 338 */       result.add(line);
/*     */     }
/* 340 */     return result;
/*     */   }
/*     */ 
/*     */   public static <R extends Readable,  extends Closeable, T> T readLines(InputSupplier<R> supplier, LineProcessor<T> callback)
/*     */     throws IOException
/*     */   {
/* 355 */     boolean threw = true;
/* 356 */     Readable r = (Readable)supplier.getInput();
/*     */     try {
/* 358 */       LineReader lineReader = new LineReader(r);
/*     */       String line;
/* 360 */       while ((line = lineReader.readLine()) != null) {
/* 361 */         if (!callback.processLine(line)) {
/* 362 */           break;
/*     */         }
/*     */       }
/* 365 */       threw = false;
/*     */     } finally {
/* 367 */       Closeables.close((Closeable)r, threw);
/*     */     }
/* 369 */     return callback.getResult();
/*     */   }
/*     */ 
/*     */   public static InputSupplier<Reader> join(Iterable<? extends InputSupplier<? extends Reader>> suppliers)
/*     */   {
/* 389 */     return new InputSupplier() {
/*     */       public Reader getInput() throws IOException {
/* 391 */         return new MultiReader(this.val$suppliers.iterator());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static InputSupplier<Reader> join(InputSupplier<? extends Reader>[] suppliers)
/*     */   {
/* 399 */     return join(Arrays.asList(suppliers));
/*     */   }
/*     */ 
/*     */   public static void skipFully(Reader reader, long n)
/*     */     throws IOException
/*     */   {
/* 414 */     while (n > 0L) {
/* 415 */       long amt = reader.skip(n);
/* 416 */       if (amt == 0L)
/*     */       {
/* 418 */         if (reader.read() == -1) {
/* 419 */           throw new EOFException();
/*     */         }
/* 421 */         n -= 1L;
/*     */       } else {
/* 423 */         n -= amt;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static Writer asWriter(Appendable target)
/*     */   {
/* 439 */     if ((target instanceof Writer)) {
/* 440 */       return (Writer)target;
/*     */     }
/* 442 */     return new AppendableWriter(target);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.CharStreams
 * JD-Core Version:    0.6.2
 */