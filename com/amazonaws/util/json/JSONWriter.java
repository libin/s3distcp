/*     */ package com.amazonaws.util.json;
/*     */ 
/*     */ import com.amazonaws.util.BinaryUtils;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class JSONWriter
/*     */ {
/*     */   private static final int maxdepth = 20;
/*     */   private boolean comma;
/*     */   protected char mode;
/*     */   private JSONObject[] stack;
/*     */   private int top;
/*     */   protected Writer writer;
/*     */ 
/*     */   public JSONWriter(Writer w)
/*     */   {
/* 100 */     this.comma = false;
/* 101 */     this.mode = 'i';
/* 102 */     this.stack = new JSONObject[20];
/* 103 */     this.top = 0;
/* 104 */     this.writer = w;
/*     */   }
/*     */ 
/*     */   private JSONWriter append(String s)
/*     */     throws JSONException
/*     */   {
/* 114 */     if (s == null) {
/* 115 */       throw new JSONException("Null pointer");
/*     */     }
/* 117 */     if ((this.mode == 'o') || (this.mode == 'a')) {
/*     */       try {
/* 119 */         if ((this.comma) && (this.mode == 'a')) {
/* 120 */           this.writer.write(44);
/*     */         }
/* 122 */         this.writer.write(s);
/*     */       } catch (IOException e) {
/* 124 */         throw new JSONException(e);
/*     */       }
/* 126 */       if (this.mode == 'o') {
/* 127 */         this.mode = 'k';
/*     */       }
/* 129 */       this.comma = true;
/* 130 */       return this;
/*     */     }
/* 132 */     throw new JSONException("Value out of sequence.");
/*     */   }
/*     */ 
/*     */   public JSONWriter array()
/*     */     throws JSONException
/*     */   {
/* 145 */     if ((this.mode == 'i') || (this.mode == 'o') || (this.mode == 'a')) {
/* 146 */       push(null);
/* 147 */       append("[");
/* 148 */       this.comma = false;
/* 149 */       return this;
/*     */     }
/* 151 */     throw new JSONException("Misplaced array.");
/*     */   }
/*     */ 
/*     */   private JSONWriter end(char m, char c)
/*     */     throws JSONException
/*     */   {
/* 162 */     if (this.mode != m) {
/* 163 */       throw new JSONException(m == 'a' ? "Misplaced endArray." : "Misplaced endObject.");
/*     */     }
/*     */ 
/* 166 */     pop(m);
/*     */     try {
/* 168 */       this.writer.write(c);
/*     */     } catch (IOException e) {
/* 170 */       throw new JSONException(e);
/*     */     }
/* 172 */     this.comma = true;
/* 173 */     return this;
/*     */   }
/*     */ 
/*     */   public JSONWriter endArray()
/*     */     throws JSONException
/*     */   {
/* 183 */     return end('a', ']');
/*     */   }
/*     */ 
/*     */   public JSONWriter endObject()
/*     */     throws JSONException
/*     */   {
/* 193 */     return end('k', '}');
/*     */   }
/*     */ 
/*     */   public JSONWriter key(String s)
/*     */     throws JSONException
/*     */   {
/* 205 */     if (s == null) {
/* 206 */       throw new JSONException("Null key.");
/*     */     }
/* 208 */     if (this.mode == 'k') {
/*     */       try {
/* 210 */         this.stack[(this.top - 1)].putOnce(s, Boolean.TRUE);
/* 211 */         if (this.comma) {
/* 212 */           this.writer.write(44);
/*     */         }
/* 214 */         this.writer.write(JSONObject.quote(s));
/* 215 */         this.writer.write(58);
/* 216 */         this.comma = false;
/* 217 */         this.mode = 'o';
/* 218 */         return this;
/*     */       } catch (IOException e) {
/* 220 */         throw new JSONException(e);
/*     */       }
/*     */     }
/* 223 */     throw new JSONException("Misplaced key.");
/*     */   }
/*     */ 
/*     */   public JSONWriter object()
/*     */     throws JSONException
/*     */   {
/* 237 */     if (this.mode == 'i') {
/* 238 */       this.mode = 'o';
/*     */     }
/* 240 */     if ((this.mode == 'o') || (this.mode == 'a')) {
/* 241 */       append("{");
/* 242 */       push(new JSONObject());
/* 243 */       this.comma = false;
/* 244 */       return this;
/*     */     }
/* 246 */     throw new JSONException("Misplaced object.");
/*     */   }
/*     */ 
/*     */   private void pop(char c)
/*     */     throws JSONException
/*     */   {
/* 257 */     if (this.top <= 0) {
/* 258 */       throw new JSONException("Nesting error.");
/*     */     }
/* 260 */     char m = this.stack[(this.top - 1)] == null ? 'a' : 'k';
/* 261 */     if (m != c) {
/* 262 */       throw new JSONException("Nesting error.");
/*     */     }
/* 264 */     this.top -= 1;
/* 265 */     this.mode = (this.stack[(this.top - 1)] == null ? 'a' : this.top == 0 ? 'd' : 'k');
/*     */   }
/*     */ 
/*     */   private void push(JSONObject jo)
/*     */     throws JSONException
/*     */   {
/* 274 */     if (this.top >= 20) {
/* 275 */       throw new JSONException("Nesting too deep.");
/*     */     }
/* 277 */     this.stack[this.top] = jo;
/* 278 */     this.mode = (jo == null ? 'a' : 'k');
/* 279 */     this.top += 1;
/*     */   }
/*     */ 
/*     */   public JSONWriter value(boolean b)
/*     */     throws JSONException
/*     */   {
/* 291 */     return append(b ? "true" : "false");
/*     */   }
/*     */ 
/*     */   public JSONWriter value(double d)
/*     */     throws JSONException
/*     */   {
/* 301 */     return value(new Double(d));
/*     */   }
/*     */ 
/*     */   public JSONWriter value(long l)
/*     */     throws JSONException
/*     */   {
/* 311 */     return value(new Long(l));
/*     */   }
/*     */ 
/*     */   public JSONWriter value(Date date)
/*     */     throws JSONException
/*     */   {
/* 322 */     return value(new Long(date.getTime() / 1000L));
/*     */   }
/*     */ 
/*     */   public JSONWriter value(ByteBuffer b)
/*     */     throws JSONException
/*     */   {
/* 333 */     b.mark();
/* 334 */     byte[] bytes = new byte[b.remaining()];
/* 335 */     b.get(bytes, 0, bytes.length);
/* 336 */     b.reset();
/* 337 */     return value(BinaryUtils.toBase64(bytes));
/*     */   }
/*     */ 
/*     */   public JSONWriter value(Object o)
/*     */     throws JSONException
/*     */   {
/* 349 */     return append(JSONObject.valueToString(o));
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.json.JSONWriter
 * JD-Core Version:    0.6.2
 */