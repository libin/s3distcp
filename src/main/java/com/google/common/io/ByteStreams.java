/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.hash.HashCode;
/*     */ import com.google.common.hash.HashFunction;
/*     */ import com.google.common.hash.Hasher;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.DataOutput;
/*     */ import java.io.DataOutputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.Arrays;
/*     */ import java.util.zip.Checksum;
/*     */ 
/*     */ @Beta
/*     */ public final class ByteStreams
/*     */ {
/*     */   private static final int BUF_SIZE = 4096;
/*     */ 
/*     */   public static InputSupplier<ByteArrayInputStream> newInputStreamSupplier(byte[] b)
/*     */   {
/*  64 */     return newInputStreamSupplier(b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public static InputSupplier<ByteArrayInputStream> newInputStreamSupplier(byte[] b, final int off, final int len)
/*     */   {
/*  78 */     return new InputSupplier()
/*     */     {
/*     */       public ByteArrayInputStream getInput() {
/*  81 */         return new ByteArrayInputStream(this.val$b, off, len);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static void write(byte[] from, OutputSupplier<? extends OutputStream> to)
/*     */     throws IOException
/*     */   {
/*  95 */     Preconditions.checkNotNull(from);
/*  96 */     boolean threw = true;
/*  97 */     OutputStream out = (OutputStream)to.getOutput();
/*     */     try {
/*  99 */       out.write(from);
/* 100 */       threw = false;
/*     */     } finally {
/* 102 */       Closeables.close(out, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static long copy(InputSupplier<? extends InputStream> from, OutputSupplier<? extends OutputStream> to)
/*     */     throws IOException
/*     */   {
/* 117 */     int successfulOps = 0;
/* 118 */     InputStream in = (InputStream)from.getInput();
/*     */     try {
/* 120 */       OutputStream out = (OutputStream)to.getOutput();
/*     */       try {
/* 122 */         long count = copy(in, out);
/* 123 */         successfulOps++;
/* 124 */         long l1 = count;
/*     */ 
/* 126 */         Closeables.close(out, successfulOps < 1);
/* 127 */         successfulOps++;
/*     */ 
/* 130 */         Closeables.close(in, successfulOps < 2); return l1;
/*     */       }
/*     */       finally
/*     */       {
/* 126 */         Closeables.close(out, successfulOps < 1);
/* 127 */         successfulOps++;
/*     */       }
/*     */     } finally {
/* 130 */       Closeables.close(in, successfulOps < 2);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static long copy(InputSupplier<? extends InputStream> from, OutputStream to)
/*     */     throws IOException
/*     */   {
/* 146 */     boolean threw = true;
/* 147 */     InputStream in = (InputStream)from.getInput();
/*     */     try {
/* 149 */       long count = copy(in, to);
/* 150 */       threw = false;
/* 151 */       return count;
/*     */     } finally {
/* 153 */       Closeables.close(in, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static long copy(InputStream from, OutputSupplier<? extends OutputStream> to)
/*     */     throws IOException
/*     */   {
/* 170 */     boolean threw = true;
/* 171 */     OutputStream out = (OutputStream)to.getOutput();
/*     */     try {
/* 173 */       long count = copy(from, out);
/* 174 */       threw = false;
/* 175 */       return count;
/*     */     } finally {
/* 177 */       Closeables.close(out, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static long copy(InputStream from, OutputStream to)
/*     */     throws IOException
/*     */   {
/* 192 */     byte[] buf = new byte[4096];
/* 193 */     long total = 0L;
/*     */     while (true) {
/* 195 */       int r = from.read(buf);
/* 196 */       if (r == -1) {
/*     */         break;
/*     */       }
/* 199 */       to.write(buf, 0, r);
/* 200 */       total += r;
/*     */     }
/* 202 */     return total;
/*     */   }
/*     */ 
/*     */   public static long copy(ReadableByteChannel from, WritableByteChannel to)
/*     */     throws IOException
/*     */   {
/* 216 */     ByteBuffer buf = ByteBuffer.allocate(4096);
/* 217 */     long total = 0L;
/* 218 */     while (from.read(buf) != -1) {
/* 219 */       buf.flip();
/* 220 */       while (buf.hasRemaining()) {
/* 221 */         total += to.write(buf);
/*     */       }
/* 223 */       buf.clear();
/*     */     }
/* 225 */     return total;
/*     */   }
/*     */ 
/*     */   public static byte[] toByteArray(InputStream in)
/*     */     throws IOException
/*     */   {
/* 237 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 238 */     copy(in, out);
/* 239 */     return out.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] toByteArray(InputSupplier<? extends InputStream> supplier)
/*     */     throws IOException
/*     */   {
/* 250 */     boolean threw = true;
/* 251 */     InputStream in = (InputStream)supplier.getInput();
/*     */     try {
/* 253 */       byte[] result = toByteArray(in);
/* 254 */       threw = false;
/* 255 */       return result;
/*     */     } finally {
/* 257 */       Closeables.close(in, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static ByteArrayDataInput newDataInput(byte[] bytes)
/*     */   {
/* 266 */     return new ByteArrayDataInputStream(bytes);
/*     */   }
/*     */ 
/*     */   public static ByteArrayDataInput newDataInput(byte[] bytes, int start)
/*     */   {
/* 277 */     Preconditions.checkPositionIndex(start, bytes.length);
/* 278 */     return new ByteArrayDataInputStream(bytes, start);
/*     */   }
/*     */ 
/*     */   public static ByteArrayDataOutput newDataOutput()
/*     */   {
/* 420 */     return new ByteArrayDataOutputStream();
/*     */   }
/*     */ 
/*     */   public static ByteArrayDataOutput newDataOutput(int size)
/*     */   {
/* 430 */     Preconditions.checkArgument(size >= 0, "Invalid size: %s", new Object[] { Integer.valueOf(size) });
/* 431 */     return new ByteArrayDataOutputStream(size);
/*     */   }
/*     */ 
/*     */   public static long length(InputSupplier<? extends InputStream> supplier)
/*     */     throws IOException
/*     */   {
/* 576 */     long count = 0L;
/* 577 */     boolean threw = true;
/* 578 */     InputStream in = (InputStream)supplier.getInput();
/*     */     try
/*     */     {
/*     */       while (true) {
/* 582 */         long amt = in.skip(2147483647L);
/* 583 */         if (amt == 0L) {
/* 584 */           if (in.read() == -1) {
/* 585 */             threw = false;
/* 586 */             return count;
/*     */           }
/* 588 */           count += 1L;
/*     */         } else {
/* 590 */           count += amt;
/*     */         }
/*     */       }
/*     */     } finally {
/* 594 */       Closeables.close(in, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static boolean equal(InputSupplier<? extends InputStream> supplier1, InputSupplier<? extends InputStream> supplier2)
/*     */     throws IOException
/*     */   {
/* 605 */     byte[] buf1 = new byte[4096];
/* 606 */     byte[] buf2 = new byte[4096];
/*     */ 
/* 608 */     boolean threw = true;
/* 609 */     InputStream in1 = (InputStream)supplier1.getInput();
/*     */     try {
/* 611 */       InputStream in2 = (InputStream)supplier2.getInput();
/*     */       try {
/*     */         while (true) {
/* 614 */           int read1 = read(in1, buf1, 0, 4096);
/* 615 */           int read2 = read(in2, buf2, 0, 4096);
/*     */           boolean bool1;
/* 616 */           if ((read1 != read2) || (!Arrays.equals(buf1, buf2))) {
/* 617 */             threw = false;
/* 618 */             return false;
/* 619 */           }if (read1 != 4096) {
/* 620 */             threw = false;
/* 621 */             return true;
/*     */           }
/*     */         }
/*     */       } finally {
/*     */       }
/*     */     }
/*     */     finally {
/* 628 */       Closeables.close(in1, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void readFully(InputStream in, byte[] b)
/*     */     throws IOException
/*     */   {
/* 644 */     readFully(in, b, 0, b.length);
/*     */   }
/*     */ 
/*     */   public static void readFully(InputStream in, byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 663 */     if (read(in, b, off, len) != len)
/* 664 */       throw new EOFException();
/*     */   }
/*     */ 
/*     */   public static void skipFully(InputStream in, long n)
/*     */     throws IOException
/*     */   {
/* 681 */     while (n > 0L) {
/* 682 */       long amt = in.skip(n);
/* 683 */       if (amt == 0L)
/*     */       {
/* 685 */         if (in.read() == -1) {
/* 686 */           throw new EOFException();
/*     */         }
/* 688 */         n -= 1L;
/*     */       } else {
/* 690 */         n -= amt;
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static <T> T readBytes(InputSupplier<? extends InputStream> supplier, ByteProcessor<T> processor)
/*     */     throws IOException
/*     */   {
/* 705 */     byte[] buf = new byte[4096];
/* 706 */     boolean threw = true;
/* 707 */     InputStream in = (InputStream)supplier.getInput();
/*     */     try {
/*     */       int amt;
/*     */       do {
/* 711 */         amt = in.read(buf);
/* 712 */         if (amt == -1) {
/* 713 */           threw = false;
/* 714 */           break;
/*     */         }
/*     */       }
/* 716 */       while (processor.processBytes(buf, 0, amt));
/* 717 */       return processor.getResult();
/*     */     } finally {
/* 719 */       Closeables.close(in, threw);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static long getChecksum(InputSupplier<? extends InputStream> supplier, Checksum checksum)
/*     */     throws IOException
/*     */   {
/* 735 */     return ((Long)readBytes(supplier, new ByteProcessor()
/*     */     {
/*     */       public boolean processBytes(byte[] buf, int off, int len) {
/* 738 */         this.val$checksum.update(buf, off, len);
/* 739 */         return true;
/*     */       }
/*     */ 
/*     */       public Long getResult()
/*     */       {
/* 744 */         long result = this.val$checksum.getValue();
/* 745 */         this.val$checksum.reset();
/* 746 */         return Long.valueOf(result);
/*     */       }
/*     */     })).longValue();
/*     */   }
/*     */ 
/*     */   public static HashCode hash(InputSupplier<? extends InputStream> supplier, HashFunction hashFunction)
/*     */     throws IOException
/*     */   {
/* 764 */     Hasher hasher = hashFunction.newHasher();
/* 765 */     return (HashCode)readBytes(supplier, new ByteProcessor()
/*     */     {
/*     */       public boolean processBytes(byte[] buf, int off, int len) {
/* 768 */         this.val$hasher.putBytes(buf, off, len);
/* 769 */         return true;
/*     */       }
/*     */ 
/*     */       public HashCode getResult()
/*     */       {
/* 774 */         return this.val$hasher.hash();
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static int read(InputStream in, byte[] b, int off, int len)
/*     */     throws IOException
/*     */   {
/* 805 */     if (len < 0) {
/* 806 */       throw new IndexOutOfBoundsException("len is negative");
/*     */     }
/* 808 */     int total = 0;
/* 809 */     while (total < len) {
/* 810 */       int result = in.read(b, off + total, len - total);
/* 811 */       if (result == -1) {
/*     */         break;
/*     */       }
/* 814 */       total += result;
/*     */     }
/* 816 */     return total;
/*     */   }
/*     */ 
/*     */   public static InputSupplier<InputStream> slice(InputSupplier<? extends InputStream> supplier, final long offset, long length)
/*     */   {
/* 834 */     Preconditions.checkNotNull(supplier);
/* 835 */     Preconditions.checkArgument(offset >= 0L, "offset is negative");
/* 836 */     Preconditions.checkArgument(length >= 0L, "length is negative");
/* 837 */     return new InputSupplier() {
/*     */       public InputStream getInput() throws IOException {
/* 839 */         InputStream in = (InputStream)this.val$supplier.getInput();
/* 840 */         if (offset > 0L) {
/*     */           try {
/* 842 */             ByteStreams.skipFully(in, offset);
/*     */           } catch (IOException e) {
/* 844 */             Closeables.closeQuietly(in);
/* 845 */             throw e;
/*     */           }
/*     */         }
/* 848 */         return new LimitInputStream(in, this.val$length);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static InputSupplier<InputStream> join(Iterable<? extends InputSupplier<? extends InputStream>> suppliers)
/*     */   {
/* 870 */     return new InputSupplier() {
/*     */       public InputStream getInput() throws IOException {
/* 872 */         return new MultiInputStream(this.val$suppliers.iterator());
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static InputSupplier<InputStream> join(InputSupplier<? extends InputStream>[] suppliers)
/*     */   {
/* 880 */     return join(Arrays.asList(suppliers));
/*     */   }
/*     */ 
/*     */   private static class ByteArrayDataOutputStream
/*     */     implements ByteArrayDataOutput
/*     */   {
/*     */     final DataOutput output;
/*     */     final ByteArrayOutputStream byteArrayOutputSteam;
/*     */ 
/*     */     ByteArrayDataOutputStream()
/*     */     {
/* 442 */       this(new ByteArrayOutputStream());
/*     */     }
/*     */ 
/*     */     ByteArrayDataOutputStream(int size) {
/* 446 */       this(new ByteArrayOutputStream(size));
/*     */     }
/*     */ 
/*     */     ByteArrayDataOutputStream(ByteArrayOutputStream byteArrayOutputSteam) {
/* 450 */       this.byteArrayOutputSteam = byteArrayOutputSteam;
/* 451 */       this.output = new DataOutputStream(byteArrayOutputSteam);
/*     */     }
/*     */ 
/*     */     public void write(int b) {
/*     */       try {
/* 456 */         this.output.write(b);
/*     */       } catch (IOException impossible) {
/* 458 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(byte[] b) {
/*     */       try {
/* 464 */         this.output.write(b);
/*     */       } catch (IOException impossible) {
/* 466 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(byte[] b, int off, int len) {
/*     */       try {
/* 472 */         this.output.write(b, off, len);
/*     */       } catch (IOException impossible) {
/* 474 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeBoolean(boolean v) {
/*     */       try {
/* 480 */         this.output.writeBoolean(v);
/*     */       } catch (IOException impossible) {
/* 482 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeByte(int v) {
/*     */       try {
/* 488 */         this.output.writeByte(v);
/*     */       } catch (IOException impossible) {
/* 490 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeBytes(String s) {
/*     */       try {
/* 496 */         this.output.writeBytes(s);
/*     */       } catch (IOException impossible) {
/* 498 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeChar(int v) {
/*     */       try {
/* 504 */         this.output.writeChar(v);
/*     */       } catch (IOException impossible) {
/* 506 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeChars(String s) {
/*     */       try {
/* 512 */         this.output.writeChars(s);
/*     */       } catch (IOException impossible) {
/* 514 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeDouble(double v) {
/*     */       try {
/* 520 */         this.output.writeDouble(v);
/*     */       } catch (IOException impossible) {
/* 522 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeFloat(float v) {
/*     */       try {
/* 528 */         this.output.writeFloat(v);
/*     */       } catch (IOException impossible) {
/* 530 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeInt(int v) {
/*     */       try {
/* 536 */         this.output.writeInt(v);
/*     */       } catch (IOException impossible) {
/* 538 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeLong(long v) {
/*     */       try {
/* 544 */         this.output.writeLong(v);
/*     */       } catch (IOException impossible) {
/* 546 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeShort(int v) {
/*     */       try {
/* 552 */         this.output.writeShort(v);
/*     */       } catch (IOException impossible) {
/* 554 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void writeUTF(String s) {
/*     */       try {
/* 560 */         this.output.writeUTF(s);
/*     */       } catch (IOException impossible) {
/* 562 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public byte[] toByteArray() {
/* 567 */       return this.byteArrayOutputSteam.toByteArray();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static class ByteArrayDataInputStream
/*     */     implements ByteArrayDataInput
/*     */   {
/*     */     final DataInput input;
/*     */ 
/*     */     ByteArrayDataInputStream(byte[] bytes)
/*     */     {
/* 285 */       this.input = new DataInputStream(new ByteArrayInputStream(bytes));
/*     */     }
/*     */ 
/*     */     ByteArrayDataInputStream(byte[] bytes, int start) {
/* 289 */       this.input = new DataInputStream(new ByteArrayInputStream(bytes, start, bytes.length - start));
/*     */     }
/*     */ 
/*     */     public void readFully(byte[] b)
/*     */     {
/*     */       try {
/* 295 */         this.input.readFully(b);
/*     */       } catch (IOException e) {
/* 297 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void readFully(byte[] b, int off, int len) {
/*     */       try {
/* 303 */         this.input.readFully(b, off, len);
/*     */       } catch (IOException e) {
/* 305 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int skipBytes(int n) {
/*     */       try {
/* 311 */         return this.input.skipBytes(n);
/*     */       } catch (IOException e) {
/* 313 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public boolean readBoolean() {
/*     */       try {
/* 319 */         return this.input.readBoolean();
/*     */       } catch (IOException e) {
/* 321 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public byte readByte() {
/*     */       try {
/* 327 */         return this.input.readByte();
/*     */       } catch (EOFException e) {
/* 329 */         throw new IllegalStateException(e);
/*     */       } catch (IOException impossible) {
/* 331 */         throw new AssertionError(impossible);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int readUnsignedByte() {
/*     */       try {
/* 337 */         return this.input.readUnsignedByte();
/*     */       } catch (IOException e) {
/* 339 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public short readShort() {
/*     */       try {
/* 345 */         return this.input.readShort();
/*     */       } catch (IOException e) {
/* 347 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int readUnsignedShort() {
/*     */       try {
/* 353 */         return this.input.readUnsignedShort();
/*     */       } catch (IOException e) {
/* 355 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public char readChar() {
/*     */       try {
/* 361 */         return this.input.readChar();
/*     */       } catch (IOException e) {
/* 363 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public int readInt() {
/*     */       try {
/* 369 */         return this.input.readInt();
/*     */       } catch (IOException e) {
/* 371 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public long readLong() {
/*     */       try {
/* 377 */         return this.input.readLong();
/*     */       } catch (IOException e) {
/* 379 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public float readFloat() {
/*     */       try {
/* 385 */         return this.input.readFloat();
/*     */       } catch (IOException e) {
/* 387 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public double readDouble() {
/*     */       try {
/* 393 */         return this.input.readDouble();
/*     */       } catch (IOException e) {
/* 395 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public String readLine() {
/*     */       try {
/* 401 */         return this.input.readLine();
/*     */       } catch (IOException e) {
/* 403 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public String readUTF() {
/*     */       try {
/* 409 */         return this.input.readUTF();
/*     */       } catch (IOException e) {
/* 411 */         throw new IllegalStateException(e);
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.ByteStreams
 * JD-Core Version:    0.6.2
 */