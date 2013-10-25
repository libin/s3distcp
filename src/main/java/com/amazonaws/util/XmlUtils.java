/*    */ package com.amazonaws.util;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import org.xml.sax.ContentHandler;
/*    */ import org.xml.sax.InputSource;
/*    */ import org.xml.sax.SAXException;
/*    */ import org.xml.sax.XMLReader;
/*    */ import org.xml.sax.helpers.XMLReaderFactory;
/*    */ 
/*    */ public class XmlUtils
/*    */ {
/*    */   public static XMLReader parse(InputStream in, ContentHandler handler)
/*    */     throws SAXException, IOException
/*    */   {
/* 31 */     XMLReader reader = XMLReaderFactory.createXMLReader();
/* 32 */     reader.setContentHandler(handler);
/* 33 */     reader.parse(new InputSource(in));
/* 34 */     in.close();
/* 35 */     return reader;
/*    */   }
/*    */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     com.amazonaws.util.XmlUtils
 * JD-Core Version:    0.6.2
 */