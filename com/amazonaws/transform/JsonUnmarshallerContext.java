/*     */ package com.amazonaws.transform;
/*     */ 
/*     */ import com.amazonaws.http.HttpResponse;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import org.codehaus.jackson.JsonParser;
/*     */ import org.codehaus.jackson.JsonToken;
/*     */ 
/*     */ public class JsonUnmarshallerContext
/*     */ {
/*     */   private final JsonParser jsonParser;
/*  39 */   private final Stack<String> stack = new Stack();
/*  40 */   private String stackString = "";
/*     */   private String currentField;
/*  44 */   private Map<String, String> metadata = new HashMap();
/*  45 */   private List<MetadataExpression> metadataExpressions = new ArrayList();
/*     */   public JsonToken currentToken;
/*     */   private JsonToken nextToken;
/*     */   private final HttpResponse httpResponse;
/*     */ 
/*     */   public JsonUnmarshallerContext(JsonParser jsonParser)
/*     */   {
/*  53 */     this(jsonParser, null);
/*     */   }
/*     */ 
/*     */   public JsonUnmarshallerContext(JsonParser jsonParser, HttpResponse httpResponse) {
/*  57 */     this.jsonParser = jsonParser;
/*  58 */     this.httpResponse = httpResponse;
/*     */   }
/*     */ 
/*     */   public String getHeader(String header)
/*     */   {
/*  73 */     if (this.httpResponse == null) return null;
/*     */ 
/*  75 */     return (String)this.httpResponse.getHeaders().get(header);
/*     */   }
/*     */ 
/*     */   public HttpResponse getHttpResponse() {
/*  79 */     return this.httpResponse;
/*     */   }
/*     */ 
/*     */   public int getCurrentDepth()
/*     */   {
/*  90 */     int depth = 0;
/*  91 */     for (String s : this.stack) {
/*  92 */       if ((!s.equals(JsonToken.START_OBJECT.asString())) && (!s.equals(JsonToken.START_ARRAY.toString()))) {
/*  93 */         depth++;
/*     */       }
/*     */     }
/*  96 */     if (this.currentField != null) depth++;
/*  97 */     return depth;
/*     */   }
/*     */ 
/*     */   public String readText()
/*     */     throws IOException
/*     */   {
/* 109 */     switch (1.$SwitchMap$org$codehaus$jackson$JsonToken[this.currentToken.ordinal()]) {
/*     */     case 1:
/* 111 */       String text = this.jsonParser.getText();
/* 112 */       return text;
/*     */     case 2:
/* 113 */       return "false";
/*     */     case 3:
/* 114 */       return "true";
/*     */     case 4:
/* 115 */       return null;
/*     */     case 5:
/*     */     case 6:
/* 118 */       return this.jsonParser.getNumberValue().toString();
/*     */     case 7:
/* 120 */       return this.jsonParser.getText();
/*     */     }
/* 122 */     throw new RuntimeException("We expected a VALUE token but got: " + this.currentToken);
/*     */   }
/*     */ 
/*     */   public boolean isStartOfDocument()
/*     */   {
/* 128 */     return (this.jsonParser == null) || (this.jsonParser.getCurrentToken() == null);
/*     */   }
/*     */ 
/*     */   public boolean testExpression(String expression)
/*     */   {
/* 141 */     if (expression.equals("."))
/* 142 */       return true;
/* 143 */     return this.stackString.endsWith(expression);
/*     */   }
/*     */ 
/*     */   public boolean testExpression(String expression, int stackDepth)
/*     */   {
/* 161 */     if (expression.equals(".")) return true;
/*     */ 
/* 163 */     int index = -1;
/* 164 */     while ((index = expression.indexOf("/", index + 1)) > -1)
/*     */     {
/* 166 */       if (expression.charAt(index + 1) != '@') {
/* 167 */         stackDepth++;
/*     */       }
/*     */     }
/*     */ 
/* 171 */     return (this.stackString.endsWith("/" + expression)) && (stackDepth == getCurrentDepth());
/*     */   }
/*     */ 
/*     */   public JsonToken nextToken()
/*     */     throws IOException
/*     */   {
/* 178 */     JsonToken token = this.nextToken != null ? this.nextToken : this.jsonParser.nextToken();
/*     */ 
/* 181 */     this.currentToken = token;
/* 182 */     this.nextToken = null;
/*     */ 
/* 184 */     updateContext();
/* 185 */     return token;
/*     */   }
/*     */ 
/*     */   public JsonToken peek() throws IOException {
/* 189 */     if (this.nextToken != null) return this.nextToken;
/*     */ 
/* 191 */     this.nextToken = this.jsonParser.nextToken();
/* 192 */     return this.nextToken;
/*     */   }
/*     */ 
/*     */   public JsonParser getJsonParser() {
/* 196 */     return this.jsonParser;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getMetadata()
/*     */   {
/* 207 */     return this.metadata;
/*     */   }
/*     */ 
/*     */   public void registerMetadataExpression(String expression, int targetDepth, String storageKey)
/*     */   {
/* 226 */     this.metadataExpressions.add(new MetadataExpression(expression, targetDepth, storageKey));
/*     */   }
/*     */ 
/*     */   private void updateContext()
/*     */     throws IOException
/*     */   {
/* 251 */     if (this.currentToken == null) return;
/*     */ 
/* 253 */     if ((this.currentToken == JsonToken.START_OBJECT) || (this.currentToken == JsonToken.START_ARRAY)) {
/* 254 */       if (this.currentField != null) {
/* 255 */         this.stack.push(this.currentField);
/* 256 */         this.stack.push(this.currentToken.asString());
/* 257 */         this.currentField = null;
/*     */       }
/* 259 */     } else if ((this.currentToken == JsonToken.END_OBJECT) || (this.currentToken == JsonToken.END_ARRAY)) {
/* 260 */       if (!this.stack.isEmpty()) {
/* 261 */         boolean squareBracketsMatch = (this.currentToken == JsonToken.END_ARRAY) && (((String)this.stack.peek()).equals(JsonToken.START_ARRAY.asString()));
/* 262 */         boolean curlyBracketsMatch = (this.currentToken == JsonToken.END_OBJECT) && (((String)this.stack.peek()).equals(JsonToken.START_OBJECT.asString()));
/* 263 */         if ((squareBracketsMatch) || (curlyBracketsMatch)) {
/* 264 */           this.stack.pop();
/* 265 */           this.stack.pop();
/*     */         }
/*     */       }
/* 268 */       this.currentField = null;
/* 269 */     } else if (this.currentToken == JsonToken.FIELD_NAME) {
/* 270 */       String t = this.jsonParser.getText();
/* 271 */       this.currentField = t;
/*     */     }
/*     */ 
/* 274 */     rebuildStackString();
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 279 */     return this.stackString;
/*     */   }
/*     */ 
/*     */   private void rebuildStackString() {
/* 283 */     this.stackString = "";
/*     */ 
/* 285 */     for (String s : this.stack) {
/* 286 */       if ((!s.equals(JsonToken.START_ARRAY.asString())) && (!s.equals(JsonToken.START_OBJECT.asString()))) {
/* 287 */         this.stackString = (this.stackString + "/" + s);
/*     */       }
/*     */     }
/*     */ 
/* 291 */     if (this.currentField != null) {
/* 292 */       this.stackString = (this.stackString + "/" + this.currentField);
/*     */     }
/*     */ 
/* 295 */     if (this.stackString == "") this.stackString = "/";
/*     */   }
/*     */ 
/*     */   private class MetadataExpression
/*     */   {
/*     */     public String expression;
/*     */     public int targetDepth;
/*     */     public String key;
/*     */ 
/*     */     public MetadataExpression(String expression, int targetDepth, String key)
/*     */     {
/* 244 */       this.expression = expression;
/* 245 */       this.targetDepth = targetDepth;
/* 246 */       this.key = key;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.JsonUnmarshallerContext
 * JD-Core Version:    0.6.2
 */