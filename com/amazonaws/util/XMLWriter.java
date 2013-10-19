/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import java.io.IOException;
/*     */ import java.io.Writer;
/*     */ import java.util.Date;
/*     */ import java.util.Stack;
/*     */ 
/*     */ public class XMLWriter
/*     */ {
/*     */   private static final String PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
/*     */   private final Writer writer;
/*     */   private final String xmlns;
/*  38 */   private Stack<String> elementStack = new Stack();
/*  39 */   private boolean rootElement = true;
/*     */ 
/*     */   public XMLWriter(Writer w)
/*     */   {
/*  50 */     this(w, null);
/*     */   }
/*     */ 
/*     */   public XMLWriter(Writer w, String xmlns)
/*     */   {
/*  65 */     this.writer = w;
/*  66 */     this.xmlns = xmlns;
/*  67 */     append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
/*     */   }
/*     */ 
/*     */   public XMLWriter startElement(String element)
/*     */   {
/*  81 */     append("<" + element);
/*  82 */     if ((this.rootElement) && (this.xmlns != null)) {
/*  83 */       append(" xmlns=\"" + this.xmlns + "\"");
/*  84 */       this.rootElement = false;
/*     */     }
/*  86 */     append(">");
/*  87 */     this.elementStack.push(element);
/*  88 */     return this;
/*     */   }
/*     */ 
/*     */   public XMLWriter endElement()
/*     */   {
/*  99 */     String lastElement = (String)this.elementStack.pop();
/* 100 */     append("</" + lastElement + ">");
/* 101 */     return this;
/*     */   }
/*     */ 
/*     */   public XMLWriter value(String s)
/*     */   {
/* 115 */     append(s);
/* 116 */     return this;
/*     */   }
/*     */ 
/*     */   public XMLWriter value(Date date)
/*     */   {
/* 130 */     append(StringUtils.fromDate(date));
/* 131 */     return this;
/*     */   }
/*     */ 
/*     */   public XMLWriter value(Object obj)
/*     */   {
/* 146 */     append(obj.toString());
/* 147 */     return this;
/*     */   }
/*     */ 
/*     */   private void append(String s) {
/*     */     try {
/* 152 */       this.writer.append(s);
/*     */     } catch (IOException e) {
/* 154 */       throw new AmazonClientException("Unable to write XML document", e);
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.XMLWriter
 * JD-Core Version:    0.6.2
 */