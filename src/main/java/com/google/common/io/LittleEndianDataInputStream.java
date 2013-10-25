/*     */ package com.google.common.io;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.Ints;
/*     */ import com.google.common.primitives.Longs;
/*     */ import java.io.DataInput;
/*     */ import java.io.DataInputStream;
/*     */ import java.io.EOFException;
/*     */ import java.io.FilterInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ 
/*     */ @Beta
/*     */ public final class LittleEndianDataInputStream extends FilterInputStream
/*     */   implements DataInput
/*     */ {
/*     */   public LittleEndianDataInputStream(InputStream in)
/*     */   {
/*  53 */     super((InputStream)Preconditions.checkNotNull(in));
/*     */   }
/*     */ 
/*     */   public String readLine()
/*     */   {
/*  61 */     throw new UnsupportedOperationException("readLine is not supported");
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] b) throws IOException
/*     */   {
/*  66 */     ByteStreams.readFully(this, b);
/*     */   }
/*     */ 
/*     */   public void readFully(byte[] b, int off, int len) throws IOException
/*     */   {
/*  71 */     ByteStreams.readFully(this, b, off, len);
/*     */   }
/*     */ 
/*     */   public int skipBytes(int n) throws IOException
/*     */   {
/*  76 */     return (int)this.in.skip(n);
/*     */   }
/*     */ 
/*     */   public int readUnsignedByte() throws IOException
/*     */   {
/*  81 */     int b1 = this.in.read();
/*  82 */     if (0 > b1) {
/*  83 */       throw new EOFException();
/*     */     }
/*     */ 
/*  86 */     return b1;
/*     */   }
/*     */ 
/*     */   public int readUnsignedShort()
/*     */     throws IOException
/*     */   {
/* 100 */     byte b1 = readAndCheckByte();
/* 101 */     byte b2 = readAndCheckByte();
/*     */ 
/* 103 */     return Ints.fromBytes((byte)0, (byte)0, b2, b1);
/*     */   }
/*     */ 
/*     */   public int readInt()
/*     */     throws IOException
/*     */   {
/* 116 */     byte b1 = readAndCheckByte();
/* 117 */     byte b2 = readAndCheckByte();
/* 118 */     byte b3 = readAndCheckByte();
/* 119 */     byte b4 = readAndCheckByte();
/*     */ 
/* 121 */     return Ints.fromBytes(b4, b3, b2, b1);
/*     */   }
/*     */ 
/*     */   public long readLong()
/*     */     throws IOException
/*     */   {
/* 134 */     byte b1 = readAndCheckByte();
/* 135 */     byte b2 = readAndCheckByte();
/* 136 */     byte b3 = readAndCheckByte();
/* 137 */     byte b4 = readAndCheckByte();
/* 138 */     byte b5 = readAndCheckByte();
/* 139 */     byte b6 = readAndCheckByte();
/* 140 */     byte b7 = readAndCheckByte();
/* 141 */     byte b8 = readAndCheckByte();
/*     */ 
/* 143 */     return Longs.fromBytes(b8, b7, b6, b5, b4, b3, b2, b1);
/*     */   }
/*     */ 
/*     */   public float readFloat()
/*     */     throws IOException
/*     */   {
/* 156 */     return Float.intBitsToFloat(readInt());
/*     */   }
/*     */ 
/*     */   public double readDouble()
/*     */     throws IOException
/*     */   {
/* 170 */     return Double.longBitsToDouble(readLong());
/*     */   }
/*     */ 
/*     */   public String readUTF() throws IOException
/*     */   {
/* 175 */     return new DataInputStream(this.in).readUTF();
/*     */   }
/*     */ 
/*     */   public short readShort()
/*     */     throws IOException
/*     */   {
/* 188 */     return (short)readUnsignedShort();
/*     */   }
/*     */ 
/*     */   public char readChar()
/*     */     throws IOException
/*     */   {
/* 201 */     return (char)readUnsignedShort();
/*     */   }
/*     */ 
/*     */   public byte readByte() throws IOException
/*     */   {
/* 206 */     return (byte)readUnsignedByte();
/*     */   }
/*     */ 
/*     */   public boolean readBoolean() throws IOException
/*     */   {
/* 211 */     return readUnsignedByte() != 0;
/*     */   }
/*     */ 
/*     */   private byte readAndCheckByte()
/*     */     throws IOException, EOFException
/*     */   {
/* 223 */     int b1 = this.in.read();
/*     */ 
/* 225 */     if (-1 == b1) {
/* 226 */       throw new EOFException();
/*     */     }
/*     */ 
/* 229 */     return (byte)b1;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.io.LittleEndianDataInputStream
 * JD-Core Version:    0.6.2
 */