/*     */ package com.amazonaws.transform;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Stack;
/*     */ import javax.xml.namespace.QName;
/*     */ import javax.xml.stream.XMLEventReader;
/*     */ import javax.xml.stream.XMLStreamException;
/*     */ import javax.xml.stream.events.Attribute;
/*     */ import javax.xml.stream.events.Characters;
/*     */ import javax.xml.stream.events.StartElement;
/*     */ import javax.xml.stream.events.XMLEvent;
/*     */ 
/*     */ public class StaxUnmarshallerContext
/*     */ {
/*     */   private XMLEvent currentEvent;
/*     */   private final XMLEventReader eventReader;
/*  43 */   public final Stack<String> stack = new Stack();
/*  44 */   private String stackString = "";
/*     */ 
/*  46 */   private Map<String, String> metadata = new HashMap();
/*  47 */   private List<MetadataExpression> metadataExpressions = new ArrayList();
/*     */   private Iterator<?> attributeIterator;
/*     */   private final Map<String, String> headers;
/*     */ 
/*     */   public StaxUnmarshallerContext(XMLEventReader eventReader)
/*     */   {
/*  59 */     this(eventReader, null);
/*     */   }
/*     */ 
/*     */   public StaxUnmarshallerContext(XMLEventReader eventReader, Map<String, String> headers)
/*     */   {
/*  73 */     this.eventReader = eventReader;
/*  74 */     this.headers = headers;
/*     */   }
/*     */ 
/*     */   public String getHeader(String header)
/*     */   {
/*  88 */     if (this.headers == null) return null;
/*     */ 
/*  90 */     return (String)this.headers.get(header);
/*     */   }
/*     */ 
/*     */   public String readText()
/*     */     throws XMLStreamException
/*     */   {
/* 100 */     if (this.currentEvent.isAttribute()) {
/* 101 */       Attribute attribute = (Attribute)this.currentEvent;
/* 102 */       return attribute.getValue();
/*     */     }
/*     */ 
/* 105 */     StringBuilder sb = new StringBuilder();
/*     */     while (true) {
/* 107 */       XMLEvent event = this.eventReader.peek();
/* 108 */       if (event.getEventType() == 4) {
/* 109 */         this.eventReader.nextEvent();
/* 110 */         sb.append(event.asCharacters().getData()); } else {
/* 111 */         if (event.getEventType() == 2) {
/* 112 */           return sb.toString();
/*     */         }
/* 114 */         throw new RuntimeException(new StringBuilder().append("Encountered unexpected event: ").append(event.toString()).toString());
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public int getCurrentDepth()
/*     */   {
/* 127 */     return this.stack.size();
/*     */   }
/*     */ 
/*     */   public boolean testExpression(String expression)
/*     */   {
/* 140 */     if (expression.equals(".")) return true;
/* 141 */     return this.stackString.endsWith(expression);
/*     */   }
/*     */ 
/*     */   public boolean testExpression(String expression, int startingStackDepth)
/*     */   {
/* 159 */     if (expression.equals(".")) return true;
/*     */ 
/* 161 */     int index = -1;
/* 162 */     while ((index = expression.indexOf("/", index + 1)) > -1)
/*     */     {
/* 164 */       if (expression.charAt(index + 1) != '@') {
/* 165 */         startingStackDepth++;
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 170 */     return (startingStackDepth == getCurrentDepth()) && (this.stackString.endsWith(new StringBuilder().append("/").append(expression).toString()));
/*     */   }
/*     */ 
/*     */   public boolean isStartOfDocument()
/*     */     throws XMLStreamException
/*     */   {
/* 183 */     return this.eventReader.peek().isStartDocument();
/*     */   }
/*     */ 
/*     */   public XMLEvent nextEvent()
/*     */     throws XMLStreamException
/*     */   {
/* 194 */     if ((this.attributeIterator != null) && (this.attributeIterator.hasNext()))
/* 195 */       this.currentEvent = ((XMLEvent)this.attributeIterator.next());
/*     */     else {
/* 197 */       this.currentEvent = this.eventReader.nextEvent();
/*     */     }
/*     */ 
/* 200 */     if (this.currentEvent.isStartElement()) {
/* 201 */       this.attributeIterator = this.currentEvent.asStartElement().getAttributes();
/*     */     }
/*     */ 
/* 204 */     updateContext(this.currentEvent);
/*     */     XMLEvent nextEvent;
/* 206 */     if (this.eventReader.hasNext()) {
/* 207 */       nextEvent = this.eventReader.peek();
/* 208 */       if ((nextEvent != null) && (nextEvent.isCharacters())) {
/* 209 */         for (MetadataExpression metadataExpression : this.metadataExpressions) {
/* 210 */           if (testExpression(metadataExpression.expression, metadataExpression.targetDepth)) {
/* 211 */             this.metadata.put(metadataExpression.key, nextEvent.asCharacters().getData());
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 217 */     return this.currentEvent;
/*     */   }
/*     */ 
/*     */   public Map<String, String> getMetadata()
/*     */   {
/* 228 */     return this.metadata;
/*     */   }
/*     */ 
/*     */   public void registerMetadataExpression(String expression, int targetDepth, String storageKey)
/*     */   {
/* 246 */     this.metadataExpressions.add(new MetadataExpression(expression, targetDepth, storageKey));
/*     */   }
/*     */ 
/*     */   private void updateContext(XMLEvent event)
/*     */   {
/* 271 */     if (event == null) return;
/*     */ 
/* 273 */     if (event.isEndElement()) {
/* 274 */       this.stack.pop();
/* 275 */       this.stackString = "";
/* 276 */       for (String s : this.stack)
/* 277 */         this.stackString = new StringBuilder().append(this.stackString).append("/").append(s).toString();
/*     */     }
/* 279 */     else if (event.isStartElement()) {
/* 280 */       this.stack.push(event.asStartElement().getName().getLocalPart());
/* 281 */       this.stackString = new StringBuilder().append(this.stackString).append("/").append(event.asStartElement().getName().getLocalPart()).toString();
/* 282 */     } else if (event.isAttribute()) {
/* 283 */       Attribute attribute = (Attribute)event;
/* 284 */       this.stackString = "";
/* 285 */       for (String s : this.stack) {
/* 286 */         this.stackString = new StringBuilder().append(this.stackString).append("/").append(s).toString();
/*     */       }
/* 288 */       this.stackString = new StringBuilder().append(this.stackString).append("/@").append(attribute.getName().getLocalPart()).toString();
/*     */     }
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
/* 264 */       this.expression = expression;
/* 265 */       this.targetDepth = targetDepth;
/* 266 */       this.key = key;
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.transform.StaxUnmarshallerContext
 * JD-Core Version:    0.6.2
 */