/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.annotations.GwtIncompatible;
/*     */ import java.util.Collections;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ 
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class Splitter
/*     */ {
/*     */   private final CharMatcher trimmer;
/*     */   private final boolean omitEmptyStrings;
/*     */   private final Strategy strategy;
/*     */   private final int limit;
/*     */ 
/*     */   private Splitter(Strategy strategy)
/*     */   {
/* 109 */     this(strategy, false, CharMatcher.NONE, 2147483647);
/*     */   }
/*     */ 
/*     */   private Splitter(Strategy strategy, boolean omitEmptyStrings, CharMatcher trimmer, int limit)
/*     */   {
/* 114 */     this.strategy = strategy;
/* 115 */     this.omitEmptyStrings = omitEmptyStrings;
/* 116 */     this.trimmer = trimmer;
/* 117 */     this.limit = limit;
/*     */   }
/*     */ 
/*     */   public static Splitter on(char separator)
/*     */   {
/* 129 */     return on(CharMatcher.is(separator));
/*     */   }
/*     */ 
/*     */   public static Splitter on(CharMatcher separatorMatcher)
/*     */   {
/* 143 */     Preconditions.checkNotNull(separatorMatcher);
/*     */ 
/* 145 */     return new Splitter(new Strategy()
/*     */     {
/*     */       public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 148 */         return new Splitter.SplittingIterator(splitter, toSplit) {
/*     */           int separatorStart(int start) {
/* 150 */             return Splitter.1.this.val$separatorMatcher.indexIn(this.toSplit, start);
/*     */           }
/*     */ 
/*     */           int separatorEnd(int separatorPosition) {
/* 154 */             return separatorPosition + 1;
/*     */           }
/*     */         };
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public static Splitter on(String separator)
/*     */   {
/* 170 */     Preconditions.checkArgument(separator.length() != 0, "The separator may not be the empty string.");
/*     */ 
/* 173 */     return new Splitter(new Strategy()
/*     */     {
/*     */       public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 176 */         return new Splitter.SplittingIterator(splitter, toSplit) {
/*     */           public int separatorStart(int start) {
/* 178 */             int delimeterLength = Splitter.2.this.val$separator.length();
/*     */ 
/* 181 */             int p = start; int last = this.toSplit.length() - delimeterLength;
/* 182 */             label80: for (; p <= last; p++) {
/* 183 */               for (int i = 0; i < delimeterLength; i++) {
/* 184 */                 if (this.toSplit.charAt(i + p) != Splitter.2.this.val$separator.charAt(i)) {
/*     */                   break label80;
/*     */                 }
/*     */               }
/* 188 */               return p;
/*     */             }
/* 190 */             return -1;
/*     */           }
/*     */ 
/*     */           public int separatorEnd(int separatorPosition) {
/* 194 */             return separatorPosition + Splitter.2.this.val$separator.length();
/*     */           }
/*     */         };
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.util.regex")
/*     */   public static Splitter on(Pattern separatorPattern)
/*     */   {
/* 215 */     Preconditions.checkNotNull(separatorPattern);
/* 216 */     Preconditions.checkArgument(!separatorPattern.matcher("").matches(), "The pattern may not match the empty string: %s", new Object[] { separatorPattern });
/*     */ 
/* 219 */     return new Splitter(new Strategy()
/*     */     {
/*     */       public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 222 */         final Matcher matcher = this.val$separatorPattern.matcher(toSplit);
/* 223 */         return new Splitter.SplittingIterator(splitter, toSplit) {
/*     */           public int separatorStart(int start) {
/* 225 */             return matcher.find(start) ? matcher.start() : -1;
/*     */           }
/*     */ 
/*     */           public int separatorEnd(int separatorPosition) {
/* 229 */             return matcher.end();
/*     */           }
/*     */         };
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   @GwtIncompatible("java.util.regex")
/*     */   public static Splitter onPattern(String separatorPattern)
/*     */   {
/* 253 */     return on(Pattern.compile(separatorPattern));
/*     */   }
/*     */ 
/*     */   public static Splitter fixedLength(int length)
/*     */   {
/* 267 */     Preconditions.checkArgument(length > 0, "The length may not be less than 1");
/*     */ 
/* 269 */     return new Splitter(new Strategy()
/*     */     {
/*     */       public Splitter.SplittingIterator iterator(Splitter splitter, CharSequence toSplit) {
/* 272 */         return new Splitter.SplittingIterator(splitter, toSplit) {
/*     */           public int separatorStart(int start) {
/* 274 */             int nextChunkStart = start + Splitter.4.this.val$length;
/* 275 */             return nextChunkStart < this.toSplit.length() ? nextChunkStart : -1;
/*     */           }
/*     */ 
/*     */           public int separatorEnd(int separatorPosition) {
/* 279 */             return separatorPosition;
/*     */           }
/*     */         };
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Splitter omitEmptyStrings()
/*     */   {
/* 306 */     return new Splitter(this.strategy, true, this.trimmer, this.limit);
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Splitter limit(int limit)
/*     */   {
/* 330 */     Preconditions.checkArgument(limit > 0, "must be greater than zero: %s", new Object[] { Integer.valueOf(limit) });
/* 331 */     return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, limit);
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Splitter trimResults()
/*     */   {
/* 346 */     return trimResults(CharMatcher.WHITESPACE);
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Splitter trimResults(CharMatcher trimmer)
/*     */   {
/* 363 */     Preconditions.checkNotNull(trimmer);
/* 364 */     return new Splitter(this.strategy, this.omitEmptyStrings, trimmer, this.limit);
/*     */   }
/*     */ 
/*     */   public Iterable<String> split(final CharSequence sequence)
/*     */   {
/* 375 */     Preconditions.checkNotNull(sequence);
/*     */ 
/* 377 */     return new Iterable() {
/*     */       public Iterator<String> iterator() {
/* 379 */         return Splitter.this.spliterator(sequence);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private Iterator<String> spliterator(CharSequence sequence) {
/* 385 */     return this.strategy.iterator(this, sequence);
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   @Beta
/*     */   public MapSplitter withKeyValueSeparator(String separator)
/*     */   {
/* 397 */     return withKeyValueSeparator(on(separator));
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   @Beta
/*     */   public MapSplitter withKeyValueSeparator(Splitter keyValueSplitter)
/*     */   {
/* 410 */     return new MapSplitter(this, keyValueSplitter, null);
/*     */   }
/*     */ 
/*     */   private static abstract class SplittingIterator extends AbstractIterator<String>
/*     */   {
/*     */     final CharSequence toSplit;
/*     */     final CharMatcher trimmer;
/*     */     final boolean omitEmptyStrings;
/* 489 */     int offset = 0;
/*     */     int limit;
/*     */ 
/*     */     abstract int separatorStart(int paramInt);
/*     */ 
/*     */     abstract int separatorEnd(int paramInt);
/*     */ 
/*     */     protected SplittingIterator(Splitter splitter, CharSequence toSplit)
/*     */     {
/* 493 */       this.trimmer = splitter.trimmer;
/* 494 */       this.omitEmptyStrings = splitter.omitEmptyStrings;
/* 495 */       this.limit = splitter.limit;
/* 496 */       this.toSplit = toSplit;
/*     */     }
/*     */ 
/*     */     protected String computeNext()
/*     */     {
/* 506 */       int nextStart = this.offset;
/* 507 */       while (this.offset != -1) {
/* 508 */         int start = nextStart;
/*     */ 
/* 511 */         int separatorPosition = separatorStart(this.offset);
/*     */         int end;
/* 512 */         if (separatorPosition == -1) {
/* 513 */           int end = this.toSplit.length();
/* 514 */           this.offset = -1;
/*     */         } else {
/* 516 */           end = separatorPosition;
/* 517 */           this.offset = separatorEnd(separatorPosition);
/*     */         }
/* 519 */         if (this.offset == nextStart)
/*     */         {
/* 527 */           this.offset += 1;
/* 528 */           if (this.offset >= this.toSplit.length()) {
/* 529 */             this.offset = -1;
/*     */           }
/*     */         }
/*     */         else
/*     */         {
/* 534 */           while ((start < end) && (this.trimmer.matches(this.toSplit.charAt(start)))) {
/* 535 */             start++;
/*     */           }
/* 537 */           while ((end > start) && (this.trimmer.matches(this.toSplit.charAt(end - 1)))) {
/* 538 */             end--;
/*     */           }
/*     */ 
/* 541 */           if ((this.omitEmptyStrings) && (start == end))
/*     */           {
/* 543 */             nextStart = this.offset;
/*     */           }
/*     */           else
/*     */           {
/* 547 */             if (this.limit == 1)
/*     */             {
/* 551 */               end = this.toSplit.length();
/* 552 */               this.offset = -1;
/*     */ 
/* 554 */               while ((end > start) && (this.trimmer.matches(this.toSplit.charAt(end - 1)))) {
/* 555 */                 end--;
/*     */               }
/*     */             }
/* 558 */             this.limit -= 1;
/*     */ 
/* 561 */             return this.toSplit.subSequence(start, end).toString();
/*     */           }
/*     */         }
/*     */       }
/* 563 */       return (String)endOfData();
/*     */     }
/*     */   }
/*     */ 
/*     */   private static abstract interface Strategy
/*     */   {
/*     */     public abstract Iterator<String> iterator(Splitter paramSplitter, CharSequence paramCharSequence);
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public static final class MapSplitter
/*     */   {
/*     */     private static final String INVALID_ENTRY_MESSAGE = "Chunk [%s] is not a valid entry";
/*     */     private final Splitter outerSplitter;
/*     */     private final Splitter entrySplitter;
/*     */ 
/*     */     private MapSplitter(Splitter outerSplitter, Splitter entrySplitter)
/*     */     {
/* 428 */       this.outerSplitter = outerSplitter;
/* 429 */       this.entrySplitter = ((Splitter)Preconditions.checkNotNull(entrySplitter));
/*     */     }
/*     */ 
/*     */     public Map<String, String> split(CharSequence sequence)
/*     */     {
/* 448 */       Map map = new LinkedHashMap();
/* 449 */       for (String entry : this.outerSplitter.split(sequence)) {
/* 450 */         Iterator entryFields = this.entrySplitter.spliterator(entry);
/*     */ 
/* 452 */         Preconditions.checkArgument(entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { entry });
/* 453 */         String key = (String)entryFields.next();
/* 454 */         Preconditions.checkArgument(!map.containsKey(key), "Duplicate key [%s] found.", new Object[] { key });
/*     */ 
/* 456 */         Preconditions.checkArgument(entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { entry });
/* 457 */         String value = (String)entryFields.next();
/* 458 */         map.put(key, value);
/*     */ 
/* 460 */         Preconditions.checkArgument(!entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[] { entry });
/*     */       }
/* 462 */       return Collections.unmodifiableMap(map);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Splitter
 * JD-Core Version:    0.6.2
 */