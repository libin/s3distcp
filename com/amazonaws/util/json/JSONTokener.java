/*     */ package com.amazonaws.util.json;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ 
/*     */ public class JSONTokener
/*     */ {
/*     */   private int character;
/*     */   private boolean eof;
/*     */   private int index;
/*     */   private int line;
/*     */   private char previous;
/*     */   private Reader reader;
/*     */   private boolean usePrevious;
/*     */ 
/*     */   public JSONTokener(Reader reader)
/*     */   {
/*  55 */     this.reader = (reader.markSupported() ? reader : new BufferedReader(reader));
/*     */ 
/*  57 */     this.eof = false;
/*  58 */     this.usePrevious = false;
/*  59 */     this.previous = '\000';
/*  60 */     this.index = 0;
/*  61 */     this.character = 1;
/*  62 */     this.line = 1;
/*     */   }
/*     */ 
/*     */   public JSONTokener(String s)
/*     */   {
/*  72 */     this(new StringReader(s));
/*     */   }
/*     */ 
/*     */   public void back()
/*     */     throws JSONException
/*     */   {
/*  82 */     if ((this.usePrevious) || (this.index <= 0)) {
/*  83 */       throw new JSONException("Stepping back two steps is not supported");
/*     */     }
/*  85 */     this.index -= 1;
/*  86 */     this.character -= 1;
/*  87 */     this.usePrevious = true;
/*  88 */     this.eof = false;
/*     */   }
/*     */ 
/*     */   public static int dehexchar(char c)
/*     */   {
/*  99 */     if ((c >= '0') && (c <= '9')) {
/* 100 */       return c - '0';
/*     */     }
/* 102 */     if ((c >= 'A') && (c <= 'F')) {
/* 103 */       return c - '7';
/*     */     }
/* 105 */     if ((c >= 'a') && (c <= 'f')) {
/* 106 */       return c - 'W';
/*     */     }
/* 108 */     return -1;
/*     */   }
/*     */ 
/*     */   public boolean end() {
/* 112 */     return (this.eof) && (!this.usePrevious);
/*     */   }
/*     */ 
/*     */   public boolean more()
/*     */     throws JSONException
/*     */   {
/* 122 */     next();
/* 123 */     if (end()) {
/* 124 */       return false;
/*     */     }
/* 126 */     back();
/* 127 */     return true;
/*     */   }
/*     */ 
/*     */   public char next()
/*     */     throws JSONException
/*     */   {
/*     */     int c;
/*     */     int c;
/* 138 */     if (this.usePrevious) {
/* 139 */       this.usePrevious = false;
/* 140 */       c = this.previous;
/*     */     } else {
/*     */       try {
/* 143 */         c = this.reader.read();
/*     */       } catch (IOException exception) {
/* 145 */         throw new JSONException(exception);
/*     */       }
/*     */ 
/* 148 */       if (c <= 0) {
/* 149 */         this.eof = true;
/* 150 */         c = 0;
/*     */       }
/*     */     }
/* 153 */     this.index += 1;
/* 154 */     if (this.previous == '\r') {
/* 155 */       this.line += 1;
/* 156 */       this.character = (c == 10 ? 0 : 1);
/* 157 */     } else if (c == 10) {
/* 158 */       this.line += 1;
/* 159 */       this.character = 0;
/*     */     } else {
/* 161 */       this.character += 1;
/*     */     }
/* 163 */     this.previous = ((char)c);
/* 164 */     return this.previous;
/*     */   }
/*     */ 
/*     */   public char next(char c)
/*     */     throws JSONException
/*     */   {
/* 176 */     char n = next();
/* 177 */     if (n != c) {
/* 178 */       throw syntaxError("Expected '" + c + "' and instead saw '" + n + "'");
/*     */     }
/*     */ 
/* 181 */     return n;
/*     */   }
/*     */ 
/*     */   public String next(int n)
/*     */     throws JSONException
/*     */   {
/* 195 */     if (n == 0) {
/* 196 */       return "";
/*     */     }
/*     */ 
/* 199 */     char[] buffer = new char[n];
/* 200 */     int pos = 0;
/*     */ 
/* 202 */     while (pos < n) {
/* 203 */       buffer[pos] = next();
/* 204 */       if (end()) {
/* 205 */         throw syntaxError("Substring bounds error");
/*     */       }
/* 207 */       pos++;
/*     */     }
/* 209 */     return new String(buffer);
/*     */   }
/*     */ 
/*     */   public char nextClean()
/*     */     throws JSONException
/*     */   {
/*     */     while (true)
/*     */     {
/* 220 */       char c = next();
/* 221 */       if ((c == 0) || (c > ' '))
/* 222 */         return c;
/*     */     }
/*     */   }
/*     */ 
/*     */   public String nextString(char quote)
/*     */     throws JSONException
/*     */   {
/* 241 */     StringBuffer sb = new StringBuffer();
/*     */     while (true) {
/* 243 */       char c = next();
/* 244 */       switch (c) {
/*     */       case '\000':
/*     */       case '\n':
/*     */       case '\r':
/* 248 */         throw syntaxError("Unterminated string");
/*     */       case '\\':
/* 250 */         c = next();
/* 251 */         switch (c) {
/*     */         case 'b':
/* 253 */           sb.append('\b');
/* 254 */           break;
/*     */         case 't':
/* 256 */           sb.append('\t');
/* 257 */           break;
/*     */         case 'n':
/* 259 */           sb.append('\n');
/* 260 */           break;
/*     */         case 'f':
/* 262 */           sb.append('\f');
/* 263 */           break;
/*     */         case 'r':
/* 265 */           sb.append('\r');
/* 266 */           break;
/*     */         case 'u':
/* 268 */           sb.append((char)Integer.parseInt(next(4), 16));
/* 269 */           break;
/*     */         case '"':
/*     */         case '\'':
/*     */         case '/':
/*     */         case '\\':
/* 274 */           sb.append(c);
/* 275 */           break;
/*     */         default:
/* 277 */           throw syntaxError("Illegal escape.");
/*     */         }
/*     */         break;
/*     */       default:
/* 281 */         if (c == quote) {
/* 282 */           return sb.toString();
/*     */         }
/* 284 */         sb.append(c);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public String nextTo(char d)
/*     */     throws JSONException
/*     */   {
/* 297 */     StringBuffer sb = new StringBuffer();
/*     */     while (true) {
/* 299 */       char c = next();
/* 300 */       if ((c == d) || (c == 0) || (c == '\n') || (c == '\r')) {
/* 301 */         if (c != 0) {
/* 302 */           back();
/*     */         }
/* 304 */         return sb.toString().trim();
/*     */       }
/* 306 */       sb.append(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String nextTo(String delimiters)
/*     */     throws JSONException
/*     */   {
/* 319 */     StringBuffer sb = new StringBuffer();
/*     */     while (true) {
/* 321 */       char c = next();
/* 322 */       if ((delimiters.indexOf(c) >= 0) || (c == 0) || (c == '\n') || (c == '\r'))
/*     */       {
/* 324 */         if (c != 0) {
/* 325 */           back();
/*     */         }
/* 327 */         return sb.toString().trim();
/*     */       }
/* 329 */       sb.append(c);
/*     */     }
/*     */   }
/*     */ 
/*     */   public Object nextValue()
/*     */     throws JSONException
/*     */   {
/* 342 */     char c = nextClean();
/*     */ 
/* 345 */     switch (c) {
/*     */     case '"':
/*     */     case '\'':
/* 348 */       return nextString(c);
/*     */     case '{':
/* 350 */       back();
/* 351 */       return new JSONObject(this);
/*     */     case '(':
/*     */     case '[':
/* 354 */       back();
/* 355 */       return new JSONArray(this);
/*     */     }
/*     */ 
/* 367 */     StringBuffer sb = new StringBuffer();
/* 368 */     while ((c >= ' ') && (",:]}/\\\"[{;=#".indexOf(c) < 0)) {
/* 369 */       sb.append(c);
/* 370 */       c = next();
/*     */     }
/* 372 */     back();
/*     */ 
/* 374 */     String s = sb.toString().trim();
/* 375 */     if (s.equals("")) {
/* 376 */       throw syntaxError("Missing value");
/*     */     }
/* 378 */     return JSONObject.stringToValue(s);
/*     */   }
/*     */ 
/*     */   public char skipTo(char to)
/*     */     throws JSONException
/*     */   {
/*     */     char c;
/*     */     try
/*     */     {
/* 392 */       int startIndex = this.index;
/* 393 */       int startCharacter = this.character;
/* 394 */       int startLine = this.line;
/* 395 */       this.reader.mark(2147483647);
/*     */       do {
/* 397 */         c = next();
/* 398 */         if (c == 0) {
/* 399 */           this.reader.reset();
/* 400 */           this.index = startIndex;
/* 401 */           this.character = startCharacter;
/* 402 */           this.line = startLine;
/* 403 */           return c;
/*     */         }
/*     */       }
/* 405 */       while (c != to);
/*     */     } catch (IOException exc) {
/* 407 */       throw new JSONException(exc);
/*     */     }
/*     */ 
/* 410 */     back();
/* 411 */     return c;
/*     */   }
/*     */ 
/*     */   public JSONException syntaxError(String message)
/*     */   {
/* 422 */     return new JSONException(message + toString());
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 432 */     return " at " + this.index + " [character " + this.character + " line " + this.line + "]";
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.json.JSONTokener
 * JD-Core Version:    0.6.2
 */