/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Splitter;
/*     */ import com.google.common.hash.HashCode;
/*     */ import com.google.common.hash.HashFunction;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.Closeable;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.nio.MappedByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.FileChannel.MapMode;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.zip.Checksum;
/*     */ 
/*     */ @Beta
/*     */ public final class Files
/*     */ {
/*     */   private static final int TEMP_DIR_ATTEMPTS = 10000;
/*     */ 
/*     */   public static BufferedReader newReader(File file, Charset charset)
/*     */     throws FileNotFoundException
/*     */   {
/*  77 */     return new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
/*     */   }
/*     */ 
/*     */   public static BufferedWriter newWriter(File file, Charset charset)
/*     */     throws FileNotFoundException
/*     */   {
/*  92 */     return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), charset));
/*     */   }
/*     */ 
/*     */   public static InputSupplier<FileInputStream> newInputStreamSupplier(File file)
/*     */   {
/* 105 */     Preconditions.checkNotNull(file);
/* 106 */     return new InputSupplier()
/*     */     {
/*     */       public FileInputStream getInput() throws IOException {
/* 109 */         return new FileInputStream(this.val$file);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static OutputSupplier<FileOutputStream> newOutputStreamSupplier(File file)
/*     */   {
/* 123 */     return newOutputStreamSupplier(file, false);
/*     */   }
/*     */ 
/*     */   public static OutputSupplier<FileOutputStream> newOutputStreamSupplier(File file, final boolean append)
/*     */   {
/* 137 */     Preconditions.checkNotNull(file);
/* 138 */     return new OutputSupplier()
/*     */     {
/*     */       public FileOutputStream getOutput() throws IOException {
/* 141 */         return new FileOutputStream(this.val$file, append);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static InputSupplier<InputStreamReader> newReaderSupplier(File file, Charset charset)
/*     */   {
/* 157 */     return CharStreams.newReaderSupplier(newInputStreamSupplier(file), charset);
/*     */   }
/*     */ 
/*     */   public static OutputSupplier<OutputStreamWriter> newWriterSupplier(File file, Charset charset)
/*     */   {
/* 171 */     return newWriterSupplier(file, charset, false);
/*     */   }
/*     */ 
/*     */   public static OutputSupplier<OutputStreamWriter> newWriterSupplier(File file, Charset charset, boolean append)
/*     */   {
/* 187 */     return CharStreams.newWriterSupplier(newOutputStreamSupplier(file, append), charset);
/*     */   }
/*     */ 
/*     */   public static byte[] toByteArray(File file)
/*     */     throws IOException
/*     */   {
/* 201 */     Preconditions.checkArgument(file.length() <= 2147483647L);
/* 202 */     if (file.length() == 0L)
/*     */     {
/* 204 */       return ByteStreams.toByteArray(newInputStreamSupplier(file));
/*     */     }
/*     */ 
/* 207 */     byte[] b = new byte[(int)file.length()];
/* 208 */     boolean threw = true;
/* 209 */     InputStream in = new FileInputStream(file);
/*     */     try {
/* 211 */       ByteStreams.readFully(in, b);
/* 212 */       threw = false;
/*     */     } finally {
/* 214 */       Closeables.close(in, threw);
/*     */     }
/* 216 */     return b;
/*     */   }
/*     */ 
/*     */   public static String toString(File file, Charset charset)
/*     */     throws IOException
/*     */   {
/* 231 */     return new String(toByteArray(file), charset.name());
/*     */   }
/*     */ 
/*     */   public static void copy(InputSupplier<? extends InputStream> from, File to)
/*     */     throws IOException
/*     */   {
/* 244 */     ByteStreams.copy(from, newOutputStreamSupplier(to));
/*     */   }
/*     */ 
/*     */   public static void write(byte[] from, File to)
/*     */     throws IOException
/*     */   {
/* 255 */     ByteStreams.write(from, newOutputStreamSupplier(to));
/*     */   }
/*     */ 
/*     */   public static void copy(File from, OutputSupplier<? extends OutputStream> to)
/*     */     throws IOException
/*     */   {
/* 268 */     ByteStreams.copy(newInputStreamSupplier(from), to);
/*     */   }
/*     */ 
/*     */   public static void copy(File from, OutputStream to)
/*     */     throws IOException
/*     */   {
/* 279 */     ByteStreams.copy(newInputStreamSupplier(from), to);
/*     */   }
/*     */ 
/*     */   public static void copy(File from, File to)
/*     */     throws IOException
/*     */   {
/* 291 */     Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", new Object[] { from, to });
/*     */ 
/* 293 */     copy(newInputStreamSupplier(from), to);
/*     */   }
/*     */ 
/*     */   public static <R extends Readable,  extends Closeable> void copy(InputSupplier<R> from, File to, Charset charset)
/*     */     throws IOException
/*     */   {
/* 309 */     CharStreams.copy(from, newWriterSupplier(to, charset));
/*     */   }
/*     */ 
/*     */   public static void write(CharSequence from, File to, Charset charset)
/*     */     throws IOException
/*     */   {
/* 324 */     write(from, to, charset, false);
/*     */   }
/*     */ 
/*     */   public static void append(CharSequence from, File to, Charset charset)
/*     */     throws IOException
/*     */   {
/* 339 */     write(from, to, charset, true);
/*     */   }
/*     */ 
/*     */   private static void write(CharSequence from, File to, Charset charset, boolean append)
/*     */     throws IOException
/*     */   {
/* 355 */     CharStreams.write(from, newWriterSupplier(to, charset, append));
/*     */   }
/*     */ 
/*     */   public static <W extends Appendable,  extends Closeable> void copy(File from, Charset charset, OutputSupplier<W> to)
/*     */     throws IOException
/*     */   {
/* 371 */     CharStreams.copy(newReaderSupplier(from, charset), to);
/*     */   }
/*     */ 
/*     */   public static void copy(File from, Charset charset, Appendable to)
/*     */     throws IOException
/*     */   {
/* 386 */     CharStreams.copy(newReaderSupplier(from, charset), to);
/*     */   }
/*     */ 
/*     */   public static boolean equal(File file1, File file2)
/*     */     throws IOException
/*     */   {
/* 395 */     if ((file1 == file2) || (file1.equals(file2))) {
/* 396 */       return true;
/*     */     }
/*     */ 
/* 404 */     long len1 = file1.length();
/* 405 */     long len2 = file2.length();
/* 406 */     if ((len1 != 0L) && (len2 != 0L) && (len1 != len2)) {
/* 407 */       return false;
/*     */     }
/* 409 */     return ByteStreams.equal(newInputStreamSupplier(file1), newInputStreamSupplier(file2));
/*     */   }
/*     */ 
/*     */   public static File createTempDir()
/*     */   {
/* 433 */     File baseDir = new File(System.getProperty("java.io.tmpdir"));
/* 434 */     String baseName = System.currentTimeMillis() + "-";
/*     */ 
/* 436 */     for (int counter = 0; counter < 10000; counter++) {
/* 437 */       File tempDir = new File(baseDir, baseName + counter);
/* 438 */       if (tempDir.mkdir()) {
/* 439 */         return tempDir;
/*     */       }
/*     */     }
/* 442 */     throw new IllegalStateException("Failed to create directory within 10000 attempts (tried " + baseName + "0 to " + baseName + 9999 + ')');
/*     */   }
/*     */ 
/*     */   public static void touch(File file)
/*     */     throws IOException
/*     */   {
/* 455 */     if ((!file.createNewFile()) && (!file.setLastModified(System.currentTimeMillis())))
/*     */     {
/* 457 */       throw new IOException("Unable to update modification time of " + file);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void createParentDirs(File file)
/*     */     throws IOException
/*     */   {
/* 472 */     File parent = file.getCanonicalFile().getParentFile();
/* 473 */     if (parent == null)
/*     */     {
/* 481 */       return;
/*     */     }
/* 483 */     parent.mkdirs();
/* 484 */     if (!parent.isDirectory())
/* 485 */       throw new IOException("Unable to create parent directories of " + file);
/*     */   }
/*     */ 
/*     */   public static void move(File from, File to)
/*     */     throws IOException
/*     */   {
/* 499 */     Preconditions.checkNotNull(to);
/* 500 */     Preconditions.checkArgument(!from.equals(to), "Source %s and destination %s must be different", new Object[] { from, to });
/*     */ 
/* 503 */     if (!from.renameTo(to)) {
/* 504 */       copy(from, to);
/* 505 */       if (!from.delete()) {
/* 506 */         if (!to.delete()) {
/* 507 */           throw new IOException("Unable to delete " + to);
/*     */         }
/* 509 */         throw new IOException("Unable to delete " + from);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String readFirstLine(File file, Charset charset)
/*     */     throws IOException
/*     */   {
/* 527 */     return CharStreams.readFirstLine(newReaderSupplier(file, charset));
/*     */   }
/*     */ 
/*     */   public static List<String> readLines(File file, Charset charset)
/*     */     throws IOException
/*     */   {
/* 543 */     return CharStreams.readLines(newReaderSupplier(file, charset));
/*     */   }
/*     */ 
/*     */   public static <T> T readLines(File file, Charset charset, LineProcessor<T> callback)
/*     */     throws IOException
/*     */   {
/* 559 */     return CharStreams.readLines(newReaderSupplier(file, charset), callback);
/*     */   }
/*     */ 
/*     */   public static <T> T readBytes(File file, ByteProcessor<T> processor)
/*     */     throws IOException
/*     */   {
/* 576 */     return ByteStreams.readBytes(newInputStreamSupplier(file), processor);
/*     */   }
/*     */ 
/*     */   public static long getChecksum(File file, Checksum checksum)
/*     */     throws IOException
/*     */   {
/* 591 */     return ByteStreams.getChecksum(newInputStreamSupplier(file), checksum);
/*     */   }
/*     */ 
/*     */   public static HashCode hash(File file, HashFunction hashFunction)
/*     */     throws IOException
/*     */   {
/* 605 */     return ByteStreams.hash(newInputStreamSupplier(file), hashFunction);
/*     */   }
/*     */ 
/*     */   public static MappedByteBuffer map(File file)
/*     */     throws IOException
/*     */   {
/* 625 */     return map(file, FileChannel.MapMode.READ_ONLY);
/*     */   }
/*     */ 
/*     */   public static MappedByteBuffer map(File file, FileChannel.MapMode mode)
/*     */     throws IOException
/*     */   {
/* 648 */     if (!file.exists()) {
/* 649 */       throw new FileNotFoundException(file.toString());
/*     */     }
/* 651 */     return map(file, mode, file.length());
/*     */   }
/*     */ 
/*     */   public static MappedByteBuffer map(File file, FileChannel.MapMode mode, long size)
/*     */     throws FileNotFoundException, IOException
/*     */   {
/* 677 */     RandomAccessFile raf = new RandomAccessFile(file, mode == FileChannel.MapMode.READ_ONLY ? "r" : "rw");
/*     */ 
/* 680 */     boolean threw = true;
/*     */     try {
/* 682 */       MappedByteBuffer mbb = map(raf, mode, size);
/* 683 */       threw = false;
/* 684 */       return mbb;
/*     */     } finally {
/* 686 */       Closeables.close(raf, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   private static MappedByteBuffer map(RandomAccessFile raf, FileChannel.MapMode mode, long size) throws IOException
/*     */   {
/* 692 */     FileChannel channel = raf.getChannel();
/*     */ 
/* 694 */     boolean threw = true;
/*     */     try {
/* 696 */       MappedByteBuffer mbb = channel.map(mode, 0L, size);
/* 697 */       threw = false;
/* 698 */       return mbb;
/*     */     } finally {
/* 700 */       Closeables.close(channel, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String simplifyPath(String pathname)
/*     */   {
/* 726 */     if (pathname.length() == 0) {
/* 727 */       return ".";
/*     */     }
/*     */ 
/* 731 */     Iterable components = Splitter.on('/').omitEmptyStrings().split(pathname);
/*     */ 
/* 733 */     List path = new ArrayList();
/*     */ 
/* 736 */     for (String component : components) {
/* 737 */       if (!component.equals("."))
/*     */       {
/* 739 */         if (component.equals("..")) {
/* 740 */           if ((path.size() > 0) && (!((String)path.get(path.size() - 1)).equals("..")))
/* 741 */             path.remove(path.size() - 1);
/*     */           else
/* 743 */             path.add("..");
/*     */         }
/*     */         else {
/* 746 */           path.add(component);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 751 */     String result = Joiner.on('/').join(path);
/* 752 */     if (pathname.charAt(0) == '/') {
/* 753 */       result = "/" + result;
/*     */     }
/*     */ 
/* 756 */     while (result.startsWith("/../")) {
/* 757 */       result = result.substring(3);
/*     */     }
/* 759 */     if (result.equals("/.."))
/* 760 */       result = "/";
/* 761 */     else if ("".equals(result)) {
/* 762 */       result = ".";
/*     */     }
/*     */ 
/* 765 */     return result;
/*     */   }
/*     */ 
/*     */   public static String getFileExtension(String fileName)
/*     */   {
/* 776 */     Preconditions.checkNotNull(fileName);
/* 777 */     int dotIndex = fileName.lastIndexOf(46);
/* 778 */     return dotIndex == -1 ? "" : fileName.substring(dotIndex + 1);
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.Files
 * JD-Core Version:    0.6.2
 */