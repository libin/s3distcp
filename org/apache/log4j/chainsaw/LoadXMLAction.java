/*     */ package org.apache.log4j.chainsaw;
/*     */ 
/*     */ import java.awt.event.ActionEvent;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.StringReader;
/*     */ import javax.swing.AbstractAction;
/*     */ import javax.swing.JFileChooser;
/*     */ import javax.swing.JFrame;
/*     */ import javax.swing.JOptionPane;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.parsers.SAXParser;
/*     */ import javax.xml.parsers.SAXParserFactory;
/*     */ import org.apache.log4j.Logger;
/*     */ import org.xml.sax.InputSource;
/*     */ import org.xml.sax.SAXException;
/*     */ import org.xml.sax.XMLReader;
/*     */ 
/*     */ class LoadXMLAction extends AbstractAction
/*     */ {
/*  44 */   private static final Logger LOG = Logger.getLogger(LoadXMLAction.class);
/*     */   private final JFrame mParent;
/*  53 */   private final JFileChooser mChooser = new JFileChooser();
/*     */   private final XMLReader mParser;
/*     */   private final XMLFileHandler mHandler;
/*     */ 
/*     */   LoadXMLAction(JFrame aParent, MyTableModel aModel)
/*     */     throws SAXException, ParserConfigurationException
/*     */   {
/*  55 */     this.mChooser.setMultiSelectionEnabled(false);
/*  56 */     this.mChooser.setFileSelectionMode(0);
/*     */ 
/*  76 */     this.mParent = aParent;
/*  77 */     this.mHandler = new XMLFileHandler(aModel);
/*  78 */     this.mParser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
/*  79 */     this.mParser.setContentHandler(this.mHandler);
/*     */   }
/*     */ 
/*     */   public void actionPerformed(ActionEvent aIgnore)
/*     */   {
/*  87 */     LOG.info("load file called");
/*  88 */     if (this.mChooser.showOpenDialog(this.mParent) == 0) {
/*  89 */       LOG.info("Need to load a file");
/*  90 */       File chosen = this.mChooser.getSelectedFile();
/*  91 */       LOG.info("loading the contents of " + chosen.getAbsolutePath());
/*     */       try {
/*  93 */         int num = loadFile(chosen.getAbsolutePath());
/*  94 */         JOptionPane.showMessageDialog(this.mParent, "Loaded " + num + " events.", "CHAINSAW", 1);
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/* 100 */         LOG.warn("caught an exception loading the file", e);
/* 101 */         JOptionPane.showMessageDialog(this.mParent, "Error parsing file - " + e.getMessage(), "CHAINSAW", 0);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private int loadFile(String aFile)
/*     */     throws SAXException, IOException
/*     */   {
/* 121 */     synchronized (this.mParser)
/*     */     {
/* 123 */       StringBuffer buf = new StringBuffer();
/* 124 */       buf.append("<?xml version=\"1.0\" standalone=\"yes\"?>\n");
/* 125 */       buf.append("<!DOCTYPE log4j:eventSet ");
/* 126 */       buf.append("[<!ENTITY data SYSTEM \"file:///");
/* 127 */       buf.append(aFile);
/* 128 */       buf.append("\">]>\n");
/* 129 */       buf.append("<log4j:eventSet xmlns:log4j=\"Claira\">\n");
/* 130 */       buf.append("&data;\n");
/* 131 */       buf.append("</log4j:eventSet>\n");
/*     */ 
/* 133 */       InputSource is = new InputSource(new StringReader(buf.toString()));
/*     */ 
/* 135 */       this.mParser.parse(is);
/* 136 */       return this.mHandler.getNumEvents();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /Users/libinpan/Work/s3/s3distcp.jar
 * Qualified Name:     org.apache.log4j.chainsaw.LoadXMLAction
 * JD-Core Version:    0.6.2
 */