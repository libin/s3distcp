/*     */ package com.google.common.net;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Ascii;
/*     */ import com.google.common.base.CharMatcher;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Objects.ToStringHelper;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.base.Splitter;
/*     */ import com.google.common.collect.ImmutableList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible(emulated=true)
/*     */ public final class InternetDomainName
/*     */ {
/*  79 */   private static final CharMatcher DOTS_MATCHER = CharMatcher.anyOf(".。．｡");
/*     */ 
/*  81 */   private static final Splitter DOT_SPLITTER = Splitter.on('.');
/*  82 */   private static final Joiner DOT_JOINER = Joiner.on('.');
/*     */   private static final int NO_PUBLIC_SUFFIX_FOUND = -1;
/*     */   private static final String DOT_REGEX = "\\.";
/*     */   private static final int MAX_PARTS = 127;
/*     */   private static final int MAX_LENGTH = 253;
/*     */   private static final int MAX_DOMAIN_PART_LENGTH = 63;
/*     */   private final String name;
/*     */   private final ImmutableList<String> parts;
/*     */   private final int publicSuffixIndex;
/* 253 */   private static final CharMatcher DASH_MATCHER = CharMatcher.anyOf("-_");
/*     */ 
/* 255 */   private static final CharMatcher PART_CHAR_MATCHER = CharMatcher.JAVA_LETTER_OR_DIGIT.or(DASH_MATCHER);
/*     */ 
/*     */   InternetDomainName(String name)
/*     */   {
/* 143 */     name = Ascii.toLowerCase(DOTS_MATCHER.replaceFrom(name, '.'));
/*     */ 
/* 145 */     if (name.endsWith(".")) {
/* 146 */       name = name.substring(0, name.length() - 1);
/*     */     }
/*     */ 
/* 149 */     Preconditions.checkArgument(name.length() <= 253, "Domain name too long: '%s':", new Object[] { name });
/* 150 */     this.name = name;
/*     */ 
/* 152 */     this.parts = ImmutableList.copyOf(DOT_SPLITTER.split(name));
/* 153 */     Preconditions.checkArgument(this.parts.size() <= 127, "Domain has too many parts: '%s'", new Object[] { name });
/* 154 */     Preconditions.checkArgument(validateSyntax(this.parts), "Not a valid domain name: '%s'", new Object[] { name });
/*     */ 
/* 156 */     this.publicSuffixIndex = findPublicSuffix();
/*     */   }
/*     */ 
/*     */   private int findPublicSuffix()
/*     */   {
/* 166 */     int partsSize = this.parts.size();
/*     */ 
/* 168 */     for (int i = 0; i < partsSize; i++) {
/* 169 */       String ancestorName = DOT_JOINER.join(this.parts.subList(i, partsSize));
/*     */ 
/* 171 */       if (TldPatterns.EXACT.contains(ancestorName)) {
/* 172 */         return i;
/*     */       }
/*     */ 
/* 178 */       if (TldPatterns.EXCLUDED.contains(ancestorName)) {
/* 179 */         return i + 1;
/*     */       }
/*     */ 
/* 182 */       if (matchesWildcardPublicSuffix(ancestorName)) {
/* 183 */         return i;
/*     */       }
/*     */     }
/*     */ 
/* 187 */     return -1;
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static InternetDomainName fromLenient(String domain)
/*     */   {
/* 201 */     return from(domain);
/*     */   }
/*     */ 
/*     */   public static InternetDomainName from(String domain)
/*     */   {
/* 225 */     return new InternetDomainName((String)Preconditions.checkNotNull(domain));
/*     */   }
/*     */ 
/*     */   private static boolean validateSyntax(List<String> parts)
/*     */   {
/* 235 */     int lastIndex = parts.size() - 1;
/*     */ 
/* 239 */     if (!validatePart((String)parts.get(lastIndex), true)) {
/* 240 */       return false;
/*     */     }
/*     */ 
/* 243 */     for (int i = 0; i < lastIndex; i++) {
/* 244 */       String part = (String)parts.get(i);
/* 245 */       if (!validatePart(part, false)) {
/* 246 */         return false;
/*     */       }
/*     */     }
/*     */ 
/* 250 */     return true;
/*     */   }
/*     */ 
/*     */   private static boolean validatePart(String part, boolean isFinalPart)
/*     */   {
/* 271 */     if ((part.length() < 1) || (part.length() > 63)) {
/* 272 */       return false;
/*     */     }
/*     */ 
/* 285 */     String asciiChars = CharMatcher.ASCII.retainFrom(part);
/*     */ 
/* 287 */     if (!PART_CHAR_MATCHER.matchesAllOf(asciiChars)) {
/* 288 */       return false;
/*     */     }
/*     */ 
/* 293 */     if ((DASH_MATCHER.matches(part.charAt(0))) || (DASH_MATCHER.matches(part.charAt(part.length() - 1))))
/*     */     {
/* 295 */       return false;
/*     */     }
/*     */ 
/* 306 */     if ((isFinalPart) && (CharMatcher.DIGIT.matches(part.charAt(0)))) {
/* 307 */       return false;
/*     */     }
/*     */ 
/* 310 */     return true;
/*     */   }
/*     */ 
/*     */   public String name()
/*     */   {
/* 317 */     return this.name;
/*     */   }
/*     */ 
/*     */   public ImmutableList<String> parts()
/*     */   {
/* 326 */     return this.parts;
/*     */   }
/*     */ 
/*     */   public boolean isPublicSuffix()
/*     */   {
/* 343 */     return this.publicSuffixIndex == 0;
/*     */   }
/*     */ 
/*     */   public boolean hasPublicSuffix()
/*     */   {
/* 357 */     return this.publicSuffixIndex != -1;
/*     */   }
/*     */ 
/*     */   public InternetDomainName publicSuffix()
/*     */   {
/* 367 */     return hasPublicSuffix() ? ancestor(this.publicSuffixIndex) : null;
/*     */   }
/*     */ 
/*     */   public boolean isUnderPublicSuffix()
/*     */   {
/* 390 */     return this.publicSuffixIndex > 0;
/*     */   }
/*     */ 
/*     */   public boolean isTopPrivateDomain()
/*     */   {
/* 414 */     return this.publicSuffixIndex == 1;
/*     */   }
/*     */ 
/*     */   public InternetDomainName topPrivateDomain()
/*     */   {
/* 440 */     if (isTopPrivateDomain()) {
/* 441 */       return this;
/*     */     }
/* 443 */     Preconditions.checkState(isUnderPublicSuffix(), "Not under a public suffix: %s", new Object[] { this.name });
/* 444 */     return ancestor(this.publicSuffixIndex - 1);
/*     */   }
/*     */ 
/*     */   public boolean hasParent()
/*     */   {
/* 451 */     return this.parts.size() > 1;
/*     */   }
/*     */ 
/*     */   public InternetDomainName parent()
/*     */   {
/* 463 */     Preconditions.checkState(hasParent(), "Domain '%s' has no parent", new Object[] { this.name });
/* 464 */     return ancestor(1);
/*     */   }
/*     */ 
/*     */   private InternetDomainName ancestor(int levels)
/*     */   {
/* 476 */     return from(DOT_JOINER.join(this.parts.subList(levels, this.parts.size())));
/*     */   }
/*     */ 
/*     */   public InternetDomainName child(String leftParts)
/*     */   {
/* 490 */     return from((String)Preconditions.checkNotNull(leftParts) + "." + this.name);
/*     */   }
/*     */ 
/*     */   @Deprecated
/*     */   public static boolean isValidLenient(String name)
/*     */   {
/* 501 */     return isValid(name);
/*     */   }
/*     */ 
/*     */   public static boolean isValid(String name)
/*     */   {
/*     */     try
/*     */     {
/* 531 */       from(name);
/* 532 */       return true; } catch (IllegalArgumentException e) {
/*     */     }
/* 534 */     return false;
/*     */   }
/*     */ 
/*     */   private static boolean matchesWildcardPublicSuffix(String domain)
/*     */   {
/* 543 */     String[] pieces = domain.split("\\.", 2);
/* 544 */     return (pieces.length == 2) && (TldPatterns.UNDER.contains(pieces[1]));
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 550 */     return Objects.toStringHelper(this).add("name", this.name).toString();
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object object)
/*     */   {
/* 562 */     if (object == this) {
/* 563 */       return true;
/*     */     }
/*     */ 
/* 566 */     if ((object instanceof InternetDomainName)) {
/* 567 */       InternetDomainName that = (InternetDomainName)object;
/* 568 */       return this.name.equals(that.name);
/*     */     }
/*     */ 
/* 571 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 576 */     return this.name.hashCode();
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.net.InternetDomainName
 * JD-Core Version:    0.6.2
 */