/*     */ package com.amazonaws.util;
/*     */ 
/*     */ import com.amazonaws.AmazonClientException;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.URL;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.text.ParseException;
/*     */ import java.util.Date;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.xpath.XPath;
/*     */ import javax.xml.xpath.XPathConstants;
/*     */ import javax.xml.xpath.XPathExpressionException;
/*     */ import javax.xml.xpath.XPathFactory;
/*     */ import org.apache.commons.codec.binary.Base64;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ import org.xml.sax.SAXException;
/*     */ 
/*     */ public class XpathUtils
/*     */ {
/*  50 */   private static XPathFactory xpathFactory = XPathFactory.newInstance();
/*     */ 
/*  52 */   private static XPath xpath = xpathFactory.newXPath();
/*     */ 
/*  55 */   private static DateUtils dateUtils = new DateUtils();
/*     */ 
/*  58 */   private static Log log = LogFactory.getLog(XpathUtils.class);
/*     */ 
/*  60 */   private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*     */ 
/*     */   public static Document documentFrom(InputStream is)
/*     */     throws SAXException, IOException, ParserConfigurationException
/*     */   {
/*  66 */     is = new NamespaceRemovingInputStream(is);
/*  67 */     Document doc = factory.newDocumentBuilder().parse(is);
/*  68 */     is.close();
/*  69 */     return doc;
/*     */   }
/*     */ 
/*     */   public static Document documentFrom(String xml) throws SAXException, IOException, ParserConfigurationException
/*     */   {
/*  74 */     return documentFrom(new ByteArrayInputStream(xml.getBytes()));
/*     */   }
/*     */ 
/*     */   public static Document documentFrom(URL url) throws SAXException, IOException, ParserConfigurationException
/*     */   {
/*  79 */     return documentFrom(url.openStream());
/*     */   }
/*     */ 
/*     */   public static Double asDouble(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/*  99 */     String doubleString = evaluateAsString(expression, node);
/* 100 */     return isEmptyString(doubleString) ? null : Double.valueOf(Double.parseDouble(doubleString));
/*     */   }
/*     */ 
/*     */   public static String asString(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 120 */     return evaluateAsString(expression, node);
/*     */   }
/*     */ 
/*     */   public static Integer asInteger(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 140 */     String intString = evaluateAsString(expression, node);
/* 141 */     return isEmptyString(intString) ? null : Integer.valueOf(Integer.parseInt(intString));
/*     */   }
/*     */ 
/*     */   public static Boolean asBoolean(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 161 */     String booleanString = evaluateAsString(expression, node);
/* 162 */     return isEmptyString(booleanString) ? null : Boolean.valueOf(Boolean.parseBoolean(booleanString));
/*     */   }
/*     */ 
/*     */   public static Float asFloat(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 182 */     String floatString = evaluateAsString(expression, node);
/* 183 */     return isEmptyString(floatString) ? null : Float.valueOf(floatString);
/*     */   }
/*     */ 
/*     */   public static Long asLong(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 203 */     String longString = evaluateAsString(expression, node);
/* 204 */     return isEmptyString(longString) ? null : Long.valueOf(Long.parseLong(longString));
/*     */   }
/*     */ 
/*     */   public static Byte asByte(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 224 */     String byteString = evaluateAsString(expression, node);
/* 225 */     return isEmptyString(byteString) ? null : Byte.valueOf(byteString);
/*     */   }
/*     */ 
/*     */   public static Date asDate(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 246 */     String dateString = evaluateAsString(expression, node);
/* 247 */     if (isEmptyString(dateString)) return null;
/*     */     try
/*     */     {
/* 250 */       return dateUtils.parseIso8601Date(dateString);
/*     */     } catch (ParseException e) {
/* 252 */       log.warn("Unable to parse date '" + dateString + "':  " + e.getMessage(), e);
/* 253 */     }return null;
/*     */   }
/*     */ 
/*     */   public static ByteBuffer asByteBuffer(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 274 */     String base64EncodedString = evaluateAsString(expression, node);
/* 275 */     if (isEmptyString(base64EncodedString)) return null;
/*     */ 
/* 277 */     if (!isEmpty(node)) {
/*     */       try {
/* 279 */         byte[] base64EncodedBytes = base64EncodedString.getBytes("UTF-8");
/* 280 */         byte[] decodedBytes = Base64.decodeBase64(base64EncodedBytes);
/* 281 */         return ByteBuffer.wrap(decodedBytes);
/*     */       } catch (UnsupportedEncodingException e) {
/* 283 */         throw new AmazonClientException("Unable to unmarshall XML data into a ByteBuffer", e);
/*     */       }
/*     */     }
/* 286 */     return null;
/*     */   }
/*     */ 
/*     */   public static boolean isEmpty(Node node)
/*     */   {
/* 298 */     return node == null;
/*     */   }
/*     */ 
/*     */   public static Node asNode(String nodeName, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 318 */     if (node == null) return null;
/* 319 */     return (Node)xpath.evaluate(nodeName, node, XPathConstants.NODE);
/*     */   }
/*     */ 
/*     */   public static int nodeLength(NodeList list)
/*     */   {
/* 331 */     return list == null ? 0 : list.getLength();
/*     */   }
/*     */ 
/*     */   private static String evaluateAsString(String expression, Node node)
/*     */     throws XPathExpressionException
/*     */   {
/* 350 */     if (isEmpty(node)) return null;
/*     */ 
/* 352 */     if (expression != ".")
/*     */     {
/* 363 */       if (asNode(expression, node) == null) return null;
/*     */     }
/*     */ 
/* 366 */     String s = xpath.evaluate(expression, node);
/*     */ 
/* 368 */     return s.trim();
/*     */   }
/*     */ 
/*     */   private static boolean isEmptyString(String s)
/*     */   {
/* 379 */     if (s == null) return true;
/* 380 */     if (s.trim().equals("")) return true;
/*     */ 
/* 382 */     return false;
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.XpathUtils
 * JD-Core Version:    0.6.2
 */