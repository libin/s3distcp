/*     */ package com.google.gson.stream;
/*     */ 
/*     */ import java.io.Closeable;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ public class JsonWriter
/*     */   implements Closeable
/*     */ {
/*     */   private final Writer out;
/* 128 */   private final List<JsonScope> stack = new ArrayList();
/*     */   private String indent;
/*     */   private String separator;
/*     */   private boolean lenient;
/*     */   private boolean htmlSafe;
/*     */   private String deferredName;
/*     */   private boolean serializeNulls;
/*     */ 
/*     */   public JsonWriter(Writer out)
/*     */   {
/* 130 */     this.stack.add(JsonScope.EMPTY_DOCUMENT);
/*     */ 
/* 142 */     this.separator = ":";
/*     */ 
/* 150 */     this.serializeNulls = true;
/*     */ 
/* 158 */     if (out == null) {
/* 159 */       throw new NullPointerException("out == null");
/*     */     }
/* 161 */     this.out = out;
/*     */   }
/*     */ 
/*     */   public final void setIndent(String indent)
/*     */   {
/* 173 */     if (indent.length() == 0) {
/* 174 */       this.indent = null;
/* 175 */       this.separator = ":";
/*     */     } else {
/* 177 */       this.indent = indent;
/* 178 */       this.separator = ": ";
/*     */     }
/*     */   }
/*     */ 
/*     */   public final void setLenient(boolean lenient)
/*     */   {
/* 195 */     this.lenient = lenient;
/*     */   }
/*     */ 
/*     */   public boolean isLenient()
/*     */   {
/* 202 */     return this.lenient;
/*     */   }
/*     */ 
/*     */   public final void setHtmlSafe(boolean htmlSafe)
/*     */   {
/* 213 */     this.htmlSafe = htmlSafe;
/*     */   }
/*     */ 
/*     */   public final boolean isHtmlSafe()
/*     */   {
/* 221 */     return this.htmlSafe;
/*     */   }
/*     */ 
/*     */   public final void setSerializeNulls(boolean serializeNulls)
/*     */   {
/* 229 */     this.serializeNulls = serializeNulls;
/*     */   }
/*     */ 
/*     */   public final boolean getSerializeNulls()
/*     */   {
/* 237 */     return this.serializeNulls;
/*     */   }
/*     */ 
/*     */   public JsonWriter beginArray()
/*     */     throws IOException
/*     */   {
/* 247 */     writeDeferredName();
/* 248 */     return open(JsonScope.EMPTY_ARRAY, "[");
/*     */   }
/*     */ 
/*     */   public JsonWriter endArray()
/*     */     throws IOException
/*     */   {
/* 257 */     return close(JsonScope.EMPTY_ARRAY, JsonScope.NONEMPTY_ARRAY, "]");
/*     */   }
/*     */ 
/*     */   public JsonWriter beginObject()
/*     */     throws IOException
/*     */   {
/* 267 */     writeDeferredName();
/* 268 */     return open(JsonScope.EMPTY_OBJECT, "{");
/*     */   }
/*     */ 
/*     */   public JsonWriter endObject()
/*     */     throws IOException
/*     */   {
/* 277 */     return close(JsonScope.EMPTY_OBJECT, JsonScope.NONEMPTY_OBJECT, "}");
/*     */   }
/*     */ 
/*     */   private JsonWriter open(JsonScope empty, String openBracket)
/*     */     throws IOException
/*     */   {
/* 285 */     beforeValue(true);
/* 286 */     this.stack.add(empty);
/* 287 */     this.out.write(openBracket);
/* 288 */     return this;
/*     */   }
/*     */ 
/*     */   private JsonWriter close(JsonScope empty, JsonScope nonempty, String closeBracket)
/*     */     throws IOException
/*     */   {
/* 297 */     JsonScope context = peek();
/* 298 */     if ((context != nonempty) && (context != empty)) {
/* 299 */       throw new IllegalStateException("Nesting problem: " + this.stack);
/*     */     }
/* 301 */     if (this.deferredName != null) {
/* 302 */       throw new IllegalStateException("Dangling name: " + this.deferredName);
/*     */     }
/*     */ 
/* 305 */     this.stack.remove(this.stack.size() - 1);
/* 306 */     if (context == nonempty) {
/* 307 */       newline();
/*     */     }
/* 309 */     this.out.write(closeBracket);
/* 310 */     return this;
/*     */   }
/*     */ 
/*     */   private JsonScope peek()
/*     */   {
/* 317 */     return (JsonScope)this.stack.get(this.stack.size() - 1);
/*     */   }
/*     */ 
/*     */   private void replaceTop(JsonScope topOfStack)
/*     */   {
/* 324 */     this.stack.set(this.stack.size() - 1, topOfStack);
/*     */   }
/*     */ 
/*     */   public JsonWriter name(String name)
/*     */     throws IOException
/*     */   {
/* 334 */     if (name == null) {
/* 335 */       throw new NullPointerException("name == null");
/*     */     }
/* 337 */     if (this.deferredName != null) {
/* 338 */       throw new IllegalStateException();
/*     */     }
/* 340 */     this.deferredName = name;
/* 341 */     return this;
/*     */   }
/*     */ 
/*     */   private void writeDeferredName() throws IOException {
/* 345 */     if (this.deferredName != null) {
/* 346 */       beforeName();
/* 347 */       string(this.deferredName);
/* 348 */       this.deferredName = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   public JsonWriter value(String value)
/*     */     throws IOException
/*     */   {
/* 359 */     if (value == null) {
/* 360 */       return nullValue();
/*     */     }
/* 362 */     writeDeferredName();
/* 363 */     beforeValue(false);
/* 364 */     string(value);
/* 365 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter nullValue()
/*     */     throws IOException
/*     */   {
/* 374 */     if (this.deferredName != null) {
/* 375 */       if (this.serializeNulls) {
/* 376 */         writeDeferredName();
/*     */       } else {
/* 378 */         this.deferredName = null;
/* 379 */         return this;
/*     */       }
/*     */     }
/* 382 */     beforeValue(false);
/* 383 */     this.out.write("null");
/* 384 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(boolean value)
/*     */     throws IOException
/*     */   {
/* 393 */     writeDeferredName();
/* 394 */     beforeValue(false);
/* 395 */     this.out.write(value ? "true" : "false");
/* 396 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(double value)
/*     */     throws IOException
/*     */   {
/* 407 */     if ((Double.isNaN(value)) || (Double.isInfinite(value))) {
/* 408 */       throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
/*     */     }
/* 410 */     writeDeferredName();
/* 411 */     beforeValue(false);
/* 412 */     this.out.append(Double.toString(value));
/* 413 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(long value)
/*     */     throws IOException
/*     */   {
/* 422 */     writeDeferredName();
/* 423 */     beforeValue(false);
/* 424 */     this.out.write(Long.toString(value));
/* 425 */     return this;
/*     */   }
/*     */ 
/*     */   public JsonWriter value(Number value)
/*     */     throws IOException
/*     */   {
/* 436 */     if (value == null) {
/* 437 */       return nullValue();
/*     */     }
/*     */ 
/* 440 */     writeDeferredName();
/* 441 */     String string = value.toString();
/* 442 */     if ((!this.lenient) && ((string.equals("-Infinity")) || (string.equals("Infinity")) || (string.equals("NaN"))))
/*     */     {
/* 444 */       throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
/*     */     }
/* 446 */     beforeValue(false);
/* 447 */     this.out.append(string);
/* 448 */     return this;
/*     */   }
/*     */ 
/*     */   public void flush()
/*     */     throws IOException
/*     */   {
/* 456 */     this.out.flush();
/*     */   }
/*     */ 
/*     */   public void close()
/*     */     throws IOException
/*     */   {
/* 465 */     this.out.close();
/*     */ 
/* 467 */     if (peek() != JsonScope.NONEMPTY_DOCUMENT)
/* 468 */       throw new IOException("Incomplete document");
/*     */   }
/*     */ 
/*     */   private void string(String value) throws IOException
/*     */   {
/* 473 */     this.out.write("\"");
/* 474 */     int i = 0; for (int length = value.length(); i < length; i++) {
/* 475 */       char c = value.charAt(i);
/*     */ 
/* 487 */       switch (c) {
/*     */       case '"':
/*     */       case '\\':
/* 490 */         this.out.write(92);
/* 491 */         this.out.write(c);
/* 492 */         break;
/*     */       case '\t':
/* 495 */         this.out.write("\\t");
/* 496 */         break;
/*     */       case '\b':
/* 499 */         this.out.write("\\b");
/* 500 */         break;
/*     */       case '\n':
/* 503 */         this.out.write("\\n");
/* 504 */         break;
/*     */       case '\r':
/* 507 */         this.out.write("\\r");
/* 508 */         break;
/*     */       case '\f':
/* 511 */         this.out.write("\\f");
/* 512 */         break;
/*     */       case '&':
/*     */       case '\'':
/*     */       case '<':
/*     */       case '=':
/*     */       case '>':
/* 519 */         if (this.htmlSafe)
/* 520 */           this.out.write(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
/*     */         else {
/* 522 */           this.out.write(c);
/*     */         }
/* 524 */         break;
/*     */       case ' ':
/*     */       case ' ':
/* 528 */         this.out.write(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
/* 529 */         break;
/*     */       default:
/* 532 */         if (c <= '\037')
/* 533 */           this.out.write(String.format("\\u%04x", new Object[] { Integer.valueOf(c) }));
/*     */         else {
/* 535 */           this.out.write(c);
/*     */         }
/*     */         break;
/*     */       }
/*     */     }
/* 540 */     this.out.write("\"");
/*     */   }
/*     */ 
/*     */   private void newline() throws IOException {
/* 544 */     if (this.indent == null) {
/* 545 */       return;
/*     */     }
/*     */ 
/* 548 */     this.out.write("\n");
/* 549 */     for (int i = 1; i < this.stack.size(); i++)
/* 550 */       this.out.write(this.indent);
/*     */   }
/*     */ 
/*     */   private void beforeName()
/*     */     throws IOException
/*     */   {
/* 559 */     JsonScope context = peek();
/* 560 */     if (context == JsonScope.NONEMPTY_OBJECT)
/* 561 */       this.out.write(44);
/* 562 */     else if (context != JsonScope.EMPTY_OBJECT) {
/* 563 */       throw new IllegalStateException("Nesting problem: " + this.stack);
/*     */     }
/* 565 */     newline();
/* 566 */     replaceTop(JsonScope.DANGLING_NAME);
/*     */   }
/*     */ 
/*     */   private void beforeValue(boolean root)
/*     */     throws IOException
/*     */   {
/* 578 */     switch (1.$SwitchMap$com$google$gson$stream$JsonScope[peek().ordinal()]) {
/*     */     case 1:
/* 580 */       if ((!this.lenient) && (!root)) {
/* 581 */         throw new IllegalStateException("JSON must start with an array or an object.");
/*     */       }
/*     */ 
/* 584 */       replaceTop(JsonScope.NONEMPTY_DOCUMENT);
/* 585 */       break;
/*     */     case 2:
/* 588 */       replaceTop(JsonScope.NONEMPTY_ARRAY);
/* 589 */       newline();
/* 590 */       break;
/*     */     case 3:
/* 593 */       this.out.append(',');
/* 594 */       newline();
/* 595 */       break;
/*     */     case 4:
/* 598 */       this.out.append(this.separator);
/* 599 */       replaceTop(JsonScope.NONEMPTY_OBJECT);
/* 600 */       break;
/*     */     case 5:
/* 603 */       throw new IllegalStateException("JSON must have only one top-level value.");
/*     */     default:
/* 607 */       throw new IllegalStateException("Nesting problem: " + this.stack);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.stream.JsonWriter
 * JD-Core Version:    0.6.2
 */