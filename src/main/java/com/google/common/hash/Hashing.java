/*     */ package com.google.common.hash;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.VisibleForTesting;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.primitives.UnsignedInts;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Iterator;
/*     */ 
/*     */ @Beta
/*     */ public final class Hashing
/*     */ {
/*  44 */   private static final int GOOD_FAST_HASH_SEED = (int)System.currentTimeMillis();
/*     */ 
/*  94 */   private static final Murmur3_32HashFunction MURMUR3_32 = new Murmur3_32HashFunction(0);
/*     */ 
/* 116 */   private static final Murmur3_128HashFunction MURMUR3_128 = new Murmur3_128HashFunction(0);
/*     */ 
/* 126 */   private static final HashFunction MD5 = new MessageDigestHashFunction("MD5");
/*     */ 
/* 136 */   private static final HashFunction SHA_1 = new MessageDigestHashFunction("SHA-1");
/*     */ 
/* 146 */   private static final HashFunction SHA_256 = new MessageDigestHashFunction("SHA-256");
/*     */ 
/* 156 */   private static final HashFunction SHA_512 = new MessageDigestHashFunction("SHA-512");
/*     */ 
/*     */   public static HashFunction goodFastHash(int minimumBits)
/*     */   {
/*  57 */     int bits = checkPositiveAndMakeMultipleOf32(minimumBits);
/*     */ 
/*  59 */     if (bits == 32)
/*  60 */       return murmur3_32(GOOD_FAST_HASH_SEED);
/*  61 */     if (bits <= 128) {
/*  62 */       return murmur3_128(GOOD_FAST_HASH_SEED);
/*     */     }
/*     */ 
/*  65 */     int hashFunctionsNeeded = (bits + 127) / 128;
/*  66 */     HashFunction[] hashFunctions = new HashFunction[hashFunctionsNeeded];
/*  67 */     int seed = GOOD_FAST_HASH_SEED;
/*  68 */     for (int i = 0; i < hashFunctionsNeeded; i++) {
/*  69 */       hashFunctions[i] = murmur3_128(seed);
/*  70 */       seed += 1500450271;
/*     */     }
/*  72 */     return new ConcatenatedHashFunction(hashFunctions);
/*     */   }
/*     */ 
/*     */   public static HashFunction murmur3_32(int seed)
/*     */   {
/*  82 */     return new Murmur3_32HashFunction(seed);
/*     */   }
/*     */ 
/*     */   public static HashFunction murmur3_32()
/*     */   {
/*  91 */     return MURMUR3_32;
/*     */   }
/*     */ 
/*     */   public static HashFunction murmur3_128(int seed)
/*     */   {
/* 103 */     return new Murmur3_128HashFunction(seed);
/*     */   }
/*     */ 
/*     */   public static HashFunction murmur3_128()
/*     */   {
/* 113 */     return MURMUR3_128;
/*     */   }
/*     */ 
/*     */   public static HashFunction md5()
/*     */   {
/* 123 */     return MD5;
/*     */   }
/*     */ 
/*     */   public static HashFunction sha1()
/*     */   {
/* 133 */     return SHA_1;
/*     */   }
/*     */ 
/*     */   public static HashFunction sha256()
/*     */   {
/* 143 */     return SHA_256;
/*     */   }
/*     */ 
/*     */   public static HashFunction sha512()
/*     */   {
/* 153 */     return SHA_512;
/*     */   }
/*     */ 
/*     */   public static long padToLong(HashCode hashCode)
/*     */   {
/* 164 */     return hashCode.bits() < 64 ? UnsignedInts.toLong(hashCode.asInt()) : hashCode.asLong();
/*     */   }
/*     */ 
/*     */   public static int consistentHash(HashCode hashCode, int buckets)
/*     */   {
/* 181 */     return consistentHash(padToLong(hashCode), buckets);
/*     */   }
/*     */ 
/*     */   public static int consistentHash(long input, int buckets)
/*     */   {
/* 198 */     Preconditions.checkArgument(buckets > 0, "buckets must be positive: %s", new Object[] { Integer.valueOf(buckets) });
/* 199 */     long h = input;
/* 200 */     int candidate = 0;
/*     */     while (true)
/*     */     {
/* 207 */       h = 2862933555777941757L * h + 1L;
/* 208 */       double inv = 2147483648.0D / ((int)(h >>> 33) + 1);
/* 209 */       int next = (int)((candidate + 1) * inv);
/*     */ 
/* 211 */       if ((next >= 0) && (next < buckets))
/* 212 */         candidate = next;
/*     */       else
/* 214 */         return candidate;
/*     */     }
/*     */   }
/*     */ 
/*     */   public static HashCode combineOrdered(Iterable<HashCode> hashCodes)
/*     */   {
/* 230 */     Iterator iterator = hashCodes.iterator();
/* 231 */     Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
/* 232 */     int bits = ((HashCode)iterator.next()).bits();
/* 233 */     byte[] resultBytes = new byte[bits / 8];
/* 234 */     for (HashCode hashCode : hashCodes) {
/* 235 */       byte[] nextBytes = hashCode.asBytes();
/* 236 */       Preconditions.checkArgument(nextBytes.length == resultBytes.length, "All hashcodes must have the same bit length.");
/*     */ 
/* 238 */       for (int i = 0; i < nextBytes.length; i++) {
/* 239 */         resultBytes[i] = ((byte)(resultBytes[i] * 37 ^ nextBytes[i]));
/*     */       }
/*     */     }
/* 242 */     return HashCodes.fromBytesNoCopy(resultBytes);
/*     */   }
/*     */ 
/*     */   public static HashCode combineUnordered(Iterable<HashCode> hashCodes)
/*     */   {
/* 256 */     Iterator iterator = hashCodes.iterator();
/* 257 */     Preconditions.checkArgument(iterator.hasNext(), "Must be at least 1 hash code to combine.");
/* 258 */     byte[] resultBytes = new byte[((HashCode)iterator.next()).bits() / 8];
/* 259 */     for (HashCode hashCode : hashCodes) {
/* 260 */       byte[] nextBytes = hashCode.asBytes();
/* 261 */       Preconditions.checkArgument(nextBytes.length == resultBytes.length, "All hashcodes must have the same bit length.");
/*     */ 
/* 263 */       for (int i = 0; i < nextBytes.length; tmp102_100++)
/*     */       {
/*     */         int tmp102_100 = i;
/*     */         byte[] tmp102_99 = resultBytes; tmp102_99[tmp102_100] = ((byte)(tmp102_99[tmp102_100] + nextBytes[tmp102_100]));
/*     */       }
/*     */     }
/* 267 */     return HashCodes.fromBytesNoCopy(resultBytes);
/*     */   }
/*     */ 
/*     */   static int checkPositiveAndMakeMultipleOf32(int bits)
/*     */   {
/* 274 */     Preconditions.checkArgument(bits > 0, "Number of bits must be positive");
/* 275 */     return bits + 31 & 0xFFFFFFE0;
/*     */   }
/*     */ 
/*     */   @VisibleForTesting
/*     */   static final class ConcatenatedHashFunction extends AbstractCompositeHashFunction
/*     */   {
/*     */     private final int bits;
/*     */ 
/*     */     ConcatenatedHashFunction(HashFunction[] functions) {
/* 284 */       super();
/* 285 */       int bitSum = 0;
/* 286 */       for (HashFunction function : functions) {
/* 287 */         bitSum += function.bits();
/*     */       }
/* 289 */       this.bits = bitSum;
/*     */     }
/*     */ 
/*     */     HashCode makeHash(Hasher[] hashers)
/*     */     {
/* 295 */       byte[] bytes = new byte[this.bits / 8];
/* 296 */       ByteBuffer buffer = ByteBuffer.wrap(bytes);
/* 297 */       for (Hasher hasher : hashers) {
/* 298 */         buffer.put(hasher.hash().asBytes());
/*     */       }
/* 300 */       return HashCodes.fromBytesNoCopy(bytes);
/*     */     }
/*     */ 
/*     */     public int bits()
/*     */     {
/* 305 */       return this.bits;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.hash.Hashing
 * JD-Core Version:    0.6.2
 */