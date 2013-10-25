/*     */ package com.google.common.base;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import java.io.IOException;
/*     */ import java.util.AbstractList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.CheckReturnValue;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @GwtCompatible
/*     */ public class Joiner
/*     */ {
/*     */   private final String separator;
/*     */ 
/*     */   public static Joiner on(String separator)
/*     */   {
/*  71 */     return new Joiner(separator);
/*     */   }
/*     */ 
/*     */   public static Joiner on(char separator)
/*     */   {
/*  78 */     return new Joiner(String.valueOf(separator));
/*     */   }
/*     */ 
/*     */   private Joiner(String separator)
/*     */   {
/*  84 */     this.separator = ((String)Preconditions.checkNotNull(separator));
/*     */   }
/*     */ 
/*     */   private Joiner(Joiner prototype) {
/*  88 */     this.separator = prototype.separator;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @Beta
/*     */   public final <A extends Appendable, I,  extends Iterable<?>,  extends Iterator<?>> A appendTo(A appendable, I parts)
/*     */     throws IOException
/*     */   {
/* 104 */     return appendTo(appendable, (Iterator)parts);
/*     */   }
/*     */ 
/*     */   public <A extends Appendable> A appendTo(A appendable, Iterable<?> parts)
/*     */     throws IOException
/*     */   {
/* 112 */     return appendTo(appendable, parts.iterator());
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public <A extends Appendable> A appendTo(A appendable, Iterator<?> parts)
/*     */     throws IOException
/*     */   {
/* 123 */     Preconditions.checkNotNull(appendable);
/* 124 */     if (parts.hasNext()) {
/* 125 */       appendable.append(toString(parts.next()));
/* 126 */       while (parts.hasNext()) {
/* 127 */         appendable.append(this.separator);
/* 128 */         appendable.append(toString(parts.next()));
/*     */       }
/*     */     }
/* 131 */     return appendable;
/*     */   }
/*     */ 
/*     */   public final <A extends Appendable> A appendTo(A appendable, Object[] parts)
/*     */     throws IOException
/*     */   {
/* 139 */     return appendTo(appendable, Arrays.asList(parts));
/*     */   }
/*     */ 
/*     */   public final <A extends Appendable> A appendTo(A appendable, @Nullable Object first, @Nullable Object second, Object[] rest)
/*     */     throws IOException
/*     */   {
/* 148 */     return appendTo(appendable, iterable(first, second, rest));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @Beta
/*     */   public final <I,  extends Iterable<?>,  extends Iterator<?>> StringBuilder appendTo(StringBuilder builder, I parts)
/*     */   {
/* 164 */     return appendTo(builder, (Iterator)parts);
/*     */   }
/*     */ 
/*     */   public final StringBuilder appendTo(StringBuilder builder, Iterable<?> parts)
/*     */   {
/* 173 */     return appendTo(builder, parts.iterator());
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public final StringBuilder appendTo(StringBuilder builder, Iterator<?> parts)
/*     */   {
/*     */     try
/*     */     {
/* 186 */       appendTo(builder, parts);
/*     */     } catch (IOException impossible) {
/* 188 */       throw new AssertionError(impossible);
/*     */     }
/* 190 */     return builder;
/*     */   }
/*     */ 
/*     */   public final StringBuilder appendTo(StringBuilder builder, Object[] parts)
/*     */   {
/* 199 */     return appendTo(builder, Arrays.asList(parts));
/*     */   }
/*     */ 
/*     */   public final StringBuilder appendTo(StringBuilder builder, @Nullable Object first, @Nullable Object second, Object[] rest)
/*     */   {
/* 209 */     return appendTo(builder, iterable(first, second, rest));
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   @Beta
/*     */   public final <I,  extends Iterable<?>,  extends Iterator<?>> String join(I parts)
/*     */   {
/* 224 */     return join((Iterator)parts);
/*     */   }
/*     */ 
/*     */   public final String join(Iterable<?> parts)
/*     */   {
/* 232 */     return join(parts.iterator());
/*     */   }
/*     */ 
/*     */   @Beta
/*     */   public final String join(Iterator<?> parts)
/*     */   {
/* 243 */     return appendTo(new StringBuilder(), parts).toString();
/*     */   }
/*     */ 
/*     */   public final String join(Object[] parts)
/*     */   {
/* 251 */     return join(Arrays.asList(parts));
/*     */   }
/*     */ 
/*     */   public final String join(@Nullable Object first, @Nullable Object second, Object[] rest)
/*     */   {
/* 259 */     return join(iterable(first, second, rest));
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Joiner useForNull(final String nullText)
/*     */   {
/* 268 */     Preconditions.checkNotNull(nullText);
/* 269 */     return new Joiner(this, nullText) {
/*     */       CharSequence toString(Object part) {
/* 271 */         return part == null ? nullText : Joiner.this.toString(part);
/*     */       }
/*     */ 
/*     */       public Joiner useForNull(String nullText) {
/* 275 */         Preconditions.checkNotNull(nullText);
/* 276 */         throw new UnsupportedOperationException("already specified useForNull");
/*     */       }
/*     */ 
/*     */       public Joiner skipNulls() {
/* 280 */         throw new UnsupportedOperationException("already specified useForNull");
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public Joiner skipNulls()
/*     */   {
/*     */     // Byte code:
/*     */     //   0: new 31	com/google/common/base/Joiner$2
/*     */     //   3: dup
/*     */     //   4: aload_0
/*     */     //   5: aload_0
/*     */     //   6: invokespecial 32	com/google/common/base/Joiner$2:<init>	(Lcom/google/common/base/Joiner;Lcom/google/common/base/Joiner;)V
/*     */     //   9: areturn
/*     */   }
/*     */ 
/*     */   @CheckReturnValue
/*     */   public MapJoiner withKeyValueSeparator(String keyValueSeparator)
/*     */   {
/* 331 */     return new MapJoiner(this, keyValueSeparator, null);
/*     */   }
/*     */ 
/*     */   CharSequence toString(Object part)
/*     */   {
/* 541 */     Preconditions.checkNotNull(part);
/* 542 */     return (part instanceof CharSequence) ? (CharSequence)part : part.toString();
/*     */   }
/*     */ 
/*     */   private static Iterable<Object> iterable(final Object first, final Object second, Object[] rest)
/*     */   {
/* 547 */     Preconditions.checkNotNull(rest);
/* 548 */     return new AbstractList() {
/*     */       public int size() {
/* 550 */         return this.val$rest.length + 2;
/*     */       }
/*     */ 
/*     */       public Object get(int index) {
/* 554 */         switch (index) {
/*     */         case 0:
/* 556 */           return first;
/*     */         case 1:
/* 558 */           return second;
/*     */         }
/* 560 */         return this.val$rest[(index - 2)];
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static final class MapJoiner
/*     */   {
/*     */     private final Joiner joiner;
/*     */     private final String keyValueSeparator;
/*     */ 
/*     */     private MapJoiner(Joiner joiner, String keyValueSeparator)
/*     */     {
/* 357 */       this.joiner = joiner;
/* 358 */       this.keyValueSeparator = ((String)Preconditions.checkNotNull(keyValueSeparator));
/*     */     }
/*     */ 
/*     */     public <A extends Appendable> A appendTo(A appendable, Map<?, ?> map)
/*     */       throws IOException
/*     */     {
/* 366 */       return appendTo(appendable, map.entrySet());
/*     */     }
/*     */ 
/*     */     public StringBuilder appendTo(StringBuilder builder, Map<?, ?> map)
/*     */     {
/* 375 */       return appendTo(builder, map.entrySet());
/*     */     }
/*     */ 
/*     */     public String join(Map<?, ?> map)
/*     */     {
/* 383 */       return join(map.entrySet());
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     @Beta
/*     */     public <A extends Appendable, I,  extends Iterable<? extends Map.Entry<?, ?>>,  extends Iterator<? extends Map.Entry<?, ?>>> A appendTo(A appendable, I entries)
/*     */       throws IOException
/*     */     {
/* 401 */       Iterator iterator = (Iterator)entries;
/* 402 */       return appendTo(appendable, iterator);
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public <A extends Appendable> A appendTo(A appendable, Iterable<? extends Map.Entry<?, ?>> entries)
/*     */       throws IOException
/*     */     {
/* 414 */       return appendTo(appendable, entries.iterator());
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public <A extends Appendable> A appendTo(A appendable, Iterator<? extends Map.Entry<?, ?>> parts)
/*     */       throws IOException
/*     */     {
/* 426 */       Preconditions.checkNotNull(appendable);
/* 427 */       if (parts.hasNext()) {
/* 428 */         Map.Entry entry = (Map.Entry)parts.next();
/* 429 */         appendable.append(this.joiner.toString(entry.getKey()));
/* 430 */         appendable.append(this.keyValueSeparator);
/* 431 */         appendable.append(this.joiner.toString(entry.getValue()));
/* 432 */         while (parts.hasNext()) {
/* 433 */           appendable.append(this.joiner.separator);
/* 434 */           Map.Entry e = (Map.Entry)parts.next();
/* 435 */           appendable.append(this.joiner.toString(e.getKey()));
/* 436 */           appendable.append(this.keyValueSeparator);
/* 437 */           appendable.append(this.joiner.toString(e.getValue()));
/*     */         }
/*     */       }
/* 440 */       return appendable;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     @Beta
/*     */     public <I,  extends Iterable<? extends Map.Entry<?, ?>>,  extends Iterator<? extends Map.Entry<?, ?>>> StringBuilder appendTo(StringBuilder builder, I entries)
/*     */       throws IOException
/*     */     {
/* 457 */       Iterator iterator = (Iterator)entries;
/* 458 */       return appendTo(builder, iterator);
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public StringBuilder appendTo(StringBuilder builder, Iterable<? extends Map.Entry<?, ?>> entries)
/*     */     {
/* 470 */       return appendTo(builder, entries.iterator());
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public StringBuilder appendTo(StringBuilder builder, Iterator<? extends Map.Entry<?, ?>> entries)
/*     */     {
/*     */       try
/*     */       {
/* 483 */         appendTo(builder, entries);
/*     */       } catch (IOException impossible) {
/* 485 */         throw new AssertionError(impossible);
/*     */       }
/* 487 */       return builder;
/*     */     }
/*     */ 
/*     */     @Deprecated
/*     */     @Beta
/*     */     public <I,  extends Iterable<? extends Map.Entry<?, ?>>,  extends Iterator<? extends Map.Entry<?, ?>>> String join(I entries)
/*     */       throws IOException
/*     */     {
/* 504 */       Iterator iterator = (Iterator)entries;
/* 505 */       return join(iterator);
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public String join(Iterable<? extends Map.Entry<?, ?>> entries)
/*     */     {
/* 516 */       return join(entries.iterator());
/*     */     }
/*     */ 
/*     */     @Beta
/*     */     public String join(Iterator<? extends Map.Entry<?, ?>> entries)
/*     */     {
/* 527 */       return appendTo(new StringBuilder(), entries).toString();
/*     */     }
/*     */ 
/*     */     @CheckReturnValue
/*     */     public MapJoiner useForNull(String nullText)
/*     */     {
/* 536 */       return new MapJoiner(this.joiner.useForNull(nullText), this.keyValueSeparator);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.base.Joiner
 * JD-Core Version:    0.6.2
 */