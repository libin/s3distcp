/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.charset.Charset;
/*     */ 
/*     */ abstract class AbstractStreamingHashFunction
/*     */   implements HashFunction
/*     */ {
/*     */   public HashCode hashString(CharSequence input)
/*     */   {
/*  37 */     return newHasher().putString(input).hash();
/*     */   }
/*     */ 
/*     */   public HashCode hashString(CharSequence input, Charset charset) {
/*  41 */     return newHasher().putString(input, charset).hash();
/*     */   }
/*     */ 
/*     */   public HashCode hashInt(int input) {
/*  45 */     return newHasher().putInt(input).hash();
/*     */   }
/*     */ 
/*     */   public HashCode hashLong(long input) {
/*  49 */     return newHasher().putLong(input).hash();
/*     */   }
/*     */ 
/*     */   public HashCode hashBytes(byte[] input) {
/*  53 */     return newHasher().putBytes(input).hash();
/*     */   }
/*     */ 
/*     */   public HashCode hashBytes(byte[] input, int off, int len) {
/*  57 */     return newHasher().putBytes(input, off, len).hash();
/*     */   }
/*     */ 
/*     */   public Hasher newHasher(int expectedInputSize) {
/*  61 */     Preconditions.checkArgument(expectedInputSize >= 0);
/*  62 */     return newHasher();
/*     */   }
/*     */ 
/*     */   protected static abstract class AbstractStreamingHasher extends AbstractHasher
/*     */   {
/*     */     private final ByteBuffer buffer;
/*     */     private final int bufferSize;
/*     */     private final int chunkSize;
/*     */ 
/*     */     protected AbstractStreamingHasher(int chunkSize)
/*     */     {
/*  91 */       this(chunkSize, chunkSize);
/*     */     }
/*     */ 
/*     */     protected AbstractStreamingHasher(int chunkSize, int bufferSize)
/*     */     {
/* 105 */       Preconditions.checkArgument(bufferSize % chunkSize == 0);
/*     */ 
/* 108 */       this.buffer = ByteBuffer.allocate(bufferSize + 7).order(ByteOrder.LITTLE_ENDIAN);
/*     */ 
/* 111 */       this.bufferSize = bufferSize;
/* 112 */       this.chunkSize = chunkSize;
/*     */     }
/*     */ 
/*     */     protected abstract void process(ByteBuffer paramByteBuffer);
/*     */ 
/*     */     protected void processRemaining(ByteBuffer bb)
/*     */     {
/* 129 */       bb.position(bb.limit());
/* 130 */       bb.limit(this.chunkSize + 7);
/* 131 */       while (bb.position() < this.chunkSize) {
/* 132 */         bb.putLong(0L);
/*     */       }
/* 134 */       bb.limit(this.chunkSize);
/* 135 */       bb.flip();
/* 136 */       process(bb);
/*     */     }
/*     */ 
/*     */     public final Hasher putBytes(byte[] bytes)
/*     */     {
/* 141 */       return putBytes(bytes, 0, bytes.length);
/*     */     }
/*     */ 
/*     */     public final Hasher putBytes(byte[] bytes, int off, int len)
/*     */     {
/* 146 */       return putBytes(ByteBuffer.wrap(bytes, off, len).order(ByteOrder.LITTLE_ENDIAN));
/*     */     }
/*     */ 
/*     */     private final Hasher putBytes(ByteBuffer readBuffer)
/*     */     {
/* 151 */       if (readBuffer.remaining() <= this.buffer.remaining()) {
/* 152 */         this.buffer.put(readBuffer);
/* 153 */         munchIfFull();
/* 154 */         return this;
/*     */       }
/*     */ 
/* 158 */       int bytesToCopy = this.bufferSize - this.buffer.position();
/* 159 */       for (int i = 0; i < bytesToCopy; i++) {
/* 160 */         this.buffer.put(readBuffer.get());
/*     */       }
/* 162 */       munch();
/*     */ 
/* 165 */       while (readBuffer.remaining() >= this.chunkSize) {
/* 166 */         process(readBuffer);
/*     */       }
/*     */ 
/* 170 */       this.buffer.put(readBuffer);
/* 171 */       return this;
/*     */     }
/*     */ 
/*     */     public final Hasher putString(CharSequence charSequence)
/*     */     {
/* 176 */       for (int i = 0; i < charSequence.length(); i++) {
/* 177 */         putChar(charSequence.charAt(i));
/*     */       }
/* 179 */       return this;
/*     */     }
/*     */ 
/*     */     public final Hasher putByte(byte b)
/*     */     {
/* 184 */       this.buffer.put(b);
/* 185 */       munchIfFull();
/* 186 */       return this;
/*     */     }
/*     */ 
/*     */     public final Hasher putShort(short s)
/*     */     {
/* 191 */       this.buffer.putShort(s);
/* 192 */       munchIfFull();
/* 193 */       return this;
/*     */     }
/*     */ 
/*     */     public final Hasher putChar(char c)
/*     */     {
/* 198 */       this.buffer.putChar(c);
/* 199 */       munchIfFull();
/* 200 */       return this;
/*     */     }
/*     */ 
/*     */     public final Hasher putInt(int i)
/*     */     {
/* 205 */       this.buffer.putInt(i);
/* 206 */       munchIfFull();
/* 207 */       return this;
/*     */     }
/*     */ 
/*     */     public final Hasher putLong(long l)
/*     */     {
/* 212 */       this.buffer.putLong(l);
/* 213 */       munchIfFull();
/* 214 */       return this;
/*     */     }
/*     */ 
/*     */     public final <T> Hasher putObject(T instance, Funnel<? super T> funnel)
/*     */     {
/* 219 */       funnel.funnel(instance, this);
/* 220 */       return this;
/*     */     }
/*     */ 
/*     */     public final HashCode hash()
/*     */     {
/* 225 */       munch();
/* 226 */       this.buffer.flip();
/* 227 */       if (this.buffer.remaining() > 0) {
/* 228 */         processRemaining(this.buffer);
/*     */       }
/* 230 */       return makeHash();
/*     */     }
/*     */ 
/*     */     abstract HashCode makeHash();
/*     */ 
/*     */     private void munchIfFull()
/*     */     {
/* 237 */       if (this.buffer.remaining() < 8)
/*     */       {
/* 239 */         munch();
/*     */       }
/*     */     }
/*     */ 
/*     */     private void munch() {
/* 244 */       this.buffer.flip();
/* 245 */       while (this.buffer.remaining() >= this.chunkSize)
/*     */       {
/* 248 */         process(this.buffer);
/*     */       }
/* 250 */       this.buffer.compact();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.AbstractStreamingHashFunction
 * JD-Core Version:    0.6.2
 */