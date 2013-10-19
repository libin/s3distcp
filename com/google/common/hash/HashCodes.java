/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.base.Preconditions;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ @Beta
/*     */ public final class HashCodes
/*     */ {
/*     */   public static HashCode fromInt(int hash)
/*     */   {
/*  39 */     return new IntHashCode(hash);
/*     */   }
/*     */ 
/*     */   public static HashCode fromLong(long hash)
/*     */   {
/*  77 */     return new LongHashCode(hash);
/*     */   }
/*     */ 
/*     */   public static HashCode fromBytes(byte[] bytes)
/*     */   {
/* 119 */     Preconditions.checkArgument(bytes.length >= 4, "A HashCode must contain at least 4 bytes.");
/* 120 */     return fromBytesNoCopy((byte[])bytes.clone());
/*     */   }
/*     */ 
/*     */   static HashCode fromBytesNoCopy(byte[] bytes)
/*     */   {
/* 129 */     return new BytesHashCode(bytes);
/*     */   }
/*     */   private static final class BytesHashCode extends HashCode implements Serializable {
/*     */     final byte[] bytes;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/* 136 */     BytesHashCode(byte[] bytes) { this.bytes = bytes; }
/*     */ 
/*     */     public int bits()
/*     */     {
/* 140 */       return this.bytes.length * 8;
/*     */     }
/*     */ 
/*     */     public byte[] asBytes() {
/* 144 */       return (byte[])this.bytes.clone();
/*     */     }
/*     */ 
/*     */     public int asInt() {
/* 148 */       return this.bytes[0] & 0xFF | (this.bytes[1] & 0xFF) << 8 | (this.bytes[2] & 0xFF) << 16 | (this.bytes[3] & 0xFF) << 24;
/*     */     }
/*     */ 
/*     */     public long asLong()
/*     */     {
/* 155 */       if (this.bytes.length < 8)
/*     */       {
/* 157 */         throw new IllegalStateException("Not enough bytes");
/*     */       }
/* 159 */       return this.bytes[0] & 0xFF | (this.bytes[1] & 0xFF) << 8 | (this.bytes[2] & 0xFF) << 16 | (this.bytes[3] & 0xFF) << 24 | (this.bytes[4] & 0xFF) << 32 | (this.bytes[5] & 0xFF) << 40 | (this.bytes[6] & 0xFF) << 48 | (this.bytes[7] & 0xFF) << 56;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class LongHashCode extends HashCode
/*     */     implements Serializable
/*     */   {
/*     */     final long hash;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     LongHashCode(long hash)
/*     */     {
/*  84 */       this.hash = hash;
/*     */     }
/*     */ 
/*     */     public int bits() {
/*  88 */       return 64;
/*     */     }
/*     */ 
/*     */     public byte[] asBytes() {
/*  92 */       return new byte[] { (byte)(int)this.hash, (byte)(int)(this.hash >> 8), (byte)(int)(this.hash >> 16), (byte)(int)(this.hash >> 24), (byte)(int)(this.hash >> 32), (byte)(int)(this.hash >> 40), (byte)(int)(this.hash >> 48), (byte)(int)(this.hash >> 56) };
/*     */     }
/*     */ 
/*     */     public int asInt()
/*     */     {
/* 104 */       return (int)this.hash;
/*     */     }
/*     */ 
/*     */     public long asLong() {
/* 108 */       return this.hash;
/*     */     }
/*     */   }
/*     */ 
/*     */   private static final class IntHashCode extends HashCode
/*     */     implements Serializable
/*     */   {
/*     */     final int hash;
/*     */     private static final long serialVersionUID = 0L;
/*     */ 
/*     */     IntHashCode(int hash)
/*     */     {
/*  46 */       this.hash = hash;
/*     */     }
/*     */ 
/*     */     public int bits() {
/*  50 */       return 32;
/*     */     }
/*     */ 
/*     */     public byte[] asBytes() {
/*  54 */       return new byte[] { (byte)this.hash, (byte)(this.hash >> 8), (byte)(this.hash >> 16), (byte)(this.hash >> 24) };
/*     */     }
/*     */ 
/*     */     public int asInt()
/*     */     {
/*  62 */       return this.hash;
/*     */     }
/*     */ 
/*     */     public long asLong() {
/*  66 */       throw new IllegalStateException("this HashCode only has 32 bits; cannot create a long");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.HashCodes
 * JD-Core Version:    0.6.2
 */