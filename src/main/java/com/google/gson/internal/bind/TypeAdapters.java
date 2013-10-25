/*     */ package com.google.gson.internal.bind;
/*     */ 
/*     */ import com.google.gson.Gson;
/*     */ import com.google.gson.JsonArray;
/*     */ import com.google.gson.JsonElement;
/*     */ import com.google.gson.JsonIOException;
/*     */ import com.google.gson.JsonNull;
/*     */ import com.google.gson.JsonObject;
/*     */ import com.google.gson.JsonPrimitive;
/*     */ import com.google.gson.JsonSyntaxException;
/*     */ import com.google.gson.TypeAdapter;
/*     */ import com.google.gson.TypeAdapterFactory;
/*     */ import com.google.gson.annotations.SerializedName;
/*     */ import com.google.gson.internal.LazilyParsedNumber;
/*     */ import com.google.gson.reflect.TypeToken;
/*     */ import com.google.gson.stream.JsonReader;
/*     */ import com.google.gson.stream.JsonToken;
/*     */ import com.google.gson.stream.JsonWriter;
/*     */ import java.io.IOException;
/*     */ import java.lang.reflect.Field;
/*     */ import java.net.InetAddress;
/*     */ import java.net.URI;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.BitSet;
/*     */ import java.util.Calendar;
/*     */ import java.util.Date;
/*     */ import java.util.GregorianCalendar;
/*     */ import java.util.HashMap;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.StringTokenizer;
/*     */ import java.util.UUID;
/*     */ 
/*     */ public final class TypeAdapters
/*     */ {
/*  59 */   public static final TypeAdapter<Class> CLASS = new TypeAdapter()
/*     */   {
/*     */     public void write(JsonWriter out, Class value) throws IOException {
/*  62 */       throw new UnsupportedOperationException("Attempted to serialize java.lang.Class: " + value.getName() + ". Forgot to register a type adapter?");
/*     */     }
/*     */ 
/*     */     public Class read(JsonReader in) throws IOException
/*     */     {
/*  67 */       throw new UnsupportedOperationException("Attempted to deserialize a java.lang.Class. Forgot to register a type adapter?");
/*     */     }
/*  59 */   };
/*     */ 
/*  71 */   public static final TypeAdapterFactory CLASS_FACTORY = newFactory(Class.class, CLASS);
/*     */ 
/*  73 */   public static final TypeAdapter<BitSet> BIT_SET = new TypeAdapter() {
/*     */     public BitSet read(JsonReader in) throws IOException {
/*  75 */       if (in.peek() == JsonToken.NULL) {
/*  76 */         in.nextNull();
/*  77 */         return null;
/*     */       }
/*     */ 
/*  80 */       BitSet bitset = new BitSet();
/*  81 */       in.beginArray();
/*  82 */       int i = 0;
/*  83 */       JsonToken tokenType = in.peek();
/*  84 */       while (tokenType != JsonToken.END_ARRAY)
/*     */       {
/*     */         boolean set;
/*  86 */         switch (TypeAdapters.30.$SwitchMap$com$google$gson$stream$JsonToken[tokenType.ordinal()]) {
/*     */         case 1:
/*  88 */           set = in.nextInt() != 0;
/*  89 */           break;
/*     */         case 2:
/*  91 */           set = in.nextBoolean();
/*  92 */           break;
/*     */         case 3:
/*  94 */           String stringValue = in.nextString();
/*     */           try {
/*  96 */             set = Integer.parseInt(stringValue) != 0;
/*     */           } catch (NumberFormatException e) {
/*  98 */             throw new JsonSyntaxException("Error: Expecting: bitset number value (1, 0), Found: " + stringValue);
/*     */           }
/*     */ 
/*     */         default:
/* 103 */           throw new JsonSyntaxException("Invalid bitset value type: " + tokenType);
/*     */         }
/* 105 */         if (set) {
/* 106 */           bitset.set(i);
/*     */         }
/* 108 */         i++;
/* 109 */         tokenType = in.peek();
/*     */       }
/* 111 */       in.endArray();
/* 112 */       return bitset;
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, BitSet src) throws IOException {
/* 116 */       if (src == null) {
/* 117 */         out.nullValue();
/* 118 */         return;
/*     */       }
/*     */ 
/* 121 */       out.beginArray();
/* 122 */       for (int i = 0; i < src.length(); i++) {
/* 123 */         int value = src.get(i) ? 1 : 0;
/* 124 */         out.value(value);
/*     */       }
/* 126 */       out.endArray();
/*     */     }
/*  73 */   };
/*     */ 
/* 130 */   public static final TypeAdapterFactory BIT_SET_FACTORY = newFactory(BitSet.class, BIT_SET);
/*     */ 
/* 132 */   public static final TypeAdapter<Boolean> BOOLEAN = new TypeAdapter()
/*     */   {
/*     */     public Boolean read(JsonReader in) throws IOException {
/* 135 */       if (in.peek() == JsonToken.NULL) {
/* 136 */         in.nextNull();
/* 137 */         return null;
/* 138 */       }if (in.peek() == JsonToken.STRING)
/*     */       {
/* 140 */         return Boolean.valueOf(Boolean.parseBoolean(in.nextString()));
/*     */       }
/* 142 */       return Boolean.valueOf(in.nextBoolean());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Boolean value) throws IOException {
/* 146 */       if (value == null) {
/* 147 */         out.nullValue();
/* 148 */         return;
/*     */       }
/* 150 */       out.value(value.booleanValue());
/*     */     }
/* 132 */   };
/*     */ 
/* 158 */   public static final TypeAdapter<Boolean> BOOLEAN_AS_STRING = new TypeAdapter() {
/*     */     public Boolean read(JsonReader in) throws IOException {
/* 160 */       if (in.peek() == JsonToken.NULL) {
/* 161 */         in.nextNull();
/* 162 */         return null;
/*     */       }
/* 164 */       return Boolean.valueOf(in.nextString());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Boolean value) throws IOException {
/* 168 */       out.value(value == null ? "null" : value.toString());
/*     */     }
/* 158 */   };
/*     */ 
/* 172 */   public static final TypeAdapterFactory BOOLEAN_FACTORY = newFactory(Boolean.TYPE, Boolean.class, BOOLEAN);
/*     */ 
/* 175 */   public static final TypeAdapter<Number> BYTE = new TypeAdapter()
/*     */   {
/*     */     public Number read(JsonReader in) throws IOException {
/* 178 */       if (in.peek() == JsonToken.NULL) {
/* 179 */         in.nextNull();
/* 180 */         return null;
/*     */       }
/*     */       try {
/* 183 */         int intValue = in.nextInt();
/* 184 */         return Byte.valueOf((byte)intValue);
/*     */       } catch (NumberFormatException e) {
/* 186 */         throw new JsonSyntaxException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Number value) throws IOException {
/* 191 */       out.value(value);
/*     */     }
/* 175 */   };
/*     */ 
/* 195 */   public static final TypeAdapterFactory BYTE_FACTORY = newFactory(Byte.TYPE, Byte.class, BYTE);
/*     */ 
/* 198 */   public static final TypeAdapter<Number> SHORT = new TypeAdapter()
/*     */   {
/*     */     public Number read(JsonReader in) throws IOException {
/* 201 */       if (in.peek() == JsonToken.NULL) {
/* 202 */         in.nextNull();
/* 203 */         return null;
/*     */       }
/*     */       try {
/* 206 */         return Short.valueOf((short)in.nextInt());
/*     */       } catch (NumberFormatException e) {
/* 208 */         throw new JsonSyntaxException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Number value) throws IOException {
/* 213 */       out.value(value);
/*     */     }
/* 198 */   };
/*     */ 
/* 217 */   public static final TypeAdapterFactory SHORT_FACTORY = newFactory(Short.TYPE, Short.class, SHORT);
/*     */ 
/* 220 */   public static final TypeAdapter<Number> INTEGER = new TypeAdapter()
/*     */   {
/*     */     public Number read(JsonReader in) throws IOException {
/* 223 */       if (in.peek() == JsonToken.NULL) {
/* 224 */         in.nextNull();
/* 225 */         return null;
/*     */       }
/*     */       try {
/* 228 */         return Integer.valueOf(in.nextInt());
/*     */       } catch (NumberFormatException e) {
/* 230 */         throw new JsonSyntaxException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Number value) throws IOException {
/* 235 */       out.value(value);
/*     */     }
/* 220 */   };
/*     */ 
/* 239 */   public static final TypeAdapterFactory INTEGER_FACTORY = newFactory(Integer.TYPE, Integer.class, INTEGER);
/*     */ 
/* 242 */   public static final TypeAdapter<Number> LONG = new TypeAdapter()
/*     */   {
/*     */     public Number read(JsonReader in) throws IOException {
/* 245 */       if (in.peek() == JsonToken.NULL) {
/* 246 */         in.nextNull();
/* 247 */         return null;
/*     */       }
/*     */       try {
/* 250 */         return Long.valueOf(in.nextLong());
/*     */       } catch (NumberFormatException e) {
/* 252 */         throw new JsonSyntaxException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Number value) throws IOException {
/* 257 */       out.value(value);
/*     */     }
/* 242 */   };
/*     */ 
/* 261 */   public static final TypeAdapter<Number> FLOAT = new TypeAdapter()
/*     */   {
/*     */     public Number read(JsonReader in) throws IOException {
/* 264 */       if (in.peek() == JsonToken.NULL) {
/* 265 */         in.nextNull();
/* 266 */         return null;
/*     */       }
/* 268 */       return Float.valueOf((float)in.nextDouble());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Number value) throws IOException {
/* 272 */       out.value(value);
/*     */     }
/* 261 */   };
/*     */ 
/* 276 */   public static final TypeAdapter<Number> DOUBLE = new TypeAdapter()
/*     */   {
/*     */     public Number read(JsonReader in) throws IOException {
/* 279 */       if (in.peek() == JsonToken.NULL) {
/* 280 */         in.nextNull();
/* 281 */         return null;
/*     */       }
/* 283 */       return Double.valueOf(in.nextDouble());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Number value) throws IOException {
/* 287 */       out.value(value);
/*     */     }
/* 276 */   };
/*     */ 
/* 291 */   public static final TypeAdapter<Number> NUMBER = new TypeAdapter()
/*     */   {
/*     */     public Number read(JsonReader in) throws IOException {
/* 294 */       JsonToken jsonToken = in.peek();
/* 295 */       switch (TypeAdapters.30.$SwitchMap$com$google$gson$stream$JsonToken[jsonToken.ordinal()]) {
/*     */       case 4:
/* 297 */         in.nextNull();
/* 298 */         return null;
/*     */       case 1:
/* 300 */         return new LazilyParsedNumber(in.nextString());
/*     */       }
/* 302 */       throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Number value) throws IOException
/*     */     {
/* 307 */       out.value(value);
/*     */     }
/* 291 */   };
/*     */ 
/* 311 */   public static final TypeAdapterFactory NUMBER_FACTORY = newFactory(Number.class, NUMBER);
/*     */ 
/* 313 */   public static final TypeAdapter<Character> CHARACTER = new TypeAdapter()
/*     */   {
/*     */     public Character read(JsonReader in) throws IOException {
/* 316 */       if (in.peek() == JsonToken.NULL) {
/* 317 */         in.nextNull();
/* 318 */         return null;
/*     */       }
/* 320 */       String str = in.nextString();
/* 321 */       if (str.length() != 1) {
/* 322 */         throw new JsonSyntaxException("Expecting character, got: " + str);
/*     */       }
/* 324 */       return Character.valueOf(str.charAt(0));
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Character value) throws IOException {
/* 328 */       out.value(value == null ? null : String.valueOf(value));
/*     */     }
/* 313 */   };
/*     */ 
/* 332 */   public static final TypeAdapterFactory CHARACTER_FACTORY = newFactory(Character.TYPE, Character.class, CHARACTER);
/*     */ 
/* 335 */   public static final TypeAdapter<String> STRING = new TypeAdapter()
/*     */   {
/*     */     public String read(JsonReader in) throws IOException {
/* 338 */       JsonToken peek = in.peek();
/* 339 */       if (peek == JsonToken.NULL) {
/* 340 */         in.nextNull();
/* 341 */         return null;
/*     */       }
/*     */ 
/* 344 */       if (peek == JsonToken.BOOLEAN) {
/* 345 */         return Boolean.toString(in.nextBoolean());
/*     */       }
/* 347 */       return in.nextString();
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, String value) throws IOException {
/* 351 */       out.value(value);
/*     */     }
/* 335 */   };
/*     */ 
/* 355 */   public static final TypeAdapterFactory STRING_FACTORY = newFactory(String.class, STRING);
/*     */ 
/* 357 */   public static final TypeAdapter<StringBuilder> STRING_BUILDER = new TypeAdapter()
/*     */   {
/*     */     public StringBuilder read(JsonReader in) throws IOException {
/* 360 */       if (in.peek() == JsonToken.NULL) {
/* 361 */         in.nextNull();
/* 362 */         return null;
/*     */       }
/* 364 */       return new StringBuilder(in.nextString());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, StringBuilder value) throws IOException {
/* 368 */       out.value(value == null ? null : value.toString());
/*     */     }
/* 357 */   };
/*     */ 
/* 372 */   public static final TypeAdapterFactory STRING_BUILDER_FACTORY = newFactory(StringBuilder.class, STRING_BUILDER);
/*     */ 
/* 375 */   public static final TypeAdapter<StringBuffer> STRING_BUFFER = new TypeAdapter()
/*     */   {
/*     */     public StringBuffer read(JsonReader in) throws IOException {
/* 378 */       if (in.peek() == JsonToken.NULL) {
/* 379 */         in.nextNull();
/* 380 */         return null;
/*     */       }
/* 382 */       return new StringBuffer(in.nextString());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, StringBuffer value) throws IOException {
/* 386 */       out.value(value == null ? null : value.toString());
/*     */     }
/* 375 */   };
/*     */ 
/* 390 */   public static final TypeAdapterFactory STRING_BUFFER_FACTORY = newFactory(StringBuffer.class, STRING_BUFFER);
/*     */ 
/* 393 */   public static final TypeAdapter<URL> URL = new TypeAdapter()
/*     */   {
/*     */     public URL read(JsonReader in) throws IOException {
/* 396 */       if (in.peek() == JsonToken.NULL) {
/* 397 */         in.nextNull();
/* 398 */         return null;
/*     */       }
/* 400 */       String nextString = in.nextString();
/* 401 */       return "null".equals(nextString) ? null : new URL(nextString);
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, URL value) throws IOException {
/* 405 */       out.value(value == null ? null : value.toExternalForm());
/*     */     }
/* 393 */   };
/*     */ 
/* 409 */   public static final TypeAdapterFactory URL_FACTORY = newFactory(URL.class, URL);
/*     */ 
/* 411 */   public static final TypeAdapter<URI> URI = new TypeAdapter()
/*     */   {
/*     */     public URI read(JsonReader in) throws IOException {
/* 414 */       if (in.peek() == JsonToken.NULL) {
/* 415 */         in.nextNull();
/* 416 */         return null;
/*     */       }
/*     */       try {
/* 419 */         String nextString = in.nextString();
/* 420 */         return "null".equals(nextString) ? null : new URI(nextString);
/*     */       } catch (URISyntaxException e) {
/* 422 */         throw new JsonIOException(e);
/*     */       }
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, URI value) throws IOException {
/* 427 */       out.value(value == null ? null : value.toASCIIString());
/*     */     }
/* 411 */   };
/*     */ 
/* 431 */   public static final TypeAdapterFactory URI_FACTORY = newFactory(URI.class, URI);
/*     */ 
/* 433 */   public static final TypeAdapter<InetAddress> INET_ADDRESS = new TypeAdapter()
/*     */   {
/*     */     public InetAddress read(JsonReader in) throws IOException {
/* 436 */       if (in.peek() == JsonToken.NULL) {
/* 437 */         in.nextNull();
/* 438 */         return null;
/*     */       }
/*     */ 
/* 441 */       return InetAddress.getByName(in.nextString());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, InetAddress value) throws IOException {
/* 445 */       out.value(value == null ? null : value.getHostAddress());
/*     */     }
/* 433 */   };
/*     */ 
/* 449 */   public static final TypeAdapterFactory INET_ADDRESS_FACTORY = newTypeHierarchyFactory(InetAddress.class, INET_ADDRESS);
/*     */ 
/* 452 */   public static final TypeAdapter<UUID> UUID = new TypeAdapter()
/*     */   {
/*     */     public UUID read(JsonReader in) throws IOException {
/* 455 */       if (in.peek() == JsonToken.NULL) {
/* 456 */         in.nextNull();
/* 457 */         return null;
/*     */       }
/* 459 */       return UUID.fromString(in.nextString());
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, UUID value) throws IOException {
/* 463 */       out.value(value == null ? null : value.toString());
/*     */     }
/* 452 */   };
/*     */ 
/* 467 */   public static final TypeAdapterFactory UUID_FACTORY = newFactory(UUID.class, UUID);
/*     */ 
/* 469 */   public static final TypeAdapterFactory TIMESTAMP_FACTORY = new TypeAdapterFactory()
/*     */   {
/*     */     public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 472 */       if (typeToken.getRawType() != Timestamp.class) {
/* 473 */         return null;
/*     */       }
/*     */ 
/* 476 */       final TypeAdapter dateTypeAdapter = gson.getAdapter(Date.class);
/* 477 */       return new TypeAdapter() {
/*     */         public Timestamp read(JsonReader in) throws IOException {
/* 479 */           Date date = (Date)dateTypeAdapter.read(in);
/* 480 */           return date != null ? new Timestamp(date.getTime()) : null;
/*     */         }
/*     */ 
/*     */         public void write(JsonWriter out, Timestamp value) throws IOException {
/* 484 */           dateTypeAdapter.write(out, value);
/*     */         }
/*     */       };
/*     */     }
/* 469 */   };
/*     */ 
/* 490 */   public static final TypeAdapter<Calendar> CALENDAR = new TypeAdapter() { private static final String YEAR = "year";
/*     */     private static final String MONTH = "month";
/*     */     private static final String DAY_OF_MONTH = "dayOfMonth";
/*     */     private static final String HOUR_OF_DAY = "hourOfDay";
/*     */     private static final String MINUTE = "minute";
/*     */     private static final String SECOND = "second";
/*     */ 
/* 500 */     public Calendar read(JsonReader in) throws IOException { if (in.peek() == JsonToken.NULL) {
/* 501 */         in.nextNull();
/* 502 */         return null;
/*     */       }
/* 504 */       in.beginObject();
/* 505 */       int year = 0;
/* 506 */       int month = 0;
/* 507 */       int dayOfMonth = 0;
/* 508 */       int hourOfDay = 0;
/* 509 */       int minute = 0;
/* 510 */       int second = 0;
/* 511 */       while (in.peek() != JsonToken.END_OBJECT) {
/* 512 */         String name = in.nextName();
/* 513 */         int value = in.nextInt();
/* 514 */         if ("year".equals(name))
/* 515 */           year = value;
/* 516 */         else if ("month".equals(name))
/* 517 */           month = value;
/* 518 */         else if ("dayOfMonth".equals(name))
/* 519 */           dayOfMonth = value;
/* 520 */         else if ("hourOfDay".equals(name))
/* 521 */           hourOfDay = value;
/* 522 */         else if ("minute".equals(name))
/* 523 */           minute = value;
/* 524 */         else if ("second".equals(name)) {
/* 525 */           second = value;
/*     */         }
/*     */       }
/* 528 */       in.endObject();
/* 529 */       return new GregorianCalendar(year, month, dayOfMonth, hourOfDay, minute, second); }
/*     */ 
/*     */     public void write(JsonWriter out, Calendar value)
/*     */       throws IOException
/*     */     {
/* 534 */       if (value == null) {
/* 535 */         out.nullValue();
/* 536 */         return;
/*     */       }
/* 538 */       out.beginObject();
/* 539 */       out.name("year");
/* 540 */       out.value(value.get(1));
/* 541 */       out.name("month");
/* 542 */       out.value(value.get(2));
/* 543 */       out.name("dayOfMonth");
/* 544 */       out.value(value.get(5));
/* 545 */       out.name("hourOfDay");
/* 546 */       out.value(value.get(11));
/* 547 */       out.name("minute");
/* 548 */       out.value(value.get(12));
/* 549 */       out.name("second");
/* 550 */       out.value(value.get(13));
/* 551 */       out.endObject();
/*     */     }
/* 490 */   };
/*     */ 
/* 555 */   public static final TypeAdapterFactory CALENDAR_FACTORY = newFactoryForMultipleTypes(Calendar.class, GregorianCalendar.class, CALENDAR);
/*     */ 
/* 558 */   public static final TypeAdapter<Locale> LOCALE = new TypeAdapter()
/*     */   {
/*     */     public Locale read(JsonReader in) throws IOException {
/* 561 */       if (in.peek() == JsonToken.NULL) {
/* 562 */         in.nextNull();
/* 563 */         return null;
/*     */       }
/* 565 */       String locale = in.nextString();
/* 566 */       StringTokenizer tokenizer = new StringTokenizer(locale, "_");
/* 567 */       String language = null;
/* 568 */       String country = null;
/* 569 */       String variant = null;
/* 570 */       if (tokenizer.hasMoreElements()) {
/* 571 */         language = tokenizer.nextToken();
/*     */       }
/* 573 */       if (tokenizer.hasMoreElements()) {
/* 574 */         country = tokenizer.nextToken();
/*     */       }
/* 576 */       if (tokenizer.hasMoreElements()) {
/* 577 */         variant = tokenizer.nextToken();
/*     */       }
/* 579 */       if ((country == null) && (variant == null))
/* 580 */         return new Locale(language);
/* 581 */       if (variant == null) {
/* 582 */         return new Locale(language, country);
/*     */       }
/* 584 */       return new Locale(language, country, variant);
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, Locale value) throws IOException
/*     */     {
/* 589 */       out.value(value == null ? null : value.toString());
/*     */     }
/* 558 */   };
/*     */ 
/* 593 */   public static final TypeAdapterFactory LOCALE_FACTORY = newFactory(Locale.class, LOCALE);
/*     */ 
/* 595 */   public static final TypeAdapter<JsonElement> JSON_ELEMENT = new TypeAdapter() {
/*     */     public JsonElement read(JsonReader in) throws IOException {
/* 597 */       switch (TypeAdapters.30.$SwitchMap$com$google$gson$stream$JsonToken[in.peek().ordinal()]) {
/*     */       case 3:
/* 599 */         return new JsonPrimitive(in.nextString());
/*     */       case 1:
/* 601 */         String number = in.nextString();
/* 602 */         return new JsonPrimitive(new LazilyParsedNumber(number));
/*     */       case 2:
/* 604 */         return new JsonPrimitive(Boolean.valueOf(in.nextBoolean()));
/*     */       case 4:
/* 606 */         in.nextNull();
/* 607 */         return JsonNull.INSTANCE;
/*     */       case 5:
/* 609 */         JsonArray array = new JsonArray();
/* 610 */         in.beginArray();
/* 611 */         while (in.hasNext()) {
/* 612 */           array.add(read(in));
/*     */         }
/* 614 */         in.endArray();
/* 615 */         return array;
/*     */       case 6:
/* 617 */         JsonObject object = new JsonObject();
/* 618 */         in.beginObject();
/* 619 */         while (in.hasNext()) {
/* 620 */           object.add(in.nextName(), read(in));
/*     */         }
/* 622 */         in.endObject();
/* 623 */         return object;
/*     */       case 7:
/*     */       case 8:
/*     */       case 9:
/*     */       case 10:
/*     */       }
/* 629 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/*     */     public void write(JsonWriter out, JsonElement value) throws IOException
/*     */     {
/* 634 */       if ((value == null) || (value.isJsonNull())) {
/* 635 */         out.nullValue();
/* 636 */       } else if (value.isJsonPrimitive()) {
/* 637 */         JsonPrimitive primitive = value.getAsJsonPrimitive();
/* 638 */         if (primitive.isNumber())
/* 639 */           out.value(primitive.getAsNumber());
/* 640 */         else if (primitive.isBoolean())
/* 641 */           out.value(primitive.getAsBoolean());
/*     */         else {
/* 643 */           out.value(primitive.getAsString());
/*     */         }
/*     */       }
/* 646 */       else if (value.isJsonArray()) {
/* 647 */         out.beginArray();
/* 648 */         for (JsonElement e : value.getAsJsonArray()) {
/* 649 */           write(out, e);
/*     */         }
/* 651 */         out.endArray();
/*     */       }
/* 653 */       else if (value.isJsonObject()) {
/* 654 */         out.beginObject();
/* 655 */         for (Map.Entry e : value.getAsJsonObject().entrySet()) {
/* 656 */           out.name((String)e.getKey());
/* 657 */           write(out, (JsonElement)e.getValue());
/*     */         }
/* 659 */         out.endObject();
/*     */       }
/*     */       else {
/* 662 */         throw new IllegalArgumentException("Couldn't write " + value.getClass());
/*     */       }
/*     */     }
/* 595 */   };
/*     */ 
/* 667 */   public static final TypeAdapterFactory JSON_ELEMENT_FACTORY = newFactory(JsonElement.class, JSON_ELEMENT);
/*     */ 
/* 702 */   public static final TypeAdapterFactory ENUM_FACTORY = newEnumTypeHierarchyFactory();
/*     */ 
/*     */   public static <TT> TypeAdapterFactory newEnumTypeHierarchyFactory() {
/* 705 */     return new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 708 */         Class rawType = typeToken.getRawType();
/* 709 */         if ((!Enum.class.isAssignableFrom(rawType)) || (rawType == Enum.class)) {
/* 710 */           return null;
/*     */         }
/* 712 */         if (!rawType.isEnum()) {
/* 713 */           rawType = rawType.getSuperclass();
/*     */         }
/* 715 */         return new TypeAdapters.EnumTypeAdapter(rawType);
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <TT> TypeAdapterFactory newFactory(TypeToken<TT> type, final TypeAdapter<TT> typeAdapter)
/*     */   {
/* 722 */     return new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 725 */         return typeToken.equals(this.val$type) ? typeAdapter : null;
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <TT> TypeAdapterFactory newFactory(Class<TT> type, final TypeAdapter<TT> typeAdapter)
/*     */   {
/* 732 */     return new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 735 */         return typeToken.getRawType() == this.val$type ? typeAdapter : null;
/*     */       }
/*     */       public String toString() {
/* 738 */         return "Factory[type=" + this.val$type.getName() + ",adapter=" + typeAdapter + "]";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <TT> TypeAdapterFactory newFactory(Class<TT> unboxed, final Class<TT> boxed, final TypeAdapter<? super TT> typeAdapter)
/*     */   {
/* 745 */     return new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 748 */         Class rawType = typeToken.getRawType();
/* 749 */         return (rawType == this.val$unboxed) || (rawType == boxed) ? typeAdapter : null;
/*     */       }
/*     */       public String toString() {
/* 752 */         return "Factory[type=" + boxed.getName() + "+" + this.val$unboxed.getName() + ",adapter=" + typeAdapter + "]";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <TT> TypeAdapterFactory newFactoryForMultipleTypes(Class<TT> base, final Class<? extends TT> sub, final TypeAdapter<? super TT> typeAdapter)
/*     */   {
/* 760 */     return new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 763 */         Class rawType = typeToken.getRawType();
/* 764 */         return (rawType == this.val$base) || (rawType == sub) ? typeAdapter : null;
/*     */       }
/*     */       public String toString() {
/* 767 */         return "Factory[type=" + this.val$base.getName() + "+" + sub.getName() + ",adapter=" + typeAdapter + "]";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   public static <TT> TypeAdapterFactory newTypeHierarchyFactory(Class<TT> clazz, final TypeAdapter<TT> typeAdapter)
/*     */   {
/* 775 */     return new TypeAdapterFactory()
/*     */     {
/*     */       public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
/* 778 */         return this.val$clazz.isAssignableFrom(typeToken.getRawType()) ? typeAdapter : null;
/*     */       }
/*     */       public String toString() {
/* 781 */         return "Factory[typeHierarchy=" + this.val$clazz.getName() + ",adapter=" + typeAdapter + "]";
/*     */       }
/*     */     };
/*     */   }
/*     */ 
/*     */   private static final class EnumTypeAdapter<T extends Enum<T>> extends TypeAdapter<T>
/*     */   {
/* 671 */     private final Map<String, T> nameToConstant = new HashMap();
/* 672 */     private final Map<T, String> constantToName = new HashMap();
/*     */ 
/*     */     public EnumTypeAdapter(Class<T> classOfT) {
/*     */       try {
/* 676 */         for (Enum constant : (Enum[])classOfT.getEnumConstants()) {
/* 677 */           String name = constant.name();
/* 678 */           SerializedName annotation = (SerializedName)classOfT.getField(name).getAnnotation(SerializedName.class);
/* 679 */           if (annotation != null) {
/* 680 */             name = annotation.value();
/*     */           }
/* 682 */           this.nameToConstant.put(name, constant);
/* 683 */           this.constantToName.put(constant, name);
/*     */         }
/*     */       } catch (NoSuchFieldException e) {
/* 686 */         throw new AssertionError();
/*     */       }
/*     */     }
/*     */ 
/* 690 */     public T read(JsonReader in) throws IOException { if (in.peek() == JsonToken.NULL) {
/* 691 */         in.nextNull();
/* 692 */         return null;
/*     */       }
/* 694 */       return (Enum)this.nameToConstant.get(in.nextString()); }
/*     */ 
/*     */     public void write(JsonWriter out, T value) throws IOException
/*     */     {
/* 698 */       out.value(value == null ? null : (String)this.constantToName.get(value));
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.internal.bind.TypeAdapters
 * JD-Core Version:    0.6.2
 */