/*      */ package com.google.gson.stream;
/*      */ 
/*      */ import com.google.gson.internal.JsonReaderInternalAccess;
/*      */ import com.google.gson.internal.bind.JsonTreeReader;
/*      */ import java.io.Closeable;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.Reader;
/*      */ 
/*      */ public class JsonReader
/*      */   implements Closeable
/*      */ {
/*  193 */   private static final char[] NON_EXECUTE_PREFIX = ")]}'\n".toCharArray();
/*      */   private static final String TRUE = "true";
/*      */   private static final String FALSE = "false";
/*  198 */   private final StringPool stringPool = new StringPool();
/*      */   private final Reader in;
/*  204 */   private boolean lenient = false;
/*      */ 
/*  212 */   private final char[] buffer = new char[1024];
/*  213 */   private int pos = 0;
/*  214 */   private int limit = 0;
/*      */ 
/*  219 */   private int bufferStartLine = 1;
/*  220 */   private int bufferStartColumn = 1;
/*      */ 
/*  225 */   private JsonScope[] stack = new JsonScope[32];
/*  226 */   private int stackSize = 0;
/*      */   private JsonToken token;
/*      */   private String name;
/*      */   private String value;
/*      */   private int valuePos;
/*      */   private int valueLength;
/*      */   private boolean skipping;
/*      */ 
/*      */   public JsonReader(Reader in)
/*      */   {
/*  228 */     push(JsonScope.EMPTY_DOCUMENT);
/*      */ 
/*  249 */     this.skipping = false;
/*      */ 
/*  255 */     if (in == null) {
/*  256 */       throw new NullPointerException("in == null");
/*      */     }
/*  258 */     this.in = in;
/*      */   }
/*      */ 
/*      */   public final void setLenient(boolean lenient)
/*      */   {
/*  291 */     this.lenient = lenient;
/*      */   }
/*      */ 
/*      */   public final boolean isLenient()
/*      */   {
/*  298 */     return this.lenient;
/*      */   }
/*      */ 
/*      */   public void beginArray()
/*      */     throws IOException
/*      */   {
/*  306 */     expect(JsonToken.BEGIN_ARRAY);
/*      */   }
/*      */ 
/*      */   public void endArray()
/*      */     throws IOException
/*      */   {
/*  314 */     expect(JsonToken.END_ARRAY);
/*      */   }
/*      */ 
/*      */   public void beginObject()
/*      */     throws IOException
/*      */   {
/*  322 */     expect(JsonToken.BEGIN_OBJECT);
/*      */   }
/*      */ 
/*      */   public void endObject()
/*      */     throws IOException
/*      */   {
/*  330 */     expect(JsonToken.END_OBJECT);
/*      */   }
/*      */ 
/*      */   private void expect(JsonToken expected)
/*      */     throws IOException
/*      */   {
/*  337 */     peek();
/*  338 */     if (this.token != expected) {
/*  339 */       throw new IllegalStateException("Expected " + expected + " but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  342 */     advance();
/*      */   }
/*      */ 
/*      */   public boolean hasNext()
/*      */     throws IOException
/*      */   {
/*  349 */     peek();
/*  350 */     return (this.token != JsonToken.END_OBJECT) && (this.token != JsonToken.END_ARRAY);
/*      */   }
/*      */ 
/*      */   public JsonToken peek()
/*      */     throws IOException
/*      */   {
/*  357 */     if (this.token != null) {
/*  358 */       return this.token;
/*      */     }
/*      */ 
/*  361 */     switch (2.$SwitchMap$com$google$gson$stream$JsonScope[this.stack[(this.stackSize - 1)].ordinal()]) {
/*      */     case 1:
/*  363 */       if (this.lenient) {
/*  364 */         consumeNonExecutePrefix();
/*      */       }
/*  366 */       this.stack[(this.stackSize - 1)] = JsonScope.NONEMPTY_DOCUMENT;
/*  367 */       JsonToken firstToken = nextValue();
/*  368 */       if ((!this.lenient) && (this.token != JsonToken.BEGIN_ARRAY) && (this.token != JsonToken.BEGIN_OBJECT)) {
/*  369 */         throw new IOException("Expected JSON document to start with '[' or '{' but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */       }
/*      */ 
/*  372 */       return firstToken;
/*      */     case 2:
/*  374 */       return nextInArray(true);
/*      */     case 3:
/*  376 */       return nextInArray(false);
/*      */     case 4:
/*  378 */       return nextInObject(true);
/*      */     case 5:
/*  380 */       return objectValue();
/*      */     case 6:
/*  382 */       return nextInObject(false);
/*      */     case 7:
/*  384 */       int c = nextNonWhitespace(false);
/*  385 */       if (c == -1) {
/*  386 */         return JsonToken.END_DOCUMENT;
/*      */       }
/*  388 */       this.pos -= 1;
/*  389 */       if (!this.lenient) {
/*  390 */         throw syntaxError("Expected EOF");
/*      */       }
/*  392 */       return nextValue();
/*      */     case 8:
/*  394 */       throw new IllegalStateException("JsonReader is closed");
/*      */     }
/*  396 */     throw new AssertionError();
/*      */   }
/*      */ 
/*      */   private void consumeNonExecutePrefix()
/*      */     throws IOException
/*      */   {
/*  405 */     nextNonWhitespace(true);
/*  406 */     this.pos -= 1;
/*      */ 
/*  408 */     if ((this.pos + NON_EXECUTE_PREFIX.length > this.limit) && (!fillBuffer(NON_EXECUTE_PREFIX.length))) {
/*  409 */       return;
/*      */     }
/*      */ 
/*  412 */     for (int i = 0; i < NON_EXECUTE_PREFIX.length; i++) {
/*  413 */       if (this.buffer[(this.pos + i)] != NON_EXECUTE_PREFIX[i]) {
/*  414 */         return;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  419 */     this.pos += NON_EXECUTE_PREFIX.length;
/*      */   }
/*      */ 
/*      */   private JsonToken advance()
/*      */     throws IOException
/*      */   {
/*  426 */     peek();
/*      */ 
/*  428 */     JsonToken result = this.token;
/*  429 */     this.token = null;
/*  430 */     this.value = null;
/*  431 */     this.name = null;
/*  432 */     return result;
/*      */   }
/*      */ 
/*      */   public String nextName()
/*      */     throws IOException
/*      */   {
/*  443 */     peek();
/*  444 */     if (this.token != JsonToken.NAME) {
/*  445 */       throw new IllegalStateException("Expected a name but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  448 */     String result = this.name;
/*  449 */     advance();
/*  450 */     return result;
/*      */   }
/*      */ 
/*      */   public String nextString()
/*      */     throws IOException
/*      */   {
/*  462 */     peek();
/*  463 */     if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
/*  464 */       throw new IllegalStateException("Expected a string but was " + peek() + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  468 */     String result = this.value;
/*  469 */     advance();
/*  470 */     return result;
/*      */   }
/*      */ 
/*      */   public boolean nextBoolean()
/*      */     throws IOException
/*      */   {
/*  481 */     peek();
/*  482 */     if (this.token != JsonToken.BOOLEAN) {
/*  483 */       throw new IllegalStateException("Expected a boolean but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  487 */     boolean result = this.value == "true";
/*  488 */     advance();
/*  489 */     return result;
/*      */   }
/*      */ 
/*      */   public void nextNull()
/*      */     throws IOException
/*      */   {
/*  500 */     peek();
/*  501 */     if (this.token != JsonToken.NULL) {
/*  502 */       throw new IllegalStateException("Expected null but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  506 */     advance();
/*      */   }
/*      */ 
/*      */   public double nextDouble()
/*      */     throws IOException
/*      */   {
/*  519 */     peek();
/*  520 */     if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
/*  521 */       throw new IllegalStateException("Expected a double but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  525 */     double result = Double.parseDouble(this.value);
/*      */ 
/*  527 */     if ((result >= 1.0D) && (this.value.startsWith("0"))) {
/*  528 */       throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  531 */     if ((!this.lenient) && ((Double.isNaN(result)) || (Double.isInfinite(result)))) {
/*  532 */       throw new MalformedJsonException("JSON forbids NaN and infinities: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  536 */     advance();
/*  537 */     return result;
/*      */   }
/*      */ 
/*      */   public long nextLong()
/*      */     throws IOException
/*      */   {
/*  551 */     peek();
/*  552 */     if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
/*  553 */       throw new IllegalStateException("Expected a long but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*      */     long result;
/*      */     try
/*      */     {
/*  559 */       result = Long.parseLong(this.value);
/*      */     } catch (NumberFormatException ignored) {
/*  561 */       double asDouble = Double.parseDouble(this.value);
/*  562 */       result = ()asDouble;
/*  563 */       if (result != asDouble) {
/*  564 */         throw new NumberFormatException("Expected a long but was " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  569 */     if ((result >= 1L) && (this.value.startsWith("0"))) {
/*  570 */       throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  574 */     advance();
/*  575 */     return result;
/*      */   }
/*      */ 
/*      */   public int nextInt()
/*      */     throws IOException
/*      */   {
/*  589 */     peek();
/*  590 */     if ((this.token != JsonToken.STRING) && (this.token != JsonToken.NUMBER)) {
/*  591 */       throw new IllegalStateException("Expected an int but was " + this.token + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*      */     int result;
/*      */     try
/*      */     {
/*  597 */       result = Integer.parseInt(this.value);
/*      */     } catch (NumberFormatException ignored) {
/*  599 */       double asDouble = Double.parseDouble(this.value);
/*  600 */       result = (int)asDouble;
/*  601 */       if (result != asDouble) {
/*  602 */         throw new NumberFormatException("Expected an int but was " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  607 */     if ((result >= 1L) && (this.value.startsWith("0"))) {
/*  608 */       throw new MalformedJsonException("JSON forbids octal prefixes: " + this.value + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  612 */     advance();
/*  613 */     return result;
/*      */   }
/*      */ 
/*      */   public void close()
/*      */     throws IOException
/*      */   {
/*  620 */     this.value = null;
/*  621 */     this.token = null;
/*  622 */     this.stack[0] = JsonScope.CLOSED;
/*  623 */     this.stackSize = 1;
/*  624 */     this.in.close();
/*      */   }
/*      */ 
/*      */   public void skipValue()
/*      */     throws IOException
/*      */   {
/*  633 */     this.skipping = true;
/*      */     try {
/*  635 */       int count = 0;
/*      */       do {
/*  637 */         JsonToken token = advance();
/*  638 */         if ((token == JsonToken.BEGIN_ARRAY) || (token == JsonToken.BEGIN_OBJECT))
/*  639 */           count++;
/*  640 */         else if ((token == JsonToken.END_ARRAY) || (token == JsonToken.END_OBJECT))
/*  641 */           count--;
/*      */       }
/*  643 */       while (count != 0);
/*      */     } finally {
/*  645 */       this.skipping = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void push(JsonScope newTop) {
/*  650 */     if (this.stackSize == this.stack.length) {
/*  651 */       JsonScope[] newStack = new JsonScope[this.stackSize * 2];
/*  652 */       System.arraycopy(this.stack, 0, newStack, 0, this.stackSize);
/*  653 */       this.stack = newStack;
/*      */     }
/*  655 */     this.stack[(this.stackSize++)] = newTop;
/*      */   }
/*      */ 
/*      */   private JsonToken nextInArray(boolean firstElement) throws IOException
/*      */   {
/*  660 */     if (firstElement) {
/*  661 */       this.stack[(this.stackSize - 1)] = JsonScope.NONEMPTY_ARRAY;
/*      */     }
/*      */     else {
/*  664 */       switch (nextNonWhitespace(true)) {
/*      */       case 93:
/*  666 */         this.stackSize -= 1;
/*  667 */         return this.token = JsonToken.END_ARRAY;
/*      */       case 59:
/*  669 */         checkLenient();
/*      */       case 44:
/*  671 */         break;
/*      */       default:
/*  673 */         throw syntaxError("Unterminated array");
/*      */       }
/*      */     }
/*      */ 
/*  677 */     switch (nextNonWhitespace(true)) {
/*      */     case 93:
/*  679 */       if (firstElement) {
/*  680 */         this.stackSize -= 1;
/*  681 */         return this.token = JsonToken.END_ARRAY;
/*      */       }
/*      */ 
/*      */     case 44:
/*      */     case 59:
/*  687 */       checkLenient();
/*  688 */       this.pos -= 1;
/*  689 */       this.value = "null";
/*  690 */       return this.token = JsonToken.NULL;
/*      */     }
/*  692 */     this.pos -= 1;
/*  693 */     return nextValue();
/*      */   }
/*      */ 
/*      */   private JsonToken nextInObject(boolean firstElement)
/*      */     throws IOException
/*      */   {
/*  704 */     if (firstElement)
/*      */     {
/*  706 */       switch (nextNonWhitespace(true)) {
/*      */       case 125:
/*  708 */         this.stackSize -= 1;
/*  709 */         return this.token = JsonToken.END_OBJECT;
/*      */       }
/*  711 */       this.pos -= 1;
/*      */     }
/*      */     else {
/*  714 */       switch (nextNonWhitespace(true)) {
/*      */       case 125:
/*  716 */         this.stackSize -= 1;
/*  717 */         return this.token = JsonToken.END_OBJECT;
/*      */       case 44:
/*      */       case 59:
/*  720 */         break;
/*      */       default:
/*  722 */         throw syntaxError("Unterminated object");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  727 */     int quote = nextNonWhitespace(true);
/*  728 */     switch (quote) {
/*      */     case 39:
/*  730 */       checkLenient();
/*      */     case 34:
/*  732 */       this.name = nextString((char)quote);
/*  733 */       break;
/*      */     default:
/*  735 */       checkLenient();
/*  736 */       this.pos -= 1;
/*  737 */       this.name = nextLiteral(false);
/*  738 */       if (this.name.length() == 0) {
/*  739 */         throw syntaxError("Expected name");
/*      */       }
/*      */       break;
/*      */     }
/*  743 */     this.stack[(this.stackSize - 1)] = JsonScope.DANGLING_NAME;
/*  744 */     return this.token = JsonToken.NAME;
/*      */   }
/*      */ 
/*      */   private JsonToken objectValue()
/*      */     throws IOException
/*      */   {
/*  752 */     switch (nextNonWhitespace(true)) {
/*      */     case 58:
/*  754 */       break;
/*      */     case 61:
/*  756 */       checkLenient();
/*  757 */       if (((this.pos < this.limit) || (fillBuffer(1))) && (this.buffer[this.pos] == '>'))
/*  758 */         this.pos += 1; break;
/*      */     default:
/*  762 */       throw syntaxError("Expected ':'");
/*      */     }
/*      */ 
/*  765 */     this.stack[(this.stackSize - 1)] = JsonScope.NONEMPTY_OBJECT;
/*  766 */     return nextValue();
/*      */   }
/*      */ 
/*      */   private JsonToken nextValue() throws IOException
/*      */   {
/*  771 */     int c = nextNonWhitespace(true);
/*  772 */     switch (c) {
/*      */     case 123:
/*  774 */       push(JsonScope.EMPTY_OBJECT);
/*  775 */       return this.token = JsonToken.BEGIN_OBJECT;
/*      */     case 91:
/*  778 */       push(JsonScope.EMPTY_ARRAY);
/*  779 */       return this.token = JsonToken.BEGIN_ARRAY;
/*      */     case 39:
/*  782 */       checkLenient();
/*      */     case 34:
/*  784 */       this.value = nextString((char)c);
/*  785 */       return this.token = JsonToken.STRING;
/*      */     }
/*      */ 
/*  788 */     this.pos -= 1;
/*  789 */     return readLiteral();
/*      */   }
/*      */ 
/*      */   private boolean fillBuffer(int minimum)
/*      */     throws IOException
/*      */   {
/*  799 */     char[] buffer = this.buffer;
/*      */ 
/*  803 */     int line = this.bufferStartLine;
/*  804 */     int column = this.bufferStartColumn;
/*  805 */     int i = 0; for (int p = this.pos; i < p; i++) {
/*  806 */       if (buffer[i] == '\n') {
/*  807 */         line++;
/*  808 */         column = 1;
/*      */       } else {
/*  810 */         column++;
/*      */       }
/*      */     }
/*  813 */     this.bufferStartLine = line;
/*  814 */     this.bufferStartColumn = column;
/*      */ 
/*  816 */     if (this.limit != this.pos) {
/*  817 */       this.limit -= this.pos;
/*  818 */       System.arraycopy(buffer, this.pos, buffer, 0, this.limit);
/*      */     } else {
/*  820 */       this.limit = 0;
/*      */     }
/*      */ 
/*  823 */     this.pos = 0;
/*      */     int total;
/*  825 */     while ((total = this.in.read(buffer, this.limit, buffer.length - this.limit)) != -1) {
/*  826 */       this.limit += total;
/*      */ 
/*  829 */       if ((this.bufferStartLine == 1) && (this.bufferStartColumn == 1) && (this.limit > 0) && (buffer[0] == 65279)) {
/*  830 */         this.pos += 1;
/*  831 */         this.bufferStartColumn -= 1;
/*      */       }
/*      */ 
/*  834 */       if (this.limit >= minimum) {
/*  835 */         return true;
/*      */       }
/*      */     }
/*  838 */     return false;
/*      */   }
/*      */ 
/*      */   private int getLineNumber() {
/*  842 */     int result = this.bufferStartLine;
/*  843 */     for (int i = 0; i < this.pos; i++) {
/*  844 */       if (this.buffer[i] == '\n') {
/*  845 */         result++;
/*      */       }
/*      */     }
/*  848 */     return result;
/*      */   }
/*      */ 
/*      */   private int getColumnNumber() {
/*  852 */     int result = this.bufferStartColumn;
/*  853 */     for (int i = 0; i < this.pos; i++) {
/*  854 */       if (this.buffer[i] == '\n')
/*  855 */         result = 1;
/*      */       else {
/*  857 */         result++;
/*      */       }
/*      */     }
/*  860 */     return result;
/*      */   }
/*      */ 
/*      */   private int nextNonWhitespace(boolean throwOnEof)
/*      */     throws IOException
/*      */   {
/*  872 */     char[] buffer = this.buffer;
/*  873 */     int p = this.pos;
/*  874 */     int l = this.limit;
/*      */     int c;
/*      */     while (true)
/*      */     {
/*  876 */       if (p == l) {
/*  877 */         this.pos = p;
/*  878 */         if (!fillBuffer(1)) {
/*      */           break;
/*      */         }
/*  881 */         p = this.pos;
/*  882 */         l = this.limit;
/*      */       }
/*      */ 
/*  885 */       c = buffer[(p++)];
/*  886 */       switch (c) {
/*      */       case 9:
/*      */       case 10:
/*      */       case 13:
/*      */       case 32:
/*  891 */         break;
/*      */       case 47:
/*  894 */         this.pos = p;
/*  895 */         if ((p == l) && (!fillBuffer(1))) {
/*  896 */           return c;
/*      */         }
/*      */ 
/*  899 */         checkLenient();
/*  900 */         char peek = buffer[this.pos];
/*  901 */         switch (peek)
/*      */         {
/*      */         case '*':
/*  904 */           this.pos += 1;
/*  905 */           if (!skipTo("*/")) {
/*  906 */             throw syntaxError("Unterminated comment");
/*      */           }
/*  908 */           p = this.pos + 2;
/*  909 */           l = this.limit;
/*  910 */           break;
/*      */         case '/':
/*  914 */           this.pos += 1;
/*  915 */           skipToEndOfLine();
/*  916 */           p = this.pos;
/*  917 */           l = this.limit;
/*  918 */           break;
/*      */         default:
/*  921 */           return c;
/*      */         }
/*      */         break;
/*      */       case 35:
/*  925 */         this.pos = p;
/*      */ 
/*  931 */         checkLenient();
/*  932 */         skipToEndOfLine();
/*  933 */         p = this.pos;
/*  934 */         l = this.limit;
/*      */       }
/*      */     }
/*      */ 
/*  938 */     this.pos = p;
/*  939 */     return c;
/*      */ 
/*  942 */     if (throwOnEof) {
/*  943 */       throw new EOFException("End of input at line " + getLineNumber() + " column " + getColumnNumber());
/*      */     }
/*      */ 
/*  946 */     return -1;
/*      */   }
/*      */ 
/*      */   private void checkLenient() throws IOException
/*      */   {
/*  951 */     if (!this.lenient)
/*  952 */       throw syntaxError("Use JsonReader.setLenient(true) to accept malformed JSON");
/*      */   }
/*      */ 
/*      */   private void skipToEndOfLine()
/*      */     throws IOException
/*      */   {
/*  962 */     while ((this.pos < this.limit) || (fillBuffer(1))) {
/*  963 */       char c = this.buffer[(this.pos++)];
/*  964 */       if ((c == '\r') || (c == '\n'))
/*      */         break;
/*      */     }
/*      */   }
/*      */ 
/*      */   private boolean skipTo(String toFind)
/*      */     throws IOException
/*      */   {
/*  972 */     label67: for (; (this.pos + toFind.length() <= this.limit) || (fillBuffer(toFind.length())); this.pos += 1) {
/*  973 */       for (int c = 0; c < toFind.length(); c++) {
/*  974 */         if (this.buffer[(this.pos + c)] != toFind.charAt(c)) {
/*      */           break label67;
/*      */         }
/*      */       }
/*  978 */       return true;
/*      */     }
/*  980 */     return false;
/*      */   }
/*      */ 
/*      */   private String nextString(char quote)
/*      */     throws IOException
/*      */   {
/*  995 */     char[] buffer = this.buffer;
/*  996 */     StringBuilder builder = null;
/*      */     while (true) {
/*  998 */       int p = this.pos;
/*  999 */       int l = this.limit;
/*      */ 
/* 1001 */       int start = p;
/* 1002 */       while (p < l) {
/* 1003 */         int c = buffer[(p++)];
/*      */ 
/* 1005 */         if (c == quote) {
/* 1006 */           this.pos = p;
/* 1007 */           if (this.skipping)
/* 1008 */             return "skipped!";
/* 1009 */           if (builder == null) {
/* 1010 */             return this.stringPool.get(buffer, start, p - start - 1);
/*      */           }
/* 1012 */           builder.append(buffer, start, p - start - 1);
/* 1013 */           return builder.toString();
/*      */         }
/*      */ 
/* 1016 */         if (c == 92) {
/* 1017 */           this.pos = p;
/* 1018 */           if (builder == null) {
/* 1019 */             builder = new StringBuilder();
/*      */           }
/* 1021 */           builder.append(buffer, start, p - start - 1);
/* 1022 */           builder.append(readEscapeCharacter());
/* 1023 */           p = this.pos;
/* 1024 */           l = this.limit;
/* 1025 */           start = p;
/*      */         }
/*      */       }
/*      */ 
/* 1029 */       if (builder == null) {
/* 1030 */         builder = new StringBuilder();
/*      */       }
/* 1032 */       builder.append(buffer, start, p - start);
/* 1033 */       this.pos = p;
/* 1034 */       if (!fillBuffer(1))
/* 1035 */         throw syntaxError("Unterminated string");
/*      */     }
/*      */   }
/*      */ 
/*      */   private String nextLiteral(boolean assignOffsetsOnly)
/*      */     throws IOException
/*      */   {
/* 1050 */     StringBuilder builder = null;
/* 1051 */     this.valuePos = -1;
/* 1052 */     this.valueLength = 0;
/* 1053 */     int i = 0;
/*      */     while (true)
/*      */     {
/* 1057 */       if (this.pos + i < this.limit) {
/* 1058 */         switch (this.buffer[(this.pos + i)]) {
/*      */         case '#':
/*      */         case '/':
/*      */         case ';':
/*      */         case '=':
/*      */         case '\\':
/* 1064 */           checkLenient();
/*      */         case '\t':
/*      */         case '\n':
/*      */         case '\f':
/*      */         case '\r':
/*      */         case ' ':
/*      */         case ',':
/*      */         case ':':
/*      */         case '[':
/*      */         case ']':
/*      */         case '{':
/*      */         case '}':
/* 1076 */           break;
/*      */         default:
/* 1057 */           i++; break;
/*      */         }
/*      */ 
/*      */       }
/* 1085 */       else if (i < this.buffer.length) {
/* 1086 */         if (!fillBuffer(i + 1))
/*      */         {
/* 1089 */           this.buffer[this.limit] = '\000';
/*      */         }
/*      */ 
/*      */       }
/*      */       else
/*      */       {
/* 1095 */         if (builder == null) {
/* 1096 */           builder = new StringBuilder();
/*      */         }
/* 1098 */         builder.append(this.buffer, this.pos, i);
/* 1099 */         this.valueLength += i;
/* 1100 */         this.pos += i;
/* 1101 */         i = 0;
/* 1102 */         if (!fillBuffer(1))
/* 1103 */           break;
/*      */       }
/*      */     }
/*      */     String result;
/*      */     String result;
/* 1108 */     if ((assignOffsetsOnly) && (builder == null)) {
/* 1109 */       this.valuePos = this.pos;
/* 1110 */       result = null;
/*      */     }
/*      */     else
/*      */     {
/*      */       String result;
/* 1111 */       if (this.skipping) {
/* 1112 */         result = "skipped!";
/*      */       }
/*      */       else
/*      */       {
/*      */         String result;
/* 1113 */         if (builder == null) {
/* 1114 */           result = this.stringPool.get(this.buffer, this.pos, i);
/*      */         } else {
/* 1116 */           builder.append(this.buffer, this.pos, i);
/* 1117 */           result = builder.toString();
/*      */         }
/*      */       }
/*      */     }
/* 1119 */     this.valueLength += i;
/* 1120 */     this.pos += i;
/* 1121 */     return result;
/*      */   }
/*      */ 
/*      */   public String toString() {
/* 1125 */     return getClass().getSimpleName() + " near " + getSnippet();
/*      */   }
/*      */ 
/*      */   private char readEscapeCharacter()
/*      */     throws IOException
/*      */   {
/* 1138 */     if ((this.pos == this.limit) && (!fillBuffer(1))) {
/* 1139 */       throw syntaxError("Unterminated escape sequence");
/*      */     }
/*      */ 
/* 1142 */     char escaped = this.buffer[(this.pos++)];
/* 1143 */     switch (escaped) {
/*      */     case 'u':
/* 1145 */       if ((this.pos + 4 > this.limit) && (!fillBuffer(4))) {
/* 1146 */         throw syntaxError("Unterminated escape sequence");
/*      */       }
/*      */ 
/* 1149 */       char result = '\000';
/* 1150 */       int i = this.pos; for (int end = i + 4; i < end; i++) {
/* 1151 */         char c = this.buffer[i];
/* 1152 */         result = (char)(result << '\004');
/* 1153 */         if ((c >= '0') && (c <= '9'))
/* 1154 */           result = (char)(result + (c - '0'));
/* 1155 */         else if ((c >= 'a') && (c <= 'f'))
/* 1156 */           result = (char)(result + (c - 'a' + 10));
/* 1157 */         else if ((c >= 'A') && (c <= 'F'))
/* 1158 */           result = (char)(result + (c - 'A' + 10));
/*      */         else {
/* 1160 */           throw new NumberFormatException("\\u" + this.stringPool.get(this.buffer, this.pos, 4));
/*      */         }
/*      */       }
/* 1163 */       this.pos += 4;
/* 1164 */       return result;
/*      */     case 't':
/* 1167 */       return '\t';
/*      */     case 'b':
/* 1170 */       return '\b';
/*      */     case 'n':
/* 1173 */       return '\n';
/*      */     case 'r':
/* 1176 */       return '\r';
/*      */     case 'f':
/* 1179 */       return '\f';
/*      */     case '"':
/*      */     case '\'':
/*      */     case '\\':
/*      */     }
/*      */ 
/* 1185 */     return escaped;
/*      */   }
/*      */ 
/*      */   private JsonToken readLiteral()
/*      */     throws IOException
/*      */   {
/* 1193 */     this.value = nextLiteral(true);
/* 1194 */     if (this.valueLength == 0) {
/* 1195 */       throw syntaxError("Expected literal value");
/*      */     }
/* 1197 */     this.token = decodeLiteral();
/* 1198 */     if (this.token == JsonToken.STRING) {
/* 1199 */       checkLenient();
/*      */     }
/* 1201 */     return this.token;
/*      */   }
/*      */ 
/*      */   private JsonToken decodeLiteral()
/*      */     throws IOException
/*      */   {
/* 1208 */     if (this.valuePos == -1)
/*      */     {
/* 1210 */       return JsonToken.STRING;
/* 1211 */     }if ((this.valueLength == 4) && (('n' == this.buffer[this.valuePos]) || ('N' == this.buffer[this.valuePos])) && (('u' == this.buffer[(this.valuePos + 1)]) || ('U' == this.buffer[(this.valuePos + 1)])) && (('l' == this.buffer[(this.valuePos + 2)]) || ('L' == this.buffer[(this.valuePos + 2)])) && (('l' == this.buffer[(this.valuePos + 3)]) || ('L' == this.buffer[(this.valuePos + 3)])))
/*      */     {
/* 1216 */       this.value = "null";
/* 1217 */       return JsonToken.NULL;
/* 1218 */     }if ((this.valueLength == 4) && (('t' == this.buffer[this.valuePos]) || ('T' == this.buffer[this.valuePos])) && (('r' == this.buffer[(this.valuePos + 1)]) || ('R' == this.buffer[(this.valuePos + 1)])) && (('u' == this.buffer[(this.valuePos + 2)]) || ('U' == this.buffer[(this.valuePos + 2)])) && (('e' == this.buffer[(this.valuePos + 3)]) || ('E' == this.buffer[(this.valuePos + 3)])))
/*      */     {
/* 1223 */       this.value = "true";
/* 1224 */       return JsonToken.BOOLEAN;
/* 1225 */     }if ((this.valueLength == 5) && (('f' == this.buffer[this.valuePos]) || ('F' == this.buffer[this.valuePos])) && (('a' == this.buffer[(this.valuePos + 1)]) || ('A' == this.buffer[(this.valuePos + 1)])) && (('l' == this.buffer[(this.valuePos + 2)]) || ('L' == this.buffer[(this.valuePos + 2)])) && (('s' == this.buffer[(this.valuePos + 3)]) || ('S' == this.buffer[(this.valuePos + 3)])) && (('e' == this.buffer[(this.valuePos + 4)]) || ('E' == this.buffer[(this.valuePos + 4)])))
/*      */     {
/* 1231 */       this.value = "false";
/* 1232 */       return JsonToken.BOOLEAN;
/*      */     }
/* 1234 */     this.value = this.stringPool.get(this.buffer, this.valuePos, this.valueLength);
/* 1235 */     return decodeNumber(this.buffer, this.valuePos, this.valueLength);
/*      */   }
/*      */ 
/*      */   private JsonToken decodeNumber(char[] chars, int offset, int length)
/*      */   {
/* 1246 */     int i = offset;
/* 1247 */     int c = chars[i];
/*      */ 
/* 1249 */     if (c == 45) {
/* 1250 */       c = chars[(++i)];
/*      */     }
/*      */ 
/* 1253 */     if (c == 48) {
/* 1254 */       c = chars[(++i)]; } else {
/* 1255 */       if ((c >= 49) && (c <= 57))
/* 1256 */         c = chars[(++i)];
/* 1257 */       while ((c >= 48) && (c <= 57)) {
/* 1258 */         c = chars[(++i)]; continue;
/*      */ 
/* 1261 */         return JsonToken.STRING;
/*      */       }
/*      */     }
/* 1264 */     if (c == 46) {
/* 1265 */       c = chars[(++i)];
/* 1266 */       while ((c >= 48) && (c <= 57)) {
/* 1267 */         c = chars[(++i)];
/*      */       }
/*      */     }
/*      */ 
/* 1271 */     if ((c == 101) || (c == 69)) {
/* 1272 */       c = chars[(++i)];
/* 1273 */       if ((c == 43) || (c == 45)) {
/* 1274 */         c = chars[(++i)];
/*      */       }
/* 1276 */       if ((c >= 48) && (c <= 57))
/* 1277 */         c = chars[(++i)];
/* 1278 */       while ((c >= 48) && (c <= 57)) {
/* 1279 */         c = chars[(++i)]; continue;
/*      */ 
/* 1282 */         return JsonToken.STRING;
/*      */       }
/*      */     }
/*      */ 
/* 1286 */     if (i == offset + length) {
/* 1287 */       return JsonToken.NUMBER;
/*      */     }
/* 1289 */     return JsonToken.STRING;
/*      */   }
/*      */ 
/*      */   private IOException syntaxError(String message)
/*      */     throws IOException
/*      */   {
/* 1298 */     throw new MalformedJsonException(message + " at line " + getLineNumber() + " column " + getColumnNumber());
/*      */   }
/*      */ 
/*      */   private CharSequence getSnippet()
/*      */   {
/* 1303 */     StringBuilder snippet = new StringBuilder();
/* 1304 */     int beforePos = Math.min(this.pos, 20);
/* 1305 */     snippet.append(this.buffer, this.pos - beforePos, beforePos);
/* 1306 */     int afterPos = Math.min(this.limit - this.pos, 20);
/* 1307 */     snippet.append(this.buffer, this.pos, afterPos);
/* 1308 */     return snippet;
/*      */   }
/*      */ 
/*      */   static {
/* 1312 */     JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess() {
/*      */       public void promoteNameToValue(JsonReader reader) throws IOException {
/* 1314 */         if ((reader instanceof JsonTreeReader)) {
/* 1315 */           ((JsonTreeReader)reader).promoteNameToValue();
/* 1316 */           return;
/*      */         }
/* 1318 */         reader.peek();
/* 1319 */         if (reader.token != JsonToken.NAME) {
/* 1320 */           throw new IllegalStateException("Expected a name but was " + reader.peek() + " " + " at line " + reader.getLineNumber() + " column " + reader.getColumnNumber());
/*      */         }
/*      */ 
/* 1323 */         reader.value = reader.name;
/* 1324 */         reader.name = null;
/* 1325 */         reader.token = JsonToken.STRING;
/*      */       }
/*      */     };
/*      */   }
/*      */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.google.gson.stream.JsonReader
 * JD-Core Version:    0.6.2
 */