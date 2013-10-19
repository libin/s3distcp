/*     */ package com.google.common.net;
/*     */ 
/*     */ import com.google.common.annotations.Beta;
/*     */ import com.google.common.annotations.GwtCompatible;
/*     */ import com.google.common.base.Ascii;
/*     */ import com.google.common.base.CharMatcher;
/*     */ import com.google.common.base.Charsets;
/*     */ import com.google.common.base.Function;
/*     */ import com.google.common.base.Joiner;
/*     */ import com.google.common.base.Joiner.MapJoiner;
/*     */ import com.google.common.base.Objects;
/*     */ import com.google.common.base.Optional;
/*     */ import com.google.common.base.Preconditions;
/*     */ import com.google.common.collect.ImmutableCollection;
/*     */ import com.google.common.collect.ImmutableListMultimap;
/*     */ import com.google.common.collect.ImmutableListMultimap.Builder;
/*     */ import com.google.common.collect.ImmutableMap;
/*     */ import com.google.common.collect.ImmutableMap.Builder;
/*     */ import com.google.common.collect.ImmutableMultiset;
/*     */ import com.google.common.collect.ImmutableSet;
/*     */ import com.google.common.collect.Iterables;
/*     */ import com.google.common.collect.Maps;
/*     */ import com.google.common.collect.Multimap;
/*     */ import com.google.common.collect.Multimaps;
/*     */ import java.nio.charset.Charset;
/*     */ import java.util.Collection;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import javax.annotation.Nullable;
/*     */ import javax.annotation.concurrent.Immutable;
/*     */ 
/*     */ @Beta
/*     */ @GwtCompatible
/*     */ @Immutable
/*     */ public final class MediaType
/*     */ {
/*     */   private static final String CHARSET_ATTRIBUTE = "charset";
/*  85 */   private static final ImmutableListMultimap<String, String> UTF_8_CONSTANT_PARAMETERS = ImmutableListMultimap.of("charset", Ascii.toLowerCase(Charsets.UTF_8.name()));
/*     */ 
/*  89 */   private static final CharMatcher TOKEN_MATCHER = CharMatcher.ASCII.and(CharMatcher.JAVA_ISO_CONTROL.negate()).and(CharMatcher.isNot(' ')).and(CharMatcher.noneOf("()<>@,;:\\\"/[]?="));
/*     */ 
/*  92 */   private static final CharMatcher QUOTED_TEXT_MATCHER = CharMatcher.ASCII.and(CharMatcher.noneOf("\"\\\r"));
/*     */ 
/*  98 */   private static final CharMatcher LINEAR_WHITE_SPACE = CharMatcher.anyOf(" \t\r\n");
/*     */   private static final String APPLICATION_TYPE = "application";
/*     */   private static final String AUDIO_TYPE = "audio";
/*     */   private static final String IMAGE_TYPE = "image";
/*     */   private static final String TEXT_TYPE = "text";
/*     */   private static final String VIDEO_TYPE = "video";
/*     */   private static final String WILDCARD = "*";
/* 120 */   public static final MediaType ANY_TYPE = createConstant("*", "*");
/* 121 */   public static final MediaType ANY_TEXT_TYPE = createConstant("text", "*");
/* 122 */   public static final MediaType ANY_IMAGE_TYPE = createConstant("image", "*");
/* 123 */   public static final MediaType ANY_AUDIO_TYPE = createConstant("audio", "*");
/* 124 */   public static final MediaType ANY_VIDEO_TYPE = createConstant("video", "*");
/* 125 */   public static final MediaType ANY_APPLICATION_TYPE = createConstant("application", "*");
/*     */ 
/* 128 */   public static final MediaType CACHE_MANIFEST_UTF_8 = createConstantUtf8("text", "cache-manifest");
/*     */ 
/* 130 */   public static final MediaType CSS_UTF_8 = createConstantUtf8("text", "css");
/* 131 */   public static final MediaType CSV_UTF_8 = createConstantUtf8("text", "csv");
/* 132 */   public static final MediaType HTML_UTF_8 = createConstantUtf8("text", "html");
/* 133 */   public static final MediaType I_CALENDAR_UTF_8 = createConstantUtf8("text", "calendar");
/* 134 */   public static final MediaType PLAIN_TEXT_UTF_8 = createConstantUtf8("text", "plain");
/*     */ 
/* 140 */   public static final MediaType TEXT_JAVASCRIPT_UTF_8 = createConstantUtf8("text", "javascript");
/* 141 */   public static final MediaType VCARD_UTF_8 = createConstantUtf8("text", "vcard");
/* 142 */   public static final MediaType XML_UTF_8 = createConstantUtf8("text", "xml");
/*     */ 
/* 145 */   public static final MediaType GIF = createConstant("image", "gif");
/* 146 */   public static final MediaType ICO = createConstant("image", "vnd.microsoft.icon");
/* 147 */   public static final MediaType JPEG = createConstant("image", "jpeg");
/* 148 */   public static final MediaType PNG = createConstant("image", "png");
/* 149 */   public static final MediaType SVG_UTF_8 = createConstantUtf8("image", "svg+xml");
/* 150 */   public static final MediaType TIFF = createConstant("image", "tiff");
/*     */ 
/* 153 */   public static final MediaType MP4_AUDIO = createConstant("audio", "mp4");
/* 154 */   public static final MediaType MPEG_AUDIO = createConstant("audio", "mpeg");
/* 155 */   public static final MediaType OGG_AUDIO = createConstant("audio", "ogg");
/* 156 */   public static final MediaType WEBM_AUDIO = createConstant("audio", "webm");
/*     */ 
/* 159 */   public static final MediaType MP4_VIDEO = createConstant("video", "mp4");
/* 160 */   public static final MediaType MPEG_VIDEO = createConstant("video", "mpeg");
/* 161 */   public static final MediaType OGG_VIDEO = createConstant("video", "ogg");
/* 162 */   public static final MediaType QUICKTIME = createConstant("video", "quicktime");
/* 163 */   public static final MediaType WEBM_VIDEO = createConstant("video", "webm");
/* 164 */   public static final MediaType WMV = createConstant("video", "x-ms-wmv");
/*     */ 
/* 167 */   public static final MediaType ATOM_UTF_8 = createConstantUtf8("application", "atom+xml");
/* 168 */   public static final MediaType BZIP2 = createConstant("application", "x-bzip2");
/* 169 */   public static final MediaType FORM_DATA = createConstant("application", "x-www-form-urlencoded");
/*     */ 
/* 171 */   public static final MediaType GZIP = createConstant("application", "x-gzip");
/*     */ 
/* 177 */   public static final MediaType JAVASCRIPT_UTF_8 = createConstantUtf8("application", "javascript");
/*     */ 
/* 179 */   public static final MediaType JSON_UTF_8 = createConstantUtf8("application", "json");
/* 180 */   public static final MediaType KML = createConstant("application", "vnd.google-earth.kml+xml");
/* 181 */   public static final MediaType KMZ = createConstant("application", "vnd.google-earth.kmz");
/* 182 */   public static final MediaType MICROSOFT_EXCEL = createConstant("application", "vnd.ms-excel");
/* 183 */   public static final MediaType MICROSOFT_POWERPOINT = createConstant("application", "vnd.ms-powerpoint");
/*     */ 
/* 185 */   public static final MediaType MICROSOFT_WORD = createConstant("application", "msword");
/* 186 */   public static final MediaType OCTET_STREAM = createConstant("application", "octet-stream");
/* 187 */   public static final MediaType OGG_CONTAINER = createConstant("application", "ogg");
/* 188 */   public static final MediaType OOXML_DOCUMENT = createConstant("application", "vnd.openxmlformats-officedocument.wordprocessingml.document");
/*     */ 
/* 190 */   public static final MediaType OOXML_PRESENTATION = createConstant("application", "vnd.openxmlformats-officedocument.presentationml.presentation");
/*     */ 
/* 192 */   public static final MediaType OOXML_SHEET = createConstant("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet");
/*     */ 
/* 194 */   public static final MediaType OPENDOCUMENT_GRAPHICS = createConstant("application", "vnd.oasis.opendocument.graphics");
/*     */ 
/* 196 */   public static final MediaType OPENDOCUMENT_PRESENTATION = createConstant("application", "vnd.oasis.opendocument.presentation");
/*     */ 
/* 198 */   public static final MediaType OPENDOCUMENT_SPREADSHEET = createConstant("application", "vnd.oasis.opendocument.spreadsheet");
/*     */ 
/* 200 */   public static final MediaType OPENDOCUMENT_TEXT = createConstant("application", "vnd.oasis.opendocument.text");
/*     */ 
/* 202 */   public static final MediaType PDF = createConstant("application", "pdf");
/* 203 */   public static final MediaType POSTSCRIPT = createConstant("application", "postscript");
/* 204 */   public static final MediaType RTF_UTF_8 = createConstantUtf8("application", "rtf");
/* 205 */   public static final MediaType SHOCKWAVE_FLASH = createConstant("application", "x-shockwave-flash");
/*     */ 
/* 207 */   public static final MediaType TAR = createConstant("application", "x-tar");
/* 208 */   public static final MediaType XHTML_UTF_8 = createConstantUtf8("application", "xhtml+xml");
/* 209 */   public static final MediaType ZIP = createConstant("application", "zip");
/*     */ 
/* 211 */   private static final ImmutableMap<MediaType, MediaType> KNOWN_TYPES = new ImmutableMap.Builder().put(ANY_TYPE, ANY_TYPE).put(ANY_TEXT_TYPE, ANY_TEXT_TYPE).put(ANY_IMAGE_TYPE, ANY_IMAGE_TYPE).put(ANY_AUDIO_TYPE, ANY_AUDIO_TYPE).put(ANY_VIDEO_TYPE, ANY_VIDEO_TYPE).put(ANY_APPLICATION_TYPE, ANY_APPLICATION_TYPE).put(CACHE_MANIFEST_UTF_8, CACHE_MANIFEST_UTF_8).put(CSS_UTF_8, CSS_UTF_8).put(CSV_UTF_8, CSV_UTF_8).put(HTML_UTF_8, HTML_UTF_8).put(I_CALENDAR_UTF_8, I_CALENDAR_UTF_8).put(PLAIN_TEXT_UTF_8, PLAIN_TEXT_UTF_8).put(TEXT_JAVASCRIPT_UTF_8, TEXT_JAVASCRIPT_UTF_8).put(VCARD_UTF_8, VCARD_UTF_8).put(XML_UTF_8, XML_UTF_8).put(GIF, GIF).put(ICO, ICO).put(JPEG, JPEG).put(PNG, PNG).put(SVG_UTF_8, SVG_UTF_8).put(TIFF, TIFF).put(MP4_AUDIO, MP4_AUDIO).put(MPEG_AUDIO, MPEG_AUDIO).put(OGG_AUDIO, OGG_AUDIO).put(WEBM_AUDIO, WEBM_AUDIO).put(MP4_VIDEO, MP4_VIDEO).put(MPEG_VIDEO, MPEG_VIDEO).put(OGG_VIDEO, OGG_VIDEO).put(QUICKTIME, QUICKTIME).put(WEBM_VIDEO, WEBM_VIDEO).put(WMV, WMV).put(ATOM_UTF_8, ATOM_UTF_8).put(BZIP2, BZIP2).put(FORM_DATA, FORM_DATA).put(GZIP, GZIP).put(JAVASCRIPT_UTF_8, JAVASCRIPT_UTF_8).put(JSON_UTF_8, JSON_UTF_8).put(KML, KML).put(KMZ, KMZ).put(MICROSOFT_EXCEL, MICROSOFT_EXCEL).put(MICROSOFT_POWERPOINT, MICROSOFT_POWERPOINT).put(MICROSOFT_WORD, MICROSOFT_WORD).put(OCTET_STREAM, OCTET_STREAM).put(OGG_CONTAINER, OGG_CONTAINER).put(OOXML_DOCUMENT, OOXML_DOCUMENT).put(OOXML_PRESENTATION, OOXML_PRESENTATION).put(OOXML_SHEET, OOXML_SHEET).put(OPENDOCUMENT_GRAPHICS, OPENDOCUMENT_GRAPHICS).put(OPENDOCUMENT_PRESENTATION, OPENDOCUMENT_PRESENTATION).put(OPENDOCUMENT_SPREADSHEET, OPENDOCUMENT_SPREADSHEET).put(OPENDOCUMENT_TEXT, OPENDOCUMENT_TEXT).put(PDF, PDF).put(POSTSCRIPT, POSTSCRIPT).put(RTF_UTF_8, RTF_UTF_8).put(SHOCKWAVE_FLASH, SHOCKWAVE_FLASH).put(TAR, TAR).put(XHTML_UTF_8, XHTML_UTF_8).put(ZIP, ZIP).build();
/*     */   private final String type;
/*     */   private final String subtype;
/*     */   private final ImmutableListMultimap<String, String> parameters;
/* 625 */   private static final Joiner.MapJoiner PARAMETER_JOINER = Joiner.on("; ").withKeyValueSeparator("=");
/*     */ 
/*     */   private MediaType(String type, String subtype, ImmutableListMultimap<String, String> parameters)
/*     */   {
/* 284 */     this.type = type;
/* 285 */     this.subtype = subtype;
/* 286 */     this.parameters = parameters;
/*     */   }
/*     */ 
/*     */   private static MediaType createConstant(String type, String subtype) {
/* 290 */     return new MediaType(type, subtype, ImmutableListMultimap.of());
/*     */   }
/*     */ 
/*     */   private static MediaType createConstantUtf8(String type, String subtype) {
/* 294 */     return new MediaType(type, subtype, UTF_8_CONSTANT_PARAMETERS);
/*     */   }
/*     */ 
/*     */   public String type()
/*     */   {
/* 299 */     return this.type;
/*     */   }
/*     */ 
/*     */   public String subtype()
/*     */   {
/* 304 */     return this.subtype;
/*     */   }
/*     */ 
/*     */   public ImmutableListMultimap<String, String> parameters()
/*     */   {
/* 309 */     return this.parameters;
/*     */   }
/*     */ 
/*     */   private Map<String, ImmutableMultiset<String>> parametersAsMap() {
/* 313 */     return Maps.transformValues(this.parameters.asMap(), new Function()
/*     */     {
/*     */       public ImmutableMultiset<String> apply(Collection<String> input) {
/* 316 */         return ImmutableMultiset.copyOf(input);
/*     */       }
/*     */     });
/*     */   }
/*     */ 
/*     */   public Optional<Charset> charset()
/*     */   {
/* 330 */     ImmutableSet charsetValues = ImmutableSet.copyOf(this.parameters.get("charset"));
/* 331 */     switch (charsetValues.size()) {
/*     */     case 0:
/* 333 */       return Optional.absent();
/*     */     case 1:
/* 335 */       return Optional.of(Charset.forName((String)Iterables.getOnlyElement(charsetValues)));
/*     */     }
/* 337 */     throw new IllegalStateException(new StringBuilder().append("Multiple charset values defined: ").append(charsetValues).toString());
/*     */   }
/*     */ 
/*     */   public MediaType withoutParameters()
/*     */   {
/* 346 */     return this.parameters.isEmpty() ? this : create(this.type, this.subtype);
/*     */   }
/*     */ 
/*     */   public MediaType withParameters(Multimap<String, String> parameters)
/*     */   {
/* 355 */     return create(this.type, this.subtype, parameters);
/*     */   }
/*     */ 
/*     */   public MediaType withParameter(String attribute, String value)
/*     */   {
/* 367 */     Preconditions.checkNotNull(attribute);
/* 368 */     Preconditions.checkNotNull(value);
/* 369 */     String normalizedAttribute = normalizeToken(attribute);
/* 370 */     ImmutableListMultimap.Builder builder = ImmutableListMultimap.builder();
/* 371 */     for (Map.Entry entry : this.parameters.entries()) {
/* 372 */       String key = (String)entry.getKey();
/* 373 */       if (!normalizedAttribute.equals(key)) {
/* 374 */         builder.put(key, entry.getValue());
/*     */       }
/*     */     }
/* 377 */     builder.put(normalizedAttribute, normalizeParameterValue(normalizedAttribute, value));
/* 378 */     MediaType mediaType = new MediaType(this.type, this.subtype, builder.build());
/*     */ 
/* 380 */     return (MediaType)Objects.firstNonNull(KNOWN_TYPES.get(mediaType), mediaType);
/*     */   }
/*     */ 
/*     */   public MediaType withCharset(Charset charset)
/*     */   {
/* 393 */     Preconditions.checkNotNull(charset);
/* 394 */     return withParameter("charset", charset.name());
/*     */   }
/*     */ 
/*     */   public boolean hasWildcard()
/*     */   {
/* 399 */     return ("*".equals(this.type)) || ("*".equals(this.subtype));
/*     */   }
/*     */ 
/*     */   public boolean is(MediaType mediaTypeRange)
/*     */   {
/* 429 */     return ((mediaTypeRange.type.equals("*")) || (mediaTypeRange.type.equals(this.type))) && ((mediaTypeRange.subtype.equals("*")) || (mediaTypeRange.subtype.equals(this.subtype))) && (this.parameters.entries().containsAll(mediaTypeRange.parameters.entries()));
/*     */   }
/*     */ 
/*     */   public static MediaType create(String type, String subtype)
/*     */   {
/* 441 */     return create(type, subtype, ImmutableListMultimap.of());
/*     */   }
/*     */ 
/*     */   static MediaType createApplicationType(String subtype)
/*     */   {
/* 450 */     return create("application", subtype);
/*     */   }
/*     */ 
/*     */   static MediaType createAudioType(String subtype)
/*     */   {
/* 459 */     return create("audio", subtype);
/*     */   }
/*     */ 
/*     */   static MediaType createImageType(String subtype)
/*     */   {
/* 468 */     return create("image", subtype);
/*     */   }
/*     */ 
/*     */   static MediaType createTextType(String subtype)
/*     */   {
/* 477 */     return create("text", subtype);
/*     */   }
/*     */ 
/*     */   static MediaType createVideoType(String subtype)
/*     */   {
/* 486 */     return create("video", subtype);
/*     */   }
/*     */ 
/*     */   private static MediaType create(String type, String subtype, Multimap<String, String> parameters)
/*     */   {
/* 491 */     Preconditions.checkNotNull(type);
/* 492 */     Preconditions.checkNotNull(subtype);
/* 493 */     Preconditions.checkNotNull(parameters);
/* 494 */     String normalizedType = normalizeToken(type);
/* 495 */     String normalizedSubtype = normalizeToken(subtype);
/* 496 */     Preconditions.checkArgument((!"*".equals(normalizedType)) || ("*".equals(normalizedSubtype)), "A wildcard type cannot be used with a non-wildcard subtype");
/*     */ 
/* 498 */     ImmutableListMultimap.Builder builder = ImmutableListMultimap.builder();
/* 499 */     for (Map.Entry entry : parameters.entries()) {
/* 500 */       String attribute = normalizeToken((String)entry.getKey());
/* 501 */       builder.put(attribute, normalizeParameterValue(attribute, (String)entry.getValue()));
/*     */     }
/* 503 */     MediaType mediaType = new MediaType(normalizedType, normalizedSubtype, builder.build());
/*     */ 
/* 505 */     return (MediaType)Objects.firstNonNull(KNOWN_TYPES.get(mediaType), mediaType);
/*     */   }
/*     */ 
/*     */   private static String normalizeToken(String token) {
/* 509 */     Preconditions.checkArgument(TOKEN_MATCHER.matchesAllOf(token));
/* 510 */     return Ascii.toLowerCase(token);
/*     */   }
/*     */ 
/*     */   private static String normalizeParameterValue(String attribute, String value) {
/* 514 */     return "charset".equals(attribute) ? Ascii.toLowerCase(value) : value;
/*     */   }
/*     */ 
/*     */   public static MediaType parse(String input)
/*     */   {
/* 523 */     Preconditions.checkNotNull(input);
/* 524 */     Tokenizer tokenizer = new Tokenizer(input);
/*     */     try {
/* 526 */       String type = tokenizer.consumeToken(TOKEN_MATCHER);
/* 527 */       tokenizer.consumeCharacter('/');
/* 528 */       String subtype = tokenizer.consumeToken(TOKEN_MATCHER);
/* 529 */       ImmutableListMultimap.Builder parameters = ImmutableListMultimap.builder();
/* 530 */       while (tokenizer.hasMore()) {
/* 531 */         tokenizer.consumeCharacter(';');
/* 532 */         tokenizer.consumeTokenIfPresent(LINEAR_WHITE_SPACE);
/* 533 */         String attribute = tokenizer.consumeToken(TOKEN_MATCHER);
/* 534 */         tokenizer.consumeCharacter('=');
/*     */         String value;
/* 536 */         if ('"' == tokenizer.previewChar()) {
/* 537 */           tokenizer.consumeCharacter('"');
/* 538 */           StringBuilder valueBuilder = new StringBuilder();
/* 539 */           while ('"' != tokenizer.previewChar()) {
/* 540 */             if ('\\' == tokenizer.previewChar()) {
/* 541 */               tokenizer.consumeCharacter('\\');
/* 542 */               valueBuilder.append(tokenizer.consumeCharacter(CharMatcher.ASCII));
/*     */             } else {
/* 544 */               valueBuilder.append(tokenizer.consumeToken(QUOTED_TEXT_MATCHER));
/*     */             }
/*     */           }
/* 547 */           String value = valueBuilder.toString();
/* 548 */           tokenizer.consumeCharacter('"');
/*     */         } else {
/* 550 */           value = tokenizer.consumeToken(TOKEN_MATCHER);
/*     */         }
/* 552 */         parameters.put(attribute, value);
/*     */       }
/* 554 */       return create(type, subtype, parameters.build());
/*     */     } catch (IllegalStateException e) {
/* 556 */       throw new IllegalArgumentException(e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean equals(@Nullable Object obj)
/*     */   {
/* 608 */     if (obj == this)
/* 609 */       return true;
/* 610 */     if ((obj instanceof MediaType)) {
/* 611 */       MediaType that = (MediaType)obj;
/* 612 */       return (this.type.equals(that.type)) && (this.subtype.equals(that.subtype)) && (parametersAsMap().equals(that.parametersAsMap()));
/*     */     }
/*     */ 
/* 617 */     return false;
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 622 */     return Objects.hashCode(new Object[] { this.type, this.subtype, parametersAsMap() });
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 632 */     StringBuilder builder = new StringBuilder().append(this.type).append('/').append(this.subtype);
/* 633 */     if (!this.parameters.isEmpty()) {
/* 634 */       builder.append("; ");
/* 635 */       Multimap quotedParameters = Multimaps.transformValues(this.parameters, new Function()
/*     */       {
/*     */         public String apply(String value) {
/* 638 */           return MediaType.TOKEN_MATCHER.matchesAllOf(value) ? value : MediaType.escapeAndQuote(value);
/*     */         }
/*     */       });
/* 641 */       PARAMETER_JOINER.appendTo(builder, quotedParameters.entries());
/*     */     }
/* 643 */     return builder.toString();
/*     */   }
/*     */ 
/*     */   private static String escapeAndQuote(String value) {
/* 647 */     StringBuilder escaped = new StringBuilder(value.length() + 16).append('"');
/* 648 */     for (char ch : value.toCharArray()) {
/* 649 */       if ((ch == '\r') || (ch == '\\') || (ch == '"')) {
/* 650 */         escaped.append('\\');
/*     */       }
/* 652 */       escaped.append(ch);
/*     */     }
/* 654 */     return escaped.append('"').toString();
/*     */   }
/*     */ 
/*     */   private static final class Tokenizer
/*     */   {
/*     */     final String input;
/* 562 */     int position = 0;
/*     */ 
/*     */     Tokenizer(String input) {
/* 565 */       this.input = input;
/*     */     }
/*     */ 
/*     */     String consumeTokenIfPresent(CharMatcher matcher) {
/* 569 */       Preconditions.checkState(hasMore());
/* 570 */       int startPosition = this.position;
/* 571 */       this.position = matcher.negate().indexIn(this.input, startPosition);
/* 572 */       return hasMore() ? this.input.substring(startPosition, this.position) : this.input.substring(startPosition);
/*     */     }
/*     */ 
/*     */     String consumeToken(CharMatcher matcher) {
/* 576 */       int startPosition = this.position;
/* 577 */       String token = consumeTokenIfPresent(matcher);
/* 578 */       Preconditions.checkState(this.position != startPosition);
/* 579 */       return token;
/*     */     }
/*     */ 
/*     */     char consumeCharacter(CharMatcher matcher) {
/* 583 */       Preconditions.checkState(hasMore());
/* 584 */       char c = previewChar();
/* 585 */       Preconditions.checkState(matcher.matches(c));
/* 586 */       this.position += 1;
/* 587 */       return c;
/*     */     }
/*     */ 
/*     */     char consumeCharacter(char c) {
/* 591 */       Preconditions.checkState(hasMore());
/* 592 */       Preconditions.checkState(previewChar() == c);
/* 593 */       this.position += 1;
/* 594 */       return c;
/*     */     }
/*     */ 
/*     */     char previewChar() {
/* 598 */       Preconditions.checkState(hasMore());
/* 599 */       return this.input.charAt(this.position);
/*     */     }
/*     */ 
/*     */     boolean hasMore() {
/* 603 */       return (this.position >= 0) && (this.position < this.input.length());
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.common.net.MediaType
 * JD-Core Version:    0.6.2
 */