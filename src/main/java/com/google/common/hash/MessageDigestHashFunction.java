/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.ByteOrder;
/*     */ import java.nio.charset.Charset;
/*     */ import java.security.MessageDigest;
/*     */ import java.security.NoSuchAlgorithmException;
/*     */ 
/*     */ final class MessageDigestHashFunction extends AbstractStreamingHashFunction
/*     */ {
/*     */   private final String algorithmName;
/*     */   private final int bits;
/*     */ 
/*     */   MessageDigestHashFunction(String algorithmName)
/*     */   {
/*  42 */     this.algorithmName = algorithmName;
/*  43 */     this.bits = (getMessageDigest(algorithmName).getDigestLength() * 8);
/*     */   }
/*     */ 
/*     */   public int bits() {
/*  47 */     return this.bits;
/*     */   }
/*     */ 
/*     */   private static MessageDigest getMessageDigest(String algorithmName) {
/*     */     try {
/*  52 */       return MessageDigest.getInstance(algorithmName);
/*     */     } catch (NoSuchAlgorithmException e) {
/*  54 */       throw new AssertionError(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Hasher newHasher() {
/*  59 */     return new MessageDigestHasher(getMessageDigest(this.algorithmName), null);
/*     */   }
/*     */   private static class MessageDigestHasher implements Hasher {
/*     */     private final MessageDigest digest;
/*     */     private final ByteBuffer scratch;
/*     */     private boolean done;
/*     */ 
/*  68 */     private MessageDigestHasher(MessageDigest digest) { this.digest = digest;
/*  69 */       this.scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN); }
/*     */ 
/*     */     public Hasher putByte(byte b)
/*     */     {
/*  73 */       checkNotDone();
/*  74 */       this.digest.update(b);
/*  75 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putBytes(byte[] bytes) {
/*  79 */       checkNotDone();
/*  80 */       this.digest.update(bytes);
/*  81 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putBytes(byte[] bytes, int off, int len) {
/*  85 */       checkNotDone();
/*  86 */       Preconditions.checkPositionIndexes(off, off + len, bytes.length);
/*  87 */       this.digest.update(bytes, off, len);
/*  88 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putShort(short s) {
/*  92 */       checkNotDone();
/*  93 */       this.scratch.putShort(s);
/*  94 */       this.digest.update(this.scratch.array(), 0, 2);
/*  95 */       this.scratch.clear();
/*  96 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putInt(int i) {
/* 100 */       checkNotDone();
/* 101 */       this.scratch.putInt(i);
/* 102 */       this.digest.update(this.scratch.array(), 0, 4);
/* 103 */       this.scratch.clear();
/* 104 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putLong(long l) {
/* 108 */       checkNotDone();
/* 109 */       this.scratch.putLong(l);
/* 110 */       this.digest.update(this.scratch.array(), 0, 8);
/* 111 */       this.scratch.clear();
/* 112 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putFloat(float f) {
/* 116 */       checkNotDone();
/* 117 */       this.scratch.putFloat(f);
/* 118 */       this.digest.update(this.scratch.array(), 0, 4);
/* 119 */       this.scratch.clear();
/* 120 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putDouble(double d) {
/* 124 */       checkNotDone();
/* 125 */       this.scratch.putDouble(d);
/* 126 */       this.digest.update(this.scratch.array(), 0, 8);
/* 127 */       this.scratch.clear();
/* 128 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putBoolean(boolean b) {
/* 132 */       return putByte((byte)(b ? 1 : 0));
/*     */     }
/*     */ 
/*     */     public Hasher putChar(char c) {
/* 136 */       checkNotDone();
/* 137 */       this.scratch.putChar(c);
/* 138 */       this.digest.update(this.scratch.array(), 0, 2);
/* 139 */       this.scratch.clear();
/* 140 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putString(CharSequence charSequence) {
/* 144 */       for (int i = 0; i < charSequence.length(); i++) {
/* 145 */         putChar(charSequence.charAt(i));
/*     */       }
/* 147 */       return this;
/*     */     }
/*     */ 
/*     */     public Hasher putString(CharSequence charSequence, Charset charset) {
/* 151 */       return putBytes(charSequence.toString().getBytes(charset));
/*     */     }
/*     */ 
/*     */     public <T> Hasher putObject(T instance, Funnel<? super T> funnel) {
/* 155 */       checkNotDone();
/* 156 */       funnel.funnel(instance, this);
/* 157 */       return this;
/*     */     }
/*     */ 
/*     */     private void checkNotDone() {
/* 161 */       Preconditions.checkState(!this.done, "Cannot use Hasher after calling #hash() on it");
/*     */     }
/*     */ 
/*     */     public HashCode hash() {
/* 165 */       this.done = true;
/* 166 */       return HashCodes.fromBytesNoCopy(this.digest.digest());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.MessageDigestHashFunction
 * JD-Core Version:    0.6.2
 */